using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class Attachment : Comment
    {
        public string Url { get; set; }

        public Attachment(string content, string url) : base (content)
        {
            Url = url;
        }
    }
}
