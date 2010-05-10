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
package com.l2jfree.gameserver.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.handler.itemhandlers.BeastSoulShot;
import com.l2jfree.gameserver.handler.itemhandlers.BeastSpice;
import com.l2jfree.gameserver.handler.itemhandlers.BeastSpiritShot;
import com.l2jfree.gameserver.handler.itemhandlers.BlessedSpiritShot;
import com.l2jfree.gameserver.handler.itemhandlers.Book;
import com.l2jfree.gameserver.handler.itemhandlers.ColorName;
import com.l2jfree.gameserver.handler.itemhandlers.DoorKey;
import com.l2jfree.gameserver.handler.itemhandlers.Elixir;
import com.l2jfree.gameserver.handler.itemhandlers.EnchantAttribute;
import com.l2jfree.gameserver.handler.itemhandlers.EnchantScrolls;
import com.l2jfree.gameserver.handler.itemhandlers.ExtractableItems;
import com.l2jfree.gameserver.handler.itemhandlers.FishShots;
import com.l2jfree.gameserver.handler.itemhandlers.Harvester;
import com.l2jfree.gameserver.handler.itemhandlers.ItemSkills;
import com.l2jfree.gameserver.handler.itemhandlers.Maps;
import com.l2jfree.gameserver.handler.itemhandlers.MercTicket;
import com.l2jfree.gameserver.handler.itemhandlers.PetFood;
import com.l2jfree.gameserver.handler.itemhandlers.Potions;
import com.l2jfree.gameserver.handler.itemhandlers.Recipes;
import com.l2jfree.gameserver.handler.itemhandlers.RollingDice;
import com.l2jfree.gameserver.handler.itemhandlers.ScrollOfResurrection;
import com.l2jfree.gameserver.handler.itemhandlers.Seed;
import com.l2jfree.gameserver.handler.itemhandlers.SevenSignsRecord;
import com.l2jfree.gameserver.handler.itemhandlers.SoulCrystals;
import com.l2jfree.gameserver.handler.itemhandlers.SoulShots;
import com.l2jfree.gameserver.handler.itemhandlers.SpecialXMas;
import com.l2jfree.gameserver.handler.itemhandlers.SpiritShot;
import com.l2jfree.gameserver.handler.itemhandlers.SummonItems;
import com.l2jfree.gameserver.handler.itemhandlers.TeleportBookmark;
import com.l2jfree.gameserver.handler.itemhandlers.WrappedPack;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.restriction.global.GlobalRestrictions;
import com.l2jfree.gameserver.templates.item.L2EtcItem;
import com.l2jfree.gameserver.templates.item.L2Item;
import com.l2jfree.util.HandlerRegistry;
import com.l2jfree.util.NumberHandlerRegistry;

public final class ItemHandler
{
	private static final Log _log = LogFactory.getLog(ItemHandler.class);
	
	private static final class SingletonHolder
	{
		private static final ItemHandler INSTANCE = new ItemHandler();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private final NumberHandlerRegistry<IItemHandler> _byItemId = new NumberHandlerRegistry<IItemHandler>() {
		@Override
		protected String getName()
		{
			return "ItemHandlerByItemId";
		}
	};
	private final HandlerRegistry<String, IItemHandler> _byHandlerName = new HandlerRegistry<String, IItemHandler>() {
		@Override
		protected String getName()
		{
			return "ItemHandlerByHandlerName";
		}
	};
	
	private ItemHandler()
	{
		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new Book());
		registerItemHandler(new ColorName());
		registerItemHandler(new DoorKey());
		registerItemHandler(new Elixir());
		registerItemHandler(new EnchantAttribute());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new ExtractableItems());
		registerItemHandler(new FishShots());
		registerItemHandler(new Harvester());
		registerItemHandler(new ItemSkills());
		registerItemHandler(new Maps());
		registerItemHandler(new MercTicket());
		registerItemHandler(new PetFood());
		registerItemHandler(new Potions());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new Seed());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new SoulCrystals());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new SummonItems());
		registerItemHandler(new TeleportBookmark());
		registerItemHandler(new WrappedPack());
		
		_log.info("ItemHandler: Loaded " + _byHandlerName.size() + " handlers by handlerName.");
		
		for (L2Item item : ItemTable.getInstance().getAllTemplates())
		{
			if (!(item instanceof L2EtcItem))
				continue;
			
			final String handlerName = ((L2EtcItem)item).getHandlerName();
			
			if (handlerName.isEmpty())
				continue;
			
			final IItemHandler handlerByHandlerName = _byHandlerName.get(handlerName);
			
			if (handlerByHandlerName == null)
			{
				_log.warn("ItemHandler: Missing handler for '" + handlerName + "'!");
				continue;
			}
			
			_byItemId.register(item.getItemId(), handlerByHandlerName);
		}
		
		_log.info("ItemHandler: Loaded " + _byItemId.size() + " handlers by itemId.");
	}
	
	public void registerItemHandler(IItemHandler handler)
	{
		_byHandlerName.register(handler.getClass().getSimpleName().intern(), handler);
	}
	
	public boolean hasItemHandler(int itemId, L2ItemInstance item)
	{
		return get(itemId, item) != null;
	}
	
	public boolean useItem(int itemId, L2Playable playable, L2ItemInstance item)
	{
		return useItem(itemId, playable, item, true);
	}
	
	public boolean useItem(int itemId, L2Playable playable, L2ItemInstance item, boolean warn)
	{
		final IItemHandler handler = get(itemId, item);
		
		if (handler == null)
		{
			if (warn)
				_log.warn("No item handler registered for item ID " + itemId + ".");
			return false;
		}
		
		if (!GlobalRestrictions.canUseItemHandler(handler.getClass(), itemId, playable, item))
			return true;
		
		handler.useItem(playable, item);
		return true;
	}
	
	private IItemHandler get(int itemId, L2ItemInstance item)
	{
		return _byItemId.get(itemId);
	}
}
