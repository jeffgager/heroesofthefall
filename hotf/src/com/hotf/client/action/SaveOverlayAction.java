package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.OverlayResult;

public class SaveOverlayAction implements Action<PlaceResult> {

	private OverlayResult overlayResult;

	public SaveOverlayAction() {
		super();
	}

	public SaveOverlayAction(OverlayResult overlayResult) {
		super();
		this.overlayResult = overlayResult;
	}

	/**
	 * @return the mapResult
	 */
	public OverlayResult getMapResult() {
		return overlayResult;
	}
	
}
