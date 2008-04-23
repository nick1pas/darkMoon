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
package net.sf.l2j.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;

import net.sf.l2j.Config;
import net.sf.l2j.loginserver.manager.BanManager;
import net.sf.l2j.loginserver.manager.GameServerManager;
import net.sf.l2j.loginserver.manager.LoginManager;
import net.sf.l2j.loginserver.services.SystemService;
import net.sf.l2j.loginserver.thread.GameServerListener;
import net.sf.l2j.status.Status;
import net.sf.l2j.tools.L2Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jserver.mmocore.network.SelectorServerConfig;
import com.l2jserver.mmocore.network.SelectorThread;

/**
 * Main class for loginserver
 * 
 */
public class L2LoginServer
{
	/** Protocol revision */
	public static final int PROTOCOL_REV = 0x0102;
	
	/**instance */
    private static L2LoginServer _instance;
    /**Logger */
    private static Log _log = LogFactory.getLog(L2LoginServer.class);
    /**the gameserver listener store all gameserver connected to the client*/
    private GameServerListener _gameServerListener;
    private SelectorThread<L2LoginClient> _selectorThread;
    public static Status statusServer;
    
    /**
     * @return the instance of L2LoginServer
     */
    public static L2LoginServer getInstance()
    {
        return _instance;
    }    
    
    /**
     * Instantiate loginserver and launch it
     * Initialize log folder, telnet console and registry
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Throwable
    {
        _instance = new L2LoginServer();
    }
    
    public L2LoginServer() throws Throwable 
    {        
        // Local Constants
        // ----------------
        final String LOG_FOLDER = "log"; // Name of folder for log file
        final String LOG_NAME = "./config/logging.properties"; // Name of log file

        // Create log folder
        // ------------------
        File logFolder = new File(LOG_FOLDER);
        logFolder.mkdir();

        // Create input stream for log file -- or store file data into memory
        // ------------------------------------------------------------------
        InputStream is = new FileInputStream(new File(LOG_NAME));
        LogManager.getLogManager().readConfiguration(is);
        is.close();

        // Initialize config 
        // ------------------
        Config.load();
        
        // Initialize Application context (registry of beans)
        // ---------------------------------------------------
        L2Registry.loadRegistry(new String[]{"spring.xml"});
        
        //L2EMU_ADD_START
        SystemService.getInstance().getSystemTime();
        SystemService.getInstance().getOperationalSystem();
        SystemService.getInstance().getCpuInfo();
        SystemService.getInstance().getMachineInfo();
        //L2EMU_ADD_END
        
        // o Initialize LoginManager
        // -------------------------
        LoginManager.load();        
        
        // o Initialize GameServer Manager
        // ------------------------------
        GameServerManager.getInstance();
        
        // o Initialize ban list
        // ----------------------
        BanManager.getInstance();
        
        // o Initialize SelectorThread
        // ----------------------------
        initNetworkLayer();
        
        // o Initialize GS listener
        // ----------------------------
        initGSListener();        
        
        // o Start status telnet server
        // --------------------------
        initTelnetServer();
        
        // o Start the server
        // ------------------
        startServer();
        //L2EMU_EDIT_STARY
        _log.info("LoginServer: Login Server Ready on: "+Config.LOGIN_SERVER_HOSTNAME+" : "+Config.LOGIN_SERVER_PORT);     
        //L2EMU_EDIT_END
    }

	/**
	 * 
	 */
	private void startServer() {
		try
        {
            _selectorThread.openServerSocket();
        }
        catch (IOException e)
        {
            _log.fatal("FATAL: Failed to open server socket. Reason: "+e.getMessage(),e);
            System.exit(1);
        }
        _selectorThread.start();
	}

	/**
	 * @throws IOException
	 */
	private void initTelnetServer() throws IOException {
		if (Config.IS_TELNET_ENABLED)
        {
            statusServer = new Status();
            statusServer.start();
        }
        else
        {
        	//L2EMU_EDIT_START
            _log.info("LoginServer: Telnet server is currently disabled.");
            //L2EMU_EDIT_END
        }
	}

	/**
	 * 
	 */
	private void initGSListener() {
		try
        {
            _gameServerListener = new GameServerListener();
            _gameServerListener.start();
            //L2EMU_EDIT_START
            _log.info("LoginServer: Listening for GameServers on: "+Config.LOGIN_HOSTNAME+" : "+Config.LOGIN_PORT);
            //L2EMU_EDIT_END
        }
        catch (IOException e)
        {
            _log.fatal("FATAL: Failed to start the Game Server Listener. Reason: "+e.getMessage(),e);
            System.exit(1);
        }
	}

	/**
	 * 
	 */
	private void initNetworkLayer() {
		SelectorServerConfig ssc = null;
		try {
			ssc = new SelectorServerConfig(InetAddress.getByName(Config.LOGIN_SERVER_HOSTNAME), Config.LOGIN_SERVER_PORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
        L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
        SelectorHelper sh = new SelectorHelper();
        try
        {
            _selectorThread = new SelectorThread<L2LoginClient>(ssc, loginPacketHandler, sh, sh);
            _selectorThread.setAcceptFilter(sh);
        }
        catch (IOException e)
        {
            _log.fatal("FATAL: Failed to open Selector. Reason: "+e.getMessage(),e);
            System.exit(1);
        }
	}
    
	public Status getStatusServer()
	{
		return statusServer;
	}

	public GameServerListener getGameServerListener() {
		return _gameServerListener;
	}    
	
	public void shutdown(boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}
