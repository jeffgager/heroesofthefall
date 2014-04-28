/**
 * 
 */
package com.hotf.server;

import com.hotf.server.model.Game;

/**
 * @author Jeff
 *
 */
public class GamePostCounter extends ShardedCounter {

	private static final String NAME = "Game_";
	
	public GamePostCounter(Game game) {
		super(NAME + game.getId());
	}
	
}
