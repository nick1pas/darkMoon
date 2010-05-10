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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands:
 * <ul>
 * 	<li>admin_unblockip</li>
 * </ul>
 *
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminUnblockIp implements IAdminCommandHandler
{

	private static final Log		_log			= LogFactory.getLog(AdminTeleport.class);

	private static final String[]	ADMIN_COMMANDS	=
													{ "admin_unblockip" };

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, com.l2jfree.gameserver.model.L2PcInstance)
	 */
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_unblockip "))
		{
			try
			{
				String ipAddress = command.substring(16);
				if (unblockIp(ipAddress, activeChar))
				{
					activeChar.sendMessage("Removed IP " + ipAddress + " from blocklist!");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Send syntax to the user
				activeChar.sendMessage("Usage mode: //unblockip <ip>");
			}
		}

		return true;
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	/**
	 * @param ipAddress
	 * @param activeChar
	 */
	private boolean unblockIp(String ipAddress, L2PcInstance activeChar)
	{
		//LoginServerThread.getInstance().unBlockip(ipAddress);
		_log.warn("IP removed by GM " + activeChar.getName());
		return true;
	}

}
