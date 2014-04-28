package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.AccountsResult;

public class GetAccountsAction implements Action<AccountsResult> {

	private String name;
	
	public GetAccountsAction() {
		super();
	}

	/**
	 * @param name
	 */
	public GetAccountsAction(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
