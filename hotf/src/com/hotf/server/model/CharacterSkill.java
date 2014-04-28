package com.hotf.server.model;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class CharacterSkill extends ModelBase implements Serializable {

	@Persistent private CharacterGeneralSkill generalSkill;
	@Persistent	private String name;
	@Persistent	private Integer ranks;
	@Persistent	private Integer level;
	@Persistent	private Integer modifier;

	/**
	 * Empty constructor.
	 */
	public CharacterSkill() {
		super();
	}

	/**
	 * Character Constructor.
	 * @param name Name
	 */
	public CharacterSkill(CharacterGeneralSkill generalSkill, String name) {
		super();
		setGeneralSkill(generalSkill);
		setName(name);
	}

	public CharacterGeneralSkill getGeneralSkill() {
		return generalSkill;
	}

	public void setGeneralSkill(CharacterGeneralSkill generalSkill) {
		this.generalSkill = generalSkill;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRanks() {
		return ranks;
	}

	public void setRanks(Integer ranks) {
		this.ranks = ranks;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

}