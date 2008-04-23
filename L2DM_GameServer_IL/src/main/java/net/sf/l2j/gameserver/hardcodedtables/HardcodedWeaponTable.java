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
 * This Class Handles List of All HardCoded Weapons IDs in Source.<br>
 *  @author  Rayan RPG For L2Emu Project !
 */
public class HardcodedWeaponTable
{
    /** Cursed Weapon */
    public final int ZARICHE = 8190;
    
    /** Hero Weapons Table */
    public final int INFINITY_BLADE = 6611;
    public final int INFINITY_CLEAVER = 6612;
    public final int INFINITY_AXE = 6613;
    public final int INFINITY_ROD = 6614;
    public final int INFINITY_CRUSHER = 6615;
    public final int INFINITY_SCEPTER = 6616;
    public final int INFINITY_STINGER = 6617;
    public final int INFINITY_FANG  = 6618;
    public final int INFINITY_BOWE = 6619;
    public final int INFINITY_WING = 6620;
    public final int INFINITY_SPEAR = 6621;
    
    private static HardcodedWeaponTable _instance;
	public static HardcodedWeaponTable getInstance()
	{
		if (_instance == null)
			_instance = new HardcodedWeaponTable();
		return _instance;
	}
}