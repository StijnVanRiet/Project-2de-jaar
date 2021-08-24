using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace _2021_dotnet_g_01.Data.Repositories
{
    public class CustomerRepository : ICustomerRepository
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly DbSet<Customer> _customers;

        public CustomerRepository(ApplicationDbContext dbContext)
        {
            _dbContext = dbContext;
            _customers = dbContext.Customers;
        }

        //hoe customer van supportmanager onderscheiden?
        public IEnumerable<Customer> GetAll()
        {
            return _customers.Include(c => c.Contracts).ThenInclude(c => c.Tickets).Include(c => c.Contracts).ThenInclude(c => c.ContractType).ToList();
        }

        public Customer GetByCustomerName(string customerName)
        {
            return _customers.Include(c => c.Contracts).ThenInclude(c => c.Tickets).Include(c => c.Contracts).ThenInclude(c => c.ContractType).SingleOrDefault(c => c.UserName.Equals(customerName));
        }

        public Customer GetByCustomerId(string customerId)
        {
            return _customers.Include(c => c.Contracts).ThenInclude(c => c.Tickets).Include(c => c.Contracts).ThenInclude(c => c.ContractType).SingleOrDefault(c => c.Id.Equals(customerId));
        }


        public void Add(Customer customer)
        {
            _customers.Add(customer);
        }

        public void SaveChanges()
        {
            _dbContext.SaveChanges();
        }

    }
}
