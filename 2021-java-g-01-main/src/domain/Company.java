package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import errors.InfoException;

@Entity
public class Company implements Serializable, RegexChecker {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int companyId;
	private String name;
	private String address;
	@ElementCollection
	private List<String> phoneNumbers;

	protected Company() {
	}


	public Company(String name, String address, List<String> phoneNumbers) {
		setName(name);
		setAddress(address);
		setPhoneNumbers(phoneNumbers);
	}

	public String getAddress() {
		return address;
	}

	// bepaalde setters zijn protected omdat we deze gebruiken in de Customer klasse
	protected void setAddress(String address) {
		this.address = address;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	protected void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

}
