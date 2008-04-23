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
package net.sf.l2j.gameserver.model.mapregion;

import net.sf.l2j.gameserver.model.world.L2Polygon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Noctarius
 *
 */
public class L2MapArea
{
    protected final static Log _log = LogFactory.getLog(L2MapArea.class.getName());

    private final int _tileHeightWidth = 32768;

    private int _id = 0;
    private int _restartId = 0;
    
    private int _X = 0;
    private int _Y = 0;
    
    private L2MapRegion _region = null;
    
    public L2MapArea(int restartId, int x, int y)
    {
    	_id = (int)((System.nanoTime() + x) / (y * 256));
    	
    	_restartId = restartId;
    	_X = (x - 15) * _tileHeightWidth - 164608;
    	_Y = (y - 10) * _tileHeightWidth - 262144;
    	
    	L2Polygon poly = new L2Polygon();
    	poly.addPoint(_X, _Y);
    	poly.addPoint(_X + _tileHeightWidth, _Y);
    	poly.addPoint(_X + _tileHeightWidth, _Y +_tileHeightWidth);
    	poly.addPoint(_X, _Y + _tileHeightWidth);
    	
    	_region = new L2MapRegion(_id, _restartId, poly, this);
    }
    
    public int getId()
    {
    	return _id;
    }
    
    public int getRestartId()
    {
    	return _restartId;
    }
    
    public boolean checkIfInRegion(int x, int y)
    {
    	return (x >= _X &&
    			x <= (_X + _tileHeightWidth) &&
    			y >= _Y &&
    			y <= (_Y + _tileHeightWidth));
    }
    
    public L2MapRegion getMapRegion()
    {
    	return _region;
    }
}
