package com.hotf.client.view;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.hotf.client.action.result.PostResult;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface AccountView extends IsWidget {
	
	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	
	void setShowPortraits(boolean showPortraits);
	void setPosts(List<PostResult> posts);
	void setMorePosts(List<PostResult> posts);
	void setPost(PostResult post);
	void redoList();
	
	HasText getUsernameField();
	HasEnabled getUsernameEnabled();
	HasText getFetchRowsField();
	HasText getSearchRowsField();
	HasValue<Boolean> getShowPortraitsField();
	Focusable getUsernameFocus();
	HasVisibility getGetMorePostsLink();
	HasVisibility getNoMorePostsField();

	public interface Presenter extends Activity {
		void getMorePosts();
		void showTac();
		void save();
	}

}