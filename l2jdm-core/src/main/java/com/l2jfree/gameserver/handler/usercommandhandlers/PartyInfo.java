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
package com.l2jfree.gameserver.handler.usercommandhandlers;

import com.l2jfree.gameserver.handler.IUserCommandHandler;
import com.l2jfree.gameserver.model.L2Party;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/**
 * Support for /partyinfo command
 * @author Tempy - 28 Jul 05
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[]	COMMAND_IDS	=
											{ 81 };

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.l2jfree.gameserver.model.L2PcInstance)
	 */
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
			return false;

		activeChar.sendPacket(SystemMessageId.PARTY_INFORMATION);
		if (!activeChar.isInParty())
		{
			activeChar.sendPacket(SystemMessageId.WAR_LIST);
			return false;
		}

		L2Party playerParty = activeChar.getParty();
		int memberCount = playerParty.getMemberCount();
		int lootDistribution = playerParty.getLootDistribution();
		String partyLeader = playerParty.getPartyMembers().get(0).getName();

		switch (lootDistribution)
		{
		case L2Party.ITEM_LOOTER:
			activeChar.sendPacket(SystemMessageId.LOOTING_FINDERS_KEEPERS);
			break;
		case L2Party.ITEM_ORDER:
			activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN);
			break;
		case L2Party.ITEM_ORDER_SPOIL:
			activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN_INCLUDE_SPOIL);
			break;
		case L2Party.ITEM_RANDOM:
			activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM);
			break;
		case L2Party.ITEM_RANDOM_SPOIL:
			activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM_INCLUDE_SPOIL);
			break;
		}

		SystemMessage sm = new SystemMessage(SystemMessageId.PARTY_LEADER_C1);
		sm.addString(partyLeader);
		activeChar.sendPacket(sm);
		activeChar.sendMessage("Members: " + memberCount + "/9");
		activeChar.sendPacket(SystemMessageId.WAR_LIST);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.handler.IUserCommandHandler#getUserCommandList()
	 */
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}