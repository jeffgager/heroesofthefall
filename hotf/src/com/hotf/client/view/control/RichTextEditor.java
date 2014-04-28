package com.hotf.client.view.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Justification;
import com.hotf.client.ClientFactory;
import com.hotf.client.StringConstants;
import com.hotf.client.view.HasImage;
import com.hotf.client.view.HasImageImpl;
import com.hotf.client.view.HasVisibility;
import com.hotf.client.view.HasVisibilityImpl;
import com.hotf.client.view.dialog.TextInputDialog;

public class RichTextEditor extends Composite implements HasHTML, Focusable {

	interface MyUiBinder extends UiBinder<Widget, RichTextEditor> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField(provided = true) StringConstants constants;
	@UiField ToggleButton boldButton;
	@UiField ToggleButton italicButton;
	@UiField ToggleButton underlineButton;
	@UiField ToggleButton subscriptButton;
	@UiField ToggleButton superscriptButton;
	@UiField ToggleButton strikethroughButton;
	@UiField ListBox backColors;
	@UiField ListBox foreColors;
	@UiField ListBox fonts;
	@UiField ListBox fontSizes;
	@UiField RichTextArea textEditField;
	@UiField HTMLPanel mainPanel;
	@UiField Image portraitField;
	
	public RichTextEditor(ClientFactory clientFactory) {

		super();

		constants = clientFactory.getStringConstants();

		initWidget(uiBinder.createAndBindUi(this));
		
		portraitHidden = new HasVisibilityImpl(portraitField);
		portraitImage = new HasImageImpl(portraitField);

	}

	@UiHandler("boldButton")
	public void boldButton(ClickEvent e) {
		textEditField.getFormatter().toggleBold();
		updateStatus();
	}

	@UiHandler("italicButton")
	public void italicButton(ClickEvent e) {
		textEditField.getFormatter().toggleItalic();
		updateStatus();
	}

	@UiHandler("underlineButton")
	public void underlineButton(ClickEvent e) {
		textEditField.getFormatter().toggleUnderline();
		updateStatus();
	}

	@UiHandler("subscriptButton")
	public void subscriptButton(ClickEvent e) {
		textEditField.getFormatter().toggleSubscript();
		updateStatus();
	}

	@UiHandler("superscriptButton")
	public void superscriptButton(ClickEvent e) {
		textEditField.getFormatter().toggleSuperscript();
		updateStatus();
	}

	@UiHandler("justifyLeftButton")
	public void justifyLeftButton(ClickEvent e) {
		textEditField.getFormatter().setJustification(Justification.LEFT);
		updateStatus();
	}

	@UiHandler("justifyCenterButton")
	public void justifyCenterButton(ClickEvent e) {
		textEditField.getFormatter().setJustification(Justification.CENTER);
		updateStatus();
	}

	@UiHandler("justifyRightButton")
	public void justifyRightButton(ClickEvent e) {
		textEditField.getFormatter().setJustification(Justification.RIGHT);
		updateStatus();
	}

	@UiHandler("strikethroughButton")
	public void strikeThroughButton(ClickEvent e) {
		textEditField.getFormatter().toggleStrikethrough();
		updateStatus();
	}

	@UiHandler("indentButton")
	public void indentButton(ClickEvent e) {
		textEditField.getFormatter().rightIndent();
		updateStatus();
	}

	@UiHandler("outdentButton")
	public void outdentButton(ClickEvent e) {
		textEditField.getFormatter().leftIndent();
		updateStatus();
	}

	@UiHandler("hrButton")
	public void hrButton(ClickEvent e) {
		textEditField.getFormatter().insertHorizontalRule();
		updateStatus();
	}

	@UiHandler("olButton")
	public void olButton(ClickEvent e) {
		textEditField.getFormatter().insertOrderedList();
		updateStatus();
	}

	@UiHandler("ulButton")
	public void ulButton(ClickEvent e) {
		textEditField.getFormatter().insertUnorderedList();
		updateStatus();
	}

	@UiHandler("removeFormatButton")
	public void removeFormatButton(ClickEvent e) {
		textEditField.getFormatter().removeFormat();
		updateStatus();
	}

	@UiHandler("insertImageButton")
	public void drawImage(ClickEvent e) {
		final TextInputDialog d = GWT.create(TextInputDialog.class);
		d.setText("Enter image URL");
		d.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String url = event.getValue();
				if (url != null && url.length() > 0) {
					textEditField.getFormatter().insertImage(url);
					updateStatus();
				}
			}
		});
		d.center();
	}

	@UiHandler("textEditField")
	public void textEditField(ClickEvent e) {
		updateStatus();
	}
	
	@UiHandler("textEditField")
	public void textEditField(KeyUpEvent e) {
		updateStatus();
	}

	@UiHandler("backColors")
	public void backColors(ChangeEvent e) {
        textEditField.getFormatter().setBackColor(backColors.getValue(backColors.getSelectedIndex()));
        backColors.setSelectedIndex(0);
	}

	@UiHandler("foreColors")
	public void foreColors(ChangeEvent e) {
		textEditField.getFormatter().setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
        foreColors.setSelectedIndex(0);
	}

	@UiHandler("fonts")
	public void fonts(ChangeEvent e) {
		textEditField.getFormatter().setFontName(fonts.getValue(fonts.getSelectedIndex()));
        fonts.setSelectedIndex(0);
	}

	private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] {
		RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL,
		RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM,
		RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE,
		RichTextArea.FontSize.XX_LARGE};

	 @UiHandler("fontSizes")
	public void fontSize(ChangeEvent e) {
		textEditField.getFormatter().setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
        fontSizes.setSelectedIndex(0);
	}

	/**
	 * Updates the status of all the stateful buttons.
	 */
	private void updateStatus() {
		boldButton.setDown(textEditField.getFormatter().isBold());
		italicButton.setDown(textEditField.getFormatter().isItalic());
		underlineButton.setDown(textEditField.getFormatter().isUnderlined());
		subscriptButton.setDown(textEditField.getFormatter().isSubscript());
		superscriptButton.setDown(textEditField.getFormatter().isSuperscript());
		strikethroughButton.setDown(textEditField.getFormatter().isStrikethrough());
	}

	public void resetButtons() {
        boldButton.setDown(false);
        italicButton.setDown(false);
        underlineButton.setDown(false);
        subscriptButton.setDown(false);
        superscriptButton.setDown(false);
        strikethroughButton.setDown(false);
	}

	@Override
	public String getHTML() {
		return textEditField.getHTML();
	}

	@Override
	public void setHTML(String html) {
		textEditField.setHTML(html);
	}

	@Override
	public String getText() {
		return textEditField.getText();
	}

	@Override
	public void setText(String text) {
		textEditField.setHTML(text);
	}

	@Override
	public void setTabIndex(int index) {
		textEditField.setTabIndex(index);
	}

	@Override
	public int getTabIndex() {
		return textEditField.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		textEditField.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		textEditField.setFocus(focused);
	}

	public void setWidth(String width) {
		textEditField.setWidth(width);
	}
	
	public void setHeight(String height) {
		textEditField.setHeight(height);
	}
	
	private HasImage portraitImage;

	public HasImage getPortraitImage() {
		return portraitImage;
	}
	
	private HasVisibility portraitHidden;
	
	public HasVisibility getPortraitHidden() {
		return portraitHidden;
	}

}
