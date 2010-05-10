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

import com.l2jfree.Config;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.L2Npc;
import com.l2jfree.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jfree.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.DeleteObject;
import com.l2jfree.gameserver.network.serverpackets.SpawnItem;

public final class PcKnownList extends PlayableKnownList
{
	public PcKnownList(L2PcInstance activeChar)
	{
		super(activeChar);
	}
	
	/**
	 * Add a visible L2Object to L2PcInstance _knownObjects and _knownPlayer (if necessary) and send Server-Client Packets needed to inform the L2PcInstance of its state and actions in progress.<BR><BR>
	 *
	 * <B><U> object is a L2ItemInstance </U> :</B><BR><BR>
	 * <li> Send Server-Client Packet DropItem/SpawnItem to the L2PcInstance </li><BR><BR>
	 *
	 * <B><U> object is a L2DoorInstance </U> :</B><BR><BR>
	 * <li> Send Server-Client Packets DoorInfo and DoorStatusUpdate to the L2PcInstance </li>
	 * <li> Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance </li><BR><BR>
	 *
	 * <B><U> object is a L2Npc </U> :</B><BR><BR>
	 * <li> Send Server-Client Packet NpcInfo to the L2PcInstance </li>
	 * <li> Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance </li><BR><BR>
	 *
	 * <B><U> object is a L2Summon </U> :</B><BR><BR>
	 * <li> Send Server-Client Packet NpcInfo/PetItemList (if the L2PcInstance is the owner) to the L2PcInstance </li>
	 * <li> Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance </li><BR><BR>
	 *
	 * <B><U> object is a L2PcInstance </U> :</B><BR><BR>
	 * <li> Send Server-Client Packet CharInfo to the L2PcInstance </li>
	 * <li> If the object has a private store, Send Server-Client Packet PrivateStoreMsgSell to the L2PcInstance </li>
	 * <li> Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance </li><BR><BR>
	 *
	 * @param object The L2Object to add to _knownObjects and _knownPlayer
	 */
	@Override
	public boolean addKnownObject(L2Object object)
	{
		if (!super.addKnownObject(object))
			return false;
		
		if (object instanceof L2PcInstance && ((L2PcInstance)object).inObserverMode())
			return false;
		
		sendInfoOf(object);
		
		return true;
	}
	
	public void refreshInfos()
	{
		for (L2Object object : getKnownObjects().values())
		{
			if (object instanceof L2PcInstance && ((L2PcInstance)object).inObserverMode())
				continue;
			
			sendInfoOf(object);
		}
	}
	
	private void sendInfoOf(L2Object object)
	{
		if (object.getPoly().isMorphed() && object.getPoly().getPolyType().equals("item"))
		{
			//if (object.getPolytype().equals("item"))
			getActiveChar().sendPacket(new SpawnItem(object));
			//else if (object.getPolytype().equals("npc"))
			//	sendPacket(new NpcInfoPoly(object, this));
		}
		else
		{
			object.sendInfo(getActiveChar());
			
			if (object instanceof L2Character)
			{
				// Update the state of the L2Character object client side by sending Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance
				L2Character obj = (L2Character)object;
				if (obj.getAI() != null)
					obj.getAI().describeStateToPlayer(getActiveChar());
			}
		}
	}
	
	/**
	 * Remove a L2Object from L2PcInstance _knownObjects and _knownPlayer (if necessary) and send Server-Client Packet DeleteObject to the L2PcInstance.<BR><BR>
	 *
	 * @param object The L2Object to remove from _knownObjects and _knownPlayer
	 *
	 */
	@Override
	public boolean removeKnownObject(L2Object object)
	{
		if (!super.removeKnownObject(object))
			return false;
		
		// Send Server-Client Packet DeleteObject to the L2PcInstance
		getActiveChar().sendPacket(new DeleteObject(object));
		
		if (Config.TEST_KNOWNLIST && getActiveChar().isGM() && object instanceof L2Npc)
			getActiveChar().sendMessage("Knownlist,removed NPC: " + object.getName());
		return true;
	}
	
	// =========================================================
	// Method - Private
	
	// =========================================================
	// Property - Public
	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance)_activeChar;
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		// when knownlist grows, the distance to forget should be at least
		// the same as the previous watch range, or it becomes possible that
		// extra charinfo packets are being sent (watch-forget-watch-forget)
		int knownlistSize = getKnownObjects().size();
		if (knownlistSize <= 25)
			return 4000;
		
		if (knownlistSize <= 35)
			return 3500;
		
		if (knownlistSize <= 70)
			return 2910;
		
		return 2310;
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		int knownlistSize = getKnownObjects().size();
		
		if (knownlistSize <= 25)
			return 3400; // empty field
			
		if (knownlistSize <= 35)
			return 2900;
		if (knownlistSize <= 70)
			return 2300;
		
		return 1700; // Siege, TOI, city
	}
	
	@Override
	public final boolean tryRemoveObject(L2Object obj)
	{
		final L2PcInstance pc = getActiveChar();
		
		if (obj instanceof L2BoatInstance)
		{
			if (((L2BoatInstance) obj).getVehicleDeparture() == null)
				return false;
			
			if (pc.isInBoat() && pc.getBoat() == obj)
				return false;
		}
		else if (obj instanceof L2AirShipInstance)
		{
			if (((L2AirShipInstance) obj).getAirShipInfo() == null)
				return false;
			
			if (pc.isInAirShip() && pc.getAirShip() == obj)
				return false;
		}
		
		return super.tryRemoveObject(obj);
	}
}
