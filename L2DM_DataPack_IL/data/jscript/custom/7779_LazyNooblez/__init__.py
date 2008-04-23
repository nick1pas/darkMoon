##########################################################
#Made by Z0mbie!                                         # 
#ну ето...скриптиг даёт нублеса без прохождения квеста   #
#не забываем настроить цену в COUNT                      #
##########################################################   
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "7779_LazyNooblez"

#Itemz
ADENA = 57
COUNT = 1000000000
# NPCz
NPC = 70037
class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = " "
   if event == "1" :
      st.giveItems(ADENA,COUNT)
      st.getPlayer().setNoble(True)
      htmltext = "Nooblez set to True!"
   if event == "2" :
      st.giveItems(ADENA,COUNT)
      st.getPlayer().setNoble(False)
      htmltext = "Nooblez set to False!"
   return htmltext 

     
 def onTalk (self,npc,player) :
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   htmltext = " "
   if not st : return htmltext
   if  npcId == NPC:
       htmltext = "event-1.htm"
   return htmltext

QUEST       = Quest(7779, qn, "custom")
QUEST.addStartNpc(NPC)                        
QUEST.addTalkId(NPC)

print "--------->LazyNooblez loaded######*" 
