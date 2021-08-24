package domain;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.UserDaoJpa;

public class ContractTypeManager {
	private ObservableList<ContractType> contractTypeList;
	private FilteredList<ContractType> filteredContractTypeList;
	private SortedList<ContractType> sortedContractTypeList;

	// sort
	private final Comparator<ContractType> byContractTypeStatus = (c1, c2) -> c1.getContractTypeStatus()
			.compareTo(c2.getContractTypeStatus());

	private UserDaoJpa userdao;

	public ContractTypeManager() {
		userdao = new UserDaoJpa();

		contractTypeList = FXCollections.observableList(userdao.getAllContractTypes());
		filteredContractTypeList = new FilteredList<>(contractTypeList, p -> true);
		sortedContractTypeList = new SortedList<>(filteredContractTypeList, byContractTypeStatus);
	}

	public ObservableList<ContractType> getContractTypeList() {
		return sortedContractTypeList;
	}

	public void changeContractTypeFilter(String status) {
		filteredContractTypeList.setPredicate(type -> {
			if (status == null || status.isBlank())
				return true;

			return (type.getContractTypeStatus().name().toLowerCase().equals(status));
		});
	}

	public ContractType addContractType(String name, TICKETCREATIONMETHOD method, TICKETCREATIONTIME time, int max,
			int min, double price) {
		// TODO add contract type
		return null;
	}

	public void editContractType(ContractType c) {
		// TODO edit contract type
	}
}
