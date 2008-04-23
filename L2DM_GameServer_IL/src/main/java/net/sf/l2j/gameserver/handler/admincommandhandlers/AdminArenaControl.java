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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedSkillTable;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *<b> This class handles following admin commands: </b><br><br> 
 *
 * <li> admin_arenaroot  = roots all players in a pvo zone <br>
 * <li> admin_arenakill = kills all players in a pvo zone <br>
 * <li> admin_arenares =  ressurects all players in a pvo zone <br>
 * <li> admin_arenasleep =  sleeps all players in a pvo zone <br>
 * <li> admin_arenaparalyze =   paralyzes all players in a pvo zone <br>
 * <li> admin_arenafear =  fears all players in a pvo zone <br>
 * <li> admin_arenacancel =  cancel all players in a pvo zone <br><br>
 * 
 *<b> Usage: </b><br><br> 
 *
 * <li> //arenaroot <br>
 * <li> //arenakill <br>
 * <li> //arenares <br>
 * <li> //arenasleep <br>
 * <li> //arenaparalyze <br>
 * <li> //arenafear <br>
 * <li> //arenacancel <br><br> 
 *
 * @version $Revision: 120 $ $Date: 2007/08/04 10:46:00 $ <br><br> 
 *
 * @author Rayan RPG for L2Emu Project !
 *
 */
public class AdminArenaControl implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS = 
	{   
		"admin_arenaroot",
		"admin_arenakill",
		"admin_arenares",
		"admin_arenasleep",
		"admin_arenaparalyze",
		"admin_arenafear",
		"admin_arenacancel"
	};
	private final static Log _log = LogFactory.getLog(AdminArenaControl.class.getName());
	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{	
		HardcodedSkillTable hst = new HardcodedSkillTable();
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}
		if (command.startsWith("admin_arenaroot"))
		{
			if(_log.isDebugEnabled())
				_log.info("command arena root requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;

					counter++;
					SkillTable.getInstance().getInfo(hst.ROOT, 30);
					admin.sendMessage("System: You Rooted "+counter+" players.");
				}
			}
		}
		if (command.startsWith("admin_arenasleep"))//retorna o ARENA SLEEP
		{
			if(_log.isDebugEnabled())
				_log.info("command arena sleep requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers()) 
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM()) 
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue; 
					counter++;
					SkillTable.getInstance().getInfo(hst.SLEEP, 30);
					admin.sendMessage("System: You Sleeped "+counter+" players.");
				}
			}
		}
		if (command.startsWith("admin_arenakill"))//ARENA KILL
		{
			if(_log.isDebugEnabled())
				_log.info("command arena kill requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;
					counter++;

					player.getStatus().setCurrentHp(0);//set hp tp player.
					player.doDie(admin);
					admin.sendMessage("System: You Killed "+counter+" players.");
				}
			}
		}
		if (command.startsWith("admin_arenares"))// ARENA RES
		{
			if(_log.isDebugEnabled())
				_log.info("command arena ress requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM() && player.isDead())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;
					counter++;
					player.doRevive();
					admin.sendMessage("System: You Ressurected "+counter+" players.");

				}
			}
		}
		if (command.startsWith("admin_arenaparalyze"))//ARENA PARALYZE
		{
			if(_log.isDebugEnabled())
				_log.info("command arena paralyze requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;

					counter++;
					SkillTable.getInstance().getInfo(hst.PARALYZE, 30);  //paralyze skill id
					admin.sendMessage("System: You Paralyzed "+counter+" players.");
				}
			}
		}
		if (command.startsWith("admin_arenafear"))//ARENA FEAR
		{
			if(_log.isDebugEnabled())
				_log.info("command arena fear requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;

					counter++;
					SkillTable.getInstance().getInfo(hst.FEAR, 30);  //fear skill id
					admin.sendMessage("System: You Feared "+counter+" players.");
				}
			}
		}
		if (command.startsWith("admin_arenacancel"))//ARENA CANCEL
		{
			if(_log.isDebugEnabled())
				_log.info("command arena cancel requested");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(ZoneManager.getInstance().checkIfInZonePvP(player) &&  !player.isGM())
				{
					if (!Util.checkIfInRange(500,admin,admin,true)) continue;

					counter++;
					SkillTable.getInstance().getInfo(hst.CANCEL, 30);  //cancel skill id

					admin.sendMessage("System: You Cancelled "+counter+" Players.");
				}
			}
		}
		return true;
	}
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	/**
	 * 
	 * @param level
	 * @return
	 */
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
}