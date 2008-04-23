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

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */

public class Potions implements IItemHandler
{
    private static final int[] ITEM_IDS =
        { 65, 725, 726, 727, 728, 733, 734, 735, 1060, 1061, 1062, 1073, 1374, 1375,
                1539, 1540, 5283, 5591, 5592, 6035, 6036, 6652, 6553, 6554, 6555,
                8193, 8194, 8195, 8196, 8197, 8198, 8199, 8200, 8201, 8202,
                8600, 8601, 8602, 8603, 8604, 8605, 8606, 8607, 8608, 8609,
				8610, 8611, 8612, 8613, 8614,
				//elixir of life
				8622, 8623, 8624, 8625, 8626, 8627,
				//elixir of Strength
				8628, 8629, 8630, 8631, 8632, 8633,
				//elixir of cp 
				8634, 8635, 8636, 8637, 8638, 8639};


    public synchronized void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
        L2PcInstance activeChar;
        boolean res = false;
        if (playable instanceof L2PcInstance)
            activeChar = (L2PcInstance) playable;
        else if (playable instanceof L2PetInstance)
            activeChar = ((L2PetInstance) playable).getOwner();
        else
            return;

        if (activeChar.isInOlympiadMode())
        {
            activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
            return;
        }

        if (activeChar.isAllSkillsDisabled())
        {
            ActionFailed af = new ActionFailed();
            activeChar.sendPacket(af);
            return;
        }

        int itemId = item.getItemId();

