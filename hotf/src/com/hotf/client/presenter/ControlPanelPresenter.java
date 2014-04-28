package com.hotf.client.presenter;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.event.ChangeCharacterEvent;
import com.hotf.client.event.ChangeCharacterEventHandler;
import com.hotf.client.event.ChangeGameEvent;
import com.hotf.client.event.ChangeGameEventHandler;
import com.hotf.client.event.ChangeMarkerEvent;
import com.hotf.client.event.ChangeMarkerEventHandler;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePlaceEventHandler;
import com.hotf.client.event.LoadPlaceEvent;
import com.hotf.client.event.LoadPlaceEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.event.PlayEvent;
import com.hotf.client.event.PlayEventHandler;
import com.hotf.client.event.SelectCharacterEvent;
import com.hotf.client.event.SelectCharacterEventHandler;
import com.hotf.client.event.SelectPlaceEvent;
import com.hotf.client.event.SelectPlaceEventHandler;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace.CreateOrEdit;
import com.hotf.client.presenter.CharacterViewActivity.CharacterViewPlace;
import com.hotf.client.presenter.GameViewActivity.GameViewPlace;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace;
import com.hotf.client.presenter.PlaceViewActivity.PlaceViewPlace;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.component.ControlPanel;
import com.hotf.client.view.control.CharacterOracle.CharacterSuggestion;
import com.hotf.client.view.control.LocationOracle.LocationSuggestion;

/**
 * ControlPanel Presenter.
 * 
 * @author Jeff Gager
 */
