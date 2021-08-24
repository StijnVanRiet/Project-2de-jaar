package gui.manageTicket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import domain.CommentManager;
import domain.Customer;
import domain.DCManageTicket;
import domain.DCManageUser;
import domain.Employee;
import domain.PENDINGSTATUS;
import domain.TICKETSTATUS;
import domain.TICKETTYPE;
import domain.Ticket;
import errors.InfoException;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewTicketController extends VBox {

	@FXML
	private Label lblTicketTitle;
	@FXML
	private GridPane formGrid;
	@FXML
	private ChoiceBox<String> cbStatus;
	@FXML
	private ChoiceBox<String> cbPendingStatus;
	@FXML
	private TextField txfCreationDate;
	@FXML
	private TextField txfTitle;
	@FXML
	private TextField txfDescription;
	@FXML
	private ChoiceBox<String> cbType;
	@FXML
	private ChoiceBox<String> cbEngineer;
	@FXML
	private ChoiceBox<String> cbCustomer;
	@FXML
	private Label lblError;
	@FXML
	private HBox buttons;
	@FXML
	private Button btnAttachments;
	@FXML
	private Button btnSaveChanges;
	@FXML
	private Button btnAdd;
	@FXML
	private VBox commentSection;
	@FXML
	private Button btnRemoveComment;
	@FXML
	private ListView<String> listComments;
	@FXML
	private TextField addCommentField;
	@FXML
	private Button btnAddComment;

	private CommentManager commentManager; // gaat de listview van comments regelen
	private final DCManageTicket dcManageTicket = new DCManageTicket();
	private final DCManageUser dcManageUser = new DCManageUser();
	private final TicketsSidebarController tsb;
	private final Ticket t;

	/**
	 * dit scherm toont het geselecteerde ticket
	 */
	public ViewTicketController(TicketsSidebarController tsb, Ticket t) {
		this.tsb = tsb;
		this.t = t;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewTicket.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// lists fill up the items in the choiceboxes ( aka dropdown boxes )
		ObservableList<String> ticketStatusList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.TICKETSTATUS.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbStatus.setItems(ticketStatusList);
		ObservableList<String> pendingStatusList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.PENDINGSTATUS.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbPendingStatus.setItems(pendingStatusList);
		ObservableList<String> typeList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.TICKETTYPE.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbType.setItems(typeList);

		ObservableList<String> engineerList = FXCollections.observableArrayList(
				dcManageUser.getEngineerList().stream().map(e -> e.getUsername()).collect(Collectors.toList()));
		ObservableList<String> customerList = FXCollections.observableArrayList(
				dcManageUser.getCustomerList().stream().map(c -> c.getUsername()).collect(Collectors.toList()));
		cbEngineer.setItems(engineerList);
		cbCustomer.setItems(customerList);

		// labels veranderen naar tekst van het ticket

		if (t != null) {
			commentManager = new CommentManager(t);
			lblTicketTitle.setText("Ticket " + t.getTitle());
			txfTitle.setText(t.getTitle());
			cbStatus.setValue(t.getTicketStatus().toString());
			cbPendingStatus.setValue(t.getPendingStatus().toString());
			txfCreationDate.setText(t.getCreationDate().toString());
			txfDescription.setText(t.getDescription());
			cbType.setValue(t.getType().toString());
			cbEngineer.setValue(t.getAssignedEngineer().getUsername());
			cbCustomer.setValue(t.getCustomerDetails().getUsername());

			buttons.getChildren().remove(btnAdd);

			listComments.setItems(t.getCommentsList());


			// disable button als er geen comments meer zijn
			commentManager.addObserver(e -> btnRemoveComment.setDisable(commentManager.noComments()));
			// disable button als er niets is ingevuld
			btnAddComment.disableProperty().bind(Bindings.isEmpty(addCommentField.textProperty()));
		}

		// new ticket
		else {
			lblTicketTitle.setText("New Ticket");

			this.getChildren().remove(commentSection);

			// remove creation date row
			formGrid.getChildren().remove(5);
			formGrid.getChildren().remove(4);
			// remove pending status row
			formGrid.getChildren().remove(3);
			formGrid.getChildren().remove(2);
			// remove status row
			formGrid.getChildren().remove(1);
			formGrid.getChildren().remove(0);

			buttons.getChildren().remove(btnSaveChanges);
			buttons.getChildren().remove(btnAttachments);
		}

	}

	// Event Listener on Button[#btnAttachments].onAction
	@FXML
	public void btnAttachmentsOnAction(ActionEvent event) {
		//Helaas niet kunnen afwerken
	}

	// Event Listener on Button[#btnSaveChanges].onAction
	@FXML
	public void btnSaveChangesOnAction(ActionEvent event) {
		
		TICKETSTATUS status;
		try {
			status = TICKETSTATUS.valueOf(cbStatus.getValue());
		} catch (NullPointerException e) {
			status = null;
		}
		PENDINGSTATUS pendingStatus;
		try {
			pendingStatus = PENDINGSTATUS.valueOf(cbPendingStatus.getValue());
		} catch (NullPointerException e) {
			pendingStatus = null;
		}
		String title = txfTitle.getText();
		String description = txfDescription.getText();
		TICKETTYPE type;
		try {
			type = TICKETTYPE.valueOf(cbType.getValue());
		} catch (NullPointerException e) {
			type = null;
		}
		
		String usernameEngineer = cbEngineer.getValue();
		Employee employee;
		try {
			employee = dcManageUser.getEmployeeList().stream()
					.filter(e -> e.getUsername().equals(usernameEngineer)).findFirst().get();
		} catch (NoSuchElementException ex) {
			employee = null;
		}

		String usernameCustomer = cbCustomer.getValue();
		Customer customer;
		try {
			customer = dcManageUser.getCustomerList().stream()
					.filter(c -> c.getUsername().equals(usernameCustomer)).findFirst().get();
		} catch (NoSuchElementException ex) {
			customer = null;
		}
		
		try {
			Ticket editedTicket = new Ticket.Builder().ticketStatus(status).pendingStatus(pendingStatus).ticketType(type).title(title)
					.description(description).customerDetails(customer).assignedEngineer(employee).build(t);
			lblError.setVisible(false);
			dcManageTicket.editTicket(editedTicket);
			//tsb.refreshTable();
			tsb.selectTicket(editedTicket);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
		
	}

	// Event Listener on Button[#btnAdd].onAction
	@FXML
	public void btnAddOnAction(ActionEvent event) {
		// TODO errors
		String title = txfTitle.getText();
		String description = txfDescription.getText();
		TICKETTYPE type;
		try {
			type = TICKETTYPE.valueOf(cbType.getValue());
		} catch (NullPointerException e) {
			type = null;
		}
		
		String usernameEngineer = cbEngineer.getValue();
		Employee employee;
		try {
			employee = dcManageUser.getEmployeeList().stream()
					.filter(e -> e.getUsername().equals(usernameEngineer)).findFirst().get();
		} catch (NoSuchElementException ex) {
			employee = null;
		}

		String usernameCustomer = cbCustomer.getValue();
		Customer customer;
		try {
			customer = dcManageUser.getCustomerList().stream()
					.filter(c -> c.getUsername().equals(usernameCustomer)).findFirst().get();
		} catch (NoSuchElementException ex) {
			customer = null;
		}
		
		try {
			Ticket newTicket = new Ticket.Builder().ticketType(type).title(title)
					.description(description).customerDetails(customer).assignedEngineer(employee).build(null);
			lblError.setVisible(false);
			dcManageTicket.editTicket(newTicket);	//customer wordt geupdate in de db, niet ticket
			tsb.selectTicket(newTicket);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
	}

	// Event Listener on Button[#btnRemoveComment].onAction
	@FXML
	public void removeComment(ActionEvent event) {
		String selectedComment = listComments.getSelectionModel().getSelectedItem();
		if (selectedComment != null) {
			listComments.getSelectionModel().clearSelection();
			dcManageTicket.removeComment(t, selectedComment);
			listComments.setItems(t.getCommentsList());
		}
	}

	// Event Listener on Button[#btnAddComment].onAction
	@FXML
	public void addComment(ActionEvent event) {
		dcManageTicket.addComment(t, addCommentField.getText());
		listComments.setItems(t.getCommentsList());
		listComments.getSelectionModel().selectLast();
		addCommentField.clear();
	}
}
