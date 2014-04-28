'''
Created on 24 Mar 2012
@author: Jeff
'''

from google.appengine._internal.django.utils.datetime_safe import datetime
from google.appengine.api import users
from google.appengine.ext import ndb as db
from google.appengine.ext.ndb import query
from google.appengine.ext.ndb.query import Cursor

from model import Profile, Place, Role, SiteMasterProfile, Presence, Narrative
import staticmodel


class NoUserException(Exception):
    """Raised where no authenticated user can be provided by the GAE user service.
    """
    pass

class ActionException(Exception):
    """Raised where an action cannot be completed and the reason can be reported to the user
    """
    pass

def _get_profile():
    user = users.get_current_user()
    if user is None:
        raise NoUserException()
    userid = str(user.user_id())
    profile = db.Key(Profile, userid).get()
    if profile is None:
        profile = db.Key(SiteMasterProfile, userid).get()
    return profile, userid

class NoUserNameException(Exception):
    """Raised where a user profile has no user name
    """
    pass

def _get_my_profile():
    profile, userid = _get_profile()
    if profile is None or profile.name is None:
        raise NoUserNameException()
    return profile

def _get_site_master():
    sml = SiteMasterProfile.query().fetch(1)
    return sml[0] if len(sml) > 0 else None

def get_profiles(search_string):
    ''' get a list of profile names that satisfy a mandatory search string '''
    if search_string == None or len(search_string) < 1:
        raise ActionException('You must provide a search string')
    upper_search = search_string.upper();
    return [p.name for p in 
        Profile.query(query.AND(
        Profile.name_order >= upper_search,
        Profile.name_order <= upper_search + "ZZZZZZ")).order(Profile.name_order).fetch(10)]

def get_my_profile_name():
    ''' return the current users profile name ''' 
    return _get_my_profile().name

def register_new_user(name):
    ''' register a Profile for the current user as long as the name hasn't been used and the user doesn't already have a Profile '''
    if name is None:
        raise ActionException('Name is required')
    if len(Profile.query(Profile.name_order == name.upper()).fetch(1)) > 0:
        raise ActionException('Name has been used by someone else')
    profile, userid = _get_profile()
    if profile is None:
        sm = _get_site_master()
        if sm is None:
            profile = SiteMasterProfile(id=userid)
            profile.free_games = 1
            profile.free_places = 2
        else:
            profile = Profile(id=userid)
    else:
        raise ActionException('User already has a profile')
    profile.name = name
    profile.name_order = name.upper()
    profile.updated = datetime.now()
    profile.playing = profile.key
    profile.put()
    return profile.key.urlsafe()

def create_place(place_name):
    ''' create a new place in the current users profile '''
    if place_name is None:
        raise ActionException('Name is required')
    my_profile = _get_my_profile()
    place = Place(name=place_name, parent=my_profile.key)
    place.name = place_name
    place.name_order = place_name.upper()
    place.updated = datetime.now()
    place.put()
    return place.key.urlsafe()

def _get_place(place_id):
    return db.Key(urlsafe=place_id).get()

def get_place_properties(place_id):
    ''' get the properties of a Place '''
    place = _get_place(place_id)
    return {"name":place.name, "description":place.description, "writable":place.writable}

def get_my_places():
    ''' get the id and name of the current users Places '''
    return [(place.key.urlsafe(), place.name) for place in Place.query(ancestor=_get_my_profile().key).order(Place.name_order).fetch()]

def save_place(place_id, name=None, description=None):
    ''' change the properties of a Place '''
    place = _get_place(place_id)
    if place is None:
        raise ActionException('Place not found')
    place.name = name if name != None else place.name 
    place.name_order = name.upper() if name != None else place.name_order
    place.description = description if description != None else place.description
    place.updated = datetime.now()
    place.put()

def delete_place(place_id):
    ''' delete a Place as long as no Roles are Present there ''' 
    if len(get_roles_in_place(place_id)) > 0:
        raise ActionException('This place is occupied so it cannot be removed') 
    db.Key(urlsafe=place_id).delete()
    
def create_role(role_name):
    ''' create a Role in the current users Profile '''
    if role_name is None:
        raise ActionException('Role name is required')
    my_profile = _get_my_profile()
    role = Role(name=role_name, parent=my_profile.key)
    role.name = role_name
    role.name_order = role_name.upper()
    role.updated = datetime.now()
    role.put()
    return role.key.urlsafe()

def _get_role(role_id):
    return db.Key(urlsafe=role_id).get()

def get_role_properties(role):
    ''' get the properties of a Role '''
    return {"id":role.key.urlsafe(), "name":role.name, "description":role.description, "updated":role.updated, "writable":role.writable}

