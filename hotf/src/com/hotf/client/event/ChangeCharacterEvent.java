package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;

public class ChangeCharacterEvent extends GwtEvent<ChangeCharacterEventHandler> {

	public static Type<ChangeCharacterEventHandler> TYPE = new Type<ChangeCharacterEventHandler>();

	private CharacterResult character;
	
	public ChangeCharacterEvent(CharacterResult character) {
		super();
		this.character = character;
		GWT.log("Firing ChangeCharacterEvent");
	}

	@Override
	public Type<ChangeCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeCharacterEventHandler handler) {
		handler.onChange(this);
	}

	public CharacterResult getCharacter() {
		return character;
	}

}
