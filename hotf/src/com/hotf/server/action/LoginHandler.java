package com.hotf.server.action;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.LoginAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.server.model.Account;

public class LoginHandler implements ActionHandler<LoginAction, AccountResult> {

	private static final Logger log = Logger.getLogger(LoginHandler.class.getName());

	private GetAccountHandler getAccountHandler;

	public LoginHandler() {
	}
	
	@Override
	public AccountResult execute(LoginAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling LoginAction");

		Account account = getAccountHandler.getMyAccount();

		return getAccountHandler.getResult(account);

	}

	@Override
	public Class<LoginAction> getActionType() {
		return LoginAction.class;
	}

	@Override
	public void rollback(LoginAction action, AccountResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

}