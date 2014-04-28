package com.hotf.server.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.client.standard.StandardDispatchService;
import net.customware.gwt.dispatch.server.DefaultActionHandlerRegistry;
import net.customware.gwt.dispatch.server.Dispatch;
import net.customware.gwt.dispatch.server.InstanceActionHandlerRegistry;
import net.customware.gwt.dispatch.server.SimpleDispatch;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;
import net.customware.gwt.dispatch.shared.ServiceException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hotf.client.action.validators.CharacterValidator;
import com.hotf.client.action.validators.SkillValidator;
import com.hotf.client.exception.NotSignedInException;
import com.hotf.client.exception.ValidationException;
import com.hotf.server.action.CreateStrongholdHandler;
import com.hotf.server.action.GetAccountHandler;
import com.hotf.server.action.GetAccountsHandler;
import com.hotf.server.action.GetCharacterHandler;
import com.hotf.server.action.GetCharactersByPlaceHandler;
import com.hotf.server.action.GetCharactersPlayableHandler;
import com.hotf.server.action.GetGameHandler;
import com.hotf.server.action.GetGamesByNameHandler;
import com.hotf.server.action.GetPlaceHandler;
import com.hotf.server.action.GetPlacesByGameHandler;
import com.hotf.server.action.GetPostHandler;
import com.hotf.server.action.GetPostsHandler;
import com.hotf.server.action.GetTopicsHandler;
import com.hotf.server.action.GetUploadUrlHandler;
import com.hotf.server.action.LoginHandler;
import com.hotf.server.action.LogoutHandler;
import com.hotf.server.action.MarkImportantPostHandler;
import com.hotf.server.action.PlayHandler;
import com.hotf.server.action.SaveAccountHandler;
import com.hotf.server.action.SaveCharacterHandler;
import com.hotf.server.action.SaveGameHandler;
import com.hotf.server.action.SaveOverlayHandler;
import com.hotf.server.action.SavePlaceHandler;
import com.hotf.server.action.SavePostHandler;

@SuppressWarnings("serial")
public class HotfDispatchServlet extends RemoteServiceServlet implements StandardDispatchService {

	private static GetCharacterHandler getCharacterHandler = new GetCharacterHandler();
	private static GetPlaceHandler getPlaceHandler = new GetPlaceHandler();
	private static GetPostsHandler getPostsHandler = new GetPostsHandler();
	private static GetAccountHandler getAccountHandler = new GetAccountHandler();
	
    private Dispatch dispatch;

