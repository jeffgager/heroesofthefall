/**
 * 
 */
package com.hotf.client;

import java.util.Collection;
import java.util.List;

import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameImplementResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.action.result.OverlayResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PostResult;


/**
 * @author Jeff
 *
 */
public interface GameController {

	void getAccount();
	void login();
	void play(String characterId);
	void play(String characterId, String locationId);
	void logout();
	void getUploadUrl();

	void getLatestPosts();
	void getLatestImportantPosts();
	void getMorePosts(boolean importantOnly);

	void getLatestPosts(PlaceResult place);
	void getLatestImportantPosts(PlaceResult place);
	void getMorePosts(boolean importantOnly, PlaceResult place);
	
	void getLatestPosts(GameResult place);
	void getLatestImportantPosts(GameResult place);
	void getMorePosts(boolean importantOnly, GameResult place);

	void getLatestTopics();
	void getMoreTopics();
	
	void getLatestGames();
	void getMoreGames();
	void createStronghold(AccountResult account);
	
	void loadCharacter(String characterId);
	void loadGame(String locationId);
	void loadLocation(String locationId);

	void saveAccount(AccountResult account);
	void saveGame(GameResult game);
	void saveLocation(PlaceResult location);
	void saveMap(OverlayResult map);
	void saveCharacter(CharacterResult character);
	void saveCharacter(CharacterResult character, boolean duplicate);
	void savePost(PostResult post);
	void markImportantPost(PostResult post, String characterId);

	void getAccounts(String name);

    List<GameImplementResult> getSkillImplements(String skillName);
    GameWeaponResult getWeapon(String name);
	CharacterResult getCharacterById(String id);
	String getTargetArea(int idx);
	Collection<CharacterResult> getOtherCharacters();
    
}
