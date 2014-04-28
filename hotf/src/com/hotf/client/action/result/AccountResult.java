package com.hotf.client.action.result;
 
import java.util.Date;

import net.customware.gwt.dispatch.shared.Result;

public class AccountResult implements Result {

	private String id; 
	private String strongholdGameId;
	private String homeLocationId;
	private String playingCharacterId;
	private String personalCharacterId;
	private String name;
	private Integer fetchRows;
	private Integer searchRows;
	private Boolean showPortraits;
	private Boolean updatePermission;
	private Date tacAccepted;
	private Boolean administrator;
	
	public AccountResult() {
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
	 * @return the strongholdGameId
	 */
	public String getStrongholdGameId() {
		return strongholdGameId;
	}
	/**
	 * @param strongholdGameId the strongholdGameId to set
	 */
	public void setStrongholdGameId(String strongholdGameId) {
		this.strongholdGameId = strongholdGameId;
	}
	/**
	 * @return the homeLocationId
	 */
	public String getHomeLocationId() {
		return homeLocationId;
	}

	/**
	 * @param homeLocationId the homeLocationId to set
	 */
	public void setHomeLocationId(String homeLocationId) {
		this.homeLocationId = homeLocationId;
	}

	/**
	 * @return the playingCharacterId
	 */
	public String getPlayingCharacterId() {
		return playingCharacterId;
	}
	/**
	 * @param playingCharacterId the playingCharacterId to set
	 */
	public void setPlayingCharacterId(String playingCharacterId) {
		this.playingCharacterId = playingCharacterId;
	}

	/**
	 * @return the personalCharacterId
	 */
	public String getPersonalCharacterId() {
		return personalCharacterId;
	}

	/**
	 * @param personalCharacterId the personalCharacterId to set
	 */
	public void setPersonalCharacterId(String personalCharacterId) {
		this.personalCharacterId = personalCharacterId;
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
	 * @return the fetchRows
	 */
	public Integer getFetchRows() {
		return fetchRows;
	}
	/**
	 * @param fetchRows the fetchRows to set
	 */
	public void setFetchRows(Integer fetchRows) {
		this.fetchRows = fetchRows;
	}
	/**
	 * @return the searchRows
	 */
	public Integer getSearchRows() {
		return searchRows;
	}
	/**
	 * @param searchRows the searchRows to set
	 */
	public void setSearchRows(Integer searchRows) {
		this.searchRows = searchRows;
	}
	/**
	 * @return the showPortraits
	 */
	public Boolean getShowPortraits() {
		return showPortraits;
	}
	/**
	 * @param showPortraits the showPortraits to set
	 */
	public void setShowPortraits(Boolean showPortraits) {
		this.showPortraits = showPortraits;
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
	 * @return the tacAccepted
	 */
	public Date getTacAccepted() {
		return tacAccepted;
	}

	/**
	 * @param tacAccepted the tacAccepted to set
	 */
	public void setTacAccepted(Date tacAccepted) {
		this.tacAccepted = tacAccepted;
	}

	public Boolean getAdministrator() {
		return administrator;
	}

	public void setAdministrator(Boolean administrator) {
		this.administrator = administrator;
	}

}
