package com.hotf.server.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.GetGameAction;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameGeneralSkillResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameSkillResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Game;
import com.hotf.server.model.GameArmour;
import com.hotf.server.model.GameArtifact;
import com.hotf.server.model.GameWeapon;

public class GetGameHandler implements ActionHandler<GetGameAction, GameResult> {

	private static final Logger log = Logger.getLogger(GetGameHandler.class.getName());
	private GetAccountHandler getAccountHandler;
	private GetCharacterHandler getCharacterHandler;

	public GetGameHandler() {
	}
	
	@Override
	public GameResult execute(GetGameAction action, ExecutionContext context) throws DispatchException {
		Account account = getAccountHandler.getMyAccount();
		Game game = getGame(action.getId());
		return getResult(account, game);
	}

	public GameResult getResult(Account account, Game game) {
		
		GameResult gameResult = new GameResult();
		
		gameResult.setDescription(game.getDescription());
		gameResult.setGameMasterCharacterId(game.getGameMasterCharacterId());
		gameResult.setId(game.getId());
		gameResult.setTitle(game.getTitle());
		gameResult.setCreated(game.getCreated());
		gameResult.setUpdated(game.getUpdated());
		com.hotf.server.model.Character gm = getCharacterHandler.getCharacter(game.getGameMasterCharacterId());
		gameResult.setOwner(gm.getName());
		gameResult.setUpdatePermission(getUpdatePermission(account, game, gm.getGameMasterAccountId()));

		List<GameWeaponResult> weapons = new ArrayList<GameWeaponResult>();
		for (GameWeapon w : game.getWeapons()) {
			GameWeaponResult wr = new GameWeaponResult();
			wr.setName(w.getName());
			wr.setSlashDamage(w.getSlashDamage());
			wr.setCrushDamage(w.getCrushDamage());
			wr.setPierceDamage(w.getPierceDamage());
			wr.setDefence(w.getDefence());
			wr.setTwoHanded(w.getTwoHanded());
			wr.setMaxRange(w.getMaxRange());
			wr.setMinRange(w.getMinRange());
			wr.setShotMaxRange(w.getShotMaxRange());
			wr.setShotMinRange(w.getShotMinRange());
			wr.setDamageRating(w.getDamageRating());
			wr.setStrengthRating(w.getStrengthRating());
			wr.setInitiative(w.getInitiative());
			wr.setSkillNames(new ArrayList<String>(Arrays.asList(w.getSkillNames())));
			weapons.add(wr);
		}
		gameResult.setWeapons(weapons);

		List<GameArmourResult> armour = new ArrayList<GameArmourResult>();
		for (GameArmour a : game.getArmour()) {
			GameArmourResult ar = new GameArmourResult();
			ar.setName(a.getName());
			ar.setSlashDefence(a.getSlashDefence());
			ar.setCrushDefence(a.getCrushDefence());
			ar.setPierceDefence(a.getPierceDefence());
			ar.setInitiative(a.getInitiative());
			armour.add(ar);
		}
		gameResult.setArmour(armour);

		List<GameArtifactResult> artifact = new ArrayList<GameArtifactResult>();
		for (GameArtifact a : game.getArtifacts()) {
			GameArtifactResult ar = new GameArtifactResult();
			ar.setName(a.getName());
			ar.setEffect(a.getEffect());
			ar.setSkillNames(new ArrayList<String>(Arrays.asList(a.getSkillNames())));
			artifact.add(ar);
		}
		gameResult.setArtifacts(artifact);

		gameResult.setGeneralSkills(new ArrayList<GameGeneralSkillResult>(getGeneralSkills().values()));

		return gameResult;
	}

	public Map<String, GameGeneralSkillResult> getGeneralSkills() {
		if (generalSkills == null) {
			initialiseSkills();
		}
		return generalSkills;
	}
	
