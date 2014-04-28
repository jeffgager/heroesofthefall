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
public class GameWeapon extends ModelBase implements Serializable {

	@Persistent private Game game;
	@Persistent	private String name;
	@Persistent private Integer minRange;
	@Persistent private Integer maxRange;
	@Persistent private Integer shotMinRange;
	@Persistent private Integer shotMaxRange;
	@Persistent private Boolean twoHanded;
	@Persistent private Integer defence;
	@Persistent private Integer slashDamage;
	@Persistent private Integer crushDamage;
	@Persistent private Integer pierceDamage;
	@Persistent private Integer damageRating;
	@Persistent private Integer strengthRating;
	@Persistent private Integer initiative;
	@Persistent private String[] skillNames;

	/**
	 * Empty constructor.
	 */
	public GameWeapon() {
		super();
	}

	public GameWeapon(Game game, String name) {
		super();
		this.game = game;
		this.name = name;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinRange() {
		return minRange;
	}

	public void setMinRange(Integer minRange) {
		this.minRange = minRange;
	}

	public Integer getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Integer maxRange) {
		this.maxRange = maxRange;
	}

	public Integer getDefence() {
		return defence;
	}

	public void setDefence(Integer defence) {
		this.defence = defence;
	}

	public Integer getSlashDamage() {
		return slashDamage;
	}

	public void setSlashDamage(Integer slashDamage) {
		this.slashDamage = slashDamage;
	}

	public Integer getCrushDamage() {
		return crushDamage;
	}

	public void setCrushDamage(Integer crushDamage) {
		this.crushDamage = crushDamage;
	}

	public Integer getPierceDamage() {
		return pierceDamage;
	}

	public void setPierceDamage(Integer pierceDamage) {
		this.pierceDamage = pierceDamage;
	}

	public Integer getDamageRating() {
		return damageRating;
	}

	public void setDamageRating(Integer damageRating) {
		this.damageRating = damageRating;
	}

	public Integer getStrengthRating() {
		return strengthRating;
	}

	public void setStrengthRating(Integer strength) {
		this.strengthRating = strength;
	}

	public Integer getInitiative() {
		return initiative;
	}

	public void setInitiative(Integer initiative) {
		this.initiative = initiative;
	}

	public String[] getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(String[] skillNames) {
		this.skillNames = skillNames;
	}

	public Integer getShotMinRange() {
		return shotMinRange;
	}

	public void setShotMinRange(Integer shotMinRange) {
		this.shotMinRange = shotMinRange;
	}

	public Integer getShotMaxRange() {
		return shotMaxRange;
	}

	public void setShotMaxRange(Integer shotMaxRange) {
		this.shotMaxRange = shotMaxRange;
	}

	public Boolean getTwoHanded() {
		return twoHanded;
	}

	public void setTwoHanded(Boolean twoHanded) {
		this.twoHanded = twoHanded;
	}

}