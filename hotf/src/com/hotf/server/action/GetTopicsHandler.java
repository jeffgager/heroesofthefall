package com.hotf.server.action;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetTopicsByNameAction;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PlacesResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.LocationPostCounter;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetTopicsHandler implements ActionHandler<GetTopicsByNameAction, PlacesResult> {

	private static final Logger log = Logger.getLogger(GetTopicsHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetPlaceHandler getPlaceHandler;

	public GetTopicsHandler() {
	}

	@Override
	public PlacesResult execute(GetTopicsByNameAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetTopicsByNameAction");

		return getResult(action.getName());

	}

	public PlacesResult getResult(String createdorder) throws NotSignedInException {

		Account account;
		try {
			account = getAccountHandler.getMyAccount();
		} catch (NotSignedInException e) {
			account = null;
		}
		PlacesResult placesResult = new PlacesResult();

		//ensure values are set and return them
		HashMap<String, Game> games = new HashMap<String, Game>();

		for (Place l : getPlacesPublic(account, createdorder)) {
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
			placeResult.setPostCount(new LocationPostCounter(l).getCount());

			placesResult.getPlaces().add(placeResult);
		}
		return placesResult;
	}
	
	/**
	 * @return List of Public Places
	 */
	@SuppressWarnings("unchecked")
	public List<Place> getPlacesPublic(Account account, String createdorder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Place.class);
			q.setOrdering("type desc, createdOrder desc");
			q.setRange(0, account != null ? account.getFetchRows(): 20);
			q.declareImports("import com.hotf.server.model.Location.Type");

			List<Place> list;
			if (createdorder != null && createdorder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_createdOrder");
				q.setFilter("type != null && type != 'START' && createdOrder >= p_createdOrder");
				String n = createdorder.toUpperCase();
				log.info("Getting more public places from the Datastore");
				list = (List<Place>) q.execute(n);

			} else {

				//execute without filter
				q.setFilter("type != null && type != 'START'");
				log.info("Getting public places from the Datastore");
				list = (List<Place>) q.execute();

			}

			return list;
		
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
	public Class<GetTopicsByNameAction> getActionType() {
		return GetTopicsByNameAction.class;
	}

	@Override
	public void rollback(GetTopicsByNameAction action, PlacesResult result, ExecutionContext context) throws DispatchException {
		
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