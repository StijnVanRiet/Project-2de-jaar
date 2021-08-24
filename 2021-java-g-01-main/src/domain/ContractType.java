package domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@NamedQueries({ 
	@NamedQuery(name = "ContractType.getAllContractTypes", query = "SELECT c FROM ContractType c")})
public class ContractType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contractTypeID;
	
	private String name;
	
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<Contract> contracts;

	// method: ways a ticket can be made, time: when a ticket can be made
	@Enumerated(EnumType.STRING)
	private TICKETCREATIONMETHOD ticketCreationMethod;
	@Enumerated(EnumType.STRING)
	private TICKETCREATIONTIME ticketCreationTime;

	// maximal time a ticket can take to fully process, in days
	private int maxHandleTime;

	// minimal length a contract can be, in years
	private int minContractLength;

	private double price;
	@Enumerated(EnumType.STRING)
	private CONTRACTTYPESTATUS contractTypeStatus;

	// TODO
	// check ctors for validity with JPA!

	protected ContractType() {
	}

	public ContractType(String name, TICKETCREATIONMETHOD method, TICKETCREATIONTIME time, int maxTime, int minLength,
			double price, CONTRACTTYPESTATUS status) {
		setName(name);
		setTicketCreationMethod(method);
		setTicketCreationTime(time);
		setMaxHandleTime(maxTime);
		setMinContractLength(minLength);
		setPrice(price);
		setContractTypeStatus(status);
	}

	public ContractType(String name, TICKETCREATIONMETHOD method, TICKETCREATIONTIME time, int maxTime, int minLength,
			double price) {
		this(name, method, time, maxTime, minLength, price, CONTRACTTYPESTATUS.Active);
	}

	public int numberOfContracts() {
		// TODO
		// shows number of (active) contracts of this type
		return 0;
	}

	public int numberOfTickets() {
		// TODO
		// shows number of tickets handled under a contract of this type
		return 0;
	}

	public double ticketsOnTime() {
		// TODO
		// shows percentage of tickets under a contract of this type, that were handled
		// on time
		return 0.0;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("Contract type name cannot be empty.");

		// TODO
		// check if name is unique

		this.name = name;
	}

	public TICKETCREATIONMETHOD getTicketCreationMethod() {
		return this.ticketCreationMethod;
	}

	public void setTicketCreationMethod(TICKETCREATIONMETHOD method) {
		this.ticketCreationMethod = method;
	}

	public TICKETCREATIONTIME getTicketCreationTime() {
		return this.ticketCreationTime;
	}

	public void setTicketCreationTime(TICKETCREATIONTIME time) {
		this.ticketCreationTime = time;
	}

	public int getMaxHandleTime() {
		return maxHandleTime;
	}

	public void setMaxHandleTime(int maxHandleTime) {
		if (maxHandleTime <= 0)
			throw new IllegalArgumentException("Maximum handle time cannot be zero or negative.");

		this.maxHandleTime = maxHandleTime;
	}

	public int getMinContractLength() {
		return minContractLength;
	}

	public void setMinContractLength(int minContractLength) {
		if (minContractLength < 1 || minContractLength > 3)
			throw new IllegalArgumentException("Contract length can only be 1, 2, or 3 years.");

		this.minContractLength = minContractLength;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		if (price <= 0)
			throw new IllegalArgumentException("Price cannot be zero or negative.");

		this.price = price;
	}

	public CONTRACTTYPESTATUS getContractTypeStatus() {
		return contractTypeStatus;
	}

	public void setContractTypeStatus(CONTRACTTYPESTATUS contractTypeStatus) {
		this.contractTypeStatus = contractTypeStatus;
	}

	public StringProperty idProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(contractTypeID));
		return i;
	}

	public StringProperty nameProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(name);
		return i;
	}

	public StringProperty methodProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(ticketCreationMethod.toString());
		return i;
	}

	public StringProperty timeProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(ticketCreationTime.toString());
		return i;
	}

	public StringProperty maxTimeProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(maxHandleTime));
		return i;
	}

	public StringProperty minLengthProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(minContractLength));
		return i;
	}

	public StringProperty priceProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(price));
		return i;
	}

	public StringProperty statusProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(contractTypeStatus.toString());
		return i;
	}

}
