using _2021_dotnet_g_01.Models.Domain;
using System;
using System.Collections.Generic;

namespace _2021_dotnet_g_01.Tests.Data
{
    public class DummyApplicationDbContext
    {
        public IEnumerable<ContractType> ContractTypes { get; }
        public IEnumerable<Contract> Contracts { get; }
        public IEnumerable<LoginAttempt> LoginAttempts { get; }
        public IEnumerable<Comment> Comments { get; }
        public IEnumerable<Ticket> Tickets { get; }
        public Customer Customer;
        public ContractType ContractType1;
        public ContractType ContractType2;
        public Contract Contract1;
        public Contract Contract2;
        public LoginAttempt LoginAttempt1;
        public LoginAttempt LoginAttempt2;
        public Comment Comment1;
        public Comment Comment2;
        public Ticket Ticket1;
        public Ticket Ticket2;

        public DummyApplicationDbContext()
        {
            // ContractTypes
            ContractType1 = new ContractType("24/7 Support", SupportType.Always);
            ContractType2 = new ContractType("Regular Support", SupportType.WorkingHours);
            ContractTypes = new[] { ContractType1, ContractType2 };
            // Comments
            Comment1 = new Comment("bert@email.com: The computer broke");
            Comment2 = new Comment("bert@email.com: Have you tried turning it on and off again?");
            Comments = new[] { Comment1, Comment2 };
            // Tickets
            Ticket1 = new Ticket("Help my pc broke", "my screen is black", TicketType.NotUrgent);
            Ticket1.AddComment(Comment1);
            Ticket2 = new Ticket("Need new mouse", "my mouse broke", TicketType.Urgent);
            Ticket2.AddComment(Comment2);
            Tickets = new[] { Ticket1, Ticket2 };
            // Contracts
            Contract1 = new Contract("Very important contract", DateTime.Now, ContractType1, 1);
            Contract1.AddTicket(Ticket1);
            Contract2 = new Contract("Special contract", DateTime.Now.AddYears(1).AddDays(1), ContractType2, 5);
            Contract2.AddTicket(Ticket2);
            Contracts = new[] { Contract1, Contract2 };
            // Customer
            Customer = new Customer("user@email.com");
            Customer.Email = Customer.UserName;
            Customer.AddContract(Contract1);
            Customer.AddContract(Contract2);
            // LoginAttempts
            LoginAttempt1 = new LoginAttempt(Customer.Id, true);
            LoginAttempt2 = new LoginAttempt(Customer.Id, false);
            LoginAttempts = new[] { LoginAttempt1, LoginAttempt2 };
        }

    }
}
