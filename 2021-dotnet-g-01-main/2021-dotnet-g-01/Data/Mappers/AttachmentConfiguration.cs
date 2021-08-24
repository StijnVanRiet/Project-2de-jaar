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
    public class AttachmentConfiguration : IEntityTypeConfiguration<Attachment>
    {
        public void Configure(EntityTypeBuilder<Attachment> builder)
        {
            //Key
            //builder.HasKey(a => a.CommentId);

            //Properties
            //builder.Property(a => a.CommentId).ValueGeneratedOnAdd();
            //builder.Property(a => a.Content).IsRequired();
            builder.Property(a => a.Url).IsRequired();

        }
    }
}
