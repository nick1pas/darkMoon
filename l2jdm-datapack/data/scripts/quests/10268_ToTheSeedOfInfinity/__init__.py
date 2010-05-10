# Made by Kerberos v1.0 on 2009/05/1
# this script is part of the Official L2J Datapack Project.
# Visit http://www.l2jdp.com/forum for more details.

import sys

from com.l2jfree.gameserver.model.quest import State
from com.l2jfree.gameserver.model.quest import QuestState
from com.l2jfree.gameserver.model.quest import Quest as JQuest

qn = "10268_ToTheSeedOfInfinity"

#NPCs
Keucereus = 32548
Tepios = 32530

#items
Introduction = 13811

class Quest (JQuest) :
    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [Introduction]

    def onAdvEvent (self,event,npc, player) :
        htmltext = event
        st = player.getQuestState(qn)
        if not st : return
        if event == "32548-05.htm" :
            st.set("cond","1")
            st.setState(State.STARTED)
            st.playSound("ItemSound.quest_accept")
            st.giveItems(Introduction,1)
        return htmltext

    def onTalk (self,npc,player):
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
        cond = st.getInt("cond")
        if id == State.COMPLETED :
            if npcId == Tepios :
                htmltext = "32530-02.htm" 
            else:
                htmltext = "32548-0a.htm"
        elif id == State.CREATED and npcId == Keucereus:
            if player.getLevel() < 75 :
                htmltext = "32548-00.htm"
            else :
                htmltext = "32548-01.htm"
        elif id == State.STARTED and npcId == Keucereus:
            htmltext = "32548-06.htm"
        elif id == State.STARTED and npcId == Tepios:
            htmltext = "32530-01.htm"
            st.rewardItems(57,16671)
            st.addExpAndSp(100640,10098)
            st.unset("cond")
            st.exitQuest(False)
            st.playSound("ItemSound.quest_finish")
        return htmltext

QUEST       = Quest(10268,qn,"To the Seed of Infinity")

QUEST.addStartNpc(Keucereus)

QUEST.addTalkId(Keucereus)
QUEST.addTalkId(Tepios)
