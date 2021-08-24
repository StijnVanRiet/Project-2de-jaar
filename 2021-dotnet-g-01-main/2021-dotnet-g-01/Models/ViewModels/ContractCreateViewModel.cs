using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;

namespace _2021_dotnet_g_01.Models.ViewModels
{
    public class ContractCreateViewModel
    {
        [Display(Name = "Name", Prompt = "Name")]
        [Required(ErrorMessage = "The Name field is required.")]
        [StringLength(50, ErrorMessage = "{0} may not contain more than 50 characters.")]
        [RegularExpression(@"[A-Z][a-zA-Z\s]*", ErrorMessage = "Please enter a valid Name (must start with a capital letter).")]
        [Remote("IsValidName", "Contract")]
        public string Name { get; set; }

        [Display(Name = "ContractTypeId")]
        [Required(ErrorMessage = "The Contract type field is required.")]
        public int ContractTypeId { get; set; }

        [Display(Name = "StartDate")]
        [Required(ErrorMessage = "The Start date field is required.")]
        [Remote("IsValidStartDate", "Contract", AdditionalFields = nameof(DurationInYears))]
        [DataType(DataType.Date)]
        public DateTime StartDate { get; set; }

        [Display(Name = "DurationInYears")]
        [Required(ErrorMessage = "The Duration in years field is required.")]
        [Remote("IsValidDurationInYears", "Contract", AdditionalFields = nameof(StartDate))]
        public int DurationInYears { get; set; }

        public ICollection<Contract> Contracts { get; set; }

        public ContractCreateViewModel()
        { }

        public ContractCreateViewModel(Customer customer)
        {
            Contracts = customer.Contracts.Where(c => c.ContractStatus != ContractStatus.Ended).OrderBy(c => c.StartDate).ToList();
        }

    }
}