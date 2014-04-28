package com.hotf.client.view.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;

public class TermsAndConditionsDialogImpl extends DialogBox implements TermsAndConditionsDialog {

	private static PostEditorImplUiBinder uiBinder = GWT.create(PostEditorImplUiBinder.class);

	interface PostEditorImplUiBinder extends UiBinder<Widget, TermsAndConditionsDialogImpl> {
	}

	@UiField(provided = true) final Resources resources;
	@UiField Button acceptButton;
	@UiField Button notacceptButton;
	@UiField Button closeButton;
	
	public TermsAndConditionsDialogImpl(ClientFactory clientFactory, boolean accept) {

		super();
		
		this.resources = clientFactory.getResources();

		setModal(true);
		setAutoHideEnabled(false);
		setText("Terms and Conditions - www.heroesofthefall.com");
		setSize("600px", "600px");

		setWidget(uiBinder.createAndBindUi(this));
		
		acceptButton.setVisible(accept);
		notacceptButton.setVisible(accept);
		closeButton.setVisible(!accept);
		
	}
	
	private Presenter presenter;
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Presenter getPresenter() {
		return presenter;
	}
	
	@UiHandler ("notacceptButton")
	public void notacceptLink(ClickEvent e) {
		presenter.notacceptTac();
	}

	@UiHandler ("acceptButton")
	public void acceptLink(ClickEvent e) {
		presenter.acceptTac();
	}

	@UiHandler ("closeButton")
	public void closeLink(ClickEvent e) {
		presenter.closeTac();
	}

}
