import sys
from com.l2jfree.gameserver.model.quest.jython import QuestJython as JQuest
from com.l2jfree.gameserver.network.serverpackets import NpcSay
from com.l2jfree.tools.random import Rnd

# delu_lizardman_special_agent
class delu_lizardman_special_agent(JQuest) :

    # init function.  Add in here variables that you'd like to be inherited by subclasses (if any)
    def __init__(self, id, name, descr):
        self.delu_lizardman_special_agent = 21105
        self.FirstAttacked = False
        # finally, don't forget to call the parent constructor to prepare the event triggering
        # mechanisms etc.
        JQuest.__init__(self, id, name, descr)

    def onAttack (self, npc, player, damage, isPet, skill) :
        objId = npc.getObjectId()
        if self.FirstAttacked :
           if Rnd.get(40) : return
           npc.broadcastPacket(NpcSay(objId, 0, npc.getNpcId(), "Hey! Were having a duel here!"))
        else :
           self.FirstAttacked = True
           npc.broadcastPacket(NpcSay(objId, 0, npc.getNpcId(), "How dare you interrupt our fight! Hey guys, help!"))
        return 

    def onKill (self, npc, player, isPet) :
        npcId = npc.getNpcId()
        if npcId == self.delu_lizardman_special_agent :
            objId=npc.getObjectId()
            self.FirstAttacked = False
        elif self.FirstAttacked :
            self.addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 0)
        return 

QUEST = delu_lizardman_special_agent(-1, "delu_lizardman_special_agent", "ai")

QUEST.addKillId(QUEST.delu_lizardman_special_agent)

QUEST.addAttackId(QUEST.delu_lizardman_special_agent)