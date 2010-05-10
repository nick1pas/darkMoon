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
package com.l2jfree.gameserver.instancemanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javolution.io.UTF8StreamReader;
import javolution.util.FastMap;
import javolution.xml.stream.XMLStreamConstants;
import javolution.xml.stream.XMLStreamException;
import javolution.xml.stream.XMLStreamReaderImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.geodata.GeoData;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.entity.Instance;
import com.l2jfree.util.L2FastSet;
import com.l2jfree.util.LookupTable;

/**
 * @author evill33t, GodKratos
 * 
 */
public class InstanceManager
{
	private final static Log _log = LogFactory.getLog(InstanceManager.class);

	private final FastMap<Integer, Instance> _instanceList = new FastMap<Integer, Instance>();
	private final FastMap<Integer, InstanceWorld> _instanceWorlds = new FastMap<Integer, InstanceWorld>();

	private final AtomicInteger _instanceIds = new AtomicInteger(300000);

	// InstanceId Names
	private final LookupTable<String> _instanceIdNames = new LookupTable<String>();
	private final Map<Integer, Map<Integer, Long>> _playerInstanceTimes = new FastMap<Integer, Map<Integer, Long>>();
	
	private static final String ADD_INSTANCE_TIME = "INSERT INTO character_instance_time (charId,instanceId,time) values (?,?,?) ON DUPLICATE KEY UPDATE time=?";
	private static final String RESTORE_INSTANCE_TIMES = "SELECT instanceId,time FROM character_instance_time WHERE charId=?";
	private static final String DELETE_INSTANCE_TIME = "DELETE FROM character_instance_time WHERE charId=? AND instanceId=?";
	
	public long getInstanceTime(int playerObjId, int id)
	{
		if (!_playerInstanceTimes.containsKey(playerObjId))
			restoreInstanceTimes(playerObjId);
		if (_playerInstanceTimes.get(playerObjId).containsKey(id))
			return _playerInstanceTimes.get(playerObjId).get(id);
		return -1;
	}

	public Map<Integer,Long> getAllInstanceTimes(int playerObjId)
	{
		if (!_playerInstanceTimes.containsKey(playerObjId))
			restoreInstanceTimes(playerObjId);
		return _playerInstanceTimes.get(playerObjId);
	}

