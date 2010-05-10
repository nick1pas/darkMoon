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
package com.l2jfree.gameserver.handler.itemhandlers;

import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public final class SoulShots implements IItemHandler
{
	private static final int[]	ITEM_IDS	=
											{ 5789, 1835, 1463, 1464, 1465, 1466, 1467, 22082, 22083, 22084, 22085, 22086 };

	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (playable instanceof L2PcInstance)
			playable.getShots().chargeSoulshot(item);
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
