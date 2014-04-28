package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class CharacterResult implements Result {

	private String id;
	private String locationId;
	private String name;
	private String description;
	private String sheet;
	private String status;
	private Double tokenX;
	private Double tokenY;
	private Boolean tokenHidden;
	private String playerAccountId;
	private String gameMasterAccountId;
	private String characterType;
	private Boolean updatePermission;
	private String label;
	private String playerName;
	private Date created;
	private Date updated;
	private List<CharacterResult> characters = new ArrayList<CharacterResult>();
	private List<CharacterResult> otherCharacters = new ArrayList<CharacterResult>();
	private Integer characterPoints = new Integer(0);
	private Integer vigor;
	private Integer mettle;
	private Integer wit;
	private Integer glamour;
	private Integer spirit;
	private Integer wyrd;
	private Integer age;
	private Integer skillRanks;
	private String handed;
	private String leftHand;
	private String rightHand;
	private String defence;
	private List<CharacterGeneralSkillResult> generalSkills = new ArrayList<CharacterGeneralSkillResult>();
	private String armourType[] = new String[23];
	private Integer slashArmour[] = new Integer[23];
	private Integer crushArmour[] = new Integer[23];
	private Integer pierceArmour[] = new Integer[23];
	private List<String> weapons = new ArrayList<String>();
	private List<String> armour = new ArrayList<String>();
	private List<String> artifacts = new ArrayList<String>();
	private String leftTargetCharacterId;
	private String rightTargetCharacterId;
	private Integer leftTargetArea;
	private Integer rightTargetArea;
	private String leftTargetStrike;
	private String rightTargetStrike;

	public CharacterResult() {
		super();
		for (int i = 0; i < 23; i++) {
			armourType[i] = "No armour";
			slashArmour[i] = new Integer(0);
			crushArmour[i] = new Integer(0);
			pierceArmour[i] = new Integer(0);
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the sheet
	 */
	public String getSheet() {
		return sheet;
	}

	/**
	 * @param sheet the sheet to set
	 */
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the tokenX
	 */
	public Double getTokenX() {
		return tokenX;
	}

	/**
	 * @param tokenX the tokenX to set
	 */
	public void setTokenX(Double tokenX) {
		this.tokenX = tokenX;
	}

	/**
	 * @return the tokenY
	 */
	public Double getTokenY() {
		return tokenY;
	}

	/**
	 * @param tokenY the tokenY to set
	 */
	public void setTokenY(Double tokenY) {
		this.tokenY = tokenY;
	}

	/**
	 * @return the tokenHidden
	 */
	public Boolean getTokenHidden() {
		return tokenHidden;
	}

	/**
	 * @param tokenHidden the tokenHidden to set
	 */
	public void setTokenHidden(Boolean tokenHidden) {
		this.tokenHidden = tokenHidden;
	}

	/**
	 * @return the playerAccountId
	 */
	public String getPlayerAccountId() {
		return playerAccountId;
	}

	/**
	 * @param playerAccountId the playerAccountId to set
	 */
	public void setPlayerAccountId(String playerAccountId) {
		this.playerAccountId = playerAccountId;
	}

	/**
	 * @return the gameMasterAccountId
	 */
	public String getGameMasterAccountId() {
		return gameMasterAccountId;
	}

	/**
	 * @param gameMasterAccountId the gameMasterAccountId to set
	 */
	public void setGameMasterAccountId(String gameMasterAccountId) {
		this.gameMasterAccountId = gameMasterAccountId;
	}

	/**
	 * @return the characterType
	 */
	public String getCharacterType() {
		return characterType;
	}

	/**
	 * @param characterType the characterType to set
	 */
	public void setCharacterType(String characterType) {
		this.characterType = characterType;
	}

	/**
	 * @return the updatePermission
	 */
	public Boolean getUpdatePermission() {
		return updatePermission;
	}

	/**
	 * @param updatePermission the updatePermission to set
	 */
	public void setUpdatePermission(Boolean updatePermission) {
		this.updatePermission = updatePermission;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * @return the characters
	 */
	public List<CharacterResult> getCharacters() {
		return characters;
	}

	/**
	 * @param characters the characters to set
	 */
	public void setCharacters(List<CharacterResult> characters) {
		this.characters = characters;
	}

	/**
	 * @return the otherCharacters
	 */
	public List<CharacterResult> getOtherCharacters() {
		return otherCharacters;
	}

	/**
	 * @param otherCharacters the otherCharacters to set
	 */
	public void setOtherCharacters(List<CharacterResult> otherCharacters) {
		this.otherCharacters = otherCharacters;
	}

	/**
	 * @return the vigor
	 */
	public Integer getVigor() {
		return vigor;
	}

	/**
	 * @param vigor the vigor to set
	 */
	public void setVigor(Integer vigor) {
		this.vigor = vigor;
	}

	/**
	 * @return the wit
	 */
	public Integer getWit() {
		return wit;
	}

	/**
	 * @param wit the wit to set
	 */
	public void setWit(Integer wit) {
		this.wit = wit;
	}

	/**
	 * @return the glamour
	 */
	public Integer getGlamour() {
		return glamour;
	}

	/**
	 * @param glamour the glamour to set
	 */
	public void setGlamour(Integer glamour) {
		this.glamour = glamour;
	}

	/**
	 * @return the wyrd
	 */
	public Integer getWyrd() {
		return wyrd;
	}

	/**
	 * @param wyrd the wyrd to set
	 */
	public void setWyrd(Integer wyrd) {
		this.wyrd = wyrd;
	}

	/**
	 * @return the characterPoints
	 */
	public Integer getCharacterPoints() {
		return characterPoints;
	}

	/**
	 * @return the mettle
	 */
	public Integer getMettle() {
		return mettle;
	}

	/**
	 * @param mettle the mettle to set
	 */
	public void setMettle(Integer mettle) {
		this.mettle = mettle;
	}

	/**
	 * @return the spirit
	 */
	public Integer getSpirit() {
		return spirit;
	}

	/**
	 * @param spirit the spirit to set
	 */
	public void setSpirit(Integer spirit) {
		this.spirit = spirit;
	}

	/**
	 * @param characterPoints the characterPoints to set
	 */
	public void setCharacterPoints(Integer characterPoints) {
		this.characterPoints = characterPoints;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(Integer age) {
		this.age = age;
	}

	/**
	 * @return the handed
	 */
	public String getHanded() {
		return handed;
	}

	/**
	 * @param handed the handed to set
	 */
	public void setHanded(String handed) {
		this.handed = handed;
	}

	/**
	 * @return the skillRanks
	 */
	public Integer getSkillRanks() {
		return skillRanks;
	}

	/**
	 * @param skillRanks the skillRanks to set
	 */
	public void setSkillRanks(Integer skillRanks) {
		this.skillRanks = skillRanks;
	}

	/**
	 * @return the skills
	 */
	public List<CharacterGeneralSkillResult> getGeneralSkills() {
		return generalSkills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setGeneralSkills(List<CharacterGeneralSkillResult> skills) {
		this.generalSkills = skills;
	}

	public String[] getArmourType() {
		return armourType;
	}

	public void setArmourType(String[] armourType) {
		this.armourType = armourType;
	}

	/**
	 * @return the slashArmour
	 */
	public Integer[] getSlashArmour() {
		return slashArmour;
	}

	/**
	 * @param slashArmour the slashArmour to set
	 */
	public void setSlashArmour(Integer[] slashArmour) {
		this.slashArmour = slashArmour;
	}

	/**
	 * @return the crushArmour
	 */
	public Integer[] getCrushArmour() {
		return crushArmour;
	}

	/**
	 * @param crushArmour the crushArmour to set
	 */
	public void setCrushArmour(Integer[] crushArmour) {
		this.crushArmour = crushArmour;
	}

	/**
	 * @return the pierceArmour
	 */
	public Integer[] getPierceArmour() {
		return pierceArmour;
	}

	/**
	 * @param pierceArmour the pierceArmour to set
	 */
	public void setPierceArmour(Integer[] pierceArmour) {
		this.pierceArmour = pierceArmour;
	}

	public List<String> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<String> weapons) {
		this.weapons = weapons;
	}

	public List<String> getArmour() {
		return armour;
	}

	public void setArmour(List<String> armour) {
		this.armour = armour;
	}

	public List<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<String> artifacts) {
		this.artifacts = artifacts;
	}

	public String getLeftHand() {
		return leftHand;
	}

	public void setLeftHand(String leftHand) {
		this.leftHand = leftHand;
	}

	public String getRightHand() {
		return rightHand;
	}

	public void setRightHand(String rightHand) {
		this.rightHand = rightHand;
	}

	public String getDefence() {
		return defence;
	}

	public void setDefence(String defence) {
		this.defence = defence;
	}

	public String getLeftTargetCharacterId() {
		return leftTargetCharacterId;
	}

	public void setLeftTargetCharacterId(String leftTargetCharacterId) {
		this.leftTargetCharacterId = leftTargetCharacterId;
	}

	public String getRightTargetCharacterId() {
		return rightTargetCharacterId;
	}

	public void setRightTargetCharacterId(String rightTargetCharacterId) {
		this.rightTargetCharacterId = rightTargetCharacterId;
	}

	public Integer getLeftTargetArea() {
		return leftTargetArea;
	}

	public void setLeftTargetArea(Integer leftTargetArea) {
		this.leftTargetArea = leftTargetArea;
	}

	public Integer getRightTargetArea() {
		return rightTargetArea;
	}

	public void setRightTargetArea(Integer rightTargetArea) {
		this.rightTargetArea = rightTargetArea;
	}

	public String getLeftTargetStrike() {
		return leftTargetStrike;
	}

	public void setLeftTargetStrike(String leftTargetStrike) {
		this.leftTargetStrike = leftTargetStrike;
	}

	public String getRightTargetStrike() {
		return rightTargetStrike;
	}

	public void setRightTargetStrike(String rightTargetStrike) {
		this.rightTargetStrike = rightTargetStrike;
	}

}
