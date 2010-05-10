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
package com.l2jfree.gameserver.communitybbs.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.communitybbs.bb.Forum;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class ForumsBBSManager extends BaseBBSManager
{
	private final static Log		_log	= LogFactory.getLog(ForumsBBSManager.class);
	private final List<Forum>				_table;
	private int						_lastid	= 1;

	/**
	 * @return
	 */
	public static ForumsBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private ForumsBBSManager()
	{
		_table = new FastList<Forum>();
		load();
	}

	public void addForum(Forum ff)
	{
		if (ff == null)
			return;

		_table.add(ff);

		if (ff.getID() > _lastid)
		{
			_lastid = ff.getID();
		}
	}

	/**
	 *
	 */
	private void load()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_type=0");
			ResultSet result = statement.executeQuery();
			while (result.next())
			{
				int forumId = result.getInt("forum_id");
				Forum f = new Forum(forumId, null);
				addForum(f);
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("data error on Forum (root): ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void initRoot()
	{
		for (Forum f : _table)
			f.vload();
		_log.info("Loaded " + _table.size() + " forums. Last forum id used: " + _lastid);
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.communitybbs.Manager.BaseBBSManager#parsecmd(java.lang.String, com.l2jfree.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param string
	 * @return
	 */
	public Forum getForumByName(String Name)
	{
		for (Forum f : _table)
		{
			if (f.getName().equals(Name))
			{
				return f;
			}
		}

		return null;
	}

	/**
	 * @param name
	 * @param forumByName
	 * @return
	 */
	public Forum createNewForum(String name, Forum parent, int type, int perm, int oid)
	{
		Forum forum = new Forum(name, parent, type, perm, oid);
		forum.insertIntoDb();
		return forum;
	}

	/**
	 * @return
	 */
	public int getANewID()
	{
		return ++_lastid;
	}

	/**
	 * @param idf
	 * @return
	 */
	public Forum getForumByID(int idf)
	{
		for (Forum f : _table)
		{
			if (f.getID() == idf)
			{
				return f;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.communitybbs.Manager.BaseBBSManager#parsewrite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.l2jfree.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ForumsBBSManager _instance = new ForumsBBSManager();
	}
}