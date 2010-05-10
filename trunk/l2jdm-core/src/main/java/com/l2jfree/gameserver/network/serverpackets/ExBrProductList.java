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

public class ExBrProductList extends L2GameServerPacket
{
	private static final String _S__EXBRPRODUCTLIST = "[S] FE:A7 ExBR_ProductList ch[d(unk)]";
	public static final ExBrProductList EMPTY = new ExBrProductList();

	/** Unknown packet structure when list isn't empty! */
	private ExBrProductList()
	{
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0xa7);

		writeD(0x00); // list size
		// ???
	}

	@Override
	public String getType()
	{
		return _S__EXBRPRODUCTLIST;
	}
}
