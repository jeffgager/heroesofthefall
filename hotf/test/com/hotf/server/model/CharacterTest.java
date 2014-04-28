/**
 * 
 */
package com.hotf.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hotf.client.model.exceptions.AccessRightException;

/**
 * @author Jeff
 * 
 */
public class CharacterTest extends LocalDataStoreTestHelper {

	@Test
	public void testPersist() {

		login();

		Game g1 = new Game("g1");g1.persist();
		Game g2 = new Game("g2");g2.persist();
		Location l1 = new Location(g1.getId(), "l1");l1.persist();l1.persist();
		Location l2 = new Location(g1.getId(), "l2");l1.persist();l2.persist();
		Location l3 = new Location(g2.getId(), "l3");l1.persist();l3.persist();

		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character c11 = new Character(l1.getId(), "c11");c11.persist();
		Character c2 = new Character(l2.getId(), "c2");c2.persist();
		Character c22 = new Character(l2.getId(), "c22");c22.persist();
		Character c3 = new Character(l3.getId(), "c3");c3.persist();
		Character c33 = new Character(l3.getId(), "c33");c33.persist();

		assertTrue(Character.findByLocation(l1.getId()).get(0).equals(c1));
		assertTrue(Character.findByLocation(l1.getId()).get(1).equals(c11));
		assertEquals(l1.getId(), c1.getLocationId());
		assertEquals(l1.getId(), c11.getLocationId());
		assertEquals(g1.getId(), l1.getGameId());
		assertEquals(2, Character.findByLocation(l1.getId()).size());

		assertTrue(Character.findByLocation(l2.getId()).get(0).equals(c2));
		assertTrue(Character.findByLocation(l2.getId()).get(1).equals(c22));
		assertEquals(l2.getId(), c2.getLocationId());
		assertEquals(l2.getId(), c22.getLocationId());
		assertEquals(g1.getId(), l2.getGameId());
		assertEquals(2, Character.findByLocation(l2.getId()).size());

		assertTrue(Character.findByLocation(l3.getId()).get(0).equals(c3));
		assertTrue(Character.findByLocation(l3.getId()).get(1).equals(c33));
		assertEquals(l3.getId(), c3.getLocationId());
		assertEquals(l3.getId(), c33.getLocationId());
		assertEquals(g2.getId(), l3.getGameId());
		assertEquals(2, Character.findByLocation(l3.getId()).size());

		Account account = Account.findMyAccount();
		ArrayList<Character> l = new ArrayList<Character>();
		l.add(c1);l.add(c11);
		assertEquals(l, Character.findByLocation(l1.getId()));
		l.clear();l.add(c2);l.add(c22);
		assertEquals(l, Character.findByLocation(l2.getId()));
		l.clear();l.add(c3);l.add(c33);
		assertEquals(l, Character.findByLocation(l3.getId()));

		assertEquals(account.getId(), c1.getPlayerAccountId());
		assertEquals(account.getId(), c2.getPlayerAccountId());
		assertEquals(account.getId(), c3.getPlayerAccountId());
		assertEquals(account.getId(), c11.getPlayerAccountId());
		assertEquals(account.getId(), c22.getPlayerAccountId());
		assertEquals(account.getId(), c33.getPlayerAccountId());

	}

	@Test
	public void testFindCharactersAtLocation() {
		login();
		Game g1 = new Game("g1");g1.persist();
		Location l1 = new Location(g1.getId(), "l1");l1.persist();
		Game g2 = new Game("g2");g2.persist();
		Location l2 = new Location(g2.getId(), "l2");l2.persist();
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character c2 = new Character(l1.getId(), "c2");c2.persist();
		Character c3 = new Character(l1.getId(), "c3");c3.persist();
		new Character(l2.getId(), "c4").persist();
		Character c5 = new Character(l1.getId(), "c5");c5.persist();
		new Character(l2.getId(), "c6").persist();

		ArrayList<Character> eg = new ArrayList<Character>();
		eg.add(c1);
		eg.add(c2);
		eg.add(c3);
		eg.add(c5);
		List<Character> characters = Character.findByLocation(l1.getId());
		assertEquals(eg, characters);

	}

