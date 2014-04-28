package com.hotf.client.view;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.action.result.CharacterGeneralSkillResult;
import com.hotf.client.action.result.CharacterSkillResult;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameWeaponResult;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 * 
 * @author drfibonacci
 */
public interface CharacterEdit extends IsWidget {
	
	HasSelectionHandlers<Suggestion> getPlayerSelection();
	HasText getPlayerField();
	HasEnabled getPlayerEnabled();
	HasText getNameField();
	Focusable getNameFocus();
	HasText getStatusField();
	HasHTML getDescriptionField();
	HasHTML getSheetField();
	HasImage getPortraitField();
	HasVisibility getPortraitHidden();
	
	void setShowTabs(boolean value);
	HasText getWyrdField();
	HasText getHandedField();
	HasText getSkillRanksField();
	HasText getCharacterPointsField();
	HasText getCharacterCostField();
	HasText getVigourField();
	HasText getMettleField();
	HasText getWitField();
	HasText getGlamourField();
	HasText getSpiritField();
	List<CharacterGeneralSkillResult> getSkillsList();
	void setArmour(String armourType[], Integer[] slash, Integer[] crush, Integer[] pierce);
	HasText getSkillCostField();
	int getSelectedTarget();
	
	void setWeaponSelectionList(List<GameWeaponResult> weapons);
	void setArmourSelectionList(List<GameArmourResult> armour);
	void setArtifactSelectionList(List<GameArtifactResult> artifact);
	void setGeneralSkillSelectionList(List<String> generalSkills);
	void setSkillSelectionList(List<String> skills);

	void setWeaponList(List<GameWeaponResult> weapons);
	void setArmourList(List<GameArmourResult> armour, List<Integer> indexes);
	void setArtifactList(List<GameArtifactResult> artifacts);
	void setGeneralSkillList(List<CharacterGeneralSkillResult> skills);

	void setGameMasterControls(boolean gm);
	
	void setPresenter(Presenter presenter);

	public interface Presenter extends Activity {
		void validate();
		void close();
		void save();
		void updateArmourSelection(int targetArea, int armourType);
		String integerToString(Integer i);
		void replaceArmour(int idx);
		void setSlashArmour(int target, String v);
		void setCrushArmour(int target, String v);
		void setPierceArmour(int target, String v);
		void addWeapon(String id);
		void addArmour(String id);
		void addArtifact(String id);
		void deleteWeapon(GameWeaponResult weapon);
		void deleteArmour(GameArmourResult armour);
		void deleteArtifact(GameArtifactResult armour);
		void selectGeneralSkill(CharacterGeneralSkillResult skill);
		void selectSkill(CharacterSkillResult skill);
		void addGeneralSkill(String id);
		void removeGeneralSkill();
		void addSkill(String id);
		void removeSkill();
		void uploadPortrait();
	}

}