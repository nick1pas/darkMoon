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

import com.l2jfree.gameserver.model.actor.L2Summon;

/**
 *
 * @author  KenM
 */
public class ExPartyPetWindowDelete extends L2GameServerPacket
{
    private final static String S_FE_6A_EXPARTYPETWINDOWDELETE = "[S] FE:6a ExPartyPetWindowDelete";
    private final L2Summon _summon;
    
    public ExPartyPetWindowDelete(L2Summon summon)
    {
        _summon = summon;
    }
    /**
     * @see com.l2jfree.gameserver.serverpackets.L2GameServerPacket#getType()
     */
    @Override
    public String getType()
    {
        return S_FE_6A_EXPARTYPETWINDOWDELETE;
    }

    /**
     * @see com.l2jfree.gameserver.serverpackets.L2GameServerPacket#writeImpl()
     */
    @Override
    protected void writeImpl()
    {
        writeC(0xfe);
        writeH(0x6a);
        writeD(_summon.getObjectId());
        writeD(_summon.getOwner().getObjectId());
        writeS(_summon.getName());
    }
    
}
