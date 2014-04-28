package com.hotf.server.action;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.hotf.client.action.result.PostResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Post;

public class GetPostHandler {

	private static final Logger log = Logger.getLogger(GetPostHandler.class.getName());
	private static final String DICE_ROLE = "<b>[Roll";

	private GetAccountHandler getAccountHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetPlaceHandler getPlaceHandler;

	/**
	 * Get PostResult from a post.
	 * @param post
	 * @return PostResult
	 */
	public PostResult getResult(Account account, Post post) throws NotSignedInException {

		try {
			
			PostResult postResult = new PostResult();

			postResult.setCharacterId(post.getCharacterId());
			String characterName = getCharacterHandler.getCharacter(post.getCharacterId()).getName();
			String accountName = getAccountHandler.getAccount(post.getPlayerAccountId()).getName();
			if (accountName.toUpperCase().equals(characterName.toUpperCase())) {
				postResult.setCharacterName(characterName);
			} else {
				postResult.setCharacterName(characterName + " (" +  accountName + ")");
			}
			postResult.setCreated(post.getCreated());
			postResult.setCreatedOrder(post.getCreatedOrder());
			postResult.setId(post.getId());
			postResult.setLocationId(post.getLocationId());
			postResult.setLocationName(getPlaceHandler.getLocation(post.getLocationId()).getName());
			postResult.setTargetedCharacterId(post.getTargetedCharacterId());
			if (post.getTargetedCharacterId() == null) {
				postResult.setTargetName(null);
			} else {
				postResult.setTargetName(getCharacterHandler.getCharacter(post.getTargetedCharacterId()).getName());
			}
			postResult.setText(post.getText());
			postResult.setUpdated(post.getUpdated());
			postResult.setUpdatePermission(getUpdatePermission(account, post));
			postResult.setImportant(account != null && post.getImportantIds().contains(account.getPlayingCharacterId()));
			postResult.setMarkerX(post.getMarkerX());
			postResult.setMarkerY(post.getMarkerY());
			return postResult;

		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			throw t;

		}
		
	}

	/**
	 * @return true if the users has update permission, otherwise return false
	 */
	public Boolean getUpdatePermission(Account account, Post post) {
		
		String gmAccountId = post.getGameMasterAccountId(); 
		String playerAccountId = post.getPlayerAccountId();

		Boolean updatePermission = false;

		if (playerAccountId != null && account != null && playerAccountId.equals(account.getId()) && !post.getText().contains(DICE_ROLE)) {
			
			updatePermission = true;

		//or if the account that is playing the GM is the currently logged in account
		} else if (account != null && gmAccountId != null && gmAccountId.equals(account.getId())) {
			
			updatePermission = true;

		//update allowed if no GM character set 
		} else if (gmAccountId == null || playerAccountId == null) {
			
			updatePermission = true;

		//or if the account that is playing the character is the currently logged in account
		} else {
		
			updatePermission = false;
		
		}

		//return the update permission
		return updatePermission;

	}

	/**
	 * Get a post by id
	 * @param postId If of post
	 * @return Post
	 */
	public Post getPost(String postId) {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {
			
			log.info("Getting Post by id from the Datastore");
			Post post = pm.getObjectById(Post.class, postId);

			return post;

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
	 * @param getPlaceHandler the getPlaceHandler to set
	 */
	public void setGetPlaceHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

}
