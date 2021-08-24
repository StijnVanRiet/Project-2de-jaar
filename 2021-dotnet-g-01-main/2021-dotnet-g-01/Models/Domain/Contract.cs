using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class Contract
    {
        #region Fields
        private string _name;
        private int _durationInYears;
        private DateTime _startDate;
        #endregion

        #region Properties
        public int ContractId { get; set; }
        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                    throw new ArgumentException("A contract must have a name");
                if (value.Length > 50)
                    throw new ArgumentException("Name of contract may not exceed 50 characters");
                if (!Regex.IsMatch(value, @"[A-Z][a-zA-Z\s]*"))
                    throw new ArgumentException("Name of contract may only contain letters and must start with a capital letter");
                _name = value;
            }
        }
        public ICollection<Ticket> Tickets { get; set; }
        public ContractType ContractType { get; set; }
        public int DurationInYears
        {
            get
            {
                return _durationInYears;
            }
            set
            {
                if (value <= 0)
                    throw new ArgumentException("Duration must be greater than zero");
                _durationInYears = value;
            }
        }
        public DateTime StartDate
        {
            get
            {
                return _startDate;
            }
            set
            {
                if (value < DateTime.Today)
                    throw new ArgumentException("Start date cannot be in the past");
                _startDate = value;
            }
        }
        public DateTime EndDate { get; set; }
        public ContractStatus ContractStatus { get; set; }
        #endregion

        #region Constructors
        protected Contract()
        {
            Tickets = new List<Ticket>();
            ContractStatus = ContractStatus.Requested;
        }
        public Contract(string name, DateTime startDate, ContractType contractType, int durationInYears) : this()
        {
            Name = name.Trim();
            ContractType = contractType;
            DurationInYears = durationInYears;
            StartDate = startDate;
            EndDate = StartDate.AddYears(durationInYears);

        }
        #endregion

        #region Methods
        public bool IsActive()
        {

            return ContractStatus != ContractStatus.Ended && (DateTime.Now.Ticks > StartDate.Ticks && DateTime.Now.Ticks < EndDate.Ticks);
        }

        public bool IsOverlappingWith(DateTime startDate, DateTime endDate)
        {
            return ContractStatus != ContractStatus.Ended ? (StartDate.Date < endDate.AddDays(1).Date && startDate.Date < EndDate.AddDays(1).Date) : false;
        }

        public bool IsOwnerOfTicket(Ticket ticket)
        {
            return Tickets.Contains(ticket);
        }

        public void AddTicket(Ticket ticket)
        {
            Tickets.Add(ticket);
        }

        public List<Ticket> GiveRunningTickets()
        {
            return Tickets.Where(t => t.TicketStatus != TicketStatus.Cancelled && t.TicketStatus != TicketStatus.Closed).ToList();
        }

        public void CancelAllTickets()
        {
            //alle tickets die nog niet gecancelled of geclosed zijn worden gecancelled

            //lijst met alle tickets die al gecancelled of geclosed zijn 
            List<Ticket> l1 = Tickets.Where(t => t.TicketStatus == TicketStatus.Cancelled || t.TicketStatus == TicketStatus.Closed).ToList();
            //lijst met alle running tickets die worden gecancelled
            List<Ticket> l2 = GiveRunningTickets().Select(t => { t.TicketStatus = TicketStatus.Cancelled; return t; }).ToList();
            //merge lists
            l1.AddRange(l2);
            Tickets = l1;
        }
        #endregion
    }

    public enum ContractStatus
    {
        Requested = 1,
        Running = 2,
        Ended = 3
    }
}
