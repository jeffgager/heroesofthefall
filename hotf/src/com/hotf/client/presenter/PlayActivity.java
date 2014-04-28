package com.hotf.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.ClientFactory;
import com.hotf.client.GameController;
import com.hotf.client.HotfRulesEngine;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.event.AddToNextPostEvent;
import com.hotf.client.event.AddToNextPostEventHandler;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeAccountEventHandler;
import com.hotf.client.event.ChangeCharacterEvent;
import com.hotf.client.event.ChangeCharacterEventHandler;
import com.hotf.client.event.ChangeGameEvent;
import com.hotf.client.event.ChangeGameEventHandler;
import com.hotf.client.event.ChangeMarkerEvent;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePlaceEventHandler;
import com.hotf.client.event.ChangePostEvent;
import com.hotf.client.event.ChangePostEventHandler;
import com.hotf.client.event.LoadOtherCharactersEvent;
import com.hotf.client.event.LoadOtherCharactersEventHandler;
import com.hotf.client.event.LoadPostsEvent;
import com.hotf.client.event.LoadPostsEventHandler;
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
import com.hotf.client.view.PlayView;
import com.hotf.client.view.control.CharacterOracle.CharacterSuggestion;
import com.hotf.client.view.control.LocationOracle.LocationSuggestion;

/**
 * PlayPresenter.
 * The Presenter for PlayView's.
 * 
 * @author Jeff Gager
 */
public class PlayActivity extends AbstractActivity implements PlayView.Presenter, LoadPostsEventHandler, LoadOtherCharactersEventHandler, ChangePostEventHandler, ChangeCharacterEventHandler, ChangePlaceEventHandler, PlayEventHandler, SelectCharacterEventHandler, SelectPlaceEventHandler, ChangeAccountEventHandler, ChangeGameEventHandler, AddToNextPostEventHandler {

