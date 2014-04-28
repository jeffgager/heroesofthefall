/**
 * 
 */
package com.hotf.server.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.hotf.server.PMF;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Character;
import com.hotf.server.model.Place;

/**
 * @author Jeff
 * 
 */
@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(UploadServlet.class.getName());

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		BlobKey blobKey = blobs.get("upload").get(0);

		String uploadType = req.getParameter("uploadType");
		String uploadId = req.getParameter("uploadId");

		try {

			PersistenceManager pm = PMF.get().getPersistenceManager();
	        PMUtils.setPersistenceManager(pm);
			pm.currentTransaction().begin();
	
	        try {
	        	
				MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
				cache.delete(uploadId);

	        } catch (Throwable t) {
	        	
				log.severe(t.getMessage());
				t.printStackTrace();

	        }
	
			if ("map".equals(uploadType)) {
				
				Place place = HotfDispatchServlet.getGetLocationHandler().getLocation(uploadId);
				if (place.getMap() != null) {
					blobstoreService.delete(place.getMap());
				}
				place.setMap(blobKey);
				place.setUpdated(new Date());
	
			} else if ("portrait".equals(uploadType)) {
				
				Character character = HotfDispatchServlet.getGetCharacterHandler().getCharacter(uploadId);
				if (character.getPortrait() != null) {
					blobstoreService.delete(character.getPortrait());
				}
				character.setPortrait(blobKey);
				character.setUpdated(new Date());
	
			}
	
			pm.currentTransaction().commit();
	        try {
	        	
				MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
				cache.delete(uploadId);

	        } catch (Throwable t) {
	        	
				log.severe(t.getMessage());
				t.printStackTrace();

	        }
			
		} catch (RuntimeException t) {
			
			log.log(Level.SEVERE, t.getMessage(), t);
			t.printStackTrace();

		}

	}

}