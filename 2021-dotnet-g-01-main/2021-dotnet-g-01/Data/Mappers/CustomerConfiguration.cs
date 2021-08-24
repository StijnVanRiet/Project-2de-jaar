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
    public class CustomerConfiguration : IEntityTypeConfiguration<Customer>
    {
        public void Configure(EntityTypeBuilder<Customer> builder)
        {
            //Key
            builder.HasKey(c => c.Id);

            //Properties
            //builder.Property(c => c.CustomerId).ValueGeneratedOnAdd();
            //builder.Property(c => c.Username).HasMaxLength(255).IsRequired();
            //builder.Property(c => c.Password).HasMaxLength(255).IsRequired();

            //Associations
            builder.HasMany(c => c.Contracts).WithOne().IsRequired(false).OnDelete(DeleteBehavior.SetNull);
        }
    }
}
