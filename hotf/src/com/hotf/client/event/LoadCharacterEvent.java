package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameResult;

public class LoadCharacterEvent extends GwtEvent<LoadCharacterEventHandler> {

	public static Type<LoadCharacterEventHandler> TYPE = new Type<LoadCharacterEventHandler>();

	private CharacterResult character;
	private GameResult game;

	public LoadCharacterEvent(GameResult game, CharacterResult character) {
		super();
		this.game = game;
		this.character = character;
		GWT.log("Firing LoadCharacterEvent");
	}

	@Override
	public Type<LoadCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadCharacterEventHandler handler) {
		handler.onLoadCharacter(this);
	}

	/**
	 * @return the Character
	 */
	public CharacterResult getCharacter() {
		return character;
	}

	public GameResult getGame() {
		return game;
	}

}
