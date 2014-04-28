package com.hotf.server.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.client.action.GetCharacterAction;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.CharacterGeneralSkill;
import com.hotf.server.model.CharacterSkill;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;

public class GetCharacterHandler implements ActionHandler<GetCharacterAction, CharacterResult> {

	private static final Logger log = Logger.getLogger(GetCharacterHandler.class.getName());

	private GetAccountHandler getAccountHandler;
	private GetGameHandler getGameHandler;
	private GetPlaceHandler getPlaceHandler;

	public GetCharacterHandler() {
	}

	@Override
	public CharacterResult execute(GetCharacterAction action, ExecutionContext context) throws DispatchException {

		log.info("Handling GetCharacterAction");

		Account account = getAccountHandler.getMyAccount();
		Character character = getCharacter(action.getId());
		Place place = getPlaceHandler.getLocation(character.getLocationId());
		Game game = getGameHandler.getGame(place.getGameId());
		return getResult(account, game, place, character);

	}

	public CharacterResult getResult(Account userAccount, Game game, Place place, Character character) throws NotSignedInException {
		return getResult(userAccount, getAccountHandler.getAccount(character.getPlayerAccountId()), game, place, character);
	}
	
	public CharacterResult getResult(Account userAccount, Account playerAccount, Game game, Place place, Character character) {
		CharacterResult characterResult = new CharacterResult();
		characterResult.setCharacterType(character.getCharacterType());
		characterResult.setDescription(character.getDescription());
		characterResult.setSheet(character.getSheet());
		characterResult.setGameMasterAccountId(character.getGameMasterAccountId());
		characterResult.setId(character.getId());
		characterResult.setLocationId(character.getLocationId());
		characterResult.setName(character.getName());
		characterResult.setPlayerAccountId(character.getPlayerAccountId());
		characterResult.setStatus(character.getStatus());
		characterResult.setTokenHidden(character.getTokenHidden());
		characterResult.setTokenX(new Double(character.getTokenX()));
		characterResult.setTokenY(new Double(character.getTokenY()));
		characterResult.setUpdatePermission(getUpdatePermission(userAccount, character));
		characterResult.setCreated(character.getCreated());
		characterResult.setUpdated(character.getUpdated());
		characterResult.setPlayerName(playerAccount.getName());
		characterResult.setCharacterPoints(character.getCharacterPoints());
		characterResult.setVigor(character.getVigor());
		characterResult.setWit(character.getWit());
		characterResult.setMettle(character.getMettle());
		characterResult.setGlamour(character.getGlamour());
		characterResult.setWyrd(character.getWyrd());
		characterResult.setSpirit(character.getSpirit());
		characterResult.setCharacterPoints(character.getCharacterPoints());
		characterResult.setAge(character.getAge());
		characterResult.setHanded(character.getHanded());
		String lhw = character.getLeftHand();
		characterResult.setLeftHand(lhw == null ? "Hand Strike" : lhw);
		String rhw = character.getRightHand();
		characterResult.setRightHand(rhw == null ? "Hand Strike" : rhw);
		characterResult.setDefence(character.getDefence());
		characterResult.setSkillRanks(character.getSkillRanks());
		characterResult.setLeftTargetCharacterId(character.getLeftTargetCharacterId());
		characterResult.setRightTargetCharacterId(character.getRightTargetCharacterId());
		Integer lhta = character.getLeftTargetArea();
		characterResult.setLeftTargetArea(lhta == null ? 0 : lhta);
		Integer rhta = character.getRightTargetArea();
		characterResult.setRightTargetArea(rhta == null ? 0 : rhta);
		String lhts = character.getLeftTargetStrike();
		characterResult.setLeftTargetStrike(lhts == null ? "Swing" : lhts);
		String rhts = character.getRightTargetStrike();
		characterResult.setRightTargetStrike(rhts == null ? "Swing" : rhts);
		if (character.getArmourType() != null) {
			for (int i = 0; i < character.getArmourType().length; i++) {
				characterResult.getArmourType()[i] = character.getArmourType()[i];
				characterResult.getSlashArmour()[i] = character.getSlashArmour()[i];
				characterResult.getCrushArmour()[i] = character.getCrushArmour()[i];
				characterResult.getPierceArmour()[i] = character.getPierceArmour()[i];
			}
		}
		List<CharacterGeneralSkillResult> generalSkills = new ArrayList<CharacterGeneralSkillResult>();
		for (CharacterGeneralSkill generalSkill : character.getSkills()) {
			CharacterGeneralSkillResult generalSkillResult = new CharacterGeneralSkillResult();
			generalSkillResult.setGameGeneralSkill(getGameHandler.getGeneralSkill(generalSkill.getName()));
			generalSkillResult.setRanks(generalSkill.getRanks());
			generalSkillResult.setLevel(generalSkill.getLevel());
			generalSkillResult.setModifier(generalSkill.getModifier());
			generalSkills.add(generalSkillResult);
			for (CharacterSkill skill : generalSkill.getSkills()) {
				CharacterSkillResult skillResult = new CharacterSkillResult();
				skillResult.setGeneralSkill(generalSkillResult);
				skillResult.setGameSkill(getGameHandler.getSkill(skill.getName()));
				skillResult.setRanks(skill.getRanks());
				skillResult.setLevel(skill.getLevel());
				skillResult.setModifier(skill.getModifier());
				generalSkillResult.getSkills().add(skillResult);
			}
		}
		characterResult.setGeneralSkills(generalSkills);

		characterResult.setWeapons(new ArrayList<String>(character.getWeapons()));
		characterResult.setArmour(new ArrayList<String>(character.getArmour()));
		characterResult.setArtifacts(new ArrayList<String>(character.getArtifacts()));

		if (game.getGameMasterCharacterId().equals(character.getId())) {
			characterResult.setLabel(character.getName() + " (GM) " + " in " + place.getName() + " at " + game.getTitle());
		} else {
			characterResult.setLabel(character.getName() + " in " + place.getName() + " at " + game.getTitle());
		}

		return characterResult;

	}

