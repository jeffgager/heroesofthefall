package com.hotf.server.model;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION", extensions = { @Extension(vendorName = "datanucleus", key = "field-name", value = "version") })
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public class ModelBase implements Serializable {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String id;

	@Persistent
	private int version;

	public ModelBase() {
		super();
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

}
