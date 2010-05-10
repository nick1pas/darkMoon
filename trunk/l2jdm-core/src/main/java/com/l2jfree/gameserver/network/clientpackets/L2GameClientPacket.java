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

import java.nio.BufferUnderflowException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.L2GameClient;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jfree.mmocore.network.InvalidPacketException;
import com.l2jfree.mmocore.network.ReceivablePacket;

/**
 * Packets received by the game server from clients
 * 
 * @author KenM
 */
public abstract class L2GameClientPacket extends ReceivablePacket<L2GameClient, L2GameClientPacket, L2GameServerPacket>
{
	protected static final Log _log = LogFactory.getLog(L2GameClientPacket.class);
	
	protected L2GameClientPacket()
	{
	}
	
	@Override
	protected final boolean read() throws BufferUnderflowException, RuntimeException
	{
		readImpl();
		return true;
	}
	
	protected abstract void readImpl() throws BufferUnderflowException, RuntimeException;
	
	/**
	 * Spawn protect removal moved to subclasses, take care of it.<br>
	 * <ul>
	 * <li>Action (if it's the second click on the target)</li>
	 * <li>AttackRequest</li>
	 * <li>MoveBackwardToLocation</li>
	 * <li>RequestActionUse</li>
	 * <li>RequestMagicSkillUse</li>
	 * </ul>
	 * It could include pickup and talk too, but less is better.
	 */
	@Override
	protected abstract void runImpl() throws InvalidPacketException, RuntimeException;
	
	protected final void sendPacket(SystemMessageId sm)
	{
		getClient().sendPacket(sm.getSystemMessage());
	}
	
	protected final L2PcInstance getActiveChar()
	{
		return getClient().getActiveChar();
	}
	
	protected final void requestFailed(SystemMessageId sm)
	{
		requestFailed(sm.getSystemMessage());
	}
	
	protected final void requestFailed(L2GameServerPacket gsp)
	{
		sendPacket(gsp);
		sendAF();
	}
	
	protected final void sendAF()
	{
		sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	protected final long readCompQ()
	{
		if (Config.PACKET_FINAL)
			return readQ();
		else
			return readD();
	}
}
