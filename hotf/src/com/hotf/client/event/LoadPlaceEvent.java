package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PlaceResult;

public class LoadPlaceEvent extends GwtEvent<LoadPlaceEventHandler> {

	public static Type<LoadPlaceEventHandler> TYPE = new Type<LoadPlaceEventHandler>();

	private PlaceResult location;

	public LoadPlaceEvent(PlaceResult location) {
		super();
		this.location = location;
		GWT.log("Firing LoadPlaceEvent");
	}

	@Override
	public Type<LoadPlaceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadPlaceEventHandler handler) {
		handler.onLoadPlace(this);
	}

	/**
	 * @return the location
	 */
	public PlaceResult getPlace() {
		return location;
	}

}
