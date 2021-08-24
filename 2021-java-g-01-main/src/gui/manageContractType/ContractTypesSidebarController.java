package gui.manageContractType;

import java.io.IOException;
import domain.ContractType;
import domain.DCManageContractType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class ContractTypesSidebarController extends VBox {

	@FXML
	private MenuButton statusFilter;
	@FXML
	private Button btnAddContractType;
	@FXML
	private TableView<ContractType> contractTypesTable;
	@FXML
	private TableColumn<ContractType, String> nameCol;
	@FXML
	private TableColumn<ContractType, String> statusCol;
	@FXML
	private TableColumn<ContractType, String> numberOfRunningContractsCol;

	private final DCManageContractType dcManageContractType = new DCManageContractType();
	private final ContractTypeScreenViewController csv;

	public ContractTypesSidebarController(ContractTypeScreenViewController csv) {
		this.csv = csv;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ContractTypesSidebar.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
		//numberOfRunningContractsCol.setCellValueFactory(cellData -> cellData.getValue().numberOfRunningContractsProperty());

		contractTypesTable.setItems(dcManageContractType.getContractTypeList());

		// Add change listener
		contractTypesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldContractType, newContractType) -> {
			if (newContractType != null) {
				csv.changeContent(new ViewContractTypeController(this, newContractType));
			}
		});

	}

	// Event Listener on Button[#btnAddContractType].onAction
		@FXML
		public void addContractType(ActionEvent event) {
			contractTypesTable.getSelectionModel().clearSelection();
			csv.changeContent(new ViewContractTypeController(this, null));
		}

	public void selectContractType(ContractType c) {
		// selecteer het juiste ticket in de lijst (nodig als een nieuw ticket wordt
		// gemaakt
		contractTypesTable.getSelectionModel().select(c);
		// vraag aan tsv om zijn content te veranderen naar een scherm gebaseerd op het
		// meegegeven ticket
		csv.changeContent(new ViewContractTypeController(this, c));
	}

	// Event Listener on MenuItem[#filterAll].onAction
		@FXML
		public void filterAllOnAction(ActionEvent event) {
			statusFilter.setText("All");
			dcManageContractType.changeContractTypeFilter("");
		}
		// Event Listener on MenuItem[#filterActive].onAction
		@FXML
		public void filterActiveOnAction(ActionEvent event) {
			statusFilter.setText("Active");
			dcManageContractType.changeContractTypeFilter(statusFilter.getText());
		}
		// Event Listener on MenuItem[#filterNonactive].onAction
		@FXML
		public void filterNonActiveOnAction(ActionEvent event) {
			statusFilter.setText("Nonactive");
			dcManageContractType.changeContractTypeFilter(statusFilter.getText());
		}
	
	public void refreshTable() {
		// werkt enkel als een rij wordt geupdate, niet als een rij wordt toegevoegd,
		// ik weet niet waarom
		contractTypesTable.refresh();
	}
}
