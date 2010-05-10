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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.lang.L2Integer;
import com.l2jfree.util.SingletonSet;

/**
 * @author NB4L1
 */
public final class FriendListManager
{
	private static final Log _log = LogFactory.getLog(FriendListManager.class);
	
	private static final String SELECT_QUERY = "SELECT charId1, charId2 FROM character_friends WHERE charId1=? or charId2=?";
	private static final String INSERT_QUERY = "INSERT INTO character_friends (charId1, charId2) VALUES (?,?)";
	private static final String DELETE_QUERY = "DELETE FROM character_friends WHERE (charId1=? AND charId2=?) OR (charId1=? AND charId2=?)";
	
	public static FriendListManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private final Map<Integer, Set<Integer>> _friends = new FastMap<Integer, Set<Integer>>();
	
	private FriendListManager()
	{
		_log.info("FriendListManager: initialized.");
	}
	
	public synchronized Set<Integer> getFriendList(Integer objectId)
	{
		Set<Integer> set = _friends.get(objectId);
		
		if (set == null)
		{
			_friends.put(objectId, set = new SingletonSet<Integer>());
			
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				
				PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
				statement.setInt(1, objectId);
				statement.setInt(2, objectId);
				
				ResultSet rset = statement.executeQuery();
				
				while (rset.next())
				{
					Integer objId1 = L2Integer.valueOf(rset.getInt("charId1"));
					Integer objId2 = L2Integer.valueOf(rset.getInt("charId2"));
					
					Set<Integer> set1 = _friends.get(objId1);
					if (set1 != null)
						set1.add(objId2);
					
					Set<Integer> set2 = _friends.get(objId2);
					if (set2 != null)
						set2.add(objId1);
				}
				
				rset.close();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.warn("", e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
		
		return set;
	}
	
	public synchronized boolean insert(Integer objId1, Integer objId2)
	{
		boolean modified = false;
		
		modified |= _friends.containsKey(objId1) && _friends.get(objId1).add(objId2);
		modified |= _friends.containsKey(objId2) && _friends.get(objId2).add(objId1);
		
		if (!modified)
			return false;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement(INSERT_QUERY);
			statement.setInt(1, Math.min(objId1, objId2));
			statement.setInt(2, Math.max(objId1, objId2));
			
			statement.execute();
			
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	public synchronized boolean remove(Integer objId1, Integer objId2)
	{
		boolean modified = false;
		
		modified |= _friends.containsKey(objId1) && _friends.get(objId1).remove(objId2);
		modified |= _friends.containsKey(objId2) && _friends.get(objId2).remove(objId1);
		
		if (!modified)
			return false;
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement(DELETE_QUERY);
			statement.setInt(1, objId1);
			statement.setInt(2, objId2);
			statement.setInt(3, objId2);
			statement.setInt(4, objId1);
			
			statement.execute();
			
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final FriendListManager _instance = new FriendListManager();
	}
}
