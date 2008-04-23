__all__ = [
'core',
'antharas',
'baium',
'valakas',
'sailren',
'vanhalter',
'drchaos',
'Benom'
]

for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "AIManager: Failed To Import Individual Data: ",name