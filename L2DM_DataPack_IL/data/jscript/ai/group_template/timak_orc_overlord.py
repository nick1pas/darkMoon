import sys
from net.sf.l2j.util import Rnd
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest
from net.sf.l2j.gameserver.serverpackets import CreatureSay
from net.sf.l2j.gameserver.serverpackets import PlaySound

# timak_orc_overlord
class timak_orc_overlord(JQuest) :

    # init function.  Add in here variables that you'd like to be inherited by subclasses (if any)
    def __init__(self,id,name,descr):
        self.timak_orc_overlord = 20588
        self.FirstAttacked = False
        # finally, don't forget to call the parent constructor to prepare the event triggering
        # mechanisms etc.
        JQuest.__init__(self,id,name,descr)

    def onAttack (self,npc,player,damage,isPet):
        objId=npc.getObjectId()
        if self.FirstAttacked:
           if Rnd.get(50) : return
           npc.broadcastPacket(CreatureSay(objId,0,"Timak Orc Overlord","Dear ultimate power!!!"))
        else :
           self.FirstAttacked = True
        return 

    def onKill (self,npc,player,isPet):
        npcId = npc.getNpcId()
        if npcId == self.timak_orc_overlord:
            objId=npc.getObjectId()
            self.FirstAttacked = False
        elif self.FirstAttacked :
            self.addSpawn(npcId,npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),True,0)
        return 

# now call the constructor (starts up the ai)
QUEST		= timak_orc_overlord(-1,"timak_orc_overlord","ai")

QUEST.addKillId(QUEST.timak_orc_overlord)
QUEST.addAttackId(QUEST.timak_orc_overlord)

