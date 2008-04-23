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
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_repair_character [charname] = repairs a character that cannot login anymore :D <br> <b><br>
 *  
 * <b>Usage:</b><br><br>
 *  
 * <li> //repair_character <br><br>
 * 
 * @Rewritten by Rayan for L2Emu Project
 *  
 * @since 687
 */
public class AdminRepairChar implements IAdminCommandHandler
{
	private final static Log _log = LogFactory.getLog(AdminRepairChar.class.getName());

	private static String[][] ADMIN_COMMANDS = 
	{{"admin_repair_character",

		"Restores a crashed character that cannot login anymore.",
		"Usage: //repair_character <charname>",
	}};
	private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}

		if (command.startsWith("admin_repair_character "))
		{
			try
			{
				String charName = command.substring(23);
				repairCrashedCharacter(charName, admin);
				return true;
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("please specify character name.");
			}
		}
		return true;
	}
	/**
	 * Restores a Crashed Character that can't login anymore<br>
	 * <br> Actions: <br><br>
	 * <li> Redirects bugged character to floran <br>
	 * <li> Deletes all shortcuts in case of problem with it. <br>
	 * <li> Updates inventory items in case of a problem with it <br><br>
	 * 
	 * @param command
	 * @param charname
	 */
	private void repairCrashedCharacter(String charName,L2PcInstance admin)
	{
		if (charName.equals(" "))
			return;

		//prevents of modify a online character
		for(L2PcInstance player :L2World.getInstance().getAllPlayers())
		{
			if(charName.equals(player.getName().toLowerCase()))
			{
				player.sendMessage("character is online , this is really needed?");
				return;
			}
		}
		Connection con = null;
		try
		{
			//creates a connection to DB
			con = L2DatabaseFactory.getInstance().getConnection(con);

			//Gets char object id on DB.
			PreparedStatement statement;
			statement = con.prepareStatement("SELECT obj_id FROM characters WHERE char_name=?");
			statement.setString(1,charName);
			ResultSet rset = statement.executeQuery();

			if(admin==null)
			{
				if(Config.DEVELOPER)
					_log.info("admin is null!");
				return;
			}

			//send informative message to GM
			if(Config.DEVELOPER)
				_log.info("admin name"+admin.getName());
			admin.sendMessage("searching character trougth DB...");
			int objId = 0;
			if (rset.next())
			{
				objId = rset.getInt(1);
			}
			if(Config.DEVELOPER)
				_log.info("object if for char: "+charName+" is: "+objId+".");
			rset.close();
			statement.close();

			//prevents to get a 0 object id.
			if (objId == 0)
			{
				con.close(); 
				if(Config.DEVELOPER)
					_log.info("error object id == 0");
				return;
			}

			//redirects buggued character to floran
			statement = con.prepareStatement("UPDATE characters SET x=17867, y=170259, z=-3503 WHERE obj_id=?");
			admin.sendMessage("moving character to floran, via Database");
			if(Config.DEVELOPER)
			{
				admin.sendMessage("statment executed sucessfull");
				_log.info("moving character to floran, via Database");
			}
			statement.setInt(1, objId);
			statement.execute();
			if(Config.DEVELOPER)
				_log.info("statment executed sucessfull");
			statement.close();

			//deletes all shortcuts in case of problem with it.
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			admin.sendMessage("deleted, all character shortcuts via Database");
			statement.setInt(1, objId);
			statement.execute();
			if(Config.DEVELOPER)
				_log.info("statment executed sucessfull");
			statement.close();

			//updates inventory items in case of a problem with it
			statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=? AND loc=\"PAPERDOLL\"");
			statement.setInt(1, objId);
			statement.execute();
			admin.sendMessage("updated character inventory via Database!");
			admin.sendMessage("character items repaired via Database!");
			if(Config.DEVELOPER)
			{
				_log.info("statment executed sucessfull");
				_log.info("character repaired.");
			}
			admin.sendMessage("character repair task sucessfully finished!");
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			_log.warn("GameServer: could not repair character:", e);
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
	/**
	 * Checks the level of access of requester<br>
	 * @param level
	 * @return
	 */
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#getAdminCommandList()
	 */
	public String[] getAdminCommandList()
	{
		String[] _adminCommandsOnly = new String[ ADMIN_COMMANDS.length];
		for (int i=0; i <  ADMIN_COMMANDS.length; i++)
		{
			_adminCommandsOnly[i]= ADMIN_COMMANDS[i][0];
		}
		return _adminCommandsOnly;
	}
}