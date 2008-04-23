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
package net.sf.l2j.gameserver.hardcodedtables;

/**
 * Class Dedicated to Remove From Source all Ids <br>
 * making easier to change all stuff in a unique class. <br>
 *
 * @author  Rayan RPG For L2Emu Project 
 */
public class HardcodedMultisellist
{
   /*
	317325001.xml -> Adventure Guildsman: Use Life Crystal Schuttgart
	317325002.xml -> Adventure Guildsman: Adventurer's Box
	318055002.xml -> Adventure Guildsman: Use Life Crystal Giran
	318145003.xml -> Adventure Guildsman: Use Life Crystal Oren
	318275004.xml -> Adventure Guildsman: Use Life Crystal Aden
	318375005.xml -> Adventure Guildsman: Use Life Crystal Goddard
	318235006.xml -> Adventure Guildsman: Use Life Crystal Hunters Village
	318195007.xml -> Adventure Guildsman: Use Life Crystal Heine
	318335008.xml -> Adventure Guildsman: Use Life Crystal Rune
	317885009.xml -> Adventure Guildsman: Use Life Crystal Gludin
	317925010.xml -> Adventure Guildsman: Use Life Crystal Gludio
	317975011.xml -> Adventure Guildsman: Use Life Crystal Dion*/

    /** Adventure Guildsman Multisell Table */
    public static final int ADVENTURER_BOX = 317325002;
    public static final int SCHUTTGART_LIFE_CRYSTAL = 317325001;
    public static final int GIRAN_LIFE_CRYSTAL = 318055002;
    public static final int OREN_LIFE_CRYSTAL = 318145003;
    public static final int ADEN_LIFE_CRYSTAL = 318275004;
    public static final int GODDARD_LIFE_CRYSTAL = 318375005;
    public static final int HUNTERS_VILLAGE_LIFE_CRYSTAL = 318235006;
    public static final int HEINE_LIFE_CRYSTAL = 318195007;
    public static final int RUNE_LIFE_CRYSTAL = 318335008;
    public static final int GLUDIN_LIFE_CRYSTAL = 317885009;
    public static final int GLUDIO_LIFE_CRYSTAL = 317925010;
    public static final int DION_LIFE_CRYSTAL = 317975011;
    
    //** Olympiad Manager Multisell Table */
    public static final int OLYMPIAD_MANAGER = 102;
    
    
    private  HardcodedMultisellist _instance;
	public  HardcodedMultisellist getInstance()
	{
		if (_instance == null)
			_instance = new HardcodedMultisellist();
		return _instance;
	}
}