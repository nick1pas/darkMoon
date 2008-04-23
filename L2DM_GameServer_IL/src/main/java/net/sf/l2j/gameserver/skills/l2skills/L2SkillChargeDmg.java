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
package net.sf.l2j.gameserver.skills.l2skills;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.effects.EffectCharge;
import net.sf.l2j.gameserver.templates.L2WeaponType;
import net.sf.l2j.gameserver.templates.StatsSet;

public class L2SkillChargeDmg extends L2Skill 
{
	final int chargeSkillId;
	
	public L2SkillChargeDmg(StatsSet set)
	{
		super(set);
		chargeSkillId = set.getInteger("charge_skill_id");
	}

	public boolean checkCondition(L2Character activeChar, L2Object target, boolean itemOrWeapon)
	{
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance)activeChar;
			EffectCharge e = (EffectCharge)player.getFirstEffect(chargeSkillId);
			if(e == null || e.numCharges < getNumCharges())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
				sm.addSkillName(getId());
				activeChar.sendPacket(sm);
				return false;
			}
		}
		return super.checkCondition(activeChar, target, itemOrWeapon);
	}
	
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
			return;
		
		// get the effect
		EffectCharge effect = (EffectCharge) activeChar.getFirstEffect(chargeSkillId);
		if (effect == null || effect.numCharges < getNumCharges())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addSkillName(getId());
			activeChar.sendPacket(sm);
			return;
		}
		double modifier = 0;
		modifier = (effect.numCharges-getNumCharges())*0.33;
		if (getTargetType() != SkillTargetType.TARGET_AREA && getTargetType() != SkillTargetType.TARGET_MULTIFACE)
			effect.numCharges -= getNumCharges();
		if (activeChar instanceof L2PcInstance)
			activeChar.sendPacket(new EtcStatusUpdate((L2PcInstance)activeChar));
		if (effect.numCharges == 0)
			effect.exit();
		for (L2Object element : targets) {
			L2ItemInstance weapon = activeChar.getActiveWeaponInstance();
			L2Character target = (L2Character)element;
			if (target.isAlikeDead())
				continue;

			boolean shld = Formulas.getInstance().calcShldUse(activeChar, target);
			boolean crit = false;
			if (getBaseCritRate() > 0)
				crit = Formulas.getInstance().calcCrit(getBaseCritRate() * 10 * Formulas.getInstance().getSTRBonus(activeChar));

			boolean soul = (weapon != null 
							&& weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT 
							&& weapon.getItemType() != L2WeaponType.DAGGER );
			
			// damage calculation, crit is static 2x
			int damage = (int)Formulas.getInstance().calcPhysDam(activeChar, target, this, shld, false, false, soul);
			if (crit) damage *= 2;

			if (activeChar instanceof L2PcInstance)
			{
				L2PcInstance activeCaster = (L2PcInstance)activeChar;
				
				if (activeCaster.isGM() && activeCaster.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
					damage = 0;
			}

			if (damage > 0)
			{
				double finalDamage = damage;
				finalDamage = finalDamage+(modifier*finalDamage);
				target.reduceCurrentHp(finalDamage, activeChar);
				
				activeChar.sendDamageMessage(target, (int)finalDamage, false, crit, false);
				
				if (soul && weapon!= null)
					weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
			}
			else
			{
				activeChar.sendDamageMessage(target, 0, false, false, true);
			}
		}        // effect self :]
		L2Effect seffect = activeChar.getFirstEffect(getId());
		if (seffect != null && seffect.isSelfEffect())
		{
			//Replace old effect with new one.
			seffect.exit();
		}
		// cast self effect if any
		getEffectsSelf(activeChar);
	}
}
