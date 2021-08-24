package domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "userType", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "User.getAllEmployees", query = "SELECT u FROM User u WHERE TYPE(u) = Employee"),
		@NamedQuery(name = "User.getAllCustomers", query = "SELECT u FROM User u WHERE TYPE(u) = Customer"),
		@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")})
public abstract class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Enumerated(EnumType.STRING)
	private STATUS status = STATUS.Active;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	@Temporal(TemporalType.DATE)
	private Date startDate = new Date();

	protected User() {
	}

	// ctor without status gets automatically set to Active
	public User(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
		generateUsername();
		generatePassword();
	}
	
	public User(String firstName, String lastName, STATUS status) {
		this(firstName, lastName);
		setStatus(status);
	}
	
	private void generateUsername() {
		String formatPattern = "ddMMyy";
		SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
		this.username = String.format("%s%c%s", firstName, Character.toUpperCase(this.lastName.charAt(0)), sdf.format(getStartDate()));
	}

	/**
	 * based on https://www.geeksforgeeks.org/generating-password-otp-java/
	 */
	private void generatePassword() {
		// A strong password has Cap_chars, Lower_chars,
		// numeric value and symbols. So we are using all of
		// them to generate our password
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Small_chars = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String symbols = "!@#$%^&*_=+-/.?<>)";
		int passwordLength = 10;

		String values = Capital_chars + Small_chars + numbers + symbols;

		// Using random method
		Random rndm_method = new Random();

		char[] password = new char[passwordLength];

		for (int i = 0; i < passwordLength; i++) {
			// Use of charAt() method : to get character value
			// Use of nextInt() as it is scanning the value as int
			password[i] = values.charAt(rndm_method.nextInt(values.length()));

		}
		// convert charArray to string and initialize password with it
		this.password = String.valueOf(password);
	}

	public int getId() {
		return id;
	}

	public StringProperty idProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(id));
		return i;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public StringProperty statusProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(status));
		return i;
	}

	public String getUsername() {
		return username;
	}

	public StringProperty usernameProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(username);
		return i;
	}

	public String getPassword() {
		return password;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getSeniority() {
		long m = System.currentTimeMillis() - startDate.getTime();
		m = (m / (1000 * 60 * 60 * 24)) % 365;
		String i = String.valueOf(m);
		return i;
	}

	public StringProperty seniorityProperty() {
		long m = System.currentTimeMillis() - startDate.getTime();
		m = (m / (1000 * 60 * 60 * 24)) % 365;
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(m));
		return i;
	}

	public String getFirstName() {
		return firstName;
	}

	protected void setFirstName(String firstName) {
		// sets first letter of name to uppercase, the rest in lowercase
		this.firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
	}

	public String getLastName() {
		return lastName;
	}

	protected void setLastName(String lastName) {
		// sets first letter of name to uppercase, the rest in lowercase
		this.lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
