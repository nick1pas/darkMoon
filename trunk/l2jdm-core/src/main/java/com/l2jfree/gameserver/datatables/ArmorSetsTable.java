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

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.model.L2ArmorSet;

/**
 * @author Luno & Psychokiller1888
 */
public final class ArmorSetsTable
{
	private static final Log _log = LogFactory.getLog(ArmorSetsTable.class);
	
	private final FastMap<Integer, L2ArmorSet> _armorSets = new FastMap<Integer, L2ArmorSet>();
	
	public static ArmorSetsTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private ArmorSetsTable()
	{
		loadData();
	}
	
	private void loadData()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con
					.prepareStatement("SELECT chest, legs, head, gloves, feet, skill, shield, shield_skill_id, enchant6skill, mw_legs, mw_head, mw_gloves, mw_feet, mw_shield FROM armorsets");
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				int chest = rset.getInt("chest");
				int legs = rset.getInt("legs");
				int head = rset.getInt("head");
				int gloves = rset.getInt("gloves");
				int feet = rset.getInt("feet");
				String[] skills = rset.getString("skill").split(";");
				int shield = rset.getInt("shield");
				int shield_skill_id = rset.getInt("shield_skill_id");
				int enchant6skill = rset.getInt("enchant6skill");
				int mwork_legs = rset.getInt("mw_legs");
				int mwork_head = rset.getInt("mw_head");
				int mwork_gloves = rset.getInt("mw_gloves");
				int mwork_feet = rset.getInt("mw_feet");
				int mwork_shield = rset.getInt("mw_shield");
				_armorSets.put(chest, new L2ArmorSet(chest, legs, head, gloves, feet, skills, shield, shield_skill_id,
						enchant6skill, mwork_legs, mwork_head, mwork_gloves, mwork_feet, mwork_shield));
			}
			
			_log.info("ArmorSetsTable: Loaded " + _armorSets.size() + " armor sets.");
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warn("Error while loading armor sets ", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public boolean setExists(int chestId)
	{
		return _armorSets.containsKey(chestId);
	}
	
	public L2ArmorSet getSet(int chestId)
	{
		return _armorSets.get(chestId);
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final ArmorSetsTable _instance = new ArmorSetsTable();
	}
}
