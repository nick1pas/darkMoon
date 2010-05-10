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
package com.l2jfree.gameserver.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public interface IVoicedCommandHandler
{
	public static final Log _log = LogFactory.getLog(IVoicedCommandHandler.class);
	
	/**
	 * this is the worker method that is called when someone uses an .user command.
	 * 
	 * @param command
	 * @param activeChar
	 * @param target
	 * @return <code>true</code> if handler was triggered by the call, <code>false</code> otherwise
	 */
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target);
	
	/**
	 * this method is called at initialization to register all the handlers automatically
	 * 
	 * @return the commands associated with the handler
	 */
	public String[] getVoicedCommandList();
}
