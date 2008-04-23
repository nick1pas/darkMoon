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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> this class manages the following admin commands: </b><br><br>
 * 
 *  <li> admin_jail = jails a character <br>
 *	<li> admin_unjail = undo jail action <br><br>
 *	
 * <b> Usage: </b><br><br>
 * 
 * <li> //jail [char_name] [time_in_seconds] <br>
 * <li> //unjail [char_name] <br><br>
 * 
 * @author Rayan RPG for L2Emu Project !
 *  
 */
public class AdminJail implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminJail.class.getName());

	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_jail",
		"admin_unjail"
	};
	private static final int REQUIRED_LEVEL = Config.GM_BAN_CHAT;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel())))
			{
				return false;
			}
		}

		String player = "";
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		if (command.startsWith("admin_jail"))
		{
			try
			{
				player = st.nextToken();
				int delay = 0;
				try
				{
					delay = Integer.parseInt(st.nextToken());
				} 
				catch (NumberFormatException nfe) {
				} catch (NoSuchElementException nsee) {}
				
				L2PcInstance playerObj = L2World.getInstance().getPlayer(player);
				
			    if (playerObj != null)
				{
					playerObj.setInJail(true, delay);
					admin.sendMessage("Character "+player+" jailed for "+(delay>0 ? delay+" minutes." : "ever!"));
				} 
				else
					jailOfflinePlayer(admin, player, delay);
			} catch (NoSuchElementException nsee) 
			{
				admin.sendMessage("please specify character name.");
			} catch(Exception e)
			{
				if (_log.isDebugEnabled())  _log.debug("",e);
			}            
		}
		else if (command.startsWith("admin_unjail"))
		{
			try
			{
				player = st.nextToken();
				L2PcInstance playerObj = L2World.getInstance().getPlayer(player);

				if (playerObj != null)
				{
					playerObj.setInJail(false, 0);
					admin.sendMessage("Character "+player+" removed from jail");
				} else
					unjailOfflinePlayer(admin, player);
			} catch (NoSuchElementException nsee) 
			{
				admin.sendMessage("Specify a character name.");
			} catch(Exception e)
			{
				if (_log.isDebugEnabled())  _log.debug("",e);
			}            
		}

		return true;
	}
	/**
	 *  Jails offLine Player <br>
	 *  
	 * @param admin
	 * @param name
	 * @param delay
	 */
	private void jailOfflinePlayer(L2PcInstance admin, String name, int delay)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);

			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?");
			statement.setInt(1, -114356);
			statement.setInt(2, -249645);
			statement.setInt(3, -2984);
			statement.setInt(4, 1);
			statement.setLong(5, delay * 60000L);
			statement.setString(6, name);

			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();

			if (count == 0)
				admin.sendMessage("Character not found!");
			else
				admin.sendMessage("Character "+name+" jailed for "+(delay>0 ? delay+" minutes." : "ever!"));
		} catch (SQLException se)
		{
			admin.sendMessage("SQLException while jailing player");
			if (_log.isDebugEnabled())  _log.debug("",se);
		} finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}
	/**
	 * unjail offline player <br>
	 * 
	 * @param admin
	 * @param name
	 */
	private void unjailOfflinePlayer(L2PcInstance admin, String name)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);

			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?");
			statement.setInt(1, 17836);
			statement.setInt(2, 170178);
			statement.setInt(3, -3507);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.setString(6, name);

			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();

			if (count == 0)
				admin.sendMessage("Character not found!");
			else
				admin.sendMessage("Character "+name+" removed from jail");
		} 
		catch (SQLException se)
		{
			admin.sendMessage("SQLException while jailing player");

			if (_log.isDebugEnabled())  _log.debug("",se);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
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