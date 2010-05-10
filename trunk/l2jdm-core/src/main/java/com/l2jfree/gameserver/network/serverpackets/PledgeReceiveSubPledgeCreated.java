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
package com.l2jfree.gameserver.network.serverpackets;

import com.l2jfree.gameserver.model.L2Clan;
import com.l2jfree.gameserver.model.L2ClanMember;
import com.l2jfree.gameserver.model.L2Clan.SubPledge;

/**
 *
 * @author  -Wooden-
 */
public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private static final String	_S__FE_3F_PLEDGERECEIVESUBPLEDGECREATED	= "[S] FE:3F PledgeReceiveSubPledgeCreated";
	private final SubPledge			_subPledge;
	private final L2Clan				_clan;

	/**
	 * @param member
	 */
	public PledgeReceiveSubPledgeCreated(SubPledge subPledge, L2Clan clan)
	{
		_subPledge = subPledge;
		_clan = clan;
	}

	/**
	 * @see com.l2jfree.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x40);

		writeD(0x01);
		writeD(_subPledge.getId());
		writeS(_subPledge.getName());
		writeS(getLeaderName());
	}
	
	private String getLeaderName()
	{
		final int leaderId = _subPledge.getLeaderId();
		
		if (_subPledge.getId() == L2Clan.SUBUNIT_ACADEMY || leaderId == 0)
			return "";
		
		final L2ClanMember leader = _clan.getClanMember(leaderId);
		
		if (leader != null)
			return leader.getName();
		
		_log.warn("SubPledgeLeader: " + leaderId + " is missing from clan: " + _clan.getName() + "[" + _clan.getClanId() + "]");
		return "";
	}

	/**
	 * @see com.l2jfree.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__FE_3F_PLEDGERECEIVESUBPLEDGECREATED;
	}

}
