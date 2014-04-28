package com.hotf.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.HasHtml;
import com.hotf.client.Resources;

public class GameViewImpl extends Composite implements GameView {

	private static GameViewImplUiBinder uiBinder = GWT.create(GameViewImplUiBinder.class);

	interface GameViewImplUiBinder extends UiBinder<Widget, GameViewImpl> {
	}

	private Presenter presenter;

	@UiField(provided = true) final Resources resources;
	@UiField HTML titleField;
	@UiField HTML descriptionField;
	@UiField Anchor editLink;

	public GameViewImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();

		initWidget(uiBinder.createAndBindUi(this));

		editHidden = new HasVisibilityImpl(editLink);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	private HasVisibility editHidden;
	
	@Override
	public HasVisibility getEditHidden() {
		return editHidden;
	}

	@Override
	public HasHTML getTitleValue() {
		return titleField;
	}

	@Override
	public HasHTML getDescriptionValue() {
		return new HasHtml(descriptionField);
	}

	@UiHandler ("closeLink")
	public void close(ClickEvent e) {
		presenter.close();
	}
		
	@UiHandler ("editLink")
	public void edit(ClickEvent e) {
		presenter.edit();
	}

}
