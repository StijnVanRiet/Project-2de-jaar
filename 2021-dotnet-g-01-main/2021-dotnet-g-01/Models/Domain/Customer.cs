using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.Linq;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class Customer : IdentityUser
    {
        #region Fields
        #endregion

        #region Properties
        public DateTime StartDate { get; set; }
        public Status Status { get; set; }
        public ICollection<Contract> Contracts { get; set; }
        public bool IsSupportManager { get; set; }
        #endregion

        #region Constructors
        public Customer() : this("noName")
        {

        }

        public Customer(string username, bool? isSupportManager = false) : base(username)
        {
            if (string.IsNullOrWhiteSpace(username))
                throw new ArgumentException("A customer must have a username");
            if (username.Length > 30)
                throw new ArgumentException("Username of customer may not exceed 30 characters");
            StartDate = DateTime.Now;
            Status = Status.Active;
            Contracts = new List<Contract>();
            IsSupportManager = (bool)isSupportManager;
        }
        #endregion

        #region Methods
        public Contract GetActiveContract()
        {
            //if(!HasActiveContract())
            //{
            //    throw new ArgumentException("Customer does not have any contracts");
            //}
            return Contracts.ToArray().SingleOrDefault(c => c.IsActive());
        }

        public bool HasActiveContract()
        {
            return GetActiveContract() != null;
        }

        public bool IsOwnerOfContract(Contract contract)
        {
            return Contracts.Contains(contract);
        }

        public bool IsOwnerOfTicket(Ticket ticket)
        {
            return Contracts.ToArray().Where(c => c.IsOwnerOfTicket(ticket)).Count() == 1;
        }

        public IEnumerable<Ticket> GetTickets()
        {
            List<Ticket> tickets = new List<Ticket>();
            foreach (Contract contract in Contracts)
            {
                tickets.AddRange(contract.Tickets);
            }

            return tickets.OrderBy(t => t.Title).ToList();
        }

        public bool IsContractTimespanOccupied(DateTime startDate, DateTime endDate)
        {
            return Contracts.ToArray().Where(c => c.IsOverlappingWith(startDate, endDate)).Count() != 0;
        }

        public void AddContract(Contract contract)
        {
            if (IsContractTimespanOccupied(contract.StartDate, contract.EndDate))
            {
                throw new ArgumentException("a contract cannot overlap with an existing contract");
            }
            if (!IsContractNameUnique(contract.Name))
            {
                throw new ArgumentException("a contract with this name already exists");
            }
            Contracts.Add(contract);
        }

        public bool IsContractNameUnique(string name)
        {
            //maak list van contract names
            List<string> names = Contracts.Select(c => c.Name).ToList();
            //voeg nieuwe naam toe
            names.Add(name.Trim());
            //check voor duplicates
            return names.Distinct().Count() == names.Count();
        }
        #endregion
    }

    public enum Status
    {
        Active,
        Blocked,
        Nonactive
    }
}
