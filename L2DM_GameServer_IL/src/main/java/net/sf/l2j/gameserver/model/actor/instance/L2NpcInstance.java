/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.model.actor.instance;

import static net.sf.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import java.text.DateFormat;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.Config.CacheType;
import net.sf.l2j.gameserver.Olympiad;
import net.sf.l2j.gameserver.SevenSigns;
import net.sf.l2j.gameserver.SevenSignsFestival;
import net.sf.l2j.gameserver.Shutdown;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.BuffTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.PetDataTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedItemTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.BaiumManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.QuestManager;
import net.sf.l2j.gameserver.instancemanager.TownManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager.RoomType;
import net.sf.l2j.gameserver.instancemanager.games.Lottery;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.L2Attackable;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2DropCategory;
import net.sf.l2j.gameserver.model.L2DropData;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Multisell;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.L2Summon;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.MobGroupTable;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.actor.knownlist.NpcKnownList;
import net.sf.l2j.gameserver.model.actor.stat.NpcStat;
import net.sf.l2j.gameserver.model.actor.status.NpcStatus;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.L2Event;
import net.sf.l2j.gameserver.model.entity.Town;
import net.sf.l2j.gameserver.model.entity.events.CTF;
import net.sf.l2j.gameserver.model.entity.events.DM;
import net.sf.l2j.gameserver.model.entity.events.FortressSiege;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.model.entity.events.VIP;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.ExQuestInfo;
import net.sf.l2j.gameserver.serverpackets.ExShowVariationCancelWindow;
import net.sf.l2j.gameserver.serverpackets.ExShowVariationMakeWindow;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.NpcInfo;
import net.sf.l2j.gameserver.serverpackets.RadarControl;
import net.sf.l2j.gameserver.serverpackets.SocialAction;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.serverpackets.ValidateLocation;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.templates.L2BuffTemplate;
import net.sf.l2j.gameserver.templates.L2Item;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.templates.L2Weapon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a Non-Player-Character in the world. It can be a monster or a friendly character.
 * It also uses a template to fetch some static values. The templates are hardcoded in the client, so we can rely on them.<BR><BR>
 * 
 * L2Character :<BR><BR>
 * <li>L2Attackable</li>
 * <li>L2BoxInstance</li>
 * <li>L2FolkInstance</li>
 * 
 * @version $Revision: 1.32.2.7.2.24 $ $Date: 2005/04/11 10:06:09 $
 */
public class L2NpcInstance extends L2Character
{
    protected final static Log _log = LogFactory.getLog(L2NpcInstance.class.getName());

    /** The interaction distance of the L2NpcInstance(is used as offset in MovetoLocation method) */
    public static final int INTERACTION_DISTANCE = 150;
    
    /** The L2Spawn object that manage this L2NpcInstance */
    private L2Spawn _spawn;
    
    /** The flag to specify if this L2NpcInstance is busy */
    private boolean _isBusy = false;
    
    /** The busy message for this L2NpcInstance */
    private String _busyMessage = "";

    /** True if endDecayTask has already been called */
    volatile boolean _isDecayed = false;

    /** True if a Dwarf has used Spoil on this L2NpcInstance */
    private boolean _isSpoil = false;

    /** The castle index in the array of L2Castle this L2NpcInstance belongs to */
    private int _castleIndex = -2;
    
    public String _CTF_FlagTeamName;
    public boolean isEventMob = false,
                  _isEventMobTvT = false,
                  _isEventMobDM = false,
                  _isEventMobCTF = false,
                  _isCTF_throneSpawn = false,
  				  _isCTF_Flag = false,
  				  _isEventVIPNPC = false,
                  _isEventVIPNPCEnd = false,
                  _isEventMobFOS = false,
                  _isFOS_Artifact = false,
                  //L2EMU_ADD_BEGIN
                     isJailMob = false,
    				_isUsingShots = false;
                   //L2EMU_ADD_END

    private boolean _isInTown = false;
    private int _isSpoiledBy = 0;

    protected RandomAnimationTask _rAniTask = null;
    private int _currentLHandId;  // normally this shouldn't change from the template, but there exist exceptions
    private int _currentRHandId;  // normally this shouldn't change from the template, but there exist exceptions

    private int _currentCollisionHeight; // used for npc grow effect skills
    private int _currentCollisionRadius; // used for npc grow effect skills

    /** Task launching the function onRandomAnimation()
    * Scheduled for L2MonsterInstance only if AllowRandomAnimation=true
    */
    protected class RandomAnimationTask implements Runnable
    {
        public void run()
        {
            try
            {
                if(this != _rAniTask)
                    return; // Shouldn't happen, but who knows... just to make sure every active npc has only one timer.
                if(isMob())
                {
                    // Cancel further animation timers until intention is changed to ACTIVE again.
                    if(getAI().getIntention() != AI_INTENTION_ACTIVE)
                        return; 
                }
                else
                {
                    if (!isInActiveRegion()) // NPCs in inactive region don't run this task 
                        return;
                    // update knownlist to remove playable which aren't in range any more
                    getKnownList().updateKnownObjects(); 
                }

                if(!(isDead() || isStunned() || isSleeping() || isParalyzed()))
                    onRandomAnimation();

                startRandomAnimationTimer();
            }
            catch (Throwable t) {}
        }
    }

    /**
     * Send a packet SocialAction to all L2PcInstance in the _KnownPlayers of the L2NpcInstance and create a new RandomAnimation Task.<BR><BR>
     */
    public void onRandomAnimation()
    {
        // Send a packet SocialAction to all L2PcInstance in the _KnownPlayers of the L2NpcInstance
        SocialAction sa = new SocialAction(getObjectId(), Rnd.get(2, 3));
        broadcastPacket(sa);
    }

    /**
     * Create a RandomAnimation Task that will be launched after the calculated delay.<BR><BR>
     */
    public void startRandomAnimationTimer()
    {
        if (!hasRandomAnimation())
            return;

        int minWait = isMob() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
        int maxWait = isMob() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;

        // Calculate the delay before the next animation
        int interval = Rnd.get(minWait, maxWait) * 1000;

        // Create a RandomAnimation Task that will be launched after the calculated delay 
        _rAniTask = new RandomAnimationTask();
        ThreadPoolManager.getInstance().scheduleGeneral(_rAniTask, interval);
    }

    /**
     * Check if the server allows Random Animation.<BR><BR>
     */
    public boolean hasRandomAnimation()
    {
        return (Config.MAX_NPC_ANIMATION > 0);
    }

    public class destroyTemporalNPC implements Runnable
    {
        private L2Spawn _oldSpawn;
        
        public destroyTemporalNPC(L2Spawn spawn)
        {
            _oldSpawn = spawn;
        }
                
        public void run()
        {
            try
            {
                _oldSpawn.getLastSpawn().deleteMe();
                _oldSpawn.stopRespawn();
                SpawnTable.getInstance().deleteSpawn(_oldSpawn, false);
            } 
            catch (Throwable t) {}
            }
        }
        
        public class destroyTemporalSummon implements Runnable
        {
            L2Summon _summon;
            L2PcInstance _player;
            
            public destroyTemporalSummon(L2Summon summon, L2PcInstance player)
            {
                _summon = summon;
                _player = player;
            }
            
        public void run()
        {
            _summon.unSummon(_player);
        }
     }
    
    /**
     * Constructor of L2NpcInstance (use L2Character constructor).<BR><BR>
     *  
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to set the _template of the L2Character (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)  </li>
     * <li>Set the name of the L2Character</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
     * 
     * @param objectId Identifier of the object to initialized
     * @param template The L2NpcTemplate to apply to the NPC
     * 
     */
    public L2NpcInstance(int objectId, L2NpcTemplate template)
    {
        // Call the L2Character constructor to set the _template of the L2Character, copy skills from template to object 
        // and link _calculators to NPC_STD_CALCULATOR
        super(objectId, template);
        getKnownList();    // init knownlist
        getStat();         // init stats
        getStatus();       // init status
        super.initCharStatusUpdateValues(); // init status upadte values

        // initialize the "current" equipment
        _currentLHandId = getTemplate().getLhand();
        _currentRHandId = getTemplate().getRhand();
        // initialize the "current" collisions 
        _currentCollisionHeight = getTemplate().getCollisionHeight();
        _currentCollisionRadius = getTemplate().getCollisionRadius();

        if (template == null)
        {
            _log.fatal("No template for Npc. Please check your datapack is setup correctly.");
            return;
        }
        
        // Set the name and the title of the L2Character
        setName(template.getName());
        setTitle(template.getTitle());
    }

    @Override
    public NpcKnownList getKnownList()
    {
        if(super.getKnownList() == null || !(super.getKnownList() instanceof NpcKnownList))
            setKnownList(new NpcKnownList(this));
        return (NpcKnownList)super.getKnownList();
    }
    
    @Override
    public NpcStat getStat()
    {
        if(super.getStat() == null || !(super.getStat() instanceof NpcStat))
            setStat(new NpcStat(this));
        return (NpcStat)super.getStat();
    }
    
    @Override
    public NpcStatus getStatus()
    {
        if(super.getStatus() == null || !(super.getStatus() instanceof NpcStatus))
            setStatus(new NpcStatus(this));
        return (NpcStatus)super.getStatus();
    }

    /** Return the L2NpcTemplate of the L2NpcInstance. */
    @Override
    public final L2NpcTemplate getTemplate() 
    { 
        return (L2NpcTemplate)super.getTemplate(); 
    }
    
    /**
     * Return the generic Identifier of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    public int getNpcId()
    {
        return getTemplate().getNpcId();
    }
    
    @Override
    public boolean isAttackable()
    {
        return false;
    }
    
    /**
     * Return the faction Identifier of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     * 
     * <B><U> Concept</U> :</B><BR><BR>
     * If a NPC belows to a Faction, other NPC of the faction inside the Faction range will help it if it's attacked<BR><BR>
     * 
     */
    public final String getFactionId()
    {
        return getTemplate().getFactionId();
    }

    /**
     * Return the Level of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    @Override
    public final int getLevel() 
    {
        return getTemplate().getLevel();
    }
    
    /**
     * Return True if the L2NpcInstance is agressive (ex : L2MonsterInstance in function of aggroRange).<BR><BR>
     */
    public boolean isAggressive()
    {
        return false;
    }
    
    /**
     * Return the Aggro Range of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    public int getAggroRange()
    {
        return getTemplate().getAggroRange();
    }
    
    /**
     * Return the Faction Range of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    public int getFactionRange()
    {
        return getTemplate().getFactionRange();
    }
    
    /**
     * Return True if this L2NpcInstance is undead in function of the L2NpcTemplate.<BR><BR>
     */
    @Override
    public boolean isUndead()
    {
        return getTemplate().isUndead();
    }
    
