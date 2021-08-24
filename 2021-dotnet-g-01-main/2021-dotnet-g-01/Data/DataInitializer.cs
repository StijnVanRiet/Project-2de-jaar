using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Identity;
using System;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data
{
    public class DataInitializer
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly UserManager<Customer> _userManager;

        public DataInitializer(ApplicationDbContext dbContext, UserManager<Customer> userManager)
        {
            _dbContext = dbContext;
            _userManager = userManager;
        }

        public async Task InitializeData()
        {
            _dbContext.Database.EnsureDeleted();
            if (_dbContext.Database.EnsureCreated())
            {
                await InitializeSupportManagersAndCustomers();

                //ContractTypes
                ContractType contractType1 = new ContractType("24/7 Support", SupportType.Always);
                ContractType contractType2 = new ContractType("Regular Support", SupportType.WorkingHours);
                ContractType contractType3 = new ContractType("Weekend Support", SupportType.Weekends);
                _dbContext.ContractTypes.Add(contractType1);
                _dbContext.ContractTypes.Add(contractType2);
                _dbContext.ContractTypes.Add(contractType3);
                _dbContext.SaveChanges();

                //Contracts
                Contract contract1 = new Contract("Very important contract", DateTime.Now, contractType1, 1);
                Contract contract2 = new Contract("The usual contract", DateTime.Now, contractType2, 5);
                Contract contract3 = new Contract("Special contract", DateTime.Now.AddYears(2).AddDays(2), contractType3, 3); //anders overlappen contracten
                _dbContext.Contracts.Add(contract1);
                _dbContext.Contracts.Add(contract2);
                _dbContext.Contracts.Add(contract3);
                _dbContext.SaveChanges();

                //Link Customers
                Customer hans = _dbContext.Customers.SingleOrDefault(c => c.UserName.Equals("hans@email.com"));
                Customer frank = _dbContext.Customers.SingleOrDefault(c => c.UserName.Equals("frank@email.com"));
                Customer jan = _dbContext.Customers.SingleOrDefault(c => c.UserName.Equals("jan@email.com"));
                Customer johan = _dbContext.Customers.SingleOrDefault(c => c.UserName.Equals("johan@email.com"));
                hans.AddContract(contract1);
                hans.AddContract(contract3);
                frank.AddContract(contract2);       
                _dbContext.SaveChanges();

                //LoginAttempts
                LoginAttempt test1 = new LoginAttempt(hans.Id, true);
                LoginAttempt test2 = new LoginAttempt(frank.Id, true);
                LoginAttempt test3 = new LoginAttempt(hans.Id, false);
                LoginAttempt test4 = new LoginAttempt(jan.Id, true);
                LoginAttempt test5 = new LoginAttempt(hans.Id, false);
                _dbContext.LoginAttempts.Add(test1);
                _dbContext.LoginAttempts.Add(test2);
                _dbContext.LoginAttempts.Add(test3);
                _dbContext.LoginAttempts.Add(test4);
                _dbContext.LoginAttempts.Add(test5);
                _dbContext.SaveChanges();

                //Comments
                Comment comment1 = new Comment("bert@email.com: This is a comment");
                Comment comment2 = new Comment("bert@email.com: Yes");
                Comment comment3 = new Comment("bert@email.com: No");
                Comment comment4 = new Comment("bert@email.com: The computer broke");
                Comment comment5 = new Comment("bert@email.com: Have you tried turning it on and off again?");
                Comment comment6 = new Attachment("hans@email.com: It looks like this", "12c3a3e9-7ada-463a-9534-62a437781be3.jpg");


                //Tickets
                Ticket ticket1 = new Ticket("Help my pc broke", "my screen is black", TicketType.NotUrgent);
                contract1.AddTicket(ticket1);
                ticket1.AddComment(comment1);
                ticket1.AddComment(comment2);
                ticket1.AddComment(comment6);
                Ticket ticket2 = new Ticket("Program is not working", "program gives bluescreen", TicketType.NotUrgent);
                contract2.AddTicket(ticket2);
                ticket2.AddComment(comment3);
                Ticket ticket3 = new Ticket("Need new mouse", "my mouse broke", TicketType.Urgent);
                contract1.AddTicket(ticket3);
                ticket3.AddComment(comment4);
                ticket3.AddComment(comment5);
                _dbContext.Tickets.Add(ticket1);
                _dbContext.Tickets.Add(ticket2);
                _dbContext.Tickets.Add(ticket3);
                _dbContext.SaveChanges();
            }
        }

        private async Task InitializeSupportManagersAndCustomers()
        {
            Customer hans = new Customer { UserName = "hans@email.com", Email = "hans@email.com" };
            await _userManager.CreateAsync(hans, "P@ssword1");
            await _userManager.AddClaimAsync(hans, new Claim(ClaimTypes.Role, "customer"));

            Customer frank = new Customer { UserName = "frank@email.com", Email = "frank@email.com" };
            await _userManager.CreateAsync(frank, "P@ssword1");
            await _userManager.AddClaimAsync(frank, new Claim(ClaimTypes.Role, "customer"));

            //Customer zonder contracts of tickets, om te testen
            Customer jan = new Customer { UserName = "jan@email.com", Email = "jan@email.com" };
            await _userManager.CreateAsync(jan, "P@ssword1");
            await _userManager.AddClaimAsync(jan, new Claim(ClaimTypes.Role, "customer"));

            //Customer die blocked is, om te testen
            Customer pierre = new Customer { UserName = "pierre@email.com", Email = "pierre@email.com", Status = Status.Blocked };
            await _userManager.CreateAsync(pierre, "P@ssword1");
            await _userManager.AddClaimAsync(pierre, new Claim(ClaimTypes.Role, "customer"));

            //Beetje onlogisch, maar een supportManager is ook een customer
            Customer bert = new Customer { UserName = "bert@email.com", Email = "bert@email.com", IsSupportManager = true };
            await _userManager.CreateAsync(bert, "P@ssword1");
            await _userManager.AddClaimAsync(bert, new Claim(ClaimTypes.Role, "supportManager"));

            //Beetje onlogisch, maar een supportManager is ook een customer
            Customer johan = new Customer { UserName = "johan@email.com", Email = "johan@email.com", IsSupportManager = true };
            await _userManager.CreateAsync(johan, "P@ssword1");
            await _userManager.AddClaimAsync(johan, new Claim(ClaimTypes.Role, "supportManager"));
        }
    }
}
