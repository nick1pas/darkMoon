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

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands:
 * - invul = turns invulnerability on/off
 * 
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler
{
	private final static Log		_log			= LogFactory.getLog(AdminInvul.class);
	private static final String[]	ADMIN_COMMANDS	=
													{ "admin_invul", "admin_setinvul" };

	public boolean useAdminCommand(String command0, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command0, " ");
		String command = st.nextToken();
		
		String param = st.hasMoreTokens() ? st.nextToken() : "";
		
		final Boolean isInvul;
		if (param.equalsIgnoreCase("on") || param.equalsIgnoreCase("true") || param.equalsIgnoreCase("1"))
			isInvul = true;
		else if (param.equalsIgnoreCase("off") || param.equalsIgnoreCase("false") || param.equalsIgnoreCase("0"))
			isInvul = false;
		else
			isInvul = null;
		
		if (command.equals("admin_invul"))
			handleInvul(activeChar, isInvul);
		if (command.equals("admin_setinvul"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance)
			{
				handleInvul((L2PcInstance) target, isInvul);
			}
		}
		return true;
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private void handleInvul(L2PcInstance activeChar, Boolean isInvul)
	{
		if (isInvul == null)
			isInvul = !activeChar.isInvul();
		
		String text;
		if (!isInvul)
		{
			activeChar.setIsInvul(false);
			if (activeChar.getPet() != null)
				activeChar.getPet().setIsInvul(false);
			
			text = activeChar.getName() + " is now mortal.";
			if (_log.isDebugEnabled())
				_log.debug("GM: Gm removed invul mode from character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		else
		{
			activeChar.setIsInvul(true);
			if (activeChar.getPet() != null)
				activeChar.getPet().setIsInvul(true);
			
			text = activeChar.getName() + " is now invulnerable.";
			if (_log.isDebugEnabled())
				_log.debug("GM: Gm activated invul mode for character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		activeChar.sendMessage(text);
	}
}
