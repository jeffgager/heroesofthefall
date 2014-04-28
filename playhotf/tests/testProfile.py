'''
Created on 11 May 2012

@author: Jeff
'''
import os
import unittest

from google.appengine.ext import testbed, ndb

from actions import register_new_user, get_profiles, _get_my_profile, \
    NoUserException, ActionException, NoUserNameException, get_my_profile_name, \
    set_playing, create_role
from model import Profile, SiteMasterProfile, DataModelException


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

    def tearDown(self):
        self.testbed.deactivate()

    def testNotLoggedIn(self):
        '''Should just fail with a NoUserException'''
        self.assertRaises(NoUserException, _get_my_profile)

    def testBlankProfileNameSignup(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('SiteAdmin')
        logoutCurrentUser()
        setCurrentUser("new1@gmail.com", "NewUser1")
        self.assertRaises(DataModelException, register_new_user, '')
        self.assertRaises(ActionException, register_new_user, None)
        
    def testFirstSignup(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        '''Should fail repeatedly with a NoUserNameException'''
        self.assertRaises(NoUserNameException, _get_my_profile)
        self.assertRaises(NoUserNameException, _get_my_profile)
        '''Should create the Site Administrator with its own place for template Roles'''
        register_new_user('SiteAdmin')
        '''Should have a SiteAdmin now'''
        self.assertIsInstance(_get_my_profile(), SiteMasterProfile)
        self.assertEquals(SiteMasterProfile.query().count(), 1)
        self.assertEquals(Profile.query().count(), 1)
        '''Should be called whatever it was set up as'''
        self.assertEquals('SiteAdmin', get_my_profile_name())
        '''Check number of entities'''
        self.assertEquals(1, SiteMasterProfile.query().count())
        '''Should just fail with a NoUserException if logged out'''
        logoutCurrentUser()
        self.assertRaises(NoUserException, _get_my_profile)
        
    def testNextSignup(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        self.assertRaises(NoUserNameException, _get_my_profile)
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('SiteAdmin')
        self.assertIsInstance(_get_my_profile(), Profile)
        self.assertEquals('SiteAdmin', get_my_profile_name())
        self.assertEquals(1, SiteMasterProfile.query().count())
        self.assertEquals(1, Profile.query().count())
        logoutCurrentUser()
        self.assertRaises(NoUserException, _get_my_profile)
        setCurrentUser("new1@gmail.com", "NewUser1")
        self.assertRaises(NoUserNameException, _get_my_profile)
        self.assertRaises(NoUserNameException, _get_my_profile)
        '''Should create a GameMaster for this new profile with its own Place for private conversations'''
        register_new_user('NewUserOne')
        '''Should have an profile now'''
        self.assertIsInstance(_get_my_profile(), Profile)
        '''Should be called whatever it was set up as'''
        self.assertEquals('NewUserOne', get_my_profile_name())
        self.assertEquals(2, Profile.query().count())
        logoutCurrentUser()
        self.assertRaises(NoUserException, _get_my_profile)

    def testManySignups(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('SiteAdmin')
        logoutCurrentUser()

        setCurrentUser("new1@gmail.com", "NewUser1")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('NewUserOne')
        logoutCurrentUser()

        setCurrentUser("new2@gmail.com", "NewUser2")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('NewUserTwo')
        logoutCurrentUser()

        setCurrentUser("new3@gmail.com", "NewUser3")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('NewUserThree')
        logoutCurrentUser()

        setCurrentUser("another1@gmail.com", "AnotherUser1")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('AnotherUserOne')
        logoutCurrentUser()

        setCurrentUser("another2@gmail.com", "AnotherUser2")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('AnotherUserTwo')
        logoutCurrentUser()

        setCurrentUser("another3@gmail.com", "AnotherUser3")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('AnotherUserThree')

        self.assertEqual(len(get_profiles("Another")), 3)
        self.assertEqual(len(get_profiles("another")), 3)
        self.assertEqual(len(get_profiles("new")), 3)

    def testChangingSomeoneElsesProfile(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('SiteAdmin')
        site_admin_id = _get_my_profile().key.urlsafe()
        logoutCurrentUser()
        setCurrentUser("new1@gmail.com", "NewUser1")
        register_new_user('NewUserOne')
        new_user_1_id = _get_my_profile().key.urlsafe()
        role_1_id = create_role("Test Role 1")
        set_playing(role_1_id)
        logoutCurrentUser()
        setCurrentUser("new2@gmail.com", "NewUser2")
        ''' This code simulates an attack against the Model, using the Google Account ID to load another users Profile '''
        profile = ndb.Key(urlsafe=new_user_1_id).get()
        profile.playing = ndb.Key(urlsafe=role_1_id)
        self.assertRaises(DataModelException, profile.put)
        profile = ndb.Key(urlsafe=site_admin_id).get()
        profile.playing = ndb.Key(urlsafe=role_1_id)
        self.assertRaises(DataModelException, profile.put)
        register_new_user('NewUserTwo')
        ''' This code simulates an attack against the Model, using the Google Account ID to load another users Profile '''
        profile = ndb.Key(urlsafe=new_user_1_id).get()
        profile.playing = ndb.Key(urlsafe=role_1_id)
        self.assertRaises(DataModelException, profile.put)
        profile = ndb.Key(urlsafe=site_admin_id).get()
        profile.playing = ndb.Key(urlsafe=role_1_id)
        self.assertRaises(DataModelException, profile.put)

    def testNameUsed(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('SiteAdmin')
        logoutCurrentUser()
        setCurrentUser("new1@gmail.com", "NewUser1")
        register_new_user('NewUserOne')
        logoutCurrentUser()
        setCurrentUser("new2@gmail.com", "NewUser2")
        register_new_user('NewUserTwo')
        logoutCurrentUser()
        setCurrentUser("new3@gmail.com", "NewUser3")
        self.assertRaises(ActionException, register_new_user, 'NewUserOne')

    def testProfilePreviouslyRegistered(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('SiteAdmin')
        logoutCurrentUser()
        setCurrentUser("new1@gmail.com", "NewUser1")
        register_new_user('NewUserOne')
        logoutCurrentUser()
        setCurrentUser("new2@gmail.com", "NewUser2")
        register_new_user('NewUserTwo')
        logoutCurrentUser()
        setCurrentUser("new1@gmail.com", "NewUser3")
        self.assertRaises(ActionException, register_new_user, 'NewUserOne')

    def testLoginAndOut(self):
        setCurrentUser("admin@gmail.com", "Administrator")
        self.assertRaises(NoUserNameException, _get_my_profile)
        logoutCurrentUser()
        setCurrentUser("admin@gmail.com", "Administrator")
        self.assertRaises(NoUserNameException, _get_my_profile)
        register_new_user('AnotherUserTwo')
        logoutCurrentUser()
        setCurrentUser("admin@gmail.com", "Administrator")
        self.assertIsInstance(_get_my_profile(), Profile)        
        
    def testGetProfiles(self):
        self.assertRaises(ActionException, get_profiles, None)
        self.assertEqual(len(get_profiles('a')), 0)
        setCurrentUser("admin@gmail.com", "Administrator")
        register_new_user('Admin')
        logoutCurrentUser()
        setCurrentUser("user1@gmail.com", "User1")
        register_new_user('User1')
        logoutCurrentUser()
        self.assertEqual(len(get_profiles('a')), 1)
        self.assertEqual(len(get_profiles('u')), 1)
        self.assertEqual(len(get_profiles('A')), 1)
        self.assertEqual(len(get_profiles('U')), 1)


if __name__ == "__main__":
    unittest.main()

