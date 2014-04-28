package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.LogoutResult;

public class LogoutAction implements Action<LogoutResult> {

	private String urlbase;

	public LogoutAction() {
		super();
	}

	public LogoutAction(String urlbase) {
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
