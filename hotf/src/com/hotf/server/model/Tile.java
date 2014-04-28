package com.hotf.server.model;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable = "true", identityType = IdentityType.APPLICATION)
public class Tile extends ModelBase implements Serializable {

	@Persistent	private String name;
	@Persistent	private String nameUpper;
	@Persistent	private BlobKey blob;

	/**
	 * Constructor.
	 */
	public Tile() {
		super();
	}

	/**
	 * Constructor.
	 * @param name of new location
	 */
	public Tile(String name) {
		super();
		setName(name);
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

}
