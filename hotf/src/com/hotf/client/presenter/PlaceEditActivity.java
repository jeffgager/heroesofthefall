package com.hotf.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.event.ChangeMarkerEvent;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePlaceEventHandler;
import com.hotf.client.event.LoadPlaceEvent;
import com.hotf.client.event.LoadPlaceEventHandler;
import com.hotf.client.event.SelectPlaceEvent;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace.CreateOrEdit;
import com.hotf.client.presenter.PlaceViewActivity.PlaceViewPlace;
import com.hotf.client.view.PlaceEdit;

public class PlaceEditActivity extends AbstractActivity implements PlaceEdit.Presenter, LoadPlaceEventHandler, ChangePlaceEventHandler {

	private ClientFactory clientFactory;
	private PlaceResult location;
	private HandlerRegistration loadPlaceRegistration;
	private HandlerRegistration changePlaceRegistration;

	public PlaceEditActivity(PlaceEditPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;
		
		//initialise form fields for new character
		final PlaceEdit locationView = clientFactory.getLocationView();
		locationView.setPresenter(this);

		//listen to events
		changePlaceRegistration = clientFactory.getEventBus().addHandler(ChangePlaceEvent.TYPE, this);
		loadPlaceRegistration = clientFactory.getEventBus().addHandler(LoadPlaceEvent.TYPE, this);

		if (place.getCreateOrEdit().equals(CreateOrEdit.CreatePlace)) {
			
			location = new PlaceResult();
			location.setGameId(place.getId());
			location.setType(place.isPublicPlace() ? "PUBLIC" : null);

			locationView.getNameField().setText(place.isPublicPlace() ? "New Topic" : "New Place");
			locationView.getDescriptionField().setHTML("");
			locationView.getTypeField().setText(place.isPublicPlace() ? "PUBLIC" : "PRIVATE");
			
		} else {
			
			locationView.getNameField().setText("");
			locationView.getDescriptionField().setHTML("");
			clientFactory.getGameController().loadLocation(place.getId());

		}

	}

	@Override
	public void onStop() {
		loadPlaceRegistration.removeHandler();
		changePlaceRegistration.removeHandler();
	}
	
	@Override
	public void onLoadPlace(LoadPlaceEvent event) {

		this.location = event.getPlace();

		//update form fields with data
		final PlaceEdit locationView = clientFactory.getLocationView();
		locationView.getNameField().setText(location.getName());
		if (location.getDescription() != null) {
			locationView.getDescriptionField().setHTML(location.getDescription());
		} else {
			locationView.getDescriptionField().setHTML("");
		}
		String type = location.getType();
		locationView.getTypeField().setText(type == null ? "PRIVATE" : type);

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		//show form
		final PlaceEdit locationView = clientFactory.getLocationView();
		clientFactory.getAppWindowView().setWidget(locationView);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				locationView.getNameFocus().setFocus(true);
			}
		});
		
	}
	private boolean saving = false;

	@Override
	public void close() {
		History.back();
	}

	@Override
	public void save() {

		//save changes to character
		final PlaceEdit locationView = clientFactory.getLocationView();
		location.setName(locationView.getNameField().getText());
		String typeValue = locationView.getTypeField().getText();
		location.setType("PRIVATE".equals(typeValue) ? null : typeValue);
		location.setDescription(locationView.getDescriptionField().getHTML());
		saving = true;
		clientFactory.getGameController().saveLocation(location);
		clientFactory.getEventBus().fireEvent(new ChangeMarkerEvent());

	}

	@Override
	public void onChange(ChangePlaceEvent event) {
		if (saving) {
			saving = false;
			boolean created = location.getId() == null;
			location = event.getPlace();
			clientFactory.getEventBus().fireEvent(new SelectPlaceEvent(location));
			if (created) {
				clientFactory.getPlaceController().goTo(new PlaceViewPlace(location.getId()));
			} else {
				close();
			}
		}
	}
	
	public static class PlaceEditPlace extends Place {

		public static enum CreateOrEdit {CreatePlace, EditPlace};

		private CreateOrEdit createOrEdit;
		private String id;
		private boolean publicPlace;
		
		public PlaceEditPlace(CreateOrEdit createOrEdit, String id, boolean publicPlace) {
			this.createOrEdit = createOrEdit;
			this.id = id;
			this.publicPlace = publicPlace;
		}

		public CreateOrEdit getCreateOrEdit() {
			return createOrEdit;
		}
		
		public String getId() {
			return id;
		}

		public boolean isPublicPlace() {
			return publicPlace;
		}

	}

}