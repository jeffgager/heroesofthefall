/**
 * 
 */
package com.hotf.server;

import com.hotf.server.model.Place;

/**
 * @author Jeff
 *
 */
public class LocationPostCounter extends ShardedCounter {

	private static final String NAME = "Location_";

	public LocationPostCounter(Place place) {
		super(NAME + place.getId());
	}
	
}
