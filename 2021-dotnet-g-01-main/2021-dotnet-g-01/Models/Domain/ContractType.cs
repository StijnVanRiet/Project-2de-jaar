using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace _2021_dotnet_g_01.Models.Domain
{
    public class ContractType
    {
        #region Fields
        private string _name;
        #endregion

        #region Properties
        public int ContractTypeId { get; set; }
        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                    throw new ArgumentException("A contract type must have a name");
                if (value.Length > 30)
                    throw new ArgumentException("Name of contract type may not exceed 30 characters");
                _name = value;
            }
        }
        public SupportType SupportType { get; set; }
        #endregion

        #region Constructors
        public ContractType(string name, SupportType supportType)
        {
            Name = name;
            SupportType = supportType;
        }
        #endregion
    }

    public enum SupportType
    {
        Always = 1,
        WorkingHours = 2,
        Weekends = 3
    }
}
