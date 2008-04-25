import sys
from net.sf.l2j.gameserver.model.quest.jython   import QuestJython as JQuest
from net.sf.l2j.gameserver.ai                   import CtrlIntention


class halishaI(JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.HALISHASPAWNED = False

    def onAttack (self,npc,player,damage,isPet) :
      if self.HALISHASPAWNED == False :
       npcId = npc.getNpcId()
       if npcId == 29045 :
         maxHp = npc.getMaxHp()
         curHp = npc.getStatus().getCurrentHp()
         if (curHp / maxHp) * 100 <= 70 :
             newNpc = self.addSpawn(29046,174238,-88007,-5114,0,False,9000000)
             newNpc.setRunning()
             newNpc.addDamageHate(player,0,9999)
             newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)
             self.HALISHASPAWNED = True

QUEST      = halishaI(-1,"halishaI","ai")
QUEST.addAttackId(29045)
print "Halisha AI -> HaliSpawner..."
print "................. iniciated!"

