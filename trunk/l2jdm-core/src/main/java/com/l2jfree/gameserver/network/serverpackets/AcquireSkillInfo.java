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

import javolution.util.FastList;

public class AcquireSkillInfo extends L2GameServerPacket
{
    private static final String _S__91_AQUIRESKILLINFO = "[S] 91 AquireSkillInfo [dddd d (dddd)]";
    private final FastList<Req> _reqs;
    private final int _id, _level, _spCost, _mode;
    
	private class Req
    {
		public int itemId;
		public int count;
		public int type;
		public int unk;
        
		public Req(int pType, int pItemId, int pCount, int pUnk)
        {
			itemId = pItemId;
			type = pType;
			count = pCount;
			unk = pUnk;
        }
    }

    public AcquireSkillInfo(int id, int level, int spCost, int mode)
    {
        _reqs = new FastList<Req>();
        _id = id;
        _level = level;
        _spCost = spCost;
        _mode = mode;
    }
    
    public void addRequirement(int type, int id, int count, int unk)
    {
        _reqs.add(new Req(type, id, count, unk));
    }
    
    @Override
    protected final void writeImpl()
    {
        writeC(0x91);
        writeD(_id);
        writeD(_level);
        writeD(_spCost);
        writeD(_mode); //c4
        
        writeD(_reqs.size());

        for (Req temp : _reqs)
        {
			writeD(temp.type);
			writeD(temp.itemId);
			writeCompQ(temp.count);
			writeD(temp.unk);
        }
    }
    
    /* (non-Javadoc)
     * @see com.l2jfree.gameserver.serverpackets.ServerBasePacket#getType()
     */
    @Override
    public String getType()
    {
        return _S__91_AQUIRESKILLINFO;
    }
}
