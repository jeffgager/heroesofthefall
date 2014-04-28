package com.hotf.server.action;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.hotf.client.action.CreateStrongholdAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class CreateStrongholdHandler implements ActionHandler<CreateStrongholdAction, AccountResult> {

	private static final Logger log = Logger.getLogger(CreateStrongholdHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private SaveGameHandler saveGameHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetPlaceHandler getPlaceHandler;
	
	public CreateStrongholdHandler() {
	}
	
	@Override
	public AccountResult execute(CreateStrongholdAction action, ExecutionContext context) throws DispatchException {

		User user = getAccountHandler.findGAEUser();

		AccountResult accountResult = action.getAccount();

		//cache account
        try {
        	
    		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    		cache.delete(user.getUserId());

        } catch (Throwable t) {
        	
			log.severe(t.getMessage());

        }

		log.info("Handling CreateStrongholdAction");
		Account account = getAccountHandler.getMyAccount();

		createStronghold(account, accountResult.getName());

        try {
        	
    		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    		cache.delete(account.getPlayingCharacterId());
    		cache.delete(account.getStrongholdGameId());
    		cache.delete(user.getUserId());

        } catch (Throwable t) {
        	
			log.severe(t.getMessage());

        }
		accountResult.setPlayingCharacterId(account.getPlayingCharacterId());
		accountResult.setPersonalCharacterId(account.getPersonalCharacterId());
		accountResult.setHomeLocationId(account.getHomeLocationId());
		accountResult.setStrongholdGameId(account.getStrongholdGameId());

		return accountResult;

	}

	/**
	 * Create a stronghold for this account
	 */
	private void createStronghold(Account account, String name) throws NotSignedInException {

		if (name == null) {
			log.severe("No name");
			return;
		}
		
		//create a stronghold
		log.info("Creating stronghold game");
		Game stronghold = new Game(name + " Stronghold");
		saveGameHandler.save(stronghold, null, null, null);
		String gmcharid = stronghold.getGameMasterCharacterId();

		//set personal character name
		log.info("Setting stronghold gamemaster name");
		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		Character gmcharacter = getCharacterHandler.getCharacter(gmcharid);
		gmcharacter.setName(name);
		pm.currentTransaction().commit();

		//change start location to a private home locations
		log.info("Change Start Location to Home");
		
		pm.currentTransaction().begin();
		Place home = getPlaceHandler.getLocation(gmcharacter.getLocationId());
		String homeid = home.getId();
		home.setType(null);
		home.setName("Home");
		home.setGameMasterAccountId(account.getId());
		pm.currentTransaction().commit();
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.delete(gmcharacter.getLocationId());

		//set the stronghold GM character to the personal character and initial character being played
		log.info("Setting personal and playing character stronghold gamemaster");
		pm.currentTransaction().begin();
		String personalCharacterId = gmcharid;
		account.setPlayingCharacterId(personalCharacterId);
		account.setPersonalCharacterId(personalCharacterId);
		account.setStrongholdGameId(stronghold.getId());
		account.setHomeLocationId(homeid);
		account.setUpdated(new Date());
		pm.currentTransaction().commit();

	}

	@Override
	public Class<CreateStrongholdAction> getActionType() {
		return CreateStrongholdAction.class;
	}

	@Override
	public void rollback(CreateStrongholdAction action, AccountResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param saveGameHandler the saveGameHandler to set
	 */
	public void setSaveGameHandler(SaveGameHandler saveGameHandler) {
		this.saveGameHandler = saveGameHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

	/**
	 * @param getPlaceHandler
	 */
	public void setGetLocationHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}
	
}
