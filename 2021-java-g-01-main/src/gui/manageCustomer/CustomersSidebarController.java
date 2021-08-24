package gui.manageCustomer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import java.io.IOException;
import domain.Customer;
import domain.DCManageUser;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class CustomersSidebarController extends VBox {

	@FXML
	private MenuButton filter;
	@FXML
	private TextField txtFilter;
	@FXML
	private Button btnAddCustomer;
	@FXML
	private TableView<Customer> customersTable;
	@FXML
	private TableColumn<Customer, String> usernameCol;
	@FXML
	private TableColumn<Customer, String> statusCol;
	@FXML
	private TableColumn<Customer, String> companyCol;

	private final DCManageUser dcManageUser = new DCManageUser();
	private final CustomersScreenViewController csv;

	public CustomersSidebarController(CustomersScreenViewController csv) {
		this.csv = csv;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomersSidebar.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
		statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
		companyCol.setCellValueFactory(cellData -> cellData.getValue().companyProperty());

		customersTable.setItems(dcManageUser.getCustomerList());

		// Add change listener
		customersTable.getSelectionModel().selectedItemProperty()
				.addListener((observableValue, oldCustomer, newCustomer) -> {
					if (newCustomer != null) {
						csv.changeContent(new ViewCustomerController(this, newCustomer));
					}
				});

	}

	// Event Listener on Button[#btnAddCustomer].onAction
	@FXML
	public void addCustomer(ActionEvent event) {
		customersTable.getSelectionModel().clearSelection();
		csv.changeContent(new ViewCustomerController(this, null));
	}

	public void selectCustomer(Customer c) {
		// selecteer de juiste customer in de lijst (nodig als een nieuwe customer wordt
		// gemaakt
		customersTable.getSelectionModel().select(c);
		// vraag aan csv om zijn content te veranderen naar een scherm gebaseerd op de
		// meegegeven customer
		csv.changeContent(new ViewCustomerController(this, c));
	}

	// Event Listener on MenuItem[#btnFilterUsername].onAction
	@FXML
	public void filterUsername(ActionEvent event) {
		filter.setText("Username");
	}


	// Event Listener on MenuItem[#btnFilterStatus].onAction
	@FXML
	public void filterStatus(ActionEvent event) {
		filter.setText("Status");
	}

	// Event Listener on MenuItem[#btnFilterCompany].onAction
	@FXML
	public void filterCompany(ActionEvent event) {
		filter.setText("Company");
	}

	// Event Listener on TextField[#txtFilter].onKeyReleased
	@FXML
	public void filter(KeyEvent event) {
		dcManageUser.changeCustomerFilter(filter.getText(), txtFilter.getText());
	}

	public void refreshTable() {
		// werkt enkel als een rij wordt geupdate, niet als een rij wordt toegevoegd,
		// ik weet niet waarom
		customersTable.refresh();
	}

}
