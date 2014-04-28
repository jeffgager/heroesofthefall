package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.AccountResult;

public class ChangeAccountEvent extends GwtEvent<ChangeAccountEventHandler> {

	public static Type<ChangeAccountEventHandler> TYPE = new Type<ChangeAccountEventHandler>();

	private AccountResult account;
	
	public ChangeAccountEvent(AccountResult account) {
		super();
		this.account = account;
		GWT.log("Firing ChangeAccountEvent");
	}

	@Override
	public Type<ChangeAccountEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeAccountEventHandler handler) {
		handler.onChange(this);
	}

	public AccountResult getAccount() {
		return account;
	}

}
