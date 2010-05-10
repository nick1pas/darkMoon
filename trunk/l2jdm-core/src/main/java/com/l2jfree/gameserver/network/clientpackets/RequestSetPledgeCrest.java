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
package com.l2jfree.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.cache.CrestCache;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.model.L2Clan;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSetPledgeCrest extends L2GameClientPacket
{
	private static final String	_C__53_REQUESTSETPLEDGECREST	= "[C] 53 RequestSetPledgeCrest";

	private int					_length;
	private byte[]				_data;

	@Override
	protected void readImpl()
	{
		_length = readD();
		if (_length < 0 || _length > 256)
			return;

		_data = new byte[_length];
		readB(_data);
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getActiveChar();
		if (activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if (!L2Clan.checkPrivileges(activeChar, L2Clan.CP_CL_REGISTER_CREST))
		{
			requestFailed(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		else if (clan.getLevel() < 3)
		{
			requestFailed(SystemMessageId.CLAN_LVL_3_NEEDED_TO_SET_CREST);
			return;
		}
		else if (clan.getDissolvingExpiryTime() > 0)
		{
			requestFailed(SystemMessageId.CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS);
			return;
		}

		if (_length < 0 || _length > 256)
		{
			requestFailed(SystemMessageId.INVALID_INSIGNIA_FORMAT);
			return;
		}

		CrestCache crestCache = CrestCache.getInstance();

		if (_length == 0 || _data.length == 0)
		{
			crestCache.removePledgeCrest(clan.getCrestId());
			clan.setHasCrest(false);
			sendPacket(SystemMessageId.CLAN_CREST_HAS_BEEN_DELETED);

			for (L2PcInstance member : clan.getOnlineMembers(0))
				member.broadcastUserInfo();
		}
		else
		{
			int newId = IdFactory.getInstance().getNextId();
			if (!crestCache.savePledgeCrest(newId, _data))
			{
				//all lies, the problem is server-side :D
				requestFailed(SystemMessageId.INVALID_INSIGNIA_COLOR);
				_log.warn("Error saving crest of clan:" + clan.getName());
				return;
			}

			if (clan.hasCrest())
				crestCache.removeOldPledgeCrest(clan.getCrestId());

			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?");
				statement.setInt(1, newId);
				statement.setInt(2, clan.getClanId());
				statement.executeUpdate();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.warn("could not update the crest id:", e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}

			clan.setCrestId(newId);
			clan.setHasCrest(true);

			for (L2PcInstance member : clan.getOnlineMembers(0))
				member.broadcastUserInfo();
		}

		sendAF();
	}

	@Override
	public String getType()
	{
		return _C__53_REQUESTSETPLEDGECREST;
	}
}
