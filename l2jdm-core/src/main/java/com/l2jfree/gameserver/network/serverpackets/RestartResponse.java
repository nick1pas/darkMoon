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
package com.l2jfree.gameserver.network.serverpackets;

public class RestartResponse extends L2GameServerPacket
{
	private static final String _S__RESTARTRESPONSE = "[S] 71 RestartResponse c[ds]";
	public static final RestartResponse PACKET = new RestartResponse();

	private final String _message;

	private RestartResponse()
	{
		_message = "ok merong~ khaha";
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x71);

		writeD(0x01); // 1 - OK
		writeS(_message);
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__RESTARTRESPONSE;
	}
}
