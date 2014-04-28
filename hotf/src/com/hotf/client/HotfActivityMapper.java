package com.hotf.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.hotf.client.presenter.AccountActivity;
import com.hotf.client.presenter.CharacterEditActivity;
import com.hotf.client.presenter.CharacterViewActivity;
import com.hotf.client.presenter.GameEditActivity;
import com.hotf.client.presenter.GameViewActivity;
import com.hotf.client.presenter.GamesActivity;
import com.hotf.client.presenter.PlaceDrawActivity;
import com.hotf.client.presenter.PlaceEditActivity;
import com.hotf.client.presenter.PlaceViewActivity;
import com.hotf.client.presenter.PlayActivity;
import com.hotf.client.presenter.AccountActivity.ControlPlace;
import com.hotf.client.presenter.CharacterEditActivity.CharacterEditPlace;
import com.hotf.client.presenter.CharacterViewActivity.CharacterViewPlace;
import com.hotf.client.presenter.GameEditActivity.GameEditPlace;
import com.hotf.client.presenter.GameViewActivity.GameViewPlace;
import com.hotf.client.presenter.GamesActivity.GamesPlace;
import com.hotf.client.presenter.PlaceDrawActivity.PlaceDrawPlace;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace;
import com.hotf.client.presenter.PlaceViewActivity.PlaceViewPlace;
import com.hotf.client.presenter.PlayActivity.PlayPlace;

public class HotfActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;
	
	public HotfActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		gamesActivity = new GamesActivity(clientFactory);
		controlActivity = new AccountActivity(clientFactory);
		playActivity = new PlayActivity(clientFactory);
	}

	private Activity gamesActivity;
	private Activity controlActivity;
	private Activity playActivity;

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof ControlPlace) {
			return controlActivity;
		} else if (place instanceof GamesPlace) {
			return gamesActivity;
		} else if (place instanceof PlayPlace) {
			if (clientFactory.getAccount().getPlayingCharacterId().equals(clientFactory.getAccount().getPersonalCharacterId())) {
				return gamesActivity;
			}
			return playActivity;
		} else if (place instanceof CharacterEditPlace) {
			return new CharacterEditActivity((CharacterEditPlace)place, clientFactory);
		} else if (place instanceof CharacterViewPlace) {
			return new CharacterViewActivity((CharacterViewPlace)place, clientFactory);
		} else if (place instanceof PlaceViewPlace) {
			return new PlaceViewActivity((PlaceViewPlace)place, clientFactory);
		} else if (place instanceof GameViewPlace) {
			return new GameViewActivity((GameViewPlace)place, clientFactory);
		} else if (place instanceof GameEditPlace) {
			return new GameEditActivity((GameEditPlace)place, clientFactory);
		} else if (place instanceof PlaceEditPlace) {
			return new PlaceEditActivity((PlaceEditPlace)place, clientFactory);
		} else if (place instanceof PlaceDrawPlace) {
			return new PlaceDrawActivity((PlaceDrawPlace)place, clientFactory);
		} else {
			return gamesActivity;
		}
	}

}
