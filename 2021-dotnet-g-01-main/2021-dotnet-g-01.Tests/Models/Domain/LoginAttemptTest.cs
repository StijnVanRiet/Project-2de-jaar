using _2021_dotnet_g_01.Models.Domain;
using System;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class LoginAttemptTest
    {
        private readonly LoginAttempt _loginAttempt;
        private readonly bool _succesful;

        public LoginAttemptTest()
        {
            _succesful = true;
            _loginAttempt = new LoginAttempt("1", _succesful);
        }

        [Fact]
        public void NewLoginAttempt_TimeNow()
        {
            //Assert
            Assert.Equal(DateTime.Now.ToString("MM/dd/yyyy H:mm"), _loginAttempt.Time.ToString("MM/dd/yyyy H:mm"));
        }

        [Fact]
        public void NewLoginAttempt_SetsSuccesful()
        {
            Assert.Equal(_succesful, _loginAttempt.Succesful);
        }
    }
}