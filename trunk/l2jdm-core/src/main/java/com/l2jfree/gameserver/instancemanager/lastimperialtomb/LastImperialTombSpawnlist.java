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
package com.l2jfree.gameserver.instancemanager.lastimperialtomb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

/**
*
* @author  L2J_JP SANDMAN
*/

public class LastImperialTombSpawnlist
{
    private final static Log _log = LogFactory.getLog(LastImperialTombSpawnlist.class);

    private static List<L2Spawn> _Room1SpawnList1st = new FastList<L2Spawn>();
    private static List<L2Spawn> _Room1SpawnList2nd = new FastList<L2Spawn>();
    private static List<L2Spawn> _Room1SpawnList3rd = new FastList<L2Spawn>();
    private static List<L2Spawn> _Room1SpawnList4th = new FastList<L2Spawn>();
    private static List<L2Spawn> _Room2InsideSpawnList = new FastList<L2Spawn>();
    private static List<L2Spawn> _Room2OutsideSpawnList = new FastList<L2Spawn>();

    private LastImperialTombSpawnlist()
    {
    }

	public static LastImperialTombSpawnlist getInstance()
	{
		return SingletonHolder._instance;
	}

    public void fill()
    {
        Connection con = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("SELECT * FROM lastimperialtomb_spawnlist ORDER BY id");
            ResultSet rset = statement.executeQuery();

            int npcTemplateId;
            L2Spawn spawnDat;
            L2NpcTemplate npcTemplate;

            while (rset.next())
            {
            	npcTemplateId = rset.getInt("npc_templateid");
                npcTemplate = NpcTable.getInstance().getTemplate(npcTemplateId);

                if (npcTemplate != null)
                {
                    spawnDat = new L2Spawn(npcTemplate);
                    spawnDat.setId(rset.getInt("id"));
                    spawnDat.setAmount(rset.getInt("count"));
                    spawnDat.setLocx(rset.getInt("locx"));
                    spawnDat.setLocy(rset.getInt("locy"));
                    spawnDat.setLocz(rset.getInt("locz"));
                    spawnDat.setHeading(rset.getInt("heading"));
                    spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));

                    switch(npcTemplateId)
                    {
	                    case 18328:
	                    case 18330:
	                    case 18332:
	                    	_Room1SpawnList1st.add(spawnDat);
	                    	break;

	                    case 18329:
	                    	_Room1SpawnList2nd.add(spawnDat);
	                    	break;

	                    case 18333:
	                    	_Room1SpawnList3rd.add(spawnDat);
	                    	break;

	                    case 18331:
	                    	_Room1SpawnList4th.add(spawnDat);
	                    	break;

	                    case 18339:
	                    	_Room2InsideSpawnList.add(spawnDat);
	                    	break;

	                    case 18334:
	                    case 18335:
	                    case 18336:
	                    case 18337:
	                    case 18338:
	                    	_Room2OutsideSpawnList.add(spawnDat);
	                    	break;
                    }
                }
                else
                {
                    _log.warn("LastImperialTombSpawnlist: Data missing in NPC table for ID: " + npcTemplateId + ".");
                }
            }

            rset.close();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warn("LastImperialTombSpawnlist: Spawn could not be initialized: ", e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }

        _log.info("LastImperialTombSpawnlist: Loaded " + _Room1SpawnList1st.size() + " Room1 1st Npc Spawn Locations.");
        _log.info("LastImperialTombSpawnlist: Loaded " + _Room1SpawnList2nd.size() + " Room1 2nd Npc Spawn Locations.");
        _log.info("LastImperialTombSpawnlist: Loaded " + _Room1SpawnList3rd.size() + " Room1 3rd Npc Spawn Locations.");
        _log.info("LastImperialTombSpawnlist: Loaded " + _Room1SpawnList4th.size() + " Room1 4th Npc Spawn Locations.");
        _log.info("LastImperialTombSpawnlist: Loaded " + _Room2InsideSpawnList.size() + " Room2 Inside Npc Spawn Locations.");
        _log.info("LastImperialTombSpawnlist: Loaded " + _Room2OutsideSpawnList.size() + " Room2 Outside Npc Spawn Locations.");
    }

    public void clear()
    {
    	_Room1SpawnList1st.clear();
    	_Room1SpawnList2nd.clear();
    	_Room1SpawnList3rd.clear();
    	_Room1SpawnList4th.clear();
    	_Room2InsideSpawnList.clear();
    	_Room2OutsideSpawnList.clear();
    }

    public List<L2Spawn> getRoom1SpawnList1st()
    {
    	return _Room1SpawnList1st;
    }

    public List<L2Spawn> getRoom1SpawnList2nd()
    {
    	return _Room1SpawnList2nd;
    }

    public List<L2Spawn> getRoom1SpawnList3rd()
    {
    	return _Room1SpawnList3rd;
    }

    public List<L2Spawn> getRoom1SpawnList4th()
    {
    	return _Room1SpawnList4th;
    }

    public List<L2Spawn> getRoom2InsideSpawnList()
    {
    	return _Room2InsideSpawnList;
    }

    public List<L2Spawn> getRoom2OutsideSpawnList()
    {
    	return _Room2OutsideSpawnList;
    }

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final LastImperialTombSpawnlist _instance = new LastImperialTombSpawnlist();
	}
}
