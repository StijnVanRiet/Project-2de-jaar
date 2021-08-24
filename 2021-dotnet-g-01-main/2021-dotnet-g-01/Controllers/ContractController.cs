using _2021_dotnet_g_01.Models.Domain;
using _2021_dotnet_g_01.Models.Filters;
using _2021_dotnet_g_01.Models.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using System;
using System.Collections.Generic;
using System.Linq;

namespace _2021_dotnet_g_01.Controllers
{
    [Authorize]
    public class ContractController : Controller
    {
        private readonly IContractRepository _contractRepository;
        private readonly IContractTypeRepository _contractTypeRepository;
        private readonly ICustomerRepository _customerRepository;

        public ContractController(IContractRepository contractRepository, IContractTypeRepository contractTypeRepository, ICustomerRepository customerRepository)
        {
            _contractRepository = contractRepository;
            _contractTypeRepository = contractTypeRepository;
            _customerRepository = customerRepository;
        }

        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Index(int? contractStatusNumber, Customer customer)
        {
            if (contractStatusNumber == null)
            {
                contractStatusNumber = 0;
            }
            IEnumerable<Contract> contracts;
            contracts = _customerRepository.GetByCustomerId(customer.Id).Contracts.OrderBy(c => c.StartDate).ToList();
            if (contractStatusNumber != 0)
            {
                contracts = contracts.Where(c => (int)c.ContractStatus == contractStatusNumber).ToList();
            }
            ViewData["HasActiveContract"] = customer.HasActiveContract();
            ViewData["ActiveContract"] = customer.HasActiveContract() ? customer.GetActiveContract().Name : "";
            ViewData["HasContract"] = customer.Contracts.Count() != 0;
            ViewData["ContractStatus"] = CreateContractStatusSelectList(contractStatusNumber);
            return View(contracts);
        }

        [Authorize(Policy = "SupportManager")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult IndexForSupportManager(int? contractStatusNumber)
        {
            if (contractStatusNumber == null)
            {
                contractStatusNumber = 0;
            }
            IEnumerable<Contract> contracts;
            contracts = _contractRepository.GetAllIncludingContractTypes();

            if (contractStatusNumber != 0)
            {
                contracts = contracts.Where(c => (int)c.ContractStatus == contractStatusNumber).ToList();
            }

            ViewData["ContractStatus"] = CreateContractStatusSelectList(contractStatusNumber);
            return View(contracts);
        }

        [Authorize(Policy = "Customer")]
        public IActionResult Details(int id)
        {
            Contract contract = _contractRepository.GetByContractIdIncludingContractTypeAndTickets(id);
            if (contract == null)
                return NotFound();
            ViewData["IsSupportManager"] = false;
            ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfContract(contract));
            return View(contract);
        }

        [Authorize(Policy = "SupportManager")]
        public IActionResult DetailsForSupportManager(int id)
        {
            Contract contract = _contractRepository.GetByContractIdIncludingContractTypeAndTickets(id);
            if (contract == null)
                return NotFound();
            ViewData["IsSupportManager"] = true;
            ViewData["Customer"] = _customerRepository.GetAll().SingleOrDefault(c => c.IsOwnerOfContract(contract));
            return View(nameof(Details), contract);
        }

        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Create(Customer customer)
        {
            ViewData["ContractTypes"] = CreateContractTypeSelectList();
            return View(new ContractCreateViewModel(customer));
        }

        [HttpPost]
        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult Create(ContractCreateViewModel contractCreateViewModel, Customer customer)
        {
            if (ModelState.IsValid)
            {
                // customer is ingelogde customer
                try
                {
                    ContractType contractType = _contractTypeRepository.GetByContractTypeId(contractCreateViewModel.ContractTypeId);
                    Contract contract = new Contract(contractCreateViewModel.Name, contractCreateViewModel.StartDate, contractType, contractCreateViewModel.DurationInYears);
                    customer.AddContract(contract);
                    _customerRepository.SaveChanges();
                    TempData["message"] = $"You successfully added contract {contract.Name}.";
                }
                catch (Exception e)
                {
                    TempData["error"] = $"Sorry, something went wrong, the contract was not added ({e.Message}).";
                    return RedirectToAction(nameof(Create));
                }
                return RedirectToAction(nameof(Index));
            }
            ViewData["ContractTypes"] = CreateContractTypeSelectList();
            return View(new ContractCreateViewModel(customer));
        }

        [Authorize(Policy = "Customer")]
        public IActionResult Terminate(int id)
        {
            // customers cancels tickets
            Contract contract = _contractRepository.GetByContractIdIncludingContractTypeAndTickets(id);
            if (contract == null)
                return NotFound();
            if (contract.ContractStatus == ContractStatus.Ended)
            {
                TempData["error"] = $"{contract.Name} is already terminated.";
                return RedirectToAction(nameof(Index));
            }
            else
            {
                return View(contract);
            }
        }

        [Authorize(Policy = "Customer")]
        [HttpPost, ActionName("Terminate")]
        public IActionResult TerminateConfirmed(int id)
        {
            Contract contract = null;
            try
            {
                contract = _contractRepository.GetByContractIdIncludingContractTypeAndTickets(id);
                contract.ContractStatus = ContractStatus.Ended;          //geen echte delete, contract wordt ended
                contract.CancelAllTickets();  //tickets die niet closed of cancelled zijn worden gecancelled                           
                _contractRepository.SaveChanges();
                TempData["message"] = $"You successfully terminated contract {contract.Name}.";
            }
            catch (Exception e)
            {
                TempData["error"] = $"Sorry, something went wrong, contract {contract?.Name} was not terminated ({e.Message}).";
            }
            return RedirectToAction(nameof(Index));
        }

        private SelectList CreateContractTypeSelectList()
        {
            return new SelectList(_contractTypeRepository.GetAll().OrderBy(ct => ct.Name).ToList(),
                nameof(ContractType.ContractTypeId), nameof(ContractType.Name));
        }

        private SelectList CreateContractStatusSelectList(int? selectedContractStatusNumber)
        {
            var contractStatuses = from ContractStatus cs in Enum.GetValues(typeof(ContractStatus))
                                   select new
                                   {
                                       ID = (int)cs,
                                       Name = cs.ToString()
                                   };
            return new SelectList(contractStatuses, "ID", "Name", selectedContractStatusNumber);
        }

        //remote methods in ContractCreateViewModel for validation
        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult IsValidStartDate(DateTime startDate, int durationInYears, Customer customer)
        {
            if (startDate < DateTime.Today)
                return Json("Start date cannot be in the past.");
            if (customer.IsContractTimespanOccupied(startDate, startDate.AddYears(durationInYears)))
                return Json("The period cannot overlap with other contracts.");
            return Json(true);
        }

        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult IsValidDurationInYears(int durationInYears, DateTime startDate, Customer customer)
        {
            if (durationInYears <= 0)
                return Json("Duration must be greater than zero.");
            if (customer.IsContractTimespanOccupied(startDate, startDate.AddYears(durationInYears)))
                return Json("The period cannot overlap with other contracts.");
            return Json(true);
        }

        [ServiceFilter(typeof(CustomerFilter))]
        public IActionResult IsValidName(string name, Customer customer)
        {
            if (!customer.IsContractNameUnique(name))
                return Json("A contract with this name already exists.");
            return Json(true);
        }
    }
}

