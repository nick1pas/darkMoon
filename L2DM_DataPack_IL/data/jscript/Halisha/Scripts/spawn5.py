import sys
from net.sf.l2j.gameserver.model.quest.jython   import QuestJython as JQuest
from net.sf.l2j.gameserver.ai                   import CtrlIntention
from net.sf.l2j.gameserver.lib import Rnd


class frinmnde(JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)

    def onAttack (self,npc,player,damage,isPet) :
       npcId = npc.getNpcId()
       if npcId == 29045 :
         if Rnd.get(100) <= 2 :
             newNpc = self.addSpawn(29051,174238,-88007,-5114,0,False,9000000)
             newNpc.setRunning()
             newNpc.addDamageHate(player,0,9999)
             newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)

QUEST      = frinmnde(-1,"frinmnde","ai")
QUEST.addAttackId(29045)
QUEST.addAttackId(29051)
print "MinionController -> Spawn 5:"
print "................. iniciated!"
print "MinionController: spawnpoints loaded!"

