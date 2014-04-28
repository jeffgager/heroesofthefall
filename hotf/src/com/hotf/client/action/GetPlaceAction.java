package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlaceResult;

public class GetPlaceAction implements Action<PlaceResult> {

	private String id;
	
	public GetPlaceAction() {
		super();
	}
	
	public GetPlaceAction(String id) {
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
