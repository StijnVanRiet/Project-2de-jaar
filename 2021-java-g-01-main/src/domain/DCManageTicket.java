package domain;

import java.util.List;

import javafx.collections.ObservableList;

public class DCManageTicket extends DCLoggedInUser {

	private final TicketManager ticketManager;

	public DCManageTicket() {
		this.ticketManager = new TicketManager();
	}

	public ObservableList<Ticket> getTicketList() {
		return ticketManager.getTicketList(getCurrentUser());
	}

	public void changeTicketFilter(String ticketStatus, String pendingStatus) {
		ticketManager.changeTicketFilter(ticketStatus, pendingStatus);
	}
	
	public void editTicket(Ticket ticket) {
		ticketManager.editTicket(ticket);
	}
	
	public void addComment(Ticket ticket, String response) {
		String comment = getCurrentUser().getUsername() + " commented: " + response;
		ticket.addComment(comment);
		editTicket(ticket);
	}
	
	public void removeComment(Ticket ticket, String comment) {
		ticket.removeComment(comment);
		editTicket(ticket);
	}

}
