package com.hotf.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.OverlayResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePlaceEventHandler;
import com.hotf.client.event.LoadPlaceEvent;
import com.hotf.client.event.LoadPlaceEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.view.PlaceDraw;

public class PlaceDrawActivity extends AbstractActivity implements PlaceDraw.Presenter, LoadPlaceEventHandler, ChangePlaceEventHandler {

	private ClientFactory clientFactory;
	private PlaceResult location;
	private HandlerRegistration loadPlaceRegistration;
	private HandlerRegistration changePlaceRegistration;

	public PlaceDrawActivity(PlaceDrawPlace place, final ClientFactory clientFactory) {

		this.clientFactory = clientFactory;

		//show form
		final PlaceDraw placeDraw = clientFactory.getLocationDraw();
		placeDraw.setPresenter(this);

		//listen to location change events
		changePlaceRegistration = clientFactory.getEventBus().addHandler(ChangePlaceEvent.TYPE, this);
		loadPlaceRegistration = clientFactory.getEventBus().addHandler(LoadPlaceEvent.TYPE, this);

		clientFactory.getGameController().loadLocation(place.getId());

		clientFactory.getAppWindowView().setWidget(placeDraw);

	}

	private AcceptsOneWidget container;

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {

		this.container = container;

	}

	@Override
	public void onStop() {
		loadPlaceRegistration.removeHandler();
		changePlaceRegistration.removeHandler();
	}
	
	@Override
	public void onLoadPlace(LoadPlaceEvent event) {
		
		//edit location
		location = event.getPlace();

		//update form fields with data
		final PlaceDraw placeDraw = clientFactory.getLocationDraw();
		placeDraw.setImageData(
				location.getHasMap() ? ImageUtils.get().getMapUrl(location) : null, 
				location.getHasOverlay() ? ImageUtils.get().getOverlayUrl(location): null);

		container.setWidget(placeDraw);
		
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Drawing Map - " + location.getLabel()));

	}

	private boolean saving = false;

	@Override
	public void close() {
		History.back();
	}
	
	@Override
	public void save() {

		//save changes to character
		saving = true;
		OverlayResult map = new OverlayResult();
		map.setId(location.getId());
		final PlaceDraw placeDraw = clientFactory.getLocationDraw();
		map.setOverlayUrl(placeDraw.getImageData());
		
		clientFactory.getGameController().saveMap(map);
		
	}

	@Override
	public void onChange(ChangePlaceEvent event) {
		if (saving) {
			saving = false;
			close();
		}
	}
	
	public static class PlaceDrawPlace extends Place {

		public static final String TOKEN = "DrawPlace";

		private String id;
		
		public PlaceDrawPlace(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

}