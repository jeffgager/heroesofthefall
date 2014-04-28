package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class CharacterGeneralSkillResult implements Result {

	private GameGeneralSkillResult gameGeneralSkill;
	private Integer ranks;
	private Integer level;
	private Integer modifier;
	private List<CharacterSkillResult> skills = new ArrayList<CharacterSkillResult>();

	public CharacterGeneralSkillResult() {
		super();
	}

	public GameGeneralSkillResult getGameGeneralSkill() {
		return gameGeneralSkill;
	}

	public void setGameGeneralSkill(GameGeneralSkillResult gameGeneralSkill) {
		this.gameGeneralSkill = gameGeneralSkill;
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

	public List<CharacterSkillResult> getSkills() {
		return skills;
	}

	public void setSkills(List<CharacterSkillResult> skills) {
		this.skills = skills;
	}

}
