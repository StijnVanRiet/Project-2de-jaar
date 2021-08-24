package domain;

import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.UserDaoJpa;

public class CustomerManager {

	private final Customer c;

	// Manage contacts
	private ObservableList<Contact> contactList;
	private FilteredList<Contact> filteredContactList;
	private SortedList<Contact> sortedContactList;
	// soirt
	private final Comparator<Contact> byFirstName = (c1, c2) -> c1.getFirstName()
			.compareToIgnoreCase(c2.getFirstName());

	// Manage contracts
	private ObservableList<Contract> contractList;
	private FilteredList<Contract> filteredContractList;
	private SortedList<Contract> sortedContractList;
	// sort
	private final Comparator<Contract> byContractType = (c1, c2) -> c1.getContractType().toString()
			.compareToIgnoreCase(c2.getContractType().toString());

	public CustomerManager(Customer c) {
		this.c = c;
	}

	public ObservableList<Contact> getContactList() {
		contactList = FXCollections.observableList(c.getContacts());
		filteredContactList = new FilteredList<>(contactList, p -> true);
		sortedContactList = new SortedList<>(filteredContactList, byFirstName);
		return sortedContactList;
	}

	public ObservableList<Contract> getContractList() {
		contractList = FXCollections.observableList(c.getContracts());
		filteredContractList = new FilteredList<>(contractList, p -> true);
		sortedContractList = new SortedList<>(filteredContractList, byContractType);
		return sortedContractList;
	}
}
