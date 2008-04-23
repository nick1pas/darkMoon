__all__ = [
'polymorphing_angel',
'feedable_beasts',
'chests',

'cats_eye_bandit',
'delu_lizardman_special_agent',
'delu_lizardman_special_commander',
'karul_bugbear',
'mutation',
'ol_mahum_general',
'retreat_onattack',
'splendor',
'timak_orc_overlord',
'timak_orc_troop_leader',
'turek_orc_footman',
'turek_orc_supplier',
'turek_orc_warlord'

]

for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "AIManager: Failed To Import Group Template Data: ",name
        