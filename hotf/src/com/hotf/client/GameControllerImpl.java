/**
 * 
 */
package com.hotf.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.customware.gwt.dispatch.client.DefaultExceptionHandler;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.client.standard.StandardDispatchAsync;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hotf.client.action.CreateStrongholdAction;
import com.hotf.client.action.GetAccountAction;
import com.hotf.client.action.GetAccountsAction;
import com.hotf.client.action.GetCharacterAction;
import com.hotf.client.action.GetGameAction;
import com.hotf.client.action.GetGamesByNameAction;
import com.hotf.client.action.GetPlaceAction;
import com.hotf.client.action.GetPostsAction;
import com.hotf.client.action.GetTopicsByNameAction;
import com.hotf.client.action.GetUploadUrlAction;
import com.hotf.client.action.LoginAction;
import com.hotf.client.action.LogoutAction;
import com.hotf.client.action.MarkImportantPostAction;
import com.hotf.client.action.PlayAction;
import com.hotf.client.action.SaveAccountAction;
import com.hotf.client.action.SaveCharacterAction;
import com.hotf.client.action.SaveGameAction;
import com.hotf.client.action.SaveOverlayAction;
import com.hotf.client.action.SavePlaceAction;
import com.hotf.client.action.SavePostAction;
import com.hotf.client.action.result.AccountResult;
import com.hotf.client.action.result.AccountsResult;
import com.hotf.client.action.result.CharacterResult;
import com.hotf.client.action.result.GameArtifactResult;
import com.hotf.client.action.result.GameImplementResult;
import com.hotf.client.action.result.GameResult;
import com.hotf.client.action.result.GameWeaponResult;
import com.hotf.client.action.result.GamesResult;
import com.hotf.client.action.result.LogoutResult;
import com.hotf.client.action.result.OverlayResult;
import com.hotf.client.action.result.PlaceResult;
import com.hotf.client.action.result.PlacesResult;
import com.hotf.client.action.result.PlayResult;
import com.hotf.client.action.result.PostResult;
import com.hotf.client.action.result.PostsResult;
import com.hotf.client.action.result.UploadUrlResult;
import com.hotf.client.event.ChangeAccountEvent;
import com.hotf.client.event.ChangeCharacterEvent;
import com.hotf.client.event.ChangeGameEvent;
import com.hotf.client.event.ChangeMarkerEvent;
import com.hotf.client.event.ChangeMarkerEventHandler;
import com.hotf.client.event.ChangePlaceEvent;
import com.hotf.client.event.ChangePostEvent;
import com.hotf.client.event.LoadAccountsEvent;
import com.hotf.client.event.LoadCharacterEvent;
import com.hotf.client.event.LoadCharactersEvent;
import com.hotf.client.event.LoadGameEvent;
import com.hotf.client.event.LoadGamesEvent;
import com.hotf.client.event.LoadGamesEventHandler;
import com.hotf.client.event.LoadOtherCharactersEvent;
import com.hotf.client.event.LoadPlaceEvent;
import com.hotf.client.event.LoadPlacesEvent;
import com.hotf.client.event.LoadPostsEvent;
import com.hotf.client.event.LoadPostsEventHandler;
import com.hotf.client.event.LoadTopicsEvent;
import com.hotf.client.event.LoadTopicsEventHandler;
import com.hotf.client.event.LoginEvent;
import com.hotf.client.event.MessageEvent;
import com.hotf.client.event.MessageEvent.Level;
import com.hotf.client.event.PlayEvent;
import com.hotf.client.event.SelectCharacterEvent;
import com.hotf.client.event.UploadUrlEvent;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.client.exception.ValidationException;
import com.hotf.client.presenter.AccountActivity.ControlPlace;
import com.hotf.client.presenter.GamesActivity.GamesPlace;

/**
 * @author Jeff
 *
 */
public class GameControllerImpl implements GameController, LoadPostsEventHandler, LoadTopicsEventHandler, LoadGamesEventHandler, ChangeMarkerEventHandler {

	private final DispatchAsync dispatchAsync = new StandardDispatchAsync(new DefaultExceptionHandler());
	private final String[] TARGET_AREA = {
			"Head", "Left Eye", "Right Eye", "Face", "Neck", "Throat",
			"Left Shoulder", "Right Shoulder", "Left Arm", "Right Arm",
			"Left Hand", "Right Hand", "Chest", "Abdomen", "Buttock",
			"Groin", "Left Thigh", "Right Thigh", "Left Calf", 
			"Right Calf", "Left Foot", "Right Foot", "Back"};
	
