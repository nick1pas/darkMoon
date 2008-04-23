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
package net.sf.l2j.loginserver.manager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;

import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.l2j.Config;
import net.sf.l2j.loginserver.L2LoginClient;
import net.sf.l2j.loginserver.L2LoginServer;
import net.sf.l2j.loginserver.beans.Accounts;
import net.sf.l2j.loginserver.beans.FailedLoginAttempt;
import net.sf.l2j.loginserver.beans.GameServerInfo;
import net.sf.l2j.loginserver.beans.SessionKey;
import net.sf.l2j.loginserver.gameserverpackets.ServerStatus;
import net.sf.l2j.loginserver.services.AccountsServices;
import net.sf.l2j.loginserver.services.exception.AccountBannedException;
import net.sf.l2j.loginserver.services.exception.AccountModificationException;
import net.sf.l2j.loginserver.services.exception.AccountWrongPasswordException;
import net.sf.l2j.loginserver.services.exception.HackingException;
import net.sf.l2j.loginserver.thread.GameServerThread;
import net.sf.l2j.tools.L2Registry;
import net.sf.l2j.tools.codec.Base64;
import net.sf.l2j.tools.math.ScrambledKeyPair;
import net.sf.l2j.util.Rnd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles login on loginserver.
 * It store connection for each account.
 * 
 * The ClientThread use LoginManager to :
 *  - store his connection identifier
 *  - retrieve basic information
 *  - delog an account
 */
public class LoginManager
{
	private static final Log _log = LogFactory.getLog(LoginManager.class);
    private static final Log _logLogin = LogFactory.getLog("login");
    private static final Log _logLoginTries = LogFactory.getLog("login.try");
    private static final Log _logLoginFailed = LogFactory.getLog("login.failed");
	
	private static LoginManager _instance;
	
	/** Authed Clients on LoginServer*/
	protected Map<String, L2LoginClient> _loginServerClients = new FastMap<String, L2LoginClient>().setShared(true);
	
	/** Keep trace of login attempt for an inetadress*/
	private Map<InetAddress, FailedLoginAttempt> _hackProtection;
    
    /** Clients that are on the LS but arent assocated with a account yet*/
    protected Set<L2LoginClient> _clients = new FastSet<L2LoginClient>();

	private ScrambledKeyPair[] _keyPairs;

    protected byte[][] _blowfishKeys;

    private static final int BLOWFISH_KEYS = 20;

    private AccountsServices _service = null;

    
    
    public static enum AuthLoginResult { INVALID_PASSWORD, ACCOUNT_BANNED, ALREADY_ON_LS, ALREADY_ON_GS, AUTH_SUCCESS , SYSTEM_ERROR};
    /**
     * Load the LoginManager
     * @throws GeneralSecurityException
     */
	public static void load() throws GeneralSecurityException
	{
		if (_instance == null)
		{
			_instance = new LoginManager();
		}
		else
		{
			throw new IllegalStateException("LoginManager can only be loaded a single time.");
		}
	}    
    
