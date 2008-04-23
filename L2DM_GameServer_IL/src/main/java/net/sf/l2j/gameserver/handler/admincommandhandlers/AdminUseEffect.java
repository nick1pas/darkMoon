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
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_gmcancel <br><br>
 * 
 * @author Rayan RPG
 *
 */
public class AdminUseEffect implements IAdminCommandHandler 
{
	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;
	
	private static String[] ADMIN_COMMANDS = 
	{
		"admin_gmcancel"// Head
	};
	
	public boolean useAdminCommand(String command, L2PcInstance admin) 
	{
		if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) 
			return false;
		
		//L2EMU_ADD_START
		if (command.startsWith("admin_gmcancel"))
		{
			try
			{
				L2Object target = admin.getTarget();
				L2Character player = null;
				if (target instanceof L2Character)
				{
					player = (L2Character)target;
					player.stopAllEffects();
					admin.sendMessage("cancelled all effects of player "+player.getName());
				}
			}
			catch (Exception e)
			{
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