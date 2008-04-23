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
import net.sf.l2j.gameserver.datatables.TradeListTable;
import net.sf.l2j.gameserver.gameserverpackets.ServerStatus;
import net.sf.l2j.gameserver.instancemanager.CastleManorManager;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.instancemanager.IrcManager;
import net.sf.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import net.sf.l2j.gameserver.instancemanager.QuestManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.RaidPointsManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ServerClose;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class provides the functions for shutting down and restarting the server
 * It closes all open clientconnections and saves all data.
 * 
 * @version $Revision: 1.2.4.5 $ $Date: 2005/03/27 15:29:09 $
 */
public class Shutdown extends Thread implements ShutdownMBean 
{
    private final static Log _log = LogFactory.getLog(Shutdown.class.getName());
    private static Shutdown _instance;
    private static Shutdown _counterInstance = null;
    
    private int _secondsShut;    
    
    private shutdownModeType _shutdownMode;
    
    public enum shutdownModeType
    {
    	SIGTERM  ("Terminating"),
    	SHUTDOWN ("Shutting down"),
    	RESTART ("Restarting"),
    	ABORT ("Aborting");
    	
    	private final String _modeText;
    	
    	shutdownModeType (String modeText)
    	{
    		_modeText = modeText;
    	}
    	
    	public String getText()
    	{
    		return _modeText;
    	}
    }
    
    /**
     * Default constuctor is only used internal to create the shutdown-hook instance
     *
     */
    public Shutdown() {
        _secondsShut = -1;
        _shutdownMode = shutdownModeType.SIGTERM;
    }
    
    /**
     * This creates a countdown instance of Shutdown. 
     * 
     * @param seconds   how many seconds until shutdown
     * @param restart   true is the server shall restart after shutdown
     * 
     */
    public Shutdown(int seconds, shutdownModeType mode) {
        if (seconds < 0) {
            seconds = 0;
        }
        _secondsShut = seconds+ Config.SHT_Fake_SECONDS_SHUTDOWN;
        _shutdownMode = mode;
    }

    /**
     * get the shutdown-hook instance
     * the shutdown-hook instance is created by the first call of this function,
     * but it has to be registrered externaly.
     * 
     * @return  instance of Shutdown, to be used as shutdown hook 
     */
    public static Shutdown getInstance()
    {
        if (_instance == null)
        {
            _instance = new Shutdown();
        }
        return _instance;
    }
    
    public static Shutdown getCounterInstance()
    {
        return _counterInstance;
    }
    
    /**
     * this function is called, when a new thread starts
     * 
     * if this thread is the thread of getInstance, then this is the shutdown hook
     * and we save all data and disconnect all clients.
     * 
     * after this thread ends, the server will completely exit
     * 
     * if this is not the thread of getInstance, then this is a countdown thread.
     * we start the countdown, and when we finished it, and it was not aborted,
     * we tell the shutdown-hook why we call exit, and then call exit
     * 
     * when the exit status of the server is 1, startServer.sh / startServer.bat
     * will restart the server.
     * 
     */
    @Override
    public void run()
    {
        // disallow new logins
        try
        {
            //Doesnt actually do anything
            //Server.gameServer.getLoginController().setMaxAllowedOnlinePlayers(0);
        }
        catch (Throwable t) {}
        if (this == _instance)
        {
            // ensure all services are stopped
            try
            {
                GameTimeController.getInstance().stopTimer();
            }
            catch (Throwable t) {}
            // stop all threadpolls
            try
            {
                ThreadPoolManager.getInstance().shutdown();
            }
            catch (Throwable t) {}
            // last byebye, save all data and quit this server
            // logging doesnt work here :(
            saveData();
            try
            {
                LoginServerThread.getInstance().interrupt();
            }
            catch (Throwable t) {}

            // saveData sends messages to exit players, so shutdown selector after it
            try
            {
                GameServer.gameServer.getSelectorThread().shutdown();
                GameServer.gameServer.getSelectorThread().setDaemon(true);
            }
            catch (Throwable t) {}

            // commit data, last chance
            //try
            //{
            //    L2DatabaseFactory.getInstance().shutdown();
            //}
            //catch (Throwable t) {}

            // server will quit, when this function ends.
            if (_instance._shutdownMode == shutdownModeType.RESTART)
                Runtime.getRuntime().halt(2);
            else
                Runtime.getRuntime().halt(0);
        }
        else
        {
            // gm shutdown: send warnings and then call exit to start shutdown sequence
            countdown();
            // last point where logging is operational :(
            _log.warn("Shutdown countdown is over. " + _instance._shutdownMode.getText() + " NOW!");
            switch (_shutdownMode) {
                case SHUTDOWN:
                    _instance.setMode(shutdownModeType.SHUTDOWN);
                    System.exit(0);
                    break;
                case RESTART:
                    _instance.setMode(shutdownModeType.RESTART);
                    System.exit(2);
                    break;
            }
        }
    }

