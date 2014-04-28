package com.hotf.server.action;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.SaveOverlayAction;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.OverlayResult;
import com.hotf.client.exception.AccessRightException;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class SaveOverlayHandler implements ActionHandler<SaveOverlayAction, PlaceResult> {

	private static final Logger log = Logger.getLogger(SaveOverlayHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetGameHandler getGameHandler;
	private SavePlaceHandler savePlaceHandler;
	
	public SaveOverlayHandler() {
	}
	
	@Override
	public PlaceResult execute(SaveOverlayAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SaveMapAction");

		OverlayResult overlayResult = action.getMapResult();

		String id = overlayResult.getId();

		//cache Location
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.delete(id);
		cache.delete("mapOverlay:" + id);

		Place place = getPlaceHandler.getLocation(id);

		Account account = getAccountHandler.getMyAccount();
		if (!getPlaceHandler.getUpdatePermission(account, place)) {
			throw new AccessRightException();
		}
		
		if (overlayResult.getOverlayUrl() != null) {
			place.setOverlayUrl(overlayResult.getOverlayUrl());
		}

		if (!getPlaceHandler.getUpdatePermission(account, place)) {
			throw new AccessRightException("You cannot change this Location");
		}

		savePlaceHandler.save(place);
		cache.put(place.getId(), place);

		Game game = getGameHandler.getGame(place.getGameId());
		return getPlaceHandler.getResult(account, game, place);

	}

	@Override
	public Class<SaveOverlayAction> getActionType() {
		return SaveOverlayAction.class;
	}

	@Override
	public void rollback(SaveOverlayAction action, PlaceResult result, ExecutionContext context) throws DispatchException {
		
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
	 * @param savePlaceHandler the saveLocationHandler to set
	 */
	public void setSaveLocationHandler(SavePlaceHandler savePlaceHandler) {
		this.savePlaceHandler = savePlaceHandler;
	}

}