    /**
     * Send a packet NpcInfo with state of abnormal effect to all L2PcInstance in the _knownPlayers of the L2NpcInstance.<BR><BR>
     */
    @Override
    public void updateAbnormalEffect()
    {
        //NpcInfo info = new NpcInfo(this);
        //broadcastPacket(info);
        
        // Send a Server->Client packet NpcInfo with state of abnormal effect to all L2PcInstance in the _knownPlayers of the L2NpcInstance
        for (L2PcInstance player : getKnownList().getKnownPlayers().values())
            if (player != null && this != null)
                player.sendPacket(new NpcInfo(this, player));
    }
    
    /**
     * Return the distance under which the object must be add to _knownObject in function of the object type.<BR><BR>
     *   
     * <B><U> Values </U> :</B><BR><BR>
     * <li> object is a L2FolkInstance : 0 (don't remember it) </li>
     * <li> object is a L2Character : 0 (don't remember it) </li>
     * <li> object is a L2PlayableInstance : 1500 </li>
     * <li> others : 500 </li><BR><BR> 
     * 
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2Attackable</li><BR><BR> 
     * 
     * @param object The Object to add to _knownObject
     * 
     */
    public int getDistanceToWatchObject(L2Object object)
    {
        if (object instanceof L2FestivalGuideInstance)
            return 10000;
        
        if (object instanceof L2FolkInstance || !(object instanceof L2Character))
            return 0;
        
        if (object instanceof L2PlayableInstance) 
            return 1500;
        
        return 500;
    }
    
    /**
     * Return the distance after which the object must be remove from _knownObject in function of the object type.<BR><BR>
     *   
     * <B><U> Values </U> :</B><BR><BR>
     * <li> object is not a L2Character : 0 (don't remember it) </li>
     * <li> object is a L2FolkInstance : 0 (don't remember it)</li>
     * <li> object is a L2PlayableInstance : 3000 </li>
     * <li> others : 1000 </li><BR><BR>
     * 
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2Attackable</li><BR><BR> 
     * 
     * @param object The Object to remove from _knownObject
     * 
     */
    public int getDistanceToForgetObject(L2Object object)
    {
        return 2*getDistanceToWatchObject(object);
    }
    
    /**
     * Return False.<BR><BR>
     *   
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2MonsterInstance : Check if the attacker is not another L2MonsterInstance</li>
     * <li> L2PcInstance</li><BR><BR>
     */
    @Override
    public boolean isAutoAttackable(@SuppressWarnings("unused") L2Character attacker) 
    {
        return false;
    }
    
    /**
     * Return the Identifier of the item in the left hand of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    public int getLeftHandItem()
    {
        return _currentLHandId;
    }
    
    /**
     * Return the Identifier of the item in the right hand of this L2NpcInstance contained in the L2NpcTemplate.<BR><BR>
     */
    public int getRightHandItem()
    {
        return _currentRHandId;
    }
    
    /**
     * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
     */
    public boolean isSpoil() 
    {
        return _isSpoil;
    }
    
    /**
     * Set the spoil state of this L2NpcInstance.<BR><BR>
     */
    public void setSpoil(boolean isSpoil) 
    {
        _isSpoil = isSpoil;
    }

    public final int getIsSpoiledBy() 
    { 
        return _isSpoiledBy; 
    }
    
    public final void setIsSpoiledBy(int value) 
    { 
        _isSpoiledBy = value; 
    }

    /**
     * Return the busy status of this L2NpcInstance.<BR><BR>
     */
    public final boolean isBusy()
    {
        return _isBusy;
    }
    
    /**
     * Set the busy status of this L2NpcInstance.<BR><BR>
     */
    public void setBusy(boolean isBusy)
    {
        _isBusy = isBusy;
    }
    
    /**
     * Return the busy message of this L2NpcInstance.<BR><BR>
     */
    public final String getBusyMessage()
    {
        return _busyMessage;
    }
    
    /**
     * Set the busy message of this L2NpcInstance.<BR><BR>
     */
    public void setBusyMessage(String message)
    {
        _busyMessage = message;
    }

    protected boolean canTarget(L2PcInstance player)
    {
        if (player.isOutOfControl())
        {
            player.sendPacket(new ActionFailed());
            return false;
        }
        // Restrict interactions during restart/shutdown
        if (Config.SAFE_REBOOT && Config.SAFE_REBOOT_DISABLE_NPC_ITERACTION && Shutdown.getCounterInstance() != null 
                && Shutdown.getCounterInstance().getCountdown() <= Config.SAFE_REBOOT_TIME)
        {
            sendMessage("NPC interaction disabled during restart/shutdown.");
            player.sendPacket(new ActionFailed());
            return false;
        }
        return true;
    }

    protected boolean canInteract(L2PcInstance player)
    {
        // TODO: NPC busy check etc...
        
        //if (!canTarget(player))
        //    return false;

        if (!isInsideRadius(player, INTERACTION_DISTANCE, false, false))
            return false;

        return true;
    }

    
    /**
     * Manage actions when a player click on the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions on first click on the L2NpcInstance (Select it)</U> :</B><BR><BR>
     * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li>
     * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
     * <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar </li>
     * <li>Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client </li><BR><BR>
     * 
     * <B><U> Actions on second click on the L2NpcInstance (Attack it/Intercat with it)</U> :</B><BR><BR>
     * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
     * <li>If L2NpcInstance is autoAttackable, notify the L2PcInstance AI with AI_INTENTION_ATTACK (after a height verification)</li>
     * <li>If L2NpcInstance is NOT autoAttackable, notify the L2PcInstance AI with AI_INTENTION_INTERACT (after a distance verification) and show message</li><BR><BR>
     * 
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
     * that client wait an other packet</B></FONT><BR><BR>
     * 
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Client packet : Action, AttackRequest</li><BR><BR>
     * 
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2ArtefactInstance : Manage only fisrt click to select Artefact</li><BR><BR>
     * <li> L2GuardInstance : </li><BR><BR>
     * 
     * @param player The L2PcInstance that start an action on the L2NpcInstance
     * 
     */
    @Override
    public void onAction(L2PcInstance player)
    {
        if (!canTarget(player))
        	return;
        try{
        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget())
        {
            if (_log.isDebugEnabled()) _log.debug("new target selected:"+getObjectId());
            
            // Set the target of the L2PcInstance player
            player.setTarget(this);
            
            // Check if the player is attackable (without a forced attack)
            if (isAutoAttackable(player))
            {
                // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
                // The player.getLevel() - getLevel() permit to display the correct color in the select window
                MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
                player.sendPacket(my);

                // Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
                player.sendPacket(su);
            }
            else
            {
                // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
                MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
                player.sendPacket(my);
            }
            
            // Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
            player.sendPacket(new ValidateLocation(this));
        }
        else
        {
            player.sendPacket(new ValidateLocation(this));
            // Check if the player is attackable (without a forced attack) and isn't dead
            if (isAutoAttackable(player) && !isAlikeDead())
            {
                // Check the height difference
                if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
                {
                    // Set the L2PcInstance Intention to AI_INTENTION_ATTACK
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
                    // player.startAttack(this);
                }
                else
                {
                    // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
                    player.sendPacket(new ActionFailed());
                }
            }
            else if(!isAutoAttackable(player)) 
            {
                // Calculate the distance between the L2PcInstance and the L2NpcInstance
                if (!canInteract(player))
                {
                    // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
                else 
                {
                    // Send a Server->Client packet SocialAction to the all L2PcInstance on the _knownPlayer of the L2NpcInstance
                    // to display a social action of the L2NpcInstance on their client
                    SocialAction sa = new SocialAction(getObjectId(), Rnd.get(8));
                    broadcastPacket(sa);
                    
                    // Open a chat window on client with the text of the L2NpcInstance
                    if (isEventMob)
                       L2Event.showEventHtml(player, String.valueOf(getObjectId()));
                    else if (_isEventMobTvT)
                       TvT.showEventHtml(player, String.valueOf(getObjectId()));
                    else if (_isEventMobDM)
                        DM.showEventHtml(player, String.valueOf(getObjectId()));
                    else if (_isEventMobFOS)
                        FortressSiege.showEventHtml(player, String.valueOf(getObjectId()));
                    else if (_isFOS_Artifact)
                    	FortressSiege.showArtifactHtml(player, String.valueOf(getObjectId()));
                    else if (_isEventMobCTF)
                       CTF.showEventHtml(player, String.valueOf(getObjectId()));
                    else if (_isCTF_Flag && player._inEventCTF)
                    	CTF.showFlagHtml(player, String.valueOf(this.getObjectId()),_CTF_FlagTeamName);
                    else if (_isCTF_throneSpawn)
                    	CTF.CheckRestoreFlags();
                    else if (_isEventVIPNPC)
                       VIP.showJoinHTML(player, String.valueOf(getObjectId()));
                    else if (_isEventVIPNPCEnd)
                       VIP.showEndHTML(player, String.valueOf(getObjectId()));
                    else 
                    {
                        Quest[] qlst = getTemplate().getEventQuests(Quest.QuestEventType.NPC_FIRST_TALK);
                        if ( (qlst != null) && qlst.length == 1)
                            qlst[0].notifyFirstTalk(this, player);
                        else
                            showChatWindow(player, 0);
                    }
                    player.sendPacket(new ActionFailed());
                }
            }
            else
            	player.sendPacket(new ActionFailed());
        }
    	}catch (Throwable e){
    		System.out.println("Error: L2NpcInstance--> onAction(){"+e.toString()+"}\n\n");
    		player.sendPacket(new ActionFailed());
    		return;
    	}        
    }
    