	public void setInstanceTime(int playerObjId, int id, long time)
	{
		if (!_playerInstanceTimes.containsKey(playerObjId))
			restoreInstanceTimes(playerObjId);
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			statement = con.prepareStatement(ADD_INSTANCE_TIME);
			statement.setInt(1, playerObjId);
			statement.setInt(2, id);
			statement.setLong(3, time);
			statement.setLong(4, time);
			statement.execute();
			statement.close();
			_playerInstanceTimes.get(playerObjId).put(id, time);
		}
		catch (Exception e)
		{
			_log.warn("Could not insert character instance time data: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void deleteInstanceTime(int playerObjId, int id)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			statement = con.prepareStatement(DELETE_INSTANCE_TIME);
			statement.setInt(1, playerObjId);
			statement.setInt(2, id);
			statement.execute();
			statement.close();
			_playerInstanceTimes.get(playerObjId).remove(id);
		}
		catch (Exception e)
		{
			_log.warn("Could not delete character instance time data: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void restoreInstanceTimes(int playerObjId)
	{
		if (_playerInstanceTimes.containsKey(playerObjId))
			return; // already restored
		_playerInstanceTimes.put(playerObjId, new FastMap<Integer, Long>());
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_INSTANCE_TIMES);
			statement.setInt(1, playerObjId);
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				int id = rset.getInt("instanceId");
				long time = rset.getLong("time");
				if (time < System.currentTimeMillis())
					deleteInstanceTime(playerObjId, id);
				else
					_playerInstanceTimes.get(playerObjId).put(id, time);
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("Could not delete character instance time data: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public String getInstanceIdName(int id)
	{
		if (_instanceIdNames.containsKey(id))
			return _instanceIdNames.get(id);
		return ("UnknownInstance");
	}
	
	private void loadInstanceNames()
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream(Config.DATAPACK_ROOT + "/data/instancenames.xml");
			XMLStreamReaderImpl xpp = new XMLStreamReaderImpl();
			xpp.setInput(new UTF8StreamReader().setInput(in));
			for (int e = xpp.getEventType(); e != XMLStreamConstants.END_DOCUMENT; e = xpp.next())
			{
				if (e == XMLStreamConstants.START_ELEMENT)
				{
					if (xpp.getLocalName().toString().equals("instance"))
					{
						Integer id = Integer.valueOf(xpp.getAttributeValue(null, "id").toString());
						String name = xpp.getAttributeValue(null, "name").toString();
						_instanceIdNames.put(id, name);
					}
				}
			}
		}
		catch (FileNotFoundException e)
		{
			_log.warn("instancenames.xml could not be loaded: file not found");
		}
		catch (XMLStreamException xppe)
		{
			xppe.printStackTrace();
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public class InstanceWorld
	{
		public int instanceId;
		public int templateId = -1;
		public final L2FastSet<Integer> allowed = new L2FastSet<Integer>().setShared(true);
		public int status;
	}

	public void addWorld(InstanceWorld world)
	{
		_instanceWorlds.put(world.instanceId, world);
	}

	public InstanceWorld getWorld(int instanceId)
	{
		return _instanceWorlds.get(instanceId);
	}

	public InstanceWorld getPlayerWorld(L2PcInstance player)
	{
		for (FastMap.Entry<Integer, InstanceWorld> entry = _instanceWorlds.head(), end = _instanceWorlds.tail();
				(entry = entry.getNext()) != end;)
		{
			// check if the player have a World Instance where he/she is allowed to enter
			InstanceWorld iw = entry.getValue();
			if (iw.allowed.contains(player.getObjectId()))
				return iw;
		}
		return null;
	}

	public Instance getDynamicInstance(L2PcInstance player)
	{
		for (FastMap.Entry<Integer, Instance> entry = _instanceList.head(), end = _instanceList.tail();
				(entry = entry.getNext()) != end;)
		{
			// check if the player is in a dynamic instance
			Instance i = entry.getValue();
			if (i.containsPlayer(player.getObjectId()))
				return i;
		}
		return null;
	}

	public static final InstanceManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private InstanceManager()
	{
		_log.info("Initializing InstanceManager");
		loadInstanceNames();
		_log.info("Loaded " + _instanceIdNames.size() + " instance names");
		
		Instance themultiverse = new Instance(-1);
		themultiverse.setName("multiverse");
		_instanceList.put(themultiverse.getId(), themultiverse);
		_log.info("Multiverse Instance created");

		Instance universe = new Instance(0);
		universe.setName("universe");
		_instanceList.put(universe.getId(), universe);
		_log.info("Universe Instance created");
	}

	public void destroyInstance(int instanceid)
	{
		if (instanceid == 0)
			return;
		Instance temp = _instanceList.get(instanceid);
		if (temp != null)
		{
			temp.removeNpcs();
			temp.removePlayers();
			temp.removeDoors();
			temp.cancelTimer();
			_instanceList.remove(instanceid);
			_instanceWorlds.remove(instanceid);
			GeoData.getInstance().deleteInstanceGeodata(instanceid);
		}
	}

	public Instance getInstance(int instanceid)
	{
		return _instanceList.get(instanceid);
	}
	
	public FastMap<Integer,Instance> getInstances()
	{
		return _instanceList;
	}
	
	@Deprecated
	public boolean createInstance(int id)
	{
		if (getInstance(id) != null)
			return false;
		
		Instance instance = new Instance(id);
		_instanceList.put(instance.getId(), instance);
		return true;
	}
	
	@Deprecated
	public boolean createInstanceFromTemplate(int id, String template)
	{
		if (getInstance(id) != null)
			return false;
		
		Instance instance = Instance.createInstance(id, template);
		_instanceList.put(instance.getId(), instance);
		
		return true;
	}
	
	public int createDynamicInstance(String template)
	{
		Instance instance = Instance.createInstance(_instanceIds.incrementAndGet(), template);
		_instanceList.put(instance.getId(), instance);
		
		return instance.getId();
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final InstanceManager _instance = new InstanceManager();
	}
}
