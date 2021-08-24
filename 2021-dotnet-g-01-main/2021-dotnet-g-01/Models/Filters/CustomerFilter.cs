using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Mvc.Filters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Filters
{
    public class CustomerFilter : ActionFilterAttribute
    {
        private readonly ICustomerRepository _customerRepository;

        public CustomerFilter(ICustomerRepository customerRepository)
        {
            _customerRepository = customerRepository;
        }

        public override void OnActionExecuting(ActionExecutingContext context)
        {
            context.ActionArguments["customer"] = context.HttpContext.User.Identity.IsAuthenticated ? _customerRepository.GetByCustomerName(context.HttpContext.User.Identity.Name) : null;
            base.OnActionExecuting(context);
        }

    }
}
