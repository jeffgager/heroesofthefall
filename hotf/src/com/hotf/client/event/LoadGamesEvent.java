package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.GameResult;

public class LoadGamesEvent extends GwtEvent<LoadGamesEventHandler> {

	public static Type<LoadGamesEventHandler> TYPE = new Type<LoadGamesEventHandler>();

	private boolean refresh;
	private List<GameResult> games;

	public LoadGamesEvent(boolean refresh, List<GameResult> games) {
		super();
		this.refresh = refresh;
		this.games = games;
		GWT.log("Firing LoadGamesEvent");
	}

	@Override
	public Type<LoadGamesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadGamesEventHandler handler) {
		handler.onLoadGames(this);
	}

	public List<GameResult> getGames() {
		return games;
	}

	public boolean isRefresh() {
		return refresh;
	}

}
