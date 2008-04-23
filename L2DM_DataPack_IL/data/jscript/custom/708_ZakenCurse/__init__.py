# Zaken's Curse : Event
# Written by Paf Euclide updated and fixed by Rayan
# Fixed again for L2J - Fulminus, Kerberos_20, BigBro
##############
import sys
from java.util                                import Calendar
from net.sf.l2j                               import L2DatabaseFactory
from net.sf.l2j.gameserver                    import Announcements
from net.sf.l2j.gameserver.datatables         import NpcTable
from net.sf.l2j.gameserver.datatables         import SkillTable
from net.sf.l2j.gameserver.datatables         import SpawnTable
from net.sf.l2j.gameserver.model              import L2Spawn
from net.sf.l2j.gameserver.model.quest        import QuestState
from net.sf.l2j.gameserver.model.quest        import State
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest
from net.sf.l2j.util                          import Rnd

qn = "708_ZakenCurse"
#event properties
EVENT_DATE = [9 , 10]#[DD, MM]
EVENT_NPC = 31857
EVENT_DAY_TO_STAY = 1#how many days after event date Event_Npc will be respawn (for giving reward)

#items
CUPID_BOW = 9141
GOLDEN_APIGA = 9143
PIRATE_HAT = 8922

#cupid's bow's skills
SKILLS = [3260, 3262]

CHANCE = [45, 5, 2]#[HugePigs, SuperHugePig, GoldenPig]
REWARD = [1, 7, 10]#[ HugePigs, SuperHugePig, GoldenPig]

#Event Manager Spawnlist
EM = [
        [10693, 17345, -4590, 45486],
        [115095, -178309, -917, 10631],
        [47566, 51138, -3001, 33264],
        [-45272, -112396, -245, 652],
        [-84542, 244682, -3735, 53988],
        [147855, 26629, -2210, 19523],
        [16249, 142870, -2711, 12022],
        [17832, 170509, -3531, 48408],
        [83057, 149281, -3474, 31176],
        [-80858, 149456, -3070, 16948],
        [-12147, 122760, -3102, 34490],
        [110954, 218935, -3548, 0],
        [117158, 75807, -2735, 25189],
        [82494, 53151, -1501, 946],
        [43556, -47626, -802, 42552],
        [147388, -55436, -2738, 62376],
        [87775, -143216, -1298, 27295]
    ]

