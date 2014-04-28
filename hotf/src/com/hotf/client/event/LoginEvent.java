package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.AccountResult;

public class LoginEvent extends GwtEvent<LoginEventHandler> {

	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();

	private AccountResult accountResult;
	
	public LoginEvent(AccountResult accountResult) {
		super();
		this.accountResult = accountResult;
		GWT.log("Firing LoginEvent");
	}

	@Override
	public Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLogin(this);
	}

	public AccountResult getAccountResult() {
		return accountResult;
	}

}
