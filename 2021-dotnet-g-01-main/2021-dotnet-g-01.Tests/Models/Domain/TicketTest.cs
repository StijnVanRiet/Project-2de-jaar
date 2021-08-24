using _2021_dotnet_g_01.Models.Domain;
using System;
using System.Linq;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class TicketTest
    {
        private readonly Ticket _ticket;

        public TicketTest()
        {
            _ticket = new Ticket("Title", "description", TicketType.NotUrgent); ;
        }

        [Fact]
        public void NewTicket_StatusCreated()
        {
            //Assert
            Assert.Equal(TicketStatus.Created, _ticket.TicketStatus);
        }

        [Fact]
        public void NewTicket_CreationDateNow()
        {
            //Assert
            Assert.Equal(DateTime.Now.ToString("MM/dd/yyyy H:mm"), _ticket.CreationDate.ToString("MM/dd/yyyy H:mm"));
        }

        [Fact]
        public void NewTicket_HasEmptyCommentList()
        {
            //Assert
            Assert.False(_ticket.Comments.Any());
        }

        [Theory]
        [InlineData("")] //empty
        [InlineData("Tiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiitle")] //too long
        [InlineData("t@iiiiiiiitle")] //wrong format
        [InlineData("title")] //wrong format
        public void NewTicket_WrongTitle_Fails(string title)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Ticket(title, "description", TicketType.NotUrgent));
        }

        [Theory]
        [InlineData("")] //empty
        [InlineData("Deeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeescription")] //too long
        public void NewTicket_WrongDescription_Fails(string description)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Ticket("Title", description, TicketType.NotUrgent));
        }

        [Fact]
        public void AddComment_AddsComment()
        {
            //Arrange
            Comment comment = new Comment("user@mail.com: This is a comment");
            //Act
            _ticket.AddComment(comment);
            //Assert
            Assert.Equal(_ticket.Comments.LastOrDefault(), comment);
        }

        [Fact]
        public void AddComment_AddsCommentToClosedTicket_Fails()
        {
            //Arrange
            Comment comment = new Comment("user@mail.com: This is a comment");
            _ticket.TicketStatus = TicketStatus.Closed;
            //Act
            //Assert
            Assert.Throws<ArgumentException>(() => _ticket.AddComment(comment));
        }
    }
}
