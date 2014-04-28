package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PostResult;

public class SavePostAction implements Action<PostResult> {

	private PostResult post;

	public SavePostAction() {
		super();
	}

	public SavePostAction(PostResult post) {
		super();
		this.post = post;
	}

	/**
	 * @return the post
	 */
	public PostResult getPost() {
		return post;
	}
	
}