	private ClientFactory clientFactory;

	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public PlayActivity(final ClientFactory clientFactory) {
		
		this.clientFactory = clientFactory;
		
		clientFactory.getEventBus().addHandler(PlayEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeAccountEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeGameEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeCharacterEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangePlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangePostEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(SelectPlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(SelectCharacterEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadOtherCharactersEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadPostsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(AddToNextPostEvent.TYPE, this);

		final PlayView postListView = clientFactory.getPostListView();
		postListView.getLocationSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
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
		postListView.getCharacterSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
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

		postListView.clearPlaceMarker();

	}

	@Override
	public void weaponChange() {
		postingMessage();
		setAttackTypes();
	}
	
	@Override
	public void privateFieldChange() {
		postingMessage();
	}
	
	@Override
	public void importantFieldChange() {
		postingMessage();
	}
	
	private AcceptsOneWidget container;

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		
		this.container = container;
		
		PlayView postListView = clientFactory.getPostListView();
		postListView.setPresenter(this);
		postListView.setImportantPostValue(false);
		postListView.setPrivatePostValue(false);
		
		clientFactory.getGameController().play(clientFactory.getAccount().getPlayingCharacterId());
		
	}
	
	@Override
	public void onStop() {
		container = null;
	}
	
	@Override
	public void onPlay(PlayEvent event) {
	
		game = event.getGame();
		playingCharacter = event.getCharacter();
		selectedCharacter = event.getCharacter();
		playingLocation = event.getLocation();
		selectedLocation = event.getLocation();
		
		if (container == null) {
			return;
		}

		PlayView postListView = clientFactory.getPostListView();
		postListView.getLocationSelectionField().setText(playingLocation.getName());
		postListView.getCharacterSelectionField().setText(playingCharacter.getName());
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.appendHtmlConstant("<p>");
		sb.appendEscaped(playingLocation.getName());
		sb.appendHtmlConstant("</p>");
		postListView.getMapNameField().setHTML(sb.toSafeHtml());
		postListView.getGMCommandsHidden().setVisible(game.getUpdatePermission() && "GM".equals(selectedCharacter.getCharacterType()));

		initMap();
		initCharacter();
		container.setWidget(postListView);
		postingMessage();

	}

	private void postingMessage() {
		if (container == null || game == null || playingCharacter == null || playingLocation == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		PlayView postListView = clientFactory.getPostListView();
		sb.append("Posting as ");
		postListView.getPrivateFieldEnabled().setEnabled(playingLocation.getType() == null);
		sb.append(playingCharacter.getName());
		Boolean isPrivatePost = postListView.getPrivatePostValue();
		if (isPrivatePost != null && isPrivatePost) {
			if (selectedCharacter != null && !selectedCharacter.getId().equals(playingCharacter.getId())) {
				sb.append(" to " + selectedCharacter.getName() + " only");
			} else {
				sb.append(" to the Game Master only");
			}
		}
		if (account != null && playingLocation != null && !account.getHomeLocationId().equals(playingLocation.getId())) {
			if (selectedLocation != null) {
				if (playingCharacter.getCharacterType().equals("GM")) {
					sb.append(" in " + selectedLocation.getName());
				} else {
					sb.append(" in " + playingLocation.getName());
				}
			}
		}
		if (selectedLocation != null && account != null && !selectedLocation.getId().equals(account.getHomeLocationId())) {
			sb.append(" at " + game.getTitle());
		}
		String status = playingCharacter.getStatus();
		sb.append(" Status is " + (status == null || status.length() <= 0 ? "Ok" : status));
		Boolean isImportantPost = postListView.getImportantPostValue();
		if (isImportantPost != null && isImportantPost) {
			sb.append(" (Important)");
		}
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, sb.toString()));
	}

	private boolean importantOnly;

	@Override
	public void getMorePosts() {

		//request more posts
		clientFactory.getGameController().getMorePosts(importantOnly);

	}
	
	private List<PostResult> posts;

	@Override
	public void onLoadPosts(LoadPostsEvent event) {

		if (container == null) {
			return;
		}

		//important only
		importantOnly = event.isImportant();
		
		//get view
		PlayView postListView = clientFactory.getPostListView();
		
		//get the posts and stop if there are no posts to load
		posts = event.getPosts();

		//clear existing posts if this is a refresh event
		if (event.isRefresh()) {
			postListView.setPosts(posts);
		} else {
			postListView.setMorePosts(posts);
		}
		
		//show or hide next rows panel
		int fetchRows = account != null ? account.getFetchRows(): 20;
		boolean morerows = posts.size() >= fetchRows;
		postListView.getGetMorePostsLink().setVisible(morerows);
		postListView.getNoMorePostsField().setVisible(!morerows);
		
		updateMarkers(event.isRefresh());

		init();
		posted = false;

	}

	private void updateMarkers(boolean refresh) {
		final PlayView playView = clientFactory.getPostListView();
		if (refresh) {
			playView.clearPostMarkers();
		}
		if (posts == null) {
			return;
		}
		List<String> markers = new ArrayList<String>();
		for (PostResult p : posts) {
			String cid = p.getCharacterId();
			if (p.getMarkerX() + p.getMarkerY() >= 0 && 
				!markers.contains(cid) && 
				p.getLocationId().equals(selectedLocation.getId())) {
				playView.addPostMarker(p.getCharacterName(), p.getMarkerX(), p.getMarkerY());
				markers.add(cid);
			} else if (p.getMarkerX() == -99 && p.getMarkerY() == -99) {
				markers.add(cid);
			}
		}
	}
	
	@Override
	public void selectCharacter(CharacterResult character) {
		clientFactory.getEventBus().fireEvent(new SelectCharacterEvent(character));
	}
	
	@Override
	public void clearSelection() {
		clientFactory.getEventBus().fireEvent(new SelectCharacterEvent(playingCharacter));
	}

	@Override
	public void onLoadOtherCharacters(LoadOtherCharactersEvent event) {

		if (container == null) {
			return;
		}

		PlayView playView = clientFactory.getPostListView();
		playView.clearTokens();
		
		for (CharacterResult c : event.getCharacters()) {
			if (c.getUpdatePermission() || !c.getTokenHidden()) {
				if (!c.getId().equals(game.getGameMasterCharacterId())) {
					playView.addToken(c);
				}
			}
		}
		updateMarkers(true);
		updateTargets();

		HotfRulesEngine re = clientFactory.getRulesEngine();
		playView.setActions(re.getCharacterActions(event.getCharacters()));

	}

	private void updateTargets() {
		GameController gc = clientFactory.getGameController();
		CharacterResult ltc = gc.getCharacterById(playingCharacter.getLeftTargetCharacterId());
		CharacterResult rtc = gc.getCharacterById(playingCharacter.getRightTargetCharacterId());
		PlayView postListView = clientFactory.getPostListView();
		postListView.setLeftTargetCharacterName(ltc != null ? ltc.getName() : "No Target Selected");
		postListView.setRightTargetCharacterName(rtc != null ? rtc.getName() : "No Target Selected");
		postListView.setLeftTargetArea(playingCharacter.getLeftTargetArea());
		postListView.setRightTargetArea(playingCharacter.getRightTargetArea());
	}

	protected int stringToInteger(String s) {
		if (s == null) {
			return 0;
		}
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private AccountResult account;

	@Override
	public void onChange(ChangeAccountEvent event) {

		if (container == null) {
			return;
		}

		account = event.getAccount();
		if (account == null) {
			return;
		}
		PlayView postListView = clientFactory.getPostListView();
		postListView.getPortraitHidden().setVisible(event.getAccount().getShowPortraits());
		postListView.setShowPortraits(account.getShowPortraits());
		postListView.redoList();
	}

	@Override
	public void onChange(ChangePostEvent event) {
		PlayView postListView = clientFactory.getPostListView();
		postListView.setPost(event.getPost());
	}
	
	@Override
	public void onChange(ChangeGameEvent event) {
		
		GameResult gameresult = event.getGame();
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		sb.appendHtmlConstant("<h1>");
		sb.appendEscaped(gameresult.getTitle());
		sb.appendHtmlConstant("</h1>");
		PlayView postListView = clientFactory.getPostListView();
		postListView.getGMCommandsHidden().setVisible(gameresult.getUpdatePermission() && "GM".equals(selectedCharacter.getCharacterType()));

	}
	
	@Override
	public void editPost(PostResult post) {
		new PostEditorPresenter(game, post, clientFactory);
	}

	@Override
	public void markImportant(PostResult post) {
		clientFactory.getGameController().markImportantPost(post, clientFactory.getAccount().getPlayingCharacterId());
	}

	@Override
	public void onChange(ChangeCharacterEvent event) {
		
		PlayView postListView = clientFactory.getPostListView();
		//update playing character (if that's the character that changed
		CharacterResult character = event.getCharacter();
		if (playingCharacter != null && playingCharacter.getId() != null && playingCharacter.getId().equals(character.getId())) {
			playingCharacter = event.getCharacter();
			postListView.getCharacterSelectionField().setText(event.getCharacter().getName());
			postingMessage();
			initCharacter();
		}

		//update selected character
		if (selectedCharacter != null && character.getId() != null && character.getId().equals(selectedCharacter.getId())) {
			if (moving != null && moving) {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Moved - " + character.getLabel()));
				moving = null;
			}
			if (hiding != null) {
				String hc = hiding ? "Hidden - " : "Revealed - ";
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, hc + character.getLabel()));
				hiding = null;
			}
			if (event.getCharacter().getId().equals(selectedCharacter.getId())) {
				selectedCharacter = event.getCharacter();
			}
			postListView.scroll(selectedCharacter.getTokenX().intValue(), selectedCharacter.getTokenY().intValue());
			postingMessage();
			initMap();
		}

		HotfRulesEngine re = clientFactory.getRulesEngine();
		GameController gc = clientFactory.getGameController();
		postListView.setActions(re.getCharacterActions(gc.getOtherCharacters()));

		postListView.addToken(character);

	}

