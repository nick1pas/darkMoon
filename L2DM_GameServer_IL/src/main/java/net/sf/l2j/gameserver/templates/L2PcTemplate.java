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
package net.sf.l2j.gameserver.templates;

import java.util.List;

import javolution.util.FastList;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.base.Race;

/**
 * Base template for all type of playable characters 
 * Override {@link net.sf.l2j.gameserver.templates.L2CharTemplate} to add some properties
 * specific to pc. 
 *
 * <br/>
 * <br/>
 * <font color="red">
 * <b>Property don't change in the time, this is just a template, not the currents status 
 * of characters !</b>
 * </font>
 */
public class L2PcTemplate extends L2CharTemplate {
	
	/** The Class object of the L2PcInstance */
	private ClassId classId;
	
	private Race   race;
	private String className;
	
	private int    spawnX;
	private int    spawnY;
	private int    spawnZ;
	
	private int     classBaseLevel;
	private float   lvlHpAdd;
	private float   lvlHpMod;
	private float   lvlCpAdd;
	private float   lvlCpMod;
	private float   lvlMpAdd;
	private float   lvlMpMod;
	
	private List<L2Item> _items = new FastList<L2Item>();
	
	
	public L2PcTemplate(StatsSet set)
	{
		super(set);
		classId   = ClassId.values()[set.getInteger("classId")];
		race      = Race.values()[set.getInteger("raceId")];
		className = set.getString("className");
		
		spawnX    = set.getInteger("spawnX");
		spawnY    = set.getInteger("spawnY");
		spawnZ    = set.getInteger("spawnZ");
		
		classBaseLevel = set.getInteger("classBaseLevel");
		lvlHpAdd  = set.getFloat("lvlHpAdd");
		lvlHpMod  = set.getFloat("lvlHpMod");
        lvlCpAdd  = set.getFloat("lvlCpAdd");
        lvlCpMod  = set.getFloat("lvlCpMod");
		lvlMpAdd  = set.getFloat("lvlMpAdd");
		lvlMpMod  = set.getFloat("lvlMpMod");
	}
	
	/**
	 * add starter equipment
	 * @param i
	 */
	public void addItem(int itemId)
	{
		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item != null)
			_items.add(item);
	}
	
	/**
	 *
	 * @return itemIds of all the starter equipment
	 */
	public L2Item[] getItems()
	{
		return _items.toArray(new L2Item[_items.size()]);
	}

    /**
     * @return the classBaseLevel
     */
    public int getClassBaseLevel()
    {
        return classBaseLevel;
    }

    /**
     * @param classBaseLevel the classBaseLevel to set
     */
    public void setClassBaseLevel(int _classBaseLevel)
    {
        classBaseLevel = _classBaseLevel;
    }

    /**
     * @return the classId
     */
    public ClassId getClassId()
    {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(ClassId _classId)
    {
        classId = _classId;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String _className)
    {
        className = _className;
    }

    /**
     * @return the lvlCpAdd
     */
    public float getLvlCpAdd()
    {
        return lvlCpAdd;
    }

    /**
     * @param lvlCpAdd the lvlCpAdd to set
     */
    public void setLvlCpAdd(float _lvlCpAdd)
    {
        lvlCpAdd = _lvlCpAdd;
    }

    /**
     * @return the lvlCpMod
     */
    public float getLvlCpMod()
    {
        return lvlCpMod;
    }

    /**
     * @param lvlCpMod the lvlCpMod to set
     */
    public void setLvlCpMod(float _lvlCpMod)
    {
        lvlCpMod = _lvlCpMod;
    }

    /**
     * @return the lvlHpAdd
     */
    public float getLvlHpAdd()
    {
        return lvlHpAdd;
    }

    /**
     * @param lvlHpAdd the lvlHpAdd to set
     */
    public void setLvlHpAdd(float _lvlHpAdd)
    {
        lvlHpAdd = _lvlHpAdd;
    }

    /**
     * @return the lvlHpMod
     */
    public float getLvlHpMod()
    {
        return lvlHpMod;
    }

    /**
     * @param lvlHpMod the lvlHpMod to set
     */
    public void setLvlHpMod(float _lvlHpMod)
    {
        lvlHpMod = _lvlHpMod;
    }

    /**
     * @return the lvlMpAdd
     */
    public float getLvlMpAdd()
    {
        return lvlMpAdd;
    }

    /**
     * @param lvlMpAdd the lvlMpAdd to set
     */
    public void setLvlMpAdd(float _lvlMpAdd)
    {
        lvlMpAdd = _lvlMpAdd;
    }

    /**
     * @return the lvlMpMod
     */
    public float getLvlMpMod()
    {
        return lvlMpMod;
    }

    /**
     * @param lvlMpMod the lvlMpMod to set
     */
    public void setLvlMpMod(float _lvlMpMod)
    {
        lvlMpMod = _lvlMpMod;
    }

    /**
     * @return the race
     */
    public Race getRace()
    {
        return race;
    }

    /**
     * @param race the race to set
     */
    public void setRace(Race _race)
    {
        race = _race;
    }

    /**
     * @return the spawnX
     */
    public int getSpawnX()
    {
        return spawnX;
    }

    /**
     * @param spawnX the spawnX to set
     */
    public void setSpawnX(int _spawnX)
    {
        spawnX = _spawnX;
    }

    /**
     * @return the spawnY
     */
    public int getSpawnY()
    {
        return spawnY;
    }

    /**
     * @param spawnY the spawnY to set
     */
    public void setSpawnY(int _spawnY)
    {
        spawnY = _spawnY;
    }

    /**
     * @return the spawnZ
     */
    public int getSpawnZ()
    {
        return spawnZ;
    }

    /**
     * @param spawnZ the spawnZ to set
     */
    public void setSpawnZ(int _spawnZ)
    {
        spawnZ = _spawnZ;
    }
}
