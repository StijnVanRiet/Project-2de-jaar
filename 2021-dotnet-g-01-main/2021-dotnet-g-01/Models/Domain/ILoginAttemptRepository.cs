using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public interface ILoginAttemptRepository
    {
        IEnumerable<LoginAttempt> GetByCustomerId(string customerId);
        void Add(LoginAttempt loginAttempt);
        void SaveChanges();
    }
}
