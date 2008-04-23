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

import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.effects.EffectCharge;
import net.sf.l2j.gameserver.templates.StatsSet;

/**
 * 
 * @author schursin 
 *
 */
public class L2SkillExitBuffs extends L2Skill 
{
	final int numCharges;
	final int chargeSkillId;
	final String exitBuffs;

	public L2SkillExitBuffs(StatsSet set)
	{
		super(set);

		numCharges    = set.getInteger("num_charges", getLevel());
		chargeSkillId = set.getInteger("charge_skill_id");
		exitBuffs     = set.getString ("exitBuffs");
	}

	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if (activeChar.isAlikeDead()) return;

		// get the effect
		EffectCharge effect = (EffectCharge)activeChar.getFirstEffect(chargeSkillId);
		if (effect == null || effect.numCharges < this.numCharges)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addSkillName(this.getId());
			activeChar.sendPacket(sm);
			return;
		}

		// decrease?
		effect.numCharges -= this.numCharges;

		// update icons
		activeChar.updateEffectIcons();

		// maybe exit? no charge
		if (effect.numCharges == 0) effect.exit();

		// what skills we need to debaff?
		String[] buffList = this.exitBuffs.split(";");

		// debaff it
		for (L2Effect Effect : activeChar.getAllEffects())
		{
			int effectId = Effect.getSkill().getId();

			for (String buffItem : buffList)
				if (Integer.parseInt(buffItem) == effectId) Effect.exit();
		}
	}
}