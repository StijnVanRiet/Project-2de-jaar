package domain;

public abstract class DCLoggedInUser {
	private static Employee currentUser;
	protected static final UserManager userManager = new UserManager();

	public Employee getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Employee currentUser) {
		DCLoggedInUser.currentUser = currentUser;
	}

	public static UserManager getUsermanager() {
		return userManager;
	}

}
