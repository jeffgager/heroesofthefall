package com.hotf.client.action.result;
 
import net.customware.gwt.dispatch.shared.Result;

public class UploadUrlResult implements Result {

	private String url;

	public UploadUrlResult() {
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
