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

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.templates.L2Item;

/**
 * <b> This class handles following admin commands:</b><br><br>
 * 
 * <li> admin_itemcreate = show menu <br>
 * <li> admin_create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.<br><br>
 * 
 * <b>Usage:</b><br><br>
 * 
 * <li> //itemcreate <br>
 * <li> //create_item <br><br>
 * 
 * 
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_itemcreate",
		"admin_create_item",
		//L2EMU_ADD
		"admin_create_adena"
		//L2EMU_ADD
	};
	private static final int REQUIRED_LEVEL = Config.GM_CREATE_ITEM;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
		
		if (command.equals("admin_itemcreate"))
		{
			//L2EMU_EDIT
			AdminHelpPage.showSubMenuPage(admin, "itemcreation_menu.htm");
			//L2EMU_EDIT
		}
		//L2EMU_ADD
		else if (command.startsWith("admin_create_adena"))
		{
			StringTokenizer st = new StringTokenizer(command);
			try
			{
				st.nextToken();
				int numval = Integer.parseInt(st.nextToken());

				L2Object target = admin.getTarget();
				L2PcInstance player = null;

				if (target instanceof L2PcInstance)
					player = (L2PcInstance)target;
				else
					player = admin;

				player.getInventory().addItem("admin_create_adena", 57, numval, admin, admin);
                player.sendMessage(admin.getName() + " has given " + numval  +" Adena in your inventory.");
                admin.sendMessage("You have given " + numval + " Adena to " + player.getName());		
			}
			catch (StringIndexOutOfBoundsException e)
			{

				admin.sendMessage("Error while creating Adena.");
			}
		}
		//L2EMU_ADD
		else if (command.startsWith("admin_create_item"))
		{
			try
			{
				String val = command.substring(17);
				StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens()== 2)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					String num = st.nextToken();
					int numval = Integer.parseInt(num);
					createItem(admin,idval,numval);
				}
				else if (st.countTokens()== 1)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					createItem(admin,idval,1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("Usage: //itemcreate <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				admin.sendMessage("Specify a valid number.");
			}
			//L2EMU_EDIT
			AdminHelpPage.showSubMenuPage(admin, "itemcreation_menu.htm");
			//L2EMU_EDIT
		}
		
		return true;
	}
	private void createItem(L2PcInstance admin, int id, int num)
	{
		if (num > 20)
		{
			L2Item template = ItemTable.getInstance().getTemplate(id);
			if (!template.isStackable())
			{
				admin.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
		}
		admin.getInventory().addItem("Admin", id, num, admin, null);
		//L2EMU_DISABLE BEGIN 
		//This method not work if this apply
		//admin.sendPacket(new InventoryUpdate());
		//L2EMU_Disable_END
		//L2EMU_ADD_BEGIN
		admin.sendPacket(new ItemList(admin, true));
		//L2Emu_ADD_END
		admin.sendMessage("You have spawned " + num + " item(s) number " + id + " in your inventory.");
	}
	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
	
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
}