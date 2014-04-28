package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.AccountResult;

public class SaveAccountAction implements Action<AccountResult> {

	private AccountResult account;

	public SaveAccountAction() {
		super();
	}

	public SaveAccountAction(AccountResult account) {
		super();
		this.account = account;
	}

	/**
	 * @return the account
	 */
	public AccountResult getAccount() {
		return account;
	}

}
