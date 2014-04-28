/**
 * 
 */
package com.hotf.client.action.result;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

/**
 * @author Jeff
 *
 */
public class PlacesResult implements Result {

	private List<PlaceResult> locations = new ArrayList<PlaceResult>();
	
	public PlacesResult() {
		super();
	}

	public List<PlaceResult> getPlaces() {
		return locations;
	}
	
}