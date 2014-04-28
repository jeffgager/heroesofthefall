package com.hotf.client.view.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.view.HasVisibility;
import com.hotf.client.view.control.RichTextEditor;

public class PostEditorDialogImpl extends DialogBox implements PostEditorDialog {

	private static PostEditorImplUiBinder uiBinder = GWT.create(PostEditorImplUiBinder.class);

	interface PostEditorImplUiBinder extends UiBinder<Widget, PostEditorDialogImpl> {
	}

	@UiField(provided = true) Resources resources;
	@UiField(provided = true) RichTextEditor postEditField;
	@UiField Label postAttackResultField;
	
	public PostEditorDialogImpl(ClientFactory clientFactory) {

		super();
		
		this.resources = clientFactory.getResources();
		this.postEditField = clientFactory.createRichTextEditor();
		
		setWidget(uiBinder.createAndBindUi(this));

	}

	private Presenter presenter;
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Presenter getPresenter() {
		return presenter;
	}
	
	@Override
	public HasHTML getPostEditField() {
		return postEditField;
	}

	@Override
	public HasText getPostAttackResultField() {
		return postAttackResultField;
	}

	private HasVisibility resultVisible = new HasVisibility() {
		@Override
		public void setVisible(boolean visible) {
			postAttackResultField.setVisible(visible);
		}
		@Override
		public boolean isVisible() {
			return postAttackResultField.isVisible();
		}
	};
	@Override
	public HasVisibility getPostAttackResultEnabled() {
		return resultVisible;
	}
	
	@UiHandler ("saveButton")
	public void saveLink(ClickEvent e) {
		presenter.save();
	}

	@UiHandler ("cancelButton")
	public void cancelLink(ClickEvent e) {
		presenter.cancel();
	}

}
