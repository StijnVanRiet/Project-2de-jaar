using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public interface ICustomerRepository
    {
        IEnumerable<Customer> GetAll();
        Customer GetByCustomerName(string customerName);
        Customer GetByCustomerId(string customerId);
        void Add(Customer customer);
        void SaveChanges();
    }
}
