package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;

public class LoadCharactersEvent extends GwtEvent<LoadCharactersEventHandler> {

	public static Type<LoadCharactersEventHandler> TYPE = new Type<LoadCharactersEventHandler>();

	private List<CharacterResult> characters;

	public LoadCharactersEvent(List<CharacterResult> characters) {
		super();
		this.characters = characters;
		GWT.log("Firing LoadCharactersEvent");
	}

	@Override
	public Type<LoadCharactersEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadCharactersEventHandler handler) {
		handler.onLoadCharacters(this);
	}

	public List<CharacterResult> getCharacters() {
		return characters;
	}

}
