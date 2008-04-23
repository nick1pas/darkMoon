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
package net.sf.l2j.gameserver.handler.admincommandhandlers;


import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.CharInfo;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.UserInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> enchant_armor = enchants armor <br>
 * <li>  admin_seteh = enchants Head <br>
 * <li>  admin_setec = enchants Chest <br>
 * <li>  admin_seteg = enchants Gloves <br>
 * <li>  admin_setel = enchants Legs <br>
 * <li>  admin_seteb = enchants Body <br>
 * <li>  admin_setes = enchants Left Hand (Shield) <br>
 * <li>  admin_setle = enchants Left Ear <br>
 * <li>  admin_setre = enchants Right Ear <br>
 * <li>  admin_setlf = enchants Left Finger <br>
 * <li>  admin_setrf = enchants Right Finger <br>
 * <li>  admin_seten = enchants Neck <br>
 * <li>  admin_setun = enchants Under Wear <br>
 * <li>  admin_setba = enchants Boot <br>
 * <li>  admin_enchant = opens enchant menu <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li> //admin_seteh <br>
 * <li> //admin_setec <br>
 * <li> //admin_seteg <br>
 * <li> //admin_setel <br> 
 * <li> //admin_seteb <br> 
 * <li> //admin_setes <br>
 * <li> //admin_setle <br> 
 * <li> //admin_setre <br>
 * <li> //admin_setlf <br>
 * <li> //admin_setrf <br>
 * <li> //admin_seten <br>
 * <li> //admin_setun <br>
 * <li> //admin_setba <br> 
 * <li> //admin_enchant <br><br>
 * 
 * 
 * @version $Revision: 1.3.2.1.2.10 $ $Date: 2005/08/24 21:06:06 $
 */
public class AdminEncArmor implements IAdminCommandHandler {

	private static String[] ADMIN_COMMANDS = 
	{
		"admin_seteh", // Head
		"admin_setec", // Chest
		"admin_seteg", // Gloves
		"admin_setel", // Legs
		"admin_seteb", // Body
		"admin_setes", // Left Hand (Shield)
		"admin_setle", // Left Ear
		"admin_setre", // Right Ear
		"admin_setlf", // Left Finger
		"admin_setrf", // Right Finger
		"admin_seten", // Neck
		"admin_setun", // Under Wear
		"admin_setba", // Boot
		"admin_enchant",
	};
	protected static Log _log = LogFactory.getLog(AdminEncArmor.class.getName());
	private static final int REQUIRED_LEVEL = Config.GM_ENCHANT;

	public boolean useAdminCommand(String command, L2PcInstance admin) 
	{
		if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) 
			return false;

		if (command.equals("admin_enchant")) showMainPage(admin);
		else
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();

