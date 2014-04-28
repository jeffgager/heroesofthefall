package com.hotf.client.view;

import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.user.client.ui.IsWidget;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PostResult;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface GamesView extends IsWidget {
	
	void setPresenter(Presenter presenter);
	void setGames(List<GameResult> games);
	void setMoreGames(List<GameResult> games);
	void setGame(GameResult game);
	void setPosts(List<PostResult> posts);
	void setMorePosts(List<PostResult> posts);
	void setMaxReplies(int value);
	void redoList();
	
	HasVisibility getGetMoreGamesLink();
	HasVisibility getNoMoreGamesField();
	
	public interface Presenter extends Activity {
		void getMoreGames();
		void editGame(GameResult place);
		void getPosts(GameResult place);
		void getMorePosts(GameResult place);
		void createGame();
		void createPost(GameResult place, String text);
	}

}