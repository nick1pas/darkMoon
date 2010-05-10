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

import com.l2jfree.gameserver.model.TradeList;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * 3 section to this packet 1)playerinfo which is always sent dd
 * 
 * 2)list of items which can be added to sell d(hhddddhhhd)
 * 
 * 3)list of items which have already been setup for sell in previous sell
 * private store sell manageent d(hhddddhhhdd) *
 * 
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreManageListSell extends L2GameServerPacket
{
	private static final String			_S__B3_PRIVATESELLLISTSELL	= "[S] 9a PrivateSellListSell";
	private final int					_objId;
	private final long					_playerAdena;
	private final boolean				_packageSale;
	private final TradeList.TradeItem[]	_itemList;
	private final TradeList.TradeItem[]	_sellList;

	public PrivateStoreManageListSell(L2PcInstance player, boolean isPackageSale)
	{
		_objId = player.getObjectId();
		_playerAdena = player.getAdena();
		player.getSellList().updateItems();
		_packageSale = isPackageSale;
		_itemList = player.getInventory().getAvailableItems(player.getSellList());
		_sellList = player.getSellList().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xa0);
		//section 1
		writeD(_objId);
		writeD(_packageSale ? 1 : 0); // Package sell
		writeCompQ(_playerAdena);

		//section2
		writeD(_itemList.length); //for potential sells
		for (TradeList.TradeItem item : _itemList)
		{
			writeD(item.getItem().getType2());
			writeD(item.getObjectId());
			writeD(item.getItem().getItemDisplayId());
			writeCompQ(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());//enchant lvl
			writeH(item.getCustomType2());
			writeD(item.getItem().getBodyPart());
			writeCompQ(item.getPrice()); //store price

			writeElementalInfo(item); //8x h or d
		}

		//section 3
		writeD(_sellList.length); //count for any items already added for sell
		for (TradeList.TradeItem item : _sellList)
		{
			writeD(item.getItem().getType2());
			writeD(item.getObjectId());
			writeD(item.getItem().getItemDisplayId());
			writeCompQ(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());//enchant lvl
			writeH(0x00);
			writeD(item.getItem().getBodyPart());
			writeCompQ(item.getPrice());//your price
			writeCompQ(item.getItem().getReferencePrice()); //store price

			writeElementalInfo(item); //8x h or d
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__B3_PRIVATESELLLISTSELL;
	}
}
