/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.model.quest;

import java.util.List;

import javolution.util.FastList;
import net.sf.l2j.gameserver.model.L2DropData;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author XaKa
 *
 */
public class QuestDropInfo
{
	public class DropInfo
	{
		public int	dropItemID;
		public int	dropItemObjID;
		public int	dropItemCount;

		/**
		 * Constructor of DropInfo that initialzes class variables
		 * @param itemID : int designating the ID of the item
		 * @param itemCount : int designating the quantity of items needed for quest
		 * @param itemObjID : int designating the ID of the item in the inventory of the player
		 */
		public DropInfo(int itemID, int itemCount, int itemObjID)
		{
			dropItemID		= itemID;
			dropItemObjID	= itemObjID;
			dropItemCount	= itemCount;
		}
	}
	
	public FastList<DropInfo> dropList;
	
	/**
	 * Add informations for dropped items in the inventory of the player.
	 * @param pcInstance : L2PcInstance designating the player
	 * @param questName : String designating the name of the quest
	 */
	public QuestDropInfo(L2PcInstance pcInstance, String questName)
	{
		// Get the QuestState and the State from the name of the quest
		QuestState	questState		= pcInstance.getQuestState(questName);
		dropList = new FastList<DropInfo>();
		if (questState == null)
		    return;

		if (questState.getDrops() != null)
        {
			for(List<L2DropData> questDrop : questState.getDrops().values())
			{
				// Get drops given by the mob
             if(questDrop == null)
            	 continue;
                
            // Go through all drops of the mob 
    			for(L2DropData dropInfo : questDrop)
    			{
    				int dropID 		= dropInfo.getItemId();
    				int dropObjID	= 0;
    				int dropCount	= questState.getQuestItemsCount(dropID);
    				//If player doesn't have this quest item(doesn't kill need npc? or other) then skip it
    				if(pcInstance.getInventory().getItemByItemId(dropID) == null)
    				{
    					continue;
    				}
    				
    				dropObjID = pcInstance.getInventory().getItemByItemId(dropID).getObjectId();
    				// Add info for the dropped item in the player's inventory 
    				dropList.add(new DropInfo(dropID, dropCount, dropObjID));
    			}
            }
		}
	}
}