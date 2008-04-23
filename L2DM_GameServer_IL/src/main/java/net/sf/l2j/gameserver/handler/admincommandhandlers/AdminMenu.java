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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles following admin commands:
 * - handles every admin menu command
 * 
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminMenu implements IAdminCommandHandler 
{
	private static final Log _log = LogFactory.getLog(AdminMenu.class.getName());

	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_char_manage",
		"admin_teleport_character_to_menu",
		"admin_recall_char_menu", 
		"admin_recall_party_menu",
		"admin_recall_clan_menu",
		"admin_goto_char_menu",
		"admin_kick_menu",
		"admin_kill_menu",
		"admin_ban_menu",
		"admin_unban_menu"
	};
	private static final int REQUIRED_LEVEL = Config.GM_ACCESSLEVEL;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
			
		String target = (admin.getTarget() != null?admin.getTarget().getName():"no-target");
		if (command.equals("admin_char_manage"))
			showMainPage(admin);
		else if (command.startsWith("admin_teleport_character_to_menu"))
		{
			String[] data = command.split(" ");
			if(data.length==5)
			{
				String playerName=data[1];
				L2PcInstance player = L2World.getInstance().getPlayer(playerName);
				if(player!=null)
					teleportCharacter(player,Integer.parseInt(data[2]),Integer.parseInt(data[3]),Integer.parseInt(data[4]),admin, "Admin is teleporting you.");
			}
			showMainPage(admin);
		}
		else if (command.startsWith("admin_recall_char_menu"))
		{
			try
			{
				String targetName = command.substring(23);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				teleportCharacter(player,admin.getX(),admin.getY(),admin.getZ(),admin, "Admin is teleporting you.");
			}
			catch (StringIndexOutOfBoundsException e){}
		}
		else if (command.startsWith("admin_recall_party_menu"))
		{
			int x=admin.getX(), y = admin.getY(), z=admin.getZ();
			try
			{
				String targetName = command.substring(24);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				if(player == null)
				{
					admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
					return true;
				}
				if(!player.isInParty())
				{
					admin.sendMessage("Player is not in party.");
					teleportCharacter(player,x,y,z,admin, "Admin is teleporting you.");
					return true;
				}
				for(L2PcInstance pm : player.getParty().getPartyMembers())
					teleportCharacter(pm, x, y, z, admin, "Your party is being teleported by an Admin.");
			}
			catch (Exception e){}
		}
		else if (command.startsWith("admin_recall_clan_menu"))
		{
			int x=admin.getX(), y = admin.getY(), z=admin.getZ();
			try
			{
				String targetName = command.substring(23);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				if(player == null)
				{
					admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
					return true;
			}
				L2Clan clan = player.getClan();
				if(clan==null)
				{
					admin.sendMessage("Player is not in a clan.");
					teleportCharacter(player,x,y,z,admin, "Admin is teleporting you.");
					return true;
				}
				L2PcInstance[] members = clan.getOnlineMembers("");
				for (L2PcInstance element : members)
					teleportCharacter(element, x, y, z, admin, "Your clan is being teleported by an Admin.");
			}
			catch (Exception e){}
 		}
		else if (command.startsWith("admin_goto_char_menu"))
		{
			try
			{
				String targetName = command.substring(21);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				teleportToCharacter(admin, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("Target not found.");
				showMainPage(admin);
			}
		}
		else if (command.equals("admin_kill_menu"))
		{
			handleKill(admin);
		}
		else if (command.startsWith("admin_kick_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				if (plyr != null)
				{
					plyr.logout();
					sm.addString("You kicked " + plyr.getName() + " from the game.");
				}
				else
					sm.addString("Player " + player + " was not found in the game.");
				admin.sendPacket(sm);
			}
			showMainPage(admin);
		}
		else if (command.startsWith("admin_ban_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				if (plyr != null)
				{
					plyr.logout();
				}
				setAccountAccessLevel(player, admin, -100);
			}
			showMainPage(admin);
		}
		else if (command.startsWith("admin_unban_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				setAccountAccessLevel(player, admin, 0);
			}
			showMainPage(admin);
		}
		return true;
	}
	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
	private boolean checkLevel(int level) 
	{
		return (level >= REQUIRED_LEVEL);
	}
	private void handleKill(L2PcInstance admin)
	{
		handleKill(admin, null);
	}
	private void handleKill(L2PcInstance admin, String player)
	{
		L2Object obj = admin.getTarget();
		L2Character target = (L2Character)obj;
		String filename = "main_menu.htm";
		if (player != null)
		{
			L2PcInstance plyr = L2World.getInstance().getPlayer(player);
			if (plyr != null)
				target = plyr;
			admin.sendMessage("You killed " + plyr.getName());
		}
		if (target != null)
		{
			if (target instanceof L2PcInstance)
			{
				target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, admin);
				filename = "charmanagement_menu.htm";
			}
			else if (target.isChampion())
				target.reduceCurrentHp(target.getMaxHp()*Config.CHAMPION_HP + 1, admin);
			else 
				target.reduceCurrentHp(target.getMaxHp() + 1, admin);
		}
		else
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
		}
		//L2EMU_EDIT
		AdminHelpPage.showMenuPage(admin, filename);
		//L2EMU_EDIT
	}

	private void teleportCharacter(L2PcInstance player, int x, int y, int z, L2PcInstance admin, String message)
	{
		if (player != null) 
		{
			player.sendMessage(message);
			player.teleToLocation(x, y, z, true);
		}
		else
			admin.sendMessage("Target not found.");
		showMainPage(admin);
	}
	private void teleportToCharacter(L2PcInstance admin, L2Object target)
	{
		L2PcInstance player = null;
		if (target != null && target instanceof L2PcInstance) 
			player = (L2PcInstance)target;
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		if (player.getObjectId() == admin.getObjectId())
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ON_YOURSELF));
		else
		{
			admin.teleToLocation(player.getX(), player.getY(), player.getZ(), true);
			admin.sendMessage("You're teleporting yourself to character " + player.getName());
		}
		showMainPage(admin);
	}
	/**
	 * @param admin
	 */
	private void showMainPage(L2PcInstance admin)
	{
		//L2EMU_EDIT
		AdminHelpPage.showSubMenuPage(admin, "charmanagement_menu.htm");
		//L2EMU_EDIT
	}

	private void setAccountAccessLevel(String player, L2PcInstance admin, int banLevel)
	{
		java.sql.Connection con = null;
		try
		{		   
			con = L2DatabaseFactory.getInstance().getConnection(con);
			String stmt = "SELECT account_name FROM characters WHERE char_name = ?";
			PreparedStatement statement = con.prepareStatement(stmt);
			statement.setString(1, player);
			ResultSet result = statement.executeQuery();
			if (result.next())
			{
				String acc_name = result.getString(1);
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				if(acc_name.length() > 0)
				{
					LoginServerThread.getInstance().sendAccessLevel(acc_name, banLevel);
					sm.addString("Account Access Level for "+player+" set to "+banLevel+".");
				}
				else
					sm.addString("Couldn't find player: "+player+".");
				admin.sendPacket(sm);
			}
			else
				admin.sendMessage("Specified player name didn't lead to a valid account.");
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("Could not set accessLevel:"+e);
			if (_log.isDebugEnabled())
				e.printStackTrace();
		}
		finally 
		{
			try 
			{ 
				con.close(); 
			}
			catch (Exception e) {}
		}
	}
}