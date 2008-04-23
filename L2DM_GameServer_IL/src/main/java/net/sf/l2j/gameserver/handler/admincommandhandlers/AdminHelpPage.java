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
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands:
 * - help path = shows /data/html/admin/path file to char, should not be used by GM's directly
 * 
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:02 $
 */
public class AdminHelpPage implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = { "admin_help" };
	private static final int REQUIRED_LEVEL = Config.GM_MIN;

	public boolean useAdminCommand(String command, L2PcInstance admin) {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!checkLevel(admin.getAccessLevel())) return false;
		
		if (command.startsWith("admin_help"))
		{
			try
			{
				String val = command.substring(11);
				showHelpPage(admin, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				//case of empty filename
			}			
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

	//PUBLIC & STATIC so other classes from package can include it directly
	public static void showHelpPage(L2PcInstance targetChar, String filename)
	{
        String content = HtmCache.getInstance().getHtmForce("data/html/admin/" + filename);
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setHtml(content);
        targetChar.sendPacket(adminReply);
	}
	//L2EMU_ADD
	/**
	 * Shows a File Located in data/html/admin/menus/ <br>
	 * 
	 * used to send main menus page <br>
	 * @param targetChar
	 * @param filename
	 */
	public static void showMenuPage(L2PcInstance targetChar, String filename)
	{
        String content = HtmCache.getInstance().getHtmForce("data/html/admin/menus/" + filename);
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setHtml(content);
        targetChar.sendPacket(adminReply);
	}
	/**
	 * Shows a File Located in data/html/admin/menus/submenus/ <br>
	 *
     * used to send submenus page <br>
	 * @param targetChar
	 * @param filename
	 */
	public static void showSubMenuPage(L2PcInstance targetChar, String filename)
	{
        String content = HtmCache.getInstance().getHtmForce("data/html/admin/menus/submenus/" + filename);
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setHtml(content);
        targetChar.sendPacket(adminReply);
	}
	/**
	 * Shows a File Located in data/html/admin/tele/ <br>
	 *
     * used to send teleports page <br>
	 * @param targetChar
	 * @param filename
	 */
	public static void showTeleMenuPage(L2PcInstance targetChar, String filename)
	{
        String content = HtmCache.getInstance().getHtmForce("data/html/admin/tele/" + filename);
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setHtml(content);
        targetChar.sendPacket(adminReply);
	}
	//L2EMU_ADD
}
