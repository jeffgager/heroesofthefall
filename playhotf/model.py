'''
@author: Jeff
'''

import re
from string import lower

from google.appengine.api import users
from google.appengine.ext import ndb as db
from google.appengine.ext.ndb.polymodel import PolyModel


class DataModelException(Exception):
    """Raise to halt an operation and report an exception to the client
    """
    pass

def _iseditable(obj, key=None):
    profile = None
    if isinstance(obj, Profile):
        profile = obj
    elif isinstance(obj, Place) or isinstance(obj, Role):
        profile = obj.key.parent().get()
    elif isinstance(obj, Presence):
        profile = obj.key.parent().parent().get()
    elif obj == Profile:
        profile = key.get()
    elif obj == Place or obj == Role:
        profile = key.parent().get()
    elif obj == Presence:
        profile = key.parent().parent().get()
    if profile is None or profile.saved == False:
        return True
    ownersprofilekey = profile.key.id();
    user = users.get_current_user()
    if user is None:
        raise DataModelException()
    myprofilekey = str(user.user_id())
    return  myprofilekey == ownersprofilekey

def _verify_update_access(obj):
    if not _iseditable(obj):
        raise DataModelException('No Update Access')

@classmethod
def _verify_delete_access(cls, key):
    if not _iseditable(cls, key):
        raise DataModelException('No Delete Access')

@classmethod
def _post_get(cls, key, future):
    obj = future.get_result()
    if obj is not None:
        obj.saved = True
        obj.writable = _iseditable(obj)

def _post_put(self, future):
    self.saved = True
    self.writable = _iseditable(self)

def name_validator(self, value):
    nameLength = len(value)
    if value is None or nameLength < 4 or nameLength > 40:
        raise DataModelException('Name must be between 4 and 40 characters long')

def profile_name_validator(self, user_name):
    namelen = len(user_name)
    if user_name is None or namelen < 4 or namelen > 20:
        raise DataModelException('Your nmust be between 4 and 20 characters long')
    if re.search("\W", user_name) is not None:
        raise DataModelException('Your name can only contain the characters A-Z, a-z, 0-9 and _')
    if re.search("[^a-z]", lower(user_name[:3])) is not None:
        raise DataModelException('The first three characters of your name can only contain the characters A-Z, a-z')

def place_name_validator(self, place_name):
    if place_name == None or len(place_name) < 4 or len(place_name) > 60:
        raise DataModelException('Name must be between 4 and 60 characters long')
    validset = set(re.compile(r'([\sa-zA-Z0-9\-\(\)])').findall(place_name))
    titleset = set(re.compile(r'.').findall(place_name))
    if len(validset) < len(titleset):
        raise DataModelException('Place Name contains the following invalid characters: ' + str("".join(titleset.difference(validset))))

''' parent=Profile, id=Generated '''
class Place(db.Model):
    name = db.StringProperty("name", validator=place_name_validator)          # Name
    name_order = db.StringProperty("order", required=True)                    # Name order (upper case)
    description = db.TextProperty("desc")                                     # Description of the Place
    saved = False
    writable = False
    _post_get_hook = _post_get
    _post_put_hook = _post_put
    _pre_put_hook = _verify_update_access
    _pre_delete_hook = _verify_delete_access

''' parent=Profile, id=Generated '''
class Role(PolyModel):
    name = db.StringProperty("name", required=True, 
                             validator=name_validator)                  # Name  
    name_order = db.StringProperty("order", required=True)              # Name order (upper case)
    description = db.StringProperty("desc", indexed=False)              # Description of the Identity
    portrait = db.BlobKeyProperty("port")                               # BlobKey for the portrait of the Identity
    updated = db.DateTimeProperty("updt", indexed=False, required=True) # Date/Time updated
    saved = False
    writable = False
    _post_get_hook = _post_get
    _post_put_hook = _post_put
    _pre_put_hook = _verify_update_access
    _pre_delete_hook = _verify_delete_access

