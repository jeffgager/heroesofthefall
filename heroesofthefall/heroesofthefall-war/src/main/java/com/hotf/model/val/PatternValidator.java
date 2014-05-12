package com.hotf.model.val;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PatternValidator {

	public static final String MESSAGE_RESOURCE_BUNDLE = "validation";
	public static final String VALIDATION_TYPE = "validation.pattern";
	private Pattern pattern;
	private String message;
	
	public PatternValidator(final String kind, final String property) {
		assert kind != null && property != null;
		ResourceBundle res = ResourceBundle.getBundle(MESSAGE_RESOURCE_BUNDLE);
		pattern = Pattern.compile(res.getString(kind + "." + property + "." + VALIDATION_TYPE));
		this.message = res.getString(kind + "." + property + "." + VALIDATION_TYPE + ".message");
	}
	
	public String validate(final String value) throws ValidationException {
		if (!pattern.matcher(value).matches()) {
			throw new ValidationException(message);
		}
		return value;
	}

}