	@Override
	public void onChange(ChangePlaceEvent event) {
		
		//update playing location (if that's the character that changed
		PlaceResult location = event.getPlace();
		if (playingLocation != null && playingLocation.getId() != null && playingLocation.getId().equals(location.getId())) {
			playingLocation = location;
			initMap();
			postingMessage();
		}
		if (selectedLocation != null && selectedLocation.getId() != null && selectedLocation.getId().equals(location.getId())) {
			selectedLocation = location;
			initMap();
			postingMessage();
			PlayView postListView = clientFactory.getPostListView();
			postListView.getLocationSelectionField().setText(location.getName());
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant("<p>");
			sb.appendEscaped(playingLocation.getName());
			sb.appendHtmlConstant("</p>");
			postListView.getMapNameField().setHTML(sb.toSafeHtml());
		}

	}
	
	private int markerX = -50;
	private int markerY = -50;

	@Override
	public void mapClick(int x, int y) {

		markerX = x;
		markerY = y;
		PlayView postListView = clientFactory.getPostListView();
		int range = 0;
		if (!"GM".equals(playingCharacter.getCharacterType())) {
			range = ImageUtils.get().distance(
					playingCharacter.getTokenX().intValue(), playingCharacter.getTokenY().intValue(), 
					markerX, markerY);
		} else if (!playingCharacter.getId().equals(selectedCharacter.getId())) {
			range = ImageUtils.get().distance(
					selectedCharacter.getTokenX().intValue(), selectedCharacter.getTokenY().intValue(), 
					markerX, markerY);
		}
		postListView.setPlaceMarker(markerX, markerY, range);
		clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent(markerX, markerY));

