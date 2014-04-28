package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PostResult;

public class ChangePostEvent extends GwtEvent<ChangePostEventHandler> {

	public static Type<ChangePostEventHandler> TYPE = new Type<ChangePostEventHandler>();

	private PostResult post;
	
	public ChangePostEvent(PostResult post) {
		super();
		this.post = post;
		GWT.log("Firing ChangePostEvent");
	}

	@Override
	public Type<ChangePostEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangePostEventHandler handler) {
		handler.onChange(this);
	}

	public PostResult getPost() {
		return post;
	}

}
