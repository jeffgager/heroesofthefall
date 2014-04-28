package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class UploadUrlEvent extends GwtEvent<UploadUrlEventHandler> {

	public static Type<UploadUrlEventHandler> TYPE = new Type<UploadUrlEventHandler>();

	private String url;

	public UploadUrlEvent(String url) {
		super();
		this.url = url;
		GWT.log("Firing UploadUrlEvent");
	}

	@Override
	public Type<UploadUrlEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UploadUrlEventHandler handler) {
		handler.onUrl(this);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

}
