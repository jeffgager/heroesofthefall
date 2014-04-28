/**
 * 
 */
package com.hotf.server;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

/**
 * @author Jeff
 *
 */
public class PMUtils {

	private static final ThreadLocal<PersistenceManager> PERSISTENCE_MANAGERS = new ThreadLocal<PersistenceManager>();
	private static Logger log = Logger.getLogger(PMUtils.class.getName());

	/** Returns a fresh EntityManager */
	public static PersistenceManager getPersistenceManager() {
		log.info("Using PersistenceManager");
		return PERSISTENCE_MANAGERS.get();
	}

	public static void setPersistenceManager(PersistenceManager pm) {
		PERSISTENCE_MANAGERS.set(pm);
	}
	
	public static void remove() {
		PERSISTENCE_MANAGERS.remove();
	}
	
}
