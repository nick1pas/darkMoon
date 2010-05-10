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

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.CharNameTable;
import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class AdminRepairChar implements IAdminCommandHandler
{
	private final static Log _log = LogFactory.getLog(AdminRepairChar.class);
	
	private static final String[] ADMIN_COMMANDS =
		{ "admin_restore", "admin_repair" };
	
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		handleRepair(command, activeChar);
		return true;
	}
	
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleRepair(String command, L2PcInstance activeChar)
	{
		String[] parts = command.split(" ");
		if (parts.length != 2)
			return;
		
		final Integer objId = CharNameTable.getInstance().getByName(parts[1]);
		
		if (objId == 0)
			return;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=17867, y=170259, z=-3450 WHERE charId=?");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=? AND loc=\"PAPERDOLL\"");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			
			activeChar.sendMessage("Character " + parts[1] + " got repaired.");
		}
		catch (Exception e)
		{
			_log.warn("Could not repair character: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
}
