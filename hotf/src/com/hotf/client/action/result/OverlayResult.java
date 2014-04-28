/**
 * 
 */
package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 *
 */
public class OverlayResult implements Result {

	private String id;
	private String overlayUrl;
	
	public OverlayResult() {
		super();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the overlayUrl
	 */
	public String getOverlayUrl() {
		return overlayUrl;
	}

	/**
	 * @param overlayUrl the overlayUrl to set
	 */
	public void setOverlayUrl(String overlayUrl) {
		this.overlayUrl = overlayUrl;
	}


}