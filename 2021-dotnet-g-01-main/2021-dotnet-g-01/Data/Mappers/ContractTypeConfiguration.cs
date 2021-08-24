using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Mappers
{
    public class ContractTypeConfiguration : IEntityTypeConfiguration<ContractType>
    {
        public void Configure(EntityTypeBuilder<ContractType> builder)
        {
            //Key
            builder.HasKey(ct => ct.ContractTypeId);
            
            //Properties
            builder.Property(ct => ct.ContractTypeId).ValueGeneratedOnAdd();
            builder.Property(ct => ct.Name).HasMaxLength(255).IsRequired();
        }
    }
}
