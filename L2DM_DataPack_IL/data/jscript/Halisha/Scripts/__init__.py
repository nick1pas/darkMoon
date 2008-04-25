__all__ = [
'hali',
'halispwner',
'spawn1',
'spawn2',
'spawn3',
'spawn4',
'spawn5',
]

for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "HalishaManager: Failed To Import Jython script: ",name

#NecroLorD