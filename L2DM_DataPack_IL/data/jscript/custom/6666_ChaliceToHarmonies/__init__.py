# The Chalice to Harmonies - v1 by bloodshed (XoTTa6bI4)
import sys
from net.sf.l2j import Config
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.serverpackets import SocialAction
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "6666_ChaliceToHarmonies"

#NPC
EINHASAD = 57777
NEWYEAR = 31961

#ITEMS
EARTH,FIRE,VETER,SPIRIT,WATER,CHASHA = range(20001,20007)

#MOBS, DROPS, CHANCES & REWARDS
MOB_E,MOB_F,MOB_V,MOB_S,MOB_W = [ 21408,21376,21300,20281,20994 ]
DROPLIST = {MOB_E:[EARTH,5],MOB_F:[FIRE,5],MOB_V:[VETER,5],MOB_S:[SPIRIT,5],MOB_W:[WATER,5]}

#REWARDS
COL = 4037
DCS = 5126

#needed count
count = 4

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   cond = st.getInt("cond")
   htmltext = event
   chasha = st.getQuestItemsCount(CHASHA)
   voda = st.getQuestItemsCount(WATER)
   ogon = st.getQuestItemsCount(FIRE)
   zemlya = st.getQuestItemsCount(EARTH)
   veter = st.getQuestItemsCount(VETER)
   duh = st.getQuestItemsCount(SPIRIT)
   if event == "66666-03.htm" and cond == 0 :
     if st.getPlayer().getLevel() >= 60 :
        st.set("cond","1")
        st.setState(STARTED)
        st.playSound("ItemSound.quest_accept")
     else :
        htmltext = "66666-02.htm"
        st.exitQuest(1)
   elif event == "66666-07.htm" :
     if cond == 2 and voda == ogon == zemlya == veter == duh == 4 :
        htmltext = "66666-06.htm"
        st.playSound("ItemSound.quest_itemget")
        st.giveItems(CHASHA,1)
        st.takeItems(WATER,-1)
        st.takeItems(FIRE,-1)
        st.takeItems(EARTH,-1)
        st.takeItems(VETER,-1)
        st.takeItems(SPIRIT,-1)
        st.set("cond","3")
   elif event == "66666-09.htm" :
      if chasha == 1 and cond == 3 :
        htmltext = "66666-09.htm"
        st.takeItems(CHASHA,-1)
        st.giveItems(COL,40)
        st.giveItems(DCS,1)
        st.playSound("ItemSound.quest_finish")
        ObjectId=st.getPlayer().getObjectId()
        st.getPlayer().broadcastPacket(SocialAction(ObjectId,3))
        st.set("cond","4")
        st.setState(COMPLETED)
        st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player) :
   htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   if st :
       cond = st.getInt("cond")
       chasha = st.getQuestItemsCount(CHASHA)
       voda = st.getQuestItemsCount(WATER)
       ogon = st.getQuestItemsCount(FIRE)
       zemlya = st.getQuestItemsCount(EARTH)
       veter = st.getQuestItemsCount(VETER)
       duh = st.getQuestItemsCount(SPIRIT)
       if cond == 0 and npcId == EINHASAD :
          htmltext = "66666-01.htm"
       elif st.getState() == STARTED :
           if cond == 1 and npcId == EINHASAD :
              htmltext = "66666-05.htm"
           elif cond == 2 and voda == ogon == zemlya == veter == duh == 4 and npcId == NEWYEAR :
              htmltext = "66666-04.htm"
       if cond == 2 and npcId == EINHASAD :
           htmltext = "66666-05.htm"
       if cond == 3 and npcId == NEWYEAR :
           htmltext = "66666-10.htm"
       if cond == 3 and npcId == EINHASAD :
           htmltext = "66666-08.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember: return
   st = partyMember.getQuestState(qn)
   if st :
        if st.getState() == STARTED :
            cond = st.getInt("cond")
            item,chance = DROPLIST[npc.getNpcId()]
            prevItems = st.getQuestItemsCount(item)
            numItems, chance = divmod(chance*1,100)
            if st.getRandom(100) < chance :
              numItems = numItems + 1
            if count < (prevItems + numItems) :
              numItems = count - prevItems
            if numItems != 0 :
              st.giveItems(item,int(numItems))
              if st.getQuestItemsCount(WATER) == st.getQuestItemsCount(FIRE) ==  st.getQuestItemsCount(EARTH) == st.getQuestItemsCount(VETER) == st.getQuestItemsCount(SPIRIT) == count :
                 st.set("cond","2")
                 st.playSound("ItemSound.quest_middle")
              else :
                 st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(6666,qn,"custom")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(EINHASAD)
QUEST.addTalkId(EINHASAD)
QUEST.addTalkId(NEWYEAR)

for mob in DROPLIST.keys() :
  QUEST.addKillId(mob)

for item in range(20001,20006):
    STARTED.addQuestDrop(EINHASAD,item,1)