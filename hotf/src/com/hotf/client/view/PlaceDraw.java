package com.hotf.client.view;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide its
 * container widget.
 * 
 * @author drfibonacci
 */
public interface PlaceDraw extends IsWidget {
	
	void setPresenter(Presenter presenter);
	void setImageData(String map, String overlay);
	String getImageData();

	public interface Presenter extends Activity {
		void close();
		void save();
	}

}