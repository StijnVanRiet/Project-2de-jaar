using System;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class Comment
    {
        #region Fields
        private string _content;
        #endregion

        #region Properties
        public int CommentId { get; set; }
        public string Content
        {
            get
            {
                return _content;
            }
            set
            {
                string comment = value.Split(":")[1];
                if (string.IsNullOrWhiteSpace(comment) && value.Split(":").Length <= 2)
                    throw new ArgumentException("Comment cannot be empty");
                if (value.Length > 100)
                    throw new ArgumentException("Comment may not exceed 100 characters");
                _content = value;
            }
        }
        #endregion

        #region Constructors
        public Comment(string content)
        {
            Content = content;
        }
        #endregion

    }
}
