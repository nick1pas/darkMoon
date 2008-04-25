import sys
from net.sf.l2j.gameserver.model.quest.jython   import QuestJython as JQuest
from net.sf.l2j.gameserver.ai                   import CtrlIntention
from net.sf.l2j.gameserver.lib import Rnd


class frinmna(JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)

    def onAttack (self,npc,player,damage,isPet) :
       npcId = npc.getNpcId()
       if npcId == 29045 :
         if Rnd.get(100) <= 2 :
             newNpc = self.addSpawn(29051,172676,-87645,-5107,0,False,9000000)
             newNpc.setRunning()
             newNpc.addDamageHate(player,0,9999)
             newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)

QUEST      = frinmna(-1,"frinmna","ai")
QUEST.addAttackId(29045)
QUEST.addAttackId(29051)
print "Halisha AI -> :"
print "MinionController -> Spawn 1:"
print "................. iniciated!"

