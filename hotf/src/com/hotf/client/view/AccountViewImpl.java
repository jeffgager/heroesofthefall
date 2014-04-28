package com.hotf.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.Resources;
import com.hotf.client.action.result.PostResult;

public class AccountViewImpl extends Composite implements AccountView {

	private static PlayViewImplUiBinder uiBinder = GWT.create(PlayViewImplUiBinder.class);

	interface PlayViewImplUiBinder extends UiBinder<Widget, AccountViewImpl> {
	}

	private Presenter presenter;
	private ClientFactory clientFactory;
	private PostCell postCell;
	private ListDataProvider<PostResult> listData = new ListDataProvider<PostResult>();
	private boolean showPortraits;

	@UiField(provided = true) final Resources resources;
	@UiField TextBox usernameField;
	@UiField TextBox fetchRowsField;
	@UiField TextBox searchRowsField;
	@UiField CheckBox showPortraitsField;
	@UiField(provided = true) CellList<PostResult> postList;
	@UiField Anchor getMorePostsLink;
	@UiField HTML noMorePostsField;

	public AccountViewImpl(ClientFactory clientFactory) {

		super();

		this.clientFactory = clientFactory;
		this.resources = clientFactory.getResources();
		postCell = new PostCell(resources);
		listData = new ListDataProvider<PostResult>();
		postList = new CellList<PostResult>(postCell);
		listData.addDataDisplay(postList);

		initWidget(uiBinder.createAndBindUi(this));

		getMorePosts = new HasVisibilityImpl(getMorePostsLink);
		noMorePosts = new HasVisibilityImpl(noMorePostsField);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				usernameField.setFocus(true);
			}
		});
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	@UiHandler("getMorePostsLink")
	public void getMorePostsLink(ClickEvent e) {
		presenter.getMorePosts();
	}

	@Override
	public void setPosts(List<PostResult> posts) {
		listData.getList().clear();
		int s = posts.size();
		listData.getList().addAll(posts);
		postList.setRowCount(s, true);
		postList.setVisibleRange(0, s);
	}

	@Override
	public void setMorePosts(List<PostResult> posts) {
		int s = postList.getRowCount() + posts.size();
		listData.getList().addAll(posts);
		postList.setRowCount(s, true);
		postList.setVisibleRange(0, s);
	}

	@Override
	public void setPost(PostResult post) {
		postCell.setValue(post);
	}

	private HasVisibility getMorePosts;

	/**
	 * @return GetMorePostsLink
	 */
	@Override
	public HasVisibility getGetMorePostsLink() {
		return getMorePosts;
	}

	private HasVisibility noMorePosts;

	/**
	 * @return NoMorePostsField
	 */
	@Override
	public HasVisibility getNoMorePostsField() {
		return noMorePosts;
	}

	public class PostCell extends AbstractCell<PostResult> {

		private Resources resources;

		public PostCell(Resources resources) {
			super("click", "keydown");
			this.resources = resources;
		}

		private Context eventContext;
		private Element eventParent;

		public void setValue(PostResult post) {
			
			if (eventContext == null || eventContext == null) {
				return;
			}
			
			setValue(eventContext, eventParent, post);
			
		}

		@Override
		public void render(Context context, PostResult post, SafeHtmlBuilder sb) {

			if (post == null) {
				return;
			}

			sb.appendHtmlConstant("<div class='" + resources.style().post() + "'>");

			if (showPortraits) {
				sb.appendHtmlConstant("<div style='height: 62px; width: 62px;' class='" + resources.style().postOwner() + "'>");
				sb.appendHtmlConstant("<img id='" + post.getCharacterId() + "' src='" + ImageUtils.get().getPortraitUrlLazilly(post.getCharacterId()) + "'/>");
				sb.appendHtmlConstant("</div>");
			}

			if (post.getImportant()) {
				sb.appendHtmlConstant("<img style='margin: 3px 6px 0px 6px;' class='" + resources.style().postIcon() + "' title='Important Post' src='/images/important.png'/>");
			}
			if (post.getTargetedCharacterId() != null) {
				sb.appendHtmlConstant("<img style='margin: 3px 6px 0px 6px;' class='" + resources.style().postIcon() + "' title='Private Post' src='/images/locked.gif'/>");
			}

			sb.appendHtmlConstant(post.getText());
			Date mapdate = post.getUpdated();
			if (mapdate == null) {
				mapdate = post.getCreated();
			}

			sb.appendHtmlConstant("<div class='" + resources.style().postContext() + "'>");
			sb.appendHtmlConstant("<p>Posted by <strong>");
			sb.appendHtmlConstant(SafeHtmlUtils.htmlEscape(post.getCharacterName()));
			sb.appendHtmlConstant("</strong>&nbsp;");
			String target = post.getTargetName();
			if (target != null) {
				sb.appendHtmlConstant(" to " + SafeHtmlUtils.htmlEscape(target) + " only ");
			}
			sb.appendHtmlConstant("&nbsp;in&nbsp;<strong>");
			sb.appendHtmlConstant(SafeHtmlUtils.htmlEscape(post.getLocationName()));
			sb.appendHtmlConstant("</strong>&nbsp;at&nbsp;");
			DateTimeFormat dtf = clientFactory.getDateTimeFormat();
			sb.appendHtmlConstant(dtf.format(post.getCreated()));
			sb.appendHtmlConstant("&nbsp;");
			if (post.getUpdated() != null) {
				sb.appendHtmlConstant("(Updated at ");
				sb.appendHtmlConstant(dtf.format(post.getUpdated()));
				sb.appendHtmlConstant(")");
			}
			sb.appendHtmlConstant("</p>");
			sb.appendHtmlConstant("</div>");

			sb.appendHtmlConstant("</div>");

		}

	}

	@Override
	public void setShowPortraits(boolean showPortraits) {
		this.showPortraits = showPortraits;
	}

	@Override
	public void redoList() {
		postList.redraw();
	}

	/**
	 * @return usernameField
	 */
	@Override
	public HasText getUsernameField() {
		return usernameField;
	}

	@Override
	public HasEnabled getUsernameEnabled() {
		return usernameField;
	}

	@Override
	public Focusable getUsernameFocus() {
		return usernameField;
	}
	
	/**
	 * @return the fetchRowsField
	 */
	@Override
	public HasText getFetchRowsField() {
		return fetchRowsField;
	}

	/**
	 * @return the searchRowsField
	 */
	@Override
	public HasText getSearchRowsField() {
		return searchRowsField;
	}
	
	/**
	 * @return the showPortraitsField
	 */
	@Override
	public HasValue<Boolean> getShowPortraitsField() {
		return showPortraitsField;
	}
	
	@UiHandler ("tacLink")
	public void tacLink(ClickEvent e) {
		presenter.showTac();
	}

	@UiHandler ("saveLink")
	public void saveLink(ClickEvent e) {
		presenter.save();
	}

}
