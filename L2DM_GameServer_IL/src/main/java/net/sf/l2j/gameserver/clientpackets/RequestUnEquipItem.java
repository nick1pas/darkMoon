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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2Item;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.8.2.3.2.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestUnEquipItem extends L2GameClientPacket
{
	private static final String _C__11_REQUESTUNEQUIPITEM = "[C] 11 RequestUnequipItem";
	private final static Log _log = LogFactory.getLog(RequestUnEquipItem.class.getName());

	// cd
	private int _slot;
	
	/**
	 * packet type id 0x11
	 * format:		cd 
	 * @param decrypt
	 */
    @Override
    protected void readImpl()
    {
        _slot = readD();
    }

    @Override
    protected void runImpl()
	{
    	if (_log.isDebugEnabled()) 
    		_log.debug("request unequip slot " + _slot);
        
		L2PcInstance activeChar = getClient().getActiveChar();
        
		if (activeChar == null)
		    return;
		if (activeChar._haveFlagCTF){
            activeChar.sendMessage("You can't unequip a CTF flag.");
            return;			
		}
		L2ItemInstance item = activeChar.getInventory().getPaperdollItemByL2ItemId(_slot);
		if (item != null && item.isWear())
		{
			// Wear-items are not to be unequipped
			return;
		}
        // Prevent of unequiping a cursed weapon
        if (_slot == L2Item.SLOT_LR_HAND && activeChar.isCursedWeaponEquiped())
        {
            // Message ?
            activeChar.sendMessage("You can't unequip a cursed Weapon.");
            return;
        }
        
        // Prevent player from unequipping items in special conditions
        if (activeChar.isStunned() || activeChar.isSleeping() 
                   || activeChar.isParalyzed() || activeChar.isAlikeDead())
        {
            activeChar.sendMessage("Your status does not allow you to do that.");
            return;
        }
        if (activeChar.isAttackingNow() || activeChar.isCastingNow()) 
           return;
        
        //L2EMU_ADD
        //remove skill of cupid's bow
        if(item != null && item.isCupidBow()){
        	if(item.getItemId() == 9140){        			
        		activeChar.removeSkill(SkillTable.getInstance().getInfo(3261, 1));	
        	}
        	else{
        		activeChar.removeSkill(SkillTable.getInstance().getInfo(3260, 0));
        		activeChar.removeSkill(SkillTable.getInstance().getInfo(3262, 0));
        	}
        }
        //L2EMU_ADD
        
		L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(_slot); 
		
		for (L2ItemInstance element : unequiped) {
            activeChar.checkSSMatch(null, element);			
            activeChar.getInventory().updateInventory(element);
		}
		activeChar.broadcastUserInfo();
		
		// this can be 0 if the user pressed the right mousebutton twice very fast
		if (unequiped.length > 0)
		{
        	
            SystemMessage sm = null;
            if (unequiped[0].getEnchantLevel() > 0)
            {
            	sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
            	sm.addNumber(unequiped[0].getEnchantLevel());
            	sm.addItemName(unequiped[0].getItemId());
            }
            else
            {
	            sm = new SystemMessage(SystemMessageId.S1_DISARMED);
	            sm.addItemName(unequiped[0].getItemId());
            }
            activeChar.sendPacket(sm);
            sm = null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__11_REQUESTUNEQUIPITEM;
	}
}
