/**
 * 
 */
package com.hotf.client.exception;

/**
 * @author Jeff
 *
 */
@SuppressWarnings("serial")
public class RemoveLocationWithCharactersException extends RuntimeException {

	public static final String MESSAGE = "You cannot remove a Location that contains Characters";
	
	public RemoveLocationWithCharactersException() {
		super(MESSAGE);
	}

}
