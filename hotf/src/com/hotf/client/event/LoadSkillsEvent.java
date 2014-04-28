package com.hotf.client.event;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterGeneralSkillResult;

public class LoadSkillsEvent extends GwtEvent<LoadSkillsEventHandler> {

	public static Type<LoadSkillsEventHandler> TYPE = new Type<LoadSkillsEventHandler>();

	private List<CharacterGeneralSkillResult> skills;
	
	public LoadSkillsEvent(List<CharacterGeneralSkillResult> skills) {
		super();
		this.skills = skills;
		GWT.log("Firing LoadSkillsEvent");
	}

	@Override
	public Type<LoadSkillsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadSkillsEventHandler handler) {
		handler.onLoadSkills(this);
	}
	
	public List<CharacterGeneralSkillResult> getSkills() {
		return skills;
	}

}
