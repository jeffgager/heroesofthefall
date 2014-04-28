package com.hotf.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.hotf.client.ClientFactory;
import com.hotf.client.HotfRulesEngine.CombatAction;
import com.hotf.client.HotfRulesEngine.OUTCOME;
import com.hotf.client.ImageUtils;
import com.hotf.client.Resources;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.event.AddToNextPostEvent;
import com.hotf.client.image.Images;
import com.hotf.client.view.control.CharacterOracle;
import com.hotf.client.view.control.Chooser;
import com.hotf.client.view.control.LocationOracle;
import com.hotf.client.view.control.RichTextEditor;

public class PlayViewImpl extends Composite implements PlayView {

	private static PlayViewImplUiBinder uiBinder = GWT.create(PlayViewImplUiBinder.class);

	interface PlayViewImplUiBinder extends UiBinder<Widget, PlayViewImpl> {
	}

	private Images images = GWT.create(Images.class); 
	private Presenter presenter;
	private ClientFactory clientFactory;
	private PostCell postCell;
	private ActionCell actionCell;
	private ListDataProvider<PostResult> postListData = new ListDataProvider<PostResult>();
	private ListDataProvider<CombatAction> actionListData = new ListDataProvider<CombatAction>();
	private boolean showPortraits;

	@UiField(provided = true) final Resources resources;
	@UiField(provided = true) final RichTextEditor postField;
	@UiField(provided = true) final CellList<PostResult> postList;
	@UiField(provided = true) final CellList<CombatAction> actionList;
	@UiField Anchor showHideMap;
	@UiField Anchor showHideActions;
	@UiField Anchor privateField;
	@UiField Anchor importantField;
	@UiField Anchor getMorePostsLink;
	@UiField HTML noMorePostsField;
	@UiField AbsolutePanel placeMapField;
	@UiField HTML placeDescriptionField;
	@UiField FlowPanel mapBackground;
	@UiField ScrollPanel scrollPanel;
	@UiField VerticalPanel mapPanel;
	@UiField VerticalPanel actionPanel;
	@UiField FlowPanel postPanel;
	@UiField HTML rssLink;
	
	@UiField (provided=true) Chooser selectLocationChooser;
	@UiField (provided=true) Chooser selectCharacterChooser;
	@UiField HTML mapNameField;
	@UiField Anchor hideCharacter;
	@UiField Anchor copyCharacter;
	@UiField Anchor moveCharacterMap;
	@UiField Anchor createCharacter;
	@UiField Anchor openMap;
	@UiField Anchor openGame;
	@UiField Anchor createMap;

	@UiField ListBox leftHandField;
	@UiField ListBox rightHandField;
	@UiField Label leftLabel;
	@UiField Label rightLabel;
	@UiField Anchor leftTargetCharacter;
	@UiField Anchor rightTargetCharacter;
	@UiField ListBox leftTargetArea;
	@UiField ListBox rightTargetArea;
	@UiField ListBox leftTargetStrike;
	@UiField ListBox rightTargetStrike;

