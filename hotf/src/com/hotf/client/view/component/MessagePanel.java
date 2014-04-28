package com.hotf.client.view.component;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.view.HasVisibility;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface MessagePanel extends IsWidget {
	
	void setPresenter(Presenter presenter);
	
	void setLevel(Level level);
	
	HasText getMessageField();
	
	HasVisibility getMessageHidden();

	public interface Presenter {
	}

}