package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.GameResult;

public class LoadGameEvent extends GwtEvent<LoadGameEventHandler> {

	public static Type<LoadGameEventHandler> TYPE = new Type<LoadGameEventHandler>();

	private GameResult game;

	public LoadGameEvent(GameResult Game) {
		super();
		this.game = Game;
		GWT.log("Firing LoadGameEvent");
	}

	@Override
	public Type<LoadGameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadGameEventHandler handler) {
		handler.onLoadGame(this);
	}

	/**
	 * @return the Game
	 */
	public GameResult getGame() {
		return game;
	}

}
