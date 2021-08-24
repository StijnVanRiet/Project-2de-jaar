package gui.manageEmployee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import domain.DCManageUser;
import domain.Employee;
import domain.ROLE;
import domain.STATUS;
import errors.InfoException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewEmployeeController extends VBox {

	@FXML
	private Label lblUsername;
	@FXML
	private GridPane formGrid;
	@FXML
	private Label lblStatus;
	@FXML
	private ChoiceBox<String> cbStatus;
	@FXML
	private ChoiceBox<String> cbRole;
	@FXML
	private Label lblSeniority;
	@FXML
	private TextField txfSeniority;
	@FXML
	private TextField txfFirstName;
	@FXML
	private TextField txfLastName;
	@FXML
	private TextField txfCountry;
	@FXML
	private TextField txfCity;
	@FXML
	private TextField txfStreetName;
	@FXML
	private TextField txfStreetNumber;
	@FXML
	private Label lblError;
	@FXML
	private TextField txfPrivatePhoneNumber;
	@FXML
	private TextField txfWorkPhoneNumber;
	@FXML
	private TextField txfEmailAddress;
	@FXML
	private HBox buttons;
	@FXML
	private Button btnSaveChanges;
	@FXML
	private Button btnAdd;
	
	private final DCManageUser dcManageUser = new DCManageUser();
	private final EmployeesSidebarController esb;
	// this object gets shown in the ViewController
	private final Employee e;

	/**
	 * dit scherm toont de geselecteerde employee
	 */
	public ViewEmployeeController(EmployeesSidebarController esb, Employee e) {
		this.esb = esb;
		this.e = e;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEmployee.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// lists fill up the items in the choiceboxes ( aka dropdown boxes )
		ObservableList<String> employeeRoleList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.ROLE.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbRole.setItems(employeeRoleList);
		ObservableList<String> statusList = FXCollections.observableArrayList(
				EnumSet.allOf(domain.STATUS.class).stream().map(r -> r.name()).collect(Collectors.toList()));
		cbStatus.setItems(statusList);
		

		// labels veranderen naar tekst van de employee
		if (e != null) {
			cbRole.setValue(e.getRole().toString());
			cbStatus.setValue(e.getStatus().toString());
			lblUsername.setText("Employee " + e.getUsername());
			txfSeniority.setText(e.getSeniority());
			txfFirstName.setText(e.getFirstName());
			txfLastName.setText(e.getLastName());

			String[] items = e.getAddress().split("\\s*,\\s*");
			txfCountry.setText(items[0]);
			txfCity.setText(items[1]);
			txfStreetName.setText(items[2]);
			txfStreetNumber.setText(items[3]);

			txfPrivatePhoneNumber.setText(e.getPhoneNumbers().get(0));
			txfWorkPhoneNumber.setText(e.getPhoneNumbers().get(1));

			txfEmailAddress.setText(e.getEmail());

			buttons.getChildren().remove(btnAdd);
		}

		// new employee
		else {
			txfFirstName.setDisable(false);
			txfFirstName.setEditable(true);
			txfLastName.setDisable(false);
			txfLastName.setEditable(true);
			
			lblUsername.setText("New Employee");
			formGrid.getChildren().remove(lblStatus);
			formGrid.getChildren().remove(cbStatus);
			formGrid.getChildren().remove(lblSeniority);
			formGrid.getChildren().remove(txfSeniority);

			buttons.getChildren().remove(btnSaveChanges);
		}

	}

	// Event Listener on Button[#btnSaveChanges].onAction
	@FXML
	public void btnSaveChangesOnAction(ActionEvent event) {
		String firstName = txfFirstName.getText();
		String lastName = txfLastName.getText();
		STATUS status = STATUS.valueOf(cbStatus.getValue());
		ROLE role = ROLE.valueOf(cbRole.getValue());
		String address = String.join(", ", txfCountry.getText(), txfCity.getText(), txfStreetName.getText(), txfStreetNumber.getText());
		List<String> phoneNumbers = List.of(txfPrivatePhoneNumber.getText(), txfWorkPhoneNumber.getText());
		String email = txfEmailAddress.getText();

		try {
			Employee editedEmployee = new Employee.Builder().role(role).firstName(firstName)
					.lastName(lastName).address(address).phoneNumbers(phoneNumbers).email(email).build(e);
			lblError.setVisible(false);
			dcManageUser.editEmployee(editedEmployee); //enkel update naar de db
			esb.refreshTable();
			esb.selectEmployee(editedEmployee);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
		
	}

	// Event Listener on Button[#btnAdd].onAction
	@FXML
	public void btnAddOnAction(ActionEvent event) {
		ROLE role;
		try {
			role = ROLE.valueOf(cbRole.getValue());				//To avoid crash if no role selected
		} catch (NullPointerException e) {
			role = null;
		}
		String firstName = txfFirstName.getText();
		String lastName = txfLastName.getText();
		String address = String.join(", ", txfCountry.getText(), txfCity.getText(), txfStreetName.getText(), txfStreetNumber.getText());
		List<String> phoneNumbers = List.of(txfPrivatePhoneNumber.getText(), txfWorkPhoneNumber.getText());
		String email = txfEmailAddress.getText();
		
		try {
			Employee newEmployee = new Employee.Builder().role(role).firstName(firstName)
					.lastName(lastName).address(address).phoneNumbers(phoneNumbers).email(email).build(null);
			dcManageUser.addEmployee(newEmployee);
			esb.selectEmployee(newEmployee);
			lblError.setVisible(false);
		} catch (InfoException ie) {
			lblError.setText(ie.getInvalidInfo());
			lblError.setVisible(true);
		}
		
	}

}
