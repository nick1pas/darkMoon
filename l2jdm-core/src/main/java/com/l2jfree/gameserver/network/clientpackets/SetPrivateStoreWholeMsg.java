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

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;

public class SetPrivateStoreWholeMsg extends L2GameClientPacket
{
	private static final String	_C__SETPRIVATESTOREWHOLEMSG	= "[C] D0:4A SetPrivateStoreWholeMsg ch[s]";

	private String	_msg;

	@Override
	protected void readImpl()
	{
		_msg = readS();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getActiveChar();
		if (player == null)
			return;
		if (player.getSellList() == null)
		{
			sendAF();
			return;
		}

		player.getSellList().setTitle(_msg);
		sendPacket(new ExPrivateStoreSetWholeMsg(player));

		sendAF();
	}

	@Override
	public String getType()
	{
		return _C__SETPRIVATESTOREWHOLEMSG;
	}
}