	@Test
	public void testGMUpdate() {
		
		login2();

		Account a2 = Account.findMyAccount();
		
		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();

		c1.setName("name");
		c1.setDescription("description");
		c1.setPlayerAccountId(a2.getId());
		c1.persist();

		Character c2 = Character.findByLocation(l.getId()).get(0);

		assertEquals("name", c2.getName());
		assertEquals("description", c2.getDescription());

		c2.setName("namex");
		c2.setDescription("descriptionx");
		c2.persist();

		Character c3 = Character.findByLocation(l.getId()).get(0);

		assertEquals("namex", c3.getName());
		assertEquals("descriptionx", c3.getDescription());

	}

	@Test
	public void testPlayerUpdate() {

		login2();

		Account a2 = Account.findMyAccount();

		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();

		c1.setName("name");
		c1.setDescription("description");
		c1.setPlayerAccountId(a2.getId());
		c1.persist();

		Character c2 = Character.findByLocation(l.getId()).get(0);

		assertEquals("name", c2.getName());
		assertEquals("description", c2.getDescription());

		login2();

		c2.setName("namex");
		c2.setDescription("descriptionx");
		c2.persist();

		Character c3 = Character.findByLocation(l.getId()).get(0);

		assertEquals("namex", c3.getName());
		assertEquals("descriptionx", c3.getDescription());

	}

	@Test
	public void testUnauthorisedUpdate() {

		login2();

		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();

		c1.setName("name");
		c1.setDescription("description");
		c1.persist();

		Character c2 = Character.findByLocation(l.getId()).get(0);

		assertEquals("name", c2.getName());
		assertEquals("description", c2.getDescription());

		login2();

		boolean accessexp = false;
		try {
			c2.setName("namex");
			c2.setDescription("descriptionx");
			c2.persist();
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

	@Test
	public void testFindInterestedAll() {

		login();
		Account a1 = Account.findMyAccount();
		a1.setName("XXXX");Account.persist(a1);
		Account.createStronghold(a1, "XXXX");
		Game g1 = new Game("g1");g1.persist();
		Location l1 = (Location) Location.findByGameAndName(g1.getId()).get(0);
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character gm1 = Character.findCharacter(g1.getGameMasterCharacterId());
		Character pc = Character.findCharacter(a1.getPlayingCharacterId());

		assertEquals(gm1.getId(), g1.getGameMasterCharacterId());
		assertEquals(g1.getId(), l1.getGameId());
		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l1.getId(), c1.getLocationId());
		
		login2();
		Account a2 = Account.findMyAccount();
		a2.setName("YYYYY");Account.persist(a2);
		Account.createStronghold(a2, "XXXX");
		Game g2 = new Game("g2");g2.persist();
		Location l2 = (Location) Location.findByGameAndName(g2.getId()).get(0);		
		Character c2 = new Character(l2.getId(), "c2");c2.persist();
		Character gm2 = Character.findCharacter(g2.getGameMasterCharacterId());
		Character pc2 = Character.findCharacter(a2.getPlayingCharacterId());

		assertEquals(gm2.getId(), g2.getGameMasterCharacterId());
		assertEquals(g2.getId(), l2.getGameId());
		assertEquals(a2.getId(), c2.getPlayerAccountId());
		assertEquals(l2.getId(), c2.getLocationId());

		c1.assignToLocation(l2.getId());

		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l2.getId(), c1.getLocationId());

		login();

		List<Character> eg = new ArrayList<Character>();
		eg.add(c1);eg.add(pc);eg.add(gm1);
		assertEquals(eg, Character.findInterestedByName());

		login2();
		
		eg.clear();eg.add(c1);eg.add(c2);eg.add(pc2);eg.add(gm2);
		assertEquals(eg, Character.findInterestedByName());

	}

