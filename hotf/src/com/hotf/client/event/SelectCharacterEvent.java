package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterResult;

public class SelectCharacterEvent extends GwtEvent<SelectCharacterEventHandler> {

	public static Type<SelectCharacterEventHandler> TYPE = new Type<SelectCharacterEventHandler>();

	private boolean refresh;
	private CharacterResult character;
	
	public SelectCharacterEvent(CharacterResult character) {
		super();
		this.character = character;
		GWT.log("Firing SelectCharacterEvent");
	}

	@Override
	public Type<SelectCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectCharacterEventHandler handler) {
		handler.onSelectCharacter(this);
	}

	public boolean isRefresh() {
		return refresh;
	}

	public CharacterResult getCharacter() {
		return character;
	}

}
