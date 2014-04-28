package com.hotf.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeAccountEventHandler;
import com.hotf.client.presenter.AccountActivity.ControlPlace;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace.CreateOrEdit;
import com.hotf.client.presenter.CharacterViewActivity.CharacterViewPlace;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace;
import com.hotf.client.presenter.GameViewActivity.GameViewPlace;
import com.hotf.client.presenter.GamesActivity.GamesPlace;
import com.hotf.client.presenter.PlaceDrawActivity.PlaceDrawPlace;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace;
import com.hotf.client.presenter.PlaceViewActivity.PlaceViewPlace;
import com.hotf.client.presenter.PlayActivity.PlayPlace;

public class HotfHistoryMapper implements PlaceHistoryMapper, ChangeAccountEventHandler {

	private static final String SEPERATOR = ":";

	public HotfHistoryMapper(ClientFactory clientFactory) {
		clientFactory.getEventBus().addHandler(ChangeAccountEvent.TYPE, this);
	}

	private AccountResult account;

	@Override
	public void onChange(ChangeAccountEvent event) {
		account = event.getAccount();
	}

	@Override
	public Place getPlace(String token) {
	
		try {
			
			if (account == null) {
				return new GamesPlace();
			} else if (account.getTacAccepted() == null) {
				return new ControlPlace();
			} else if (token == null) {
				return new PlayPlace();
			} else if (token.startsWith(PlayPlace.TOKEN)) {
				return new PlayPlace();
			} else if (token.startsWith(ControlPlace.TOKEN)) {
				return new ControlPlace();
			} else if (token.startsWith(GamesPlace.TOKEN)) {
				return new GamesPlace();
			} else if (token.contains(CharacterEditPlace.CreateOrEdit.CreateCharacter.toString()) ||
					token.contains(CharacterEditPlace.CreateOrEdit.EditCharacter.toString())) {
				String [] s = token.split(SEPERATOR);
				return new CharacterEditPlace(CreateOrEdit.valueOf(s[0]), s[1]);
			} else if (token.contains(PlaceEditPlace.CreateOrEdit.CreatePlace.toString()) ||
					token.contains(PlaceEditPlace.CreateOrEdit.EditPlace.toString())) {
				String [] s = token.split(SEPERATOR);
				return new PlaceEditPlace(PlaceEditPlace.CreateOrEdit.valueOf(s[0]), s[1], Boolean.parseBoolean(s[2]));
			} else if (token.startsWith(PlaceDrawPlace.TOKEN)) {
				String [] s = token.split(SEPERATOR);
				return new PlaceDrawPlace(s[1]);
			} else if (token.contains(CharacterViewPlace.TOKEN)) {
				String [] s = token.split(SEPERATOR);
				return new CharacterViewPlace(s[1]);
			} else if (token.contains(PlaceViewPlace.TOKEN)) {
				String [] s = token.split(SEPERATOR);
				return new PlaceViewPlace(s[1]);
			} else if (token.contains(GameViewPlace.TOKEN)) {
				String [] s = token.split(SEPERATOR);
				return new GameViewPlace(s[1]);
			} else if (token.contains(GameEditPlace.CreateOrEdit.CreateGame.toString()) || token.contains(GameEditPlace.CreateOrEdit.EditGame.toString())) {
				String [] s = token.split(SEPERATOR);
				return new GameEditPlace(GameEditPlace.CreateOrEdit.valueOf(s[0]), s[1]);
			} else {
				return new PlayPlace();
			}

		} catch (IndexOutOfBoundsException e) {
			
			return new PlayPlace();
			
		}
	}
	
	@Override
	public String getToken(Place place) {
		if (place instanceof PlayPlace) {
			return PlayPlace.TOKEN;
		} else if (place instanceof ControlPlace) {
			return ControlPlace.TOKEN;
		} else if (place instanceof GamesPlace) {
			return GamesPlace.TOKEN;
		} else if (place instanceof CharacterEditPlace) {
			CharacterEditPlace cep = (CharacterEditPlace)place;
			return cep.getCreateOrEdit() + SEPERATOR + cep.getId();
		} else if (place instanceof PlaceEditPlace) {
			PlaceEditPlace pep = (PlaceEditPlace)place;
			return pep.getCreateOrEdit() + SEPERATOR + pep.getId() + SEPERATOR + Boolean.toString(pep.isPublicPlace());
		} else if (place instanceof PlaceDrawPlace) {
			PlaceDrawPlace pdp = (PlaceDrawPlace)place;
			return PlaceDrawPlace.TOKEN + SEPERATOR + pdp.getId();
		} else if (place instanceof CharacterViewPlace) {
			CharacterViewPlace cvp = (CharacterViewPlace)place;
			return CharacterViewPlace.TOKEN + SEPERATOR + cvp.getId();
		} else if (place instanceof PlaceViewPlace) {
			PlaceViewPlace pvp = (PlaceViewPlace)place;
			return PlaceViewPlace.TOKEN + SEPERATOR + pvp.getId();
		} else if (place instanceof GameViewPlace) {
			GameViewPlace gvp = (GameViewPlace)place;
			return GameViewPlace.TOKEN + SEPERATOR + gvp.getId();
		} else if (place instanceof GameEditPlace) {
			GameEditPlace gvp = (GameEditPlace)place;
			return gvp.getCreateOrEdit() + SEPERATOR + gvp.getId();
		} else {
			return GamesPlace.TOKEN;
		}
	}

}