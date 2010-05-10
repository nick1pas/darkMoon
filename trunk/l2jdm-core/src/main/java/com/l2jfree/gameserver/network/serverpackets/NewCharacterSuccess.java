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

import java.util.ArrayList;

import com.l2jfree.gameserver.datatables.CharTemplateTable;
import com.l2jfree.gameserver.model.base.ClassId;
import com.l2jfree.gameserver.templates.chars.L2PcTemplate;

public class NewCharacterSuccess extends StaticPacket
{
	private static final String				_S__NEWCHARACTERSUCCESS	= "[S] 0D NewCharacterSuccess c[d->dddddddddddddddddddd<-]";
	private static final int[]				DELIMITER				= { 0x46, 0x0A };
	public static final NewCharacterSuccess	PACKET					= new NewCharacterSuccess();
	static
	{
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(0));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.HumanFighter));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.HumanMystic));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ElvenFighter));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ElvenMystic));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DarkFighter));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DarkMystic));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.OrcFighter));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.OrcMystic));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DwarvenFighter));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.MaleSoldier));
		PACKET.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.FemaleSoldier));
		PACKET.finish();
	}

	private final ArrayList<L2PcTemplate>	_chars = new ArrayList<L2PcTemplate>();

	private NewCharacterSuccess()
	{
	}

	public void addChar(L2PcTemplate template)
	{
		_chars.add(template);
	}

	public void finish()
	{
		_chars.trimToSize();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x0D);
		writeD(_chars.size());

		for (int i = 0; i < _chars.size(); i++)
		{
			L2PcTemplate temp = _chars.get(i);
			if (temp == null)
				continue;

			writeD(temp.getRace().ordinal());
			writeD(temp.getClassId().getId());
			writeD(DELIMITER[0]);
			writeD(temp.getBaseSTR());
			writeD(DELIMITER[1]);
			writeD(DELIMITER[0]);
			writeD(temp.getBaseDEX());
			writeD(DELIMITER[1]);
			writeD(DELIMITER[0]);
			writeD(temp.getBaseCON());
			writeD(DELIMITER[1]);
			writeD(DELIMITER[0]);
			writeD(temp.getBaseINT());
			writeD(DELIMITER[1]);
			writeD(DELIMITER[0]);
			writeD(temp.getBaseWIT());
			writeD(DELIMITER[1]);
			writeD(DELIMITER[0]);
			writeD(temp.getBaseMEN());
			writeD(DELIMITER[1]);
		}
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__NEWCHARACTERSUCCESS;
	}
}
