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
package net.sf.l2j.gameserver.model.entity.events;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.Ride;

public class L2EventTeleporter implements Runnable
{
	private L2PcInstance _player = null;
	private int _coordinateX = 0;
	private int _coordinateY = 0;
	private int _coordinateZ = 0;
	boolean _removeBuffs;

	/**
	 * Manages all Teleports done within a Raid Event.
	 * @param player --> Player being teleported
	 * @param coordinateX --> CX
	 * @param coordinateY --> CY
	 * @param coordinateZ --> CZ
	 * @param delay --> Delay to do the actual teleport.
	 * @param removeBuffs --> Boolean to allow removal of buffs.
	 */
	public L2EventTeleporter(L2PcInstance player, int coordinateX, int coordinateY, int coordinateZ , int delay, boolean removeBuffs)
	{
		_player = player;
		_coordinateX = coordinateX;
		_coordinateY = coordinateY;
		_coordinateZ = coordinateZ;
		_removeBuffs = removeBuffs;
		//Espera para hacer el teleport
		long _delay = delay * 1000L;
		if (delay == 0)
			_delay = 0;
		ThreadPoolManager.getInstance().scheduleGeneral(this, _delay);
	}

	public void run()
	{
		if (_player == null)
			return;

		if (_player.isMounted())
		{
			if (_player.isFlying())
				_player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
			Ride dismount = new Ride(_player.getObjectId(), Ride.ACTION_DISMOUNT, 0);
			_player.broadcastPacket(dismount);
			_player.setMountType(0);
			_player.setMountObjectID(0);
		}
		L2Summon summon = _player.getPet();
		if (_removeBuffs && summon != null)
			summon.unSummon(_player);
		if (_removeBuffs)
		{
			for (L2Effect effect : _player.getAllEffects())
			{
				if (effect != null)
					effect.exit();
			}
		}
		_player.getStatus().setCurrentCp(_player.getMaxCp()+5000);
		_player.getStatus().setCurrentHp(_player.getMaxHp()+5000);
		_player.getStatus().setCurrentMp(_player.getMaxMp()+5000);
		_player.teleToLocation(_coordinateX, _coordinateY, _coordinateZ, false);
		_player.broadcastStatusUpdate();
		_player.broadcastUserInfo();
	}
}
