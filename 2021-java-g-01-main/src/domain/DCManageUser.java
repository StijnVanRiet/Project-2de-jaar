package domain;

import java.util.List;
import javafx.collections.ObservableList;

public class DCManageUser extends DCLoggedInUser {

	public DCManageUser() {
	}

	public void changeEmployeeFilter(String filter, String filterValue) {
		userManager.changeEmployeeFilter(filter, filterValue);
	}

	public void changeCustomerFilter(String filter, String filterValue) {
		userManager.changeCustomerFilter(filter, filterValue);
	}

	public ObservableList<Employee> getEmployeeList() {
		return userManager.getEmployeeList();
	}

	public ObservableList<Customer> getCustomerList() {
		return userManager.getCustomerList();
	}

	public List<Employee> getEngineerList() {
		return userManager.getEngineerList();
	}

	public void addEmployee(Employee e) {
		userManager.addEmployee(e);
	}

	public void addCustomer(Customer c) {
		userManager.addCustomer(c);
	}

	public void editEmployee(Employee e) {
		userManager.editEmployee(e);
	}

	public void editCustomer(Customer c) {
		userManager.editCustomer(c);
	}

}
