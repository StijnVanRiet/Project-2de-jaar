package domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class Contract implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contractID;
	@OneToOne(cascade = CascadeType.PERSIST)
	private ContractType contractType;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	@Enumerated(EnumType.STRING)
	private CONTRACTSTATUS status;
	
	protected Contract() {
	}
	
	public Contract(ContractType contractType, Date startDate, Date endDate) {
		setContractID(contractID);
		setContractType(contractType);
		setStartDate(startDate);
		setEndDate(endDate);
		setStatus(CONTRACTSTATUS.Requested);
	}
	
	
	public int getContractID() {
		return contractID;
	}
	private void setContractID(int contractID) {
		this.contractID = contractID;
	}

	public ContractType getContractType() {
		return contractType;
	}
	private void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	public CONTRACTSTATUS getStatus() {
		return status;
	}
	private void setStatus(CONTRACTSTATUS status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}
	private void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	private void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	
	public StringProperty numberProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(contractID));
		return i;
	}
	
	public StringProperty typeProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(contractType.getName());
		return i;
	}

	public StringProperty startDateProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		i.set(df.format(startDate));
		return i;
	}

	public StringProperty endDateProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		i.set(df.format(endDate));
		return i;
	}
	
	public StringProperty statusProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(status.toString());
		return i;
	}

}
