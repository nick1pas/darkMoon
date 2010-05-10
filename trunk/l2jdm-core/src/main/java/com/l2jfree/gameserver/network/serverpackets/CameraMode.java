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

public class CameraMode extends L2GameServerPacket
{
	private static final String _S__CAMERAMODE = "[S] F7 CameraMode c[d]";
	public static final CameraMode FIRST_PERSON = new CameraMode(0x01);
	public static final CameraMode THIRD_PERSON = new CameraMode(0x00);

	private final int _mode;

	private CameraMode(int mode)
	{
		_mode = mode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf7);
		writeD(_mode);
	}
	
	@Override
	public String getType()
	{
		return _S__CAMERAMODE;
	}
}
