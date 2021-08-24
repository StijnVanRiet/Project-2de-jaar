using System;
using System.Collections.Generic;
using System.Text;
using _2021_dotnet_g_01.Data.Mappers;
using _2021_dotnet_g_01.Models;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using _2021_dotnet_g_01.Models.ViewModels;

namespace _2021_dotnet_g_01.Data
{
    public class ApplicationDbContext : IdentityDbContext<Customer>
    {
        public DbSet<Contract> Contracts { get; set; }
        public DbSet<ContractType> ContractTypes { get; set; }
        public DbSet<Customer> Customers { get; set; }
        public DbSet<LoginAttempt> LoginAttempts { get; set; }
        public DbSet<Ticket> Tickets { get; set; }

        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);
            builder.ApplyConfiguration(new LoginAttemptConfiguration());
            builder.ApplyConfiguration(new CommentConfiguration());
            builder.ApplyConfiguration(new ContractConfiguration());
            builder.ApplyConfiguration(new ContractTypeConfiguration());
            builder.ApplyConfiguration(new TicketConfiguration());
            builder.ApplyConfiguration(new CustomerConfiguration());
            builder.ApplyConfiguration(new AttachmentConfiguration());
        }
    }
}
