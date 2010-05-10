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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.model.L2World;

public final class RecordTable
{
	private static final Log _log = LogFactory.getLog(RecordTable.class);
	
	private static final class SingletonHolder
	{
		private static RecordTable INSTANCE = new RecordTable();
	}
	
	public static RecordTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private int _record;
	private String _date;
	
	private RecordTable()
	{
		load();
	}
	
	private void load()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con
				.prepareStatement("SELECT maxplayer, date FROM record ORDER BY maxplayer DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();
			
			if (rset.next())
			{
				_record = rset.getInt("maxplayer");
				_date = rset.getString("date");
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public synchronized void update()
	{
		final int onlinePlayerCount = L2World.getInstance().getAllPlayersCount();
		
		if (_record < onlinePlayerCount)
		{
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				
				PreparedStatement statement = con.prepareStatement("INSERT INTO record (maxplayer, date) VALUES (?, NOW())");
				statement.setInt(1, onlinePlayerCount);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warn("", e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
			
			load();
		}
	}
	
	public int getRecord()
	{
		return _record;
	}
	
	public String getDate()
	{
		return _date;
	}
}
