package com.hotf.model.val;

import java.util.regex.Pattern;

public class PatternValidator {

	private Pattern pattern;
	private String message;
	
	public PatternValidator(String patternString, String message) {
		pattern = Pattern.compile(patternString);
		this.message = message;
	}
	
	public String validate(String value) throws ValidationException {
		if (!pattern.matcher(value).matches()) {
			throw new ValidationException(message);
		}
		return value;
	}

}
