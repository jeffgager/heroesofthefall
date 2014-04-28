package com.hotf.client.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.hotf.client.action.result.CharacterGeneralSkillResult;

public class LoadSkillEvent extends GwtEvent<LoadSkillEventHandler> {

	public static Type<LoadSkillEventHandler> TYPE = new Type<LoadSkillEventHandler>();

	private CharacterGeneralSkillResult skill;

	public LoadSkillEvent(CharacterGeneralSkillResult skill) {
		super();
		this.skill = skill;
		GWT.log("Firing LoadSkillEvent");
	}

	@Override
	public Type<LoadSkillEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadSkillEventHandler handler) {
		handler.onLoadSkill(this);
	}

	/**
	 * @return the skill
	 */
	public CharacterGeneralSkillResult getSkill() {
		return skill;
	}

}
