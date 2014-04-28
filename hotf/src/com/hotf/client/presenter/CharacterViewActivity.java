package com.hotf.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.event.LoadCharacterEvent;
import com.hotf.client.event.LoadCharacterEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace.CreateOrEdit;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.CharacterView;

public class CharacterViewActivity extends AbstractActivity implements CharacterView.Presenter, LoadCharacterEventHandler {

	private CharacterViewPlace place;
	private ClientFactory clientFactory;
	private CharacterResult character;
	private boolean loading = false;
	private HandlerRegistration loadCharacterRegistration;

	public CharacterViewActivity(CharacterViewPlace place, ClientFactory clientFactory) {

		this.place = place;
		this.clientFactory = clientFactory;

		loadCharacterRegistration = clientFactory.getEventBus().addHandler(LoadCharacterEvent.TYPE, this);

		final CharacterView characterView = clientFactory.getCharacterView();
		characterView.setPresenter(this);

		//clear form fields 
		characterView.getPlayerField().setText("");
		characterView.getNameField().setText("");
		characterView.getStatusField().setText("");
		characterView.getPortraitField().setUrl(ImageUtils.get().getDefaultPortrait());
		characterView.getPortraitHidden().setVisible(clientFactory.getAccount().getShowPortraits());
		characterView.getDescriptionField().setHTML("");
		characterView.getEditHidden().setVisible(false);

	}
	
	@Override
	public void onStop() {
		loadCharacterRegistration.removeHandler();
	}

	private AcceptsOneWidget container;
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		this.container = container;
		loading = true;
		clientFactory.getGameController().loadCharacter(place.getId());

	}

	@Override
	public void onLoadCharacter(LoadCharacterEvent event) {
		
		if (!loading) {
			return;
		}
		loading = false;

		character = event.getCharacter();

		//update form fields with data
		final CharacterView characterView = clientFactory.getCharacterView();
		characterView.getPlayerField().setText(character.getPlayerName());
		characterView.getNameField().setText(character.getName());
		String status = character.getStatus();
		characterView.getStatusField().setText(status == null || status.length() <= 0 ? "Ok" : status);
		characterView.getPortraitField().setUrl(ImageUtils.get().getPortraitUrl(character));
		characterView.getPortraitHidden().setVisible(clientFactory.getAccount().getShowPortraits());
		String description = character.getDescription();
		characterView.getDescriptionField().setHTML(description != null ? description : "");
		characterView.getEditHidden().setVisible(character.getUpdatePermission());

		container.setWidget(characterView);

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Character - " + character.getLabel()));

	}
	
	protected String integerToString(Integer i) {
		if (i == null) {
			return null;
		}
		return i.toString();
	}

	@Override
	public void close() {
		clientFactory.getPlaceController().goTo(new PlayPlace());
	}

	@Override
	public void edit() {
		clientFactory.getPlaceController().goTo(new CharacterEditPlace(CreateOrEdit.EditCharacter, character.getId()));
	}

	public static class CharacterViewPlace extends Place {

		public static final String TOKEN = "ViewCharacter";
		private String id;
		
		public CharacterViewPlace(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

}