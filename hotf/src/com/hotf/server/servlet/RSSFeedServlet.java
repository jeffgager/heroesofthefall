/**
 * 
 */
package com.hotf.server.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hotf.server.PMF;
import com.hotf.server.PMUtils;
import com.hotf.server.model.Character;
import com.hotf.server.model.Post;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Jeff
 */
@SuppressWarnings("serial")
public class RSSFeedServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(RSSFeedServlet.class.getName());
    private static final String DEFAULT_FEED_TYPE = "default.feed.type";
    private static final String FEED_TYPE = "type";
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";
    private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";
 	
    private String _defaultFeedType;

    public void init() {
        _defaultFeedType = getServletConfig().getInitParameter(DEFAULT_FEED_TYPE);
        _defaultFeedType = (_defaultFeedType!=null) ? _defaultFeedType : "atom_0.3";
    }
 
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        PMUtils.setPersistenceManager(pm);

        response.setContentType("application/rss+xml");
        
        try {
        	
            String characterId = request.getParameter("characterId");

            if (characterId == null || characterId.equals("null")) {
            	return;
            }

            Character character = HotfDispatchServlet.getGetCharacterHandler().getCharacter(characterId);
            if (character == null) {
            	return;
            }

            SyndFeed feed = getFeed(request, character);
            
            String feedType = request.getParameter(FEED_TYPE);
            feedType = (feedType!=null) ? feedType : _defaultFeedType;
            feed.setFeedType(feedType);
 
            response.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,response.getWriter());

        } catch (FeedException t) {

            String msg = COULD_NOT_GENERATE_FEED_ERROR;
            log.severe(msg);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
 
		} finally {
            pm.close();
        }

	}

    private SyndFeed getFeed(HttpServletRequest req, Character c) {
    	
        SyndFeed feed = new SyndFeedImpl();
        
        feed.setTitle(c.getName() + " at Heroes of The Fall");
        feed.setLink("http://play.heroesofthefall.com");
        feed.setDescription("Heroes of The Fall character feed for " + c.getName());
        feed.setLanguage("en-gb");
        
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        Date feedpublished = null;
        for (Post p : HotfDispatchServlet.getGetPostsHandler().getByPresence(null, c.getId(), null)) {
            if (feedpublished == null) {
            	feedpublished = p.getCreated();
            }
            Character pc = HotfDispatchServlet.getGetCharacterHandler().getCharacter(p.getCharacterId());
        	SyndEntry entry = new SyndEntryImpl();
        	entry.setAuthor(pc.getName());
            entry.setTitle(pc.getName() + " Posted at Heroes of The Fall");
            String guid = null;
            try {
                guid = URLEncoder.encode(p.getId(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			entry.setLink(req.getRequestURL() + "/" + guid);
			entry.setUri("urn:uuid:" + guid);
            entry.setPublishedDate(p.getCreated());
            entry.setUpdatedDate(p.getCreated());

            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(p.getText());
            entry.setDescription(description);
            entries.add(entry);
        	
        }
        if (feedpublished != null) {
        	feed.setPublishedDate(feedpublished);
        }
        feed.setEntries(entries);
 
        return feed;
    
    }

}
