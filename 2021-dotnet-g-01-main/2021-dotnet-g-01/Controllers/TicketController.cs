using _2021_dotnet_g_01.Models.Domain;
using _2021_dotnet_g_01.Models.Filters;
using _2021_dotnet_g_01.Models.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http.Headers;
using System.Text.RegularExpressions;

namespace _2021_dotnet_g_01.Controllers
{
    [Authorize]
    public class TicketController : Controller
    {
        private readonly ITicketRepository _ticketRepository;
        private readonly ICustomerRepository _customerRepository;
        private readonly IContractRepository _contractRepository;
        private readonly IWebHostEnvironment _environment;

        public TicketController(ITicketRepository ticketRepository, ICustomerRepository customerRepository, IContractRepository contractRepository, IWebHostEnvironment environment)
        {
            _ticketRepository = ticketRepository;
            _customerRepository = customerRepository;
            _contractRepository = contractRepository;
            _environment = environment;
        }

        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Index(int? ticketStatusNumber, Customer customer)
        {
            // enkel tickets van customer worden getoond
            if (ticketStatusNumber == null)
            {
                ticketStatusNumber = 0;
            }
            IEnumerable<Ticket> tickets = _customerRepository.GetByCustomerId(customer.Id).GetTickets();
            if (ticketStatusNumber != 0)
            {
                tickets = tickets.Where(t => (int)t.TicketStatus == ticketStatusNumber).ToList();
            }
            ViewData["HasActiveContract"] = customer.HasActiveContract();
            ViewData["TicketStatus"] = CreateTicketStatusSelectList(ticketStatusNumber);
            return View(tickets);
        }

        [Authorize(Policy = "SupportManager")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult IndexForSupportManager(int? ticketStatusNumber)
        {
            // supportmanager kan alle tickets zien
            if (ticketStatusNumber == null)
            {
                ticketStatusNumber = 0;
            }
            IEnumerable<Ticket> tickets = _ticketRepository.GetAll().OrderBy(t => t.Title).ToList();
            if (ticketStatusNumber != 0)
            {
                tickets = tickets.Where(t => (int)t.TicketStatus == ticketStatusNumber).ToList();
            }
            ViewData["TicketStatus"] = CreateTicketStatusSelectList(ticketStatusNumber);
            return View(tickets);
        }

        [Authorize(Policy = "Customer")]
        public IActionResult Details(int id)
        {
            Ticket ticket = _ticketRepository.GetByTicketIdIncludingCommentsAndAttachments(id);
            if (ticket == null)
                return NotFound();
            ViewData["IsSupportManager"] = false;
            ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
            return View(new TicketDetailViewModel(ticket));
        }

        [HttpPost]
        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Details(int id, TicketDetailViewModel ticketDetailViewModel, Customer customer)
        {
            Ticket ticket = _ticketRepository.GetByTicketIdIncludingCommentsAndAttachments(id);
            var newFileName = string.Empty;

            bool attachmentAddedFlag = false;
            if (ModelState.IsValid)
            {
                // comment toevoegen aan ticket               
                try
                {
                    if (HttpContext.Request.Form.Files != null)
                    {
                        var fileName = string.Empty;

                        var files = HttpContext.Request.Form.Files;

                        foreach (var file in files)
                        {
                            if (file.Length > 0)
                            {
                                fileName = ContentDispositionHeaderValue.Parse(file.ContentDisposition).FileName.Trim('"');
                                var myUniqueFileName = Convert.ToString(Guid.NewGuid());
                                var fileExtension = Path.GetExtension(fileName);
                                newFileName = myUniqueFileName + fileExtension;
                                fileName = Path.Combine(_environment.WebRootPath, "attachments") + $@"\{newFileName}";
                                ticket.AddComment(new Attachment(customer.Email + ": " + ticketDetailViewModel.Comment, newFileName));

                                using (FileStream fs = System.IO.File.Create(fileName))
                                {
                                    file.CopyTo(fs);
                                    fs.Flush();
                                }
                                attachmentAddedFlag = true;
                            }
                            TempData["message"] = $"You successfully added attachment {ticketDetailViewModel.Comment}.";
                        }
                    }
                    if (attachmentAddedFlag == false)
                    {
                        ticket.AddComment(new Comment(customer.Email + ": " + ticketDetailViewModel.Comment));
                        TempData["message"] = $"You successfully added comment {ticketDetailViewModel.Comment}.";

                    }
                    _ticketRepository.SaveChanges();
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, comment {ticketDetailViewModel.Comment} was not added ({e.Message}).";
                }

                ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
                ViewData["IsSupportManager"] = false;
                ModelState.Clear(); //zorgt ervoor dat 'comment' vakje leeg wordt
                return View(nameof(Details), new TicketDetailViewModel(ticket));
            }
            ViewData["IsSupportManager"] = false;
            ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
            return View(ticketDetailViewModel);
        }


        [Authorize(Policy = "SupportManager")]
        public IActionResult DetailsForSupportManager(int id)
        {
            // support manager ziet ook van wie ticket is
            Ticket ticket = _ticketRepository.GetByTicketIdIncludingCommentsAndAttachments(id);
            if (ticket == null)
                return NotFound();
            ViewData["IsSupportManager"] = true;
            ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket));
            ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
            return View(nameof(Details), new TicketDetailViewModel(ticket));
        }

