package com.hotf.client.action;

import net.customware.gwt.dispatch.shared.Action;

import com.hotf.client.action.result.CharacterResult;

public class SaveCharacterAction implements Action<CharacterResult> {

	private CharacterResult characterResult;
	private Boolean duplicate;

	public SaveCharacterAction() {
		super();
	}

	/**
	 * @param characterResult
	 */
	public SaveCharacterAction(CharacterResult characterResult) {
		super();
		this.duplicate = false;
		this.characterResult = characterResult;
	}

	/**
	 * @return the characterResult
	 */
	public CharacterResult getCharacterResult() {
		return characterResult;
	}

	public Boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(Boolean duplicate) {
		this.duplicate = duplicate;
	}

}
