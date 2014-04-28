/**
 * 
 */
package com.hotf.client.view.control;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.event.LoadPlacesEvent;
import com.hotf.client.event.LoadPlacesEventHandler;

/**
 * @author Jeff
 *
 */
public class LocationOracle extends SuggestOracle implements LoadPlacesEventHandler {

	private List<PlaceResult> locations;

	public class LocationSuggestion implements Suggestion {
		
		private PlaceResult location;
		private String imagehtml;
		private String label;
		
		public LocationSuggestion(PlaceResult location) {
			this.location = location;
			this.label = location.getLabel();
			this.imagehtml = "<div><img style='width:22px;height:22px;'src='" + ImageUtils.get().getMapOverlayUrl(location) + "'/>" + label + "</div>";
		}
		
		@Override
		public String getDisplayString() {
			return imagehtml;
		}
		
		@Override
		public String getReplacementString() {
			return location.getName();
		}

		public PlaceResult getLocation() {
			return location;
		}

	}
	 
	private ClientFactory clientFactory;

	public LocationOracle(ClientFactory clientFactory) {
		clientFactory.getEventBus().addHandler(LoadPlacesEvent.TYPE, this);
		this.clientFactory = clientFactory;
	}

	@Override
	public void onLoadPlaces(LoadPlacesEvent event) {
		locations = event.getPlaces();
	}
	
	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {

		final Response r = new Response();
		final ArrayList<LocationSuggestion> suggestions = new ArrayList<LocationSuggestion>();
		r.setSuggestions(suggestions);
		
		if (locations == null) {
			return;
		}

		int searchRows = clientFactory.getAccount().getSearchRows();

		for (PlaceResult location : locations) {
			if (suggestions.size() >= searchRows) {
				break;
			}
			suggestions.add(new LocationSuggestion(location));
		}
		callback.onSuggestionsReady(request, r);

	}
	
	@Override
	public void requestSuggestions(final Request request, final Callback callback) {

		final Response r = new Response();
		final ArrayList<LocationSuggestion> suggestions = new ArrayList<LocationSuggestion>();
		r.setSuggestions(suggestions);
		
		if (locations == null) {
			return;
		}

		int searchRows = clientFactory.getAccount().getSearchRows();
		String query = request.getQuery();
		for (PlaceResult location : locations) {
			if (suggestions.size() >= searchRows) {
				break;
			}
			if (location.getLabel().toUpperCase().contains(query.toUpperCase())) {
				suggestions.add(new LocationSuggestion(location));
			}
		}
		callback.onSuggestionsReady(request, r);

	}

	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}

}
