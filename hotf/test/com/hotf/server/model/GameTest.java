/**
 * 
 */
package com.hotf.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.hotf.client.model.exceptions.AccessRightException;

/**
 * @author Jeff
 *
 */
public class GameTest extends LocalDataStoreTestHelper {

	@Test
	public void testCreate() {

		login();
		Account a1 = Account.findMyAccount();
		a1.setName("XXXX");
		Account.persist(a1);
		Account.createStronghold(a1, "XXXX");
		Character gmsh = Character.findInterestedByName().get(0);
		Game sh = Game.findGame(Location.findLocation(gmsh.getLocationId()).getGameId());
		assertNotNull(sh);
		
		Game g1 = new Game("g1");g1.persist();
		Character gm1 = Character.findCharacter(g1.getGameMasterCharacterId());
		assertEquals(gm1.getId(), g1.getGameMasterCharacterId());
		assertEquals(2, Character.findInterestedByName().size());
		
		Game g2 = new Game("g2");g2.persist();
		Character gm2 = Character.findCharacter(g2.getGameMasterCharacterId());
		assertEquals(gm2.getId(), g2.getGameMasterCharacterId());
		assertEquals(3, Character.findInterestedByName().size());

	}

	@Test
	public void testUpdate() {
		
		login();

		Game g1 = new Game("g1");g1.persist();

		g1.setTitle("Zaphod");
		g1.setDescription("Zaphod");
		g1.persist();

		Game g2 = Game.findGame(g1.getId());
		assertEquals(g1.getTitle(), g2.getTitle());
		assertEquals(g1.getDescription(), g2.getDescription());

	}

	@Test
	public void testUnauthorizedUpdate() {
		
		login();

		Game g1 = new Game("g1");g1.persist();

		g1.setTitle("Zaphod");
		g1.setDescription("Zaphod");
		g1.persist();
		
		login2();

		boolean accessexp = false;
		try {
			g1.setTitle("Zaphod");
			g1.setDescription("Zaphod");
			g1.persist();
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);
	
	}

	@Test
	public void testUpdateInvalidTitle() {
		
		login();

		Game g1 = new Game("g1");g1.persist();

		boolean stateexp = false;
		try {
			g1.setTitle("Z");
			g1.persist();
		} catch (IllegalStateException e) {
			stateexp = true;
		}

		assertTrue(stateexp);

	}

}