#Event Mob Spawnlist
MOB = [
        [12739, 172819, -3415, 53544],
        [13386, 172680, -3452, 63628],
        [13304, 173135, -3449, 53988],
        [13229, 173748, -3463, 19658],
        [14639, 174127, -3486, 65020],
        [14924, 174953, -3602, 7150],
        [15625, 175086, -3662, 59709],
        [16243, 174928, -3644, 60588],
        [16217, 174024, -3691, 47810],
        [16989, 173868, -3626, 64213],
        [16919, 173152, -3598, 43726],
        [16477, 172906, -3565, 35563],
        [15708, 173583, -3576, 25291],
        [17796, 173812, -3645, 63328],
        [18306, 174341, -3680, 8494],
        [18731, 174004, -3639, 55432],
        [19147, 174213, -3598, 64786],
        [19715, 173364, -3578, 55248],
        [20446, 173433, -3577, 5533],
        [20738, 173844, -3577, 59844],
        [20235, 172255, -3582, 45099],
        [21085, 172082, -3577, 63738],
        [21857, 172346, -3567, 2650],
        [21763, 173214, -3577, 16169],
        [21583, 171240, -3547, 59056],
        [20920, 170514, -3556, 41043],
        [20314, 169681, -3532, 49447],
        [19792, 169950, -3560, 26862],
        [19419, 168903, -3480, 48761],
        [18970, 169172, -3483, 29138],
        [18014, 168372, -3524, 38624],
        [18275, 167273, -3486, 50371],
        [17227, 167052, -3519, 34197],
        [16007, 167490, -3546, 24702],
        [14782, 167310, -3643, 36856],
        [14296, 167874, -3636, 16980],
        [14201, 169242, -3616, 19444],
        [14354, 170546, -3558, 13843],
        [15016, 170893, -3485, 3419],
        [15978, 171333, -3555, 11655],
        [21891, 170635, -3516, 49199],
        [21252, 169570, -3443, 50277],
        [18828, 168262, -3463, 34177],
        [17409, 167654, -3460, 33168],
        [15513, 168057, -3512, 21964],
        [12083, 172092, -3514, 29851],
        [12720, 173616, -3463, 10111],
        [14383, 173431, -3459, 53120],
        [14621, 173096, -3485, 57956],
        [14555, 172248, -3479, 48166],
        [15692, 171932, -3564, 62491],
        [17146, 172578, -3546, 4609],
        [17973, 173254, -3580, 4655],
        [19411, 173905, -3604, 2154],
        [20159, 174128, -3577, 54788],
        [23193, 172600, -3472, 554],
        [23850, 172349, -3396, 57840],
        [24495, 171338, -3381, 54967],
        [25261, 170669, -3385, 60908],
        [26423, 170887, -3413, 943],
        [26357, 170026, -3348, 26605],
        [25473, 169179, -3330, 40551],
        [26009, 168673, -3295, 64740],
        [26556, 168599, -3253, 2226],
        [27314, 168350, -3220, 47035],
        [27086, 166750, -3365, 46402],
        [27221, 165848, -3447, 59247],
        [26110, 165447, -3437, 34146],
        [26548, 164573, -3496, 54428],
        [23190, 166013, -3339, 23885],
        [21682, 167176, -3370, 24088],
        [20640, 167878, -3390, 27964],
        [20771, 168524, -3395, 13984],
        [19174, 170072, -3558, 21523],
        [18615, 170401, -3507, 54788],
        [21504, 173649, -3577, 2177],
        [21986, 171263, -3567, 55609],
        [23064, 170718, -3419, 19893],
        [23737, 171751, -3408, 9846],
        [24991, 172189, -3388, 7754],
        [25393, 173308, -3428, 15644],
        [27313, 172418, -3388, 56835],
        [28511, 172474, -3476, 14904],
        [28403, 170634, -3390, 44315],
        [27401, 169594, -3271, 43576],
        [26214, 169225, -3256, 38961],
        [29224, 166327, -3548, 56697],
        [28861, 164369, -3632, 38263],
        [15806, 164244, -3644, 48476],
        [15933, 165069, -3556, 15252],
        [15342, 165490, -3557, 26398],
        [14679, 166316, -3611, 16307],
        [15156, 166694, -3551, 14326],
        [13016, 166792, -3713, 3823],
        [11674, 166816, -3719, 39015],
        [11058, 167733, -3696, 19620],
        [9968, 167180, -3604, 43620],
        [9112, 167076, -3641, 29917],
        [9528, 168170, -3545, 12466],
        [9690, 169159, -3535, 60733],
        [9969, 170049, -3538, 8305],
        [11123, 171612, -3605, 9787],
        [10489, 173775, -3660, 23256],
        [9770, 175077, -3662, 19973],
        [10552, 175728, -3623, 8360],
        [11806, 176327, -3583, 6701],
        [13250, 172087, -3477, 36585],
        [14915, 171874, -3504, 3516],
        [16333, 170029, -3558, 64040],
        [17037, 168803, -3544, 54926],
        [16409, 166252, -3494, 45158],
        [14410, 163938, -3702, 41423],
        [13200, 165174, -3696, 19611],
        [12473, 166578, -3675, 21933],
        [13818, 168499, -3660, 14813],
        [18012, 174804, -3673, 1808],
        [23437, 169290, -3436, 64996],
        [25486, 167606, -3227, 23874],
        [22918, 165340, -3340, 37225],
        [20191, 166388, -3405, 31650],
        [19683, 167733, -3395, 15523]
    ]

