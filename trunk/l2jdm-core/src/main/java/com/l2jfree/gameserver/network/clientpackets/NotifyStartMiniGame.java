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

/**
 * Sent by the client once player starts the
 * Bejeweled-alike mini game (/minigame).
 */
public final class NotifyStartMiniGame extends L2GameClientPacket
{
	private static final String _C__NOTIFYSTARTMINIGAME = "[C] D0:56 NotifyStartMiniGame ch";

	@Override
	protected void readImpl()
	{
		// trigger packet
	}

	@Override
	protected void runImpl()
	{
		// No idea what should the server do about that
	}

	@Override
	public String getType()
	{
		return _C__NOTIFYSTARTMINIGAME;
	}
}
