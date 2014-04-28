package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.UploadUrlResult;

public class GetUploadUrlAction implements Action<UploadUrlResult> {

	private String urlbase;

	public GetUploadUrlAction() {
		super();
	}

	public GetUploadUrlAction(String urlbase) {
		super();
		this.urlbase = urlbase;
	}

	/**
	 * @return the urlbase
	 */
	public String getUrlbase() {
		return urlbase;
	}

	/**
	 * @param urlbase the urlbase to set
	 */
	public void setUrlbase(String urlbase) {
		this.urlbase = urlbase;
	}
	
}
