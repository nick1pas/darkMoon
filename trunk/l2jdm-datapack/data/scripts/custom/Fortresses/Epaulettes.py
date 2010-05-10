#By: Vital
from com.l2jfree.gameserver.model.quest         import State
from com.l2jfree.gameserver.model.quest         import QuestState
from com.l2jfree.gameserver.model.quest.jython  import QuestJython as JQuest
from com.l2jfree.tools.random                   import Rnd

qn = "Epaulettes"

GUARDS = [35684,35720,35753,35789,35822,35853,35889,35922,35958,35996,36029,36065,36103,36136,36167,
          36203,36241,36279,36312,36348,36386,35673,35705,35742,35774,35811,35842,35874,35911,35943,
          35981,36018,36050,36088,36125,36156,36188,36226,36264,36301,36333,36371,35671,35703,35740,
          35772,35809,35840,35872,35909,35941,35979,36016,36048,36086,36123,36154,36186,36224,36262,
          36299,36331,36369,35711,35780,35880,35949,35987,36056,36094,36194,36232,36270,35674,35678,
          35679,35706,35714,35715,35743,35747,35748,35775,35783,35784,35812,35816,35817,35843,35847,
          35848,35875,35883,35884,35912,35916,35917,35944,35952,35953,35982,35990,35991,36019,36023,
          36024,36051,36059,36060,36089,36097,36098,36126,36130,36131,36157,36161,36162,36189,36197,
          36198,36227,36235,36236,36265,36273,36274,36302,36306,36307,36334,36342,36343,36372,36380,
          36381,35682,35718,35751,35787,35820,35851,35887,35920,35956,35994,36027,36063,36101,36134,
          36165,36201,36239,36277,36310,36346,36384,35672,35704,35741,35773,35810,35841,35873,35910,
          35942,35980,36017,36049,36087,36124,36155,36187,36225,36263,36300,36332,36370,35712,35781,
          35881,35950,35988,36057,36095,36195,36233,36271,35681,35717,35750,35786,35819,35850,35886,
          35919,35955,35993,36026,36062,36100,36133,36164,36200,36238,36276,36309,36345,36383]

COMMANDERS = [35670,35677,35680,35683,35702,35713,35716,35719,35721,35722,35739,35746,35749,35752,35771,
              35782,35785,35788,35790,35791,35808,35815,35818,35821,35839,35846,35849,35852,35871,35882,
              35885,35888,35890,35891,35908,35915,35918,35921,35940,35951,35954,35957,35959,35960,35978,
              35989,35992,35995,35997,35998,36015,36022,36025,36028,36047,36058,36061,36064,36066,36067,
              36085,36096,36099,36102,36104,36105,36122,36129,36132,36135,36153,36160,36163,36166,36185,
              36196,36199,36202,36204,36205,36223,36234,36237,36240,36242,36243,36261,36272,36275,36278,
              36280,36281,36298,36305,36308,36311,36330,36341,36344,36347,36349,36350,36368,36379,36382,
              36385,36387,36388]

class Epaulettes (JQuest) :
    def __init__(self,id,name,descr) : 
        JQuest.__init__(self,id,name,descr)

    def onKill(self,npc,player,isPet):
        npcId = npc.getNpcId()
        if npcId in GUARDS :
            npc.dropItem(player,9912,Rnd.get(2,8))
        elif npcId in COMMANDERS :
            npc.dropItem(player,9912,Rnd.get(77,90))

QUEST = Epaulettes(-1,qn,"Custom")
for guard in GUARDS :
    QUEST.addKillId(guard)
for commander in COMMANDERS :
    QUEST.addKillId(commander)