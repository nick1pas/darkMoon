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
package com.l2jfree.gameserver.instancemanager.hellbound;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.datatables.DoorTable;
import com.l2jfree.gameserver.instancemanager.InstanceManager;
import com.l2jfree.gameserver.model.Location;
import com.l2jfree.gameserver.model.entity.Instance;
import com.l2jfree.gameserver.model.entity.hellbound.TowerOfNaiaInstance;
import com.l2jfree.gameserver.model.entity.hellbound.TowerOfNaiaRoom;

/**
 * @author hex1r0
 */
public final class TowerOfNaiaManager
{
	private static Log _log = LogFactory.getLog(TowerOfNaiaManager.class);
	
	// Bridge doors between Tully and Naia towers
	public static final int[] TOWER_ENTER_DOOR_IDS =
		{ 20250004, 20250005, 20250006, 20250007, 20250008, 20250009 };
	
	//public static final Location ROOF_START_POINT 			= new Location(16430, 244437, 11618);
	public static final Location WAITING_ROOM_START_POINT = new Location(-47211, 246115, -9129);
	
	public static final int ROOF_LOCK_ID 				= 18491;
	public static final int ROOF_CONTROLLER_ID 			= 18492;
	public static final int WARD_ID 					= 18493;
	public static final int ROOM_CONTROLLER_ID 			= 18494; // Ingenious Contraption
	public static final int DARION_ID 					= 25603;
	
	public static FastList<TowerOfNaiaRoom> ROOMS = new FastList<TowerOfNaiaRoom>();
	
	private int _instanceCount = 0;
	
	public void init()
	{
		_log.info("TowerOfNaiaManager: Init.");
		ROOMS.clear();
		for (int i = 0; i < 12; i++)
		{
			TowerOfNaiaRoom room = new TowerOfNaiaRoom();
			room.init(i + 1);
			ROOMS.add(room);
		}
		Instance.registerInstanceFactory(new TowerOfNaiaInstance.NaiaFactory());
		openEnteranceDoors(); // Open doors first, if Darion spawns close them
		_log.info("TowerOfNaiaManager: Done.");
	}
	
	public int startInstance()
	{
		if (_instanceCount > 0 && !Config.ALLOW_NAIA_MULTY_PARTY_INVASION)
			return Integer.MIN_VALUE;
		
		_instanceCount++;
		int instanceId = InstanceManager.getInstance().createDynamicInstance("TowerOfNaiaRoom.xml");
		prepareRoom(instanceId);
		return instanceId;
	}
	
	public void finishInstance(final int instanceId)
	{
		_instanceCount--;
		InstanceManager.getInstance().destroyInstance(instanceId);
		DoorTable.getInstance().openDoors(new int[]	{ 18250024, 18250025 }); // Open doors to the Core of Naia
	}
	
	public void prepareRoom(final int instanceId)
	{
		TowerOfNaiaInstance instance = (TowerOfNaiaInstance) InstanceManager.getInstance().getInstance(instanceId);
		if (instance == null)
			return;
		 
		instance.prepareRoom();
	}
	
	public void startRoomInvasion(final int instanceId)
	{
		TowerOfNaiaInstance instance = (TowerOfNaiaInstance) InstanceManager.getInstance().getInstance(instanceId);
		if (instance == null)
			return;
		
		instance.startRoomInvasion();
	}
	
	public void finishRoomInvasion(final int instanceId)
	{
		TowerOfNaiaInstance instance = (TowerOfNaiaInstance) InstanceManager.getInstance().getInstance(instanceId);
		if (instance == null)
			return;
		
		instance.finishRoomInvasion();
	}
	
	public void notifyMobKilled(final int instanceId)
	{
		TowerOfNaiaInstance instance = (TowerOfNaiaInstance) InstanceManager.getInstance().getInstance(instanceId);
		if (instance == null)
			return;
		
		instance.areAllNpcsKilled();
	}
	
	public void openEnteranceDoors()
	{
		DoorTable.getInstance().openDoors(TOWER_ENTER_DOOR_IDS);
	}
	
	public void closeEnteranceDoors()
	{
		DoorTable.getInstance().closeDoors(TOWER_ENTER_DOOR_IDS);
	}
	
	private static final class SingletonHolder
	{
		public static final TowerOfNaiaManager INSTANCE = new TowerOfNaiaManager();
	}
	
	public static TowerOfNaiaManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
