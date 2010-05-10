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

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.actor.L2Npc;
import com.l2jfree.gameserver.model.entity.ClanHall;

/**
 * Alternate siege guard manager specifically for clan halls, without unnecessary clutter.
 * @author Savormix
 */
public final class ContestableHideoutGuardManager
{
	private static final Log _log = LogFactory.getLog(ContestableHideoutGuardManager.class);
	private static final String LOAD_SIEGE_GUARDS = "SELECT id,npcId,x,y,z,heading,respawnDelay FROM clanhall_siege_guards WHERE hallId=?";
	private final ClanHall _hideout;
	private L2Spawn[] _guardSpawn = new L2Spawn[0];

	public ContestableHideoutGuardManager(ClanHall hideout)
	{
		_hideout = hideout;
		load();
	}

	public final void load()
	{
		Connection con = null;
		FastList<L2Spawn> guards = new FastList<L2Spawn>(50);
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_SIEGE_GUARDS);
			ps.setInt(1, _hideout.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				L2Spawn s = new L2Spawn(NpcTable.getInstance().getTemplate(rs.getInt("npcId")));
				s.setId(rs.getInt("id"));
				s.setAmount(1);
				s.setLocx(rs.getInt("x"));
				s.setLocy(rs.getInt("y"));
				s.setLocz(rs.getInt("z"));
				s.setHeading(rs.getInt("heading"));
				s.setRespawnDelay(rs.getInt("respawnDelay"));
				s.setLocation(0);
				guards.add(s);
			}
			_guardSpawn = guards.toArray(new L2Spawn[guards.size()]);
		}
		catch (Exception e)
		{
			_log.error("Failed loading " + _hideout.getName() + "'s siege guards!", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public final void spawnSiegeGuards()
	{
		for (L2Spawn s : _guardSpawn)
			if (s != null)
				s.init();
	}

	public final void despawnSiegeGuards()
	{
		for (L2Spawn s : _guardSpawn)
		{
			if (s == null) continue;
			s.stopRespawn();
			L2Npc guard = s.getLastSpawn();
			if (guard != null)
				guard.doDie(guard);
		}
	}

	public final ClanHall getContestableClanHall()
	{
		return _hideout;
	}
}
