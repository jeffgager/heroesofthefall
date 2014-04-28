package com.hotf.server.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.SaveCharacterAction;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.action.validators.CharacterValidator;
import com.hotf.client.exception.AccessRightException;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.EmailService;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.CharacterGeneralSkill;
import com.hotf.server.model.CharacterSkill;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;
import com.hotf.server.model.Post;

public class SaveCharacterHandler implements ActionHandler<SaveCharacterAction, CharacterResult> {

	private static final Logger log = Logger.getLogger(SaveCharacterHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetCharacterHandler getCharacterHandler;
	private GetCharactersPlayableHandler getCharactersPlayableHandler;
	private GetCharactersByPlaceHandler getCharactersByPlaceHandler;
	private GetPlaceHandler getPlaceHandler;
	private GetGameHandler getGameHandler;
	private SavePostHandler savePostHandler;
	private CharacterValidator characterValidator;
	
	public SaveCharacterHandler() {
	}
	
	@Override
	public CharacterResult execute(SaveCharacterAction action, ExecutionContext context) throws DispatchException {

		log.info("Handle SaveCharacterAction");
		
		Account account = getAccountHandler.getMyAccount();
		CharacterResult characterResult = action.getCharacterResult();
		characterValidator.validate(characterResult);

		//cache Character
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		Character character = null;
		String id = characterResult.getId();

		if (id == null) {
			character = new Character(characterResult.getLocationId(), characterResult.getName());
		} else if (action.isDuplicate()) {
			String locid = getCharacterHandler.getCharacter(account.getPlayingCharacterId()).getLocationId();
			character = new Character(locid, characterResult.getName());
			character.setPortrait(getCharacterHandler.getCharacter(id).getPortrait());
		} else {
			cache.delete(id);
			character = getCharacterHandler.getCharacter(id);
			character.setName(characterResult.getName());
			character.setLocationId(characterResult.getLocationId());
		}

		String desciption = characterResult.getDescription();
		character.setDescription(desciption == null ? "" : desciption);
		String sheet = characterResult.getSheet();
		character.setSheet(sheet == null ? "" : sheet);
		character.setStatus(characterResult.getStatus());
		String playerid = characterResult.getPlayerAccountId();
		character.setPlayerAccountId(playerid);
		character.setTokenHidden(characterResult.getTokenHidden() == null ? false : characterResult.getTokenHidden());
		if (action.isDuplicate()) {
			character.setTokenX(characterResult.getTokenX().intValue() + 40);
		} else {
			character.setTokenX(characterResult.getTokenX().intValue());
		}
		character.setTokenY(characterResult.getTokenY().intValue());
		character.setCharacterPoints(characterResult.getCharacterPoints());
		character.setVigor(characterResult.getVigor());
		character.setMettle(characterResult.getMettle());
		character.setWit(characterResult.getWit());
		character.setGlamour(characterResult.getGlamour());
		character.setSpirit(characterResult.getSpirit());
		character.setWyrd(characterResult.getWyrd());
		character.setAge(characterResult.getAge());
		character.setHanded(characterResult.getHanded());
		character.setLeftHand(characterResult.getLeftHand());
		character.setRightHand(characterResult.getRightHand());
		character.setDefence(characterResult.getDefence());
		character.setSkillRanks(characterResult.getSkillRanks());
		character.setArmourType(characterResult.getArmourType());
		character.setSlashArmour(characterResult.getSlashArmour());
		character.setCrushArmour(characterResult.getCrushArmour());
		character.setPierceArmour(characterResult.getPierceArmour());
		character.setWeapons(characterResult.getWeapons());
		character.setArmour(characterResult.getArmour());
		character.setArtifacts(characterResult.getArtifacts());
		character.setLeftTargetCharacterId(characterResult.getLeftTargetCharacterId());
		character.setRightTargetCharacterId(characterResult.getRightTargetCharacterId());
		character.setLeftTargetArea(characterResult.getLeftTargetArea());
		character.setRightTargetArea(characterResult.getRightTargetArea());
		character.setLeftTargetStrike(characterResult.getLeftTargetStrike());
		character.setRightTargetStrike(characterResult.getRightTargetStrike());
		List<CharacterGeneralSkillResult> generalSkillResults = characterResult.getGeneralSkills();
		List<CharacterGeneralSkill> generalSkills = new ArrayList<CharacterGeneralSkill>(generalSkillResults.size());
		for (CharacterGeneralSkillResult generalSkillResult : generalSkillResults) {
			CharacterGeneralSkill generalSkill = new CharacterGeneralSkill(character, generalSkillResult.getGameGeneralSkill().getName());
			generalSkill.setRanks(generalSkillResult.getRanks());
			generalSkill.setLevel(generalSkillResult.getLevel());
			generalSkill.setModifier(generalSkillResult.getModifier());
			generalSkills.add(generalSkill);
			List<CharacterSkill> skills = new ArrayList<CharacterSkill>(generalSkillResult.getSkills().size());;
			for (CharacterSkillResult skillResult : generalSkillResult.getSkills()) {
				CharacterSkill skill = new CharacterSkill(generalSkill, skillResult.getGameSkill().getName());
				skill.setRanks(skillResult.getRanks());
				skill.setLevel(skillResult.getLevel());
				skill.setModifier(skillResult.getModifier());
				skills.add(skill);
			}
			generalSkill.setSkills(skills);
		}

		Place place = getPlaceHandler.getLocation(character.getLocationId());
		Game game = getGameHandler.getGame(place.getGameId());

		if (game.getGameMasterCharacterId().equals(character.getId())) {
			character.setCharacterType("GM");
		} else if (character.getPlayerAccountId().equals(character.getGameMasterAccountId())) {
			character.setCharacterType("NPC");
		} else {
			character.setCharacterType("Player");
		}

		if (!getCharacterHandler.getUpdatePermission(account, character)) {
			throw new AccessRightException("You cannot change this Character");
		}

		save(character, generalSkills, id == null);

		// if you have changed the account that is playing this character
		// whatever account was previously playing that character should go back
		// to playing their personal character - unless they are the GM character 
		// that owns the character being changed.
		if (!playerid.equals(character.getPlayerAccountId()) && character.getGameMasterAccountId() != playerid) {
			PersistenceManager pm = PMUtils.getPersistenceManager();
			pm.currentTransaction().begin();
			Account a = getAccountHandler.getAccount(playerid);
			a.setPlayingCharacterId(a.getPersonalCharacterId());
			pm.currentTransaction().commit();
			cache.put(a.getGaeUser().getUserId(), a);
		}

		cache.put(character.getId(), character);

		characterResult = getCharacterHandler.getResult(account, game, place, character);
		characterResult.setCharacters(getCharactersPlayableHandler.getCharactersResult(account, getCharactersPlayableHandler.getInterestedByName()));
		characterResult.setOtherCharacters(getCharactersPlayableHandler.getCharactersResult(account, getCharactersByPlaceHandler.getByLocation(account, game, place.getId())));

		return characterResult;

	}

	public void save(Character character, List<CharacterGeneralSkill> skills, boolean initialise) throws NotSignedInException {
		
		PersistenceManager pm = PMUtils.getPersistenceManager();

		try {

			if (character.getLocationId() == null) {
				throw new IllegalStateException("Cannot persist a Character without a Location");
			}

			Date today = new Date();
			String accountId = getAccountHandler.getMyAccount().getId();

			if (character.getId() == null) {
				
				pm.currentTransaction().begin();
				character.setGameMasterAccountId(accountId);
				character.setPlayerAccountId(accountId);
				character.setCreated(today);
				character.setSkills(skills);
				if (initialise){
					if (character.getVigor() == null) {
						character.setVigor(4);
					}
					if (character.getMettle() == null) {
						character.setMettle(4);
					}
					if (character.getWit() == null) {
						character.setWit(3);
					}
					if (character.getGlamour() == null) {
						character.setGlamour(3);
					}
					if (character.getSpirit() == null) {
						character.setSpirit(0);
					}
					character.setSlashArmour(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
					character.setCrushArmour(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
					character.setPierceArmour(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
					character.setArmourType(new String[] {"No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour", "No Armour"});
					CharacterGeneralSkill uc = new CharacterGeneralSkill(character, "Unarmed Combat");
					List<CharacterSkill> ucskills = new ArrayList<CharacterSkill>();
					uc.setLevel(0);
					uc.setModifier(25);
					CharacterSkill strike = new CharacterSkill(uc, "Strike");
					strike.setLevel(0);
					strike.setModifier(25);
					ucskills.add(strike);
					CharacterSkill dodge = new CharacterSkill(uc, "Dodge");
					dodge.setLevel(0);
					dodge.setModifier(25);
					ucskills.add(dodge);
					uc.setSkills(ucskills);
					List<CharacterGeneralSkill> ucgskills = new ArrayList<CharacterGeneralSkill>();
					ucgskills.add(uc);
					character.setSkills(ucgskills);
					List<String> armour = new ArrayList<String>();
					armour.add("No Armour");
					character.setArmour(armour);
					List<String> weapons = new ArrayList<String>();
					weapons.add("Hand Strike");
					weapons.add("Foot Strike");
					weapons.add("Head Butt");
					weapons.add("Dodge");
					character.setWeapons(weapons);
					character.setLeftHand("Hand Strike");
					character.setRightHand("Hand Strike");
					character.setDefence("N");
				}
				
				pm.makePersistent(character);
				pm.currentTransaction().commit();

			} else {
			
				pm.currentTransaction().begin();
				character.setUpdated(today);
				if (skills != null) {
					pm.deletePersistentAll(character.getSkills());
					character.getSkills().clear();
					character.getSkills().addAll(skills);
					pm.makePersistentAll(skills);
				}
				pm.currentTransaction().commit();
				if (character.getPreviousPlayer() != null) {
					Account oldAccount = getAccountHandler.getAccount(character.getPreviousPlayer());
					Account newAccount = getAccountHandler.getAccount(character.getPlayerAccountId());
					if (!oldAccount.getId().equals(accountId)) {
						Post p = new Post(oldAccount.getPersonalCharacterId(), character.getName() + " has been assigned to someone else. " +
						"This character will no longer be available to you to play.");
						p.setLocationId(oldAccount.getHomeLocationId());
						savePostHandler.save(p);
						pm.currentTransaction().begin();
						oldAccount.setPlayingCharacterId(oldAccount.getPersonalCharacterId());
						pm.currentTransaction().commit();
						MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
						cache.put(oldAccount.getGaeUser().getUserId(), oldAccount);
						EmailService.get().sendCharacterRemovedNotification(oldAccount, character);
						log.info("Character reassigned " + oldAccount.getName());
					}
					if (!newAccount.getId().equals(accountId)) {
						Post p = new Post(newAccount.getPersonalCharacterId(), character.getName() + " has been assigned to you. " + 
						"To play this character you need to go to the Play View, select the character and click the Play link.");
						p.setLocationId(newAccount.getHomeLocationId());
						savePostHandler.save(p);
						pm.currentTransaction().begin();
						newAccount.setPlayingCharacterId(character.getId());
						pm.currentTransaction().commit();
						MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
						cache.put(newAccount.getGaeUser().getUserId(), newAccount);
						EmailService.get().sendCharacterAssignedNotification(newAccount, character);
						log.info("Character assigned to " + newAccount.getName());
					}
				}

			}
			
			log.info("Persisted Character " + character.getName());

		} catch (RuntimeException t) {
			
			log.severe(t.getMessage());
			t.printStackTrace();
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			throw t;

		}

	}
	
	@Override
	public Class<SaveCharacterAction> getActionType() {
		return SaveCharacterAction.class;
	}

	@Override
	public void rollback(SaveCharacterAction action, CharacterResult result, ExecutionContext context) throws DispatchException {
		
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

	/**
	 * @param getPlaceHandler the getLocationHandler to set
	 */
	public void setGetLocationHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

	/**
	 * @param getGameHandler the getGameHandler to set
	 */
	public void setGetGameHandler(GetGameHandler getGameHandler) {
		this.getGameHandler = getGameHandler;
	}

	/**
	 * @param savePostHandler the savePostHandler to set
	 */
	public void setSavePostHandler(SavePostHandler savePostHandler) {
		this.savePostHandler = savePostHandler;
	}

	/**
	 * @param getCharactersPlayableHandler
	 */
	public void setGetCharactersHandler(GetCharactersPlayableHandler getCharactersPlayableHandler) {
		this.getCharactersPlayableHandler = getCharactersPlayableHandler;
	}

	/**
	 * @param getCharactersByPlaceHandler
	 */
	public void setGetOtherCharactersHandler(GetCharactersByPlaceHandler getCharactersByPlaceHandler) {
		this.getCharactersByPlaceHandler = getCharactersByPlaceHandler;
	}

	public void setCharacterValidator(CharacterValidator characterValidator) {
		this.characterValidator = characterValidator;
	}

}