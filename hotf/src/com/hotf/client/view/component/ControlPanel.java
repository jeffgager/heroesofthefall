package com.hotf.client.view.component;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.hotf.client.view.HasVisibility;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface ControlPanel extends IsWidget {
	
	void setPresenter(Presenter presenter);
	
	Presenter getPresenter();
	HasSelectionHandlers<Suggestion> getCharacterSelection();
	HasText getCharacterSelectionField();
	HasSelectionHandlers<Suggestion> getLocationSelection();
	HasText getLocationSelectionField();
	HasSafeHtml getMapNameField();
	HasVisibility getGMCommandsHidden();
	
	public interface Presenter {
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
	}

}