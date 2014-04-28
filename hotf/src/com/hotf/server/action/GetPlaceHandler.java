package com.hotf.server.action;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.GetPlaceAction;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.LocationPostCounter;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetPlaceHandler implements ActionHandler<GetPlaceAction, PlaceResult> {

	private static final Logger log = Logger.getLogger(GetPlaceHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetGameHandler getGameHandler;
	
	public GetPlaceHandler() {
	}
	
	@Override
	public PlaceResult execute(GetPlaceAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetLocationAction");

		Account account = getAccountHandler.getMyAccount();
		Place place = getLocation(action.getId());
		Game game = getGameHandler.getGame(place.getGameId());
		return getResult(account, game, place);

	}

	public PlaceResult getResult(Account account, Game game, Place place) throws NotSignedInException {
		PlaceResult placeResult = new PlaceResult();
		placeResult.setDescription(place.getDescription());
		placeResult.setGameId(place.getGameId());
		placeResult.setId(place.getId());
		placeResult.setName(place.getName());
		placeResult.setUpdatePermission(getUpdatePermission(account, place));
		placeResult.setCreated(place.getCreated());
		placeResult.setCreatedOrder(place.getCreatedOrder());
		placeResult.setUpdated(place.getUpdated());
		placeResult.setLabel(place.getName() + " at " + game.getTitle());
		placeResult.setType(place.getType() == null ? null : place.getType());
		placeResult.setOwner(getAccountHandler.getAccount(
				getCharacterHandler.getCharacter(game.getGameMasterCharacterId()).getPlayerAccountId()).getName());
		placeResult.setPostCount(new LocationPostCounter(place).getCount());
		placeResult.setHasOverlay(place.getOverlay() != null);
		placeResult.setHasMap(place.getMap() != null);

		return placeResult;
	}
	
	/**
	 * @param Place Id to find
	 * @return Location found or null if no Location found with Id
	 */
	public Place getLocation(String locationId) {
		
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		if (cache.contains(locationId)) {
			log.info("Getting Location from Memcache");
			try {
				return (Place)cache.get(locationId);
			} catch (InvalidValueException e) {
				log.warning("Clearing cache");
				cache.clearAll();
			}
		}

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			log.info("Getting place from Datastore by id");
			Place place = pm.getObjectById(Place.class, locationId);
			cache.put(locationId, place);
			return place;

		} catch (RuntimeException t) {
			
			log.severe(t.getMessage());

			//roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;

		}

	}
	
	/**
	 * @return true if the users has update permission, otherwise return false
	 */
	public Boolean getUpdatePermission(Account account, Place place) {

		boolean updatePermission = false;

		String gmAccountId = place.getGameMasterAccountId();

		if (account != null && gmAccountId.equals(account.getId())) {
			
			updatePermission = true;

		}
			
		//return the update permission
		return updatePermission;

	}
	
	@Override
	public Class<GetPlaceAction> getActionType() {
		return GetPlaceAction.class;
	}

	@Override
	public void rollback(GetPlaceAction action, PlaceResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getGameHandler the getGameHandler to set
	 */
	public void setGetGameHandler(GetGameHandler getGameHandler) {
		this.getGameHandler = getGameHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

}