package com.hotf.client.presenter;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeAccountEventHandler;
import com.hotf.client.event.ChangeCharacterEvent;
import com.hotf.client.event.ChangeCharacterEventHandler;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePlaceEventHandler;
import com.hotf.client.event.ChangePostEvent;
import com.hotf.client.event.ChangePostEventHandler;
import com.hotf.client.event.LoadPostsEvent;
import com.hotf.client.event.LoadPostsEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.PlayEvent;
import com.hotf.client.event.PlayEventHandler;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.AccountView;
import com.hotf.client.view.dialog.TermsAndConditionsDialog;
import com.hotf.client.view.dialog.TermsAndConditionsDialogImpl;

/**
 * PlayPresenter.
 * The Presenter for PlayView's.
 * 
 * @author Jeff Gager
 */
public class AccountActivity extends AbstractActivity implements AccountView.Presenter, LoadPostsEventHandler, ChangePostEventHandler, PlayEventHandler, ChangeAccountEventHandler, ChangeCharacterEventHandler, ChangePlaceEventHandler, TermsAndConditionsDialog.Presenter {

	private ClientFactory clientFactory;

	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public AccountActivity(final ClientFactory clientFactory) {
		
		this.clientFactory = clientFactory;

		clientFactory.getEventBus().addHandler(ChangeAccountEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeCharacterEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangePlaceEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadPostsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(PlayEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangePostEvent.TYPE, this);

	}

	private AcceptsOneWidget container;
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		this.container = container;
		this.saving = false;

		AccountResult account = clientFactory.getAccount();
		if (account == null) {
			return;
		}
		AccountView strongholdView = clientFactory.getStrongholdView();
		strongholdView.setPresenter(this);
		strongholdView.getUsernameEnabled().setEnabled(account.getStrongholdGameId() == null);
		strongholdView.getUsernameField().setText(account.getName());
		strongholdView.getFetchRowsField().setText(account.getFetchRows().toString());
		strongholdView.getSearchRowsField().setText(account.getSearchRows().toString());
		strongholdView.getShowPortraitsField().setValue(account.getShowPortraits());

		if (account.getPersonalCharacterId() != null) {
			clientFactory.getGameController().play(account.getPersonalCharacterId(), account.getHomeLocationId());
		} else {
			container.setWidget(strongholdView);
			String n = clientFactory.getAccount().getName();
			if (n != null && n.length() > 0) {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Account Settings - " + n));
			} else {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Account Settings"));
			}
		}
		
	}

	@Override
	public void onStop() {
		container = null;
	}
	
	@Override
	public void onPlay(PlayEvent event) {
	
		playingCharacter = event.getCharacter();
		playingLocation = event.getLocation();
		if (container != null) {
			AccountView strongholdView = clientFactory.getStrongholdView();
			container.setWidget(strongholdView);
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Account Settings - " + clientFactory.getAccount().getName()));
		}

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
		AccountView strongholdView = clientFactory.getStrongholdView();
		
		//get the posts and stop if there are no posts to load
		posts = event.getPosts();

		//clear existing posts if this is a refresh event
		if (event.isRefresh()) {
			strongholdView.setPosts(posts);
		} else {
			strongholdView.setMorePosts(posts);
		}

		//show or hide next rows panel
		AccountResult account = clientFactory.getAccount();
		int fetchRows = account != null ? account.getFetchRows() : 20;
		boolean morerows = posts.size() >= fetchRows;
		strongholdView.getGetMorePostsLink().setVisible(morerows);
		strongholdView.getNoMorePostsField().setVisible(!morerows);

	}

	@Override
	public void onChange(ChangePostEvent event) {
		AccountView strongholdView = clientFactory.getStrongholdView();
		strongholdView.setPost(event.getPost());
	}
	
	@Override
	public void onChange(ChangeCharacterEvent event) {
		
		//update playing character (if that's the character that changed
		CharacterResult character = event.getCharacter();
		if (playingCharacter != null && playingCharacter.getId() != null && playingCharacter.getId().equals(character.getId())) {
			playingCharacter = event.getCharacter();
		}

	}

	@Override
	public void onChange(ChangePlaceEvent event) {
		
		//update playing location (if that's the character that changed
		PlaceResult location = event.getPlace();
		if (playingLocation != null && playingLocation.getId() != null && playingLocation.getId().equals(location.getId())) {
			playingLocation = location;
		}

	}
	
	private CharacterResult playingCharacter;
	private PlaceResult playingLocation;
	private AccountResult account;

	@Override
	public void onChange(ChangeAccountEvent event) {
		if (event.getAccount() == null) {
			return;
		}
		if (saving) {
			clientFactory.getPlaceController().goTo(new PlayPlace());
		} else {
			account = event.getAccount();
			AccountView strongholdView = clientFactory.getStrongholdView();
			strongholdView.setShowPortraits(account.getShowPortraits());
			strongholdView.getUsernameEnabled().setEnabled(account.getStrongholdGameId() == null);
			strongholdView.redoList();
		}
	}

	private TermsAndConditionsDialog tac;

	@Override
	public void showTac() {
		
		tac = new TermsAndConditionsDialogImpl(clientFactory, false);
		tac.setPresenter(this);
		tac.center();

	}
	
	/**
	 * Save action
	 */
	@Override
	public void save() {

		AccountView accountView = clientFactory.getStrongholdView();
		String un = accountView.getUsernameField().getText();
		if (un == null || un.length() <= 2) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Please enter a user name"));
			return;
		}
		tac = new TermsAndConditionsDialogImpl(clientFactory, true);
		tac.setPresenter(this);
		tac.center();
		
	}

	@Override
	public void closeTac() {
		tac.hide();
	}
	
	@Override
	public void notacceptTac() {
		tac.hide();
		clientFactory.getGameController().logout();

	}
	
	private boolean saving = false;

	@Override
	public void acceptTac() {
		
		tac.hide();

		// get the view
		final AccountView strongholdView = clientFactory.getStrongholdView();
		strongholdView.setPresenter(this);

		// get the name and if it is empty or changed the validate it and save
		// it if ok
		final String name = strongholdView.getUsernameField().getText().trim();

		// edit account
		account.setId(clientFactory.getAccount().getId());
		account.setPlayingCharacterId(clientFactory.getAccount().getPlayingCharacterId());
		account.setStrongholdGameId(clientFactory.getAccount().getStrongholdGameId());
		account.setName(name);
		try {
			account.setFetchRows(Integer.valueOf(strongholdView.getFetchRowsField().getText()));
		} catch (NumberFormatException e) {
			account.setFetchRows(null);
		}
		try {
			account.setSearchRows(Integer.valueOf(strongholdView.getSearchRowsField().getText()));
		} catch (NumberFormatException e) {
			account.setSearchRows(null);
		}
		account.setShowPortraits(strongholdView.getShowPortraitsField().getValue());

		saving = true;

		//save account to server
		clientFactory.getGameController().saveAccount(account);

	}
	
	public static class ControlPlace extends Place {
		public static final String TOKEN = "account";
	}

}
