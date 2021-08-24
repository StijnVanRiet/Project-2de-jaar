using _2021_dotnet_g_01.Models.Domain;
using System;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class CommentTest
    {
        [Theory]
        [InlineData("user@email:")] //empty
        [InlineData("user@email:Cooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooomment")] //too long
        public void NewComment_WrongContent_Fails(string content)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new Comment(content));
        }
    }
}