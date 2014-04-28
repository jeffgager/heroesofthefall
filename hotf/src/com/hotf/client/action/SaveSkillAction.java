package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.CharacterGeneralSkillResult;

public class SaveSkillAction implements Action<CharacterGeneralSkillResult> {

	private CharacterGeneralSkillResult skillResult;
	private String characterId;
	private boolean remove = false;

	public SaveSkillAction() {
		super();
	}

	public SaveSkillAction(String characterId, CharacterGeneralSkillResult skillResult) {
		super();
		this.characterId = characterId;
		this.skillResult = skillResult;
		this.remove = false;
	}

	public SaveSkillAction(CharacterGeneralSkillResult skillResult, boolean remove) {
		super();
		this.skillResult = skillResult;
		this.remove = remove;
	}

	/**
	 * @return the skillResult
	 */
	public CharacterGeneralSkillResult getSkillResult() {
		return skillResult;
	}

	/**
	 * @param skillResult the skillResult to set
	 */
	public void setSkillResult(CharacterGeneralSkillResult skillResult) {
		this.skillResult = skillResult;
	}

	/**
	 * @return the characterId
	 */
	public String getCharacterId() {
		return characterId;
	}

	/**
	 * @param characterId the characterId to set
	 */
	public void setCharacterId(String characterId) {
		this.characterId = characterId;
	}

	/**
	 * @return the remove
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * @param remove the remove to set
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

}
