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
package com.l2jfree.loginserver.loginserverpackets;

import com.l2jfree.L2Config;

public final class InitLS extends LoginToGamePacket
{
	public InitLS(byte[] publickey)
	{
		super(0x00);
		writeD(L2Config.LOGIN_PROTOCOL_L2J);
		writeD(publickey.length);
		writeB(publickey);
		// let the l2jfree game servers know we support enhanced protocol
		writeD(L2Config.LOGIN_PROTOCOL_CURRENT);
	}
}
