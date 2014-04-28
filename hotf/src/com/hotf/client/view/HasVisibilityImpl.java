/**
 * 
 */
package com.hotf.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Jeff
 *
 */
public class HasVisibilityImpl implements HasVisibility {

	private Widget widget;
	
	public HasVisibilityImpl(IsWidget isWidget) {
		this(isWidget.asWidget());
	}

	public HasVisibilityImpl(Widget widget) {
		this.widget = widget;
	}

	@Override
	public void setVisible(boolean visible) {
		widget.setVisible(visible);
	}

	@Override
	public boolean isVisible() {
		return widget.isVisible();
	}
}