    /**
     * Manage and Display the GM console to modify the L2NpcInstance (GM only).<BR><BR>
     * 
     * <B><U> Actions (If the L2PcInstance is a GM only)</U> :</B><BR><BR>
     * <li>Set the L2NpcInstance as target of the L2PcInstance player (if necessary)</li>
     * <li>Send a Server->Client packet MyTargetSelected to the L2PcInstance player (display the select window)</li>
     * <li>If L2NpcInstance is autoAttackable, send a Server->Client packet StatusUpdate to the L2PcInstance in order to update L2NpcInstance HP bar </li>
     * <li>Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance </li><BR><BR>
     * 
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Each group of Server->Client packet must be terminated by a ActionFailed packet in order to avoid
     * that client wait an other packet</B></FONT><BR><BR>
     * 
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Client packet : Action</li><BR><BR>
     * 
     * @param client The thread that manage the player that pessed Shift and click on the L2NpcInstance
     * 
     */
    @Override
    public void onActionShift(L2GameClient client)
    {
        // Get the L2PcInstance corresponding to the thread
        L2PcInstance player = client.getActiveChar();
        if (player == null) return;
        
        // Check if the L2PcInstance is a GM
        if (player.getAccessLevel() >= Config.GM_ACCESSLEVEL)
        {
            // Set the target of the L2PcInstance player
            player.setTarget(this);
            
            // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
            // The player.getLevel() - getLevel() permit to display the correct color in the select window
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            
            // Check if the player is attackable (without a forced attack)
            if (isAutoAttackable(player))
            {
                // Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
                player.sendPacket(su);
            }
            
            // Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            TextBuilder html1 = new TextBuilder("<html><body><center><font color=\"LEVEL\">NPC Information</font></center><br><br>");
            String className = getClass().getName().substring(43);
            html1.append("<br>");
            
            html1.append("Instance Type: " + className + "<br1>Faction: " + getFactionId() + "<br1>Location ID: " + (getSpawn() != null ? getSpawn().getLocation() : 0) + "<br1>");
            
            if (this instanceof L2ControllableMobInstance)
                html1.append("Mob Group: " + MobGroupTable.getInstance().getGroupForMob((L2ControllableMobInstance)this).getGroupId() + "<br>");
            else
                html1.append("Respawn Time: " + (getSpawn()!=null ? (getSpawn().getRespawnDelay() / 1000)+"  Seconds<br>" : "?  Seconds<br>"));
            
            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>Object ID</td><td>"+getObjectId()+"</td><td>NPC ID</td><td>"+getTemplate().getNpcId()+"</td></tr>");
            html1.append("<tr><td>Castle</td><td>"+getCastle().getCastleId()+"</td><td>Coords</td><td>"+getX()+","+getY()+","+getZ()+"</td></tr>");
            html1.append("<tr><td>Level</td><td>"+getLevel()+"</td><td>Aggro</td><td>"+((this instanceof L2Attackable)? ((L2Attackable)this).getAggroRange() : 0)+"</td></tr>");
            html1.append("</table><br>");
            
            html1.append("<font color=\"LEVEL\">Combat</font>");
            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>Current HP</td><td>"+getStatus().getCurrentHp()+"</td><td>Current MP</td><td>"+getStatus().getCurrentMp()+"</td></tr>");
            html1.append("<tr><td>Max.HP</td><td>"+(int)(getMaxHp()/getStat().calcStat(Stats.MAX_HP , 1, this, null))+"*"+getStat().calcStat(Stats.MAX_HP , 1, this, null)+"</td><td>Max.MP</td><td>"+getMaxMp()+"</td></tr>");
            html1.append("<tr><td>P.Atk.</td><td>"+getPAtk(null)+"</td><td>M.Atk.</td><td>"+getMAtk(null,null)+"</td></tr>");
            html1.append("<tr><td>P.Def.</td><td>"+getPDef(null)+"</td><td>M.Def.</td><td>"+getMDef(null,null)+"</td></tr>");
            html1.append("<tr><td>Accuracy</td><td>"+getAccuracy()+"</td><td>Evasion</td><td>"+getEvasionRate(null)+"</td></tr>");
            html1.append("<tr><td>Critical</td><td>"+getCriticalHit(null,null)+"</td><td>Speed</td><td>"+getRunSpeed()+"</td></tr>");
            html1.append("<tr><td>Atk.Speed</td><td>"+getPAtkSpd()+"</td><td>Cast.Speed</td><td>"+getMAtkSpd()+"</td></tr>");
            html1.append("</table><br>");
            
            html1.append("<font color=\"LEVEL\">Basic Stats</font>");
            html1.append("<table border=\"0\" width=\"100%\">");
            html1.append("<tr><td>STR</td><td>"+getStat().getSTR()+"</td><td>DEX</td><td>"+getStat().getDEX()+"</td><td>CON</td><td>"+getStat().getCON()+"</td></tr>");
            html1.append("<tr><td>INT</td><td>"+getINT()+"</td><td>WIT</td><td>"+getStat().getWIT()+"</td><td>MEN</td><td>"+getStat().getMEN()+"</td></tr>");
            html1.append("</table>");
            
            html1.append("<br><center><table><tr><td><button value=\"Edit NPC\" action=\"bypass -h admin_edit_npc " + getTemplate().getNpcId() + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br1></td>");
            html1.append("<td><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><br1></tr>");
            html1.append("<tr><td><button value=\"Show DropList\" action=\"bypass -h admin_show_droplist " + getTemplate().getNpcId() + "\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");           
            html1.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
            // [L2J_JP ADD START]
            html1.append("<tr><td><button value=\"Show Skillist\" action=\"bypass -h admin_show_skilllist_npc " + getTemplate().getNpcId() + "\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td></td></tr>"); 
            // [L2J_JP ADD END]
            html1.append("</table></center><br>");
            html1.append("</body></html>");
            
            html.setHtml(html1.toString());
            player.sendPacket(html);
        }
        // allow to see the stats of npc if option is activated and if not a box
        else if ( (Config.ALT_GAME_VIEWNPC) || (Config.ALT_GAME_VIEWNPC_COMBAT || Config.ALT_GAME_VIEWNPC_BASIC || Config.ALT_GAME_VIEWNPC_DROP  && !(this instanceof L2ChestInstance)))
        {
            // Set the target of the L2PcInstance player
            player.setTarget(this);
            
            // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
            // The player.getLevel() - getLevel() permit to display the correct color in the select window
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            
            // Check if the player is attackable (without a forced attack)
            if (isAutoAttackable(player))
            {   
                // Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
                player.sendPacket(su);
            }
            
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            TextBuilder html1 = new TextBuilder("<html><body>");
            
            //L2EMU_EDIT_START
            
            // npc name
            html1.append("<center><font color=\"LEVEL\">[Npc]</font></center><br>");
            html1.append(getName());
            
            //shows npc combat stats
            if (Config.ALT_GAME_VIEWNPC_COMBAT)
            {
            html1.append("<br><center><font color=\"LEVEL\">[Combat Stats]</font></center>");
            html1.append("<table border=0 width=\"100%\">");
            html1.append("<tr><td>Max.HP</td><td>"+(int)(getMaxHp()/getStat().calcStat(Stats.MAX_HP , 1, this, null))+"*"+(int) getStat().calcStat(Stats.MAX_HP , 1, this, null)+"</td><td>Max.MP</td><td>"+getMaxMp()+"</td></tr>");
            html1.append("<tr><td>P.Atk.</td><td>"+getPAtk(null)+"</td><td>M.Atk.</td><td>"+getMAtk(null,null)+"</td></tr>");
            html1.append("<tr><td>P.Def.</td><td>"+getPDef(null)+"</td><td>M.Def.</td><td>"+getMDef(null,null)+"</td></tr>");
            html1.append("<tr><td>Accuracy</td><td>"+getAccuracy()+"</td><td>Evasion</td><td>"+getEvasionRate(null)+"</td></tr>");
            html1.append("<tr><td>Critical</td><td>"+getCriticalHit(null,null)+"</td><td>Speed</td><td>"+getRunSpeed()+"</td></tr>");
            html1.append("<tr><td>Atk.Speed</td><td>"+getPAtkSpd()+"</td><td>Cast.Speed</td><td>"+getMAtkSpd()+"</td></tr>");
            html1.append("<tr><td>Race</td><td>"+getTemplate().getRace()+"</td><td></td><td></td></tr>");
            html1.append("</table>");
            }
            
            // shows basic stats
            if (Config.ALT_GAME_VIEWNPC_BASIC)
            {
            html1.append("<br><center><font color=\"LEVEL\">[Basic Stats]</font></center>");
            html1.append("<table border=0 width=\"100%\">");
            html1.append("<tr><td>STR</td><td>"+getStat().getSTR()+"</td><td>DEX</td><td>"+getStat().getDEX()+"</td><td>CON</td><td>"+getStat().getCON()+"</td></tr>");
            html1.append("<tr><td>INT</td><td>"+getINT()+"</td><td>WIT</td><td>"+getStat().getWIT()+"</td><td>MEN</td><td>"+getStat().getMEN()+"</td></tr>");
            html1.append("</table>");
            }
             // shows drop stats
            if (Config.ALT_GAME_VIEWNPC_DROP)
            {            
                html1.append("<br><center><font color=\"LEVEL\">[Drop Info]</font></center>");
                html1.append("<br>");

                for(L2DropCategory cat : getTemplate().getDropData())
                    for(L2DropData drop : cat.getAllDrops())
                    if (!Config.ALT_GAME_VIEWNPC_QUESTDROP)
                    {
                        // default info about drop item
                        String name = ItemTable.getInstance().getTemplate(drop.getItemId()).getName();
                        int chance  = drop.getChance();  // drop chance
                        int min_qty = drop.getMinDrop(); // min. q-ty of items
                        int max_qty = drop.getMaxDrop(); // max. q-ty of items

                        // calculating stats using drop-config
                        if (drop.getItemId() == HardcodedItemTable.ADENA_ID)
                        {
                            chance  *= Config.RATE_DROP_ADENA;
                            min_qty *= Config.RATE_DROP_ADENA; // temporary calculation
                            max_qty *= Config.RATE_DROP_ADENA; // temporary calculation
                        }
                        else
                            if (cat.isSweep())
                                chance *= Config.RATE_DROP_SPOIL;
                            else
                                chance *= Config.RATE_DROP_ITEMS;

                        // chance in percents
                        if (chance > drop.MAX_CHANCE) chance = drop.getChance();
                        double percentChance = chance * 0.0001;

                        // output line (if min. q-ty = max. q-ty shows only min.)
                        html1.append(  (drop.isQuestDrop()?"Quest: ":(cat.isSweep()?"Spoil: ":"Drop: ")) + name + "   " + percentChance + "%" + "   ["+ min_qty + ((min_qty==max_qty)?"":" - " + max_qty) +"]<br1>"  );
                    }

                html1.append("</body></html>");
            }

            html.setHtml(html1.toString());
            player.sendPacket(html);
        }
        
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }
    //L2EMU_EDIT_END
    
    /** Return the L2Castle this L2NpcInstance belongs to. */
    public final Castle getCastle()
    {
        // Get castle this NPC belongs to (excluding L2Attackable)
        if (_castleIndex < 0)
        {
            Town town = TownManager.getInstance().getTown(this);
            // Npc was spawned in town
            _isInTown = (town != null);
            
            if (! _isInTown )
            {
            	_castleIndex = CastleManager.getInstance().getClosestCastle(this).getCastleId();
            }
            else
            	if (town.getCastle() != null)
            		_castleIndex = town.getCastle().getCastleId();
            	else
            		_castleIndex = CastleManager.getInstance().getClosestCastle(this).getCastleId();
        }

        return CastleManager.getInstance().getCastleById(_castleIndex);
    }
    
    public final boolean getIsInTown()
    {
        if (_castleIndex < 0) getCastle();
        return _isInTown;
    }
    
    /**
     * Open a quest or chat window on client with the text of the L2NpcInstance in function of the command.<BR><BR>
     * 
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Client packet : RequestBypassToServer</li><BR><BR>
     * 
     * @param command The command string received from client
     * 
     */
    public void onBypassFeedback(L2PcInstance player, String command)
    {
        //if (canInteract(player))
        {
            if (isBusy() && getBusyMessage().length()>0)
            {
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/npcbusy.htm");
                html.replace("%busymessage%", getBusyMessage());
                html.replace("%npcname%", getName());
                html.replace("%playername%", player.getName());
                player.sendPacket(html);
            }
            else if (Config.ALLOW_WYVERN_UPGRADER && command.startsWith("upgrade") && player.getClan()!=null && player.getClan().getHasCastle()!=0)
            {
                String type = command.substring(8);
                
                if(type.equalsIgnoreCase("wyvern"))
                {
                    L2NpcTemplate wind = NpcTable.getInstance().getTemplate(PetDataTable.STRIDER_WIND_ID);
                    L2NpcTemplate star = NpcTable.getInstance().getTemplate(PetDataTable.STRIDER_STAR_ID);
                    L2NpcTemplate twilight = NpcTable.getInstance().getTemplate(PetDataTable.STRIDER_TWILIGHT_ID);
                    
                    L2Summon summon = player.getPet();
                    L2NpcTemplate myPet = summon.getTemplate();
                               
                    if ((myPet.equals(wind) || myPet.equals(star) || myPet.equals(twilight)) 
                            && player.getAdena()>=20000000 
                            && (player.getInventory().getItemByObjectId(summon.getControlItemId())!=null)
                        )
                    {
                        int exchangeItem = PetDataTable.WYVERN_ID;
                        if (!player.reduceAdena("PetUpdate", 20000000, this, true)) return;
                        player.getInventory().destroyItem("PetUpdate", summon.getControlItemId(), 1, player, this);
                        
                        L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(20629);
                        try 
                        {
                            L2Spawn spawn = new L2Spawn(template1);
                            
                            spawn.setLocx(getX()+20);
                            spawn.setLocy(getY()+20);
                            spawn.setLocz(getZ());
                            spawn.setAmount(1);
                            spawn.setHeading(player.getHeading());
                            spawn.setRespawnDelay(1);
                           
                            SpawnTable.getInstance().addNewSpawn(spawn, false);
                            
                            spawn.init();
                            spawn.getLastSpawn().getStatus().setCurrentHp(999999999);
                            spawn.getLastSpawn().setName("baal");
                            spawn.getLastSpawn().setTitle("hell's god");
                            spawn.getLastSpawn().isEventMob = true;
                            spawn.getLastSpawn().isAggressive();
                            spawn.getLastSpawn().decayMe();
                            spawn.getLastSpawn().spawnMe(spawn.getLastSpawn().getX(),spawn.getLastSpawn().getY(),spawn.getLastSpawn().getZ());
                            
                             
                            int level = summon.getLevel();
                            int chance = (level-54)*10;
                            spawn.getLastSpawn().broadcastPacket(new MagicSkillUser(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));
                            spawn.getLastSpawn().broadcastPacket(new MagicSkillUser(spawn.getLastSpawn(), summon, 1034, 1, 1, 1));
                           
                            if(Rnd.nextInt(100)<chance) 
                            {
                                ThreadPoolManager.getInstance().scheduleGeneral(new destroyTemporalSummon(summon, player), 6000);
                                player.getInventory().addItem("PetUpdate", exchangeItem, 1, player, this);
                                
                                NpcHtmlMessage adminReply = new NpcHtmlMessage(getObjectId());    
                                TextBuilder replyMSG = new TextBuilder("<html><body>");
                                replyMSG.append("Congratulations, the evolution suceeded.");
                                replyMSG.append("</body></html>");
                                adminReply.setHtml(replyMSG.toString());
                                player.sendPacket(adminReply);
                            } else 
                            {
                                summon.reduceCurrentHp(summon.getStatus().getCurrentHp(), player);
                            }
                            ThreadPoolManager.getInstance().scheduleGeneral(new destroyTemporalNPC(spawn), 15000);
                        
                            ItemList il = new ItemList(player, true);
                            player.sendPacket(il);
                        } catch(Exception e) 
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        NpcHtmlMessage adminReply = new NpcHtmlMessage(getObjectId());      
                        TextBuilder replyMSG = new TextBuilder("<html><body>");
                       
                        replyMSG.append("You will need 20.000.000 and have the pet summoned for the ceremony ...");
                        replyMSG.append("</body></html>");
                        
                        adminReply.setHtml(replyMSG.toString());
                        player.sendPacket(adminReply);
                    }
                }
                else if (Config.CLASS_MASTER_STRIDER_UPDATE && type.equalsIgnoreCase("strider")) 
                {
                    L2NpcTemplate wind = NpcTable.getInstance().getTemplate(PetDataTable.HATCHLING_WIND_ID);
                    L2NpcTemplate star = NpcTable.getInstance().getTemplate(PetDataTable.HATCHLING_STAR_ID);
                    L2NpcTemplate twilight = NpcTable.getInstance().getTemplate(PetDataTable.HATCHLING_TWILIGHT_ID);
                    
                    L2Summon summon = player.getPet();
                    L2NpcTemplate myPet = summon.getTemplate();
                               
                    if ((myPet.equals(wind) || myPet.equals(star) || myPet.equals(twilight)) 
                            && player.getAdena()>=6000000 
                            && (player.getInventory().getItemByObjectId(summon.getControlItemId())!=null)
                        )
                    {
                        int exchangeItem = PetDataTable.STRIDER_TWILIGHT_ID;
                        if(myPet.equals(wind)) 
                            exchangeItem = PetDataTable.STRIDER_WIND_ID;
                        else if(myPet.equals(star)) 
                            exchangeItem = PetDataTable.STRIDER_STAR_ID;
                        
                        if (!player.reduceAdena("PetUpdate", 6000000, this, true)) return;
                        player.getInventory().destroyItem("PetUpdate", summon.getControlItemId(), 1, player, this);
                        
                        L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(689);
                        try 
                        {
                            L2Spawn spawn = new L2Spawn(template1);
                            
                            spawn.setLocx(getX()+20);
                            spawn.setLocy(getY()+20);
                            spawn.setLocz(getZ());
                            spawn.setAmount(1);
                            spawn.setHeading(player.getHeading());
                            spawn.setRespawnDelay(1);
                           
                            SpawnTable.getInstance().addNewSpawn(spawn, false);
                            
                            spawn.init();
                            spawn.getLastSpawn().getStatus().setCurrentHp(999999999);
                            spawn.getLastSpawn().setName("mercebu");
                            spawn.getLastSpawn().setTitle("baal's son");
                            
                            spawn.getLastSpawn().isAggressive();
                            spawn.getLastSpawn().decayMe();
                            spawn.getLastSpawn().spawnMe(spawn.getLastSpawn().getX(),spawn.getLastSpawn().getY(),spawn.getLastSpawn().getZ());
                            
                            spawn.getLastSpawn().broadcastPacket(new MagicSkillUser(spawn.getLastSpawn(), summon, 297, 1, 1, 1));
                            
                            int level = summon.getLevel();
                            int chance = (level-34)*10;
                            spawn.getLastSpawn().broadcastPacket(new MagicSkillUser(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));
                            spawn.getLastSpawn().broadcastPacket(new MagicSkillUser(spawn.getLastSpawn(), summon, 1034, 1, 1, 1));
                            
                            if(Rnd.nextInt(100)<chance) 
                            {
                                ThreadPoolManager.getInstance().scheduleGeneral(new destroyTemporalSummon(summon, player), 6000);
                                player.getInventory().addItem("PetUpdate", exchangeItem, 1, player, this);
                                NpcHtmlMessage adminReply = new NpcHtmlMessage(getObjectId());      
                                TextBuilder replyMSG = new TextBuilder("<html><body>");
                              
                                replyMSG.append("Congratulations, the evolution suceeded.");
                                replyMSG.append("</body></html>");
                               
                                adminReply.setHtml(replyMSG.toString());
                                player.sendPacket(adminReply);
                            } else 
                            {
                                summon.reduceCurrentHp(summon.getStatus().getCurrentHp(), player);
                            }
                            
                            ThreadPoolManager.getInstance().scheduleGeneral(new destroyTemporalNPC(spawn), 15000);
                            ItemList il = new ItemList(player, true);
                            player.sendPacket(il);
                        } catch(Exception e) 
                        {
                            e.printStackTrace();
                        }
                    }else
                    {
                        NpcHtmlMessage adminReply = new NpcHtmlMessage(getObjectId());      
                        TextBuilder replyMSG = new TextBuilder("<html><body>");
                       
                         replyMSG.append("You will need 6.000.000 and have the pet summoned for the ceremony ...");
                        replyMSG.append("</body></html>");
                        
                        adminReply.setHtml(replyMSG.toString());
                        player.sendPacket(adminReply);
                    }
                }
            }            
            else if (command.equalsIgnoreCase("TerritoryStatus"))
            {
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId()); 
                { 
                    if (getCastle().getOwnerId() > 0)
                    {
                        html.setFile("data/html/territorystatus.htm");
                        L2Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
                        html.replace("%clanname%", clan.getName());
                        html.replace("%clanleadername%", clan.getLeaderName());
                    }
                    else
                    {
                        html.setFile("data/html/territorynoclan.htm");
                    }
                }
                html.replace("%castlename%", getCastle().getName());
                html.replace("%taxpercent%", "" + getCastle().getTaxPercent());
                html.replace("%objectId%", String.valueOf(getObjectId()));
                {
                    if (getCastle().getCastleId() > 6)
                    {
                        html.replace("%territory%", "The Kingdom of Elmore");
                    }
                    else
                    {
                        html.replace("%territory%", "The Kingdom of Aden");
                    }
                }
                player.sendPacket(html); 
            }
            else if (command.startsWith("Quest"))
            {
                String quest = "";
                try 
                {
                   quest = command.substring(5).trim();
                } catch (IndexOutOfBoundsException ioobe) {}

                if (quest.length() == 0)
                    showQuestWindow(player);
                else
                    showQuestWindow(player, quest);
            }
            else if (command.startsWith("Chat"))
            {
                int val = 0;
                try 
                {
                   val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {}
                showChatWindow(player, val);
            }
            else if (command.startsWith("Link"))
        	{
            	String path = command.substring(5).trim();
                if (path.indexOf("..") != -1)
                    return;
        		String filename = "data/html/"+path;
        		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        		html.setFile(filename);
        		html.replace("%objectId%", String.valueOf(getObjectId()));
        		player.sendPacket(html);
        	}
            else if (command.startsWith("EnterRift"))  
            {  
            	try  
            	{  
            	  Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc  
            	  DimensionalRiftManager.getInstance().start(player, RoomType.getRoomTypeEnum(b1), this);
            	}  
            	catch(Exception e){}  
            }  
            else if (command.startsWith("ChangeRiftRoom"))  
            {  
            	if(player.isInParty() && player.getParty().isInDimensionalRift())  
            	{  
            		player.getParty().getDimensionalRift().manualTeleport(player, this);  
            	}  
            	else  
            	{  
            		DimensionalRiftManager.getInstance().handleCheat(player, this);  
            	}  
            }  
            else if (command.startsWith("ExitRift"))  
            {  
            	if(player.isInParty() && player.getParty().isInDimensionalRift())  
            	{  
            		player.getParty().getDimensionalRift().manualExitRift(player, this);  
            	}  
            	else  
            	{  
            		DimensionalRiftManager.getInstance().handleCheat(player, this);  
            	}  
            }  
            else if (command.startsWith("NobleTeleport"))
            {
               if (!player.isNoble())
               {
                   String filename = "data/html/teleporter/nobleteleporter-no.htm";
                   NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                    html.setFile(filename);
                    html.replace("%objectId%", String.valueOf(getObjectId()));
                    html.replace("%npcname%", getName());
                    player.sendPacket(html);
                   return;
               }
                int val = 0;
                try 
                {
                   val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {}
                showChatWindow(player, val);
            }    
            else if (command.startsWith("Loto"))
            {
                int val = 0;
                try 
                {
                   val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException ioobe) {
                } catch (NumberFormatException nfe) {}
                if (val == 0){
                    // new loto ticket
                    for (int i=0;i<5;i++)
                        player.setLoto(i,0);
                }
                showLotoWindow(player, val);
            }
            else if (command.startsWith("CPRecovery"))
            {
                makeCPRecovery(player);
            }
            else if (command.startsWith("SupportMagic"))
            {
                makeSupportMagic(player);
            }
            else if (command.startsWith("multisell"))
            {
            	L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(command.substring(9).trim()), player, false, getCastle().getTaxRate());
            }
            else if (command.startsWith("exc_multisell"))
            {
            	L2Multisell.getInstance().SeparateAndSend(Integer.parseInt(command.substring(13).trim()), player, true, getCastle().getTaxRate());
            }
            else if (command.startsWith("Augment"))
            {
                int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
                switch (cmdChoice)
                {
                    case 1:
                        player.sendPacket(new SystemMessage(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED));
                        player.sendPacket(new ExShowVariationMakeWindow());
                        break;
                    case 2:
                        player.sendPacket(new SystemMessage(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION));
                        player.sendPacket(new ExShowVariationCancelWindow());
                        break;
                }
            }           
            else if (command.startsWith("npcfind_byid"))
            {
                try
                {               
                     L2Spawn spawn = SpawnTable.getInstance().getTemplate(Integer.parseInt(command.substring(12).trim()));
                     if (spawn != null)
                     {
                     player.sendPacket(new RadarControl(0,1,spawn.getLocx(),spawn.getLocy(),spawn.getLocz()));
                     }
                     else
                     {
                     player.sendMessage("Boss isnt in Game - Blame Datapack Developer, Boss ID: "+Integer.parseInt(command.substring(12).trim()));
                     }  
                } catch (NumberFormatException nfe)
                { 
                    player.sendMessage("Wrong command parameters");
                }            
             }
             else if (command.equals("questlist"))
             {    
                 player.sendPacket(new ExQuestInfo());
             }
             else if (command.startsWith("MakeBuffs"))
             {
                 makeBuffs(player,command.substring(9).trim());
             }
            else if (command.equalsIgnoreCase("exchange"))
            {
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/merchant/exchange.htm");
                html.replace("%objectId%", String.valueOf(getObjectId()));
                player.sendPacket(html);
            }
            // [J2J_JP ADD START]
            else if (command.startsWith("open_gate"))
            {
                final DoorTable _doorTable = DoorTable.getInstance();
                int doorId;

                StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
                
                while (st.hasMoreTokens())
                {
                    doorId = Integer.parseInt(st.nextToken());
                    try
                    {
                        _doorTable.getDoor(doorId).openMe();
                        _doorTable.getDoor(doorId).onOpen();
                    }
                    catch(NullPointerException e)
                    {
                        _log.warn("Door Id does not exist.(" +doorId + ")" );
                    }
                }
                return;

            }
            else if (command.equalsIgnoreCase("wake_baium"))
            {
                setTarget(player);
                BaiumManager.getInstance().spawnBaium(this);
            }
            // [J2J_JP ADD END]            
            else if (command.startsWith("remove_death_penalty"))
            {
                NpcHtmlMessage Reply = new NpcHtmlMessage(getObjectId());      
                TextBuilder replyMSG = new TextBuilder("<html><body>Black Judge:<br>");
            	
                if(player.getDeathPenaltyBuffLevel()>0)
                {
                	if (player.getAdena()>= (25200 * Config.RATE_DROP_ADENA))
                	{
            			if (!player.reduceAdena("DeathPenality", (int)(25200 * Config.RATE_DROP_ADENA), this, true)) return;
                		player.setDeathPenaltyBuffLevel(player.getDeathPenaltyBuffLevel()-1);
                		player.updateEffectIcons();
                		player.sendPacket(new SystemMessage(SystemMessageId.DEATH_PENALTY_LIFTED));
                		return;
                	}
                	else
                    	replyMSG.append("The wound you have received from death's touch is too deep to be healed for the money you have to give me. Find more money if you wish death's mark to be fully removed from you.");
                }
                else
                {
                	replyMSG.append("You have no more death wounds that require healing.<br>");
                	replyMSG.append("Go forth and fight, both for this world and your own glory.");
                }
                
                replyMSG.append("</body></html>");
                Reply.setHtml(replyMSG.toString());
            	player.sendPacket(Reply);
            }
            //L2EMU_ADD_BEGIN
            //For Decrease Character level
            else if (command.startsWith("CustomDecreaseCharLevel"))
            {
            	CustomChangeCharLevel(player,command.substring(23).trim(), false);
            }
            else if (command.startsWith("CustomIncreaseCharLevel"))
            {
            	CustomChangeCharLevel(player,command.substring(23).trim(), true);
            }
            //L2EMU_ADD_END
        }
        player.sendPacket(new ActionFailed());
    }

    /* L2EMU_ADD_BEGIN
     * Custom Change (decrease) character level
     * Visor123
     */
    public void CustomChangeCharLevel(L2PcInstance player, String DeltaStr, boolean increase)
    {
    	int DeltaLevel = 0;
    	try
    	{
    		DeltaLevel = Integer.parseInt(DeltaStr);
    	}
    	catch (NumberFormatException  e)
    	{
    		DeltaLevel = 0;
    	}
    	if (DeltaLevel > 0) 
    	{
            if (player == null) return;
            L2CustomChangeCharLevel.ChangeCharLevel(player, DeltaLevel, increase);
    	}
    }
    //L2EMU_ADD_END
    /**
     * Cast buffs on player, this function ignore target type
     * only buff effects are aplied to player
     * 
     * @param player Target player
     * @param buffTemplate Name of buff template
     */
    public void makeBuffs(L2PcInstance player, String buffTemplate)
    {
        int _templateId = 0;
        
        try
        {
            _templateId = Integer.parseInt(buffTemplate); 
        }
        catch (NumberFormatException  e)
        {
            _templateId = BuffTemplateTable.getInstance().getTemplateIdByName(buffTemplate);
        }
        
        if (_templateId>0) makeBuffs(player, _templateId);
    }
    
    /**
     * Cast buffs on player, this function ignore target type
     * only buff effects are aplied to player
     * 
     * @param player Target player
     * @param _templateId Id of buff template
     */
    public void makeBuffs(L2PcInstance player, int _templateId)
    {
        if (player == null) return;
        
        FastList<L2BuffTemplate> _templateBuffs = new  FastList<L2BuffTemplate>();
        
        _templateBuffs = BuffTemplateTable.getInstance().getBuffTemplate(_templateId);
        
        if (_templateBuffs == null  || _templateBuffs.size() == 0) return;

        setTarget(player);
        
        int _priceTotal = 0;
        //TODO: add faction points support (evil33t, im waiting for you ^^ )
        //TODO: add more options for player condition, like: pk, ssq winner/looser...etc
        //TODO: add ancient adena price
        //TODO: add autobuff tasks for npc (with options range,ignorePrice,showCast)
        //TODO: add buff template striction to specified npc ids, merchants like 
        for (L2BuffTemplate _buff:_templateBuffs)
        {
            if ( _buff.checkPlayer(player) && _buff.checkPrice(player)) 
            {
                if (player.getInventory().getAdena() >= (_priceTotal + _buff.getAdenaPrice()))
                {
                    _priceTotal+=_buff.getAdenaPrice();
                     
                    if (_buff.forceCast() || player.getFirstEffect(_buff.getSkill()) == null)
                    {
                        // regeneration ^^
                        getStatus().setCurrentHpMp(getMaxHp(), getMaxMp());
                        
                        // yes, its not for all skills right, but atleast player will know 
                        // for what he paid =)
                        SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT); 
                        sm.addSkillName(_buff.getSkill().getId());
                        player.sendPacket(sm);
                        sm = null;
                        if (_buff.getSkill().getTargetType() == SkillTargetType.TARGET_SELF)
                        {
                        	//L2EMU_ADD
                        	L2Effect oldEffect = player.getFirstEffect(_buff.getSkill().getId());
							
							//remove old effects so we can update all efefcts in progress
                        	if(oldEffect!=null)
							oldEffect.exit();
							
                        	//L2EMU_ADD
                        	
                            // Ignore skill cast time, using 100ms for NPC buffer's animation
                            MagicSkillUser msu = new MagicSkillUser(player, player, _buff.getSkill().getId(), _buff.getSkill().getLevel(), 100, 0);
                            broadcastPacket(msu);
                            
                            for (L2Effect effect : _buff.getSkill().getEffectsSelf(player))
                            {
                                player.addEffect(effect);
                            }
                            // hack for newbie summons
                            if (_buff.getSkill().getSkillType() == SkillType.SUMMON)
                            {
                                player.doCast(_buff.getSkill());
                            }
                        }
                        else
                        {   
                        	//L2EMU_ADD
                        	L2Effect oldEffect = player.getFirstEffect(_buff.getSkill().getId());

                        	//remove old effects so we can update all efefcts in progress
                        	if(oldEffect!=null)
                        	oldEffect.exit();
                        	
                        	//L2EMU_ADD
                        	
                        	// Ignore skill cast time, using 100ms for NPC buffer's animation
                        	MagicSkillUser msu = new MagicSkillUser(this, player, _buff.getSkill().getId(), _buff.getSkill().getLevel(), 100, 0);
                        	broadcastPacket(msu);
                        }
                        
                        for (L2Effect effect : _buff.getSkill().getEffects(this, player))
                        {
                            player.addEffect(effect);
                        }
                        //  Pause between buffs
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e) {}
                    }
                }
            }
        }
        player.reduceAdena("NpcBuffer", _priceTotal, player.getLastFolkNPC(), true);
    }

    /**
     * Return null (regular NPCs don't have weapons instancies).<BR><BR>
     */
    @Override
    public L2ItemInstance getActiveWeaponInstance()
    {
        // regular NPCs dont have weapons instancies
        return null;
    }
    
    /**
     * Return the weapon item equiped in the right hand of the L2NpcInstance or null.<BR><BR>
     */
    @Override
    public L2Weapon getActiveWeaponItem()
    {
        // Get the weapon identifier equiped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().getRhand();
        
        if (weaponId < 1)
            return null;
        
        // Get the weapon item equiped in the right hand of the L2NpcInstance
        L2Item item = ItemTable.getInstance().getTemplate(getTemplate().getRhand());
        
        if (!(item instanceof L2Weapon))
            return null;
        
        return (L2Weapon)item;
    }
    
    /**
     * Return null (regular NPCs don't have weapons instancies).<BR><BR>
     */
    @Override
    public L2ItemInstance getSecondaryWeaponInstance()
    {
        // regular NPCs dont have weapons instancies
        return null;
    }
    
    /**
     * Return the weapon item equiped in the left hand of the L2NpcInstance or null.<BR><BR>
     */
    @Override
    public L2Weapon getSecondaryWeaponItem()
    {
        // Get the weapon identifier equiped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().getLhand();
        
        if (weaponId < 1)
            return null;
        
        // Get the weapon item equiped in the right hand of the L2NpcInstance
        L2Item item = ItemTable.getInstance().getTemplate(getTemplate().getLhand());
        
        if (!(item instanceof L2Weapon))
            return null;
        
        return (L2Weapon)item;
    }
    
    /**
     * Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance.<BR><BR>
     * 
     * @param player The L2PcInstance who talks with the L2NpcInstance
     * @param content The text of the L2NpcMessage
     * 
     */
    public void insertObjectIdAndShowChatWindow(L2PcInstance player, String content)
    {
        // Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
        content = content.replaceAll("%objectId%", String.valueOf(getObjectId()));
        NpcHtmlMessage npcReply = new NpcHtmlMessage(getObjectId());
        npcReply.setHtml(content);
        player.sendPacket(npcReply);
    }
    
    /**
     * Return the pathfile of the selected HTML file in function of the npcId and of the page number.<BR><BR>
     *   
     * <B><U> Format of the pathfile </U> :</B><BR><BR>
     * <li> if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li>
     * <li> if the file exists on the server (page number > 0) : <B>data/html/default/12006-1.htm</B> (npcId-page number)</li>
     * <li> if the file doesn't exist on the server : <B>data/html/npcdefault.htm</B> (message : "I have nothing to say to you")</li><BR><BR>
     * 
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2GuardInstance : Set the pathfile to data/html/guard/12006-1.htm (npcId-page number)</li><BR><BR>
     * 
     * @param npcId The Identifier of the L2NpcInstance whose text must be display
     * @param val The number of the page to display
     * 
     */
    public String getHtmlPath(int npcId, int val)
    {
        String pom = "";
        
        if (val == 0)
            pom = "" + npcId;
        else 
            pom = npcId + "-" + val;
        
        String temp = "data/html/default/" + pom + ".htm";
        
        if (Config.TYPE_CACHE == CacheType.none)
        {
        	// If not running lazy cache the file must be in the cache or it doesnt exist
        	if (HtmCache.getInstance().contains(temp))
        		return temp;
        }
        else
        {
        	if (HtmCache.getInstance().isLoadable(temp))
        		return temp;
        }
        
        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "data/html/npcdefault.htm";
    }
    
    /**
     * Open a choose quest window on client with all quests available of the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance </li><BR><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param quests The table containing quests of the L2NpcInstance
     * 
     */
    public void showQuestChooseWindow(L2PcInstance player, Quest[] quests) 
    {
        TextBuilder sb = new TextBuilder();
        //L2EMU_EDIT_START
        sb.append("<html><body>");
        //L2EMU_EDIT_END
        for (Quest q : quests) 
        {
            //L2EMU_EDIT_START
            sb.append("<a action=\"bypass -h npc_").append(getObjectId())
            .append("_Quest ").append(q.getName()).append("\">[")
            .append(q.getDescr()).append("]</a><br>");
            //L2EMU_EDIT_END
        }
        
        sb.append("</body></html>");
        
        // Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
        insertObjectIdAndShowChatWindow(player, sb.toString());
    }
    
    /**
     * Open a quest window on client with the text of the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the text of the quest state in the folder data/jscript/quests/questId/stateId.htm </li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance </li>
     * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet </li><BR><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param questId The Identifier of the quest to display the message
     * 
     */
    public void showQuestWindow(L2PcInstance player, String questId) 
    {
        String content;
        
        if (player.getWeightPenalty()>=3){  
        	player.sendPacket(new SystemMessage(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT));
            return;
        }
        
        //FileInputStream fis = null;
        
        // Get the state of the selected quest
        QuestState qs = player.getQuestState(questId);
        
        if (qs != null) 
        {
            // If the quest is alreday started, no need to show a window
            if (!qs.getQuest().notifyTalk(this, qs))
                return;
        }
        else
        {
            Quest q = QuestManager.getInstance().getQuest(questId);
            if (q != null) 
            {
                // check for start point
                Quest[] qlst = getTemplate().getEventQuests(Quest.QuestEventType.QUEST_START);
                
                if (qlst != null && qlst.length > 0) 
                {
                    for (Quest element : qlst) {
                        if (element == q) 
                        {
                            qs = q.newQuestState(player);
                            //disabled by mr. becouse quest dialog only show on second click.
                            //if(qs.getState().getName().equalsIgnoreCase("completed"))
                            //{
                            if (!qs.getQuest().notifyTalk(this, qs))
                                return; // no need to show a window
                            //}
                            break;
                        }
                    }
                }
            }
        }
        
        if (qs == null) 
        {
            // no quests found
            content = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>";
        } 
        else 
        {
            questId = qs.getQuest().getName();
            String stateId = qs.getStateId();
            String path = "data/jscript/quests/"+questId+"/"+stateId+".htm";
            content = HtmCache.getInstance().getHtm(path);
            
            if (_log.isDebugEnabled())
            {
                if (content != null)
                {
                    _log.debug("Showing quest window for quest "+questId+" html path: " + path);
                }
                else
                {
                    _log.debug("File not exists for quest "+questId+" html path: " + path);
                }
            }
        }
        
        // Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
        if (content != null)
            insertObjectIdAndShowChatWindow(player, content);
        
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket( new ActionFailed() );
    }
    
    /**
     * Collect awaiting quests/start points and display a QuestChooseWindow (if several available) or QuestWindow.<BR><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * 
     */
    public void showQuestWindow(L2PcInstance player) 
    {
        // collect awaiting quests and start points
        FastList<Quest> options = new FastList<Quest>();
        
        QuestState[] awaits = player.getQuestsForTalk(getTemplate().getNpcId());
        Quest[] starts = getTemplate().getEventQuests(Quest.QuestEventType.QUEST_START);
        
        // Quests are limited between 1 and 999 because those are the quests that are supported by the client. 
        // By limitting them there, we are allowed to create custom quests at higher IDs without interfering 
        if (awaits != null) 
        {
            for (QuestState x : awaits) 
            {
                if (!options.contains(x))
                    if((x.getQuest().getQuestIntId()>0) && (x.getQuest().getQuestIntId()<1000))
                        options.add(x.getQuest());
            }
        }
        
        if (starts != null) 
        {
            for (Quest x : starts) 
            {
                if (!options.contains(x))
                    if((x.getQuestIntId()>0) && (x.getQuestIntId()<1000))
                        options.add(x);
            }
        }
        
        // Display a QuestChooseWindow (if several quests are available) or QuestWindow
        if (options.size() > 1) 
        {
            showQuestChooseWindow(player, options.toArray(new Quest[options.size()]));
        }
        else if (options.size() == 1) 
        {
            showQuestWindow(player, options.get(0).getName());
        }
        else 
        {
            showQuestWindow(player, "");
        }
    }
    
    /**
     * Open a Loto window on client with the text of the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number </li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance </li>
     * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet </li><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param val The number of the page of the L2NpcInstance to display
     * 
     */
    /**
     * Open a Loto window on client with the text of the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number </li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance </li>
     * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet </li><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param val The number of the page of the L2NpcInstance to display
     * 
     */
    // 0 - first buy lottery ticket window
    // 1-20 - buttons
    // 21 - second buy lottery ticket window
    // 22 - selected ticket with 5 numbers
    // 23 - current lottery jackpot
    // 24 - Previous winning numbers/Prize claim
    // >24 - check lottery ticket by item object id
    public void showLotoWindow(L2PcInstance player, int val)
    {
        int npcId = getTemplate().getNpcId();
        String filename;
        SystemMessage sm;
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        
        if (val == 0) // 0 - first buy lottery ticket window
        {
            filename = (getHtmlPath(npcId, 1));
            html.setFile(filename);
        }
        else if (val >= 1 && val <= 21) // 1-20 - buttons, 21 - second buy lottery ticket window
        {
            if (!Lottery.getInstance().isStarted())
            {
                //tickets can't be sold
            	player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD));
                return;
            }
            if (!Lottery.getInstance().isSellableTickets())
            {
                //tickets can't be sold
            	player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE));
                return;
            }

            filename = (getHtmlPath(npcId, 5));
            html.setFile(filename);
            
            int count = 0;
            int found = 0;
            // counting buttons and unsetting button if found
            for (int i = 0; i < 5; i++)
            {
                if (player.getLoto(i) == val)
                {
                         //unsetting button
                    player.setLoto(i, 0);
                    found = 1;
                }
                else if (player.getLoto(i) > 0)
                {
                    count++;
                }
            }
            
                 //if not rearched limit 5 and not unseted value
            if (count < 5 && found == 0 && val <= 20)
                for (int i = 0; i < 5; i++)
                    if (player.getLoto(i) == 0)
                    {
                        player.setLoto(i, val);
                        break;
                    }
            
            //setting pusshed buttons
            count = 0;
            for (int i = 0; i < 5; i++)
                if (player.getLoto(i) > 0)
                {
                    count++;
                    String button = String.valueOf(player.getLoto(i));
                    if (player.getLoto(i) < 10) button = "0" + button;
                    String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
                    String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
                    html.replace(search, replace);
                }
            
            if (count == 5)
            {
                String search = "0\">Return";
                String replace = "22\">The winner selected the numbers above.";
                html.replace(search, replace);
            }
        }
        else if (val == 22) //22 - selected ticket with 5 numbers
        {
            if (!Lottery.getInstance().isStarted())
            {
                //tickets can't be sold
            	player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD));
                return;
            }
            if (!Lottery.getInstance().isSellableTickets())
            {
                //tickets can't be sold
            	player.sendPacket(new SystemMessage(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE));
                return;
            }
                 
            int price = Config.ALT_LOTTERY_TICKET_PRICE;
            int lotonumber = Lottery.getInstance().getId();
            int enchant = 0;
            int type2 = 0;
                 
            for (int i = 0; i < 5; i++)
            {
                if (player.getLoto(i) == 0) return;
                     
                if (player.getLoto(i) < 17) enchant += Math.pow(2, player.getLoto(i) - 1);
                else type2 += Math.pow(2, player.getLoto(i) - 17);
            }
            if (player.getAdena() < price)
            {
                     sm = new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
                     player.sendPacket(sm);
                     return;
            }
            if (!player.reduceAdena("Loto", price, this, true)) return;
            Lottery.getInstance().increasePrize(price);

            sm = new SystemMessage(SystemMessageId.ACQUIRED);
            sm.addNumber(lotonumber);
            sm.addItemName(4442);
            player.sendPacket(sm);
            
            L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), 4442);
            item.setCount(1);
            item.setCustomType1(lotonumber);
            item.setEnchantLevel(enchant);
            item.setCustomType2(type2);
            player.getInventory().addItem("Loto", item, player, this);
            
            InventoryUpdate iu = new InventoryUpdate();
            iu.addItem(item);
            L2ItemInstance adenaupdate = player.getInventory().getItemByItemId(HardcodedItemTable.ADENA_ID);
            iu.addModifiedItem(adenaupdate);
            player.sendPacket(iu);
            
            filename = (getHtmlPath(npcId, 3));
            html.setFile(filename);
        }
        else if (val == 23) //23 - current lottery jackpot
        {
            filename = (getHtmlPath(npcId, 3));
            html.setFile(filename);
        }
        else if (val == 24) // 24 - Previous winning numbers/Prize claim
        {
            filename = (getHtmlPath(npcId, 4));
            html.setFile(filename);
            
            int lotonumber = Lottery.getInstance().getId();
            String message = "";
            for (L2ItemInstance item : player.getInventory().getItems())
            {
                if (item == null) continue;
                if (item.getItemId() == 4442 && item.getCustomType1() < lotonumber)
                {
                    message = message + "<a action=\"bypass -h npc_%objectId%_Loto "
                    + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
                    int[] numbers = Lottery.getInstance().decodeNumbers(item.getEnchantLevel(),
                                                                        item.getCustomType2());
                    for (int i = 0; i < 5; i++)
                    {
                        message += numbers[i] + " ";
                    }
                    int[] check = Lottery.getInstance().checkTicket(item);
                    if (check[0] > 0)
                    {
                        switch (check[0])
                        {
                            case 1:
                                message += "- 1st Prize";
                                break;
                            case 2:
                                message += "- 2nd Prize";
                                break;
                            case 3:
                                message += "- 3th Prize";
                                break;
                            case 4:
                                message += "- 4th Prize";
                                break;
                        }
                        message += " " + check[1] + "a.";
                    }
                    message += "</a><br>";
                }
            }
            if (message == "")
            {
                message += "There is no winning lottery ticket...<br>";
            }
            html.replace("%result%", message);
        }
        else if (val > 24) // >24 - check lottery ticket by item object id
        {
            int lotonumber = Lottery.getInstance().getId();
            L2ItemInstance item = player.getInventory().getItemByObjectId(val);
            if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber) return;
            int[] check = Lottery.getInstance().checkTicket(item);
            
            sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
            sm.addItemName(4442);
            player.sendPacket(sm);
            
            int adena = check[1];
            if (adena > 0)
                player.addAdena("Loto", adena, this, true);
            player.destroyItem("Loto", item, this, false);
            return;
        }
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%race%", "" + Lottery.getInstance().getId());
        html.replace("%adena%", "" + Lottery.getInstance().getPrize());
        html.replace("%ticket_price%", "" + Config.ALT_LOTTERY_TICKET_PRICE);
        html.replace("%prize5%", "" + (Config.ALT_LOTTERY_5_NUMBER_RATE * 100));
        html.replace("%prize4%", "" + (Config.ALT_LOTTERY_4_NUMBER_RATE * 100));
        html.replace("%prize3%", "" + (Config.ALT_LOTTERY_3_NUMBER_RATE * 100));
        html.replace("%prize2%", "" + Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
        html.replace("%enddate%", "" + DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
        player.sendPacket(html);
             
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(new ActionFailed());
    }
    
    public void makeCPRecovery(L2PcInstance player)
    {
        if (getNpcId() != 31225 && getNpcId() != 31226) return;
        if (player.isCursedWeaponEquiped())
        {
           player.sendMessage("Go away, you're not welcome here.");
           return;
        }

        int neededmoney = 100;
        SystemMessage sm;
        if (!player.reduceAdena("RestoreCP", neededmoney, player.getLastFolkNPC(), true)) return;
        player.getStatus().setCurrentCp(getStatus().getCurrentCp()+5000);
        //cp restored
        sm = new SystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
        sm.addString(player.getName());
        player.sendPacket(sm);
    }
    
   
    /**
     * Add Newbie helper buffs to L2Player according to its level.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the range level in wich player must be to obtain buff </li>
     * <li>If player level is out of range, display a message and return </li>
     * <li>According to player level cast buff </li><BR><BR>
     * 
     * <FONT COLOR=#FF0000><B> Newbie Helper Buff list is define in buff templates sql table as "SupportMagic"</B></FONT><BR><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * 
     */
    public void makeSupportMagic(L2PcInstance player)
    {
        // Prevent a cursed weapon weilder of being buffed
        if (player.isCursedWeaponEquiped())
           return;
      
        int _newbieBuffsId = BuffTemplateTable.getInstance().getTemplateIdByName("SupportMagic");
        
        if (_newbieBuffsId == 0) return;
        
        int _lowestLevel = BuffTemplateTable.getInstance().getLowestLevel(_newbieBuffsId);
        int _highestLevel = BuffTemplateTable.getInstance().getHighestLevel(_newbieBuffsId);
        
        // If the player is too high level, display a message and return
        if ((player.getLevel()>0 && player.getLevel() > _highestLevel) || !player.isNewbie())
        {
            String content = "<html><body>Newbie Guide:<br>Only a <font color=\"LEVEL\">novice character of level "+ _highestLevel +" or less</font> can receive my support magic.<br>Your novice character is the first one that you created and raised in this world.</body></html>";
            insertObjectIdAndShowChatWindow(player, content);
            return;
        }
        
        // If the player is too low level, display a message and return
        if (player.getLevel() < _lowestLevel)
        {
            String content = "<html><body>Come back here when you have reached level "+ _lowestLevel +". I will give you support magic then.</body></html>";
            insertObjectIdAndShowChatWindow(player, content);
            return;
        }
        
        makeBuffs(player,_newbieBuffsId);
    }
    
    public void showChatWindow(L2PcInstance player)
    {
        showChatWindow(player, 0);
    }
    
    /**
     * Returns true if html exists
     * @param player
     * @param type
     * @return boolean
     */
    private boolean showPkDenyChatWindow(L2PcInstance player, String type)
    {
        String html = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");

        if (html != null)
        {
            NpcHtmlMessage pkDenyMsg = new NpcHtmlMessage(getObjectId());
            pkDenyMsg.setHtml(html);
            player.sendPacket(pkDenyMsg);
            player.sendPacket(new ActionFailed());
            return true;
        }

        return false;
    }

    /**
     * Open a chat window on client with the text of the L2NpcInstance.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the text of the selected HTML file in function of the npcId and of the page number </li>
     * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance </li>
     * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet </li><BR>
     * 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param val The number of the page of the L2NpcInstance to display
     * 
     */
    public void showChatWindow(L2PcInstance player, int val)
    {
    	if (player.getKarma() > 0)
        {	
    		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof L2MerchantInstance)
			{
				if (showPkDenyChatWindow(player, "merchant"))
					return;
			}
    		else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && this instanceof L2TeleporterInstance)
			{
				if (showPkDenyChatWindow(player, "teleporter"))
					return;
			}
    		else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && this instanceof L2WarehouseInstance)
    		{
    			if (showPkDenyChatWindow(player, "warehouse"))
    				return;
    		}
    		else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof L2FishermanInstance)
			{
				if (showPkDenyChatWindow(player, "fisherman"))
					return;
			}
        }
    	
        if (getTemplate().getType() == "L2Auctioneer" && val==0)
            return;

        int npcId = getTemplate().getNpcId();
        
        /* For use with Seven Signs implementation */
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
        int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
        int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
        int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
        boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
        int compWinner = SevenSigns.getInstance().getCabalHighestScore();
        
        switch (npcId) {
                case 31078:
                case 31079:
                case 31080:
                case 31081:
                case 31082: // Dawn Priests
                case 31083:
                case 31084:
                case 31168:
                case 31692:
                case 31694:
                //L2EMU_ADD_START
                case 31997:
                //L2EMU_ADD_END
                switch (playerCabal) 
                {
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) 
                            if (compWinner == SevenSigns.CABAL_DAWN)
                                if (compWinner != sealGnosisOwner)
                                    filename += "dawn_priest_2c.htm";
                                else
                                    filename += "dawn_priest_2a.htm";
                            else
                                filename += "dawn_priest_2b.htm";                        
                        else
                            filename += "dawn_priest_1b.htm";
                        break;
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) 
                            filename += "dawn_priest_3b.htm";
                        else
                            filename += "dawn_priest_3a.htm";
                        break;
                    default:
                        if (isSealValidationPeriod)
                            if (compWinner == SevenSigns.CABAL_DAWN)
                                filename += "dawn_priest_4.htm";
                            else
                                filename += "dawn_priest_2b.htm";
                        else
                            filename += "dawn_priest_1a.htm";                    
                    break;
                }
                break;
                case 31085:
                case 31086:
                case 31087:
                case 31088: // Dusk Priest
                case 31089:
                case 31090:
                case 31091:
                case 31169:
                case 31693:
                case 31695:
                //L2EMU_ADD_START
                case 31998:
                //L2EMU_ADD_END
                switch (playerCabal) 
                {
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) 
                            if (compWinner == SevenSigns.CABAL_DUSK)
                                if (compWinner != sealGnosisOwner)
                                    filename += "dusk_priest_2c.htm";
                                else
                                    filename += "dusk_priest_2a.htm";
                            else
                                filename += "dusk_priest_2b.htm";
                        else
                            filename += "dusk_priest_1b.htm";
                        break;
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) 
                            filename += "dusk_priest_3b.htm";
                        else 
                            filename += "dusk_priest_3a.htm";
                        break;
                    default:
                        if (isSealValidationPeriod)
                            if (compWinner == SevenSigns.CABAL_DUSK)
                                filename += "dusk_priest_4.htm";
                            else
                                filename += "dusk_priest_2b.htm";
                        else
                            filename += "dusk_priest_1a.htm";
                    break;
                }
                break;
                case 31095: //
                case 31096: //
                case 31097: //
                case 31098: // Enter Necropolises
                case 31099: //
                case 31100: //
                case 31101: //
                case 31102: //
                if (isSealValidationPeriod) 
                {
                    if (Config.ALT_STRICT_SEVENSIGNS)
                    {
                        if (compWinner == SevenSigns.CABAL_DAWN && (playerCabal != SevenSigns.CABAL_DAWN || sealAvariceOwner != SevenSigns.CABAL_DAWN)) 
                        {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                            filename += "necro_no.htm";
                            break;
                        }
                        else if (compWinner == SevenSigns.CABAL_DUSK && (playerCabal != SevenSigns.CABAL_DUSK || sealAvariceOwner != SevenSigns.CABAL_DUSK))
                        {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                            filename += "necro_no.htm";
                            break;
                        }
                    }
                }
                else 
                {
                    if (Config.ALT_STRICT_SEVENSIGNS)
                    {
                        if (playerCabal == SevenSigns.CABAL_NULL)
                        {
                            filename += "necro_no.htm";
                            break;
                        }
                    }
                }
                filename = (getHtmlPath(npcId, val)); // do the default!
                break;
                case 31114: //
                case 31115: //
                case 31116: // Enter Catacombs
                case 31117: //
                case 31118: //
                case 31119: //
                if (isSealValidationPeriod) 
                {
                    if (Config.ALT_STRICT_SEVENSIGNS)
                    {
                        if (compWinner == SevenSigns.CABAL_DAWN && (playerCabal != SevenSigns.CABAL_DAWN || sealGnosisOwner != SevenSigns.CABAL_DAWN)) 
                        {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                            filename += "cata_no.htm";
                            break;
                        }
                        else if (compWinner == SevenSigns.CABAL_DUSK && (playerCabal != SevenSigns.CABAL_DUSK || sealGnosisOwner != SevenSigns.CABAL_DUSK))
                        {
                            player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                            filename += "cata_no.htm";
                            break;
                        }
                    }
                }
                else 
                {
                    if (Config.ALT_STRICT_SEVENSIGNS)
                    {
                        if (playerCabal == SevenSigns.CABAL_NULL)
                        {
                            filename += "cata_no.htm";
                            break;
                        }
                    }
                }
                filename = (getHtmlPath(npcId, val)); // do the default!
                break;
            case 31111: // Gatekeeper Spirit (Disciples)
                if (playerCabal == sealAvariceOwner && playerCabal == compWinner)
                {
                    switch (sealAvariceOwner)
                    {
                        case SevenSigns.CABAL_DAWN:
                            filename += "spirit_dawn.htm";
                            break;
                        case SevenSigns.CABAL_DUSK:
                            filename += "spirit_dusk.htm";
                            break;
                        case SevenSigns.CABAL_NULL:
                            filename += "spirit_null.htm";
                            break;
                    }
                }
                else
                {
                    filename += "spirit_null.htm";
                }
                break;
            case 31112: // Gatekeeper Spirit (Disciples)
                filename += "spirit_exit.htm";
                break;
            case 31127: //
            case 31128: //
            case 31129: // Dawn Festival Guides
            case 31130: //
            case 31131: //
                filename += "festival/dawn_guide.htm";
                break;
            case 31137: //
            case 31138: //
            case 31139: // Dusk Festival Guides
            case 31140: //
            case 31141: //
                filename += "festival/dusk_guide.htm";
                break;
            case 31092: // Black Marketeer of Mammon
                filename += "blkmrkt_1.htm";
                break;
            case 31113: // Merchant of Mammon
                if(Config.ALT_STRICT_SEVENSIGNS)
                {
                    switch (compWinner)
                    {
                        case SevenSigns.CABAL_DAWN:
                            if (playerCabal != compWinner || playerCabal != sealAvariceOwner)
                            {
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                                return;
                            }
                            break;
                        case SevenSigns.CABAL_DUSK:
                            if (playerCabal != compWinner || playerCabal != sealAvariceOwner)
                            {
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                                return;
                            }
                            break;
                    }
                }
                filename += "mammmerch_1.htm";
                break;
            case 31126: // Blacksmith of Mammon
                if (Config.ALT_STRICT_SEVENSIGNS)
                {
                    switch (compWinner)
                    {
                        case SevenSigns.CABAL_DAWN:
                            if (playerCabal != compWinner || playerCabal != sealGnosisOwner)
                            {
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DAWN));
                                return;
                            }
                            break;
                        case SevenSigns.CABAL_DUSK:
                            if (playerCabal != compWinner || playerCabal != sealGnosisOwner)
                            {
                                player.sendPacket(new SystemMessage(SystemMessageId.CAN_BE_USED_BY_DUSK));
                                return;
                            }
                            break;
                    }
                }
                filename += "mammblack_1.htm";
                break;
            case 31132:
            case 31133:
            case 31134:
            case 31135:
            case 31136:  // Festival Witches
            case 31142:
            case 31143:
            case 31144:
            case 31145:
            case 31146:             
                filename += "festival/festival_witch.htm";
                break;
            case 31688:
                if (player.isNoble())
                    filename = Olympiad.OLYMPIAD_HTML_FILE + "noble_main.htm";
                else
                    filename = (getHtmlPath(npcId, val));
                break;
            case 31690:
            case 31769:
            case 31770:
            case 31771:
            case 31772:
                if (player.isHero() && !player.isFakeHero())
                    filename = Olympiad.OLYMPIAD_HTML_FILE + "hero_main.htm";
                else
                    filename = (getHtmlPath(npcId, val));
                break;
            default:
            	if (npcId >= 31865 && npcId <= 31918)  
            	{  
            		filename += "rift/GuardianOfBorder.htm";  
            		break;  
            	}  
                if ((npcId >= 31093 && npcId <= 31094) ||
                        (npcId >= 31172 && npcId <= 31201) ||
                        (npcId >= 31239 && npcId <= 31254))
                    return;
            // Get the text of the selected HTML file in function of the npcId and of the page number
            filename = (getHtmlPath(npcId, val));
            break;
        }
        
        // Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance 
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        
        //String word = "npc-"+npcId+(val>0 ? "-"+val : "" )+"-dialog-append";
        
        if (this instanceof L2MerchantInstance)
            if (Config.LIST_PET_RENT_NPC.contains(npcId))
                html.replace("_Quest", "_RentPet\">Rent Pet</a><br><a action=\"bypass -h npc_%objectId%_Quest");
        
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
        player.sendPacket(html);
        
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket( new ActionFailed() );
    }
    
    /**
     * Open a chat window on client with the text specified by the given file name and path,<BR>
     * relative to the datapack root.
     * <BR><BR>
     * Added by Tempy 
     * @param player The L2PcInstance that talk with the L2NpcInstance
     * @param filename The filename that contains the text to send
     *  
     */
    public void showChatWindow(L2PcInstance player, String filename)
    {
        // Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance 
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%objectId%",String.valueOf(getObjectId()));
        player.sendPacket(html);
        
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket( new ActionFailed() );
    }
    
    /**
     * Return the Exp Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_XP).<BR><BR>
     */
    public int getExpReward()
    {
        double rateXp = getStat().calcStat(Stats.MAX_HP , 1, this, null);
        return (int)(getTemplate().getRewardExp() * rateXp * Config.RATE_XP);
    }
    
    /**
     * Return the SP Reward of this L2NpcInstance contained in the L2NpcTemplate (modified by RATE_SP).<BR><BR>
     */
    public int getSpReward()
    {
        double rateSp = getStat().calcStat(Stats.MAX_HP , 1, this, null);
        return (int)(getTemplate().getRewardSp() * rateSp * Config.RATE_SP);
    }
    
    /**
     * Kill the L2NpcInstance (the corpse disappeared after 7 seconds).<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Create a DecayTask to remove the corpse of the L2NpcInstance after 7 seconds </li>
     * <li>Set target to null and cancel Attack or Cast </li>
     * <li>Stop movement </li>
     * <li>Stop HP/MP/CP Regeneration task </li>
     * <li>Stop all active skills effects in progress on the L2Character </li>
     * <li>Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform </li>
     * <li>Notify L2Character AI </li><BR><BR>
     * 
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2Attackable </li><BR><BR>
     * 
     * @param killer The L2Character who killed it
     * 
     */
    @Override
    public boolean doDie(L2Character killer) 
    {
        if (!super.doDie(killer))
            return false;

        // normally this wouldn't really be needed, but for those few exceptions, 
        // we do need to reset the weapons back to the initial templated weapon.
        _currentLHandId = getTemplate().getLhand();
        _currentRHandId = getTemplate().getRhand();
        _currentCollisionHeight = getTemplate().getCollisionHeight();
        _currentCollisionRadius = getTemplate().getCollisionRadius();
        DecayTaskManager.getInstance().addDecayTask(this);
        return true;
    }

    /**
     * Set the spawn of the L2NpcInstance.<BR><BR>
     * 
     * @param spawn The L2Spawn that manage the L2NpcInstance
     * 
     */
    public void setSpawn(L2Spawn spawn)
    {
        _spawn = spawn;
    }
    
    /**
     * Remove the L2NpcInstance from the world and update its spawn object (for a complete removal use the deleteMe method).<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Remove the L2NpcInstance from the world when the decay task is launched </li>
     * <li>Decrease its spawn counter </li>
     * <li>Manage Siege task (killFlag, killCT) </li><BR><BR>
     * 
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR><BR>
     * 
     * @see net.sf.l2j.gameserver.model.L2Object#decayMe()
     */
    @Override
    public void onDecay()
    {
         if (isDecayed()) return;
         setDecayed(true);
         
         // reset champion status if the thing is a mob
         setChampion(false);
         
        // Remove the L2NpcInstance from the world when the decay task is launched
        super.onDecay();
        
        // Decrease its spawn counter
        if (_spawn != null)
            _spawn.decreaseCount(this);
    }
    
    /**
     * Remove PROPERLY the L2NpcInstance from the world.<BR><BR>
     * 
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Remove the L2NpcInstance from the world and update its spawn object </li>
     * <li>Remove all L2Object from _knownObjects and _knownPlayer of the L2NpcInstance then cancel Attak or Cast and notify AI </li>
     * <li>Remove L2Object object from _allObjects of L2World </li><BR><BR>
     * 
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR><BR>
     * 
     */
    public void deleteMe()
    {
        //FIXME: this is just a temp hack, we should find a better solution
        
        try { decayMe(); } catch (Throwable t) {_log.fatal("deletedMe(): " + t); }
        
        // Remove all L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI
        try { getKnownList().removeAllKnownObjects(); } catch (Throwable t) {_log.fatal("deletedMe(): " + t); }
        
        // Remove L2Object object from _allObjects of L2World
        L2World.getInstance().removeObject(this);
    }
    
    /**
     * Return the L2Spawn object that manage this L2NpcInstance.<BR><BR>
     */
    public L2Spawn getSpawn()
    {
        return _spawn;
    }
    
    @Override
    public String toString()
    {
        return getTemplate().getName();
    }

    public boolean isDecayed()
    {
       return _isDecayed;
    }
    
    public void setDecayed(boolean decayed)
    {
       _isDecayed = decayed;
    }
  
    public void endDecayTask()
    {
        if (!isDecayed()) 
        {
           DecayTaskManager.getInstance().cancelDecayTask(this);
           onDecay();
       }
    }
    
    public boolean isMob() // rather delete this check
    {
    	return false; // This means we use MAX_NPC_ANIMATION instead of MAX_MONSTER_ANIMATION
    }
    
    // Two functions to change the appearance of the equipped weapons on the NPC
    // This is only useful for a few NPCs and is most likely going to be called from AI
    public void setLHandId(int newWeaponId)
    {
        _currentLHandId = newWeaponId;
    }

    public void setRHandId(int newWeaponId)
    {
        _currentRHandId = newWeaponId;
    }

    public void setCollisionHeight(int height)
    {
        _currentCollisionHeight = height;
    }

    public void setCollisionRadius(int radius)
    {
        _currentCollisionRadius = radius;
    }

    public int getCollisionHeight()
    {
        return _currentCollisionHeight;
    }

    public int getCollisionRadius()
    {
        return _currentCollisionRadius;
    }

     
     
     
   //L2EMU_ADD_START
    public void cancel(L2PcInstance plyr)  
   {  
            plyr.stopAllEffects();  
   } 
    @SuppressWarnings("unused")
	public boolean isPrivateEventMob;
    //L2EMU_ADD_END
}
