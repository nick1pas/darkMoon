/*
 * $Header: GameServerListener.java, 14-Jul-2005 03:26:20 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 14-Jul-2005 03:26:20 $
 * $Revision: 1 $
 * $Log: GameServerListener.java,v $
 * Revision 1  14-Jul-2005 03:26:20  luisantonioa
 * Added copyright notice
 *
 * 
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
package net.sf.l2j.loginserver.manager;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.l2j.loginserver.beans.GameServerInfo;
import net.sf.l2j.loginserver.beans.Gameservers;
import net.sf.l2j.loginserver.services.GameserversServices;
import net.sf.l2j.tools.L2Registry;
import net.sf.l2j.tools.util.HexUtil;
import net.sf.l2j.util.Rnd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manager servers
 * Servers come from server.xml file and database. 
 * For each server in database, an instance of Gameserver is launch. The manager controls each gameserver threads.
 */
public class GameServerManager
{
    private static final Log _log = LogFactory.getLog(GameServerManager.class);
    private static GameServerManager __instance = null;

    // Game Server from database
    private Map<Integer, GameServerInfo> _gameServers = new FastMap<Integer, GameServerInfo>().setShared(true);

    // RSA Config
    private static final int KEYS_SIZE = 10;
    private KeyPair[] _keyPairs;

	private GameserversServices _gsServices = null;
    private GameserversServices _gsServicesXml = null;


    /**
     * Return singleton 
     * exit the program if we didn't succeed to load the instance
     * @return  GameServerManager
     */
    public static GameServerManager getInstance()
    {
        if (__instance == null)
        {
            try
            {
                __instance = new GameServerManager();
            } catch (NoSuchAlgorithmException e)
            {
                _log.fatal("FATAL: Failed loading GameServerManager. Reason: "+e.getMessage(),e);
                System.exit(1);
            } catch (InvalidAlgorithmParameterException e)
            {
                _log.fatal("FATAL: Failed loading GameServerManager. Reason: "+e.getMessage(),e);
                System.exit(1);
            }
        }
        return __instance;
    }

    /**
     * Initialize keypairs
     * Initialize servers list from xml and db
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchAlgorithmException 
     */
    private GameServerManager() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
    {
        // o Load DAO 
        // ---------
        _gsServices = (GameserversServices) L2Registry.getBean("GameserversServices");
        _gsServicesXml = (GameserversServices) L2Registry.getBean("GameserversServicesXml");

        // o Load Servers
        // --------------
        load();

        // o Load RSA keys
        // ---------------
        this.loadRSAKeys();
    }
    
    /**
     * Load RSA keys 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidAlgorithmParameterException
     */
    private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
    {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512,RSAKeyGenParameterSpec.F4);
        keyGen.initialize(spec);
        
        _keyPairs = new KeyPair[KEYS_SIZE];
        for (int i = 0; i < KEYS_SIZE; i++)
        {
            _keyPairs[i] = keyGen.genKeyPair();
        }
        //L2EMU_EDIT_START
        _log.info("GameServerManager: Cached "+_keyPairs.length+" RSA keys for Game Server communication.");
        //L2EMU_EDIT_END
    }    
    
    public Map<Integer, GameServerInfo> getRegisteredGameServers()
    {
        return _gameServers;
    }
    
    public GameServerInfo getRegisteredGameServerById(int id)
    {
        return _gameServers.get(id);
    }

    public boolean hasRegisteredGameServerOnId(int id)
    {
        return _gameServers.containsKey(id);
    }
    
    public boolean registerWithFirstAvailableId(GameServerInfo gsi)
    {
        // avoid two servers registering with the same "free" id
        synchronized (_gameServers)
        {
        	List<Gameservers> serverNames = _gsServicesXml.getAllGameservers();
        	for (Gameservers entry : serverNames)
            {
                if (!_gameServers.containsKey(entry.getServerId()))
                {
                    _gameServers.put(entry.getServerId(), gsi);
                    gsi.setId(entry.getServerId());
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean register(int id, GameServerInfo gsi)
    {
        // avoid two servers registering with the same id
        synchronized (_gameServers)
        {
            if (!_gameServers.containsKey(id))
            {
                _gameServers.put(id, gsi);
                gsi.setId(id);
                return true;
            }
        }
        return false;
    }
    
    public void registerServerOnDB(GameServerInfo gsi)
    {
        this.registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getIp());
    }    
    
    public void registerServerOnDB(byte[] hexId, int id, String externalHost)
    {
        Gameservers gs = new Gameservers(id,HexUtil.hexToString(hexId),externalHost);
        _gsServices.createGameserver(gs);
    }    
    
    public String getServerNameById(int id)
    {
        return _gsServicesXml.getGameserverName(id);
    }
    
    /**
     * Load Gameserver from DAO
     * For each gameserver, instantiate a GameServer, (a container that hold a thread)
     */
    private void load()
    {
        List<Gameservers> listGs = _gsServices.getAllGameservers();
        GameServerInfo gsi;
        for (Gameservers gsFromDAO : listGs)
        {
            gsi = new GameServerInfo(gsFromDAO.getServerId(), HexUtil.stringToHex(gsFromDAO.getHexid()));
            _gameServers.put(gsFromDAO.getServerId(), gsi);
        }
      //L2EMU_EDIT_START
        _log.info("LoginServer: Initializing Game Server Manager...");
        _log.info("GameServerManager: Loaded "+ listGs.size()+" Gameserver(s).");
      //L2EMU_EDIT_START
    }


     /**
     * 
     * @param id - the server id
     */
    public void deleteServer(int id)
    {
        _gsServices.deleteGameserver(id);
    }
    
    /**
     * 
     * @param id - the server id
     */
    public void deleteAllServer()
    {
        _gsServices.removeAll();
    }    

  

    /**
     * 
     * @return
     */
    public KeyPair getKeyPair()
    {
        return _keyPairs[Rnd.nextInt(10)];
    }

    /**
     * 
     * @param id
     * @return
     */
    public String getServerName(int id)
    {
        return _gsServicesXml.getGameserverName(id);
    }

    /**
     * 
     * @param id
     * @return
     */
    public List<Gameservers> getServers()
    {
        return _gsServicesXml.getAllGameservers();
    }
}
