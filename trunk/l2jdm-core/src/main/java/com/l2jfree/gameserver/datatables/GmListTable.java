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
package com.l2jfree.gameserver.datatables;

import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/**
 * This class stores references to all online game masters. (access level > 100)
 * 
 * @version $Revision: 1.2.2.1.2.7 $ $Date: 2005/04/05 19:41:24 $
 */
public class GmListTable
{
	private static final Log _log = LogFactory.getLog(GmListTable.class);
	
	/** Set(L2PcInstance>) containing all the GM in game */
	private static final FastMap<L2PcInstance, Boolean> _gmList = new FastMap<L2PcInstance, Boolean>().setShared(true);
	
	public static List<L2PcInstance> getAllGms(boolean includeHidden)
	{
		FastList<L2PcInstance> tmpGmList = new FastList<L2PcInstance>();
		for (FastMap.Entry<L2PcInstance, Boolean> n = _gmList.head(), end = _gmList.tail(); (n = n.getNext()) != end;)
		{
			if (includeHidden || !n.getValue())
				tmpGmList.add(n.getKey());
		}
		return tmpGmList;
	}
	
	public static List<String> getAllGmNames(boolean includeHidden)
	{
		FastList<String> tmpGmList = new FastList<String>();
		for (FastMap.Entry<L2PcInstance, Boolean> n = _gmList.head(), end = _gmList.tail(); (n = n.getNext()) != end;)
		{
			if (!n.getValue())
				tmpGmList.add(n.getKey().getName());
			else if (includeHidden)
				tmpGmList.add(n.getKey().getName() + " (invis)");
		}
		return tmpGmList;
	}
	
	static
	{
		_log.info("GmListTable: initialized.");
	}
	
	/**
	 * Add a L2PcInstance player to the Set _gmList
	 */
	public static void addGm(L2PcInstance player, boolean hidden)
	{
		if (_log.isDebugEnabled())
			_log.debug("added gm: " + player.getName());
		
		_gmList.put(player, hidden);
	}
	
	public static void deleteGm(L2PcInstance player)
	{
		if (_log.isDebugEnabled())
			_log.debug("deleted gm: " + player.getName());
		
		_gmList.remove(player);
	}
	
	/**
	 * GM will be displayed on clients gmlist
	 * 
	 * @param player
	 */
	public static void showGm(L2PcInstance player)
	{
		FastMap.Entry<L2PcInstance, Boolean> gm = _gmList.getEntry(player);
		if (gm != null)
			gm.setValue(false);
	}
	
	/**
	 * GM will no longer be displayed on clients gmlist
	 * 
	 * @param player
	 */
	public static void hideGm(L2PcInstance player)
	{
		FastMap.Entry<L2PcInstance, Boolean> gm = _gmList.getEntry(player);
		if (gm != null)
			gm.setValue(true);
	}
	
	public static boolean isGmOnline(boolean includeHidden)
	{
		for (boolean b : _gmList.values())
		{
			if (includeHidden || !b)
				return true;
		}
		
		return false;
	}
	
	public static void sendListToPlayer(L2PcInstance player)
	{
		if (!isGmOnline(player.isGM()))
		{
			player.sendPacket(SystemMessageId.NO_GM_PROVIDING_SERVICE_NOW);
		}
		else
		{
			player.sendPacket(SystemMessageId.GM_LIST);
			
			for (String name : getAllGmNames(player.isGM()))
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.GM_C1);
				sm.addString(name);
				player.sendPacket(sm);
			}
			
			player.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
		}
	}
	
	public static void broadcastToGMs(L2GameServerPacket packet)
	{
		for (L2PcInstance gm : getAllGms(true))
		{
			gm.sendPacket(packet);
		}
	}
	
	public static void broadcastMessageToGMs(String message)
	{
		for (L2PcInstance gm : getAllGms(true))
		{
			gm.sendPacket(SystemMessage.sendString(message));
		}
	}
}
