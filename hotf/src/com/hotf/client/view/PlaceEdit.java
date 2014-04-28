package com.hotf.client.view;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 * 
 * @author drfibonacci
 */
public interface PlaceEdit extends IsWidget {
	
	HasText getNameField();
	HasText getTypeField();
	Focusable getNameFocus();
	HasHTML getDescriptionField();
	void setPresenter(Presenter presenter);

	public interface Presenter extends Activity {
		void close();
		void save();
	}

}