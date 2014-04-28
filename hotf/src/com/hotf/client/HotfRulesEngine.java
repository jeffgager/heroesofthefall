package com.hotf.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import com.google.gwt.i18n.client.NumberFormat;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.action.result.GameWeaponResult;

public class HotfRulesEngine {

	private static final Integer[][] FEAT_TABLE = {
		{2, 2, 2},
		{2, 2, 3},
		{2, 2, 5},
		{2, 2, null},
		{2, 3, null},
		{3, 3, null},
		{2, null, null},
		{3, null, null},
		{5, null, null},
		{7, null, null},
		{8, null, null},
		{7, 7, null},
		{8, 7, null},
		{8, 8, null},
		{8, 8, 5},
		{8, 8, 7},
		{8, 8, 8}
	};

	public static enum OUTCOME {PARRY, BLOCK, MINOR, SERIOUS, CRITICAL};

	private static final String DAMAGE[][] = {
		{"bruising, stun", "fracture, unconcious", "instant death", "bleeding", "serious bleeding, shock", "instant death", "bleeding, stun", "serious bleeding, shock", "instant death"},
		{"temporarily blind, shock", "fracture, blinded, unconcious", "slow death", "bleeding, blind, shock", "serious bleeding, blind, shock", "slow death", "bleeding, blind, shock", "serious bleeding, blind, shock", "instant death"},
		{"temporarily blind, shock", "fracture, blinded, unconcious", "slow death", "bleeding, blind, shock", "serious bleeding, blind, shock", "slow death", "bleeding, blind, shock", "serious bleeding, blind, shock", "instant death"},
		{"fracture, stun", "fracture, unconcious", "slow death", "bleeding, stun", "serious bleeding, shock", "slow death", "bleeding, stun", "serious bleeding, stun", "slow death"},
		{"bruising, stun", "bruising, unconcious", "instant death", "bleeding", "serious bleeding, shock", "decapitated", "bleeding, stun", "serious bleeding, shock", "instant death"},
		{"bruising, stun", "bruising, shock", "instant death", "bleeding, stun", "serious bleeding, shock", "decapitated", "bleeding, stun", "serious bleeding, shock", "instant death"},
		{"bruising", "fracture, fumble, stun", "crush, shock", "bleeding", "serious bleeding, fumble, stun", "severed arm, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising", "fracture, fumble, stun", "crush, shock", "bleeding", "serious bleeding, fumble, stun", "severed arm, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising", "fracture, fumble, stun", "crush, shock", "bleeding", "serious bleeding, fumble, stun", "severed arm, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising", "fracture, fumble, stun", "crush, shock", "bleeding", "serious bleeding, fumble, stun", "severed arm, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising, fumble", "fracture, fumble, stun", "crush, shock", "bleeding, fumble", "serious bleeding, stun", "severed hand, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising, fumble", "fracture, fumble, stun", "crush, shock", "bleeding, fumble", "serious bleeding, stun", "severed hand, shock", "bleeding, fumble", "serious bleeding, stun", "serious bleeding, shock"},
		{"bruising", "fracture, buising, stun", "crush, slow death", "bleeding", "serious bleeding, shock", "slow death", "bleeding, stun", "serious bleeding, shock", "instant death"},
		{"bruising, stun", "buising, shock", "slow death", "bleeding", "serious bleeding, shock", "slow death", "bleeding, stun", "serious bleeding, shock", "slow death"},
		{"bruising", "fracure, stumble, shock", "crush, shock", "bleeding", "serious bleeding, stun", "serious bleeding, shock", "bleeding", "serious bleeding, stumnle, stun", "serious bleeding, shock"},
		{"bruising, stun", "shock, stumble", "slow death, fracture", "serious bleeding, stun", "serious bleeding, shock", "slow death", "serious bleeding, shock", "serious bleeding, shock", "instant death"},
		{"bruising, stumble", "fracture, stumble, shock", "crush, shock", "bleeding", "serious bleeding, shock", "severed leg, shock", "bleeding", "serious bleeding, stumble, stun", "serious bleeding, shock"},
		{"bruising, stumble", "fracture, stumble, shock", "crush, shock", "bleeding", "serious bleeding, shock", "severed leg, shock", "bleeding", "serious bleeding, stumble, stun", "serious bleeding, shock"},
		{"bruising, stumble", "fracture, stumble, stun", "crush, stumble, shock", "bleeding, stumble", "serious bleeding", "severed leg, shock", "bleeding", "serious bleeding", "serious bleeding, shock"},
		{"bruising, stumble", "fracture, stumble, stun", "crush, stumble, shock", "bleeding, stumble", "serious bleeding", "severed leg, shock", "bleeding", "serious bleeding", "serious bleeding, shock"},
		{"bruising, stumble", "fracture, stumble, stun", "crush, stumble, shock", "bleeding, stumble", "serious bleeding", "severed foot, shock", "bleeding, stumble", "serious bleeding, stumble, stun", "serious bleeding, shock"},
		{"bruising, stumble", "fracture, stumble, stun", "crush, stumble, shock", "bleeding, stumble", "serious bleeding", "severed foot, shock", "bleeding, stumble", "serious bleeding, stumble, stun", "serious bleeding, shock"},
		{"bruising", "fracture, buising, stun", "crush, slow death", "bleeding", "serious bleeding, shock", "slow death", "bleeding, stun", "serious bleeding, shock", "instant death"},
	};

