/**
 * 
 */
package com.hotf.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Jeff
 * 
 */
public class DataServiceFilter implements Filter {

	private static PersistenceManagerFactory factory = null;
	private static Logger log = Logger.getLogger(DataServiceFilter.class.getName());

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		PersistenceManager pm = null;
		try {
			pm = factory.getPersistenceManager();
			PMUtils.setPersistenceManager(pm);
			log.info("Created PersistenceManager");
			chain.doFilter(request, response);
		} catch (Throwable t) {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			log.log(Level.SEVERE, t.getMessage(), t);
		} finally {
			try {
				if (pm != null) {
					pm.close();
					log.info("Closed PersistenceManager");
				}
				PMUtils.remove();
				log.info("Removed PersistenceManager");
			} catch (Throwable t) {
			}
		}
	}

	public void init(FilterConfig config) {
		destroy();
		factory = PMF.get();
		log.info("Created PersistenceManagerFactory");
	}

	public void destroy() {
		if (factory != null) {
			log.info("Closed PersistenceManagerFactory");
			factory.close();
		}
	}
}