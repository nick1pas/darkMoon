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

import com.l2jfree.gameserver.model.actor.L2Npc;

/**
 *
 * @author Kerberos
 */
public final class NpcSay extends L2GameServerPacket
{
	// dddS
	private static final String _S__30_NPCSAY = "[S] 30 NpcSay";
	private final int _objectId;
	private final int _textType;
	private final int _npcId;
	private final String _text;

	public NpcSay(int objectId, int messageType, int npcId, String text)
	{
		_objectId = objectId;
		_textType = messageType;
		_npcId = 1000000 + npcId;
		_text = text;
	}

	public NpcSay(L2Npc npc, String text)
	{
		_objectId = npc.getObjectId();
		_textType = 0;
		_npcId = 1000000 + npc.getNpcId();
		_text = text;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x30);
		writeD(_objectId);
		writeD(_textType);
		writeD(_npcId);
		writeS(_text);
	}

	@Override
	public String getType()
	{
		return _S__30_NPCSAY;
	}
}
