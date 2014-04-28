/**
 * 
 */
package com.hotf.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Jeff
 *
 */
public interface Resources extends ClientBundle {

	/**
	 * The styles used in this widget.
	 */
	@Source("style.css")
	Style style();

	public interface Style extends CssResource {

		String page();
		
		String pageHeader();

		String pageHeaderLeft();
		
		String pageHeaderRight();

		String authenticationBox();

		String messageBox();

		String info();
		
		String error();

		String advert();

		String listBox();

		String selectCharacter();
		
		String selectCommand();

		String commands();
		
		String pageBody();

		String post();

		String postOptions();
		
		String postOwner();
		
		String characterPortrait();

		String chooser();

		String postContext();
		
		String panelContext();

		String postCommands();

		String postInputBox();

		String postMap();

		String postIcon();

		String placeMapPanel();
	
		String morePosts();
		
		String pageFooter();

		String rightButton();
		
		String form();

		String formLabel();

		String groupLabel();

		String wideFormField();
		
		String formField();

		String wideViewField();
		
		String viewField();

		String formArea();

		String marker();
		
		String markerPortrait();
		
		String markerLabel();

		String invisible();
		
		String selected();
		
		String commandStack();

		String postBoxPanel();

		String postControlPanel();

		String commandBackground();

		String save();

		String selectionPanel();

		String visibleToken();

		String hiddenToken();

		String markerToken();

		String selectedToken();

		String donate();

		String white();
		
		String red();

		String blue();

		String green();

		String resultTable();
		String resultCell();
		String headingCell();
		String resultCellLabel();
		String resultSuccessCellLabel();
		String smallFormField();

		String selectedTab();
		String range();
		String selectedCommand();
		String editableCell();

	}

}
