/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.clientpackets;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSetAllyCrest extends L2GameClientPacket
{
    private static final String _C__87_REQUESTSETALLYCREST = "[C] 87 RequestSetAllyCrest";
    static Log _log = LogFactory.getLog(RequestSetAllyCrest.class.getName());
            
    private int _length;
    
    private byte[] _data;
    
    @Override
    protected void readImpl()
    {
        _length  = readD();
        if (_length < 0 || _length > 192)
            return;

        _data = new byte[_length];
        readB(_data);
    }

    @Override
    protected void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (_length < 0)
        {
            activeChar.sendMessage("File transfer error.");
            return;
        }

        if (_length > 192)
        {
            activeChar.sendMessage("The crest file size was too big (max 192 bytes).");
            return;
        }

        if (activeChar.getAllyId() != 0)
        {   
            L2Clan leaderclan = ClanTable.getInstance().getClan(activeChar.getAllyId());
            
            if (activeChar.getClanId() != leaderclan.getClanId() || !activeChar.isClanLeader())
            {   
	 			// [L2J_JP ADD]
				SystemMessage sm = new SystemMessage(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
				activeChar.sendPacket(sm);
                return;
            }
            
            CrestCache crestCache = CrestCache.getInstance();
            
            int newId = IdFactory.getInstance().getNextId();
            
            if (!crestCache.saveAllyCrest(newId,_data))
            {
                _log.info( "Error loading crest of ally:" + leaderclan.getAllyName());
                return;
            }
            
            if (leaderclan.getAllyCrestId() != 0)
            {
                crestCache.removeAllyCrest(leaderclan.getAllyCrestId());
            }
            
            java.sql.Connection con = null;
            
            try
            {
                con = L2DatabaseFactory.getInstance().getConnection(con);
                PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_crest_id = ? WHERE ally_id = ?");
                statement.setInt(1, newId);
                statement.setInt(2, leaderclan.getAllyId());
                statement.executeUpdate();
                statement.close();
            }
            catch (SQLException e)
            {
                _log.warn("could not update the ally crest id:"+e.getMessage());
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }
            
            
            for (L2Clan clan : ClanTable.getInstance().getClans())
            {
                if (clan.getAllyId() == activeChar.getAllyId())
                {
                    clan.setAllyCrestId(newId);
                    for (L2PcInstance member : clan.getOnlineMembers(""))
                        member.broadcastUserInfo();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
     */
    @Override
    public String getType()
    {
        return _C__87_REQUESTSETALLYCREST;
    }
}
