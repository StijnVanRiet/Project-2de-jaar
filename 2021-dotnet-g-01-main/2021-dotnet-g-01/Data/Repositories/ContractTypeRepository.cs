using _2021_dotnet_g_01.Models;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Repositories
{
    public class ContractTypeRepository : IContractTypeRepository
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly DbSet<ContractType> _contractTypes;

        public ContractTypeRepository(ApplicationDbContext dbContext)
        {
            _dbContext = dbContext;
            _contractTypes = dbContext.ContractTypes;
        }

        public IEnumerable<ContractType> GetAll()
        {
            return _contractTypes.ToList();
        }

        public ContractType GetByContractTypeId(int contractTypeId)
        {
            return _contractTypes.SingleOrDefault(ct => ct.ContractTypeId == contractTypeId);
        }

    }
}
