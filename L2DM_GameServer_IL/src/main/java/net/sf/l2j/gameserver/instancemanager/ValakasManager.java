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

/**
 @author L2J_JP SANDMAN
 **/

package net.sf.l2j.gameserver.instancemanager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.instance.L2BossInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.IZone;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.serverpackets.SocialAction;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class ...
 * control for sequence of figth with Valakas.
 * @version $Revision: $ $Date: $
 * @author  L2J_JP SANDMAN
 */
public class ValakasManager
{
    private final static Log _log = LogFactory.getLog(ValakasManager.class.getName());
    private static ValakasManager _instance = new ValakasManager();

    // config
    // Interval time of Boss.
    protected int _intervalOfBoss;

    // Delay of appearance time of Boss.
    protected int _appTimeOfBoss;

    // Activity time of Boss.
    protected int _activityTimeOfBoss;

    // Capacity Of Lair Of Valakas.
    protected int _capacity;
    
    // Whether it moves at random after Valakas appears is decided.
    protected boolean _moveAtRandom = true;
    
    // location of teleport cube.
    private final int _teleportCubeId = 31759;
    private final int _teleportCubeLocation[][] =
    	{
    		{214880, -116144, -1644, 0},
    		{213696, -116592, -1644, 0},
    		{212112, -116688, -1644, 0},
    		{211184, -115472, -1664, 0},
    		{210336, -114592, -1644, 0},
    		{211360, -113904, -1644, 0},
    		{213152, -112352, -1644, 0},
    		{214032, -113232, -1644, 0},
    		{214752, -114592, -1644, 0},
    		{209824, -115568, -1421, 0},
    		{210528, -112192, -1403, 0},
    		{213120, -111136, -1408, 0},
    		{215184, -111504, -1392, 0},
    		{215456, -117328, -1392, 0},
    		{213200, -118160, -1424, 0}
    	};
    protected List<L2Spawn> _teleportCubeSpawn = new FastList<L2Spawn>();
    protected List<L2NpcInstance> _teleportCube = new FastList<L2NpcInstance>();

    // list of intruders.
    protected List<L2PcInstance> _playersInLair = new FastList<L2PcInstance>();

    // spawn data of monsters.
    protected Map<Integer,L2Spawn> _monsterSpawn = new FastMap<Integer,L2Spawn>();

    // instance of monsters.
    protected List<L2NpcInstance> _monsters = new FastList<L2NpcInstance>();
    
    // tasks.
    protected Future _cubeSpawnTask = null;
    protected Future _monsterSpawnTask = null;
    protected Future _intervalEndTask = null;
    protected Future _activityTimeEndTask = null;
    protected Future _onPlayersAnnihilatedTask = null;
    protected Future _socialTask = null;
    protected Future _mobiliseTask = null;
    protected Future _moveAtRandomTask = null;
    
    // status in lair.
    protected boolean _isBossSpawned = false;
    protected boolean _isIntervalForNextSpawn = false;
    protected IZone  _zone;
    protected String _zoneName;
    protected String _questName;
    
    // location of banishment
    private final int _banishmentLocation[][] = 
    	{
    		{150604, -56283, -2980},
    		{144857, -56386, -2980},
    		{147696, -56845, -2780}
    	};
    
    public ValakasManager()
    {
    }

    public static ValakasManager getInstance()
    {
        if (_instance == null) _instance = new ValakasManager();

        return _instance;
    }

