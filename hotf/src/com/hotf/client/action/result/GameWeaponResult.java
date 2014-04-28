package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameWeaponResult extends GameImplementResult implements Result {

	private Integer minRange;
	private Integer maxRange;
	private Boolean twoHanded;
	private Integer shotMinRange;
	private Integer shotMaxRange;
	private Integer defence;
	private Integer slashDamage;
	private Integer crushDamage;
	private Integer pierceDamage;
	private Integer damageRating;
	private Integer strengthRating;
	private Integer initiative;

	public GameWeaponResult() {
		super();
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

	public void setStrengthRating(Integer strengthRating) {
		this.strengthRating = strengthRating;
	}

	public Integer getInitiative() {
		return initiative;
	}

	public void setInitiative(Integer initiative) {
		this.initiative = initiative;
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
