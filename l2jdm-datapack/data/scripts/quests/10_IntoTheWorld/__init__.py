# Created by CubicVirtuoso
# Any problems feel free to drop by #l2j-datapack on irc.freenode.net
import sys
from com.l2jfree import Config
from com.l2jfree.gameserver.model.quest import State
from com.l2jfree.gameserver.model.quest import QuestState
from com.l2jfree.gameserver.model.quest.jython import QuestJython as JQuest

qn = "10_IntoTheWorld"

#NPCs
BALANKI = 30533
REED    = 30520
GERALD  = 30650

#ITEM
VERY_EXPENSIVE_NECKLACE = 7574

#REWARDS
SCROLL_OF_ESCAPE_GIRAN = 7559
MARK_OF_TRAVELER = 7570

class Quest (JQuest) :

    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        self.questItemIds = [VERY_EXPENSIVE_NECKLACE]

    def onAdvEvent (self,event,npc, player) :
        htmltext = event
        st = player.getQuestState(qn)
        if not st : return
        if event == "30533-03.htm" :
            st.set("cond","1")
            st.setState(State.STARTED)
            st.playSound("ItemSound.quest_accept")
        elif event == "30520-02.htm" :
            st.set("cond","2")
            st.giveItems(VERY_EXPENSIVE_NECKLACE,1)
        elif event == "30650-02.htm" :
            st.set("cond","3")
            st.takeItems(VERY_EXPENSIVE_NECKLACE,1)
        elif event == "30533-06.htm" :
            st.rewardItems(SCROLL_OF_ESCAPE_GIRAN,1)
            st.giveItems(MARK_OF_TRAVELER,1)
            st.unset("cond")
            st.exitQuest(False)
            st.playSound("ItemSound.quest_finish")
        return htmltext

    def onTalk (self,npc,player):
        npcId = npc.getNpcId()
        htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
        st = player.getQuestState(qn)
        if not st: return htmltext
        id = st.getState()
        if npcId == 30533 and id == State.COMPLETED :
            htmltext = "<html><body>I can't supply you with another Giran Scroll of Escape. Sorry traveller.</body></html>"
        elif id == State.CREATED :
            if player.getRace().ordinal() == 4 :
                if player.getLevel() >= 3 and player.getLevel() <= 10 :
                    htmltext = "30533-02.htm"
                else :
                    htmltext = "30533-01.htm"
                    st.exitQuest(1)
        elif id == State.STARTED:
            if npcId == BALANKI and st.getInt("cond") == 1 :
                htmltext = "30533-04.htm"
            elif npcId == REED and st.getInt("cond") == 3 :
                htmltext = "30520-04.htm"
                st.set("cond","4")
            elif npcId == REED and st.getInt("cond") :
                if st.getQuestItemsCount(VERY_EXPENSIVE_NECKLACE) == 0 :
                    htmltext = "30520-01.htm"
                else :
                    htmltext = "30520-03.htm"
            elif npcId == GERALD and st.getInt("cond")== 2 :
                if st.getQuestItemsCount(VERY_EXPENSIVE_NECKLACE) :
                    htmltext = "30650-01.htm"
            elif npcId == BALANKI and st.getInt("cond")== 4 :
                htmltext = "30533-05.htm"
        return htmltext

QUEST       = Quest(10,qn,"Into The World")

QUEST.addStartNpc(BALANKI)

QUEST.addTalkId(BALANKI)
QUEST.addTalkId(REED)
QUEST.addTalkId(GERALD)
