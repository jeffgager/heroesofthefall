package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.AccountResult;

public class LoadAccountsEvent extends GwtEvent<LoadAccountsEventHandler> {

	public static Type<LoadAccountsEventHandler> TYPE = new Type<LoadAccountsEventHandler>();

	private List<AccountResult> accounts;

	public LoadAccountsEvent(List<AccountResult> accounts) {
		super();
		this.accounts = accounts;
		GWT.log("Firing LoadAccountsEvent");
	}

	@Override
	public Type<LoadAccountsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadAccountsEventHandler handler) {
		handler.onLoadAccounts(this);
	}

	public List<AccountResult> getAccounts() {
		return accounts;
	}

}
