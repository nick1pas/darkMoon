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
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br> 
 * <li> admin_open = open a selected door (needs target) <br>
 * <li> admin_close = closes a selected door (needs target)  <br>
 * <li> admin_openall = opens all _knowDoors near that GM <br>
 * <li> admin_closeall = closes all _knowDoors near that GM <br><br>
 * 
 * <b> Door Table: </b><br><br> 
 * <li> coliseum door1 = 24190001 <br>
 * <li> coliseum door2 = 24190002 <br>
 * <li> coliseum door3 = 24190003 <br>
 * <li> coliseum door4 = 24190004 <br><br>
 * 
 * <b> Usage: </b><br><br> 
 * 
 * <li> //open <br>
 * <li> //close <br>
 * <li> //openall <br>
 * <li> //closeall <br><br>
 *
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminDoorControl implements IAdminCommandHandler
{
	private static Log _log = LogFactory.getLog(AdminDoorControl.class.getName());
	private static final int REQUIRED_LEVEL = Config.GM_DOOR;
	private static DoorTable _doorTable;
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_open",
		"admin_close",
		"admin_openall",
		"admin_closeall"
	};
	
    public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		SystemMessage sm;
		_doorTable = DoorTable.getInstance();

		try
		{
			if (command.startsWith("admin_open "))
			{
				int doorId = Integer.parseInt(command.substring(11));
				if (_doorTable.getDoor(doorId) != null)
					_doorTable.getDoor(doorId).openMe();
				else
				{
					 for (Castle castle: CastleManager.getInstance().getCastles().values())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).openMe();
						}
					}
				}
			}
			else if (command.startsWith("admin_close "))
			{
				int doorId = Integer.parseInt(command.substring(12));
				if (_doorTable.getDoor(doorId) != null)
					_doorTable.getDoor(doorId).closeMe();
				else
				{
					for (Castle castle: CastleManager.getInstance().getCastles().values())
					{
						if (castle.getDoor(doorId) != null)
						{
							castle.getDoor(doorId).closeMe();
						}
					}
				}
			}
			if (command.equals("admin_closeall"))
			{
				for(L2DoorInstance door : _doorTable.getDoors())
					door.closeMe();
				for (Castle castle: CastleManager.getInstance().getCastles().values())
					for (L2DoorInstance door: castle.getDoors())
						door.closeMe();
			}
			if (command.equals("admin_openall"))
			{
				for(L2DoorInstance door : _doorTable.getDoors())
					door.openMe();
				for (Castle castle: CastleManager.getInstance().getCastles().values())
					for (L2DoorInstance door: castle.getDoors())
						door.openMe();
			}
			if (command.equals("admin_open"))
			{
				L2Object target = admin.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance)target).openMe();
				}
				else
				{
					//L2EMU_EDIT_START
					sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("Incorrect target.");
					admin.sendPacket(sm);
					sm = null;
					//L2EMU_EDIT_END
				}
			}

			if (command.equals("admin_close"))
			{
				L2Object target = admin.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance)target).closeMe();
				}
				else
				{
					//L2EMU_EDIT_START
					sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("Incorrect target.");
					admin.sendPacket(sm);
					sm = null;
					//L2EMU_EDIT_END
				}
			}
		} 
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
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