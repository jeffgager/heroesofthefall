'''
Created on 4 Jan 2014
@author: Jeff
'''

class Armour:
    def __init__(self, name, slash, crush, pierce, initiative):
        self.name = name
        self.slash = slash
        self.crush = crush
        self.pierce = pierce
        self.initiative = initiative

class Skill:
    def __init__(self, name, attribute):
        ["V","W","G"].index(attribute)
        self.name = name
        self.attribute = attribute

class Profession:
    def __init__(self, name):
        self.name = name
        self.skills = []

class Weapon:
    def __init__(self, name,two_handed,defence=None,
               close_min=None,close_max=None,close_slash=None,close_crush=None,close_pierce=None,close_initiative=None,melee_skill=None,
               range_min=None,range_max=None,range_slash=None,range_crush=None,range_pierce=None,range_initiative=None,accuracy_skill=None,
               range_opt=None):
        self.name=name
        [0, 1, 2].index(two_handed)
        self.two_handed=two_handed
        self.defence=defence
        self.close_min=close_min
        self.close_max=close_max
        self.close_slash=close_slash
        self.close_crush=close_crush
        self.close_pierce=close_pierce
        self.close_initiative=close_initiative
        self.melee_skill=melee_skill
        self.range_min=range_min
        self.range_max=range_max
        self.range_slash=range_slash
        self.range_crush=range_crush
        self.range_pierce=range_pierce
        self.range_initiative=range_initiative
        self.accuracy_skill=accuracy_skill
        self.range_opt=range_opt
               
class StaticModel:
    def __init__(self, armour, professions, skills, weapons):
        self.armour = armour
        self.professions = professions
        self.skills = skills
        self.weapons = weapons

