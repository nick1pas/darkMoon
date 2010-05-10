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

import com.l2jfree.gameserver.network.SystemMessageId;

/**
 * Format: (ch)
 * just a trigger
 * @author -Wooden-
 */
public class RequestExFishRanking extends L2GameClientPacket
{
    private static final String _C__D0_1F_REQUESTEXFISHRANKING = "[C] D0:1F RequestExFishRanking";

    @Override
    protected void readImpl()
    {
    }

    @Override
    protected void runImpl()
    {
    	//TODO: implement? deprecated?
        _log.debug("C5: RequestExFishRanking");
        requestFailed(SystemMessageId.NOT_WORKING_PLEASE_TRY_AGAIN_LATER);
    }

    @Override
    public String getType()
    {
        return _C__D0_1F_REQUESTEXFISHRANKING;
    }
}
