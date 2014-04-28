package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

public class GameResult implements Result {
	
	private String id;
	private String gameMasterCharacterId;
	private String title;
	private String owner;
	private String description;
	private Date created;
	private Date updated;
	private Boolean updatePermission;
	private List<CharacterResult> characters = new ArrayList<CharacterResult>();
	private List<PlaceResult> locations = new ArrayList<PlaceResult>();
	private List<GameWeaponResult> weapons = new ArrayList<GameWeaponResult>();
	private List<GameArmourResult> armour = new ArrayList<GameArmourResult>();
	private List<GameArtifactResult> artifacts = new ArrayList<GameArtifactResult>();
	private List<GameGeneralSkillResult> generalSkills = new ArrayList<GameGeneralSkillResult>();
	private PlaceResult start;

	public GameResult() {
		super();
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
	 * @return the gameMasterCharacterId
	 */
	public String getGameMasterCharacterId() {
		return gameMasterCharacterId;
	}

	/**
	 * @param gameMasterCharacterId the gameMasterCharacterId to set
	 */
	public void setGameMasterCharacterId(String gameMasterCharacterId) {
		this.gameMasterCharacterId = gameMasterCharacterId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @return the locations
	 */
	public List<PlaceResult> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<PlaceResult> locations) {
		this.locations = locations;
	}

	/**
	 * @return the start
	 */
	public PlaceResult getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(PlaceResult start) {
		this.start = start;
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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<GameWeaponResult> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<GameWeaponResult> weapons) {
		this.weapons = weapons;
	}

	public List<GameArmourResult> getArmour() {
		return armour;
	}

	public void setArmour(List<GameArmourResult> armour) {
		this.armour = armour;
	}

	public List<GameGeneralSkillResult> getGeneralSkills() {
		return generalSkills;
	}

	public void setGeneralSkills(List<GameGeneralSkillResult> generalSkills) {
		this.generalSkills = generalSkills;
	}

	public List<GameArtifactResult> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<GameArtifactResult> artifacts) {
		this.artifacts = artifacts;
	}

}
