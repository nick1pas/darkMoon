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
package com.l2jfree.gameserver.handler.skillhandlers;

import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.templates.skills.L2SkillType;

/**
 *
 * @author  Julian
 */
public class DeluxeKey implements ISkillHandler
{
	private static final L2SkillType[]	SKILL_IDS	=
													{ L2SkillType.DELUXE_KEY_UNLOCK };

	public void useSkill(L2Character activeChar, L2Skill skill, L2Character... targets)
	{
		if (_log.isDebugEnabled())
			_log.info("Deluxe key casting succeded.");

		// This is just a dummy skill handler for deluxe chest keys,
		// since the AI responce onSkillUse handles the rest.
	}

	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
