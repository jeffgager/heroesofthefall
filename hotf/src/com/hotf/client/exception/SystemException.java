/**
 * 
 */
package com.hotf.client.exception;

import net.customware.gwt.dispatch.shared.DispatchException;

/**
 * @author Jeff
 *
 */
@SuppressWarnings("serial")
public class SystemException extends DispatchException {

	public SystemException(String message) {
		super(message);
	}

}
