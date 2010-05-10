/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class AdminBoat implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = { "admin_boat" };

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2BoatInstance boat = activeChar.getBoat();
		if (boat == null)
		{
			activeChar.sendMessage("Usage only possible while riding a boat.");
			return false;
		}

		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();

		if (st.hasMoreTokens())
		{
			String cmd = st.nextToken();
			if (cmd.equals("cycle"))
			{
				if (boat.isInCycle())
				{
					boat.stopCycle();
					activeChar.sendMessage("Boat cycle stopped.");
				}
				else
				{
					boat.startCycle();
					activeChar.sendMessage("Boat cycle started.");
				}
			}
			else if (cmd.equals("reload"))
			{
				boat.reloadPath();
				activeChar.sendMessage("Boat path reloaded.");
			}
			else
				showUsage(activeChar);
		}
		else
		{
			activeChar.sendMessage("====== Boat Information ======");
			activeChar.sendMessage("Name: "+boat.getBoatName()+" ("+boat.getId()+") ObjId: "+boat.getObjectId());
			activeChar.sendMessage("Cycle: "+boat.isInCycle()+" ("+boat.getCycle()+")");
			activeChar.sendMessage("Players inside: "+boat.getSizeInside());
			activeChar.sendMessage("Position: "+boat.getX()+" "+boat.getY()+" "+boat.getZ()+" "+boat.getPosition().getHeading());
			activeChar.sendMessage("==============================");
		}

		return true;
	}

	private void showUsage(L2PcInstance cha)
	{
		cha.sendMessage("Usage: //boat [cycle|reload]");
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}