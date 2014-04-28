package com.hotf.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.validators.CharacterValidator;
import com.hotf.client.action.validators.SkillValidator;
import com.hotf.client.presenter.AppWindowPresenter;
import com.hotf.client.presenter.MessagePanelPresenter;
import com.hotf.client.view.AccountView;
import com.hotf.client.view.AccountViewImpl;
import com.hotf.client.view.AppWindowView;
import com.hotf.client.view.AppWindowViewImpl;
import com.hotf.client.view.CharacterEdit;
import com.hotf.client.view.CharacterEditImpl;
import com.hotf.client.view.CharacterView;
import com.hotf.client.view.CharacterViewImpl;
import com.hotf.client.view.GameEdit;
import com.hotf.client.view.GameEditImpl;
import com.hotf.client.view.GameView;
import com.hotf.client.view.GameViewImpl;
import com.hotf.client.view.GamesView;
import com.hotf.client.view.GamesViewImpl;
import com.hotf.client.view.PlaceDraw;
import com.hotf.client.view.PlaceDrawImpl;
import com.hotf.client.view.PlaceEdit;
import com.hotf.client.view.PlaceEditImpl;
import com.hotf.client.view.PlaceView;
import com.hotf.client.view.PlaceViewImpl;
import com.hotf.client.view.PlayView;
import com.hotf.client.view.PlayViewImpl;
import com.hotf.client.view.component.MessagePanel;
import com.hotf.client.view.component.MessagePanelImpl;
import com.hotf.client.view.control.RichTextEditor;
import com.hotf.client.view.dialog.PostEditorDialog;
import com.hotf.client.view.dialog.PostEditorDialogImpl;

public class ClientFactoryImpl implements ClientFactory  {
	
	public ClientFactoryImpl() {
		
		//AppWindow and Components
		messagePanel = new MessagePanelImpl(this);
		new MessagePanelPresenter(this);
		appWindowView = new AppWindowViewImpl(this);
		appWindowView.setPresenter(new AppWindowPresenter(this));

		//PostListView
		postListView = new PlayViewImpl(this);

		//PlaceListView
		gameListView = new GamesViewImpl(this);

		//CharacterView
		characterView = new CharacterViewImpl(this);

		//GameView 
		gameView = new GameViewImpl(this);

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //EventBus
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private EventBus eventBus;

	/**
	 * @return EventBus
	 */
	public EventBus getEventBus() {
		
		if (eventBus == null) {
			eventBus = new SimpleEventBus();
		}
		
		return eventBus;
		
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Place Controller
	///////////////////////////////////////////////////////////////////////////////////////////////////////
    private PlaceController placeController;

    @Override
    public PlaceController getPlaceController() {
    	if (placeController == null) {
    		placeController = new PlaceController(getEventBus());
		}
		return placeController;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Game Controller
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private GameController gameController;

	/**
	 * @return the gameController
	 */
	public GameController getGameController() {
		if (gameController == null) {
			gameController = new GameControllerImpl(this);
		}
		return gameController;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Account
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private AccountResult account;
	
	/**
	 * @return the account
	 */
	public AccountResult getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(AccountResult account) {
		this.account = account;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Date time format
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy hh:mm:ss");

	@Override
	public DateTimeFormat getDateTimeFormat() {
		return dateFormat;
	}

	private StringConstants stringConstants = (StringConstants) GWT.create(StringConstants.class);

	/////////////////////////////////////////////////////////////////////////////////////////////////////
    //StringConstants
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public StringConstants getStringConstants() {
		return stringConstants;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Resources
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private Resources resources;

	@Override
	public Resources getResources() {
		if (resources == null) {
			resources = GWT.create(Resources.class);
			resources.style().ensureInjected();
		}
		return resources;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //AppWindow
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private AppWindowView appWindowView;

	/**
	 * @return AppWindow
	 */
	@Override
	public AppWindowView getAppWindowView() {
		
		return appWindowView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //MessagePanel
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private MessagePanel messagePanel;

	/**
	 * @return MessagePanel
	 */
	@Override
	public MessagePanel getMessagePanel() {
		
		return messagePanel;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //PostListView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private PlayView postListView;

	/**
	 * @return PostListView
	 */
	@Override
	public PlayView getPostListView() {
		
		return postListView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //PlaceView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private PlaceView placeView;

	@Override
	public PlaceView getPlaceView() {

		if (placeView == null) {
			placeView = new PlaceViewImpl(this);
		}
		return placeView;

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //GameListView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private GamesView gameListView;

	/**
	 * @return GameListView
	 */
	@Override
	public GamesView getGameListView() {
		
		return gameListView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //CharacterEdit
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private CharacterEdit characterEdit;

	/**
	 * @return CharacterEdit
	 */
	@Override
	public CharacterEdit getCharacterEdit() {

		if (characterEdit == null) {
			characterEdit = new CharacterEditImpl(this);
		}
		return characterEdit;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //CharacterView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private CharacterView characterView;

	/**
	 * @return CharacterView
	 */
	@Override
	public CharacterView getCharacterView() {

		return characterView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //LocationView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private PlaceEdit locationView;

	/**
	 * @return LocationView
	 */
	@Override
	public PlaceEdit getLocationView() {

		if (locationView == null) {
			locationView = new PlaceEditImpl(this);
		}
		return locationView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //LocationDraw
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private PlaceDraw placeDraw;

	@Override
	public PlaceDraw getLocationDraw() {
		if (placeDraw == null) {
			placeDraw = new PlaceDrawImpl(this);
		}
		return placeDraw;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //GameEdit
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private GameEdit gameEdit;

	/**
	 * @return GameEdit
	 */
	@Override
	public GameEdit getGameEdit() {

		if (gameEdit == null) {
			gameEdit = new GameEditImpl(this);
		}
		return gameEdit;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //GameView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private GameView gameView;

	/**
	 * @return GameView
	 */
	@Override
	public GameView getGameView() {

		return gameView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //StrongholdView
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private AccountView strongholdView;

	/**
	 * @return StrongholdView
	 */
	@Override
	public AccountView getStrongholdView() {

		if (strongholdView == null) {
			strongholdView = new AccountViewImpl(this);
		}
		return strongholdView;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //PostEditorDialog
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private PostEditorDialog postEditor;

	/**
	 * @return PostEditorDialog
	 */
	@Override
	public PostEditorDialog getPostEditor() {
		
		if (postEditor == null) {
			postEditor = new PostEditorDialogImpl(this);
		}
		return postEditor;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //CreateRichTextEditor
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return RichTextEditor
	 */
	@Override
	public RichTextEditor createRichTextEditor() {
		
		RichTextEditor richText = new RichTextEditor(this);
		return richText;
	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Validators
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private CharacterValidator characterValidator;

	@Override
	public CharacterValidator getCharacterValidator() {
		if (characterValidator == null) {
			characterValidator = new CharacterValidator(new SkillValidator());
		}
		return characterValidator;
	}
	
	private SkillValidator skillValidator;

	@Override
	public SkillValidator getSkillValidator() {
		if (skillValidator == null) {
			skillValidator = new SkillValidator();
		}
		return skillValidator;
	}

	private HotfRulesEngine rulesEngine;
	
	@Override
	public HotfRulesEngine getRulesEngine() {
		if (rulesEngine == null) {
			rulesEngine = new HotfRulesEngine();
			rulesEngine.setClientFactory(this);
		}
		return rulesEngine;
	}
	
}
