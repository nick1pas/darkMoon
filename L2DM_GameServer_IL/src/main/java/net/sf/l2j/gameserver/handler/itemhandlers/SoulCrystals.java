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
package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedItemTable;
import net.sf.l2j.gameserver.model.L2Attackable;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.4 $ $Date: 2005/08/14 21:31:07 $
 */

public class SoulCrystals implements IItemHandler
{
    protected static Log _log = LogFactory.getLog(SoulCrystals.class);
    
	// First line is for Red Soul Crystals, second is Green and third is Blue Soul Crystals,
    // ordered by ascending level, from 0 to 13... 
    private static final int[] ITEM_IDS = { 
    	HardcodedItemTable.RED_SOUL_CRYSTAL,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_1,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_3,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_4,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_6,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_7,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_9,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_10,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_12,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_13,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_1,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_3,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_4,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_6,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_7,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_9,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_10,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_12,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_13,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_1,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_3,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_4,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_6,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_7,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_9,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_10,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_12,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_13
        };

	
	// Our main method, where everything goes on
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		L2PcInstance activeChar = (L2PcInstance)playable;
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2MonsterInstance))
		{
			// Send a System Message to the caster
            SystemMessage sm = new SystemMessage(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(sm);
			
			// Send a Server->Client packet ActionFailed to the L2PcInstance 
            ActionFailed af = new ActionFailed();
            activeChar.sendPacket(af);
			
            return;
		}
        
        // u can use soul crystal only when target hp goes below <50%
        if(((L2MonsterInstance)target).getStatus().getCurrentHp() > ((L2MonsterInstance)target).getMaxHp()/2.0)
        {
            ActionFailed af = new ActionFailed();
            activeChar.sendPacket(af);
            return;
        }
        
		int crystalId = item.getItemId();

        // Soul Crystal Casting section
        L2Skill skill = SkillTable.getInstance().getInfo(2096, 1);
        activeChar.useMagic(skill, false, true);
        // End Soul Crystal Casting section
        
        // Continue execution later
        CrystalFinalizer cf = new CrystalFinalizer(activeChar, target, crystalId);
        ThreadPoolManager.getInstance().scheduleEffect(cf, skill.getHitTime());
		
	}

	static class CrystalFinalizer implements Runnable
	{
		private L2PcInstance _activeChar;
		private L2Attackable _target;
		private int _crystalId;
		
		CrystalFinalizer(L2PcInstance activeChar, L2Object target, int crystalId)
		{
		    _activeChar = activeChar;
		    _target = (L2Attackable)target;
		    _crystalId = crystalId;
		}
		
		public void run()
		{
        	if (_activeChar.isDead() || _target.isDead()) 
                return; 
        	_activeChar.enableAllSkills();
            try {
            	_target.addAbsorber(_activeChar, _crystalId);
            	_activeChar.setTarget(_target);
            } catch (Throwable e) {
                _log.error(e.getMessage(),e);
            }
		}
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}