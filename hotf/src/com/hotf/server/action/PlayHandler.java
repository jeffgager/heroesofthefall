package com.hotf.server.action;

import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.PlayAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharactersResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PlayResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PlacesResult;
import com.hotf.client.action.result.PostsResult;
import com.hotf.client.exception.ValidationException;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class PlayHandler implements ActionHandler<PlayAction, PlayResult> {

	private static final Logger log = Logger.getLogger(PlayHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetPlacesByGameHandler getPlacesByGameHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetCharactersPlayableHandler getCharactersPlayableHandler;
	private GetCharactersByPlaceHandler getCharactersByPlaceHandler;
	private GetPostsHandler getPostsHandler;
	private SaveAccountHandler saveAccountHandler;

	public PlayHandler() {
	}
	
	@Override
	public PlayResult execute(PlayAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle PlayAction");
		
		//cache Character
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.delete(getAccountHandler.findGAEUser().getUserId());
		Account account = getAccountHandler.getMyAccount();

		PlayResult login = new PlayResult();

		String characterId = action.getCharacterId();
		String locationId = action.getLocationId();
		
		Character playingCharacter = getCharacterHandler.getCharacter(characterId);

		String gmkey = playingCharacter.getInterested()[0];
		String playerkey = playingCharacter.getInterested()[1];
		if (!gmkey.equals(account.getId()) &&
			!playerkey.equals(account.getId())) {
			throw new ValidationException("You can't play this character");
		}
		
		if (locationId == null) {
			account.setPlayingCharacterId(characterId);
			saveAccountHandler.save(account);
			cache.put(getAccountHandler.findGAEUser().getUserId(), account);
		}

		AccountResult accountResult = getAccountHandler.getResult(account);
		login.setAccount(accountResult);

		Place place = getPlaceHandler.getLocation(locationId == null ? playingCharacter.getLocationId() : locationId);
		Game game = getGameHandler.getGame(place.getGameId());

		login.setPlayingCharacter(getCharacterHandler.getResult(account, game, place, playingCharacter));

		PlaceResult placeResult = getPlaceHandler.getResult(account, game, place);
		login.setLocation(placeResult);

		GameResult gameResult = getGameHandler.getResult(account, game);
		login.setGame(gameResult);

		CharactersResult interestedCharactersResult = new CharactersResult();
		interestedCharactersResult.getCharacters().addAll(getCharactersPlayableHandler.getCharactersResult(account, getCharactersPlayableHandler.getInterestedByName()));
		login.setInterested(interestedCharactersResult);

		PostsResult postsResult = null;
		if (locationId != null) {
			postsResult = getPostsHandler.getResultByPlace(false, locationId, null);
		} else if (place.getType() != null) {
			postsResult = getPostsHandler.getResultByPlace(false, place.getId(), null);
		} else {
			postsResult = getPostsHandler.getResult(false, null);
		}
		
		login.setPosts(postsResult);

		CharactersResult charactersResult = new CharactersResult();
		charactersResult.getCharacters().addAll(getCharactersPlayableHandler.getCharactersResult(account, getCharactersByPlaceHandler.getByLocation(account, game, place.getId())));
		login.setColocated(charactersResult);

		PlacesResult placesResult = new PlacesResult();
		for (Place l : getPlacesByGameHandler.getByGame(game.getId())) {
			PlaceResult lr = getPlaceHandler.getResult(account, game, l);
			placesResult.getPlaces().add(lr);
		}
		login.setLocations(placesResult);

		return login;

	}

	@Override
	public Class<PlayAction> getActionType() {
		return PlayAction.class;
	}

	@Override
	public void rollback(PlayAction action, PlayResult result, ExecutionContext context) throws DispatchException {
		
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

	/**
	 * @param getPlacesByGameHandler the getLocationsHandler to set
	 */
	public void setGetLocationsHandler(GetPlacesByGameHandler getPlacesByGameHandler) {
		this.getPlacesByGameHandler = getPlacesByGameHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

	/**
	 * @param getCharactersPlayableHandler the getCharactersHandler to set
	 */
	public void setGetCharactersHandler(GetCharactersPlayableHandler getCharactersPlayableHandler) {
		this.getCharactersPlayableHandler = getCharactersPlayableHandler;
	}

	/**
	 * @param getCharactersByPlaceHandler the getOtherCharactersHandler to set
	 */
	public void setGetOtherCharactersHandler(
			GetCharactersByPlaceHandler getCharactersByPlaceHandler) {
		this.getCharactersByPlaceHandler = getCharactersByPlaceHandler;
	}

	/**
	 * @param getPostsHandler the getPostsHandler to set
	 */
	public void setGetPostsHandler(GetPostsHandler getPostsHandler) {
		this.getPostsHandler = getPostsHandler;
	}

	/**
	 * @param saveAccountHandler the saveAccountHandler to set
	 */
	public void setSaveAccountHandler(SaveAccountHandler saveAccountHandler) {
		this.saveAccountHandler = saveAccountHandler;
	}

}