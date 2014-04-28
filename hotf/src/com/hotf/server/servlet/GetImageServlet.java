/**
 * 
 */
package com.hotf.server.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.server.PMF;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Character;
import com.hotf.server.model.Place;

/**
 * @author Jeff
 * TODO Only show locations you have a character present in unless you are GM
 */
@SuppressWarnings("serial")
public class GetImageServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(GetImageServlet.class.getName());
    private SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String characterId = request.getParameter("characterId");
    	String mapOverlayId = request.getParameter("mapOverlayId");
    	String mapId = request.getParameter("mapId");
    	String overlayId = request.getParameter("overlayId");
    	
    	try {

	        if (characterId != null && !characterId.equals("null")) {
	
	        	serveCharacter(characterId, response);
	
	        } else if (mapOverlayId != null && !mapOverlayId.equals("null")) {
	
	        	serveMapOverlay(mapOverlayId, response);
	
	        } else if (mapId != null && !mapId.equals("null")) {
	        	
	        	serveMap(mapId, response);
	        
	        } else if (overlayId != null && !overlayId.equals("null")) {
	        	
	        	serveOverlay(overlayId, response);
	        
	        }
    	
    	} catch (RuntimeException t) {
    		
			log.log(Level.SEVERE, t.getMessage(), t);
			throw t;

    	}
    	
    }

    private void serveDefaultPortrait(HttpServletResponse response) throws IOException {

        response.setHeader("Cache-Control", "max-age=28800");
        response.setContentType("image/png");
        response.sendRedirect("/images/notoken.png");

    }

    private void serveDefaultMap(HttpServletResponse response) throws IOException {

        response.setHeader("Cache-Control", "max-age=28800");
    	response.setContentType("image/png");
        response.sendRedirect("/images/nomap.png");

    }

    private void serveMap(String mapId, HttpServletResponse response) throws IOException {
    	
    	log.info("GetImageServlet map:" + mapId);
    	Place place = getLocation(mapId);
        if (place == null) {
        	serveDefaultMap(response);
        	return;
        }

        BlobKey map = place.getMap();
        if (map == null) {
        	serveDefaultMap(response);
        	return;
        }

        BlobInfoFactory blobInfoFactory = new BlobInfoFactory(); 
        BlobInfo avatarinfo = blobInfoFactory.loadBlobInfo(map);
        response.setContentType(avatarinfo.getContentType());
        Date lm = place.getUpdated();
        if (lm == null) {
        	lm = place.getCreated();
        }
        response.setHeader("Last-Modified", df.format(lm));
        response.setHeader("Cache-Control", "max-age=28800");
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		blobstoreService.serve(map, response);

    }
    
    private void serveOverlay(String overlayId, HttpServletResponse response) throws IOException {
    	
       	log.info("GetImageServlet overlay:" + overlayId);
       	Place place = getLocation(overlayId);
        if (place == null) {
        	return;
        }

        if (place.getOverlay() == null) {
        	return;
        }

        response.setContentType("image/png");
        Date lm = place.getUpdated();
        if (lm == null) {
        	lm = place.getCreated();
        }
        response.setHeader("Last-Modified", df.format(lm));
        response.setHeader("Cache-Control", "max-age=28800");
		response.getOutputStream().write(place.getOverlay());

    }
    
    private void serveMapOverlay(String mapOverlayId, HttpServletResponse response) throws IOException {
    	
		log.info("GetImageServlet map with overlay:" + mapOverlayId);

		Place place = getLocation(mapOverlayId);
        if (place == null) {
        	serveDefaultMap(response);
        	return;
        }

        BlobKey map = place.getMap();
        if (map == null) {
        	serveOverlay(mapOverlayId, response);
        	return;
        }

        if (place.getOverlay() == null) {
        	serveMap(mapOverlayId, response);
        	return;
        }

        response.setContentType("image/png");
        Date lm = place.getUpdated();
        if (lm == null) {
        	lm = place.getCreated();
        }
        response.setHeader("Last-Modified", df.format(lm));
        response.setHeader("Cache-Control", "max-age=28800");

        try {
        	
            MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
            if (cache.contains("mapOverlay:" + mapOverlayId)) {
    			response.getOutputStream().write((byte[])cache.get("mapOverlay:" + mapOverlayId));
    			return;
    		}

        } catch (Throwable t) {
        	
			log.severe(t.getMessage());
			t.printStackTrace();

        }

        //get map and overlay imageas
        Image mapimg = ImagesServiceFactory.makeImageFromBlob(map);
        Image overlayimg = ImagesServiceFactory.makeImage(place.getOverlay());

        //*fudge* apply a transform to mapimg to make width and height available, discard the transformed image - don't need it.
        //This is required because of a blobstore bug, or maybe a shortcut. Could be performance implications doing this.
        ImagesServiceFactory.getImagesService().applyTransform(
        		ImagesServiceFactory.makeCrop(0.0D, 0.0D, 1.0D, 1.0D), mapimg);

        //create list of composites
        ArrayList<Composite> imgs = new ArrayList<Composite>();
        imgs.add(ImagesServiceFactory.makeComposite(mapimg, 0, 0, 1.0F, Composite.Anchor.TOP_LEFT));
        imgs.add(ImagesServiceFactory.makeComposite(overlayimg, 0, 0, 1.0F, Composite.Anchor.TOP_LEFT));

        //build composite image
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        Image compositeimg = imagesService.composite(imgs, mapimg.getWidth(), mapimg.getHeight(), 0xfffffff, ImagesService.OutputEncoding.PNG);

        //serve composite image
        byte[] data = compositeimg.getImageData();
        try {
        	
            MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    		cache.put("mapOverlay:" + mapOverlayId, data);

        } catch (Throwable t) {
        	
			log.severe(t.getMessage());
			t.printStackTrace();

        }
		response.getOutputStream().write(data);

    }
    
    private void serveCharacter(String characterId, HttpServletResponse response) throws IOException {
    	
        log.info("GetImageServlet portrait:" + characterId);

        Character character = getCharacter(characterId);
        if (character == null) {
        	serveDefaultPortrait(response);
        	return;
        }

        BlobKey portrait = character.getPortrait();
        if (portrait == null) {
        	serveDefaultPortrait(response);
        	return;
        }

        BlobInfoFactory blobInfoFactory = new BlobInfoFactory(); 
        BlobInfo portraitinfo = blobInfoFactory.loadBlobInfo(portrait);
        response.setContentType(portraitinfo.getContentType());
        Date lm = character.getUpdated();
        if (lm == null) {
        	lm = character.getCreated();
        }
        response.setHeader("Last-Modified", df.format(lm));
        response.setHeader("Cache-Control", "max-age=28800");
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		blobstoreService.serve(portrait, response);

    }
    
	private Place getLocation(String id) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
        PMUtils.setPersistenceManager(pm);
        try {
        	return HotfDispatchServlet.getGetLocationHandler().getLocation(id);
        } finally {
            pm.close();
        }

    }
    
	private Character getCharacter(String id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        PMUtils.setPersistenceManager(pm);
        try {
        	return HotfDispatchServlet.getGetCharacterHandler().getCharacter(id);
        } finally {
            pm.close();
        }

    }
    
}
