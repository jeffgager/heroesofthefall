package com.hotf.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
public class Post extends ModelBase implements Serializable {

	@Persistent	private String characterId;
	@Persistent	private String locationId;
	@Persistent	private String targetedCharacterId;
	@Persistent	private Text text;
	@Persistent	private Date created;
	@Persistent	private String createdOrder;
	@Persistent	private Date updated;
	@Persistent	private Set<String> presentIds = new HashSet<String>();
	@Persistent	private Set<String> importantIds = new HashSet<String>();
	@Persistent	private String playerAccountId;
	@Persistent	private String gameMasterAccountId;
	@Persistent	private Integer markerX = 0;
	@Persistent	private Integer markerY = 0;

	public Post() {
		super();
	}
	
	public Post(String characterId, String text) {
		setCharacterId(characterId);
		setText(text);
	}
	
	public String getCharacterId() {
		return characterId;
	}

	public void setCharacterId(String characterId) {
		this.characterId = characterId;
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
	 * @return the targetedCharacterId
	 */
	public String getTargetedCharacterId() {
		return targetedCharacterId;
	}

	/**
	 * @param targetedCharacterId the targetedCharacterId to set
	 */
	public void setTargetedCharacterId(String targetedCharacterId) {
		this.targetedCharacterId = targetedCharacterId;
	}

	/**
	 * @return Text
	 */
	public String getText() {
		if (text == null) return null;
		return text.getValue();
	}

	/**
	 * @param text Text to set
	 */
	public void setText(String text) {
		if (text == null || text.length() < 1) {
			throw new IllegalStateException("Post text is required");
		}
		this.text = new Text(text);
	}

	/**
	 * Return Text
	 */
	@Override
	public String toString() {
		return text.getValue();
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

	public Integer getMarkerX() {
		if (markerX == null) { 
			return new Integer(-50);
		}
		return markerX;
	}

	public void setMarkerX(Integer markerX) {
		this.markerX = markerX;
	}

	public Integer getMarkerY() {
		if (markerY == null) { 
			return new Integer(-50);
		}
		return markerY;
	}

	public void setMarkerY(Integer markerY) {
		this.markerY = markerY;
	}

	/**
	 * @return present Id
	 */
	public Set<String> getPresentIds() {
		return presentIds;
	}

	/**
	 * @param presentIds presentIds to set
	 */
	public void setPresentIds(Set<String> presentIds) {
		this.presentIds = presentIds;
	}

	/**
	 * @return important Id
	 */
	public Set<String> getImportantIds() {
		return importantIds;
	}

	/**
	 * @param importantIds importantIds to set
	 */
	public void setImportantIds(Set<String> importantIds) {
		this.importantIds = importantIds;
	}

	/**
	 * @param important the important to set
	 */
	public void addImportant(String characterId) {
		this.importantIds.add(characterId);
	}
	
	/**
	 * @param important the important to remove
	 */
	public void removeImportant(String characterId) {
		this.importantIds.remove(characterId);
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

}
