package domain;

public class DCLoginUser extends DCLoggedInUser {

	public DCLoginUser() {
	}

	public String verifyUser(String un, String pw) {
		return userManager.verifyUser(un, pw);
	}

}
