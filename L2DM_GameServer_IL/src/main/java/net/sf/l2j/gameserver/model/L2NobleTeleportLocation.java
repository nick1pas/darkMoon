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

/**
 * This class ...
 *
 * @version $Revision: 1.1.3 form L2Emu $ $Date: 200/05/24 15:29:18 $
 * Author Scar69
 */
public class L2NobleTeleportLocation
{
	private int _teleId;
	private int _locX;
	private int _locY;
	private int _locZ;
	private int _price;


	/**
	 * @param id
	 */
	public void setTeleId(int id)
	{
		_teleId = id;
	}

	/**
	 * @param locX
	 */
	public void setLocX(int locX)
	{
		_locX = locX;
	}

	/**
	 * @param locY
	 */
	public void setLocY(int locY)
	{
		_locY = locY;
	}

	/**
	 * @param locZ
	 */
	public void setLocZ(int locZ)
	{
		_locZ = locZ;
	}

	/**
	 * @param locZ
	 */
	public void setPrice(int price)
	{
		_price = price;
	}


	/**
	 * @return
	 */
	public int getTeleId()
	{
		return _teleId;
	}

	/**
	 * @return
	 */
	public int getLocX()
	{
		return _locX;
	}

	/**
	 * @return
	 */
	public int getLocY()
	{
		return _locY;
	}

	/**
	 * @return
	 */
	public int getLocZ()
	{
		return _locZ;
	}

	/**
	 * @return
	 */
	public int getPrice()
	{
		return _price;
    }
}