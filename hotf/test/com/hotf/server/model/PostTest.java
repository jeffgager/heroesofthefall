/**
 * 
 */
package com.hotf.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.jdo.PersistenceManager;

import org.junit.Test;

import com.hotf.client.model.exceptions.AccessRightException;
import com.hotf.server.PMUtils;

/**
 * @author Jeff
 *
 */
public class PostTest extends LocalDataStoreTestHelper {

	@Test
	public void testPostAll() {

		login();
		
		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();l1.persist();
		Location l2 = new Location(g.getId(), "l2");l2.persist();l2.persist();
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character c2 = new Character(l1.getId(), "c2");c2.persist();
		Character c3 = new Character(l2.getId(), "c3");c3.persist();
		new Post(c1.getId(), "c1 Post 1").persist();
		new Post(c1.getId(), "c1 Post 2").persist();
		new Post(c2.getId(), "c2 Post 1").persist();
		new Post(c2.getId(), "c2 Post 2").persist();
		new Post(c3.getId(), "c3 Post 1").persist();
		new Post(c3.getId(), "c3 Post 2").persist();
	
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());

		c2.assignToLocation(l2.getId());
		
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());

		new Post(c1.getId(), "c1 Post 3").persist();
		new Post(c2.getId(), "c2 Post 3").persist();
		new Post(c3.getId(), "c3 Post 3").persist();

		assertEquals(5, Post.findByPresence(c1.getId(), null).size());
		assertEquals(6, Post.findByPresence(c2.getId(), null).size());
		assertEquals(4, Post.findByPresence(c3.getId(), null).size());
		assertEquals(5, Post.findByPresence(c1.getId(), null).size());
		assertEquals(6, Post.findByPresence(c2.getId(), null).size());
		assertEquals(4, Post.findByPresence(c3.getId(), null).size());

		c2.assignToLocation(l1.getId());

		Post p1 = new Post(c1.getId(), "c1 Post 4");p1.persist();
		Post p2 = new Post(c2.getId(), "c2 Post 4");p2.persist();
		Post p3 = new Post(c3.getId(), "c3 Post 4");p3.persist();

		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		Account account = Account.findMyAccount();
		account.setFetchRows(10);
		pm.currentTransaction().commit();

		assertEquals(7, Post.findByPresence(c1.getId(), null).size());
		assertEquals(8, Post.findByPresence(c2.getId(), null).size());
		assertEquals(5, Post.findByPresence(c3.getId(), null).size());

		pm.currentTransaction().begin();
		account.setFetchRows(40);
		pm.currentTransaction().commit();

		assertEquals(7, Post.findByPresence(c1.getId(), null).size());
		assertEquals(8, Post.findByPresence(c2.getId(), null).size());
		assertEquals(5, Post.findByPresence(c3.getId(), null).size());

		assertEquals(p2, Post.findByPresence(c1.getId(), null).get(0));
		assertEquals(p2, Post.findByPresence(c2.getId(), null).get(0));
		assertEquals(p3, Post.findByPresence(c3.getId(), null).get(0));

	}

	@Test
	public void testPostFetch() {

		login();
		
		PersistenceManager pm = PMUtils.getPersistenceManager();
		pm.currentTransaction().begin();
		Account account = Account.findMyAccount();
		account.setFetchRows(10);
		pm.currentTransaction().commit();

		Game g = new Game("g1");g.persist();
		Location l1 = new Location(g.getId(), "l1");l1.persist();l1.persist();
		Location l2 = new Location(g.getId(), "l2");l2.persist();l2.persist();
		Character c1 = new Character(l1.getId(), "c1");c1.persist();
		Character c2 = new Character(l1.getId(), "c2");c2.persist();
		Character c3 = new Character(l2.getId(), "c3");c3.persist();
		
		Post p1 = new Post(c1.getId(), "c1 Post 1");p1.persist();
		Post p2 = new Post(c1.getId(), "c1 Post 2");p2.persist();
		Post p3 = new Post(c2.getId(), "c2 Post 1");p3.persist();
		Post p4 = new Post(c2.getId(), "c2 Post 2");p4.persist();
		Post p5 = new Post(c3.getId(), "c3 Post 1");p5.persist();
		Post p6 = new Post(c3.getId(), "c3 Post 2");p6.persist();
	
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());

		assertEquals(2, Post.findByPresence(c1.getId(), p2.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c2.getId(), p2.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c3.getId(), p6.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c1.getId(), p4.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c2.getId(), p4.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c1.getId(), p6.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c2.getId(), p6.getCreatedOrder()).size());

		c2.assignToLocation(l2.getId());
		
		assertEquals(4, Post.findByPresence(c1.getId(), null).size());
		assertEquals(4, Post.findByPresence(c2.getId(), null).size());
		assertEquals(2, Post.findByPresence(c3.getId(), null).size());

		assertEquals(2, Post.findByPresence(c1.getId(), p2.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c2.getId(), p2.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c3.getId(), p6.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c1.getId(), p4.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c2.getId(), p4.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c1.getId(), p6.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c2.getId(), p6.getCreatedOrder()).size());

		Post p7 = new Post(c1.getId(), "c1 Post 3");p7.persist();
		Post p8 = new Post(c2.getId(), "c2 Post 3");p8.persist();
		Post p9 = new Post(c3.getId(), "c3 Post 3");p9.persist();

		assertEquals(5, Post.findByPresence(c1.getId(), null).size());
		assertEquals(6, Post.findByPresence(c2.getId(), null).size());
		assertEquals(4, Post.findByPresence(c3.getId(), null).size());

		assertEquals(3, Post.findByPresence(c1.getId(), p2.getCreatedOrder()).size());
		assertEquals(4, Post.findByPresence(c2.getId(), p2.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c3.getId(), p6.getCreatedOrder()).size());

		assertEquals(1, Post.findByPresence(c1.getId(), p4.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c2.getId(), p4.getCreatedOrder()).size());
		assertEquals(4, Post.findByPresence(c3.getId(), p4.getCreatedOrder()).size());

		assertEquals(1, Post.findByPresence(c1.getId(), p6.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c2.getId(), p6.getCreatedOrder()).size());
		assertEquals(2, Post.findByPresence(c3.getId(), p6.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c1.getId(), p7.getCreatedOrder()).size());
		assertEquals(1, Post.findByPresence(c2.getId(), p8.getCreatedOrder()).size());
		assertEquals(1, Post.findByPresence(c3.getId(), p8.getCreatedOrder()).size());

		assertEquals(0, Post.findByPresence(c2.getId(), p9.getCreatedOrder()).size());
		assertEquals(0, Post.findByPresence(c3.getId(), p9.getCreatedOrder()).size());
		
	}

	@Test
	public void testGMUpdate() {
		
		login2();

		Account a2 = Account.findMyAccount();
		
		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();
		c1.setPlayerAccountId(a2.getId());
		c1.persist();
		Post p1 = new Post(c1.getId(), "c1 post 1");p1.persist();

		p1.setText("c1 post 1 update 1");
		p1.persist();

		Post p2 = Post.findByPresence(c1.getId(), null).get(0);

		assertEquals("c1 post 1 update 1", p2.getText());

	}

	@Test
	public void testPlayerUpdate() {

		login2();

		Account a2 = Account.findMyAccount();
		
		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();
		c1.setPlayerAccountId(a2.getId());
		c1.persist();
		Post p1 = new Post(c1.getId(), "c1 post 1");p1.persist();

		login2();
		p1.setText("c1 post 1 update 1");
		p1.persist();

		Post p2 = Post.findByPresence(c1.getId(), null).get(0);

		assertEquals("c1 post 1 update 1", p2.getText());

	}

	@Test
	public void testUnauthorisedUpdate() {

		login();

		Game g = new Game("g1");g.persist();
		Location l = new Location(g.getId(), "l1");l.persist();
		Character c1 = new Character(l.getId(), "c1");c1.persist();
		Post p1 = new Post(c1.getId(), "c1 post 1");p1.persist();

		login2();
		boolean accessexp = false;
		try {
			p1.setMarkerX(22);
			p1.persist();
		} catch (AccessRightException e) {
			accessexp = true;
		}

		assertTrue(accessexp);

	}

}
