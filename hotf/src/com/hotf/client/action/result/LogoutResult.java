package com.hotf.client.action.result;
 
import net.customware.gwt.dispatch.shared.Result;

public class LogoutResult implements Result {

	private String url;

	public LogoutResult() {
		super();
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
