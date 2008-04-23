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
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2FriendList;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.util.Broadcast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author evill33t
 * 
 */
public class Wedding implements IVoicedCommandHandler
{
    protected static Log _log = LogFactory.getLog(Wedding.class);
    private static final String[] VOICED_COMMANDS = { "divorce", "engage", "gotolove" };

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.handler.IVoicedCommandHandler#useVoicedCommand(String, net.sf.l2j.gameserver.model.L2PcInstance), String)
     */
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        if (command.startsWith("engage"))
            return Engage(activeChar);
        else if (command.startsWith("divorce"))
            return Divorce(activeChar);
        else if (command.startsWith("gotolove"))
            return GoToLove(activeChar);
        return false;
    }
    
    public boolean Divorce(L2PcInstance activeChar)
    {
        if(activeChar.getPartnerId() == 0)
            return false;

        int _partnerId = activeChar.getPartnerId();
        int _coupleId = activeChar.getCoupleId();
        int AdenaAmount = 0;
        
        if (activeChar.isMaried())
        {
            activeChar.sendMessage("You are now divorced."); 

            AdenaAmount = (activeChar.getAdena()/100)*Config.WEDDING_DIVORCE_COSTS;
            activeChar.getInventory().reduceAdena("Wedding", AdenaAmount, activeChar, null);
            
        }
        else
            activeChar.sendMessage("You have broken up as a couple.");
       
        L2PcInstance partner;
        partner = (L2PcInstance)L2World.getInstance().findObject(_partnerId);
        
        if (partner != null)
        {
            partner.setPartnerId(0);
            if (partner.isMaried())
                partner.sendMessage("Your fiance has decided to divorce from you.");
            else
                partner.sendMessage("Your fiance has decided to break the engagement with you.");

            // give adena
            if(AdenaAmount>0)
                partner.addAdena("WEDDING", AdenaAmount, null, false);
        }
        
        CoupleManager.getInstance().deleteCouple(_coupleId);
        return true;
    }

    public boolean Engage(L2PcInstance activeChar)
    {
        // check target
        if (activeChar.getTarget() == null)
        {
            activeChar.sendMessage("You have no one targeted.");
            return false;
        }
        
        // check if target is a l2pcinstance
        if (!(activeChar.getTarget() instanceof L2PcInstance))
        {
            activeChar.sendMessage("You can only ask another player for engagement");

            return false;
        }
        
        // check if player is already engaged
        if (activeChar.getPartnerId()!=0)
        {
            activeChar.sendMessage("You are already engaged.");
            if (Config.WEDDING_PUNISH_INFIDELITY)
            {
                activeChar.startAbnormalEffect(L2Character.ABNORMAL_EFFECT_BIG_HEAD); // give player a Big Head
                // lets recycle the sevensigns debuffs
                int skillId;
    
                int skillLevel = 1;
                
                if (activeChar.getLevel() > 40)
                    skillLevel = 2;
                
                if (activeChar.isMageClass())
                    skillId = 4361;
                else
                    skillId = 4362;
                
                L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
                
                if (activeChar.getFirstEffect(skill) == null)
                {
                    skill.getEffects(activeChar, activeChar);
                    SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
                    sm.addSkillName(skillId);
                    activeChar.sendPacket(sm);
                }
            }   
            return false;
        }

        L2PcInstance ptarget = (L2PcInstance)activeChar.getTarget();
        
        // check if player target himself
        if (ptarget.getObjectId() == activeChar.getObjectId())
        {
            activeChar.sendMessage("Is there something wrong with you, are you trying to go out with yourself?");
            return false;
        }

        if (ptarget.isMaried())
        {
            activeChar.sendMessage("Player already married.");
            return false;
        }

        if (ptarget.getPartnerId() != 0)
        {
            activeChar.sendMessage("Player already engaged.");
            return false;
        }

        if (ptarget.isEngageRequest())
        {
            activeChar.sendMessage("Player already asked by someone else.");
            return false;
        }
        
        if (ptarget.getAppearance().getSex() == activeChar.getAppearance().getSex() && !Config.WEDDING_SAMESEX)
        {
            activeChar.sendMessage("You can't ask someone of the same sex for engagement.");
            return false;
        }
        
        if (!L2FriendList.isInFriendList(activeChar, ptarget))
        {
            activeChar.sendMessage("The player you want to ask is not on your friends list, you must first be on each others friends list before you choose to engage.");
            return false;
        }
        
        ptarget.setEngageRequest(true, activeChar.getObjectId());
        ptarget.sendPacket(new ConfirmDlg(614,activeChar.getName()+" asking you to engage. Do you want to start a new relationship?"));
        return true;
    }
    
    public boolean GoToLove(L2PcInstance activeChar)
    {
        if (activeChar.isCastingNow() || activeChar.isMovementDisabled() 
                || activeChar.isMuted() || activeChar.isAlikeDead())
            return false;
        if (!activeChar.isMaried())
        {
            activeChar.sendMessage("You're not married."); 
            return false;
        }
        else if (activeChar.getPartnerId()==0)
        {
            activeChar.sendMessage("Couldn't find your fiance in Database - Inform a Gamemaster.");
            _log.error("Married but couldn't find partner for "+activeChar.getName());
            return false;
        }
        // Check to see if the player is in olympiad.
        else if (activeChar.isInOlympiadMode())
        {
            activeChar.sendMessage("You are in Olympiad!");
            return false;
        }
        // Check to see if the player is in observer mode
        else if (activeChar.inObserverMode())
        {
            activeChar.sendMessage("You are in observer mode.");
            return false;
        }
        // Check to see if the player is in an event
        else if (activeChar.isInFunEvent())
        {
            activeChar.sendMessage("You are in event now.");
            return false;
        }
        // Check to see if the player is in a festival.
        else if (activeChar.isFestivalParticipant()) 
        {
            activeChar.sendMessage("You can't escape from a festival.");
            return false;
        }
         // Check to see if the player is in dimensional rift.
        else if (DimensionalRiftManager.getInstance().checkIfInRiftZone
                (activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
        {
            activeChar.sendMessage("You are in the dimensional rift.");
            return false;
        }
        // Check to see if player is in jail
        else if (activeChar.isInJail() || ZoneManager.getInstance().checkIfInZone(ZoneType.Jail, activeChar))
        {
            activeChar.sendMessage("You can't escape from jail.");
            return false;
        }
        // Check if player is in Siege
        else if (CastleManager.getInstance().getCastle(activeChar) != null
        		&& CastleManager.getInstance().getCastle(activeChar).getSiege().getIsInProgress())
        {
            activeChar.sendMessage("You are in siege, you can't go to your partner.");
            return false;
        }
        // Check if player is in Duel
        else if (activeChar.isInDuel())
        {
            activeChar.sendMessage("You are in a duel!");
            return false;
        }
        // Check if player is a Cursed Weapon owner
        else if (activeChar.isCursedWeaponEquiped())
        {
            activeChar.sendMessage("You are currently holding a cursed weapon.");
            return false;
        }
        // Check if player is in a Monster Derby Track
        else if (ZoneManager.getInstance().checkIfInZone(ZoneType.MonsterDerbyTrack, activeChar))
        {
        	activeChar.sendMessage("You can't escape from a Monster Derby Track.");
        	return false;
        }

        L2PcInstance partner;
        partner = (L2PcInstance)L2World.getInstance().findObject(activeChar.getPartnerId());
        if (partner == null)
        {
            activeChar.sendMessage("Your partner is not online.");
            return false;
        }
        else if (partner.isInJail() || ZoneManager.getInstance().checkIfInZone(ZoneType.Jail, partner))
        {
            activeChar.sendMessage("Your partner is in jail.");
            return false;
        }
        else if (partner.isInOlympiadMode())
        {
            activeChar.sendMessage("Your partner is in Olympiad now.");
            return false;
        }
        else if (partner.inObserverMode())
        {
            activeChar.sendMessage("Your partner is in observer mode.");
            return false;
        }
        else if (partner.isInDuel())
        {
            activeChar.sendMessage("Your partner is in a duel.");
            return false;
        }
        else if (partner.isInFunEvent())
        {
            activeChar.sendMessage("Your partner is in an event.");
            return false;
        }
        else if (DimensionalRiftManager.getInstance().checkIfInRiftZone
                (partner.getX(), partner.getY(), partner.getZ(), false))
        {
            activeChar.sendMessage("Your partner is in dimensional rift.");
            return false;
        }
        else if (partner.isFestivalParticipant())
        {
        	activeChar.sendMessage("Your partner is in a festival.");
        	return false;
        }
        else if(CastleManager.getInstance().getCastle(partner) != null 
        		&& CastleManager.getInstance().getCastle(partner).getSiege().getIsInProgress())
        {
        	if (partner.getAppearance().getSex())
        		activeChar.sendMessage("Your partner is in siege, you can't go to her.");
        	else
        		activeChar.sendMessage("Your partner is in siege, you can't go to him.");
        	return false;
        }
        else if (partner.isCursedWeaponEquiped())
        {
        	activeChar.sendMessage("Your partner is currently holding a cursed weapon.");
        	return false;
        }
        else if (ZoneManager.getInstance().checkIfInZone(ZoneType.MonsterDerbyTrack, partner))
        {
        	activeChar.sendMessage("Your partner is in a Monster Derby Track.");
        	return false;
        }
        
        int teleportTimer = Config.WEDDING_TELEPORT_INTERVAL*1000;
        
        activeChar.sendMessage("After " + teleportTimer/60000 + " min. you will be teleported to your fiance.");
        activeChar.getInventory().reduceAdena("Wedding", Config.WEDDING_TELEPORT_PRICE, activeChar, null);
        
        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        //SoE Animation section
        activeChar.setTarget(activeChar);
        activeChar.disableAllSkills();

        MagicSkillUser msk = new MagicSkillUser(activeChar, 1050, 1, teleportTimer, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000/*900*/);
        SetupGauge sg = new SetupGauge(0, teleportTimer);
        activeChar.sendPacket(sg);
        //End SoE Animation section

        EscapeFinalizer ef = new EscapeFinalizer(activeChar,partner.getX(),partner.getY(),partner.getZ(),partner.isIn7sDungeon());
        // continue execution later
        activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, teleportTimer));
        activeChar.setSkillCastEndTime(10+GameTimeController.getGameTicks()+teleportTimer/GameTimeController.MILLIS_IN_TICK);
        
        return true;
    }

    private static class EscapeFinalizer implements Runnable
    {
        private L2PcInstance _activeChar;
        private int _partnerx;
        private int _partnery;
        private int _partnerz;
        private boolean _to7sDungeon;
        
        EscapeFinalizer(L2PcInstance activeChar, int x, int y, int z, boolean to7sDungeon)
        {
            _activeChar = activeChar;
            _partnerx = x;
            _partnery = y;
            _partnerz = z;
            _to7sDungeon = to7sDungeon;
        }
        
        public void run()
        {
            if (_activeChar.isDead()) 
                return; 
            _activeChar.setIsIn7sDungeon(_to7sDungeon);
            _activeChar.enableAllSkills();

            try 
            {
                _activeChar.teleToLocation(_partnerx, _partnery, _partnerz);
            }
            catch (Throwable e) { _log.error(e.getMessage(),e); }
        }
    }
    
    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.handler.IVoicedCommandHandler#getVoicedCommandList()
     */
    public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}