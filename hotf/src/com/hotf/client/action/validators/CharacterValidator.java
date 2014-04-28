/**
 * 
 */
package com.hotf.client.action.validators;

import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.exception.ValidationException;

/**
 * @author Jeff
 *
 */
public class CharacterValidator {

	private static final String NAME_SIZE = "Name must be between 2 and 35 characters long";
	private static final String STATUS_SIZE = "Status must be no more than 255 characters long";
	private static final String ATTRIBUTES_TOO_HIGH = "You do not have enough Character Points to have these attributes ";
	private static final String CHARACTER_POINTS_INVALID = "Character points must be between 0 and 32";
	private static final String ATTRIBUTE_INVALID = " must be between 0 and 9";
	private static final String AGE_INVALID = "Age must be between 0 and 100";
	private static final String HANDED_INVALID = "Must be (R)ight, (L)eft or (A)mbidextrous";
	private static final String SKILL_RANKS_INVALID = "Must be (3)Novice, (6)Jouneyman or (9)Expert";
	private static final String SKILL_RANKS_TOO_MANY = "Too many skill ranks";

	private static final int[] ATTRIBUTE_COST = {0, -4, -2, -1, 0, 1, 2, 4, 8, 16};
	private static final int[] SPIRIT_ATTRIBUTE_COST = {0, 2, 3, 4, 6, 8, 10, 12, 14, 16};
	
	private SkillValidator skillValidator;
	private int characterPointCost;
	private int skillRankCost = 0;
	
	public CharacterValidator(SkillValidator skillValidator) {
		this.skillValidator = skillValidator;
	}

	public void validate(CharacterResult c) {

		if (c.getName() == null) {
			throw new ValidationException(NAME_SIZE);
		}
		int namelength = c.getName().length();
		if (namelength < 2 || namelength > 35) {
			throw new ValidationException(NAME_SIZE);
		}

		if (c.getId() == null || c.getId().length() == 0) {
			return;
		}

		if (c.getStatus() != null && c.getStatus().length() > 255) {
			throw new ValidationException(STATUS_SIZE);
		}

		Integer vigour = c.getVigor();
		if (vigour != null && (vigour < 0 || vigour > 9)) {
			throw new ValidationException("Vigour" + ATTRIBUTE_INVALID);
		}
		Integer mettle = c.getMettle();
		if (mettle != null && (mettle < 0 || mettle > 9)) {
			throw new ValidationException("Mettle" + ATTRIBUTE_INVALID);
		}
		Integer glamour = c.getGlamour();
		if (glamour != null && (glamour < 0 || glamour > 9)) {
			throw new ValidationException("Glamour" + ATTRIBUTE_INVALID);
		}
		Integer wit = c.getWit();
		if (wit != null && (wit < 0 || wit > 9)) {
			throw new ValidationException("Wit" + ATTRIBUTE_INVALID);
		}
		Integer spirit = c.getSpirit();
		if (spirit != null && (spirit < 0 || spirit > 9)) {
			throw new ValidationException("Spirit" + ATTRIBUTE_INVALID);
		}

		characterPointCost = 0;
		Integer cp = c.getCharacterPoints();
		if (cp != null && cp > 0) {
			if (cp > 32) {
				throw new ValidationException(CHARACTER_POINTS_INVALID);
			}
			characterPointCost = ATTRIBUTE_COST[vigour] + 
				ATTRIBUTE_COST[mettle] + 
				ATTRIBUTE_COST[wit] + 
				ATTRIBUTE_COST[glamour] + 
				SPIRIT_ATTRIBUTE_COST[spirit];
			if (characterPointCost > cp) {
				throw new ValidationException(ATTRIBUTES_TOO_HIGH + characterPointCost + " / " + cp);
			}
		}
		String handed = c.getHanded();
		if (handed != null && handed.length() == 1 && !"LRA".contains(handed)) {
			throw new ValidationException(HANDED_INVALID);
		}
		Integer age = c.getAge();
		if (age != null && age != 0 && (age.intValue() < 0 || age.intValue() > 100)) {
			throw new ValidationException(AGE_INVALID);
		}
		int ranks = c.getSkillRanks() != null ? c.getSkillRanks() : 0;
		if (ranks > 0) {
			if (ranks != 3 && ranks != 6 && ranks != 9) {
				throw new ValidationException(SKILL_RANKS_INVALID);
			}
		}

		skillRankCost = 0;

		for (CharacterGeneralSkillResult gs : c.getGeneralSkills()) {
			skillRankCost += gs.getRanks() == null ? 0 : gs.getRanks();
			skillValidator.validate(gs);
			for (CharacterSkillResult s : gs.getSkills()) {
				skillRankCost += s.getRanks() == null ? 0 : s.getRanks();
				skillValidator.validate(s);
			}
		}
		if (ranks != 0 && skillRankCost > ranks) {
			throw new ValidationException(SKILL_RANKS_TOO_MANY);
		}

	}

	public int getCharacterPointCost() {
		return characterPointCost;
	}

	public int getSkillRankCost() {
		return skillRankCost;
	}

}
