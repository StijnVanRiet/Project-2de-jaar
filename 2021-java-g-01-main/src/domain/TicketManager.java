package domain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.UserDaoJpa;

public class TicketManager {

	private ObservableList<Ticket> ticketList;
	private FilteredList<Ticket> filteredTicketList;
	private SortedList<Ticket> sortedTicketList;

	// sort
	private final Comparator<Ticket> byTicketStatus = (t1, t2) -> t1.getTicketStatus().compareTo(t2.getTicketStatus());

	private UserDaoJpa userdao;

	public TicketManager() {
		userdao = new UserDaoJpa();

		// maak ticket list
		ticketList = FXCollections.observableList(userdao.getAllTickets());
		filteredTicketList = new FilteredList<>(ticketList, p -> true);
		sortedTicketList = new SortedList<>(filteredTicketList, byTicketStatus);
	}

	public ObservableList<Ticket> getTicketList(Employee currentUser) {
		// engineer mag enkel tickets zien die aan hem/haar zijn toegewezen
		if ((currentUser).getRole().equals(ROLE.Engineer)) {
			
			ticketList = FXCollections.observableList(userdao.getTicketsByEngineerId(currentUser.getId()));
			filteredTicketList = new FilteredList<>(ticketList, p -> true);
			sortedTicketList = new SortedList<>(filteredTicketList, byTicketStatus);
			return sortedTicketList;
		}

		// ander supportmanager en die mag alle tickets zien
		ticketList = FXCollections.observableList(userdao.getAllTickets());
		filteredTicketList = new FilteredList<>(ticketList, p -> true);
		sortedTicketList = new SortedList<>(filteredTicketList, byTicketStatus);
		return sortedTicketList;
	}


	public void editTicket(Ticket ticket) {
		UserDaoJpa.startTransaction();
		userdao.update(ticket.getCustomerDetails());	//update customer want ticket wordt wordt in de db gestoken onder customer
		UserDaoJpa.commitTransaction();
	}
	

	/*
	 * filters based on status, no input required!
	 * 
	 * Created Pending Closed Canceled
	 * 
	 * Specific pending sub-statuses:
	 * 
	 * InformationNeeded InformationReceived InDevelopment
	 */
	public void changeTicketFilter(String ticketStatus, String pendingStatus) {
		filteredTicketList.setPredicate(ticket -> {
			// if filter text is empty, display all
			if (ticketStatus == null || ticketStatus.isBlank())
				return true;

			if (ticket.getTicketStatus().name().toLowerCase().equals("pending")) {
				if (pendingStatus == null || pendingStatus.isBlank())
					return true;
				// replaceAll to remove possible whitespaces
				return (ticket.getPendingStatus().name().toLowerCase()
						.equals(pendingStatus.toLowerCase().replaceAll("\\s", "")));
			}

			return (ticket.getTicketStatus().name().toLowerCase().equals(ticketStatus.toLowerCase()));
		});
	}

}
