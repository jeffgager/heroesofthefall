package com.hotf.server.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetCharactersPlayableAction;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharactersResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetCharactersPlayableHandler implements ActionHandler<GetCharactersPlayableAction, CharactersResult> {

	private static final Logger log = Logger.getLogger(GetCharactersPlayableHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetGameHandler getGameHandler;
	
	public GetCharactersPlayableHandler() {
	}

	@Override
	public CharactersResult execute(GetCharactersPlayableAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetCharactersAction");

		CharactersResult charactersResult = new CharactersResult();
		Account account = getAccountHandler.getMyAccount();
		charactersResult.getCharacters().addAll(getCharactersResult(account, getInterestedByName()));
		return charactersResult;

	}

	public List<CharacterResult> getCharactersResult(Account userAccount, List<Character> characters) throws NotSignedInException {
		
		List<CharacterResult> characterResults = new ArrayList<CharacterResult>();

		HashMap<String, Place> places = new HashMap<String, Place>();
		HashMap<String, Game> games = new HashMap<String, Game>();
		HashMap<String, Account> accounts = new HashMap<String, Account>();

		for (Character character : characters) {

			if (character.getId().equals(userAccount.getPersonalCharacterId())) {
				continue;
			}

			String locid = character.getLocationId();
			Place place = null;
			if (places.containsKey(locid)) {
				place = places.get(locid);
			} else {
				place = getPlaceHandler.getLocation(locid);
				places.put(locid, place);
			}

			Game game = null;
			String gameid = place.getGameId();
			if (games.containsKey(gameid)) {
				game = games.get(gameid);
			} else {
				game = getGameHandler.getGame(gameid);
				games.put(gameid, game);
			}

			Account playerAccount = null;
			String playerAccountId = character.getPlayerAccountId();
			if (accounts.containsKey(playerAccountId)) {
				playerAccount = accounts.get(playerAccountId);
			} else {
				playerAccount = getAccountHandler.getAccount(playerAccountId);
				accounts.put(playerAccountId, playerAccount);
			}

			CharacterResult characterResult = getCharacterHandler.getResult(userAccount, playerAccount, game, place, character);
			characterResults.add(characterResult);
		}
		return characterResults;

	}

	/**
	 * Get characters user can play (has an interest in) by name. 
	 * @param name Name to look for
	 * @return List of Characters
	 */
	@SuppressWarnings("unchecked")
	public List<Character> getInterestedByName() throws NotSignedInException {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {
			
			Query q = pm.newQuery(Character.class);
			Account account = getAccountHandler.getMyAccount();
			q.setOrdering("nameUpper asc");
			List<Character> list;
			
			//execute without filter
			q.declareParameters("String p_accountId");
			q.setFilter("interested == p_accountId ");
			log.info("Getting playable characters from Datastore");
			list = (List<Character>) q.execute(account.getId());
			
			//ensure values are set and return them
			return list;
		
		} catch (RuntimeException t) {
			
			log.severe(t.getMessage());
			throw t;

		}

	}

	@Override
	public Class<GetCharactersPlayableAction> getActionType() {
		return GetCharactersPlayableAction.class;
	}

	@Override
	public void rollback(GetCharactersPlayableAction action, CharactersResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
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
