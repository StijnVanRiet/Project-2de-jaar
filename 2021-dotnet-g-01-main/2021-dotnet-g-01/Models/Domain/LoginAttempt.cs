using System;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class LoginAttempt
    {
        #region Fields
        #endregion

        #region Properties
        public int LoginAttemptId { get; set; }
        public DateTime Time { get; set; }
        public string CustomerId { get; set; }
        public bool Succesful { get; set; }
        #endregion

        #region Constructors
        public LoginAttempt(string customerId, bool succesful)
        {
            Time = DateTime.Now;
            CustomerId = customerId;
            Succesful = succesful;
        }
        #endregion
    }
}