    // initialize
    public void init()
    {
    	// read configuration.
    	_intervalOfBoss = Config.FWV_INTERVALOFVALAKAS;
    	_appTimeOfBoss = Config.FWV_APPTIMEOFVALAKAS;
    	_activityTimeOfBoss = Config.FWV_ACTIVITYTIMEOFVALAKAS;
    	_capacity = Config.FWV_CAPACITYOFLAIR;
    	_moveAtRandom = Config.FWV_MOVEATRANDOM;
    	
    	// initialize status in lair.
    	_isBossSpawned = false;
    	_isIntervalForNextSpawn = false;
    	_playersInLair.clear();
        _zoneName = "Lair of Valakas";
        _questName = "valakas";

        // setting spawn data of monsters.
        try
        {
            L2NpcTemplate template1;
            L2Spawn tempSpawn;
            
            // Valakas.
            template1 = NpcTable.getInstance().getTemplate(29028);
            tempSpawn = new L2Spawn(template1);
            tempSpawn.setLocx(212852);
            tempSpawn.setLocy(-114842);
            tempSpawn.setLocz(-1632);
            //tempSpawn.setHeading(22106);
            tempSpawn.setHeading(833);
            tempSpawn.setAmount(1);
            tempSpawn.setRespawnDelay(_intervalOfBoss * 2);
            SpawnTable.getInstance().addNewSpawn(tempSpawn, false);
            _monsterSpawn.put(29028, tempSpawn);
        }
        catch (Exception e)
        {
            _log.warn(e.getMessage());
        }

        // setting spawn data of teleport cube.
        try
        {
            L2NpcTemplate Cube = NpcTable.getInstance().getTemplate(_teleportCubeId);
            L2Spawn spawnDat;
            for (int[] element : _teleportCubeLocation) {
                spawnDat = new L2Spawn(Cube);
                spawnDat.setAmount(1);
                spawnDat.setLocx(element[0]);
                spawnDat.setLocy(element[1]);
                spawnDat.setLocz(element[2]);
                spawnDat.setHeading(element[3]);
                spawnDat.setRespawnDelay(60);
                spawnDat.setLocation(0);
                SpawnTable.getInstance().addNewSpawn(spawnDat, false);
                _teleportCubeSpawn.add(spawnDat);
            }
        }
        catch (Exception e)
        {
            _log.warn(e.getMessage());
        }
        //L2EMU_EDIT_START
        _log.info("GameServer: Initializing Valakas Manager.");
        //L2EMU_EDIT_END
    }

    // return list of intruders.
    public List<L2PcInstance> getPlayersInLair()
	{
		return _playersInLair;
	}

    public boolean checkIfInZone(L2PcInstance pc)
    {
    	if ( _zone == null )
    		_zone = ZoneManager.getInstance().getZone(ZoneType.BossDangeon, _zoneName );
    	return _zone.checkIfInZone(pc);
    }
    
    // Whether it lairs is confirmed. 
    public boolean isEnableEnterToLair()
    {
    	if(_playersInLair.size() >= _capacity) return false;
    	
    	return(_isBossSpawned == false && _isIntervalForNextSpawn == false);
    }

    // update list of intruders.
    public void addPlayerToLair(L2PcInstance pc)
    {
        if (!_playersInLair.contains(pc)) _playersInLair.add(pc);
    }
    
    // Whether the players was annihilated is confirmed. 
    public synchronized boolean isPlayersAnnihilated()
    {
    	for (L2PcInstance pc : _playersInLair)
		{
			// player is must be alive and stay inside of lair.
			if (!pc.isDead() && checkIfInZone(pc))
			{
				return false;
			}
		}
		return true;
    }

    // banishes players from lair.
    public void banishesPlayers()
    {
    	for(L2PcInstance pc : _playersInLair)
    	{
    		if(pc.getQuestState(_questName) != null) pc.getQuestState(_questName).exitQuest(true);
    		if(checkIfInZone(pc))
    		{
        		int driftX = Rnd.get(-80,80);
        		int driftY = Rnd.get(-80,80);
        		int loc = Rnd.get(3);
        		pc.teleToLocation(_banishmentLocation[loc][0] + driftX,_banishmentLocation[loc][1] + driftY,_banishmentLocation[loc][2]);
    		}
    	}
    	_playersInLair.clear();
    }
    
    // do spawn teleport cube.
    public void spawnCube()
    {
		for (L2Spawn spawnDat : _teleportCubeSpawn)
		{
			_teleportCube.add(spawnDat.doSpawn());
		}
    	_isIntervalForNextSpawn = true;
    }
    
	// When the party is annihilated, they are banished.
    public void checkAnnihilated()
    {
    	if(isPlayersAnnihilated())
    	{
    		_onPlayersAnnihilatedTask =
				ThreadPoolManager.getInstance().scheduleEffect(new OnPlayersAnnihilatedTask(),5000);    			
    	}
    }

	// When the party is annihilated, they are banished.
	private class OnPlayersAnnihilatedTask implements Runnable
	{
		public OnPlayersAnnihilatedTask()
		{
		}
		
