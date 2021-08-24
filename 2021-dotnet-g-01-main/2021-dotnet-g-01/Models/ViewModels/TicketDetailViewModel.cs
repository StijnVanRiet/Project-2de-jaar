using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace _2021_dotnet_g_01.Models.ViewModels
{
    public class TicketDetailViewModel
    {
        public string Title { get; set; }
        public string Description { get; set; }
        public DateTime CreationDate { get; set; }
        public TicketType TicketType { get; set; }
        public TicketStatus TicketStatus { get; set; }
        public string AssignedEngineer { get; set; }
        public ICollection<Comment> Comments { get; set; }

        [Display(Name = "Comment", Prompt = "Comment")]
        [Required(ErrorMessage = "Cannot add an empty comment.")]
        [StringLength(100, ErrorMessage = "{0} may not contain more than 100 characters.")]
        public string Comment { get; set; }

        public List<IFormFile> AttachmentFiles { get; set; }

        public TicketDetailViewModel()
        { }
        public TicketDetailViewModel(Ticket ticket)
        {
            Title = ticket.Title;
            Description = ticket.Description;
            CreationDate = ticket.CreationDate;
            TicketType = ticket.TicketType;
            TicketStatus = ticket.TicketStatus;
            AssignedEngineer = ticket.AssignedEngineer;
            Comments = ticket.Comments;
        }
        public TicketDetailViewModel(Ticket ticket, string comment) : this(ticket)
        {
            Comment = comment;
        }
    }
}