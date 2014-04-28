package com.hotf.client.exception;


/**
 * @author Jeff Gager
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {
	
	private String validationCode;
	
	public ValidationException(String validationCode) {
		this.validationCode = validationCode;
	}

	public String getValidationCode() {
		return validationCode;
	}
	
	@Override
	public String getMessage() {
		return validationCode.toString();
	}
	
}
