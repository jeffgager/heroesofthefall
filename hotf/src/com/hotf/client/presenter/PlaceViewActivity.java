package com.hotf.client.presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.event.LoadPlaceEvent;
import com.hotf.client.event.LoadPlaceEventHandler;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.UploadUrlEvent;
import com.hotf.client.event.UploadUrlEventHandler;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.presenter.PlaceDrawActivity.PlaceDrawPlace;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace;
import com.hotf.client.presenter.PlaceEditActivity.PlaceEditPlace.CreateOrEdit;
import com.hotf.client.presenter.PlayActivity.PlayPlace;
import com.hotf.client.view.PlaceView;
import com.hotf.client.view.dialog.UploadDialog;

public class PlaceViewActivity extends AbstractActivity implements PlaceView.Presenter, LoadPlaceEventHandler {

	private ClientFactory clientFactory;
	private PlaceResult location;
	private boolean loading = false;
	private HandlerRegistration loadPlaceRegistration;

	public PlaceViewActivity(PlaceViewPlace place, ClientFactory clientFactory) {
		
		this.clientFactory = clientFactory;

		loadPlaceRegistration = clientFactory.getEventBus().addHandler(LoadPlaceEvent.TYPE, this);

		final PlaceView placeView = clientFactory.getPlaceView();
		placeView.setPresenter(this);

		//clear form fields 
		placeView.getNameField().setText("");
		placeView.getMapField().setUrl(ImageUtils.get().getDefaultMap());
		placeView.getDescriptionField().setHTML("");
		placeView.getEditHidden().setVisible(false);
		placeView.getDrawHidden().setVisible(false);
		placeView.getUploadHidden().setVisible(false);
		placeView.getClearMapHidden().setVisible(false);

		loading = true;
		clientFactory.getGameController().loadLocation(place.getId());

	}

	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		
		final PlaceView placeView = clientFactory.getPlaceView();
		container.setWidget(placeView);

	}

	@Override
	public void onStop() {
		loadPlaceRegistration.removeHandler();
	}
	
	@Override
	public void onLoadPlace(LoadPlaceEvent event) {
		
		if (!loading) {
			return;
		}
		loading = false;

		location = event.getPlace();

		//update form fields with data
		final PlaceView placeView = clientFactory.getPlaceView();
		placeView.getNameField().setText(location.getName());
		placeView.getMapField().setUrl(ImageUtils.get().getMapOverlayUrl(location));
		String description = location.getDescription();
		placeView.getDescriptionField().setHTML(description != null ? description : "");
		placeView.getEditHidden().setVisible(location.getUpdatePermission());
		placeView.getDrawHidden().setVisible(location.getUpdatePermission());
		placeView.getUploadHidden().setVisible(location.getUpdatePermission());
		placeView.getClearMapHidden().setVisible(location.getUpdatePermission());

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.PAGE, "Place - " + location.getLabel()));

	}
	
	@Override
	public void close() {
		clientFactory.getPlaceController().goTo(new PlayPlace());
	}

	@Override
	public void edit() {
		clientFactory.getPlaceController().goTo(new PlaceEditPlace(CreateOrEdit.EditPlace, location.getId(), false));
	}

	@Override
	public void uploadMap() {

		final UploadDialog ud = GWT.create(UploadDialog.class);
		ud.setText("Upload Map");
		ud.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clientFactory.getGameController().getUploadUrl();
			}
		});
		clientFactory.getEventBus().addHandler(UploadUrlEvent.TYPE, new UploadUrlEventHandler() {
			@Override
			public void onUrl(UploadUrlEvent event) {
				ud.submit(event.getUrl(), "map", location.getId());
			}
		});
		ud.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				clientFactory.getGameController().loadLocation(location.getId());
				loading = true;
				ud.hide();
			}
		});
		ud.center();

	}

	@Override
	public void clearMap() {
		location.setHasMap(false);
		location.setHasOverlay(false);
		loading = true;
		clientFactory.getGameController().saveLocation(location);
	}

	@Override
	public void draw() {
		clientFactory.getPlaceController().goTo(new PlaceDrawPlace(location.getId()));
	}

	public static class PlaceViewPlace extends Place {

		public static final String TOKEN = "ViewPlace";
		private String id;
		
		public PlaceViewPlace(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

}