package com.hotf.client.view;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.hotf.client.ClientFactory;
import com.hotf.client.Resources;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.PostResult;

public class GamesViewImpl extends Composite implements GamesView {

	private static GameListViewImplUiBinder uiBinder = GWT.create(GameListViewImplUiBinder.class);

	interface GameListViewImplUiBinder extends UiBinder<Widget, GamesViewImpl> {
	}

	private ClientFactory clientFactory;
	private Presenter presenter;
	private GameCell gameCell;
	private ListDataProvider<GameResult> gameData;
	private SingleSelectionModel<GameResult> gameSelectionModel = new SingleSelectionModel<GameResult>();
	private int maxreplies = 0;

	@UiField(provided = true) final Resources resources;
	@UiField(provided = true) CellList<GameResult> gameList;
	@UiField Anchor getMoreGamesLink;
	@UiField HTML noMoreGamesField;

	public GamesViewImpl(ClientFactory clientFactory) {

		super();

		this.clientFactory = clientFactory;
		this.resources = clientFactory.getResources();
		gameCell = new GameCell(resources);
		gameData = new ListDataProvider<GameResult>();
		ProvidesKey<GameResult> keyprovier = new ProvidesKey<GameResult>() {
			@Override
			public Object getKey(GameResult item) {
				return item == null ? null : item.getId();
			}
		};
		gameList = new CellList<GameResult>(gameCell, GWT.<ActionListResources>create(ActionListResources.class), keyprovier);
		gameList.setSelectionModel(gameSelectionModel);
		gameData.addDataDisplay(gameList);

		initWidget(uiBinder.createAndBindUi(this));

		getMoreGames = new HasVisibilityImpl(getMoreGamesLink);
		noMoreGames = new HasVisibilityImpl(noMoreGamesField);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("getMoreGamesLink")
	public void getMoreGamesLink(ClickEvent e) {
		presenter.getMoreGames();
	}

	@UiHandler ("createGameLink")
	public void createGame(ClickEvent e) {
		presenter.createGame();
	}

	@Override
	public void setGames(List<GameResult> games) {
		gameData.getList().clear();
		int s = games.size();
		gameData.getList().addAll(games);
		gameList.setRowCount(s, true);
		gameList.setVisibleRange(0, s);
	}
	
	@Override
	public void setMoreGames(List<GameResult> games) {
		int s = gameList.getRowCount() + games.size();
		gameData.getList().addAll(games);
		gameList.setRowCount(s, true);
		gameList.setVisibleRange(0, s);
	}

	@Override
	public void setGame(GameResult game) {
		List<GameResult> l = gameData.getList();
		for (int i = 0; i < l.size(); i++) {
			GameResult p = l.get(i);
			if (p.getId().equals(game.getId())) {
				l.set(i, game);
				break;
			}
		}
		gameCell.selectedGame = game;
		gameSelectionModel.setSelected(game, true);
		gameList.redraw();
	}
	
	private List<PostResult> selectedGamePosts;

	@Override
	public void setPosts(List<PostResult> posts) {
		selectedGamePosts = posts;
		gameList.redraw();
		scrollToSelectedGame();
	}
	
	private void scrollToSelectedGame() {
		GameResult selectedGame = gameSelectionModel.getSelectedObject();
		if (selectedGame == null) {
			return;
		}
		Element selected = DOM.getElementById(selectedGame.getId());
		selected.scrollIntoView();
	}

	@Override
	public void setMorePosts(List<PostResult> posts) {
		selectedGamePosts.addAll(posts);
		gameList.redraw();
		scrollToMorePostMarker();
	}
	
	private void scrollToMorePostMarker() {
		Element selected = DOM.getElementById("morePostMarker");
		if (selected != null) {
			selected.scrollIntoView();
		}
	}
	
	private HasVisibility getMoreGames;

	@Override
	public HasVisibility getGetMoreGamesLink() {
		return getMoreGames;
	}

	private HasVisibility noMoreGames;

	@Override
	public HasVisibility getNoMoreGamesField() {
		return noMoreGames;
	}

	public class GameCell extends AbstractCell<GameResult> {

		private Resources resources;
		private GameResult selectedGame;
		private GameResult editGame;

		public GameCell(Resources resources) {
			super("click", "keyup");
			this.resources = resources;
		}

		private Context eventContext;
		private Element eventParent;

		public void setValue(GameResult game) {
			
			if (eventContext == null || eventParent == null) {
				return;
			}
			
			setValue(eventContext, eventParent, game);
			
		}

		@Override
		public void render(Context context, GameResult game, SafeHtmlBuilder sb) {

			if (game == null) {
				return;
			}

			sb.appendHtmlConstant("<div class='" + resources.style().post() + "'>");

			sb.appendHtmlConstant("<div class='" + resources.style().postCommands() + "'>");
			if (game.getUpdatePermission()) {
				sb.appendHtmlConstant("<a class='edit'>Edit</a>");
			}
			sb.appendHtmlConstant("<a class='reply'>Reply</a>");
			sb.appendHtmlConstant("</div>");

			sb.appendHtmlConstant("<h1 id='" + game.getId() + "'>" + game.getTitle() + "</h1>");

			if (gameSelectionModel.isSelected(game)) {
				sb.appendHtmlConstant("<div style='margin:0px 13px 0px 13px;'>" + game.getDescription() + "</div>");
				sb.appendHtmlConstant("<div class='" + resources.style().postContext() + "'>");
				sb.appendHtmlConstant("<p>Topic Created&nbsp;by&nbsp;<strong>");
				sb.appendHtmlConstant(SafeHtmlUtils.htmlEscape(game.getOwner()));
				sb.appendHtmlConstant("</strong>&nbsp;at&nbsp;");
				DateTimeFormat dtf = clientFactory.getDateTimeFormat();
				sb.appendHtmlConstant(dtf.format(game.getCreated()));
				sb.appendHtmlConstant("&nbsp;");
				if (game.getUpdated() != null && game.getUpdated().after(game.getCreated())) {
					sb.appendHtmlConstant("(Updated at ");
					sb.appendHtmlConstant(dtf.format(game.getUpdated()));
					sb.appendHtmlConstant(")");
				}
				sb.appendHtmlConstant("</p>");
				sb.appendHtmlConstant("</div>");

				if (game.equals(editGame)) {
					sb.appendHtmlConstant("<div class='" + resources.style().postCommands() + "'>");
					sb.appendHtmlConstant("<a class='saveButton'>Save</a>");
					sb.appendHtmlConstant("</div>");
					sb.appendHtmlConstant("<textarea class='editBox' style='margin:0px 13px 0px 13px;display:block; width:600px; height:200px;'></textarea>");
				}

				sb.appendHtmlConstant("<div style='margin:0px 13px 0px 13px;' id='posts'>");

				if (selectedGamePosts != null) {

					int morepostindex = ((int)(selectedGamePosts.size() / maxreplies)) * maxreplies;
					for (int i = 0; i < selectedGamePosts.size(); i++) {
						PostResult post = selectedGamePosts.get(i);
						sb.appendHtmlConstant("<div>");

						sb.appendHtmlConstant("<div style='text-align:left;'>" + post.getText() + "</div>");

						sb.appendHtmlConstant("<div style='clear: left; margin-bottom: 6px; ' class='" + resources.style().postContext() + "'>");
						sb.appendHtmlConstant("<p>Reply by <strong>");
						sb.appendHtmlConstant(SafeHtmlUtils.htmlEscape(post.getCharacterName()));
						sb.appendHtmlConstant("</strong>&nbsp;at&nbsp;");
						sb.appendHtmlConstant(dtf.format(post.getCreated()));
						sb.appendHtmlConstant("&nbsp;");
						if (post.getUpdated() != null) {
							sb.appendHtmlConstant("(Updated at ");
							sb.appendHtmlConstant(dtf.format(post.getUpdated()));
							sb.appendHtmlConstant(")");
						}
						sb.appendHtmlConstant("</p>");
						sb.appendHtmlConstant("</div>");
						if (i == morepostindex) {
							sb.appendHtmlConstant("<span id='morePostMarker'> </span>");
						}
						sb.appendHtmlConstant("</div>");
					}
					
					int postcount = selectedGamePosts.size();
					if (postcount > 0 && postcount % maxreplies == 0) {
						sb.appendHtmlConstant("<div style='margin-bottom: 13px; display: inline; float: right;border: 1px solid #BBB; border-radius: 13px; -moz-border-radius: 13px; background: #D3DEF6 url(hotf/gwt/chrome/images/hborder.png) repeat-x 0px -989px;'>");
						sb.appendHtmlConstant("<a style='margin: 4px 5px 4px 5px; padding: 4px 4px 4px 4px;' class='moreReplies'>More</a>");
						sb.appendHtmlConstant("</div>");
					}

				}

			} else {
				
				sb.appendHtmlConstant("<div class='" + resources.style().postContext() + "'>");
				sb.appendHtmlConstant("<p>Topic Created&nbsp;by&nbsp;<strong>");
				sb.appendHtmlConstant(SafeHtmlUtils.htmlEscape(game.getOwner()));
				sb.appendHtmlConstant("</strong>&nbsp;at&nbsp;");
				DateTimeFormat dtf = clientFactory.getDateTimeFormat();
				sb.appendHtmlConstant(dtf.format(game.getCreated()));
				sb.appendHtmlConstant("&nbsp;");
				if (game.getUpdated() != null && game.getUpdated().after(game.getCreated())) {
					sb.appendHtmlConstant("(Updated at ");
					sb.appendHtmlConstant(dtf.format(game.getUpdated()));
					sb.appendHtmlConstant(")");
				}
				sb.appendHtmlConstant("</p>");
				sb.appendHtmlConstant("</div>");

			}
			
			sb.appendHtmlConstant("</div>");
		
		}

		private String inputText = null;

		@Override
		public void onBrowserEvent(Context context, Element parent,
				GameResult value, NativeEvent event,
				ValueUpdater<GameResult> valueUpdater) {

			eventContext = context;
			eventParent = parent;

			super.onBrowserEvent(context, parent, value, event, valueUpdater);

			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}
			Element target = Element.as(eventTarget);

			String type = event.getType();
			String classname = target.getClassName();
			if ("keyup".equals(type)) {
				event.stopPropagation();
				if ("editBox".equals(classname)) {
					inputText = ((TextAreaElement)target.cast()).getValue();
				}
			} else if ("click".equals(type)) {
				if ("editBox".equals(classname)) {
					return;
				}
				if ("moreReplies".equals(classname)) {
					presenter.getMorePosts(value);
					return;
				}
				if (value.getUpdatePermission() && "edit".equals(classname)) {
					presenter.editGame(value);
					return;
				}
				if (selectedGame != null && !selectedGame.getId().equals(value.getId())) {
					gameSelectionModel.setSelected(gameSelectionModel.getSelectedObject(), false);
					gameSelectionModel.setSelected(value, true);
					editGame = null;
				}
				if ("reply".equals(classname)) {
					if (clientFactory.getAccount() == null) {
						clientFactory.getGameController().login();
						return;
					}
					editGame = value;
				} else {
					editGame = null;
				}
				if ("saveButton".equals(classname)) {
					if (inputText != null && inputText.length() > 0) {
						presenter.createPost(value, inputText);
					}
					return;
				}
				selectedGame = value;
				presenter.getPosts(value);
			}
			
		}

		@Override
		public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, GameResult value) {
			if (value == editGame) {
				return true;
			}
			return super.isEditing(context, parent, value);
		}
		
	}

	@Override
	public void redoList() {
		gameList.redraw();
	}

	@Override
	public void setMaxReplies(int value) {
		maxreplies = value;
	}

}
