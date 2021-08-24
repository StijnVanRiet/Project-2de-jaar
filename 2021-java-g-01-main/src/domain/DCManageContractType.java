package domain;

import javafx.collections.ObservableList;

public class DCManageContractType {

	private final ContractTypeManager contractTypeManager;

	public DCManageContractType() {
		this.contractTypeManager = new ContractTypeManager();
	}

	public ObservableList<ContractType> getContractTypeList() {
		return contractTypeManager.getContractTypeList();
	}

	public void changeContractTypeFilter(String status) {
		contractTypeManager.changeContractTypeFilter(status);
	}

	public ContractType addContractType(String name, TICKETCREATIONMETHOD method, TICKETCREATIONTIME time, int max,
			int min, double price) {
		return contractTypeManager.addContractType(name, method, time, max, min, price);
	}

	public void editContractType(ContractType c) {
		contractTypeManager.editContractType(c);
	}
}
