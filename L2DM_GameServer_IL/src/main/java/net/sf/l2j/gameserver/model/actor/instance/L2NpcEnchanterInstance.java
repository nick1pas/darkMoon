/*
 * This program is free software; you can redistribute it and/or modify
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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.serverpackets.CharInfo;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.UserInfo;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * A Custom Instance to Enchant Stuff via NPC :D <br>
 * original idea by <b>MakCuMkA</b>.
 * 
 * @author Rayan RPG for L2Emu Project
 * 
 * @since 1075
 *
 */
public class L2NpcEnchanterInstance extends L2NpcInstance
{

	/**
	 * 
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#onBypassFeedback(net.sf.l2j.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */
	public void onBypassFeedback(L2PcInstance player, String command)
	{   
		if(!Config.ALLOW_NPC_ENCHANTER)
			return;
		
		if(Config.ALLOW_ENCHANT_WEAPONS)
		{
			if(command.equalsIgnoreCase("EnchantWeaponD"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantWeaponC"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantWeaponB"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantWeaponA"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantWeaponS"))
			{

			}
		}
		if(Config.ALLOW_ENCHANT_ARMORS)
		{
			if(command.equalsIgnoreCase("EnchantArmorD"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantArmorC"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantArmorB"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantArmorA"))
			{

			}
			else if(command.equalsIgnoreCase("EnchantArmorS"))
			{

			}
			
		}
	}

    private void setEnchant(L2NpcInstance npc, L2PcInstance player, int ench, int armorType)
    {
        //targets player
    	setTarget(player);
    	
        L2Object target = npc.getTarget();
        
        //valiudates target
        if (target == null) 
        {
        	System.out.println("null target");
        	return;
        }
        
       // L2PcInstance player = null;
        if (target instanceof L2PcInstance)
        {
            player = (L2PcInstance) target;
        }

        // now we need to find the equipped weapon of the targeted character...
        int curEnchant = 0; // display purposes only
        L2ItemInstance itemInstance = null;

        // only attempt to enchant if there is a weapon equipped
        L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
        if (parmorInstance != null && parmorInstance.getEquipSlot() == armorType)
        {
            itemInstance = parmorInstance;
        } else 
        {
            // for bows and double handed weapons
            parmorInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
            if (parmorInstance != null && parmorInstance.getEquipSlot() == Inventory.PAPERDOLL_LRHAND)
                itemInstance = parmorInstance;
        }
        
        if (itemInstance != null)
        {
            curEnchant = itemInstance.getEnchantLevel();
            
            // set enchant value
            player.getInventory().unEquipItemInSlotAndRecord(armorType);
            itemInstance.setEnchantLevel(ench);
            player.getInventory().equipItemAndRecord(itemInstance);

            // send packets
            InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(itemInstance);
            player.sendPacket(iu);
            player.broadcastPacket(new CharInfo(player));
            player.sendPacket(new UserInfo(player));

            // informations
            player.sendMessage("Changed enchantment of "+ itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
        }
    }
	public L2NpcEnchanterInstance(int objectId, L2NpcTemplate template) 
	{
		super(objectId, template);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Loads the main npc window(based on id)
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#getHtmlPath(int, int)
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";

		if (val == 0) pom = "" + npcId;
		else pom = npcId + "-" + val;

		return HtmlPathService.ENCHANTER_HTML_PATH + pom + ".htm";
	}
}