package com.hotf.client.action.result;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameSkillResult implements Result {

	private GameGeneralSkillResult generalSkill;
	private String name;
	private String attribute;

	public GameSkillResult() {
		super();
	}

	public GameGeneralSkillResult getGeneralSkill() {
		return generalSkill;
	}

	public void setGeneralSkill(GameGeneralSkillResult generalSkill) {
		this.generalSkill = generalSkill;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
