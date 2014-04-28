package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PlaceResult;

public class PlayEvent extends GwtEvent<PlayEventHandler> {

	public static Type<PlayEventHandler> TYPE = new Type<PlayEventHandler>();

	private CharacterResult character;
	private PlaceResult location;
	private GameResult game;
	
	public PlayEvent(GameResult game, PlaceResult location, CharacterResult character) {
		super();
		this.character = character;
		this.location = location;
		this.game = game;
		GWT.log("Firing PlayEvent");
	}

	@Override
	public Type<PlayEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PlayEventHandler handler) {
		handler.onPlay(this);
	}

	public CharacterResult getCharacter() {
		return character;
	}

	public PlaceResult getLocation() {
		return location;
	}

	public GameResult getGame() {
		return game;
	}

}
