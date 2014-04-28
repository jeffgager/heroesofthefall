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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.HasHtml;
import com.hotf.client.Resources;

public class CharacterViewImpl extends Composite implements CharacterView {

	private static CharacterViewImplUiBinder uiBinder = GWT.create(CharacterViewImplUiBinder.class);

	interface CharacterViewImplUiBinder extends UiBinder<Widget, CharacterViewImpl> {
	}

	private Presenter presenter;
	
	@UiField(provided = true) final Resources resources;
	@UiField HTML nameField;
	@UiField HTML statusField;
	@UiField HTML playerField;
	@UiField HTML descriptionField;
	@UiField Image portraitField;
	@UiField Anchor editLink;

	public CharacterViewImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();

		initWidget(uiBinder.createAndBindUi(this));

		portrait = new HasImageImpl(portraitField);
		portraitHidden = new HasVisibilityImpl(portraitField);
		editHidden = new HasVisibilityImpl(editLink);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasHTML getPlayerField() {
		return playerField;
	}

	@Override
	public HasHTML getNameField() {
		return nameField;
	}

	@Override
	public HasHTML getStatusField() {
		return statusField;
	}

	private HasImage portrait;

	@Override
	public HasImage getPortraitField() {
		return portrait;
	}
	
	private HasVisibility portraitHidden;
	
	@Override
	public HasVisibility getPortraitHidden() {
		return portraitHidden;
	}

	private HasVisibility editHidden;
	
	@Override
	public HasVisibility getEditHidden() {
		return editHidden;
	}

	@Override
	public HasHTML getDescriptionField() {
		return new HasHtml(descriptionField);
	}
	
	@UiHandler ("closeLink")
	public void closeLink(ClickEvent e) {
		presenter.close();
	}
		
	@UiHandler ("editLink")
	public void editLink(ClickEvent e) {
		presenter.edit();
	}
		
}
