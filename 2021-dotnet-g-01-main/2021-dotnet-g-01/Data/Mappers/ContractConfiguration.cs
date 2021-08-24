using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Mappers
{
    public class ContractConfiguration : IEntityTypeConfiguration<Contract>
    {
        public void Configure(EntityTypeBuilder<Contract> builder)
        {
            //Key
            builder.HasKey(c => c.ContractId);

            //Properties
            builder.Property(c => c.ContractId).ValueGeneratedOnAdd();
            builder.Property(c => c.Name).HasMaxLength(255).IsRequired();

            //Associations
            builder.HasOne(c => c.ContractType).WithMany().IsRequired().OnDelete(DeleteBehavior.Cascade);
            builder.HasMany(c => c.Tickets).WithOne().IsRequired(false).OnDelete(DeleteBehavior.SetNull);
        }
    }
}
