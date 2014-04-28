package com.hotf.client.view.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.view.HasVisibility;
import com.hotf.client.view.HasVisibilityImpl;

public class MessagePanelImpl extends Composite implements MessagePanel {

	private static MessagePanelImplUiBinder uiBinder = GWT.create(MessagePanelImplUiBinder.class);

	interface MessagePanelImplUiBinder extends UiBinder<Widget, MessagePanelImpl> {
	}

	@UiField(provided = true) final Resources resources;
	@UiField Label messageField;

	public MessagePanelImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();

		initWidget(uiBinder.createAndBindUi(this));
		
		messageHidden = new HasVisibilityImpl(messageField);

	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public void setLevel(Level level) {
		switch (level) {
			case PAGE : 
			case INFO : { 
				messageField.addStyleName(resources.style().info());
				messageField.removeStyleName(resources.style().error());
				messageField.removeStyleName(resources.style().advert());
				break;
			}
			case LOADING : 
			case ERROR : {
				messageField.removeStyleName(resources.style().info());
				messageField.addStyleName(resources.style().error());
				messageField.removeStyleName(resources.style().advert());
				break;
			}
		}
	}

	@Override
	public HasText getMessageField() {
		return messageField;
	}
	
	private HasVisibility messageHidden;

	@Override
	public HasVisibility getMessageHidden() {
		return messageHidden;
	}
	
}
