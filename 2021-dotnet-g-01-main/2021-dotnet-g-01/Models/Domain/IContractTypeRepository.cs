using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public interface IContractTypeRepository
    {
        IEnumerable<ContractType> GetAll();
        ContractType GetByContractTypeId(int contractTypeId);
    }
}