	private ClientFactory clientFactory;
	private SkillChecker skillChecker = new SkillChecker();
	
	public Collection<CombatAction> getCharacterActions(Collection<CharacterResult> characters) {

		final TreeSet<CombatAction> combatActions = new TreeSet<CombatAction>(new Comparator<CombatAction>() {
			NumberFormat nf = NumberFormat.getFormat("00");
			@Override
			public int compare(CombatAction ca1, CombatAction ca2) {
				return (nf.format(ca2.getInitiative()) + ca2.getCharacter().getId()).compareTo(
						nf.format(ca1.getInitiative()) + ca1.getCharacter().getId());
			}
		});

		for (CharacterResult character : characters) {

			CombatAction left = new CombatAction(character, "L");
			CombatAction right = new CombatAction(character, "R");
			if (left.getWeapon().getTwoHanded() || right.getWeapon().getTwoHanded()) {
				if (left.getWeapon().getName().equals(right.getWeapon().getName())) {
					if ("R".equals(character.getHanded())) {
						combatActions.add(right);
					} else {
						combatActions.add(left);
					}
				}
			} else {
				combatActions.add(left);
				combatActions.add(right);
			}

		}
		
		return combatActions;

	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private int integerToInt(Integer i) {
		return i == null ? 0 : i.intValue();
	}

	private int distance(int x1, int y1, int x2, int y2) {
		return (int)((Math.sqrt(
				Math.pow(Math.abs(x1 - x2), 2) + 
				Math.pow(Math.abs(y1 - y2), 2)
				) / 30.0D) * 3.0);
	}


	private int getWeaponDefence(String strike, String attackingWeaponName, int range, GameWeaponResult defendingWeapon) {
		
		int wd = 0;
		if ("Shoot".equals(strike)) {
			if (attackingWeaponName.toLowerCase().contains("stone")) {
				if (range < 5) {
					wd = 99;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 6;
				} else if (range >= 81 && range <= 160) {
					wd = 8;
				} else if (range > 160) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("dagger") ||
					   attackingWeaponName.toLowerCase().contains("fransisca")) {
				if (range < 5) {
					wd = 99;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 6;
				} else if (range > 80) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("spear")) {
				if (range < 5) {
					wd = 99;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 5;
				} else if (range > 80) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("javelin") ||
					   attackingWeaponName.toLowerCase().contains("dart")) {
				if (range < 5) {
					wd = 99;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 5;
				} else if (range >= 81 && range <= 160) {
					wd = 7;
				} else if (range > 160) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("sling")) {
				if (range < 11) {
					wd = 99;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 5;
				} else if (range >= 81 && range <= 160) {
					wd = 6;
				} else if (range >= 161 && range <= 320) {
					wd = 7;
				} else if (range > 320) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("crossbow")) {
				if (range < 3) {
					wd = 99;
				} else if (range == 3) { 
					wd = 0;
				} else if (range == 4) { 
					wd = 1;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 5;
				} else if (range >= 81 && range <= 160) {
					wd = 6;
				} else if (range >= 161 && range <= 320) {
					wd = 7;
				} else if (range >= 321 && range <= 640) {
					wd = 8;
				} else if (range > 640) {
					wd = 99;
				}
			} else if (attackingWeaponName.toLowerCase().contains("bow")) {
				if (range < 5) {
					wd = 99;
				} else if (range >= 5 && range <= 10) { 
					wd = 2;
				} else if (range >= 11 && range <= 20) {
					wd = 3;
				} else if (range >= 21 && range <= 40) {
					wd = 4;
				} else if (range >= 41 && range <= 80) {
					wd = 5;
				} else if (range >= 81 && range <= 160) {
					wd = 6;
				} else if (range >= 161 && range <= 320) {
					wd = 7;
				} else if (range >= 321 && range <= 640) {
					wd = 8;
				} else if (range > 640) {
					wd = 99;
				}
			}
		} else {
			wd = defendingWeapon == null ? 0 : integerToInt(defendingWeapon.getDefence());
		}

		return wd;

	}
	
	private int getTargetAreaModifier(int targetArea) {
		int targetAreaFactor = 0;
		if (targetArea == 1 || targetArea == 2) {
			targetAreaFactor = 3;
		} else if (targetArea == 3 || targetArea == 5 || targetArea == 15) {
			targetAreaFactor = 2;
		} else if (targetArea == 4 || targetArea == 10 || targetArea == 11 || targetArea == 20 || targetArea == 21) {
			targetAreaFactor = 1;
		}
		return targetAreaFactor;
	}

	public class SkillChecker {

		private int bestLevel;
		private int bestMod;

		private void findBestSkill(CharacterResult character, String skill) {
			for (CharacterGeneralSkillResult cgs : character.getGeneralSkills()) {
				if (skill.equals(cgs.getGameGeneralSkill().getName()) && cgs.getLevel() > bestLevel) {
					bestLevel = cgs.getLevel();
					if ("Player".equals(character.getCharacterType())) {
						bestMod = cgs.getModifier();
					}
				}
				for (CharacterSkillResult cs : cgs.getSkills()) {
					if (skill.equals(cs.getGameSkill().getName()) && cs.getLevel() > bestLevel) {
						bestLevel = cs.getLevel();
						if ("Player".equals(character.getCharacterType())) {
							bestMod = cs.getModifier();
						}
					}
				}
			}
		}
		
		public void findBestWeaponSkill(CharacterResult character, GameWeaponResult weapon) {

			bestLevel = 0;
			bestMod = 0;

			if (weapon == null) {
				findBestSkill(character, "Dodge");
				return;
			}

			for (String wsn : weapon.getSkillNames()) {
				findBestSkill(character, wsn);
			}
			
		}
		
		public int getBestLevel() {
			return bestLevel;
		}
		
		public int getBestMod() {
			return bestMod;
		}
		
	}

	public class CombatAction {
		
		private CharacterResult character;
		private String grip;
		private String strike;
		private GameWeaponResult weapon;
		private int weaponLevel;
		private int weaponMod;
		private int initiative;
		private Integer slashDamage;
		private Integer crushDamage;
		private Integer pierceDamage;
		private int weaponDamage;
		private boolean offHand;
		private int areaIndex;
		private String areaName;
		private int areaModifier;
		private CharacterResult target;
		private String targetArmour;
		private int targetSlashDefence;
		private int targetCrushDefence;
		private int targetPierceDefence;
		private int targetArmourDefence;
		private int targetRange;
		private boolean targetOutOfRange;
		private GameWeaponResult targetWeapon;
		private boolean targetOffHand;
		private int targetWeaponDefence;
		private int targetWeaponLevel;
		private int targetWeaponMod;
		private int feat;
		private String damageType;
		private boolean acting = false;
		private boolean actorVisible = false;
		private boolean bothVisible = false;
		private OUTCOME outcome;
		private int modifier = 0;

		public CombatAction(CharacterResult character, String hand) {

			this.character = character;

			GameController gc = clientFactory.getGameController();

			//Attacker skill
			if ("L".equals(hand)) {
				strike = character.getLeftTargetStrike();
				weapon = gc.getWeapon(character.getLeftHand());
				areaIndex = character.getLeftTargetArea();
				offHand = "R".equals(character.getHanded()) ? true : false;
				grip = "Left Hand";
				target = gc.getCharacterById(character.getLeftTargetCharacterId());
			} else if ("R".equals(hand)) {
				strike = character.getRightTargetStrike();
				weapon = gc.getWeapon(character.getRightHand());
				areaIndex = character.getRightTargetArea();
				offHand = "L".equals(character.getHanded()) ? true : false;
				grip = "Right Hand";
				target = gc.getCharacterById(character.getRightTargetCharacterId());
			}
			if (weapon.getTwoHanded()) {
				grip = "Both Hands";
			}
			areaName = gc.getTargetArea(areaIndex);
			skillChecker.findBestWeaponSkill(character, weapon);
			weaponLevel = skillChecker.getBestLevel() - (offHand? 1 : 0);
			weaponMod = skillChecker.getBestMod();
			acting = !("Defend".equals(strike) || target == null);
			initiative = acting ? (weaponLevel + weapon.getInitiative()) : 0;
			slashDamage = weapon.getSlashDamage();
			crushDamage = weapon.getCrushDamage();
			pierceDamage = weapon.getPierceDamage();
			actorVisible = character.getUpdatePermission();
			bothVisible = character.getUpdatePermission() && target != null && target.getUpdatePermission();

			//Target defence
			if (acting) {
				targetArmour = target.getArmourType()[areaIndex];
				targetSlashDefence = integerToInt(target.getSlashArmour()[areaIndex]);
				targetCrushDefence = integerToInt(target.getCrushArmour()[areaIndex]);
				targetPierceDefence = integerToInt(target.getPierceArmour()[areaIndex]);
				targetRange = distance(character.getTokenX().intValue(), character.getTokenY().intValue(),
						target.getTokenX().intValue(), target.getTokenY().intValue());
				if ("Shoot".equals(strike)) {
					targetOutOfRange = targetRange < weapon.getShotMinRange() || targetRange > weapon.getShotMaxRange();
				} else {
					targetOutOfRange = targetRange < weapon.getMinRange() || targetRange > weapon.getMaxRange();
				}
				if (!targetOutOfRange) {
					if ("Shoot".equals(strike)) {
						targetWeapon = gc.getWeapon("Dodge");
						targetOffHand = false;
					} else if ("L".equals(target.getDefence())) {
						targetWeapon = gc.getWeapon(target.getLeftHand());
						targetOffHand = "R".equals(target.getHanded()) ? true : false;
					} else if ("R".equals(target.getDefence())) {
						targetWeapon = gc.getWeapon(target.getRightHand());
						targetOffHand = "L".equals(target.getHanded()) ? true : false;
					} else {
						targetWeapon = gc.getWeapon("Dodge");
						targetOffHand = false;
					}
					targetWeaponDefence = getWeaponDefence(strike, weapon.getName(), targetRange, targetWeapon);
					skillChecker.findBestWeaponSkill(target, targetWeapon);
					targetWeaponLevel = skillChecker.getBestLevel() - (targetOffHand ? 1 : 0);
					targetWeaponMod = skillChecker.getBestMod();
				}
			}

			//Feat
			areaModifier = getTargetAreaModifier(areaIndex);
			Integer slashFeat = null;
			if (slashDamage != null) {
				slashFeat = (targetWeaponLevel + targetSlashDefence + targetWeaponDefence) -
						  (weaponLevel + slashDamage + areaModifier);
			}
			Integer pierceFeat = null;
			if (pierceDamage != null) {
				pierceFeat = (targetWeaponLevel + targetPierceDefence + targetWeaponDefence) -
						  (weaponLevel + pierceDamage + areaModifier);
			}
			Integer crushFeat = 0;
			if (crushDamage != null) {
				crushFeat = (targetWeaponLevel + targetCrushDefence + targetWeaponDefence) -
						  (weaponLevel + crushDamage + areaModifier);
			}
			if ("Swing".equals(strike) && slashFeat != null && (slashFeat <= crushFeat || crushFeat == null)) {
				damageType = "Slash";
				feat = slashFeat;
				targetArmourDefence = targetSlashDefence;
				weaponDamage = slashDamage == null ? 0 : slashDamage;
			} else if (("Thrust".equals(strike) || "Shoot".equals(strike)) && pierceFeat != null && (pierceFeat <= crushFeat || crushFeat == null)) {
				damageType = "Pierce";
				feat = pierceFeat;
				targetArmourDefence = targetPierceDefence;
				weaponDamage = pierceDamage == null ? 0 : pierceDamage;
			} else {
				damageType = "Crush";
				feat = crushFeat;
				targetArmourDefence = targetCrushDefence;
				weaponDamage = crushDamage == null ? 0 : crushDamage;
			}

		}

		public String getNarrative() {
			
			StringBuffer sb = new StringBuffer();
			
			if (acting && bothVisible) {
				sb.append("(" + initiative + ") ");
			}
			sb.append(character.getName() + " ");
			String off = offHand ? " (Off Hand)" : "";
			if ("Defend".equals(strike)) {
				sb.append("defending with ");
				sb.append(grip);
				sb.append(off + " ");
				sb.append(weapon.getName());
			} else if (target == null || !acting) {
				sb.append("holding ");
				sb.append(weapon.getName());
				sb.append(" in " + grip + off);
			} else if (!actorVisible) {
				sb.append(strike);
				sb.append(" ");
				sb.append(weapon.getName());
				sb.append(" with " + grip + off + " ");
			} else {
				sb.append(strike);
				sb.append(" ");
				sb.append(weapon.getName());
				sb.append(" with " + grip + off + " ");
				sb.append(" at ");
				sb.append(areaName);
				sb.append(" (");
				sb.append(targetArmour);
				sb.append(") of ");
				sb.append(target.getName());
				sb.append(" from ");
				sb.append(targetRange);
				String oor = targetOutOfRange ? " (Out of Range!)" : "";
				sb.append(" feet " + oor);
			}

			return sb.toString();
		}
		
		public String getFormula() {
			
			if (targetOutOfRange || !bothVisible || "Defend".equals(strike) || target == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("(Defence Skill(" + targetWeaponLevel + ") + ");
			sb.append("Shoot".equals(strike) ? "Range" : "Weapon");
			sb.append("(" + targetWeaponDefence + ") + ");
			sb.append("Armour(" + targetArmourDefence + ") + ");
			sb.append("Target(" + areaModifier + ") ");
			sb.append(") - (Attack Skill(" + (weaponLevel + modifier) + ") + ");
			sb.append("Weapon(" + weaponDamage + ")) ");
			sb.append("= " + (feat - modifier));

			return sb.toString();
		}

		public String getModifierText() {
			if (targetOutOfRange || !bothVisible || "Defend".equals(strike) || target == null || !"Player".equals(character.getCharacterType())) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("d100 >=" + weaponMod);
			return sb.toString();
		}
		
		public String getRoll(int idx) {

			if (targetOutOfRange || !bothVisible || "Defend".equals(strike) || target == null || idx < 0 || idx > 4) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			int index = (Math.max(Math.min((feat + idx - 2 - modifier), 8), -8) + 8);
			Integer rollRequired1 = FEAT_TABLE[index][0];
			Integer rollRequired2 = FEAT_TABLE[index][1];
			Integer rollRequired3 = FEAT_TABLE[index][2];
			String logic = feat > 0 ? " and " : " or ";
			sb.append("3d8 [>=" + (rollRequired1) + "]");
			sb.append( (rollRequired2 != null ? logic + "[>=" +rollRequired2 + "]" : ""));
			sb.append( (rollRequired3 != null ? logic + "[>=" +rollRequired3 + "]"  : ""));
			
			return sb.toString();

		}

		public String getOutcome(int idx) {

			if (targetOutOfRange || !bothVisible || idx < 0 || idx > 4) {
				return "";
			}

			StringBuilder sb = new StringBuilder();

			if (idx == 0) {
				if (!"Shoot".equals(strike)) {
					sb.append("Cannot hold initiative for one round");
				}
			} else if (idx == 1) {
				if ("Shoot".equals(strike)) {
					sb.append("Miss");
				} else {
					sb.append("Forced Backward");
				}
			} else if ("Crush".equals(damageType)) {
				sb.append("Crush Damage: " + DAMAGE[areaIndex][idx - 2]);
			} else if ("Slash".equals(damageType)) {
				sb.append("Slash Damage: " + DAMAGE[areaIndex][idx - 2 + 3]);
			} else if ("Pierce".equals(damageType)) {
				sb.append("Pierce Damage: " + DAMAGE[areaIndex][idx - 2 + 6]);
			}
			if (sb.toString().contains("stun")) {
				sb.append(" cannot hold initiative for " + (10 - character.getMettle()) + " rounds");
			}
			if (sb.toString().contains("stumble")) {
				sb.append(" immediately fall to the ground");
			}
			if (sb.toString().contains("fumble")) {
				sb.append(" immediately drop anything held with defending hand");
			}
			if (sb.toString().contains("shock")) {
				sb.append(" mettle roll at level " + (8 - character.getMettle()) + " unable to act for [d100] minutes if failed");
			}
			if (sb.toString().contains("unconcious")) {
				sb.append(" for [d100] minutes");
			}
			return sb.toString();

		}

		public boolean isActorVisible() {
			return actorVisible;
		}
		
		public boolean isBothVisible() {
			return bothVisible;
		}
		
		public CharacterResult getCharacter() {
			return character;
		}

		public String getStrike() {
			return strike;
		}

		public GameWeaponResult getWeapon() {
			return weapon;
		}

		public int getWeaponLevel() {
			return weaponLevel;
		}

		public int getWeaponMod() {
			return weaponMod;
		}

		public int getInitiative() {
			return initiative;
		}

		public int getSlashDamage() {
			return slashDamage;
		}

		public int getCrushDamage() {
			return crushDamage;
		}

		public int getPierceDamage() {
			return pierceDamage;
		}

		public boolean isOffHand() {
			return offHand;
		}

		public int getAreaIndex() {
			return areaIndex;
		}

		public String getAreaName() {
			return areaName;
		}

		public CharacterResult getTarget() {
			return target;
		}

		public String getTargetArmour() {
			return targetArmour;
		}

		public int getTargetSlashDefence() {
			return targetSlashDefence;
		}

		public int getTargetCrushDefence() {
			return targetCrushDefence;
		}

		public int getTargetPierceDefence() {
			return targetPierceDefence;
		}

		public int getTargetRange() {
			return targetRange;
		}

		public boolean isTargetOutOfRange() {
			return targetOutOfRange;
		}

		public GameWeaponResult getTargetWeapon() {
			return targetWeapon;
		}

		public boolean isTargetOffHand() {
			return targetOffHand;
		}

		public int getTargetWeaponDefence() {
			return targetWeaponDefence;
		}

		public int getTargetWeaponLevel() {
			return targetWeaponLevel;
		}

		public int getTargetWeaponMod() {
			return targetWeaponMod;
		}

		public int getFeat() {
			return feat;
		}

		public String getDamageType() {
			return damageType;
		}

		public String getGrip() {
			return grip;
		}

		public boolean isActing() {
			return acting;
		}

		public OUTCOME getOutcome() {
			return outcome;
		}

		public void setOutcome(OUTCOME outcome) {
			this.outcome = outcome;
		}
		
		public boolean getModifier() {
			return modifier > 0;
		}

		public void setModifier(boolean value) {
			modifier = value ? 1 : 0;
		}
	}

}
