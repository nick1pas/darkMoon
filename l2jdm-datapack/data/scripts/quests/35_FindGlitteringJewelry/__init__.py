# Made by disKret
import sys
from com.l2jfree.tools.random import Rnd
from com.l2jfree.gameserver.model.quest import State
from com.l2jfree.gameserver.model.quest import QuestState
from com.l2jfree.gameserver.model.quest.jython import QuestJython as JQuest

qn = "35_FindGlitteringJewelry"

#ITEM
ROUGH_JEWEL   = 7162
ORIHARUKON    = 1893
SILVER_NUGGET = 1873
THONS         = 4044

#REWARD
JEWEL_BOX = 7077

#NPC
ELLIE  = 30091
FELTON = 30879

#MOB
ALLIGATOR = 20135

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [ROUGH_JEWEL]

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond")
   if event == "30091-1.htm" and cond == 0 :
     st.set("cond","1")
     st.setState(State.STARTED)
     st.playSound("ItemSound.quest_accept")
   if event == "30879-1.htm" and cond == 1:
     st.set("cond","2")
   if event == "30091-3.htm" and cond == 3:
     st.takeItems(ROUGH_JEWEL,10)
     st.set("cond","4")
   if event == "30091-5.htm" and cond == 4:
     if st.getQuestItemsCount(ORIHARUKON) >= 5 and st.getQuestItemsCount(SILVER_NUGGET) >= 500 and st.getQuestItemsCount(THONS) >= 150 :
       st.takeItems(ORIHARUKON,5)
       st.takeItems(SILVER_NUGGET,500)
       st.takeItems(THONS,150)
       st.giveItems(JEWEL_BOX,1)
       st.playSound("ItemSound.quest_finish")
       st.unset("cond")
       st.exitQuest(False)
     else :
       htmltext = "You don't have enough materials"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext
   npcId = npc.getNpcId()
   cond = st.getInt("cond")
   id = st.getState()
   if id == State.COMPLETED:
     htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif npcId == ELLIE and cond == 0 and st.getQuestItemsCount(JEWEL_BOX) == 0 :
     fwear=player.getQuestState("37_PleaseMakeMeFormalWear")
     if fwear :
       if fwear.get("cond") == "6" :
         htmltext = "30091-0.htm"
         return htmltext
       else:
         st.exitQuest(1)
     else:
       st.exitQuest(1)
   elif npcId == FELTON and cond == 1 :
     htmltext = "30879-0.htm"
   elif id == State.STARTED :  
       if npcId == ELLIE and st.getQuestItemsCount(ROUGH_JEWEL) == 10 :
         htmltext = "30091-2.htm"
       elif npcId == ELLIE and cond == 4 and st.getQuestItemsCount(ORIHARUKON) >= 5 and st.getQuestItemsCount(SILVER_NUGGET) >= 500 and st.getQuestItemsCount(THONS) >= 150 :
         htmltext = "30091-4.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember1 = self.getRandomPartyMember(player,"1")
   partyMember2 = self.getRandomPartyMember(player,"2")
   partyMember = partyMember1 # initialize
   if not partyMember1 and not partyMember2: return
   elif not partyMember2 : partyMember = partyMember1
   elif not partyMember1 : partyMember = partyMember2
   else :
       if Rnd.get(2): partyMember = partyMember2
   
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   if not st : return 
   if st.getState() != State.STARTED : return   
   count = st.getQuestItemsCount(ROUGH_JEWEL)
   if count<10 :
     st.giveItems(ROUGH_JEWEL,int(1))
     if count == 9 :
       st.playSound("ItemSound.quest_middle")
       st.set("cond","3")
     else:
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(35,qn,"Find Glittering Jewelry")

QUEST.addStartNpc(ELLIE)
QUEST.addTalkId(ELLIE)

QUEST.addTalkId(FELTON)
QUEST.addKillId(ALLIGATOR)
