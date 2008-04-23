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
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *<b> This class handles following admin commands: </b><br><br> 
 *
 * <li> admin_masskill = kills all players in server <br>
 * <li> admin_massress = ressurect all players in server  <br><br>
 *
 *<b> Usage: </b><br><br> 
 *
 * <li> //masskill <br>
 * <li> //massress <br><br>
 * 
 * @author Rayan RPG for L2Emu Project!
 *  
 */
public class AdminMassControl implements IAdminCommandHandler {

	private static String[] ADMIN_COMMANDS = 
	{   
		"admin_masskill",
		"admin_massress"
	};
	private final static Log _log = LogFactory.getLog(AdminMassControl.class.getName());
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{	
		SystemMessage sm;
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}
		if (command.startsWith("admin_masskill"))//MASS KILL
		{
			if(_log.isDebugEnabled())
				_log.info("debug command masss kill working");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(!player.isGM())
				{
					counter++;
					player.getStatus().setCurrentHp(0);
					player.doDie(player);
					sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("System: You Killed "+counter+" players.");
					admin.sendPacket(sm);
					sm = null;
				}
			}
		}
		if (command.startsWith("admin_massress")) //MASS RESS
		{
			if(_log.isDebugEnabled())
				_log.info("debug command masss ress working");
			int counter = 0;
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if(!player.isGM() && player.isDead())
				{
					counter++;
					player.doRevive();
					sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("System: You Ressurected "+counter+" players.");
					admin.sendPacket(sm);
					sm = null;
				}
			}

		}
		return true;
	}
	private static final int REQUIRED_LEVEL = Config.GM_MENU;

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