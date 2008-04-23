__all__ = [
'1001_dual_swords',
'1002_unseal',
'1003_reseal',
'1004_create',
'1005_unseal',
'1007_enhance',
'1008_enhance_mammon',
'1009_remove_mammon',
'1010_exchange',
'1011_enhance_mammon_s', 
'1012_upgrade' 
]
print ""
print "BlackSmithManager: Initializing BlackSmith Data Please Wait..."
for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "BlackSmithManager: Failed To Import BlackSmith Data: ",name
print "BlackSmithManager: All Blacksmith Data Load Completed!"
print ""
