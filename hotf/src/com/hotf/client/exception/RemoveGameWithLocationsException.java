/**
 * 
 */
package com.hotf.client.exception;

/**
 * @author Jeff
 *
 */
@SuppressWarnings("serial")
public class RemoveGameWithLocationsException extends RuntimeException {

	public static final String MESSAGE = "You cannot remove a Game that contains Locations";
	
	public RemoveGameWithLocationsException() {
		super(MESSAGE);
	}

}
