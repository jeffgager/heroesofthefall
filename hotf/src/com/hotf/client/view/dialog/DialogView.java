/**
 * 
 */
package com.hotf.client.view.dialog;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Jeff
 *
 */
public interface DialogView extends IsWidget{

	void center();
	void hide();
	void setText(String title);

}
