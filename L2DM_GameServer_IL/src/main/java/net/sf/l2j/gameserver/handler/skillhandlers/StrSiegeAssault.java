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

import net.sf.l2j.gameserver.handler.ISkillHandler;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.templates.L2WeaponType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author _tomciaaa_
 * 
 */
public class StrSiegeAssault implements ISkillHandler
{
    private final static Log _log = LogFactory.getLog(StrSiegeAssault.class);

    private static final SkillType[] SKILL_IDS =
    { SkillType.STRSIEGEASSAULT };

    public void useSkill(L2Character activeChar, @SuppressWarnings("unused")
    L2Skill skill, @SuppressWarnings("unused")
    L2Object[] targets)
    {
        if (activeChar == null || !(activeChar instanceof L2PcInstance))
            return;

        L2PcInstance player = (L2PcInstance) activeChar;

        if (!SiegeManager.checkIfOkToUseStriderSiegeAssault(player, false))
        {

            try
            {
                L2ItemInstance itemToTake = player.getInventory().getItemByItemId(skill.getItemConsumeId());
                if (itemToTake.getCount() - skill.getItemConsume() <= 0)
                    itemToTake.decayMe();
                else
                    itemToTake.setCount(itemToTake.getCount() - skill.getItemConsume());

                //TODO: damage calculation below is crap - needs rewrite
                int damage = 0;

                for (L2Object element : targets) {
                    L2Character target = (L2Character) element;
                    L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
                    if (activeChar instanceof L2PcInstance && target instanceof L2PcInstance && target.isAlikeDead()
                            && target.isFakeDeath())
                    {
                        target.stopFakeDeath(null);
                    } else if (target.isAlikeDead())
                        continue;

                    boolean dual = activeChar.isUsingDualWeapon();
                    boolean shld = Formulas.getInstance().calcShldUse(activeChar, target);
                    boolean crit = Formulas.getInstance()
                            .calcCrit(activeChar, target, activeChar.getCriticalHit(target, skill));
                    boolean soul = (weapon != null && weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT && weapon
                            .getItemType() != L2WeaponType.DAGGER);

                    if (!crit && (skill.getCondition() & L2Skill.COND_CRIT) != 0)
                        damage = 0;
                    else
                        damage = (int) Formulas.getInstance().calcPhysDam(activeChar, target, skill, shld, crit, dual, soul);

                    if (damage > 0)
                    {
                        target.reduceCurrentHp(damage, activeChar);
                        if (soul && weapon != null)
                            weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);

                        activeChar.sendDamageMessage(target, damage, false, false, false);
                    } else
                        activeChar.sendMessage(skill.getName() + " failed.");
                }
            } catch (Exception e)
            {
                _log.error(e);
            }
        }
    }

    public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}
