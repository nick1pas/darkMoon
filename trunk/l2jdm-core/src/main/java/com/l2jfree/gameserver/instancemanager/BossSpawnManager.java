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

import java.util.concurrent.ScheduledFuture;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.datatables.GmListTable;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.datatables.SpawnTable;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.actor.L2Boss;
import com.l2jfree.gameserver.templates.StatsSet;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;
import com.l2jfree.tools.random.Rnd;

/**
 * 
 * @author Crion, kombat
 */
public abstract class BossSpawnManager
{
	protected final static Log						_log	= LogFactory.getLog(BossSpawnManager.class);

	protected final FastMap<Integer, L2Boss>				_bosses;
	protected final FastMap<Integer, L2Spawn>				_spawns;
	protected final FastMap<Integer, StatsSet>				_storedInfo;
	protected final FastMap<Integer, ScheduledFuture<?>>	_schedules;

	public static enum StatusEnum
	{
		ALIVE, DEAD, UNDEFINED
	}

	public BossSpawnManager()
	{
		_bosses = new FastMap<Integer, L2Boss>().setShared(true);
		_schedules = new FastMap<Integer, ScheduledFuture<?>>().setShared(true);
		_storedInfo = new FastMap<Integer, StatsSet>().setShared(true);
		_spawns = new FastMap<Integer, L2Spawn>().setShared(true);
		init();
	}

	protected abstract void init();

	private class SpawnTask implements Runnable
	{
		private final int	bossId;

		public SpawnTask(int npcId)
		{
			bossId = npcId;
		}

		public void run()
		{
			L2Boss raidboss = null;

			if (bossId == 25328)
				raidboss = DayNightSpawnManager.getInstance().handleBoss(_spawns.get(bossId));
			else
				raidboss = (L2Boss) _spawns.get(bossId).doSpawn();

			if (raidboss != null)
			{
				raidboss.setRaidStatus(StatusEnum.ALIVE);

				StatsSet info = new StatsSet();
				info.set("currentHp", raidboss.getStatus().getCurrentHp());
				info.set("currentMp", raidboss.getStatus().getCurrentMp());
				info.set("respawnTime", 0L);

				_storedInfo.put(bossId, info);

				GmListTable.broadcastMessageToGMs("Spawning Raid Boss " + raidboss.getName());

				_bosses.put(bossId, raidboss);
			}
			_schedules.remove(bossId);
		}
	}

	public void updateStatus(L2Boss boss, boolean isBossDead)
	{
		if (!_storedInfo.containsKey(boss.getNpcId()))
			return;

		StatsSet info = _storedInfo.get(boss.getNpcId());

		if (isBossDead)
		{
			boss.setRaidStatus(StatusEnum.DEAD);

			long respawnTime;
			int RespawnMinDelay = boss.getSpawn().getRespawnMinDelay();
			int RespawnMaxDelay = boss.getSpawn().getRespawnMaxDelay();
			long respawn_delay = Rnd.get((int) (RespawnMinDelay * 1000 * Config.RAID_MIN_RESPAWN_MULTIPLIER),
					(int) (RespawnMaxDelay * 1000 * Config.RAID_MAX_RESPAWN_MULTIPLIER));
			respawnTime = System.currentTimeMillis() + respawn_delay;

			info.set("currentHp", boss.getMaxHp());
			info.set("currentMp", boss.getMaxMp());
			info.set("respawnTime", respawnTime);

			_log.info("BossSpawnManager: Updated " + boss.getName() + " respawn time to " + respawnTime);

			if (!_schedules.containsKey(boss.getNpcId()))
			{
				ScheduledFuture<?> futureSpawn = ThreadPoolManager.getInstance().scheduleGeneral(new SpawnTask(boss.getNpcId()), respawn_delay);

				_schedules.put(boss.getNpcId(), futureSpawn);
				// To update immediately Database uncomment on the following line, to post the hour of respawn raid boss on your site for example or to envisage a crash landing of the waiter.
				updateDb();
			}
		}
		else
		{
			boss.setRaidStatus(StatusEnum.ALIVE);

			info.set("currentHp", boss.getStatus().getCurrentHp());
			info.set("currentMp", boss.getStatus().getCurrentMp());
			info.set("respawnTime", 0L);
		}

		_storedInfo.remove(boss.getNpcId());
		_storedInfo.put(boss.getNpcId(), info);
		_log.info(getClass().getSimpleName()+": Updated "+NpcTable.getInstance().getTemplate(boss.getNpcId()).getName()+"(" +boss.getNpcId()+ ") status to " +boss.getRaidStatus());
	}