    public HotfDispatchServlet() {

        InstanceActionHandlerRegistry registry = new DefaultActionHandlerRegistry();

        //create components
        GetAccountHandler getAccountHandler = new GetAccountHandler();
        GetPostHandler getPostHandler = new GetPostHandler();
        CreateStrongholdHandler createStrongholdHandler = new CreateStrongholdHandler();
        GetAccountsHandler getAccountsHandler = new GetAccountsHandler();
        GetGameHandler getGameHandler = new GetGameHandler();
        GetGamesByNameHandler getGamesByNameHandler = new GetGamesByNameHandler();
        GetCharactersPlayableHandler getCharactersPlayableHandler = new GetCharactersPlayableHandler();
        GetCharactersByPlaceHandler getCharactersByPlaceHandler = new GetCharactersByPlaceHandler();
        GetPlacesByGameHandler getPlacesByGameHandler = new GetPlacesByGameHandler();
        GetTopicsHandler getTopicsHandler = new GetTopicsHandler();
        GetPostsHandler getPostsHandler = new GetPostsHandler();
        MarkImportantPostHandler markImportantPostHandler =  new MarkImportantPostHandler();
        SaveAccountHandler saveAccountHandler = new SaveAccountHandler();
        SavePostHandler savePostHandler = new SavePostHandler();
        SaveCharacterHandler saveCharacterHandler = new SaveCharacterHandler(); 
        SavePlaceHandler savePlaceHandler = new SavePlaceHandler();
        SaveGameHandler saveGameHandler = new SaveGameHandler();
        SaveOverlayHandler saveOverlayHandler = new SaveOverlayHandler();
        LoginHandler loginHandler = new LoginHandler();
        PlayHandler playHandler = new PlayHandler();
        LogoutHandler logoutHandler = new LogoutHandler();
        GetUploadUrlHandler getUploadUrlHandler = new GetUploadUrlHandler();
        CharacterValidator characterValidator = new CharacterValidator(new SkillValidator());

        //register components
        
        registry.addHandler(getAccountHandler);
        registry.addHandler(createStrongholdHandler);
        registry.addHandler(getAccountsHandler);
        registry.addHandler(getPlaceHandler);
        registry.addHandler(getCharacterHandler);
        registry.addHandler(getGameHandler);
        registry.addHandler(getCharactersPlayableHandler);
        registry.addHandler(getCharactersByPlaceHandler);
        registry.addHandler(getPlacesByGameHandler);
        registry.addHandler(getTopicsHandler);
        registry.addHandler(getGamesByNameHandler);
        registry.addHandler(getPostsHandler);
        registry.addHandler(markImportantPostHandler);
        registry.addHandler(saveAccountHandler);
        registry.addHandler(savePostHandler);
        registry.addHandler(saveCharacterHandler);
        registry.addHandler(savePlaceHandler);
        registry.addHandler(saveGameHandler);
        registry.addHandler(saveOverlayHandler);
        registry.addHandler(loginHandler);
        registry.addHandler(playHandler);
        registry.addHandler(logoutHandler);
        registry.addHandler(getUploadUrlHandler);

        //set dependencies
        getAccountHandler.setSaveAccountHandler(saveAccountHandler);
        createStrongholdHandler.setGetAccountHandler(getAccountHandler);
        createStrongholdHandler.setGetCharacterHandler(getCharacterHandler);
        createStrongholdHandler.setSaveGameHandler(saveGameHandler);
        createStrongholdHandler.setGetLocationHandler(getPlaceHandler);
        getAccountsHandler.setGetAccountHandler(getAccountHandler);
        getPlaceHandler.setGetAccountHandler(getAccountHandler);
        getPlaceHandler.setGetCharacterHandler(getCharacterHandler);
        getPlaceHandler.setGetGameHandler(getGameHandler);
        getCharacterHandler.setGetAccountHandler(getAccountHandler);
        getCharacterHandler.setGetGameHandler(getGameHandler);
        getCharacterHandler.setGetPlaceHandler(getPlaceHandler);
        getCharactersPlayableHandler.setGetAccountHandler(getAccountHandler);
        getCharactersPlayableHandler.setGetCharacterHandler(getCharacterHandler);
        getCharactersPlayableHandler.setGetGameHandler(getGameHandler);
        getCharactersPlayableHandler.setGetLocationHandler(getPlaceHandler);
        getCharactersByPlaceHandler.setGetCharactersHandler(getCharactersPlayableHandler);
        getCharactersByPlaceHandler.setGetAccountHandler(getAccountHandler);
        getCharactersByPlaceHandler.setGetGameHandler(getGameHandler);
        getCharactersByPlaceHandler.setGetLocationHandler(getPlaceHandler);
        getPlacesByGameHandler.setGetAccountHandler(getAccountHandler);
        getPlacesByGameHandler.setGetGameHandler(getGameHandler);
        getPlacesByGameHandler.setGetLocationHandler(getPlaceHandler);
        getTopicsHandler.setGetAccountHandler(getAccountHandler);
        getTopicsHandler.setGetGameHandler(getGameHandler);
        getTopicsHandler.setGetLocationHandler(getPlaceHandler);
        getGameHandler.setGetAccountHandler(getAccountHandler);
        getGameHandler.setGetCharacterHandler(getCharacterHandler);
        getGamesByNameHandler.setGetAccountHandler(getAccountHandler);
        getGamesByNameHandler.setGetGameHandler(getGameHandler);
        getGamesByNameHandler.setGetLocationHandler(getPlaceHandler);
        getPostHandler.setGetAccountHandler(getAccountHandler);
        getPostHandler.setGetCharacterHandler(getCharacterHandler);
        getPostHandler.setGetPlaceHandler(getPlaceHandler);
        getPostsHandler.setGetAccountHandler(getAccountHandler);
        getPostsHandler.setGetPostHandler(getPostHandler);
        markImportantPostHandler.setGetAccountHandler(getAccountHandler);
        markImportantPostHandler.setGetPostHandler(getPostHandler);
        saveAccountHandler.setGetAccountHandler(getAccountHandler);
        savePostHandler.setGetAccountHandler(getAccountHandler);
        savePostHandler.setGetCharacterHandler(getCharacterHandler);
        savePostHandler.setGetOtherCharactersHandler(getCharactersByPlaceHandler);
        savePostHandler.setGetPostHandler(getPostHandler);
        savePostHandler.setGetPostsHandler(getPostsHandler);
        saveCharacterHandler.setGetAccountHandler(getAccountHandler);
        saveCharacterHandler.setGetCharacterHandler(getCharacterHandler);
        saveCharacterHandler.setGetCharactersHandler(getCharactersPlayableHandler);
        saveCharacterHandler.setGetOtherCharactersHandler(getCharactersByPlaceHandler);
        saveCharacterHandler.setGetGameHandler(getGameHandler);
        saveCharacterHandler.setGetLocationHandler(getPlaceHandler);
        saveCharacterHandler.setSavePostHandler(savePostHandler);
        saveCharacterHandler.setCharacterValidator(characterValidator);
        savePlaceHandler.setGetAccountHandler(getAccountHandler);
        savePlaceHandler.setGetGameHandler(getGameHandler);
        savePlaceHandler.setGetLocationHandler(getPlaceHandler);
        savePlaceHandler.setGetCharactersHandler(getCharactersPlayableHandler);
        savePlaceHandler.setGetLocationsHandler(getPlacesByGameHandler);
        saveGameHandler.setGetAccountHandler(getAccountHandler);
        saveGameHandler.setGetGameHandler(getGameHandler);
        saveGameHandler.setGetCharacterHandler(getCharacterHandler);
        saveGameHandler.setSaveCharacterHandler(saveCharacterHandler);
        saveGameHandler.setSaveLocationHandler(savePlaceHandler);
        saveGameHandler.setGetCharactersHandler(getCharactersPlayableHandler);
        saveGameHandler.setGetLocationHandler(getPlaceHandler);
        saveGameHandler.setGetLocationsHandler(getPlacesByGameHandler);
        saveGameHandler.setSaveAccountHandler(saveAccountHandler);
        saveOverlayHandler.setGetAccountHandler(getAccountHandler);
        saveOverlayHandler.setGetGameHandler(getGameHandler);
        saveOverlayHandler.setGetLocationHandler(getPlaceHandler);
        saveOverlayHandler.setSaveLocationHandler(savePlaceHandler);
        loginHandler.setGetAccountHandler(getAccountHandler);
        playHandler.setGetAccountHandler(getAccountHandler);
        playHandler.setGetCharacterHandler(getCharacterHandler);
        playHandler.setGetCharactersHandler(getCharactersPlayableHandler);
        playHandler.setGetGameHandler(getGameHandler);
        playHandler.setGetLocationHandler(getPlaceHandler);
        playHandler.setGetLocationsHandler(getPlacesByGameHandler);
        playHandler.setGetOtherCharactersHandler(getCharactersByPlaceHandler);
        playHandler.setGetPostsHandler(getPostsHandler);
        playHandler.setSaveAccountHandler(saveAccountHandler);

        dispatch = new SimpleDispatch(registry);

    }

