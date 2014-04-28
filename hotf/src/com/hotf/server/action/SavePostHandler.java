package com.hotf.server.action;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.SavePostAction;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.exception.AccessRightException;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.EmailService;
import com.hotf.server.GamePostCounter;
import com.hotf.server.LocationPostCounter;
import com.hotf.server.PMUtils;
import com.hotf.server.dice.RollProcessor;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;
import com.hotf.server.model.Post;

public class SavePostHandler implements ActionHandler<SavePostAction, PostResult> {

	private static final Logger log = Logger.getLogger(SavePostHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetCharactersByPlaceHandler getCharactersByPlaceHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetPostHandler getPostHandler;
	private GetPostsHandler getPostsHandler;

	public SavePostHandler() {
	}
	
	@Override
	public PostResult execute(SavePostAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SavePostAction");

		//get PostResult to save
		PostResult postResult = action.getPost();
		String id = postResult.getId();
		Post post = null;

		//create new Post, or fetch existing Post
		if (id == null) {
			post = new Post(postResult.getCharacterId(), postResult.getText());
			post.setTargetedCharacterId(postResult.getTargetedCharacterId());
		} else {
			post = getPostHandler.getPost(id);
		}

		//check permissions
		Account account = getAccountHandler.getMyAccount();
		if (!getPostHandler.getUpdatePermission(account, post)) {
			throw new AccessRightException();
		}

		//update post with changes
		if (postResult.getImportant()) {
			post.addImportant(postResult.getCharacterId());
		}
		post.setText(postResult.getText());
		String text = postResult.getText();
		post.setText(text == null ? "" : text);
		post.setMarkerX(postResult.getMarkerX());
		post.setMarkerY(postResult.getMarkerY());
		post.setLocationId(postResult.getLocationId());
		
		if (!getPostHandler.getUpdatePermission(account, post)) {
			throw new AccessRightException("You cannot change this Post");
		}
		
		return save(post);

	}

	public PostResult save(Post post) throws NotSignedInException {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		
		try {

			Account account = getAccountHandler.getMyAccount();
			Place postingLocation = pm.getObjectById(Place.class, post.getLocationId());
			Character postingCharacter = getCharacterHandler.getCharacter(post.getCharacterId());

			Date today = new Date();
			CharacterResult changedCharacter = null;
			if (post.getId() == null) {
				
				post.setGameMasterAccountId(postingCharacter.getGameMasterAccountId());
				post.setPlayerAccountId(postingCharacter.getPlayerAccountId());

				//set location
				if (post.getLocationId() == null) {
					post.setLocationId(postingCharacter.getLocationId());
				}

				//set created date
				post.setCreated(today);
			
				//poster is always present but never notified
				post.getPresentIds().add(postingCharacter.getId());

				//GM is always present and notified but as long as the they are not posting
				Game game = pm.getObjectById(Game.class, postingLocation.getGameId());
				post.getPresentIds().add(game.getGameMasterCharacterId());
				Account gameMastersAccount = getAccountHandler.getAccount(getCharacterHandler.getCharacter(game.getGameMasterCharacterId()).getPlayerAccountId());
				if (!postingCharacter.getPlayerAccountId().equals(gameMastersAccount.getId())) {
					EmailService.get().sendPostNotification(gameMastersAccount, game, postingLocation, postingCharacter, post);
				}

				//others present and notified only if the poster is visible
				//TODO notifications of posts in public places
//				if (postingLocation.getType() != null) {
//					List<Character> characters = getCharactersByWatchedPlaceHandler.getByWatchPlace(getAccountHandler.getMyAccount(), postingLocation.getId());
//					for (Character c : characters) {
//						post.getPresentIds().add(c.getId());
//						if (c.getPlayerAccountId().equals(postingCharacter.getPlayerAccountId()) || c.getPlayerAccountId().equals(gameMastersAccount.getId())) {
//							continue;
//						}
//						EmailService.get().sendPostNotification(getAccountHandler.getAccount(c.getPlayerAccountId()), game, postingLocation, postingCharacter, post);
//					}
//				} else if (post.getTargetedCharacterId() != null) {
				if (post.getTargetedCharacterId() != null) {

					Character targeted = getCharacterHandler.getCharacter(post.getTargetedCharacterId());

					//add targeted character to presentIds
					post.getPresentIds().add(targeted.getId());
					if (!targeted.getPlayerAccountId().equals(postingCharacter.getPlayerAccountId()) && !targeted.getPlayerAccountId().equals(gameMastersAccount.getId())) {
						EmailService.get().sendPostNotification(getAccountHandler.getAccount(targeted.getPlayerAccountId()), game, postingLocation, postingCharacter, post);
					}
				
				} else if (!postingCharacter.getTokenHidden()) {
					List<Character> characters = getCharactersByPlaceHandler.getByLocation(getAccountHandler.getMyAccount(), game, postingLocation.getId());
					for (Character c : characters) {
						post.getPresentIds().add(c.getId());
						if (c.getPlayerAccountId().equals(postingCharacter.getPlayerAccountId()) || c.getPlayerAccountId().equals(gameMastersAccount.getId())) {
							continue;
						}
						EmailService.get().sendPostNotification(getAccountHandler.getAccount(c.getPlayerAccountId()), game, postingLocation, postingCharacter, post);
					}
				}

				//process rolls in text
				post.setText(RollProcessor.get().process(post.getText()));
				post.setCreatedOrder(Long.toString(today.getTime()) + '#');

				//persist
				pm.currentTransaction().begin();
				pm.makePersistent(post);
				pm.currentTransaction().commit();

				//Increment Post counts
				new LocationPostCounter(postingLocation).increment();
				new GamePostCounter(game).increment();
				
				//now we have an id - set createdOrder
				pm.currentTransaction().begin();
				post.setCreatedOrder(Long.toString(today.getTime()) + '#' + post.getId());
				pm.currentTransaction().commit();

			} else {

				//set date updated and commit
				pm.currentTransaction().begin();
				post.setUpdated(today);
				pm.currentTransaction().commit();

			}

			List<PostResult> postResults = null;
			if (postingLocation.getType() != null) {
				postResults = getPostsHandler.getResultByPlace(false, postingLocation.getId(), null).getPosts();
			} else {
				postResults = getPostsHandler.getResult(false, null).getPosts();
			}

			// HRDS fix for newly created posts that might not be in the query
			boolean updated = false;
			for (int i = 0; i < postResults.size(); i++) {
				PostResult p = postResults.get(i);
				if (p.getId().equals(post.getId())) {
					updated = true;
				}
			}
			if (!updated) {
				int endidx = postResults.size() - 1;
				if (endidx >= 0) {
					postResults.remove(endidx);
				}
				postResults.add(0, getPostHandler.getResult(account, post));
			}

			log.info(postingCharacter.getName() + " Posted");

			//re-initialise PostResult and return
			PostResult postResult = getPostHandler.getResult(account, post);
			postResult.setChangedCharacter(changedCharacter);
			postResult.setPosts(postResults);

			return postResult;

		} catch (RuntimeException t) {
			log.severe(t.getMessage());
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;
		}

	}

	@Override
	public Class<SavePostAction> getActionType() {
		return SavePostAction.class;
	}

	@Override
	public void rollback(SavePostAction action, PostResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getCharactersByPlaceHandler the getOtherCharactersHandler to set
	 */
	public void setGetOtherCharactersHandler(
			GetCharactersByPlaceHandler getCharactersByPlaceHandler) {
		this.getCharactersByPlaceHandler = getCharactersByPlaceHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

	/**
	 * @param getPostHandler the getPostHandler to set
	 */
	public void setGetPostHandler(GetPostHandler getPostHandler) {
		this.getPostHandler = getPostHandler;
	}

	/**
	 * @param getPostsHandler the getPostsHandler to set
	 */
	public void setGetPostsHandler(GetPostsHandler getPostsHandler) {
		this.getPostsHandler = getPostsHandler;
	}

}