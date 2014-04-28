package com.hotf.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.validators.CharacterValidator;
import com.hotf.client.action.validators.SkillValidator;
import com.hotf.client.view.AccountView;
import com.hotf.client.view.AppWindowView;
import com.hotf.client.view.CharacterEdit;
import com.hotf.client.view.CharacterView;
import com.hotf.client.view.GameEdit;
import com.hotf.client.view.GameView;
import com.hotf.client.view.GamesView;
import com.hotf.client.view.PlaceDraw;
import com.hotf.client.view.PlaceEdit;
import com.hotf.client.view.PlaceView;
import com.hotf.client.view.PlayView;
import com.hotf.client.view.component.MessagePanel;
import com.hotf.client.view.control.RichTextEditor;
import com.hotf.client.view.dialog.PostEditorDialog;

/**
 * @author u847852
 *
 */
/**
 * @author u847852
 *
 */
public interface ClientFactory {

	DateTimeFormat getDateTimeFormat();

	Resources getResources();

	EventBus getEventBus();

	GameController getGameController();

	AppWindowView getAppWindowView();

	AccountView getStrongholdView();

	PlayView getPostListView();

	GamesView getGameListView();

	PostEditorDialog getPostEditor();

	CharacterEdit getCharacterEdit();

	CharacterView getCharacterView();

	PlaceEdit getLocationView();

	PlaceDraw getLocationDraw();

	PlaceView getPlaceView();
	
	GameEdit getGameEdit();

	GameView getGameView();

	MessagePanel getMessagePanel();

	void setAccount(AccountResult account);

	AccountResult getAccount();

	StringConstants getStringConstants();

	RichTextEditor createRichTextEditor();

    PlaceController getPlaceController();
    
    CharacterValidator getCharacterValidator();

    SkillValidator getSkillValidator();

    HotfRulesEngine getRulesEngine();

}
