using _2021_dotnet_g_01.Models.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class CustomerTest
    {
        private readonly Customer _customer;
        private readonly Contract _contract;
        private readonly ContractType _contractType;

        public CustomerTest()
        {
            _customer = new Customer("user@mail.com");
            _contractType = new ContractType("24/7 Support", SupportType.Always);
            _contract = new Contract("Contract", DateTime.Now, _contractType, 1);
        }

        [Fact]
        public void NewCustomer_StatusActive()
        {
            //Assert
            Assert.Equal(Status.Active, _customer.Status);
        }

        [Fact]
        public void NewCustomer_StartDateNow()
        {
            //Assert
            Assert.Equal(DateTime.Now.ToString("MM/dd/yyyy H:mm"), _customer.StartDate.ToString("MM/dd/yyyy H:mm"));
        }

        [Fact]
        public void NewCustomer_HasEmptyContractList()
        {
            //Assert
            Assert.False(_customer.Contracts.Any());
        }

        [Theory]
        [InlineData("")] //empty
        [InlineData("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuser@mail.com")] //too long
        public void NewCustomer_WrongUserName_Fails(string userName)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Customer(userName));
        }

        [Fact]
        public void AddContract_AddsContract()
        {
            //Act
            _customer.AddContract(_contract);
            //Assert
            Assert.Equal(_customer.Contracts.LastOrDefault(), _contract);
        }

        [Fact]
        public void AddContract_AddsContractInOccupiedTimeSpan_Fails()
        {
            //Arrange
            Contract contract = new Contract("Contract2", DateTime.Now.AddYears(0), _contractType, 1);
            _customer.AddContract(_contract);
            //Act
            //Assert
            Assert.Throws<ArgumentException>(() => _customer.AddContract(contract));
        }

        [Fact]
        public void AddContract_AddsContractWithAlreadyExistingName_Fails()
        {
            //Arrange
            Contract contract = new Contract("Contract", DateTime.Now.AddYears(1).AddDays(1), _contractType, 1);
            _customer.AddContract(_contract);
            //Act
            //Assert
            Assert.Throws<ArgumentException>(() => _customer.AddContract(contract));
        }

        [Fact]
        public void GetActiveContract_GivesActiveContract()
        {
            //Arrange
            Contract contract = new Contract("Contract2", DateTime.Now.AddYears(1).AddDays(1), _contractType, 1);
            _customer.AddContract(contract);
            _customer.AddContract(_contract);
            //Act
            //Assert
            Assert.Equal(_customer.GetActiveContract(), _contract);
        }

        [Fact]
        public void IsOwnerOfContract_ReturnsFalse()
        {
            //Assert
            Assert.False(_customer.IsOwnerOfContract(_contract));
        }

        [Fact]
        public void IsOwnerOfTicket_ReturnsTrue()
        {
            //Arrange
            Ticket ticket = new Ticket("Title", "description", TicketType.NotUrgent);
            _contract.AddTicket(ticket);
            _customer.AddContract(_contract);
            //Act
            //Assert
            Assert.True(_customer.IsOwnerOfTicket(ticket));
        }

        [Fact]
        public void GetTickets_ReturnsTickets()
        {
            //Arrange
            Ticket ticket1 = new Ticket("Title", "description", TicketType.NotUrgent);
            Ticket ticket2 = new Ticket("Title", "description", TicketType.NotUrgent);
            List<Ticket> tickets = new List<Ticket>();
            tickets.Add(ticket1);
            tickets.Add(ticket2);
            _contract.AddTicket(ticket1);
            _contract.AddTicket(ticket2);
            _customer.AddContract(_contract);
            //Act
            //Assert
            Assert.Equal(_customer.GetTickets(), tickets);
        }
    }
}
