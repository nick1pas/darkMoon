# MassGakekeeper Teleport System V2.1
import sys
from net.sf.l2j.util import Rnd
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "9999_MassGatekeepers"

Mass_Gatekeeper_Gludio     = 35095
Mass_Gatekeeper_Dion       = 35137
Mass_Gatekeeper_Giran      = 35179
Mass_Gatekeeper_Oren       = 35221
Mass_Gatekeeper_Aden       = 35266
Mass_Gatekeeper_Innadril   = 35311
Mass_Gatekeeper_Goddard    = 35355
Mass_Gatekeeper_Schuttgart = 35547
Time_To_Teleport           = 30000 # time in ms

Gludio_Ports = [[-18110,109413,-2496],[-18285,109423,-2496], \
                [-18112,109075,-2496],[-18150,109213,-2496], \
                [-17910,109350,-2496],[-17880,109110,-2496], \
                [-18300,109100,-2496],[-18250,109280,-2496], \
                [-18000,109300,-2496],[-18130,109220,-2496]]

Dion_Ports = [[22074,160352,-2690],[22307,160352,-2690], \
              [21850,160352,-2690],[22074,160700,-2690], \
              [22150,160420,-2690],[21980,160540,-2690], \
              [22200,160300,-2690],[21970,160630,-2690], \
              [22100,160590,-2690],[21860,160450,-2690]]

Giran_Ports = [[116507,145095,-2563],[116400,145095,-2563], \
               [116250,145095,-2563],[116507,145100,-2563], \
               [116300,145150,-2563],[116730,144950,-2563], \
               [116100,145140,-2563],[116780,145250,-2563], \
               [116240,145180,-2563],[116710,145250,-2563]]

Oren_Ports = [[82576,37189,-2290],[82577,36969,-2290], \
              [82599,37428,-2290],[82941,37191,-2290], \
              [82787,37244,-2290],[82863,37002,-2290], \
              [82718,37166,-2290],[82944,37332,-2290], \
              [82859,37199,-2290],[82690,37066,-2290]]

Aden_Ports = [[147446,4652,-340],[147140,4565,-340], \
              [147260,4328,-340],[147452,4316,-340], \
              [147665,4435,-340],[147750,4598,-340], \
              [147636,4786,-340],[147507,4636,-340], \
              [147373,4486,-340],[147533,4444,-340]]

Innadril_Ports = [[116025,249097,-787],[116251,249138,-787], \
                  [116176,249269,-787],[115812,249126,-787], \
                  [115864,249363,-787],[115982,249480,-787], \
                  [116108,249484,-787],[116036,249342,-787], \
                  [115973,249408,-787],[116123,249294,-787]]

Goddard_Ports = [[147460,-48274,-2276],[147226,-48428,-2276], \
                 [147070,-48492,-2276],[147192,-48633,-2276], \
                 [147362,-48766,-2276],[147660,-48684,-2276], \
                 [147826,-48514,-2276],[147455,-48502,-2276], \
                 [147653,-48309,-2276],[147290,-48505,-2276]]

Schuttgart_Ports = [[77546,-152366,-544],[77156,-152561,-544], \
                    [77345,-152750,-544],[77543,-152754,-544], \
                    [77547,-152563,-544],[77744,-152754,-544], \
                    [77921,-152579,-544],[77747,-152372,-544], \
                    [77350,-152366,-544],[77449,-152675,-544]]

class Quest (JQuest):

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

  def onEvent (self,event,st) :
    X = Rnd.get(10)
    Y = Rnd.get(10)
    Z = Rnd.get(10)
    if event == "gludio_throne" :
       st.getPlayer().teleToLocation(Gludio_Ports[X][0],Gludio_Ports[Y][1],Gludio_Ports[Z][2])
    if event == "dion_throne" :
       st.getPlayer().teleToLocation(Dion_Ports[X][0],Dion_Ports[Y][1],Dion_Ports[Z][2])
    if event == "giran_throne" :
       st.getPlayer().teleToLocation(Giran_Ports[X][0],Giran_Ports[Y][1],Giran_Ports[Z][2])
    if event == "oren_throne" :
       st.getPlayer().teleToLocation(Oren_Ports[X][0],Oren_Ports[Y][1],Oren_Ports[Z][2])
    if event == "aden_throne" :
       st.getPlayer().teleToLocation(Aden_Ports[X][0],Aden_Ports[Y][1],Aden_Ports[Z][2])
    if event == "innadril_throne" :
       st.getPlayer().teleToLocation(Innadril_Ports[X][0],Innadril_Ports[Y][1],Innadril_Ports[Z][2])
    if event == "goddard_throne" :
       st.getPlayer().teleToLocation(Goddard_Ports[X][0],Goddard_Ports[Y][1],Goddard_Ports[Z][2])
    if event == "schuttgart_throne" :
       st.getPlayer().teleToLocation(Schuttgart_Ports[X][0],Schuttgart_Ports[Y][1],Schuttgart_Ports[Z][2])
    if st.getQuestTimer(event) :
       st.getQuestTimer(event).cancel()
    st.setState(COMPLETED)
    return  

  def onTalk (self,npc,player) :
    htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
    npcId = npc.getNpcId()
    st = player.getQuestState(qn)
    if not st : return htmltext
    id = st.getState()
    if id == STARTED :
        htmltext = "CastleTeleportDelayed.htm"
    elif npcId == Mass_Gatekeeper_Gludio :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("gludio_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Dion :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("dion_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Giran :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("giran_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Oren :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("oren_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Aden :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("aden_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Innadril :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("innadril_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Goddard :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("goddard_throne",Time_To_Teleport,npc)
    if npcId == Mass_Gatekeeper_Schuttgart :
        htmltext = "CastleTeleportDelayed.htm"
        st.startQuestTimer("schuttgart_throne",Time_To_Teleport,npc)
    st.setState(STARTED)
    return htmltext

# Quest class and state definition
QUEST       = Quest(9999, qn, "Teleports")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST)
COMPLETED   = State('Completed', QUEST)

# Quest initialization
QUEST.setInitialState(CREATED)
# Quest NPC starter initialization
QUEST.addStartNpc(35095)
QUEST.addStartNpc(35137)
QUEST.addStartNpc(35179)
QUEST.addStartNpc(35221)
QUEST.addStartNpc(35266)
QUEST.addStartNpc(35311)
QUEST.addStartNpc(35355)
QUEST.addStartNpc(35547)
QUEST.addTalkId(35095)
QUEST.addTalkId(35137)
QUEST.addTalkId(35179)
QUEST.addTalkId(35221)
QUEST.addTalkId(35266)
QUEST.addTalkId(35311)
QUEST.addTalkId(35355)
QUEST.addTalkId(35547)