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
package net.sf.l2j.gameserver.model;

import javolution.util.FastList;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.util.Util;

public class L2SiegeClan
{

	private int _clanId                = 0;
	private FastList<L2NpcInstance> _flag  = new FastList<L2NpcInstance>();
	private int _numFlagsAdded = 0;
	private SiegeClanType _type;

	public enum SiegeClanType
	{
		OWNER,
		DEFENDER,
		ATTACKER,
		DEFENDER_PENDING
	}

	public L2SiegeClan(int clanId, SiegeClanType type)
	{
		_clanId = clanId;
		_type = type;
	}

	public int getNumFlags()
	{
		return _numFlagsAdded;
	}

	public void addFlag(L2NpcInstance flag) 
	{	
		_numFlagsAdded++;
		getFlag().add(flag);
	}

	public boolean removeFlag(L2NpcInstance flag)
	{
		if (flag == null) return false;
		boolean ret = getFlag().remove(flag);
		//flag.deleteMe();
		//check if null objects or dups remain in the list.
		//for some reason, this might be happenning sometimes...
		// delete false dupplicates: if this flag got deleted, delete its copies too.
		if (ret)
			while (getFlag().remove(flag)) ;

		// now delete nulls
		int n;
		boolean more = true;
		while (more)
		{
			more = false;
			n = getFlag().size();
			if (n>0)
				for(int i=0; i<n;i++)
					if(getFlag().get(i)==null)
					{
						getFlag().remove(i);
						more = true;
						break;
					}
		}

		flag.deleteMe();
		return ret;
	}

	public void removeFlags()
	{
		for (L2NpcInstance flag: getFlag())
			removeFlag(flag);
	}

	public final int getClanId() { return _clanId; }

	public final FastList<L2NpcInstance> getFlag()
	{
		if (_flag == null) _flag  = new FastList<L2NpcInstance>();
		return _flag;
	}

	/*** get nearest Flag to Object ***/
	public final L2NpcInstance getClosestFlag(L2Object obj)
	{
		double closestDistance = Double.MAX_VALUE;
		double distance;
		L2NpcInstance _flag = null;

		for (L2NpcInstance flag: getFlag())
		{
			if (flag  == null)
				continue;
			distance = Util.calculateDistance(obj.getX(), obj.getY(), obj.getZ(), flag.getX(), flag.getX(), flag.getZ(), true);
			if (closestDistance > distance)
			{
				closestDistance = distance;
				_flag = flag;
			}
		}
		return _flag;
	}
	
	public SiegeClanType getType() { return _type; }
    
    public void setType(SiegeClanType setType) { _type = setType; } 
}
