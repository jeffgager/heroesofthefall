package com.hotf.client.view.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public class Resources {
	
	private static ResourceBundle bundle;

	public static ResourceBundle get() {
		if (bundle == null) {
			bundle = GWT.create(ResourceBundle.class);
			bundle.style().ensureInjected();
		}
		return bundle;
	}

	public interface ResourceBundle extends ClientBundle {

		/**
		 * The styles used in this widget.
		 */
		@Source("style.css")
		Style style();

	}

}
