/**
 * 
 */
package com.hotf.client.exception;

/**
 * @author Jeff
 *
 */
@SuppressWarnings("serial")
public class AccessRightException extends RuntimeException {

	public static final String MESSAGE = "Access right violation";

	public AccessRightException() {
		super(MESSAGE);
	}

	public AccessRightException(String message) {
		super(message);
	}

}
