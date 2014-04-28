package com.hotf.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Character extends ModelBase implements Serializable {

	@Persistent	private String locationId;
	@Persistent	private BlobKey portrait;
	@Persistent	private String name;
	@Persistent	private String nameUpper;
	@Persistent	private Date created;
	@Persistent	private Date updated;
	@Persistent	private Text description;
	@Persistent	private Text sheet;
	@Persistent	private int tokenX = 0;
	@Persistent	private int tokenY = 0;
	@Persistent	private boolean tokenHidden;
	@Persistent	private String[] interested = new String[2];
	@Persistent	private String characterType;
	@Persistent	private String status;
	@Persistent	private String lastPostOrder;
	@Persistent	private Integer characterPoints;
	@Persistent	private Integer vigor;
	@Persistent	private Integer mettle;
	@Persistent	private Integer wit;
	@Persistent	private Integer glamour;
	@Persistent	private Integer spirit;
	@Persistent	private Integer wyrd;
	@Persistent	private Integer age;
	@Persistent	private Integer skillRanks;
	@Persistent	private String handed;
	@Persistent	private String leftHand;
	@Persistent	private String rightHand;
	@Persistent	private String defence;
	@Persistent	private String armourType[];
	@Persistent	private Integer slashArmour[];
	@Persistent	private Integer crushArmour[];
	@Persistent	private Integer pierceArmour[];
	@Persistent(mappedBy = "character") @Element(dependent = "true") private List<CharacterGeneralSkill> skills;
	@Persistent	private List<String> armour = new ArrayList<String>();
	@Persistent	private List<String> weapons = new ArrayList<String>();
	@Persistent	private List<String> artifacts = new ArrayList<String>();
	@Persistent	private String leftTargetCharacterId;
	@Persistent	private String rightTargetCharacterId;
	@Persistent	private Integer leftTargetArea;
	@Persistent	private Integer rightTargetArea;
	@Persistent	private String leftTargetStrike;
	@Persistent	private String rightTargetStrike;
	@NotPersistent private String previousPlayer = null;

	/**
	 * Empty constructor.
	 */
	public Character() {
		super();
	}

	/**
	 * Character Constructor.
	 * @param locationId Location Id
	 * @param name Name
	 */
	public Character(String locationId, String name) {
		super();
		setLocationId(locationId);
		if (name == null) {
			setName("Game Master");
		} else {
			setName(name);
		}
	}

	/**
	 * @return locationId
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId to set
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public BlobKey getPortrait() {
		return portrait;
	}

	public void setPortrait(BlobKey portrait) {
		this.portrait = portrait;
	}

	/**
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name to set
	 */
	public void setName(String name) {
		if (name == null || name.length() < 1) {
			throw new IllegalStateException("Name is required");
		}
		if (name.equals(getName())) {
			return;
		}
		this.name = name;
		setNameUpper(name.toUpperCase());
	}

	/**
	 * @return the name in Upper case
	 */
	public String getNameUpper() {
		return nameUpper;
	}

	/**
	 * @param nameUpper the upper case name to set
	 */
	public void setNameUpper(String nameUpper) {
		this.nameUpper = nameUpper;
	}

	/**
	 * @return name
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return Date created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created date to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return Date updated
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated date to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * @return Description
	 */
	public String getDescription() {
		if (description == null) return null;
		return description.getValue();
	}

	/**
	 * @param description to set
	 */
	public void setDescription(String description) {
		this.description = new Text(description);
	}

	/**
	 * @return sheet
	 */
	public String getSheet() {
		if (sheet == null) return null;
		return sheet.getValue();
	}

	/**
	 * @param sheet to set
	 */
	public void setSheet(String sheet) {
		this.sheet = new Text(sheet);
	}

	/**
	 * @return the tokenX
	 */
	public int getTokenX() {
		return tokenX;
	}

	/**
	 * @param tokenX the tokenX to set
	 */
	public void setTokenX(int tokenX) {
		this.tokenX = tokenX;
	}

	/**
	 * @return the tokenY
	 */
	public int getTokenY() {
		return tokenY;
	}

	/**
	 * @param tokenY the tokenY to set
	 */
	public void setTokenY(int tokenY) {
		this.tokenY = tokenY;
	}

	/**
	 * @return the tokenHidden
	 */
	public boolean getTokenHidden() {
		return tokenHidden;
	}

	/**
	 * @param tokenHidden the tokenHidden to set
	 */
	public void setTokenHidden(boolean tokenHidden) {
		this.tokenHidden = tokenHidden;
	}

	/**
	 * @param interested the interested to set
	 */
	public void setInterested(String[] interested) {
		this.interested = interested;
	}
	
	/**
	 * @return the interested
	 */
	public String[] getInterested() {
		return interested;
	}
	
	public String getPlayerAccountId() {
		return interested[0];
	}

	public String getPreviousPlayer() {
		return previousPlayer;
	}
	
	public void setPlayerAccountId(String accountId) {
		if (getPlayerAccountId() != null && !getPlayerAccountId().equals(accountId)) {
			previousPlayer = getPlayerAccountId();
		}
		setInterested(new String[] {accountId, getGameMasterAccountId()});
	}

	public String getGameMasterAccountId() {
		return interested[1];
	}

	public void setGameMasterAccountId(String gameMasterId) {
		setInterested(new String[] {getPlayerAccountId(), gameMasterId});
	}

	/**
	 * @return Character Type
	 */
	public String getCharacterType() {
		return characterType;
	}

	/**
	 * @param type the type to set
	 */
	public void setCharacterType(String type) {
		this.characterType = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the lastPostOrder
	 */
	public String getLastPostOrder() {
		return lastPostOrder;
	}

	/**
	 * @param lastPostOrder the lastPostOrder to set
	 */
	public void setLastPostOrder(String lastPostOrder) {
		this.lastPostOrder = lastPostOrder;
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
	 * @return the characterPoints
	 */
	public Integer getCharacterPoints() {
		return characterPoints;
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
	public List<CharacterGeneralSkill> getSkills() {
		return skills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setSkills(List<CharacterGeneralSkill> skills) {
		this.skills = skills;
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

	public List<String> getArmour() {
		return armour;
	}

	public void setArmour(List<String> armour) {
		this.armour = armour;
	}

	public List<String> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<String> weapons) {
		this.weapons = weapons;
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
