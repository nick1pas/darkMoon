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
package com.l2jfree.gameserver.network.clientpackets;

import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.RecipeShopSellList;

/**
 * Packet sent when player clicks "< Previous" button when viewing a selected recipe in the
 * manufacture shop.
 */
public class RequestRecipeShopSellList extends L2GameClientPacket
{
	private static final String	_C__REQUESTRECIPESHOPSELLLIST	= "[C] 0C RequestRecipeShopSellList c[d]";

	private int _targetId;

	@Override
	protected void readImpl()
	{
		_targetId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getActiveChar();
		if (player == null)
			return;

		if (player.isAlikeDead())
		{
			sendAF();
			return;
		}

		final L2PcInstance manufacturer;
		if (player.getTargetId() == _targetId)
			manufacturer = player.getTarget(L2PcInstance.class);
		else
			manufacturer = L2World.getInstance().findPlayer(_targetId);
		if (manufacturer != null)
			sendPacket(new RecipeShopSellList(player, manufacturer));

		sendAF();
	}

	@Override
	public String getType()
	{
		return _C__REQUESTRECIPESHOPSELLLIST;
	}
}
