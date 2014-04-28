package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameArtifactResult extends GameImplementResult implements Result {

	private Integer effect;

	public GameArtifactResult() {
		super();
	}

	public Integer getEffect() {
		return effect;
	}

	public void setEffect(Integer effect) {
		this.effect = effect;
	}

}
