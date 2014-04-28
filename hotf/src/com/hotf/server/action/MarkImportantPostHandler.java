package com.hotf.server.action;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.hotf.client.action.MarkImportantPostAction;
import com.hotf.client.action.result.PostResult;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Post;

public class MarkImportantPostHandler implements ActionHandler<MarkImportantPostAction, PostResult> {

	private static final Logger log = Logger.getLogger(MarkImportantPostHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetPostHandler getPostHandler;

	public MarkImportantPostHandler() {
	}
	
	@Override
	public PostResult execute(MarkImportantPostAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling MarkImportantPost action");

		//current account
		Account account = getAccountHandler.getMyAccount();

		String postid = action.getPostID();
		String characterid = action.getCharacterId();
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		
		Post post = getPostHandler.getPost(postid);
		
		if (post.getImportantIds().contains(characterid)) {
			
			post.removeImportant(characterid);
			
		} else {

			post.addImportant(characterid);
			
		}
		pm.currentTransaction().commit();

		return getPostHandler.getResult(account, post);

	}

	@Override
	public Class<MarkImportantPostAction> getActionType() {
		return MarkImportantPostAction.class;
	}

	@Override
	public void rollback(MarkImportantPostAction action, PostResult result, ExecutionContext context) throws DispatchException {
		
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