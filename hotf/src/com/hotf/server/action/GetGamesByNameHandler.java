package com.hotf.server.action;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetGamesByNameAction;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GamesResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.GamePostCounter;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetGamesByNameHandler implements ActionHandler<GetGamesByNameAction, GamesResult> {

	private static final Logger log = Logger.getLogger(GetGamesByNameHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetPlaceHandler getPlaceHandler;

	public GetGamesByNameHandler() {
	}

	@Override
	public GamesResult execute(GetGamesByNameAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetGamesByNameAction");

		return getResult(action.getName());

	}

	public GamesResult getResult(String createdorder) throws NotSignedInException {

		Account account;
		try {
			account = getAccountHandler.getMyAccount();
		} catch (NotSignedInException e) {
			account = null;
		}
		GamesResult gamesResult = new GamesResult();

		for (Place l : getPlacesStart(account, createdorder)) {
			String gameid = l.getGameId();
			Game game = getGameHandler.getGame(gameid);
			GameResult result = getGameHandler.getResult(account, game);
			PlaceResult placeResult = getPlaceHandler.getResult(account, game, l);
			placeResult.setLabel(l.getName() + " at " + result.getTitle() + " (" + new GamePostCounter(game).getCount() + " posts)");
			result.setStart(placeResult);
			gamesResult.getGames().add(result);
		}
		return gamesResult;
	}
	
	/**
	 * @return List of Public Places
	 */
	@SuppressWarnings("unchecked")
	public List<Place> getPlacesStart(Account account, String createdorder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {

			
			//prepare query
			Query q = pm.newQuery(Place.class);
			q.setOrdering("createdOrder desc");
			q.setRange(0, account != null ? account.getFetchRows() : 20);

			List<Place> list;
			if (createdorder != null && createdorder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_createdOrder");
				q.setFilter("type == 'START' && createdOrder >= p_createdOrder");
				String n = createdorder.toUpperCase();
				log.info("Getting more public places from the Datastore");
				list = (List<Place>) q.execute(n);

			} else {

				//execute without filter
				q.setFilter("type == 'START'");
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
	public Class<GetGamesByNameAction> getActionType() {
		return GetGamesByNameAction.class;
	}

	@Override
	public void rollback(GetGamesByNameAction action, GamesResult result, ExecutionContext context) throws DispatchException {
		
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