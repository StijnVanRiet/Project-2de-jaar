using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Repositories
{
    public class LoginAttemptRepository : ILoginAttemptRepository
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly DbSet<LoginAttempt> _loginAttempts;

        public LoginAttemptRepository(ApplicationDbContext dbContext)
        {
            _dbContext = dbContext;
            _loginAttempts = dbContext.LoginAttempts;
        }


        public IEnumerable<LoginAttempt> GetByCustomerId(string customerId)
        {
            return _loginAttempts.Where(l => l.CustomerId.Equals(customerId)).ToList();
        }
        public void Add(LoginAttempt loginAttempt)
        {
            _loginAttempts.Add(loginAttempt);
        }

        public void SaveChanges()
        {
            _dbContext.SaveChanges();
        }
    }
}
