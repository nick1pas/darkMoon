# Made by disKret
import sys
from com.l2jfree.gameserver.model.quest import State
from com.l2jfree.gameserver.model.quest import QuestState
from com.l2jfree.gameserver.model.quest.jython import QuestJython as JQuest

qn = "37_PleaseMakeMeFormalWear"

#NPC
ALEXIS = 30842
LEIKAR = 31520
JEREMY = 31521
MIST   = 31627

#ITEM
MYSTERIOUS_CLOTH = 7076
JEWEL_BOX = 7077
SEWING_KIT = 7078
DRESS_SHOES_BOX = 7113
FORMAL_WEAR = 6408
SIGNET_RING = 7164
ICE_WINE = 7160
BOX_OF_COOKIES = 7159

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if st.getState() != State.COMPLETED :
    if event == "30842-1.htm" and cond == 0:
      st.set("cond","1")
      st.setState(State.STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "31520-1.htm" and cond == 1:
      st.giveItems(SIGNET_RING,1)
      st.set("cond","2")
    elif event == "31521-1.htm" and cond == 2 :
      st.giveItems(ICE_WINE,1)
      st.set("cond","3")
    elif event == "31627-1.htm" and cond == 3:
      if st.getQuestItemsCount(ICE_WINE):
         st.takeItems(ICE_WINE,1)
         st.set("cond","4")
      else:
         htmltext = "You don't have enough materials"
    elif event == "31521-3.htm" and cond == 4:
       st.giveItems(BOX_OF_COOKIES,1)
       st.set("cond","5")
    elif event == "31520-3.htm" and cond == 5:
       st.set("cond","6")
    elif event == "31520-5.htm" and cond == 6:
       if st.getQuestItemsCount(MYSTERIOUS_CLOTH) and st.getQuestItemsCount(JEWEL_BOX) and st.getQuestItemsCount(SEWING_KIT) :
         st.takeItems(MYSTERIOUS_CLOTH,1)
         st.takeItems(JEWEL_BOX,1)
         st.takeItems(SEWING_KIT,1)
         st.set("cond","7")
       else :
         htmltext = "You don't have enough materials"
    elif event == "31520-7.htm" :
       if st.getQuestItemsCount(DRESS_SHOES_BOX) :
         st.takeItems(DRESS_SHOES_BOX,1)
         st.giveItems(FORMAL_WEAR,1)
         st.exitQuest(False)
         st.unset("cond")
         st.playSound("ItemSound.quest_finish")
       else :
         htmltext = "You don't have enough materials"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if npcId == ALEXIS and cond == 0 :
     if id == State.COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
     elif player.getLevel() >= 60 :
       htmltext = "30842-0.htm"
       return htmltext
     else:
       htmltext = "30842-2.htm"
       st.exitQuest(1)
   elif id == State.STARTED :    
       if npcId == LEIKAR and cond == 1 :
         htmltext = "31520-0.htm"
       elif npcId == JEREMY and st.getQuestItemsCount(SIGNET_RING) :
         st.takeItems(SIGNET_RING,1)
         htmltext = "31521-0.htm"
       elif npcId == MIST and st.getQuestItemsCount(ICE_WINE) :
         htmltext = "31627-0.htm"
       elif npcId == JEREMY and cond == 4 :
         htmltext = "31521-2.htm"
       elif npcId == LEIKAR and st.getQuestItemsCount(BOX_OF_COOKIES) :
         st.takeItems(BOX_OF_COOKIES,1)
         htmltext = "31520-2.htm"
       elif npcId == LEIKAR and st.getQuestItemsCount(MYSTERIOUS_CLOTH) and st.getQuestItemsCount(JEWEL_BOX) and st.getQuestItemsCount(SEWING_KIT) :
         htmltext = "31520-4.htm"
       elif npcId == LEIKAR and st.getQuestItemsCount(DRESS_SHOES_BOX) :
         htmltext = "31520-6.htm"
   return htmltext

QUEST       = Quest(37,qn,"Please Make Me Formal Wear")

QUEST.addStartNpc(ALEXIS)

QUEST.addTalkId(ALEXIS)
QUEST.addTalkId(LEIKAR)
QUEST.addTalkId(JEREMY)
QUEST.addTalkId(MIST)
