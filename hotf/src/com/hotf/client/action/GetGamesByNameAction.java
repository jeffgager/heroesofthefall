package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.GamesResult;

public class GetGamesByNameAction implements Action<GamesResult> {

	private String name;
	
	public GetGamesByNameAction() {
		super();
	}

	public GetGamesByNameAction(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
