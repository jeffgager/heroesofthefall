/**
 * 
 */
package com.hotf.server;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.hotf.server.model.Account;
import com.hotf.server.model.Character;
import com.hotf.server.model.Game;
import com.hotf.server.model.Place;
import com.hotf.server.model.Post;

/**
 * @author Jeff
 *
 */
@SuppressWarnings("serial")
public class EmailService implements Serializable {

	private static final Logger log = Logger.getLogger(EmailService.class.getName());
	private static EmailService emailService;

	private EmailService() {
	}

	public static EmailService get() {
		if (emailService == null) {
			emailService = new EmailService();
		}
		return emailService;
	}

	public void sendCharacterAssignedNotification(final Account a, final Character character) {

        log.info("Sending character assigned notification email to " + a.getEmail());
        
        StringBuffer msgbody = new StringBuffer();
        msgbody.append("The character ");
        msgbody.append(SimpleHtmlSanitizer.sanitizeHtml(character.getName()).asString());
        msgbody.append(" has been assigned to your account.");
        msgbody.append("<br>");
        msgbody.append("<br>");

        sendEmail(a, "Character Assigned at Heroes of The Fall", msgbody.toString());

	}

	public void sendCharacterRemovedNotification(final Account a, final Character character) {

        log.info("Sending character removed notification email to " + a.getEmail());
        
        StringBuffer msgbody = new StringBuffer();
        msgbody.append("The character ");
        msgbody.append(SimpleHtmlSanitizer.sanitizeHtml(character.getName()).asString());
        msgbody.append(" has been removed from your account.");
        msgbody.append("<br>");
        msgbody.append("<br>");

        sendEmail(a, "Character Removed at Heroes of The Fall", msgbody.toString());

	}

	public void sendPostNotification(final Account a, final Game game, final Place place, final Character character, final Post post) {

        log.info("Sending post notification email to " + a.getEmail());
        
        StringBuffer msgbody = new StringBuffer();
        msgbody.append(SimpleHtmlSanitizer.sanitizeHtml(character.getName()).asString());
        msgbody.append(" posted in ");
        msgbody.append(SimpleHtmlSanitizer.sanitizeHtml(place.getName()).asString());
        msgbody.append("<br>");
        msgbody.append("<br>");
        msgbody.append(" at ");
        msgbody.append(SimpleHtmlSanitizer.sanitizeHtml(game.getTitle()).asString());
        msgbody.append("<br>");
        msgbody.append("<br>");
        msgbody.append(post.getText());

        String title = null;
        if (place.getType() == null) {
        	title = "Heroes of The Fall - New private post in " + place.getName() + " at " + game.getTitle();
        } else {
        	title = "Heroes of The Fall - New public post in " + place.getName();
        }
        sendEmail(a, title, msgbody.toString());

	}

	public void sendEmail(final Account a, final String subject, final String body) {

		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {

        	Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("hotfrpg@gmail.com", "Heroes of The Fall"));
            String email = a.getEmail().replace("\"","").replace("'", "").trim();
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(email, a.getName()));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            log.info("Sent notification email to " + email);

        } catch (UnsupportedEncodingException e) {
        	log.log(Level.SEVERE, "MessagingException: " + e.getMessage(), e);
        	e.printStackTrace();
        	throw new RuntimeException(e);
        } catch (AddressException e) {
        	log.log(Level.SEVERE, "MessagingException: " + e.getMessage(), e);
        	e.printStackTrace();
        	throw new RuntimeException(e);
        } catch (MessagingException e) {
        	log.log(Level.SEVERE, "MessagingException: " + e.getMessage(), e);
        	e.printStackTrace();
        	throw new RuntimeException(e);
        } catch (Exception e) {
        	log.severe(e.getMessage());
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }

	}

}
