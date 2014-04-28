package com.hotf.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.view.control.RichTextEditor;

public class PlaceEditImpl extends Composite implements PlaceEdit {

	private static LocationViewImplUiBinder uiBinder = GWT.create(LocationViewImplUiBinder.class);

	interface LocationViewImplUiBinder extends UiBinder<Widget, PlaceEditImpl> {
	}

	private Presenter presenter;

	@UiField(provided = true) final Resources resources;
	@UiField TextBox nameField;
	@UiField TextBox typeField;
	@UiField(provided = true) RichTextEditor descriptionField;

	public PlaceEditImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();
		this.descriptionField = clientFactory.createRichTextEditor();

		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasText getNameField() {
		return nameField;
	}

	@Override
	public HasText getTypeField() {
		return typeField;
	}

	@Override
	public Focusable getNameFocus() {
		return nameField;
	}
	
	@Override
	public HasHTML getDescriptionField() {
		return descriptionField;
	}

	@UiHandler ("closeLink")
	public void close(ClickEvent e) {
		presenter.close();
	}
		
	@UiHandler ("saveLink")
	public void save(ClickEvent e) {
		presenter.save();
	}
	
}
