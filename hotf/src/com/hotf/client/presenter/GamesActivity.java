package com.hotf.client.presenter;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeAccountEventHandler;
import com.hotf.client.event.LoadGameEvent;
import com.hotf.client.event.LoadGameEventHandler;
import com.hotf.client.event.LoadGamesEvent;
import com.hotf.client.event.LoadGamesEventHandler;
import com.hotf.client.event.LoadPostsEvent;
import com.hotf.client.event.LoadPostsEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.event.PlayEvent;
import com.hotf.client.event.PlayEventHandler;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace;
import com.hotf.client.view.GamesView;

/**
 * PlayPresenter.
 * The Presenter for PlayView's.
 * 
 * @author Jeff Gager
 */
public class GamesActivity extends AbstractActivity implements GamesView.Presenter, LoadGamesEventHandler, PlayEventHandler, LoadPostsEventHandler, ChangeAccountEventHandler, LoadGameEventHandler {

	private ClientFactory clientFactory;
	private GameResult game;
	private CharacterResult playingCharacter;
	

	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public GamesActivity(final ClientFactory clientFactory) {
		
		this.clientFactory = clientFactory;

		clientFactory.getEventBus().addHandler(PlayEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadPostsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadGameEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadGamesEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeAccountEvent.TYPE, this);

	}

	private AcceptsOneWidget container;

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		this.container = container;

		GamesView gameListView = clientFactory.getGameListView();
		gameListView.setPresenter(this);
		gameListView.setMaxReplies(account != null ? account.getFetchRows() : 20);
		if (account != null) {
			clientFactory.getGameController().play(account.getPersonalCharacterId(), account.getHomeLocationId());
		}
		clientFactory.getGameController().getLatestGames();
		
	}

	@Override
	public void onStop() {
		container = null;
	}
	
	@Override
	public void onPlay(PlayEvent event) {
		game = event.getGame();
		playingCharacter = event.getCharacter();
	}

	private AccountResult account;
	
	@Override
	public void onChange(ChangeAccountEvent event) {
	
		account = event.getAccount();
		if (account == null) {
			return;
		}

		//get view
		GamesView gameListView = clientFactory.getGameListView();	
		gameListView.setMaxReplies(account.getFetchRows());

	}
	
	@Override
	public void getMoreGames() {

		//request more posts
		clientFactory.getGameController().getMoreGames();

	}
	
	private List<GameResult> games;

	@Override
	public void onLoadGames(LoadGamesEvent event) {

		if (container == null) {
			return;
		}

		//get view
		GamesView gameListView = clientFactory.getGameListView();
		
		//get the places and stop if there are no posts to load
		games = event.getGames();

		//clear existing places if this is a refresh event
		if (event.isRefresh()) {
			gameListView.setGames(games);
		} else {
			gameListView.setMoreGames(games);
		}
		
		//show or hide next rows panel
		int fetchRows = account != null ? account.getFetchRows() : 20;
		boolean morerows = games.size() >= fetchRows;
		gameListView.getGetMoreGamesLink().setVisible(morerows);
		gameListView.getNoMoreGamesField().setVisible(!morerows);

		container.setWidget(gameListView);

		if (account == null) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Public Game Listing"));
		} else {
			String name = account.getName();
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Posting as " + name + " in the Public Game Listing"));
		}
		
	}

	@Override
	public void onLoadGame(LoadGameEvent event) {
		
		if (container == null) {
			return;
		}

		//get view
		GamesView gameListView = clientFactory.getGameListView();
		gameListView.setGame(event.getGame());

	}
	
	@Override
	public void editGame(GameResult game) {
		clientFactory.getPlaceController().goTo(new GameEditPlace(GameEditPlace.CreateOrEdit.EditGame, game.getId()));
	}

	@Override
	public void createGame() {
		if (game == null) {
			clientFactory.getGameController().login();
		} else {
			clientFactory.getPlaceController().goTo(new GameEditPlace(GameEditPlace.CreateOrEdit.CreateGame, null));
		}
	}

	private boolean gettingposts = false;

	@Override
	public void getPosts(GameResult game) {
		gettingposts = true;
		clientFactory.getGameController().getLatestPosts(game);
	}
	
	@Override
	public void getMorePosts(GameResult game) {
		gettingposts = true;
		clientFactory.getGameController().getMorePosts(false, game);
	}

	@Override
	public void onLoadPosts(LoadPostsEvent event) {
		
		if (container == null || !gettingposts) {
			return;
		}

		//get view
		GamesView gameListView = clientFactory.getGameListView();

		//get the posts and stop if there are no posts to load
		List<PostResult> posts = event.getPosts();

		//clear existing posts if this is a refresh event
		if (event.isRefresh()) {
			gameListView.setPosts(posts);
		} else {
			gameListView.setMorePosts(posts);
		}

		gettingposts = false;

		if (savingGame != null) {
			clientFactory.getGameController().loadLocation(savingGame.getStart().getId());
			savingGame = null;
		}

	}

	private GameResult savingGame= null;
	
	@Override
	public void createPost(GameResult game, String postText) {

		//create post
		PostResult post = new PostResult();
		post.setCharacterId(playingCharacter.getId());
		post.setText(postText);
		post.setLocationId(game.getStart().getId());
		post.setImportant(false);
		savingGame = game;

		//save post
		gettingposts = true;
		clientFactory.getGameController().savePost(post);
		
	}

	
	public static class GamesPlace extends Place {
		public static final String TOKEN = "games";
	}

}
