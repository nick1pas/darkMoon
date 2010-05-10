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
package com.l2jfree.gameserver.model.entity;

import java.util.List;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.mapregion.TeleportWhereType;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.tools.random.Rnd;

public class Entity
{
	protected static Log _log = LogFactory.getLog(Entity.class);

	protected L2Zone _zone;

	public void registerZone(L2Zone zone)
	{
		_zone = zone;
	}

	public L2Zone getZone()
	{
		return _zone;
	}

	public int getTownId()
	{
		if (_zone != null) return _zone.getTownId();

		_log.error(getClassName()+" has no zone defined");
		return 0; // Talking Island
	}

	public int getCastleId()
	{
		if (_zone != null) return _zone.getCastleId();

		_log.error(getClassName()+" has no zone defined");
		return 1; // Gludio
	}

	public int getFortId()
	{
		if (_zone != null) return _zone.getFortId();

		_log.error(getClassName()+" has no zone defined");
		return 0;
	}

	public boolean checkIfInZone(L2Character cha)
	{
		if (_zone != null) return _zone.isInsideZone(cha);

		_log.error(getClassName()+" has no zone defined");
		return false;
	}

	public boolean checkIfInZone(int x, int y, int z)
	{
		if (_zone != null) return _zone.isInsideZone(x, y, z);

		_log.error(getClassName()+" has no zone defined");
		return false;
	}

	public boolean checkIfInZone(int x, int y)
	{
		if (_zone != null) return _zone.isInsideZone(x, y);

		_log.error(getClassName()+" has no zone defined");
		return false;
	}

	public double getDistanceToZone(int x, int y)
	{
		if (_zone != null) return _zone.getDistanceToZone(x, y);

		_log.error(getClassName()+" has no zone defined");
		return Double.MAX_VALUE;
	}

	protected List<L2PcInstance> getPlayersInside()
	{
		List<L2PcInstance> lst = new FastList<L2PcInstance>();
		for (L2Character cha : getZone().getCharactersInside())
		{
			if (cha instanceof L2PcInstance)
				lst.add((L2PcInstance)cha);
		}
		return lst;
	}

	protected L2PcInstance getRandomPlayer()
	{
		List<L2PcInstance> lst = getPlayersInside();
		if (!lst.isEmpty())
		{
			return lst.get(Rnd.get(lst.size()));
		}
		return null;
	}

	/**
	 * @param cha
	 */
	protected boolean checkBanish(L2PcInstance cha)
	{
		return true;
	}

	public void banishForeigners()
	{
		for (L2PcInstance player : getPlayersInside())
		{
			if (checkBanish(player))
				player.teleToLocation(TeleportWhereType.Town);
		}
	}

	public void broadcastToPlayers(String message)
	{
		SystemMessage msg = SystemMessage.sendString(message);
		for (L2PcInstance player : getPlayersInside())
		{
			player.sendPacket(msg);
		}
	}

	public void broadcastToPlayers(L2GameServerPacket gsp)
	{
		for (L2PcInstance player : getPlayersInside())
		{
			player.sendPacket(gsp);
		}
	}

	public String getClassName()
	{
		String[] parts = this.getClass().toString().split("\\.");
		return parts[parts.length-1];
	}
}
