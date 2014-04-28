package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlayResult;

public class PlayAction implements Action<PlayResult> {

	private String characterId;
	private String locationId;
	
	public PlayAction() {
		super();
	}

	/**
	 * @param characterId
	 */
	public PlayAction(String characterId) {
		super();
		this.characterId = characterId;
		this.locationId = null;
	}

	/**
	 * @param characterId
	 * @param locationId
	 */
	public PlayAction(String characterId, String locationId) {
		super();
		this.characterId = characterId;
		this.locationId = locationId;
	}

	/**
	 * @return the characterId
	 */
	public String getCharacterId() {
		return characterId;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}

}
