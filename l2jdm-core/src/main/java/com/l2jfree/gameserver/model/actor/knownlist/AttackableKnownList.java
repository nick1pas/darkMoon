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
package com.l2jfree.gameserver.model.actor.knownlist;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.L2Attackable;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;


public class AttackableKnownList extends NpcKnownList
{
	// =========================================================
	// Data Field
	
	// =========================================================
	// Constructor
	public AttackableKnownList(L2Attackable activeChar)
	{
		super(activeChar);
	}

	// =========================================================
	// Method - Public
	@Override
	public boolean removeKnownObject(L2Object object)
	{
		if (!super.removeKnownObject(object))
			return false;

		// Remove the L2Object from the _aggrolist of the L2Attackable
		if (object instanceof L2Character)
			getActiveChar().getAggroList().remove(object);
		
		// Set the L2Attackable Intention to AI_INTENTION_IDLE
		//FIXME: This is a temporary solution
		if (getActiveChar().hasAI() && object instanceof L2PcInstance && getKnownPlayers().isEmpty())
		{
			getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}

		return true;
	}
	
	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public
	@Override
	public L2Attackable getActiveChar()
	{
		return (L2Attackable) _activeChar;
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		if (getActiveChar().getAggroListRP().get(object) != null)
			return 3000;
		return Math.min(2200, 2 * getDistanceToWatchObject(object));
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2NpcInstance || !(object instanceof L2Character))
			return 0;
		
		if (object instanceof L2Playable)
			return 1500;
		
		if (getActiveChar().getAggroRange() > getActiveChar().getFactionRange())
			return getActiveChar().getAggroRange();
		
		if (getActiveChar().getFactionRange() > 300)
			return getActiveChar().getFactionRange();
		
		return 300;
	}
}
