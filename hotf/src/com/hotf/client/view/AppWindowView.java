package com.hotf.client.view;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface AppWindowView extends IsWidget, AcceptsOneWidget {

	HasVisibility getStrongholdLinkHidden();
	HasVisibility getPlayLinkHidden();
	HasVisibility getGamesLinkHidden();
	HasText getLoginOut();

	void setPresenter(Presenter presenter);

	public interface Presenter {
		void stronghold();
		void play();
		void loginOut();
		void games();
	}

}