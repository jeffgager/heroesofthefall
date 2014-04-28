package com.hotf.server.action;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.SavePlaceAction;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.exception.AccessRightException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class SavePlaceHandler implements ActionHandler<SavePlaceAction, PlaceResult> {

	private static final Logger log = Logger.getLogger(SavePlaceAction.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetGameHandler getGameHandler;
	private GetCharactersPlayableHandler getCharactersPlayableHandler;
	private GetPlacesByGameHandler getPlacesByGameHandler;

	public SavePlaceHandler() {
	}

	@Override
	public PlaceResult execute(SavePlaceAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SaveLocationAction");

		PlaceResult placeResult = action.getLocationResult();
		String id = placeResult.getId();
		Place place = null;
		Account account = getAccountHandler.getMyAccount();

		//cache Location
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.delete(id);
		cache.delete("mapOverlay:" + id);

		if (id == null) {
			place = new Place(placeResult.getGameId(), placeResult.getName());
			place.setGameMasterAccountId(account.getId());
			place.setType(placeResult.getType());
		} else {
			place = getPlaceHandler.getLocation(id);
		}

		if (!getPlaceHandler.getUpdatePermission(account, place)) {
			throw new AccessRightException();
		}
		
		String desciption = placeResult.getDescription();
		place.setDescription(desciption == null ? "" : desciption);
		place.setName(placeResult.getName());

		if (placeResult.getHasMap() != null && !placeResult.getHasMap() && place.getMap() != null) {
			BlobstoreServiceFactory.getBlobstoreService().delete(place.getMap());
			place.setMap(null);
		}
		
		if (placeResult.getHasOverlay() != null && !placeResult.getHasOverlay() && place.getOverlay() != null) {
			place.setOverlay(null);
		}

		if (!getPlaceHandler.getUpdatePermission(account, place)) {
			throw new AccessRightException("You cannot change this Location");
		}

		save(place);

		cache.put(place.getId(), place);

		Game game = getGameHandler.getGame(place.getGameId());
		placeResult = getPlaceHandler.getResult(account, game, place);
		placeResult.setCharacters(getCharactersPlayableHandler.getCharactersResult(account, getCharactersPlayableHandler.getInterestedByName()));
		for (Place l : getPlacesByGameHandler.getByGame(game.getId())) {
			placeResult.getLocations().add(getPlaceHandler.getResult(account, game, l));
		}

		return placeResult;

	}

	/**
	 * Save changes.
	 */
	public void save(Place place) {

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			Date today = new Date();

			if (place.getId() == null) {
				
				//update date created and date updated
				place.setCreated(today);
				place.setUpdated(today);
				
				//persist
				pm.currentTransaction().begin();
				pm.makePersistent(place);
				pm.currentTransaction().commit();

				//now we have an id - set createdOrder
				pm.currentTransaction().begin();
				place.setCreatedOrder(Long.toString(today.getTime()) + '#' + place.getId());
				pm.currentTransaction().commit();

				log.info("Created Location " + place.getName());

			} else {
				
				//update date updated
				pm.currentTransaction().begin();
				place.setUpdated(today);
				pm.currentTransaction().commit();

				log.info("Updated Location " + place.getName());

			}

		} catch (RuntimeException t) {
			log.severe(t.getMessage());
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;
		}

	}

	@Override
	public Class<SavePlaceAction> getActionType() {
		return SavePlaceAction.class;
	}

	@Override
	public void rollback(SavePlaceAction action, PlaceResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getPlaceHandler the getLocationHandler to set
	 */
	public void setGetLocationHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

	/**
	 * @param getGameHandler the getGameHandler to set
	 */
	public void setGetGameHandler(GetGameHandler getGameHandler) {
		this.getGameHandler = getGameHandler;
	}

	/**
	 * @param getCharactersPlayableHandler
	 */
	public void setGetCharactersHandler(GetCharactersPlayableHandler getCharactersPlayableHandler) {
		this.getCharactersPlayableHandler = getCharactersPlayableHandler;
	}

	/**
	 * @param getPlacesByGameHandler
	 */
	public void setGetLocationsHandler(GetPlacesByGameHandler getPlacesByGameHandler) {
		this.getPlacesByGameHandler = getPlacesByGameHandler;
	}
	
}