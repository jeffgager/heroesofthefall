package com.hotf.client.view;

import java.util.Collection;
import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.HotfRulesEngine.CombatAction;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.action.result.PostResult;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface PlayView extends IsWidget {
	
	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	
	void setShowPortraits(boolean showPortraits);
	void setPosts(List<PostResult> posts);
	void setActions(Collection<CombatAction> characters);
	void setMorePosts(List<PostResult> posts);
	void setPost(PostResult post);
	void resetButtons();
	void redoList();
	void setWeaponList(List<GameWeaponResult> weapons, int leftSelection, int rightSelection);
	
	HasVisibility getGetMorePostsLink();
	HasVisibility getNoMorePostsField();
	HasHTML getPostField();
	HasHTML getPlaceDescriptionField();
	HasText getGameNameField();
	HasEnabled getPrivateFieldEnabled();
	boolean  getPrivatePostValue();
	void setPrivatePostValue(boolean value);
	boolean  getImportantPostValue();
	void setImportantPostValue(boolean value);
	Focusable getPostFocus();
	HasImage getPortraitImage();
	HasVisibility getPortraitHidden();

	void showMap();
	void showPost();
	
	void setMapUrl(String url);
	void setBackgroundUrl(String url);
	void setBackgroundColor(String color);
	void scroll(int x, int y);
	
	void setRss(String name, String id);

	HasSelectionHandlers<Suggestion> getCharacterSelection();
	HasText getCharacterSelectionField();
	HasSelectionHandlers<Suggestion> getLocationSelection();
	HasText getLocationSelectionField();
	HasSafeHtml getMapNameField();
	HasVisibility getGMCommandsHidden();

	HasText getLeftHandField();
	HasText getRightHandField();

	void setPlaceMarker(Integer x, Integer y, Integer range);
	void clearPlaceMarker();
	void addPostMarker(String name, Integer x, Integer y);
	void clearPostMarkers();
	void setRangeMarker(Integer x, Integer y, Integer range);
	void clearRangeMarker();
	void addToken(CharacterResult character);
	void clearTokens();
	void setLeftTargetCharacterName(String name);
	void setRightTargetCharacterName(String name);
	void setLeftTargetArea(Integer area);
	void setRightTargetArea(Integer area);
	void setLeftStrikeList(List<String> sl, int sel); 
	void setRightStrikeList(List<String> sl, int sel);

	public interface Presenter extends Activity {
		void getMorePosts();
		void editPost(PostResult post);
		void markImportant(PostResult post);
		void createPost();
		void mapClick(int x, int y);
		void refresh();
		void important();
		void createMap();
		void openMap();
		void openCharacter();
		void openGame();
		void editCharacter();
		void copyCharacter();
		void moveCharacter();
		void hideCharacter();
		void playCharacter();
		void createCharacter();
		void privateFieldChange();
		void importantFieldChange();
		void weaponChange();
		void clearedPlaceMarker();
		void clearedPostMarker();
		void selectCharacter(CharacterResult character);
		void clearSelection();
		void setLeftHand(String weapon);
		void setRightHand(String weapon);
		void leftTargetCharacter();
		void rightTargetCharacter();
		void leftTargetArea(Integer area);
		void rightTargetArea(Integer area);
		void leftTargetStrike(String strike);
		void rightTargetStrike(String strike);
	}

}