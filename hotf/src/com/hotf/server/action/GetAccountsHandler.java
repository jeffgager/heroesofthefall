package com.hotf.server.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetAccountsAction;
import com.hotf.client.action.result.AccountsResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;

public class GetAccountsHandler implements ActionHandler<GetAccountsAction, AccountsResult> {

	private static final Logger log = Logger.getLogger(GetAccountsHandler.class.getName());

	private GetAccountHandler getAccountHandler;

	public GetAccountsHandler() {
	}

	@Override
	public AccountsResult execute(GetAccountsAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetAccountsAction");

		getAccountHandler.findGAEUser();

		AccountsResult accountsResult = new AccountsResult();
		for (Account a : get(action.getName())) {
			accountsResult.getAccounts().add(getAccountHandler.getResult(a));
		}
		return accountsResult;

	}

	@SuppressWarnings("unchecked")
	public List<Account> get(String name) throws NotSignedInException {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			//prepare query
			Query q = pm.newQuery(Account.class);
			Account account = getAccountHandler.getMyAccount();
			q.setRange(0, account.getSearchRows());
			q.setOrdering("nameUpper asc");
			List<Account> list;

			if (name != null && name.length() > 0) {

				//execute with filter
				q.declareParameters("String p_namemin");
				q.setFilter("nameUpper >= p_namemin");
				String n = name.toUpperCase();
				log.info("Getting Accounts from Datastore with name: " + n);
				list = (List<Account>) q.execute(n);

			} else {

				//execute without filter
				list = (List<Account>) q.execute();

			}

			ArrayList<Account> l = new ArrayList<Account>();
			for (Account a : list) {
				if (a.getHomeLocationId() != null) {
					l.add(a);
				}
			}
			return l;

		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			// roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}

			throw t;

		}

	}

	@Override
	public Class<GetAccountsAction> getActionType() {
		return GetAccountsAction.class;
	}

	@Override
	public void rollback(GetAccountsAction action, AccountsResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

}
