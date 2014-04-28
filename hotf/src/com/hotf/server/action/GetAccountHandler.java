package com.hotf.server.action;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.hotf.client.action.GetAccountAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;

public class GetAccountHandler implements ActionHandler<GetAccountAction, AccountResult>{

	private static final Logger log = Logger.getLogger(GetAccountHandler.class.getName());
	private SaveAccountHandler saveAccountHandler;
	
	public GetAccountHandler() {
	}

	@Override
	public AccountResult execute(GetAccountAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetAccountAction");

		Account a = getMyAccount();

		if (a != null) {
			log.info("Found account for " + a.getName());
		}
		
		return getResult(a);

	}

	public AccountResult getResult(Account account) {
		AccountResult accountResult = new AccountResult();
		accountResult.setId(account.getId());
		accountResult.setName(account.getName());
		accountResult.setFetchRows(account.getFetchRows());
		accountResult.setSearchRows(account.getSearchRows());
		accountResult.setShowPortraits(account.getShowPortraits());
		accountResult.setPlayingCharacterId(account.getPlayingCharacterId());
		accountResult.setPersonalCharacterId(account.getPersonalCharacterId());
		accountResult.setStrongholdGameId(account.getStrongholdGameId());
		accountResult.setHomeLocationId(account.getHomeLocationId());
		accountResult.setTacAccepted(account.getTacAccepted());
		accountResult.setAdministrator(isAdministrator());
		return accountResult;
	}

	public boolean isAdministrator() {
		return UserServiceFactory.getUserService().isUserAdmin();
	}

	/**
	 * Find Account for current GAE user, creating one if necessary.
	 * 
	 * @return Account found or created
	 */
	public Account getMyAccount() throws NotSignedInException {

		// get current GAE user
		User user = findGAEUser();
		
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		if (cache.contains(user.getUserId())) {
			log.info("Getting logged in Account from Memcache");
			try {
				return (Account)cache.get(user.getUserId());
			} catch (InvalidValueException e) {
				log.warning("Clearing cache");
				cache.clearAll();
			}
		}

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			Account account = pm.getObjectById(Account.class, user.getUserId());
			cache.put(user.getUserId(), account);
			return account;

		} catch (JDOObjectNotFoundException e) {
			
			log.info("Creating new Account");
			Account account = new Account(user);
			saveAccountHandler.save(account);
			cache.put(user.getUserId(), account);
			return account;

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
	public Class<GetAccountAction> getActionType() {
		return GetAccountAction.class;
	}

	/**
	 * Get Account.
	 * @param Account Id to find
	 * @return Account found or null if no Account found with Id
	 */
	public Account getAccount(String id) throws NotSignedInException {

		findGAEUser();

		if (id == null) {
			return null;
		}

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			return pm.getObjectById(Account.class, id);

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
	 * @return GAE User
	 * @throws NotSignedInException
	 *             if user in not signed in.
	 */
	public User findGAEUser() throws NotSignedInException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			throw new NotSignedInException();
		}

		return user;

	}

	@Override
	public void rollback(GetAccountAction action, AccountResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param saveAccountHandler the saveAccountHandler to set
	 */
	public void setSaveAccountHandler(SaveAccountHandler saveAccountHandler) {
		this.saveAccountHandler = saveAccountHandler;
	}

}