			if (command.startsWith("admin_seteh"))
			{	
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_HEAD);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set helmet enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}	
			else if (command.startsWith("admin_setec"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_CHEST);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set chest armor enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}	
			else if (command.startsWith("admin_seteg"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_GLOVES);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set gloves enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_seteb"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_FEET);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set boots enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setel"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_LEGS);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set leggings enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setes"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_LHAND);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set shield enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setle"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_LEAR);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Left Earring enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setre"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_REAR);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Right Earring enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setlf"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_LFINGER);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Left Ring enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setrf"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_RFINGER);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Right Ring enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_seten"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_NECK);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Necklace enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setun"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_UNDER);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Underwear enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
			else if (command.startsWith("admin_setba"))
			{
				try
				{   
					int ench = Integer.parseInt(st.nextToken());
					setTarget(admin, ench ,Inventory.PAPERDOLL_BACK);
				}
				catch (StringIndexOutOfBoundsException e)
				{
					if ( Config.DEVELOPER ) _log.info("Set Cloak enchant error: "+e);
					admin.sendMessage("Please specify new enchant value.");
				}
			}
		}
		return true;
	}

	private void setTarget(L2PcInstance admin, int ench ,int armorType)
	{
		// set target
		L2Object target = admin.getTarget();
		if (target == null)
			target = admin;
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) 
		{
			player = (L2PcInstance)target;
		} 
		else 
		{
			return;
		}

		// check value
		if ( ench >= 0 && ench <= 65535 ) 
		{
			// now we need to find the equipped weapon of the targeted character...
			int curEnchant = 0; // display purposes only
			int dropSlot = armorType;
			boolean canEnchant = false;
			L2ItemInstance parmorInstance = null;
			L2ItemInstance armorToEnchant = null;

			// only attempt to enchant if there is a weapon equipped
			if ( canEnchant == false )
			{
				// check equip
				parmorInstance = player.getInventory().getPaperdollItem(dropSlot);
				if (parmorInstance != null && parmorInstance.getEquipSlot() == dropSlot ) 
				{ 		
					armorToEnchant = parmorInstance;
					curEnchant = armorToEnchant.getEnchantLevel();
					canEnchant = true;
				}
			}

			if ( canEnchant == true && armorToEnchant != null ) {
				// set enchant value
				player.getInventory().unEquipItemInSlotAndRecord(dropSlot);
				parmorInstance.setEnchantLevel(ench);							    
				player.getInventory().equipItemAndRecord(armorToEnchant);

				InventoryUpdate iu = new InventoryUpdate();	
				iu.addModifiedItem(armorToEnchant);
				player.sendPacket(iu);

				CharInfo info1 = new CharInfo(player);
				player.broadcastPacket(info1);
				UserInfo info2 = new UserInfo(player);
				player.sendPacket(info2);

				// information
				admin.sendMessage("Changed enchantment of equipped item ("+armorToEnchant.getItem().getName()+") from ("+curEnchant+") to ("+ench+").");			
				player.sendMessage("Admin has changed the enchantment of your equipped item ("+armorToEnchant.getItem().getName()+") from "+curEnchant+" to "+ench+".");

			}
			else {
				// no item is equipped
				// notify admin

				admin.sendMessage("Cannot enchant item for player "+player.getName()+": No item equipped.");		
				return;
			}
		}
		else 
		{
			// inform our gm of their mistake
			admin.sendMessage("You must set the enchant level to be between 0-65535.");					
		}
	}
	public void showMainPage(L2PcInstance admin)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<center><table width=260><tr><td width=40>");
		replyMSG.append("<button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		replyMSG.append("</td><td width=180>");
		replyMSG.append("<center>Enchant Equip</center>");
		replyMSG.append("</td><td width=40>");
		replyMSG.append("</td></tr></table></center><br>");
		replyMSG.append("<center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Underwear\" action=\"bypass -h admin_setun $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Helmet\" action=\"bypass -h admin_seteh $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");		
		replyMSG.append("<button value=\"Cloak\" action=\"bypass -h admin_setba $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Mask\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Necklace\" action=\"bypass -h admin_seten $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		replyMSG.append("</center><center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Weapon\" action=\"bypass -h admin_setew $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Chest\" action=\"bypass -h admin_setec $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");		
		replyMSG.append("<button value=\"Shield\" action=\"bypass -h admin_setes $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Earring\" action=\"bypass -h admin_setre $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Earring\" action=\"bypass -h admin_setle $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		replyMSG.append("</center><center><table width=270><tr><td>");
		replyMSG.append("<button value=\"Gloves\" action=\"bypass -h admin_seteg $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Leggings\" action=\"bypass -h admin_setel $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");		
		replyMSG.append("<button value=\"Boots\" action=\"bypass -h admin_seteb $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Ring\" action=\"bypass -h admin_setrf $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Ring\" action=\"bypass -h admin_setlf $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		replyMSG.append("</center><br>");
		replyMSG.append("<center>[Enchant 0-65535]</center>");
		replyMSG.append("<center><edit var=\"menu_command\" width=100 height=15></center><br>");
		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		admin.sendPacket(adminReply); 
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	/**
	 * 
	 * @param level
	 * @return
	 */
	private boolean checkLevel(int level) {	
		return (level >= REQUIRED_LEVEL);
	}
}