	@Test
	public void testFindInterestedSetRange() {

		login();
		Account a1 = Account.findMyAccount();
		Game g1 = new Game("g1");g1.persist();
		Location l1 = (Location) Location.findByGameAndName(g1.getId()).get(0);
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character gm1 = Character.findCharacter(g1.getGameMasterCharacterId());

		assertEquals(gm1.getId(), g1.getGameMasterCharacterId());
		assertEquals(g1.getId(), l1.getGameId());
		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l1.getId(), c1.getLocationId());
		
		login2();
		Account a2 = Account.findMyAccount();
		Game g2 = new Game("g2");g2.persist();
		Location l2 = (Location) Location.findByGameAndName(g2.getId()).get(0);		
		Character c2 = new Character(l2.getId(), "c2");c2.persist();
		Character gm2 = Character.findCharacter(g2.getGameMasterCharacterId());

		assertEquals(gm2.getId(), g2.getGameMasterCharacterId());
		assertEquals(g2.getId(), l2.getGameId());
		assertEquals(a2.getId(), c2.getPlayerAccountId());
		assertEquals(l2.getId(), c2.getLocationId());

		c1.assignToLocation(l2.getId());

		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l2.getId(), c1.getLocationId());

		login();

		Account account = Account.findMyAccount();
		account.setSearchRows(3);
		Account.persist(account);

		List<Character> eg = new ArrayList<Character>();
		eg.add(c1);eg.add(gm1);
		assertEquals(eg, Character.findInterestedByName());

		login2();

		account = Account.findMyAccount();
		account.setSearchRows(3);
		Account.persist(account);

		eg.clear();eg.add(c1);eg.add(c2);eg.add(gm2);
		assertEquals(eg, Character.findInterestedByName());

	}

	@Test
	public void testFindSetRangeSearch() {
		
		login();
		Account a1 = Account.findMyAccount();
		a1.setName("xxxxx");
		Account.persist(a1);
		Account.createStronghold(a1, "XXXX");
		Game g1 = new Game("g1");g1.persist();
		Location l1 = (Location) Location.findByGameAndName(g1.getId()).get(0);
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character gm1 = Character.findCharacter(g1.getGameMasterCharacterId());

		assertEquals(gm1.getId(), g1.getGameMasterCharacterId());
		assertEquals(g1.getId(), l1.getGameId());
		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l1.getId(), c1.getLocationId());
		
		login2();
		Account a2 = Account.findMyAccount();
		Game g2 = new Game("g2");g2.persist();
		Location l2 = (Location) Location.findByGameAndName(g2.getId()).get(0);		
		Character c2 = new Character(l2.getId(), "c2");c2.persist();
		Character gm2 = Character.findCharacter(g2.getGameMasterCharacterId());

		assertEquals(gm2.getId(), g2.getGameMasterCharacterId());
		assertEquals(g2.getId(), l2.getGameId());
		assertEquals(a2.getId(), c2.getPlayerAccountId());
		assertEquals(l2.getId(), c2.getLocationId());

		c1.assignToLocation(l2.getId());

		assertEquals(a1.getId(), c1.getPlayerAccountId());
		assertEquals(l2.getId(), c1.getLocationId());

		login();

		Account account = Account.findMyAccount();
		account.setSearchRows(3);
		Account.persist(account);

	}

	@Test 
	public void testCharacterType() {
		
		login2();
		Account a2 = Account.findMyAccount();
		
		login();
		
		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		
		//GM
		Character c1 = Character.findCharacter(g.getGameMasterCharacterId());

		//Player
		Character c2 = new Character(l.getId(), "c3");c2.persist();
		c2.setPlayerAccountId(a2.getId());
		c2.persist();

		//NPC
		Character c3 = new Character(l.getId(), "c3");c3.persist();
		
		assertEquals("GM", c1.getCharacterType());
		assertEquals("Player", c2.getCharacterType());
		assertEquals("NPC", c3.getCharacterType());
		
	}

}
