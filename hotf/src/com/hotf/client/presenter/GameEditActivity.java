package com.hotf.client.presenter;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameImplementResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.event.ChangeGameEvent;
import com.hotf.client.event.ChangeGameEventHandler;
import com.hotf.client.event.LoadGameEvent;
import com.hotf.client.event.LoadGameEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace.CreateOrEdit;
import com.hotf.client.presenter.GameViewActivity.GameViewPlace;
import com.hotf.client.view.GameEdit;

public class GameEditActivity extends AbstractActivity implements GameEdit.Presenter, ChangeGameEventHandler, LoadGameEventHandler {

	private ClientFactory clientFactory;
	private GameResult game;
	private HandlerRegistration loadGameRegistration;
	private HandlerRegistration changeGameRegistration;

	public GameEditActivity(GameEditPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;

		//initialise the view
		final GameEdit gameEdit = clientFactory.getGameEdit();

		//install presenter
		gameEdit.setPresenter(this);

		//install event listeners
		loadGameRegistration = clientFactory.getEventBus().addHandler(LoadGameEvent.TYPE, this);
		changeGameRegistration = clientFactory.getEventBus().addHandler(ChangeGameEvent.TYPE, this);
		
		if (place.getCreateOrEdit().equals(CreateOrEdit.CreateGame)) {

			//create a game
			game = new GameResult();
			gameEdit.setShowTabs(false);
			gameEdit.getTitleValue().setText("New Game");
			gameEdit.getDescriptionValue().setHTML("");
			gameEdit.setGeneralSkills(game.getGeneralSkills());
			clientFactory.getAppWindowView().setWidget(gameEdit);
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Creating a new Game"));

		} else {

			gameEdit.setShowTabs(false);
			clientFactory.getGameController().loadGame(place.getId());

		}

		//set focus field
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				gameEdit.getTitleFocus().setFocus(true);
			}
		});
		
	}

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		
		final GameEdit gameEdit = clientFactory.getGameEdit();
		clientFactory.getAppWindowView().setWidget(gameEdit);

	}

	@Override
	public void onStop() {
		loadGameRegistration.removeHandler();
		changeGameRegistration.removeHandler();
	}
	
	@Override
	public void onLoadGame(LoadGameEvent event) {

		game = event.getGame();

		final GameEdit gameEdit = clientFactory.getGameEdit();
		gameEdit.setShowTabs(true);
		gameEdit.getTitleValue().setText(game.getTitle());
		gameEdit.getDescriptionValue().setHTML(game.getDescription() != null ? game.getDescription() : "");
		gameEdit.setWeaponList(game.getWeapons());
		gameEdit.setArmourList(game.getArmour());
		gameEdit.setArtifactsList(game.getArtifacts());
		gameEdit.setGeneralSkills(game.getGeneralSkills());
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Editing Game - " + game.getTitle()));
		
	}

	private boolean saving = false;

	@Override
	public void save() {

		final GameEdit gameEdit = clientFactory.getGameEdit();
		game.setTitle(gameEdit.getTitleValue().getText());
		game.setDescription(gameEdit.getDescriptionValue().getHTML());
		saving = true;
		clientFactory.getGameController().saveGame(game);

	}

	@Override
	public void onChange(ChangeGameEvent event) {
		if (saving) {
			saving = false;
			boolean created = game.getId() == null;
			game = event.getGame();
			if (created) {
				clientFactory.getPlaceController().goTo(new GameViewPlace(game.getId()));
			} else {
				close();
			}
		}
	}
	
	@Override
	public void close() {
		History.back();
	}

	@Override
	public void addWeapon() {
		final GameEdit gameEdit = clientFactory.getGameEdit();
		GameWeaponResult w = new GameWeaponResult();
		w.setName("New Weapon");
		game.getWeapons().add(0, w);
		gameEdit.setWeaponList(game.getWeapons());
	}

	@Override
	public void addArmour() {
		final GameEdit gameEdit = clientFactory.getGameEdit();
		GameArmourResult a = new GameArmourResult();
		a.setName("New Armour");
		game.getArmour().add(0, a);
		gameEdit.setArmourList(game.getArmour());
	}
	
	@Override
	public void addArtifact() {
		final GameEdit gameEdit = clientFactory.getGameEdit();
		GameArtifactResult a = new GameArtifactResult();
		a.setName("New Artifact");
		game.getArtifacts().add(0, a);
		gameEdit.setArtifactsList(game.getArtifacts());
	}
	
	@Override
	public void addSkillWeapon(String skillName, GameWeaponResult weapon) {
		for (GameWeaponResult w : game.getWeapons()) {
			if (w.getName().equals(weapon.getName())) {
				w.getSkillNames().add(skillName);
				return;
			}
		}
		GWT.log("Failed to find weapon when adding");
	}
	
	@Override
	public void removeSkillWeapon(String skillName, GameWeaponResult weapon) {
		for (GameWeaponResult w : game.getWeapons()) {
			if (w.getName().equals(weapon.getName())) {
				w.getSkillNames().remove(skillName);
				return;
			}
		}
		GWT.log("Failed to find weapon when removing");
	}
	
	@Override
	public void addSkillArtifact(String skillName, GameArtifactResult artifact) {
		for (GameArtifactResult w : game.getArtifacts()) {
			if (w.getName().equals(artifact.getName())) {
				w.getSkillNames().add(skillName);
				return;
			}
		}
		GWT.log("Failed to find artifact when adding");
	}
	
	@Override
	public void removeSkillArtifact(String skillName, GameArtifactResult artifact) {
		for (GameArtifactResult w : game.getArtifacts()) {
			if (w.getName().equals(artifact.getName())) {
				w.getSkillNames().remove(skillName);
				return;
			}
		}
		GWT.log("Failed to find artifact when removing");
	}
	
	@Override
	public List<GameImplementResult> getSkillImplements(String skillName) {
		return clientFactory.getGameController().getSkillImplements(skillName);
	}
	
	public static class GameEditPlace extends Place {

		public static enum CreateOrEdit {CreateGame, EditGame};

		private CreateOrEdit createOrEdit;
		private String id;
		
		public GameEditPlace() {
			this.createOrEdit = CreateOrEdit.CreateGame;
			this.id = null;
		}

		public GameEditPlace(CreateOrEdit createOrEdit, String id) {
			this.createOrEdit = createOrEdit;
			this.id = id;
		}

		public CreateOrEdit getCreateOrEdit() {
			return createOrEdit;
		}
		
		public String getId() {
			return id;
		}

	}

}