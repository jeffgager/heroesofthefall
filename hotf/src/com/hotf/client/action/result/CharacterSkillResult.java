package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class CharacterSkillResult implements Result {

	private CharacterGeneralSkillResult generalSkill;
	private GameSkillResult gameSkill;
	private Integer ranks;
	private Integer level;
	private Integer modifier;

	public CharacterSkillResult() {
		super();
	}

	public CharacterGeneralSkillResult getGeneralSkill() {
		return generalSkill;
	}
	
	public void setGeneralSkill(CharacterGeneralSkillResult generalSkill) {
		this.generalSkill = generalSkill;
	}
	
	public GameSkillResult getGameSkill() {
		return gameSkill;
	}

	public void setGameSkill(GameSkillResult gameSkill) {
		this.gameSkill = gameSkill;
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
