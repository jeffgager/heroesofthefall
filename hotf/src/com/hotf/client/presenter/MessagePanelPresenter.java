package com.hotf.client.presenter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.hotf.client.ClientFactory;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.event.MessageEventHandler;
import com.hotf.client.view.component.MessagePanel;

/**
 * MessagePanel Presenter.
 * 
 * @author Jeff Gager
 */
public class MessagePanelPresenter implements MessagePanel.Presenter, MessageEventHandler {

	private static final int DEFAULT_DELAY = 8000;
	private static final String LOADING = "Loading";

	private ClientFactory clientFactory;
	private String pageMessage;

	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public MessagePanelPresenter(ClientFactory clientFactory) {

		this.clientFactory = clientFactory;

		clientFactory.getEventBus().addHandler(MessageEvent.TYPE, this);

	}

	@Override
	public void onMessage(MessageEvent event) {
		MessagePanel messagePanel = clientFactory.getMessagePanel();
		Level level = event.getLevel();
		String message = event.getMessage();
		if (level.equals(Level.PAGE)) {
			if (message == null) {
				message = pageMessage;
			} else {
				pageMessage = message;
			}
		} else if (level.equals(Level.LOADING)) {
			message = LOADING;
		} else {
			delayPageMessage();
		}
		messagePanel.getMessageField().setText(message);
		messagePanel.setLevel(level);
	}

	private void delayPageMessage() {
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
			@Override
			public boolean execute() {
				MessagePanel messagePanel = clientFactory.getMessagePanel();
				messagePanel.getMessageField().setText(pageMessage);
				messagePanel.setLevel(Level.PAGE);
				return false;
			}
		}, DEFAULT_DELAY);
	}

}