public class ControlPanelPresenter implements ControlPanel.Presenter,
	PlayEventHandler, ChangeCharacterEventHandler, ChangePlaceEventHandler, ChangeGameEventHandler,  
	SelectPlaceEventHandler, SelectCharacterEventHandler, LoadPlaceEventHandler, ChangeMarkerEventHandler {

	private ClientFactory clientFactory;
	private ControlPanel controlPanel;
	
	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public ControlPanelPresenter(final ControlPanel controlPanel, final ClientFactory clientFactory) {

		this.controlPanel = controlPanel;
		this.clientFactory = clientFactory;

		clientFactory.getEventBus().addHandler(PlayEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeCharacterEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangePlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeGameEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(SelectPlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(SelectCharacterEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadPlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeMarkerEvent.TYPE, this);

		controlPanel.getLocationSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof LocationSuggestion) {
					LocationSuggestion locationSuggestion = (LocationSuggestion)suggestion;
					playingCharacter.setLocationId(locationSuggestion.getLocation().getId());
					clientFactory.getGameController().saveCharacter(playingCharacter);
					clientFactory.getEventBus().fireEvent(new SelectPlaceEvent(locationSuggestion.getLocation()));
				}
			}
		});
		controlPanel.getCharacterSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof CharacterSuggestion) {
					CharacterSuggestion characterSuggestion = (CharacterSuggestion)suggestion;
					clientFactory.getEventBus().fireEvent(new SelectCharacterEvent(characterSuggestion.getCharacter()));
					String newlocid = characterSuggestion.getCharacter().getLocationId();
					if (!selectedCharacter.getLocationId().equals(newlocid)) {
						clientFactory.getGameController().loadLocation(newlocid);
					}
				}
			}
		});
		
	}

	private CharacterResult playingCharacter;
	private PlaceResult playingLocation;
	private GameResult game;

	@Override
	public void onPlay(PlayEvent event) {
		
		playingCharacter = event.getCharacter();
		selectedCharacter = event.getCharacter();
		playingLocation = event.getLocation();
		selectedLocation = event.getLocation();
		game = event.getGame();

		controlPanel.getLocationSelectionField().setText(playingLocation.getName());
		controlPanel.getCharacterSelectionField().setText(playingCharacter.getName());
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.appendHtmlConstant("<p>");
		sb.appendEscaped(playingLocation.getName());
		sb.appendHtmlConstant("</p>");
		controlPanel.getMapNameField().setHTML(sb.toSafeHtml());
		controlPanel.getGMCommandsHidden().setVisible(game.getUpdatePermission() && "GM".equals(selectedCharacter.getCharacterType()));

	}

	@Override
	public void onChange(ChangePlaceEvent event) {

		//update playing location (if that's the character that changed
		PlaceResult location = event.getPlace();
		if (playingLocation != null && playingLocation.getId() != null && playingLocation.getId().equals(location.getId())) {
			playingLocation = location;
		}
		if (selectedLocation != null && selectedLocation.getId() != null && selectedLocation.getId().equals(location.getId())) {
			selectedLocation = location;
			controlPanel.getLocationSelectionField().setText(location.getName());
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant("<p>");
			sb.appendEscaped(playingLocation.getName());
			sb.appendHtmlConstant("</p>");
			controlPanel.getMapNameField().setHTML(sb.toSafeHtml());
		}
		
	}
	
	@Override
	public void onChange(ChangeCharacterEvent event) {
		
		//update playing character (if that's the character that changed
		CharacterResult character = event.getCharacter();
		if (playingCharacter != null && playingCharacter.getId() != null && playingCharacter.getId().equals(character.getId())) {
			playingCharacter = event.getCharacter();
			controlPanel.getCharacterSelectionField().setText(event.getCharacter().getName());
		}

		//update selected character (if that's the character that changed
		if (selectedCharacter != null && selectedCharacter.getId() != null && selectedCharacter.getId().equals(character.getId())) {
			controlPanel.getCharacterSelectionField().setText(character.getName());
			if (moving != null && moving) {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Moved - " + character.getLabel()));
				moving = null;
			} 
			if (hiding != null) {
				String hc = hiding ? "Hidden - " : "Revealed - ";
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, hc + character.getLabel()));
				hiding = null;
			}
			selectedCharacter = character;
		}

	}
	
	@Override
	public void onChange(ChangeGameEvent event) {
		
		GameResult gameresult = event.getGame();
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.appendHtmlConstant("<h1>");
		sb.appendEscaped(gameresult.getTitle());
		sb.appendHtmlConstant("</h1>");
		controlPanel.getGMCommandsHidden().setVisible(gameresult.getUpdatePermission() && "GM".equals(selectedCharacter.getCharacterType()));

	}
	
	private CharacterResult selectedCharacter;

	@Override
	public void onSelectCharacter(SelectCharacterEvent event) {

		if (selectedCharacter == null || selectedCharacter.getId().equals(event.getCharacter().getId())) {
			return;
		}

		selectedCharacter = event.getCharacter();
		controlPanel.getCharacterSelectionField().setText(selectedCharacter.getName());

	}

	private PlaceResult selectedLocation;

	@Override
	public void onSelectPlace(final SelectPlaceEvent event) {

		if (selectedLocation != null && selectedLocation.getId().equals(event.getPlace().getId())) {
			return;
		}

		selectedLocation = event.getPlace();
		controlPanel.getLocationSelectionField().setText(selectedLocation.getName());

	}

	@Override
	public void onLoadPlace(LoadPlaceEvent event) {
		clientFactory.getEventBus().fireEvent(new SelectPlaceEvent(event.getPlace()));
	}

	private int markerX = 0;
	private int markerY = 0;

	@Override
	public void onChange(ChangeMarkerEvent event) {
		markerX = event.getX();
		markerY = event.getY();
	}

	@Override
	public void openCharacter() {
		if (selectedCharacter.getUpdatePermission()) {
			clientFactory.getPlaceController().goTo(new CharacterEditPlace(CreateOrEdit.EditCharacter, selectedCharacter.getId()));
		} else {
			clientFactory.getPlaceController().goTo(new CharacterViewPlace(selectedCharacter.getId()));
		}
	}

	@Override
	public void openGame() {
		clientFactory.getPlaceController().goTo(new GameViewPlace(game.getId()));
	}

	@Override
	public void refresh() {

		clientFactory.getPlaceController().goTo(new PlayPlace());
		
		//request latest posts from server
		clientFactory.getGameController().getLatestPosts();

	}
	
	@Override
	public void important() {
		
		clientFactory.getPlaceController().goTo(new PlayPlace());
		
		//request latest important posts from server
		clientFactory.getGameController().getLatestImportantPosts();

	}
	
	@Override
	public void createMap() {
		clientFactory.getPlaceController().goTo(new PlaceEditPlace(PlaceEditPlace.CreateOrEdit.CreatePlace, game.getId(), false));
	}
	
	@Override
	public void openMap() {
		clientFactory.getPlaceController().goTo(new PlaceViewPlace(selectedLocation.getId()));
	}

	@Override
	public void editCharacter() {
		if (!selectedCharacter.getUpdatePermission()) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You cannot edit this character"));
		} else {
			clientFactory.getPlaceController().goTo(new CharacterEditPlace(CreateOrEdit.EditCharacter, selectedCharacter.getId()));
		}
	}
	
	@Override
	public void createCharacter() {
		if (markerX < 0 || markerY < 0) {
			clientFactory.getPostListView().showMap();
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You must set the map marker to show where the character will be created"));
		} else {
			clientFactory.getPlaceController().goTo(new CharacterEditPlace(CreateOrEdit.CreateCharacter, selectedLocation.getId()));
		}
	}

	@Override
	public void copyCharacter() {

		if (selectedCharacter == null || selectedCharacter.getId().equals(playingCharacter.getId())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Select a character to copy"));
			return;
		}
		if (markerX < 0 || markerY < 0) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Place your marker where you want to create a copy of the selected character"));
			return;
		}
		clientFactory.getGameController().saveCharacter(selectedCharacter, true);
		
	}

	private Boolean moving = null;

	@Override
	public void moveCharacter() {
		
		if (!game.getUpdatePermission()) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Only the Game Master can move character tokens"));
			return;
		}

		if (selectedCharacter.getCharacterType().equals("GM")) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "The Game Master does not have a token and cannot be moved"));
			return;
		}

		if (markerX < 0 || markerY < 0) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You must set the map marker to show where the character will be moved to"));
			return;
		}

		selectedCharacter.setTokenX(new Double(markerX));
		selectedCharacter.setTokenY(new Double(markerY));
		selectedCharacter.setLocationId(selectedLocation.getId());
		moving = true;
		clientFactory.getGameController().saveCharacter(selectedCharacter);
		clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent());

	}

	private Boolean hiding = null;
	
	@Override
	public void hideCharacter() {

		if (selectedCharacter.getId().equals(game.getGameMasterCharacterId())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "The Game Master does not have a token and cannot be hidden"));
			return;
		}

		selectedCharacter.setTokenHidden(selectedCharacter.getTokenHidden() == null ? false : !selectedCharacter.getTokenHidden());
		clientFactory.getGameController().saveCharacter(selectedCharacter);
		hiding = selectedCharacter.getTokenHidden();

	}

	@Override
	public void playCharacter() {
		clientFactory.getGameController().play(selectedCharacter.getId());
	}

}