    /**
     * This functions starts a shutdown countdown
     * 
     * @param activeChar    GM who issued the shutdown command
     * @param seconds       seconds until shutdown
     * @param restart       true if the server will restart after shutdown
     */
    public void startShutdown(L2PcInstance activeChar, int seconds, shutdownModeType mode)
    {
        startShutdown(activeChar.getName()+"("+activeChar.getObjectId()+")", seconds, mode); 	
    }
    /**
     * This functions starts a shutdown countdown
     * 
     * @param initiator     Who issued the shutdown command
     * @param seconds       seconds until shutdown
     * @param restart       true if the server will restart after shutdown
     */
    public void startShutdown(String _initiator, int seconds, shutdownModeType mode) 
    {

        _log.warn(_initiator+" issued shutdown command. " + mode.getText() + " in "+seconds+ " seconds!");

        setMode(mode);

       Announcements.getInstance().announceToAll("Attention players!");
       //L2EMU_EDIT_START
       Announcements.getInstance().announceToAll("Server "+Config.SERVER_NAME+" is " + _shutdownMode.getText().toLowerCase() + " in "+seconds+ " seconds!");
       //L2EMU_EDIT_END
       
       if(Config.IRC_ENABLED && !Config.IRC_ANNOUNCE)
    	   IrcManager.getInstance().getConnection().sendChan("Server is " + _shutdownMode.getText().toLowerCase() + " in "+seconds+ " seconds!");

       if (_counterInstance != null) {
            _counterInstance._abort();
        }
        
        // the main instance should only run for shutdown hook, so we start a new instance
        _counterInstance = new Shutdown(seconds, mode);
        _counterInstance.start();
    }

    /**
     * This function aborts a running countdown
     * 
     * @param activeChar    GM who issued the abort command
     */
    public void abort(L2PcInstance activeChar)
    {
    	abort(activeChar.getName()+"("+activeChar.getObjectId()+")");
    }
    /**
     * This function aborts a running countdown
     * 
     * @param activeChar    GM who issued the abort command
     */
    public void abort(String _initiator)
    {
        _log.warn(_initiator + " issued shutdown ABORT. " + _shutdownMode.getText() + " has been stopped!");
        Announcements.getInstance().announceToAll("Server aborts " + _shutdownMode.getText().toLowerCase() + " and continues normal operation!");

        if(Config.IRC_ENABLED && !Config.IRC_ANNOUNCE)
            IrcManager.getInstance().getConnection().sendChan("Server aborts " + _shutdownMode.getText().toLowerCase() + " and continues normal operation!");
        

        if (_counterInstance != null) {
            _counterInstance._abort();
            _counterInstance = null;
        }
    }
    
    /**
     * get the current count down
     * @param mode  what mode shall be set
     */
    public int getCountdown()
    {
        return _secondsShut;
    }
    
    /**
     * set the shutdown mode
     * @param mode  what mode shall be set
     */
    private void setMode(shutdownModeType mode)
    {
        _shutdownMode = mode;
    }

    /**
     * set shutdown mode to ABORT
     *
     */
    private void _abort()
    {
        _shutdownMode = shutdownModeType.ABORT;
    }

