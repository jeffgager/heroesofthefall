package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;

public class LoadOtherCharactersEvent extends GwtEvent<LoadOtherCharactersEventHandler> {

	public static Type<LoadOtherCharactersEventHandler> TYPE = new Type<LoadOtherCharactersEventHandler>();

	private List<CharacterResult> characters;

	public LoadOtherCharactersEvent(List<CharacterResult> characters) {
		super();
		this.characters = characters;
		GWT.log("Firing LoadOtherCharactersEvent");
	}

	@Override
	public Type<LoadOtherCharactersEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadOtherCharactersEventHandler handler) {
		handler.onLoadOtherCharacters(this);
	}

	public List<CharacterResult> getCharacters() {
		return characters;
	}

}
