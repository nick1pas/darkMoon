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
package net.sf.l2j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import net.sf.l2j.loginserver.services.PathFindingService;

/**
 * This class containce global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * It's initialized at the very begin of startup, and later JIT will optimize
 * away debug/unused code.
 * 
 * @author mkizub
 */
public final class Config {

	//private static final Log _log = LogFactory.getLog(Config.class.getName());

	//=================================================================================
	public static final String  DB_FILE = PathFindingService.DB_FILE;
	//=================================================================================
	public static String DATABASE_DRIVER;    // Driver to access to database
	public static String DATABASE_URL;        // Path to access to database 
	public static String DATABASE_LOGIN;      // Database login 
	public static String DATABASE_PASSWORD;   //Database password 

	//*****************************************************************************
	public static void LoadDBSettings()
	{
		System.out.println("Loading: "+DB_FILE+".");
		try 
		{
			Properties DBSettings    = new Properties();
			InputStream is           = new FileInputStream(new File(DB_FILE));  
			DBSettings.load(is);
			is.close();

			DATABASE_DRIVER          = DBSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL             = DBSettings.getProperty("URL", "jdbc:mysql://localhost/Emu_DB");
			DATABASE_LOGIN           = DBSettings.getProperty("Login", "root");
			DATABASE_PASSWORD        = DBSettings.getProperty("Password", "root");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+DB_FILE+" File.");
		}

	}
	//=======================================================================================
	public static final String  TELNET_FILE					= PathFindingService.TELNET_FILE;
	//=======================================================================================
	public static boolean IS_TELNET_ENABLED;       // Is telnet enabled ? 

	//********************************************************************
	public static void loadTelnetConfig()
	{
		System.out.println("Loading: "+TELNET_FILE+".");
		try
		{
			Properties telnetSettings   = new Properties();
			InputStream is              = new FileInputStream(new File(TELNET_FILE));  
			telnetSettings.load(is);
			is.close();

			IS_TELNET_ENABLED   = Boolean.valueOf(telnetSettings.getProperty("EnableTelnet", "false"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+TELNET_FILE+" File.");
		}

	}
	//==========================================================================================
	public static final String  NETWORK_FILE    = PathFindingService.NETWORK_FILE;
	//=======================================================================================
	public static String  LOGIN_SERVER_HOSTNAME;  //Client login port/host 
	public static String  LOGIN_HOSTNAME;       // GameServer login port/host 
	public static int     LOGIN_SERVER_PORT;
	public static int     LOGIN_PORT;
	public static int     IP_UPDATE_TIME;

	//*******************************************************************************************
	public static void loadNetworkConfig()
	{
		System.out.println("Loading: "+NETWORK_FILE+".");
		try 
		{
			Properties NetworkSettings    = new Properties();
			InputStream is                = new FileInputStream(new File(NETWORK_FILE));  
			NetworkSettings.load(is);
			is.close();

			IP_UPDATE_TIME         = Integer.parseInt(NetworkSettings.getProperty("IpUpdateTime","0")) * 60 * 1000;
			LOGIN_SERVER_PORT      = Integer.parseInt(NetworkSettings.getProperty("LoginServerPort","2106"));
			LOGIN_HOSTNAME         = NetworkSettings.getProperty("LoginHostName","127.0.0.1");
			LOGIN_SERVER_HOSTNAME  = NetworkSettings.getProperty("LoginServerHostName","0.0.0.0");
			LOGIN_PORT             = Integer.parseInt(NetworkSettings.getProperty("LoginPort","9014"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+NETWORK_FILE+" File.");
		}
	}

	//==========================================================================================
	public static final String  BAN_FILE    = PathFindingService.BAN_FILE;
	//===========================================================================================
	public static int LOGIN_TRY_BEFORE_BAN;    // Number of login tries before IP ban gets activated, default 10*/       
	public static int LOGIN_BLOCK_AFTER_BAN;   // Number of seconds the IP ban will last, default 10 minutes */

	//*******************************************************************************************
	public static void loadBanConfig()
	{
		System.out.println("Loading: "+BAN_FILE+".");
		try 
		{
			Properties BanSettings       = new Properties();
			InputStream is               = new FileInputStream(new File(BAN_FILE));  
			BanSettings.load(is);
			is.close();

			LOGIN_TRY_BEFORE_BAN   = Integer.parseInt(BanSettings.getProperty("LoginTryBeforeBan", "10"));
			LOGIN_BLOCK_AFTER_BAN  = Integer.parseInt(BanSettings.getProperty("LoginBlockAfterBan", "600"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+BAN_FILE+" File.");
		}
	}

	//==========================================================================================
	public static final String  DEV_FILE    = PathFindingService.DEV_FILE;
	//===========================================================================================
	public static boolean DEVELOPER;           //Enable/disable code 'in progress' 

	//*******************************************************************************************
	public static void loadDevConfig()
	{
		System.out.println("Loading: "+DEV_FILE+".");
		try 
		{
			Properties DevSettings    = new Properties();
			InputStream is               = new FileInputStream(new File(DEV_FILE));  
			DevSettings.load(is);
			is.close();
			DEVELOPER    = Boolean.parseBoolean(DevSettings.getProperty("Developer", "false"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+DEV_FILE+" File.");
		}
	}

	//L2EMU_ADD_START
	//==========================================================================================
	public static final String  SECURITY_FILE    = PathFindingService.SECURITY_FILE;
	//===========================================================================================
	public static int      FAST_CONNECTION_LIMIT;
	public static int      NORMAL_CONNECTION_TIME;
	public static int      FAST_CONNECTION_TIME;
	public static int      MAX_CONNECTION_PER_IP;
	public static boolean  FLOOD_PROTECTION;
	//*************************************************************************************************
	public static void loadSecuritySettings()
	{
		System.out.println("Loading: "+SECURITY_FILE+".");
		try 
		{
			Properties securitySettings    = new Properties();
			InputStream is                 = new FileInputStream(new File(SECURITY_FILE));  
			securitySettings.load(is);
			is.close();

			
			FLOOD_PROTECTION       = Boolean.parseBoolean(securitySettings.getProperty("EnableFloodProtection","true"));
			FAST_CONNECTION_LIMIT  = Integer.parseInt(securitySettings.getProperty("FastConnectionLimit","15"));
			NORMAL_CONNECTION_TIME = Integer.parseInt(securitySettings.getProperty("NormalConnectionTime","700"));
			FAST_CONNECTION_TIME   = Integer.parseInt(securitySettings.getProperty("FastConnectionTime","350"));
			MAX_CONNECTION_PER_IP  = Integer.parseInt(securitySettings.getProperty("MaxConnectionPerIP","50"));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+SECURITY_FILE+" File.");
		}
	}
	//L2EMU_ADD_END

	//==========================================================================================
	public static final String  LOGIN_FILE    = PathFindingService.LOGIN_FILE;
	//===========================================================================================
	public static boolean SHOW_LICENCE;          // Show licence or not just after login (if false, will directly go to the Server List */
	public static boolean ACCEPT_NEW_GAMESERVER; // Accept new game server ? 
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static int     GM_MIN;
	public static boolean ALLOW_MULT_LOGIN_OF_SAME_ACC;
	//********************************************************************************************
	public static void loadlsConfig()
	{
		System.out.println("Loading: "+LOGIN_FILE+".");
		try
		{
			Properties serverSettings    = new Properties();
			InputStream is               = new FileInputStream(new File(LOGIN_FILE));  
			serverSettings.load(is);
			is.close();
			ALLOW_MULT_LOGIN_OF_SAME_ACC = Boolean.parseBoolean(serverSettings.getProperty("AllowMultipleLoginOnSameAcc","false"));
			ACCEPT_NEW_GAMESERVER  = Boolean.parseBoolean(serverSettings.getProperty("AcceptNewGameServer","false"));
			GM_MIN                 = Integer.parseInt(serverSettings.getProperty("GMMinLevel", "100"));
			SHOW_LICENCE           = Boolean.parseBoolean(serverSettings.getProperty("ShowLicence", "true"));
			AUTO_CREATE_ACCOUNTS   = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts","false"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+LOGIN_FILE+" File.");
		}
	}

	//********************************************************************************************
	public static void load()
	{  
		System.out.println("Initializing Config Files Please Wait...");
		loadBanConfig();
		LoadDBSettings();
		loadDevConfig();
		loadlsConfig();
		loadNetworkConfig();
		loadTelnetConfig();
		LoadDBSettings();
		//L2EMU_ADD_START
		loadSecuritySettings();
		//L2EMU_ADD_END

		//Initialize config properties for DB
		// ----------------------------------
		initDBProperties();
	}

	// it has no instancies
	private Config() {}

	/**
	 * To keep compatibility with old loginserver.properties, add db properties into system properties
	 * Spring will use those values later
	 */
	public static void initDBProperties() 
	{
		System.setProperty("net.sf.l2j.db.driverclass", DATABASE_DRIVER );
		System.setProperty("net.sf.l2j.db.urldb", DATABASE_URL );
		System.setProperty("net.sf.l2j.db.user", DATABASE_LOGIN );
		System.setProperty("net.sf.l2j.db.password", DATABASE_PASSWORD );		
	}
}