package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlacesResult;

public class GetPlacesByGameAction implements Action<PlacesResult> {

	private String gameId;
	
	public GetPlacesByGameAction() {
		super();
	}

	/**
	 * @param gameId
	 */
	public GetPlacesByGameAction(String gameId) {
		super();
		this.gameId = gameId;
	}

	/**
	 * @return the gameId
	 */
	public String getGameId() {
		return gameId;
	}

}
