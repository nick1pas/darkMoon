/* This program is free software; you can redistribute it and/or modify
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
package net.sf.l2j.gameserver.util;

import javolution.util.FastMap;
import javolution.util.FastMap.Entry;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Flood protector
 * 
 * @author durgus
 */
public class FloodProtector
{
	private static final Log _log = LogFactory.getLog(FloodProtector.class.getName());
	private static FloodProtector _instance;

	public static final FloodProtector getInstance()
	{
		if (_instance == null)
			_instance = new FloodProtector();
		return _instance;
	}

	// =========================================================
	// Data Field
	private FastMap<Integer,Integer[]> _floodClient;

	// =========================================================
	
	//	L2EMU_EDIT_ADD
	// reuse delays for protected actions (in game ticks 1 tick = 100ms)
	private static final int[] REUSEDELAY = new int[]
	                                                {
		4,                        // PROTECTED_USEITEM             0
		42,                       // PROTECTED_ROLLDICE            1
		42,                       // PROTECTED_FIREWORK            2
		Config.GLOBAL_CHAT_TIME,  // PROTECTED_GLOBAL_CHAT         3
		Config.TRADE_CHAT_TIME,   // PROTECTED_TRADE_CHAT          4
		16,                       // PROTECTED_ITEMPETSUMMON       5
		100,                      // PROTECTED_HEROVOICE           6
		15                        // PROTECTED_UNKNOWNPACKET       7
	                                                };

	// protected actions
	public static final int PROTECTED_USEITEM		= 0;
	public static final int PROTECTED_ROLLDICE		= 1;
	public static final int PROTECTED_FIREWORK		= 2;
	public static final int PROTECTED_GLOBAL_CHAT	= 3;
	public static final int PROTECTED_TRADE_CHAT	= 4;
	public static final int PROTECTED_ITEMPETSUMMON	= 5;
	public static final int PROTECTED_HEROVOICE		= 6;
	public static final int PROTECTED_UNKNOWNPACKET = 7;
    //L2EMU_EDIT_ADD
	
	// =========================================================
	// Constructor
	private FloodProtector()
	{
		 //L2EMU_EDIT
		_log.info("GameServer: Initializing Flood Protector.");
		 //L2EMU_EDIT
		_floodClient = new FastMap<Integer, Integer[]>(Config.FLOODPROTECTOR_INITIALSIZE).setShared(true);
	}
	
	/**
	 * Add a new player to the flood protector
	 * (should be done for all players when they enter the world)
	 * @param playerObjId
	 */
	public void registerNewPlayer(int playerObjId)
	{
		// create a new array
		Integer[] array = new Integer[REUSEDELAY.length];
		for (int i=0; i<array.length; i++)
			array[i] = 0;
		
		// register the player with an empty array
		_floodClient.put(playerObjId, array);
	}
	
	/**
	 * Remove a player from the flood protector
	 * (should be done if player loggs off)
	 * @param playerObjId
	 */
	public void removePlayer(int playerObjId)
	{
		_floodClient.remove(playerObjId);
	}
	
	/**
	 * Return the size of the flood protector
	 * @return size
	 */
	public int getSize()
	{
		return _floodClient.size();
	}
	
	/**
	 * Try to perform the requested action
	 * 
	 * @param playerObjId
	 * @param action
	 * @return true if the action may be performed
	 */
	//L2EMU_EDIT_ADD
	public boolean tryPerformAction(int playerObjId, int action)
	{
		try
		{
			Entry<Integer, Integer[]> entry = _floodClient.getEntry(playerObjId);

			if(entry != null)
			{
				Integer[] value = entry.getValue();

				if(value!=null)
				{
					if (value[action] < GameTimeController.getGameTicks())
					{
						value[action] = GameTimeController.getGameTicks()+REUSEDELAY[action];
						entry.setValue(value);
						return true;
					}
				}
			}
		}
		catch(Exception e)
		{
			_log.warn("FloodProtector: Error,  "+e.getMessage());
		}
		return false;
	}
	//L2EMU_EDIT_ADD
}