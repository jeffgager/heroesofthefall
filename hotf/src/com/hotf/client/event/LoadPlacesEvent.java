package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PlaceResult;

public class LoadPlacesEvent extends GwtEvent<LoadPlacesEventHandler> {

	public static Type<LoadPlacesEventHandler> TYPE = new Type<LoadPlacesEventHandler>();

	private List<PlaceResult> locations;

	public LoadPlacesEvent(List<PlaceResult> locations) {
		super();
		this.locations = locations;
		GWT.log("Firing LoadPlacesEvent");
	}

	@Override
	public Type<LoadPlacesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadPlacesEventHandler handler) {
		handler.onLoadPlaces(this);
	}

	public List<PlaceResult> getPlaces() {
		return locations;
	}

}