        switch(itemId)
        {
	         // Mana potions
        	case 726: // mana drug, xml: 2003
        		//L2EMU_ADD_BEGIN
        		if (!isUseable(activeChar, L2Effect.EffectType.MANA_HEAL_OVER_TIME, itemId, 2003))
        			return;
        		//L2EMU_ADD_END
	            res = usePotion(activeChar, 2003, 1); // configurable through xml
	            break;
	                                                    // till handler implemented
        	case 728: // mana_potion, xml: 2005
        		if (!isUseable(activeChar,itemId,2005)) 
					return;
	            res = usePotion(activeChar, 2005, 1);
	            break;

	        // Healing and speed potions
        	case 65: // red_potion, xml: 2001
				if (!isUseable(activeChar,itemId,2001)) 
					return;
	            res = usePotion(activeChar, 2001, 1);
	            break;
        	case 725: // healing_drug, xml: 2002
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId, 2002))
        			return;
	            res = usePotion(activeChar, 2002, 1);
	            break;
        	case 727: // _healing_potion, xml: 2032
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId,2032))
        			return;
 				res = usePotion(activeChar, 2032, 1);
	            break;
        	case 733: //_Endeavor potion, xml: 2010
				if (!isUseable(activeChar,itemId,2010)) 
					return;
	            res = usePotion(activeChar, 2010, 1);
	            break;
        	case 734: // quick_step_potion, xml: 2011
				if (!isUseable(activeChar,itemId,2011)) 
					return;
	            res = usePotion(activeChar, 2011, 1);
	            break;
        	case 735: // swift_attack_potion, xml: 2012
				if (!isUseable(activeChar,itemId,2012)) 
					return;
	            res = usePotion(activeChar, 2012, 1);
	            break;
        	case 1060: // lesser_healing_potion,
        	case 1073: // beginner's potion, xml:
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId,2031))
        			return;
 				res = usePotion(activeChar, 2031, 1);
	            break;
        	case 1061: // healing_potion, xml: 2032
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId,2032))
        			return;
 				res = usePotion(activeChar, 2032, 1);
	            break;
        	case 1062: // haste_potion, xml: 2033
				if (!isUseable(activeChar,itemId,2033)) 
					return;
	            res = usePotion(activeChar, 2033, 1);
	            break;
        	case 1374: // adv_quick_step_potion, xml: 2034
				if (!isUseable(activeChar,itemId,2034)) 
					return;
	            res = usePotion(activeChar, 2034, 1);
	            break;
        	case 1375: // adv_swift_attack_potion, xml: 2035
				if (!isUseable(activeChar,itemId,2035)) 
					return;
	            res = usePotion(activeChar, 2035, 1);
	            break;
        	case 1539: // greater_healing_potion, xml: 2037
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId, 2037))
        			return;
 				res = usePotion(activeChar, 2037, 1);
	            break;
        	case 1540: // quick_healing_potion, xml: 2038
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId, 2038))
        			return;
 				res = usePotion(activeChar, 2038, 1);
	            break;
        	case 5283: // Rice Cake, xml: 2136
        		if (!isUseable(activeChar, L2Effect.EffectType.HEAL_OVER_TIME, itemId,2136)) 
        			return;
 				MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2136, 1, 1, 0);
	            activeChar.broadcastPacket(MSU);
	            res= usePotion(activeChar, 2136, 1);
	            break;
        	case 5591: // CP and Greater CP
        	case 5592: // Potion
				// elixir of Mental Strength
				if (!isUseable(activeChar, L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME, itemId,2166)) 
					return;
 				res = usePotion(activeChar, 2166, (itemId == 5591) ? 1 : 2);
	            break;
        	case 6035: // Magic Haste Potion, xml: 2169
        	case 6036: // Greater Magic Haste Potion, xml: 2169
				if (!isUseable(activeChar,itemId,2169)) 
					return;
        		res = usePotion(activeChar, 2169, (itemId == 6035) ? 1 : 2);
        		break;
            // ELIXIR 
            case 8622:  
            case 8623:  
            case 8624: 
            case 8625:  
            case 8626:  
            case 8627:
            	// elixir of Life
        		if (!isUseable(activeChar,itemId, 2136)) 
        			return;
				if ( 
						(itemId == 8622 && activeChar.getExpertiseIndex()==0) ||
						(itemId == 8623 && activeChar.getExpertiseIndex()==1) ||
						(itemId == 8624 && activeChar.getExpertiseIndex()==2) ||
						(itemId == 8625 && activeChar.getExpertiseIndex()==3) ||
						(itemId == 8626 && activeChar.getExpertiseIndex()==4) ||
						(itemId == 8627 && activeChar.getExpertiseIndex()==5)
					)
					res = usePotion(activeChar, 2287, (activeChar.getExpertiseIndex()+1));
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE);
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					return;
				}
                break; 
            case 8628:  
            case 8629:  
            case 8630:  
            case 8631:  
            case 8632:  
            case 8633: 
                // elixir of Strength
        		if (!isUseable(activeChar,itemId, 2288)) 
        			return;
                if (				
						(itemId == 8628 && activeChar.getExpertiseIndex()==0) ||
						(itemId == 8629 && activeChar.getExpertiseIndex()==1) ||
						(itemId == 8630 && activeChar.getExpertiseIndex()==2) ||
						(itemId == 8631 && activeChar.getExpertiseIndex()==3) ||
						(itemId == 8632 && activeChar.getExpertiseIndex()==4) ||
						(itemId == 8633 && activeChar.getExpertiseIndex()==5)
					)
					res = usePotion(activeChar, 2288, (activeChar.getExpertiseIndex()+1));
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE);
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					return;
				}		
                break;
            case 8634: 
            case 8635:  
            case 8636:  
            case 8637:  
            case 8638:  
            case 8639: 
                // elixir of cp
        		if (!isUseable(activeChar,itemId, 2289)) 
        			return;
                if (  
             	 		(itemId == 8634 && activeChar.getExpertiseIndex()==0) ||
						(itemId == 8635 && activeChar.getExpertiseIndex()==1) ||
						(itemId == 8636 && activeChar.getExpertiseIndex()==2) ||
						(itemId == 8637 && activeChar.getExpertiseIndex()==3) ||
						(itemId == 8638 && activeChar.getExpertiseIndex()==4) ||
						(itemId == 8639 && activeChar.getExpertiseIndex()==5)
					)
					res = usePotion(activeChar, 2289, (activeChar.getExpertiseIndex()+1));
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.INCOMPATIBLE_ITEM_GRADE);
					sm.addItemName(itemId);
					activeChar.sendPacket(sm);
					return;
				}			
                break;
	        // Valakas Amulets
            case 6652: // Amulet Protection of Valakas
	            res = usePotion(activeChar, 2231, 1);
	            break;
            case 6653: // Amulet Flames of Valakas
	            res = usePotion(activeChar, 2223, 1);
	            break;
            case 6654: // Amulet Flames of Valakas
	            res = usePotion(activeChar, 2233, 1);
	            break;
            case 6655: // Amulet Slay Valakas
	            res = usePotion(activeChar, 2232, 1);
	            break;
	
	        // Herbs
            case 8600: // Herb of Life
	            res = usePotion(activeChar, 2278, 1);
	            break;
            case 8601: // Greater Herb of Life
	            res = usePotion(activeChar, 2278, 2);
	            break;
            case 8602: // Superior Herb of Life
	            res = usePotion(activeChar, 2278, 3);
	            break;
            case 8603: // Herb of Mana
	            res = usePotion(activeChar, 2279, 1);
	            break;
            case 8604: // Greater Herb of Mane
	            res = usePotion(activeChar, 2279, 2);
	            break;
            case 8605: // Superior Herb of Mane
	            res = usePotion(activeChar, 2279, 3);
	            break;
            case 8606: // Herb of Strength
	            res = usePotion(activeChar, 2280, 1);
	            break;
            case 8607: // Herb of Magic
	            res = usePotion(activeChar, 2281, 1);
	            break;
            case 8608: // Herb of Atk. Spd.
	            res = usePotion(activeChar, 2282, 1);
	            break;
            case 8609: // Herb of Casting Spd.
	            res = usePotion(activeChar, 2283, 1);
	            break;
            case 8610: // Herb of Critical Attack
	            res = usePotion(activeChar, 2284, 1);
	            break;
            case 8611: // Herb of Speed
	            res = usePotion(activeChar, 2285, 1);
	            break;
            case 8612: // Herb of Warrior
	            res = usePotion(activeChar, 2280, 1);// Herb of Strength
	            res = usePotion(activeChar, 2282, 1);// Herb of Atk. Spd
	            res = usePotion(activeChar, 2284, 1);// Herb of Critical Attack
	            break;
            case 8613: // Herb of Mystic
	            res = usePotion(activeChar, 2281, 1);// Herb of Magic
	            res = usePotion(activeChar, 2283, 1);// Herb of Casting Spd.
	            break;
            case 8614: // Herb of Warrior
	            res = usePotion(activeChar, 2278, 3);// Superior Herb of Life
	            res = usePotion(activeChar, 2279, 3);// Superior Herb of Mana
	            break;
            case 8193: // Fisherman's Potion - Green
	            if (activeChar.getSkillLevel(1315) <= 3) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 1);
	            break;
            case 8194:  // Fisherman's Potion - Jade
	            if (activeChar.getSkillLevel(1315) <= 6) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 2);
	            break;
            case 8195: // Fisherman's Potion - Blue
	            if (activeChar.getSkillLevel(1315) <= 9) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 3);
	            break;
            case 8196: // Fisherman's Potion - Yellow
	            if (activeChar.getSkillLevel(1315) <= 12) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 4);
	            break;
            case 8197: // Fisherman's Potion - Orange
	            if (activeChar.getSkillLevel(1315) <= 15) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 5);
	            break;
            case 8198: // Fisherman's Potion - Purple
	            if (activeChar.getSkillLevel(1315) <= 18) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 6);
	            break;
            case 8199: // Fisherman's Potion - Red
	            if (activeChar.getSkillLevel(1315) <= 21) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 7);
	            break;
            case 8200: // Fisherman's Potion - White
	            if (activeChar.getSkillLevel(1315) <= 24) {
	                playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                playable.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	                return;
	            }
	            res = usePotion(activeChar, 2274, 8);
	            break;
            case 8201:  // Fisherman's Potion - Black
	            res = usePotion(activeChar, 2274, 9);
	            break;
            case 8202:  // Fishing Potion
	            res = usePotion(activeChar, 2275, 1);
	            break;
	        default:
        }
        
        if (res)
            playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
    }

    private boolean isEffectReplaceable(L2PcInstance activeChar, Enum effectType, int itemId)
	{
		L2Effect[] effects = activeChar.getAllEffects();
		
		if (effects == null) return true;

		for (L2Effect e : effects)
		{
			if (e.getEffectType() == effectType)
			{
				// One can reuse pots after 2/3 of their duration is over.
				// It would be faster to check if its > 10 but that would screw custom pot durations...
				if (e.getTaskTime() > (e.getSkill().getBuffDuration()*67)/100000) return true;
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
				sm.addItemName(itemId);
				activeChar.sendPacket(sm);
				return false;
			}
		}
		return true;
	}

    private boolean isUseable(L2PcInstance activeChar, int itemId, int skillid)
    {
    	if (activeChar.isSkillDisabled(skillid))
    	{
    		SystemMessage sm = new SystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
    		sm.addItemName(itemId);
    		activeChar.sendPacket(sm);
    		return false;
    	}
    	return true;
    }
    
    private boolean isUseable(L2PcInstance activeChar, Enum effectType, int itemId,int skillid)
    {
    	return (isEffectReplaceable(activeChar,effectType,itemId) && isUseable(activeChar,itemId,skillid));
    }

    public boolean usePotion(L2PcInstance activeChar, int magicId, int level)
    {
        L2Skill skill = SkillTable.getInstance().getInfo(magicId, level);
        if (skill != null)
        {
            activeChar.doCast(skill);
            if (!(activeChar.isSitting() && !skill.isPotion()))
                return true;
        }
        return false;
    }

    public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}
