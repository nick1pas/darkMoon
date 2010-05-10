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
package com.l2jfree.gameserver.model;

import java.util.List;

import javolution.util.FastList;

import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.itemcontainer.Inventory;
import com.l2jfree.gameserver.model.itemcontainer.PcInventory;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 * 
 * @version $Revision: 1.4.2.1.2.5 $ $Date: 2005/03/27 15:29:33 $
 */
public class L2TradeList
{
	private final FastList<L2ItemInstance> _items;
	private final int _listId;
	private boolean _confirmed;
	private boolean _gm;
	private String _buystorename,_sellstorename;
	private boolean _custom;

	private int _npcId;

	public L2TradeList(int listId)
	{
		_items = new FastList<L2ItemInstance>();
		_listId = listId;
		_confirmed = false;
	}

	public void setNpcId(String id)
	{
		try
		{
			_gm = false;
			_npcId = Integer.parseInt(id);
		}
		catch (Exception e)
		{
			if (id.equalsIgnoreCase("gm"))
				_gm = true;
		}
	}

	public void setCustom(boolean custom)
	{
	_custom = custom;
	}

	public boolean isCustom()
	{
	return _custom;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public boolean isGm()
	{
		return _gm;
	}

	public void addItem(L2ItemInstance item)
	{
		_items.add(item);
	}

	public void replaceItem(int itemID, long price)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getItemId() == itemID)
			{
				item.setPriceToSell(price);
			}
		}
	}

	public boolean decreaseCount(int itemID, long count)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getItemId() == itemID)
			{
				long newCount = item.getCount() - count;
				if (newCount < 0)
					continue;
				
				item.setCount(newCount);
				return true;
			}
		}
		
		return false;
	}

	public void restoreCount(int time)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getCountDecrease() && item.getRestoreTime() == time)
			{
				item.restoreInitCount();
			}
		}
	}

	public void removeItem(int itemID)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getItemId() == itemID)
			{
				_items.remove(i);
			}
		}
	}

	/**
	 * @return Returns the listId.
	 */
	public int getListId()
	{
		return _listId;
	}
	public void setSellStoreName(String name)
	{
		_sellstorename = name;
	}
	public String getSellStoreName()
	{
		return _sellstorename;
	}
	public void setBuyStoreName(String name)
	{
		_buystorename = name;
	}
	public String getBuyStoreName()
	{
		return _buystorename;
	}

	/**
	 * @return Returns the items.
	 */
	public FastList<L2ItemInstance> getItems()
	{
		return _items;
	}

	public List<L2ItemInstance> getItems(int start, int end)
	{
		return _items.subList(start, end);
	}

	public long getPriceForItemId(int itemId)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getItemId() == itemId)
			{
				return item.getPriceToSell();
			}
		}
		return -1;
	}

	public boolean countDecrease(int itemId)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getItemId() == itemId)
			{
				return item.getCountDecrease();
			}
		}
		return false;
	}

	public boolean containsItemId(int itemId)
	{
		for (L2ItemInstance item : _items)
		{
			if (item.getItemId() == itemId)
				return true;
		}
		
		return false;
	}
	public L2ItemInstance getItem(int ObjectId)
	{
		for (int i = 0; i < _items.size(); i++)
		{
			L2ItemInstance item = _items.get(i);
			if (item.getObjectId() == ObjectId)
			{
				return item;
			}
		}
		return null;
	}
	
	public synchronized void setConfirmedTrade(boolean x)
	{
		_confirmed = x;
	}
	
	public synchronized boolean hasConfirmed()
	{
		return _confirmed;
	}
	
	public void removeItem(int objId, long count)
	{
		L2ItemInstance temp;
		for (int y = 0; y < _items.size(); y++)
		{
			temp = _items.get(y);
			if (temp.getObjectId() == objId)
			{
				if (count == temp.getCount())
				{
					_items.remove(temp);
				}
				break;
			}
		}
	}

	public boolean contains(int objId)
	{
		boolean bool = false;
		L2ItemInstance temp;
		for (int y = 0; y < _items.size(); y++)
		{
			temp = _items.get(y);
			if (temp.getObjectId()  == objId)
			{
				bool = true;
				break;
			}
		}
		
		return bool;
	}

	public boolean validateTrade(L2PcInstance player)
	{
		Inventory playersInv = player.getInventory();
		L2ItemInstance playerItem,temp;
		
		for (int y = 0; y < _items.size(); y++)
		{
			temp = _items.get(y);
			playerItem = playersInv.getItemByObjectId(temp.getObjectId());
			if (playerItem == null || playerItem.getCount() < temp.getCount())
				return false;
		}
		return true;
	}

	public void updateBuyList(L2PcInstance player, FastList<TradeItem> list)
	{
		TradeItem temp;
		int count = 0;
		Inventory playersInv = player.getInventory();
		L2ItemInstance temp2;
		
		while (count!= list.size())
		{
			temp = list.get(count);
			temp2 = playersInv.getItemByItemId(temp.getItemId());
			if (temp2 == null)
			{
				list.remove(count);
				count--;
			}
			else
			{
				if (temp.getCount() == 0)
				{
					list.remove(count);
					count--;
				}
			}
			count++;
		}
	}
	
	public void updateSellList(L2PcInstance player, FastList<TradeItem> list)
	{
		Inventory playersInv = player.getInventory();
		TradeItem temp;
		L2ItemInstance temp2;
		int count = 0;
		while (count != list.size())
		{
			temp = list.get(count);
			temp2 = playersInv.getItemByObjectId(temp.getObjectId());
			if (temp2 == null)
			{
				list.remove(count);
				count = count-1;
			}
			else
			{
				if (temp2.getCount() < temp.getCount())
				{
					temp.setCount(temp2.getCount());
				}
				
			}
			count++;
		}
	}

	public synchronized void buySellItems(L2PcInstance buyer, FastList<TradeItem> buyerslist, L2PcInstance seller, FastList<TradeItem> sellerslist)
	{
		Inventory sellerInv         = seller.getInventory();
		Inventory buyerInv          = buyer.getInventory();
		
		//TradeItem buyerItem         = null;
		TradeItem temp2             = null;
		
		L2ItemInstance sellerItem   = null;
		L2ItemInstance temp         = null;
		L2ItemInstance newitem      = null;
		L2ItemInstance adena        = null;
		int enchantLevel            = 0;
		
		InventoryUpdate buyerupdate     = new InventoryUpdate();
		InventoryUpdate sellerupdate    = new InventoryUpdate();
		
		ItemTable itemTable = ItemTable.getInstance();
		
		long amount = 0;
		int x = 0;
		int y = 0;
		
		List<SystemMessage> sysmsgs = new FastList<SystemMessage>();
		SystemMessage msg = null;
		
		for (TradeItem buyerItem : buyerslist)
		{
			for (x = 0; x < sellerslist.size(); x++)//find in sellerslist
			{
				temp2 = sellerslist.get(x);
				if (temp2.getItemId() == buyerItem.getItemId())
				{
					sellerItem = sellerInv.getItemByItemId(buyerItem.getItemId());
					break;
				}
			}
			
			if (sellerItem !=null && temp2 != null)
			{
				if (buyerItem.getCount()> temp2.getCount())
				{
					amount = temp2.getCount();
				}
				if (buyerItem.getCount()> sellerItem.getCount())
				{
					amount = sellerItem.getCount();
				}
				else
				{
					amount = buyerItem.getCount();
				}
				if (buyerItem.getCount() > PcInventory.MAX_ADENA / buyerItem.getOwnersPrice())
				{
					//_log.warn("Integer Overflow on Cost. Possible Exploit attempt between "+buyer.getName()+" and "+seller.getName()+".");
					return;
				}
				//int cost = amount * buyerItem.getOwnersPrice();
				enchantLevel = sellerItem.getEnchantLevel();
				sellerItem = sellerInv.destroyItem("", sellerItem.getObjectId(),amount, null, null);
	//		        buyer.reduceAdena(cost);
	//		        seller.addAdena(cost);
				newitem = itemTable.createItem("L2TradeList", sellerItem.getItemId(), amount, buyer, seller);
				newitem.setEnchantLevel(enchantLevel);
				temp = buyerInv.addItem("", newitem, null, null);
				if (amount == 1)//system msg stuff
				{
					msg = new SystemMessage(SystemMessageId.C1_PURCHASED_S2);
					msg.addString(buyer.getName());
					msg.addItemName(sellerItem);
					sysmsgs.add(msg);
					msg = new SystemMessage(SystemMessageId.C1_PURCHASED_S2);
					msg.addString("You");
					msg.addItemName(sellerItem);
					sysmsgs.add(msg);
				}
				else
				{
					msg = new SystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
					msg.addString(buyer.getName());
					msg.addItemName(sellerItem);
					msg.addItemNumber(amount);
					sysmsgs.add(msg);
					msg = new SystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
					msg.addString("You");
					msg.addItemName(sellerItem);
					msg.addItemNumber(amount);
					sysmsgs.add(msg);
				}
				if (temp2.getCount() == buyerItem.getCount())
				{
					sellerslist.remove(temp2);
					buyerItem.setCount(0);
				}
				else
				{
					if (buyerItem.getCount()< temp2.getCount())
					{
						temp2.setCount(temp2.getCount() - buyerItem.getCount());
					}
					else
					{
						buyerItem.setCount(buyerItem.getCount() - temp2.getCount());
					}
				}
				
				if (sellerItem .getLastChange() == L2ItemInstance.MODIFIED)
				{
					sellerupdate.addModifiedItem(sellerItem);
				}
				else
				{
					L2World world = L2World.getInstance();
					world.removeObject(sellerItem );
					sellerupdate.addRemovedItem(sellerItem );
				}
				
				if (temp.getLastChange() == L2ItemInstance.MODIFIED)
				{
					buyerupdate.addModifiedItem(temp);
				}
				else
				{
					buyerupdate.addNewItem(temp);
				}
				
				
				//}
				
				sellerItem =  null;
			}
		}
		if (newitem != null)
		{
			//updateSellList(seller,sellerslist);
			adena = seller.getInventory().getAdenaInstance();
			adena.setLastChange(L2ItemInstance.MODIFIED);
			sellerupdate.addModifiedItem(adena);
			adena = buyer.getInventory().getAdenaInstance();
			adena.setLastChange(L2ItemInstance.MODIFIED);
			buyerupdate.addModifiedItem(adena);
			
			seller.sendPacket(sellerupdate);
			buyer.sendPacket(buyerupdate );
			y = 0;
			
			for (x = 0; x < sysmsgs.size(); x++)
			{
				
				if (y == 0)
				{
					seller.sendPacket(sysmsgs.get(x));
					y = 1;
				}
				else
				{
					buyer.sendPacket(sysmsgs.get(x));
					y = 0;
				}
			}
		}
	}
}