def create_static_model():
    
    armour = {}
    def create_armour(name, slash, crush, pierce, initiative):
        armour[name] = Armour(name=name, slash=slash, crush=crush, pierce=pierce, initiative=initiative)
    
    create_armour("Padding", 1, 1, 0, 0)
    create_armour("Leather", 1, 1, 1, -1)
    create_armour("Ring Mail", 2, 1, 1, -1)
    create_armour("Scale", 3, 0, 2, -2)
    create_armour("Jazerant", 3, 0, 2, -2)
    create_armour("Chain Mail", 3, 0, 2, -1)
    create_armour("Splint", 3, 1, 2, -2)
    create_armour("Plate", 3, 2, 3, -3)

    professions = {}
    skills = {}
    def create_profession(name):
        profession = Profession(name=name)
        professions[name] = profession
        return profession

    melee = create_profession("Melee")
    accuracy = create_profession("Accuracy")
    larceny = create_profession("Larceny")
    hunting = create_profession("Hunting")
    espionage = create_profession("Espionage")
    boat_craft = create_profession("Boat Craft")
    assassination = create_profession("Assassination")
    horsemanship = create_profession("Horsemanship")
    performance = create_profession("Performance")
    command = create_profession("Command")
    scholarship = create_profession("Scholarship")
    shamanism = create_profession("Shamanism")
    unarmed_combat = create_profession("Unarmed Combat")
    artifice = create_profession("Artifice")
    pathfinding = create_profession("Pathfinding")
    pathfinder_artificer = create_profession("Pathfinder Artificer")
    trading = create_profession("Trading")
    ministry = create_profession("Ministry")
    nobility = create_profession("Nobility")
    administration = create_profession("Administration")

    def create_skill(professions, attribute, name):
        skill = Skill(name=name, attribute=attribute)
        for profession in professions:
            profession.skills.append(skill)
        skills[name] = skill
        return skill

    heavy_swords = create_skill([melee], "V", "Heavy Swords")
    light_swords = create_skill([melee], "V", "Light Swords")
    daggers = create_skill([melee, assassination], "V", "Daggers")
    concussion_weapons = create_skill([melee], "V", "Concussion Weapons")
    flails = create_skill([melee], "V", "Flails")
    axes = create_skill([melee], "V", "Axes")
    spears = create_skill([melee, hunting], "V", "Spears Under 8'")
    long_spears = create_skill([melee, hunting], "V", "Spears Over 8'")
    polearms = create_skill([melee], "V", "Polearms")
    lances = create_skill([melee], "V", "Lances")
    shields = create_skill([melee], "V", "Shields")
    create_skill([melee, unarmed_combat], "V", "Dodge")
    create_skill([melee, artifice, accuracy], "W", "Weapon Purchase")
    create_skill([melee, unarmed_combat, command], "G", "Intimidation")

    thrown_spears = create_skill([accuracy], "V", "Thrown Spears")
    bows = create_skill([accuracy, hunting, assassination], "V", "Bows")
    thrown_daggers = create_skill([accuracy], "V", "Thrown Daggers")
    thrown_fransiscas = create_skill([accuracy], "V", "Thrown Franciscas")
    crossbows = create_skill([accuracy, hunting, assassination], "V", "Crossbows")
    slings = create_skill([accuracy, hunting], "V", "Slings")            

    create_skill([larceny], "W", "Cut Purse")
    create_skill([larceny, assassination], "V", "Climb")
    create_skill([larceny, espionage], "G", "Disguise")
    create_skill([larceny, artifice, espionage, pathfinder_artificer], "W", "Pick Lock")
    create_skill([larceny], "W", "Valuation")
    create_skill([larceny, assassination, espionage, pathfinding], "W", "Move Stealthily")
    create_skill([larceny, trading, espionage, performance, administration], "G", "Bluff")
    create_skill([larceny, espionage], "G", "Underworld Connections")
    create_skill([larceny, espionage, assassination, pathfinding], "W", "Awareness - Urban")

    create_skill([hunting, shamanism, pathfinding], "W", "Tracking")
    create_skill([hunting, shamanism, pathfinding], "W", "Stalking - Wilderness")
    create_skill([hunting, shamanism, pathfinding], "W", "Awareness - Wilderness")
    create_skill([hunting, artifice, pathfinder_artificer], "W", "Traps")
    create_skill([hunting], "W", "Navigation")
    create_skill([hunting, scholarship, shamanism], "W", "Herb Lore")
    create_skill([hunting, boat_craft, shamanism], "W", "Weather Lore")
    create_skill([hunting], "V", "Butchery")

    create_skill([espionage, assassination, pathfinding], "W", "Stalking - Urban")
    create_skill([espionage, trading, administration], "G", "Persuade")
    create_skill([espionage, ministry, performance, nobility, administration], "G", "Etiquette")
    create_skill([espionage, scholarship, trading, nobility, nobility, administration], "W", "Languages")
    create_skill([espionage, scholarship, pathfinder_artificer, ministry, nobility, administration], "W", "Literacy")
    create_skill([espionage, scholarship], "W", "Map Making")
    create_skill([espionage], "W", "Search")
    create_skill([espionage, assassination], "W", "Poisons")
    create_skill([espionage, administration], "W", "Forgery")

    create_skill([boat_craft], "V", "Sailing")
    create_skill([boat_craft], "V", "Rowing")
    create_skill([boat_craft, pathfinding], "W", "Sea Navigation")
    create_skill([boat_craft, artifice, pathfinder_artificer], "W", "Rope Craft")
    create_skill([boat_craft], "W", "Boat Building")
    create_skill([boat_craft], "V", "Swimming")
    create_skill([boat_craft], "W", "Boat Purchase")

    create_skill([assassination], "V", "Garrotte")

    create_skill([horsemanship, nobility], "V", "Riding")
    create_skill([horsemanship], "W", "Horse Care")
    create_skill([horsemanship], "W", "Horse Purchase")

    create_skill([performance, command, shamanism, trading, ministry, nobility], "G", "Oratory")
    create_skill([performance], "V", "Juggling")
    create_skill([performance], "V", "Acrobatics")
    create_skill([performance], "W", "Musical instrument")
    create_skill([performance], "W", "Legerdemain")
    create_skill([performance], "G", "Disguise")

    create_skill([command, ministry, nobility], "G", "Leadership")
    create_skill([command, artifice, pathfinder_artificer], "W", "Siege Craft")
    create_skill([command], "W", "Drill")
    create_skill([command, trading, administration], "W", "Logistics")
    create_skill([command], "W", "Awareness - Military")

    create_skill([scholarship, pathfinder_artificer, ministry], "W", "Dead Languages")
    create_skill([scholarship, shamanism, pathfinder_artificer, ministry], "W", "Magic Lore")
    create_skill([scholarship, shamanism], "W", "Healing")
    create_skill([scholarship, artifice, pathfinder_artificer], "W", "Alchemy")
    create_skill([scholarship], "W", "History")
    
    create_skill([shamanism, pathfinding], "W", "Land Navigation")
    create_skill([shamanism], "W", "Animal Lore")

    create_skill([unarmed_combat], "V", "Strike")
    create_skill([unarmed_combat], "V", "Grapple")

    create_skill([artifice, pathfinder_artificer], "W", "Carpentry")
    create_skill([artifice, pathfinder_artificer], "W", "Metal Working")
    create_skill([artifice, pathfinder_artificer], "W", "Stone Masonry")
    create_skill([artifice, pathfinder_artificer], "W", "Armourer")

    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")
    create_skill([pathfinding], "W", "Talent")

    create_skill([pathfinder_artificer], "W", "Talent")

    create_skill([trading], "W", "Assess Value")
    create_skill([trading], "W", "Purchase Anything")
    create_skill([trading], "G", "Trade Connections")

    create_skill([ministry], "G", "Rituals")

    create_skill([nobility], "W", "Arts")
    create_skill([nobility], "G", "Diplomacy")
    create_skill([nobility], "G", "Noble Connections")

    create_skill([administration], "G", "Administrative Connections")

    weapons = {}
    def create_weapon(name,two_handed,defence=None,
               close_min=None,close_max=None,close_slash=None,close_crush=None,close_pierce=None,close_initiative=None,melee_skill=None,
               range_min=None,range_max=None,range_slash=None,range_crush=None,range_pierce=None,range_initiative=None,accuracy_skill=None,
               range_opt=None):
        
        weapons[name] = Weapon(name=name,two_handed=two_handed,defence=defence,
               close_min=close_min,
               close_max=close_max,
               close_slash=close_slash,
               close_crush=close_crush,
               close_pierce=close_pierce,
               close_initiative=close_initiative,
               melee_skill=melee_skill, 
               range_min=range_min,
               range_max=range_max,
               range_slash=range_slash,
               range_crush=range_crush,
               range_pierce=range_pierce,
               range_initiative=range_initiative,
               accuracy_skill=accuracy_skill,
               range_opt=range_opt)

    create_weapon("Stone",two_handed=0,
           range_min=5,range_max=80,             range_crush=0                ,range_initiative=11,accuracy_skill=accuracy)
    
    create_weapon("Stick",two_handed=0,defence=0,
           close_min=2,close_max=6              ,close_crush=-1               ,close_initiative=4 ,melee_skill=melee)

    create_weapon("Dagger",two_handed=0,defence=0,
           close_min=0,close_max=3 ,close_slash=0              ,close_pierce=1,close_initiative=1 ,melee_skill=daggers,
           range_min=5,range_max=80,             range_crush=-1,range_pierce=0,range_initiative=11,accuracy_skill=thrown_daggers)

    create_weapon("Knife",two_handed=0,defence=0,
           close_min=0,close_max=2,close_slash=0               ,close_pierce=0,close_initiative=0 ,melee_skill=melee)

    create_weapon("Short Sword",two_handed=0,defence=1,
           close_min=1,close_max=5,close_slash=1,close_crush=0 ,close_pierce=1,close_initiative=2 ,melee_skill=light_swords)

    create_weapon("Long Sword",two_handed=0,defence=1,
           close_min=2,close_max=6,close_slash=1,close_crush=1 ,close_pierce=1,close_initiative=3 ,melee_skill=heavy_swords)

    create_weapon("Rapier",two_handed=0,defence=1,
           close_min=2,close_max=6,close_slash=0               ,close_pierce=1,close_initiative=4 ,melee_skill=light_swords)
    
    create_weapon("Sabre",two_handed=0,defence=1,
           close_min=2,close_max=6,close_slash=1,close_crush=0 ,close_pierce=1,close_initiative=4 ,melee_skill=light_swords)
    
    create_weapon("Cutlass",two_handed=0,defence=1,
           close_min=2,close_max=6,close_slash=1,close_crush=0 ,close_pierce=1,close_initiative=4 ,melee_skill=light_swords)
    
    create_weapon("Bastard Sword",two_handed=1,defence=0,
           close_min=3,close_max=7,close_slash=1,close_crush=1 ,close_pierce=1,close_initiative=4 ,melee_skill=heavy_swords)
    
    create_weapon("Two Handed Sword",two_handed=2,defence=0,
           close_min=3,close_max=8,close_slash=2,close_crush=2 ,close_pierce=1,close_initiative=4 ,melee_skill=heavy_swords)
    
    create_weapon("Fransisca",two_handed=0,defence=0,
           close_min=2,close_max=5,close_slash=2,close_crush=1                ,close_initiative=2 ,melee_skill=axes,
           range_min=5,range_max=80,             range_crush=1 ,range_pierce=1,range_initiative=10,accuracy_skill=thrown_fransiscas)
    
    create_weapon("Battle Axe",two_handed=1,defence=0,
           close_min=3,close_max=5,close_slash=2,close_crush=1                ,close_initiative=2 ,melee_skill=axes)
    
    create_weapon("Great Axe",two_handed=2,defence=-1,
           close_min=3,close_max=6,close_slash=3,close_crush=2                ,close_initiative=2 ,melee_skill=axes)
    
    create_weapon("Club",two_handed=0,defence=0,
           close_min=2,close_max=5              ,close_crush=1                ,close_initiative=2 ,melee_skill=concussion_weapons)
    
    create_weapon("War Hammer",two_handed=0,defence=0,
           close_min=2,close_max=5              ,close_crush=2 ,close_pierce=1,close_initiative=1 ,melee_skill=concussion_weapons)
    
    create_weapon("Mace",two_handed=0,defence=0,
           close_min=2,close_max=5              ,close_crush=2                ,close_initiative=1 ,melee_skill=concussion_weapons)
    
    create_weapon("Flail",two_handed=0,defence=0,
           close_min=4,close_max=6              ,close_crush=2                ,close_initiative=2 ,melee_skill=flails)
    
    create_weapon("Staff",two_handed=1,defence=1,
           close_min=1,close_max=7              ,close_crush=0                ,close_initiative=4 ,melee_skill=concussion_weapons)

    create_weapon("Spear 6'",two_handed=0,defence=0,
           close_min=2,close_max=8              ,close_crush=-1,close_pierce=1,close_initiative=6 ,melee_skill=spears,
           range_min=5,range_max=80                            ,range_pierce=1,range_initiative=10,accuracy_skill=thrown_spears,
           range_opt=20)

    create_weapon("Spear 8'",two_handed=0,defence=0,
           close_min=3,close_max=10             ,close_crush=-1,close_pierce=1,close_initiative=8 ,melee_skill=long_spears)

    create_weapon("Mounted Lance",two_handed=0,defence=0,
           close_min=6,close_max=12             ,close_crush=0 ,close_pierce=3,close_initiative=8 ,melee_skill=lances)

    create_weapon("Javelin",two_handed=0,defence=0,
           close_min=2,close_max=8                             ,close_pierce=1,close_initiative=6 ,melee_skill=spears,
           range_min=5,range_max=160                           ,range_pierce=1,range_initiative=10,accuracy_skill=thrown_spears,
           range_opt=20)

    create_weapon("Sling",two_handed=0,defence=0,
           range_min=5,range_max=320            ,range_crush=1,range_pierce=0,range_initiative=9 ,accuracy_skill=slings,
           range_opt=80)

    create_weapon("Short Bow",two_handed=2,defence=0,
           range_min=5,range_max=620                          ,range_pierce=2,range_initiative=11,accuracy_skill=bows,
           range_opt=201)

    create_weapon("Long Bow",two_handed=2,defence=0,
           range_min=5,range_max=700                          ,range_pierce=2,range_initiative=11,accuracy_skill=bows,
           range_opt=201)

    create_weapon("Crossbow",two_handed=2,defence=0,
           range_min=3,range_max=600                          ,range_pierce=2,range_initiative=11,accuracy_skill=crossbows,
           range_opt=301)

    create_weapon("Halberd",two_handed=2,defence=1,
           close_min=3,close_max=10,close_slash=2,close_crush=1,close_pierce=1,close_initiative=7 ,melee_skill=polearms)

    create_weapon("Targe",two_handed=0,defence=1,
           close_min=0,close_max=2              ,close_crush=-1              ,close_initiative=-1,melee_skill=shields)

    create_weapon("Medium Shield",two_handed=0,defence=2,
           close_min=0,close_max=2              ,close_crush=-1              ,close_initiative=-1,melee_skill=shields)

    create_weapon("Large Shield",two_handed=0,defence=3,
           close_min=0,close_max=2              ,close_crush=-1              ,close_initiative=-1,melee_skill=shields)

    create_weapon("Hand Strike",two_handed=0,defence=0,
           close_min=0,close_max=2              ,close_crush=-1              ,close_initiative=0 ,melee_skill=melee)

    create_weapon("Foot Strike",two_handed=0,
           close_min=0,close_max=3              ,close_crush=-1              ,close_initiative=0 ,melee_skill=melee)

    create_weapon("Head Butt",two_handed=0,
           close_min=0,close_max=1              ,close_crush=-1              ,close_initiative=-1,melee_skill=melee)
    
    return StaticModel(armour, professions, skills, weapons)

static_model = None

def get():
    global static_model
    if (static_model == None):
        static_model = create_static_model()
    return static_model 
    
