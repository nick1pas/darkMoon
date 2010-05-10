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

import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.handler.ISkillConditionChecker;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.instancemanager.CCHManager;
import com.l2jfree.gameserver.instancemanager.FortSiegeManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jfree.gameserver.model.entity.CCHSiege;
import com.l2jfree.gameserver.model.entity.FortSiege;
import com.l2jfree.gameserver.model.entity.Siege;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.skills.l2skills.L2SkillSiegeFlag;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;
import com.l2jfree.gameserver.templates.skills.L2SkillType;

/**
 * @author _drunk_
 * 
 */
public class SiegeFlag extends ISkillConditionChecker
{
	private static final L2SkillType[]	SKILL_IDS	=
													{ L2SkillType.SIEGEFLAG };

	@Override
	public boolean checkConditions(L2Character activeChar, L2Skill skill)
	{
		if (!(activeChar instanceof L2PcInstance))
			return false;
		
		final L2PcInstance player = (L2PcInstance) activeChar;
		
		if (!SiegeManager.checkIfOkToPlaceFlag(player, false) && !FortSiegeManager.checkIfOkToPlaceFlag(player, false))
			return false;
		else if (player.isInsideZone(L2Zone.FLAG_NO_HQ))
			return false;
		
		return super.checkConditions(activeChar, skill);
	}
	
	public void useSkill(L2Character activeChar, L2Skill skill0, L2Character... targets)
	{
		L2SkillSiegeFlag skill = (L2SkillSiegeFlag)skill0;
		
		if (!(activeChar instanceof L2PcInstance))
			return;

		L2PcInstance player = (L2PcInstance) activeChar;

		Siege siege = SiegeManager.getInstance().getSiege(player);
		FortSiege fsiege = FortSiegeManager.getInstance().getSiege(player);
		CCHSiege csiege = CCHManager.getInstance().getSiege(player);
		// In a siege zone
		if (siege != null && SiegeManager.checkIfOkToPlaceFlag(player, false))
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(35062);
			if (skill != null && template != null)
			{
				// spawn a new flag
				L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, IdFactory.getInstance().getNextId(), template, skill.isAdvanced());
				flag.setTitle(player.getClan().getName());
				flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
				flag.setHeading(player.getHeading());
				flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
			}
		}
		else if (fsiege != null && FortSiegeManager.checkIfOkToPlaceFlag(player, false))
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(35062);
			if (skill != null && template != null)
			{
				// spawn a new flag
				L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, IdFactory.getInstance().getNextId(), template, skill.isAdvanced());
				flag.setTitle(player.getClan().getName());
				flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
				flag.setHeading(player.getHeading());
				flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
			}
		}
		else if (csiege != null && CCHManager.checkIfOkToPlaceFlag(player, false))
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(35062);
			if (skill != null && template != null)
			{
				L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, IdFactory.getInstance().getNextId(), template, skill.isAdvanced());
				flag.setTitle(player.getClan().getName());
				flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
				flag.setHeading(player.getHeading());
				flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
			}
		}
	}

	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
