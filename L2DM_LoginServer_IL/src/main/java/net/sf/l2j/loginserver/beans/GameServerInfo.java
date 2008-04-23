/*
 * $HeadURL: $
 *
 * $Author: $
 * $Date: $
 * $Revision: $
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
package net.sf.l2j.loginserver.beans;

import net.sf.l2j.loginserver.gameserverpackets.ServerStatus;
import net.sf.l2j.loginserver.thread.GameServerThread;

/**
 *  
 */
public class GameServerInfo
{
    // auth
    private int _id;
    private byte[] _hexId;
    private boolean _isAuthed;
    
    // status
    private GameServerThread _gst;
    private int _status;
    
    // network
    private String _ip;
    private int _port;

    // config
    private boolean _isPvp = true;
    private boolean _isTestServer;
    private boolean _isShowingClock;
    private boolean _isShowingBrackets;
    private int _maxPlayers;
    
    public GameServerInfo(int id, byte[] hexId, GameServerThread gst)
    {
        _id = id;
        _hexId = hexId;
        _gst = gst;
        _status = ServerStatus.STATUS_DOWN; 
    }
    
    public GameServerInfo(int id, byte[] hexId)
    {
        this(id, hexId, null);
    }
    
    public void setId(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
    
    public byte[] getHexId()
    {
        return _hexId;
    }
    
    public void setAuthed(boolean isAuthed)
    {
        _isAuthed = isAuthed;
    }
    
    public boolean isAuthed()
    {
        return _isAuthed;
    }
    
    public void setGameServerThread(GameServerThread gst)
    {
        _gst = gst;
    }
    
    public GameServerThread getGameServerThread()
    {
        return _gst;
    }
    
    public void setStatus(int status)
    {
        _status = status;
    }
    
    public int getStatus()
    {
        return _status;
    }
    
    public int getCurrentPlayerCount()
    {
        if (_gst == null)
            return 0;
        return _gst.getPlayerCount();
    }
    
    public void setIp(String ip)
    {
    	_ip = ip;
    }
    
	public String getIp()
	{
		return _ip;
	}
    
    public int getPort()
    {
        return _port;
    }
    
    public void setPort(int port)
    {
        _port = port;
    }
    
    public void setMaxPlayers(int maxPlayers)
    {
        _maxPlayers = maxPlayers;
    }
    
    public int getMaxPlayers()
    {
        return _maxPlayers;
    }
    
    public boolean isPvp()
    {
        return _isPvp;
    }
    
    public void setTestServer(boolean val)
    {
        _isTestServer = val;
    }
    
    public boolean isTestServer()
    {
        return _isTestServer;
    }
    
    public void setShowingClock(boolean clock)
    {
        _isShowingClock = clock;
    }
    
    public boolean isShowingClock()
    {
        return _isShowingClock;
    }
    
    public void setShowingBrackets(boolean val)
    {
        _isShowingBrackets = val;
    }
    
    public boolean isShowingBrackets()
    {
        return _isShowingBrackets;
    }
    
    public void setDown()
    {
        this.setAuthed(false);
        this.setPort(0);
        this.setGameServerThread(null);
        this.setStatus(ServerStatus.STATUS_DOWN);
    }
}