	private ClientFactory clientFactory;
	
	private CharacterResult playingCharacter;
	private PlaceResult location;
	private GameResult game;
    private Logger logger = Logger.getLogger("GameController");
    
	public GameControllerImpl(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

		clientFactory.getEventBus().addHandler(LoadPostsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadTopicsEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(LoadGamesEvent.TYPE, this);
		clientFactory.getEventBus().addHandler(ChangeMarkerEvent.TYPE, this);
	}

	@Override
	public void getAccount() {
		
		logger.log(java.util.logging.Level.INFO, "Getting Account");
		
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetAccountAction(), new AsyncCallback<AccountResult>() {
			@Override
			public void onSuccess(AccountResult account) {
				clientFactory.setAccount(account);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(account));
				login();
			}
			@Override
			public void onFailure(Throwable caught) {
				clientFactory.setAccount(null);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(null));
				clientFactory.getPlaceController().goTo(new GamesPlace());
			}
		});

	}

	@Override
	public void login() {
		
		logger.log(java.util.logging.Level.INFO, "Requested Login");

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new LoginAction(), new AsyncCallback<AccountResult>() {
			@Override
			public void onSuccess(AccountResult account) {
				//set account
				clientFactory.setAccount(account);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(account));

				logger.log(java.util.logging.Level.INFO, "Started Login: " + account.getName());
				if (account.getTacAccepted() == null) {
					logger.log(java.util.logging.Level.INFO, "Starting preferences activity");
					clientFactory.getPlaceController().goTo(new ControlPlace());
					clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Please enter your user name"));
					return;
				}
				
				clientFactory.getEventBus().fireEvent(new LoginEvent(account));

			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	@Override
	public void play(String characterId, String locationId) {
		
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new PlayAction(characterId, locationId), new AsyncCallback<PlayResult>() {
			@Override
			public void onSuccess(PlayResult response) {

				//set account
				AccountResult account = response.getAccount();
				clientFactory.setAccount(account);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(account));

				if (response.getPlayingCharacter() == null) {
					clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Please enter your user name"));
					clientFactory.getPlaceController().goTo(new ControlPlace());
					return;
				}

				game = response.getGame();
				location = response.getLocation();
				playingCharacter = response.getPlayingCharacter();

				clientFactory.getEventBus().fireEvent(new LoadGameEvent(game));
				clientFactory.getEventBus().fireEvent(new PlayEvent(game, location, playingCharacter));

				//get list of locations in the game 
				clientFactory.getEventBus().fireEvent(new LoadPlacesEvent(response.getLocations().getPlaces()));

				//load posts
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, true, response.getPosts().getPosts()));
				
				//get list of character user can play
				List<CharacterResult> playableCharacters = response.getInterested().getCharacters();
				clientFactory.getEventBus().fireEvent(new LoadCharactersEvent(playableCharacters));

				//load other characters
				updateCharacters(response.getColocated().getCharacters());
				clientFactory.getEventBus().fireEvent(new LoadOtherCharactersEvent(response.getColocated().getCharacters()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());

			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});

	}

	@Override
	public void play(String characterId) {
		
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new PlayAction(characterId), new AsyncCallback<PlayResult>() {
			@Override
			public void onSuccess(PlayResult response) {

				//set account
				AccountResult account = response.getAccount();
				clientFactory.setAccount(account);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(account));

				if (response.getPlayingCharacter() == null) {
					clientFactory.getEventBus().fireEvent(new MessageEvent(Level.INFO, "Please enter your user name"));
					clientFactory.getPlaceController().goTo(new ControlPlace());
					return;
				}

				game = response.getGame();
				location = response.getLocation();
				playingCharacter = response.getPlayingCharacter();

				clientFactory.getEventBus().fireEvent(new LoadGameEvent(game));
				clientFactory.getEventBus().fireEvent(new PlayEvent(game, location, playingCharacter));

				//get list of locations in the game 
				clientFactory.getEventBus().fireEvent(new LoadPlacesEvent(response.getLocations().getPlaces()));

				//load posts
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, true, response.getPosts().getPosts()));
				
				//get list of character user can play
				List<CharacterResult> playableCharacters = response.getInterested().getCharacters();
				clientFactory.getEventBus().fireEvent(new LoadCharactersEvent(playableCharacters));

				//load other characters
				updateCharacters(response.getColocated().getCharacters());
				clientFactory.getEventBus().fireEvent(new LoadOtherCharactersEvent(response.getColocated().getCharacters()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());

			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});

	}


	private Map<String, List<GameImplementResult>> skillImplements = new HashMap<String, List<GameImplementResult>>();

	@Override
	public List<GameImplementResult> getSkillImplements(String skillName) {
		List<GameImplementResult> wl = skillImplements.get(skillName);
		if (wl != null) {
			return wl;
		}
		wl = new ArrayList<GameImplementResult>();
		for (GameWeaponResult w : game.getWeapons()) {
			if (w.getSkillNames().contains(skillName)) {
				wl.add(w);
			}
		}
		for (GameArtifactResult w : game.getArtifacts()) {
			if (w.getSkillNames().contains(skillName)) {
				wl.add(w);
			}
		}
		return wl;
	}

	@Override
	public void logout() {
		
		String url = GWT.getHostPageBaseURL() + Window.Location.getQueryString();
		dispatchAsync.execute(new LogoutAction(url), new AsyncCallback<LogoutResult>() {
			@Override
			public void onSuccess(LogoutResult result) {
				Window.Location.assign(result.getUrl());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});

	}

	@Override
	public void getUploadUrl() {
		
		String url = GWT.getHostPageBaseURL() + Window.Location.getQueryString();
		dispatchAsync.execute(new GetUploadUrlAction(url), new AsyncCallback<UploadUrlResult>() {
			@Override
			public void onSuccess(UploadUrlResult result) {
				clientFactory.getEventBus().fireEvent(new UploadUrlEvent(result.getUrl()));
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});

	}

	/**
	 * Get the latest Topics
	 */
	public void getLatestTopics() {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetTopicsByNameAction(null), new AsyncCallback<PlacesResult>() {
			@Override
			public void onSuccess(PlacesResult result) {
				clientFactory.getEventBus().fireEvent(new LoadTopicsEvent(true, result.getPlaces()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	private String lastTopicCreatedOrder;

	/**
	 * Get the more Topics
	 */
	public void getMoreTopics() {
		if (lastTopicCreatedOrder == null) {
			getLatestTopics();
		}
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetTopicsByNameAction(lastTopicCreatedOrder), new AsyncCallback<PlacesResult>() {
			@Override
			public void onSuccess(PlacesResult result) {
				clientFactory.getEventBus().fireEvent(new LoadTopicsEvent(false, result.getPlaces()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	@Override
	public void onLoadTopics(LoadTopicsEvent event) {

		//get the posts and stop if there are no posts to load
		List<PlaceResult> places = event.getTopics();
		if (places.size() <= 0) {
			return;
		}

		//record the id of the final post so that 'Get more posts' knows where to start from
		lastTopicCreatedOrder = places.get(places.size() - 1).getCreatedOrder();

	}

	/**
	 * Get the latest Games
	 */
	public void getLatestGames() {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetGamesByNameAction(null), new AsyncCallback<GamesResult>() {
			@Override
			public void onSuccess(GamesResult result) {
				clientFactory.getEventBus().fireEvent(new LoadGamesEvent(true, result.getGames()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	private String lastGameCreatedOrder;

	/**
	 * Get the more Games
	 */
	public void getMoreGames() {
		if (lastGameCreatedOrder == null) {
			getLatestGames();
		}
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetGamesByNameAction(lastGameCreatedOrder), new AsyncCallback<GamesResult>() {
			@Override
			public void onSuccess(GamesResult result) {
				clientFactory.getEventBus().fireEvent(new LoadGamesEvent(false, result.getGames()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	@Override
	public void onLoadGames(LoadGamesEvent event) {

		List<GameResult> games = event.getGames();
		if (games.size() <= 0) {
			return;
		}

		//record the id of the final post so that 'Get more posts' knows where to start from
		lastGameCreatedOrder = games.get(games.size() - 1).getStart().getCreatedOrder();

	}

	/**
	 * Get the latest Posts
	 */
	@Override
	public void getLatestPosts() {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetPostsAction(false, null), new AsyncCallback<PostsResult>() {
			@Override
			public void onSuccess(PostsResult result) {
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, true, result.getPosts()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	/**
	 * Get the latest Posts
	 */
	@Override
	public void getLatestPosts(PlaceResult place) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetPostsAction(false, null, place.getId()), new AsyncCallback<PostsResult>() {
			@Override
			public void onSuccess(PostsResult result) {
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, true, result.getPosts()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	/**
	 * Get the latest important posts
	 */
	@Override
	public void getLatestImportantPosts() {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		GetPostsAction a = null;
		if (location.getType() == null) {
			a = new GetPostsAction(true, null);
		} else {
			a = new GetPostsAction(true, null, location.getId());
		}
		dispatchAsync.execute(a, new AsyncCallback<PostsResult>() {
			@Override
			public void onSuccess(PostsResult result) {
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(true, true, result.getPosts()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}
	
	/**
	 * Get the latest important posts
	 */
	@Override
	public void getLatestImportantPosts(PlaceResult place) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetPostsAction(true, null, place.getId()), new AsyncCallback<PostsResult>() {
			@Override
			public void onSuccess(PostsResult result) {
				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(true, true, result.getPosts()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}

	@Override
	public void getLatestPosts(GameResult game) {
		getLatestPosts(game.getStart());
	}
	
	@Override
	public void getMorePosts(boolean importantOnly, GameResult game) {
		getMorePosts(importantOnly, game.getStart());
	}
	
	@Override
	public void getLatestImportantPosts(GameResult game) {
		getLatestImportantPosts(game.getStart());
	}
	
	private String lastPostCreatedOrder;

	/**
	 * Get some more posts.
	 */
	@Override
	public void getMorePosts(boolean importantOnly) {
		if (lastPostCreatedOrder == null) {
			if (importantOnly) {
				getLatestImportantPosts();
			} else {
				getLatestPosts();
			}
		} else {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
			if (importantOnly) {
				dispatchAsync.execute(new GetPostsAction(true, lastPostCreatedOrder), new AsyncCallback<PostsResult>() {
					@Override
					public void onSuccess(PostsResult result) {
						clientFactory.getEventBus().fireEvent(new LoadPostsEvent(true, false, result.getPosts()));
						clientFactory.getEventBus().fireEvent(new MessageEvent());
					}
					@Override
					public void onFailure(Throwable caught) {
						handleFailure(caught);
					}
				});
			} else {
				GetPostsAction a = null;
				if (location.getType() == null) {
					a = new GetPostsAction(false, lastPostCreatedOrder);
				} else {
					a = new GetPostsAction(false, lastPostCreatedOrder, location.getId());
				}
				dispatchAsync.execute(a, new AsyncCallback<PostsResult>() {
					@Override
					public void onSuccess(PostsResult result) {
						clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, false, result.getPosts()));
						clientFactory.getEventBus().fireEvent(new MessageEvent());
					}
					@Override
					public void onFailure(Throwable caught) {
						handleFailure(caught);
					}
				});
			}
		}
	}

	/**
	 * Get some more posts.
	 */
	@Override
	public void getMorePosts(boolean importantOnly, PlaceResult place) {
		if (lastPostCreatedOrder == null) {
			if (importantOnly) {
				getLatestImportantPosts();
			} else {
				getLatestPosts();
			}
		} else {
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
			if (importantOnly) {
				dispatchAsync.execute(new GetPostsAction(true, lastPostCreatedOrder, location.getId()), new AsyncCallback<PostsResult>() {
					@Override
					public void onSuccess(PostsResult result) {
						clientFactory.getEventBus().fireEvent(new LoadPostsEvent(true, false, result.getPosts()));
						clientFactory.getEventBus().fireEvent(new MessageEvent());
					}
					@Override
					public void onFailure(Throwable caught) {
						handleFailure(caught);
					}
				});
			} else {
				GetPostsAction a = null;
				if (place.getType() == null) {
					a = new GetPostsAction(false, lastPostCreatedOrder);
				} else {
					a = new GetPostsAction(false, lastPostCreatedOrder, place.getId());
				}
				dispatchAsync.execute(a, new AsyncCallback<PostsResult>() {
					@Override
					public void onSuccess(PostsResult result) {
						clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, false, result.getPosts()));
						clientFactory.getEventBus().fireEvent(new MessageEvent());
					}
					@Override
					public void onFailure(Throwable caught) {
						handleFailure(caught);
					}
				});
			}
		}
	}

	@Override
	public void onLoadPosts(LoadPostsEvent event) {

		//get the posts and stop if there are no posts to load
		List<PostResult> posts = event.getPosts();
		if (posts.size() <= 0) {
			return;
		}

		//record the id of the final post so that 'Get more posts' knows where to start from
		lastPostCreatedOrder = posts.get(posts.size() - 1).getCreatedOrder();

	}

	@Override
	public void saveAccount(AccountResult account) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new SaveAccountAction(account), new AsyncCallback<AccountResult>() {
			@Override
			public void onSuccess(AccountResult result) {
				clientFactory.setAccount(result);
				if (result.getStrongholdGameId() == null) {
					createStronghold(result);
				} else {
					clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(result));
					clientFactory.getEventBus().fireEvent(new MessageEvent());
					login();
				}
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void createStronghold(AccountResult account) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new CreateStrongholdAction(account), new AsyncCallback<AccountResult>() {
			@Override
			public void onSuccess(AccountResult result) {
				clientFactory.setAccount(result);
				clientFactory.getEventBus().fireEvent(new ChangeAccountEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
				login();
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void saveGame(GameResult game) {
		final boolean created = game.getId() == null;
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new SaveGameAction(game), new AsyncCallback<GameResult>() {
			@Override
			public void onSuccess(GameResult result) {
				GameControllerImpl.this.game = result;
				if (created) {
					play(result.getGameMasterCharacterId());
				}
				clientFactory.getEventBus().fireEvent(new LoadPlacesEvent(result.getLocations()));
				clientFactory.getEventBus().fireEvent(new LoadCharactersEvent(result.getCharacters()));
				clientFactory.getEventBus().fireEvent(new ChangeGameEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void loadLocation(String locationId) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetPlaceAction(locationId), new AsyncCallback<PlaceResult>() {
			@Override
			public void onSuccess(PlaceResult result) {
				clientFactory.getEventBus().fireEvent(new LoadPlaceEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}
	
	@Override
	public void loadCharacter(String characterId) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetCharacterAction(characterId), new AsyncCallback<CharacterResult>() {
			@Override
			public void onSuccess(CharacterResult result) {
				clientFactory.getEventBus().fireEvent(new LoadCharacterEvent(game, result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}
	
	@Override
	public void loadGame(String gameId) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetGameAction(gameId), new AsyncCallback<GameResult>() {
			@Override
			public void onSuccess(GameResult result) {
				GameControllerImpl.this.game = result;
				clientFactory.getEventBus().fireEvent(new LoadGameEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}
	
	@Override
	public void saveLocation(final PlaceResult location) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new SavePlaceAction(location), new AsyncCallback<PlaceResult>() {
			@Override
			public void onSuccess(PlaceResult result) {
				GameControllerImpl.this.location = result;
				clientFactory.getEventBus().fireEvent(new LoadPlacesEvent(result.getLocations()));
				clientFactory.getEventBus().fireEvent(new LoadCharactersEvent(result.getCharacters()));
				clientFactory.getEventBus().fireEvent(new ChangePlaceEvent(result));
				clientFactory.getEventBus().fireEvent(new LoadPlaceEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void saveMap(OverlayResult map) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new SaveOverlayAction(map), new AsyncCallback<PlaceResult>() {
			@Override
			public void onSuccess(PlaceResult result) {
				clientFactory.getEventBus().fireEvent(new ChangePlaceEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void saveCharacter(final CharacterResult character) {
		saveCharacter(character, false);
	}
	
	@Override
	public void saveCharacter(final CharacterResult character, boolean duplicate) {

		//set location and position of a new character and set character type
		if (character.getId() == null || duplicate) {
			character.setTokenX(new Double(markerx));
			character.setTokenY(new Double(markery));
		} else {
			if (!character.getPlayerAccountId().equals(character.getGameMasterAccountId())) {
				character.setCharacterType("Player");
			} else if (character.getId().equals(game.getGameMasterCharacterId())) {
				character.setCharacterType("GM");
			} else {
				character.setCharacterType("NPC");
			}
		}
		
		//default account to logged in account
		String userAccountId = clientFactory.getAccount().getId();
		if (character.getPlayerAccountId() == null) {
			character.setPlayerAccountId(userAccountId);
		}

		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		SaveCharacterAction action = new SaveCharacterAction(character);
		action.setDuplicate(duplicate);
		dispatchAsync.execute(action, new AsyncCallback<CharacterResult>() {
			@Override
			public void onSuccess(CharacterResult result) {
				updateCharacters(result.getOtherCharacters());
				clientFactory.getEventBus().fireEvent(new LoadCharactersEvent(result.getCharacters()));
				clientFactory.getEventBus().fireEvent(new LoadOtherCharactersEvent(result.getOtherCharacters()));
				clientFactory.getEventBus().fireEvent(new ChangeCharacterEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
		
	}
	
	@Override
	public void savePost(final PostResult post) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new SavePostAction(post), new AsyncCallback<PostResult>() {
			@Override
			public void onSuccess(PostResult result) {
				
				// HRDS fix for updated posts where the update may not be in the query
				List<PostResult> fp = result.getPosts();
				if (post.getId() != null) {
					int postCount = fp.size();
					for (int i = 0; i < postCount; i++) {
						PostResult p = fp.get(i);
						if (p.getId().equals(post.getId())) {
							fp.remove(i);
							fp.add(i, post);
						}
					}
				}

				clientFactory.getEventBus().fireEvent(new LoadPostsEvent(false, true, fp));
				clientFactory.getEventBus().fireEvent(new ChangePostEvent(result));
				CharacterResult changedCharacter = result.getChangedCharacter();
				if (changedCharacter != null) {
					clientFactory.getEventBus().fireEvent(new ChangeCharacterEvent(changedCharacter));
				}
				clientFactory.getEventBus().fireEvent(new SelectCharacterEvent(playingCharacter));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void markImportantPost(PostResult post, String characterId) {
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new MarkImportantPostAction(post.getId(), characterId), new AsyncCallback<PostResult>() {
			@Override
			public void onSuccess(PostResult result) {
				clientFactory.getEventBus().fireEvent(new ChangePostEvent(result));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			}
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			};
		});
	}

	@Override
	public void getAccounts(String name) {
		
		clientFactory.getEventBus().fireEvent(new MessageEvent(Level.LOADING));
		dispatchAsync.execute(new GetAccountsAction(name), new AsyncCallback<AccountsResult>() {
			public void onSuccess(AccountsResult result) {
				clientFactory.getEventBus().fireEvent(new LoadAccountsEvent(result.getAccounts()));
				clientFactory.getEventBus().fireEvent(new MessageEvent());
			};
			@Override
			public void onFailure(Throwable caught) {
				handleFailure(caught);
			}
		});
	}
	
	private int markerx = -50;
	private int markery = -50;

	@Override
	public void onChange(ChangeMarkerEvent event) {
		markerx = event.getX();
		markery = event.getY();
	}

	@Override
	public GameWeaponResult getWeapon(String name) {
		for (GameWeaponResult w : game.getWeapons()) {
			if (name.equals(w.getName())) {
				return w;
			}
		}
		return null;
	}

	private Map<String, CharacterResult> otherCharacters = new HashMap<String, CharacterResult>();

	private void updateCharacters(List<CharacterResult> characters) {
		otherCharacters.clear();
		for (CharacterResult c : characters) {
			otherCharacters.put(c.getId(), c);
		}
	}

	@Override
	public Collection<CharacterResult> getOtherCharacters() {
		return otherCharacters.values();
	}
	
	@Override
	public CharacterResult getCharacterById(String id) {
		if (id == null) { 
			return null;
		}
		return otherCharacters.get(id);
	}

	@Override
	public String getTargetArea(int idx) {
		return TARGET_AREA[idx];
	}
	
	private void handleFailure(Throwable error) {
		if (error instanceof NotSignedInException) {
			NotSignedInException nsi = (NotSignedInException)error;
			GWT.log("redirecting to " + nsi.getUrl());
			Window.Location.assign(nsi.getUrl() + URL.encode(Window.Location.getQueryString()));
		} else if (error instanceof DispatchException) {
			DispatchException e = (DispatchException)error;
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, e.getMessage()));
		} else if (error instanceof ValidationException) {
			ValidationException e = (ValidationException)error;
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, e.getMessage()));
		} else {
			logger.log(java.util.logging.Level.SEVERE, error.getMessage(), error);
			GWT.log(error.getMessage(), error);
			clientFactory.getEventBus().fireEvent(new MessageEvent(Level.ERROR, "Failed"));
		}
	}

}
