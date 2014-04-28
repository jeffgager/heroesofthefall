package com.hotf.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Text;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable = "true", identityType = IdentityType.APPLICATION)
public class Game extends ModelBase implements Serializable {

	@Persistent	private String gameMasterCharacterId;
	@Persistent	private String title;
	@Persistent	private String titleUpper;
	@Persistent	private Date created;
	@Persistent	private Date updated;
	@Persistent	private Text description;
	@Persistent(mappedBy = "game") @Element(dependent = "true") private List<GameWeapon> weapons;
	@Persistent(mappedBy = "game") @Element(dependent = "true") private List<GameArmour> armour;
	@Persistent(mappedBy = "game") @Element(dependent = "true") private List<GameArtifact> artifacts;

	/**
	 * Game Constructor.
	 */
	public Game() {
		super();
	}

	/**
	 * Game Constructor.
	 * 
	 * @param title
	 *            Title
	 */
	public Game(String title) {
		super();
		setTitle(title);
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
	 * @return Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            to set
	 */
	public void setTitle(String title) {
		if (title == null || title.length() < 2) {
			throw new IllegalStateException("Title must be two characters or more");
		}
		this.title = title;
		setTitleUpper(title.toUpperCase());
	}

	/**
	 * @return Title in upper case
	 */
	public String getTitleUpper() {
		return titleUpper;
	}

	/**
	 * @param titleUpper
	 *            Upper case title
	 */
	public void setTitleUpper(String titleUpper) {
		this.titleUpper = titleUpper;
	}

	/**
	 * @see Object.toString()
	 */
	@Override
	public String toString() {
		return title;
	}

	/**
	 * @return Date created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            date to set
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
	 * @param updated
	 *            date to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * @return Description
	 */
	public String getDescription() {
		if (description == null)
			return null;
		return description.getValue();
	}

	/**
	 * @param description
	 *            to set
	 */
	public void setDescription(String description) {
		this.description = new Text(description);
	}

	public List<GameWeapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<GameWeapon> weapons) {
		this.weapons = weapons;
	}

	public List<GameArmour> getArmour() {
		return armour;
	}

	public void setArmour(List<GameArmour> armour) {
		this.armour = armour;
	}

	public List<GameArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<GameArtifact> artifacts) {
		this.artifacts = artifacts;
	}

}
