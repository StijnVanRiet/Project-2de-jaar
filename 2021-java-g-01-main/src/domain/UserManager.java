package domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.UserDaoJpa;

public class UserManager extends DCLoggedInUser {

	private UserDaoJpa userdao;

	private List<LoginAttempt> loginAttempts;

	// Manage Employees
	private ObservableList<Employee> employeeList;
	private FilteredList<Employee> filteredEmployeeList;
	private SortedList<Employee> sortedEmployeeList;

	// Manage Customers
	private ObservableList<Customer> customerList;
	private FilteredList<Customer> filteredCustomerList;
	private SortedList<Customer> sortedCustomerList;

	// sort
	private final Comparator<User> byUsername = (u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername());
	private final Comparator<User> byLastName = (u1, u2) -> u1.getLastName().compareToIgnoreCase(u2.getLastName());
	private final Comparator<User> byFirstName = (u1, u2) -> u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
	private final Comparator<User> byStatus = (p1, p2) -> p1.getStatus().compareTo(p2.getStatus());
	private final Comparator<User> sortOrder = byUsername.thenComparing(byLastName).thenComparing(byFirstName)
			.thenComparing(byStatus);

	public UserManager() {
		userdao = new UserDaoJpa();
		this.loginAttempts = new ArrayList<>();
	}

	public ObservableList<Employee> getEmployeeList() {
		employeeList = FXCollections.observableList(userdao.getEmployees());
		filteredEmployeeList = new FilteredList<>(employeeList, p -> true);
		sortedEmployeeList = new SortedList<>(filteredEmployeeList, sortOrder);
		return sortedEmployeeList;
	}

	public ObservableList<Customer> getCustomerList() {
		customerList = FXCollections.observableList(userdao.getCustomers());
		filteredCustomerList = new FilteredList<>(customerList, p -> true);
		sortedCustomerList = new SortedList<>(filteredCustomerList, sortOrder);
		return sortedCustomerList;
	}

	public List<Employee> getEngineerList() {
		// TODO kan deze list rechtstreeks uit Databank gemaakt worden
		// geeft een lijst van enkel engineers
		return getEmployeeList().stream().filter(e -> e.getRole().equals(ROLE.Engineer)).collect(Collectors.toList());
	}

	public String verifyUser(String username, String password) {

		Employee user;
		// zoek user
		try {
			if (userdao.getUserByUsername(username) instanceof Employee)
				user = (Employee) userdao.getUserByUsername(username);
			else
				throw new EntityNotFoundException();
		} catch (EntityNotFoundException e) {
			// TODO wordt bij verify user niet best een exception gethrowed?
			return "User not found";
		}
		String message = "";

		if (user.getStatus() != STATUS.Active) {
			message = "This account is " + user.getStatus().toString().toLowerCase();
			logLogin(username, false);
		} else {
			// password check
			if (user.getPassword().equals(password)) {
				setCurrentUser(user);
				logLogin(username, true);
			} else {
				message = "Incorrect password";
				logLogin(username, false);
				// login attempts check
				if (checkMaxLogin(username)) {
					message += ". Max login attempts exceeded. This account is now blocked";
					blockUser(username);
				}
			}
		}

		return message;
	}


	private void logLogin(String username, boolean successful) {
		loginAttempts.add(new LoginAttempt(username, new Date(), successful));
	}

	private boolean checkMaxLogin(String username) { // checken of user disabled moet worden
		return loginAttempts.stream().filter(l -> l.getUsername().equals(username)).count() >= 5;
	}

	private void blockUser(String username) {

		try {
			User toBlock = userdao.getUserByUsername(username);
			toBlock.setStatus(STATUS.Blocked);
			UserDaoJpa.startTransaction();
			userdao.update(toBlock);
			UserDaoJpa.commitTransaction();
		} catch (EntityNotFoundException e) {
			System.err.println("There is no such user with that username!");
		}

	}

	public void changeEmployeeFilter(String filter, String filterValue) {
		filteredEmployeeList.setPredicate(employee -> {
			// If filter text is empty, display all persons.
			if (filterValue == null || filterValue.isBlank())
				return true;

			boolean b = false;

			switch (filter.toLowerCase()) {
			case "username" -> b = employee.getUsername().toLowerCase().contains(filterValue.toLowerCase());
			case "status" -> b = employee.getStatus().name().toLowerCase().contains(filterValue.toLowerCase());
			case "role" -> b = employee.getRole().name().toLowerCase().contains(filterValue.toLowerCase());
			case "first name" -> b = employee.getFirstName().toLowerCase().contains(filterValue.toLowerCase());
			case "last name" -> b = employee.getLastName().toLowerCase().contains(filterValue.toLowerCase());
			}

			return b;
		});
	}

	public void changeCustomerFilter(String filter, String filterValue) {
		filteredCustomerList.setPredicate(customer -> {
			// If filter text is empty, display all persons.
			if (filterValue == null || filterValue.isBlank())
				return true;

			boolean b = false;

			switch (filter.toLowerCase()) {
			case "username" -> b = customer.getUsername().toLowerCase().contains(filterValue.toLowerCase());
			case "status" -> b = customer.getStatus().name().toLowerCase().contains(filterValue.toLowerCase());
			case "company" -> b = customer.getCompanyName().toLowerCase().contains(filterValue.toLowerCase());
			case "first name" -> b = customer.getFirstName().toLowerCase().contains(filterValue.toLowerCase());
			case "last name" -> b = customer.getLastName().toLowerCase().contains(filterValue.toLowerCase());
			}

			return b;
		});
	}

	public void addEmployee(Employee e) {
		UserDaoJpa.startTransaction();
		userdao.insert(e);
		UserDaoJpa.commitTransaction();
		employeeList.add(e);
	}


	public void addCustomer(Customer c) {
		UserDaoJpa.startTransaction();
		userdao.insert(c);
		UserDaoJpa.commitTransaction();
		customerList.add(c);
	}


	public void editEmployee(Employee e) {
		UserDaoJpa.startTransaction();
		userdao.update(e);
		UserDaoJpa.commitTransaction();
	}

	public void editCustomer(Customer c) {
		UserDaoJpa.startTransaction();
		userdao.update(c);
		UserDaoJpa.commitTransaction();
	}

}
