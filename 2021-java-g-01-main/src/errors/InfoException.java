package errors;

import java.util.List;

public class InfoException extends RuntimeException{
	private String className;
	private List<String> invalidInfo;
	
	public InfoException(String className, List<String> invalidInfo) {
		super("Cannot save changes in " + className + "please check the following issues: ");
		this.className = className;
		this.invalidInfo = invalidInfo;
	}
	
	public String getInvalidInfo(){
		String issues = "Cannot save changes in " + className + ", please check the following issues: ";
		for (String issue : invalidInfo) {
			issues += String.format("%n - %s", issue);
		}
		return issues;
	}
	
	
	//Superclass constructors
	public InfoException() {
		super();
	}

	public InfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InfoException(String message, Throwable cause) {
		super(message, cause);
	}

	public InfoException(String message) {
		super(message);
	}

	public InfoException(Throwable cause) {
		super(cause);
	}
	
	

}
