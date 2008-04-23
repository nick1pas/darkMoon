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
package net.sf.l2j.gameserver.instancemanager;

import javolution.util.FastMap;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2RaidBossInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: $ $Date: $
 * @author  godson
 */

public class DayNightSpawnManager {
    
    private final static Log _log = LogFactory.getLog(DayNightSpawnManager.class.getName());
    private static DayNightSpawnManager _instance;
    private static FastMap<L2Spawn, L2NpcInstance> _dayCreatures;
    private static FastMap<L2Spawn, L2NpcInstance> _nightCreatures;
    private static FastMap<L2Spawn, L2RaidBossInstance> _bosses;
    
    //private static int _currentState;  // 0 = Day, 1 = Night
    
    public static DayNightSpawnManager getInstance()
    {
        if (_instance == null)
            _instance = new DayNightSpawnManager(); 
        return _instance;
    }
    
    private DayNightSpawnManager()
    {
        _dayCreatures = new FastMap<L2Spawn, L2NpcInstance>();
        _nightCreatures = new FastMap<L2Spawn, L2NpcInstance>();
        _bosses = new FastMap<L2Spawn, L2RaidBossInstance>();
          //L2EMU_EDIT
        _log.info("GameServer: Day / Night Spawn Handler Initialized");
         //L2EMU_EDIT
    }
    
    public void addDayCreature(L2Spawn spawnDat)
    { 
        if (_dayCreatures.containsKey(spawnDat))
        {
            _log.warn("DayNightSpawnManager: Spawn already added into day map");
            return;
        }
        _dayCreatures.put(spawnDat, null);
    }
    
    public void addNightCreature(L2Spawn spawnDat)
    {
        if (_nightCreatures.containsKey(spawnDat))
        {
            _log.warn("DayNightSpawnManager: Spawn already added into night map");
            return;
        }
        _nightCreatures.put(spawnDat, null);
    }
    /*
     * Spawn Day Creatures, and Unspawn Night Creatures
     */
    public void spawnDayCreatures(){
    	spawnCreatures(_nightCreatures,_dayCreatures, "night", "day");
	}
    /*
     * Spawn Night Creatures, and Unspawn Day Creatures
     */
    public void spawnNightCreatures(){
    	spawnCreatures(_dayCreatures,_nightCreatures, "day", "night");
	}
    /*
     * Manage Spawn/Respawn
     * Arg 1 : Map with L2NpcInstance must be unspawned
     * Arg 2 : Map with L2NpcInstance must be spawned
     * Arg 3 : String for log info for unspawned L2NpcInstance
     * Arg 4 : String for log info for spawned L2NpcInstance
     */
    private void spawnCreatures(FastMap<L2Spawn, L2NpcInstance> UnSpawnCreatures,FastMap<L2Spawn, L2NpcInstance> SpawnCreatures, String UnspawnLogInfo, String SpawnLogInfo){
        try
        {
            if (UnSpawnCreatures.size() != 0)
            {
                int i = 0;
                for (L2NpcInstance dayCreature : UnSpawnCreatures.values())
                {
                    if (dayCreature == null) continue;
                    
                    dayCreature.getSpawn().stopRespawn();
                    dayCreature.deleteMe();
                    i++;
                }
               //L2EMU_EDIT
                _log.info("GameServer: Deleted " + i + " "+UnspawnLogInfo+" Creatures");
              //L2EMU_EDIT
            }
            
            int i = 0;
            L2NpcInstance creature = null;
            for (L2Spawn spawnDat : SpawnCreatures.keySet())
            {
                if (SpawnCreatures.get(spawnDat) == null)
                {
                    creature = spawnDat.doSpawn();
                    if (creature == null) continue;
                    
                    SpawnCreatures.remove(spawnDat);
                    SpawnCreatures.put(spawnDat, creature);
                    creature.getStatus().setCurrentHp(creature.getMaxHp());
                    creature.getStatus().setCurrentMp(creature.getMaxMp());		
					creature = SpawnCreatures.get(spawnDat);
					creature.getSpawn().startRespawn();
                }
                else
                {
                    creature = SpawnCreatures.get(spawnDat);
                    if (creature == null) continue;
                    
                    creature.getSpawn().startRespawn();
                    creature.getStatus().setCurrentHp(creature.getMaxHp());
                    creature.getStatus().setCurrentMp(creature.getMaxMp());
                    creature.spawnMe();
                }
                
                i++;
            }
          //L2EMU_EDIT
            _log.info("GameServer: Spawning " + i + " "+SpawnLogInfo+" Creatures");
          //L2EMU_EDIT
        }catch(Exception e){e.printStackTrace();}
    }
    private void changeMode(int mode)
    {
        if (_nightCreatures.size() == 0 && _dayCreatures.size() == 0)
            return;
        
        switch(mode) {
            case 0:
                spawnDayCreatures();
                specialNightBoss(0);
                break;
            case 1:
                spawnNightCreatures();
                specialNightBoss(1);
                break;
                default:
                    _log.warn("DayNightSpawnManager: Wrong mode sent");
                break;
        }
    }
    
    public void notifyChangeMode()
    {
        try{
            if (GameTimeController.getInstance().isNowNight())
                changeMode(1);
            else
                changeMode(0);
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void cleanUp()
    {
        _nightCreatures.clear();
        _dayCreatures.clear();
        _bosses.clear();
    }
    
    private void specialNightBoss(int mode)
    {
        try{
        for (L2Spawn spawn : _bosses.keySet())
        {
            L2RaidBossInstance boss = _bosses.get(spawn);
            
            if (boss == null && mode == 1)
            {
                boss = (L2RaidBossInstance)spawn.doSpawn();
                RaidBossSpawnManager.getInstance().notifySpawnNightBoss(boss);
                _bosses.remove(spawn);
                _bosses.put(spawn, boss);
                continue;
            }
            
            if (boss == null && mode == 0)
                continue;
            
            if(boss.getNpcId() == 25328 && boss.getRaidStatus().equals(RaidBossSpawnManager.StatusEnum.ALIVE))
                handleHellmans(boss, mode);
            return;
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    private void handleHellmans(L2RaidBossInstance boss, int mode)
    {
        switch(mode)
        {
            case 0:
                boss.deleteMe();
                _log.info("DayNightSpawnManager: Deleting Hellman raidboss");
                break;
            case 1:
                boss.spawnMe();
                _log.info("DayNightSpawnManager: Spawning Hellman raidboss");
                break;
        }
    }
    
    public L2RaidBossInstance handleBoss(L2Spawn spawnDat)
    {
        if(_bosses.containsKey(spawnDat)) return _bosses.get(spawnDat);
        
        if (GameTimeController.getInstance().isNowNight())
        {    
            L2RaidBossInstance raidboss = (L2RaidBossInstance)spawnDat.doSpawn();
            _bosses.put(spawnDat, raidboss);
            
            return raidboss;
        }
        _bosses.put(spawnDat, null);
        return null;
    }
}