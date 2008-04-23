/* This program is free software; you can redistribute it and/or modify
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

import net.sf.l2j.gameserver.model.L2ItemInstance.ItemLocation;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class PcWarehouse extends Warehouse
{
    //private static final Log _log = LogFactory.getLog(PcWarehouse.class.getName());

    private L2PcInstance _owner;

    public PcWarehouse(L2PcInstance owner)
    {
        _owner = owner;
    }

    @Override
    public L2PcInstance getOwner() { return _owner; }
    @Override
    public ItemLocation getBaseLocation() { return ItemLocation.WAREHOUSE; }
    public String getLocationId() { return "0"; }
    public int getLocationId(@SuppressWarnings("unused") boolean dummy) { return 0; }
    public void setLocationId(@SuppressWarnings("unused") L2PcInstance dummy) { return; }

    @Override
    public boolean validateCapacity(int slots)
    {
        return (_items.size() + slots <= _owner.getWareHouseLimit());
    }
}