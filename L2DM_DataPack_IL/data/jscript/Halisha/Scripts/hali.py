import sys
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest
from net.sf.l2j.gameserver.serverpackets import PlaySound
from net.sf.l2j.gameserver.ai import CtrlIntention

# Boss: frin
class frin(JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.FirstAttacked = False

    def onAttack (self,npc,player,damage,isPet):
        objId=npc.getObjectId()
        if self.FirstAttacked == False:
           self.addSpawn(29050,172674,-87178,-5107,0,False,9000000)
           self.addSpawn(29050,172602,-88686,-5107,0,False,9000000)
           self.addSpawn(29050,178569,-88693,-5107,0,False,9000000)
           self.addSpawn(29050,175842,-88687,-5107,0,False,9000000)
           self.FirstAttacked = True
           npc.broadcastPacket(PlaySound(1, "b08_s01", 1, objId, npc.getX(), npc.getY(), npc.getZ()))
        return 

    def onKill (self,npc,player,isPet):
        npcId = npc.getNpcId()
        if npcId == 29046:
            objId=npc.getObjectId()
            npc.broadcastPacket(PlaySound(1, "b08_s01", 1, objId, npc.getX(), npc.getY(), npc.getZ()))
            self.addSpawn(29051,172674,-87178,-5107,0,False,9000000)
            self.addSpawn(29051,172602,-88686,-5107,0,False,9000000)
            self.addSpawn(29051,178569,-88693,-5107,0,False,9000000)
            self.addSpawn(29051,175842,-88687,-5107,0,False,9000000)
            self.addSpawn(29047,npc.getX(), npc.getY(), npc.getZ(),0,False,9000000)
            return 

# now call the constructor (starts up the ai)
QUEST		= frin(-1,"frin","ai")


QUEST.addAttackId(29045)
QUEST.addKillId(29046)
print "AI: Halisha AI:"
print "Halisha AI -> mainscrpipt..."
print "................. inicialized!"