def get_my_roles():
    ''' get the Roles in the current users Profile '''
    return [get_role_properties(role) for role in Role.query(ancestor=_get_my_profile().key).order(Role.name_order).fetch()]

def _get_presences(place_id):
    place = db.Key(urlsafe=place_id)
    return [presence for presence in Presence.query(ancestor=place).fetch()]

def _get_roles_in_place(place_id):
    ''' get the Roles in a Place '''
    roles_list = [presence.role for presence in _get_presences(place_id)]
    return roles_list

def get_roles_in_place(place_id):
    ''' get the Roles in a Place '''
    def get_props(presence):
        role = presence.role.get()
        props = get_role_properties(role)
        props.update({"damage":presence.damage})
        return props
    return [get_props(presence) for presence in _get_presences(place_id)]

def save_role(role_id, name=None, description=None, portrait=None):
    ''' change the properties of a Role '''
    role = _get_role(role_id)
    if role is None:
        raise ActionException('role not found')
    role.name = name if name != None else role.name
    role.name_order = name.upper() if name != None else role.name_order
    role.description = description if description != None else role.description
    role.portrait = portrait if portrait != None else role.portrait
    role.updated = datetime.now()
    role.put()

def delete_role(role_id):
    ''' delete a Role as long as it is not Present in a Place ''' 
    if len(Presence.query(Presence.role == db.Key(urlsafe=role_id)).fetch()) > 0:
        raise ActionException('Role still has presence')
    db.Key(urlsafe=role_id).delete()

def add_skill_to_role(role_id, skillname):
    ''' Add a RoleSkill to a Role '''
    role = _get_role(role_id)
    if role is None:
        raise ActionException('role not found')
    role.name = name if name != None else role.name
    role.name_order = name.upper() if name != None else role.name_order
    role.description = description if description != None else role.description
    role.portrait = portrait if portrait != None else role.portrait
    role.updated = datetime.now()
    role.put()

def set_playing(role_id):
    ''' set the Role that the current user is playing in their Profile '''
    profile = _get_my_profile()
    profile.playing = profile.key if role_id is None else db.Key(urlsafe=role_id)
    profile.put()

def _get_playing():
    return _get_my_profile().playing

def get_playing():
    _get_playing().urlsafe()
    
def _get_presence(role_id, place_id):
    ''' true if a Role is in a Place '''
    profile = _get_my_profile()
    role = _get_role(role_id)
    if role is None:
        raise ActionException('role not found')
    place = _get_place(place_id)
    if place is None:
        raise ActionException('place not found')
    pkey = db.Key(Profile, profile.key.id(), Place, place.key.id(), Presence, role.name)
    return pkey.get()

def is_present(role_id, place_id):
    return _get_presence(role_id, place_id) != None

def create_presence(role_id, place_id):
    ''' create a Presence for a Role in a Place '''
    profile = _get_my_profile()
    if is_present(role_id, place_id):
        raise ActionException('Role is already present')
    role = _get_role(role_id)
    place = _get_place(place_id)
    pkey = db.Key(Profile, profile.key.id(), Place, place.key.id(), Presence, role.name)
    presence = pkey.get()
    if presence is None:
        presence = Presence(id=role.name, parent=place.key)
        presence.role = role.key
    presence.put()
    return presence.key.urlsafe()

def delete_presence(role_id, place_id):
    ''' Delete a Presence '''
    presence = _get_presence(role_id, place_id)
    if presence is None:
        raise ActionException('presence not found')
    presence.key.delete()

def save_presence(role_id, place_id, damage=None):
    ''' Save a Presence '''
    presence = _get_presence(role_id, place_id)
    if presence is None:
        raise ActionException('presence not found')
    presence.damage = damage if damage != None else presence.damage

def _get_narrative(narrative_id):
    return db.Key(urlsafe=narrative_id).get()

def get_narrative_properties(narrative):
    ''' get the properties of a Narrative '''
    role = narrative.role.get()
    place = narrative.place.get()
    return {"id":narrative.key.urlsafe(),
          "role":role.name, 
         "place":place.name, 
          "body":narrative.body, 
       "created":narrative.created, 
       "updated":narrative.updated, 
     "journaled": _get_my_profile().playing in narrative.journaled}

def create_narrative(place_id, narrative_body):
    ''' create Narrative text for a Role in a Place '''
    profile = _get_my_profile()
    playing_role = profile.playing.urlsafe()
    if playing_role is None:
        raise ActionException('Not playing a Role')
    if not is_present(playing_role, place_id):
        raise ActionException('Role not present')
    place = _get_place(place_id)
    narrative = Narrative(role=_get_playing(), place=place.key)
    roles = _get_roles_in_place(place_id)
    roles.append(place.key.parent())
    narrative.observed = roles
    narrative.body = narrative_body
    narrative.created = datetime.now()
    narrative.createdby = profile.playing
    narrative.put()
    return narrative.key.urlsafe()

