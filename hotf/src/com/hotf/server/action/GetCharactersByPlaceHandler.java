package com.hotf.server.action;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetCharactersByPlaceAction;
import com.hotf.client.action.result.CharactersResult;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetCharactersByPlaceHandler implements ActionHandler<GetCharactersByPlaceAction, CharactersResult> {

	private static final Logger log = Logger.getLogger(GetCharactersByPlaceHandler.class.getName());
	private final String[] CHARACTER_TYPES = {"NPC", "Player"};

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetCharactersPlayableHandler getCharactersPlayableHandler;
	
	public GetCharactersByPlaceHandler() {
	}

	@Override
	public CharactersResult execute(GetCharactersByPlaceAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetOtherCharactersAction");

		CharactersResult charactersResult = new CharactersResult();
		Account account = getAccountHandler.getMyAccount();
		String locationId = action.getLocationId();
		Place place = getPlaceHandler.getLocation(locationId);
		Game game = getGameHandler.getGame(place.getGameId());
		charactersResult.getCharacters().addAll(getCharactersPlayableHandler.getCharactersResult(account, getByLocation(account, game, locationId)));
		return charactersResult;

	}

	/**
	 * Get Characters in Location.
	 * @param locationId to find Characters in
	 * @return List of Characters
	 */
	@SuppressWarnings("unchecked")
	public List<Character> getByLocation(Account account, Game game, String locationId) {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		
		try {
			
			Query q = pm.newQuery(Character.class);
			q.declareParameters("String p_locationId, String p_ctypes");
			q.setFilter("locationId == p_locationId && characterType == p_ctypes");
			q.setOrdering("nameUpper asc");
			
			log.info("Getting Characters by place from the Datastore");
			List<Character> list = (List<Character>) q.execute(locationId, Arrays.asList(CHARACTER_TYPES));

			return list;
		
		} catch (RuntimeException t) {
			
			log.severe(t.getMessage());
			throw t;

		}

	}

	@Override
	public Class<GetCharactersByPlaceAction> getActionType() {
		return GetCharactersByPlaceAction.class;
	}

	@Override
	public void rollback(GetCharactersByPlaceAction action, CharactersResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getCharactersPlayableHandler the getCharactersHandler to set
	 */
	public void setGetCharactersHandler(GetCharactersPlayableHandler getCharactersPlayableHandler) {
		this.getCharactersPlayableHandler = getCharactersPlayableHandler;
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
	 * @param getPlaceHandler the getLocationHandler to set
	 */
	public void setGetLocationHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

}
