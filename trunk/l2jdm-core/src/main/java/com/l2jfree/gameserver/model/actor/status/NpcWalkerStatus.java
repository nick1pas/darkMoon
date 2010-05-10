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
package com.l2jfree.gameserver.model.actor.status;

import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2NpcWalkerInstance;

/**
 * @author NB4L1
 */
public final class NpcWalkerStatus extends NpcStatus
{
	public NpcWalkerStatus(L2NpcWalkerInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2NpcWalkerInstance getActiveChar()
	{
		return (L2NpcWalkerInstance)_activeChar;
	}
	
	@Override
	boolean canReduceHp(double value, L2Character attacker, boolean awake, boolean isDOT, boolean isConsume)
	{
		return false;
	}
}
