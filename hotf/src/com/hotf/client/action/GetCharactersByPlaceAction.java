package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.CharactersResult;

public class GetCharactersByPlaceAction implements Action<CharactersResult> {

	private String locationId;
	
	public GetCharactersByPlaceAction() {
		super();
	}

	public GetCharactersByPlaceAction(String locationId) {
		super();
		this.locationId = locationId;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}
	
}
