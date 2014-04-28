package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PostResult;

public class MarkImportantPostAction implements Action<PostResult> {

	private String postId;
	private String characterId;

	public MarkImportantPostAction() {
		super();
	}

	public MarkImportantPostAction(String postId, String characterId) {
		super();
		this.postId = postId;
		this.characterId = characterId;
	}

	/**
	 * @return the post
	 */
	public String getPostID() {
		return postId;
	}
	
	/**
	 * @return the characterId
	 */
	public String getCharacterId() {
		return characterId;
	}

}
