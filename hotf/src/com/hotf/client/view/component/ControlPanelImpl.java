package com.hotf.client.view.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.view.HasVisibility;
import com.hotf.client.view.control.CharacterOracle;
import com.hotf.client.view.control.Chooser;
import com.hotf.client.view.control.LocationOracle;

public class ControlPanelImpl extends Composite implements ControlPanel {

	private static CommandPanelImplUiBinder uiBinder = GWT.create(CommandPanelImplUiBinder.class);

	interface CommandPanelImplUiBinder extends UiBinder<Widget, ControlPanelImpl> {
	}

	@UiField(provided = true) final Resources resources;
	@UiField (provided=true) Chooser selectLocationChooser;
	@UiField (provided=true) Chooser selectCharacterChooser;
	@UiField HTML mapNameField;
	@UiField Anchor hideCharacter;
	@UiField Anchor copyCharacter;
	@UiField Anchor moveCharacterMap;
	@UiField Anchor createCharacter;
	@UiField Anchor openMap;
	@UiField Anchor createMap;

	public ControlPanelImpl(ClientFactory clientFactory) {

		super();

		this.resources = clientFactory.getResources();

		LocationOracle locationOracle = new LocationOracle(clientFactory);
		selectLocationChooser = new Chooser(locationOracle);
		
		CharacterOracle characterOracle = new CharacterOracle(clientFactory);
		selectCharacterChooser = new Chooser(characterOracle);

		initWidget(uiBinder.createAndBindUi(this));

		gmCommandsHidden = new GMCommandVisibility();

	}

	private Presenter presenter;
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public HasSelectionHandlers<Suggestion> getCharacterSelection() {
		return selectCharacterChooser;
	}
	
	@Override
	public HasText getCharacterSelectionField() {
		return selectCharacterChooser;
	}
	
	@Override
	public HasSelectionHandlers<Suggestion> getLocationSelection() {
		return selectLocationChooser;
	}
	
	@Override
	public HasText getLocationSelectionField() {
		return selectLocationChooser;
	}
	
	private HasVisibility gmCommandsHidden;
	
	@Override
	public HasVisibility getGMCommandsHidden() {
		return gmCommandsHidden;
	}

	@Override
	public HasSafeHtml getMapNameField() {
		return mapNameField;
	}
	
	@UiHandler ("openCharacter")
	public void openCharacter(ClickEvent e) {
		presenter.openCharacter();
	}

	@UiHandler ("openGame")
	public void openGame(ClickEvent e) {
		presenter.openGame();
	}

	@UiHandler ("refresh")
	public void refresh(ClickEvent e) {
		presenter.refresh();
	}

	@UiHandler ("important")
	public void important(ClickEvent e) {
		presenter.important();
	}

	@UiHandler ("createMap")
	public void createMap(ClickEvent e) {
		presenter.createMap();
	}

	@UiHandler ("openMap")
	public void openMap(ClickEvent e) {
		presenter.openMap();
	}

	@UiHandler ("createCharacter")
	public void createCharacter(ClickEvent e) {
		presenter.createCharacter();
	}

	@UiHandler ("copyCharacter")
	public void copyCharacter(ClickEvent e) {
		presenter.copyCharacter();
	}

	@UiHandler ("moveCharacterMap")
	public void moveCharacterMap(ClickEvent e) {
		presenter.moveCharacter();
	}

	@UiHandler ("playCharacter")
	public void playCharacter(ClickEvent e) {
		presenter.playCharacter();
	}

	@UiHandler ("hideCharacter")
	public void hideCharacter(ClickEvent e) {
		presenter.hideCharacter();
	}
	
	private class GMCommandVisibility implements HasVisibility {

		private boolean visible;

		@Override
		public boolean isVisible() {
			return visible;
		}

		@Override
		public void setVisible(boolean visible) {
			this.visible = visible;
			selectLocationChooser.setVisible(visible);
			mapNameField.setVisible(!visible);
			hideCharacter.setVisible(visible);
			copyCharacter.setVisible(visible);
			moveCharacterMap.setVisible(visible);
			createCharacter.setVisible(visible);
			createMap.setVisible(visible);
			openMap.setVisible(visible);
		}
		
	}

}