		public void run()
		{
		    // banishes players from lair.
			banishesPlayers();
			
            // clean up task.
            if(_onPlayersAnnihilatedTask != null)
            {
            	_onPlayersAnnihilatedTask.cancel(true);
            	_onPlayersAnnihilatedTask = null;
            }
		}
	}

    // setting Valakas spawn task.
    public void setValakasSpawnTask()
    {
    	// When someone has already invaded the lair, nothing is done.
    	if (_playersInLair.size() >= 1) return;

    	if (_monsterSpawnTask == null)
        {
    		_monsterSpawnTask = ThreadPoolManager.getInstance().scheduleEffect(	new ValakasSpawn(1,null),_appTimeOfBoss);
        }
    }
    
    // do spawn Valakas.
    private class ValakasSpawn implements Runnable
    {
    	int _distance = 6502500;
    	int _taskId;
    	L2BossInstance _valakas = null;
    	
    	ValakasSpawn(int taskId,L2BossInstance valakas)
    	{
    		_taskId = taskId;
    		_valakas = valakas;
		}
    	
    	public void run()
    	{
    		SocialAction sa = null;
    		
    		switch(_taskId)
    		{
	    		case 1:
	            	// do spawn.
	            	L2Spawn valakasSpawn = _monsterSpawn.get(29028);
	            	_valakas = (L2BossInstance)valakasSpawn.doSpawn();
	            	_monsters.add(_valakas);
	            	updateKnownList(_valakas);
	            	_valakas.setIsImobilised(true);
	            	_valakas.setIsInSocialAction(true);
	    		
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(2,_valakas), 16);
	
					break;
	
	    		case 2:
	            	// do social.
	                sa = new SocialAction(_valakas.getObjectId(), 1);
	                _valakas.broadcastPacket(sa);
					
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 1800,180,-1,1500,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(3,_valakas), 1500);
	
					break;
	
	    		case 3:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 1300,180,-5,3000,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(4,_valakas), 3300);
	
					break;
	
