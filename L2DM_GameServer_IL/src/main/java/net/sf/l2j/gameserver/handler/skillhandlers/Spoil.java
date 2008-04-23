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
package net.sf.l2j.gameserver.handler.skillhandlers; 

import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Formulas;

/** 
 * @author _drunk_ 
 */ 
public class Spoil implements ISkillHandler 
{ 
    //private static Logger _log = Logger.getLogger(Spoil.class.getName()); 
    private static final SkillType[] SKILL_IDS = {SkillType.SPOIL};
    
    public void useSkill(L2Character activeChar, L2Skill skill, @SuppressWarnings("unused") L2Object[] targets)
    { 
        if (!(activeChar instanceof L2PcInstance))
            return;

        L2Object[] targetList = skill.getTargetList(activeChar);
        
        if (targetList == null)
        {
            return;
        }

        for (L2Object element : targetList) {
            if (!(element instanceof L2MonsterInstance))
                continue;

            L2MonsterInstance target = (L2MonsterInstance) element;
			
            if (target.isSpoil()) {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.ALREDAY_SPOILED));
                continue;
            }

            // SPOIL SYSTEM by Lbaldi
            boolean spoil = false;
            if ( target.isDead() == false ) 
            {
                spoil = Formulas.getInstance().calcMagicSuccess(activeChar, (L2Character)element, skill);
                
                if (spoil)
                {
                    target.setSpoil(true);
                    target.setIsSpoiledBy(activeChar.getObjectId());
                    activeChar.sendPacket(new SystemMessage(SystemMessageId.SPOIL_SUCCESS));
                }
                else
                {
                    SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
                    sm.addString(target.getName());
                    sm.addSkillName(skill.getDisplayId());
                    activeChar.sendPacket(sm);
                }
                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar);
            }
        }
    } 
    
    public SkillType[] getSkillIds()
    { 
        return SKILL_IDS; 
    } 
}
