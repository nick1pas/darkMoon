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
package com.l2jfree.gameserver.model.actor.instance;

import javolution.util.FastMap;

import com.l2jfree.gameserver.datatables.ClanTable;
import com.l2jfree.gameserver.instancemanager.ClanHallManager;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.status.CCHLeaderStatus;
import com.l2jfree.gameserver.model.actor.status.CharStatus;
import com.l2jfree.gameserver.model.entity.CCHSiege;
import com.l2jfree.gameserver.model.entity.ClanHall;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

/**
 * Represents the monster that is the leader of a clan hall.<BR>
 * When this monster is killed, the clan that is registered and did the most damage takes over
 * the clan hall for the next week.
 * @author Savormix
 */
public final class L2CCHBossInstance extends L2MonsterInstance
{
	private final FastMap<Integer, Integer>	_damage;
	private final int						_hideoutIndex;

	/**
	 * @param objectId
	 * @param template
	 */
	public L2CCHBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		getStatus();
		_damage = new FastMap<Integer, Integer>().setShared(true);
		switch (getNpcId())
		{
		case 35410:
			_hideoutIndex = 34;
			break;
		case 35629:
			_hideoutIndex = 64;
			break;
		default:
			_hideoutIndex = -1;
		}
	}

	@Override
	protected CharStatus initStatus()
	{
		return new CCHLeaderStatus(this);
	}

	@Override
	public CCHLeaderStatus getStatus()
	{
		return (CCHLeaderStatus) _status;
	}

	@Override
	public final boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		int max = -1;
		int winner = -1;
		for (Integer i : _damage.keySet())
		{
			if (_damage.get(i) > max)
			{
				max = _damage.get(i);
				winner = i;
			}
		}
		getHideout().getSiege().endSiege(ClanTable.getInstance().getClan(winner));
		return true;
	}

	public final FastMap<Integer, Integer> getDamageTable()
	{
		return _damage;
	}

	public final CCHSiege getSiege()
	{
		return getHideout().getSiege();
	}

	private final ClanHall getHideout()
	{
		if (_hideoutIndex < 0)
			return null;
		else
			return ClanHallManager.getInstance().getClanHallById(_hideoutIndex);
	}
}
