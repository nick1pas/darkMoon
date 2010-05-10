/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.skills.l2skills;

import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.templates.StatsSet;
import com.l2jfree.gameserver.templates.skills.L2EffectType;

/**
 * Used for Break Duress skill mainly uses number of charges to negate, negate number depends on charge consume<br>
 * FIXME: this skill is hardcoded like hell
 * 
 * @author Darki699
 */
public class L2SkillChargeNegate extends L2Skill
{
	public L2SkillChargeNegate(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(L2Character activeChar, L2Character... targets)
	{
		if (activeChar.isAlikeDead() || !(activeChar instanceof L2PcInstance))
			return;
		
		for (L2Character target : targets)
		{
			if (target.isAlikeDead())
				continue;
			
			for (L2Effect e : target.getAllEffects())
			{
				if (e.getEffectType() == L2EffectType.ROOT)
					e.exit();
				
				if (getLevel() > 1 && e.stackTypesEqual("RunSpeedDown"))
					e.exit();
			}
		}
	}
}
