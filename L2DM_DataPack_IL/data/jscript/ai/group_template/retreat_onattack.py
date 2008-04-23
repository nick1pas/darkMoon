import sys
from net.sf.l2j.gameserver.model.quest.jython   import QuestJython as JQuest
from net.sf.l2j.gameserver.model.actor.instance import L2NpcInstance
from net.sf.l2j.gameserver.model                import L2CharPosition
from net.sf.l2j.gameserver.ai                   import CtrlIntention
from net.sf.l2j.gameserver.serverpackets        import CreatureSay
from net.sf.l2j.gameserver.lib                  import Rnd

# flee onAttack (current version is rather fear than retreat)
# ToDo: find a way to check position instead of using a timer to stop fleeing
#       make zones or a list of "basements" for mobs to retreat to

class retreat_onattack(JQuest) :

    # init function.  Add in here variables that you'd like to be inherited by subclasses (if any)
    def __init__(self,id,name,descr):
        self.MobSpawns ={
                20432: {'HP': 100, 'chance': 100}, # Elpy
                20058: {'HP': 50, 'chance': 10} # Ol Mahum Guard
                }
        # made a second dictionary for the texts
        self.MobTexts ={
                20058: ["I'll be back", "You are stronger than expected"] # I don't recall the retail text they say, so I made custom ones to test it
                }
        # finally, don't forget to call the parent constructor to prepare the event triggering
        # mechanisms etc.
        JQuest.__init__(self,id,name,descr)
    def onAdvEvent(self,event,npc,player) :
        if event == "Retreat" and npc and player:
            npc.setIsAfraid(0)
            npc.addDamageHate(player,0,100)
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player)

    def onAttack(self,npc,player,damage,isPet) :
        npcId = npc.getNpcId()
        objId = npc.getObjectId()
        if self.MobSpawns.has_key(npcId) :
            if npc.getStatus().getCurrentHp() <= npc.getMaxHp() * self.MobSpawns[npcId]['HP'] / 100 and Rnd.get(100) < self.MobSpawns[npcId]['chance'] :
                if self.MobTexts.has_key(npcId) :
                    text = self.MobTexts[npcId][Rnd.get(len(self.MobTexts[npcId]))]
                    npcName = npc.getName()
                    npc.broadcastPacket(CreatureSay(objId,0,npcName,text))
                posX = npc.getX()
                posY = npc.getY()
                posZ = npc.getZ()
                signX = -500
                signY = -500
                if npc.getX() > player.getX() :
                    signX = 500
                if npc.getY() > player.getY() :
                    signY = 500
                posX = posX + signX
                posY = posY + signY
                npc.setIsAfraid(1)
                npc.setRunning()
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,L2CharPosition(posX,posY,posZ,0))
                self.startQuestTimer("Retreat", 10000, npc, player)
        return

# now call the constructor (starts up the ai)
QUEST      = retreat_onattack(-1,"retreat_onattack","ai")
for i in QUEST.MobSpawns.keys() :
    QUEST.addAttackId(i)