	    		case 4:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 500,180,-8,600,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(5,_valakas), 1300);
	
					break;
	
	    		case 5:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 1200,180,-5,300,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(6,_valakas), 1600);
	
					break;
	
	    		case 6:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 2800,250,70,0,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(7,_valakas), 200);
	
					break;
	
	    		case 7:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 2600,30,60,3400,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(8,_valakas), 5700);
	
					break;
	
	    		case 8:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 700,150,-65,0,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(9,_valakas), 1400);
	
					break;
	
	    		case 9:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 1200,150,-55,2900,15000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(10,_valakas), 6700);
	
					break;
	
	    		case 10:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 750,170,-10,1700,5700);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(11,_valakas), 3700);
	
					break;
	
	    		case 11:
					// set camera.
					for (L2PcInstance pc : _playersInLair)
					{
						if (pc.getPlanDistanceSq(_valakas) <= _distance)
						{
							pc.enterMovieMode();
							pc.specialCamera(_valakas, 840,170,-5,1200,2000);
						} else
						{
							pc.leaveMovieMode();
						}
					}
	
					// set next task.
		            if(_socialTask != null)
		            {
		            	_socialTask.cancel(true);
		            	_socialTask = null;
		            }
					_socialTask = ThreadPoolManager.getInstance().scheduleEffect(new ValakasSpawn(12,_valakas), 2000);
	
					break;
	
	    		case 12:
					// reset camera.
					for (L2PcInstance pc : _playersInLair)
					{
						pc.leaveMovieMode();
					}

					_mobiliseTask = ThreadPoolManager.getInstance().scheduleEffect(new SetMobilised(_valakas),16);
	
	                // move at random.
	                if(_moveAtRandom)
	                {
	                	L2CharPosition pos = new L2CharPosition(Rnd.get(211080, 214909),Rnd.get(-115841, -112822),-1662,0);
	                	_moveAtRandomTask = ThreadPoolManager.getInstance().scheduleEffect(new MoveAtRandom(_valakas,pos),32);
	                }
	                
	                // set delete task.
	                _activityTimeEndTask = ThreadPoolManager.getInstance().scheduleEffect(new ActivityTimeEnd(),_activityTimeOfBoss);
	
					break;
    		}
    	}
    }

    // at end of activitiy time.
    private class ActivityTimeEnd implements Runnable
    {
    	public ActivityTimeEnd()
    	{
    	}
    	
    	public void run()
    	{
    		setUnspawn();
    		
    		if(_activityTimeEndTask != null)
    		{
    			_activityTimeEndTask.cancel(true);
    			_activityTimeEndTask = null;
    		}
    	}
    }
    
    // clean Valakas's lair.
    public void setUnspawn()
	{
    	// eliminate players.
    	banishesPlayers();

    	// delete monsters.
    	for(L2NpcInstance mob : _monsters)
    	{
    		mob.getSpawn().stopRespawn();
    		mob.deleteMe();
    	}
    	_monsters.clear();
    	
    	// delete teleport cube.
		for (L2NpcInstance cube : _teleportCube)
		{
			cube.getSpawn().stopRespawn();
			cube.deleteMe();
		}
		_teleportCube.clear();
		
		// not executed tasks is canceled.
		if(_cubeSpawnTask != null)
		{
			_cubeSpawnTask.cancel(true);
			_cubeSpawnTask = null;
		}
		if(_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(true);
			_monsterSpawnTask = null;
		}
		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(true);
			_intervalEndTask = null;
		}
		if(_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(true);
			_activityTimeEndTask = null;
		}
		if(_onPlayersAnnihilatedTask != null)
		{
			_onPlayersAnnihilatedTask.cancel(true);
			_onPlayersAnnihilatedTask = null;
		}
		if(_socialTask != null)
		{
			_socialTask.cancel(true);
			_socialTask = null;
		}
		if(_mobiliseTask != null)
		{
			_mobiliseTask.cancel(true);
			_mobiliseTask = null;
		}
		if(_moveAtRandomTask != null)
		{
			_moveAtRandomTask.cancel(true);
			_moveAtRandomTask = null;
		}

		// init state of Valakas's lair.
    	_isBossSpawned = false;
    	_isIntervalForNextSpawn = true;

		// interval begin.
		setInetrvalEndTask();
	}

    // start interval.
    public void setInetrvalEndTask()
    {
    	_intervalEndTask = ThreadPoolManager.getInstance().scheduleEffect(new IntervalEnd(),_intervalOfBoss);
    }

    // at end of interval.
    private class IntervalEnd implements Runnable
    {
    	public IntervalEnd()
    	{
    	}
    	
    	public void run()
    	{
    		_isIntervalForNextSpawn = false;
    		if(_intervalEndTask != null)
    		{
    			_intervalEndTask.cancel(true);
    			_intervalEndTask = null;
    		}
    	}
    }
    
    // setting teleport cube spawn task.
    public void setCubeSpawn()
    {
		_cubeSpawnTask = ThreadPoolManager.getInstance().scheduleEffect(new CubeSpawn(),10000);
    }
    
    // update knownlist.
    protected void updateKnownList(L2NpcInstance boss)
    {
    	boss.getKnownList().getKnownPlayers().clear();
		for (L2PcInstance pc : _playersInLair)
		{
			boss.getKnownList().getKnownPlayers().put(pc.getObjectId(), pc);
		}
    }

    // do spawn teleport cube.
    private class CubeSpawn implements Runnable
    {
    	public CubeSpawn()
    	{
    	}
    	
        public void run()
        {
        	spawnCube();
        }
    }
    
    // action is enabled the boss.
    private class SetMobilised implements Runnable
    {
        private L2BossInstance _boss;
        public SetMobilised(L2BossInstance boss)
        {
        	_boss = boss;
        }

        public void run()
        {
        	_boss.setIsImobilised(false);
        	_boss.setIsInSocialAction(false);
            
            // When it is possible to act, a social action is canceled.
            if (_socialTask != null)
            {
            	_socialTask.cancel(true);
                _socialTask = null;
            }
        }
    }
    
    // Move at random on after Valakas appears.
    private class MoveAtRandom implements Runnable
    {
    	private L2NpcInstance _npc;
    	L2CharPosition _pos;
    	
    	public MoveAtRandom(L2NpcInstance npc,L2CharPosition pos)
    	{
    		_npc = npc;
    		_pos = pos;
    	}
    	
    	public void run()
    	{
    		_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,_pos);
    	}
    }
}
