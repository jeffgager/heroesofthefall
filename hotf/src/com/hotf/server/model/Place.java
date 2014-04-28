package com.hotf.server.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.hotf.server.Base64Coder;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable = "true", identityType = IdentityType.APPLICATION)
public class Place extends ModelBase implements Serializable {

	@Persistent	private String gameId;
	@Persistent	private String gameMasterAccountId;
	@Persistent private String type;
	@Persistent	private String name;
	@Persistent	private String nameUpper;
	@Persistent	private Text description;
	@Persistent	private Date created;
	@Persistent	private String createdOrder;
	@Persistent	private Date updated;
	@Persistent	private BlobKey map;
	@Persistent	private Blob overlay;

	/**
	 * Constructor.
	 */
	public Place() {
		super();
	}

	/**
	 * Constructor.
	 * @param gameId of Game.
	 * @param name of new location
	 */
	public Place(String gameId, String name) {
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

	/**
	 * @return map
	 */
	public BlobKey getMap() {
		return map;
	}

	/**
	 * @param map to set
	 */
	public void setMap(BlobKey map) {
		this.map = map;
	}

	/**
	 * @return Overlay as byte array
	 */
	public byte[] getOverlay() {
		if (overlay == null) return null;
		return overlay.getBytes();
	}

	/**
	 * @param Overlay as byte array to to set
	 */
	public void setOverlay(byte[] overlay) {
		if (overlay != null) {
			this.overlay = new Blob(overlay);
		} else {
			this.overlay = null;
		}
	}

	/**
	 * @param Overlay as url (for changes only)
	 */
	public void setOverlayUrl(String overlay) {
		if (overlay != null) {
			setOverlay(Base64Coder.decode(overlay));
		} else {
			setOverlay(null);
		}
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Created Order
	 */
	public String getCreatedOrder() {
		return createdOrder;
	}

	/**
	 * @param createdOrder Created order to set
	 */
	public void setCreatedOrder(String createdOrder) {
		this.createdOrder = createdOrder;
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

}
