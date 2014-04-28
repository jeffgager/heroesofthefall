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
public class CharactersResult implements Result {

	private List<CharacterResult> characters = new ArrayList<CharacterResult>();
	
	public CharactersResult() {
		super();
	}

	public List<CharacterResult> getCharacters() {
		return characters;
	}
	
}