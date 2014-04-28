/**
 * 
 */
package com.hotf.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.junit.Test;

import com.hotf.client.model.exceptions.AccessRightException;
import com.hotf.client.model.exceptions.NotSignedInException;
import com.hotf.client.model.exceptions.ValidationException;
import com.hotf.server.PMUtils;

/**
 * @author Jeff
 * 
 */
public class AccountTest extends LocalDataStoreTestHelper {

	@Test (expected=NotSignedInException.class)
	public void testLoggedOut() {
		Account.findMyAccount();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAccountCreation() {
		PersistenceManager pm = PMUtils.getPersistenceManager();
		
		Query q = pm.newQuery(Account.class);
		List<Account> accounts = (List<Account>) q.execute();
		assertEquals(0, accounts.size());

		boolean nsie = false;
		try {
			Account.findMyAccount();
		} catch (NotSignedInException e) {
			nsie = true;
		}
		assertTrue(nsie);

		q = pm.newQuery(Account.class);
		accounts = (List<Account>) q.execute();
		assertEquals(0, accounts.size());

		login();
		Account account = Account.findMyAccount();
		account.setName("Account Name");
		Account.persist(account);
		
		Query q2 = pm.newQuery(Account.class);
		accounts = (List<Account>) q2.execute();
		assertEquals(1, accounts.size());

		account = Account.findMyAccount();
		account.setName("Account Name2");
		Account.persist(account);
		
		Query q3 = pm.newQuery(Account.class);
		accounts = (List<Account>) q3.execute();
		assertEquals(1, accounts.size());

	}

	@Test
	public void findByIdTest() {
		login();
		Account a1 = Account.findMyAccount();
		String id = a1.getId();
		Account a2 = Account.findAccount(id);
		assertEquals(a1, a2);
	}

	@Test
	public void testFindAll() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setName("test1");Account.persist(a1);
		
		login2();
		Account a2 = Account.findMyAccount();
		a2.setName("test2");Account.persist(a2);
		
		List<Account> eg = new ArrayList<Account>();

		eg.add(a1);eg.add(a2);
		List<Account> l = Account.findByName(null);
		assertEquals(eg, l);

	}
	
	@Test
	public void testFindSetRange() {

		login();
		Account a1 = Account.findMyAccount();
		a1.setName("test1");
		Account.persist(a1);
		
		login2();
		Account a2 = Account.findMyAccount();
		a2.setName("test2");
		a2.setSearchRows(3);
		Account.persist(a2);
		
		List<Account> eg = new ArrayList<Account>();
		eg.add(a1);
		eg.add(a2);

		List<Account> l = Account.findByName(null);
		assertEquals(eg, l);

	}

	@Test
	public void testFindSetRangeSearch() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setName("test1");Account.persist(a1);
		
		login2();
		Account a2 = Account.findMyAccount();
		a2.setName("test2");
		a2.setSearchRows(3);
		Account.persist(a2);
		
		List<Account> eg = new ArrayList<Account>();

		eg.add(a1);
		List<Account> l = Account.findByName("test");
		assertEquals(eg, l);

	}
	
	@Test
	public void testUpdateAccount() {
		
		login();
		Account a1 = Account.findMyAccount();
		assertEquals(Account.DEFAULT_FETCH_ROWS, a1.getFetchRows());
		assertEquals(Account.DEFAULT_SEARCH_ROWS, a1.getSearchRows());

		a1.setName("Zaphod");
		a1.setFetchRows(25);
		a1.setSearchRows(10);
		Account.persist(a1);

		Account a2 = Account.findMyAccount();
		assertEquals(a1.getName(), a2.getName());
		assertEquals(a1.getFetchRows(), a2.getFetchRows());
		assertEquals(a1.getSearchRows(), a2.getSearchRows());

	}

	@Test 
	public void testUnauthorisedNameChange() {
		
		login();
		Account a1 = Account.findMyAccount();
		boolean accessexp = false;
		a1.setName("test1");Account.persist(a1);
		
		login2();
		Account a2 = Account.findMyAccount();
		try {
			a2.setName("test1");Account.persist(a2);
		} catch (ValidationException e) {
			accessexp = true;
		}
		
		assertTrue(accessexp);

	}
	
	@Test 
	public void testUnauthorizedPlayingCharacterChange() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setPlayingCharacterId("1");Account.persist(a1);
		
		login2();
		boolean accessexp = false;
		try {
			a1.setPlayingCharacterId("2");Account.persist(a1);
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

	@Test
	public void testUnauthorizedSearchSizeChange() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setSearchRows(3);Account.persist(a1);
		
		login2();
		boolean accessexp = false;
		try {
			a1.setSearchRows(4);Account.persist(a1);
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

	@Test
	public void testUnauthorizedFetchSizeChange() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setFetchRows(10);Account.persist(a1);
		
		login2();
		boolean accessexp = false;
		try {
			a1.setFetchRows(20);Account.persist(a1);
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

	@Test
	public void testUnauthorizedShowPortraitsChange() {

		login();
		Account a1 = Account.findMyAccount();
		a1.setShowPortraits(false);Account.persist(a1);
		
		login2();
		boolean accessexp = false;
		try {
			a1.setShowPortraits(false);Account.persist(a1);
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

	@Test
	public void testUpdateInvalidName() {
		
		login();

		Account a = Account.findMyAccount();

		boolean stateexp = false;
		try {
			a.setName("Z");
		} catch (ValidationException e) {
			stateexp = true;
		}

		assertTrue(stateexp);

	}

}
