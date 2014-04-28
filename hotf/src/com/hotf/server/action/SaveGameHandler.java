package com.hotf.server.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.SaveGameAction;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.exception.AccessRightException;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.GameArmour;
import com.hotf.server.model.GameArtifact;
import com.hotf.server.model.GameWeapon;
import com.hotf.server.model.Place;

public class SaveGameHandler implements ActionHandler<SaveGameAction, GameResult>{

	private static final Logger log = Logger.getLogger(SaveGameHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetCharacterHandler getCharacterHandler;
	private SaveAccountHandler saveAccountHandler;
	private SaveCharacterHandler saveCharacterHandler;
	private SavePlaceHandler savePlaceHandler;
	private GetCharactersPlayableHandler getCharactersPlayableHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetPlacesByGameHandler getPlacesByGameHandler;
	
	public SaveGameHandler() {
	}
	
	@Override
	public GameResult execute(SaveGameAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SaveGameAction");

		GameResult gameResult = action.getGameResult();

		String id = gameResult.getId();

		Game game = null;

		//cache game
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.delete(id);

		Account account = getAccountHandler.getMyAccount();
		cache.delete(getAccountHandler.findGAEUser().getUserId());
		if (id == null) {
			game = new Game(gameResult.getTitle());
		} else {
			game = getGameHandler.getGame(id);
		}

		String gmcid = game.getGameMasterCharacterId();
		if (gmcid != null) {
			Character gm = getCharacterHandler.getCharacter(gmcid);
			if (!getGameHandler.getUpdatePermission(account, game, gm.getGameMasterAccountId())) {
				throw new AccessRightException();
			}
		}

		String desciption = gameResult.getDescription();
		game.setDescription(desciption == null ? "" : desciption);
		game.setTitle(gameResult.getTitle());

		List<GameWeaponResult> wrs = gameResult.getWeapons();
		if (wrs == null || wrs.size() <= 0) {
			initialiseWeapons(game, wrs);
		}
		List<GameWeapon> weapons = new ArrayList<GameWeapon>(wrs.size());
		for (GameWeaponResult wr : wrs) {
			GameWeapon w = new GameWeapon(game, wr.getName());
			w.setSlashDamage(wr.getSlashDamage());
			w.setCrushDamage(wr.getCrushDamage());
			w.setPierceDamage(wr.getPierceDamage());
			w.setDefence(wr.getDefence());
			w.setTwoHanded(wr.getTwoHanded());
			w.setMaxRange(wr.getMaxRange());
			w.setMinRange(wr.getMinRange());
			w.setShotMinRange(wr.getShotMinRange());
			w.setShotMaxRange(wr.getShotMaxRange());
			w.setStrengthRating(wr.getStrengthRating());
			w.setDamageRating(wr.getDamageRating());
			w.setInitiative(wr.getInitiative());
			w.setSkillNames(wr.getSkillNames().toArray(new String[] {}));
			weapons.add(w);
		}

		List<GameArmourResult> ars = gameResult.getArmour();
		if (ars == null || ars.size() <= 0) {
			initialiseArmour(game, ars);
		}
		List<GameArmour> armour = new ArrayList<GameArmour>(ars.size());
		for (GameArmourResult wr : ars) {
			GameArmour a = new GameArmour(game, wr.getName());
			a.setSlashDefence(wr.getSlashDefence());
			a.setCrushDefence(wr.getCrushDefence());
			a.setPierceDefence(wr.getPierceDefence());
			a.setInitiative(wr.getInitiative());
			armour.add(a);
		}
		
		List<GameArtifactResult> art = gameResult.getArtifacts();
		if (art == null || art.size() <= 0) {
			initialiseArtifacts(game, art);
		}
		List<GameArtifact> artifacts = new ArrayList<GameArtifact>(art.size());
		for (GameArtifactResult wr : art) {
			GameArtifact a = new GameArtifact(game, wr.getName());
			a.setEffect(wr.getEffect());
			a.setSkillNames(wr.getSkillNames().toArray(new String[] {}));
			artifacts.add(a);
		}

		save(game, weapons, armour, artifacts);

		cache.put(game.getId(), game);

		gameResult = getGameHandler.getResult(account, game);
		gameResult.setCharacters(getCharactersPlayableHandler.getCharactersResult(account, getCharactersPlayableHandler.getInterestedByName()));
		for (Place l : getPlacesByGameHandler.getByGame(game.getId())) {
			gameResult.getLocations().add(getPlaceHandler.getResult(account, game, l));
		}

		return gameResult;
		
	}

	/**
	 * Persist Game
	 */
	public void save(Game game, List<GameWeapon> weapons, List<GameArmour> armour, List<GameArtifact> artifacts) throws NotSignedInException {

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			// get date-time stamp
			Date today = new Date();

			if (game.getId() == null) {

				// persist Game, so that we have an id.
				game.setCreated(today);
				game.setUpdated(today);
				
				pm.currentTransaction().begin();
				game.setWeapons(weapons);
				game.setArmour(armour);
				game.setArtifacts(artifacts);
				pm.makePersistent(game);
				pm.currentTransaction().commit();

				// get users Account
				MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
				cache.delete(getAccountHandler.findGAEUser().getUserId());
				Account account = getAccountHandler.getMyAccount();

				// new games get a start location and a Game Master Character.
				// create start location
				Place startLocation = new Place(game.getId(), "Start");
				startLocation.setType("START");
				startLocation.setGameMasterAccountId(account.getId());
				savePlaceHandler.save(startLocation);

				// create game master at the start location, played by the user
				// creating the Game
				Character gameMaster = new Character(startLocation.getId(), account.getName());
				gameMaster.setPlayerAccountId(account.getId());
				gameMaster.setGameMasterAccountId(account.getId());
				gameMaster.setCharacterType("GM");
				saveCharacterHandler.save(gameMaster, null, false);

				// update game masters id back onto new Game
				pm.currentTransaction().begin();
				game.setGameMasterCharacterId(gameMaster.getId());
				pm.currentTransaction().commit();
				
				// get users Account
				cache.delete(getAccountHandler.findGAEUser().getUserId());
				account = getAccountHandler.getMyAccount();

				// set playing character to new gm
				account.setPlayingCharacterId(gameMaster.getId());
				saveAccountHandler.save(account);
				
				cache.put(getAccountHandler.findGAEUser().getUserId(), account);

			} else {

				// set date updated
				pm.currentTransaction().begin();
				game.setUpdated(today);
				pm.deletePersistentAll(game.getWeapons());
				game.getWeapons().clear();
				game.getWeapons().addAll(weapons);
				pm.makePersistentAll(weapons);
				pm.deletePersistentAll(game.getArmour());
				game.getArmour().clear();
				game.getArmour().addAll(armour);
				pm.makePersistentAll(armour);
				pm.deletePersistentAll(game.getArtifacts());
				game.getArtifacts().clear();
				game.getArtifacts().addAll(artifacts);
				pm.makePersistentAll(artifacts);
				pm.currentTransaction().commit();

				log.info("Persisted Game " + game.getTitle());

			}

		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			// roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;

		}
		
	}