	/**
	 * Get character by Id.
	 * @param characterId to find
	 * @return Character
	 */
	public Character getCharacter(String characterId) {

		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		if (cache.contains(characterId)) {
			log.info("Getting Character from Memcache");
			try {
				return (Character)cache.get(characterId);
			} catch (InvalidValueException e) {
				log.warning("Clearing cache");
				cache.clearAll();
			}
		}

		try {

			PersistenceManager pm = PMUtils.getPersistenceManager();

			log.info("Getting Character from Datastore by id");
			Character character = pm.getObjectById(Character.class, characterId);
			cache.put(characterId, character);

			return character;
		
		} catch (RuntimeException t) {

			log.severe(t.getMessage());
			throw t;

		}

	}

	/**
	 * @return true if the users has update permission, otherwise return false
	 */
	public Boolean getUpdatePermission(Account account, Character character) {

		boolean updatePermission = false;

		//get location id
		String locationId = character.getLocationId();
		
		//if no location yet then allow update but don't remember permission value
		if (locationId == null) {
			return true;
		}

		//get GM character id
		String gmAccountId = character.getGameMasterAccountId();
		String playerAccountId = character.getPlayerAccountId();

		if (playerAccountId != null && playerAccountId.equals(account.getId())) {
			
			updatePermission = true;

		//or if the account that is playing the GM is the currently logged in account
		} else if (gmAccountId != null && gmAccountId.equals(account.getId())) {
			
			updatePermission = true;

		//update allowed if no GM character set 
		} else if (gmAccountId == null) {
			
			updatePermission = true;

		//or if the account that is playing the character is the currently logged in account
		} else {
		
			updatePermission = false;
		
		}

		//return the update permission
		return updatePermission;

	}
	
	@Override
	public Class<GetCharacterAction> getActionType() {
		return GetCharacterAction.class;
	}

	@Override
	public void rollback(GetCharacterAction action, CharacterResult result, ExecutionContext context) throws DispatchException {
		
	}

	/**
	 * @param getAccountHandler the getAccountHandler to set
	 */
	public void setGetAccountHandler(GetAccountHandler getAccountHandler) {
		this.getAccountHandler = getAccountHandler;
	}

	public void setGetGameHandler(GetGameHandler getGameHandler) {
		this.getGameHandler = getGameHandler;
	}
	public void setGetPlaceHandler(GetPlaceHandler getPlaceHandler) {
		this.getPlaceHandler = getPlaceHandler;
	}

}
