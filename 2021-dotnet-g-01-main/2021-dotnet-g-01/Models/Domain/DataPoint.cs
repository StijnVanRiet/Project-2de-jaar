using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace _2021_dotnet_g_01.Models.Domain
{
	//DataContract for Serializing Data - required to serve in JSON format
	[DataContract]
	public class DataPoint
	{
		public DataPoint(double y, string legendText, string label)
		{
			this.Y = y;
			this.legendText = legendText;
			this.label = label;
		}


		//Explicitly setting the name to be used while serializing to JSON.
		[DataMember(Name = "y")]
		public Nullable<double> Y = null;

		[DataMember(Name = "legendText")]
		public string legendText = null;

		[DataMember(Name = "label")]
		public string label = null;
	}
}