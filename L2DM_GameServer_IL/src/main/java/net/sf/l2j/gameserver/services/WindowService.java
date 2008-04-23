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
package net.sf.l2j.gameserver.services;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <FONT COLOR=#FF0000> WARNING: READ ONLY! </FONT> <br><br>
 *  Only use this method to send a window, dont use for chatting or npc actions. <br>
 *  
 * @author Rayan RPG <br>
 * L2Emu Project <br>
 * 
 */
public class WindowService
{
	private final static Log _log = LogFactory.getLog(WindowService.class.getName());
	public static String _serviceName = ThreadService.class.getName().toString();

	/**
	 * method to send html only, replace dont work.
	 * @param target
	 * @param path
	 * @param filename
	 */
	public static void sendWindow(L2PcInstance target, String path, String filename)
	{
		String html = HtmCache.getInstance().getHtmForce(path + filename);
		NpcHtmlMessage reply = new NpcHtmlMessage(5);
		reply.setHtml(html);
		target.sendPacket(reply);
		
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        target.sendPacket( new ActionFailed() );
        
		if(Config.DEVELOPER)
		_log.info("WindowService: Sending Window: "+filename+" for player: "+target.getName()+" in path: "+path);
	}
}