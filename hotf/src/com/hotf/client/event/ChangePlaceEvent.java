package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.PlaceResult;

public class ChangePlaceEvent extends GwtEvent<ChangePlaceEventHandler> {

	public static Type<ChangePlaceEventHandler> TYPE = new Type<ChangePlaceEventHandler>();

	private PlaceResult place;
	
	public ChangePlaceEvent(PlaceResult place) {
		super();
		this.place = place;
		GWT.log("Firing ChangePlaceEvent");
	}

	@Override
	public Type<ChangePlaceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangePlaceEventHandler handler) {
		handler.onChange(this);
	}

	public PlaceResult getPlace() {
		return place;
	}
	
}