def save_narrative(narrative_id, body=None, place_id=None, isjournaled=None):
    ''' change the properties of a Narrative '''
    narrative = _get_narrative(narrative_id)
    if narrative is None:
        raise ActionException('Narrative not found')

    if place_id is not None:
        profile = _get_my_profile()
        playing_role = profile.playing.urlsafe()
        if not is_present(playing_role, place_id):
            raise ActionException('Role not present')
        roles = _get_roles_in_place(place_id)
        place = _get_place(place_id)
        roles.append(place.key.parent())
        narrative.place = db.Key(urlsafe=place_id)
        narrative.observed = roles

    if isjournaled is not None:
        profile = _get_my_profile()
        playing_role = profile.playing.urlsafe()
        journalednow = playing_role in narrative.journaled
        if not journalednow and isjournaled == True:
            narrative.journaled.append(playing_role)
        if journalednow and isjournaled == False:
            narrative.journaled.remove(playing_role)

    narrative.body = body if body is not None else narrative.body
    narrative.updated = datetime.now()
    narrative.updatedby = _get_my_profile().key
    narrative.put()

def delete_narrative(narrative_id):
    ''' delete a Narrative '''
    narrative = _get_narrative(narrative_id)
    if narrative is None:
        raise ActionException('narrative not found')
    narrative.key.delete()

NARRATIVE_PAGE_SIZE = 10

def get_first_narrative():
    ''' get the First batch of Narratives visible to the role the current user is playing '''
    playing_role = _get_my_profile().playing
    query = Narrative.query(Narrative.observed == playing_role).order(-Narrative.created)
    narrative_list, next_cursor, more = query.fetch_page(NARRATIVE_PAGE_SIZE)
    narrative_properties= [get_narrative_properties(narrative.key.get()) for narrative in narrative_list]
    return {"narrative":narrative_properties, 
                 "next":next_cursor.urlsafe() if more else None, 
                 "more":more}

def get_more_narrative(start_cursor):
    ''' get the next batch of Narratives visible to the role the current user is playing after the cursor provided '''
    playing_role = _get_my_profile().playing
    query = Narrative.query(Narrative.observed == playing_role).order(-Narrative.created)
    narrative_list, next_cursor, more = query.fetch_page(NARRATIVE_PAGE_SIZE, start_cursor=Cursor(urlsafe=start_cursor))
    narrative_properties = [get_narrative_properties(narrative.key.get()) for narrative in narrative_list]
    return {"narrative":narrative_properties, 
                 "next":next_cursor.urlsafe() if more else None, 
                 "more":more}

def get_armour_properties(armour):
    ''' get the properties of an armour type '''
    return {"name":armour.name, 
         "slash":armour.slash, 
         "crush":armour.crush, 
        "pierce":armour.pierce, 
    "initiative":armour.initiative}

def get_armour_types():
    ''' get the list of available armour types '''
    armour_list = staticmodel.get().armour.values()
    return [get_armour_properties(armour) for armour in armour_list]

def get_profession_properties(profession):
    ''' get the properties of a Profession '''
    return {"name":profession.name,
     "skills":[get_skill_properties(skill) for skill in profession.skills]}

def get_profession_types():
    ''' get the list of available Professions and the skills associated with each '''
    professions_list = staticmodel.get().professions.values()
    return [get_profession_properties(profession) for profession in professions_list]

def get_skill_properties(skill):
    ''' get the properties of a Skill '''
    return {"name":skill.name,
     "attribute":skill.attribute}

def get_skill_types():
    ''' get the list of available Skills '''
    skills_list = staticmodel.get().skills.values()
    return [get_skill_properties(skill) for skill in skills_list]

def get_weapon_properties(weapon):
    ''' get the properties of a weapon, including the skills required to use it '''
    return {"name":weapon.name, 
            "th":weapon.two_handed, 
       "defence":weapon.defence, 
          "cmin":weapon.close_min, 
          "cmax":weapon.close_max, 
        "cslash":weapon.close_slash, 
        "ccrush":weapon.close_crush,
       "cpierce":weapon.close_pierce, 
         "cinit":weapon.close_initiative,
        "mskill":weapon.melee_skill.name if weapon.melee_skill is not None else None,
          "rmin":weapon.range_min, 
          "rmax":weapon.range_max,
        "rslash":weapon.range_slash, 
        "rcrush":weapon.range_crush,
       "rpierce":weapon.range_pierce, 
         "rinit":weapon.range_initiative,
        "askill":weapon.accuracy_skill.name if weapon.accuracy_skill is not None else None
          };

def get_weapon_types():
    ''' get the list of available armour '''
    weapon_list = staticmodel.get().weapons
    return [get_weapon_properties(weapon) for weapon in weapon_list]
