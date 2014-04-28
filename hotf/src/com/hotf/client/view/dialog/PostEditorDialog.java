package com.hotf.client.view.dialog;

import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.hotf.client.view.HasVisibility;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface PostEditorDialog extends DialogView {
	
	void setPresenter(Presenter presenter);
	HasHTML getPostEditField();
	HasText getPostAttackResultField();
	HasVisibility getPostAttackResultEnabled();

	public interface Presenter {
		void save();
		void cancel();
	}

}