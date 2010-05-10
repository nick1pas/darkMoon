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
package com.l2jfree.gameserver.model.actor.knownlist;

import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.L2SiegeGuard;
import com.l2jfree.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class DoorKnownList extends CharKnownList
{
	// =========================================================
	// Data Field

	// =========================================================
	// Constructor
	public DoorKnownList(L2DoorInstance activeChar)
	{
		super(activeChar);
	}

	// =========================================================
	// Method - Public

	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public
	@Override
	public final L2DoorInstance getActiveChar()
	{
		return (L2DoorInstance) _activeChar;
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		if (object instanceof L2SiegeGuard)
			return 800;
		if (!(object instanceof L2PcInstance))
			return 0;
		return 4000;
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2SiegeGuard)
			return 600;
		if (!(object instanceof L2PcInstance))
			return 0;
		return 2000;
	}
}
