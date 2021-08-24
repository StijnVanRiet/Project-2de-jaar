using _2021_dotnet_g_01.Controllers;
using _2021_dotnet_g_01.Models.Domain;
using _2021_dotnet_g_01.Models.ViewModels;
using _2021_dotnet_g_01.Tests.Data;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ViewFeatures;
using Moq;
using System.Collections.Generic;
using System.Linq;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Controllers
{
    public class TicketControllerTest
    {
        private readonly TicketController _controller;
        private readonly Mock<ITicketRepository> _ticketRepository;
        private readonly Mock<ICustomerRepository> _customerRepository;
        private readonly Mock<IContractRepository> _contractRepository;
        private readonly DummyApplicationDbContext _dummyContext;
        private readonly IWebHostEnvironment _environment;

        public TicketControllerTest()
        {
            _dummyContext = new DummyApplicationDbContext();
            _ticketRepository = new Mock<ITicketRepository>();
            _customerRepository = new Mock<ICustomerRepository>();
            _contractRepository = new Mock<IContractRepository>();
            _controller = new TicketController(_ticketRepository.Object, _customerRepository.Object, _contractRepository.Object, _environment)
            {
                TempData = new Mock<ITempDataDictionary>().Object
            };
        }

        #region -- Index --
        [Fact]
        public void Index_PassesOrderedListOfTicketsInViewResultModel()
        {
            _customerRepository.Setup(c => c.GetByCustomerId(_dummyContext.Customer.Id)).Returns(_dummyContext.Customer);
            var result = Assert.IsType<ViewResult>(_controller.Index(1, _dummyContext.Customer));
            List<Ticket> ticketsInModel = Assert.IsType<List<Ticket>>(result.Model);
            Assert.Equal(2, ticketsInModel.Count);
            Assert.Equal("Help my pc broke", ticketsInModel[0].Title);
            Assert.Equal("Need new mouse", ticketsInModel[1].Title);
        }

        [Fact]
        public void IndexForSupportManager_PassesOrderedListOfTicketsInViewResultModel()
        {
            _ticketRepository.Setup(t => t.GetAll()).Returns(_dummyContext.Tickets);
            var result = Assert.IsType<ViewResult>(_controller.IndexForSupportManager(1));
            List<Ticket> ticketsInModel = Assert.IsType<List<Ticket>>(result.Model);
            Assert.Equal(2, ticketsInModel.Count);
            Assert.Equal("Help my pc broke", ticketsInModel[0].Title);
            Assert.Equal("Need new mouse", ticketsInModel[1].Title);
        }
        #endregion

        #region -- Detail --
        [Fact]
        public void Detail_PassesSelectedTicketInViewResultModel()
        {
            _ticketRepository.Setup(t => t.GetByTicketIdIncludingCommentsAndAttachments(1)).Returns(_dummyContext.Ticket1);
            _contractRepository.Setup(c => c.GetAll()).Returns(_dummyContext.Contracts);
            var result = Assert.IsType<ViewResult>(_controller.Details(1));
            var ticketDvm = Assert.IsType<TicketDetailViewModel>(result.Model);
            Assert.Equal("Help my pc broke", ticketDvm.Title);
            Assert.Equal("my screen is black", ticketDvm.Description);
            Assert.Equal(TicketType.NotUrgent, ticketDvm.TicketType);
            Assert.Equal(TicketStatus.Created, ticketDvm.TicketStatus);
            Assert.Equal("", ticketDvm.AssignedEngineer);
            Assert.Equal("bert@email.com: The computer broke", ticketDvm.Comments.ElementAt(0).Content);
        }
        #endregion

        #region -- Detail POST --
        [Fact]
        public void Detail_ValidAddComment_AddsAndPersistsCommentAndRedirectsToActionDetail()
        {
            _ticketRepository.Setup(t => t.GetByTicketIdIncludingCommentsAndAttachments(1)).Returns(_dummyContext.Ticket1);
            _contractRepository.Setup(c => c.GetAll()).Returns(_dummyContext.Contracts);
            var ticketDvm = new TicketDetailViewModel(_dummyContext.Ticket1)
            {
                Comment = "comment"
            };
            var result = Assert.IsType<ViewResult>(_controller.Details(1, ticketDvm, _dummyContext.Customer));
            var ticket = _dummyContext.Ticket1;
            Assert.Equal("Details", result?.ViewName);
            Assert.Equal(2, ticket.Comments.Count);
            Assert.Equal("user@email.com: comment", ticket.Comments.ElementAt(1).Content);
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Once);
        }
        #endregion

        #region -- Edit GET --
        [Fact]
        public void Edit_PassesTicketInEditViewModel()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var result = Assert.IsType<ViewResult>(_controller.Edit(1));
            var ticketEvm = Assert.IsType<TicketEditViewModel>(result.Model);
            Assert.Equal("Help my pc broke", ticketEvm.Title);
            Assert.Equal("my screen is black", ticketEvm.Description);
        }

        [Fact]
        public void EditForSupportManager_PassesTicketInEditViewModel()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var result = Assert.IsType<ViewResult>(_controller.EditForSupportManager(1));
            var ticketEvm = Assert.IsType<TicketEditViewModel>(result.Model);
            Assert.Equal(TicketType.NotUrgent, ticketEvm.TicketType);
            Assert.Equal("Help my pc broke", ticketEvm.Title);
            Assert.Equal("my screen is black", ticketEvm.Description);
        }
        #endregion

        #region -- Edit POST --
        [Fact]
        public void Edit_ValidEdit_UpdatesAndPersistsTicketAndRedirectsToActionIndex()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var ticketEvm = new TicketEditViewModel(_dummyContext.Ticket1)
            {
                Title = "Title",
                Description = "here comes the description"
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.Edit(ticketEvm, 1));
            var ticket1 = _dummyContext.Ticket1;
            Assert.Equal("Index", result?.ActionName);
            Assert.Equal("Title", ticket1.Title);
            Assert.Equal("here comes the description", ticket1.Description);
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Once());
        }

        [Fact]
        public void EditForSupportManager_ValidEdit_UpdatesAndPersistsTicketAndRedirectsToActionIndexForSupportManager()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var ticketEvm = new TicketEditViewModel(_dummyContext.Ticket1)
            {
                TicketType = TicketType.Urgent,
                Title = "Title",
                Description = "here comes the description"
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.EditForSupportManager(ticketEvm, 1));
            var ticket1 = _dummyContext.Ticket1;
            Assert.Equal("IndexForSupportManager", result?.ActionName);
            Assert.Equal(TicketType.Urgent, ticketEvm.TicketType);
            Assert.Equal("Title", ticket1.Title);
            Assert.Equal("here comes the description", ticket1.Description);
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Once());
        }

        [Fact]
        public void Edit_InvalidEdit_DoesNotChangeNorPersistsTicketAndRedirectsToActionIndex()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var ticketEvm = new TicketEditViewModel(_dummyContext.Ticket1)
            {
                Description = ""
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.Edit(ticketEvm, 1));
            var ticket1 = _dummyContext.Ticket1;
            Assert.Equal("Edit", result?.ActionName);
            Assert.Equal("Help my pc broke", ticket1.Title);
            Assert.Equal("my screen is black", ticket1.Description);
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Never());
        }
        #endregion

        #region -- Create GET --
        [Fact]
        public void Create_PassesNewTicketInEditViewModel()
        {
            var result = Assert.IsType<ViewResult>(_controller.Create());
            var ticketEvm = Assert.IsType<TicketEditViewModel>(result.Model);
            Assert.Null(ticketEvm.Title);
            Assert.Null(ticketEvm.Description);
        }

        [Fact]
        public void CreateForSupportManager_PassesNewTicketInEditViewModel()
        {
            var result = Assert.IsType<ViewResult>(_controller.CreateForSupportManager());
            var ticketCvm = Assert.IsType<TicketCreateViewModel>(result.Model);
            Assert.Null(ticketCvm.Title);
            Assert.Null(ticketCvm.Description);
        }
        #endregion

        #region -- Create POST --
        [Fact]
        public void Create_ValidTicket_CreatesAndPersistsTicketAndRedirectsToActionIndex()
        {
            var ticketEvm = new TicketEditViewModel()
            {
                TicketType = TicketType.NotUrgent,
                Title = "Title",
                Description = "description"
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.Create(ticketEvm, _dummyContext.Customer));
            var contract = _dummyContext.Contract1;
            Assert.Equal("Index", result?.ActionName);
            Assert.Equal(2, contract.Tickets.Count);
            Assert.Equal("Help my pc broke", contract.Tickets.ElementAt(0).Title);
            Assert.Equal("Title", contract.Tickets.ElementAt(1).Title);
            _contractRepository.Verify(c => c.SaveChanges(), Times.Once);
        }

        [Fact]
        public void CreateForSupportManager_ValidTicket_CreatesAndPersistsTicketAndRedirectsToActionIndexForSupportManager()
        {
            _customerRepository.Setup(c => c.GetByCustomerName("user@email.com")).Returns(_dummyContext.Customer);
            var ticketCvm = new TicketCreateViewModel()
            {
                UserName = "user@email.com",
                TicketType = TicketType.NotUrgent,
                Title = "Title",
                Description = "description"
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.CreateForSupportManager(ticketCvm));
            var contract = _dummyContext.Contract1;
            Assert.Equal("IndexForSupportManager", result?.ActionName);
            Assert.Equal(2, contract.Tickets.Count);
            Assert.Equal("Help my pc broke", contract.Tickets.ElementAt(0).Title);
            Assert.Equal("Title", contract.Tickets.ElementAt(1).Title);
            _contractRepository.Verify(c => c.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Create_InvalidTicket_DoesNotCreateNorPersistsTicketAndRedirectsToActionIndex()
        {
            var ticketEvm = new TicketEditViewModel()
            {

                TicketType = TicketType.NotUrgent,
                Title = "Title",
                Description = ""
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.Create(ticketEvm, _dummyContext.Customer));
            var contract = _dummyContext.Contract1;
            Assert.Equal("Create", result?.ActionName);
            Assert.Equal(1, contract.Tickets.Count);
            Assert.Equal("Help my pc broke", contract.Tickets.ElementAt(0).Title);
            _contractRepository.Verify(c => c.SaveChanges(), Times.Never());
        }
        #endregion

        #region -- Cancel GET --
        [Fact]
        public void Cancel_PassesSelectedTicketInViewResultModel()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(_dummyContext.Ticket1.TicketId)).Returns(_dummyContext.Ticket1);
            _contractRepository.Setup(c => c.GetAll()).Returns(_dummyContext.Contracts);
            var result = Assert.IsType<ViewResult>(_controller.Cancel(_dummyContext.Ticket1.TicketId));
            var ticketDvm = Assert.IsType<TicketDetailViewModel>(result.Model);
            Assert.Equal("Help my pc broke", ticketDvm.Title);
            Assert.Equal("my screen is black", ticketDvm.Description);
            Assert.Equal(TicketType.NotUrgent, ticketDvm.TicketType);
            Assert.Equal(TicketStatus.Created, ticketDvm.TicketStatus);
            Assert.Equal("", ticketDvm.AssignedEngineer);
            Assert.Equal("bert@email.com: The computer broke", ticketDvm.Comments.ElementAt(0).Content);
        }
        #endregion

        #region -- Cancel POST --
        [Fact]
        public void Cancel_ExistingTicket_CancelsTicketAndPersistsChangesAndRedirectsToActionIndex()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var result = Assert.IsType<RedirectToActionResult>(_controller.CancelConfirmed(1));
            Assert.Equal("Index", result.ActionName);
            Assert.Equal(TicketStatus.Cancelled, _dummyContext.Ticket1.TicketStatus);
            _ticketRepository.Verify(t => t.GetByTicketId(1), Times.Once());
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Once());
        }
        #endregion

        #region -- Close GET --
        [Fact]
        public void Close_PassesSelectedTicketInViewResultModel()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(_dummyContext.Ticket1.TicketId)).Returns(_dummyContext.Ticket1);
            _contractRepository.Setup(c => c.GetAll()).Returns(_dummyContext.Contracts);
            var result = Assert.IsType<ViewResult>(_controller.Close(_dummyContext.Ticket1.TicketId));
            var ticketDvm = Assert.IsType<TicketDetailViewModel>(result.Model);
            Assert.Equal("Help my pc broke", ticketDvm.Title);
            Assert.Equal("my screen is black", ticketDvm.Description);
            Assert.Equal(TicketType.NotUrgent, ticketDvm.TicketType);
            Assert.Equal(TicketStatus.Created, ticketDvm.TicketStatus);
            Assert.Equal("", ticketDvm.AssignedEngineer);
            Assert.Equal("bert@email.com: The computer broke", ticketDvm.Comments.ElementAt(0).Content);
        }
        #endregion

        #region -- Close POST --
        [Fact]
        public void Close_ExistingTicket_ClosesTicketAndPersistsChangesAndRedirectsToActionIndexForSupportManager()
        {
            _ticketRepository.Setup(t => t.GetByTicketId(1)).Returns(_dummyContext.Ticket1);
            var result = Assert.IsType<RedirectToActionResult>(_controller.CloseConfirmed(1));
            Assert.Equal("IndexForSupportManager", result.ActionName);
            Assert.Equal(TicketStatus.Closed, _dummyContext.Ticket1.TicketStatus);
            _ticketRepository.Verify(t => t.GetByTicketId(1), Times.Once());
            _ticketRepository.Verify(t => t.SaveChanges(), Times.Once());
        }
        #endregion
    }
}