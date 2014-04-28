package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageEventHandler> {

	private static String LOADING_MESSAGE = "Loading";
	public static Type<MessageEventHandler> TYPE = new Type<MessageEventHandler>();
	public static enum Level {PAGE, INFO, LOADING, ERROR}

	private Level level;
	private String message;
	
	public MessageEvent() {
		level = Level.PAGE;
		message = null;
		GWT.log("Firing MessageEvent");
	}
	
	public MessageEvent(Level level) {
		this(level, null);
	}

	public MessageEvent(Level level, String message) {
		super();
		this.level = level;
		if (level.equals(Level.LOADING)) {
			this.message = LOADING_MESSAGE;
		} else {
			this.message = message;
		}
	}

	@Override
	public Type<MessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MessageEventHandler handler) {
		handler.onMessage(this);
	}

	public Level getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

}
