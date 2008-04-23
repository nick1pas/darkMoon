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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *<b> This class handles following admin commands: </b><br><br>
 *
 * <li> admin_heal = restores HP/MP/CP on target, name or radius. <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li> //heal <br><br>
 * 
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminHeal implements IAdminCommandHandler 
{
	@SuppressWarnings("unused")
	private final static Log _log = LogFactory.getLog(AdminRes.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_heal" 
	};
	private static final int REQUIRED_LEVEL = Config.GM_HEAL;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;

		if (command.equals("admin_heal"))
		{
			if (admin.getTarget() instanceof L2Character)
			{
				handleHeal((L2Character)admin.getTarget());
			}
		}
		else if (command.startsWith("admin_heal"))
		{
			try
			{
				String val = command.substring(11);

				try
				{
					int radius = Integer.parseInt(val);
					for (L2Character cha : admin.getKnownList().getKnownCharactersInRadius(radius))
					{
						handleHeal(cha);
					}
				}
				catch (NumberFormatException e)
				{
					L2Object target = L2World.getInstance().getPlayer(val);
					if (target instanceof L2Character)
						handleHeal((L2Character)target);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("Incorrect target/radius specified.");
			}
		}
		return true;
	}
	private void handleHeal(L2Character target)
	{
		target.getStatus().setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
		if (target instanceof L2PcInstance)
			target.getStatus().setCurrentCp(target.getMaxCp());
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