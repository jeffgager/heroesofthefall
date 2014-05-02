package com.hotf.model.val;

import java.util.regex.Pattern;

public class NameValidator {

	private static final String NAME_PATTERN = "^[a-zA-Z]{3,3}[a-zA-Z0-9]{0,27}";
	private static final String NAME_MESSAGE = "Name must have at least three letters followed by up to 27 letters or numbers";

	private Pattern pattern;
	
	public NameValidator() {
		pattern = Pattern.compile(NAME_PATTERN);
	}
	
	public String validate(String value) throws ValidationException {
		if (!pattern.matcher(value).matches()) {
			throw new ValidationException(NAME_MESSAGE);
		}
		return value;
	}

}
