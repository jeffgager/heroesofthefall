'''
Created on 11 May 2012

@author: Jeff
'''
import os
import unittest

from google.appengine.ext import testbed

from actions import register_new_user, create_place, create_role, get_my_roles, \
    save_role, get_roles_in_place, set_playing, delete_place, ActionException, \
    delete_role, create_presence, delete_presence, is_present, save_presence
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

    def testAdminCreateRole(self):
        self.assertIsInstance(create_role("Test Role 1"), str)

    def testNonAdminCreateRole(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        self.assertIsInstance(create_role("Test Role 1"), str)

    def testGetMyRoles(self):
        setupLotsOfRoles()
        setCurrentUser("user1@gmail.com", "User1")
        roles_list = get_my_roles()
        self.assertEqual(len(roles_list), 15)
        setCurrentUser("user2@gmail.com", "User2")
        roles_list = get_my_roles()
        self.assertEqual(len(roles_list), 3)

    def testRolesInPlace(self):
        place1 = setupLotsOfRoles()
        setCurrentUser("user1@gmail.com", "User1")
        roles_list = get_roles_in_place(place1)
        self.assertEqual(len(roles_list), 2)

    def testSaveRoleName(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        roleid = create_role("Test Role 1")
        save_role(roleid, "New Role name")
        self.assertEqual(get_my_roles()[0]["name"], "New Role name")

    def testBlankRoleName(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        roleid = create_role("Test Role 1")
        self.assertRaises(ActionException, create_role, None)
        self.assertRaises(DataModelException, create_role, "")
        self.assertRaises(DataModelException, save_role, roleid, name="")

    def testSetPlaying(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        role1 = create_role("Test Role 1")
        role2 = create_role("Test Role 2")
        set_playing(role1)
        self.assertEqual(get_my_roles()[0]["name"], "Test Role 1")
        set_playing(role2)
        self.assertEqual(get_my_roles()[1]["name"], "Test Role 2")
        set_playing(None)
        self.assertEqual(get_my_roles()[2]["name"], "User1")

    def testDeleteRole(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        self.assertEqual(len(get_roles_in_place(place1)), 0)
        role1 = create_role("Test Role 1")
        create_presence(role1, place1)
        self.assertEqual(len(get_roles_in_place(place1)), 1)
        delete_presence(role1, place1)
        delete_role(role1)
        self.assertEqual(len(get_roles_in_place(place1)), 0)

    def testSaveAnotherUsersRole(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        roleid = create_role("Test Role 1")
        save_role(roleid, "New Role name")
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        self.assertRaises(DataModelException, save_role, roleid, "Should Fail")
        register_new_user('User2')
        self.assertRaises(DataModelException, save_role, roleid, "Should Fail")

    def testDeleteAnotherUsersRole(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        role1url = create_role("Test Role 1")
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        self.assertEquals(len(get_my_roles()), 1)
        self.assertRaises(DataModelException, delete_place, role1url)

    def testWritableProperty(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        create_role("Role 1")
        myroles1 = get_my_roles()
        self.assertTrue(myroles1[0].get("writable"))
        self.assertTrue(myroles1[1].get("writable"))
        place1 = create_place("Place 1")
        create_presence(myroles1[1]["id"], place1)
        logoutCurrentUser()
        setCurrentUser("user2@gmail.com", "User2")
        register_new_user('User2')
        create_role("Role 2")
        myroles2 = get_my_roles()
        self.assertTrue(myroles2[0].get("writable"))
        self.assertTrue(myroles2[1].get("writable"))
        myroles3 = get_roles_in_place(place1)
        self.assertFalse(myroles3[0].get("writable"))


    def testCreatePresence(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        place2 = create_place("Test Place 2")
        role1 = create_role("Test Role 1")
        self.assertEqual(len(get_roles_in_place(place1)), 0)
        self.assertEqual(len(get_roles_in_place(place2)), 0)
        create_presence(role1, place1)
        self.assertEqual(len(get_roles_in_place(place1)), 1)
        self.assertEqual(len(get_roles_in_place(place2)), 0)
        create_presence(role1, place2)
        self.assertEqual(len(get_roles_in_place(place1)), 1)
        self.assertEqual(len(get_roles_in_place(place2)), 1)

    def testDeletePresence(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        place2 = create_place("Test Place 2")
        role1 = create_role("Test Role 1")
        create_presence(role1, place1)
        create_presence(role1, place2)
        self.assertEqual(len(get_roles_in_place(place1)), 1)
        self.assertEqual(len(get_roles_in_place(place2)), 1)
        delete_presence(role1, place1)
        self.assertEqual(len(get_roles_in_place(place1)), 0)
        self.assertEqual(len(get_roles_in_place(place2)), 1)
        delete_presence(role1, place2)
        self.assertEqual(len(get_roles_in_place(place1)), 0)
        self.assertEqual(len(get_roles_in_place(place2)), 0)

    def testIsPresent(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        place2 = create_place("Test Place 2")
        role1 = create_role("Test Role 1")
        role2 = create_role("Test Role 2")
        create_presence(role1, place1)
        create_presence(role2, place2)
        self.assertTrue(is_present(role1, place1))
        self.assertFalse(is_present(role1, place2))
        self.assertTrue(is_present(role2, place2))
        self.assertFalse(is_present(role2, place1))
        
    def testDeleteOccupiedPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        role1 = create_role("Test Role 1")
        create_presence(role1, place1)
        self.assertRaises(ActionException, delete_place, place1)

    def testDeleteUnoccupiedPlace(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        place2 = create_place("Test Place 2")
        role1 = create_role("Test Role 1")
        create_presence(role1, place2)
        delete_place(place1)

    def testDeleteRoleWithPresence(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        self.assertEqual(len(get_roles_in_place(place1)), 0)
        role1 = create_role("Test Role 1")
        create_presence(role1, place1)
        self.assertEqual(len(get_roles_in_place(place1)), 1)
        self.assertRaises(ActionException, delete_role, role1)

    def testSavePresence(self):
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        place1 = create_place("Test Place 1")
        role1 = create_role("Test Role 1")
        create_presence(role1, place1)
        save_presence(role1, place1, ["broken nose", "busted leg"])
        self.assertEqual(2, len(get_roles_in_place(place1)[0].get("damage")))

def setupLotsOfRoles():
    setCurrentUser("user1@gmail.com", "User1")
    register_new_user('User1')
    place1 = create_place("Test Place 1")
    role1 = create_role("Test Role 1")
    create_presence(role1, place1)
    role2 = create_role("Test Role 2")
    create_presence(role2, place1)
    create_role("Test Role 3")
    create_role("Test Role 4")
    create_role("Test Role 5")
    create_role("Test Role 6")
    create_role("Test Role 7")
    create_role("Test Role 8")
    create_role("Test Role 9")
    create_role("Test Role 10")
    create_role("Test Role 11")
    create_role("Test Role 12")
    create_role("Test Role 13")
    create_role("Test Role 14")
    setCurrentUser("user2@gmail.com", "User2")
    register_new_user('User2')
    create_place("Test Place 2")
    create_role("Test Role 15")
    create_role("Test Role 16")
    return place1

if __name__ == "__main__":
    unittest.main()

