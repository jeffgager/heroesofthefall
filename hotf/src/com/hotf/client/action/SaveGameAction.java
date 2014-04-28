package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.GameResult;

public class SaveGameAction implements Action<GameResult> {

	private GameResult gameResult;

	public SaveGameAction() {
		super();
	}

	public SaveGameAction(GameResult gameResult) {
		super();
		this.gameResult = gameResult;
	}

	/**
	 * @return the gameResult
	 */
	public GameResult getGameResult() {
		return gameResult;
	}

}
