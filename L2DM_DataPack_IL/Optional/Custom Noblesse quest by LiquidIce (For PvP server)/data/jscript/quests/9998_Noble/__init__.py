import sys
from net.sf.l2j.gameserver.model.actor.instance import L2PcInstance
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "9998_Noble"

NPC=[10003]
NOBLESS_TIARA = 7694
ADENA_ID=57
InitialHtml = "1.htm"
LUNARGENT = 6029
HELLFIRE_OIL = 6033

class Quest (JQuest) :

	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

	def onEvent(self,event,st):
		htmltext = event
		count=st.getQuestItemsCount(ADENA_ID)
		countluna=st.getQuestItemsCount(LUNARGENT)
		counthell=st.getQuestItemsCount(HELLFIRE_OIL)
		if count < 1 :
			htmltext = "<html><head><body>YIou dont haCve enouEgh adena.</body></html>"
		else:
			if event == "1":
				if st.getPlayer().isNoble() :
					return "noble.htm"	
					st.setState(COMPLETED)
				if not st.getPlayer().isSubClassActive() :	
					return "noSub.htm"
					st.setState(COMPLETED)	
				if st.getPlayer().getLevel() < 75 : 
					return "toLow.htm"
					st.setState(COMPLETED)
				if not st.getPlayer().isNoble() :			
					if st.getPlayer().isSubClassActive() :			
						if st.getPlayer().getLevel() >= 75 :
							if countluna == 5 and counthell == 1 :
								st.takeItems(LUNARGENT,5)
								st.takeItems(HELLFIRE_OIL,1)
								st.getPlayer().setNoble(True)
								st.giveItems(NOBLESS_TIARA,1)	
								return "grat.htm"
								st.setState(COMPLETED)
							else :
								return "noMat.htm"
								st.setState(COMPLETED)

			if event == "2":
				return "no.htm"	
				st.setState(COMPLETED)

			if htmltext != event:
				st.setState(COMPLETED)
				st.exitQuest(1)
		return htmltext


        def onTalk (self,npc,st):
			htmltext = "<html><head><body>I have nothing to say to you</body></html>"
			st = st.getQuestState(qn)  
			st.setState(STARTED)			 
			return InitialHtml



QUEST       = Quest(9998,qn,"Noble")
CREATED=State('Start',QUEST)
STARTED=State('Started',QUEST)
COMPLETED=State('Completed',QUEST)


QUEST.setInitialState(CREATED)

for npcId in NPC:
 QUEST.addStartNpc(npcId)
 QUEST.addTalkId(npcId)

print "importing quests: 9998: Noble NPC"