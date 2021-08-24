package domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class Contact implements Serializable, RegexChecker {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contactID;
	private String firstName;
	private String lastName;
	private String email;

	public Contact(String firstName, String lastName, String email) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
	}

	protected Contact() {

	}

	public String getEmail() {
		return email;
	}

	protected void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	protected void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	protected void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	protected int getContactID() {
		return contactID;
	}

	private void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public StringProperty firstNameProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(getFirstName());
		return i;
	}

	public StringProperty lastNameProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(getLastName());
		return i;
	}

	public StringProperty emailProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(email);
		return i;
	}
}
