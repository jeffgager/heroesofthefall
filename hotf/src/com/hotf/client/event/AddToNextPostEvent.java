package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class AddToNextPostEvent extends GwtEvent<AddToNextPostEventHandler> {

	public static Type<AddToNextPostEventHandler> TYPE = new Type<AddToNextPostEventHandler>();

	private String text;
	
	public AddToNextPostEvent(String text) {
		super();
		this.text = text;
		GWT.log("Firing AddToNextPostEvent");
	}

	@Override
	public Type<AddToNextPostEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddToNextPostEventHandler handler) {
		handler.onAdd(this);
	}

	public String getText() {
		return text;
	}

}
