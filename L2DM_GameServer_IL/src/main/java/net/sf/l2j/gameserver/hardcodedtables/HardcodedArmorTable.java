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
 * This class Manages to Return all Hardcoded Armors in Source. <br>
 * 
 * @author Rayan RPG for L2EmuProject ! 
 */
public class HardcodedArmorTable
{
    /** Hero Armors Table */
    public final int WINGS_OF_DESTINY_CIRCLET = 6842;
    
    /** ARMOR */
    public final int ACADEMY_CIRCLET = 8181;
    
    private static HardcodedArmorTable _instance;
	public static HardcodedArmorTable getInstance()
	{
		if (_instance == null)
			_instance = new HardcodedArmorTable();
		return _instance;
	}
}
