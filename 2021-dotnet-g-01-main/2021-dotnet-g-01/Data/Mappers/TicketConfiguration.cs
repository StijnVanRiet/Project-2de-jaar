using _2021_dotnet_g_01.Models;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Mappers
{
    public class TicketConfiguration : IEntityTypeConfiguration<Ticket>
    {
        public void Configure(EntityTypeBuilder<Ticket> builder)
        {
            //Key
            builder.HasKey(t => t.TicketId);

            //Properties
            builder.Property(t => t.TicketId).ValueGeneratedOnAdd();
            builder.Property(t => t.Title).HasMaxLength(255).IsRequired();
            builder.Property(t => t.Description).HasMaxLength(511).IsRequired();
            builder.Property(t => t.AssignedEngineer).HasMaxLength(255).IsRequired();

            //Associations
            builder.HasMany(t => t.Comments).WithOne().IsRequired(false).OnDelete(DeleteBehavior.SetNull);
        }
    }
}
