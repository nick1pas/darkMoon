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

import com.l2jfree.Config;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.Shutdown.DisableType;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.network.serverpackets.TradeDone;

/**
 * This class represents a packet sent by the client when a player clicks either "Yes"
 * or "No" in the trade request dialog.
 */
public class AnswerTradeRequest extends L2GameClientPacket
{
    private static final String _C__ANSWERTRADEREQUEST = "[C] 55 AnswerTradeRequest c[d]";

    private boolean				_accepted;

    @Override
    protected void readImpl()
    {
        _accepted = (readD() == 1);
    }

    @Override
    protected void runImpl()
    {
        L2PcInstance player = getActiveChar();
        if (player == null)
        	return;

        if (Shutdown.isActionDisabled(DisableType.TRANSACTION))
        {
        	requestFailed(SystemMessageId.FUNCTION_INACCESSIBLE_NOW);
            return;
        }

        L2PcInstance partner = player.getActiveRequester();
        if (partner == null || L2World.getInstance().getPlayer(partner.getObjectId()) == null)
        {
            // Trade partner not found, cancel trade
            player.sendPacket(TradeDone.CANCELLED);
            player.setActiveRequester(null);
            requestFailed(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }

        //possible exploit fix
        if (player.getActiveTradeList() != null)
        {
        	partner.sendPacket(new SystemMessage(SystemMessageId.C1_ALREADY_TRADING).addString(player.getName()));
        	requestFailed(SystemMessageId.ALREADY_TRADING);
        	return;
        }
        else if (partner.getActiveTradeList() != null)
        {
        	partner.sendPacket(SystemMessageId.ALREADY_TRADING);
        	requestFailed(new SystemMessage(SystemMessageId.C1_ALREADY_TRADING).addString(partner.getName()));
        	return;
        }

        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
        	partner.sendPacket(SystemMessageId.CANT_TRADE_WITH_TARGET);
        	requestFailed(SystemMessageId.ACCOUNT_CANT_TRADE_ITEMS);
            return;
        }

        if (_accepted && !partner.isRequestExpired())
			player.startTrade(partner);
		else
			partner.sendPacket(new SystemMessage(SystemMessageId.C1_DENIED_TRADE_REQUEST).addString(player.getName()));

		// Clears requesting status
		player.setActiveRequester(null);
		partner.onTransactionResponse();

		sendAF();
	}

	@Override
	public String getType()
	{
		return _C__ANSWERTRADEREQUEST;
	}
}
