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

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_changelvl = changes player acess lvl to a GM. <br><br>
 * 
 * <b>Usage:</b><br><br>
 * 
 * <li> //changelvl <br><br>
 * 
 * @version $Revision: 1.1.2.2.2.3 $ $Date: 2005/04/11 10:06:00 $
 */
public class AdminChangeAccessLevel implements IAdminCommandHandler
{
	//private final static Log _log = LogFactory.getLog(AdminChangeAccessLevel.class.getName());

	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_changelvl" 
	};

	private static final int REQUIRED_LEVEL = Config.GM_ACCESSLEVEL;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
			{
				return false;
			}
		}

		handleChangeLevel(command, admin);
		return true;
	}
	private void handleChangeLevel(String command, L2PcInstance admin)
	{
		String[] parts = command.split(" ");
		if (parts.length == 2)
		{
			int lvl = Integer.parseInt(parts[1]);
			if (admin.getTarget() instanceof L2PcInstance)
			{
				((L2PcInstance)admin.getTarget()).setAccessLevel(lvl);
				//L2EMU_ADD_START
				admin.sendMessage("sucessfully changed the access level of player "+admin.getTarget().getName()+" to "+lvl+" .");
				//L2EMU_ADD_END
			}
		}
		else if (parts.length == 3)
		{
			int lvl = Integer.parseInt(parts[2]);
			L2PcInstance player = L2World.getInstance().getPlayer(parts[1]);
			if (player != null)
			{
				player.setAccessLevel(lvl);
				//L2EMU_ADD_START
				admin.sendMessage("sucessfully changed the access level of player "+admin.getTarget().getName()+" to "+lvl+" .");
				//L2EMU_ADD_END
			}
		}
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