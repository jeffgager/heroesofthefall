package com.hotf.client.view.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

public class Chooser extends Composite implements HasSelectionHandlers<Suggestion>, HasText {

	interface MyUiBinder extends UiBinder<Widget, Chooser> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private String selectedOnFocus;
	private boolean clearOnSelect = false;
	private boolean changed = false;
	
	@UiField Label viewField;
	@UiField (provided=true) SuggestBox suggestBoxField;

	public Chooser(SuggestOracle suggestOracle) {

		super();

		suggestBoxField = new SuggestBox(suggestOracle);
		suggestBoxField.setAutoSelectEnabled(true);
		addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				String value = event.getSelectedItem().getReplacementString();
				if (value != null && !value.equals(selectedOnFocus)) {
					changed = true;
					Chooser.this.setText(value);
				}
				suggestBoxField.getValueBox().setFocus(false);
			}
		});
		
		suggestBoxField.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				selectedOnFocus = suggestBoxField.getText();
				changed = false;
				if (clearOnSelect) {
					suggestBoxField.setText("");
				}
				suggestBoxField.showSuggestionList();
			}
		});
		initWidget(uiBinder.createAndBindUi(this));

		suggestBoxField.getValueBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (clearOnSelect && !changed) {
					suggestBoxField.setText(selectedOnFocus);
				}
			}
		});

	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return suggestBoxField.addSelectionHandler(handler);
	}

	@Override
	public void setText(String text) {
		viewField.setText(text);
		suggestBoxField.setValue(text);
		findFieldSize();
	}
	
	@Override
	public String getText() {
		return suggestBoxField.getText();
	}

	private void findFieldSize() {
		viewField.setVisible(true);
		boolean v = isVisible();
		if (!v) {
			setVisible(true);
		}
		if (!v) {
			setVisible(false);
		}
		viewField.setVisible(false);
	}
	
	@Override
	protected void onLoad() {
		findFieldSize();

	}

	public ValueBoxBase<String> getTextBox() {
		return suggestBoxField.getValueBox();
	}
	
	/**
	 * @param clearOnSelect the clearOnSelect to set
	 */
	public void setClearOnSelect(boolean clearOnSelect) {
		this.clearOnSelect = clearOnSelect;
	}
	
}
