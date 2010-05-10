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
package ai.individual;

import javolution.util.FastMap;
import javolution.util.FastSet;
import ai.group_template.L2AttackableAIScript;

import com.l2jfree.gameserver.model.actor.L2Attackable;
import com.l2jfree.gameserver.model.actor.L2Npc;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.NpcSay;
import com.l2jfree.gameserver.network.serverpackets.PlaySound;
import com.l2jfree.tools.random.Rnd;

/**
 * Core AI
 * @author DrLecter
 * Revised By Emperorc
 */
public class Core extends L2AttackableAIScript
{
	private static final int CORE = 29006;
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	//private static final int DICOR = 29009;
	//private static final int VALIDUS = 29010;
	private static final int SUSCEPTOR = 29011;
	//private static final int PERUM = 29012;
	//private static final int PREMO = 29013;

	private final FastMap<Integer, CoreStatus> _status = new FastMap<Integer, CoreStatus>().setShared(true);

	public Core(int id, String name, String descr)
	{
		super(id, name, descr);
		registerMobs(CORE, DEATH_KNIGHT, DOOM_WRAITH, SUSCEPTOR);
	}

	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == CORE)
		{
			CoreStatus status = new CoreStatus();
			_status.put(npc.getObjectId(), status);
			npc.broadcastPacket(new PlaySound(1, npc, 10000, "BS01_A"));
			//Spawn minions
			FastSet<L2Attackable> minions = status.getMinions();
			L2Attackable minion;
			for (int i = 0; i < 5; i++)
			{
				int x = 16800 + i * 360;
				minion = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 110000, npc.getZ(), 280 + Rnd.get(40), false, 0, npc.getInstanceId());
				minions.add(minion);
				minion = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 109000, npc.getZ(), 280 + Rnd.get(40), false, 0, npc.getInstanceId());
				minions.add(minion);
				x = 16800 + i * 600;
				minion = (L2Attackable) addSpawn(DOOM_WRAITH, x, 109300, npc.getZ(), 280 + Rnd.get(40), false, 0, npc.getInstanceId());
				minions.add(minion);
			}
			for (int i = 0; i < 4; i++)
			{
				int x = 16800 + i * 450;
				minion = (L2Attackable) addSpawn(SUSCEPTOR, x, 110300, npc.getZ(), 280 + Rnd.get(40), false, 0, npc.getInstanceId());
				minions.add(minion);
			}
		}
		return null;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.contains("spawn_minion"))
		{
			Integer oId = Integer.valueOf(event.split("_")[0]);
			CoreStatus status = _status.get(oId);
			if (event.contains("de"))
			{
				for (FastSet.Record r = status.getMinions().head(), end = status.getMinions().tail();
					(r = r.getNext()) != end;)
					status.getMinions().valueOf(r).decayMe();
				FastSet.recycle(status.getMinions());
			}
			else
			{
				for (FastMap.Entry<Integer, CoreStatus> entry = _status.head(), end = _status.tail();
					(entry = entry.getNext()) != end;)
				{
					if (entry.getKey() == oId)
					{
						L2Attackable minion = (L2Attackable) addSpawn(npc.getNpcId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, npc.getInstanceId());
						entry.getValue().getMinions().add(minion);
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == CORE)
		{
			CoreStatus status = _status.get(npc.getObjectId());
			if (status.isAttacked())
			{
				if (Rnd.get(100) == 0)
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Removing intruders."));
			}
			else
			{
				status.setAttacked(true);
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "A non-permitted target has been discovered."));
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), "Starting intruder removal system."));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == CORE)
		{
			CoreStatus status = _status.get(npc.getObjectId());
			status.setAlive(false);
			int objId = npc.getObjectId();
			npc.broadcastPacket(new PlaySound(1, npc, 10000, "BS02_D"));
			npc.broadcastPacket(new NpcSay(objId, 0, npcId, "A fatal error has occurred."));
			npc.broadcastPacket(new NpcSay(objId, 0, npcId, "System is being shut down..."));
			npc.broadcastPacket(new NpcSay(objId, 0, npcId, "......"));
			status.setAttacked(false);
			addSpawn(31842, 16502, 110165, -6394, 0, false, 900000, npc.getInstanceId());
			addSpawn(31842, 18948, 110166, -6397, 0, false, 900000, npc.getInstanceId());

			startQuestTimer(objId + "_despawn_minions", 20000, null, null);
			cancelQuestTimers(objId + "_spawn_minion");
		}
		else
		{
			for (FastMap.Entry<Integer, CoreStatus> entry = _status.head(), end = _status.tail();
				(entry = entry.getNext()) != end;)
			{
				CoreStatus status = entry.getValue();
				if (status.isAlive() && status.getMinions().contains(npc))
				{
					status.getMinions().remove(npc);
					startQuestTimer(entry.getKey() + "_spawn_minion", 60000, npc, null);
					break;
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}

	public static void main(String[] args)
	{
		new Core(-1, "core", "ai");
	}

	public static class CoreStatus
	{
		private boolean _alive;
		private boolean _attacked;
		private final FastSet<L2Attackable> _minions;

		public CoreStatus()
		{
			_alive = true;
			_attacked = false;
			_minions = FastSet.newInstance();
		}

		public final boolean isAlive()
		{
			return _alive;
		}

		public final void setAlive(boolean alive)
		{
			_alive = alive;
		}

		public final boolean isAttacked()
		{
			return _attacked;
		}

		public final void setAttacked(boolean attacked)
		{
			_attacked = attacked;
		}

		public final FastSet<L2Attackable> getMinions()
		{
			return _minions;
		}
	}
}
