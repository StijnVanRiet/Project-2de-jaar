package domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface RegexChecker {

	public static boolean checkRegex(String pattern, String toCheck) {
		Pattern p = Pattern.compile(pattern);  
		Matcher m = p.matcher(toCheck);
		if (m.matches())
			return true;
		return false;
	}
	
	public static boolean checkEmail(String email) {
		return checkRegex(".+@.+\\..+", email);
	}
	
	public static boolean checkHouseNumber(String houseNumber) {
		return checkRegex("^\\d+\\w?$", houseNumber);
	}
	
	public static boolean checkPhoneNumber(String phoneNumber) {
		return checkRegex("^(?:\\+\\d{1,3}|0\\d{1,3}|00\\d{1,2})?(?:\\s?\\(\\d+\\))?(?:[-\\/\\s.]|\\d)+$", phoneNumber); //Source regex: Stackoverlow
	}
}
