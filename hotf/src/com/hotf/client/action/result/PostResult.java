/**
 * 
 */
package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 *
 */
public class PostResult implements Result {

	private String id;
	private String characterId;
	private String locationId;
	private String createdOrder;
	private String targetedCharacterId;
	private String text;
	private Date created;
	private Date updated;
	private Boolean updatePermission;
	private String locationName;
	private String characterName;
	private String targetName;
	private Boolean important;
	private Integer markerY;
	private Integer markerX;
	private List<PostResult> posts = new ArrayList<PostResult>();
	private CharacterResult changedCharacter;

	public PostResult() {
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
	 * @return the characterId
	 */
	public String getCharacterId() {
		return characterId;
	}

	/**
	 * @param characterId the characterId to set
	 */
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
	 * @return the createdOrder
	 */
	public String getCreatedOrder() {
		return createdOrder;
	}

	/**
	 * @param createdOrder the createdOrder to set
	 */
	public void setCreatedOrder(String createdOrder) {
		this.createdOrder = createdOrder;
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
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return the characterName
	 */
	public String getCharacterName() {
		return characterName;
	}

	/**
	 * @param characterName the characterName to set
	 */
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	/**
	 * @return the targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * @param targetName the targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * @return the important
	 */
	public Boolean getImportant() {
		return important;
	}

	/**
	 * @param important the important to set
	 */
	public void setImportant(Boolean important) {
		this.important = important;
	}
	
	/**
	 * @return the markerY
	 */
	public Integer getMarkerY() {
		return markerY;
	}

	/**
	 * @param markerY the markerY to set
	 */
	public void setMarkerY(Integer markerY) {
		this.markerY = markerY;
	}

	/**
	 * @return the markerX
	 */
	public Integer getMarkerX() {
		return markerX;
	}

	/**
	 * @param markerX the markerX to set
	 */
	public void setMarkerX(Integer markerX) {
		this.markerX = markerX;
	}

	/**
	 * @return the posts
	 */
	public List<PostResult> getPosts() {
		return posts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void setPosts(List<PostResult> posts) {
		this.posts = posts;
	}

	public CharacterResult getChangedCharacter() {
		return changedCharacter;
	}

	public void setChangedCharacter(CharacterResult changedCharacter) {
		this.changedCharacter = changedCharacter;
	}

	
}