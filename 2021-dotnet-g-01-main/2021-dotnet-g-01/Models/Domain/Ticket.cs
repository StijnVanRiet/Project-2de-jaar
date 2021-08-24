using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class Ticket
    {
        #region Fields
        private string _title;
        private string _description;
        private string _assignedEngineer;
        #endregion

        #region Properties
        public int TicketId { get; set; }
        public DateTime CreationDate { get; set; }
        public string Title
        {
            get
            {
                return _title;
            }
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                    throw new ArgumentException("A ticket must have a title");
                if (value.Length > 50)
                    throw new ArgumentException("Title of ticket may not exceed 50 characters");
                if (!Regex.IsMatch(value, @"[A-Z][a-zA-Z\s]*"))
                    throw new ArgumentException("Title of ticket may only contain letters and must start with a capital letter");
                _title = value;
            }
        }
        public string Description
        {
            get
            {
                return _description;
            }
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                    throw new ArgumentException("Description cannot be empty");
                if (value.Length > 100)
                    throw new ArgumentException("Description may not exceed 100 characters");
                _description = value;
            }
        }
        public ICollection<Comment> Comments { get; set; }      //Comment klasse bestaat omdat hij geen lijst van objecten kan mappen zonder dat een klasse is
        public TicketType TicketType { get; set; }
        public TicketStatus TicketStatus { get; set; }
        public string AssignedEngineer
        {
            get
            {
                return _assignedEngineer;
            }
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                    _assignedEngineer = "";
                else
                    _assignedEngineer = value;
            }
        }//Voor dotNet mag er met een vaste engineer gewerkt worden
        #endregion

        //Ticket moet geen customer kennen, want contract kent zowel ticket als customer

        #region Constructors
        protected Ticket()
        {

        }

        public Ticket(string title, string description, TicketType ticketType) : base()
        {
            Title = title.Trim();
            Description = description;
            TicketType = ticketType;

            CreationDate = DateTime.Now;
            Comments = new List<Comment>();
            TicketStatus = TicketStatus.Created;
            AssignedEngineer = "";

        }

        public Ticket(string title, string description, TicketType ticketType, string assignedEngineer = "") : this(title, description, ticketType)
        {
            AssignedEngineer = assignedEngineer;
        }
        #endregion

        #region Methods
        public void AddComment(Comment comment)
        {
            if (!IsEditable())
            {
                throw new ArgumentException($"{Title} is {TicketStatus.ToString().ToLower()} and cannot add comments");
            }
            Comments.Add(comment);
        }

        public bool IsEditable()
        {
            return (!TicketStatus.Equals(TicketStatus.Closed) && !TicketStatus.Equals(TicketStatus.Cancelled));
        }
        #endregion
    }

    public enum TicketStatus
    {
        Created = 1,
        Pending = 2,
        Closed = 3,
        Cancelled = 4
    }

    public enum TicketType
    {
        Urgent = 1,
        NotUrgent = 2,
        SolvedWithinADay = 3,
        SolvedWithinThreeBusinessDays = 4,
        SolvedWithinAWeek = 5
    }

    public static class TicketTypeExtensions
    {
        public static string GetString(this TicketType tt)
        {
            var r = new Regex(@"
                (?<=[A-Z])(?=[A-Z][a-z]) |
                 (?<=[^A-Z])(?=[A-Z]) |
                 (?<=[A-Za-z])(?=[^A-Za-z])", RegexOptions.IgnorePatternWhitespace);
            // ThisIsAnExampleSentence --> This is an example sentence
            return new String(r.Replace(tt.ToString(), " ").Select((ch, index) => (index == 0) ? ch : Char.ToLower(ch)).ToArray());
        }
    }
}
