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
public interface PlaceView extends IsWidget {
	
	HasHTML getNameField();
	HasHTML getDescriptionField();
	HasImage getMapField();
	void setPresenter(Presenter presenter);
	HasVisibility getEditHidden();
	HasVisibility getDrawHidden();
	HasVisibility getUploadHidden();
	HasVisibility getClearMapHidden();
	
	public interface Presenter extends Activity {
		void close();
		void edit();
		void uploadMap();
		void draw();
		void clearMap();
	}

}