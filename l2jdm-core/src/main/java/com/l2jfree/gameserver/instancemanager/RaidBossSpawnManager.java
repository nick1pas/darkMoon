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

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.actor.L2Boss;
import com.l2jfree.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jfree.gameserver.templates.StatsSet;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

/**
 * 
 * @author godson
 */
public class RaidBossSpawnManager extends BossSpawnManager
{
	public static RaidBossSpawnManager getInstance()
	{
		return SingletonHolder._instance;
	}

	@Override
	protected void init()
	{
		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);

			PreparedStatement statement = con.prepareStatement("SELECT * FROM raidboss_spawnlist ORDER BY boss_id");
			ResultSet rset = statement.executeQuery();

			L2Spawn spawnDat;
			L2NpcTemplate template;

			while (rset.next())
			{
				template = getValidTemplate(rset.getInt("boss_id"));
				if (template != null)
				{
					spawnDat = new L2Spawn(template);
					spawnDat.setLocx(rset.getInt("loc_x"));
					spawnDat.setLocy(rset.getInt("loc_y"));
					spawnDat.setLocz(rset.getInt("loc_z"));
					spawnDat.setAmount(rset.getInt("amount"));
					spawnDat.setHeading(rset.getInt("heading"));
					spawnDat.setRespawnMinDelay(rset.getInt("respawn_min_delay"));
					spawnDat.setRespawnMaxDelay(rset.getInt("respawn_max_delay"));

					addNewSpawn(spawnDat, rset.getLong("respawn_time"), rset.getDouble("currentHp"), rset.getDouble("currentMp"), false);
				}
				else
				{
					_log.warn("RaidBossSpawnManager: Could not load raidboss #" + rset.getInt("boss_id") + " from DB");
				}
			}

			_log.info("RaidBossSpawnManager: Loaded " + _bosses.size() + " Instances");
			_log.info("RaidBossSpawnManager: Scheduled " + _schedules.size() + " Instances");

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("RaidBossSpawnManager: Couldnt load raidboss_spawnlist table");
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@Override
	protected void insertIntoDb(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con
					.prepareStatement("INSERT INTO raidboss_spawnlist (boss_id,amount,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) VALUES(?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, spawnDat.getNpcId());
			statement.setInt(2, spawnDat.getAmount());
			statement.setInt(3, spawnDat.getLocx());
			statement.setInt(4, spawnDat.getLocy());
			statement.setInt(5, spawnDat.getLocz());
			statement.setInt(6, spawnDat.getHeading());
			statement.setLong(7, respawnTime);
			statement.setDouble(8, currentHP);
			statement.setDouble(9, currentMP);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			// Problem with storing spawn
			_log.warn("RaidBossSpawnManager: Could not store raidboss #" + spawnDat.getNpcId() + " in the DB:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@Override
	public void updateSpawn(int bossId, int x, int y, int z, int h)
	{
		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("UPDATE raidboss_spawnlist SET loc_x = ?, loc_y = ?, loc_z = ?, heading = ? WHERE boss_id=?");
			statement.setInt(1, x);
			statement.setInt(2, y);
			statement.setInt(3, z);
			statement.setInt(4, h);
			statement.setInt(5, bossId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("RaidBossSpawnManager: Could not update raidboss #" + bossId + " in DB: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@Override
	protected void deleteFromDb(L2Spawn spawnDat, int bossId)
	{
		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("DELETE FROM raidboss_spawnlist WHERE boss_id=?");
			statement.setInt(1, bossId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			// Problem with deleting spawn
			_log.warn("RaidBossSpawnManager: Could not remove raidboss #" + bossId + " from DB: ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@Override
	protected void updateDb()
	{
		for (Integer bossId : _storedInfo.keySet())
		{
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				L2Boss boss = _bosses.get(bossId);
				if (boss == null)
					continue;
				if (boss.getRaidStatus().equals(StatusEnum.ALIVE))
					updateStatus(boss, false);

				StatsSet info = _storedInfo.get(bossId);
				if (info == null)
					continue;

				PreparedStatement statement = con
						.prepareStatement("UPDATE raidboss_spawnlist SET respawn_time = ?, currentHp = ?, currentMp = ? WHERE boss_id = ?");
				statement.setLong(1, info.getLong("respawnTime"));
				statement.setDouble(2, info.getDouble("currentHp"));
				statement.setDouble(3, info.getDouble("currentMp"));
				statement.setInt(4, bossId);
				statement.execute();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.error("RaidBossSpawnManager: Couldnt update raidboss_spawnlist table", e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
	}

	@Override
	public L2NpcTemplate getValidTemplate(int bossId)
	{
		L2NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
		if (template == null)
			return null;
		if (!template.isAssignableTo(L2RaidBossInstance.class))
			return null;
		return template;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final RaidBossSpawnManager _instance = new RaidBossSpawnManager();
	}
}
