'''
Created on 11 May 2012

@author: Jeff
'''
import os
import unittest

from google.appengine.ext import testbed
from actions import register_new_user, create_place, create_role,\
    create_presence, create_narrative, set_playing, delete_presence,\
    get_first_narrative, get_more_narrative, save_narrative, delete_narrative

def setCurrentUser(email, user_id, is_admin=False):
    os.environ['USER_EMAIL'] = email or ''
    os.environ['USER_ID'] = user_id or ''
    os.environ['USER_IS_ADMIN'] = '1' if is_admin else '0'

def logoutCurrentUser():
    setCurrentUser(None, None)
    
class Test(unittest.TestCase):

    def setUp(self):
        self.testbed = testbed.Testbed()
        self.testbed.activate()
        self.testbed.init_user_stub()
        self.testbed.init_datastore_v3_stub()
        self.testbed.init_memcache_stub()
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('SiteAdmin')

    def tearDown(self):
        self.testbed.deactivate()

    def testCreateNarrative(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        user1 = register_new_user('User1')
        
        place1 = create_place("Place 1")
        role1 = create_role("Role 1")
        place2 = create_place("Place 2")
        role2 = create_role("Role 2")
    
        create_presence(role1, place1)
        set_playing(role1)
        create_narrative(place1, "Narrative 1 from Role 1 at Place 1")
        create_presence(role2, place2)
        set_playing(role2)
        create_narrative(place2, "Narrative 1 from Role 2 at Place 2")
    
        create_presence(role2, place1)
        set_playing(role1)
        create_narrative(place1, "Narrative 2 from Role 1 at Place 1")
        create_presence(role1, place2)
        set_playing(role2)
        create_narrative(place2, "Narrative 2 from Role 2 at Place 2")
    
        delete_presence(role2, place1)
        set_playing(role1)
        create_narrative(place1, "Narrative 3 from Role 1 at Place 1")
        delete_presence(role1, place2)
        set_playing(role2)
        create_narrative(place2, "Narrative 3 from Role 2 at Place 2")
    
        set_playing(role1)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 4)

        set_playing(role2)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 4)

        set_playing(user1)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 6)

        setCurrentUser("admin@gmail.com", "Administrator")
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 0)

        setCurrentUser("user1@gmail.com", "User1")
        create_presence(role1, place2)
        create_presence(role2, place1)

        set_playing(role1)
        create_narrative(place1, "Narrative 4 from Role 1 at Place 1")
        create_narrative(place1, "Narrative 5 from Role 1 at Place 1")
        create_narrative(place1, "Narrative 6 from Role 1 at Place 1")
        create_narrative(place1, "Narrative 7 from Role 1 at Place 1")
        create_narrative(place1, "Narrative 8 from Role 1 at Place 1")
        create_narrative(place1, "Narrative 9 from Role 1 at Place 1")

        set_playing(role2)
        create_narrative(place2, "Narrative 4 from Role 2 at Place 2")
        create_narrative(place2, "Narrative 5 from Role 2 at Place 2")
        create_narrative(place2, "Narrative 6 from Role 2 at Place 2")
        create_narrative(place2, "Narrative 7 from Role 2 at Place 2")
        create_narrative(place2, "Narrative 8 from Role 2 at Place 2")
        create_narrative(place2, "Narrative 9 from Role 2 at Place 2")

        set_playing(role1)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], True)
        self.assertEqual(len(rset["narrative"]), 10)
        rset = get_more_narrative(rset["next"])
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 6)

        set_playing(role2)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], True)
        self.assertEqual(len(rset["narrative"]), 10)
        rset = get_more_narrative(rset["next"])
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 6)

        set_playing(user1)
        rset = get_first_narrative()
        self.assertEqual(rset["more"], True)
        self.assertEqual(len(rset["narrative"]), 10)
        rset = get_more_narrative(rset["next"])
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 8)

        setCurrentUser("admin@gmail.com", "Administrator")
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 0)

    def testChangeNarrativeBody(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        
        place1 = create_place("Place 1")
        role1 = create_role("Role 1")

        create_presence(role1, place1)
        set_playing(role1)
        create_narrative(place1, "Narrative 1 from Role 1 at Place 1")
    
        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 1)
        narrative = rset["narrative"][0]
        self.assertEqual(narrative["role"], "Role 1")
        self.assertEqual(narrative["place"], "Place 1")
        self.assertEqual(narrative["body"], "Narrative 1 from Role 1 at Place 1")
        self.assertIsNotNone(narrative["created"])
        self.assertIsNone(narrative["updated"])
        
        save_narrative(narrative["id"], "Narrative 1 from Role 1 at Place 1 - updated")

        rset = get_first_narrative()
        self.assertEqual(rset["more"], False)
        self.assertEqual(len(rset["narrative"]), 1)
        narrative = rset["narrative"][0]
        self.assertEqual(narrative["role"], "Role 1")
        self.assertEqual(narrative["place"], "Place 1")
        self.assertEqual(narrative["body"], "Narrative 1 from Role 1 at Place 1 - updated")
        self.assertIsNotNone(narrative["created"])
        self.assertIsNotNone(narrative["updated"])

    def testChangeNarrativePlace(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        
        place1 = create_place("Place 1")
        place2 = create_place("Place 2")
        role1 = create_role("Role 1")
        role2 = create_role("Role 2")

        create_presence(role1, place1)
        create_presence(role2, place2)
        set_playing(role1)
        create_narrative(place1, "Narrative 1 from Role 1 at Place 1")
    
        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 1)
        narrative = rset["narrative"][0]
        
        set_playing(role2)
        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 0)

        set_playing(role1)
        create_presence(role1, place2)
        save_narrative(narrative["id"], place_id=place2)

        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 1)
        
        set_playing(role2)
        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 1)

    def testDeleteNarrative(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        
        place1 = create_place("Place 1")
        role1 = create_role("Role 1")

        create_presence(role1, place1)
        set_playing(role1)
        create_narrative(place1, "Narrative 1 from Role 1 at Place 1")
    
        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 1)
        narrative = rset["narrative"][0]

        delete_narrative(narrative["id"])

        rset = get_first_narrative()
        self.assertEqual(len(rset["narrative"]), 0)

if __name__ == "__main__":
    unittest.main()

