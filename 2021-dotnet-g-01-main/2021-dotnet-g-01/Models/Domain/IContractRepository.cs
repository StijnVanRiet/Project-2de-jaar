using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public interface IContractRepository
    {
        Contract GetByContractIdIncludingContractTypeAndTickets(int contractId);
        Contract GetByContractId(int contractId);
        IEnumerable<Contract> GetAll();
        IEnumerable<Contract> GetAllIncludingContractTypes();
        void Add(Contract contract);
        void SaveChanges();
    }
}
