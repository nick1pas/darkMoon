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
package net.sf.l2j.gameserver.datatables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.instancemanager.DayNightSpawnManager;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @author Nightmare
 * @version $Revision: 1.5.2.6.2.7 $ $Date: 2005/03/27 15:29:18 $
 */
public class SpawnTable implements SpawnTableMBean
{
    private final static Log _log = LogFactory.getLog(SpawnTable.class.getName());

    private static final SpawnTable _instance = new SpawnTable();

    private Map<Integer, L2Spawn> _spawntable = new FastMap<Integer, L2Spawn>();
    private int _npcSpawnCount;
    private int _cSpawnCount;
    private int _highestDbId;
    private int _highestCustomDbId;
    
    //L2EMU_ADD_START
    private int _jailSpawnCount;
    private int _highestJailDbId;
    //L2EMU_ADD_END
    
    public static SpawnTable getInstance()
    {
        return _instance;
    }

    private SpawnTable()
    {
        if (!Config.ALT_DEV_NO_SPAWNS)
            fillSpawnTable();
        else
            _log.info("GameServer: Spawns Disabled Developer Mode Enabled.");
    }
    
    public Map<Integer, L2Spawn> getSpawnTable()
    {
        return _spawntable;
    }
    
    private void fillSpawnTable()
    {
        java.sql.Connection con = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, loc_id, periodOfDay FROM spawnlist ORDER BY id");
            ResultSet rset = statement.executeQuery();

            L2Spawn spawnDat;
            L2NpcTemplate template1;

            while (rset.next())
            {
                template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
                if (template1 != null)
                {
                    if (template1.getType().equalsIgnoreCase("L2SiegeGuard"))
                    {
                        // Don't spawn siege guards
                    }
                    else if (template1.getType().equalsIgnoreCase("L2RaidBoss"))
                    {
                        // Don't spawn raidbosses
                    }
                    else if (!Config.SPAWN_CLASS_MASTER && template1.getType().equals("L2ClassMaster"))
                    {
                        // Dont' spawn class masters
                    }
                    else if (!Config.SPAWN_WYVERN_MANAGER && template1.getType().equals("L2WyvernManager"))
                    {
                        // Dont' spawn wyvern managers
                    }
                    else
                    {
                        spawnDat = new L2Spawn(template1);
                        spawnDat.setId(_npcSpawnCount);
                        spawnDat.setDbId(rset.getInt("id"));
                        spawnDat.setAmount(rset.getInt("count"));
                        spawnDat.setLocx(rset.getInt("locx"));
                        spawnDat.setLocy(rset.getInt("locy"));
                        spawnDat.setLocz(rset.getInt("locz"));
                        spawnDat.setHeading(rset.getInt("heading"));
                        spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
                        int loc_id = rset.getInt("loc_id");
                        spawnDat.setLocation(loc_id);                             
                        
                        switch(rset.getInt("periodOfDay")) {
                            case 0: // default
                                _npcSpawnCount += spawnDat.init();
                                break;
                            case 1: // Day
                                DayNightSpawnManager.getInstance().addDayCreature(spawnDat);
                                _npcSpawnCount++;
                                break;
                            case 2: // Night
                                DayNightSpawnManager.getInstance().addNightCreature(spawnDat);
                                _npcSpawnCount++;
                                break;     
                        }
                        
                        if (spawnDat.getDbId()>_highestDbId)_highestDbId=spawnDat.getDbId();
                        _spawntable.put(spawnDat.getId(), spawnDat);
                    }
                }
                else
                {
                    _log.warn("SpawnTable: Data missing or incorrect in NPC/Custom NPC table for ID: "
                        + rset.getInt("npc_templateid") + ".");
                }
            }
            rset.close();
            statement.close();
        }
        catch (Exception e)
        {
            // problem with initializing spawn, go to next one
            _log.warn("SpawnTable: Spawn could not be initialized: " + e);
        }
        finally
        {
            try
            {
                con.close();
                con=null;
            }
            catch (Exception e)
            {
            }
        }
        //L2EMU_EDIT_ADD_START
        _log.info("TablesManager: Loaded " + _spawntable.size() + " Npc Spawn Locations.");
        if(Config.LOAD_CUSTOM_SPAWNLIST)
        {
        	try
        	{
        		con = L2DatabaseFactory.getInstance().getConnection(con);
        		PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, loc_id, periodOfDay FROM custom_spawnlist ORDER BY id");
        		ResultSet rset = statement.executeQuery();

        		L2Spawn spawnDat;
        		L2NpcTemplate template1;

        		_cSpawnCount = _spawntable.size();

        		while (rset.next())
        		{
        			template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
        			if (template1 != null)
        			{
        				if (template1.getType().equalsIgnoreCase("L2SiegeGuard"))
        				{
        					// Don't spawn siege guards
        				}
        				else if (template1.getType().equalsIgnoreCase("L2RaidBoss"))
        				{
        					// Don't spawn raidbosses
        				}
        				else if (!Config.SPAWN_CLASS_MASTER && template1.getType().equals("L2ClassMaster"))
        				{
        					// Dont' spawn class masters
        				}
        				else if (!Config.JAIL_SPAWN_SYSTEM && template1.getType().equals("L2JailManager"))
        				{
        					// Dont' spawn jail managers if jail system is disabled.
        				}
        				else
        				{
        					spawnDat = new L2Spawn(template1);
        					spawnDat.setId(_npcSpawnCount);
        					spawnDat.setDbId(rset.getInt("id"));
        					spawnDat.setAmount(rset.getInt("count"));
        					spawnDat.setLocx(rset.getInt("locx"));
        					spawnDat.setLocy(rset.getInt("locy"));
        					spawnDat.setLocz(rset.getInt("locz"));
        					spawnDat.setHeading(rset.getInt("heading"));
        					spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
        					spawnDat.setCustom();
        					int loc_id = rset.getInt("loc_id");
        					spawnDat.setLocation(loc_id);                             

        					switch(rset.getInt("periodOfDay")) {
        					case 0: // default
        						_npcSpawnCount += spawnDat.init();
        						break;
        					case 1: // Day
        						DayNightSpawnManager.getInstance().addDayCreature(spawnDat);
        						_npcSpawnCount++;
        						break;
        					case 2: // Night
        						DayNightSpawnManager.getInstance().addNightCreature(spawnDat);
        						_npcSpawnCount++;
        						break;     
        					}

        					if (spawnDat.getDbId()>_highestCustomDbId)_highestCustomDbId=spawnDat.getDbId();
        					_spawntable.put(spawnDat.getId(), spawnDat);
        				}
        			}
        			else
        			{
        				_log.warn("SpawnTable: Data missing or incorrect in NPC/Custom NPC table for ID: "
        						+ rset.getInt("npc_templateid") + ".");
        			}
        		}
        		rset.close();
        		statement.close();
        	}
        	catch (Exception e)
        	{
        		// problem with initializing spawn, go to next one
        		_log.warn("SpawnTable: Custom spawn could not be initialized: " + e);
        	}
        	finally
        	{
        		try
        		{
        			con.close();
        			con=null;
        		}
        		catch (Exception e)
        		{
        		}
        	}
        	_cSpawnCount =  _spawntable.size() - _cSpawnCount;
        	if (_cSpawnCount>0)
        		_log.info("TablesManager: Loaded " + _cSpawnCount + " Custom Spawn Locations.");
        }
        if(Config.JAIL_SPAWN_SYSTEM)
        {
        	try
        	{
        		con = L2DatabaseFactory.getInstance().getConnection(con);
        		PreparedStatement statement = con.prepareStatement("SELECT id, count, npc_templateid, locx, locy, locz, heading, respawn_delay, loc_id, periodOfDay FROM jail_spawnlist ORDER BY id");
        		ResultSet rset = statement.executeQuery();

        		L2Spawn spawnDat;
        		L2NpcTemplate template1;

        		_jailSpawnCount = _spawntable.size();

        		while (rset.next())
        		{
        			template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
        			if (template1 != null)
        			{
        				if (template1.getType().equalsIgnoreCase("L2SiegeGuard"))
        				{
        					// Don't spawn siege guards
        				}
        				else if (template1.getType().equalsIgnoreCase("L2RaidBoss"))
        				{
        					// Don't spawn raidbosses
        				}
        				else if (!Config.SPAWN_CLASS_MASTER && template1.getType().equals("L2ClassMaster"))
        				{
        					// Dont' spawn class masters
        				}
        				else
        				{
        					spawnDat = new L2Spawn(template1);
        					spawnDat.setId(_npcSpawnCount);
        					spawnDat.setDbId(rset.getInt("id"));
        					spawnDat.setAmount(rset.getInt("count"));
        					spawnDat.setLocx(rset.getInt("locx"));
        					spawnDat.setLocy(rset.getInt("locy"));
        					spawnDat.setLocz(rset.getInt("locz"));
        					spawnDat.setHeading(rset.getInt("heading"));
        					spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
        					spawnDat.setCustom();
        					int loc_id = rset.getInt("loc_id");
        					spawnDat.setLocation(loc_id);                             

        					switch(rset.getInt("periodOfDay")) {
        					case 0: // default
        						_npcSpawnCount += spawnDat.init();
        						break;
        					case 1: // Day
        						DayNightSpawnManager.getInstance().addDayCreature(spawnDat);
        						_npcSpawnCount++;
        						break;
        					case 2: // Night
        						DayNightSpawnManager.getInstance().addNightCreature(spawnDat);
        						_npcSpawnCount++;
        						break;     
        					}

        					if (spawnDat.getDbId()>_highestJailDbId)_highestJailDbId=spawnDat.getDbId();
        					_spawntable.put(spawnDat.getId(), spawnDat);
        				}
        			}
        			else
        			{
        				_log.warn("SpawnTable: Data missing or incorrect in NPC/Custom NPC table for ID: "
        						+ rset.getInt("npc_templateid") + ".");
        			}
        		}
        		rset.close();
        		statement.close();
        	}
        	catch (Exception e)
        	{
        		// problem with initializing spawn, go to next one
        		_log.warn("SpawnTable: Jail spawn could not be initialized: " + e);
        	}
        	finally
        	{
        		try
        		{
        			con.close();
        		}
        		catch (Exception e)
        		{
        		}
        	}
        	_jailSpawnCount =  _spawntable.size() - _jailSpawnCount;
        	
        	if (_jailSpawnCount > 0)
        		_log.info("TablesManager: Loaded " +  _jailSpawnCount + " Jail Spawn Locations.");

        	if (_log.isDebugEnabled())
        		_log.debug("SpawnTable: Spawning completed, total number of NPCs in the world: "
        				+ _npcSpawnCount);

        }
    }
    //L2EMU_EDIT_ADD_END
    public Map<Integer, L2Spawn> getAllTemplates()
    {
        return _spawntable;
    }

    public void addNewSpawn(L2Spawn spawn, boolean storeInDb)
    {
       _npcSpawnCount++;
       if (spawn.isCustom())
       {
           _highestCustomDbId++;
           spawn.setDbId(_highestCustomDbId);
       }
       //L2EMU_ADD
       else if(spawn.isJail())
       {
    	   _highestJailDbId++;
    	   spawn.setDbId(_highestJailDbId);
       }
       //L2EMU_ADD
       else        
       {
           _highestDbId++;
           spawn.setDbId(_highestDbId);
       }
       
       spawn.setId(_npcSpawnCount);
           
       _spawntable.put(spawn.getId(), spawn);

       if (storeInDb)
       {
            java.sql.Connection con = null;

            try
            {
                con = L2DatabaseFactory.getInstance().getConnection(con);
                //L2EMU_EDIT
                PreparedStatement statement = con.prepareStatement("INSERT INTO "+(spawn.isJail()?"jail_spawnlist":spawn.isCustom()?"custom_spawnlist":"spawnlist")+" (id,count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id) values(?,?,?,?,?,?,?,?,?)");
                //L2EMU_EDIT
                statement.setInt(1, spawn.getDbId());
                statement.setInt(2, spawn.getAmount());
                statement.setInt(3, spawn.getNpcId());
                statement.setInt(4, spawn.getLocx());
                statement.setInt(5, spawn.getLocy());
                statement.setInt(6, spawn.getLocz());
                statement.setInt(7, spawn.getHeading());
                statement.setInt(8, spawn.getRespawnDelay() / 1000);
                statement.setInt(9, spawn.getLocation());
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                // problem with storing spawn
                _log.warn("SpawnTable: Could not store spawn in the DB:" + e);
            }
            finally
            {
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                }
            }
       }
    }

    public void updateSpawn(L2Spawn spawn)
    {
        java.sql.Connection con = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            //L2EMU_EDIT
            PreparedStatement statement = con.prepareStatement("update "+(spawn.isJail()?"jail_spawnlist":spawn.isCustom()?"custom_spawnlist":"spawnlist")+" set count=?,npc_templateid=?,locx=?,locy=?,locz=?,heading=?,respawn_delay=?,loc_id=? where id =?");
            //L2EMU_EDIT
            statement.setInt(1, spawn.getAmount());
            statement.setInt(2, spawn.getNpcId());
            statement.setInt(3, spawn.getLocx());
            statement.setInt(4, spawn.getLocy());
            statement.setInt(5, spawn.getLocz());
            statement.setInt(6, spawn.getHeading());
            statement.setInt(7, spawn.getRespawnDelay() / 1000);
            statement.setInt(8, spawn.getLocation());
            statement.setInt(9, spawn.getDbId());
            
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            // problem with storing spawn
            _log.warn("SpawnTable: Could not update spawn in the DB:" + e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    public void deleteSpawn(L2Spawn spawn, boolean updateDb)
    {
        if (_spawntable.remove(spawn.getId()) == null) return;

        if (updateDb)
        {
            java.sql.Connection con = null;

            try
            {
                con = L2DatabaseFactory.getInstance().getConnection(con);
                //L2EMU_EDIT
                PreparedStatement statement = con.prepareStatement("DELETE FROM "+(spawn.isJail()?"jail_spawnlist":spawn.isCustom()?"custom_spawnlist":"spawnlist")+" WHERE id=?");
                //L2EMU_EDIT
                statement.setInt(1, spawn.getDbId());
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                // problem with deleting spawn
                _log.warn("SpawnTable: Spawn "+ spawn.getDbId() +" could not be removed from DB: "+e);
            }
            finally
            {
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    
    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.datatables.SpawnTableMBean#reloadAll()
     */
    public void reloadAll()
    { 
        cleanUp();
        fillSpawnTable();
    }

    /**
     * Clear all spawns from the cache
     */
    private void cleanUp()
    { 
    	_spawntable.clear();
    }
    
    /**
     * @param id the id of the spawn npc
     * @return the template (description) of this spawn
     */
    public L2Spawn getTemplate(int id)
    {
        return _spawntable.get(id);
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.datatables.SpawnTableMBean#getNpcSpawnCount()
     */
    public int getNpcSpawnCount()
    {
        return _spawntable.size();
    }

    /**
     * Get all the spawn of a NPC<BR><BR>
     * 
     * @param npcId : ID of the NPC to find.
     * @return
     */
    public void findNPCInstances(L2PcInstance activeChar, int npcId, int teleportIndex)
    {
        int index = 0;
        for (L2Spawn spawn : _spawntable.values())
        {
            if (npcId == spawn.getNpcId())
            {
                index++;
                if (teleportIndex > -1)
                {
                    if (teleportIndex == index)
                        activeChar.teleToLocation(spawn.getLocx(), spawn.getLocy(), spawn.getLocz(), true);
                }
                else
                {
                    activeChar.sendMessage(index + " - " + spawn.getTemplate().getName() + " ("
                        + spawn.getId() + "): " + spawn.getLocx() + " " + spawn.getLocy() + " "
                        + spawn.getLocz());
                }
            }
        }
        if (index == 0) activeChar.sendMessage("No current spawns found.");
    }
}
