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
package com.l2jfree.gameserver.model.zone;

import com.l2jfree.gameserver.model.actor.L2Character;

/**
 * Inside this zone HP and/or MP are regenerated.
 * 
 * @author Savormix
 * @since 2009.04.19
 */
public class L2RegenerationZone extends L2DangerZone
{
	@Override
	protected void checkForDamage(L2Character character)
	{
		if (getHPDamagePerSecond() > 0)
			character.getStatus().increaseHp(getHPDamagePerSecond());
		
		if (getMPDamagePerSecond() > 0)
			character.getStatus().increaseMp(getMPDamagePerSecond());
	}
}
