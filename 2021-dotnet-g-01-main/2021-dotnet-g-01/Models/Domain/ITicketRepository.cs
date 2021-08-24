using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public interface ITicketRepository
    {
        Ticket GetByTicketId(int ticketId);
        Ticket GetByTicketIdIncludingCommentsAndAttachments(int ticketId);
        IEnumerable<Ticket> GetAll();
        void Add(Ticket ticket);
        void SaveChanges();
    }
}
