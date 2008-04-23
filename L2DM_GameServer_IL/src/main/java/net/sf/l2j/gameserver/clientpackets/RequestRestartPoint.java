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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.MapRegionManager;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2SiegeClan;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.entity.events.L2RaidEvent;
import net.sf.l2j.gameserver.model.mapregion.TeleportWhereType;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.Revive;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.util.IllegalPlayerAction;
import net.sf.l2j.gameserver.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RequestRestartPoint extends L2GameClientPacket
{
	private static final String _C__6d_REQUESTRESTARTPOINT = "[C] 6d RequestRestartPoint";
	private final static Log _log = LogFactory.getLog(RequestRestartPoint.class.getName());	
	
	protected int     _requestedPointType;
	protected boolean _continuation;
	
	/**
	 * packet type id 0x6d
	 * format: c
	 * @param decrypt
	 */
	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
	}
	
	class DeathTask implements Runnable
	{
		L2PcInstance activeChar;
		DeathTask (L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}
		
		public void run()
		{
			try
			{
				Location loc = null;
				Castle castle=null;
				
				if (activeChar.isInJail()) _requestedPointType = 27;
				else if (activeChar.isFestivalParticipant()) _requestedPointType = 4;

				switch (_requestedPointType)
				{
					case 1: // to clanhall
						if (activeChar.getClan().getHasHideout() == 0)
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.ClanHall);
						
						if (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan())!= null &&
								ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null)
						{
							activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
						}
						break;

					case 2: // to castle
						Boolean isInDefense = false;
						castle = CastleManager.getInstance().getCastle(activeChar);
						if (castle != null && castle.getSiege().getIsInProgress())
						{
							//siege in progress
							if (castle.getSiege().checkIsDefender(activeChar.getClan()))
								isInDefense = true;
						}
						if (activeChar.getClan() == null || (activeChar.getClan().getHasCastle() == 0 && !isInDefense))
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.Castle);
						break;

					case 3: // to siege HQ
						L2SiegeClan siegeClan = null;
						Siege siege = SiegeManager.getInstance().getSiege(activeChar);
						
						if (siege != null && siege.getIsInProgress())
							siegeClan = siege.getAttackerClan(activeChar.getClan());
						
						if (siegeClan == null || siegeClan.getFlag().size() == 0)
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.SiegeFlag);
						break;

					case 4: // Fixed or Player is a festival participant
						if (!activeChar.isGM() && !activeChar.isFestivalParticipant())
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						if (activeChar.isGM()) activeChar.restoreExp(100.0);
						loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()); // spawn them where they died
						break;

					case 27: // to jail
						if (!activeChar.isInJail()) return;
						loc = new Location(-114356, -249645, -2984);
						break;

					default:
						if (ZoneManager.getInstance().checkIfInZone(ZoneType.Jail , activeChar) ||
							ZoneManager.getInstance().checkIfInZone(ZoneType.NoEscape , activeChar) )
						{
							if (loc == null)
								loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()); // spawn them where they died
						}
						else
							loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.Town);
						break;
				}
				//Teleport and revive
				activeChar.setIsPendingRevive(true);
				activeChar.teleToLocation(loc, true);
			}
			catch (Throwable e) {}
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(null);
			activeChar.broadcastPacket(new Revive(activeChar));
			return;
		}
		else if(!activeChar.isAlikeDead())
        {
        	//L2EMU_ADD_START
        	if(_log.isDebugEnabled())
        		//L2EMU_ADD_END
        	_log.warn("Living player ["+activeChar.getName()+"] called RestartPointPacket! Ban this player!");
        	return;
        }
        //L2EMU_ADD
        if (activeChar.inClanEvent || activeChar.inPartyEvent || activeChar.inSoloEvent)
        {
        	activeChar.inClanEvent = false;
        	activeChar.inPartyEvent = false;
        	activeChar.inSoloEvent = false;
        	if (L2RaidEvent._eventType == 2)
        	{
        		if(L2RaidEvent._participatingPlayers.contains(activeChar))
        			// Clear player from Event.
        			L2RaidEvent._participatingPlayers.remove(activeChar);
        	}
        	if (L2RaidEvent._eventType == 3)
        	{
        		if (activeChar.getParty()!=null)
        			activeChar.leaveParty();
        		activeChar.sendMessage("You have been kicked from the party");
        	}
        	activeChar.sendMessage("You've been erased from the event!");
        	int num = L2RaidEvent._participatingPlayers.size();
        	if (num > 0 && num!=1)
        		num -= 1;
        	else
        		L2RaidEvent.hardFinish();
        }
        //L2EMU_ADD
		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(),activeChar.getY(), activeChar.getZ());
		if (castle != null && castle.getSiege().getIsInProgress())
		{
			//DeathFinalizer df = new DeathFinalizer(10000);
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			if (activeChar.getClan() != null
					&& castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getAttackerRespawnDelay()/1000 + " seconds.");
				activeChar.sendPacket(sm);
			}
			else
			{
				// Schedule respawn delay for defender with penalty for CT lose
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getDefenderRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getDefenderRespawnDelay()/1000 + " seconds.");
				activeChar.sendPacket(sm);
			}
			return;
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), 1);
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__6d_REQUESTRESTARTPOINT;
	}
}
