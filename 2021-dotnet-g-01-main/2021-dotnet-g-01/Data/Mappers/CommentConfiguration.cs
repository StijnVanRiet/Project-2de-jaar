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
    public class CommentConfiguration : IEntityTypeConfiguration<Comment>
    {
        public void Configure(EntityTypeBuilder<Comment> builder)
        {
            //Key
            builder.HasKey(c => c.CommentId);

            //Properties
            builder.Property(c => c.CommentId).ValueGeneratedOnAdd();
            builder.Property(c => c.Content).IsRequired();

        }
    }
}
