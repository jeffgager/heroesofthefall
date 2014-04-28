package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PlaceResult;

public class SelectPlaceEvent extends GwtEvent<SelectPlaceEventHandler> {

	public static Type<SelectPlaceEventHandler> TYPE = new Type<SelectPlaceEventHandler>();

	private boolean refresh;
	private PlaceResult location;
	
	public SelectPlaceEvent(PlaceResult Location) {
		super();
		this.location = Location;
		GWT.log("Firing SelectLocationEvent");
	}

	@Override
	public Type<SelectPlaceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectPlaceEventHandler handler) {
		handler.onSelectPlace(this);
	}

	public boolean isRefresh() {
		return refresh;
	}

	public PlaceResult getPlace() {
		return location;
	}

}
