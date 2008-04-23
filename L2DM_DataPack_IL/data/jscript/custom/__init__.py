__all__ = [
#L2EMU_ADD
#'708_ZakenCurse',
#L2EMU_ADD
#'998_christmasgift',
'4000_ShadowWeapons',
#'5007_santa',
#'5011_l2day',
'6050_KetraOrcSupport',
'6051_VarkaSilenosSupport',
#L2Sh'T_ADD
'6666_ChaliceToHarmonies',
'7000_HeroItems',
#Dark-Moon_ADD
'7779_LazyNooblez',
'8000_RaidbossInfo'
]
print ""
print "CustomManager: Initializing Custom Data Please Wait..."
for name in __all__ :
    try :
        __import__(name,globals(), locals(), [], -1)
    except :
        print "CustomManager: Failed To Import Custom Data: ",name
print "CustomManager: All Custom Data Load Complete."
print ""
