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
public class GameArtifact extends ModelBase implements Serializable {

	@Persistent private Game game;
	@Persistent	private String name;
	@Persistent private Integer effect;
	@Persistent private String[] skillNames;

	/**
	 * Empty constructor.
	 */
	public GameArtifact() {
		super();
	}

	public GameArtifact(Game game, String name) {
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

	public Integer getEffect() {
		return effect;
	}

	public void setEffect(Integer effect) {
		this.effect = effect;
	}

	public String[] getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(String[] skillNames) {
		this.skillNames = skillNames;
	}

}