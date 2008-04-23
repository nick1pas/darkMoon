package net.sf.l2j.loginserver.beans;

// Generated 16 d�c. 2006 13:48:45 by Hibernate Tools 3.2.0.beta8

/**
 * Gameservers generated by hbm2java
 */
public class Gameservers implements java.io.Serializable
{

    // Fields    

    /**
	 * 
	 */
	private static final long serialVersionUID = 2293307012167588040L;
	private String serverName;
    private int serverId;
    private String hexid;
    private String host;

    // Constructors

    /** default constructor */
    public Gameservers()
    {
    }

    /** full constructor */
    public Gameservers(int _serverId, String _hexid, String _host)
    {
        this.serverId = _serverId;
        this.hexid = _hexid;
        this.host = _host;
    }

    // Property accessors
    public int getServerId()
    {
        return this.serverId;
    }

    public void setServerId(int _serverId)
    {
        this.serverId = _serverId;
    }

    public String getHexid()
    {
        return this.hexid;
    }

    public void setHexid(String _hexid)
    {
        this.hexid = _hexid;
    }

    public String getHost()
    {
        return this.host;
    }

    public void setHost(String _host)
    {
        this.host = _host;
    }
    
    public String getServerName()
    {
        return this.serverName;
    }

    public void setServerName(String _serverName)
    {
        this.serverName = _serverName;
    }
    

}