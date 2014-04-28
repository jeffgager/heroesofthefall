/**
 * 
 */
package com.hotf.server.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.Test;

import com.hotf.client.model.exceptions.AccessRightException;
import com.hotf.server.PMUtils;


/**
 * @author Jeff
 *
 */
public class LocationTest extends LocalDataStoreTestHelper {

	@Test
	public void testCreate() {

		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();

		assertEquals(l1, Location.findByGameAndName(g.getId()).get(0));

	}

	@Test
	public void testFindAll() {

		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();

		assertEquals(l1, Location.findByGameAndName(g.getId()).get(0));

		Location l2 = new Location(g.getId(), "l2");l2.persist();
		assertEquals(l1, Location.findByGameAndName(g.getId()).get(0));
		assertEquals(l2, Location.findByGameAndName(g.getId()).get(1));
	
		Game g2 = new Game("g1");g2.persist();
		Location l3 = new Location(g2.getId(), "l3");l3.persist();
		assertEquals(l1, Location.findByGameAndName(g.getId()).get(0));

	}

	@Test
	public void testFindDefaultRange() {

		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();
		Location l2 = new Location(g.getId(), "l2");l2.persist();
		Location l3 = new Location(g.getId(), "l3");l3.persist();
		Location l4 = new Location(g.getId(), "l4");l4.persist();
		Location l5 = new Location(g.getId(), "l5");l5.persist();
		Location l6 = new Location(g.getId(), "l6");l6.persist();

		ArrayList<Location> eg = new ArrayList<Location>();
		eg.add(l1);eg.add(l2);eg.add(l3);eg.add(l4);eg.add(l5);
		
		List<Location> locations = Location.findByGameAndName(g.getId());
		assertEquals(eg, locations);
	
	}

	@Test
	public void testFindSetRange() {

		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();
		Location l2 = new Location(g.getId(), "l2");l2.persist();
		Location l3 = new Location(g.getId(), "l3");l3.persist();
		Location l4 = new Location(g.getId(), "l4");l4.persist();
		Location l5 = new Location(g.getId(), "l5");l5.persist();
		Location l6 = new Location(g.getId(), "l6");l6.persist();

		ArrayList<Location> eg = new ArrayList<Location>();
		eg.add(l1);eg.add(l2);eg.add(l3);eg.add(l4);
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		Account account = Account.findMyAccount();
		account.setSearchRows(4);
		pm.currentTransaction().commit();
	
		List<Location> locations = Location.findByGameAndName(g.getId());
		assertEquals(eg, locations);

	}

	@Test
	public void testFindSetRangeSearch() {
		
		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();
		Location l2 = new Location(g.getId(), "l2");l2.persist();
		Location l3 = new Location(g.getId(), "l3");l3.persist();
		Location l4 = new Location(g.getId(), "l4");l4.persist();
		Location l5 = new Location(g.getId(), "l5");l5.persist();
		Location l6 = new Location(g.getId(), "l6");l6.persist();

		ArrayList<Location> eg = new ArrayList<Location>();
		eg.add(l2);eg.add(l3);eg.add(l4);eg.add(l5);
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		Account account = Account.findMyAccount();
		account.setSearchRows(4);
		pm.currentTransaction().commit();
	
	}

	@Test
	public void testAuthorisedUpdate() {
		
		login();
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();

		l1.setName("game1");
		l1.setDescription("description1");
		l1.persist();
		
		Location l2 = Location.findLocation(l1.getId());
		
		assertEquals(l1.getName(), l2.getName());
		assertEquals(l1.getDescription(), l2.getDescription());

	}

	@Test
	public void testUnAuthorisedUpdate() {
		
		login();
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();

		l1.setName("game1");
		l1.setDescription("description1");
		l1.persist();
		
		Location l2 = Location.findLocation(l1.getId());
		
		assertEquals(l1.getName(), l2.getName());
		assertEquals(l1.getDescription(), l2.getDescription());

		login2();

		boolean accessexp = false;
		try {
			l2.setDescription("description1");
			l2.persist();
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

}
