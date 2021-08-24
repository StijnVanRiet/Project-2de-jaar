using _2021_dotnet_g_01.Models;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Repositories
{
    public class ContractRepository : IContractRepository
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly DbSet<Contract> _contracts;

        public ContractRepository(ApplicationDbContext dbContext)
        {
            _dbContext = dbContext;
            _contracts = dbContext.Contracts;
        }

        public Contract GetByContractIdIncludingContractTypeAndTickets(int contractId)
        {
            return _contracts.Include(c => c.Tickets).Include(c => c.ContractType).SingleOrDefault(c => c.ContractId == contractId);
        }

        public Contract GetByContractId(int contractId)
        {
            return _contracts.SingleOrDefault(c => c.ContractId == contractId);
        }

        public IEnumerable<Contract> GetAll()
        {
            return _contracts.ToList();
        }

        public IEnumerable<Contract> GetAllIncludingContractTypes()
        {
            return _contracts.Include(c => c.ContractType).OrderBy(c => c.StartDate).ToList();
        }

        public void Add(Contract Contract)
        {
            _contracts.Add(Contract);
        }

        public void SaveChanges()
        {
            _dbContext.SaveChanges();
        }


    }
}
