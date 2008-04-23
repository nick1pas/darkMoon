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
package net.sf.l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastMap;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.instance.L2TeleporterInstance;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.templates.StatsSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Instance to Manage General Functions Related to Gatekeepers
 * 
 * @author Rayan RPG
 *  
 * @since 727
 * 
 * TODO: finish This class.
 */
public class GatekeeperManager
{
	private static final Log _log = LogFactory.getLog(GatekeeperManager.class.getName());

	private static GatekeeperManager _instance;
	protected static FastMap<Integer, L2TeleporterInstance> _gatekeepers;
	protected static FastMap<Integer, L2Spawn> _spawns;
	protected static FastMap<Integer, StatsSet> _storedInfo;
	protected static FastMap<Integer, ScheduledFuture> _schedules;

	public static enum StatusEnum 
	{
		NORMAL,
		HALF_PRICE,
		BUSY,
		DISABLED
	}

	public GatekeeperManager()
	{
		_gatekeepers = new FastMap<Integer, L2TeleporterInstance>();
		_schedules = new FastMap<Integer,ScheduledFuture>();
		_storedInfo = new FastMap<Integer, StatsSet>();
		_spawns = new FastMap<Integer, L2Spawn>();
	}
	/**
	 * 
	 * @param gkId
	 * @return
	 */
	public StatusEnum getGatekeeperStatusId(int gkId)
	{
		if (_gatekeepers.containsKey(gkId))
			return _gatekeepers.get(gkId).getGkStatus();
		else 
			if (_schedules.containsKey(gkId))
				return StatusEnum.DISABLED;
			else 
				return StatusEnum.NORMAL;
	}
	/**
	 * 
	 * @return
	 */
	public GatekeeperManager.StatusEnum getGatekeeperStatus()
	{
		return getGatekeeperStatus();
	}
	/**
	 * 
	 * @return
	 */
	public static final GatekeeperManager getInstance()
	{
		if (_instance == null)
		{
			_log.info("GameServer: Initializing Gatekeeper Manager.");
			_instance = new GatekeeperManager();
		}
		return _instance;
	} 
	public L2NpcTemplate getValidTemplate(int id)
	{
		L2NpcTemplate template = NpcTable.getInstance().getTemplate(id);
		if (template == null) return null;
		if (!template.getType().equalsIgnoreCase("L2Teleporter")) return null;
		return template;
	}
	/**
	 * Loads GateKeppers
	 */
	private void init()
	{

		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);

			PreparedStatement statement = con.prepareStatement("SELECT * from gatekeepers_spawnlist ORDER BY gk_id");
			ResultSet rset = statement.executeQuery();

			L2Spawn spawnDat;
			L2NpcTemplate template;
			long respawnTime;

			while (rset.next())
			{
				template = getValidTemplate(rset.getInt("gk_id"));
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


					respawnTime = rset.getLong("respawn_time");

					addNewSpawn(spawnDat, respawnTime, rset.getDouble("currentHP"), rset.getDouble("currentMP"), false);
				}
				else
				{
					_log.warn("GatekeeperManager: Could not load gatekeeper #" + rset.getInt("gk_id") + " from DB");
				}
			}
			_log.info("GameServer: Loaded " + _gatekeepers.size() + " Gatekeepers Instances");
			_log.info("GameServer: Scheduled " + _schedules.size() + " Gatekeepers Instances");
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("GatekeeperManager: Couldnt load gatekeeper_spawnlist table");
		}
		catch (Exception e) {_log.error(e.getMessage(),e);}
		finally
		{
			try {con.close();} catch(Exception e) {_log.error(e.getMessage(),e);}
		}
	}
	/**
	 * 
	 * @param spawnDat
	 * @param respawnTime
	 * @param currentHP
	 * @param currentMP
	 * @param storeInDb
	 */
	public void addNewSpawn(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null) return;
		if (_spawns.containsKey(spawnDat.getNpcId())) return;

		int gkId = spawnDat.getNpcId();
		long time = Calendar.getInstance().getTimeInMillis();

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);

		if (respawnTime == 0L || (time > respawnTime))
		{
			L2TeleporterInstance gk = null;


			gk = (L2TeleporterInstance)spawnDat.doSpawn();

			if (gk != null)
			{
				gk.getStatus().setCurrentHp(currentHP);
				gk.getStatus().setCurrentMp(currentMP);
				gk.setGkStatus(StatusEnum.NORMAL);
				_gatekeepers.put(gkId, null);

				StatsSet info = new StatsSet();
				info.set("currentHP", currentHP);
				info.set("currentMP", currentMP);
				info.set("respawnTime", 0L);

				_storedInfo.put(gkId, info);
			}
		}
		else
		{
			ScheduledFuture futureSpawn;
			long spawnTime = respawnTime - Calendar.getInstance().getTimeInMillis();

			futureSpawn = ThreadPoolManager.getInstance().scheduleGeneral(new SpawnSheduler(gkId), spawnTime);

			_schedules.put(gkId, futureSpawn);
		}

		_spawns.put(gkId, spawnDat);

		if (storeInDb)
		{
			java.sql.Connection con = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con.prepareStatement("INSERT INTO gatekeepers_spawnlist (gk_id,amount,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?,?)");
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
				// problem with storing spawn
				_log.warn("GateKeeperManager: Could not store gatekeeper #" + gkId + " in the DB:" + e);
			}
			finally
			{
				try { con.close(); } catch (Exception e) {}
			}
		}
	}
	private class SpawnSheduler implements Runnable
	{
		private int gkId;

		public SpawnSheduler(int npcId)
		{
			gkId = npcId;
		}

		public void run()
		{
			L2TeleporterInstance gk = null;


			gk = (L2TeleporterInstance)_spawns.get(gkId).doSpawn();

			if (gk != null)
			{
				gk.setGkStatus(StatusEnum.NORMAL);
				StatsSet info = new StatsSet();
				info.set("currentHP", gk.getStatus().getCurrentHp());
				info.set("currentMP", gk.getStatus().getCurrentMp());
				info.set("respawnTime", 0L);
				_storedInfo.put(gkId, info);
				GmListTable.broadcastMessageToGMs("Spawning Gatekeepers.");
				_gatekeepers.put(gkId, gk);
			}

			_schedules.remove(gkId);
		}
	}
}