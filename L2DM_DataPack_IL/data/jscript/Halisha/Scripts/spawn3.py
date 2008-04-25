import sys
from net.sf.l2j.gameserver.model.quest.jython   import QuestJython as JQuest
from net.sf.l2j.gameserver.ai                   import CtrlIntention
from net.sf.l2j.gameserver.lib import Rnd


class frinmnc(JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)

    def onAttack (self,npc,player,damage,isPet) :
       npcId = npc.getNpcId()
       if npcId == 29045 :
         if Rnd.get(100) <= 2 :
             newNpc = self.addSpawn(29051,175795,-87177,-5107,0,False,9000000)
             newNpc.setRunning()
             newNpc.addDamageHate(player,0,9999)
             newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)

QUEST      = frinmnc(-1,"frinmnc","ai")
QUEST.addAttackId(29045)
QUEST.addAttackId(29051)
print "MinionController -> Spawn 3:"
print "................. iniciated!"

