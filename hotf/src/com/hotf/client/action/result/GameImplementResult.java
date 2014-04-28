package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 * 
 */
public class GameImplementResult implements Result {

	private String name;
	private List<String> skillNames = new ArrayList<String>();

	public GameImplementResult() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(List<String> skillNames) {
		this.skillNames = skillNames;
	}

}
