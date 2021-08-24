package gui.manageEmployee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import java.io.IOException;
import domain.DCManageUser;
import domain.Employee;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class EmployeesSidebarController extends VBox {

	@FXML
	private MenuButton filter;
	@FXML
	private TextField txtFilter;
	@FXML
	private Button btnAddEmployee;
	@FXML
	private TableView<Employee> employeesTable;
	@FXML
	private TableColumn<Employee, String> usernameCol;
	@FXML
	private TableColumn<Employee, String> lastNameCol;
	@FXML
	private TableColumn<Employee, String> firstNameCol;
	@FXML
	private TableColumn<Employee, String> statusCol;
	@FXML
	private TableColumn<Employee, String> roleCol;

	private final DCManageUser dcManageUser = new DCManageUser();
	private final EmployeesScreenViewController esv;

	public EmployeesSidebarController(EmployeesScreenViewController esv) {
		this.esv = esv;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesSidebar.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
		lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
		firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
		roleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

		employeesTable.setItems(dcManageUser.getEmployeeList());

		// Add change listener
		employeesTable.getSelectionModel().selectedItemProperty()
				.addListener((observableValue, oldEmployee, newEmployee) -> {
					if (newEmployee != null) {
						esv.changeContent(new ViewEmployeeController(this, newEmployee));
					}
				});

	}

	// Event Listener on Button[#btnPerson].onAction
	@FXML
	public void addEmployee(ActionEvent event) throws IOException {
		employeesTable.getSelectionModel().clearSelection();
		esv.changeContent(new ViewEmployeeController(this, null));
	}

	public void selectEmployee(Employee e) {
		// selecteer de juiste employee in de lijst (nodig als een nieuwe employee wordt
		// gemaakt
		employeesTable.getSelectionModel().select(e);
		// vraag aan esv om zijn content te veranderen naar een scherm gebaseerd op de
		// meegegeven employee
		esv.changeContent(new ViewEmployeeController(this, e));
	}

	// Event Listener on MenuItem[#btnFilterUsername].onAction
	@FXML
	public void filterUsername(ActionEvent event) {
		filter.setText("Username");
	}

	// Event Listener on MenuItem[#btnFilterLastName].onAction
	@FXML
	public void filterLastName(ActionEvent event) {
		filter.setText("Last Name");
	}

	// Event Listener on MenuItem[#btnFilterFirstName].onAction
	@FXML
	public void filterFirstName(ActionEvent event) {
		filter.setText("First Name");
	}

	// Event Listener on MenuItem[#btnFilterStatus].onAction
	@FXML
	public void filterStatus(ActionEvent event) {
		filter.setText("Status");
	}

	// Event Listener on MenuItem[#btnFilterRole].onAction
	@FXML
	public void filterRole(ActionEvent event) {
		filter.setText("Role");
	}

	// Event Listener on TextField[#txtFilter].onKeyReleased
	@FXML
	public void filter(KeyEvent event) {
		dcManageUser.changeEmployeeFilter(filter.getText(), txtFilter.getText());
	}

	public void refreshTable() {
		// werkt enkel als een rij wordt geupdate, niet als een rij wordt toegevoegd,
		// ik weet niet waarom
		employeesTable.refresh();
	}

}
