import sys
from com.l2jfree.tools.random import Rnd
from com.l2jfree.gameserver.model.quest.jython import QuestJython as JQuest
from com.l2jfree.gameserver.network.serverpackets import NpcSay

# timak_orc_overlord
class timak_orc_overlord(JQuest) :

    # init function.  Add in here variables that you'd like to be inherited by subclasses (if any)
    def __init__(self, id, name, descr) :
        self.timak_orc_overlord = 20588
        self.FirstAttacked = False
        # finally, don't forget to call the parent constructor to prepare the event triggering
        # mechanisms etc.
        JQuest.__init__(self, id, name, descr)

    def onAttack (self, npc, player, damage, isPet, skill) :
        objId = npc.getObjectId()
        if self.FirstAttacked:
           if Rnd.get(50) : return
           npc.broadcastPacket(NpcSay(objId, 0, npc.getNpcId(), "Dear ultimate power!!!"))
        else :
           self.FirstAttacked = True
        return 

    def onKill (self, npc, player, isPet) :
        npcId = npc.getNpcId()
        if npcId == self.timak_orc_overlord :
            objId = npc.getObjectId()
            self.FirstAttacked = False
        elif self.FirstAttacked :
            self.addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 0)
        return 

QUEST = timak_orc_overlord(-1, "timak_orc_overlord", "ai")

QUEST.addKillId(QUEST.timak_orc_overlord)

QUEST.addAttackId(QUEST.timak_orc_overlord)