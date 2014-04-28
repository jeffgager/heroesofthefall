package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class ChangeMarkerEvent extends GwtEvent<ChangeMarkerEventHandler> {

	public static Type<ChangeMarkerEventHandler> TYPE = new Type<ChangeMarkerEventHandler>();

	private int x;
	private int y;
	
	public ChangeMarkerEvent() {
		super();
		this.x = -50;
		this.y = -50;
		GWT.log("Firing ChangeMarkerEvent");
	}

	public ChangeMarkerEvent(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public Type<ChangeMarkerEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeMarkerEventHandler handler) {
		handler.onChange(this);
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
