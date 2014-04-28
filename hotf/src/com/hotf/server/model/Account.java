package com.hotf.server.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION", extensions = { @Extension(vendorName = "datanucleus", key = "field-name", value = "version") })
public class Account implements Serializable {

	public static final String NAME_EXISTS_CHECK = "Name has already been used";
	public static final String NAME_LENGTH_CHECK  = "Name must be between 4 and 20 characters long";
	public static final String FETCH_ROWS_RANGE_CHECK = "Fetch rows must be between 10 and 50";
	public static final String SEARCH_ROWS_RANGE_CHECK = "Search rows must be between 3 and 10";
	public static final Integer DEFAULT_FETCH_ROWS = 20;
	public static final Integer DEFAULT_SEARCH_ROWS = 5;

	@PrimaryKey
	@Persistent	private String id;
	@Persistent	private int version;
	@Persistent	private User gaeUser;
	@Persistent	private String strongholdGameId;
	@Persistent	private String homeLocationId;
	@Persistent	private String personalCharacterId;
	@Persistent	private String playingCharacterId;
	@Persistent	private Date created;
	@Persistent	private Date tacAccepted;
	@Persistent	private Date updated;
	@Persistent	private String name;
	@Persistent	private String nameUpper;
	@Persistent	private Integer fetchRows = DEFAULT_FETCH_ROWS;
	@Persistent	private Integer searchRows = DEFAULT_SEARCH_ROWS;
	@Persistent	private boolean showPortraits = true;

	/**
	 * Empty constructor.
	 */
	public Account() {
		super();
	}

	/**
	 * GAE Account Constructor.
	 */
	public Account(User gaeUser) {
		this();
		setId(gaeUser.getUserId());
		setGaeUser(gaeUser);
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return Email Address
	 */
	public String getEmail() {
		return gaeUser.getEmail();
	}

	/**
	 * @return the gaeUser
	 */
	public User getGaeUser() {
		return gaeUser;
	}

	/**
	 * @param gaeUser the gaeUser to set
	 */
	public void setGaeUser(User gaeUser) {
		this.gaeUser = gaeUser;
	}

	/**
	 * @return the strongholdGameId
	 */
	public String getStrongholdGameId() {
		return strongholdGameId;
	}

	/**
	 * @param strongholdGameId
	 *            the strongholdGameId to set
	 */
	public void setStrongholdGameId(String strongholdGameId) {
		this.strongholdGameId = strongholdGameId;
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
	 * @return the playingCharacterId
	 */
	public String getPlayingCharacterId() {
		return playingCharacterId;
	}

	/**
	 * @param playingCharacterId
	 *            the playingCharacterId to set
	 */
	public void setPlayingCharacterId(String playingCharacterId) {
		this.playingCharacterId = playingCharacterId;
	}

	/**
	 * @return Date created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param Date
	 *            created
	 */
	public void setCreated(Date created) {
		this.created = created;
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

	/**
	 * @return Date updated
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated
	 *            Date to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * @return Account name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
		setNameUpper(name.toUpperCase());
	}

	/**
	 * @return the nameUpper
	 */
	public String getNameUpper() {
		return nameUpper;
	}

	/**
	 * @param nameUpper
	 *            the nameUpper to set
	 */
	public void setNameUpper(String nameUpper) {
		this.nameUpper = nameUpper;
	}

	/**
	 * @return account name
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the fetchRows
	 */
	public Integer getFetchRows() {
		return fetchRows;
	}

	/**
	 * @param fetchRows
	 *            the fetchRows to set
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
	 * @param searchRows
	 *            the searchRows to set
	 */
	public void setSearchRows(Integer searchRows) {
		this.searchRows = searchRows;
	}

	/**
	 * @return the showPortraits
	 */
	public boolean getShowPortraits() {
		return showPortraits;
	}

	/**
	 * @param showPortraits
	 *            the showPortraits to set
	 */
	public void setShowPortraits(boolean showPortraits) {
		this.showPortraits = showPortraits;
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

}
