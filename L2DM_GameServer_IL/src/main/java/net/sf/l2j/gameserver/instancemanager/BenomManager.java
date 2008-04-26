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
/****************************************************************Benom Manager*******************************************************************
@ version 0.97
© Dark Moon Dev Team, 2008
**********************************************************************************************************************************************/
//todo: organize teleport to Benom
/*															INFO
Bemom ID = 29054
Benom Jail X = 12315
Benom Jail Y = -48999
Benom Jail Z = -3010
Benom Jail Heading = 61196
*/
package net.sf.l2j.gameserver.instancemanager;

import net.sf.l2j.L2DatabaseFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import net.sf.l2j.Config;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.L2Spawn;

import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.instancemanager.CastleManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BenomManager
{
    private final static Log _log = LogFactory.getLog(BenomManager.class.getName());
	private static BenomManager _instance = new BenomManager();
	
	protected String _benomState;
	protected long   _firstspawndate;
	protected long   _agrospawndate; //from this moment Benom may escape from his jail
	protected long   _benomTimer; //time before Benom will be spawned in his jail
	protected long   _benomJailUnspawnTimer; // time before will be unspawned, == time before Siege of Rune wil be started
	
	/*protected int    _baseTowerQuality;
	protected int    _currentTowerQuality;
	protected boolean isFirstGet = true;
	public boolean   _spanAllowed = false;*/
	
	//todo: spawn benom after destry 2 Control Tower of Rune
    
	public L2Spawn _benomSpawn;
	
	Castle Rune = CastleManager.getInstance().getCastleByName("Rune");
	
	public BenomManager()
    {
    }

    public static BenomManager getInstance()
    {
        if (_instance == null) _instance = new BenomManager();

        return _instance;
    }
	
	public void init()
	{
	 GetRuneInfo();
	 CalculateAdvData();
	 GetBenomData();	 
	 ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
	  {
            public void run()
            {
			BenomJailSpawner();
			JailUnSpawner();
			}
	  }, _benomTimer);
	_log.info("GameServer:: Benom Manager Initialized succesfull!");
	}
	
	private void GetRuneInfo()
	{
	if (Rune != null )
	    {
		_agrospawndate = Rune.getSiegeDate().getTimeInMillis();
		}
	}
	
	private void GetBenomData()
	{
	long firstspawndate;
	java.sql.Connection con = null;
			try
			{
				PreparedStatement statement;
				ResultSet rs;
				con = L2DatabaseFactory.getInstance().getConnection(con);
				statement = con.prepareStatement("SELECT `benomstate` FROM `benomdata`");
				rs = statement.executeQuery();
				while (rs.next())
				{
					_benomState = rs.getString("benomstate");
				}
				statement.close();
	   	    }
		 	catch (Exception e)
      	  	{
       	       _log.error("Exception: BenomManager.GetBenomData(): " + e.getMessage());
       	 	}
			//correction
			if((!_benomState.equals("NOTSPAWNED"))&&(System.currentTimeMillis() < _firstspawndate) ||(_benomState == null))
					{
					_benomState = "NOTSPAWNED";
					SaveBenomData();
					}
	}

	private void CalculateAdvData()
	{
	_firstspawndate = _agrospawndate - Config.SHT_SIEGE_BENOM_INTERVAL;
	_benomTimer = _firstspawndate - System.currentTimeMillis();
	_benomJailUnspawnTimer = _agrospawndate - System.currentTimeMillis();
	}
	
	private void SaveBenomData()
	{
	java.sql.Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement;
			statement = con.prepareStatement("UPDATE `benomdata` SET `benomstate` = ?");
			statement.setString(1, _benomState);
			statement.execute();
            statement.close();
	    }
		catch (Exception e)
        {
            _log.error("Exception: BenomManager.SaveBenomData(): " + e.getMessage());
        }        
    }
	
	public void BenomSpawner(int _npcX, int _npcY, int _npcZ, int _npcHeading)
	{
			L2NpcTemplate benomtmpl = NpcTable.getInstance().getTemplate(29054);
			_benomSpawn = new L2Spawn(benomtmpl);
            _benomSpawn.setLocx(_npcX);
            _benomSpawn.setLocy(_npcY);
            _benomSpawn.setLocz(_npcZ);
            _benomSpawn.setAmount(1);
            _benomSpawn.setHeading(_npcHeading);
            _benomSpawn.setRespawnDelay(60000);
            SpawnTable.getInstance().addNewSpawn(_benomSpawn, false);
			_benomSpawn.init();
			_benomSpawn.getLastSpawn().spawnMe(_benomSpawn.getLastSpawn().getX(), _benomSpawn.getLastSpawn().getY(), _benomSpawn.getLastSpawn().getZ());
			
	}
	
	public void BenomUnSpavner()
	{
        if (_benomSpawn == null)
            return;
        _benomSpawn.getLastSpawn().deleteMe();
        _benomSpawn.stopRespawn();
        SpawnTable.getInstance().deleteSpawn(_benomSpawn, true);
	}
	
	public void BenomKilling()
	{
    BenomUnSpavner();
	if(!_benomState.equals("KILLED")) _benomState = "KILLED";
	SaveBenomData();
	}
	
	private void BenomJailSpawner()
	{
	long currenttime = System.currentTimeMillis();
	if((currenttime >= _firstspawndate)&&(!_benomState.equals("KILLED")))
		{
		BenomSpawner(12315, -48999,-3010,61196);//x, y ,z, heading
		_benomState = "ACTIVE";
		SaveBenomData();
		}
	}
	
	public void SiegeAggroSpawn()
	{
	if(!_benomState.equals("KILLED"))
		{
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
				BenomSpawner(Config.SHT_BENOM_S_X, Config.SHT_BENOM_S_Y, Config.SHT_BENOM_S_Z ,Config.SHT_BENOM_S_HEAD);
				}
			}, 60000 );
	    }
    }
    
	
	private void JailUnSpawner()
	{
	ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
	    {
            public void run()
            {
			BenomUnSpavner();
			SiegeAggroSpawn();
			}
	    }, _benomJailUnspawnTimer);
	}
	
	
}