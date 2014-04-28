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
public class PlaceResult implements Result {

	private String id;
	private String gameId;
	private String name;
	private String description;
	private Boolean updatePermission;
	private String label;
	private Date created;
	private String createdOrder;
	private Date updated;
	private String type;
	private String owner;
	private List<CharacterResult> characters = new ArrayList<CharacterResult>();
	private List<PlaceResult> locations = new ArrayList<PlaceResult>();
	private Long postCount;
	private Boolean hasMap;
	private Boolean hasOverlay;

	public PlaceResult() {
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
	 * @return the gameId
	 */
	public String getGameId() {
		return gameId;
	}
	/**
	 * @param gameId the gameId to set
	 */
	public void setGameId(String gameId) {
		this.gameId = gameId;
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

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}
	
	public Long getPostCount() {
		return postCount;
	}

	/**
	 * @return the hasOverlay
	 */
	public Boolean getHasOverlay() {
		return hasOverlay;
	}

	/**
	 * @param hasOverlay the hasOverlay to set
	 */
	public void setHasOverlay(Boolean hasOverlay) {
		this.hasOverlay = hasOverlay;
	}

	/**
	 * @return the hasMap
	 */
	public Boolean getHasMap() {
		return hasMap;
	}

	/**
	 * @param hasMap the hasMap to set
	 */
	public void setHasMap(Boolean hasMap) {
		this.hasMap = hasMap;
	}

}