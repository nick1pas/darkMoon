# -------------------
# Create by Skeleton!
# -------------------
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "661_MakingTheHarvestGroundsSafe"

# NPC
NORMAN = 30210

# MOBS
GIANT_POISON_BEE = 21095
CLOYDY_BEAST = 21096
YOUNG_ARANEID = 21097

#QUEST ITEMS
STING_OF_GIANT_POISON = 8283
TALON_OF_YOUNG_ARANEID = 8285
CLOUDY_GEM = 8284
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30210-03.htm" :
       st.setState(STARTED)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
    elif event == "30210-09.htm" :
       st.setState(STARTED)
       st.set("cond","1")
       st.playSound("ItemSound.quest_accept")
    elif event == "30210-08.htm" :
      STING=st.getQuestItemsCount(STING_OF_GIANT_POISON)
      TALON=st.getQuestItemsCount(TALON_OF_YOUNG_ARANEID)
      GEM=st.getQuestItemsCount(CLOUDY_GEM)
      if STING+GEM+TALON >= 10 :
         st.giveItems(ADENA, STING*50+GEM*60+TALON*70+2800)
         st.takeItems(STING_OF_GIANT_POISON,STING)
         st.takeItems(TALON_OF_YOUNG_ARANEID,TALON)
         st.takeItems(CLOUDY_GEM,GEM)
      else :
         st.giveItems(ADENA,STING*50+GEM*60+TALON*70)
         st.takeItems(STING_OF_GIANT_POISON,STING)
         st.takeItems(TALON_OF_YOUNG_ARANEID,TALON)
         st.takeItems(CLOUDY_GEM,GEM)
      st.playSound("ItemSound.quest_middle")
    elif event == "30210-06.htm" :
       st.exitQuest(1)
       st.playSound("ItemSound.quest_finish")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria</body></html>"
   st = player.getQuestState(qn)
   if not st : return

   npcId=npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   if npcId == NORMAN and id == CREATED :
      if st.getPlayer().getLevel() >= 21 :
         htmltext = "30210-02.htm"
         return htmltext
      elif st.getPlayer().getLevel() < 21 :
         htmltext = "30210-01.htm"
         st.exitQuest(1)
   elif npcId == NORMAN and cond == 1 :
      S=st.getQuestItemsCount(STING_OF_GIANT_POISON)
      T=st.getQuestItemsCount(TALON_OF_YOUNG_ARANEID)
      C=st.getQuestItemsCount(CLOUDY_GEM)
      if S+T+C == 0 :
         htmltext = "30210-04.htm"
      elif S+T+C >= 0 :
         htmltext = "30210-05.htm"
   return htmltext

 def onKill (self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return

   npcId = npc.getNpcId()
   chance = st.getRandom(100)
   if st.getInt("cond") == 1 :
     if npcId == GIANT_POISON_BEE and chance < 75 :
       st.giveItems(STING_OF_GIANT_POISON,1)
       st.playSound("ItemSound.quest_itemget")
     elif npcId == CLOYDY_BEAST and chance < 71 :
       st.giveItems(CLOUDY_GEM,1)
       st.playSound("ItemSound.quest_itemget")
     elif npcId == YOUNG_ARANEID and chance < 67 :
       st.giveItems(TALON_OF_YOUNG_ARANEID,1)
       st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(661,qn,"Making The Harvest Grounds Safe")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(NORMAN)
QUEST.addTalkId(NORMAN)

QUEST.addKillId(GIANT_POISON_BEE)
QUEST.addKillId(CLOYDY_BEAST)
QUEST.addKillId(YOUNG_ARANEID)

# L2Emu Project