''' parent=Profile, id=Generated '''
class HumanNPC(Role):
    vigor = db.IntegerProperty("vig", indexed=False, required=True)
    mettle = db.IntegerProperty("met", indexed=False, required=True)
    wit = db.IntegerProperty("wit", indexed=False, required=True)
    glamour = db.IntegerProperty("glm", indexed=False, required=True)
    spirit = db.IntegerProperty("sprt", indexed=False, required=True)
    handed = db.StringProperty("hand", indexed=False, required=True, choices=["A","R","L"])
    right = db.StringProperty("rwpn", indexed=False)                     # Weapon name
    left = db.StringProperty("lwpn", indexed=False)                      # Weapon name
    head = db.StringProperty("head", indexed=False)                      # Armour name
    face = db.StringProperty("face", indexed=False)                      # Armour name
    throat = db.StringProperty("throat", indexed=False)                  # Armour name
    neck = db.StringProperty("neck", indexed=False)                      # Armour name
    chest = db.StringProperty("chest", indexed=False)                    # Armour name
    groin = db.StringProperty("groin", indexed=False)                    # Armour name
    left_eye = db.StringProperty("leye", indexed=False)                  # Armour name
    left_arm = db.StringProperty("larm", indexed=False)                  # Armour name
    left_hand = db.StringProperty("lhand", indexed=False)                # Armour name
    left_leg = db.StringProperty("lleg", indexed=False)                  # Armour name
    left_foot = db.StringProperty("lfoot", indexed=False)                # Armour name
    right_eye = db.StringProperty("reye", indexed=False)                 # Armour name
    right_arm = db.StringProperty("rarm", indexed=False)                 # Armour name
    right_hand = db.StringProperty("rhand", indexed=False)               # Armour name
    right_leg = db.StringProperty("rleg", indexed=False)                 # Armour name
    right_foot = db.StringProperty("rfoot", indexed=False)               # Armour name
    abilities = db.KeyProperty("able", kind="Ability", repeated=True)

''' parent=Profile, id=Generated '''
class HumanPC(HumanNPC):
    wyrd = db.IntegerProperty("wyrd", indexed=False, required=True)

''' parent=Root, id=GAE.userid '''
class Profile(Role):
    name = db.StringProperty("name", indexed=False, validator=profile_name_validator)   # Name of the Profile 
    playing = db.KeyProperty("play", indexed=False, kind="Role")                        # The Role that the user is currently playing
        
''' parent=Role, id=name '''
class RoleProfession(db.Model):
    name = db.StringProperty("name", indexed=False, required=True)
    level = db.IntegerProperty("lev", indexed=False, required=True)
    modifier = db.IntegerProperty("mod", indexed=False, required=True)

''' parent=Role, id=name '''
class RoleSkill(db.Model):
    professionname = db.StringProperty("prof", indexed=False, required=True)
    name = db.StringProperty("name", indexed=False, required=True)
    level = db.IntegerProperty("lev", indexed=False, required=True)
    modifier = db.IntegerProperty("mod", indexed=False, required=True)

''' parent=Root, id=GAE.userid '''
class SiteMasterProfile(Profile):
    free_places = db.IntegerProperty("free", indexed=False, required=True)      # Number of places a GM can create for free

''' parent=Place, id=Role.name'''
class Presence(db.Model):
    role = db.KeyProperty("role", kind="Role", required=True)                   # Role that is present
    damage = db.StringProperty("damg", indexed=False, repeated=True)            # List of combat damage
    saved = False
    writable = False
    _post_get_hook = _post_get
    _post_put_hook = _post_put
    _pre_put_hook = _verify_update_access
    _pre_delete_hook = _verify_delete_access

''' parent=Root, id=Generated '''
class Narrative(db.Model):
    place = db.KeyProperty("place", kind="Place", required=True)               # Place
    role = db.KeyProperty("role", kind="Role", required=True)                  # Role
    observed = db.KeyProperty("obs", kind="Role", repeated=True)               # Observers
    journaled = db.KeyProperty("jrn", kind="Role", repeated=True)              # Journals
    body = db.StringProperty("body", indexed=False, required=True)             # Text
    created = db.DateTimeProperty("crt", required=True)                        # Date/Time created
    createdby = db.KeyProperty("cby", kind="Role", required=True)              # Created user
    updated = db.DateTimeProperty("upd", indexed=False)                        # Date/Time updated
    updatedby = db.KeyProperty("uby", kind="Role")                             # Updated user
    _post_get_hook = _post_get
    _post_put_hook = _post_put
    _pre_put_hook = _verify_update_access
    _pre_delete_hook = _verify_delete_access

