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
import java.sql.ResultSet;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.Announcements;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
* <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_setdonator = gives/remove donator stats for a player. <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li> //setdonator <br><br>
 * 
 * @author Rayan
 *
 */
public class AdminDonator implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS = 
	{
		"admin_setdonator",
		"admin_donatorhero"
	};
	private final static Log _log = LogFactory.getLog(AdminDonator.class.getName());
	private static final int REQUIRED_LEVEL = Config.GM_MENU;
	
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{	
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}
		
		
		if (command.startsWith("admin_setdonator")) 
		{ 
			L2Object target = admin.getTarget(); 
			L2PcInstance player = null; 
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2); 
			if (target instanceof L2PcInstance) 
				player = (L2PcInstance)target; 
			else 
				player = admin; 
			
			if (player.isDonator()) 
			{ 
				player.setDonator(false); 
				sm.addString("You are no longer a server donator.");
				GmListTable.broadcastMessageToGMs("GM "+admin.getName()+" removed donator stat of player "+ target.getName());
				Connection connection = null;
				try
				{
					connection = L2DatabaseFactory.getInstance().getConnection(connection);
					
					PreparedStatement statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
					statement.setString(1,target.getName());
					ResultSet rset = statement.executeQuery();
					int objId = 0;
					if (rset.next())
					{
						objId = rset.getInt(1);
					}
					rset.close();
					statement.close();
					
					if (objId == 0) {connection.close(); return false;}
					
					statement = connection.prepareStatement("UPDATE characters SET is_donator=0 WHERE obj_id=?");
					statement.setInt(1, objId);
					statement.execute();
					statement.close();
					connection.close();
				}
				catch (Exception e)
				{
					_log.warn("could not set donator stats of char:", e);
				} 
				finally 
				{
					try { connection.close(); } catch (Exception e) {}
				}
			} 
			else 
			{ 
				player.setDonator(true); 
				sm.addString("You are now a server donator, congratulations!"); 
				GmListTable.broadcastMessageToGMs("GM "+admin.getName()+" has given donator stat for player "+target.getName()+".");
				Connection connection = null;
				try
				{
					connection = L2DatabaseFactory.getInstance().getConnection(connection);
					
					PreparedStatement statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
					statement.setString(1,target.getName());
					ResultSet rset = statement.executeQuery();
					int objId = 0;
					if (rset.next())
					{
						objId = rset.getInt(1);
					}
					rset.close();
					statement.close();
					
					if (objId == 0) {connection.close(); return false;}
					
					statement = connection.prepareStatement("UPDATE characters SET is_donator=1 WHERE obj_id=?");
					statement.setInt(1, objId);
					statement.execute();
					statement.close();
					connection.close();
				}
				catch (Exception e)
				{
					_log.warn("could not set donator stats of char:", e);
				} 
				finally 
				{
					try { connection.close(); } catch (Exception e) {}
				}
				
			}
			player.sendPacket(sm); 
			player.broadcastUserInfo(); 
			
			if(player.isDonator())
				Announcements.getInstance().announceToAll(player.getName() + " Has Become a Server Donator!");
		}
		return true; 
	}
		/*else if (command.startsWith("admin_donatorhero"))
		{
			try {
				
				StringTokenizer st = new StringTokenizer(command);
				String playername = st.nextToken();

				L2PcInstance player = L2World.getInstance().getPlayer(playername);

				if (player != null)
					handleDonatorHero(admin, player);
				else
				{
					Connection con = null;
					PreparedStatement statement;
					ResultSet rset;
					int objectId;

					try
					{
						con = L2DatabaseFactory.getInstance().getConnection(con);

						statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=? LIMIT 1");
						statement.setString(1, playername);
						rset = statement.executeQuery();

						objectId = rset.getInt("obj_Id");

						rset.close();
						statement.close();

						player = L2PcInstance.load(objectId);
						handleDonatorHero(admin, player);
						player.deleteMe();

					} catch(SQLException e)
					{
						admin.sendMessage("Set donator hero failed!");
						if (_log.isDebugEnabled())  _log.debug("",e);
					} finally
					{
						try{con.close();}catch(Exception e){ _log.error("",e);}
					}
				}
			} catch (Exception e) {}
		}

		return true;
	}
	
	private void handleDonatorHero(L2PcInstance activeChar, L2PcInstance player)
	{
		if (Hero.getInstance().isDonatorHero(player))
		{
			Hero.getInstance().setDonatorHero(player, false);
			activeChar.sendMessage("Added hero state to " + player.getName() + "!");
		}
		else
		{
			Hero.getInstance().setDonatorHero(player, true);
			activeChar.sendMessage("Removed hero state from " + player.getName() + "!");
		}
	}*/
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