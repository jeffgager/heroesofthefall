package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.CharacterResult;

public class GetCharacterAction implements Action<CharacterResult> {

	private String id;
	
	public GetCharacterAction() {
		super();
	}
	
	public GetCharacterAction(String id) {
		this();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
}
