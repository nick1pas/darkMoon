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
import com.l2jfree.gameserver.instancemanager.MercTicketManager;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;

public class ConfirmDlgAnswer extends L2GameClientPacket
{
    private static final String _C__CONFIRMDLG = "[C] C6 ConfirmDlg c[ddd]";

    private int					_messageId;
    private int					_answer;
    private int					_requesterId;

    @Override
    protected void readImpl()
    {
        _messageId = readD();
        _answer = readD();
        _requesterId = readD();
    }

    @Override
    protected void runImpl()
    {
        L2PcInstance cha = getActiveChar();
        if (cha == null)
        	return;

        if (_log.isDebugEnabled())
            _log.debug(getType() + ": Answer acepted. Message ID " + _messageId + ", answer " + _answer + ", Requester ID " + _requesterId);

        if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_C1_FOR_S2_XP.getId()
        		|| _messageId == SystemMessageId.RESURRECT_USING_CHARM_OF_COURAGE.getId())
            cha.reviveAnswer(_answer);
        else if (_messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
            cha.teleportAnswer(_answer, _requesterId);
        else if (_messageId == SystemMessageId.S1.getId() && Config.ALLOW_WEDDING && cha.isEngageRequest())
            cha.engageAnswer(_answer);
        else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
            cha.gatesAnswer(_answer, 1);
        else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
            cha.gatesAnswer(_answer, 0);
        else if (_messageId == SystemMessageId.PLACE_S1_CURRENT_LOCATION_DIRECTION.getId())
        	MercTicketManager.getInstance().addPosition(cha);

        sendAF();
    }

    @Override
    public String getType()
    {
        return _C__CONFIRMDLG;
    }
}
