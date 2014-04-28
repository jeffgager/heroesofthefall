/**
 * 
 */
package com.hotf.client.action.validators;

import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.exception.ValidationException;

/**
 * @author Jeff
 *
 */
public class SkillValidator {

	private static final String INVALID_LEVEL = "Level must be between 0 and 9";
	private static final String INVALID_MODIFIER = "Modifier must be between 00 and 99";

	public void validate(CharacterGeneralSkillResult sr) {
		validate(sr.getLevel(), sr.getModifier());
	}

	public void validate(CharacterSkillResult sr) {
		validate(sr.getLevel(), sr.getModifier());
	}
	
	private void validate(int level, Integer modifier) {

		if (level < 0 || level > 9) {
			throw new ValidationException(INVALID_LEVEL);
		}

		if (level < 0 || level > 99) {
			throw new ValidationException(INVALID_MODIFIER);
		}

	}

}

