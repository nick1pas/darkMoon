import sys
from net.sf.l2j.util import Rnd
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest
from net.sf.l2j.gameserver.serverpackets import CreatureSay
from net.sf.l2j.gameserver.serverpackets import PlaySound

# ol_mahum_general
class ol_mahum_general(JQuest) :

    # init function.  Add in here variables that you'd like to be inherited by subclasses (if any)
    def __init__(self,id,name,descr):
        self.ol_mahum_general = 20438
        self.FirstAttacked = False
        # finally, don't forget to call the parent constructor to prepare the event triggering
        # mechanisms etc.
        JQuest.__init__(self,id,name,descr)

    def onAttack (self,npc,player,damage,isPet):
        objId=npc.getObjectId()
        if self.FirstAttacked:
           if Rnd.get(100) : return
           npc.broadcastPacket(CreatureSay(objId,0,"Ol Mahum General","We shall see about that!."))
        else :
           self.FirstAttacked = True
           npc.broadcastPacket(CreatureSay(objId,0,"Ol Mahum General","I will definitely repey this humiliation!."))
        return 

    def onKill (self,npc,player,isPet):
        npcId = npc.getNpcId()
        if npcId == self.ol_mahum_general:
            objId=npc.getObjectId()
            self.FirstAttacked = False
        elif self.FirstAttacked :
            self.addSpawn(npcId,npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),True,0)
        return 

# now call the constructor (starts up the ai)
QUEST		= ol_mahum_general(-1,"ol_mahum_general","ai")

QUEST.addKillId(QUEST.ol_mahum_general)
QUEST.addAttackId(QUEST.ol_mahum_general)

