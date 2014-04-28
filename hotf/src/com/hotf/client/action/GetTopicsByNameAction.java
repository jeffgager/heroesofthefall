package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlacesResult;

public class GetTopicsByNameAction implements Action<PlacesResult> {

	private String name;
	
	public GetTopicsByNameAction() {
		super();
	}

	public GetTopicsByNameAction(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
