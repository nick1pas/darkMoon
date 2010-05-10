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

public class CharacterCreateFail extends L2GameServerPacket
{
	private static final String	_S__CHARACTERCREATEFAIL	= "[S] 10 CharacterCreateFail c[d]";

	/** Your character creation has failed. */
	public static final int		REASON_CREATION_FAILED = 0x00;
	/**
	 * You cannot create another character. Please delete the existing character and try again.
	 * <BR><I>Removes all settings that were selected (race, class, etc).</I>
	 */
	public static final int		REASON_TOO_MANY_CHARACTERS = 0x01;
	/** This name already exists. */
	public static final int		REASON_NAME_ALREADY_EXISTS = 0x02;
	/** Your title cannot exceed 16 characters in length. Please try again. */
	public static final int		REASON_16_ENG_CHARS = 0x03;
	/** Incorrect name. Please try again. */
	public static final int		REASON_INCORRECT_NAME = 0x04;
	/** Characters cannot be created from this server. */
	public static final int		REASON_CREATE_NOT_ALLOWED = 0x05;
	/**
	 * Unable to create character. You are unable to create a new character on the selected server.
	 * A restriction is in place which restricts users from creating characters on different servers
	 * where no previous character exists. Please choose another server.
	 */
	public static final int		REASON_CHOOSE_ANOTHER_SERVER = 0x06;

	private final int			_error;

	public CharacterCreateFail(int error)
	{
		_error = error;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x10);
		writeD(_error);
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__CHARACTERCREATEFAIL;
	}
}
