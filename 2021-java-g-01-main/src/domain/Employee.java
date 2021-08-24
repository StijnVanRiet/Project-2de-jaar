package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import errors.InfoException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@DiscriminatorValue("E")
public class Employee extends User implements Serializable, RegexChecker {
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	private ROLE role;
	private String address;
	@ElementCollection
	private List<String> phoneNumbers;
	private String email;

	protected Employee() {
	}
	
	public Employee(Builder builder) {
		super(builder.firstName, builder.lastName, builder.status);
		this.role = builder.role;
		this.address = builder.address;
		this.phoneNumbers = builder.phoneNumbers;
		this.email = builder.email;
	}

	public ROLE getRole() {
		return role;
	}

	private void setRole(ROLE role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	private void setAddress(String address) {
		this.address = address;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	private void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getEmail() {
		return email;
	}

	private void setEmail(String email) {
		this.email = email;
	}

	// nodig voor tableview
	public StringProperty roleProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(role));
		return i;
	}

	public StringProperty addressProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(address);
		return i;
	}

	public StringProperty phoneNumbersProperty() {
		String s = "";
		for (String p : phoneNumbers) {
			s += p + ", ";
		}
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(s.substring(0, s.length() - 2));
		return i;
	}

	public StringProperty emailProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(email);
		return i;
	}
	
	public static class Builder {
		private STATUS status = STATUS.Active;
		private ROLE role;
		private String firstName;
		private String lastName;
		private String address;
		private List<String> phoneNumbers;
		private String email;
		
		public Builder status(STATUS status) {
			this.status = status;
			return this;
		}
		public Builder role(ROLE role) {
			this.role = role;
			return this;
		}
		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}
		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}
		public Builder address(String address) {
			this.address = address;
			return this;
		}
		public Builder phoneNumbers(List<String> phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
			return this;
		}
		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public Employee build(Employee e) {
			List<String> invalidInfo = new ArrayList<>();
			Employee employee;
			
			//role
			if (role == null) {
				invalidInfo.add("Please select a role");
			}
			
			//name
			if (firstName.isBlank() || firstName.isEmpty())
				invalidInfo.add("Please enter a first name");
			if (lastName.isBlank() || lastName.isEmpty())
				invalidInfo.add("Please enter a last name");
			
			
			//address
			String[] addressElements = address.split(", ");
			boolean emptyFlag = false;
			for (String addressElement : addressElements) {
				if (addressElement.isBlank())
					emptyFlag = true;
			}
			if (emptyFlag || addressElements.length != 4) {
				invalidInfo.add("Please fill in all the address fields");
			} else {
				if (!RegexChecker.checkHouseNumber(addressElements[3]))
					invalidInfo.add("Please enter a valid house number");
			}
			
			//phoneNumbers
			if (phoneNumbers.get(0).isBlank() && phoneNumbers.get(1).isBlank()) {
				invalidInfo.add("Please enter at least one phone number");
			} else {
				for (String phoneNumber : phoneNumbers) {
					if (!RegexChecker.checkPhoneNumber(phoneNumber) && !phoneNumber.isBlank()) 
						invalidInfo.add("Phonenumber is invalid");
				}
			}	
			//email
			if (!RegexChecker.checkEmail(email) || email.isBlank())
				invalidInfo.add("Please enter a valid email address");
			
			//check invalidInfo
			if(!invalidInfo.isEmpty()) {
				throw new InfoException("employee", invalidInfo);
			}
			if (e == null) {
				return new Employee(this);
			} else {
				employee = e;
				employee.setStatus(status);
				employee.setRole(role);
				employee.setFirstName(firstName);
				employee.setLastName(lastName);
				employee.setAddress(address);
				employee.setPhoneNumbers(phoneNumbers);
				employee.setEmail(email);
				return employee;
			}
			
		}
		
	}
}
