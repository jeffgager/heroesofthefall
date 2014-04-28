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
public class GamesResult implements Result {

	private List<GameResult> games = new ArrayList<GameResult>();
	
	public GamesResult() {
		super();
	}

	public List<GameResult> getGames() {
		return games;
	}
	
}