def isEventPassed(val):
    now = Calendar.getInstance()
    c = Calendar.getInstance()
    c.set(Calendar.DAY_OF_MONTH, (EVENT_DATE[0]+val))
    c.set(Calendar.MONTH, (EVENT_DATE[1]-1))
    time = now.getTimeInMillis() - c.getTimeInMillis()
    #print str(time)
    #if gameserver lag during loading of the quest or if test when minute change ( 1min59s != 2min00s)
    if time > 65000 or time < -65000:
        return True
    else :
        return False

def isBestCollector(player) :
    player_id=player.getObjectId()
    con=L2DatabaseFactory.getInstance().getConnection(con)
    offline=con.prepareStatement("SELECT owner_id FROM items WHERE item_id = ? AND `count` = ( SELECT max( `count` ) FROM items WHERE item_id = ? ) LIMIT 0 , 1")
    offline.setInt(1, GOLDEN_APIGA)
    offline.setInt(2, GOLDEN_APIGA)
    rs=offline.executeQuery()
    val = False
    while(rs.next()):
        if rs.getInt("owner_id") == player_id :
            val = True
    try :
        con.close()
    except :
        pass
    return val

def unspawnNpc(npcId) :
    print "CustomManager: Zaken's Curse: Deleting npc(" + str(npcId) + ") From Table Spawnlist."
    for spawn in SpawnTable.getInstance().getSpawnTable().values():
        if spawn.getNpcId() == npcId:
            print "CustomManager: Zaken's Curse: @ [" + str(spawn.getLocx()) + "," + str(spawn.getLocy()) + "," + str(spawn.getLocz()) + "," + str(spawn.getHeading()) + "]"
            SpawnTable.getInstance().deleteSpawn(spawn, False)
            npc=spawn.getLastSpawn()
            npc.deleteMe()
    return

def spawnNpc(npcName, npcId, list):
    print "CustomManager: Zaken's Curse: Spawning " + npcName + "..."
    template = NpcTable.getInstance().getTemplate(npcId)
    i = 0
    while (i < len(list)) :
        print "CustomManager: Zaken's Curse: @ [" + str(list[i][0]) + "," + str(list[i][1]) + "," + str(list[i][2]) + "," + str(list[i][3]) + "]"
        spawn = L2Spawn(template)
        spawn.setLocx(list[i][0])
        spawn.setLocy(list[i][1])
        spawn.setLocz(list[i][2])
        spawn.setAmount(1)
        spawn.setHeading(list[i][3])
        spawn.setRespawnDelay(60)
        SpawnTable.getInstance().addNewSpawn(spawn, False)#don't save spawn in DB
        spawn.init()
        i += 1
    print "CustomManager: Zaken's Curse: Done..."
    return

