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
package com.l2jfree.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2TradeList;

/**
 *  This class manages buylists from database
 * 
 * @version $Revision: 1.5.4.13 $ $Date: 2005/04/06 16:13:38 $
 */
public class TradeListTable
{
	private final static Log				_log	= LogFactory.getLog(TradeListTable.class);

	private int								_nextListId;
	private final FastMap<Integer, L2TradeList>	_lists = new FastMap<Integer, L2TradeList>();

	/** Task launching the function for restore count of Item (Clan Hall) */
	public class RestoreCount implements Runnable
	{
		private final int	timer;

		public RestoreCount(int time)
		{
			timer = time;
		}

		public void run()
		{
			restoreCount(timer);
			dataTimerSave(timer);
			ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(timer), (long) timer * 60 * 60 * 1000);
		}
	}

	public static TradeListTable getInstance()
	{
		return SingletonHolder._instance;
	}

	private TradeListTable()
	{
		_lists.clear();
		load();
	}

	private void load(boolean custom)
	{
		Connection con = null;
		/*
		 * Initialize Shop buylist
		 */
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement1 = con.prepareStatement("SELECT * FROM " + (custom ? "custom_merchant_shopids" : "merchant_shopids"));
			ResultSet rset1 = statement1.executeQuery();
			while (rset1.next())
			{
				PreparedStatement statement = con.prepareStatement("SELECT * FROM "
						+ (custom ? "custom_merchant_buylists" : "merchant_buylists") + " WHERE shop_id=? ORDER BY "
						+ L2DatabaseFactory.getInstance().safetyString("order") + " ASC");
				statement.setInt(1, rset1.getInt("shop_id"));
				ResultSet rset = statement.executeQuery();

				L2TradeList buylist = new L2TradeList(rset1.getInt("shop_id"));

				buylist.setNpcId(rset1.getString("npc_id"));
				buylist.setCustom(custom);
				int _itemId = 0;
				int _itemCount = 0;
				int _price = 0;

				if (!buylist.isGm() && NpcTable.getInstance().getTemplate(rset1.getInt("npc_id")) == null)
					_log.warn("TradeListTable: Merchant id " + rset1.getString("npc_id") + " with" + (custom ? " custom " : " ") + "buylist "
							+ buylist.getListId() + " not exist.");

				try
				{
					while (rset.next())
					{
						_itemId = rset.getInt("item_id");
						_price = rset.getInt("price");
						int count = rset.getInt("count");
						int currentCount = rset.getInt("currentCount");
						int restoreTime = rset.getInt("time");

						L2ItemInstance buyItem = ItemTable.getInstance().createDummyItem(_itemId);
						if (buyItem == null)
							continue;
						_itemCount++;
						if (count > -1)
							buyItem.setCountDecrease(true);
						if (_price <= -1)
							_price = ItemTable.getInstance().getTemplate(_itemId).getReferencePrice();

						buyItem.setPriceToSell(_price);
						buyItem.setRestoreTime(restoreTime);
						buyItem.setInitCount(count);
						if (currentCount > -1)
							buyItem.setCount(currentCount);
						else
							buyItem.setCount(count);

						buylist.addItem(buyItem);
						if (!buylist.isGm() && buyItem.getReferencePrice() > _price && _price != -1)
							_log.warn("TradeListTable: Reference price of item " + _itemId + " in" + (custom ? " custom " : " ") + "buylist "
									+ buylist.getListId() + " higher then sell price.");
					}
				}
				catch (Exception e)
				{
					_log.warn("TradeListTable: Problem with" + (custom ? " custom " : " ") + "buylist " + buylist.getListId() + " item " + _itemId + ".");
				}

				if (_itemCount > 0)
				{
					_lists.put(buylist.getListId(), buylist);
					_nextListId = Math.max(_nextListId, buylist.getListId() + 1);
				}
				else
					_log.warn("TradeListTable: Empty " + (custom ? "custom " : "") + " buylist " + buylist.getListId() + ".");

				rset.close();
				statement.close();
			}
			rset1.close();
			statement1.close();

			_log.info("TradeListTable: Loaded " + _lists.size() + (custom ? " custom " : " ") + "buylists.");
			/*
			 *  Restore Task for reinitialize count of buy item
			 */
			try
			{
				int time = 0;
				long savetimer = 0;
				long currentMillis = System.currentTimeMillis();
				PreparedStatement statement2 = con.prepareStatement("SELECT DISTINCT time, savetimer FROM "
						+ (custom ? "merchant_buylists" : "merchant_buylists") + " WHERE time <> 0 ORDER BY time");
				ResultSet rset2 = statement2.executeQuery();
				while (rset2.next())
				{
					time = rset2.getInt("time");
					savetimer = rset2.getLong("savetimer");
					if (savetimer - currentMillis > 0)
						ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(time), savetimer - System.currentTimeMillis());
					else
						ThreadPoolManager.getInstance().scheduleGeneral(new RestoreCount(time), 0);
				}
				rset2.close();
				statement2.close();
			}
			catch (Exception e)
			{
				_log.warn("TradeListTable:" + (custom ? " custom " : " ") + "could not restore Timer for Item count.", e);
			}
		}
		catch (Exception e)
		{
			// problem with initializing buylists, go to next one
			_log.warn("TradeListTable:" + (custom ? " custom " : " ") + "buylists could not be initialized.", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void load()
	{
		load(false); // not custom
		load(true); //custom
	}

	public void reloadAll()
	{
		_lists.clear();

		load();
	}

	public L2TradeList getBuyList(int listId)
	{
		if (_lists.containsKey(listId))
			return _lists.get(listId);
		return null;
	}

	public FastList<L2TradeList> getBuyListByNpcId(int npcId)
	{
		FastList<L2TradeList> lists = new FastList<L2TradeList>();

		for (L2TradeList list : _lists.values())
		{
			if (list.isGm())
				continue;
			if (npcId == list.getNpcId())
				lists.add(list);
		}

		return lists;
	}

	protected void restoreCount(int time)
	{
		if (_lists == null)
			return;
		for (L2TradeList list : _lists.values())
		{
			list.restoreCount(time);
		}
	}

	protected void dataTimerSave(int time)
	{
		long timerSave = System.currentTimeMillis() + (long) time * 60 * 60 * 1000;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("UPDATE merchant_buylists SET savetimer =? WHERE time =?");
			statement.setLong(1, timerSave);
			statement.setInt(2, time);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.fatal("TradeController: Could not update Timer save in Buylist");
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public void dataCountStore()
	{
		Connection con = null;
		PreparedStatement statement;

		int listId;
		if (_lists == null)
			return;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);

			for (L2TradeList list : _lists.values())
			{
				if (list == null)
					continue;
				listId = list.getListId();

				for (L2ItemInstance Item : list.getItems())
				{
					if (Item.getCount() < Item.getInitCount()) //needed?
					{
						statement = con.prepareStatement("UPDATE merchant_buylists SET currentCount=? WHERE item_id=? AND shop_id=?");
						statement.setLong(1, Item.getCount());
						statement.setInt(2, Item.getItemId());
						statement.setInt(3, listId);
						statement.executeUpdate();
						statement.close();
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.fatal("TradeController: Could not store Count Item");
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final TradeListTable _instance = new TradeListTable();
	}
}