	@Override
	public Class<SaveGameAction> getActionType() {
		return SaveGameAction.class;
	}

	@Override
	public void rollback(SaveGameAction action, GameResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getGameHandler the getGameHandler to set
	 */
	public void setGetGameHandler(GetGameHandler getGameHandler) {
		this.getGameHandler = getGameHandler;
	}

	/**
	 * @param saveCharacterHandler the saveCharacterHandler to set
	 */
	public void setSaveCharacterHandler(SaveCharacterHandler saveCharacterHandler) {
		this.saveCharacterHandler = saveCharacterHandler;
	}

	/**
	 * @param savePlaceHandler the saveLocationHandler to set
	 */
	public void setSaveLocationHandler(SavePlaceHandler savePlaceHandler) {
		this.savePlaceHandler = savePlaceHandler;
	}

	/**
	 * @param getCharactersPlayableHandler
	 */
	public void setGetCharactersHandler(GetCharactersPlayableHandler getCharactersPlayableHandler) {
		this.getCharactersPlayableHandler = getCharactersPlayableHandler;
	}

	/**
	 * @param getPlacesByGameHandler
	 */
	public void setGetLocationsHandler(GetPlacesByGameHandler getPlacesByGameHandler) {
		this.getPlacesByGameHandler = getPlacesByGameHandler;
	}

	/**
	 * @param getPlaceHandler
	 */
	public void setGetLocationHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

	public void setSaveAccountHandler(SaveAccountHandler saveAccountHandler) {
		this.saveAccountHandler = saveAccountHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

	private void initialiseWeapons(Game game, List<GameWeaponResult> weapons) {
		
		GameWeaponResult handStrike = new GameWeaponResult();
		handStrike.setName("Hand Strike");
		handStrike.setMinRange(0);
		handStrike.setMaxRange(2);
		handStrike.setShotMinRange(null);
		handStrike.setShotMaxRange(null);
		handStrike.setDefence(0);
		handStrike.setTwoHanded(false);
		handStrike.setSlashDamage(null);
		handStrike.setCrushDamage(-1);
		handStrike.setPierceDamage(null);
		handStrike.setDamageRating(null);
		handStrike.setStrengthRating(null);
		handStrike.setInitiative(0);
		handStrike.setSkillNames(Arrays.asList(new String[] {"Unarmed Combat", "Strike", "Grapple"}));
		weapons.add(handStrike);

		GameWeaponResult footStrike = new GameWeaponResult();
		footStrike.setName("Foot Strike");
		footStrike.setMinRange(0);
		footStrike.setMaxRange(3);
		footStrike.setShotMinRange(null);
		footStrike.setShotMaxRange(null);
		footStrike.setTwoHanded(false);
		footStrike.setDefence(null);
		footStrike.setSlashDamage(null);
		footStrike.setCrushDamage(-1);
		footStrike.setPierceDamage(null);
		footStrike.setDamageRating(null);
		footStrike.setStrengthRating(null);
		footStrike.setInitiative(0);
		footStrike.setSkillNames(Arrays.asList(new String[] {"Unarmed Combat", "Strike"}));
		weapons.add(footStrike);
		
		GameWeaponResult headbutt = new GameWeaponResult();
		headbutt.setName("Head Butt");
		headbutt.setMinRange(0);
		headbutt.setMaxRange(1);
		headbutt.setShotMinRange(null);
		headbutt.setShotMaxRange(null);
		headbutt.setTwoHanded(false);
		headbutt.setDefence(null);
		headbutt.setSlashDamage(null);
		headbutt.setCrushDamage(-1);
		headbutt.setPierceDamage(null);
		headbutt.setDamageRating(null);
		headbutt.setStrengthRating(null);
		headbutt.setInitiative(-1);
		headbutt.setSkillNames(Arrays.asList(new String[] {"Unarmed Combat", "Strike"}));
		weapons.add(headbutt);

		GameWeaponResult stone = new GameWeaponResult();
		stone.setName("Stone");
		stone.setMinRange(0);
		stone.setMaxRange(2);
		stone.setShotMinRange(5);
		stone.setShotMaxRange(160);
		stone.setTwoHanded(false);
		stone.setDefence(0);
		stone.setSlashDamage(null);
		stone.setCrushDamage(0);
		stone.setPierceDamage(null);
		stone.setDamageRating(null);
		stone.setStrengthRating(1);
		stone.setInitiative(0);
		stone.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Ranged Combat"}));
		weapons.add(stone);

		GameWeaponResult sling = new GameWeaponResult();
		sling.setName("Sling");
		sling.setMinRange(null);
		sling.setMaxRange(null);
		sling.setShotMinRange(5);
		sling.setShotMaxRange(320);
		sling.setTwoHanded(false);
		sling.setDefence(null);
		sling.setSlashDamage(null);
		sling.setCrushDamage(1);
		sling.setPierceDamage(0);
		sling.setDamageRating(1);
		sling.setStrengthRating(1);
		sling.setInitiative(0);
		sling.setSkillNames(Arrays.asList(new String[] {"Ranged Combat", "Hand Slings"}));
		weapons.add(sling);

		GameWeaponResult dagger = new GameWeaponResult();
		dagger.setName("Dagger");
		dagger.setMinRange(0);
		dagger.setMaxRange(3);
		dagger.setShotMinRange(5);
		dagger.setShotMaxRange(80);
		dagger.setTwoHanded(false);
		dagger.setDefence(0);
		dagger.setSlashDamage(0);
		dagger.setCrushDamage(null);
		dagger.setPierceDamage(1);
		dagger.setDamageRating(null);
		dagger.setStrengthRating(1);
		dagger.setInitiative(1);
		dagger.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Daggers", "Ranged Combat", "Thrown Dagger"}));
		weapons.add(dagger);

		GameWeaponResult knife = new GameWeaponResult();
		knife.setName("Knife");
		knife.setMinRange(0);
		knife.setMaxRange(2);
		knife.setShotMinRange(null);
		knife.setShotMaxRange(null);
		knife.setTwoHanded(false);
		knife.setDefence(0);
		knife.setSlashDamage(0);
		knife.setCrushDamage(null);
		knife.setPierceDamage(0);
		knife.setDamageRating(null);
		knife.setStrengthRating(0);
		knife.setInitiative(0);
		knife.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Daggers"}));
		weapons.add(knife);
		
		GameWeaponResult garrotte = new GameWeaponResult();
		garrotte.setName("Garrotte");
		garrotte.setMinRange(0);
		garrotte.setMaxRange(0);
		garrotte.setShotMinRange(null);
		garrotte.setShotMaxRange(null);
		garrotte.setTwoHanded(true);
		garrotte.setDefence(-2);
		garrotte.setSlashDamage(null);
		garrotte.setCrushDamage(1);
		garrotte.setPierceDamage(null);
		garrotte.setDamageRating(null);
		garrotte.setStrengthRating(0);
		garrotte.setInitiative(-2);
		garrotte.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Garrotte"}));
		weapons.add(garrotte);

		GameWeaponResult shortBow = new GameWeaponResult();
		shortBow.setName("Short Bow");
		shortBow.setMinRange(null);
		shortBow.setMaxRange(null);
		shortBow.setShotMinRange(5);
		shortBow.setShotMaxRange(600);
		shortBow.setTwoHanded(true);
		shortBow.setDefence(null);
		shortBow.setSlashDamage(null);
		shortBow.setCrushDamage(null);
		shortBow.setPierceDamage(2);
		shortBow.setDamageRating(2);
		shortBow.setStrengthRating(null);
		shortBow.setInitiative(2);
		shortBow.setSkillNames(Arrays.asList(new String[] {"Ranged Combat", "Bow"}));
		weapons.add(shortBow);
		
		GameWeaponResult longBow = new GameWeaponResult();
		longBow.setName("Long Bow");
		longBow.setMinRange(null);
		longBow.setMaxRange(null);
		longBow.setShotMinRange(5);
		longBow.setShotMaxRange(700);
		longBow.setTwoHanded(true);
		longBow.setDefence(null);
		longBow.setSlashDamage(null);
		longBow.setCrushDamage(null);
		longBow.setPierceDamage(2);
		longBow.setDamageRating(3);
		longBow.setStrengthRating(null);
		longBow.setInitiative(2);
		longBow.setSkillNames(Arrays.asList(new String[] {"Ranged Combat", "Bow"}));
		weapons.add(longBow);
		
		GameWeaponResult crossBow = new GameWeaponResult();
		crossBow.setName("Cross Bow");
		crossBow.setMinRange(3);
		crossBow.setMaxRange(300);
		crossBow.setShotMinRange(5);
		crossBow.setShotMaxRange(600);
		crossBow.setTwoHanded(true);
		crossBow.setDefence(null);
		crossBow.setSlashDamage(null);
		crossBow.setCrushDamage(null);
		crossBow.setPierceDamage(2);
		crossBow.setDamageRating(3);
		crossBow.setStrengthRating(null);
		crossBow.setInitiative(2);
		crossBow.setSkillNames(Arrays.asList(new String[] {"Ranged Combat", "Crossbows and Ballistae"}));
		weapons.add(crossBow);
		
		GameWeaponResult shortSword = new GameWeaponResult();
		shortSword.setName("Short Sword");
		shortSword.setMinRange(1);
		shortSword.setMaxRange(5);
		shortSword.setTwoHanded(false);
		shortSword.setDefence(1);
		shortSword.setSlashDamage(1);
		shortSword.setCrushDamage(0);
		shortSword.setPierceDamage(1);
		shortSword.setDamageRating(0);
		shortSword.setStrengthRating(4);
		shortSword.setInitiative(2);
		shortSword.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(shortSword);
		
		GameWeaponResult longSword = new GameWeaponResult();
		longSword.setName("Long Sword");
		longSword.setMinRange(2);
		longSword.setMaxRange(6);
		longSword.setTwoHanded(false);
		longSword.setDefence(1);
		longSword.setSlashDamage(1);
		longSword.setCrushDamage(1);
		longSword.setPierceDamage(1);
		longSword.setDamageRating(1);
		longSword.setStrengthRating(4);
		longSword.setInitiative(3);
		longSword.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(longSword);

		GameWeaponResult rapier = new GameWeaponResult();
		rapier.setName("Rapier");
		rapier.setMinRange(2);
		rapier.setMaxRange(6);
		rapier.setTwoHanded(false);
		rapier.setDefence(1);
		rapier.setSlashDamage(0);
		rapier.setCrushDamage(null);
		rapier.setPierceDamage(1);
		rapier.setDamageRating(null);
		rapier.setStrengthRating(2);
		rapier.setInitiative(4);
		rapier.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Agility"}));
		weapons.add(rapier);
		
		GameWeaponResult sabre = new GameWeaponResult();
		sabre.setName("Sabre");
		sabre.setMinRange(2);
		sabre.setMaxRange(6);
		sabre.setTwoHanded(false);
		sabre.setDefence(1);
		sabre.setSlashDamage(1);
		sabre.setCrushDamage(0);
		sabre.setPierceDamage(1);
		sabre.setDamageRating(0);
		sabre.setStrengthRating(2);
		sabre.setInitiative(4);
		sabre.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Agility"}));
		weapons.add(sabre);

		GameWeaponResult cutlass = new GameWeaponResult();
		cutlass.setName("Cutlass");
		cutlass.setMinRange(2);
		cutlass.setMaxRange(6);
		cutlass.setTwoHanded(false);
		cutlass.setDefence(1);
		cutlass.setSlashDamage(1);
		cutlass.setCrushDamage(0);
		cutlass.setPierceDamage(1);
		cutlass.setDamageRating(0);
		cutlass.setStrengthRating(4);
		cutlass.setInitiative(4);
		cutlass.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(cutlass);

		GameWeaponResult bastardSword = new GameWeaponResult();
		bastardSword.setName("Bastard Sword - One Handed");
		bastardSword.setMinRange(3);
		bastardSword.setMaxRange(7);
		bastardSword.setTwoHanded(false);
		bastardSword.setDefence(0);
		bastardSword.setSlashDamage(1);
		bastardSword.setCrushDamage(1);
		bastardSword.setPierceDamage(1);
		bastardSword.setDamageRating(1);
		bastardSword.setStrengthRating(4);
		bastardSword.setInitiative(4);
		bastardSword.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(bastardSword);
		
		GameWeaponResult bastardSwordTwo = new GameWeaponResult();
		bastardSwordTwo.setName("Bastard Sword - Two Handed");
		bastardSwordTwo.setMinRange(3);
		bastardSwordTwo.setMaxRange(7);
		bastardSwordTwo.setTwoHanded(true);
		bastardSwordTwo.setDefence(0);
		bastardSwordTwo.setSlashDamage(2);
		bastardSwordTwo.setCrushDamage(1);
		bastardSwordTwo.setPierceDamage(1);
		bastardSwordTwo.setDamageRating(2);
		bastardSwordTwo.setStrengthRating(4);
		bastardSwordTwo.setInitiative(4);
		bastardSwordTwo.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(bastardSwordTwo);
		
		GameWeaponResult twoHandedSword = new GameWeaponResult();
		twoHandedSword.setName("Two Handed Sword");
		twoHandedSword.setMinRange(3);
		twoHandedSword.setMaxRange(8);
		twoHandedSword.setTwoHanded(true);
		twoHandedSword.setDefence(0);
		twoHandedSword.setSlashDamage(2);
		twoHandedSword.setCrushDamage(2);
		twoHandedSword.setPierceDamage(1);
		twoHandedSword.setDamageRating(2);
		twoHandedSword.setStrengthRating(4);
		twoHandedSword.setInitiative(4);
		twoHandedSword.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Swords Requiring Strength"}));
		weapons.add(twoHandedSword);
		
		GameWeaponResult fransisca = new GameWeaponResult();
		fransisca.setName("Fransisca");
		fransisca.setMinRange(2);
		fransisca.setMaxRange(5);
		fransisca.setTwoHanded(false);
		fransisca.setShotMinRange(5);
		fransisca.setShotMaxRange(80);
		fransisca.setDefence(0);
		fransisca.setSlashDamage(2);
		fransisca.setCrushDamage(1);
		fransisca.setPierceDamage(null);
		fransisca.setDamageRating(2);
		fransisca.setStrengthRating(3);
		fransisca.setInitiative(2);
		fransisca.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Axes", "Ranged Combat", "Thrown Fransisca"}));
		weapons.add(fransisca);
		
		GameWeaponResult battleAxe = new GameWeaponResult();
		battleAxe.setName("Battle Axe");
		battleAxe.setMinRange(2);
		battleAxe.setMaxRange(5);
		battleAxe.setTwoHanded(false);
		battleAxe.setDefence(0);
		battleAxe.setSlashDamage(2);
		battleAxe.setCrushDamage(1);
		battleAxe.setPierceDamage(null);
		battleAxe.setDamageRating(2);
		battleAxe.setStrengthRating(4);
		battleAxe.setInitiative(2);
		battleAxe.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Axes"}));
		weapons.add(battleAxe);
		
		GameWeaponResult battleAxeTwo = new GameWeaponResult();
		battleAxeTwo.setName("Battle Axe - Two Handed");
		battleAxeTwo.setMinRange(3);
		battleAxeTwo.setMaxRange(5);
		battleAxeTwo.setTwoHanded(true);
		battleAxeTwo.setDefence(0);
		battleAxeTwo.setSlashDamage(3);
		battleAxeTwo.setCrushDamage(1);
		battleAxeTwo.setPierceDamage(null);
		battleAxeTwo.setDamageRating(2);
		battleAxeTwo.setStrengthRating(4);
		battleAxeTwo.setInitiative(2);
		battleAxeTwo.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Axes"}));
		weapons.add(battleAxeTwo);
		
		GameWeaponResult greatAxe = new GameWeaponResult();
		greatAxe.setName("Great Axe");
		greatAxe.setMinRange(3);
		greatAxe.setMaxRange(6);
		greatAxe.setTwoHanded(true);
		greatAxe.setDefence(-1);
		greatAxe.setSlashDamage(3);
		greatAxe.setCrushDamage(2);
		greatAxe.setPierceDamage(null);
		greatAxe.setDamageRating(3);
		greatAxe.setStrengthRating(5);
		greatAxe.setInitiative(2);
		greatAxe.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Axes"}));
		weapons.add(greatAxe);
		
		GameWeaponResult club = new GameWeaponResult();
		club.setName("Club");
		club.setMinRange(2);
		club.setMaxRange(5);
		club.setTwoHanded(false);
		club.setDefence(0);
		club.setSlashDamage(null);
		club.setCrushDamage(0);
		club.setPierceDamage(null);
		club.setDamageRating(1);
		club.setStrengthRating(4);
		club.setInitiative(2);
		club.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(club);

		GameWeaponResult warhammer = new GameWeaponResult();
		warhammer.setName("War Hammer");
		warhammer.setMinRange(2);
		warhammer.setMaxRange(5);
		warhammer.setTwoHanded(false);
		warhammer.setDefence(0);
		warhammer.setSlashDamage(null);
		warhammer.setCrushDamage(2);
		warhammer.setPierceDamage(1);
		warhammer.setDamageRating(2);
		warhammer.setStrengthRating(5);
		warhammer.setInitiative(1);
		warhammer.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(warhammer);
		
		GameWeaponResult mace = new GameWeaponResult();
		mace.setName("Mace");
		mace.setMinRange(2);
		mace.setMaxRange(5);
		mace.setTwoHanded(false);
		mace.setDefence(0);
		mace.setSlashDamage(null);
		mace.setCrushDamage(2);
		mace.setPierceDamage(null);
		mace.setDamageRating(2);
		mace.setStrengthRating(5);
		mace.setInitiative(1);
		mace.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(mace);
		
		GameWeaponResult flail = new GameWeaponResult();
		flail.setName("Flail");
		flail.setMinRange(4);
		flail.setMaxRange(6);
		flail.setTwoHanded(false);
		flail.setDefence(0);
		flail.setSlashDamage(null);
		flail.setCrushDamage(2);
		flail.setPierceDamage(null);
		flail.setDamageRating(2);
		flail.setStrengthRating(5);
		flail.setInitiative(2);
		flail.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Flails"}));
		weapons.add(flail);
		
		GameWeaponResult staff = new GameWeaponResult();
		staff.setName("Staff");
		staff.setMinRange(1);
		staff.setMaxRange(7);
		staff.setTwoHanded(false);
		staff.setDefence(1);
		staff.setSlashDamage(null);
		staff.setCrushDamage(0);
		staff.setPierceDamage(null);
		staff.setDamageRating(0);
		staff.setStrengthRating(1);
		staff.setInitiative(4);
		staff.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(staff);
		
		GameWeaponResult staffTwo = new GameWeaponResult();
		staffTwo.setName("Staff - Two Handed");
		staffTwo.setMinRange(1);
		staffTwo.setMaxRange(7);
		staffTwo.setTwoHanded(true);
		staffTwo.setDefence(1);
		staffTwo.setSlashDamage(null);
		staffTwo.setCrushDamage(1);
		staffTwo.setPierceDamage(null);
		staffTwo.setDamageRating(0);
		staffTwo.setStrengthRating(1);
		staffTwo.setInitiative(4);
		staffTwo.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(staffTwo);
		
		GameWeaponResult stick = new GameWeaponResult();
		stick.setName("Stick");
		stick.setMinRange(2);
		stick.setMaxRange(6);
		stick.setTwoHanded(false);
		stick.setDefence(0);
		stick.setSlashDamage(null);
		stick.setCrushDamage(-1);
		stick.setPierceDamage(null);
		stick.setDamageRating(0);
		stick.setStrengthRating(0);
		stick.setInitiative(4);
		stick.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Concussion Weapons"}));
		weapons.add(stick);
		
		GameWeaponResult javelin = new GameWeaponResult();
		javelin.setName("Javelin");
		javelin.setMinRange(2);
		javelin.setMaxRange(8);
		javelin.setTwoHanded(false);
		javelin.setShotMinRange(5);
		javelin.setShotMaxRange(160);
		javelin.setDefence(0);
		javelin.setSlashDamage(null);
		javelin.setCrushDamage(null);
		javelin.setPierceDamage(1);
		javelin.setDamageRating(2);
		javelin.setStrengthRating(0);
		javelin.setInitiative(6);
		javelin.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Spears Under 8'", "Ranged Combat", "Hand Thrown Shafted Weapons"}));
		weapons.add(javelin);
		
		GameWeaponResult spearSix = new GameWeaponResult();
		spearSix.setName("Spear (6')");
		spearSix.setMinRange(2);
		spearSix.setMaxRange(8);
		spearSix.setTwoHanded(false);
		spearSix.setShotMinRange(5);
		spearSix.setShotMaxRange(80);
		spearSix.setDefence(0);
		spearSix.setSlashDamage(null);
		spearSix.setCrushDamage(-1);
		spearSix.setPierceDamage(1);
		spearSix.setDamageRating(2);
		spearSix.setStrengthRating(0);
		spearSix.setInitiative(6);
		spearSix.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Spears Under 8'", "Ranged Combat", "Hand Thrown Shafted Weapons"}));
		weapons.add(spearSix);
				
		GameWeaponResult spearEight = new GameWeaponResult();
		spearEight.setName("Spear (8')");
		spearEight.setMinRange(3);
		spearEight.setMaxRange(10);
		spearEight.setTwoHanded(false);
		spearEight.setDefence(0);
		spearEight.setSlashDamage(null);
		spearEight.setCrushDamage(-1);
		spearEight.setPierceDamage(1);
		spearEight.setDamageRating(2);
		spearEight.setStrengthRating(0);
		spearEight.setInitiative(8);
		spearEight.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Spears 8' or Over"}));
		weapons.add(spearEight);
		
		GameWeaponResult spearTen = new GameWeaponResult();
		spearTen.setName("Spear (10')");
		spearTen.setMinRange(4);
		spearTen.setMaxRange(12);
		spearTen.setDefence(0);
		spearTen.setTwoHanded(true);
		spearTen.setSlashDamage(null);
		spearTen.setCrushDamage(-1);
		spearTen.setPierceDamage(1);
		spearTen.setDamageRating(2);
		spearTen.setStrengthRating(0);
		spearTen.setInitiative(9);
		spearTen.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Spears 8' or Over"}));
		weapons.add(spearTen);
		
		GameWeaponResult lance = new GameWeaponResult();
		lance.setName("Lance");
		lance.setMinRange(6);
		lance.setMaxRange(12);
		lance.setTwoHanded(false);
		lance.setDefence(-1);
		lance.setSlashDamage(null);
		lance.setCrushDamage(0);
		lance.setPierceDamage(2);
		lance.setDamageRating(2);
		lance.setStrengthRating(1);
		lance.setInitiative(8);
		lance.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Spears 8' or Over"}));
		weapons.add(lance);
		
		GameWeaponResult lanceCharging = new GameWeaponResult();
		lanceCharging.setName("Lance - Charging");
		lanceCharging.setMinRange(6);
		lanceCharging.setMaxRange(12);
		lanceCharging.setTwoHanded(false);
		lanceCharging.setDefence(-1);
		lanceCharging.setSlashDamage(null);
		lanceCharging.setCrushDamage(0);
		lanceCharging.setPierceDamage(3);
		lanceCharging.setDamageRating(3);
		lanceCharging.setStrengthRating(1);
		lanceCharging.setInitiative(8);
		lanceCharging.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Lances From Horseback"}));
		weapons.add(lanceCharging);
		
		GameWeaponResult halberd = new GameWeaponResult();
		halberd.setName("Halberd");
		halberd.setMinRange(3);
		halberd.setMaxRange(10);
		halberd.setTwoHanded(true);
		halberd.setDefence(1);
		halberd.setSlashDamage(2);
		halberd.setCrushDamage(1);
		halberd.setPierceDamage(1);
		halberd.setDamageRating(2);
		halberd.setStrengthRating(3);
		halberd.setInitiative(7);
		halberd.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Polearms"}));
		weapons.add(halberd);
		
		GameWeaponResult targe = new GameWeaponResult();
		targe.setName("Targe");
		targe.setMinRange(0);
		targe.setMaxRange(2);
		targe.setTwoHanded(false);
		targe.setDefence(1);
		targe.setSlashDamage(null);
		targe.setCrushDamage(-1);
		targe.setPierceDamage(null);
		targe.setDamageRating(null);
		targe.setStrengthRating(4);
		targe.setInitiative(-1);
		targe.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Shields"}));
		weapons.add(targe);
		
		GameWeaponResult mediumShield = new GameWeaponResult();
		mediumShield.setName("Medium Shield");
		mediumShield.setMinRange(0);
		mediumShield.setMaxRange(2);
		mediumShield.setTwoHanded(false);
		mediumShield.setDefence(2);
		mediumShield.setSlashDamage(null);
		mediumShield.setCrushDamage(-1);
		mediumShield.setPierceDamage(null);
		mediumShield.setDamageRating(null);
		mediumShield.setStrengthRating(5);
		mediumShield.setInitiative(-1);
		mediumShield.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Shields"}));
		weapons.add(mediumShield);
		
		GameWeaponResult largeShield = new GameWeaponResult();
		largeShield.setName("Large Shield");
		largeShield.setMinRange(0);
		largeShield.setMaxRange(2);
		largeShield.setTwoHanded(false);
		largeShield.setDefence(3);
		largeShield.setSlashDamage(null);
		largeShield.setCrushDamage(-1);
		largeShield.setPierceDamage(null);
		largeShield.setDamageRating(null);
		largeShield.setStrengthRating(5);
		largeShield.setInitiative(-1);
		largeShield.setSkillNames(Arrays.asList(new String[] {"Close Combat", "Shields"}));
		weapons.add(largeShield);
		
	}
	
	private void initialiseArmour(Game game, List<GameArmourResult> armour) {
		GameArmourResult none = new GameArmourResult();
		none.setName("No Armour");
		none.setSlashDefence(0);
		none.setCrushDefence(0);
		none.setPierceDefence(0);
		none.setInitiative(0);
		armour.add(none);
		GameArmourResult padded = new GameArmourResult();
		padded.setName("Padding");
		padded.setSlashDefence(1);
		padded.setCrushDefence(1);
		padded.setPierceDefence(0);
		padded.setInitiative(0);
		armour.add(padded);
		GameArmourResult cuirbouilli = new GameArmourResult();
		cuirbouilli.setName("Cuir Bouilli");
		cuirbouilli.setSlashDefence(1);
		cuirbouilli.setCrushDefence(1);
		cuirbouilli.setPierceDefence(1);
		cuirbouilli.setInitiative(-1);
		armour.add(cuirbouilli);
		GameArmourResult studdedleather = new GameArmourResult();
		studdedleather.setName("Studded Leather");
		studdedleather.setSlashDefence(1);
		studdedleather.setCrushDefence(1);
		studdedleather.setPierceDefence(1);
		studdedleather.setInitiative(-1);
		armour.add(studdedleather);
		GameArmourResult ringmail = new GameArmourResult();
		ringmail.setName("Ring Mail");
		ringmail.setSlashDefence(2);
		ringmail.setCrushDefence(1);
		ringmail.setPierceDefence(1);
		ringmail.setInitiative(-1);
		armour.add(ringmail);
		GameArmourResult scale = new GameArmourResult();
		scale.setName("Scale/Jazerant");
		scale.setSlashDefence(3);
		scale.setCrushDefence(0);
		scale.setPierceDefence(2);
		scale.setInitiative(-2);
		armour.add(scale);
		GameArmourResult mail = new GameArmourResult();
		mail.setName("Chain Mail");
		mail.setSlashDefence(3);
		mail.setCrushDefence(0);
		mail.setPierceDefence(2);
		mail.setInitiative(-1);
		armour.add(mail);
		GameArmourResult splint = new GameArmourResult();
		splint.setName("Splint");
		splint.setSlashDefence(3);
		splint.setCrushDefence(1);
		splint.setPierceDefence(2);
		splint.setInitiative(-2);
		armour.add(splint);
		GameArmourResult plate = new GameArmourResult();
		plate.setName("Plate");
		plate.setSlashDefence(3);
		plate.setCrushDefence(2);
		plate.setPierceDefence(3);
		plate.setInitiative(-3);
		armour.add(plate);
	}

	private void initialiseArtifacts(Game game, List<GameArtifactResult> artifacts) {
		
		GameArtifactResult lockPicks = new GameArtifactResult();
		lockPicks.setName("Lock Picks");
		lockPicks.setEffect(1);
		lockPicks.setSkillNames(Arrays.asList(new String[] {"Pick Lock"}));
		artifacts.add(lockPicks);

	}

}
