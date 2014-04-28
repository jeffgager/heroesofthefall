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
public class GameArmour extends ModelBase implements Serializable {

	@Persistent private Game game;
	@Persistent	private String name;
	@Persistent private Integer slashDefence;
	@Persistent private Integer crushDefence;
	@Persistent private Integer pierceDefence;
	@Persistent private Integer initiative;

	/**
	 * Empty constructor.
	 */
	public GameArmour() {
		super();
	}

	public GameArmour(Game game, String name) {
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

}