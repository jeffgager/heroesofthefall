'''
Created on 11 May 2012

@author: Jeff
'''
import os
import unittest

from google.appengine.ext import testbed

from actions import register_new_user, get_armour_types, get_profession_types,\
    get_skill_types, get_weapon_types


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

    def testArmour(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        self.assertEqual(len(get_armour_types()), 8)

    def testProfessions(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        self.assertEqual(len(get_profession_types()), 20)
        self.assertEqual(len(get_profession_types()[0].get("skills")), 14)

    def testSkills(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        self.assertEqual(len(get_skill_types()), 97)

    def testWeapons(self):
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        self.assertEqual(len(get_weapon_types()), 34)
        self.assertIsNotNone(get_weapon_types()["Stick"].get("mskill"))
        self.assertIsNone(get_weapon_types()["Stick"].get("askill"))
        self.assertIsNotNone(get_weapon_types()["Dagger"].get("mskill"))
        self.assertIsNotNone(get_weapon_types()["Dagger"].get("askill"))

if __name__ == "__main__":
    unittest.main()

