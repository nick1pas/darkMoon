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
import com.l2jfree.gameserver.items.model.L2ExtractableProductItem;
import com.l2jfree.gameserver.items.model.L2ExtractableSkill;
import com.l2jfree.gameserver.model.L2Skill;

/**
 * @author FBIagent
 */
public class ExtractableSkillsData
{
	private final static Log					_log		= LogFactory.getLog(ExtractableSkillsData.class);

	//          Map<itemid, L2ExtractableSkill>
	private final FastMap<Integer, L2ExtractableSkill>	_items  = new FastMap<Integer, L2ExtractableSkill>();

	public static ExtractableSkillsData getInstance()
	{
		return SingletonHolder._instance;
	}

	private ExtractableSkillsData()
	{
		_items.clear();

		Scanner s;

		try
		{
			s = new Scanner(new File(Config.DATAPACK_ROOT, "data/extractable_skills.csv"));
		}
		catch (Exception e)
		{
			_log.warn("Extractable items data: Can not find '" + Config.DATAPACK_ROOT + "data/extractable_skills.csv'");
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
			int skillID = 0;
			int skillLevel = 0;

			try
			{
				skillID = Integer.parseInt(lineSplit[0]);
				skillLevel = Integer.parseInt(lineSplit[1]);
			}
			catch (Exception e)
			{
				_log.warn("Extractable skills data: Error in line " + lineCount + " -> invalid item id or wrong seperator after skill id!");
				_log.warn("		" + line);
				ok = false;
			}

			L2Skill skill = SkillTable.getInstance().getInfo(skillID, skillLevel);
			if (skill == null)
			{
				_log.warn("Extractable skills data: Error in line " + lineCount + " -> skill is null!");
				_log.warn("		" + line);
				ok = false;
			}

			if (!ok)
				continue;

			FastList<L2ExtractableProductItem> product_temp = new FastList<L2ExtractableProductItem>();

			for (int i = 1; i < lineSplit.length -1 ; i++)
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
					_log.warn("Extractable skills data: Error in line " + lineCount + " -> incomplete/invalid production data or wrong seperator!");
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
				_log.warn("Extractable skills data: Error in line " + lineCount + " -> all chances together are more then 100!");
				_log.warn("		" + line);
				continue;
			}
			int hash = SkillTable.getSkillUID(skill);
			L2ExtractableSkill product = new L2ExtractableSkill(hash, product_temp);
			_items.put(hash, product);
		}

		s.close();
		_log.info("Extractable items data: Loaded " + _items.size() + " extractable skills!");
	}

	public L2ExtractableSkill getExtractableItem(L2Skill skill)
	{
		return _items.get(SkillTable.getSkillUID(skill));
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ExtractableSkillsData _instance = new ExtractableSkillsData();
	}
}
