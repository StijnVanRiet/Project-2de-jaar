using _2021_dotnet_g_01.Models.Domain;
using _2021_dotnet_g_01.Models.Filters;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;

namespace _2021_dotnet_g_01.Controllers
{
    public class StatisticsController : Controller
    {

        private readonly ICustomerRepository _customerRepository;

        public StatisticsController(ICustomerRepository customerRepository)
        {
            _customerRepository = customerRepository;
        }

        [Authorize(Policy = "Customer")]
        [ServiceFilter(typeof(CustomerFilter))]
        public ActionResult Index(Customer customer)
        {

            IEnumerable<Ticket> tickets = _customerRepository.GetByCustomerId(customer.Id).GetTickets();
            List<DataPoint> dataPoints = new List<DataPoint>();

            foreach (TicketType tt in Enum.GetValues(typeof(TicketType)))
            {
                int aantalTicketsVoorDitType = tickets.Where(t => t.TicketType.Equals(tt)).Count();
                dataPoints.Add(new DataPoint(aantalTicketsVoorDitType, tt.GetString(), tt.GetString()));
            }

            ViewBag.DataPoints = JsonConvert.SerializeObject(dataPoints);

            return View();
        }

    }

}