        [HttpPost]
        [Authorize(Policy = "SupportManager")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult DetailsForSupportManager(int id, TicketDetailViewModel ticketDetailViewModel, Customer customer)
        {
            Ticket ticket = _ticketRepository.GetByTicketIdIncludingCommentsAndAttachments(id);
            if (ModelState.IsValid)
            {
                try
                {
                    ticket.AddComment(new Comment(customer.Email + ": " + ticketDetailViewModel.Comment));
                    _ticketRepository.SaveChanges();
                    TempData["message"] = $"You successfully added comment { ticketDetailViewModel.Comment}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, comment { ticketDetailViewModel.Comment} was not added ({e.Message}).";
                }

                ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket));
                ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
                ViewData["IsSupportManager"] = true;
                return View(nameof(Details), new TicketDetailViewModel(ticket));
            }
            ViewData["IsSupportManager"] = true;
            ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket));
            ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
            return View(nameof(Details), ticketDetailViewModel);
        }

        [Authorize(Policy = "Customer")]
        public IActionResult Edit(int id)
        {
            // enkel edit van title en description
            Ticket ticket = _ticketRepository.GetByTicketId(id);
            if (ticket == null)
                return NotFound();
            if (!ticket.IsEditable())
            {
                TempData["error"] = $"{ticket.Title} is {ticket.TicketStatus.ToString().ToLower()} and cannot be edited.";
                return RedirectToAction(nameof(Index));
            }
            else
            {
                ViewData["IsEdit"] = true;
                ViewData["IsSupportManager"] = false;
                return View(new TicketEditViewModel(ticket));
            }
        }

        [HttpPost]
        [Authorize(Policy = "Customer")]
        public IActionResult Edit(TicketEditViewModel ticketEditViewModel, int id)
        {
            if (ModelState.IsValid)
            {
                // enkel edit van title en description
                Ticket ticket = null;
                try
                {
                    ticket = _ticketRepository.GetByTicketId(id);
                    ticket.Title = ticketEditViewModel.Title;
                    ticket.Description = ticketEditViewModel.Description;
                    _ticketRepository.SaveChanges();
                    TempData["message"] = $"You successfully updated ticket {ticket.Title}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, ticket {ticket?.Title} was not updated ({e.Message}).";
                    return RedirectToAction(nameof(Edit));
                }
                return RedirectToAction(nameof(Index));
            }
            ViewData["IsEdit"] = true;
            ViewData["IsSupportManager"] = false;
            return View(ticketEditViewModel);
        }

        [Authorize(Policy = "SupportManager")]
        public IActionResult EditForSupportManager(int id)
        {
            // ook edit van tickettype
            Ticket ticket = _ticketRepository.GetByTicketId(id);
            if (ticket == null)
                return NotFound();
            if (!ticket.IsEditable())
            {
                TempData["error"] = $"{ticket.Title} is {ticket.TicketStatus.ToString().ToLower()} and cannot be edited.";
                return RedirectToAction(nameof(IndexForSupportManager));
            }
            else
            {
                ViewData["TicketTypes"] = CreateTicketTypeSelectList();
                ViewData["IsEdit"] = true;
                ViewData["IsSupportManager"] = true;
                ViewData["Engineers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(true)));
                return View(nameof(Edit), new TicketEditViewModel(ticket));
            }
        }

        [HttpPost]
        [Authorize(Policy = "SupportManager")]
        public IActionResult EditForSupportManager(TicketEditViewModel ticketEditViewModel, int id)
        {
            if (ModelState.IsValid)
            {
                // ook edit van tickettype
                Ticket ticket = null;
                try
                {
                    ticket = _ticketRepository.GetByTicketId(id);
                    ticket.Title = ticketEditViewModel.Title;
                    ticket.Description = ticketEditViewModel.Description;
                    ticket.TicketType = ticketEditViewModel.TicketType;
                    ticket.AssignedEngineer = ticketEditViewModel.AssignedEngineer;
                    _ticketRepository.SaveChanges();
                    TempData["message"] = $"You successfully updated ticket {ticket.Title}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, ticket {ticket?.Title} was not updated ({e.Message}).";
                    return RedirectToAction(nameof(EditForSupportManager));
                }
                return RedirectToAction(nameof(IndexForSupportManager));
            }
            ViewData["TicketTypes"] = CreateTicketTypeSelectList();
            ViewData["IsEdit"] = true;
            ViewData["IsSupportManager"] = true;
            ViewData["Engineers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(true)));
            return View(nameof(Edit), ticketEditViewModel);
        }

        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Create()
        {
            // nieuw ticket maken, wordt automatisch aan ingelogde user toegewezen
            ViewData["IsEdit"] = false;
            ViewData["IsSupportManager"] = false;
            ViewData["TicketTypes"] = CreateTicketTypeSelectList();
            return View(nameof(Edit), new TicketEditViewModel());
        }

        [HttpPost]
        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Create(TicketEditViewModel ticketEditViewModel, Customer customer)
        {
            if (ModelState.IsValid)
            {
                // customer is ingelogde customer
                try
                {
                    Ticket ticket = new Ticket(ticketEditViewModel.Title, ticketEditViewModel.Description, ticketEditViewModel.TicketType);
                    Contract contract = customer.GetActiveContract();
                    contract.AddTicket(ticket);
                    _contractRepository.SaveChanges();
                    TempData["message"] = $"You successfully added ticket {ticket.Title}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, the ticket was not added ({e.Message}).";
                    return RedirectToAction(nameof(Create));
                }
                return RedirectToAction(nameof(Index));
            }
            ViewData["IsEdit"] = false;
            ViewData["IsSupportManager"] = false;
            ViewData["TicketTypes"] = CreateTicketTypeSelectList();
            return View(nameof(Edit), new TicketEditViewModel());
        }

        [Authorize(Policy = "SupportManager")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult CreateForSupportManager()
        {
            // supportmanager moet een customer kiezen waarvoor ticket wordt gemaakt
            ViewData["IsEdit"] = false;
            ViewData["IsSupportManager"] = true;
            ViewData["TicketTypes"] = CreateTicketTypeSelectList();
            ViewData["Customers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(false)));
            ViewData["Engineers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(true)));
            return View(nameof(Create), new TicketCreateViewModel());
        }

        [HttpPost]
        [Authorize(Policy = "SupportManager")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult CreateForSupportManager(TicketCreateViewModel ticketCreateViewModel)
        {
            if (ModelState.IsValid)
            {
                // aparte viewmodel en view om gekozen username op te halen
                try
                {
                    Ticket ticket = new Ticket(ticketCreateViewModel.Title, ticketCreateViewModel.Description, ticketCreateViewModel.TicketType, ticketCreateViewModel.AssignedEngineer);
                    Customer customer = _customerRepository.GetByCustomerName(ticketCreateViewModel.UserName);
                    Contract contract = customer.GetActiveContract();
                    contract.AddTicket(ticket);
                    _contractRepository.SaveChanges();
                    TempData["message"] = $"You successfully added ticket {ticket.Title}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, the ticket was not added ({e.Message}).";
                    return RedirectToAction(nameof(CreateForSupportManager));
                }
                return RedirectToAction(nameof(IndexForSupportManager));
            }
            ViewData["IsEdit"] = false;
            ViewData["IsSupportManager"] = true;
            ViewData["TicketTypes"] = CreateTicketTypeSelectList();
            ViewData["Customers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(false)));
            ViewData["Engineers"] = new SelectList(_customerRepository.GetAll().Where(c => c.IsSupportManager.Equals(true)));
            return View(nameof(Create), new TicketCreateViewModel());
        }

        [Authorize(Policy = "Customer")]
        public IActionResult Cancel(int id)
        {
            // customers cancels tickets
            Ticket ticket = _ticketRepository.GetByTicketId(id);
            if (ticket == null)
                return NotFound();
            if (!ticket.IsEditable())
            {
                TempData["error"] = $"{ticket.Title} is already {ticket.TicketStatus.ToString().ToLower()}.";
                return RedirectToAction(nameof(Index));
            }
            else
            {
                ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
                return View(new TicketDetailViewModel(ticket));
            }
        }

        [Authorize(Policy = "Customer")]
        [HttpPost, ActionName("Cancel")]
        public IActionResult CancelConfirmed(int id)
        {
            Ticket ticket = null;
            try
            {
                ticket = _ticketRepository.GetByTicketId(id);
                ticket.TicketStatus = TicketStatus.Cancelled;          //geen echte delete, ticket wordt cancelled
                _ticketRepository.SaveChanges();
                TempData["message"] = $"You successfully cancelled ticket {ticket.Title}.";
            }
            catch (Exception e)
            {
                TempData["error"] = $"Sorry, something went wrong, ticket {ticket?.Title} was not cancelled ({e.Message}).";
            }
            return RedirectToAction(nameof(Index));
        }

        [Authorize(Policy = "SupportManager")]
        public IActionResult Close(int id)
        {
            // supportmanagers closes tickets
            Ticket ticket = _ticketRepository.GetByTicketId(id);
            if (ticket == null)
                return NotFound();
            if (!ticket.IsEditable())
            {
                TempData["error"] = $"{ticket.Title} is already {ticket.TicketStatus.ToString().ToLower()}.";
                return RedirectToAction(nameof(IndexForSupportManager));
            }
            else
            {
                ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket));
                ViewData["Contract"] = _contractRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfTicket(ticket)).Name;
                return View(new TicketDetailViewModel(ticket));
            }
        }

        [Authorize(Policy = "SupportManager")]
        [HttpPost, ActionName("Close")]
        public IActionResult CloseConfirmed(int id)
        {
            Ticket ticket = null;
            try
            {
                ticket = _ticketRepository.GetByTicketId(id);
                ticket.TicketStatus = TicketStatus.Closed;          //geen echte delete, ticket wordt closed
                _ticketRepository.SaveChanges();
                TempData["message"] = $"You successfully closed ticket {ticket.Title}.";
            }
            catch (Exception e)
            {
                TempData["error"] = $"Sorry, something went wrong, ticket {ticket?.Title} was not closed ({e.Message}).";
            }
            return RedirectToAction(nameof(IndexForSupportManager));
        }

        private SelectList CreateTicketStatusSelectList(int? selectedTicketStatusName)
        {
            var ticketStatuses = from TicketStatus ts in Enum.GetValues(typeof(TicketStatus))
                                 select new
                                 {
                                     ID = (int)ts,
                                     Name = ts.ToString()
                                 };
            return new SelectList(ticketStatuses, "ID", "Name", selectedTicketStatusName);
        }

        private SelectList CreateTicketTypeSelectList()
        {
            var ticketTypes = from TicketType tt in Enum.GetValues(typeof(TicketType))
                              select new
                              {
                                  ID = tt,
                                  Name = tt.GetString()
                              };
            return new SelectList(ticketTypes, "ID", "Name");
        }

        //remote method in TicketCreateViewModel for validation
        public IActionResult IsValidCustomer(string UserName)
        {
            Customer customer = _customerRepository.GetByCustomerName(UserName);

            //customer not found
            if (customer == null)
            {
                return Json($"{UserName} not found.");
            }
            //customer is blocked
            if (customer.Status.Equals(Status.Blocked))
            {
                return Json($"{UserName} is blocked.");
            }
            //customer has no active contract
            if (!customer.HasActiveContract())
            {
                return Json($"{UserName} has no active contracts.");
            }
            //is valid customer
            return Json(true);
        }

    }
}
