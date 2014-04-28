package com.hotf.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameGeneralSkillResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameSkillResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.event.ChangeCharacterEvent;
import com.hotf.client.event.ChangeCharacterEventHandler;
import com.hotf.client.event.ChangeMarkerEvent;
import com.hotf.client.event.LoadCharacterEvent;
import com.hotf.client.event.LoadCharacterEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.UploadUrlEvent;
import com.hotf.client.event.UploadUrlEventHandler;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.event.SelectCharacterEvent;
import com.hotf.client.exception.ValidationException;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace.CreateOrEdit;
import com.hotf.client.presenter.CharacterViewActivity.CharacterViewPlace;
import com.hotf.client.view.CharacterEdit;
import com.hotf.client.view.control.AccountOracle.AccountSuggestion;
import com.hotf.client.view.dialog.UploadDialog;

public class CharacterEditActivity extends AbstractActivity implements CharacterEdit.Presenter, ChangeCharacterEventHandler, LoadCharacterEventHandler {

	private static final Integer[][][] ARMOUR = new Integer[][][] {
		{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0}},
		{{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0}},
		{{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}},
		{{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}},
		{{0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}},
		{{0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3},{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},{0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2}},
		{{0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3},{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},{0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2}},
		{{0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3},{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},{0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2}},
		{{0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3},{0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2},{0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3}}
	};
	
	private ClientFactory clientFactory;
	private CharacterResult character;
	private AccountResult selectedPlayer;
	private HandlerRegistration loadCharacterRegistration;
	private HandlerRegistration changeCharacterRegistration;

	public CharacterEditActivity(CharacterEditPlace place, ClientFactory clientFactory) {
		
		this.clientFactory = clientFactory;
		
		//initialise the view
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		characterEdit.setPresenter(this);

		//listen to events
		loadCharacterRegistration = clientFactory.getEventBus().addHandler(ChangeCharacterEvent.TYPE, this);
		changeCharacterRegistration = clientFactory.getEventBus().addHandler(LoadCharacterEvent.TYPE, this);

		characterEdit.getPlayerSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof AccountSuggestion) {
					AccountSuggestion accountSuggestion = (AccountSuggestion)suggestion;
					selectedPlayer = accountSuggestion.getAccount();
				}
			}
		});

		if (place.getCreateOrEdit().equals(CreateOrEdit.CreateCharacter)) {
			
			//create character
			character = new CharacterResult();
			character.setLocationId(place.getId());
			
			//update form fields with data
			characterEdit.getNameField().setText("New Character");
			characterEdit.setShowTabs(false);
			characterEdit.getPlayerField().setText(clientFactory.getAccount().getName());
			characterEdit.getDescriptionField().setHTML("");
			characterEdit.getSheetField().setHTML("");
			characterEdit.getPlayerEnabled().setEnabled(true);
			characterEdit.getPortraitField().setUrl(ImageUtils.get().getPortraitUrl(character));
			characterEdit.getPortraitHidden().setVisible(clientFactory.getAccount().getShowPortraits());
			characterEdit.getWyrdField().setText("");
			characterEdit.getStatusField().setText("");
			characterEdit.getHandedField().setText("");
			characterEdit.getSkillRanksField().setText("");
			characterEdit.getCharacterPointsField().setText("");
			characterEdit.getVigourField().setText("");
			characterEdit.getMettleField().setText("");
			characterEdit.getWitField().setText("");
			characterEdit.getGlamourField().setText("");
			characterEdit.getSpiritField().setText("");
			characterEdit.getSkillsList().clear();
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Create Character"));
			
		} else {
			
			//edit character
			characterEdit.getNameField().setText("");
			characterEdit.setShowTabs(false);
			characterEdit.getPlayerField().setText("");
			characterEdit.getDescriptionField().setHTML("");
			characterEdit.getSheetField().setHTML("");
			characterEdit.getPlayerEnabled().setEnabled(false);
			characterEdit.getPortraitField().setUrl(ImageUtils.get().getDefaultPortrait());
			characterEdit.getPortraitHidden().setVisible(clientFactory.getAccount().getShowPortraits());
			characterEdit.getWyrdField().setText("");
			characterEdit.getStatusField().setText("");
			characterEdit.getHandedField().setText("");
			characterEdit.getSkillRanksField().setText("");
			characterEdit.getCharacterPointsField().setText("");
			characterEdit.getVigourField().setText("");
			characterEdit.getMettleField().setText("");
			characterEdit.getWitField().setText("");
			characterEdit.getGlamourField().setText("");
			characterEdit.getSpiritField().setText("");
			characterEdit.getSkillsList().clear();

			clientFactory.getGameController().loadCharacter(place.getId());

		}

	}

	@Override
	public void onStop() {
		loadCharacterRegistration.removeHandler();
		changeCharacterRegistration.removeHandler();
	}

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		container.setWidget(characterEdit);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				characterEdit.getNameFocus().setFocus(true);
			}
		});
		
	}

	private GameResult game;
	
	@Override
	public void onLoadCharacter(LoadCharacterEvent event) {
		
		character = event.getCharacter();
		game = event.getGame();

		//update form fields with data
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		characterEdit.setGameMasterControls(game.getUpdatePermission());
		characterEdit.getPlayerField().setText(character.getPlayerName());
		characterEdit.getPlayerEnabled().setEnabled(
				character.getGameMasterAccountId().equals(clientFactory.getAccount().getId())
			&& !"GM".equals(character.getCharacterType()));
		characterEdit.getNameField().setText(character.getName());
		characterEdit.getStatusField().setText(character.getStatus());
		characterEdit.getPortraitField().setUrl(ImageUtils.get().getPortraitUrl(character));
		characterEdit.getPortraitHidden().setVisible(clientFactory.getAccount().getShowPortraits());
		setWeaponList();
		setArmourList();
		setArtifactList();
		setGeneralSkillsList();
		String description = character.getDescription();

		if (description != null) {
			characterEdit.getDescriptionField().setHTML(description);
		} else {
			characterEdit.getDescriptionField().setHTML("");
		}
		String sheet = character.getSheet();
		if (sheet != null) {
			characterEdit.getSheetField().setHTML(sheet);
		} else {
			characterEdit.getSheetField().setHTML("");
		}
		
		characterEdit.getPlayerSelection().addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (suggestion instanceof AccountSuggestion) {
					AccountSuggestion accountSuggestion = (AccountSuggestion)suggestion;
					selectedPlayer = accountSuggestion.getAccount();
				}
			}
		});

		setWeaponList();
		characterEdit.getWyrdField().setText(integerToString(character.getAge()));
		characterEdit.getHandedField().setText(character.getHanded());
		characterEdit.getSkillRanksField().setText(integerToString(character.getSkillRanks()));
		characterEdit.getCharacterPointsField().setText(integerToString(character.getCharacterPoints()));
		characterEdit.getVigourField().setText(integerToString(character.getVigor()));
		characterEdit.getMettleField().setText(integerToString(character.getMettle()));
		characterEdit.getWitField().setText(integerToString(character.getWit()));
		characterEdit.getGlamourField().setText(integerToString(character.getGlamour()));
		characterEdit.getSpiritField().setText(integerToString(character.getSpirit()));
		characterEdit.setShowTabs(true);
		setArmourList();
		setArtifactList();
		setGeneralSkillsList();
		
		characterEdit.setGeneralSkillList(character.getGeneralSkills());

		characterEdit.setArmour(character.getArmourType(), character.getSlashArmour(), character.getCrushArmour(), character.getPierceArmour());

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Character - " + character.getLabel()));
		validate();

	}

	private void setWeaponList() {
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		List<GameWeaponResult> selectableWeapons = new ArrayList<GameWeaponResult>();
		for (GameWeaponResult w : game.getWeapons()) {
			if (!character.getWeapons().contains(w.getName())) {
				selectableWeapons.add(w);
			}
		}
		characterEdit.setWeaponSelectionList(selectableWeapons);
		List<GameWeaponResult> cw = new ArrayList<GameWeaponResult>();
		for (GameWeaponResult w : game.getWeapons()) {
			if (character.getWeapons().contains(w.getName())) {
				cw.add(w);
			}
		}
		characterEdit.setWeaponList(cw);
	}

	private void setArmourList() {
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		List<GameArmourResult> selectableArmour = new ArrayList<GameArmourResult>();
		for (GameArmourResult a : game.getArmour()) {
			if (!character.getArmour().contains(a.getName())) {
				selectableArmour.add(a);
			}
		}
		characterEdit.setArmourSelectionList(selectableArmour);
		List<GameArmourResult> ca = new ArrayList<GameArmourResult>();
		List<Integer> ci = new ArrayList<Integer>();
		for (int i = 0; i < game.getArmour().size(); i++) {
			GameArmourResult a = game.getArmour().get(i);
			if (character.getArmour().contains(a.getName())) {
				ca.add(a);
				ci.add(i);
			}
		}
		characterEdit.setArmourList(ca, ci);
	}
	
	private void setArtifactList() {
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		List<GameArtifactResult> selectableArtifacts = new ArrayList<GameArtifactResult>();
		for (GameArtifactResult a : game.getArtifacts()) {
			if (!character.getArtifacts().contains(a.getName())) {
				selectableArtifacts.add(a);
			}
		}
		characterEdit.setArtifactSelectionList(selectableArtifacts);
		List<GameArtifactResult> ca = new ArrayList<GameArtifactResult>();
		for (GameArtifactResult a : game.getArtifacts()) {
			if (character.getArtifacts().contains(a.getName())) {
				ca.add(a);
			}
		}
		characterEdit.setArtifactList(ca);
	}

	private void setGeneralSkillsList() {
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		List<String> selectableGeneralSkills = new ArrayList<String>();
		List<CharacterGeneralSkillResult> selectedGeneralSkills = new ArrayList<CharacterGeneralSkillResult>();
		List<String> characterGeneralSkillNames = new ArrayList<String>();
		for (CharacterGeneralSkillResult cs : character.getGeneralSkills()) {
			selectedGeneralSkills.add(cs);
			characterGeneralSkillNames.add(cs.getGameGeneralSkill().getName());
		}
		for (GameGeneralSkillResult gs : game.getGeneralSkills()) {
			if (!characterGeneralSkillNames.contains(gs.getName())) {
				selectableGeneralSkills.add(gs.getName());
			}
		}
		characterEdit.setGeneralSkillSelectionList(selectableGeneralSkills);

		setSpecificSkillsList();

		characterEdit.setGeneralSkillList(selectedGeneralSkills);
	
	}

	private void setSpecificSkillsList() {
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();

		List<String> selectableSkills = new ArrayList<String>();
		if (selectedGeneralSkill != null) {
			List<String> characterSkillNames = new ArrayList<String>();
			for (CharacterSkillResult cs : selectedGeneralSkill.getSkills()) {
				characterSkillNames.add(cs.getGameSkill().getName());
			}
			for (GameSkillResult s : selectedGeneralSkill.getGameGeneralSkill().getSkills()) {
				if (!characterSkillNames.contains(s.getName())) {
					selectableSkills.add(s.getName());
				}
			}
		}
		characterEdit.setSkillSelectionList(selectableSkills);

	}

	@Override
	public void addArmour(String id) {
		character.getArmour().add(id);
		setArmourList();
	}
	
	@Override
	public void addArtifact(String id) {
		character.getArtifacts().add(id);
		setArtifactList();
	}
	
	@Override
	public void addWeapon(String id) {
		character.getWeapons().add(id);
		setWeaponList();
	}
	
	@Override
	public void addGeneralSkill(String id) {
		for (GameGeneralSkillResult gs : game.getGeneralSkills()) {
			if (id.equals(gs.getName())) {
				CharacterGeneralSkillResult cgs = new CharacterGeneralSkillResult();
				cgs.setGameGeneralSkill(gs);
				cgs.setRanks(0);
				cgs.setLevel(4);
				cgs.setModifier(0);
				character.getGeneralSkills().add(cgs);
				setGeneralSkillsList();
				return;
			}
		}
		GWT.log("Cound not find general skill: " + id);
	}

	@Override
	public void addSkill(String id) {
		if (selectedGeneralSkill == null) {
			return;
		}
		for (CharacterGeneralSkillResult cgs : character.getGeneralSkills()) {
			if (selectedGeneralSkill.getGameGeneralSkill().getName().equals(cgs.getGameGeneralSkill().getName())) {
				for (GameSkillResult s : selectedGeneralSkill.getGameGeneralSkill().getSkills()) {
					if (s.getName().equals(id)) {
						CharacterSkillResult cs = new CharacterSkillResult();
						cs.setGameSkill(s);
						cs.setRanks(0);
						cs.setLevel(4);
						cs.setModifier(0);
						cgs.getSkills().add(cs);
						setSpecificSkillsList();
						return;
					}
				}
			}
		}
		GWT.log("Cound not find skill: " + id);
	}
	
	@Override
	public void deleteArmour(GameArmourResult a) {
		if (!game.getUpdatePermission()) {
			return;
		}
		if ("No Armour".equals(a.getName())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "You cannot remove this armour"));
			return;
		}
		character.getArmour().remove(a.getName());
		setArmourList();
	}
	
	@Override
	public void deleteArtifact(GameArtifactResult a) {
		if (!game.getUpdatePermission()) {
			return;
		}
		character.getArtifacts().remove(a.getName());
		setArtifactList();
	}

	private static final String[] PERMANENT_WEAPONS = new String[] {"Hand Strike", "Foot Strike", "Head Butt", "Dodge"};

	@Override
	public void deleteWeapon(GameWeaponResult w) {
		if (!game.getUpdatePermission()) {
			return;
		}
		for (int i = 0; i < PERMANENT_WEAPONS.length; i++) {
			if (PERMANENT_WEAPONS[i].equals(w.getName())) {
				clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "You cannot remove this weapon"));
				return;
			}
		}
		character.getWeapons().remove(w.getName());
		setWeaponList();
	}
	
	@Override
	public void removeGeneralSkill() {
		if (selectedGeneralSkill == null || !game.getUpdatePermission()) {
			return;
		}
		if (selectedGeneralSkill.getSkills().size() > 0) {
			return;
		}
		if ("Unarmed Combat".equals(selectedGeneralSkill.getGameGeneralSkill().getName())) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "You cannot remove this skill"));
			return;
		}
		character.getGeneralSkills().remove(selectedGeneralSkill);
		setGeneralSkillsList();
	}
	
	@Override
	public void removeSkill() {
		if (selectedGeneralSkill == null || selectedSkill == null || !game.getUpdatePermission()) {
			return;
		}
		if ("Unarmed Combat".equals(selectedGeneralSkill.getGameGeneralSkill().getName()) &&
			("Strike".equals(selectedSkill.getGameSkill().getName()) ||
			 "Dodge".equals(selectedSkill.getGameSkill().getName()))) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "You cannot remove this skill"));
			return;
		}
		selectedGeneralSkill.getSkills().remove(selectedSkill);
		setSpecificSkillsList();
	}
	
	private boolean saving = false;

	@Override
	public void close() {
		History.back();
	}
	
	@Override
	public void save() {

		updateCharacter();

		//save changes to character
		try {
			clientFactory.getCharacterValidator().validate(character);
			saving = true;
			clientFactory.getGameController().saveCharacter(character);

			clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent());
		} catch (ValidationException e) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, e.getMessage()));
		}

	}

	private void updateCharacter() {
		
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		character.setName(characterEdit.getNameField().getText());
		character.setDescription(characterEdit.getDescriptionField().getHTML());
		character.setSheet(characterEdit.getSheetField().getHTML());
		character.setStatus(characterEdit.getStatusField().getText());
		if (selectedPlayer != null) {
			character.setPlayerAccountId(selectedPlayer.getId());
			character.setPlayerName(selectedPlayer.getName());
		}
		character.setAge(stringToInteger(characterEdit.getWyrdField().getText()));
		character.setHanded(characterEdit.getHandedField().getText());
		character.setSkillRanks(stringToInteger(characterEdit.getSkillRanksField().getText()));
		character.setCharacterPoints(stringToInteger(characterEdit.getCharacterPointsField().getText()));
		character.setVigor(stringToInteger(characterEdit.getVigourField().getText()));
		character.setMettle(stringToInteger(characterEdit.getMettleField().getText()));
		character.setWit(stringToInteger(characterEdit.getWitField().getText()));
		character.setGlamour(stringToInteger(characterEdit.getGlamourField().getText()));
		character.setSpirit(stringToInteger(characterEdit.getSpiritField().getText()));

	}
	
	@Override
	public void updateArmourSelection(int targetArea, int armourType) {
		character.getArmourType()[targetArea] = game.getArmour().get(armourType).getName();
		character.getSlashArmour()[targetArea] = game.getArmour().get(armourType).getSlashDefence();
		character.getCrushArmour()[targetArea] = game.getArmour().get(armourType).getCrushDefence();
		character.getPierceArmour()[targetArea] = game.getArmour().get(armourType).getPierceDefence();
		CharacterEdit cev = clientFactory.getCharacterEdit();
		cev.setArmour(character.getArmourType(), character.getSlashArmour(), character.getCrushArmour(), character.getPierceArmour());
	}
	
	@Override
	public String integerToString(Integer i) {
		if (i == null) {
			return null;
		}
		return i.toString();
	}

	protected Integer stringToInteger(String s) {
		if (s == null) {
			return null;
		}
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public void onChange(ChangeCharacterEvent event) {
		if (saving) {
			saving = false;
			boolean created = character.getId() == null;
			character = event.getCharacter();
			clientFactory.getEventBus().fireEvent(new SelectCharacterEvent(character));
			if (created) {
				clientFactory.getPlaceController().goTo(new CharacterViewPlace(character.getId()));
			} else {
				close();
			}
		}
	}

	@Override
	public void validate() {
		if (character == null) {
			return;
		}
		updateCharacter();
		try {
			clientFactory.getCharacterValidator().validate(character);
		} catch (ValidationException e) {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, e.getMessage()));
		}
		final CharacterEdit characterEdit = clientFactory.getCharacterEdit();
		characterEdit.getCharacterCostField().setText(integerToString(clientFactory.getCharacterValidator().getCharacterPointCost()) + " / ");
		characterEdit.getSkillCostField().setText(integerToString(clientFactory.getCharacterValidator().getSkillRankCost()) + " / ");
	}

	private CharacterGeneralSkillResult selectedGeneralSkill = null;
	private CharacterSkillResult selectedSkill = null;
	
	@Override
	public void selectGeneralSkill(CharacterGeneralSkillResult skill) {
		selectedGeneralSkill = skill;
		if (selectedGeneralSkill != null) {
			setSpecificSkillsList();
		}
	}

	@Override
	public void selectSkill(CharacterSkillResult skill) {
		selectedSkill = skill;
	}

	@Override
	public void replaceArmour(int idx) {
		CharacterEdit cev = clientFactory.getCharacterEdit();
		String [] armourTypes = new String[23];
		for (int i = 0; i < 23; i++) {
			if (ARMOUR[idx][0][i] + ARMOUR[idx][1][i] + ARMOUR[idx][2][i] > 0) { 
				armourTypes[i] = game.getArmour().get(idx).getName();
			} else {
				armourTypes[i] = game.getArmour().get(0).getName();
			}
		}
		cev.setArmour(armourTypes, ARMOUR[idx][0], ARMOUR[idx][1], ARMOUR[idx][2]);
		character.setArmourType(armourTypes);
		character.setSlashArmour(ARMOUR[idx][0]);
		character.setCrushArmour(ARMOUR[idx][1]);
		character.setPierceArmour(ARMOUR[idx][2]);
	}
	
	@Override
	public void setSlashArmour(int target, String v) {
		character.getSlashArmour()[target] = stringToInteger(v);
	}
	
	@Override
	public void setCrushArmour(int target, String v) {
		character.getCrushArmour()[target] = stringToInteger(v);
	}
	
	@Override
	public void setPierceArmour(int target, String v) {
		character.getPierceArmour()[target] = stringToInteger(v);
	}
	
	@Override
	public void uploadPortrait() {
		
		final UploadDialog ud = GWT.create(UploadDialog.class);
		ud.setText("Upload Character Portrait");
		ud.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clientFactory.getGameController().getUploadUrl();
			}
		});
		clientFactory.getEventBus().addHandler(UploadUrlEvent.TYPE, new UploadUrlEventHandler() {
			@Override
			public void onUrl(UploadUrlEvent event) {
				ud.submit(event.getUrl(), "portrait", character.getId());
			}
		});
		ud.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				clientFactory.getGameController().loadCharacter(character.getId());
				ud.hide();
			}
		});
		ud.center();

	}

	public static class CharacterEditPlace extends Place {

		public static enum CreateOrEdit {CreateCharacter, EditCharacter};

		private CreateOrEdit createOrEdit;
		private String id;
		
		public CharacterEditPlace(CreateOrEdit createOrEdit, String id) {
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