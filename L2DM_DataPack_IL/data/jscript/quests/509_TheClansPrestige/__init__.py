# Made by dudilka
# for L2EmuProject
# 12/12/2007

import sys
from net.sf.l2j.gameserver.model.quest        import State
from net.sf.l2j.gameserver.model.quest        import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest
from net.sf.l2j.gameserver.serverpackets      import PledgeShowInfoUpdate
from net.sf.l2j.gameserver.serverpackets      import RadarControl
from net.sf.l2j.gameserver.serverpackets      import SystemMessage

qn="509_TheClansPrestige"
qd="The Clans Prestige"

# Quest NPC
GRAND_MAGISTER_VALDIS = 31331

# Quest Items
DAIMONS_EYES		    = 8489 # Daimon's Eyes : Eyes obtained by defeating Daimon the White-Eyed.
HESTIAS_FAIRY_STONE     = 8490 # Hestia's Fairy Stone : Obtain this Stone by defeating Hestia, Guardian Deity of the Hot Springs.
NUCLEUS_OF_LESSER_GOLEM = 8491 # Nucleus of Lesser Golem : Nucleus obtained by defeating Plague Golem.
FALSTONS_FANG           = 8492 # Falston's Fang : Fang obtained by defeating Demon's Agent Falston.
SHAIDS_TALON	        = 8493 # Shaid's Talon : Talon obtained by defeating Queen Shyeed.


#Quest Raid Bosses
DAIMON_THE_WHITE_EYED = 25290
HESTIA_GUARDIAN_DEITY = 25293
PLAGUE_GOLEM          = 25523
DEMONS_AGENT_FALSTON  = 25322
QUEEN_SHYEED          = 25514

# Reward
CLAN_POINTS_REWARD = 1500 # 1500 Point Per Boss

# id:[RaidBossNpcId,questItemId]
REWARDS_LIST={
    1:[DAIMON_THE_WHITE_EYED, DAIMONS_EYES],
    2:[HESTIA_GUARDIAN_DEITY, HESTIAS_FAIRY_STONE],
    3:[PLAGUE_GOLEM, NUCLEUS_OF_LESSER_GOLEM],
    4:[DEMONS_AGENT_FALSTON, FALSTONS_FANG],
    5:[QUEEN_SHYEED, SHAIDS_TALON]
    }

RADAR={
    1:[186304, -43744, -3193],
    2:[134672, -115600, -1216],
    3:[168641, -60417, -3888],
    4:[93296, -75104, -1824],
    5:[79635, -55434, -6135]
    }

class Quest (JQuest) :

 def __init__(self,id,name,descr) : JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player) :
  st = player.getQuestState(qn)
  if not st: return
  cond = st.getInt("cond")
  htmltext=event
  if event == "31331-0.htm" :
    if cond == 0 :
      st.set("cond","1")
      st.setState(STARTED)
  elif event.isdigit() :
    if int(event) in REWARDS_LIST.keys():
      st.set("raid",event)
      htmltext="31331-"+event+".htm"
      x,y,z=RADAR[int(event)]
      if x+y+z:
        player.sendPacket(RadarControl(2, 2, x, y, z))
        player.sendPacket(RadarControl(0, 1, x, y, z))
      st.playSound("ItemSound.quest_accept")
  elif event == "31331-7.htm" :
    st.playSound("ItemSound.quest_finish")
    st.exitQuest(1)
  return htmltext

 def onTalk (self,npc,player) :
  htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
  st = player.getQuestState(qn)
  if not st : return htmltext
  clan = player.getClan()
  npcId = npc.getNpcId()
  if player.getClan() == None or player.isClanLeader() == 0 :
     st.exitQuest(1)
     htmltext = "31331-0a.htm"
  elif player.getClan().getLevel() < 6 :
     st.exitQuest(1)
     htmltext =  "31331-0b.htm"
  else :
     cond = st.getInt("cond")
     raid = st.getInt("raid")
     id = st.getState()
     if id == CREATED and cond == 0 :
        htmltext =  "31331-0c.htm"
     elif id == STARTED and cond == 1 and raid in REWARDS_LIST.keys() :
        npc,item=REWARDS_LIST[raid]
        count = st.getQuestItemsCount(item)
        if not count :
           htmltext = "31331-"+str(raid)+"a.htm"
        elif count == 1 :
           htmltext = "31331-"+str(raid)+"b.htm"
           st.takeItems(item,1)
           clan.setReputationScore(clan.getReputationScore()+CLAN_POINTS_REWARD,True)
           player.sendPacket(SystemMessage(1777).addNumber(CLAN_POINTS_REWARD))
           clan.broadcastToOnlineMembers(PledgeShowInfoUpdate(clan))
  return htmltext

 def onKill(self,npc,player,isPet) :
  st = 0
  if player.isClanLeader() :
   st = player.getQuestState(qn)
  else:
   clan = player.getClan()
   if clan:
    leader = clan.getLeader()
    if leader :
     pleader = leader.getPlayerInstance()
     if pleader :
      if player.isInsideRadius(pleader, 1600, 1, 0) :
       st = pleader.getQuestState(qn)
  if not st : return
  option=st.getInt("raid")
  if st.getInt("cond") == 1 and st.getState() == STARTED and option in REWARDS_LIST.keys():
   raid,item = REWARDS_LIST[option]
   npcId=npc.getNpcId()
   if npcId == raid and not st.getQuestItemsCount(item) :
      st.giveItems(item,1)
      st.playSound("ItemSound.quest_middle")
  return


# Quest class and state definition
QUEST       = Quest(509,qn,qd)
CREATED     = State('Start',QUEST)
STARTED     = State('Started',QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(GRAND_MAGISTER_VALDIS)
QUEST.addTalkId(GRAND_MAGISTER_VALDIS)

for npc,item in REWARDS_LIST.values():
    QUEST.addKillId(npc)
    STARTED.addQuestDrop(npc,item,1)
