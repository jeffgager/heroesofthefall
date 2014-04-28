package com.hotf.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.event.LoadGameEvent;
import com.hotf.client.event.LoadGameEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace.CreateOrEdit;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.GameView;

public class GameViewActivity extends AbstractActivity implements GameView.Presenter, LoadGameEventHandler {

	private ClientFactory clientFactory;
	private GameResult game;
	private HandlerRegistration loadGameRegistration;

	public GameViewActivity(GameViewPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;

		loadGameRegistration = clientFactory.getEventBus().addHandler(LoadGameEvent.TYPE, this);

		final GameView gameView = clientFactory.getGameView();
		gameView.getTitleValue().setText("");
		gameView.getDescriptionValue().setHTML("");
		gameView.getEditHidden().setVisible(false);

		clientFactory.getGameController().loadGame(place.getId());
		
	}

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		
		//initialise the view
		final GameView gameView = clientFactory.getGameView();
		container.setWidget(gameView);
		gameView.setPresenter(this);

	}

	@Override
	public void onStop() {
		loadGameRegistration.removeHandler();
	}

	@Override
	public void onLoadGame(LoadGameEvent event) {

		game = event.getGame();

		//initialise the view
		final GameView gameView = clientFactory.getGameView();

		gameView.getTitleValue().setText(game.getTitle());
		String description = game.getDescription();
		gameView.getDescriptionValue().setHTML(description != null ? description : "");
		gameView.getEditHidden().setVisible(game.getUpdatePermission());

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Game - " + game.getTitle()));

	}

	@Override
	public void close() {
		clientFactory.getPlaceController().goTo(new PlayPlace());
	}
	
	@Override
	public void edit() {
		clientFactory.getPlaceController().goTo(new GameEditPlace(CreateOrEdit.EditGame, game.getId()));
	}

	public static class GameViewPlace extends Place {

		public static final String TOKEN = "ViewGame";
		private String id;
		
		public GameViewPlace(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

}