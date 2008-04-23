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

import net.sf.l2j.gameserver.model.L2FriendList;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 *  sample
 *  5F 
 *  01 00 00 00
 * 
 *  format  cdd
 * 
 * 
 * @version $Revision: 1.7.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerFriendInvite extends L2GameClientPacket
{
	private static final String _C__5F_REQUESTANSWERFRIENDINVITE = "[C] 5F RequestAnswerFriendInvite";
	
	private int _response;
    
    @Override
    protected void readImpl()
    {
		_response = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)  
			return;
		
		SystemMessage sm;
		
    	L2PcInstance requestor = activeChar.getActiveRequester();
    		
    	if (requestor == null)
    	{
    		sm = new SystemMessage(SystemMessageId.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
    		activeChar.sendPacket(sm);
    		sm = null;
    		return;
    	}
    	if (_response == 1) 
        {
    		sm = new SystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_INVITING_FRIEND);
    		requestor.sendPacket(sm);
			
    		L2FriendList.addToFriendList(requestor, activeChar);
    		
    		//Player added to requester friends list.                
    		sm = new SystemMessage(SystemMessageId.S1_ADDED_TO_FRIENDS);
    		sm.addString(activeChar.getName());
    		requestor.sendPacket(sm);
    		
   			//Requester has joined as friend.
    		sm = new SystemMessage(SystemMessageId.S1_JOINED_AS_FRIEND);
    		sm.addString(requestor.getName());
    		activeChar.sendPacket(sm);
		} 
    	else 
        {
			sm = new SystemMessage(SystemMessageId.FAILED_TO_INVITE_A_FRIEND);
			requestor.sendPacket(sm);
		}   		
		sm = null;
		activeChar.setActiveRequester(null);
		requestor.onTransactionResponse();
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__5F_REQUESTANSWERFRIENDINVITE;
	}
}
