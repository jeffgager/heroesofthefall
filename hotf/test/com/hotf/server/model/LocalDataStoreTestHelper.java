/**
 * 
 */
package com.hotf.server.model;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.hotf.server.PMUtils;

/**
 * @author Jeff
 * 
 */
public abstract class LocalDataStoreTestHelper {

	private LocalDatastoreServiceTestConfig dsConfig = new LocalDatastoreServiceTestConfig();
	private LocalServiceTestHelper helper;
	
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private String email = "test@email.com";
	private String domain = "roleplayheroes";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        dsConfig.setNoStorage(false);
        dsConfig.setNoIndexAutoGen(false);
        helper = new LocalServiceTestHelper(dsConfig);
		helper.setUp();
		dsConfig.setUp();
		PMUtils.setPersistenceManager(pmfInstance.getPersistenceManager());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		PMUtils.getPersistenceManager().close();
		dsConfig.tearDown();
		helper.tearDown();
	}

	public void login() {
		getHelper().setEnvEmail(email);
		getHelper().setEnvIsLoggedIn(true);
		getHelper().setEnvAuthDomain(domain);
		Account.findMyAccount();
	}

	public void login2() {
		getHelper().setEnvEmail(email + "2");
		getHelper().setEnvIsLoggedIn(true);
		getHelper().setEnvAuthDomain(domain);
		Account.findMyAccount();
	}

	public void loginAdmin() {
		getHelper().setEnvEmail(email + "2");
		getHelper().setEnvIsLoggedIn(true);
		getHelper().setEnvAuthDomain(domain);
		getHelper().setEnvIsAdmin(true);
		Account.findMyAccount();
	}

	public LocalServiceTestHelper getHelper() {
		return helper;
	}

	public String getEmail() {
		return email;
	}

	public String getDomain() {
		return domain;
	}

}
