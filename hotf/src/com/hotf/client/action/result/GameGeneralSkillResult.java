package com.hotf.client.action.result;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameGeneralSkillResult implements Result {

	private String name;
	private List<GameSkillResult> skills;

	public GameGeneralSkillResult() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GameSkillResult> getSkills() {
		return skills;
	}

	public void setSkills(List<GameSkillResult> skills) {
		this.skills = skills;
	}

}
