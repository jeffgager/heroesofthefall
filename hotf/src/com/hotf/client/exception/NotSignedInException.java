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
public class NotSignedInException extends DispatchException {

	private String url;
	
	public NotSignedInException() {
		super("Not signed in");
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
