package domain;

import java.util.Date;

public class LoginAttempt {
	private Date time;
	private String username;
	private boolean succesful;

	public LoginAttempt(String username, Date time, boolean succesful) {
		setUsername(username);
		setTime(time);
		setSuccesful(succesful);
	}

	private Date getTime() {
		return time;
	}

	private void setTime(Date time) {
		this.time = time;
	}

	public String getUsername() {
		return username;
	}

	private void setUsername(String username) {
		this.username = username;
	}

	private boolean isSuccesful() {
		return succesful;
	}

	private void setSuccesful(boolean succesful) {
		this.succesful = succesful;
	}

}