    /**
     * this counts the countdown and reports it to all players
     * countdown is aborted if mode changes to ABORT
     */
    private void countdown()
    {
        
        try {
            while (_secondsShut > 0) {
                
                int _seconds;
                int _minutes;
                int _hours;
                int _disconseconds;
				int _fakeseconds;
				int shttimer;
				//added by NecroLorD, Shilen's Temple Server
                _disconseconds = Config.SHT_Fake_SECONDS_SHUTDOWN; //number of seconds before shutdowning server for close all connection
				//if <> 0, you must add to shutdwn timer this value
				//end
                _seconds = _secondsShut;
                _minutes = Math.round(_seconds / 60);
                _hours = Math.round(_seconds / 3600);
				_fakeseconds = _seconds - _disconseconds;
				shttimer = Config.SHT_SHUTDOWNCOUNT;

                // announce only every minute after 10 minutes left and every second after 10 seconds
                if ((_fakeseconds <= shttimer || _fakeseconds == _minutes * 60) && (_fakeseconds <= 600) && _hours <=1)
                {
                    SystemMessage sm = new SystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS);
                    sm.addString(Integer.toString((_fakeseconds)));
                    Announcements.getInstance().announceToAll(sm);
                }

                try
                {
                    if (_fakeseconds <= 60 )
                    {
                        LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_DOWN);
                    }
                }
                catch (Exception e)
                {
                    // do nothing, we maybe are not connected to LS anymore
                }
                 
                _secondsShut--;
                    
                int delay = 1000; //milliseconds    
                Thread.sleep(delay);
                
                if(_shutdownMode == shutdownModeType.ABORT) break;
				//by NecroLorD
				if (_seconds == _disconseconds)
                   {
				   Announcements.getInstance().announceToAll("Server is " + _shutdownMode.getText().toLowerCase() + " NOW!");
                   disconnectAllCharacters();
                   }
				//end
            }
        }
        catch (InterruptedException e)
        {
            //this will never happen
        }
    }

    /**
     * this sends a last byebye, disconnects all players and saves data 
     *
     */
    private void saveData()
    {
        try
        {
            Announcements.getInstance().announceToAll("Server is " + _shutdownMode.getText().toLowerCase() + " NOW!");
        } catch (Throwable t) {_log.info( "", t);}

        if(Config.IRC_ENABLED && !Config.IRC_ANNOUNCE)
            IrcManager.getInstance().getConnection().sendChan("Server is " + _shutdownMode.getText().toLowerCase() + " NOW!");
        
        // we can't abort shutdown anymore, so i removed the "if" 
        disconnectAllCharacters();
        
        
        //L2EMU_EDIT_START
        
        // Seven Signs data is now saved along with Festival data.
        if (!SevenSigns.getInstance().isSealValidationPeriod())
            SevenSignsFestival.getInstance().saveFestivalData(false);

        // Save Seven Signs data before closing. :)
        SevenSigns.getInstance().saveSevenSignsData(null, true);
        System.err.println("GameServer: SevenSigns Data Saved.");
        
        // Save all raidboss status ^_^
        RaidPointsManager.getInstance().cleanUp();
        System.err.println("GameServer: All Characters Raid Points Saved.");
        
        //saves all raibosses info
        RaidBossSpawnManager.getInstance().cleanUp();
        System.err.println("GameServer: All raidboss Info Saved.");
        
        //saves all buylists data.
        TradeListTable.getInstance().dataCountStore();
        System.err.println("GameServer: Trade Controller Saved.");
        
        //saves olympiad.
        try
        {
            Olympiad.getInstance().save();
            System.err.println("GameServer: Olympiad Data Saved.");
        }
        catch(Exception e){_log.error(e.getMessage(),e);}

        // Save Cursed Weapons data before closing.
        CursedWeaponsManager.getInstance().saveData();
        System.err.println("GameServer: Cursed Weapons Data Saved.");
        
        // Save items on ground before closing
        if(Config.SAVE_DROPPED_ITEM){
        	ItemsOnGroundManager.getInstance().saveInDb();        
        	ItemsOnGroundManager.getInstance().cleanUp();
        	System.err.println("GameServer: All items on ground Saved.");
        }
        
        // Save Manor Data
        CastleManorManager.getInstance().save();		
        System.err.println("GameServer: Castle Manor Manager: Data Saved.");   
        
        // Save all global (non-player specific) Quest data that needs to persist after reboot
         QuestManager.getInstance().save();
         
        //saves latest tvt data.
        TvT.saveData();
        System.err.println("GameServer: TvT Data Saved.");
        
        //cleans all data after saving to make a freash event on next boot.
        TvT.cleanTvT();
        System.err.println("GameServer: TvT Cleared for next boot.");
        System.err.println("Data saved. All players disconnected, "+_shutdownMode.getText().toLowerCase()+".");
        //L2EMU_EDIT_END
        
        try {
            int delay = 5000;
            Thread.sleep(delay);
        } 
        catch (InterruptedException e) {
            //never happens :p
        }
    }

    /**
     * this disconnects all clients from the server
     *
     */
    private void disconnectAllCharacters()
    {
        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            //Logout Character
            try
            {
                // save player's stats and effects
                L2GameClient.saveCharToDisk(player);

                // close server
                ServerClose ql = new ServerClose();
                player.sendPacket(ql);

                // make shure to save ALL data
                player.deleteMe();
            } catch (Throwable t) {}
        }
        try { Thread.sleep(1000); } catch (Throwable t) {_log.info( "", t);}

        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            try
            {
                player.closeNetConnection();
            }
            catch (Throwable t)
            {
                // just to make sure we try to kill the connection 
            }               
        }
    }
    
    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.ShutdownMBean#processRestart(int)
     */
    public void processRestart(int seconds)
    {
        startShutdown("Mbean ask restart", seconds, shutdownModeType.RESTART);
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.ShutdownMBean#processShutdown(int)
     */
    public void processShutdown(int seconds)
    {
        startShutdown("Mbean ask shutdown", seconds, shutdownModeType.SHUTDOWN);
    }
}
