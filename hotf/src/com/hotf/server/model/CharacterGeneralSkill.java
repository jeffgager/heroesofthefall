package com.hotf.server.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class CharacterGeneralSkill extends ModelBase implements Serializable {

	@Persistent private Character character;
	@Persistent	private String name;
	@Persistent	private Integer ranks;
	@Persistent	private Integer level;
	@Persistent	private Integer modifier;
	@Persistent(mappedBy = "generalSkill") @Element(dependent = "true") private List<CharacterSkill> skills;

	/**
	 * Empty constructor.
	 */
	public CharacterGeneralSkill() {
		super();
	}

	/**
	 * Character Constructor.
	 * @param name Name
	 */
	public CharacterGeneralSkill(Character character, String name) {
		super();
		setCharacter(character);
		setName(name);
	}

	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
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

	public List<CharacterSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<CharacterSkill> skills) {
		this.skills = skills;
	}

}