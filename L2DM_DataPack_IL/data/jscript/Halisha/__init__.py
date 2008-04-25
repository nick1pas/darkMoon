__all__ = [
'Scripts'
]
print ""
print "HalishaManager: Importing Scripts Please Wait..."
for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "HalishaManager: Failed To Import script : ",name
print "HalishaManager: inicialization complete"
print "developed by NecroLorD"
print ""

# Shilen's Temple Serer
#This is a temporary version