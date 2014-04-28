package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PostResult;

public class LoadPostsEvent extends GwtEvent<LoadPostsEventHandler> {

	public static Type<LoadPostsEventHandler> TYPE = new Type<LoadPostsEventHandler>();

	private boolean important;
	private boolean refresh;
	private List<PostResult> posts;
	
	public LoadPostsEvent(boolean important, boolean refresh, List<PostResult> posts) {
		super();
		this.important = important;
		this.refresh = refresh;
		this.posts = posts;
		GWT.log("Firing LoadPostsEvent");
	}

	@Override
	public Type<LoadPostsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadPostsEventHandler handler) {
		handler.onLoadPosts(this);
	}

	public boolean isRefresh() {
		return refresh;
	}

	public boolean isImportant() {
		return important;
	}
	
	public List<PostResult> getPosts() {
		return posts;
	}

}
