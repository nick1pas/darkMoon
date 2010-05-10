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

import java.io.File;
import java.util.Scanner;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.items.model.L2ExtractableItem;
import com.l2jfree.gameserver.items.model.L2ExtractableProductItem;

/**
 * @author FBIagent
 */
public class ExtractableItemsData
{
	private final static Log					_log		= LogFactory.getLog(ExtractableItemsData.class);

	//          Map<itemid, L2ExtractableItem>
	private final FastMap<Integer, L2ExtractableItem>	_items  = new FastMap<Integer, L2ExtractableItem>();

	public static ExtractableItemsData getInstance()
	{
		return SingletonHolder._instance;
	}

	private ExtractableItemsData()
	{
		_items.clear();

		Scanner s;

		try
		{
			s = new Scanner(new File(Config.DATAPACK_ROOT, "data/extractable_items.csv"));
		}
		catch (Exception e)
		{
			_log.warn("Extractable items data: Can not find '" + Config.DATAPACK_ROOT + "data/extractable_items.csv'");
			return;
		}

		int lineCount = 0;

		while (s.hasNextLine())
		{
			lineCount++;

			String line = s.nextLine().trim();

			if (line.startsWith("#"))
				continue;
			else if (line.isEmpty())
				continue;

			String[] lineSplit = line.split(";");
			boolean ok = true;
			int itemID = 0;

			try
			{
				itemID = Integer.parseInt(lineSplit[0]);
			}
			catch (Exception e)
			{
				_log.warn("Extractable items data: Error in line " + lineCount + " -> invalid item id or wrong seperator after item id!");
				_log.warn("		" + line);
				ok = false;
			}

			if (!ok)
				continue;

			FastList<L2ExtractableProductItem> product_temp = new FastList<L2ExtractableProductItem>();

			for (int i = 0; i < lineSplit.length -1 ; i++)
			{
				ok = true;

				String[] lineSplit2 = lineSplit[i + 1].split(",");

				if (lineSplit2.length < 3)
				{
					_log.warn("Extractable items data: Error in line " + lineCount + " -> wrong seperator!");
					_log.warn("		" + line);
					ok = false;
				}

				if (!ok)
					continue;

				int[] production = null;
				int[] amount = null;
				int chance = 0;

				try
				{
					int k =0;
					production = new int[(lineSplit2.length - 1) / 2];
					amount = new int[(lineSplit2.length - 1) / 2];
					for (int j = 0; j < lineSplit2.length-1 ;j++)
					{
						production[k] = Integer.parseInt(lineSplit2[j]);
						amount[k] = Integer.parseInt(lineSplit2[j+=1]);
						k++;
					}

					chance = Integer.parseInt(lineSplit2[lineSplit2.length-1]);
				}
				catch (Exception e)
				{
					_log.warn("Extractable items data: Error in line " + lineCount + " -> incomplete/invalid production data or wrong seperator!");
					_log.warn("      " + line);
					ok = false;
				}

				if (!ok)
					continue;

				L2ExtractableProductItem product = new L2ExtractableProductItem(production, amount, chance);
				product_temp.add(product);
			}

			int fullChances = 0;

			for (L2ExtractableProductItem Pi : product_temp)
				fullChances += Pi.getChance();

			if (fullChances > 100)
			{
				_log.warn("Extractable items data: Error in line " + lineCount + " -> all chances together are more then 100!");
				_log.warn("		" + line);
				continue;
			}
			L2ExtractableItem product = new L2ExtractableItem(itemID, product_temp);
			_items.put(itemID, product);
		}

		s.close();
		_log.info("Extractable items data: Loaded " + _items.size() + " extractable items!");
	}

	public L2ExtractableItem getExtractableItem(int itemID)
	{
		return _items.get(itemID);
	}

	public int[] itemIDs()
	{
		int size = _items.size();
		int[] result = new int[size];
		int i = 0;
		for (L2ExtractableItem ei : _items.values())
		{
			result[i++] = ei.getItemId();
		}
		return result;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ExtractableItemsData _instance = new ExtractableItemsData();
	}
}
