import sys
from net.sf.l2j.gameserver.model.actor.instance import L2PcInstance
from java.util import Iterator
from net.sf.l2j.gameserver.datatables import SkillTable
from net.sf.l2j			       import L2DatabaseFactory
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "9999_NPCBuffer"

NPC=[40000]
ADENA_ID=57
QuestId     = 9999
QuestName   = "NPCBuffer"
QuestDesc   = "custom"
InitialHtml = "1.htm"

print "importing Buffer: Van NPCBuffer"

class Quest (JQuest) :

	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)


	def onEvent(self,event,st):
		htmltext = event
		count=st.getQuestItemsCount(ADENA_ID)
		if count < 5000  or st.getPlayer().getLevel() < 0 :
			htmltext = "<html><head><body>You dont have enought Adena.</body></html>"
		else:
			st.takeItems(ADENA_ID,0)
			st.getPlayer().setTarget(st.getPlayer())

			#Acumen
			if event == "2":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9900,1),False,False)
				st.getPlayer().restoreHPMP()				
				return "1.htm"
				st.setState(COMPLETED)

			#Agility
			if event == "3": 
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9901,1),False,False)
				st.getPlayer().restoreHPMP()				
				return "1.htm"
				st.setState(COMPLETED)

			#Berserker Spirit
			if event == "4":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9902,1),False,False)
				st.getPlayer().restoreHPMP()				
				return "1.htm"
				st.setState(COMPLETED)

                        #Bless Shield
			if event == "5":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9903,1),False,False)	
				st.getPlayer().restoreHPMP()
				return "1.htm"			
				st.setState(COMPLETED)

			#Blessed Body
			if event == "6":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9904,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Blessed Soul
			if event == "7":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9905,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Concentration
			if event == "8":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9906,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Death Whisper
			if event == "9":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9907,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Empower
			if event == "10":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9908,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Focus
			if event == "11":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9909,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Guidance
			if event == "12":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9910,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Haste
			if event == "13":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9911,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Magic Barrier
			if event == "14":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9912,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Mental Shield
			if event == "15":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9913,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Might
			if event == "16":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9914,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Resist Shock
			if event == "17":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9915,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Shield
			if event == "18":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9916,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Vampiric Rage
			if event == "19":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9917,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(COMPLETED)

			#Dance of Aqua Guard
			if event == "30":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9918,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Concentration
			if event == "31":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9919,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Earth Guard
			if event == "32":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9920,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Fire
			if event == "33":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9921,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Fury
			if event == "34":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9922,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Inspiration
			if event == "35":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9923,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Light
			if event == "36":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9924,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of the Mystic
			if event == "37":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9925,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of Protection
			if event == "38":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9926,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Siren's Dance
			if event == "39":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9927,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of the Vampire
			if event == "40":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9928,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Dance of the Warrior
			if event == "41":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9929,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Champion
			if event == "50":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9930,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Earth
			if event == "51":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9931,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Flame Guard
			if event == "52":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9932,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Hunter
			if event == "53":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9933,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Invocation
			if event == "54":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9934,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Life
			if event == "55":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9935,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Meditation
			if event == "56":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9936,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Renewal
			if event == "57":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9937,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Storm Guard
			if event == "58":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9938,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Vengeance
			if event == "59":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9939,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Vitality
			if event == "60":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9940,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Warding
			if event == "61":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9941,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Water
			if event == "62":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9942,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Song of Wind
			if event == "63":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9943,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Greater Might
			if event == "64":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9944,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Greater Shield
			if event == "65":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9945,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Chant of Victory
			if event == "66":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9946,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Prophecy of Fire
			if event == "67":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9947,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Prophecy of Water
			if event == "68":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9948,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Prophecy of Wind
			if event == "69":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9949,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Cancellation
			if event == "70":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Wind Walk
			if event == "71":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9951,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Nobless
			if event == "72":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9952,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			#Elementa Protection
			if event == "73":
				st.takeItems(ADENA_ID,5000)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(9953,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(COMPLETED)

			if htmltext != event:
				st.setState(COMPLETED)
				st.exitQuest(1)
		return htmltext


	def onTalk (self,npc,player):
	   st = player.getQuestState(qn)
	   htmltext = "<html><head><body>I have nothing to say to you</body></html>"
	   st.setState(STARTED)
	   return InitialHtml

QUEST       = Quest(QuestId,str(QuestId) + "_" + QuestName,QuestDesc)
CREATED=State('Start',QUEST)
STARTED=State('Started',QUEST)
COMPLETED=State('Completed',QUEST)

QUEST.setInitialState(CREATED)

for npcId in NPC:
 QUEST.addStartNpc(npcId)
 QUEST.addTalkId(npcId)
