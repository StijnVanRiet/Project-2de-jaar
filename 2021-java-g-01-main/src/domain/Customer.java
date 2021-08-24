package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import errors.InfoException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@DiscriminatorValue("C")
public class Customer extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	@OneToOne(cascade = CascadeType.PERSIST)
	private Company company;
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<Contract> contracts;
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<Contact> contacts;
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<Ticket> tickets = new ArrayList<Ticket>();

	protected Customer() {
	}

	private Customer(Builder builder) {
		super(builder.contactFirstName, builder.contactLastName, builder.status);
		this.company = builder.company;
		this.contacts = new ArrayList<>();
		this.contacts.add(builder.contact);
	}


	public void addTicket(Ticket ticket) {
		tickets.add(ticket);
	}

	// nodig voor tableview
	public StringProperty companyProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(getCompanyName());
		return i;
	}

	// getters en setters
	public List<Contract> getContracts() {
		return contracts;
	}

	private void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public Company getCompany() {
		return company;
	}

	private void setCompany(Company company) {
		this.company = company;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	private void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public String getCompanyName() {
		return this.company.getName();
	}
	

	public List<Ticket> getTickets() {
		return tickets;
	}
	// builder
	public static class Builder {
		// Customer
		private STATUS status = STATUS.Active;
		private String companyName;
		private String address;
		private List<String> phoneNumbers;
		private Company company;
		private Contact contact;

		// Contact
		private String contactFirstName;
		private String contactLastName;
		private String email;

		public Builder status(STATUS status) {
			this.status = status;
			return this;
		}

		public Builder companyName(String companyName) {
			this.companyName = companyName;
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

		public Builder contactFirstName(String contactFirstName) {
			this.contactFirstName = contactFirstName;
			return this;
		}

		public Builder contactLastName(String contactLastName) {
			this.contactLastName = contactLastName;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Customer build(Customer customer) {
			List<String> invalidInfo = new ArrayList<>();

			// companyName
			if (companyName.isBlank() || companyName.isEmpty())
				invalidInfo.add("Please enter the name of the company");

			// address
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

			// phoneNumbers
			if (phoneNumbers.get(0).isBlank() && phoneNumbers.get(1).isBlank()) {
				invalidInfo.add("Please enter at least one phone number");
			} else {
				for (String phoneNumber : phoneNumbers) {
					if (!RegexChecker.checkPhoneNumber(phoneNumber) && !phoneNumber.isBlank())
						invalidInfo.add("Phonenumber is invalid");
				}
			}

			if (customer == null) {
				// contactName
				if (contactFirstName.isBlank() || contactFirstName.isEmpty())
					invalidInfo.add("Please enter the first name of the contact");
				if (contactLastName.isBlank() || contactLastName.isEmpty())
					invalidInfo.add("Please enter the last name of the contact");
				// contactEmail
				if (!RegexChecker.checkEmail(email) || email.isBlank())
					invalidInfo.add("Please enter a valid email address");
			}
			// check invalidInfo
			if (!invalidInfo.isEmpty()) {
				throw new InfoException("customer", invalidInfo);
			}

			if (customer == null) {
				this.company = new Company(companyName, address, phoneNumbers);
				this.contact = new Contact(contactFirstName, contactLastName, email);
				return new Customer(this);
			} else {
				customer.setStatus(status);
				Company companyToEdit = customer.getCompany();
				companyToEdit.setName(companyName);
				companyToEdit.setAddress(address);
				companyToEdit.setPhoneNumbers(phoneNumbers);
				return customer;
			}

		}

		public Customer build(Customer customer, Contact contact) {
			List<String> invalidInfo = new ArrayList<>();

			// contactName
			if (contactFirstName.isBlank() || contactFirstName.isEmpty())
				invalidInfo.add("Please enter the first name of the contact");
			if (contactLastName.isBlank() || contactLastName.isEmpty())
				invalidInfo.add("Please enter the last name of the contact");
			// contactEmail
			if (!RegexChecker.checkEmail(email) || email.isBlank())
				invalidInfo.add("Please enter a valid email address");

			// check invalidInfo
			if (!invalidInfo.isEmpty()) {
				throw new InfoException("contact", invalidInfo);
			}

			if (contact == null) {
				customer.getContacts().add(new Contact(contactFirstName, contactLastName, email));
			} else {
				contact.setFirstName(contactFirstName);
				contact.setLastName(contactLastName);
				contact.setEmail(email);
			}
			return customer;
		}
	}
}