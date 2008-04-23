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
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.services.SystemService;
import net.sf.l2j.gameserver.services.WindowService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *<b> This class handles following admin commands: </b><br><br>
 *
 * <li> admin_memoryusage =  prints memory usage in console and informs admin. <br>
 * <li> admin_rateinfo = sends a page to admin informing server rates.<br>
 * <li> admin_enchant_info = sends a page to admin informing server enchant rates.<br>
 * <li> admin_freememory = prints gameserver free memory in console and informs admin. <br>
 * <li> admin_totalmemory = prints gameserver total memory in console and informs admin. <br><br>
 *
 * <b>Usage:</b><br><br>
 *
 * <li> //memoryusage <br>
 * <li> //rateinfo <br>
 * <li> //enchant_info <br>
 * <li> //freememory <br>
 * <li> //totalmemory <br><br>
 * 
 * @author  Rayan RPG for L2Emu Project!
 */
public class AdminInfo implements IAdminCommandHandler
{
	private static String[] []ADMIN_COMMANDS = 
	{{
		"admin_memoryusage",
	},
	{
		"admin_rateinfo",
	},
	{
		"admin_enchant_info",
	},
	{
		"admin_freememory", 
	},
	{ 
		"admin_totalmemory"
	}};


	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;


	/**
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, net.sf.l2j.gameserver.model.actor.instance.L2PcInstance)
	 */
	protected static Log _log = LogFactory.getLog(AdminInfo.class.getName());
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}
		if (command.startsWith("admin_rateinfo"))
		{
			WindowService.sendWindow(admin, HtmlPathService.ADMIN_HTML_PATH_INFO, "rateinfo.htm");
			return false;
		}
		if (command.startsWith("admin_enchant_info"))
		{
			WindowService.sendWindow(admin, HtmlPathService.ADMIN_HTML_PATH_INFO, "enchantinfo.htm");
			return false;
		}
		if (command.startsWith("admin_memoryusage"))
		{
			_log.info("");
			_log.info("");
			_log.info("<<------------------->>");
			_log.info(">> MEMORY USAGE DUMP <<");
			_log.info("<<------------------->>");
			_log.info("");
			_log.info("GameServer: Memory Usage: "+SystemService.getInstance().getUsedMemory()+" MB.");
			_log.info("");
			if(_log.isDebugEnabled())
				_log.info("GM: " + admin.getName()+ " Requested Memory Info.");
			admin.sendMessage("GameServer Memory Usage: MB." );
			return false;
		}
		else if (command.startsWith("admin_freememory"))
		{
			_log.info("");
			_log.info("");
			_log.info("<<------------------>>");
			_log.info(">> FREE MEMORY DUMP <<");
			_log.info("<<------------------>>");
			_log.info("");
			_log.info("GAMESERVER FREE MEMORY: MB.");    
			if(_log.isDebugEnabled())
				_log.info("GM: "+ admin.getName()+ " Requested Memory Info.");
			admin.sendMessage("GameServer: Free Memory: "+SystemService.getInstance().getFreeMemory()+" MB.");
			return false;
		}
		else  if (command.startsWith("admin_totalmemory"));
		{
			_log.info("");
			_log.info("");
			_log.info("<<------------------------------>>");
			_log.info(">> GAMESERVER TOTAL MEMORY DUMP <<");
			_log.info("<<------------------------------>>");
			_log.info("");
			_log.info("GameServer: Total Memory"+SystemService.getInstance().getTotalMemory() +" MB."); 
			if(_log.isDebugEnabled())
				_log.info("GM: " + admin.getName()+ " Requested Memory Info.");

			admin.sendMessage("GameServer Total Memory: "+SystemService.getInstance().getTotalMemory()+ " MB." );
			return false;
		}
	}
	/**
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#getAdminCommandList()
	 */
	public String[] getAdminCommandList()
	{
		String[] _adminCommandsOnly = new String[ADMIN_COMMANDS.length];

		for (int i=0; i < ADMIN_COMMANDS.length; i++)
		{
			_adminCommandsOnly[i]= ADMIN_COMMANDS[i][0];
		}
		return _adminCommandsOnly;
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