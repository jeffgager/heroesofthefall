package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameArmourResult implements Result {

	private String name;
	private Integer slashDefence;
	private Integer crushDefence;
	private Integer pierceDefence;
	private Integer initiative;

	public GameArmourResult() {
		super();
	}

	public String getName() {
		return name;
	}

	public Integer getSlashDefence() {
		return slashDefence;
	}

	public void setSlashDefence(Integer slashDefence) {
		this.slashDefence = slashDefence;
	}

	public Integer getCrushDefence() {
		return crushDefence;
	}

	public void setCrushDefence(Integer crushDefence) {
		this.crushDefence = crushDefence;
	}

	public Integer getPierceDefence() {
		return pierceDefence;
	}

	public void setPierceDefence(Integer pierceDefence) {
		this.pierceDefence = pierceDefence;
	}

	public Integer getInitiative() {
		return initiative;
	}

	public void setInitiative(Integer initiative) {
		this.initiative = initiative;
	}

	public void setName(String name) {
		this.name = name;
	}

}
