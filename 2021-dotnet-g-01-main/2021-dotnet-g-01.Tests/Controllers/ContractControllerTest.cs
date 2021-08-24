using _2021_dotnet_g_01.Controllers;
using _2021_dotnet_g_01.Models.Domain;
using _2021_dotnet_g_01.Models.ViewModels;
using _2021_dotnet_g_01.Tests.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ViewFeatures;
using Moq;
using System;
using System.Collections.Generic;
using System.Linq;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Controllers
{

    public class ContractControllerTest
    {
        private readonly ContractController _controller;
        private readonly Mock<IContractRepository> _contractRepository;
        private readonly Mock<IContractTypeRepository> _contractTypeRepository;
        private readonly Mock<ICustomerRepository> _customerRepository;
        private readonly DummyApplicationDbContext _dummyContext;

        public ContractControllerTest()
        {
            _dummyContext = new DummyApplicationDbContext();
            _contractRepository = new Mock<IContractRepository>();
            _customerRepository = new Mock<ICustomerRepository>();
            _contractTypeRepository = new Mock<IContractTypeRepository>();
            _controller = new ContractController(_contractRepository.Object, _contractTypeRepository.Object, _customerRepository.Object)
            {
                TempData = new Mock<ITempDataDictionary>().Object
            };
        }

        #region -- Index --
        [Fact]
        public void Index_PassesOrderedListOfContractsInViewResultModelAndStoresInfoInViewData()
        {
            // a random customer id always returns the same customer ( the tested on isn't a support manager )
            _customerRepository.Setup(c => c.GetByCustomerId(_dummyContext.Customer.Id)).Returns(_dummyContext.Customer);

            var result = Assert.IsType<ViewResult>(_controller.Index(1, _dummyContext.Customer));
            var contractsInModel = Assert.IsType<List<Contract>>(result.Model);
            Assert.Equal(2, contractsInModel.Count);
            Assert.Equal("Very important contract", contractsInModel[0].Name);
            Assert.Equal("Special contract", contractsInModel[1].Name);
            Assert.Equal(true, result.ViewData["HasContract"]);
        }
        #endregion

        #region -- Detail --
        [Fact]
        public void Detail_PassesSelectedContractInViewResultModel()
        {
            _contractRepository.Setup(c => c.GetByContractIdIncludingContractTypeAndTickets(_dummyContext.Contract1.ContractId)).Returns(_dummyContext.Contract1);
            var result = Assert.IsType<ViewResult>(_controller.Details(_dummyContext.Contract1.ContractId));
            var contractsInModel = Assert.IsType<Contract>(result.Model);
            //contract attributes
            Assert.Equal(_dummyContext.Contract1.ContractId, contractsInModel.ContractId);
            Assert.Equal(ContractStatus.Requested, contractsInModel.ContractStatus);
            Assert.Equal(1, contractsInModel.DurationInYears);
            // contract type
            Assert.Equal("24/7 Support", contractsInModel.ContractType.Name);
            Assert.Equal(SupportType.Always, contractsInModel.ContractType.SupportType);
            // contract ticket validation
            Assert.Equal(1, contractsInModel.Tickets.Count);
            Assert.Equal("Help my pc broke", contractsInModel.Tickets.First().Title);
            Assert.Equal("my screen is black", contractsInModel.Tickets.First().Description);
            // contract ticket comment validation
            Assert.Equal(1, contractsInModel.Tickets.First().Comments.Count);
            Assert.Equal("bert@email.com: The computer broke", contractsInModel.Tickets.First().Comments.First().Content);
        }
        #endregion

        #region -- Create GET --
        [Fact]
        public void Create_PassesNewContractInEditViewModel()
        {
            var result = Assert.IsType<ViewResult>(_controller.Create(_dummyContext.Customer));
            var contractCvm = Assert.IsType<ContractCreateViewModel>(result.Model);
            Assert.Null(contractCvm.Name);
        }
        #endregion

        #region -- Create POST --
        [Fact]
        public void Create_ValidContract_CreatesAndPersistsContractAndRedirectsToActionIndex()
        {
            _contractTypeRepository.Setup(c => c.GetByContractTypeId(_dummyContext.ContractType1.ContractTypeId)).Returns(_dummyContext.ContractType1);
            var contractCvm = new ContractCreateViewModel()
            {
                Name = "Name",
                ContractTypeId = _dummyContext.ContractType1.ContractTypeId,
                StartDate = DateTime.Now.AddYears(6).AddDays(2),
                DurationInYears = 1
            };
            _contractTypeRepository.Setup(c => c.GetByContractTypeId(contractCvm.ContractTypeId)).Returns(_dummyContext.ContractType1);
            var result = Assert.IsType<RedirectToActionResult>(_controller.Create(contractCvm, _dummyContext.Customer));
            Assert.Equal("Index", result?.ActionName);
            Assert.Equal(3, _dummyContext.Customer.Contracts.Count);
            Assert.Equal("Name", _dummyContext.Customer.Contracts.ElementAt(2).Name);
            _customerRepository.Verify(c => c.SaveChanges(), Times.Once());
        }

        [Fact]
        public void Create_InvalidContract_DoesNotCreateNorPersistsContractAndRedirectsToActionIndex()
        {
            _customerRepository.Setup(c => c.GetByCustomerId(_dummyContext.Customer.Id)).Returns(_dummyContext.Customer);
            var contractCvm = new ContractCreateViewModel()
            {
                Name = "",
                ContractTypeId = 1,
                DurationInYears = 1
            };
            var result = Assert.IsType<RedirectToActionResult>(_controller.Create(contractCvm, _dummyContext.Customer));
            Assert.Equal("Create", result?.ActionName);
            _customerRepository.Verify(c => c.SaveChanges(), Times.Never());
        }
        #endregion
    }

}