		if (markerX + markerY > 0.0D) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Save Post to place a marker at this point. Click on the marker and Save Post again to remove it."));
			clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent(markerX, markerY));
		}
	}

	public void clearedPlaceMarker() {
		markerX = -50;
		markerY = -50;
	}

	@Override
	public void clearedPostMarker() {
		markerX = -99;
		markerY = -99;
	}
	
	private CharacterResult playingCharacter;
	private PlaceResult playingLocation;
	private GameResult game;
	
	private void initMap() {

		PlayView postListView = clientFactory.getPostListView();
		postListView.setMapUrl(ImageUtils.get().getMapOverlayUrl(selectedLocation));
		postListView.getPlaceDescriptionField().setHTML(selectedLocation.getDescription());
		updateMarkers(true);
		if (selectedCharacter != null) {
			postListView.scroll(selectedCharacter.getTokenX().intValue(), selectedCharacter.getTokenY().intValue());
		}
		postListView.getGameNameField().setText(game.getTitle());

	}

	private void initCharacter() {
		PlayView postListView = clientFactory.getPostListView();
		postListView.getPortraitImage().setUrl(ImageUtils.get().getPortraitUrl(playingCharacter));
		List<GameWeaponResult> cw = new ArrayList<GameWeaponResult>();
		int leftidx = 0;
		int rightidx = 0;
		for (GameWeaponResult w : game.getWeapons()) {
			if (playingCharacter.getWeapons().contains(w.getName())) {
				cw.add(w);
				if (playingCharacter.getLeftHand().equals(w.getName())) {
					leftidx = cw.size() - 1;
				}
				if (playingCharacter.getRightHand().equals(w.getName())) {
					rightidx = cw.size() - 1;
				}
			}
		}
		postListView.setWeaponList(cw, leftidx, rightidx);
		
		setAttackTypes();
		postListView.setRss(playingCharacter.getName(), playingCharacter.getId());
		updateTargets();
	}

	private void setAttackTypes() {
		PlayView postListView = clientFactory.getPostListView();
		List<String> sl = new ArrayList<String>();
		GameWeaponResult w = clientFactory.getGameController().getWeapon(playingCharacter.getLeftHand());
		if (w.getDefence() != null) {
			sl.add("Defend");
		}
		if (w.getMinRange() != null && w.getMaxRange() != null) {
			sl.add("Swing");
			sl.add("Thrust");
		}
		if (w.getShotMinRange() != null && w.getShotMaxRange() != null) {
			sl.add("Shoot");
		}
		postListView.setLeftStrikeList(sl, Math.max(sl.indexOf(playingCharacter.getLeftTargetStrike()), 0));
		if (!sl.contains(playingCharacter.getLeftTargetStrike())) {
			playingCharacter.setLeftTargetStrike(sl.get(0));
		}

		sl.clear();
		w = clientFactory.getGameController().getWeapon(playingCharacter.getRightHand());
		if (w.getDefence() != null) {
			sl.add("Defend");
		}
		if (w.getMinRange() != null && w.getMaxRange() != null) {
			sl.add("Swing");
			sl.add("Thrust");
		}
		if (w.getShotMinRange() != null && w.getShotMaxRange() != null) {
			sl.add("Shoot");
		}
		postListView.setRightStrikeList(sl, Math.max(sl.indexOf(playingCharacter.getRightTargetStrike()), 0));
		if (!sl.contains(playingCharacter.getRightTargetStrike())) {
			playingCharacter.setRightTargetStrike(sl.get(0));
		}
		
	}

	public void init() {
		
		final PlayView postListView = clientFactory.getPostListView();
		postListView.setImportantPostValue(false);
		postListView.setPrivatePostValue(false);
		postListView.getPostField().setText(" ");
		markerX = -50;
		markerY = -50;

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				postListView.getPostFocus().setFocus(true);
			}
		});
	
	}

	private CharacterResult selectedCharacter;
	
	@Override
	public void onSelectCharacter(SelectCharacterEvent event) {
		selectedCharacter = event.getCharacter();
		PlayView postListView = clientFactory.getPostListView();
		postListView.scroll(selectedCharacter.getTokenX().intValue(), selectedCharacter.getTokenY().intValue());
		postingMessage();
		postListView.getCharacterSelectionField().setText(selectedCharacter.getName());
	}

	private PlaceResult selectedLocation;

	@Override
	public void onSelectPlace(SelectPlaceEvent event) {
		PlaceResult l = event.getPlace();
		//update selected Location
		if (selectedLocation == null || !selectedLocation.getId().equals(l.getId())) {
			selectedLocation = l;
		}
		initMap();
		postingMessage();
		PlayView postListView = clientFactory.getPostListView();
		postListView.getLocationSelectionField().setText(selectedLocation.getName());
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
		
		if (selectedCharacter.getCharacterType().equals("GM")) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "The Game Master does not have a token and cannot be moved"));
			return;
		}

		if (markerX < 0 || markerY < 0) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You must set the map marker to show where the character will be moved to"));
			return;
		}

		if (playingCharacter.getCharacterType().equals("GM")) {
			selectedCharacter.setTokenX(new Double(markerX));
			selectedCharacter.setTokenY(new Double(markerY));
			selectedCharacter.setLocationId(selectedLocation.getId());
			clientFactory.getGameController().saveCharacter(selectedCharacter);
		} else {
			playingCharacter.setTokenX(new Double(markerX));
			playingCharacter.setTokenY(new Double(markerY));
			clientFactory.getGameController().saveCharacter(playingCharacter);
		}
		moving = true;
		PlayView playView = clientFactory.getPostListView();
		playView.clearPlaceMarker();
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

	private boolean posted = false;

	@Override
	public void setLeftHand(String weapon) {
		if (playingCharacter.getId().equals(game.getGameMasterCharacterId())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "The Game Master does not have a left hand"));
			return;
		}
		playingCharacter.setLeftHand(weapon);
		setAttackTypes();
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(weapon).getTwoHanded()) {
			playingCharacter.setRightHand(weapon);
			playingCharacter.setRightTargetArea(playingCharacter.getLeftTargetArea());
			playingCharacter.setRightTargetCharacterId(playingCharacter.getLeftTargetCharacterId());
			playingCharacter.setRightTargetStrike(playingCharacter.getLeftTargetStrike());
		}
		gc.saveCharacter(playingCharacter);
	}
	
	@Override
	public void setRightHand(String weapon) {
		if (playingCharacter.getId().equals(game.getGameMasterCharacterId())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "The Game Master does not have a right hand"));
			return;
		}
		playingCharacter.setRightHand(weapon);
		setAttackTypes();
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(weapon).getTwoHanded()) {
			playingCharacter.setLeftHand(weapon);
			playingCharacter.setLeftTargetArea(playingCharacter.getRightTargetArea());
			playingCharacter.setLeftTargetCharacterId(playingCharacter.getRightTargetCharacterId());
			playingCharacter.setLeftTargetStrike(playingCharacter.getRightTargetStrike());
		}
		gc.saveCharacter(playingCharacter);
	}

	@Override
	public void leftTargetArea(Integer area) {
		playingCharacter.setLeftTargetArea(area);
		updateTargets();
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(playingCharacter.getLeftHand()).getTwoHanded()) {
			playingCharacter.setRightTargetArea(area);
		}
		gc.saveCharacter(playingCharacter);
	}
	
	@Override
	public void rightTargetArea(Integer area) {
		playingCharacter.setRightTargetArea(area);
		updateTargets();
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(playingCharacter.getRightHand()).getTwoHanded()) {
			playingCharacter.setLeftTargetArea(area);
		}
		gc.saveCharacter(playingCharacter);
	}
	
	@Override
	public void leftTargetStrike(String strike) {
		playingCharacter.setLeftTargetStrike(strike);
		if ("Defend".equals(strike)) {
			playingCharacter.setDefence("L");
		}
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(playingCharacter.getLeftHand()).getTwoHanded()) {
			playingCharacter.setRightTargetStrike(strike);
		}
		gc.saveCharacter(playingCharacter);
	}
	
	@Override
	public void rightTargetStrike(String strike) {
		playingCharacter.setRightTargetStrike(strike);
		if ("Defend".equals(strike)) {
			playingCharacter.setDefence("R");
		}
		GameController gc = clientFactory.getGameController();
		if (gc.getWeapon(playingCharacter.getRightHand()).getTwoHanded()) {
			playingCharacter.setLeftTargetStrike(strike);
		}
		gc.saveCharacter(playingCharacter);
	}

	@Override
	public void leftTargetCharacter() {
		String selid = selectedCharacter.getId();
		GameController gc = clientFactory.getGameController();
		if (gc.getCharacterById(selid) == null) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You can only target characters in the same place as your character"));
			return;
		}
		String val = playingCharacter.getLeftTargetCharacterId();
		if ((val == null || "".equals(val)) && !selid.equals(playingCharacter.getId())) {
			playingCharacter.setLeftTargetCharacterId(selid);
		} else {
			playingCharacter.setLeftTargetCharacterId(null);
		}
		if (gc.getWeapon(playingCharacter.getLeftHand()).getTwoHanded()) {
			playingCharacter.setRightTargetCharacterId(playingCharacter.getLeftTargetCharacterId());
		}
		updateTargets();
		gc.saveCharacter(playingCharacter);
	}
	
	@Override
	public void rightTargetCharacter() {
		String selid = selectedCharacter.getId();
		GameController gc = clientFactory.getGameController();
		if (gc.getCharacterById(selid) == null) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "You can only target characters in the same place as your character"));
			return;
		}
		String val = playingCharacter.getRightTargetCharacterId();
		if ((val == null || "".equals(val)) && !selid.equals(playingCharacter.getId())) {
			playingCharacter.setRightTargetCharacterId(selid);
		} else {
			playingCharacter.setRightTargetCharacterId(null);
		}
		if (gc.getWeapon(playingCharacter.getRightHand()).getTwoHanded()) {
			playingCharacter.setLeftTargetCharacterId(playingCharacter.getRightTargetCharacterId());
		}
		updateTargets();
		clientFactory.getGameController().saveCharacter(playingCharacter);
	}

	@Override
	public void onAdd(AddToNextPostEvent event) {
		PlayView playView = clientFactory.getPostListView();
		playView.getPostField().setText(event.getText());
	}
	
	@Override
	public void createPost() {
		
		if (posted) {
			return;
		}
		final PlayView postListView = clientFactory.getPostListView();
		//make sure there is something to post
		String postText = postListView.getPostField().getHTML();
		if (postText == null || postListView.getPostField().getText().length() <= 1) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "No text entered in post"));
			return;
		}

		//create post
		PostResult post = new PostResult();
		post.setCharacterId(playingCharacter.getId());
		post.setImportant(postListView.getImportantPostValue());

		post.setText(postText);
		post.setLocationId(selectedLocation.getId());

		//set private post indicator value
		String target = null;
		if (postListView.getPrivatePostValue()) {
			if (!playingCharacter.getId().equals(selectedCharacter.getId())) {
				target = selectedCharacter.getId();
			} else {
				target = game.getGameMasterCharacterId();
			}
		}
		post.setTargetedCharacterId(target);

		//set the marker position
		post.setMarkerX(markerX);
		post.setMarkerY(markerY);
		clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent());

		//save post
		clientFactory.getGameController().savePost(post);
		
		postListView.resetButtons();
		postListView.clearPlaceMarker();
		markerX = -50;
		markerY = -50;
		posted = true;
		postingMessage();

	}

	public static class PlayPlace extends Place {
		public static final String TOKEN = "play";
	}

}
