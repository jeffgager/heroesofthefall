package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.GameResult;

public class ChangeGameEvent extends GwtEvent<ChangeGameEventHandler> {

	public static Type<ChangeGameEventHandler> TYPE = new Type<ChangeGameEventHandler>();

	private GameResult game;
	
	public ChangeGameEvent(GameResult game) {
		super();
		this.game = game;
		GWT.log("Firing ChangeGameEvent");
	}

	@Override
	public Type<ChangeGameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeGameEventHandler handler) {
		handler.onChange(this);
	}

	public GameResult getGame() {
		return game;
	}
	
}
