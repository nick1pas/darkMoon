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

import java.util.Arrays;

import com.l2jfree.lang.L2System;

/**
/*
 * 
 * Special thanks to nuocnam
 * Author: LittleVexy
 * 
 * @version $Revision: 1.1.4.4 $ $Date: 2005/03/29 23:15:15 $
 */
public class L2DropData
{
	public static final int MAX_CHANCE = 1000000;

	private int _itemId;
	private int _minDrop;
	private int _maxDrop;
	private int _chance;
	private String _questID = null;
	private String[] _stateID = null;
	private int _category;

	/**
	 * Returns the ID of the item dropped
	 * @return int
	 */
	public int getItemId()
	{
		return _itemId;
	}

	/**
	 * Sets the ID of the item dropped
	 * @param itemId : int designating the ID of the item
	 */
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	/**
	 * Returns the minimum quantity of items dropped
	 * @return int
	 */
	public int getMinDrop()
	{
		return _minDrop;
	}

	/**
	 * Returns the maximum quantity of items dropped
	 * @return int
	 */
	public int getMaxDrop()
	{
		return _maxDrop;
	}

	/**
	 * Returns the chance of having a drop
	 * @return int
	 */
	public int getChance()
	{
		return _chance;
	}

	/**
	 * Sets the value for minimal quantity of dropped items
	 * @param mindrop : int designating the quantity
	 */
	public void setMinDrop(int minDrop)
	{
		_minDrop = minDrop;
	}

	/**
	 * Sets the value for maximal quantity of dopped items
	 * @param maxdrop : int designating the quantity of dropped items
	 */
	public void setMaxDrop(int maxDrop)
	{
		_maxDrop = maxDrop;
	}

	/**
	 * Sets the chance of having the item for a drop
	 * @param chance : int designating the chance
	 */
	public void setChance(int chance)
	{
		_chance = chance;
	}
	/**
	 * Returns the stateID.
	 * @return String[]
	 */
	public String[] getStateIDs()
	{
		return _stateID;
	}

	/**
	 * Adds states of the dropped item
	 * @param list : String[]
	 */
	public void addStates(String[] list)
	{
		_stateID = list;
	}

	/**
	 * Returns the questID.
	 * @return String designating the ID of the quest
	 */
	public String getQuestID()
	{
		return _questID;
	}

	/**
	 * Sets the questID
	 * @param String designating the questID to set.
	 */
	public void setQuestID(String questID)
	{
		_questID = questID;
	}

	/**
	 * Returns if the dropped item is requested for a quest
	 * @return boolean
	 */
	public boolean isQuestDrop()
	{
		return _questID != null && _stateID != null;
	}

	/**
	 * Returns a report of the object
	 * @return String
	 */
	@Override
	public String toString()
	{
		String out = "ItemID: " + getItemId() + " Min: " + getMinDrop() +
			" Max: " + getMaxDrop() + " Chance: " + (getChance() / 10000.0) + "%";
		if (isQuestDrop())
		{
			out += " QuestID: " + getQuestID() + " StateID's: " + Arrays.toString(getStateIDs());
		}
		
		return out;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return L2System.hash(_itemId);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof L2DropData))
			return false;
		
		final L2DropData other = (L2DropData)obj;
		
		if (_itemId != other._itemId)
			return false;
		
		return true;
	}

	public void setCategory(int category)
	{
		_category = category;
	}

	public int getCategory()
	{
		return _category;
	}
}
