package com.hotf.client.view;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.hotf.client.action.result.GameArmourResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameGeneralSkillResult;
import com.hotf.client.action.result.GameImplementResult;
import com.hotf.client.action.result.GameWeaponResult;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 * 
 * @author drfibonacci
 */
public interface GameEdit extends IsWidget {
	
	HasText getTitleValue();
	Focusable getTitleFocus();
	HasHTML getDescriptionValue();
	void setWeaponList(List<GameWeaponResult> weapons);
	void setArmourList(List<GameArmourResult> armour);
	void setArtifactsList(List<GameArtifactResult> artifacts);
	void setGeneralSkills(List<GameGeneralSkillResult> skills);
	void setPresenter(Presenter presenter);
	void setShowTabs(boolean value);

	public interface Presenter extends Activity {
		void close();
		void save();
		void addWeapon();
		void addArmour();
		void addArtifact();
		List<GameImplementResult> getSkillImplements(String skillName);
		void addSkillWeapon(String skillName, GameWeaponResult weapon);
		void removeSkillWeapon(String skillName, GameWeaponResult weapon);
		void addSkillArtifact(String skillName, GameArtifactResult artifact);
		void removeSkillArtifact(String skillName, GameArtifactResult artifact);
	}

}