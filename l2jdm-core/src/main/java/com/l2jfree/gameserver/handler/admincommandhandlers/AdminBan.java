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

import com.l2jfree.gameserver.LoginServerThread;
import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.Disconnection;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands:
 * - ban account_name = changes account access level to selected [u]negative[/u] access level and logs him off. If no account is specified, target's account is used.
 * - unban account_name = changes account access level to 0.
  * 
 * @version $Revision: 1.1.6.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminBan implements IAdminCommandHandler
{
	private static final String[]	ADMIN_COMMANDS	= {
		"admin_ban", "admin_unban", "admin_ban_select", "admin_banbychar"
	};

	@SuppressWarnings("null")
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken();
		String nameToBan = null;
		if (st.hasMoreTokens())
			nameToBan = st.nextToken().trim();
		L2PcInstance player = null;
		L2Object target = activeChar.getTarget();
		if (target != null && target instanceof L2PcInstance && target != activeChar)
			player = target.getActingPlayer();

		if (nameToBan == null && player == null)
		{
			if (command.contains("un"))
				activeChar.sendMessage("//unban [account name] or //unban_menu");
			else if (command.charAt(command.length() - 1) == 't')
				activeChar.sendMessage("//ban_select [account name]");
			else if (command.charAt(command.length() - 1) == 'r')
				activeChar.sendMessage("//banbychar [character name]");
			else
				activeChar.sendMessage("//ban [account name] [access level] or //ban_menu");
			activeChar.sendMessage("Or just target a player.");
			return false;
		}
		if (nameToBan != null && nameToBan.isEmpty())
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
			return false;
		}

		if (command.startsWith(ADMIN_COMMANDS[0]))
		{
			if (command.charAt(command.length() - 1) == 't')
			{
				if (nameToBan != null)
					sendBanSelect(activeChar, nameToBan);
				else
					sendBanSelect(activeChar, player.getAccountName());
			}
			else if (command.charAt(command.length() - 1) == 'r')
			{
				if (nameToBan != null)
				{
					player = L2World.getInstance().getPlayer(nameToBan);
					if (player == null)
					{
						activeChar.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
						return false;
					}
				}
				sendBanSelect(activeChar, player.getAccountName());
			}
			else
			{
				if (!st.hasMoreTokens())
				{	// missing level, so send a level selection page
					if (nameToBan != null)
						sendBanSelect(activeChar, nameToBan);
					else
						sendBanSelect(activeChar, player.getAccountName());
					return true;
				}

				int level;
				try
				{
					level = Integer.parseInt(st.nextToken());
					if (level > 0)
						level *= -1;
					else if (level == 0)
						level = -100;
				}
				catch (Exception e)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
					return false;
				}

				if (player != null && nameToBan.equalsIgnoreCase(player.getAccountName()))
				{
					player.setAccountAccesslevel(level);
					activeChar.sendMessage("Account " + player.getAccountName() + " banned.");
					try
					{
						new Disconnection(player).defaultSequence(false);
					}
					catch (Exception e)
					{
					}
				}
				else
				{
					LoginServerThread.getInstance().sendAccessLevel(nameToBan, level);
					activeChar.sendMessage("Ban for account " + nameToBan + " requested.");
				}
			}
		}
		else
		{
			if (nameToBan == null)
				nameToBan = player.getAccountName();
			LoginServerThread.getInstance().sendAccessLevel(nameToBan, 0);
			activeChar.sendMessage("Unban for account " + nameToBan + " requested.");
		}
		return true;
	}

	private void sendBanSelect(L2PcInstance gm, String account)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(gm.getObjectId());
		html.setFile("data/html/admin/ban_selection.htm");
		html.replace("%account%", account);
		gm.sendPacket(html);
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
