package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PlaceResult;

public class LoadTopicsEvent extends GwtEvent<LoadTopicsEventHandler> {

	public static Type<LoadTopicsEventHandler> TYPE = new Type<LoadTopicsEventHandler>();

	private List<PlaceResult> locations;
	private boolean refresh;

	public LoadTopicsEvent(boolean refresh, List<PlaceResult> locations) {
		super();
		this.locations = locations;
		this.refresh = refresh;
		GWT.log("Firing LoadTopicsEvent");
	}

	@Override
	public Type<LoadTopicsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadTopicsEventHandler handler) {
		handler.onLoadTopics(this);
	}

	public List<PlaceResult> getTopics() {
		return locations;
	}

	public boolean isRefresh() {
		return refresh;
	}

}
