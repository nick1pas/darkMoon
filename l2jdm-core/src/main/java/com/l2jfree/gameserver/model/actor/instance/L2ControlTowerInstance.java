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
package com.l2jfree.gameserver.model.actor.instance;

import java.util.List;

import javolution.util.FastList;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.geodata.GeoData;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.L2Npc;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.StatusUpdate;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

public class L2ControlTowerInstance extends L2Npc
{
    private List<L2Spawn> _guards;

	public L2ControlTowerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setIsInvul(false);
	}

    @Override
    public boolean isAttackable()
    {
        // Attackable during siege by attacker only
        return (getCastle() != null
                && getCastle().getCastleId() > 0
                && getCastle().getSiege().getIsInProgress());
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker)
	{
		// Attackable during siege by attacker only
		return (attacker != null
		        && attacker instanceof L2PcInstance
		        && getCastle() != null
		        && getCastle().getCastleId() > 0
		        && getCastle().getSiege().getIsInProgress()
		        && getCastle().getSiege().checkIsAttacker(((L2PcInstance)attacker).getClan()));
	}

	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		onAction(player);
	}

	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player)) return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
			StatusUpdate su = new StatusUpdate(getObjectId());
			su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
			su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
			player.sendPacket(su);
		}
		else
		{
			if (isAutoAttackable(player) && Math.abs(player.getZ() - getZ()) < 100 // Less then max height difference, delete check when geo
					&& GeoData.getInstance().canSeeTarget(player, this))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);

				// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}

    /**
     * Remove the L2ControlTowerInstance from the world.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Manage Siege task (killFlag, killCT) </li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR><BR>
     *
     * @see com.l2jfree.gameserver.model.actor.instance.L2NpcInstance#decayMe()
     */
    @Override
    public final void decayMe()
    {
        if (getCastle().getSiege().getIsInProgress())
        {
            getCastle().getSiege().killedCT(this);

            if (getGuards() != null && !getGuards().isEmpty())
            {
                for (L2Spawn spawn: getGuards())
                {
                    if (spawn == null) continue;
                    spawn.stopRespawn();
                }
            }
        }
        super.decayMe();
        // TODO: now spawn another NPC (id + 1) which represents dead tower
    }

    public void registerGuard(L2Spawn guard)
    {
        getGuards().add(guard);
    }

    public final List<L2Spawn> getGuards()
    {
        if (_guards == null) _guards = new FastList<L2Spawn>();
        return _guards;
    }
}