	public PlayViewImpl(ClientFactory clientFactory) {

		super();

		this.clientFactory = clientFactory;
		this.resources = clientFactory.getResources();
		this.postField = clientFactory.createRichTextEditor();
		postCell = new PostCell(resources);
		postListData = new ListDataProvider<PostResult>();
		postList = new CellList<PostResult>(postCell, GWT.<PostListResources>create(PostListResources.class));
		postList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		postListData.addDataDisplay(postList);
		actionCell = new ActionCell(resources);
		actionList = new CellList<CombatAction>(actionCell, GWT.<ActionListResources>create(ActionListResources.class));
		actionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		actionCell = new ActionCell(resources);
		actionListData = new ListDataProvider<CombatAction>();
		actionListData.addDataDisplay(actionList);
		
		LocationOracle locationOracle = new LocationOracle(clientFactory);
		selectLocationChooser = new Chooser(locationOracle);
		
		CharacterOracle characterOracle = new CharacterOracle(clientFactory);
		selectCharacterChooser = new Chooser(characterOracle);

		initWidget(uiBinder.createAndBindUi(this));

		gmCommandsHidden = new GMCommandVisibility();

		getMorePosts = new HasVisibilityImpl(getMorePostsLink);
		noMorePosts = new HasVisibilityImpl(noMorePostsField);

		postField.resetButtons();

		placeMapField.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				//TODO Move fails at serialize because this is a real.
				int x = (int)(e.getRelativeX(placeMapField.getElement()) * 1.0f);
				int y = (int)(e.getRelativeY(placeMapField.getElement()) * 1.0f);
				presenter.mapClick(x, y);
			}
		}, ClickEvent.getType());

		leftHandField.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.setLeftHand(leftHandField.getValue(leftHandField.getSelectedIndex()));
				updateLeftAttacksAvailable();
			}
		});
		rightHandField.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.setRightHand(rightHandField.getValue(rightHandField.getSelectedIndex()));
				updateRightAttacksAvailable();
			}
		});

		leftTargetStrike.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.leftTargetStrike(leftTargetStrike.getValue(leftTargetStrike.getSelectedIndex()));
				updateLeftAttacksAvailable();
			}
		});
		
		rightTargetStrike.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.rightTargetStrike(rightTargetStrike.getValue(rightTargetStrike.getSelectedIndex()));
				updateRightAttacksAvailable();
			}
		});

		leftTargetArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.leftTargetArea(leftTargetArea.getSelectedIndex());
			}
		});
		rightTargetArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.rightTargetArea(rightTargetArea.getSelectedIndex());
			}
		});
		
	}

	private void updateLeftAttacksAvailable() {
		boolean lha = !"Defend".equals(leftTargetStrike.getValue(leftTargetStrike.getSelectedIndex()));
		leftTargetArea.setVisible(lha && !gmvisible);
		leftTargetCharacter.setVisible(lha && !gmvisible);
	}

	private void updateRightAttacksAvailable() {
		boolean rha = !"Defend".equals(rightTargetStrike.getValue(rightTargetStrike.getSelectedIndex()));
		rightTargetArea.setVisible(rha && !gmvisible);
		rightTargetCharacter.setVisible(rha && !gmvisible);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasEnabled getPrivateFieldEnabled() {
		return privateField;
	}
	
	@Override
	public HasImage getPortraitImage() {
		return postField.getPortraitImage();
	}
	
	@Override
	public HasVisibility getPortraitHidden() {
		return postField.getPortraitHidden();
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	private int x, y = 300;

	@Override
	public void scroll(int x, int y) {
		this.x = x;
		this.y = y;
		doscroll();
	}

	private Image image;

	@Override
	public void setMapUrl(final String url) {
		if (image != null) {
			image.removeFromParent();
		}
		image = new Image(url);
		image.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				image.removeFromParent();
				DOM.setStyleAttribute(placeMapField.getElement(), "backgroundImage", "url(" + url + ")");
				sizeMap();
				doscroll();
			}
		});
		placeMapField.add(image, 0, 0);

	}

	@Override
	public void setBackgroundColor(String color) {
		DOM.setStyleAttribute(mapBackground.getElement(), "backgroundImage", "none");
		DOM.setStyleAttribute(mapBackground.getElement(), "backgroundColor", color);
	}

	@Override
	public void setBackgroundUrl(String url) {
		DOM.setStyleAttribute(mapBackground.getElement(), "backgroundImage", "url('" + url + "')");
		DOM.setStyleAttribute(mapBackground.getElement(), "backgroundRepeat", "repeat");
	}
	
	private void doscroll() {
		scrollPanel.setHorizontalScrollPosition(x - 250);
		scrollPanel.setVerticalScrollPosition(y - 250);
	}

	private boolean privatePostValue = false;
	
	@Override
	public void setPrivatePostValue(boolean value) {
		this.privatePostValue = value;
	};

	@Override
	public boolean getPrivatePostValue() {
		return privatePostValue;
	};

	private boolean importantPostValue = false;
	
	@Override
	public void setImportantPostValue(boolean value) {
		this.importantPostValue = value;
	};

	@Override
	public boolean getImportantPostValue() {
		return importantPostValue;
	};

	@Override
	public HasHTML getPostField() {
		return postField;
	}
	
	@Override
	public Focusable getPostFocus() {
		return postField;
	}

	@Override
	public HasHTML getPlaceDescriptionField() {
		return placeDescriptionField;
	}

	@UiHandler ("privateField")
	public void privateField(ClickEvent e) {
		privatePostValue = !privatePostValue;
		updatePrivateField();
		presenter.privateFieldChange();
	}
	
	private void updatePrivateField() {
		if (privatePostValue) {
			privateField.addStyleName(resources.style().selectedTab());
		} else {
			privateField.removeStyleName(resources.style().selectedTab());
		}
	}
	
	@UiHandler ("importantField")
	public void importantField(ClickEvent e) {
		importantPostValue = !importantPostValue;
		updateImportantField();
		presenter.importantFieldChange();
	}
	
	private void updateImportantField() {
		if (importantPostValue) {
			importantField.addStyleName(resources.style().selectedTab());
		} else {
			importantField.removeStyleName(resources.style().selectedTab());
		}
	}

	@Override
	public void resetButtons() {
		postField.resetButtons();
		importantPostValue = false;
		privatePostValue = false;
		updateImportantField();
		updatePrivateField();
	}

	@UiHandler ("showHideMap")
	public void showHideMap(ClickEvent e) {
		mapPanel.setVisible(!mapPanel.isVisible());
		actionPanel.setVisible(false);
		String sel = resources.style().selectedTab();
		if (mapPanel.isVisible()) {
			showHideMap.addStyleName(sel);
			showHideActions.removeStyleName(sel);
		} else {
			showHideMap.removeStyleName(sel);
			showHideActions.removeStyleName(sel);
		}
	}

	@UiHandler ("showHideActions")
	public void showHideActions(ClickEvent e) {
		mapPanel.setVisible(false);
		actionPanel.setVisible(!actionPanel.isVisible());
		String sel = resources.style().selectedTab();
		if (actionPanel.isVisible()) {
			showHideActions.addStyleName(sel);
			showHideMap.removeStyleName(sel);
		} else {
			showHideActions.removeStyleName(sel);
			showHideMap.removeStyleName(sel);
		}
	}

	@Override
	public void showPost() {
		mapPanel.setVisible(false);
		showHideMap.removeStyleName(resources.style().selectedTab());
	}

	public void showMap() {
		mapPanel.setVisible(true);
		showHideMap.addStyleName(resources.style().selectedTab());
	}
	
	@UiHandler ("saveButton")
	public void saveButton(ClickEvent e) {
		presenter.createPost();
	}
	
	@UiHandler("getMorePostsLink")
	public void getMorePostsLink(ClickEvent e) {
		presenter.getMorePosts();
	}

	@Override
	public void setPosts(List<PostResult> posts) {
		postListData.getList().clear();
		int s = posts.size();
		postListData.getList().addAll(posts);
		postList.setRowCount(s, true);
		postList.setVisibleRange(0, s);
	}
	
	@Override
	public void setActions(Collection<CombatAction> actions) {
		actionListData.getList().clear();
		int s = actions.size();
		actionListData.getList().addAll(actions);
		actionList.setRowCount(s, true);
		actionList.setVisibleRange(0, s);
	}

	@Override
	public void setMorePosts(List<PostResult> posts) {
		int s = postList.getRowCount() + posts.size();
		postListData.getList().addAll(posts);
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
				sb.appendHtmlConstant("<div style='height: 35px; width: 35px;' class='" + resources.style().postOwner() + "'>");
				sb.appendHtmlConstant("<img id='" + post.getCharacterId() + "' src='" + ImageUtils.get().getPortraitUrlLazilly(post.getCharacterId()) + "'/>");
				sb.appendHtmlConstant("</div>");
			}

			sb.appendHtmlConstant("<div class='" + resources.style().postCommands() + "'>");
			if (post.getUpdatePermission()) {
				sb.appendHtmlConstant("<a title='Edit post' class='edit'>Edit</a>");
			}
			sb.appendHtmlConstant("<a title='Mark post as important' class='important'>Important</a>");
			sb.appendHtmlConstant("</div>");

			if (post.getImportant()) {
				sb.appendHtmlConstant("<img style='margin: 3px 6px 0px 6px;' class='" + resources.style().postIcon() + "' title='Important Post' src='/images/important.png'/>");
			}
			if (post.getTargetedCharacterId() != null) {
				sb.appendHtmlConstant("<img style='margin: 3px 6px 0px 6px;' class='" + resources.style().postIcon() + "' title='Private Post' src='/images/locked.gif'/>");
			}

			sb.appendHtmlConstant("<div style='text-align:left;'>" + post.getText() + "</div>");
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

		@Override
		public void onBrowserEvent(Context context, Element parent,
				PostResult value, NativeEvent event,
				ValueUpdater<PostResult> valueUpdater) {

			eventContext = context;
			eventParent = parent;

			super.onBrowserEvent(context, parent, value, event, valueUpdater);

			if ("click".equals(event.getType())) {
				EventTarget eventTarget = event.getEventTarget();
				if (!Element.is(eventTarget)) {
					return;
				}
				Element target = Element.as(eventTarget);
				String classname = target.getClassName();
				if ("edit".equals(classname)) {
					presenter.editPost(value);
				} else if ("important".equals(classname)) {
					presenter.markImportant(value);
				}
			}
		}

	}

	public class ActionCell extends AbstractCell<CombatAction> {

		private Context eventContext;
		private Element eventParent;

		public ActionCell(Resources resources) {
			super("click");
		}

		public void setValue(CombatAction action) {
			
			if (eventContext == null || eventContext == null) {
				return;
			}
			
			setValue(eventContext, eventParent, action);
			
		}

		@Override
		public void render(Context context, CombatAction action, SafeHtmlBuilder sb) {

			if (action == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");
			sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<th colspan='5'>");
			sb.appendHtmlConstant(action.getNarrative());
			sb.appendHtmlConstant("</th>");
			sb.appendHtmlConstant("</tr>");
			if (!action.isTargetOutOfRange() && action.isActing() && action.isBothVisible()) {
				sb.appendHtmlConstant("<tr>");
				sb.appendHtmlConstant("<td colspan='5'>");
				sb.appendHtmlConstant(action.getFormula());
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("</tr>");
				String modtext = action.getModifierText();
				if (!"".equals(modtext)) {
					sb.appendHtmlConstant("<tr>");
					sb.appendHtmlConstant("<td colspan='5'>");
					sb.appendHtmlConstant(modtext);
					sb.appendHtmlConstant("</td>");
					sb.appendHtmlConstant("</tr>");
					sb.appendHtmlConstant("<tr>");
					sb.appendHtmlConstant("<td colspan='5'>");
					sb.appendHtmlConstant("<a " + (action.getModifier() ? " class='modifier " + resources.style().selectedCommand() + "'" : " class='modifier'") + ">Act at Next Level</a>");
					sb.appendHtmlConstant("</td>");
					sb.appendHtmlConstant("</tr>");
				}
				sb.appendHtmlConstant("<tr>");
				sb.appendHtmlConstant("<td>");
				if (!"Shoot".equals(action.getStrike())) {
					sb.appendHtmlConstant(action.getRoll(0));
				}
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getRoll(1));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getRoll(2));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getRoll(3));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getRoll(4));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("</tr>");
				sb.appendHtmlConstant("<tr>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getOutcome(0));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getOutcome(1));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getOutcome(2));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getOutcome(3));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant(action.getOutcome(4));
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("</tr>");
				sb.appendHtmlConstant("<tr>");
				sb.appendHtmlConstant("<td>");
				if (!"Shoot".equals(action.getStrike())) {
					sb.appendHtmlConstant("<a " + (OUTCOME.PARRY.equals(action.getOutcome()) ? " class='" + OUTCOME.PARRY.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.PARRY.name() + "'") + ">Parry</a>");
				}
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				if ("Shoot".equals(action.getStrike())) {
					sb.appendHtmlConstant("<a " + (OUTCOME.BLOCK.equals(action.getOutcome()) ? " class='" + OUTCOME.BLOCK.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.BLOCK.name() + "'") + ">Miss</a>");
				} else {
					sb.appendHtmlConstant("<a " + (OUTCOME.BLOCK.equals(action.getOutcome()) ? " class='" + OUTCOME.BLOCK.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.BLOCK.name() + "'") + ">Block</a>");
				}
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant("<a " + (OUTCOME.MINOR.equals(action.getOutcome()) ? " class='" + OUTCOME.MINOR.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.MINOR.name() + "'") + ">Minor Wound</a>");
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant("<a " + (OUTCOME.SERIOUS.equals(action.getOutcome()) ? " class='" + OUTCOME.SERIOUS.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.SERIOUS.name() + "'") + ">Serious Wound</a>");
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("<td>");
				sb.appendHtmlConstant("<a " + (OUTCOME.CRITICAL.equals(action.getOutcome()) ? " class='" + OUTCOME.CRITICAL.name() + " " + resources.style().selectedCommand() + "'" : " class='" + OUTCOME.CRITICAL.name() + "'") + ">Critical Wound</a>");
				sb.appendHtmlConstant("</td>");
				sb.appendHtmlConstant("</tr>");
			}
			sb.appendHtmlConstant("</table>");

		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				CombatAction value, NativeEvent event,
				ValueUpdater<CombatAction> valueUpdater) {

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
			if ("click".equals(type)) {
				if (OUTCOME.PARRY.name().equals(classname)) {
					value.setOutcome(OUTCOME.PARRY);
				} else if (OUTCOME.BLOCK.name().equals(classname)) {
					value.setOutcome(OUTCOME.BLOCK);
				} else if (OUTCOME.MINOR.name().equals(classname)) {
					value.setOutcome(OUTCOME.MINOR);
				} else if (OUTCOME.SERIOUS.name().equals(classname)) {
					value.setOutcome(OUTCOME.SERIOUS);
				} else if (OUTCOME.CRITICAL.name().equals(classname)) {
					value.setOutcome(OUTCOME.CRITICAL);
				} else if (classname.contains("modifier")) {
					value.setModifier(!value.getModifier());
				}
			}
			actionList.redrawRow(context.getIndex());
			StringBuffer post = new StringBuffer();
			for (CombatAction ca : actionListData.getList()) {
				if (ca.getOutcome() != null) {
					post.append(ca.getNarrative());
					post.append(" resulting in ");
					post.append(ca.getOutcome(ca.getOutcome().ordinal()));
					post.append("<br/>");
				}
			}
			clientFactory.getEventBus().fireEvent(new AddToNextPostEvent(post.toString()));
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

	@Override
	public void setRss(String name, String id) {
		rssLink.setHTML("<a href='/rss?characterId=" + id + "'><img title='RSS Feed for " + name + "' style='margin-left: 13px;float: none; border:none;width: 22px;' src='/images/rss.png'/></a>" );
	}
	
	@Override
	public HasSelectionHandlers<Suggestion> getCharacterSelection() {
		return selectCharacterChooser;
	}
	
	@Override
	public HasText getCharacterSelectionField() {
		return selectCharacterChooser;
	}
	
	@Override
	public HasSelectionHandlers<Suggestion> getLocationSelection() {
		return selectLocationChooser;
	}
	
	@Override
	public HasText getLocationSelectionField() {
		return selectLocationChooser;
	}
	
	private HasVisibility gmCommandsHidden;
	
	@Override
	public HasVisibility getGMCommandsHidden() {
		return gmCommandsHidden;
	}

	@Override
	public HasSafeHtml getMapNameField() {
		return mapNameField;
	}
	
	@Override
	public HasText getGameNameField() {
		return openGame;
	}
	
	private Image placeMarker;
	private Label placeRange;

	@Override
	public void setPlaceMarker(Integer x, Integer y, Integer range) {
		clearPlaceMarker();
		placeMarker = new Image(images.personalMarker());
		placeRange = new Label(Integer.toString(range));
		placeRange.addStyleName(resources.style().range());
		ClickHandler h = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				event.preventDefault();
				clearPlaceMarker();
				presenter.clearedPlaceMarker();
			}
		};
		placeMarker.addClickHandler(h);
		placeRange.addClickHandler(h);
		placeMapField.add(placeMarker, x - 15, y - 15);
		placeMapField.add(placeRange, x - 12, y - 12);
	}

	@Override
	public void clearPlaceMarker() {
		if (placeMarker != null) {
			placeMarker.removeFromParent();
			placeMarker = null;
		}
		if (placeRange != null) {
			placeRange.removeFromParent();
			placeRange = null;
		}
	}

	private Label rangeMarker;

	@Override
	public void setRangeMarker(Integer x, Integer y, Integer range) {
		clearRangeMarker();
		rangeMarker = new Label(Integer.toString(range));
		rangeMarker.addStyleName(resources.style().range());
		rangeMarker.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				event.preventDefault();
				clearRangeMarker();
			}
		});
		placeMapField.add(rangeMarker, x - 9, y - 9);
	}

	@Override
	public void clearRangeMarker() {
		if (rangeMarker != null) {
			rangeMarker.removeFromParent();
			rangeMarker = null;
		}
	}

	private List<Image> postMarkers = new ArrayList<Image>();
	
	@Override
	public void addPostMarker(final String name, final Integer x, final Integer y) {
		final Image postMarker = new Image(images.postMarker());
		postMarker.setSize("30px", "30px");
		postMarker.setStyleName(clientFactory.getResources().style().markerToken());
		postMarker.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				event.preventDefault();
				postMarker.removeFromParent();
				presenter.clearedPostMarker();
			}
		});
		placeMapField.add(postMarker, x - 15, y - 15);
		postMarker.setTitle(name + " Marker");
		postMarkers.add(postMarker);
	}
	
	@Override
	public void clearPostMarkers() {
		for (Image postMarker : postMarkers) {
			postMarker.removeFromParent();
		}
	}
	
	private Map<String,Image> tokens = new HashMap<String, Image>();
	
	@Override
	public void addToken(final CharacterResult character) {
		String cid = character.getId();
		if (tokens.containsKey(cid)) {
			tokens.get(cid).removeFromParent();
			tokens.remove(cid);
		}
		final Image token = new Image(ImageUtils.get().getPortraitUrl(character));
		token.setSize("30px", "30px");
		if (character.getTokenHidden() == null || !character.getTokenHidden()) {
			token.setStyleName(clientFactory.getResources().style().visibleToken());
		} else {
			token.setStyleName(clientFactory.getResources().style().hiddenToken());
		}
		token.addClickHandler(new ClickHandler() {
			private boolean selected = false;
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				event.preventDefault();
				selected = !selected;
				if (selected) {
					presenter.selectCharacter(character);
				} else {
					presenter.clearSelection();
				}
			}
		});
		String status = character.getStatus();
		token.setTitle(character.getName() + " (" + (status == null || status.length() == 0 ? "Ok" : status) + ")");
		placeMapField.add(token, character.getTokenX().intValue() - 15, character.getTokenY().intValue() - 15);
		tokens.put(cid, token);
	}
	
	@Override
	public void clearTokens() {
		for (Image token : tokens.values()) {
			token.removeFromParent();
		}
		tokens.clear();
	}
	
	@UiHandler ("openCharacter")
	public void openCharacter(ClickEvent e) {
		presenter.openCharacter();
	}

	@UiHandler ("openGame")
	public void openGame(ClickEvent e) {
		presenter.openGame();
	}

	@UiHandler ("refresh")
	public void refresh(ClickEvent e) {
		presenter.refresh();
	}

	@UiHandler ("important")
	public void important(ClickEvent e) {
		presenter.important();
	}

	@UiHandler ("createMap")
	public void createMap(ClickEvent e) {
		presenter.createMap();
	}

	@UiHandler ("openMap")
	public void openMap(ClickEvent e) {
		presenter.openMap();
	}

	@UiHandler ("createCharacter")
	public void createCharacter(ClickEvent e) {
		presenter.createCharacter();
	}

	@UiHandler ("copyCharacter")
	public void copyCharacter(ClickEvent e) {
		presenter.copyCharacter();
	}

	@UiHandler ("moveCharacterMap")
	public void moveCharacterMap(ClickEvent e) {
		presenter.moveCharacter();
	}

	@UiHandler ("playCharacter")
	public void playCharacter(ClickEvent e) {
		presenter.playCharacter();
	}

	@UiHandler ("hideCharacter")
	public void hideCharacter(ClickEvent e) {
		presenter.hideCharacter();
	}
	
	@UiHandler ("leftTargetCharacter")
	public void leftTargetCharacter(ClickEvent e) {
		presenter.leftTargetCharacter();
	}

	@UiHandler ("rightTargetCharacter")
	public void rightTargetCharacter(ClickEvent e) {
		presenter.rightTargetCharacter();
	}

	public void setLeftTargetCharacterName(String name) {
		leftTargetCharacter.setText(name);
		if (!"No Target Selected".equals(name)) {
			leftTargetCharacter.addStyleName(resources.style().selectedTab());
		} else {
			leftTargetCharacter.removeStyleName(resources.style().selectedTab());
		}
	}

	public void setRightTargetCharacterName(String name) {
		rightTargetCharacter.setText(name);
		if (!"No Target Selected".equals(name)) {
			rightTargetCharacter.addStyleName(resources.style().selectedTab());
		} else {
			rightTargetCharacter.removeStyleName(resources.style().selectedTab());
		}
	}
	private HasText leftHandValue = new HasText() {
		@Override
		public void setText(String text) {
			for (int i = 0; i < leftHandField.getItemCount(); i++) {
				if (leftHandField.getItemText(i).equals(text)) {
					leftHandField.setSelectedIndex(i);
					return;
				}
			}
			leftHandField.setSelectedIndex(0);
		}

		@Override
		public String getText() {
			return leftHandField.getValue(leftHandField.getSelectedIndex());
		}
	};

	public HasText getLeftHandField() {
		return leftHandValue;
	};

	private HasText rightHandValue = new HasText() {
		@Override
		public void setText(String text) {
			for (int i = 0; i < rightHandField.getItemCount(); i++) {
				if (rightHandField.getItemText(i).equals(text)) {
					rightHandField.setSelectedIndex(i);
					return;
				}
			}
			rightHandField.setSelectedIndex(0);
		}

		@Override
		public String getText() {
			return rightHandField.getValue(rightHandField.getSelectedIndex());
		}
	};

	public HasText getRightHandField() {
		return rightHandValue;
	};

	public void setWeaponList(List<GameWeaponResult> weapons, int leftSelection, int rightSelection) {
		
		leftHandField.clear();
		rightHandField.clear();
		for (GameWeaponResult w : weapons) {
			leftHandField.addItem(w.getName());
			rightHandField.addItem(w.getName());
		}
		leftHandField.setSelectedIndex(leftSelection);
		rightHandField.setSelectedIndex(rightSelection);

	}

	@Override
	public void setLeftStrikeList(List<String> sl, int sel) {
		leftTargetStrike.clear();
		for (String s : sl) {
			leftTargetStrike.addItem(s);
		}
		leftTargetStrike.setSelectedIndex(sel);
		updateLeftAttacksAvailable();
	}

	@Override
	public void setRightStrikeList(List<String> sl, int sel) {
		rightTargetStrike.clear();
		for (String s : sl) {
			rightTargetStrike.addItem(s);
		}
		rightTargetStrike.setSelectedIndex(sel);
		updateRightAttacksAvailable();
	}

	private void sizeMap() {
		int w = image.getWidth();
		int h = image.getHeight();
		if (placeMapField.getOffsetHeight() != h) {
			placeMapField.setWidth(w + "px");
			placeMapField.setHeight(h + "px");
		}
	}

	@Override
	public void setLeftTargetArea(Integer area) {
		leftTargetArea.setSelectedIndex(area);
	}
	
	@Override
	public void setRightTargetArea(Integer area) {
		rightTargetArea.setSelectedIndex(area);
	}

	private boolean gmvisible;

	private class GMCommandVisibility implements HasVisibility {

		@Override
		public boolean isVisible() {
			return gmvisible;
		}

		@Override
		public void setVisible(boolean v) {
			gmvisible = v;
			selectLocationChooser.setVisible(gmvisible);
			mapNameField.setVisible(!gmvisible);
			hideCharacter.setVisible(gmvisible);
			copyCharacter.setVisible(gmvisible);
			moveCharacterMap.setVisible(gmvisible);
			createCharacter.setVisible(gmvisible);
			createMap.setVisible(gmvisible);
			openMap.setVisible(gmvisible);
			leftHandField.setVisible(!gmvisible);
			rightHandField.setVisible(!gmvisible);
			leftTargetCharacter.setVisible(!gmvisible);
			rightTargetCharacter.setVisible(!gmvisible);
			leftTargetArea.setVisible(!gmvisible);
			rightTargetArea.setVisible(!gmvisible);
			leftTargetStrike.setVisible(!gmvisible);
			rightTargetStrike.setVisible(!gmvisible);
			leftLabel.setVisible(!gmvisible);
			rightLabel.setVisible(!gmvisible);
		}
		
	}

}
