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

public class PlaceViewImpl extends Composite implements PlaceView {

	private static PlaceViewImplUiBinder uiBinder = GWT.create(PlaceViewImplUiBinder.class);

	interface PlaceViewImplUiBinder extends UiBinder<Widget, PlaceViewImpl> {
	}

	private Presenter presenter;
	
	@UiField(provided = true) final Resources resources;
	@UiField HTML nameField;
	@UiField HTML descriptionField;
	@UiField Image mapField;
	@UiField Anchor editLink;
	@UiField Anchor drawLink;
	@UiField Anchor uploadMapLink;
	@UiField Anchor clearMapLink;

	public PlaceViewImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();

		initWidget(uiBinder.createAndBindUi(this));

		map = new HasImageImpl(mapField);
		editHidden = new HasVisibilityImpl(editLink);
		drawHidden = new HasVisibilityImpl(drawLink);
		uploadHidden = new HasVisibilityImpl(uploadMapLink);
		clearMapHidden = new HasVisibilityImpl(clearMapLink);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasHTML getNameField() {
		return nameField;
	}

	private HasImage map;

	@Override
	public HasImage getMapField() {
		return map;
	}
	
	private HasVisibility editHidden;
	
	@Override
	public HasVisibility getEditHidden() {
		return editHidden;
	}

	private HasVisibility drawHidden;
	
	@Override
	public HasVisibility getDrawHidden() {
		return drawHidden;
	}

	private HasVisibility uploadHidden;
	
	@Override
	public HasVisibility getUploadHidden() {
		return uploadHidden;
	}

	private HasVisibility clearMapHidden;
	
	@Override
	public HasVisibility getClearMapHidden() {
		return clearMapHidden;
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
		
	@UiHandler ("uploadMapLink")
	public void uploadMapLink(ClickEvent e) {
		presenter.uploadMap();
	}
		
	@UiHandler ("drawLink")
	public void drawLink(ClickEvent e) {
		presenter.draw();
	}
		
	@UiHandler ("clearMapLink")
	public void clearMapLink(ClickEvent e) {
		presenter.clearMap();
	}

}
