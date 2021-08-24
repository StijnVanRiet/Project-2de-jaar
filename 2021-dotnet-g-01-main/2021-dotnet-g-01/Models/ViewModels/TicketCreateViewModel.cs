using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Mvc;
using System.ComponentModel.DataAnnotations;

namespace _2021_dotnet_g_01.Models.ViewModels
{
    public class TicketCreateViewModel
    {
        [Display(Name = "UserName")]
        [Required(ErrorMessage = "The Customer field is required.")]
        [Remote("IsValidCustomer", "Ticket")]
        public string UserName { get; set; }

        [Display(Name = "TicketType")]
        [Required(ErrorMessage = "The Ticket type field is required.")]
        public TicketType TicketType { get; set; }

        [Display(Name = "AssignedEngineer")]
        public string AssignedEngineer { get; set; }

        [Display(Name = "Title", Prompt = "Title")]
        [Required(ErrorMessage = "The Title field is required.")]
        [StringLength(50, ErrorMessage = "{0} may not contain more than 50 characters.")]
        [RegularExpression(@"[A-Z][a-zA-Z\s]*", ErrorMessage = "Please enter a valid Title (must start with a capital letter).")]
        public string Title { get; set; }

        [Display(Name = "Description", Prompt = "Description")]
        [Required(ErrorMessage = "The Description field is required.")]
        [StringLength(100, ErrorMessage = "{0} may not contain more than 100 characters.")]
        public string Description { get; set; }

        public TicketCreateViewModel()
        { }
        public TicketCreateViewModel(Ticket ticket, Customer customer)
        {
            UserName = customer.UserName;
            Title = ticket.Title;
            Description = ticket.Description;
            TicketType = ticket.TicketType;
            AssignedEngineer = ticket.AssignedEngineer;
        }
    }
}