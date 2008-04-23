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
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * <li> admin_invul = turns invulnerability on/off <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li> //invul <br><br>
 * 
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminInvul.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invul",
		"admin_setinvul"
	};
	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;

	public boolean useAdminCommand(String command, L2PcInstance admin) 
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if (command.equals("admin_invul")) handleInvul(admin);
		if (command.equals("admin_setinvul"))
		{
			L2Object target = admin.getTarget();
			if (target instanceof L2PcInstance){
				handleInvul((L2PcInstance)target);
			}
		}
		return true;
	}
	private void handleInvul(L2PcInstance admin)
	{
		String text;
		if (admin.isInvul())
		{
			admin.setIsInvul(false);
			text = admin.getName() + " is now mortal";
			if (_log.isDebugEnabled())
				_log.debug("GM: Gm removed invul mode from character "+admin.getName()+"("+admin.getObjectId()+")");
		}
		else
		{
			admin.setIsInvul(true);
			text = admin.getName() + " is now invulnerable";
			if (_log.isDebugEnabled()) 
				_log.debug("GM: Gm activated invul mode for character "+admin.getName()+"("+admin.getObjectId()+")");
		}
		admin.sendMessage(text);
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