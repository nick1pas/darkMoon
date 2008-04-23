__all__ = [
'1100_teleport_with_charm',
'1101_teleport_to_race_track',
'1102_toivortex_green',
'1102_toivortex_blue',
'1102_toivortex_red',
'1103_OracleTeleport',
'1104_NewbieTravelToken',
'1630_PaganTeleporters',
'2000_NoblesseTeleport',
'2211_HuntingGroundsTeleport',
'2400_toivortex_exit',
'6111_ElrokiTeleporters'
]
print ""
print "TeleportManager: Initializing Teleport Data Please Wait..."
for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "TeleportManager: Failed To Import Teleport Data: ",name
print "TeleportManager: All Teleport Data Load Complete"
print ""
