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
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemChatChannelId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 * <b>This class handles following admin commands:</b><br><br>
 *  
 * <li> admin_gmchat text = sends text to all online GM's <br>
 * <li> admin_gmchat_menu text = same as gmchat, displays the admin panel after chat <br>
 * <li> admin_snoop = snoops a target <br>
 * <li> admin_unsnoop = undo snoop action <br><br>
 *  
 *  
 * <b>Usage:</b><br><br>
 *  
 * <li>  //gmchat <br>
 * <li>  //snoop <br>
 * <li>  //unsnoop <br>
 * <li>  //gmchat_menu <br>
 *  
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGmChat implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_gmchat",
		"admin_snoop",
		"admin_unsnoop",
		"admin_gmchat_menu"
	};
	private static final int REQUIRED_LEVEL = Config.GM_MIN;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if (command.startsWith("admin_gmchat"))
			handleGmChat(command, admin);
		
		else if(command.startsWith("admin_snoop"))
			snoop(command, admin);
		
		else if(command.startsWith("admin_unsnoop"))
			unSnoop(command, admin);
		
		if (command.startsWith("admin_gmchat_menu")) 
			AdminHelpPage.showMenuPage(admin, "main_menu.htm"); 
		return true;
	}

	/**
	 * @param command
	 * @param admin
	 */
	private void snoop(String command, L2PcInstance admin)
	{
		//valiudates snoop conditions
		validateSnoop(admin);

		L2Object target = admin.getTarget();
		L2PcInstance player = (L2PcInstance)target;
		if (player.getAccessLevel()> admin.getAccessLevel()){
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			player.sendMessage(admin.getName()+" tried to snoop your conversations. Blocked.");
			return;
		}
		player.addSnooper(admin); // GM added to player list
		admin.addSnooped(player); // Player added to GM list
		admin.sendMessage("snooping player: "+ target.getName());
	}

	/**
	 * @param command
	 * @param admin
	 */
	private void unSnoop(String command, L2PcInstance admin)
	{
		//valiudates snoop conditions
		validateSnoop(admin);

		L2Object target = admin.getTarget();
		L2PcInstance player = (L2PcInstance)target;
		player.removeSnooper(admin);
		admin.removeSnooped(player);
		admin.sendMessage("stoped snooping player: "+ target.getName());
	}
	/**
	 * @param command
	 * @param admin
	 */
	private void handleGmChat(String command, L2PcInstance admin) 
	{
		try
		{
			int offset=0;
			String text;
			if (command.contains("menu"))
				offset=17;
			else
				offset=13;
			text = command.substring(offset);
			CreatureSay cs = new CreatureSay(admin.getObjectId(), SystemChatChannelId.Chat_GM.getId() , admin.getName(),text);
			GmListTable.broadcastToGMs(cs);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
		}
	}
	/**
	 * validates snoop conditions
	 * @param admin
	 */
	public void validateSnoop(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();

		//checks if is a valid target.
		if(target == null)
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.YOU_MUST_SELECT_A_TARGET));
			return;
		}
		//no one wants to spawnm another instance if not a player
		if(!(target instanceof L2PcInstance))
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		// you cannot snoop yourself!
		if(target.equals(admin))
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
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