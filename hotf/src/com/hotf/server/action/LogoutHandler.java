package com.hotf.server.action;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.hotf.client.action.LogoutAction;
import com.hotf.client.action.result.LogoutResult;

public class LogoutHandler implements ActionHandler<LogoutAction, LogoutResult> {

	private static final Logger log = Logger.getLogger(LogoutHandler.class.getName());

	@Override
	public LogoutResult execute(LogoutAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling LogoutAction");

		LogoutResult logoutResult = new LogoutResult();
		logoutResult.setUrl(logout(action.getUrlbase()));
		return logoutResult;

	}

	/**
	 * Logout of GAE user.
	 * 
	 * @param hostpage
	 *            page to return to when logging back in
	 * @return URL to logout client from GAE
	 */
	public static String logout(String hostpage) {

		try {

			log.info("logout destination=" + hostpage);
			UserService userService = UserServiceFactory.getUserService();
			String url = userService.createLogoutURL(hostpage);
			log.info("logout url=" + url);
			return url;

		} catch (RuntimeException t) {

			log.severe(t.getMessage());
			throw t;

		}

	}

	@Override
	public Class<LogoutAction> getActionType() {
		return LogoutAction.class;
	}

	@Override
	public void rollback(LogoutAction action, LogoutResult result, ExecutionContext context) throws DispatchException {
		
	}

}