	public void addNewSpawn(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null)
			return;
		if (_spawns.containsKey(spawnDat.getNpcId()))
			return;

		int bossId = spawnDat.getNpcId();
		long time = System.currentTimeMillis();

		SpawnTable.getInstance().addNewSpawn(spawnDat, false);

		if (respawnTime == 0L || (time > respawnTime))
		{
			L2Boss raidboss = null;

			if (bossId == 25328)
				raidboss = DayNightSpawnManager.getInstance().handleBoss(spawnDat);
			else
				raidboss = (L2Boss) spawnDat.doSpawn();

			if (raidboss != null)
			{
				raidboss.getStatus().setCurrentHp(currentHP);
				raidboss.getStatus().setCurrentMp(currentMP);
				raidboss.setRaidStatus(StatusEnum.ALIVE);

				_bosses.put(bossId, raidboss);

				StatsSet info = new StatsSet();
				info.set("currentHp", currentHP);
				info.set("currentMp", currentMP);
				info.set("respawnTime", 0L);

				_storedInfo.put(bossId, info);
			}
		}
		else
		{
			ScheduledFuture<?> futureSpawn;
			long spawnTime = respawnTime - System.currentTimeMillis();
			futureSpawn = ThreadPoolManager.getInstance().scheduleGeneral(new SpawnTask(bossId), spawnTime);
			_schedules.put(bossId, futureSpawn);
		}

		_spawns.put(bossId, spawnDat);

		if (storeInDb)
			insertIntoDb(spawnDat, respawnTime, currentHP, currentMP);
	}

	protected abstract void insertIntoDb(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP);

	public void deleteSpawn(L2Spawn spawnDat, boolean updateDb)
	{
		if (spawnDat == null)
			return;
		if (!_spawns.containsKey(spawnDat.getNpcId()))
			return;

		int bossId = spawnDat.getNpcId();

		SpawnTable.getInstance().deleteSpawn(spawnDat, false);
		_spawns.remove(bossId);

		if (_bosses.containsKey(bossId))
			_bosses.remove(bossId);

		if (_schedules.containsKey(bossId))
		{
			ScheduledFuture<?> f = _schedules.get(bossId);
			f.cancel(true);
			_schedules.remove(bossId);
		}

		if (_storedInfo.containsKey(bossId))
			_storedInfo.remove(bossId);

		if (updateDb)
			deleteFromDb(spawnDat, bossId);
	}

	public abstract void updateSpawn(int bossId, int x, int y, int z, int h);

	protected abstract void deleteFromDb(L2Spawn spawnDat, int bossId);

	protected abstract void updateDb();

	public StatusEnum getRaidBossStatusId(int bossId)
	{
		if (_bosses.containsKey(bossId))
			return _bosses.get(bossId).getRaidStatus();
		else if (_schedules.containsKey(bossId))
			return StatusEnum.DEAD;
		else
			return StatusEnum.UNDEFINED;
	}

	public abstract L2NpcTemplate getValidTemplate(int bossId);

	public void notifySpawnNightBoss(L2Boss raidboss)
	{
		StatsSet info = new StatsSet();
		info.set("currentHp", raidboss.getStatus().getCurrentHp());
		info.set("currentMp", raidboss.getStatus().getCurrentMp());
		info.set("respawnTime", 0L);

		raidboss.setRaidStatus(StatusEnum.ALIVE);

		_storedInfo.put(raidboss.getNpcId(), info);

		GmListTable.broadcastMessageToGMs("Spawning Raid Boss " + raidboss.getName());

		_bosses.put(raidboss.getNpcId(), raidboss);
	}

	public boolean isDefined(int bossId)
	{
		return _spawns.containsKey(bossId);
	}

	public FastMap<Integer, L2Boss> getBosses()
	{
		return _bosses;
	}

	public FastMap<Integer, L2Spawn> getSpawns()
	{
		return _spawns;
	}

	public void reloadBosses()
	{
		init();
	}

	/**
	 * Saves all raidboss status and then clears all info from memory,
	 * including all schedules.
	 */
	public void cleanUp()
	{
		updateDb();
		_bosses.clear();
		if (_schedules != null)
		{
			for (Integer bossId : _schedules.keySet())
			{
				ScheduledFuture<?> f = _schedules.get(bossId);
				f.cancel(true);
			}
		}
		_schedules.clear();
		_storedInfo.clear();
		_spawns.clear();
	}
}