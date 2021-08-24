package gui.manageCustomer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import domain.Contact;
import domain.Contract;
import domain.Customer;
import domain.CustomerManager;
import domain.DCManageUser;
import domain.STATUS;
import errors.InfoException;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewCustomerController extends VBox {

	@FXML
	private Label lblUsername;
	@FXML
	private Label lblError;
	@FXML
	private HBox loyaltyStatusBox;
	@FXML
	private TextField txtfLoyalty;
	@FXML
	private Label lblStatus;
	@FXML
	private ChoiceBox<String> cbStatus;
	@FXML
	private TextField txtfCompanyName;
	@FXML
	private TextField txfCountry;
	@FXML
	private TextField txfCity;
	@FXML
	private TextField txfStreetName;
	@FXML
	private TextField txfStreetNumber;
	@FXML
	private TextField txfPrivatePhoneNumber;
	@FXML
	private TextField txfWorkPhoneNumber;
	@FXML
	private VBox contactVBox;
	@FXML
	private TextField txtfAddContactFirstName;
	@FXML
	private TextField txtfAddContactLastName;
	@FXML
	private TextField txtfAddContactEmail;
	@FXML
	private Label lblErrorAddContact;
	@FXML
	private HBox buttons;
	@FXML
	private Button btnSaveChanges;
	@FXML
	private Button btnAdd;
	@FXML
	private VBox tablesVBox;
	@FXML
	private TableView<Contact> contactPersonsTable;
	@FXML
	private TableColumn<Contact, String> firstNameCol;
	@FXML
	private TableColumn<Contact, String> lastNameCol;
	@FXML
	private TableColumn<Contact, String> emailCol;
	@FXML
	private TextField txtfAddContactFirstName2;
	@FXML
	private TextField txtfAddContactLastName2;
	@FXML
	private TextField txtfAddContactEmail2;
	@FXML
	private Button btnAddContactPerson;
	@FXML
	private Label lblErrorEditContact1;
	@FXML
	private VBox editContactForm;
	@FXML
	private TextField txtfEditContactFirstName;
	@FXML
	private TextField txtfEditContactLastName;
	@FXML
	private TextField txtfEditContactEmail;
	@FXML
	private Button btnSaveContactChanges;
	@FXML
	private Label lblErrorEditContact;
	@FXML
	private TableView<Contract> contractsTable;
	@FXML
	private TableColumn<Contract, String> numberCol;
	@FXML
	private TableColumn<Contract, String> typeCol;
	@FXML
	private TableColumn<Contract, String> statusCol;
	@FXML
	private TableColumn<Contract, String> startDateCol;
	@FXML
	private TableColumn<Contract, String> endDateCol;

	private CustomerManager customerManager; // gaat de tableview van contacts en contracts regelen
	private final DCManageUser dcManageUser = new DCManageUser();
	private final CustomersSidebarController csb;
	private final Customer c;

	/**
	 * dit scherm toont de geselecteerde customer
	 */
	public ViewCustomerController(CustomersSidebarController csb, Customer c) {
		this.csb = csb;
		this.c = c;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewCustomer.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// lists fill up the items in the choiceboxes ( aka dropdown boxes )
		ObservableList<String> statusList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.STATUS.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbStatus.setItems(statusList);

		// labels veranderen naar tekst van de customer
		if (c != null) {
			customerManager = new CustomerManager(c);
			lblUsername.setText("Customer " + c.getUsername());
			cbStatus.setValue(c.getStatus().toString());
			txtfLoyalty.setText(c.getSeniority());

			txtfCompanyName.setText(c.getCompanyName());

			String[] items = c.getCompany().getAddress().split("\\s*,\\s*");
			txfCountry.setText(items[0]);
			txfCity.setText(items[1]);
			txfStreetName.setText(items[2]);
			txfStreetNumber.setText(items[3]);

			txfPrivatePhoneNumber.setText(c.getCompany().getPhoneNumbers().get(0));
			txfWorkPhoneNumber.setText(c.getCompany().getPhoneNumbers().get(1));

			firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
			lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
			emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

			contactPersonsTable.setItems(customerManager.getContactList());

			// disable button als geen contact geselecteerd is
			btnSaveContactChanges.disableProperty()
					.bind(contactPersonsTable.getSelectionModel().selectedItemProperty().isNull());

			// Add change listener
			contactPersonsTable.getSelectionModel().selectedItemProperty()
					.addListener((observableValue, oldContact, newContact) -> {
						if (newContact != null) {
							txtfEditContactFirstName.setText(newContact.getFirstName());
							txtfEditContactLastName.setText(newContact.getLastName());
							txtfEditContactEmail.setText(newContact.getEmail());
						}
					});

			numberCol.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
			typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
			statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
			startDateCol.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
			endDateCol.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
			contractsTable.setItems(customerManager.getContractList());

			buttons.getChildren().remove(btnAdd);
			this.getChildren().remove(contactVBox);
		}

		// new customer
		else {
			lblUsername.setText("New Customer");
			this.getChildren().remove(loyaltyStatusBox);
			this.getChildren().remove(tablesVBox);

			buttons.getChildren().remove(btnSaveChanges);
		}

	}

	// Event Listener on Button[#btnSaveChanges].onAction
	@FXML
	public void btnSaveChangesOnAction(ActionEvent event) {
		STATUS status = STATUS.valueOf(cbStatus.getValue());
		String compName = txtfCompanyName.getText();
		String compAddress = String.join(", ", txfCountry.getText(), txfCity.getText(), txfStreetName.getText(),
				txfStreetNumber.getText());
		List<String> compPhoneNumbers = List.of(txfPrivatePhoneNumber.getText(), txfWorkPhoneNumber.getText());
		
		try {
			Customer editedCustomer = new Customer.Builder().status(status)
					.companyName(compName).address(compAddress).phoneNumbers(compPhoneNumbers).build(c);
			lblError.setVisible(false);
			dcManageUser.editCustomer(editedCustomer);
			csb.refreshTable();
			csb.selectCustomer(editedCustomer);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}	

	}

	// Event Listener on Button[#btnAdd].onAction
	@FXML
	public void btnAddOnAction(ActionEvent event) {
		String compName = txtfCompanyName.getText();
		String compAddress = String.join(", ", txfCountry.getText(), txfCity.getText(), txfStreetName.getText(),
				txfStreetNumber.getText());
		List<String> compPhoneNumbers = List.of(txfPrivatePhoneNumber.getText(), txfWorkPhoneNumber.getText());
		String firstNameContact = txtfAddContactFirstName.getText();
		String lastNameContact = txtfAddContactLastName.getText();
		String emailContact = txtfAddContactEmail.getText();
		
		try {
			Customer newCustomer = new Customer.Builder().companyName(compName).address(compAddress).phoneNumbers(compPhoneNumbers)
					.contactFirstName(firstNameContact).contactLastName(lastNameContact).email(emailContact).build(null);
			lblError.setVisible(false);
			dcManageUser.addCustomer(newCustomer);
			csb.selectCustomer(newCustomer);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
		
	}

	// Event Listener on Button[#btnAddContactPerson].onAction
	@FXML
	public void btnAddContactPersonOnAction(ActionEvent event) {
		String firstNameContact = txtfAddContactFirstName2.getText();
		String lastNameContact = txtfAddContactLastName2.getText();
		String emailContact = txtfAddContactEmail2.getText();
		
		try {
			Customer newContactCustomer = new Customer.Builder().contactFirstName(firstNameContact).contactLastName(lastNameContact).email(emailContact)
					.build(c, null);
			lblError.setVisible(false);
			dcManageUser.editCustomer(newContactCustomer);
			csb.selectCustomer(newContactCustomer);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
		
	}

	// Event Listener on Button[#btnSaveContactChanges].onAction
	@FXML
	public void btnSaveContactChangesOnAction(ActionEvent event) {
		String firstNameContact = txtfEditContactFirstName.getText();
		String lastNameContact = txtfEditContactLastName.getText();
		String emailContact = txtfEditContactEmail.getText();
		Contact contactToEdit = contactPersonsTable.getSelectionModel().getSelectedItem();
		
		try {
			Customer editedContactCustomer = new Customer.Builder().contactFirstName(firstNameContact).contactLastName(lastNameContact).email(emailContact)
					.build(c, contactToEdit);
			lblError.setVisible(false);
			dcManageUser.editCustomer(editedContactCustomer);
			csb.selectCustomer(editedContactCustomer);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
	}

}
