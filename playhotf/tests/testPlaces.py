'''
Created on 11 May 2012

@author: Jeff
'''
import os
import unittest

from google.appengine.ext import testbed

from actions import register_new_user, create_place, save_place, get_my_places, \
    get_place_properties, ActionException, delete_place, create_role,\
    create_presence, delete_presence
from model import DataModelException


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

    def testAdminCreatePlace(self):
        create_place("Test Place 1")
        self.assertEquals(len(get_my_places()), 1)

    def testNonAdminCreatePlace(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        self.assertIsInstance(create_place("Test Place 2"), str)

    def testAdminAndNonAdminCreatePlaces(self):
        self.assertIsInstance(create_place("Test Place 1"), str)
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        self.assertIsInstance(create_place("Test Place 2"), str)
        
    def testBlankPlaceName(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1url = create_place("Test Place 1")
        self.assertRaises(ActionException, create_place, None)
        self.assertRaises(DataModelException, create_place, "")
        self.assertRaises(DataModelException, save_place, place1url, name="")
        
    def testSavePlaceName(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1url = create_place("Test Place 1")
        save_place(place1url, name="This is a new place name")
        self.assertEqual(get_place_properties(place1url)["name"], "This is a new place name")

    def testGetMyPlaces(self):
        setupLotsOfPlaces()
        setCurrentUser("user1@gmail.com", "User1")
        places_list = get_my_places()
        self.assertEqual(len(places_list), 14)
        setCurrentUser("user2@gmail.com", "User2")
        places_list = get_my_places()
        self.assertEqual(len(places_list), 2)

    def testSaveAnotherUsersPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1url = create_place("Test Place 1")
        save_place(place1url, name="This is a new place name")
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        self.assertRaises(DataModelException, save_place, place1url, "Should Fail")
        register_new_user('User2')
        self.assertRaises(DataModelException, save_place, place1url, "Should Fail")

    def testDeletePlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        self.assertEquals(len(get_my_places()), 0)
        place1url = create_place("Test Place 1")
        self.assertEquals(len(get_my_places()), 1)
        delete_place(place1url)
        self.assertEquals(len(get_my_places()), 0)
        
    def testDeleteAnotherUsersPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1url = create_place("Test Place 1")
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        self.assertEquals(len(get_my_places()), 0)
        self.assertRaises(DataModelException, delete_place, place1url)

    def testWritableProperty(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1url = create_place("Test Place 1")
        self.assertTrue(get_place_properties(place1url)["writable"])
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        self.assertFalse(get_place_properties(place1url)["writable"])

    def testCreatePresenceInSomeoneElsesPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        role1 = create_role("Test Role 1")
        self.assertRaises(DataModelException, create_presence, role1, place1)

    def testDeletePresenceFromSomeoneElsesPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        role1 = create_role("Test Role 1")
        pres1 = create_presence(role1, place1)
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        self.assertRaises(ActionException, delete_presence, role1, place1)

def setupLotsOfPlaces():
    setCurrentUser("user1@gmail.com", "User1")
    register_new_user('User1')
    create_place("Test Place 1")
    create_place("Test Place 2")
    create_place("Test Place 3")
    create_place("Test Place 4")
    create_place("Test Place 5")
    create_place("Test Place 6")
    create_place("Test Place 7")
    create_place("Test Place 8")
    create_place("Test Place 9")
    create_place("Test Place 10")
    create_place("Test Place 11")
    create_place("Test Place 12")
    create_place("Test Place 13")
    create_place("Test Place 14")
    setCurrentUser("user2@gmail.com", "User2")
    register_new_user('User2')
    create_place("Test Place 15")
    create_place("Test Place 16")

if __name__ == "__main__":
    unittest.main()

