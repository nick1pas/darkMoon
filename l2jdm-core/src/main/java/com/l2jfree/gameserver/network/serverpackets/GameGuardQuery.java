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

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.L2GameClient;

/**
 * @author zabbix
 * Lets drink to code!
 */
public class GameGuardQuery extends StaticPacket
{
    private static final String _S__F9_GAMEGUARDQUERY = "[S] F9 GameGuardQuery";

    public static final GameGuardQuery STATIC_PACKET = new GameGuardQuery();

    private GameGuardQuery()
    {
    }
    
    @Override
    public void prepareToSend(final L2GameClient client, final L2PcInstance activeChar)
    {
        // Lets make user as gg-unauthorized
        // We will set him as ggOK after reply fromclient
        // or kick
        client.setGameGuardOk(false);
    }
    
    @Override
    public void writeImpl()
    {
        writeC(0x74);
    }
    
    @Override
    public String getType()
    {
        return _S__F9_GAMEGUARDQUERY;
    }
}
