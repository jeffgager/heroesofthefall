package com.hotf.server.model;

import java.io.Serializable;
import java.util.Date;

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
public class PlaceMap extends ModelBase implements Serializable {

	@Persistent	private String gameId;
	@Persistent	private String gameMasterAccountId;
	@Persistent private String type;
	@Persistent	private String name;
	@Persistent	private String nameUpper;
	@Persistent	private Text description;
	@Persistent	private Date created;
	@Persistent	private Date updated;
	@Persistent	private String backgroundTile;
	@Persistent	private String[][] tileMap;

	/**
	 * Constructor.
	 */
	public PlaceMap() {
		super();
	}

	/**
	 * Constructor.
	 * @param gameId of Game.
	 * @param name of new location
	 */
	public PlaceMap(String gameId, String name) {
		super();
		setGameId(gameId);
		setName(name);
	}

	/**
	 * @return Game Id
	 */
	public String getGameId() {
		return gameId;
	}

	/**
	 * @param gameId to set
	 */
	public void setGameId(String gameId) {
		this.gameId = gameId;
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
	 * @return Date Updated.
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

	public String getGameMasterAccountId() {
		return gameMasterAccountId;
	}

	public void setGameMasterAccountId(String gameMasterAccountId) {
		this.gameMasterAccountId = gameMasterAccountId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBackgroundTile() {
		return backgroundTile;
	}

	public void setBackgroundTile(String backgroundTile) {
		this.backgroundTile = backgroundTile;
	}

	public String[][] getTileMap() {
		return tileMap;
	}

	public void setTileMap(String[][] tileMap) {
		this.tileMap = tileMap;
	}

}
