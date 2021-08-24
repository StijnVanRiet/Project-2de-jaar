package gui.manageTicket;

import java.io.IOException;
import domain.DCManageTicket;
import domain.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class TicketsSidebarController extends VBox {

	@FXML
	private MenuButton statusFilter;
	@FXML
	private MenuButton pendingFilter;
	@FXML
	private TableView<Ticket> ticketsTable;
	@FXML
	private TableColumn<Ticket, String> titleCol;
	@FXML
	private TableColumn<Ticket, String> statusCol;
	@FXML
	private TableColumn<Ticket, String> pendingStatusCol;
	@FXML
	private Button btnAddTicket;

	private final DCManageTicket dcManageTicket = new DCManageTicket();
	private final TicketScreenViewController tsv;

	public TicketsSidebarController(TicketScreenViewController tsv) {
		this.tsv = tsv;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketsSidebar.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		statusCol.setCellValueFactory(cellData -> cellData.getValue().ticketStatusProperty());
		pendingStatusCol.setCellValueFactory(cellData -> cellData.getValue().pendingStatusProperty());

		ticketsTable.setItems(dcManageTicket.getTicketList());

		// Add change listener
		ticketsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTicket, newTicket) -> {
			if (newTicket != null) {
				tsv.changeContent(new ViewTicketController(this, newTicket));
			}
		});

	}

	@FXML
	private void addTicket(ActionEvent event) throws IOException {
		ticketsTable.getSelectionModel().clearSelection();
		tsv.changeContent(new ViewTicketController(this, null));
	}

	public void selectTicket(Ticket t) {
		ticketsTable.setItems(dcManageTicket.getTicketList());
		// selecteer het juiste ticket in de lijst (nodig als een nieuw ticket wordt
		// gemaakt
		ticketsTable.getSelectionModel().select(t);
		// vraag aan tsv om zijn content te veranderen naar een scherm gebaseerd op het
		// meegegeven ticket
		tsv.changeContent(new ViewTicketController(this, t));
	}

	// Event Listener on MenuItem[#filterAll].onAction
	@FXML
	public void filterAllOnAction(ActionEvent event) {
		pendingFilter.setText("Filter On Pending");
		pendingFilter.setVisible(false);
		statusFilter.setText("All");
		
		//to show all in table again
		dcManageTicket.changeTicketFilter("", "");
	}

	// Event Listener on MenuItem[#filterCreated].onAction
	@FXML
	public void filterCreatedOnAction(ActionEvent event) {
		pendingFilter.setText("Filter On Pending");
		pendingFilter.setVisible(false);
		statusFilter.setText("Created");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}

	// Event Listener on MenuItem[#filterPending].onAction
	@FXML
	public void filterPendingOnAction(ActionEvent event) {
		pendingFilter.setText("Filter On Pending");
		pendingFilter.setVisible(true);
		statusFilter.setText("Pending");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), "");
	}

	// Event Listener on MenuItem[#filterClosed].onAction
	@FXML
	public void filterClosedOnAction(ActionEvent event) {
		pendingFilter.setText("Filter On Pending");
		pendingFilter.setVisible(false);
		statusFilter.setText("Closed");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}

	// Event Listener on MenuItem[#filterCanceled].onAction
	@FXML
	public void filterCanceledOnAction(ActionEvent event) {
		pendingFilter.setText("Filter On Pending");
		pendingFilter.setVisible(false);
		statusFilter.setText("Canceled");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}

	// Event Listener on MenuItem[#allPending].onAction
	@FXML
	public void allPendingOnAction(ActionEvent event) {
		pendingFilter.setText("All Pending");

		dcManageTicket.changeTicketFilter(statusFilter.getText(), "");
	}

	// Event Listener on MenuItem[#informationNeeded].onAction
	@FXML
	public void informationNeededOnAction(ActionEvent event) {
		pendingFilter.setText("Information Needed");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}

	// Event Listener on MenuItem[#informationReceived].onAction
	@FXML
	public void informationReceivedOnAction(ActionEvent event) {
		pendingFilter.setText("Information Received");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}

	// Event Listener on MenuItem[#inDevelopment].onAction
	@FXML
	public void inDevelopmentOnAction(ActionEvent event) {
		pendingFilter.setText("In Development");
		
		dcManageTicket.changeTicketFilter(statusFilter.getText(), pendingFilter.getText());
	}
	
	public void refreshTable() {
		// werkt enkel als een rij wordt geupdate, niet als een rij wordt toegevoegd,
		// ik weet niet waarom
		ticketsTable.refresh();
	}
}
