using _2021_dotnet_g_01.Models;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Repositories
{
    public class TicketRepository : ITicketRepository
    {
        private readonly ApplicationDbContext _dbContext;
        private readonly DbSet<Ticket> _tickets;

        public TicketRepository(ApplicationDbContext dbContext)
        {
            _dbContext = dbContext;
            _tickets = dbContext.Tickets;
        }

        public Ticket GetByTicketId(int ticketId)
        {
            return _tickets.SingleOrDefault(t => t.TicketId == ticketId);
        }

        public Ticket GetByTicketIdIncludingCommentsAndAttachments(int ticketId)
        {
            return _tickets.Include(t => t.Comments).SingleOrDefault(t => t.TicketId == ticketId);
        }

        public IEnumerable<Ticket> GetAll()
        {
            return _tickets.ToList();
        }

        public void Add(Ticket ticket)
        {
            _tickets.Add(ticket);
        }

        public void SaveChanges()
        {
            _dbContext.SaveChanges();
        }
    }
}
