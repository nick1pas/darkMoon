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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.Shutdown;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;


/**
 * This class handles following admin commands:
 * - server_shutdown [sec] = shows menu or shuts down server in sec seconds
 * 
 * @version $Revision: 1.5.2.1.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminShutdown implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = {"admin_server_shutdown", "admin_server_restart", "admin_server_abort"};
	private static final int REQUIRED_LEVEL = Config.GM_RESTART;
	
	public boolean useAdminCommand(String command, L2PcInstance admin) {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
		
		if (command.startsWith("admin_server_shutdown"))
		{	
			try
			{
				int val = Integer.parseInt(command.substring(22)); 
				serverShutdown(admin, val, false);
			}
			catch (StringIndexOutOfBoundsException e)
			{				
				sendHtmlForm(admin);
			}
		} else if (command.startsWith("admin_server_restart"))
		{	
			try
			{
				int val = Integer.parseInt(command.substring(21)); 
				serverShutdown(admin, val, true);
			}
			catch (StringIndexOutOfBoundsException e)
			{				
				sendHtmlForm(admin);
			}
		} else if (command.startsWith("admin_server_abort"))
		{	
			serverAbort(admin);
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

	private void sendHtmlForm(L2PcInstance admin)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		
		int t = GameTimeController.getInstance().getGameTime();
		int h = t/60;
		int m = t%60;
		SimpleDateFormat format = new SimpleDateFormat("h:mm a");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);

		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/shutdown_menu.htm");
		//L2EMU_EDIT
		adminReply.replace("%count%",String.valueOf(L2World.getInstance().getAllPlayersCount()));
		adminReply.replace("%used%",String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
		adminReply.replace("%xp%",String.valueOf(Config.RATE_XP));
		adminReply.replace("%sp%",String.valueOf(Config.RATE_SP));
		adminReply.replace("%adena%",String.valueOf(Config.RATE_DROP_ADENA));
		adminReply.replace("%drop%",String.valueOf(Config.RATE_DROP_ITEMS));
		adminReply.replace("%time%",String.valueOf(format.format(cal.getTime())));
		admin.sendPacket(adminReply);			
	}
	
	private void serverShutdown(L2PcInstance admin, int seconds, boolean restart)
	{
		Shutdown.getInstance().startShutdown(admin, seconds, restart?Shutdown.shutdownModeType.RESTART:Shutdown.shutdownModeType.SHUTDOWN);
	}
	
	private void serverAbort(L2PcInstance admin)
	{
		Shutdown.getInstance().abort(admin);
	}

}