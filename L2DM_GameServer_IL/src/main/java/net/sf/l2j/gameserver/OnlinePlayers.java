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
package net.sf.l2j.gameserver;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2World;

public class OnlinePlayers
{
    private static OnlinePlayers _instance;

    class AnnounceOnline implements Runnable
    {
        public void run()
        {
        	if (L2World.getInstance().getAllPlayers().size() == 1)
        		Announcements.getInstance().announceToAll("There is: "+L2World.getInstance().getAllPlayers().size()+" online player.");
        	else
        		Announcements.getInstance().announceToAll("There are: "+L2World.getInstance().getAllPlayers().size()+" online players.");
            ThreadPoolManager.getInstance().scheduleGeneral(new AnnounceOnline(), Config.ONLINE_PLAYERS_ANNOUNCE_INTERVAL);
        }        
    }
    
    public static OnlinePlayers getInstance() 
    {
        if ( _instance == null ) 
            _instance = new OnlinePlayers();
        return _instance;
    }
    private OnlinePlayers()
    {
        ThreadPoolManager.getInstance().scheduleGeneral(new AnnounceOnline(), Config.ONLINE_PLAYERS_ANNOUNCE_INTERVAL);
    }
}