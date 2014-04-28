package com.hotf.server.action;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetPlacesByGameAction;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PlacesResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetPlacesByGameHandler implements ActionHandler<GetPlacesByGameAction, PlacesResult> {

	private static final Logger log = Logger.getLogger(GetPlacesByGameHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetGameHandler getGameHandler;

	public GetPlacesByGameHandler() {
	}

	@Override
	public PlacesResult execute(GetPlacesByGameAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetPlacesAction");

		Account account = getAccountHandler.getMyAccount();
		return getResult(account, action.getGameId());

	}

	public PlacesResult getResult(Account account, String gameId) throws NotSignedInException {
		PlacesResult placesResult = new PlacesResult();

		//ensure values are set and return them
		HashMap<String, Game> games = new HashMap<String, Game>();

		for (Place l : getByGame(gameId)) {
			String gameid = l.getGameId();
			Game game = null;
			if (games.containsKey(gameid)) {
				game = games.get(gameid);
			} else {
				game = getGameHandler.getGame(gameid);
				games.put(gameid, game);
			}
			PlaceResult placeResult = getPlaceHandler.getResult(account, game, l);
			placeResult.setLabel(l.getName() + " at " + game.getTitle());
			placesResult.getPlaces().add(placeResult);
		}
		return placesResult;
	}
	
	/**
	 * Find up to rows Locations in a Game with names beginning with string
	 * @param game Game
	 * @param string Name Start 
	 * @return List of Accounts
	 */
	@SuppressWarnings("unchecked")
	public List<Place> getByGame(String gameId) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Place.class);
			q.setOrdering("nameUpper asc");

			//execute without filter
			q.declareParameters("String p_gameId");
			q.setFilter("gameId == p_gameId");
			
			log.info("Getting places by Game from the Datastore");
			return (List<Place>) q.execute(gameId);
		
		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			//roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}

			throw t;

		}

	}

	@Override
	public Class<GetPlacesByGameAction> getActionType() {
		return GetPlacesByGameAction.class;
	}

	@Override
	public void rollback(GetPlacesByGameAction action, PlacesResult result, ExecutionContext context) throws DispatchException {
		
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

}