	/**
     * Private constructor to avoid direct instantiation. 
     * Initialize a key generator.
	 */
	private LoginManager()
	{
		try
        {
			//L2EMU_EDIT_START
            _log.info("LoginServer: Initializing Login Manager...");
            //L2EMU_EDIT_END
    		_hackProtection = new FastMap<InetAddress, FailedLoginAttempt>();
            
            _keyPairs = new ScrambledKeyPair[10];

            _service = (AccountsServices)L2Registry.getBean("AccountsServices");
            
    		KeyPairGenerator keygen = null;
            
            try
            {
            	keygen = KeyPairGenerator.getInstance("RSA");
            	RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024,RSAKeyGenParameterSpec.F4);
            	keygen.initialize(spec);
            }
            catch (GeneralSecurityException e)
            {
            	_log.fatal("Error in RSA setup:" + e);
            	_log.info("Server shutting down now");
            	System.exit(1);
            }
            
    		//generate the initial set of keys
    		for (int i = 0; i < 10; i++)
    		{
    			_keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
    		}
    		//L2EMU_ADD_START
    		_log.info("LoginManager: Cached 10 KeyPairs for RSA communication");
    		//L2EMU_ADD_START
    		
            this.testCipher((RSAPrivateKey) _keyPairs[0].getPair().getPrivate());
            
            // Store keys for blowfish communication
            this.generateBlowFishKeys();
        } 
        catch (GeneralSecurityException e)
        {
            _log.fatal("FATAL: Failed initializing LoginManager. Reason: "+e.getMessage(),e);
            System.exit(1);
        }
        
	}
    
    /**
     * This is mostly to force the initialization of the Crypto Implementation, avoiding it being done on runtime when its first needed.<BR>
     * In short it avoids the worst-case execution time on runtime by doing it on loading.
     * @param key Any private RSA Key just for testing purposes.
     * @throws GeneralSecurityException if a underlying exception was thrown by the Cipher
     */
    private void testCipher(RSAPrivateKey key) throws GeneralSecurityException
    {
        // avoid worst-case execution, KenM
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.DECRYPT_MODE, key);
    }    
	
    /**
     * 
     *
     */
    private void generateBlowFishKeys()
    {
        _blowfishKeys = new byte[BLOWFISH_KEYS][16];

        for (int i = 0; i < BLOWFISH_KEYS; i++)
        {
            for (int j = 0; j < _blowfishKeys[i].length; j++)
            {
                _blowfishKeys[i][j] = (byte) (Rnd.nextInt(255)+1);
            }
        }
        _log.info("Stored "+_blowfishKeys.length+" keys for Blowfish communication");
    }    
    
    /**
     * @return Returns a random key
     */
    public byte[] getBlowfishKey()
    {
        return _blowfishKeys[(int) (Math.random()*BLOWFISH_KEYS)];
    }
    
    /**
     * @return LoginManager singleton
     */
	public static LoginManager getInstance()
	{
		return _instance;
	}
	
    /**
     * 
     * @param account
     * @param client
     * @return a SessionKey
     */
	public SessionKey assignSessionKeyToLogin(String account, L2LoginClient client)
	{
		SessionKey key;
		
		key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
		_loginServerClients.put(account, client);
		return key;
	}
	
	public void removeAuthedLoginClient(String account)
	{
		_loginServerClients.remove(account);
	}

	public boolean isAccountInLoginServer(String account)
	{
		return _loginServerClients.containsKey(account);
	}
	
	public SessionKey assignSessionKeyToClient(String account, L2LoginClient client)
	{
		SessionKey key;

		key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
		_loginServerClients.put(account, client);
		return key;
	}	
	
	public GameServerInfo getAccountOnGameServer(String account)
	{
		Collection<GameServerInfo> serverList = GameServerManager.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
			{
				return gsi;
			}
		}
		return null;
	}	
	
	public boolean isAccountInAnyGameServer(String account)
	{
		Collection<GameServerInfo> serverList = GameServerManager.getInstance().getRegisteredGameServers().values();
		for (GameServerInfo gsi : serverList)
		{
			GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
     * 
     * @param account
     * @param password
     * @param client
     * @return true if validation succeed or false if we have technical problems
     * @throws HackingException if we detect a hacking attempt
     * @throws AccountBannedException if the use was banned
     * @throws AccountWrongPasswordException if the password was wrong
     */
	public AuthLoginResult tryAuthLogin(String account, String password, L2LoginClient client) 
    throws HackingException, AccountBannedException, AccountWrongPasswordException
	{
        AuthLoginResult ret = AuthLoginResult.INVALID_PASSWORD; 
        
        try
        {
        	//L2EMU_EDIT- multi logins config
        	// check auth
        	if (this.loginValid(account, password, client))
        	{
        		// login was successful, verify presence on Gameservers
        		ret = AuthLoginResult.ALREADY_ON_GS;

        		if(!Config.ALLOW_MULT_LOGIN_OF_SAME_ACC)
        		{
        			if (!this.isAccountInAnyGameServer(account))
        			{
        				// account isnt on any GS, verify LS itself
        				ret = AuthLoginResult.ALREADY_ON_LS;
        			}
        		}
        		// dont allow 2 simultaneous login
        		synchronized (_loginServerClients)
        		{
        			if (!_loginServerClients.containsKey(account))
        			{
        				_loginServerClients.put(account, client);
        				ret = AuthLoginResult.AUTH_SUCCESS;
        			}
        		}
        		// keep access level in the L2LoginClient
        		client.setAccessLevel(_service.getAccountById(account).getAccessLevel());
        	}
        	//L2EMU_EDIT_END - multi logins config
        }
        catch (NoSuchAlgorithmException e)
        {
            _log.error("could not check password:"+e);
            ret = AuthLoginResult.SYSTEM_ERROR;
        } 
        catch (UnsupportedEncodingException e)
        {
            _log.error("could not check password:"+e);
            ret = AuthLoginResult.SYSTEM_ERROR;
        } 
        catch (AccountModificationException e)
        {
            _log.warn("could not check password:"+e);
            ret = AuthLoginResult.SYSTEM_ERROR;
        } 
		return ret;
	}	
	public L2LoginClient getAuthedClient(String account)
	{
		return _loginServerClients.get(account);
	}
	
	public SessionKey getKeyForAccount(String account)
	{
		L2LoginClient client = _loginServerClients.get(account);
		if (client != null)
		{
			return client.getSessionKey();
		}
		return null;
	}
	
	/**
	 * Login is possible if number of player < max player for this GS
     * and the status of the GS != STATUS_GM_ONLY
     * All those conditions are not applied if the player is a GM
	 * @return
	 */
	public boolean isLoginPossible(int access, int serverId)
	{
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
		if (gsi != null && gsi.isAuthed())
		{
            return ((gsi.getCurrentPlayerCount() < gsi.getMaxPlayers() && gsi.getStatus() != ServerStatus.STATUS_GM_ONLY) || access >= Config.GM_MIN);
		}
		return false;
	}	
	
	
    /**
     * 
     * @param ServerID
     * @return online player count for a server
     */
	public int getOnlinePlayerCount(int serverId)
	{
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
		if (gsi != null && gsi.isAuthed())
		{
			return gsi.getCurrentPlayerCount();
		}
		return 0;
	}
	
	/***
     * 
     * @param ServerID
     * @return max allowed online player for a server
	 */
	public int getMaxAllowedOnlinePlayers(int id)
	{
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(id);
		if (gsi != null)
		{
			return gsi.getMaxPlayers();
		}
		return 0;
	}

    /**
     * 
     * @param user
     * @param banLevel
     */
	public void setAccountAccessLevel(String account, int banLevel)
	{
        try
        {
            _service.changeAccountLevel(account, banLevel);
        }
        catch (AccountModificationException e)
        {
            _log.error("Could not set accessLevl for user : " + account,e);
        }
	}
	
    /**
     * 
     * @param user
     * @return true if a user is a GM account
     */
	public boolean isGM(String user)
	{
        Accounts acc = _service.getAccountById(user);
        if ( acc != null )
            return acc.getAccessLevel() >= Config.GM_MIN;
        else
            return false;
                
	}
    
    /**
     * 
     * @param user
     * @return account if exist, null if not
     */
    public Accounts getAccount (String user)
    {
        return _service.getAccountById(user);
    }
	
	/**
	 * <p>This method returns one of the 10 {@link ScrambledKeyPair}.</p>
	 * <p>One of them the re-newed asynchronously using a {@link UpdateKeyPairTask} if necessary.</p>
	 * @return a scrambled keypair
	 */
	public ScrambledKeyPair getScrambledRSAKeyPair()
	{
		return _keyPairs[Rnd.nextInt(10)];
	}
	
	
	/**
	 * user name is not case sensitive any more
	 * @param user
	 * @param password
	 * @param address
     * @return true if all operations succeed
     * @throws NoSuchAlgorithmException if SHA is not supported
     * @throws UnsupportedEncodingException if UTF-8 is not supported
     * @throws AccountModificationException  if we were unable to modify the account
     * @throws AccountBannedException  if account is banned
     * @throws AccountWrongPasswordException if the password is wrong
	 */
	public boolean loginValid(String user, String password, L2LoginClient client) 
    throws NoSuchAlgorithmException, UnsupportedEncodingException, AccountModificationException, AccountBannedException, AccountWrongPasswordException
	{
		InetAddress address = client.getInetAddress();
		// player disconnected meanwhile 
        if (address == null) 
        {
            return false; 
        }
		return loginValid(user,password,address);
	}	
	
	/**
	 * user name is not case sensitive any more
	 * @param user
	 * @param password
	 * @param address
	 * @return true if all operations succeed
	 * @throws NoSuchAlgorithmException if SHA is not supported
	 * @throws UnsupportedEncodingException if UTF-8 is not supported
	 * @throws AccountModificationException  if we were unable to modify the account
	 * @throws AccountBannedException  if account is banned
	 * @throws AccountWrongPasswordException if the password is wrong
	 */
	public boolean loginValid(String user, String password, InetAddress address) 
    throws NoSuchAlgorithmException, UnsupportedEncodingException, AccountModificationException, AccountBannedException, AccountWrongPasswordException  
	{
        _logLoginTries.info("User trying to connect  '"+user+"' "+(address == null ? "null" : address.getHostAddress()));

        // o Convert password in utf8 byte array
        // ----------------------------------
        MessageDigest md = MessageDigest.getInstance("SHA");
        byte[] raw = password.getBytes("UTF-8");
        byte[] hash = md.digest(raw);            
        
        // o find Account
        // -------------
		Accounts acc = _service.getAccountById(user);
        
        // If account is not found
        // try to create it if AUTO_CREATE_ACCOUNTS is activated
        // or return false
        // ------------------------------------------------------
		if (acc == null)
		{
			return handleAccountNotFound(user, address, hash);
		}
        // If account is found
        // check ban state
        // check password and update last ip/last active
        // ---------------------------------------------
        else
        {
            // check the account is not ban
            if ( acc.getAccessLevel() < 0 )
            {
                throw new AccountBannedException (user);
            }
            try
            {
                checkPassword(hash,acc);
    			acc.setLastactive(new BigDecimal(System.currentTimeMillis()));
                if ( address != null )
                {
                    acc.setLastIp(address.getHostAddress());
                }
                _service.addOrUpdateAccount(acc);
                handleGoodLogin(user, address);
            }
            // If password are different
            // -------------------------
            catch (AccountWrongPasswordException e)
            {
                handleBadLogin(user, password, address);
                throw e;
            }
        }
		
		return true;
	}

    /**
     * @param user
     * @param address
     */
    private void handleGoodLogin(String user, InetAddress address)
    {
        // for long running servers, this should prevent blocking 
        // of users that mistype their passwords once every day :)
        if ( address != null )
        {
            _hackProtection.remove(address.getHostAddress());
        }
        if (_logLogin.isDebugEnabled())_log.debug("login successfull for '"+user+"' "+(address == null ? "null" : address.getHostAddress()));
    }

    /**
     * 
     * If login are different, increment hackProtection counter. It's maybe a hacking attempt
     * 
     * @param user
     * @param password
     * @param address
     */
    private void handleBadLogin(String user, String password, InetAddress address)
    {
        _logLoginFailed.info("login failed for user : '"+user+"' "+(address == null ? "null" : address.getHostAddress()));
        
        // In special case, adress is null, so this protection is useless
        if (address != null )
        {
    		FailedLoginAttempt failedAttempt = _hackProtection.get(address);
    		int failedCount;
    		if (failedAttempt == null)
    		{
    			_hackProtection.put(address, new FailedLoginAttempt(address, password));
    			failedCount = 1;
    		}
    		else
    		{
    			failedAttempt.increaseCounter(password);
    			failedCount = failedAttempt.getCount();
    		}
    
    		if (failedCount >= Config.LOGIN_TRY_BEFORE_BAN)
    		{
    			_log.info("Banning '"+address.getHostAddress()+"' for "+Config.LOGIN_BLOCK_AFTER_BAN+" seconds due to "+failedCount+" invalid user/pass attempts");
    			BanManager.getInstance().addBanForAddress(address, Config.LOGIN_BLOCK_AFTER_BAN*1000);
    		}
        }
    }

    /**
     * @param hash
     * @param acc 
     * @throws AccountWrongPasswordException if password is wrong 
     */
    private void checkPassword(byte[] hash, Accounts acc) 
    throws AccountWrongPasswordException
    {
        if (_log.isDebugEnabled() )_log.debug("account exists");
        
        byte[] expected = Base64.decode(acc.getPassword());
        
        for (int i=0;i<expected.length;i++)
        {
        	if (hash[i] != expected[i])
        	{
        		throw new AccountWrongPasswordException(acc.getLogin());
        	}
        }
    }

    /**
     * @param user
     * @param address
     * @param hash
     * @return true if accounts was successfully created or false is AUTO_CREATE_ACCOUNTS = false or creation failed
     * @throws AccountModificationException
     */
    private boolean handleAccountNotFound(String user, InetAddress address, byte[] hash) throws AccountModificationException
    {
        Accounts acc;
        if (Config.AUTO_CREATE_ACCOUNTS)
        {
        	if ((user.length() >= 2) && (user.length() <= 14))
        	{
                acc = new Accounts(user,Base64.encodeBytes(hash),new BigDecimal(System.currentTimeMillis()),0,(address == null ? "null" : address.getHostAddress()));
        		_service.addOrUpdateAccount(acc);
        		
                _logLogin.info("created new account for "+ user);

                if ( L2LoginServer.statusServer != null )
        			L2LoginServer.statusServer.sendMessageToTelnets("Account created for player "+user);
        		
        		return true;
        		
        	}
            _logLogin.warn("Invalid username creation/use attempt: "+user);
        	return false;
        }
        _logLogin.warn("account missing for user "+user);
        return false;
    }
}
