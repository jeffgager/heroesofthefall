package com.hotf.client.view;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 * 
 * @author drfibonacci
 */
public interface CharacterView extends IsWidget {
	
	HasHTML getPlayerField();
	HasHTML getNameField();
	HasHTML getStatusField();
	HasHTML getDescriptionField();
	HasImage getPortraitField();
	HasVisibility getPortraitHidden();
	void setPresenter(Presenter presenter);
	HasVisibility getEditHidden();
	
	public interface Presenter extends Activity {
		void close();
		void edit();
	}

}