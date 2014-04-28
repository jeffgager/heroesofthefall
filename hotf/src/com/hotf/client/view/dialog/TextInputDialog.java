/**
 * 
 */
package com.hotf.client.view.dialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Jeff
 * 
 */
public class TextInputDialog extends DialogBox implements HasValueChangeHandlers<String> {

	private VerticalPanel pWidget = new VerticalPanel();
	private TextBox fText = new TextBox();
	private Button bCancel = new Button("Cancel");
	private Button bSave = new Button("Save");
	private String text;

	/**
	 * constructor
	 * 
	 */
	public TextInputDialog() {

		setText("Text Input");

		//add text field
		pWidget.add(new ScrollPanel(fText));

		//add button panel
		HorizontalPanel bp = new HorizontalPanel();
		bp.add(bCancel);
		bp.add(bSave);
		pWidget.add(bp);

		setWidget(pWidget);
		setupHandlers();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				fText.setFocus(true);
			}
		});
	}

	/*
	 * register button handlers 
	 */
	private void setupHandlers() {

		bCancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				text = null;
				hide();
			}
		});

		bSave.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				text = fText.getText();
				fireChange();
				hide();
			}
		});

	}

	private void fireChange() {
		ValueChangeEvent.fire(this, text);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
