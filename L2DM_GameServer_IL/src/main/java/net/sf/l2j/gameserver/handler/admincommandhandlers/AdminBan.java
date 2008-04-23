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
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br>
 * 
 * <li> admin_ban = bans an character account <br>
 * <li> admin_unban = undo ban action <br><br>
 *
 * <b> Usage: </b><br><br>
 * 
 * <li> //ban [player_name] <br>
 * <li> //unban [player_name] <br><br>
 * 
 * @version $Revision: 1.1.6.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminBan implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminBan.class);
	
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_ban",
		"admin_unban",
	};
	private static final int REQUIRED_LEVEL = Config.GM_BAN;
 
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel())))
			{
				return false;
			}
		}
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String accountName = "";
		L2PcInstance plyr = null;
		if (command.startsWith("admin_ban"))
		{   
			try
			{
				plyr = L2World.getInstance().getPlayer(st.nextToken());
			}
			catch(Exception e)
			{
				L2Object target = admin.getTarget();
				if (target!=null && target instanceof L2PcInstance)
					plyr = (L2PcInstance)target;
				else
					admin.sendMessage("Wrong parameter or target.");
			}

			if (plyr!=null && !plyr.equals(admin)) // you cannot ban yourself!
			{
				accountName = plyr.getAccountName();
				LoginServerThread.getInstance().sendAccessLevel(accountName, -100);
				RegionBBSManager.getInstance().changeCommunityBoard();
				admin.sendMessage("player "+plyr.getName()+" has been banned.");
				plyr.logout();
				plyr.closeNetConnection();
			}
		}
		else if (command.startsWith("admin_unban"))
		{
			try
			{
				accountName = st.nextToken();
				LoginServerThread.getInstance().sendAccessLevel(accountName, 0);
			}
			catch(Exception e)
			{
				if (_log.isDebugEnabled()) _log.debug("",e);
			}
		}
		return true;
	}
	/**
	 * gets admin command List
	 */
	public String[] getAdminCommandList() 
	{
		return ADMIN_COMMANDS;
	}
    /**
     * @param level
     * @return
     */
	private boolean checkLevel(int level) 
	{
		return (level >= REQUIRED_LEVEL);
	}
}