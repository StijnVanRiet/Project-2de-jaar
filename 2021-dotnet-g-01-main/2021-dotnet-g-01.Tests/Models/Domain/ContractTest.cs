using _2021_dotnet_g_01.Models.Domain;
using System;
using System.Linq;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class ContractTest
    {
        private readonly Contract _contract;
        private readonly ContractType _contractType;

        public ContractTest()
        {
            _contractType = new ContractType("24/7 Support", SupportType.Always);
            _contract = new Contract("Contract", DateTime.Now, _contractType, 1);
        }

        [Fact]
        public void NewContract_EndDateIsDurationInYearsAddedToStartDate()
        {
            //Assert
            Assert.Equal(_contract.EndDate, _contract.StartDate.AddYears(_contract.DurationInYears));
        }

        [Fact]
        public void NewContract_SetsContractType()
        {
            Assert.Equal(_contractType, _contract.ContractType);
        }

        [Theory]
        [InlineData("")] //empty
        [InlineData("Naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaame")] //too long
        [InlineData("nam@#e")] //wrong format
        public void NewContract_WrongName_Fails(string name)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Contract(name, DateTime.Now, _contractType, 1));
        }

        [Fact]
        public void NewContract_StartDateHasPassed_Fails()
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Contract("Name", new DateTime(2000, 2, 2), _contractType, 1));
        }

        [Theory]
        [InlineData(0)] //zero
        [InlineData(-1)] //negative
        public void NewContract_WrongDuration_Fails(int duration)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Contract("Name", DateTime.Now, _contractType, duration));
        }

        [Fact]
        public void AddTicket_AddsTicket()
        {
            //Arrange
            Ticket ticket = new Ticket("Problem", "description", TicketType.NotUrgent);
            //Act
            _contract.AddTicket(ticket);
            //Assert
            Assert.Equal(_contract.Tickets.LastOrDefault(), ticket);
        }

        [Fact]
        public void IsOwnerOfTicket_ReturnsTrue()
        {
            //Arrange
            Ticket ticket = new Ticket("Problem", "description", TicketType.NotUrgent);
            _contract.AddTicket(ticket);
            //Act
            //Assert
            Assert.True(_contract.IsOwnerOfTicket(ticket));
        }

        [Fact]
        public void IsOwnerOfTicket_ReturnsFalse()
        {
            //Arrange
            Ticket ticket = new Ticket("Problem", "description", TicketType.NotUrgent);
            //Act
            //Assert
            Assert.False(_contract.IsOwnerOfTicket(ticket));
        }

        [Fact]
        public void IsOpen_ReturnsTrue()
        {
            //Assert
            Assert.True(_contract.IsActive());
        }

        [Fact]
        public void IsOpen_ReturnsFalse()
        {
            //Arrange
            Contract contract = new Contract("Contract", DateTime.Now.AddYears(1), _contractType, 1);
            //Act
            //Assert
            Assert.False(contract.IsActive());
        }

        [Fact]
        public void IsOverlappingWith_ReturnsTrue()
        {
            //Arrange
            Contract contract = new Contract("Contract", DateTime.Now.AddYears(0), _contractType, 1);
            //Act
            //Assert
            Assert.True(_contract.IsOverlappingWith(contract.StartDate, contract.EndDate));
        }

        [Fact]
        public void IsOverlappingWith_ReturnsFalse()
        {
            //Arrange
            Contract contract = new Contract("Contract", DateTime.Now.AddYears(1).AddDays(1), _contractType, 1);
            //Act
            //Assert
            Assert.False(_contract.IsOverlappingWith(contract.StartDate, contract.EndDate));
        }
    }
}
