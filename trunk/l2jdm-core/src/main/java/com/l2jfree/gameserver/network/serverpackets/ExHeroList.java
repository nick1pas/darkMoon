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

import java.util.Map;

import com.l2jfree.gameserver.model.entity.Hero;
import com.l2jfree.gameserver.model.olympiad.Olympiad;
import com.l2jfree.gameserver.templates.StatsSet;

/**
 * @author -Wooden-
 * Format from KenM<br>
 * Re-written by godson
 */
public class ExHeroList extends L2GameServerPacket
{
	private static final String		_S__FE_79_EXHEROLIST	= "[S] FE:79 ExHeroList [d(sdsdsdd)]";
	private final Map<Integer, StatsSet>	_heroList;

	public ExHeroList()
	{
		_heroList = Hero.getInstance().getHeroes();
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x79);

		writeD(_heroList.size());
		for (Integer heroId : _heroList.keySet())
		{
			StatsSet hero = _heroList.get(heroId);
			writeS(hero.getString(Olympiad.CHAR_NAME));
			writeD(hero.getInteger(Olympiad.CLASS_ID));
			writeS(hero.getString(Hero.CLAN_NAME, ""));
			writeD(hero.getInteger(Hero.CLAN_CREST, 0));
			writeS(hero.getString(Hero.ALLY_NAME, ""));
			writeD(hero.getInteger(Hero.ALLY_CREST, 0));
			writeD(hero.getInteger(Hero.COUNT));
		}
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__FE_79_EXHEROLIST;
	}
}