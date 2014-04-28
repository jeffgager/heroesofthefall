package com.hotf.server.action;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.GetPostsAction;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.action.result.PostsResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Post;

public class GetPostsHandler implements ActionHandler<GetPostsAction, PostsResult> {

	private static final Logger log = Logger.getLogger(GetPostsHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetPostHandler getPostHandler;

	/**
	 * Constructor.
	 */
	public GetPostsHandler() {
	}

	/**
	 * Get PostsResult from a GetPostsAction.
	 */
	@Override
	public PostsResult execute(GetPostsAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetPostsAction");

		if (action.getLocationId() != null) {
			return getResultByPlace(action.isImportant(), action.getLocationId(), action.getCreatedOrder());
		} else {
			return getResult(action.isImportant(), action.getCreatedOrder());
		}
		
	}

	/**
	 * Get PostsResult.
	 * @param characterId 
	 * @param important
	 * @param createdOrder
	 * @return PostsResult
	 */
	public PostsResult getResult(Boolean important, String createdOrder) throws NotSignedInException {
		
		Account account = getAccountHandler.getMyAccount();

		String characterId = account.getPlayingCharacterId();
		
		PostsResult postsResult = new PostsResult();

		List<Post> posts = null;
		if (important) {
			posts = getImportantByPresence(account, characterId, createdOrder);
		} else {
			posts = getByPresence(account, characterId, createdOrder);
		}

		for (Post post : posts) {
			PostResult postResult = getPostHandler.getResult(account, post);
			postsResult.getPosts().add(postResult);
		}

		return postsResult;

	}
	
	/**
	 * Get PostsResult.
	 * @param account 
	 * @param characterId 
	 * @param createdOrder
	 * @return PostsResult
	 */
	public PostsResult getResultByPlace(Boolean important, String locationId, String createdOrder) throws NotSignedInException {
		
		Account account;
		try {
			account = getAccountHandler.getMyAccount();
		} catch (NotSignedInException e) {
			account = null;
		}

		PostsResult postsResult = new PostsResult();

		List<Post> posts = null;
		if (account != null && important) {
			String characterId = account.getPlayingCharacterId();
			posts = getImportantByPlace(account, characterId, locationId, createdOrder);
		} else {
			posts = getByPlace(account, locationId, createdOrder);
		}

		for (Post post : posts) {
			PostResult postResult = getPostHandler.getResult(account, post);
			postsResult.getPosts().add(postResult);
		}

		return postsResult;

	}

	/**
	 * Get Posts a Character is present to have observed, optionally starting from a post.
	 * @param characterId Observing Character
	 * @param createdOrder Starting post Id
	 * @return List of Posts
	 */
	@SuppressWarnings("unchecked")
	public List<Post> getByPresence(Account account, String characterId, String createdOrder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Post.class);
			if (account != null) {
				q.setRange(0, account.getFetchRows());
			} else {
				q.setRange(0, 20);
			}
			q.setOrdering("createdOrder desc");
			List<Post> list;

			if (createdOrder != null && createdOrder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_characterId, String p_createdOrder");
				q.setFilter("presentIds == p_characterId && createdOrder < p_createdOrder");
				log.info("Getting posts by character and createdorder from Datastore");
				list = (List<Post>) q.execute(characterId, createdOrder);
			
			} else {
				
				//execute without filter
				q.declareParameters("String p_characterId");
				q.setFilter("presentIds == p_characterId");
				log.info("Getting posts by character from Datastore");
				list = (List<Post>) q.execute(characterId);

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

	/**
	 * Get ImportantPosts a Character is present to have observed, optionally starting from a post.
	 * @param characterId Observing Character
	 * @param createdOrder Starting post Id
	 * @return List of Posts
	 */
	@SuppressWarnings("unchecked")
	public List<Post> getImportantByPresence(Account account, String characterId, String createdOrder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Post.class);
			if (account != null) {
				q.setRange(0, account.getFetchRows());
			} 
			q.setOrdering("createdOrder desc");
			List<Post> list;

			if (createdOrder != null && createdOrder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_characterId, String p_createdOrder");
				q.setFilter("importantIds == p_characterId && presentIds == p_characterId && createdOrder < p_createdOrder");
				log.info("Getting important posts by character and createdorder from Datastore");
				list = (List<Post>) q.execute(characterId, createdOrder);
			
			} else {
				
				//execute without filter
				q.declareParameters("String p_characterId");
				q.setFilter("importantIds == p_characterId && presentIds == p_characterId");
				log.info("Getting important posts by character from Datastore");
				list = (List<Post>) q.execute(characterId);

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

	/**
	 * Get Posts a Character is present to have observed, optionally starting from a post.
	 * @param locationId Place
	 * @param createdOrder Starting post Id
	 * @return List of Posts
	 */
	@SuppressWarnings("unchecked")
	public List<Post> getImportantByPlace(Account account, String characterId, String locationId, String createdOrder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Post.class);
			q.setRange(0, account.getFetchRows());
			q.setOrdering("createdOrder desc");
			List<Post> list;

			if (createdOrder != null && createdOrder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_characterId, String p_locationId, String p_createdOrder");
				q.setFilter("importantIds == p_characterId && locationId == p_locationId && createdOrder < p_createdOrder");
				log.info("Getting posts by location and createdorder from Datastore");
				list = (List<Post>) q.execute(characterId, locationId, createdOrder);
			
			} else {
				
				//execute without filter
				q.declareParameters("String p_characterId, String p_locationId");
				q.setFilter("importantIds == p_characterId && locationId == p_locationId");
				log.info("Getting important posts by location from Datastore");
				list = (List<Post>) q.execute(characterId, locationId);

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

	/**
	 * Get Posts a Character is present to have observed, optionally starting from a post.
	 * @param locationId Place
	 * @param createdOrder Starting post Id
	 * @return List of Posts
	 */
	@SuppressWarnings("unchecked")
	public List<Post> getByPlace(Account account, String locationId, String createdOrder) {

		PersistenceManager pm = PMUtils.getPersistenceManager();
		try {
			
			//prepare query
			Query q = pm.newQuery(Post.class);
			q.setRange(0, account != null ? account.getFetchRows() : 20);
			q.setOrdering("createdOrder desc");
			List<Post> list;

			if (createdOrder != null && createdOrder.length() > 0) {

				//execute with filter
				q.declareParameters("String p_locationId, String p_createdOrder");
				q.setFilter("locationId == p_locationId && createdOrder < p_createdOrder");
				log.info("Getting posts by location and createdorder from Datastore");
				list = (List<Post>) q.execute(locationId, createdOrder);
			
			} else {
				
				//execute without filter
				q.declareParameters("String p_locationId");
				q.setFilter("locationId == p_locationId");
				log.info("Getting posts by location from Datastore");
				list = (List<Post>) q.execute(locationId);

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
	public Class<GetPostsAction> getActionType() {
		return GetPostsAction.class;
	}

	@Override
	public void rollback(GetPostsAction action, PostsResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getPostHandler the getPostHandler to set
	 */
	public void setGetPostHandler(GetPostHandler getPostHandler) {
		this.getPostHandler = getPostHandler;
	}

}