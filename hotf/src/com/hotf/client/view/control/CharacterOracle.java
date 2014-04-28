/**
 * 
 */
package com.hotf.client.view.control;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.hotf.client.ClientFactory;
import com.hotf.client.ImageUtils;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.event.LoadCharactersEvent;
import com.hotf.client.event.LoadCharactersEventHandler;

/**
 * @author Jeff
 *
 */
public class CharacterOracle extends SuggestOracle implements LoadCharactersEventHandler {

	private List<CharacterResult> characters;

	public class CharacterSuggestion implements Suggestion {
		
		private CharacterResult character;
		private String imagehtml;
		private String label;
		
		public CharacterSuggestion(CharacterResult character) {
			this.character = character;
			this.label = character.getLabel();
			this.imagehtml = "<div><img style='width:22px;height:22px;'src='" + ImageUtils.get().getPortraitUrl(character) + "'/>" + label + "</div>";
		}
		
		@Override
		public String getDisplayString() {
			return imagehtml;
		}
		
		@Override
		public String getReplacementString() {
			return character.getName();
		}

		public CharacterResult getCharacter() {
			return character;
		}

	}

	private ClientFactory clientFactory;

	public CharacterOracle(ClientFactory clientFactory) {
		clientFactory.getEventBus().addHandler(LoadCharactersEvent.TYPE, this);
		this.clientFactory = clientFactory;

	}

	@Override
	public void onLoadCharacters(LoadCharactersEvent event) {
		characters = event.getCharacters();
	}

	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {

		final Response r = new Response();

		final ArrayList<CharacterSuggestion> suggestions = new ArrayList<CharacterSuggestion>();

		r.setSuggestions(suggestions);
		
		if (characters == null) {
			return;
		}

		int searchRows = clientFactory.getAccount().getSearchRows();
		for (CharacterResult character : characters) {
			if (suggestions.size() >= searchRows) {
				break;
			}
			suggestions.add(new CharacterSuggestion(character));
		}
		callback.onSuggestionsReady(request, r);
	}
	
	@Override
	public void requestSuggestions(final Request request, final Callback callback) {

		final Response r = new Response();

		final ArrayList<CharacterSuggestion> suggestions = new ArrayList<CharacterSuggestion>();

		r.setSuggestions(suggestions);
		
		if (characters == null) {
			return;
		}

		int searchRows = clientFactory.getAccount().getSearchRows();
		String query = request.getQuery();
		for (CharacterResult character : characters) {
			if (suggestions.size() >= searchRows) {
				break;
			}
			if (character.getLabel().toUpperCase().contains(query.toUpperCase())) {
				suggestions.add(new CharacterSuggestion(character));
			}
		}
		callback.onSuggestionsReady(request, r);

	}

	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}

}
