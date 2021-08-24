package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import errors.InfoException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Entity
@NamedQueries({ @NamedQuery(name = "Ticket.getAllTickets", query = "SELECT t FROM Ticket t"),
		@NamedQuery(name = "Ticket.getTicketsByEngineerId", query = "SELECT t FROM Ticket t WHERE t.assignedEngineer.id = :engineerId"),
		@NamedQuery(name = "Ticket.getTicketsByCustomerId", query = "SELECT t FROM Ticket t WHERE t.customerDetails.id = :customerId") })
public class Ticket implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketId;
	@Enumerated(EnumType.STRING)
	private TICKETSTATUS ticketStatus = TICKETSTATUS.Created;
	@Enumerated(EnumType.STRING)
	private TICKETTYPE type;
	@Enumerated(EnumType.STRING)
	private PENDINGSTATUS pendingStatus = PENDINGSTATUS.InformationNeeded;

	private LocalDate creationDate = LocalDate.now();
	private String title;
	private String description;

	@OneToOne(cascade = CascadeType.PERSIST)
	private Customer customerDetails;
	@OneToOne(cascade = CascadeType.PERSIST)
	private Employee assignedEngineer;
	@ElementCollection
	private List<String> comments = new ArrayList<>();
	private String attachments;

	protected Ticket() {
		
	}
	
	public Ticket(Builder builder) {
		this.ticketStatus = builder.ticketStatus;
		this.type = builder.ticketType;
		this.pendingStatus = builder.pendingStatus;
		this.creationDate = builder.creationDate;
		this.title = builder.title;
		this.description = builder.description;
		this.customerDetails = builder.customerDetails;
		this.assignedEngineer = builder.assignedEngineer;
		this.comments = builder.comments;
	}


	// nodig voor tickets tableview
	public StringProperty titleProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(title);
		return i;
	}

	public StringProperty ticketStatusProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(ticketStatus));
		return i;
	}

	public StringProperty pendingStatusProperty() {
		SimpleStringProperty i = new SimpleStringProperty();
		i.set(String.valueOf(pendingStatus));
		return i;
	}

	// getters en setters
	public String getTitle() {
		return title;
	}

	public TICKETSTATUS getTicketStatus() {
		return ticketStatus;
	}

	public PENDINGSTATUS getPendingStatus() {
		return pendingStatus;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}


	public TICKETTYPE getType() {
		return type;
	}


	public Customer getCustomerDetails() {
		return customerDetails;
	}


	public Employee getAssignedEngineer() {
		return assignedEngineer;
	}


	public List<String> getComments() {
		return comments;
	}


	public String getAttachments() {
		return attachments;
	}


	// nodig voor comments listview
	public ObservableList<String> getCommentsList() {
		return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(comments));
	}

	public void addComment(String comment) {
		comments.add(comment);
	}

	public void removeComment(String comment) {
		comments.remove(comments.indexOf(comment));
	}

	public static class Builder {
		private TICKETSTATUS ticketStatus = TICKETSTATUS.Created;
		private TICKETTYPE ticketType;
		private PENDINGSTATUS pendingStatus = PENDINGSTATUS.InformationNeeded;
		private LocalDate creationDate = LocalDate.now();
		private String title;
		private String description;
		private Customer customerDetails;
		private Employee assignedEngineer;
		private List<String> comments = new ArrayList<>();
		
		public Builder ticketStatus(TICKETSTATUS ticketStatus) {
			this.ticketStatus = ticketStatus;
			return this;
		}
		public Builder ticketType(TICKETTYPE ticketType) {
			this.ticketType = ticketType;
			return this;
		}
		public Builder pendingStatus(PENDINGSTATUS pendingStatus) {
			this.pendingStatus = pendingStatus;
			return this;
		}
		public Builder creationDate(LocalDate creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		public Builder customerDetails(Customer customerDetails) {
			this.customerDetails = customerDetails;
			return this;
		}
		public Builder assignedEngineer(Employee assignedEngineer) {
			this.assignedEngineer = assignedEngineer;
			return this;
		}
		public Builder comments(List<String> comments) {
			this.comments = comments;
			return this;
		}
		
		public Ticket build(Ticket ticket) {
			List<String> invalidInfo = new ArrayList<>();
			
			//strings
			if (title.isBlank())
				invalidInfo.add("Please enter a title");
			if (description.isBlank())
				invalidInfo.add("Please enter a description");
			
			//enums + objects
			if (ticketType == null) {
				invalidInfo.add("Please select a ticket type");
			}
			if (customerDetails == null) {
				invalidInfo.add("Please select a customer");
			}
			if (assignedEngineer == null) {
				invalidInfo.add("Please select an engineer");
			}

			
			//check if ticket is cancelled or closed
			if (ticket != null) {
				if (ticket.getTicketStatus() == TICKETSTATUS.Canceled)
					throw new InfoException("ticket", new ArrayList<>(Arrays.asList("Cannot make changes to a cancelled ticket")));
				if (ticket.getTicketStatus() == TICKETSTATUS.Closed)
					throw new InfoException("ticket", new ArrayList<>(Arrays.asList("Cannot make changes to a closed ticket")));
			}
			
			
			//check invalidInfo
			if(!invalidInfo.isEmpty()) {
				throw new InfoException("ticket", invalidInfo);
			}
			
			if (ticket == null) {
				ticket = new Ticket(this);
				customerDetails.addTicket(ticket);
			} else {
				ticket.ticketStatus = ticketStatus;
				ticket.pendingStatus = pendingStatus;
				ticket.title = title;
				ticket.description = description;
				ticket.type = ticketType;
				ticket.assignedEngineer = assignedEngineer;
				ticket.customerDetails = customerDetails;
			}
			return ticket;
		}
		
	}
}
