using _2021_dotnet_g_01.Models.Domain;
using System;
using Xunit;

namespace _2021_dotnet_g_01.Tests.Models.Domain
{
    public class ContractTypeTest
    {
        private readonly ContractType _contractType;
        private readonly SupportType _supportType;

        public ContractTypeTest()
        {
            _supportType = SupportType.Always;
            _contractType = new ContractType("24/7 Support", _supportType);
        }

        [Fact]
        public void NewContractType_SetsSupportType()
        {
            Assert.Equal(_supportType, _contractType.SupportType);
        }

        [Theory]
        [InlineData("")] //empty
        [InlineData("Naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaame")] //too long
        public void NewContractType_WrongName_Fails(string name)
        {
            //Assert
            Assert.Throws<ArgumentException>(() => new ContractType(name, _supportType));
        }
    }
}