class Quest(JQuest) :
    def __init__(self,id,name,descr):
        JQuest.__init__(self,id,name,descr)
        print "CustomManager: Zaken's Curse: Event Zaken's Curse Init."
        self.Pigs = [13031, 13032, 13033, 13034, 13035]
        self.HugePigs =[13031, 13032, 13033]
        self.haveWinner = False
        unspawnNpc(EVENT_NPC) #prevent multiple spawn if server reboot
        unspawnNpc(self.Pigs[0])
        isPassed = isEventPassed(0)
        if isPassed == False :
            print "CustomManager: Zaken's Curse: Event Zaken's Curse Is Running."
            print "CustomManager: Zaken's Curse: Event Manager Need To Be Spawn..."
            spawnNpc("Event Manager", EVENT_NPC, EM)
            print "CustomManager: Zaken's Curse: Event Mobs Need To Be Spawn..."
            spawnNpc("Event Mobs", self.Pigs[0], MOB)
            Announcements.getInstance().addAnnouncement("Event Zaken's Curse : Go talk to an Event Manager !")
            for i in self.Pigs :
                self.addSkillUseId(i)
        else :
            print "CustomManager: Zaken's Curse: Event Zaken's Curse Has Passed."
            isSpawnNotNeeded = isEventPassed(EVENT_DAY_TO_STAY)
            if isSpawnNotNeeded == False :
                print "CustomManager: Zaken's Curse: Event Manager Need To Be Spawn..."
                spawnNpc("Event Manager", EVENT_NPC, EM)

    def onEvent (self,event,st):
        htmltext = event
        player=st.getPlayer()
        if event == "accept" :
            st.set("cond","1")
            st.setState(STARTED)
            st.playSound("ItemSound.quest_accept")
            st.giveItems(CUPID_BOW,1)
            htmltext = "31857-02.htm"
        elif event == "stop" :
            st.takeItems(CUPID_BOW,1)
            st.playSound("ItemSound.quest_giveup")
            st.exitQuest(1)
        elif event == "isBest" :
            if self.haveWinner == False :
                if isBestCollector(player) == True :
                    htmltext = "31857-04.htm"
                    st.giveItems(PIRATE_HAT, 1)
                    self.haveWinner = True
                else :
                    htmltext = "31857-05.htm"
            else :
                htmltext = "31857-05.htm"
            st.takeItems(GOLDEN_APIGA, st.getQuestItemsCount(GOLDEN_APIGA))
            st.playSound("ItemSound.quest_finish")
            st.exitQuest(1)
        return htmltext

    def onTalk (self,npc,player):
        st = player.getQuestState("708_ZakenCurse")
        htmltext = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"
        if not st : return htmltext
        npcId = npc.getNpcId()
        id = st.getState()
        cond = st.getInt("cond")
        isPassed = isEventPassed(0)
        if isPassed == False :
            if npcId == EVENT_NPC and id == CREATED:
                htmltext = "31857-01.htm"
            if npcId == EVENT_NPC and id == STARTED:
                htmltext = "31857-02.htm"
        elif npcId == EVENT_NPC and cond == 1 :
            st.set("cond","2")
            htmltext = "31857-03.htm"
            st.takeItems(CUPID_BOW,1)
        return htmltext

    def onSkillUse (self,npc,player,skill):
        # gather some values on local variables
        st = player.getQuestState("708_ZakenCurse")
        npcId = npc.getNpcId()
        skillId = skill.getId()
        # check if the npc and skills used are valid for this script.  Exit if invalid.
        if npcId not in self.Pigs : return
        if skillId not in SKILLS : return
        #if someone hit a pig after the end of event, unspawn all pigs
        if isEventPassed(0) == True :
            i = 0
            while (i < len(self.Pigs)) :
                unspawnNpc(self.Pigs[i])
                i += 1
            Announcements.getInstance().announceToAll("Event Zaken's Curse has ended, get your reward to an Event Manager !")
            return
        npc.onDecay()
        if skillId == 3260:
            chance = Rnd.get(1,100)
            #Heart Shot : can only spawn another Huge Pig
            if npcId < 13033:
                if chance <= CHANCE[0]:
                    self.addSpawn(npcId+1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 60000)
                else :
                    st.giveItems(GOLDEN_APIGA, REWARD[0])
                return
            elif npcId == 13034 :
                st.giveItems(GOLDEN_APIGA, REWARD[1])
            elif npcId == 13035 :
                st.giveItems(GOLDEN_APIGA, REWARD[2])
            return

        if skillId == 3262:
            chance = Rnd.get(1,100)
            #Double Heart Shot : can spawn Huge Pig, Super Huge Pig and Golden Pig
            if npcId < 13033 :
                if chance <= CHANCE[2] :
                    self.addSpawn(13035, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 60000)
                elif chance <= CHANCE[1] :
                    self.addSpawn(13034, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 60000)
                elif chance <= CHANCE[0]:
                    if npcId < 13033 :
                        self.addSpawn(npcId+1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), True, 60000)
                else :
                    st.giveItems(GOLDEN_APIGA, REWARD[0])
            else :
                if npcId == 13034 :
                    st.giveItems(GOLDEN_APIGA, REWARD[1])
                elif npcId == 13035 :
                    st.giveItems(GOLDEN_APIGA, REWARD[2])
                return
        return

# now call the constructor (starts up the quest)
QUEST        = Quest(708,qn,"custom")
CREATED      = State('Start', QUEST)
STARTED      = State('Started', QUEST)
QUEST.setInitialState(CREATED)
QUEST.addStartNpc(EVENT_NPC)
QUEST.addTalkId(EVENT_NPC)