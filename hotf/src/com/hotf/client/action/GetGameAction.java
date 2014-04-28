package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.GameResult;

public class GetGameAction implements Action<GameResult> {

	private String id;
	
	public GetGameAction() {
		super();
	}
	
	public GetGameAction(String id) {
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
