package com.hotf.client.presenter;

import java.util.List;

import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeAccountEventHandler;
import com.hotf.client.event.LoadCharactersEvent;
import com.hotf.client.event.LoadCharactersEventHandler;
import com.hotf.client.presenter.AccountActivity.ControlPlace;
import com.hotf.client.presenter.GamesActivity.GamesPlace;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.AppWindowView;

/**
 * CommandPanel Presenter.
 * 
 * @author Jeff Gager
 */
public class AppWindowPresenter implements AppWindowView.Presenter, ChangeAccountEventHandler, LoadCharactersEventHandler {

	private ClientFactory clientFactory;
	
	/**
	 * Constructor.
	 * 
	 * @param clientFactory ClientFactory
	 */
	public AppWindowPresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		clientFactory.getEventBus().addHandler(ChangeAccountEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadCharactersEvent.TYPE, this);
	}

	private AccountResult account;

	@Override
	public void onChange(ChangeAccountEvent event) {
		
		AppWindowView appWindowView = clientFactory.getAppWindowView();
		
		//find out if we are signed up properly.
		account = event.getAccount();
		
		if (account == null) {
			return;
		}

		boolean setupdone = account.getTacAccepted() != null;
		appWindowView.getStrongholdLinkHidden().setVisible(account != null);
		appWindowView.getGamesLinkHidden().setVisible(setupdone);
		checkPlayLink();

	}

	private List<CharacterResult> characters;
	
	@Override
	public void onLoadCharacters(LoadCharactersEvent event) {
		characters = event.getCharacters();
		checkPlayLink();
	}

	private void checkPlayLink() {
		AppWindowView appWindowView = clientFactory.getAppWindowView();
		appWindowView.getPlayLinkHidden().setVisible(account.getTacAccepted() != null && characters != null && characters.size() > 0);
	}
	
	@Override
	public void stronghold() {
		clientFactory.getPlaceController().goTo(new ControlPlace());
	}

	@Override
	public void play() {
		clientFactory.getPlaceController().goTo(new PlayPlace());
	}

	@Override
	public void games() {
		clientFactory.getPlaceController().goTo(new GamesPlace());
	}

	@Override
	public void loginOut() {
		if (account != null && account.getName() != null) {
			clientFactory.getGameController().logout();
		} else {
			clientFactory.getGameController().login();
		}
	}

}