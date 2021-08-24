package gui.manageContractType;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import java.io.IOException;
import java.util.EnumSet;
import java.util.stream.Collectors;

import domain.CONTRACTTYPESTATUS;
import domain.ContractType;
import domain.DCManageContractType;
import domain.TICKETCREATIONMETHOD;
import domain.TICKETCREATIONTIME;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewContractTypeController extends VBox {

	@FXML
	private Label lblContractTypeName;
	@FXML
	private GridPane formGrid;
	@FXML
	private TextField txfName;
	@FXML
	private ChoiceBox<String> cbStatus;
	@FXML
	private ChoiceBox<String> cbTicketCreationMethod;
	@FXML
	private ChoiceBox<String> cbTicketCreationTime;
	@FXML
	private TextField txfMaxHandleTime;
	@FXML
	private TextField txfMinContractLength;
	@FXML
	private TextField txfPrice;
	@FXML
	private TextField txfNumberOfRunningContracts;
	@FXML
	private TextField txfNumberOfFinishedTickets;
	@FXML
	private TextField txfPercentOfTicketsFinishedOnTime;
	@FXML
	private HBox buttons;
	@FXML
	private Button btnSaveChanges;
	@FXML
	private Button btnAdd;

	private final DCManageContractType dcManageContractType = new DCManageContractType();
	private final ContractTypesSidebarController csb;
	private final ContractType c;

	/**
	 * dit scherm toont het geselecteerde contract type
	 */
	public ViewContractTypeController(ContractTypesSidebarController csb, ContractType c) {
		this.csb = csb;
		this.c = c;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewContractType.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// lists fill up the items in the choiceboxes ( aka dropdown boxes )
		ObservableList<String> contractTypeStatusList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.CONTRACTTYPESTATUS.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbStatus.setItems(contractTypeStatusList);
		ObservableList<String> ticketCreationMethodList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.TICKETCREATIONMETHOD.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbTicketCreationMethod.setItems(ticketCreationMethodList);
		ObservableList<String> ticketCreationTimeList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.TICKETCREATIONTIME.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbTicketCreationTime.setItems(ticketCreationTimeList);
		
		// labels veranderen naar tekst van het contract type

		if (c != null) {
			lblContractTypeName.setText("Contract Type " + c.getName());
			txfName.setText(c.getName());
			cbStatus.setValue(c.getContractTypeStatus().toString());
			cbTicketCreationMethod.setValue(c.getTicketCreationMethod().toString());
			cbTicketCreationTime.setValue(c.getTicketCreationTime().toString());
			txfMaxHandleTime.setText(String.valueOf(c.getMaxHandleTime()));
			txfMinContractLength.setText(String.valueOf(c.getMinContractLength()));
			txfPrice.setText(String.valueOf(c.getPrice()));

			buttons.getChildren().remove(btnAdd);
		}

		// new ticket
		else {
			lblContractTypeName.setText("New Contract Type");




			// remove percent of tickets finished on time row
			formGrid.getChildren().remove(19);
			formGrid.getChildren().remove(18);
			// remove number of finished tickets row
			formGrid.getChildren().remove(17);
			formGrid.getChildren().remove(16);
			// remove number of running contracts row
			formGrid.getChildren().remove(15);
			formGrid.getChildren().remove(14);
			// remove status row
			formGrid.getChildren().remove(3);
			formGrid.getChildren().remove(2);
			
			buttons.getChildren().remove(btnSaveChanges);
		}

	}

	// Event Listener on Button[#btnSaveChanges].onAction
	@FXML
	public void btnSaveChangesOnAction(ActionEvent event) {
		// TODO errors
		String name = txfName.getText();
		if (!name.equals(c.getName()))
			c.setName(name);
		CONTRACTTYPESTATUS status = CONTRACTTYPESTATUS.valueOf(cbStatus.getValue());
		if (status != c.getContractTypeStatus())
			c.setContractTypeStatus(status);
		TICKETCREATIONMETHOD method = TICKETCREATIONMETHOD.valueOf(cbTicketCreationMethod.getValue());
		if (method != c.getTicketCreationMethod())
			c.setTicketCreationMethod(method);
		TICKETCREATIONTIME time = TICKETCREATIONTIME.valueOf(cbTicketCreationTime.getValue());
		if (time != c.getTicketCreationTime())
			c.setTicketCreationTime(time);

		int max = Integer.parseInt(txfMaxHandleTime.getText());
		if (max != c.getMaxHandleTime())
			c.setMaxHandleTime(max);
		int min = Integer.parseInt(txfMinContractLength.getText());
		if (min != c.getMinContractLength())
			c.setMinContractLength(min);
		double price = Double.parseDouble(txfPrice.getText());
		if (price != c.getPrice())
			c.setPrice(price);
		dcManageContractType.editContractType(c);
		csb.refreshTable();
		csb.selectContractType(c);
	}

	// Event Listener on Button[#btnAdd].onAction
	@FXML
	public void btnAddOnAction(ActionEvent event) {
		// TODO errors
		String name = txfName.getText();
		TICKETCREATIONMETHOD method = TICKETCREATIONMETHOD.valueOf(cbTicketCreationMethod.getValue());
		TICKETCREATIONTIME time = TICKETCREATIONTIME.valueOf(cbTicketCreationTime.getValue());
		int max = Integer.parseInt(txfMaxHandleTime.getText());
		int min = Integer.parseInt(txfMinContractLength.getText());
		double price = Double.parseDouble(txfPrice.getText());

		ContractType c = dcManageContractType.addContractType(name, method, time, max, min, price);
		csb.selectContractType(c);
	}

}
