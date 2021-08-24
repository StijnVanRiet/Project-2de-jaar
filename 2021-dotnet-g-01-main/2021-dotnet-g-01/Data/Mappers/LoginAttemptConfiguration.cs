using _2021_dotnet_g_01.Models.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Data.Mappers
{
    public class LoginAttemptConfiguration : IEntityTypeConfiguration<LoginAttempt>
    {
        public void Configure(EntityTypeBuilder<LoginAttempt> builder)
        {
            //Key
            builder.HasKey(l => l.LoginAttemptId);

            //Properties
            builder.Property(l => l.LoginAttemptId).ValueGeneratedOnAdd();


        }
    }
}