    public Result execute(Action<?> action) throws DispatchException {
        try {
            return dispatch.execute(action);
        } catch ( NotSignedInException e) {
        	UserService userService = UserServiceFactory.getUserService();
        	HttpServletRequest request = perThreadRequest.get();
        	String file = "";
        	String reconstructedURL = null;
			try {
				reconstructedURL = new URL(request.getScheme(),
				                               request.getServerName(),
				                               request.getServerPort(),
				                               file).toString();
			} catch (MalformedURLException e1) {
				reconstructedURL = "www.heroesofthefall.com";
			}
        	String url = userService.createLoginURL(reconstructedURL);
        	log("Redirecting to " + url);
        	e.setUrl(url);
        	throw e;
        } catch ( ValidationException e) {
            throw new ServiceException(e.getMessage());
        } catch ( RuntimeException e) {
            log( "Exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
            throw e;
        }
    }

    public static GetCharacterHandler getGetCharacterHandler() {
    	return getCharacterHandler;
    }
   
    public static GetPlaceHandler getGetLocationHandler() {
    	return getPlaceHandler;
    }

    public static GetPostsHandler getGetPostsHandler() {
    	return getPostsHandler;
    }

    public static GetAccountHandler getGetAccountHandler() {
    	return getAccountHandler;
    }

}