	/**
	 * @param gameId to find
	 * @return Game
	 */
	public Game getGame(String gameId) {

		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		if (cache.contains(gameId)) {
			log.info("Getting Game from Memcache");
			try {
				return (Game)cache.get(gameId);
			} catch (InvalidValueException e) {
				log.warning("Clearing cache");
				cache.clearAll();
			}
		}

		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			log.info("Getting game from Datastore by id");
			Game game = pm.getObjectById(Game.class, gameId);
			cache.put(gameId, game);
			return game;

		} catch (RuntimeException t) {

			log.severe(t.getMessage());

			// roll-back transactions and re-throw
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;

		}

	}

	public GameWeapon getWeaponByName(Game game, String name) {
		
		if (name != null) {
			for (GameWeapon w : game.getWeapons()) {
				if (name.equals(w.getName())) {
					return w;
				}
			}
		}

		return null;
	
	}
	
	public GameArmour getArmourByName(Game game, String name) {
		
		if (name != null) {
			for (GameArmour a : game.getArmour()) {
				if (name.equals(a.getName())) {
					return a;
				}
			}
		}

		return null;
	
	}

	/**
	 * @return true if the users has update permission, otherwise return false
	 */
	public Boolean getUpdatePermission(Account account, Game game, String gmAccountId) {

		boolean updatePermission = false;

		if (gmAccountId != null && account != null && gmAccountId.equals(account.getId())) {
			
			updatePermission = true;

		}

		//return the update permission
		return updatePermission;

	}
	
	@Override
	public Class<GetGameAction> getActionType() {
		return GetGameAction.class;
	}

	@Override
	public void rollback(GetGameAction action, GameResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	/**
	 * @param getCharacterHandler the getCharacterHandler to set
	 */
	public void setGetCharacterHandler(GetCharacterHandler getCharacterHandler) {
		this.getCharacterHandler = getCharacterHandler;
	}

	private Map<String, GameGeneralSkillResult> generalSkills;
	private Map<String, GameSkillResult> skills;

	private GameGeneralSkillResult addGeneralSkill(String name, Map<String, GameGeneralSkillResult> generalSkills) {
		
		GameGeneralSkillResult gs = new GameGeneralSkillResult();
		gs.setName(name);
		getGeneralSkills().put(gs.getName(), gs);
		List<GameSkillResult> skills = new ArrayList<GameSkillResult>();
		gs.setSkills(skills);
		return gs;

	}
	
	private void addSkill(GameGeneralSkillResult generalSkill, String name, String attr) {
		
		GameSkillResult s = new GameSkillResult();
		s.setGeneralSkill(generalSkill);
		s.setName(name);
		s.setAttribute(attr);
		skills.put(s.getName(), s);
		generalSkill.getSkills().add(s);

	}

	public GameGeneralSkillResult getGeneralSkill(String name) {
		return getGeneralSkills().get(name);
	}

	public GameSkillResult getSkill(String name) {
		return skills.get(name);
	}

	private void initialiseSkills() {

		generalSkills = new HashMap<String, GameGeneralSkillResult>();
		skills = new HashMap<String, GameSkillResult>();
		
		GameGeneralSkillResult closeCombat = addGeneralSkill("Close Combat", generalSkills);
		addSkill(closeCombat, "Swords Requiring Strength", "Vigor");
		addSkill(closeCombat, "Swords Requiring Agility", "Vigor");
		addSkill(closeCombat, "Daggers", "Vigor");

		addSkill(closeCombat, "Concussion Weapons","Vigor");
		addSkill(closeCombat, "Flails", "Vigor");
		addSkill(closeCombat, "Axes", "Vigor");
		addSkill(closeCombat, "Spears Under 8'", "Vigor");
		addSkill(closeCombat, "Spears 8' or Over", "Vigor");
		addSkill(closeCombat, "Polearms", "Vigor");
		addSkill(closeCombat, "Lances From Horseback", "Vigor");
		addSkill(closeCombat, "Shields", "Vigor");
		addSkill(closeCombat, "Dodge", "Vigor");
		addSkill(closeCombat, "Weapon Purchase", "Wit");
		addSkill(closeCombat, "Intimidation", "Glamour");

		GameGeneralSkillResult rangedCombat = addGeneralSkill("Ranged Combat", generalSkills);
		addSkill(rangedCombat, "Hand Thrown Shafted Weapons", "Vigor");
		addSkill(rangedCombat, "Bow", "Vigor");
		addSkill(rangedCombat, "Thrown Dagger", "Vigor");
		addSkill(rangedCombat, "Thrown Fransisca", "Vigor");
		addSkill(rangedCombat, "Hand Slings", "Vigor");
		addSkill(rangedCombat, "Crossbows and Ballistae", "Vigor");
		addSkill(rangedCombat, "Ranged Weapon Purchase", "Wit");

		GameGeneralSkillResult larceny = addGeneralSkill("Larceny", generalSkills);
		addSkill(larceny, "Cut Purse", "Wit");
		addSkill(larceny, "Climbing", "Vigor");
		addSkill(larceny, "Disguise", "Wit");
		addSkill(larceny, "Pick Lock", "Wit");
		addSkill(larceny, "Valuation", "Wit");
		addSkill(larceny, "Stealth", "Wit");
		addSkill(larceny, "Bluff", "Glamour");
		addSkill(larceny, "Search", "Wit");
		addSkill(larceny, "Underworld Connections", "Glamour");
		addSkill(larceny, "Awareness Urban", "Wit");
		
		GameGeneralSkillResult hunting = addGeneralSkill("Hunting", generalSkills);
		addSkill(hunting, "Tracking", "Wit");
		addSkill(hunting, "Stalking - Wilderness", "Wit");
		addSkill(hunting, "Trap Setting", "Wit");
		addSkill(hunting, "Land Navigation", "Wit");
		addSkill(hunting, "Bow", "Vigor");
		addSkill(hunting, "Crossbows and Ballistae", "Vigor");
		addSkill(hunting, "Hand Thrown Shafted Weapons", "Vigor");
		addSkill(hunting, "Spears Under 8'", "Vigor");
		addSkill(hunting, "Herb Lore", "Wit");
		addSkill(hunting, "Weather Lore", "Wit");
		addSkill(hunting, "Hand Slings", "Vigor");
		addSkill(hunting, "Skinning and Butchery", "Vigor");
		addSkill(hunting, "Awareness - Wilderness", "Wit");

		GameGeneralSkillResult espionage = addGeneralSkill("Espionage", generalSkills);
		addSkill(espionage, "Stealth", "Wit");
		addSkill(espionage, "Stalking - Urban", "Wit");
		addSkill(espionage, "Pick Lock", "Wit");
		addSkill(espionage, "Bluff", "Glamour");
		addSkill(espionage, "Pursuade", "Glamour");
		addSkill(espionage, "Etiquette", "Glamour");
		addSkill(espionage, "Languages", "Wit");
		addSkill(espionage, "Literacy", "Wit");
		addSkill(espionage, "Map Making", "Wit");
		addSkill(espionage, "Search", "Wit");
		addSkill(espionage, "Poisons", "Wit");
		addSkill(espionage, "Forgery", "Wit");
		addSkill(espionage, "Underworld Connections - Urban", "Glamour");
		addSkill(espionage, "Awareness - Urban", "Wit");

		GameGeneralSkillResult boatCraft = addGeneralSkill("Boat Craft", generalSkills);
		addSkill(boatCraft, "Sailing", "Vigor");
		addSkill(boatCraft, "Rowing", "Vigor");
		addSkill(boatCraft, "Sea Navigation", "Wit");
		addSkill(boatCraft, "Rope Craft", "Wit");
		addSkill(boatCraft, "Boat Building", "Wit");
		addSkill(boatCraft, "Swimming", "Vigor");
		addSkill(boatCraft, "Weather Lore", "Wit");
		addSkill(boatCraft, "Boat Purchase", "Wit");

		GameGeneralSkillResult assassination = addGeneralSkill("Assassination", generalSkills);
		addSkill(assassination, "Disguise", "Wit");
		addSkill(assassination, "Climbing", "Vigor");
		addSkill(assassination, "Garrotte", "Vigor");
		addSkill(assassination, "Poisons", "Wit");
		addSkill(assassination, "Bow", "Vigor");
		addSkill(assassination, "Crossbows and Ballistae", "Vigor");
		addSkill(assassination, "Daggers", "Vigor");
		addSkill(assassination, "Thrown Dagger", "Vigor");
		addSkill(assassination, "Stalking - Urban", "Wit");
		addSkill(assassination, "Stealth", "Wit");
		addSkill(assassination, "Awareness - Urban", "Wit");
		
		GameGeneralSkillResult horsemanship = addGeneralSkill("Horsemanship", generalSkills);
		addSkill(horsemanship, "Riding", "Vigor");
		addSkill(horsemanship, "Horse Care", "Wit");
		addSkill(horsemanship, "Horse Purchase", "Wit");

		GameGeneralSkillResult performance = addGeneralSkill("Performance", generalSkills);
		addSkill(performance, "Oratory", "Glamour");
		addSkill(performance, "Juggling", "Vigor");
		addSkill(performance, "Escapology", "Vigor");
		addSkill(performance, "Acrobatics", "Vigor");
		addSkill(performance, "Musical Instrament", "Wit");
		addSkill(performance, "Bluff", "Glamour");
		addSkill(performance, "Disguise", "Glamour");
		addSkill(performance, "Bluff", "Glamour");

		GameGeneralSkillResult command = addGeneralSkill("Command", generalSkills);
		addSkill(command, "Leadership", "Glamour");
		addSkill(command, "Oratory", "Glamour");
		addSkill(command, "Siege Craft", "Wit");
		addSkill(command, "Drill", "Wit");
		addSkill(command, "Logistics", "Wit");
		addSkill(command, "Intimidation", "Glamour");
		addSkill(command, "Awareness - Military", "Wit");

		GameGeneralSkillResult scholarship = addGeneralSkill("Scholarship", generalSkills);
		addSkill(scholarship, "Languages", "Wit");
		addSkill(scholarship, "Dead Languages", "Wit");
		addSkill(scholarship, "Magic Lore", "Wit");
		addSkill(scholarship, "Herb Lore", "Wit");
		addSkill(scholarship, "Literacy", "Wit");
		addSkill(scholarship, "Healing", "Wit");
		addSkill(scholarship, "Alchemy", "Wit");
		addSkill(scholarship, "Map Making", "Wit");
		addSkill(scholarship, "History", "Wit");

		GameGeneralSkillResult shamanism = addGeneralSkill("Shamanism", generalSkills);
		addSkill(shamanism, "Herb Lore", "Wit");
		addSkill(shamanism, "Magic Lore", "Wit");
		addSkill(shamanism, "Healing", "Wit");
		addSkill(shamanism, "Weather Lore", "Wit");
		addSkill(shamanism, "Land Navigation", "Wit");
		addSkill(shamanism, "Tracking", "Wit");
		addSkill(shamanism, "Animal Lore", "Wit");
		addSkill(shamanism, "Awareness - Wilderness", "Wit");
		addSkill(shamanism, "Stalking - Wilderness", "Wit");
		addSkill(shamanism, "Oratory", "Glamour");

		GameGeneralSkillResult unarmedCombat = addGeneralSkill("Unarmed Combat", generalSkills);
		addSkill(unarmedCombat, "Strike", "Vigor");
		addSkill(unarmedCombat, "Grapple", "Vigor");
		addSkill(unarmedCombat, "Dodge", "Vigor");
		addSkill(unarmedCombat, "Intimidation", "Glamour");
		
		GameGeneralSkillResult artifice = addGeneralSkill("Artifice", generalSkills);
		addSkill(artifice, "Alchemy", "Wit");
		addSkill(artifice, "Weapon Purchase", "Wit");
		addSkill(artifice, "Pick Lock", "Wit");
		addSkill(artifice, "Trap Setting", "Wit");
		addSkill(artifice, "Rope Craft", "Wit");
		addSkill(artifice, "Siege Craft", "Wit");
		addSkill(artifice, "Carpentry", "Wit");
		addSkill(artifice, "Metal Working", "Wit");
		addSkill(artifice, "Stone Mason", "Wit");
		addSkill(artifice, "Armourer", "Wit");

		GameGeneralSkillResult pathfinding = addGeneralSkill("Pathfinding", generalSkills);
		addSkill(pathfinding, "Talent", "Wit");
		addSkill(pathfinding, "Land Navigation", "Wit");
		addSkill(pathfinding, "Sea Navigation", "Wit");
		addSkill(pathfinding, "Tracking", "Wit");
		addSkill(pathfinding, "Stalking", "Wit");
		addSkill(pathfinding, "Awareness - Urban and Wilderness", "Wit");
		addSkill(pathfinding, "Stealth", "Wit");

		GameGeneralSkillResult pathfinderArtificer = addGeneralSkill("Pathfinder Artificer", generalSkills);
		addSkill(pathfinderArtificer, "Alchemy", "Wit");
		addSkill(pathfinderArtificer, "Pick Lock", "Wit");
		addSkill(pathfinderArtificer, "Trap Setting", "Wit");
		addSkill(pathfinderArtificer, "Rope Craft", "Wit");
		addSkill(pathfinderArtificer, "Siege Craft", "Wit");
		addSkill(pathfinderArtificer, "Dead Languages", "Wit");
		addSkill(pathfinderArtificer, "Literacy", "Wit");
		addSkill(pathfinderArtificer, "Magic Lore", "Wit");
		addSkill(pathfinderArtificer, "Carpentry", "Wit");
		addSkill(pathfinderArtificer, "Metal Working", "Wit");
		addSkill(pathfinderArtificer, "Stone Mason", "Wit");
		addSkill(pathfinderArtificer, "Armourer", "Wit");

		GameGeneralSkillResult trading = addGeneralSkill("Trading", generalSkills);
		addSkill(trading, "Languages", "Wit");
		addSkill(trading, "Assess Value", "Wit");
		addSkill(trading, "Purchase", "Wit");
		addSkill(trading, "Persuade", "Glamour");
		addSkill(trading, "Oratory", "Glamour");
		addSkill(trading, "Bluff", "Glamour");
		addSkill(trading, "Trade Connections", "Glamour");
		addSkill(trading, "Ministry", "Wit");
		addSkill(trading, "Rituals", "Wit");
		addSkill(trading, "Dead Languages", "Wit");
		addSkill(trading, "Literacy", "Wit");
		addSkill(trading, "Magic Lore", "Wit");
		addSkill(trading, "Oratory", "Glamour");
		addSkill(trading, "Leadership", "Glamour");
		addSkill(trading, "Etiquette", "Glamour");

		GameGeneralSkillResult nobility = addGeneralSkill("Nobility", generalSkills);
		addSkill(nobility, "Languages", "Wit");
		addSkill(nobility, "Literacy", "Wit");
		addSkill(nobility, "Etiquette", "Wit");
		addSkill(nobility, "Arts", "Wit");
		addSkill(nobility, "Leadership", "Glamour");
		
		GameGeneralSkillResult diplomacy = addGeneralSkill("Diplomacy", generalSkills);
		addSkill(diplomacy, "Oratory", "Glamour");
		addSkill(diplomacy, "Connections", "Glamour");
		addSkill(diplomacy, "Riding", "Vigor");

		GameGeneralSkillResult administration = addGeneralSkill("Administration", generalSkills);
		addSkill(administration, "Persuade", "Glamour");
		addSkill(administration, "Bluff", "Glamour");
		addSkill(administration, "Etiquette", "Glamour");
		addSkill(administration, "Administrative Connections", "Glamour");
		addSkill(administration, "Literacy", "Wit");
		addSkill(administration, "Languages", "Wit");
		addSkill(administration, "Logistics", "Wit");
		addSkill(administration, "Forgery", "Wit");
	}

}
