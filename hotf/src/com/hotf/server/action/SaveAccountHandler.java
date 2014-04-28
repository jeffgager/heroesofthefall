package com.hotf.server.action;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.ServiceException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.SaveAccountAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.client.exception.ValidationException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;

public class SaveAccountHandler implements ActionHandler<SaveAccountAction, AccountResult> {

	private static final Logger log = Logger.getLogger(SaveAccountHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	
	public SaveAccountHandler() {
	}
	
	@Override
	public AccountResult execute(SaveAccountAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SaveAccountAction");

		try {
			
			AccountResult accountResult = action.getAccount();

			//cache account
			MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
			cache.delete(getAccountHandler.findGAEUser().getUserId());
			
			Account account = getAccountHandler.getMyAccount();
			
			String oldname = account.getName();
			String newname = accountResult.getName();
			if (oldname == null) {
				if (findExistingAccountWithName(newname) > 0) {
					log.severe("Name exists");
					throw new ValidationException(Account.NAME_EXISTS_CHECK);
				}
				account.setName(newname);
			}
			account.setPlayingCharacterId(accountResult.getPlayingCharacterId());
			account.setSearchRows(accountResult.getSearchRows());
			account.setFetchRows(accountResult.getFetchRows());
			account.setShowPortraits(accountResult.getShowPortraits());

			save(account);
			
			cache.put(getAccountHandler.findGAEUser().getUserId(), account);

			return getAccountHandler.getResult(account);

		} catch (Throwable e) {
			
			throw new ServiceException(e.getMessage());

		}
		
	}

	/**
	 * Persist Account
	 */
	public void save(Account account) throws NotSignedInException {

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			Date today = new Date();

			if (account.getCreated() == null) {

				// set date created and gae user, then make persistent
				account.setCreated(today);
				pm.currentTransaction().begin();
				pm.makePersistent(account);
				pm.currentTransaction().commit();

			} else {

				pm.currentTransaction().begin();
				account.setUpdated(today);
				account.setTacAccepted(today);
				pm.currentTransaction().commit();

			}

			log.info("Persisted Account " + account.getName());

		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			// roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}

			throw t;

		}
		
	}

	/**
	 * Find existing Account with exact name. This is to called before an
	 * account name is set, to ensure that no account exists already with the
	 * same name.
	 * 
	 * @param string Name
	 * @return number found
	 */
	@SuppressWarnings("unchecked")
	public Integer findExistingAccountWithName(String name) throws NotSignedInException {

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			// prepare query
			Query q = pm.newQuery(Account.class);
			q.setRange(0, 1);
			List<Account> list;

			if (name != null && name.length() > 0) {

				// execute with filter
				q.declareParameters("String p_name, com.google.appengine.api.users.User p_user");
				q.setFilter("nameUpper == p_name && gaeUser != p_user");
				log.info("Getting Accounts by name and user");
				list = (List<Account>) q.execute(name.toUpperCase(),
						getAccountHandler.findGAEUser());

			} else {

				// execute without filter
				list = (List<Account>) q.execute();

			}

			// return number found
			return list.size();

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
	public Class<SaveAccountAction> getActionType() {
		return SaveAccountAction.class;
	}

	@Override
	public void rollback(SaveAccountAction action, AccountResult result, ExecutionContext context) throws DispatchException {
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

}