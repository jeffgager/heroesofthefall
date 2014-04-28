package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlaceResult;

public class SavePlaceAction implements Action<PlaceResult> {

	private PlaceResult placeResult;

	public SavePlaceAction() {
		super();
	}

	public SavePlaceAction(PlaceResult placeResult) {
		super();
		this.placeResult = placeResult;
	}

	/**
	 * @return the locationResult
	 */
	public PlaceResult getLocationResult() {
		return placeResult;
	}
	
}
