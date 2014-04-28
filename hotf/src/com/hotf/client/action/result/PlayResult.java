package com.hotf.client.action.result;
 
import net.customware.gwt.dispatch.shared.Result;

public class PlayResult implements Result {

	private AccountResult account;
	private CharacterResult playingCharacter;
	private PlaceResult location;
	private GameResult game;
	private PlacesResult locations;
	private CharactersResult interested;
	private CharactersResult colocated;
	private PostsResult posts;

	public PlayResult() {
		super();
	}

	/**
	 * @return the account
	 */
	public AccountResult getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(AccountResult account) {
		this.account = account;
	}

	/**
	 * @return the playingCharacter
	 */
	public CharacterResult getPlayingCharacter() {
		return playingCharacter;
	}

	/**
	 * @param playingCharacter the playingCharacter to set
	 */
	public void setPlayingCharacter(CharacterResult playingCharacter) {
		this.playingCharacter = playingCharacter;
	}

	/**
	 * @return the location
	 */
	public PlaceResult getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(PlaceResult location) {
		this.location = location;
	}

	/**
	 * @return the game
	 */
	public GameResult getGame() {
		return game;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameResult game) {
		this.game = game;
	}

	/**
	 * @return the locations
	 */
	public PlacesResult getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(PlacesResult locations) {
		this.locations = locations;
	}

	/**
	 * @return the interested
	 */
	public CharactersResult getInterested() {
		return interested;
	}

	/**
	 * @param interested the interested to set
	 */
	public void setInterested(CharactersResult interested) {
		this.interested = interested;
	}

	/**
	 * @param colocated the colocated to set
	 */
	public void setColocated(CharactersResult colocated) {
		this.colocated = colocated;
	}

	/**
	 * @return the colocated
	 */
	public CharactersResult getColocated() {
		return colocated;
	}

	/**
	 * @return the posts
	 */
	public PostsResult getPosts() {
		return posts;
	}

	/**
	 * @param posts the posts to set
	 */
	public void setPosts(PostsResult posts) {
		this.posts = posts;
	}

}
