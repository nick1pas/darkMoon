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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.l2j.gameserver.services.PathFindingService;
import net.sf.l2j.gameserver.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * It has static final fields initialized from configuration files.<br>
 * It's initialized at the very begin of startup.<br>
 * and later JIT will optimize away debug/unused code.<br>
 * 
 * @author mkizub <br>
 * 
 * @Rewritten by Rayan RPG for L2Emu Project!
 */
public final class Config
{   
	protected static Log _log = LogFactory.getLog(Config.class.getName());
	
	//=============================================================================================
	public static final String  GAMESERVER_FILE     =  PathFindingService.GAMESERVER_FILE;
	//=============================================================================================
	public static int           REQUEST_ID;
	public static int           MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	public static int           MAXIMUM_ONLINE_USERS;           // Maximum number of players allowed to play simultaneously on server
	public static int           MIN_PROTOCOL_REVISION;          // protocol revision
	public static int           MAX_PROTOCOL_REVISION;
	public static int           NEW_NODE_ID;
	public static int           SELECTED_NODE_ID;
	public static int           LINKED_NODE_ID;
	public static int           LOGIN_RESTART_TIME;             // Restarttime for Loginserver */
	public static boolean 		RESERVE_HOST_ON_LOGIN = false;
	public static boolean 		ACCEPT_ALTERNATE_ID; 			// Accept alternate ID for server ?
	public static boolean 		SERVER_LIST_BRACKET;			// Displays [] in front of server name ?
	public static boolean 		SERVER_LIST_CLOCK;				// Displays a clock next to the server name ?
	public static boolean       SERVER_GMONLY;
	public static boolean       LOGIN_RESTART_WITH_GAMESERVER;  // Restart Loginserver together with Gameserver */
	public static boolean       LOGIN_RESTART_BY_TIME;          // Restart Loginserver by time */
	public static Pattern       CNAME_PATTERN;
	public static Pattern       PET_NAME_PATTERN;      
	public static Pattern       CLAN_ALLY_NAME_PATTERN;        
	public static Pattern       TITLE_PATTERN;  
	public static File          DATAPACK_ROOT;                  // Datapack root directory
	public static String        NEW_NODE_TYPE;
   
	//**********************************************************************************************
	public static void loadGsConfig()
	{
		System.out.println("Loading: "+GAMESERVER_FILE);
		try
		{
			Properties gsSettings    = new L2Properties();
			InputStream is           = new FileInputStream(new File(GAMESERVER_FILE));
			gsSettings.load(is);
			is.close();
			
			REQUEST_ID                                = Integer.parseInt(gsSettings.getProperty("RequestServerID", "1"));
			if(REQUEST_ID<=0)
			   REQUEST_ID   = 1;
			LOGIN_RESTART_WITH_GAMESERVER	          = Boolean.valueOf(gsSettings.getProperty("LoginRestartWithGameserver", "false"));
            LOGIN_RESTART_BY_TIME			          = Boolean.valueOf(gsSettings.getProperty("LoginRestartByTime", "false"));
            LOGIN_RESTART_TIME				          = Integer.parseInt(gsSettings.getProperty("LoginRestartTime", "60"));
			MAXIMUM_ONLINE_USERS                      = Integer.parseInt(gsSettings.getProperty("MaximumOnlineUsers", "100"));
			MAX_CHARACTERS_NUMBER_PER_ACCOUNT         = Integer.parseInt(gsSettings.getProperty("CharMaxNumber", "0"));
			ACCEPT_ALTERNATE_ID                       = Boolean.parseBoolean(gsSettings.getProperty("AcceptAlternateID","true"));
			DATAPACK_ROOT                             = new File(gsSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
			MAXIMUM_ONLINE_USERS                      = Integer.parseInt(gsSettings.getProperty("MaximumOnlineUsers", "100"));
			MIN_PROTOCOL_REVISION                     = Integer.parseInt(gsSettings.getProperty("MinProtocolRevision", "694"));
			MAX_PROTOCOL_REVISION                     = Integer.parseInt(gsSettings.getProperty("MaxProtocolRevision", "709"));
			
			//checks protocols if are ok.
			if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
			{ 
				throw new Error("GameServer: Mininum Protocol Revision is bigger than Max Protocol Revision in GameServer Configuration File.");
			} 
			
			//checks character name patterns
			try
			{
				CNAME_PATTERN           = Pattern.compile(gsSettings.getProperty("CnameTemplate", "[A-Za-z0-9\\-]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				_log.warn("GameServer: Character name pattern is wrong!",e);
				CNAME_PATTERN  = Pattern.compile("[A-Za-z0-9\\-]{3,16}");
			}
			
			//checks pet names patterns
			try
			{
				PET_NAME_PATTERN        = Pattern.compile(gsSettings.getProperty("PetNameTemplate", "[A-Za-z0-9\\-]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				_log.warn("GameServer: Pet name pattern is wrong!",e);
				PET_NAME_PATTERN  = Pattern.compile("[A-Za-z0-9\\-]{3,16}");
			}
			
			//checks clan / ally names patterns
			try
			{
				CLAN_ALLY_NAME_PATTERN  = Pattern.compile(gsSettings.getProperty("ClanAllyNameTemplate", "[A-Za-z0-9 \\-]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				_log.warn("GameServer: Clan and ally name pattern is wrong!",e);
				CLAN_ALLY_NAME_PATTERN  = Pattern.compile("[A-Za-z0-9 \\-]{3,16}");
			}
			
			//checks title patterns
			try
			{
				TITLE_PATTERN           = Pattern.compile(gsSettings.getProperty("TitleTemplate", "[A-Za-z0-9 \\\\[\\\\]\\(\\)\\<\\>\\|\\!]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				_log.warn("GameServer: Character title pattern is wrong!",e);
				TITLE_PATTERN  = Pattern.compile("[A-Za-z0-9 \\\\[\\\\]\\(\\)\\<\\>\\|\\!]{3,16}");
			}
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+GAMESERVER_FILE+" File.");
		} 
	}
	
	//===================================================================================================
	public static final String  DATABASE_FILE   = PathFindingService.DATABASE_FILE;
	//===================================================================================================
	public static int    DATABASE_MAX_CONNECTIONS;
	public static String DATABASE_DRIVER;
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;

	//**************************************************************************************************
	public static void loadDbConfig()
	{
		System.out.println("Loading: "+DATABASE_FILE);
		try
		{
			Properties dbSettings    = new L2Properties();
			InputStream is           = new FileInputStream(new File(DATABASE_FILE));
			dbSettings.load(is);
			is.close();
			
			DATABASE_DRIVER                           = dbSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL                              = dbSettings.getProperty("URL", "jdbc:mysql://localhost/Emu_DB");
			DATABASE_LOGIN                            = dbSettings.getProperty("Login", "root");
			DATABASE_PASSWORD                         = dbSettings.getProperty("Password", "");
			DATABASE_MAX_CONNECTIONS                  = Integer.parseInt(dbSettings.getProperty("MaximumDbConnections", "50"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load: "+DATABASE_FILE+" File.");
		} 
	}
	//=====================================================================================================
	public static final String  NPC_BUFFER_FILE  = PathFindingService.NPC_BUFFER_FILE;
	//=====================================================================================================
	public static int      PRICE_PER_PROPHET_BUFF;
	public static int      PRICE_PER_DANCE;
	public static int      PRICE_PER_SONG;
	public static int      PRICE_PER_ORC_BUFF;
	public static int      PRICE_PER_CLAN_BUFF;
	public static int      SHAMAN_BUFFS_PRICE;
	public static int      WARCRYER_BUFFS_PRICE;
	public static int      OVERLORD_BUFFS_PRICE;
	public static int      PRICE_PER_CUBIC;
	public static int      PRICE_PER_SUMMON_BUFF;  
	public static int      PRICE_PER_HERO_BUFF;
	public static int      PRICE_PER_NOBLE_BUFF;
	public static int      FULL_PROPHET_BUFFS_PRICE;
	public static int      FULL_DANCES_PRICE;
	public static int      FULL_SONGS_PRICE;
	public static int      FULL_CLAN_BUFFS_PRICE;
	public static int      FULL_CUBICS_PRICE;
	public static int      FULL_SUMMON_BUFFS_PRICE; 
	public static int      FULL_HERO_BUFFS_PRICE;
	public static int      FULL_NOBLE_BUFFS_PRICE;
	public static int      MAX_LEVEL_TO_GET_BUFFS;
	public static int      MIN_LEVEL_TO_GET_BUFFS;
	public static int      MAX_SUMMON_LEVEL_TO_GET_BUFFS;
	public static int      MIN_SUMMON_LEVEL_TO_GET_BUFFS;
	public static int      PRICE_PER_CP_POINT;
	public static int      PRICE_PER_HP_POINT;
	public static int      PRICE_PER_MP_POINT;
	public static int      PRICE_PER_SUMMON_HP_POINT;
	public static int      PRICE_PER_SUMMON_MP_POINT;
	public static int      SP_MULTIPLIER;
	public static boolean  CAN_SEEL_HERO_BUFFS;
	public static boolean  CAN_SEEL_NOBLE_BUFFS;
	public static boolean  CAN_SEEL_SUMMON_BUFFS;
	public static boolean  CAN_SEEL_PROPHET_BUFFS;
	public static boolean  CAN_SEEL_SONGS;
	public static boolean  CAN_SEEL_DANCES;
	public static boolean  CAN_SEEL_CUBICS_BUFFS;
	public static boolean  CAN_SEEL_ORC_BUFFS;
	public static boolean  CAN_SEEL_SHAMAN_BUFFS;
	public static boolean  CAN_SEEL_OVERLORD_BUFFS;
	public static boolean  CAN_SEEL_WARCRYER_BUFFS;
	public static boolean  CAN_SEEL_FULL_HERO_BUFFS;
	public static boolean  CAN_SEEL_FULL_NOBLE_BUFFS;
	public static boolean  CAN_SEEL_FULL_SUMMON_BUFFS;
	public static boolean  CAN_SEEL_FULL_PROPHET_BUFFS;
	public static boolean  CAN_SEEL_FULL_SONGS;
	public static boolean  CAN_SEEL_FULL_DANCES;
	public static boolean  CAN_SEEL_FULL_CUBICS_BUFFS;
	public static boolean  CAN_SEEL_FULL_ORC_BUFFS;
	public static boolean  CAN_SEEL_FULL_SHAMAN_BUFFS;
	public static boolean  CAN_SEEL_FULL_OVERLORD_BUFFS;
	public static boolean  CAN_SEEL_FULL_WARCRYER_BUFFS;
	public static boolean  DONATOR_BUFFER_REQUEST_ADENA;
	public static boolean  REGENERATE_STATS_FOR_FREE;
	public static boolean  DISABLE_ANIMATION;
	public static boolean  DISABLE_NPC_BUFFER_DURING_SIEGE;
	public static boolean  ALLOW_NPC_BUFFER;
	public static boolean  ALLOW_KARMA_PLAYER;
	//*****************************************************************************************
	
	public static void loadNpcBufferConfig()
	{
		System.out.println("Loading: "+NPC_BUFFER_FILE);
		try 
		{ 
			Properties npcBufferSettings       = new L2Properties();
			InputStream is                     = new FileInputStream(new File(NPC_BUFFER_FILE));
			npcBufferSettings.load(is);
			is.close();
			 
			ALLOW_NPC_BUFFER                         = Boolean.valueOf(npcBufferSettings.getProperty("AllowNpcBuffer", "false"));
			ALLOW_KARMA_PLAYER                       = Boolean.valueOf(npcBufferSettings.getProperty("AllowKarmaPlayer", "false"));
			DISABLE_NPC_BUFFER_DURING_SIEGE          = Boolean.parseBoolean(npcBufferSettings.getProperty("DisableNpcBufferDuringSiege","false"));
			DISABLE_ANIMATION                        = Boolean.parseBoolean(npcBufferSettings.getProperty("DisableAnimation","false"));
			DONATOR_BUFFER_REQUEST_ADENA             = Boolean.parseBoolean(npcBufferSettings.getProperty("AdenaNeeded","false"));
			FULL_HERO_BUFFS_PRICE                    = Integer.parseInt(npcBufferSettings.getProperty("FullHeroBuffsPrice","10000"));
			FULL_NOBLE_BUFFS_PRICE                   = Integer.parseInt(npcBufferSettings.getProperty("FullNobleBuffsPrice","10000"));
			PRICE_PER_SUMMON_BUFF                    = Integer.parseInt(npcBufferSettings.getProperty("PricePerSummonBuff","10000"));
			PRICE_PER_HERO_BUFF                      = Integer.parseInt(npcBufferSettings.getProperty("PricePerHeroBuff","10000"));
			PRICE_PER_NOBLE_BUFF                     = Integer.parseInt(npcBufferSettings.getProperty("PricePerNobleBuff","10000"));
			PRICE_PER_SUMMON_HP_POINT                = Integer.parseInt(npcBufferSettings.getProperty("PricePerSummonHpPoint","8"));
			PRICE_PER_SUMMON_MP_POINT                = Integer.parseInt(npcBufferSettings.getProperty("PricePerSummonMpPoint","8"));
			FULL_SUMMON_BUFFS_PRICE                  = Integer.parseInt(npcBufferSettings.getProperty("FullSummonBuffsPrice","50000"));
			PRICE_PER_PROPHET_BUFF                   = Integer.parseInt(npcBufferSettings.getProperty("ProphetBuffPrice","10000"));
			PRICE_PER_SONG                           = Integer.parseInt(npcBufferSettings.getProperty("SongPrice","10000"));
			PRICE_PER_DANCE                          = Integer.parseInt(npcBufferSettings.getProperty("DancePrice","10000"));
			PRICE_PER_ORC_BUFF                       = Integer.parseInt(npcBufferSettings.getProperty("OrcBuffPrice","10000"));
			PRICE_PER_CLAN_BUFF                      = Integer.parseInt(npcBufferSettings.getProperty("ClanBuffPrice","10000"));
			SHAMAN_BUFFS_PRICE                       = Integer.parseInt(npcBufferSettings.getProperty("FullShamanBuffPrice","50000"));
			WARCRYER_BUFFS_PRICE                     = Integer.parseInt(npcBufferSettings.getProperty("FullWarcryerBuffPrice","50000"));
			OVERLORD_BUFFS_PRICE                     = Integer.parseInt(npcBufferSettings.getProperty("FullOverlordBuffPrice","50000"));
			FULL_PROPHET_BUFFS_PRICE                 = Integer.parseInt(npcBufferSettings.getProperty("FullProphetBuffsPrice","200000"));
			FULL_DANCES_PRICE                        = Integer.parseInt(npcBufferSettings.getProperty("FullDancesPrice","100000"));
			FULL_SONGS_PRICE                         = Integer.parseInt(npcBufferSettings.getProperty("FullSongsDancePrice","100000"));
			FULL_CLAN_BUFFS_PRICE                    = Integer.parseInt(npcBufferSettings.getProperty("FullClanBuffsPrice","150000"));
			SP_MULTIPLIER                            = Integer.parseInt(npcBufferSettings.getProperty("SpMultiplier","1"));
			PRICE_PER_CP_POINT                       = Integer.parseInt(npcBufferSettings.getProperty("PricePerCpPoint","8"));
			PRICE_PER_HP_POINT                       = Integer.parseInt(npcBufferSettings.getProperty("PricePerHpPoint","8"));
			PRICE_PER_MP_POINT                       = Integer.parseInt(npcBufferSettings.getProperty("PricePerMpPoint","8"));
			MIN_LEVEL_TO_GET_BUFFS                   = Integer.parseInt(npcBufferSettings.getProperty("MinLevelToGetBuffs","1"));
			MAX_LEVEL_TO_GET_BUFFS                   = Integer.parseInt(npcBufferSettings.getProperty("MaxLevelToGetBuffs","80"));
			MIN_SUMMON_LEVEL_TO_GET_BUFFS            = Integer.parseInt(npcBufferSettings.getProperty("MinSummonLevelToGetBuffs","1"));
			MAX_SUMMON_LEVEL_TO_GET_BUFFS            = Integer.parseInt(npcBufferSettings.getProperty("MaxSummonLevelToGetBuffs","80"));
			PRICE_PER_CUBIC                          = Integer.parseInt(npcBufferSettings.getProperty("PricePerCubic","10000"));
			FULL_CUBICS_PRICE                        = Integer.parseInt(npcBufferSettings.getProperty("FullCubicsPrice","100000"));
			REGENERATE_STATS_FOR_FREE                = Boolean.parseBoolean(npcBufferSettings.getProperty("RegenarateStatsForFree","false")); 
			CAN_SEEL_HERO_BUFFS                      = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellHeroBuffs","true"));
			CAN_SEEL_NOBLE_BUFFS                     = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellNobleBuffs","true"));
			CAN_SEEL_SUMMON_BUFFS                    = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellSummonBuffs","true"));
			CAN_SEEL_PROPHET_BUFFS                   = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellProhetBuffs","true"));
			CAN_SEEL_SONGS                           = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellSongs","true"));
			CAN_SEEL_DANCES                          = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellDances","true"));
			CAN_SEEL_CUBICS_BUFFS                    = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellCubics","true"));
			CAN_SEEL_ORC_BUFFS                       = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellOrcBuffs","true"));
			CAN_SEEL_SHAMAN_BUFFS                    = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellShamanBuffs","true"));
			CAN_SEEL_OVERLORD_BUFFS                  = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellOverlordBuffs","true"));
			CAN_SEEL_WARCRYER_BUFFS                  = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellWarcryerBuffs","true"));
			CAN_SEEL_FULL_HERO_BUFFS                 = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullHeroBuffs","true"));
			CAN_SEEL_FULL_NOBLE_BUFFS                = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullNobleBuffs","true"));
			CAN_SEEL_FULL_SUMMON_BUFFS               = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullSummonBuffs","true"));
			CAN_SEEL_FULL_PROPHET_BUFFS              = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullProhetBuffs","true"));
			CAN_SEEL_FULL_SONGS                      = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullSongs","true"));
			CAN_SEEL_FULL_DANCES                     = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullDances","true"));
			CAN_SEEL_FULL_CUBICS_BUFFS               = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullCubics","true"));
			CAN_SEEL_FULL_ORC_BUFFS                  = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullOrcBuffs","true"));
			CAN_SEEL_FULL_SHAMAN_BUFFS               = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullShamanBuffs","true"));
			CAN_SEEL_FULL_OVERLORD_BUFFS             = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullOverlordBuffs","true"));
			CAN_SEEL_FULL_WARCRYER_BUFFS             = Boolean.parseBoolean(npcBufferSettings.getProperty("AllowNpcBufferToSellFullWarcryerBuffs","true"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + NPC_BUFFER_FILE + " File.");
		} 
	}
	//=====================================================================================================
	public static final String  NPC_ENCHANTER_FILE  = PathFindingService.NPC_ENCHANTER_FILE;
	//=====================================================================================================
	public static boolean ALLOW_NPC_ENCHANTER;
	public static boolean ALLOW_ENCHANT_WEAPONS;
	public static boolean ALLOW_ENCHANT_ARMORS;
	public static boolean ALLOW_HERO_STUFF_ENCHANT;
	public static boolean DONATOR_FREE_ENCHANT;
	public static boolean GM_FREE_ENCHANT;
	public static int ARMOR_D_ENCH_PRICE;
	public static int ARMOR_C_ENCHANT_PRICE;
	public static int ARMOR_B_ENCHANT_PRICE;
	public static int ARMOR_A_ENCHANT_PRICE;
	public static int ARMOR_S_ENCHANT_PRICE;
	public static int WPN_D_ENCHANT_PRICE;
	public static int WPN_C_ENCHANT_PRICE;
	public static int WPN_B_ENCHANT_PRICE;
	public static int WPN_A_ENCHANT_PRICE;
	public static int WPN_S_ENCHANT_PRICE;
	//****************************************************************************************
	public static void loadNpcEnchConfig()
	{
		System.out.println("Loading: "+NPC_ENCHANTER_FILE);
		try 
		{ 
			Properties npcEnchSettings       = new L2Properties();
			InputStream is                   = new FileInputStream(new File(NPC_ENCHANTER_FILE));
			npcEnchSettings.load(is);
			is.close();

			ALLOW_NPC_ENCHANTER       = Boolean.parseBoolean(npcEnchSettings.getProperty("AllowNpcEnchanter","false"));
			ALLOW_ENCHANT_WEAPONS     = Boolean.parseBoolean(npcEnchSettings.getProperty("AllowEnchantWeapons","false"));
			ALLOW_ENCHANT_ARMORS      = Boolean.parseBoolean(npcEnchSettings.getProperty("AllowEnchantArmors","false"));
			ALLOW_HERO_STUFF_ENCHANT  = Boolean.parseBoolean(npcEnchSettings.getProperty("AllowHeroEnchantStuff","true"));
			DONATOR_FREE_ENCHANT      = Boolean.parseBoolean(npcEnchSettings.getProperty("DonatorFreeEnchant","true"));
			GM_FREE_ENCHANT           = Boolean.parseBoolean(npcEnchSettings.getProperty("GMFreeEnchant","true"));
			ARMOR_D_ENCH_PRICE        = Integer.parseInt(npcEnchSettings.getProperty("ArmorDEnchantPrice","50000"));
			ARMOR_C_ENCHANT_PRICE     = Integer.parseInt(npcEnchSettings.getProperty("ArmorCEnchantPrice","50000"));
			ARMOR_B_ENCHANT_PRICE     = Integer.parseInt(npcEnchSettings.getProperty("ArmorBEnchantPrice","50000"));
			ARMOR_A_ENCHANT_PRICE     = Integer.parseInt(npcEnchSettings.getProperty("ArmorAEnchantPrice","50000"));
			ARMOR_S_ENCHANT_PRICE     = Integer.parseInt(npcEnchSettings.getProperty("ArmorSEnchantPrice","50000"));
			WPN_D_ENCHANT_PRICE       = Integer.parseInt(npcEnchSettings.getProperty("WeaponDEnchantPrice","50000"));
			WPN_C_ENCHANT_PRICE       = Integer.parseInt(npcEnchSettings.getProperty("WeaponCEnchantPrice","50000"));
			WPN_B_ENCHANT_PRICE       = Integer.parseInt(npcEnchSettings.getProperty("WeaponBEnchantPrice","50000"));
			WPN_A_ENCHANT_PRICE       = Integer.parseInt(npcEnchSettings.getProperty("WeaponAEnchantPrice","50000"));
			WPN_S_ENCHANT_PRICE       = Integer.parseInt(npcEnchSettings.getProperty("WeaponSEnchantPrice","50000"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + NPC_BUFFER_FILE + " File.");
		} 
	}
	//=====================================================================================================
	public static final String  NPC_CHLEVEL_FILE  = PathFindingService.NPC_CHANGELEVEL_FILE;
	//=====================================================================================================
	public static boolean ALLOW_NPC_CHANGELEVEL;
	public static int CUSTOM_DECREASE_PRICE0;
	public static int CUSTOM_DECREASE_PRICE1;
	public static int CUSTOM_DECREASE_PRICE2;
	public static int CUSTOM_DECREASE_PRICE3;
	public static int CUSTOM_COEFF_DIVISIONPRICE_R4; /*for RaceID = 4*/
	//****************************************************************************************
	public static void loadNpcChLevelConfig()
	{
		System.out.println("Loading: "+NPC_CHLEVEL_FILE);
		try 
		{ 
			Properties npcChLevelSettings       = new L2Properties();
			InputStream is                   = new FileInputStream(new File(NPC_CHLEVEL_FILE));
			npcChLevelSettings.load(is);
			is.close();

			ALLOW_NPC_CHANGELEVEL         = Boolean.parseBoolean(npcChLevelSettings.getProperty("AllowNpcChangeLevel","false"));
			CUSTOM_DECREASE_PRICE0        = Integer.parseInt(npcChLevelSettings.getProperty("DecreasePrice0","10000"));
			CUSTOM_DECREASE_PRICE1        = Integer.parseInt(npcChLevelSettings.getProperty("DecreasePrice1","50000"));
			CUSTOM_DECREASE_PRICE2        = Integer.parseInt(npcChLevelSettings.getProperty("DecreasePrice2","500000"));
			CUSTOM_DECREASE_PRICE3        = Integer.parseInt(npcChLevelSettings.getProperty("DecreasePrice3","5000000"));
			CUSTOM_COEFF_DIVISIONPRICE_R4 = Integer.parseInt(npcChLevelSettings.getProperty("CoeffDivisionPriceR4","2"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + NPC_CHLEVEL_FILE + " File.");
		} 
	}
	// ====================================================================================================
	public static final String  NPC_ANNOUNCER_FILE  = PathFindingService.NPC_ANNOUNCER_FILE;
	// ====================================================================================================
	public static int     NPC_ANNOUNCER_PRICE_PER_ANNOUNCE;
	public static int     NPC_ANNOUNCER_MAX_ANNOUNCES_PER_DAY;
	public static int     NPC_ANNOUNCER_MIN_LVL_TO_ANNOUNCE;
	public static int     NPC_ANNOUNCER_MAX_LVL_TO_ANNOUNCE;
	public static boolean ALLOW_NPC_ANNOUNCER;
	public static boolean NPC_ANNOUNCER_DONATOR_ONLY;
	
	//**********************************************************************
	public static void loadNpcAnnouncerConfig()
	{
		System.out.println("Loading: "+NPC_ANNOUNCER_FILE);
		try 
		{ 
			Properties npcAnnouncerSettings       = new L2Properties();
			InputStream is                        = new FileInputStream(new File(NPC_ANNOUNCER_FILE));
			npcAnnouncerSettings.load(is);
			is.close();
			
			NPC_ANNOUNCER_DONATOR_ONLY             = Boolean.parseBoolean(npcAnnouncerSettings.getProperty("NpcAnnouncerDonatorOnly","false"));
			ALLOW_NPC_ANNOUNCER                    = Boolean.parseBoolean(npcAnnouncerSettings.getProperty("AllowNpcAnnouncer","false"));
			NPC_ANNOUNCER_PRICE_PER_ANNOUNCE       = Integer.parseInt(npcAnnouncerSettings.getProperty("PricePerAnnounce","10000"));
			NPC_ANNOUNCER_MAX_ANNOUNCES_PER_DAY    = Integer.parseInt(npcAnnouncerSettings.getProperty("AnnouncesPerDay","20"));
			NPC_ANNOUNCER_MIN_LVL_TO_ANNOUNCE      = Integer.parseInt(npcAnnouncerSettings.getProperty("MinLevelToAnnounce","0"));
			NPC_ANNOUNCER_MAX_LVL_TO_ANNOUNCE      = Integer.parseInt(npcAnnouncerSettings.getProperty("MaxLevelToAnnounce","80"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + NPC_ANNOUNCER_FILE + " File.");
		} 
	}
	
	// ====================================================================================================
	public static final String  PROTECTOR_FILE  = PathFindingService.PROTECTOR_FILE;
	// ====================================================================================================
	public static boolean PROTECTOR_PLAYER_PK;
	public static boolean PROTECTOR_PLAYER_PVP;
	public static int     PROTECTOR_RADIUS_ACTION;
	public static int     PROTECTOR_SKILLID;
	public static int     PROTECTOR_SKILLLEVEL;
	public static int     PROTECTOR_SKILLTIME;
	public static String  PROTECTOR_MESSAGE;
	
	//**********************************************************************
	public static void loadProtectorConfig()
	{
		System.out.println("Loading: "+PROTECTOR_FILE);
		try 
		{ 
			Properties ProtectorSettings       = new L2Properties();
			InputStream is                     = new FileInputStream(new File(PROTECTOR_FILE));
			ProtectorSettings.load(is);
			is.close();
			
			PROTECTOR_PLAYER_PK                = Boolean.parseBoolean(ProtectorSettings.getProperty("ProtectorPlayerPK","false"));
			PROTECTOR_PLAYER_PVP               = Boolean.parseBoolean(ProtectorSettings.getProperty("ProtectorPlayerPVP","false"));
			PROTECTOR_RADIUS_ACTION            = Integer.parseInt(ProtectorSettings.getProperty("ProtectorRadiusAction","500"));
			PROTECTOR_SKILLID                  = Integer.parseInt(ProtectorSettings.getProperty("ProtectorSkillId","1069"));
			PROTECTOR_SKILLLEVEL               = Integer.parseInt(ProtectorSettings.getProperty("ProtectorSkillLevel","42"));
			PROTECTOR_SKILLTIME                = Integer.parseInt(ProtectorSettings.getProperty("ProtectorSkillTime","800"));
			PROTECTOR_MESSAGE				   = ProtectorSettings.getProperty("ProtectorMessage","Protector, not spawnkilling here, go read the rules !!!");
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + PROTECTOR_FILE + " File.");
		} 
	}
	
	
	//=============================================================================================
	public static final String NICKS_FILE  = PathFindingService.NICKS_FILE;
	//=============================================================================================
	public static int                    CHAR_VIP_COLOR;
	public static int                    DONATOR_NAME_COLOR;
	public static int                    CLAN_LEADER_COLOR;
	public static int                    CLAN_LEADER_COLOR_CLAN_LEVEL;
	public static int                    GM_NAME_COLOR;
	public static int                    ADMIN_NAME_COLOR;
	public static boolean                CHAR_VIP_COLOR_ENABLED;
	public static boolean                CLAN_LEADER_COLOR_ENABLED;
	public static boolean                GM_NAME_COLOR_ENABLED;	// GM name color
	public static boolean                GM_TITLE_COLOR_ENABLED;
	public static enum ClanLeaderColored
	{
		name,
		title
	}				// Clan leader name color
	
	public static ClanLeaderColored      CLAN_LEADER_COLORED;
	
	//***************************************************************************
	public static void loadNicksConfig()
	{
		System.out.println("Loading: "+NICKS_FILE); 
		try 
		{ 
			Properties nickSettings            = new L2Properties();
			InputStream is                     = new FileInputStream(new File(NICKS_FILE));
			nickSettings.load(is);
			is.close();
			
			CLAN_LEADER_COLOR_ENABLED     			       		= Boolean.parseBoolean(nickSettings.getProperty("ClanLeaderNameColorEnabled", "true"));
			CLAN_LEADER_COLORED                  				= ClanLeaderColored.valueOf(nickSettings.getProperty("ClanLeaderColored", "name"));
			CLAN_LEADER_COLOR                                   = Integer.decode("0x" + nickSettings.getProperty("ClanLeaderColor", "00FFFF"));
			CLAN_LEADER_COLOR_CLAN_LEVEL                        = Integer.parseInt(nickSettings.getProperty("ClanLeaderColorAtClanLevel", "1"));
			CHAR_VIP_COLOR_ENABLED                              = Boolean.parseBoolean(nickSettings.getProperty("CharViPAllowColor", "false"));
			CHAR_VIP_COLOR                                      = Integer.decode("0x" + nickSettings.getProperty("CharViPNameColor", "FFCC00"));
			DONATOR_NAME_COLOR                                  = Integer.decode("0x" + nickSettings.getProperty("DonatorNameColor","00FFFF"));
			GM_NAME_COLOR_ENABLED                               = Boolean.parseBoolean(nickSettings.getProperty("GMNameColorEnabled", "true"));
			GM_NAME_COLOR                                       = Integer.decode("0x" + nickSettings.getProperty("GMNameColor", "00FF00"));
			ADMIN_NAME_COLOR                                    = Integer.decode("0x" + nickSettings.getProperty("AdminNameColor", "00FF00"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load "+ NICKS_FILE +" File.");

		}
	}
	 //  *******************************************************************************************
    public static final String  LOG_FILE              = "./config/logging.properties";
    //  *******************************************************************************************
    final static String LOG_FOLDER = "log"; // Name of folder for log file
    final static String LOG_FOLDER_GAME="game";    	
    //  *******************************************************************************************
    public static void loadLogConfig()
    {
    	try
    	{
    		InputStream is =  new FileInputStream(new File(LOG_FILE)); 
    		LogManager.getLogManager().readConfiguration(is);
    		is.close();
    	}
        catch (Exception e)
        {
            throw new Error("Failed to Load logging.properties File.");
        }
        _log.info("logging initialized");
        File logFolder = new File(Config.DATAPACK_ROOT, LOG_FOLDER); 
        logFolder.mkdir();
        File logFolderGame = new File(logFolder, LOG_FOLDER_GAME); 
        logFolderGame.mkdir();
    }    
    //=============================================================================================
	public static final String CHAT_FILE  = PathFindingService.CHAT_FILE;
	//=============================================================================================
	public static int	   GLOBAL_CHAT_TIME;
	public static int	   TRADE_CHAT_TIME;
	public static enum         ChatMode 
	{
		GLOBAL, 
		REGION,
		GM,
		OFF 
	}
	public static              ChatMode DEFAULT_GLOBAL_CHAT;			// Global chat state
	public static              ChatMode DEFAULT_TRADE_CHAT;				// Trade chat state
	public static boolean 				REGION_CHAT_ALSO_BLOCKED;
	//*************************************************************************************************
	public static void loadChatConfig()
	{
		System.out.println("Loading: " +CHAT_FILE); 
		try 
		{ 
			Properties chatSettings          = new L2Properties();
			InputStream is                   = new FileInputStream(new File(CHAT_FILE));
			chatSettings.load(is);
			is.close();
			
			DEFAULT_GLOBAL_CHAT             = ChatMode.valueOf(chatSettings.getProperty("GlobalChat", "REGION").toUpperCase());
			DEFAULT_TRADE_CHAT              = ChatMode.valueOf(chatSettings.getProperty("TradeChat", "REGION").toUpperCase());
			GLOBAL_CHAT_TIME                = Integer.parseInt(chatSettings.getProperty("GlobalChatTime", "1"));
			TRADE_CHAT_TIME                 = Integer.parseInt(chatSettings.getProperty("TradeChatTime", "1"));
			REGION_CHAT_ALSO_BLOCKED        = Boolean.parseBoolean(chatSettings.getProperty("RegionChatAlsoBlocked", "false"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + CHAT_FILE + " File.");
			
		}
	}
	
	//=============================================================================================
	public static final String  JAIL_FILE  = PathFindingService.JAIL_FILE;
	//============================================================= ================================
	public static int     REQUIRED_JAIL_POINTS;             // ammount of jail points player will need to get to leave jail
	public static int     POINTS_PER_KILL;                  // how many points a player wil receive per each killed mob in jail
	public static int     JAIL_POINT_CHANCE;                // percentage of change to receive a jail point
	public static int     POINTS_LOST_PER_DEATH;            // how many jail points player will loose if die in jail 
	public static boolean JAIL_IS_PVP;                      // jail must be a pvp zone?
	public static boolean ALLOW_JAIL_MANAGER;               // enables/disables jail manager NPC
	public static boolean REDUCE_JAIL_POINTS_ON_DEATH;      // should we reduce player jail points on death?
	public static boolean NOTIY_ADMINS_OF_ILLEGAL_ACTION;   // enables/disables admin notification of illegal jail actions
	public static boolean JAIL_SPAWN_SYSTEM;                // enables/disables jail spawn system
	public static boolean JAIL_DISABLE_ALL_CHAT;            // jail must disable normal chat?
	public static boolean JAIL_DISABLE_SHOUT_CHAT;          // jail must disable shout chat?
	public static boolean JAIL_DISABLE_TRADE_CHAT;          // jail must disable trade chat ?
	public static boolean JAIL_DISABLE_TELL_CHAT;           // jail must disable tell chat ?
	public static boolean JAIL_DISABLE_PARTY_CHAT;          // jail must disable party chat ?
	public static boolean JAIL_DISABLE_ALLIANCE_CHAT;       // jail must disable alliance chat ?
	public static boolean JAIL_DISABLE_HERO_CHAT;           // jail must disable hero chat ?
	public static boolean JAIL_DISABLE_CLAN_CHAT;           // jail must disable clan chat ?
	public static boolean JAIL_DISABLE_CHAT;                // jail must disable all chat ?
    //**********************************************************************************************
	public static void loadJailConfig()
	{
		System.out.println("Loading: "+JAIL_FILE); 
		try 
		{ 
			Properties jailSettings           = new L2Properties();
			InputStream is                    = new FileInputStream(new File(JAIL_FILE));
			jailSettings.load(is);
			is.close();
			
			JAIL_POINT_CHANCE                  = Integer.parseInt(jailSettings.getProperty("PointChance", "100"));
			POINTS_PER_KILL                    = Integer.parseInt(jailSettings.getProperty("PointsPerKill", "1"));
			POINTS_LOST_PER_DEATH              = Integer.parseInt(jailSettings.getProperty("PointsLostPerDeath", "0"));
			REQUIRED_JAIL_POINTS               = Integer.parseInt(jailSettings.getProperty("RequiredJailPoints", "20"));
			JAIL_SPAWN_SYSTEM                  = Boolean.valueOf(jailSettings.getProperty("EnableJailSpawnSystem", "false"));
			NOTIY_ADMINS_OF_ILLEGAL_ACTION     = Boolean.valueOf(jailSettings.getProperty("NotifyAdminsOfIllegalAction", "false"));
			ALLOW_JAIL_MANAGER                 = Boolean.valueOf(jailSettings.getProperty("AllowJailManager", "true"));
			REDUCE_JAIL_POINTS_ON_DEATH        = Boolean.valueOf(jailSettings.getProperty("ReduceJailPointsOnDeath", "true"));
			JAIL_IS_PVP                        = Boolean.valueOf(jailSettings.getProperty("JailIsPvpZone", "true"));
			JAIL_DISABLE_ALL_CHAT              = Boolean.parseBoolean(jailSettings.getProperty("JailDisableNormalChat", "false"));
			JAIL_DISABLE_SHOUT_CHAT            = Boolean.parseBoolean(jailSettings.getProperty("JailDisableShoutChat", "false"));
			JAIL_DISABLE_TELL_CHAT             = Boolean.parseBoolean(jailSettings.getProperty("JailDisableTellChat", "false"));
			JAIL_DISABLE_TRADE_CHAT            = Boolean.parseBoolean(jailSettings.getProperty("JailDisableTradeChat", "false"));
			JAIL_DISABLE_HERO_CHAT             = Boolean.parseBoolean(jailSettings.getProperty("JailDisableHeroChat", "false"));
			JAIL_DISABLE_CLAN_CHAT             = Boolean.parseBoolean(jailSettings.getProperty("JailDisableClanChat", "false"));
			JAIL_DISABLE_ALLIANCE_CHAT         = Boolean.parseBoolean(jailSettings.getProperty("JailDisableAllianceChat", "false"));
			JAIL_DISABLE_PARTY_CHAT            = Boolean.parseBoolean(jailSettings.getProperty("JailDisablePartyChat", "false"));
			JAIL_DISABLE_CHAT                  = Boolean.parseBoolean(jailSettings.getProperty("JailDisableChat", "false"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + JAIL_FILE + " File.");
			
		}
	}
	
	//============================================================================================
	public static final String  CUSTOM_FILE   = PathFindingService.CUSTOM_FILE;
	//============================================================================================
	public static double 	ALT_WEIGHT_LIMIT;	
	public static int		LEVEL_HTML_NEWBIE;	
    public static int  		ONLINE_PLAYERS_ANNOUNCE_INTERVAL; 
    public static int       MAX_PATK_SPEED;
	public static int       MAX_MATK_SPEED;
	public static int       WEAR_DELAY;
	public static int       WEAR_PRICE;
	public static int       SPAWN_X; // Custom SpawnX 
    public static int       SPAWN_Y; // Custom SpawnY 
    public static int       SPAWN_Z; // Custom SpawnZ
    public static int       PLAYER_RATE_DROP_AA = 0;
	public static int       WYVERN_SPEED;
	public static int       STRIDER_SPEED;
	public static int       ALT_BUFFER_HATE;
	public static int       STARTING_AA;                  // Amount of adenas when starting a new character */
	public static int 		MAX_SUBCLASS;                 // Allow to change max number of subclasses
	public static int       CUSTOM_RUN_SPEED;
    public static int       POTIONS_DELAY;            //should we add a delay between potions use?
    public static int       ELIXIRS_DELAY;            //should we add a delay between potions Elixirs use?
	public static boolean   ALT_FLYING_WYVERN_IN_SIEGE;
	public static boolean   ALT_ANNOUNCE_PK;
	public static boolean   ALT_ANNOUNCE_PK_NORMAL_MESSAGE;
	public static boolean   ANNOUNCE_NEAREST_TOWN_PK; //ShT; Announce nearest town for klled character
	public static boolean   LEVEL_ADD_LOAD; 
	public static boolean   SERVER_NEWS;                    // Show "data/html/servnews.htm" whenever a character enters world.*/
    public static boolean	SHOW_HTML_NEWBIE;
	public static boolean   CHAR_VIP_SKIP_SKILLS_CHECK;
    public static boolean   SHOW_HTML_WELCOME;
	public static boolean   ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE;
	public static boolean   ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean   ALT_DISABLE_RAIDBOSS_PETRIFICATION;
	public static boolean   ALT_RECOMMEND;
	public static boolean 	ALT_BLACKSMITH_USE_RECIPES;	      // Alternative setting to blacksmith use of recipes to craft - default true
    public static boolean   CHAR_TITLE;                       // Choose the title of the new chars
	public static boolean   SPAWN_CHAR;                       //Choose the spawn cordinaries for the new chars
    public static boolean   ANNOUNCE_CASTLE_LORDS;            //Announce Castle Lords ?
    public static boolean   SUBCLASS_WITH_ITEM_AND_NO_QUEST;
    public static boolean   SKILL_SUBCLASS_NOT_REMOVE;
    public static boolean   FASTER_TARGET_REVALIDATION;
    public static boolean   FORCE_UPDATE_RAIDBOSS_ON_DB; 
    public static boolean   LOAD_CUSTOM_SPAWNLIST; 
    public static boolean   LOAD_CUSTOM_NPC_TABLE;
	public static boolean   LOAD_CUSTOM_MERCHANT_BUYLISTS;
    public static String    SERVER_NAME;  
    public static String    ADD_CHAR_TITLE;                // Choose the title of the new chars
    public static enum KeepBuffs
	{DONATOR,NONE,GM,EVERYBODY	}				// how character will loose buffs?
	public static KeepBuffs KEEP_BUFFS_ON_DEATH;
	public static boolean LOAD_CUSTOM_TELEPORTS;
	public static boolean ALLOW_TELE_IN_SIEGE_TOWN;
    //*************************************************************************************************
	public static void loadCustomConfig()
	{
		System.out.println("Loading: "+CUSTOM_FILE); 
		try
		{
			Properties customSettings           = new L2Properties();
			InputStream is                      = new FileInputStream(new File(CUSTOM_FILE));
			customSettings.load(is);
			is.close();
			 
			LOAD_CUSTOM_TELEPORTS                               = Boolean.parseBoolean(customSettings.getProperty("LoadCustomTeleports","false"));
			LOAD_CUSTOM_MERCHANT_BUYLISTS                       = Boolean.parseBoolean(customSettings.getProperty("LoadCustomBuylists","false"));
			POTIONS_DELAY                                       = Integer.parseInt(customSettings.getProperty("PotionsDelay", "0"))*1000;//auto convert seconds to miliseconds
			ELIXIRS_DELAY                                       = Integer.parseInt(customSettings.getProperty("ElixirsDelay", "0"))*1000;//auto convert seconds to miliseconds
			LOAD_CUSTOM_NPC_TABLE                               = Boolean.parseBoolean(customSettings.getProperty("LoadCustomNpcs","false"));
			LOAD_CUSTOM_SPAWNLIST                               = Boolean.parseBoolean(customSettings.getProperty("LoadCustomSpawnList","false"));
			FASTER_TARGET_REVALIDATION                          = Boolean.parseBoolean(customSettings.getProperty("FasterTargetRevalidation","false"));
			SUBCLASS_WITH_ITEM_AND_NO_QUEST                     = Boolean.parseBoolean(customSettings.getProperty("SubclassWithItemAndNoQuest", "false")); 
			SKILL_SUBCLASS_NOT_REMOVE							= Boolean.parseBoolean(customSettings.getProperty("AltSubClassSkills", "false"));
			CUSTOM_RUN_SPEED                                    = Integer.parseInt(customSettings.getProperty("CustomRunSpeed", "0")); 
			KEEP_BUFFS_ON_DEATH                                 = KeepBuffs.valueOf(customSettings.getProperty("KeepBuffsFor", "NONE").toUpperCase());//avoid case problems
			MAX_SUBCLASS                                        = Integer.parseInt(customSettings.getProperty("MaxSubClass","3"));
			STARTING_AA                                         = Integer.parseInt(customSettings.getProperty("StartingAA", "100"));
			ANNOUNCE_CASTLE_LORDS                               = Boolean.valueOf(customSettings.getProperty("AnnounceCastleLords", "false"));
			CHAR_TITLE		                                   	= Boolean.valueOf(customSettings.getProperty("CharTitle", "false"));
            ADD_CHAR_TITLE			                            = customSettings.getProperty("CharAddTitle", "Newbie");
            SPAWN_CHAR		    	                            = Boolean.valueOf(customSettings.getProperty("CharCustomSpawn", "false"));
            SPAWN_X                                             = Integer.parseInt(customSettings.getProperty("SpawnX", ""));
            SPAWN_Y                                             = Integer.parseInt(customSettings.getProperty("SpawnY", ""));
            SPAWN_Z                                             = Integer.parseInt(customSettings.getProperty("SpawnZ", ""));
			SHOW_HTML_NEWBIE                                    = Boolean.parseBoolean(customSettings.getProperty("ShowHTMLNewbie", "false"));
			LEVEL_HTML_NEWBIE                                   = Integer.parseInt(customSettings.getProperty("LevelShowHTMLNewbie", "10"));
            SERVER_NEWS                                         = Boolean.parseBoolean(customSettings.getProperty("ShowServerNews", "false"));
			ALT_BLACKSMITH_USE_RECIPES                          = Boolean.parseBoolean(customSettings.getProperty("AltBlacksmithUseRecipes", "true"));
			SERVER_NAME                                         = customSettings.getProperty("ServerName","Shilen's Temple");
			ALT_FLYING_WYVERN_IN_SIEGE                          = Boolean.parseBoolean(customSettings.getProperty("AltFlyingWyvernInSiege", "false"));
			ONLINE_PLAYERS_AT_STARTUP                           = Boolean.parseBoolean(customSettings.getProperty("ShowOnlinePlayersAtStartup","true"));
			ONLINE_PLAYERS_ANNOUNCE_INTERVAL                    = Integer.parseInt(customSettings.getProperty("OnlinePlayersAnnounceInterval","900000"));
			ALT_GAME_SUBCLASS_WITHOUT_QUESTS                    = Boolean.parseBoolean(customSettings.getProperty("AltSubClassWithoutQuests", "false"));
			ALT_RECOMMEND                                       = Boolean.parseBoolean(customSettings.getProperty("AltRecommend", "false"));
			MAX_PATK_SPEED                                      = Integer.parseInt(customSettings.getProperty("MaxPAtkSpeed", "0"));
			MAX_MATK_SPEED                                      = Integer.parseInt(customSettings.getProperty("MaxMAtkSpeed", "0"));
			ALT_WEIGHT_LIMIT                                    = Double.parseDouble(customSettings.getProperty("AltWeightLimit", "1."));
			LEVEL_ADD_LOAD                                      = Boolean.valueOf(customSettings.getProperty("IncreaseWeightLimitByLevel", "false")); 
			ALT_GAME_MAGICFAILURES                              = Boolean.parseBoolean(customSettings.getProperty("MagicFailures", "false"));
			ALT_PLAYER_CAN_DROP_ADENA                           = Boolean.parseBoolean(customSettings.getProperty("PlayerCanDropAdena", "false"));
			PLAYER_RATE_DROP_ADENA                              = Integer.parseInt(customSettings.getProperty("PlayerRateDropAdena", "1"));
			ALT_ANNOUNCE_PK                                     = Boolean.parseBoolean(customSettings.getProperty("AnnouncePk", "false"));
			ALT_ANNOUNCE_PK_NORMAL_MESSAGE                      = Boolean.parseBoolean(customSettings.getProperty("AnnouncePkNormalMessage", "false"));
			ANNOUNCE_NEAREST_TOWN_PK                            = Boolean.parseBoolean(customSettings.getProperty("AnnouncePkNearestTown", "false"));
			ALT_DISABLE_RAIDBOSS_PETRIFICATION                  = Boolean.parseBoolean(customSettings.getProperty("DisableRaidBossFossilization", "false"));
			ALT_GAME_FREE_TELEPORT                              = Boolean.parseBoolean(customSettings.getProperty("AltFreeTeleporting", "false"));
		    ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE                  = Boolean.parseBoolean(customSettings.getProperty("AltNewCharAlwaysIsNewbie", "false"));
			WYVERN_SPEED                                        = Integer.parseInt(customSettings.getProperty("WyvernSpeed", "100"));
			STRIDER_SPEED                                       = Integer.parseInt(customSettings.getProperty("StriderSpeed", "80"));
			WEAR_DELAY                                          = Integer.parseInt(customSettings.getProperty("WearDelay", "5"));
			WEAR_PRICE                                          = Integer.parseInt(customSettings.getProperty("WearPrice", "10"));
			SHOW_HTML_WELCOME                                   = Boolean.parseBoolean(customSettings.getProperty("ShowHTMLWelcome", "false"));
			FORCE_UPDATE_RAIDBOSS_ON_DB                         = Boolean.parseBoolean(customSettings.getProperty("ForceUpdateRaidBossOnDB", "false"));
			ALLOW_TELE_IN_SIEGE_TOWN                            = Boolean.parseBoolean(customSettings.getProperty("AllowTeleportInSiegeTown", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+CUSTOM_FILE+" File.");
		}
	}
	//=====================================================================================
	public static final String  SAY_FILTER_FILE   =   PathFindingService.SAY_FILTER_FILE;
	//=====================================================================================
	public static int               CHAT_FILTER_PUNISHMENT_PARAM1;
	public static int               CHAT_FILTER_PUNISHMENT_PARAM2;
	public static boolean           USE_SAY_FILTER;
	public static String            CHAT_FILTER_CHARS;
	public static String            CHAT_FILTER_PUNISHMENT;
	public static ArrayList<String> FILTER_LIST = new ArrayList<String>();
	
	//***************************************************************************************
	public static void loadChatFilterConfig()
	{
		System.out.println("Loading: " +SAY_FILTER_FILE);
		try
		{
			Properties filterSettings           = new L2Properties();
			InputStream is                      = new FileInputStream(new File(SAY_FILTER_FILE));
			filterSettings.load(is);
			is.close();
			
			USE_SAY_FILTER                  = Boolean.parseBoolean(filterSettings.getProperty("UseChatFilter", "false")); 
			CHAT_FILTER_CHARS                = filterSettings.getProperty("ChatFilterChars", "***");
			CHAT_FILTER_PUNISHMENT           = filterSettings.getProperty("ChatFilterPunishment", "off");
			CHAT_FILTER_PUNISHMENT_PARAM1    = Integer.parseInt(filterSettings.getProperty("ChatFilterPunishmentParam1", "1")); 
			CHAT_FILTER_PUNISHMENT_PARAM2    = Integer.parseInt(filterSettings.getProperty("ChatFilterPunishmentParam2", "1")); 
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+SAY_FILTER_FILE+" File.");
		}
	}
	
	//=========================================================================================
	public static final String  CRAFT_MANAGER_FILE    =   PathFindingService.CRAFT_MANAGER_FILE;
	//=========================================================================================
	public static double  ALT_CRAFT_PRICE;                   // reference price multiplier 
	public static int     ALT_CRAFT_DEFAULT_PRICE;           // default price, in case reference is 0 
	public static boolean ALT_CRAFT_ALLOW_CRAFT;             // allow to craft dwarven recipes 
	public static boolean ALT_CRAFT_ALLOW_CRYSTALLIZE;       // allow to break items 
	public static boolean ALT_CRAFT_ALLOW_COMMON;            // allow to craft common craft recipes
	
	//************************************************************************************************* 
	public static void loadCraftManagerConfig()
	{
		System.out.println("Loading: " +CRAFT_MANAGER_FILE); 
		try 
		{ 
			Properties craftManagerSettings            = new L2Properties();
			InputStream is                             = new FileInputStream(new File(CRAFT_MANAGER_FILE));
			craftManagerSettings.load(is);
			is.close();
			
			ALT_CRAFT_ALLOW_CRAFT                               = Boolean.parseBoolean(craftManagerSettings.getProperty("CraftManagerDwarvenCraft", "true"));
			ALT_CRAFT_ALLOW_COMMON                              = Boolean.parseBoolean(craftManagerSettings.getProperty("CraftManagerCommonCraft", "false"));
			ALT_CRAFT_ALLOW_CRYSTALLIZE                         = Boolean.parseBoolean(craftManagerSettings.getProperty("CraftManagerCrystallize", "true"));
			ALT_CRAFT_PRICE                                     = Float.parseFloat(craftManagerSettings.getProperty("CraftManagerPriceMultiplier", "0.1"));
			ALT_CRAFT_DEFAULT_PRICE                             = Integer.parseInt(craftManagerSettings.getProperty("CraftManagerDefaultPrice", "50000"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + CRAFT_MANAGER_FILE + " File.");
		}
	}
	
	//==========================================================================================
	public static final String  CHAMPION_FILE     = PathFindingService.CHAMPION_FILE;
	//==========================================================================================
	public static int       CHAMPION_FREQUENCY;   // Frequency of spawn
	public static int       CHAMPION_HP;          // Hp multiplier
	public static int       CHAMPION_ADENA;       // Adena/Sealstone reward multiplier
	public static int       CHAMPION_REWARDS;     // Drop/Spoil reward multiplier
	public static int       CHAMPION_EXP_SP;      // Exp/Sp reward multiplier
	public static int       CHAMPION_MIN_LEVEL;   // Champion Minimum Level
	public static int       CHAMPION_MAX_LEVEL;   // Champion Maximum Level
	public static int       CHAMPION_SPCL_CHANCE; // Chance in % to drop an special reward item.
	public static int       CHAMPION_SPCL_ITEM;   // Item ID that drops from Champs.
	public static int       CHAMPION_SPCL_QTY;    // Amount of special champ drop items.
	public static int       CHAMPION_SPCL_LVL_DIFF; // level diff with mob level is more this value - don't drop an special reward item.
	public static float     CHAMPION_HP_REGEN;    // Hp.reg multiplier
	public static float     CHAMPION_ATK;         // P.Atk & M.Atk multiplier
	public static float     CHAMPION_SPD_ATK;     // Attack speed multiplier
	public static boolean   CHAMPION_BOSS;        // Bosses can be champions
	public static boolean   CHAMPION_MINIONS;     // set Minions to champions when leader champion
	public static boolean	CHAMPION_ENABLE;	  // Enable or Disable
	//****************************************************************************
	public static void loadChampionConfig()
	{
		System.out.println("Loading: " +CHAMPION_FILE); 
		try 
		{ 
			Properties championsSettings     = new L2Properties();
			InputStream is                   = new FileInputStream(new File(CHAMPION_FILE));
			championsSettings.load(is);
			is.close();
			
			CHAMPION_ENABLE			= Boolean.parseBoolean(championsSettings.getProperty("ChampionEnable", "false"));
			CHAMPION_FREQUENCY      = Integer.parseInt(championsSettings.getProperty("ChampionFrequency", "0"));
			CHAMPION_HP             = Integer.parseInt(championsSettings.getProperty("ChampionHp", "7"));
			CHAMPION_HP_REGEN       = Float.parseFloat(championsSettings.getProperty("ChampionRegenHp","1."));    
			CHAMPION_REWARDS        = Integer.parseInt(championsSettings.getProperty("ChampionRewards", "8"));
			CHAMPION_ADENA          = Integer.parseInt(championsSettings.getProperty("ChampionAdenasRewards", "1"));
			CHAMPION_ATK            = Float.parseFloat(championsSettings.getProperty("ChampionAtk", "1."));
			CHAMPION_SPD_ATK        = Float.parseFloat(championsSettings.getProperty("ChampionSpdAtk", "1."));
			CHAMPION_EXP_SP         = Integer.parseInt(championsSettings.getProperty("ChampionExpSp", "8"));
			CHAMPION_BOSS           = Boolean.parseBoolean(championsSettings.getProperty("ChampionBoss", "false"));
			CHAMPION_MIN_LEVEL      = Integer.parseInt(championsSettings.getProperty("ChampionMinLevel", "20"));
			CHAMPION_MAX_LEVEL      = Integer.parseInt(championsSettings.getProperty("ChampionMaxLevel", "60"));
			CHAMPION_MINIONS        = Boolean.parseBoolean(championsSettings.getProperty("ChampionMinions", "false"));
			CHAMPION_SPCL_CHANCE    = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemChance", "0"));
			CHAMPION_SPCL_ITEM      = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemID", "6393"));
			CHAMPION_SPCL_QTY       = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemAmount", "1"));
			CHAMPION_SPCL_LVL_DIFF  = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemLevelDiff", "0"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " + CHAMPION_FILE + " File.");

		}
	}
	
    //==================================================================================================
	public static final String  NPC_FILE      = PathFindingService.NPC_FILE;
	//==================================================================================================
	public static int               MIN_NPC_ANIMATION;
	public static int               MAX_NPC_ANIMATION;
	public static int           	MIN_MONSTER_ANIMATION;
	public static int           	MAX_MONSTER_ANIMATION;
	public static int               NPC_MIN_WALK_ANIMATION;
	public static int               NPC_MAX_WALK_ANIMATION;
	public static boolean           ALT_GAME_VIEWNPC;
    public static boolean           ALT_GAME_VIEWNPC_COMBAT;
    public static boolean           ALT_GAME_VIEWNPC_BASIC;
    public static boolean           ALT_GAME_VIEWNPC_DROP;
    public static boolean           ALT_GAME_VIEWNPC_QUESTDROP;
	public static boolean           SHOW_NPC_LVL;
	public static boolean			ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean           ALT_GAME_MOB_ATTACK_AI;	
	public static boolean           ALLOW_EXCHANGE;
	public static String            ALLOWED_NPC_TYPES;
	public static FastList<String>  LIST_ALLOWED_NPC_TYPES = new FastList<String>();
	public static int               NPC_USED_SHOTS_LEVEL;
	
	//*************************************************************************************************
	public static void loadNpcsConfig()
	{
		System.out.println("Loading: " +NPC_FILE); 
		try 
		{ 
			Properties npcSettings           = new L2Properties();
			InputStream is                   = new FileInputStream(new File(NPC_FILE));
			npcSettings.load(is);
			is.close();
			
			
			ALLOW_EXCHANGE                              = Boolean.valueOf(npcSettings.getProperty("AllowExchange", "true"));  
			ALT_GAME_VIEWNPC                            = Boolean.parseBoolean(npcSettings.getProperty("AltGameViewNpc", "false"));
            ALT_GAME_VIEWNPC_COMBAT                     = Boolean.parseBoolean(npcSettings.getProperty("AltGameViewNpcCombat", "false"));
            ALT_GAME_VIEWNPC_BASIC                      = Boolean.parseBoolean(npcSettings.getProperty("AltGameViewNpcBasic", "false"));
            ALT_GAME_VIEWNPC_DROP                       = Boolean.parseBoolean(npcSettings.getProperty("AltGameViewNpcDrop", "false"));
            ALT_GAME_VIEWNPC_QUESTDROP                  = Boolean.parseBoolean(npcSettings.getProperty("AltGameViewNpcQuestDrop", "false"));
			ALT_GAME_MOB_ATTACK_AI                      = Boolean.parseBoolean(npcSettings.getProperty("AltGameMobAttackAI", "false"));
			ALT_MOB_AGRO_IN_PEACEZONE                   = Boolean.parseBoolean(npcSettings.getProperty("AltMobAgroInPeaceZone", "true"));
			MAX_DRIFT_RANGE                             = Integer.parseInt(npcSettings.getProperty("MaxDriftRange", "300"));
			MIN_NPC_ANIMATION                           = Integer.parseInt(npcSettings.getProperty("MinNPCAnimation", "10"));
			MAX_NPC_ANIMATION                           = Integer.parseInt(npcSettings.getProperty("MaxNPCAnimation", "20"));
			NPC_MIN_WALK_ANIMATION                      = Integer.parseInt(npcSettings.getProperty("MinNPCWalkAnimation", "10"));
			NPC_MAX_WALK_ANIMATION                      = Integer.parseInt(npcSettings.getProperty("MaxNPCWalkAnimation", "20"));
			MIN_MONSTER_ANIMATION                       = Integer.parseInt(npcSettings.getProperty("MinMonsterAnimation", "5"));
			MAX_MONSTER_ANIMATION                       = Integer.parseInt(npcSettings.getProperty("MaxMonsterAnimation", "20"));
			SHOW_NPC_LVL                                = Boolean.valueOf(npcSettings.getProperty("ShowNpcLevel", "false"));
			ALLOWED_NPC_TYPES                           = npcSettings.getProperty("AllowedNPCTypes");
			NPC_USED_SHOTS_LEVEL                        = Integer.parseInt(npcSettings.getProperty("NPCUsedShotsLevel","70"));
			
			LIST_ALLOWED_NPC_TYPES = new FastList<String>();
			for (String npc_type : ALLOWED_NPC_TYPES.trim().split(","))  
			{ 
				LIST_ALLOWED_NPC_TYPES.add(npc_type.trim());
			}        
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e); 
			throw new Error("GameServer: Failed to Load " +NPC_FILE+ " File.");

		}
	}
	
    //=====================================================================================================
	public static final String  THREADS_FILE      = PathFindingService.THREADS_FILE;
	//==================================================================================================
	public static int      THREAD_P_EFFECTS;
	public static int      THREAD_P_GENERAL;
	public static int      GENERAL_PACKET_THREAD_CORE_SIZE;
	public static int      AI_MAX_THREAD;
	public static int      IO_PACKET_THREAD_CORE_SIZE;
	public static int      GENERAL_THREAD_CORE_SIZE;
	
	//*********************************************************************************************************
	public static void loadThreadsConfig()
	{
		System.out.println("Loading: " +THREADS_FILE); 
		try 
		{ 
			Properties threadsSettings                 = new L2Properties();
			InputStream is                             = new FileInputStream(new File(THREADS_FILE));
			threadsSettings.load(is);
			is.close();
			
			THREAD_P_EFFECTS                            = Integer.parseInt(threadsSettings.getProperty("ThreadPoolSizeEffects", "6"));
			THREAD_P_GENERAL                            = Integer.parseInt(threadsSettings.getProperty("ThreadPoolSizeGeneral", "15"));
			GENERAL_PACKET_THREAD_CORE_SIZE             = Integer.parseInt(threadsSettings.getProperty("GeneralPacketThreadCoreSize", "4"));
			IO_PACKET_THREAD_CORE_SIZE                  = Integer.parseInt(threadsSettings.getProperty("UrgentPacketThreadCoreSize", "2"));
			AI_MAX_THREAD                               = Integer.parseInt(threadsSettings.getProperty("AiMaxThread", "10"));
			GENERAL_THREAD_CORE_SIZE                    = Integer.parseInt(threadsSettings.getProperty("GeneralThreadCoreSize", "4"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load " + THREADS_FILE + " File.");
		} 
	}
	
	//=====================================================================================================
	public static final String  RAID_ENGINE_FILE      =    PathFindingService.RAID_ENGINE_FILE;
	//==================================================================================================
	public static int     RAID_SYSTEM_MAX_EVENTS;
	public static int     RAID_SYSTEM_FIGHT_TIME;
	public static boolean RAID_SYSTEM_GIVE_BUFFS;
	public static boolean RAID_SYSTEM_RESURRECT_PLAYER;
	public static boolean RAID_SYSTEM_ENABLED;
	//*********************************************************************************************
	
	public static void loadREConfig()
	{
		System.out.println("Loading: " +RAID_ENGINE_FILE); 
		try 
		{ 
			Properties raidEngineSettings  = new L2Properties();
			InputStream is                 = new FileInputStream(new File(RAID_ENGINE_FILE));
			raidEngineSettings.load(is);
			is.close();
			
			RAID_SYSTEM_ENABLED					= Boolean.parseBoolean(raidEngineSettings.getProperty("RaidEnginesEnabled", "false"));
			RAID_SYSTEM_GIVE_BUFFS				= Boolean.parseBoolean(raidEngineSettings.getProperty("RaidGiveBuffs", "true"));
			RAID_SYSTEM_RESURRECT_PLAYER		= Boolean.parseBoolean(raidEngineSettings.getProperty("RaidResurrectPlayer", "true"));
			RAID_SYSTEM_MAX_EVENTS				= Integer.parseInt(raidEngineSettings.getProperty("RaidMaxNumEvents", "3"));
			RAID_SYSTEM_FIGHT_TIME				= Integer.parseInt(raidEngineSettings.getProperty("RaidSystemFightTime", "60"));
			if (RAID_SYSTEM_MAX_EVENTS == 0)
			{
				RAID_SYSTEM_ENABLED = false;
				_log.fatal("Raid Engine[Config.load()]: Invalid config property: Max Events = 0?!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("GameServer: Failed to Load "+RAID_ENGINE_FILE+" File.");
		}
	}
    
	//=====================================================================================================
	public static final String  TVT_FILE      =    PathFindingService.TVT_FILE;
	//==================================================================================================
	public static boolean TVT_AUTO_STARTUP_ON_BOOT;
	public static boolean TVT_ALLOW_INTERFERENCE;
	public static boolean TVT_ALLOW_POTIONS;
	public static boolean TVT_ALLOW_SUMMON;
	public static boolean TVT_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean TVT_ON_START_UNSUMMON_PET;
	public static boolean TVT_REVIVE_RECOVERY;
	public static boolean TVT_ANNOUNCE_TEAM_STATS;
	public static boolean TVT_CLOSE_COLISEUM_DOORS;
	public static boolean TVT_ALLOW_ENEMY_HEALING;
	public static boolean TVT_ALLOW_TEAM_CASTING;
	public static boolean TVT_ALLOW_TEAM_ATTACKING;
	public static boolean TVT_DISABLE_NPC_BUFFER;
	public static boolean TVT_ANNOUNCE_REGISTRATION_LOC_NPC;
	public static boolean TVT_ANNOUNCE_SIGNUPS;
	public static boolean TVT_JOIN_CURSED;
	public static boolean TVT_PRICE_NO_KILLS;
	public static String  TVT_EVEN_TEAMS;
	public static boolean TVT_SHT_ALLOW_TVT_CYCLE;
	public static int     TVT_SHT_TVT_FIRST_START_DELAY;
	//*********************************************************************************************************
	public static void loadTvtConfig()
	{
		System.out.println("Loading: " +TVT_FILE); 
		try 
		{ 
			Properties tvtSettings               = new L2Properties();
			InputStream is                       = new FileInputStream(new File(TVT_FILE));
			tvtSettings.load(is);
			is.close();
			
			TVT_JOIN_CURSED                             = Boolean.parseBoolean(tvtSettings.getProperty("TvtJoinWithCursedWeapon", "true"));
			TVT_PRICE_NO_KILLS                          = Boolean.parseBoolean(tvtSettings.getProperty("TvtPriceNoKills", "false"));
			TVT_ALLOW_ENEMY_HEALING                     = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowEnemyHealing", "false"));
			TVT_ALLOW_TEAM_CASTING                      = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowTeamCasting", "false"));
			TVT_ALLOW_TEAM_ATTACKING                    = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowTeamAttacking", "false"));
			TVT_CLOSE_COLISEUM_DOORS                    = Boolean.parseBoolean(tvtSettings.getProperty("TvTCloseColiseumDoors", "false"));
			TVT_AUTO_STARTUP_ON_BOOT                    = Boolean.parseBoolean(tvtSettings.getProperty("TvTAutoStartUpOnBoot", "true"));
			TVT_ALLOW_INTERFERENCE                      = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowInterference", "false"));
			TVT_ALLOW_POTIONS                           = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowPotions", "false"));
			TVT_ALLOW_SUMMON                            = Boolean.parseBoolean(tvtSettings.getProperty("TvTAllowSummon", "false"));
			TVT_ON_START_REMOVE_ALL_EFFECTS             = Boolean.parseBoolean(tvtSettings.getProperty("TvTOnStartRemoveAllEffects", "true"));
			TVT_ON_START_UNSUMMON_PET                   = Boolean.parseBoolean(tvtSettings.getProperty("TvTOnStartUnsummonPet", "true"));
			TVT_REVIVE_RECOVERY                         = Boolean.parseBoolean(tvtSettings.getProperty("TvTReviveRecovery", "false"));
			TVT_ANNOUNCE_TEAM_STATS                     = Boolean.parseBoolean(tvtSettings.getProperty("TvtAnnounceTeamStats", "false"));
			TVT_EVEN_TEAMS                              = tvtSettings.getProperty("TvTEvenTeams", "BALANCE");
			TVT_DISABLE_NPC_BUFFER                      = Boolean.parseBoolean(tvtSettings.getProperty("TvTDisableNpcBuffer", "false"));
			TVT_ANNOUNCE_SIGNUPS                        = Boolean.parseBoolean(tvtSettings.getProperty("TvTAnnounceSignUp", "false"));
			TVT_ANNOUNCE_REGISTRATION_LOC_NPC           = Boolean.parseBoolean(tvtSettings.getProperty("TvTAnnounceLocNpc", "true"));
			TVT_SHT_ALLOW_TVT_CYCLE                     = Boolean.parseBoolean(tvtSettings.getProperty("AllowTvTCycle", "false"));
			TVT_SHT_TVT_FIRST_START_DELAY               = Integer.parseInt(tvtSettings.getProperty("TvTFirstStartDelay", "4"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load " + TVT_FILE + " File.");
		} 
	}
	//=====================================================================================================
	public static final String  FORTRESS_SIEGE_FILE      =    PathFindingService.FORTRESS_SIEGE_FILE;
	//==================================================================================================
	public static boolean FortressSiege_ALLOW_INTERFERENCE;
	public static boolean FortressSiege_ALLOW_POTIONS;
	public static boolean FortressSiege_ALLOW_SUMMON;
	public static boolean FortressSiege_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean FortressSiege_ON_START_UNSUMMON_PET;
	public static boolean FortressSiege_ANNOUNCE_TEAM_STATS;
	public static boolean FortressSiege_JOIN_CURSED;    
	public static boolean FortressSiege_REVIVE_RECOVERY;
	public static boolean FortressSiege_PRICE_NO_KILLS;
	public static String FortressSiege_EVEN_TEAMS;
	public static boolean FortressSiege_SAME_IP_PLAYERS_ALLOWED;
	//*********************************************************************************************************
	public static void loadFortressSiegeConfig()
	{
		System.out.println("Loading: " +FORTRESS_SIEGE_FILE); 
		try 
		{ 
			Properties fortressSiegeSettings     = new L2Properties();
			InputStream is                       = new FileInputStream(new File(FORTRESS_SIEGE_FILE));
			fortressSiegeSettings .load(is);
			is.close();
	  
			   FortressSiege_SAME_IP_PLAYERS_ALLOWED = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeSameIPPlayersAllowed", "false"));
	           FortressSiege_EVEN_TEAMS =fortressSiegeSettings .getProperty("FortressSiegeEvenTeams", "BALANCE");
	           FortressSiege_ALLOW_INTERFERENCE = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeAllowInterference", "false"));
	           FortressSiege_ALLOW_POTIONS = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeAllowPotions", "false"));
	           FortressSiege_ALLOW_SUMMON = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeAllowSummon", "false"));
	           FortressSiege_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeOnStartRemoveAllEffects", "true"));
	           FortressSiege_ON_START_UNSUMMON_PET = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeOnStartUnsummonPet", "true"));
	           FortressSiege_REVIVE_RECOVERY = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeReviveRecovery", "false"));
	           FortressSiege_ANNOUNCE_TEAM_STATS = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeAnnounceTeamStats", "false"));
	           FortressSiege_PRICE_NO_KILLS = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegePriceNoKills", "false"));
	           FortressSiege_JOIN_CURSED = Boolean.parseBoolean(fortressSiegeSettings.getProperty("FortressSiegeJoinWithCursedWeapon", "true"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load " + FORTRESS_SIEGE_FILE + " File.");
		} 
	}
	//=============================================================================================
	public static final String  CTF_FILE      =    PathFindingService.CTF_FILE;
    //==============================================================================================
	public static boolean CTF_REVIVE_RECOVERY; 
	public static boolean CTF_ALLOW_INTERFERENCE;
	public static boolean CTF_ALLOW_POTIONS;
	public static boolean CTF_ALLOW_SUMMON;
	public static boolean CTF_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean CTF_ON_START_UNSUMMON_PET;
	public static boolean CTF_DISABLE_NPC_BUFFER;
	public static String  CTF_EVEN_TEAMS;
    public static boolean CTF_ANNOUNCE_TEAM_STATS;
    public static boolean CTF_JOIN_CURSED;	
	
	//*********************************************************************************************
	public static void loadCtfConfig()
	{
		System.out.println("Loading: " +CTF_FILE); 
		try 
		{ 
			Properties ctfSettings  = new L2Properties();
			InputStream is          = new FileInputStream(new File(CTF_FILE));
			ctfSettings.load(is);
			is.close();
			
			CTF_REVIVE_RECOVERY                         = Boolean.parseBoolean(ctfSettings.getProperty("CTFReviveRecovery", "false")); 
			CTF_DISABLE_NPC_BUFFER                      = Boolean.parseBoolean(ctfSettings.getProperty("CTFDisableNpcBuffer", "false"));
			CTF_EVEN_TEAMS                              = ctfSettings.getProperty("CTFEvenTeams", "BALANCE"); 
			CTF_ALLOW_INTERFERENCE                      = Boolean.parseBoolean(ctfSettings.getProperty("CTFAllowInterference", "false"));
			CTF_ALLOW_POTIONS                           = Boolean.parseBoolean(ctfSettings.getProperty("CTFAllowPotions", "false"));
			CTF_ALLOW_SUMMON                            = Boolean.parseBoolean(ctfSettings.getProperty("CTFAllowSummon", "false"));
			CTF_ON_START_REMOVE_ALL_EFFECTS             = Boolean.parseBoolean(ctfSettings.getProperty("CTFOnStartRemoveAllEffects", "true"));
			CTF_ON_START_UNSUMMON_PET                   = Boolean.parseBoolean(ctfSettings.getProperty("CTFOnStartUnsummonPet", "true"));
			CTF_ANNOUNCE_TEAM_STATS                     = Boolean.parseBoolean(ctfSettings.getProperty("CTFAnnounceTeamStats", "false"));
			CTF_JOIN_CURSED                             = Boolean.parseBoolean(ctfSettings.getProperty("CTFJoinWithCursedWeapon", "true"));		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load " +CTF_FILE + " File.");
		} 
	}
	
    //==================================================================================================
	public static final String  DM_FILE      =    PathFindingService.DM_FILE;
	//==================================================================================================
	public static boolean DM_ALLOW_INTERFERENCE;
	public static boolean DM_DISABLE_NPC_BUFFER;
	public static boolean DM_ALLOW_POTIONS;
	public static boolean DM_ALLOW_SUMMON;
	public static boolean DM_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean DM_ON_START_UNSUMMON_PET;
   
	//***************************************************************************************************
	public static void loadDmConfig()
	{
		System.out.println("Loading: " +DM_FILE); 
		try 
		{ 
			Properties dmSettings          = new L2Properties();
			InputStream is                 = new FileInputStream(new File(DM_FILE));
			dmSettings.load(is);
			is.close();
			
			DM_DISABLE_NPC_BUFFER                       = Boolean.parseBoolean(dmSettings.getProperty("DMDisableNpcBuffer", "false"));
			DM_ALLOW_INTERFERENCE                       = Boolean.parseBoolean(dmSettings.getProperty("DMAllowInterference", "false"));
			DM_ALLOW_POTIONS                            = Boolean.parseBoolean(dmSettings.getProperty("DMAllowPotions", "false"));
			DM_ALLOW_SUMMON                             = Boolean.parseBoolean(dmSettings.getProperty("DMAllowSummon", "false"));
			DM_ON_START_REMOVE_ALL_EFFECTS              = Boolean.parseBoolean(dmSettings.getProperty("DMOnStartRemoveAllEffects", "true"));
			DM_ON_START_UNSUMMON_PET                    = Boolean.parseBoolean(dmSettings.getProperty("DMOnStartUnsummonPet", "true"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load " +DM_FILE + " File.");
		} 
	}
	//=========================================================================================
	public static final String  OPTIONS_FILE       = PathFindingService.OPTIONS_FILE;
	// ======================================================================================
	public static boolean  SERVER_LIST_TEST_SERVER;
    public static boolean  AUTODELETE_INVALID_QUEST_DATA;
	public static boolean  ONLINE_PLAYERS_AT_STARTUP;
	public static boolean  LOG_CHAT;
	public static boolean  LOG_ITEMS;
	
	/** 
	 * This is setting of experimental Client <--> Server Player coordinates synchronization<br> 
	 * <b><u>Values :</u></b> 
	 * <li>0 - no synchronization at all</li> 
	 * <li>1 - parcial synchronization Client --> Server only * using this option it is difficult for players  
	 *         to bypass obstacles</li> 
	 * <li>2 - parcial synchronization Server --> Client only</li> 
	 * <li>3 - full synchronization Client <--> Server</li> 
	 * <li>-1 - Old system: will synchronize Z only</li> 
	 */ 
	public static int      COORD_SYNCHRONIZE;
	public static int      PACKET_LIFETIME;
	public static int      MAX_DRIFT_RANGE;
	public static int      DELETE_DAYS;
	public static int      ZONE_TOWN;
	public static long     PACKET_EXECUTIONTIME;
	public static String   FISHINGMODE;
	public static boolean  LOGINSERVER_SHOW_LICENSE;
	public static boolean  SHOW_EMU_LICENSE;
	
	//************************************************************************************************
	public static void loadOptionalConfig()
	{
		System.out.println("Loading: " +OPTIONS_FILE); 
		try  
		{ 
			Properties optionsSettings    = new L2Properties();
			InputStream is               = new FileInputStream(new File(OPTIONS_FILE));
			optionsSettings.load(is);
			is.close();
			
			COORD_SYNCHRONIZE                           = Integer.parseInt(optionsSettings.getProperty("CoordSynchronize", "-1"));
			SERVER_GMONLY                               = Boolean.valueOf(optionsSettings.getProperty("ServerGMOnly", "false"));
			LOG_CHAT                                    = Boolean.valueOf(optionsSettings.getProperty("LogChat", "false"));
			LOG_ITEMS                                   = Boolean.valueOf(optionsSettings.getProperty("LogItems", "false"));
			AUTODELETE_INVALID_QUEST_DATA               = Boolean.valueOf(optionsSettings.getProperty("AutoDeleteInvalidQuestData", "false"));
			SHOW_EMU_LICENSE                            = Boolean.parseBoolean(optionsSettings.getProperty("ShowEmuLicense", "false"));
			SERVER_LIST_BRACKET                         = Boolean.valueOf(optionsSettings.getProperty("ServerListBrackets", "false"));
			SERVER_LIST_CLOCK                           = Boolean.valueOf(optionsSettings.getProperty("ServerListClock", "false"));
			LOGINSERVER_SHOW_LICENSE                    = Boolean.parseBoolean(optionsSettings.getProperty("LoginServerShowLicence", "false"));
			FISHINGMODE                                 = optionsSettings.getProperty("FishingMode", "water");
			ZONE_TOWN                                   = Integer.parseInt(optionsSettings.getProperty("ZoneTown", "0"));
			DELETE_DAYS                                 = Integer.parseInt(optionsSettings.getProperty("DeleteCharAfterDays", "7"));
		}
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+OPTIONS_FILE+" File.");
		}
	}
	
	//============================================================================================
	public static final String  CRAFTING_FILE               = PathFindingService.CRAFTING_FILE;
	//============================================================================================
    public static double  ALT_GAME_CREATION_SPEED;
	public static double  ALT_GAME_CREATION_XP_RATE;
	public static double  ALT_GAME_CREATION_SP_RATE;
	public static int     COMMON_RECIPE_LIMIT;
	public static int     DWARF_RECIPE_LIMIT;
	public static boolean ALT_GAME_CREATION;
	public static boolean IS_CRAFTING_ENABLED;
	//**********************************************************************************************
	public static void loadCraftingConfig()
	{
		System.out.println("Loading: "+CRAFTING_FILE); 
		try
		{
			Properties craftingSettings   = new L2Properties();
			InputStream is                = new FileInputStream(new File(CRAFTING_FILE));
			craftingSettings.load(is);
			is.close();
			
			IS_CRAFTING_ENABLED                  = Boolean.parseBoolean(craftingSettings.getProperty("CraftingEnabled", "true"));
			DWARF_RECIPE_LIMIT                   = Integer.parseInt(craftingSettings.getProperty("DwarfRecipeLimit","50"));
			COMMON_RECIPE_LIMIT                  = Integer.parseInt(craftingSettings.getProperty("CommonRecipeLimit","50"));
			ALT_GAME_CREATION                    = Boolean.parseBoolean(craftingSettings.getProperty("AltGameCreation", "false"));
			ALT_GAME_CREATION_SPEED              = Double.parseDouble(craftingSettings.getProperty("AltGameCreationSpeed", "1"));
			ALT_GAME_CREATION_XP_RATE            = Double.parseDouble(craftingSettings.getProperty("AltGameCreationRateXp", "1"));
			ALT_GAME_CREATION_SP_RATE            = Double.parseDouble(craftingSettings.getProperty("AltGameCreationRateSp", "1"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+CRAFTING_FILE+" File.");
		}
	}
	
	//=====================================================================================================
	public static final String  TELNET_FILE                 = PathFindingService.TELNET_FILE;
	// ==============================================================================================
	public static int    JMX_TCP_PORT;  // JMX Admin   
	public static int    JMX_HTTP_PORT;
	public static int    TELNET_PASSWORD_LENGTH;
	public static String JMX_KEYSTORE;  
	public static String JMX_KEYSTORE_PASSWORD;  
	public static boolean IS_TELNET_ENABLED;
	
	//***********************************************************************************************
	public static void loadTelnetConfig()
	{
		System.out.println("Loading: " +TELNET_FILE); 
		try
		{
			Properties telnetSettings   = new L2Properties();
			InputStream is              = new FileInputStream(new File(TELNET_FILE));
			telnetSettings.load(is);
			is.close();
			
			TELNET_PASSWORD_LENGTH = Integer.parseInt(telnetSettings.getProperty("TelnetPasswordLength","10"));
			JMX_TCP_PORT           = Integer.parseInt(telnetSettings.getProperty("admin_portJMX","-1"));
			JMX_HTTP_PORT          = Integer.parseInt(telnetSettings.getProperty("admin_portHTTP","-1"));
			JMX_KEYSTORE           = telnetSettings.getProperty("keystore","keystore.jks");
			JMX_KEYSTORE_PASSWORD  = telnetSettings.getProperty("keystore_password","");
			IS_TELNET_ENABLED      = Boolean.valueOf(telnetSettings.getProperty("EnableTelnet", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+TELNET_FILE+" File.");
		}
	}
	//============================================================================================
	public static final String  ID_FACTORY_FILE  = PathFindingService.ID_FACTORY_FILE;
	//============================================================================================
	public static enum IdFactoryType
	{
		Compaction,
		BitSet, 
		Stack, 
		Increment
	}
	public static enum ObjectMapType
	{
		L2ObjectHashMap,
		WorldObjectMap
	}
	
	public static enum ObjectSetType
	{
		L2ObjectHashSet,
		WorldObjectSet
	}
	
	public static ObjectMapType  MAP_TYPE;
	public static ObjectSetType  SET_TYPE;
	public static IdFactoryType  IDFACTORY_TYPE;
	public static boolean        BAD_ID_CHECKING;
	
	//*************************************************************************************
	public static void loadIdFactoryConfig()
	{
		System.out.println("Loading: " +ID_FACTORY_FILE); 
		try
		{
			Properties idFactorySettings   = new L2Properties();
			InputStream is                 = new FileInputStream(new File(ID_FACTORY_FILE));
			idFactorySettings.load(is);
			is.close();
			
			MAP_TYPE               = ObjectMapType.valueOf(idFactorySettings.getProperty("L2Map", "WorldObjectMap"));
			SET_TYPE               = ObjectSetType.valueOf(idFactorySettings.getProperty("L2Set", "WorldObjectSet"));
			IDFACTORY_TYPE         = IdFactoryType.valueOf(idFactorySettings.getProperty("IDFactory", "Compaction"));
			BAD_ID_CHECKING        = Boolean.valueOf(idFactorySettings.getProperty("BadIdChecking", "true"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ID_FACTORY_FILE+" File.");
		}
	}
   
	//===================================================================================================
	public static final String PVTSTORE_FILE           = PathFindingService.PVTSTORE_FILE;
	//===================================================================================================
	public static int     MAX_PVTSTORE_SLOTS_DWARF;
	public static int     MAX_PVTSTORE_SLOTS_OTHER;
	
	//	**************************************************************************************
	public static void loadPvtStoresConfig()
	{
		System.out.println("Loading: " +PVTSTORE_FILE); 
		try
		{
			Properties pvtStoresSettings    = new L2Properties();
			InputStream is              = new FileInputStream(new File(PVTSTORE_FILE));
			pvtStoresSettings.load(is);
			is.close();
			
			MAX_PVTSTORE_SLOTS_DWARF    = Integer.parseInt(pvtStoresSettings.getProperty("MaxPrivateStoreSlotsForDwarf", "5"));
			MAX_PVTSTORE_SLOTS_OTHER    = Integer.parseInt(pvtStoresSettings.getProperty("MaxPrivateStoreSlotsForOther", "4"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+PVTSTORE_FILE +" File.");
		}
	}
	
	//===================================================================================================
	public static final String  WAREHOUSE_FILE           = PathFindingService.WAREHOUSE_FILE;
	//===================================================================================================
	public static int     WAREHOUSE_SLOTS_NO_DWARF;
	public static int     WAREHOUSE_SLOTS_DWARF;
	public static int     MAX_WAREHOUSE_SLOTS_FOR_CLAN;
	public static int     FREIGHT_SLOTS;
	public static int     ALT_GAME_FREIGHT_PRICE;
	public static boolean ALT_GAME_FREIGHTS;
	public static boolean ENABLE_WAREHOUSESORTING_CLAN;    //Warehouse Sorting Clan
	public static boolean ENABLE_WAREHOUSESORTING_PRIVATE; //Warehouse Sorting Private
	public static boolean ENABLE_WAREHOUSESORTING_FREIGHT; //Warehouse Sorting freight
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_FREIGHT;
	
	//*******************************************************************************************
	public static void loadWhConfig()
	{
		System.out.println("Loading: " +WAREHOUSE_FILE); 
		try
		{
			Properties whSettings       = new L2Properties();
			InputStream is              = new FileInputStream(new File(WAREHOUSE_FILE));
			whSettings.load(is);
			is.close();
			
			ALLOW_WAREHOUSE                      = Boolean.valueOf(whSettings.getProperty("AllowWarehouse", "true"));
			ALLOW_FREIGHT                        = Boolean.valueOf(whSettings.getProperty("AllowFreight", "true"));
			ENABLE_WAREHOUSESORTING_CLAN         = Boolean.valueOf(whSettings.getProperty("EnableWarehouseSortingClan", "false"));
			ENABLE_WAREHOUSESORTING_PRIVATE      = Boolean.valueOf(whSettings.getProperty("EnableWarehouseSortingPrivate", "false"));
			ENABLE_WAREHOUSESORTING_FREIGHT      = Boolean.valueOf(whSettings.getProperty("EnableWarehouseSortingFreight", "false"));
			ALT_GAME_FREIGHTS                    = Boolean.parseBoolean(whSettings.getProperty("AltGameFreights", "false"));
			ALT_GAME_FREIGHT_PRICE               = Integer.parseInt(whSettings.getProperty("AltGameFreightPrice", "1000"));
			WAREHOUSE_SLOTS_NO_DWARF             = Integer.parseInt(whSettings.getProperty("MaxWarehouseSlotsForOther", "100"));
			WAREHOUSE_SLOTS_DWARF                = Integer.parseInt(whSettings.getProperty("MaxWarehouseSlotsForDwarf", "120"));
			MAX_WAREHOUSE_SLOTS_FOR_CLAN         = Integer.parseInt(whSettings.getProperty("MaxWarehouseSlotsForClan", "150"));
			FREIGHT_SLOTS                        = Integer.parseInt(whSettings.getProperty("MaxWarehouseFreightSlots", "20"));
			
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ WAREHOUSE_FILE +" File.");
		}
	}
	
    //===================================================================================================
	public static final String  OTHER_FILE           =    PathFindingService.OTHER_FILE;
	//===================================================================================================
	public static int               STARTING_ADENA;
	public static int               PLAYER_SPAWN_PROTECTION;
	public static int               PLAYER_FAKEDEATH_UP_PROTECTION; //Player protection after recovering from fake death (works against mobs only)
	public static int               UNSTUCK_INTERVAL;
	public static int           	DEATH_PENALTY_CHANCE;				
	public static String  			PET_RENT_NPC;
	public static String			FORBIDDEN_RAID_SKILLS;
	public static FastList<Integer> FORBIDDEN_RAID_SKILLS_LIST  = new FastList<Integer>();
	public static FastList<Integer> LIST_PET_RENT_NPC           = new FastList<Integer>();
	
	//******************************************************************************************************
	public static void loadOtherConfig()
	{
		System.out.println("Loading: "+OTHER_FILE); 
		try
		{
			Properties otherSettings    = new L2Properties();
			InputStream is              = new FileInputStream(new File(OTHER_FILE));
			otherSettings.load(is);
			is.close();

			PET_RENT_NPC       = otherSettings.getProperty("ListPetRentNpc", "30827");   
			LIST_PET_RENT_NPC = new FastList<Integer>();   
			
			for (String id : PET_RENT_NPC.split(",")) 
			{   
				LIST_PET_RENT_NPC.add(Integer.parseInt(id));   
			}

			FORBIDDEN_RAID_SKILLS = otherSettings.getProperty("ForbiddenRaidSkills", "1064,100");            
			FORBIDDEN_RAID_SKILLS_LIST = new FastList<Integer>();
			
			for (String id : FORBIDDEN_RAID_SKILLS.trim().split(","))
			{
				FORBIDDEN_RAID_SKILLS_LIST.add(Integer.parseInt(id.trim()));
			}

			STARTING_ADENA                       = Integer.parseInt(otherSettings.getProperty("StartingAdena", "0"));
			UNSTUCK_INTERVAL                     = Integer.parseInt(otherSettings.getProperty("PlayerUnstuckInterval", "350"));
			PLAYER_SPAWN_PROTECTION              = Integer.parseInt(otherSettings.getProperty("PlayerSpawnProtection", "5"));
			PLAYER_FAKEDEATH_UP_PROTECTION       = Integer.parseInt(otherSettings.getProperty("PlayerFakeDeathUpProtection", "0"));
			DEATH_PENALTY_CHANCE                 = Integer.parseInt(otherSettings.getProperty("DeathPenaltyChance", "20"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+OTHER_FILE +" File.");
		}
	}

	//===================================================================================================
	public static final String  RESPAWN_FILE           = PathFindingService.RESPAWN_FILE;
	//===================================================================================================
	public static double  RAID_MINION_RESPAWN_TIMER;     // Raid Boss Minion Spawn Timer
	public static double  RESPAWN_RESTORE_CP;			 // Percent CP is restore on respawn
	public static double  RESPAWN_RESTORE_HP;			 // Percent HP is restore on respawn
	public static double  RESPAWN_RESTORE_MP;			 // Percent MP is restore on respawn
	public static int     RESPAWN_RANDOM_MAX_OFFSET;     // The maximum offset from the base respawn point to allow.
	public static float   RAID_MIN_RESPAWN_MULTIPLIER;   // Mulitplier for Raid boss minimum time respawn
	public static float   RAID_MAX_RESPAWN_MULTIPLIER;   // Mulitplier for Raid boss maximum time respawn
	public static boolean RESPAWN_RANDOM_ENABLED;        // Allow randomizing of the respawn point in towns. 
	public static int	  ALT_DEFAULT_RESTARTTOWN;	     // Set alternative default restarttown
	//****************************************************************************************************
	public static void loadRespawnsConfig()
	{
		System.out.println("Loading: "+RESPAWN_FILE); 
		try
		{
			Properties respawnSettings     = new L2Properties();
			InputStream is                 = new FileInputStream(new File(RESPAWN_FILE));
			respawnSettings.load(is);
			is.close();
			
			ALT_DEFAULT_RESTARTTOWN	    = Integer.parseInt(respawnSettings.getProperty("AltDefaultRestartTown", "0"));
			RESPAWN_RANDOM_MAX_OFFSET   = Integer.parseInt(respawnSettings.getProperty("RespawnRandomMaxOffset", "50"));
			RAID_MINION_RESPAWN_TIMER   = Integer.parseInt(respawnSettings.getProperty("RaidMinionRespawnTime", "300000"));                
			RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(respawnSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
			RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(respawnSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));
			RESPAWN_RANDOM_ENABLED      = Boolean.parseBoolean(respawnSettings.getProperty("RespawnRandomInTown", "false"));
			RESPAWN_RESTORE_CP          = Double.parseDouble(respawnSettings.getProperty("RespawnRestoreCP", "0")) / 100;
			RESPAWN_RESTORE_HP          = Double.parseDouble(respawnSettings.getProperty("RespawnRestoreHP", "70")) / 100;
			RESPAWN_RESTORE_MP          = Double.parseDouble(respawnSettings.getProperty("RespawnRestoreMP", "70")) / 100;
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+RESPAWN_FILE +" File.");
		}
	}
	//===================================================================================================
	public static final String  PETITION_FILE           = PathFindingService.PETITION_FILE;
	//===================================================================================================
	public static int     MAX_PETITIONS_PER_PLAYER;       //max number of petitions per player
	public static int     MAX_PETITIONS_PENDING;          //max number of num checked petitions
	public static boolean PETITIONING_ALLOWED;            //Should we Allow the use of petitions?
	public static boolean PETITION_NEED_GM_ONLINE;        //need a gm online to use petition system?
	//******************************************************************************************************
	public static void loadPetitionSettings()
	{
		System.out.println("Loading: " +PETITION_FILE); 
		try
		{
			Properties petitionSettings    = new L2Properties();
			InputStream is                 = new FileInputStream(new File(PETITION_FILE));
			petitionSettings.load(is);
			is.close();
			
			PETITIONING_ALLOWED                = Boolean.parseBoolean(petitionSettings.getProperty("PetitioningAllowed", "true"));
			MAX_PETITIONS_PER_PLAYER           = Integer.parseInt(petitionSettings.getProperty("MaxPetitionsPerPlayer", "5"));
			MAX_PETITIONS_PENDING              = Integer.parseInt(petitionSettings.getProperty("MaxPetitionsPending", "25"));
			PETITION_NEED_GM_ONLINE            = Boolean.valueOf(petitionSettings.getProperty("PetitioningNeedGmOnline", "true"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+PETITION_FILE +" File.");
		}
	}
	//===================================================================================================
	public static final String  REGEN_FILE           = PathFindingService.REGEN_FILE;
	//===================================================================================================
	public static double  NPC_HP_REGEN_MULTIPLIER;        //Multipler for npcs MP regeneration
	public static double  NPC_MP_REGEN_MULTIPLIER;        //Multipler for npcs HP regeneration
	public static double  PLAYER_CP_REGEN_MULTIPLIER;     //Multipler for player CP regeneration
	public static double  PLAYER_HP_REGEN_MULTIPLIER;     //Multipler for player HP regeneration
	public static double  PLAYER_MP_REGEN_MULTIPLIER;     //Multipler for player MP regeneration
	public static double  RAID_HP_REGEN_MULTIPLIER;       //Multipler for Raid Bosses HP regeneration
	public static double  RAID_MP_REGEN_MULTIPLIER;       //Multipler for Raid Bosses MP regeneration
	public static double  RAID_DEFENCE_MULTIPLIER;        //Multipler for Raid Bosses defence rate
	
	//**************************************************************************************************
	public static void loadRegenSettings()
	{
		System.out.println("Loading: " +REGEN_FILE); 
		try
		{
			Properties regenSettings    = new L2Properties();
			InputStream is              = new FileInputStream(new File(REGEN_FILE));
			regenSettings.load(is);
			is.close();
			
			//if different from 100 (ie 100%) heal rate is modified acordingly 
			NPC_HP_REGEN_MULTIPLIER         = Double.parseDouble(regenSettings.getProperty("NPCHpRegenMultiplier", "100"))/100;
			NPC_MP_REGEN_MULTIPLIER         = Double.parseDouble(regenSettings.getProperty("NPCMpRegenMultiplier", "100"))/100;
			PLAYER_CP_REGEN_MULTIPLIER      = Double.parseDouble(regenSettings.getProperty("PlayerCpRegenMultiplier", "100"))/100;
			PLAYER_HP_REGEN_MULTIPLIER      = Double.parseDouble(regenSettings.getProperty("PlayerHpRegenMultiplier", "100"))/100;
			PLAYER_MP_REGEN_MULTIPLIER      = Double.parseDouble(regenSettings.getProperty("PlayerMpRegenMultiplier", "100"))/100;
			RAID_HP_REGEN_MULTIPLIER        = Double.parseDouble(regenSettings.getProperty("RaidHpRegenMultiplier", "100"))/100;
			RAID_MP_REGEN_MULTIPLIER        = Double.parseDouble(regenSettings.getProperty("RaidMpRegenMultiplier", "100"))/100;
			RAID_DEFENCE_MULTIPLIER         = Double.parseDouble(regenSettings.getProperty("RaidDefenceMultiplier", "100"))/100;
			RAID_MINION_RESPAWN_TIMER       = Integer.parseInt(regenSettings.getProperty("RaidMinionRespawnTime", "300000"));                 
			RAID_MIN_RESPAWN_MULTIPLIER     = Float.parseFloat(regenSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
			RAID_MAX_RESPAWN_MULTIPLIER     = Float.parseFloat(regenSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));              
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+REGEN_FILE +" File.");
		}
	}
	
	//===============================================================================================
	public static final String  ENCHANT_FILE                = PathFindingService.ENCHANT_FILE;
	//===========================================================================================
	public static int     ENCHANT_CHANCE;
	public static int     ENCHANT_CHANCE_JEWELRY;
	public static int     ENCHANT_CHANCE_JEWELRY_CRYSTAL;
	public static int     ENCHANT_CHANCE_JEWELRY_BLESSED;
	public static int     ENCHANT_CHANCE_WEAPON;
	public static int     ENCHANT_CHANCE_ARMOR;
	public static int     ENCHANT_CHANCE_WEAPON_CRYSTAL;
	public static int     ENCHANT_CHANCE_WEAPON_BLESSED;
	public static int     ENCHANT_CHANCE_ARMOR_CRYSTAL;
	public static int     ENCHANT_CHANCE_ARMOR_BLESSED;
	public static int     ENCHANT_MAX_WEAPON;
	public static int     ENCHANT_MAX_ARMOR;
	public static int     ENCHANT_MAX_JEWELRY;
	public static int     ENCHANT_SAFE_MAX;
	public static int     ENCHANT_SAFE_MAX_FULL;
	public static int     ENCHANT_DWARF_1_ENCHANTLEVEL; // Dwarf enchant System Dwarf 1 Enchantlevel?
	public static int     ENCHANT_DWARF_2_ENCHANTLEVEL; // Dwarf enchant System Dwarf 2 Enchantlevel?
	public static int     ENCHANT_DWARF_3_ENCHANTLEVEL; // Dwarf enchant System Dwarf 3 Enchantlevel?
	public static int     ENCHANT_DWARF_1_CHANCE; // Dwarf enchant System Dwarf 1 chance?
	public static int     ENCHANT_DWARF_2_CHANCE; // Dwarf enchant System Dwarf 2 chance?
	public static int     ENCHANT_DWARF_3_CHANCE; // Dwarf enchant System Dwarf 3 chance?
	public static boolean ENCHANT_BREAK_JEWELRY;
	public static boolean ENCHANT_BREAK_JEWELRY_CRYSTAL;
	public static boolean ENCHANT_BREAK_JEWELRY_BLESSED;
	public static boolean ENCHANT_BREAK_WEAPON;
	public static boolean ENCHANT_BREAK_ARMOR;
	public static boolean ENCHANT_BREAK_WEAPON_CRYSTAL;
	public static boolean ENCHANT_BREAK_WEAPON_BLESSED;
	public static boolean ENCHANT_BREAK_ARMOR_CRYSTAL;
	public static boolean ENCHANT_BREAK_ARMOR_BLESSED;
	public static boolean ENCHANT_DWARF_SYSTEM;
	public static boolean ENCHANT_HERO_WEAPONS;
	public static boolean ZARICHE_CAN_BE_ENCHANTED;
	
	//********************************************************************************************
	public static void loadEnchantConfig()
	{
		System.out.println("Loading: " +ENCHANT_FILE); 
		try  
		{  
			Properties enchantSettings  = new L2Properties();
			InputStream is              = new FileInputStream(new File(ENCHANT_FILE));
			enchantSettings.load(is);
			is.close();
			
			ENCHANT_MAX_JEWELRY            = Integer.parseInt(enchantSettings.getProperty("EnchantMaxJewelry", "255"));
			ENCHANT_BREAK_JEWELRY_BLESSED  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelryBlessed", "true"));
			ENCHANT_CHANCE_JEWELRY_BLESSED = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelryBlessed", "65"));
			ENCHANT_BREAK_JEWELRY_CRYSTAL  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelryCrystal", "true"));
			ENCHANT_CHANCE_JEWELRY_CRYSTAL = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelryCrystal", "75"));
			ENCHANT_BREAK_JEWELRY          = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelry", "true"));
			ENCHANT_CHANCE_JEWELRY         = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelry", "65"));
			ENCHANT_CHANCE                 = Integer.parseInt(enchantSettings.getProperty("EnchantChance", "65"));
			ENCHANT_CHANCE_WEAPON          = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeapon", "65"));
			ENCHANT_CHANCE_ARMOR           = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmor", "65"));
			ENCHANT_MAX_WEAPON             = Integer.parseInt(enchantSettings.getProperty("EnchantMaxWeapon", "255"));
			ENCHANT_MAX_ARMOR              = Integer.parseInt(enchantSettings.getProperty("EnchantMaxArmor", "255"));
			ENCHANT_SAFE_MAX               = Integer.parseInt(enchantSettings.getProperty("EnchantSafeMax", "3"));
			ENCHANT_SAFE_MAX_FULL          = Integer.parseInt(enchantSettings.getProperty("EnchantSafeMaxFull", "4"));
			ENCHANT_BREAK_WEAPON           = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeapon", "true"));
			ENCHANT_BREAK_ARMOR            = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmor", "true"));
			ENCHANT_CHANCE_WEAPON_CRYSTAL  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeaponCrystal", "75"));
			ENCHANT_CHANCE_ARMOR_CRYSTAL   = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmorCrystal", "75"));
			ENCHANT_BREAK_WEAPON_CRYSTAL   = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeaponCrystal", "true"));
			ENCHANT_BREAK_ARMOR_CRYSTAL    = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmorCrystal", "true"));
			ENCHANT_CHANCE_WEAPON_BLESSED  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeaponBlessed", "65"));
			ENCHANT_CHANCE_ARMOR_BLESSED   = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmorBlessed", "65"));
			ENCHANT_BREAK_WEAPON_BLESSED   = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeaponBlessed", "false"));
			ENCHANT_BREAK_ARMOR_BLESSED    = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmorBlessed", "false"));
			ZARICHE_CAN_BE_ENCHANTED       = Boolean.parseBoolean(enchantSettings.getProperty("ZaricheCanBeEnchanted", "false"));
			ENCHANT_HERO_WEAPONS           = Boolean.parseBoolean(enchantSettings.getProperty("HeroWeaponsCanBeEnchanted", "false"));
			ENCHANT_DWARF_1_ENCHANTLEVEL   = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf1Enchantlevel", "8"));
			ENCHANT_DWARF_2_ENCHANTLEVEL   = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf2Enchantlevel", "10"));
			ENCHANT_DWARF_3_ENCHANTLEVEL   = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf3Enchantlevel", "12"));
			ENCHANT_DWARF_1_CHANCE         = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf1Chance", "15"));
			ENCHANT_DWARF_2_CHANCE         = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf2Chance", "15"));
			ENCHANT_DWARF_3_CHANCE         = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf3Chance", "15"));
		}  
		catch (Exception e)
		{  
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ENCHANT_FILE+" File.");
		}  
	}
	
	//==============================================================================================
	public static final String  RATES_FILE           = PathFindingService.RATES_FILE;
	//===================================================================================================
	public static int     PLAYER_DROP_LIMIT;
	public static int     PLAYER_RATE_DROP;
	public static int     PLAYER_RATE_DROP_ITEM;
	public static int     PLAYER_RATE_DROP_EQUIP;
	public static int     PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static int     PLAYER_RATE_DROP_ADENA;
	public static int     KARMA_DROP_LIMIT;
	public static int     KARMA_RATE_DROP;
	public static int     KARMA_RATE_DROP_ITEM;
	public static int     KARMA_RATE_DROP_EQUIP;
	public static int     KARMA_RATE_DROP_EQUIP_WEAPON;
	public static int     RATE_DROP_MANOR;
	public static float   RATE_XP;
	public static float   RATE_SP;
	public static float   RATE_PARTY_XP;
	public static float   RATE_PARTY_SP;
	public static float   RATE_QUESTS_REWARD;
	public static float   RATE_RUN_SPEED; 
	public static float   RATE_DROP_ADENA;
	public static float   RATE_CONSUMABLE_COST;
	public static float   RATE_CRAFT_COST;
	public static float   RATE_DROP_ITEMS;
	public static float   RATE_DROP_SPOIL;
	public static float   RATE_DROP_QUEST;
	public static float   RATE_KARMA_EXP_LOST;
	public static float   RATE_SIEGE_GUARDS_PRICE;
	public static float   RATE_DROP_COMMON_HERBS;
	public static float   RATE_DROP_MP_HP_HERBS;
	public static float   RATE_DROP_GREATER_HERBS;
	public static float   RATE_DROP_SUPERIOR_HERBS;
	public static float   RATE_DROP_SPECIAL_HERBS;
	public static float   PET_XP_RATE;
	public static float   PET_FOOD_RATE;
	public static float   SINEATER_XP_RATE;
	public static boolean ALT_PLAYER_CAN_DROP_ADENA;
	
	//**************************************************************************************************
	public static void loadRatesConfig()
	{
		System.out.println("Loading: " +RATES_FILE); 
		try
		{
			Properties ratesSettings    = new L2Properties();
			InputStream is              = new FileInputStream(new File(RATES_FILE));
			ratesSettings.load(is);
			is.close();
			
			//**************** GAMESERVER RATES ************************/
			SINEATER_XP_RATE                = Float.parseFloat(ratesSettings.getProperty("SinEaterXpRate", "1."));
			RATE_XP                         = Float.parseFloat(ratesSettings.getProperty("RateXp", "1."));
			RATE_SP                         = Float.parseFloat(ratesSettings.getProperty("RateSp", "1."));
			RATE_PARTY_XP                   = Float.parseFloat(ratesSettings.getProperty("RatePartyXp", "1."));
			RATE_PARTY_SP                   = Float.parseFloat(ratesSettings.getProperty("RatePartySp", "1."));
			RATE_QUESTS_REWARD              = Float.parseFloat(ratesSettings.getProperty("RateQuestsReward", "1."));
			RATE_DROP_ADENA                 = Float.parseFloat(ratesSettings.getProperty("RateDropAdena", "1."));
			RATE_CONSUMABLE_COST            = Float.parseFloat(ratesSettings.getProperty("RateConsumableCost", "1."));
			RATE_CRAFT_COST                 = Float.parseFloat(ratesSettings.getProperty("RateCraftCost","1."));
			RATE_DROP_ITEMS                 = Float.parseFloat(ratesSettings.getProperty("RateDropItems", "1."));
			RATE_DROP_SPOIL                 = Float.parseFloat(ratesSettings.getProperty("RateDropSpoil", "1."));
			RATE_DROP_QUEST                 = Float.parseFloat(ratesSettings.getProperty("RateDropQuest", "1."));
			RATE_KARMA_EXP_LOST             = Float.parseFloat(ratesSettings.getProperty("RateKarmaExpLost", "1."));
			RATE_SIEGE_GUARDS_PRICE         = Float.parseFloat(ratesSettings.getProperty("RateSiegeGuardsPrice", "1."));
			RATE_DROP_COMMON_HERBS          = Float.parseFloat(ratesSettings.getProperty("RateCommonHerbs", "15."));
			RATE_DROP_MP_HP_HERBS           = Float.parseFloat(ratesSettings.getProperty("RateHpMpHerbs", "10."));
			RATE_DROP_GREATER_HERBS         = Float.parseFloat(ratesSettings.getProperty("RateGreaterHerbs", "4."));
			RATE_DROP_SUPERIOR_HERBS        = Float.parseFloat(ratesSettings.getProperty("RateSuperiorHerbs", "0.8"))*10;
			RATE_DROP_SPECIAL_HERBS         = Float.parseFloat(ratesSettings.getProperty("RateSpecialHerbs", "0.2"))*10;
			RATE_RUN_SPEED                  = Float.parseFloat(ratesSettings.getProperty("RateRunSpeed", "1.")); 
			RATE_DROP_MANOR                 = Integer.parseInt(ratesSettings.getProperty("RateDropManor", "1"));
			
			//*************** PLAYER RATES *************/
			PLAYER_DROP_LIMIT               = Integer.parseInt(ratesSettings.getProperty("PlayerDropLimit", "3"));
			PLAYER_RATE_DROP                = Integer.parseInt(ratesSettings.getProperty("PlayerRateDrop", "5"));
			PLAYER_RATE_DROP_ITEM           = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropItem", "70"));
			PLAYER_RATE_DROP_EQUIP          = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquip", "25"));
			PLAYER_RATE_DROP_EQUIP_WEAPON   = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));
			PET_XP_RATE                     = Float.parseFloat(ratesSettings.getProperty("PetXpRate", "1."));
			PET_FOOD_RATE                   = Float.parseFloat(ratesSettings.getProperty("PetFoodRate", "1"));
			KARMA_DROP_LIMIT                = Integer.parseInt(ratesSettings.getProperty("KarmaDropLimit", "10"));
			KARMA_RATE_DROP                 = Integer.parseInt(ratesSettings.getProperty("KarmaRateDrop", "70"));
			KARMA_RATE_DROP_ITEM            = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropItem", "50"));
			KARMA_RATE_DROP_EQUIP           = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquip", "40"));
			KARMA_RATE_DROP_EQUIP_WEAPON    = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));

		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+RATES_FILE+" File.");
		}
	}
    //============================================================================================
	public static final String  LEVELING_FILE           = PathFindingService.LEVELING_FILE;
	//=================================================================================================
	public static float    ALT_GAME_EXPONENT_XP;
	public static float    ALT_GAME_EXPONENT_SP;
	public static boolean  ALT_GAME_DELEVEL;
	
	//*************************************************************************************************
	public static void loadLevelingConfig()
	{
		System.out.println("Loading: " +LEVELING_FILE); 
		try
		{
			Properties levelingSettings  = new L2Properties();
			InputStream is          = new FileInputStream(new File(LEVELING_FILE));
			levelingSettings.load(is);
			is.close();
			
			ALT_GAME_DELEVEL                     = Boolean.parseBoolean(levelingSettings.getProperty("Delevel", "true"));
			ALT_GAME_EXPONENT_XP                 = Float.parseFloat(levelingSettings.getProperty("AltGameExponentXp", "0."));
			ALT_GAME_EXPONENT_SP                 = Float.parseFloat(levelingSettings.getProperty("AltGameExponentSp", "0."));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+LEVELING_FILE+" File.");
		}
	}
	//============================================================================================
	public static final String  ALT_FILE           =  PathFindingService.ALT_FILE;
	//============================================================================================
	public static int      ALT_URN_TEMP_FAIL;
    public static int      CHANCE_BREAK;
	public static int      CHANCE_LEVEL;
	public static float    ALT_GAME_SUMMON_PENALTY_RATE;      // Alternative game summon penalty  
	public static boolean  ALT_GAME_FREE_TELEPORT;
	public static boolean  ALT_STRICT_HERO_SYSTEM;
	
	//********************************************************************************************
	public static void loadAlternativeConfig()
	{
		System.out.println("Loading: " +ALT_FILE); 
		try
		{
			Properties altSettings  = new L2Properties();
			InputStream is          = new FileInputStream(new File(ALT_FILE));
			altSettings.load(is);
			is.close();
			
			ALT_STRICT_HERO_SYSTEM               = Boolean.parseBoolean(altSettings.getProperty("StrictHeroSystem", "true"));
			ALT_URN_TEMP_FAIL                    = Integer.parseInt(altSettings.getProperty("UrnTempFail", "10"));
			ALT_GAME_TIREDNESS                   = Boolean.parseBoolean(altSettings.getProperty("AltGameTiredness", "false"));
			CHANCE_BREAK                         = Integer.parseInt(altSettings.getProperty("ChanceToBreak", "10"));
			CHANCE_LEVEL                         = Integer.parseInt(altSettings.getProperty("ChanceToLevel", "32"));
			ALT_GAME_SUMMON_PENALTY_RATE         = Float.parseFloat(altSettings.getProperty("AltSummonPenaltyRate", "1."));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ALT_FILE+" File.");
		}
	}
	
	//================================================================================================
	public static final String  DEV_FILE              = PathFindingService.DEV_FILE;
	//=================================================================================================
	public static boolean ASSERT;
	public static boolean DEVELOPER;
	public static boolean ALT_DEV_NO_QUESTS;
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean SERVER_LIST_TESTSERVER;			// Display test server in the list of servers ?
    public static boolean ENABLE_JYTHON_SHELL;				// JythonShell
    public static boolean ENABLEVANHALTERMANAGER;			//Enabled Van Halter Manager
	//**************************************************************************************************
	public static void loadDevConfig()
	{
		System.out.println("Loading: " +DEV_FILE); 
		
		try
		{
			Properties devSettings  = new L2Properties();
			InputStream is          = new FileInputStream(new File(DEV_FILE));
			devSettings.load(is);
			is.close();
			
			ENABLEVANHALTERMANAGER            = Boolean.parseBoolean(devSettings.getProperty("EnableVanHalterManager", "false"));
			ENABLE_JYTHON_SHELL               = Boolean.parseBoolean(devSettings.getProperty("EnableJythonShell", "false"));
			SERVER_LIST_TESTSERVER            = Boolean.parseBoolean(devSettings.getProperty("TestServer", "false"));
		    ASSERT                            = Boolean.parseBoolean(devSettings.getProperty("Assert", "false"));
			DEVELOPER                         = Boolean.parseBoolean(devSettings.getProperty("Developer", "false"));
			ALT_DEV_NO_QUESTS                 = Boolean.parseBoolean(devSettings.getProperty("FastServerLoad", "false"));
			ALT_DEV_NO_SPAWNS                 = Boolean.parseBoolean(devSettings.getProperty("FastServerLoad2", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+DEV_FILE+" File.");
		}
	}
	
	//================================================================================================
	public static final String ANTAHARAS_FILE       = PathFindingService.ANTAHARAS_FILE;
	//================================================================================================
	public static int 		FWA_INTERVALOFANTHARAS;
    public static int 		FWA_APPTIMEOFANTHARAS;
    public static int 		FWA_ACTIVITYTIMEOFANTHARAS;
    public static int 		FWA_LIMITOFWEAK;
    public static int 		FWA_LIMITOFNORMAL;
    public static int 		FWA_INTERVALOFBEHEMOTHONWEAK;
    public static int 		FWA_INTERVALOFBEHEMOTHONNORMAL;
    public static int 		FWA_INTERVALOFBEHEMOTHONSTRONG;
    public static int 		FWA_INTERVALOFBOMBERONWEAK;
    public static int 		FWA_INTERVALOFBOMBERONNORMAL;
    public static int 		FWA_INTERVALOFBOMBERONSTRONG;
    public static boolean 	FWA_MOVEATRANDOM;
    public static boolean 	FWA_OLDANTHARAS;
    
    //*******************************************************************************
	public static void loadAntharasConfig()
	{
		System.out.println("Loading: " +ANTAHARAS_FILE);
		try
		{
			Properties antharasSettings  = new L2Properties();
			InputStream is               = new FileInputStream(new File(ANTAHARAS_FILE));
			antharasSettings.load(is);
			is.close();
			
			FWA_INTERVALOFANTHARAS = Integer.parseInt(antharasSettings.getProperty("IntervalOfAntharas", "1440"));
            if(FWA_INTERVALOFANTHARAS < 5 || FWA_INTERVALOFANTHARAS > 1440) FWA_INTERVALOFANTHARAS = 1440;
            FWA_INTERVALOFANTHARAS = FWA_INTERVALOFANTHARAS * 60000;
            FWA_APPTIMEOFANTHARAS = Integer.parseInt(antharasSettings.getProperty("AppTimeOfAntharas", "10"));
            if(FWA_APPTIMEOFANTHARAS < 5 || FWA_APPTIMEOFANTHARAS > 60) FWA_APPTIMEOFANTHARAS = 10;
            FWA_APPTIMEOFANTHARAS = FWA_APPTIMEOFANTHARAS * 60000;
            FWA_ACTIVITYTIMEOFANTHARAS = Integer.parseInt(antharasSettings.getProperty("ActivityTimeOfAntharas", "120"));
            if(FWA_ACTIVITYTIMEOFANTHARAS < 120 || FWA_ACTIVITYTIMEOFANTHARAS > 720) FWA_ACTIVITYTIMEOFANTHARAS = 120;
            FWA_ACTIVITYTIMEOFANTHARAS = FWA_ACTIVITYTIMEOFANTHARAS * 60000;
            FWA_OLDANTHARAS = Boolean.parseBoolean(antharasSettings.getProperty("OldAntharas", "false"));
            FWA_LIMITOFWEAK = Integer.parseInt(antharasSettings.getProperty("LimitOfWeak", "299"));
            FWA_LIMITOFNORMAL = Integer.parseInt(antharasSettings.getProperty("LimitOfNormal", "399"));
            FWA_INTERVALOFBEHEMOTHONWEAK = Integer.parseInt(antharasSettings.getProperty("IntervalOfBehemothOnWeak", "8"));
            if(FWA_INTERVALOFBEHEMOTHONWEAK < 1 || FWA_INTERVALOFBEHEMOTHONWEAK > 10) FWA_INTERVALOFBEHEMOTHONWEAK = 8;
            FWA_INTERVALOFBEHEMOTHONWEAK = FWA_INTERVALOFBEHEMOTHONWEAK * 60000;
            FWA_INTERVALOFBEHEMOTHONNORMAL = Integer.parseInt(antharasSettings.getProperty("IntervalOfBehemothOnNormal", "5"));
            if(FWA_INTERVALOFBEHEMOTHONNORMAL < 1 || FWA_INTERVALOFBEHEMOTHONNORMAL > 10) FWA_INTERVALOFBEHEMOTHONNORMAL = 5;
            FWA_INTERVALOFBEHEMOTHONNORMAL = FWA_INTERVALOFBEHEMOTHONNORMAL * 60000;
            FWA_INTERVALOFBEHEMOTHONSTRONG = Integer.parseInt(antharasSettings.getProperty("IntervalOfBehemothOnStrong", "3"));
            if(FWA_INTERVALOFBEHEMOTHONSTRONG < 1 || FWA_INTERVALOFBEHEMOTHONSTRONG > 10) FWA_INTERVALOFBEHEMOTHONSTRONG = 3;
            FWA_INTERVALOFBEHEMOTHONSTRONG = FWA_INTERVALOFBEHEMOTHONSTRONG * 60000;
            FWA_INTERVALOFBOMBERONWEAK = Integer.parseInt(antharasSettings.getProperty("IntervalOfBomberOnWeak", "6"));
            if(FWA_INTERVALOFBOMBERONWEAK < 1 || FWA_INTERVALOFBOMBERONWEAK > 10) FWA_INTERVALOFBOMBERONWEAK = 6;
            FWA_INTERVALOFBOMBERONWEAK = FWA_INTERVALOFBOMBERONWEAK * 60000;
            FWA_INTERVALOFBOMBERONNORMAL = Integer.parseInt(antharasSettings.getProperty("IntervalOfBomberOnNormal", "4"));
            if(FWA_INTERVALOFBOMBERONNORMAL < 1 || FWA_INTERVALOFBOMBERONNORMAL > 10) FWA_INTERVALOFBOMBERONNORMAL = 4;
            FWA_INTERVALOFBOMBERONNORMAL = FWA_INTERVALOFBOMBERONNORMAL * 60000;
            FWA_INTERVALOFBOMBERONSTRONG = Integer.parseInt(antharasSettings.getProperty("IntervalOfBomberOnStrong", "3"));
            if(FWA_INTERVALOFBOMBERONSTRONG < 1 || FWA_INTERVALOFBOMBERONSTRONG > 10) FWA_INTERVALOFBOMBERONSTRONG = 3;
            FWA_INTERVALOFBOMBERONSTRONG = FWA_INTERVALOFBOMBERONSTRONG * 60000;
            FWA_MOVEATRANDOM = Boolean.parseBoolean(antharasSettings.getProperty("MoveAtRandom", "true"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ANTAHARAS_FILE+" File.");
		}
	}
	
	//================================================================================================
	public static final String  BAIUM_FILE = PathFindingService.BAIUM_FILE;
	// =================================================================================================
 	 public static int FWB_INTERVALOFBAIUM;
	 public static int FWB_ACTIVITYTIMEOFBAIUM;
	 public static boolean FWB_MOVEATRANDOM;
	
	//*******************************************************************************
	public static void loadBaiumConfig()
	{
		System.out.println("Loading: " +BAIUM_FILE);
		try
		{
			Properties baiumSettings  = new L2Properties();
			InputStream is            = new FileInputStream(new File(BAIUM_FILE));
			baiumSettings.load(is);
			is.close();
			
			FWB_INTERVALOFBAIUM = Integer.parseInt(baiumSettings.getProperty("IntervalOfBaium", "1440"));
	        if(FWB_INTERVALOFBAIUM < 5 || FWB_INTERVALOFBAIUM > 1440) FWB_INTERVALOFBAIUM = 1440;
	        FWB_INTERVALOFBAIUM = FWB_INTERVALOFBAIUM * 60000;
	        FWB_ACTIVITYTIMEOFBAIUM = Integer.parseInt(baiumSettings.getProperty("ActivityTimeOfBaium", "120"));
	        if(FWB_ACTIVITYTIMEOFBAIUM < 120 || FWB_ACTIVITYTIMEOFBAIUM > 720) FWB_ACTIVITYTIMEOFBAIUM = 120;
	        FWB_ACTIVITYTIMEOFBAIUM = FWB_ACTIVITYTIMEOFBAIUM * 60000;
	        FWB_MOVEATRANDOM = Boolean.parseBoolean(baiumSettings.getProperty("MoveAtRandom", "true"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+BAIUM_FILE+" File.");
		}
	}
	//L2EMU_ADD_START
	//================================================================================================
	public static final String  VANHALTER_FILE = PathFindingService.VANHALTER_FILE;
	// =================================================================================================
    public static int HPH_FIXINTERVALOFHALTER;
    public static int HPH_RANDOMINTERVALOFHALTER;
    public static int HPH_APPTIMEOFHALTER;
    public static int HPH_ACTIVITYTIMEOFHALTER;
    public static int HPH_FIGHTTIMEOFHALTER;
    public static int HPH_CALLROYALGUARDHELPERCOUNT;
    public static int HPH_CALLROYALGUARDHELPERINTERVAL;
    public static int HPH_INTERVALOFDOOROFALTER;
    public static int HPH_TIMEOFLOCKUPDOOROFALTAR;
	
	//*******************************************************************************
	public static void loadVanHalterConfig()
	{
		System.out.println("Loading: " +VANHALTER_FILE);
		try
		{
			Properties VanHalterSettings  = new L2Properties();
			InputStream is            = new FileInputStream(new File(VANHALTER_FILE));
			VanHalterSettings.load(is);
			is.close();
			
			HPH_FIXINTERVALOFHALTER = Integer.parseInt(VanHalterSettings.getProperty("FixIntervalOfHalter", "172800"));
			if (HPH_FIXINTERVALOFHALTER < 300 || HPH_FIXINTERVALOFHALTER > 864000) {
			    HPH_FIXINTERVALOFHALTER = 172800;
			}
			HPH_RANDOMINTERVALOFHALTER = Integer.parseInt(VanHalterSettings.getProperty("RandomIntervalOfHalter", "86400"));
			if (HPH_RANDOMINTERVALOFHALTER < 300 || HPH_RANDOMINTERVALOFHALTER > 864000) {
			    HPH_RANDOMINTERVALOFHALTER = 86400;
			}
			HPH_APPTIMEOFHALTER = Integer.parseInt(VanHalterSettings.getProperty("AppTimeOfHalter", "20"));
			if (HPH_APPTIMEOFHALTER < 5 || HPH_APPTIMEOFHALTER > 60) {
			    HPH_APPTIMEOFHALTER = 20;
			}
			HPH_ACTIVITYTIMEOFHALTER = Integer.parseInt(VanHalterSettings.getProperty("ActivityTimeOfHalter", "21600"));
			if (HPH_ACTIVITYTIMEOFHALTER < 7200 || HPH_ACTIVITYTIMEOFHALTER > 86400) {
			    HPH_ACTIVITYTIMEOFHALTER = 21600;
			}
			HPH_FIGHTTIMEOFHALTER = Integer.parseInt(VanHalterSettings.getProperty("FightTimeOfHalter", "7200"));
			if (HPH_FIGHTTIMEOFHALTER < 7200 || HPH_FIGHTTIMEOFHALTER > 21600) {
			    HPH_FIGHTTIMEOFHALTER = 7200;
			}
			HPH_CALLROYALGUARDHELPERCOUNT = Integer.parseInt(VanHalterSettings.getProperty("CallRoyalGuardHelperCount", "6"));
			if (HPH_CALLROYALGUARDHELPERCOUNT < 1 || HPH_CALLROYALGUARDHELPERCOUNT > 6) {
			    HPH_CALLROYALGUARDHELPERCOUNT = 6;
			}
			HPH_CALLROYALGUARDHELPERINTERVAL = Integer.parseInt(VanHalterSettings.getProperty("CallRoyalGuardHelperInterval", "10"));
			if (HPH_CALLROYALGUARDHELPERINTERVAL < 1 || HPH_CALLROYALGUARDHELPERINTERVAL > 60) {
			    HPH_CALLROYALGUARDHELPERINTERVAL = 10;
			}
			HPH_INTERVALOFDOOROFALTER = Integer.parseInt(VanHalterSettings.getProperty("IntervalOfDoorOfAlter", "5400"));
			if (HPH_INTERVALOFDOOROFALTER < 60 || HPH_INTERVALOFDOOROFALTER > 5400) {
			    HPH_INTERVALOFDOOROFALTER = 5400;
			}
			HPH_TIMEOFLOCKUPDOOROFALTAR = Integer.parseInt(VanHalterSettings.getProperty("TimeOfLockUpDoorOfAltar", "180"));
			if (HPH_TIMEOFLOCKUPDOOROFALTAR < 60 || HPH_TIMEOFLOCKUPDOOROFALTAR > 600) {
			    HPH_TIMEOFLOCKUPDOOROFALTAR = 180;
			}	
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+VANHALTER_FILE+" File.");
		}
	}
	//L2EMU_ADD_END
	
	//================================================================================================
	public static final String  ANTNEST_FILE = PathFindingService.ANTNEST_FILE;
	// =================================================================================================
	public static int NURSEANT_RESPAWN_DELAY;
	
	//********************************************************************************
	public static void loadAntNestConfig()
	{
		System.out.println("Loading: " +ANTNEST_FILE);
		try
		{
			Properties antNestSettings  = new L2Properties();
			InputStream is              = new FileInputStream(new File(ANTNEST_FILE));
			antNestSettings.load(is);
			is.close();

			NURSEANT_RESPAWN_DELAY                              = Integer.parseInt(antNestSettings.getProperty("NurseAntRespawnDelay", "15"));
			if (NURSEANT_RESPAWN_DELAY < 15) NURSEANT_RESPAWN_DELAY = 15;
			else if (NURSEANT_RESPAWN_DELAY > 120) NURSEANT_RESPAWN_DELAY = 120;
			NURSEANT_RESPAWN_DELAY = NURSEANT_RESPAWN_DELAY * 1000;
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ANTNEST_FILE+" File.");
		}
	}
    //================================================================================================
	public static final String  DIMENSIONAL_RIFT_FILE =   PathFindingService.DIMENSIONAL_RIFT_FILE;
	// =================================================================================================
	public static int   RIFT_MIN_PARTY_SIZE;            //Minimum size of a party that may enter dimensional rift */
    public static int   RIFT_MAX_JUMPS;                 // Amount of random rift jumps before party is ported back */
    public static int   RIFT_AUTO_JUMPS_TIME_MIN;       // Random time between two jumps in dimensional rift - in seconds */
	public static int   RIFT_AUTO_JUMPS_TIME_MAX;
    public static int   RIFT_ENTER_COST_RECRUIT;        //Dimensional Fragment cost for entering rift */
	public static int   RIFT_ENTER_COST_SOLDIER;
	public static int   RIFT_ENTER_COST_OFFICER;
	public static int   RIFT_ENTER_COST_CAPTAIN;
	public static int   RIFT_ENTER_COST_COMMANDER;
	public static int   RIFT_ENTER_COST_HERO;
    public static int   RIFT_SPAWN_DELAY;                //Time in ms the party has to wait until the mobs spawn when entering a room */    
    public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;     // time multiplier for boss room 
	
    //********************************************************************************
	public static void loadDrConfig()
	{
		System.out.println("Loading: " +DIMENSIONAL_RIFT_FILE);
		try
		{
			Properties drSettings   = new L2Properties();
			InputStream is          = new FileInputStream(new File(DIMENSIONAL_RIFT_FILE));
			drSettings.load(is);
			is.close();
			
			RIFT_SPAWN_DELAY                 = Integer.parseInt(drSettings.getProperty("RiftSpawnDelay", "10000")); 
			RIFT_MIN_PARTY_SIZE              = Integer.parseInt(drSettings.getProperty("RiftMinPartySize", "5")); 
			RIFT_MAX_JUMPS                   = Integer.parseInt(drSettings.getProperty("MaxRiftJumps", "4")); 
			RIFT_AUTO_JUMPS_TIME_MIN         = Integer.parseInt(drSettings.getProperty("AutoJumpsDelayMin", "480")); 
			RIFT_AUTO_JUMPS_TIME_MAX         = Integer.parseInt(drSettings.getProperty("AutoJumpsDelayMax", "600")); 
			RIFT_ENTER_COST_RECRUIT          = Integer.parseInt(drSettings.getProperty("RecruitCost", "18")); 
			RIFT_ENTER_COST_SOLDIER          = Integer.parseInt(drSettings.getProperty("SoldierCost", "21")); 
			RIFT_ENTER_COST_OFFICER          = Integer.parseInt(drSettings.getProperty("OfficerCost", "24")); 
			RIFT_ENTER_COST_CAPTAIN          = Integer.parseInt(drSettings.getProperty("CaptainCost", "27")); 
			RIFT_ENTER_COST_COMMANDER        = Integer.parseInt(drSettings.getProperty("CommanderCost", "30")); 
			RIFT_ENTER_COST_HERO             = Integer.parseInt(drSettings.getProperty("HeroCost", "33")); 
			RIFT_BOSS_ROOM_TIME_MUTIPLY      = Float.parseFloat(drSettings.getProperty("BossRoomTimeMultiply", "1.5"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+DIMENSIONAL_RIFT_FILE+" File.");
		}
	}
	
    //================================================================================================
	public static final String  VALAKAS_FILE = PathFindingService.VALAKAS_FILE;
	//=================================================================================================
	public static int     CAPACITY_OF_LAIR_OF_VALAKAS;
	public static int     FWV_INTERVALOFVALAKAS;
	public static int     FWV_APPTIMEOFVALAKAS;
	public static int     FWV_ACTIVITYTIMEOFVALAKAS;
	public static int     FWV_CAPACITYOFLAIR;
	public static boolean FWV_MOVEATRANDOM;
    
	//*******************************************************************************
	public static void loadValakasConfig()
	{
		System.out.println("Loading: " +VALAKAS_FILE);
		try
		{
			Properties valakasSettings  = new L2Properties();
			InputStream is          = new FileInputStream(new File(VALAKAS_FILE));
			valakasSettings.load(is);
			is.close();
			
            FWV_INTERVALOFVALAKAS = Integer.parseInt(valakasSettings.getProperty("IntervalOfValakas", "1440"));
			if(FWV_INTERVALOFVALAKAS < 5 || FWV_INTERVALOFVALAKAS > 1440) FWV_INTERVALOFVALAKAS = 1440;
			FWV_INTERVALOFVALAKAS = FWV_INTERVALOFVALAKAS * 60000;
			FWV_APPTIMEOFVALAKAS = Integer.parseInt(valakasSettings.getProperty("AppTimeOfValakas", "20"));
			if(FWV_APPTIMEOFVALAKAS < 5 || FWV_APPTIMEOFVALAKAS > 60) FWV_APPTIMEOFVALAKAS = 10;
			FWV_APPTIMEOFVALAKAS = FWV_APPTIMEOFVALAKAS * 60000;
			FWV_ACTIVITYTIMEOFVALAKAS = Integer.parseInt(valakasSettings.getProperty("ActivityTimeOfValakas", "120"));
			if(FWV_ACTIVITYTIMEOFVALAKAS < 120 || FWV_ACTIVITYTIMEOFVALAKAS > 720) FWV_ACTIVITYTIMEOFVALAKAS = 120;
			FWV_ACTIVITYTIMEOFVALAKAS = FWV_ACTIVITYTIMEOFVALAKAS * 60000;
			FWV_CAPACITYOFLAIR = Integer.parseInt(valakasSettings.getProperty("CapacityOfLairOfValakas", "200"));
			FWV_MOVEATRANDOM = Boolean.parseBoolean(valakasSettings.getProperty("MoveAtRandom", "true"));

	
		
		CAPACITY_OF_LAIR_OF_VALAKAS                         = Integer.parseInt(valakasSettings.getProperty("CapacityOfLairOfValakas", "200"));
        if (CAPACITY_OF_LAIR_OF_VALAKAS < 9) CAPACITY_OF_LAIR_OF_VALAKAS = 9;
		else if (CAPACITY_OF_LAIR_OF_VALAKAS > 360) CAPACITY_OF_LAIR_OF_VALAKAS = 360;
		
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+VALAKAS_FILE+" File.");
		}
	}
	
	//================================================================================================
	public static final String  FOUR_SEPULCHERS_FILE = PathFindingService.FOUR_SEPULCHERS_FILE;
	//=================================================================================================
	public static int TIME_IN_A_DAY_OF_OPEN_A_DOOR;
	public static int TIME_OF_OPENING_A_DOOR;
	// [L2J_JP ADD SANDMAN]
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	
	//*************************************************************************************************
	public static void loadFsConfig()
	{
		System.out.println("Loading: " +FOUR_SEPULCHERS_FILE);
		try
		{
			Properties FSSettings  = new L2Properties();
			InputStream is         = new FileInputStream(new File(FOUR_SEPULCHERS_FILE));
			FSSettings.load(is);
			is.close();
			
			TIME_IN_A_DAY_OF_OPEN_A_DOOR                        = Integer.parseInt(FSSettings.getProperty("TimeInADayOfOpenADoor", "0"));
			TIME_OF_OPENING_A_DOOR                              = Integer.parseInt(FSSettings.getProperty("TimeOfOpeningADoor", "5"));
			FS_TIME_ATTACK                                      = Integer.parseInt(FSSettings.getProperty("TimeOfAttack", "50"));
			FS_TIME_COOLDOWN                                    = Integer.parseInt(FSSettings.getProperty("TimeOfCoolDown", "5"));
			FS_TIME_ENTRY                                       = Integer.parseInt(FSSettings.getProperty("TimeOfEntry", "3"));
			FS_TIME_WARMUP                                      = Integer.parseInt(FSSettings.getProperty("TimeOfWarmUp", "2"));
			FS_PARTY_MEMBER_COUNT                               = Integer.parseInt(FSSettings.getProperty("NumberOfNecessaryPartyMembers", "4"));
			if(FS_TIME_ATTACK <= 0) FS_TIME_ATTACK = 50;
			if(FS_TIME_COOLDOWN <= 0) FS_TIME_COOLDOWN = 5;
			if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
			if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
			if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
		
        }
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+FOUR_SEPULCHERS_FILE+" File.");
		}
	}
	
	//==========================================================================================================
	public static final String  PARTY_FILE                  = PathFindingService.PARTY_FILE;
	//===========================================================================================================
	public static double  	PARTY_XP_CUTOFF_PERCENT;			// Define the cutoff point value for the "percentage" method
	public static int 		ALT_PARTY_RANGE;
	public static int 		ALT_PARTY_RANGE2;
	public static int 		MAX_PARTY_LEVEL_DIFFERENCE;			// Maximum level difference between party members in levels  
	public static int 		PARTY_XP_CUTOFF_LEVEL;				// Define the cutoff point value for the "level" method
	public static boolean   NO_PARTY_LEVEL_LIMIT;
	public static String  	PARTY_XP_CUTOFF_METHOD;				// Define Party XP cutoff point method - Possible values: level and percentage	
	
	//********************************************************************************************
	public static void loadPartyConfig()
	{
		System.out.println("Loading: " + PARTY_FILE);
		try
		{
			Properties partySettings  = new L2Properties();
			InputStream is            = new FileInputStream(new File( PARTY_FILE));
			partySettings.load(is);
			is.close();
			
			PARTY_XP_CUTOFF_METHOD      = partySettings.getProperty("PartyXpCutoffMethod", "percentage");
			PARTY_XP_CUTOFF_PERCENT     = Double.parseDouble(partySettings.getProperty("PartyXpCutoffPercent", "3."));
			PARTY_XP_CUTOFF_LEVEL       = Integer.parseInt(partySettings.getProperty("PartyXpCutoffLevel", "30"));
			ALT_PARTY_RANGE             = Integer.parseInt(partySettings.getProperty("AltPartyRange", "1600"));
			ALT_PARTY_RANGE2           = Integer.parseInt(partySettings.getProperty("AltPartyRange2", "1400"));
			MAX_PARTY_LEVEL_DIFFERENCE  = Integer.parseInt(partySettings.getProperty("PartyMaxLevelDifference", "0"));
			NO_PARTY_LEVEL_LIMIT        = Boolean.parseBoolean(partySettings.getProperty("PartLevelLimit", "true"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+ PARTY_FILE+" File.");
		}
	}
	//===============================================================================================
	public static final String DROP_FILE                  = PathFindingService.DROP_FILE;
	//================================================================================================
	public static boolean MULTIPLE_ITEM_DROP; 
	public static boolean PRECISE_DROP_CALCULATION;  
	public static boolean DEEPBLUE_DROP_RULES;
	
	//*******************************************************************************************************
	public static void loadDropsConfig()
	{
		System.out.println("Loading: " + DROP_FILE);
		try
		{
			Properties dropSettings  = new L2Properties();
			InputStream is           = new FileInputStream(new File(DROP_FILE));
			dropSettings.load(is);
			is.close();
			
			MULTIPLE_ITEM_DROP              = Boolean.valueOf(dropSettings.getProperty("MultipleItemDrop", "true"));
			DEEPBLUE_DROP_RULES             = Boolean.parseBoolean(dropSettings.getProperty("UseDeepBlueDropRules", "true"));
			PRECISE_DROP_CALCULATION        = Boolean.valueOf(dropSettings.getProperty("PreciseDropCalculation", "true"));  
			
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+DROP_FILE+" File.");
		}
	}
	
	//=============================================================================================================
	public static final String  COMMUNITY_BOARD_FILE        = PathFindingService.COMMUNITY_BOARD_FILE;
	//===================================================================================================================
	public static int      NAME_PAGE_SIZE_COMMUNITYBOARD;
	public static int      NAME_PER_ROW_COMMUNITYBOARD;
	public static boolean  SHOW_CURSED_WEAPON_OWNER;		// Show Owner(s) of Cursed Weapons in CB ?
	public static boolean  SHOW_LEVEL_COMMUNITYBOARD;
	public static boolean  SHOW_STATUS_COMMUNITYBOARD;
	public static String   COMMUNITY_TYPE;					// Community Board
	public static String   BBS_DEFAULT;    
	
	//*************************************************************
	public static void loadCbConfig()
	{
		System.out.println("Loading: " + COMMUNITY_BOARD_FILE);
		try
		{
			Properties cbSettings  = new L2Properties();
			InputStream is         = new FileInputStream(new File(COMMUNITY_BOARD_FILE));
			cbSettings.load(is);
			is.close();
			
			COMMUNITY_TYPE                          = cbSettings.getProperty("CommunityType", "old").toLowerCase();
			BBS_DEFAULT                             = cbSettings.getProperty("BBSDefault", "_bbshome");
			SHOW_LEVEL_COMMUNITYBOARD               = Boolean.valueOf(cbSettings.getProperty("ShowLevelOnCommunityBoard", "false"));
			SHOW_STATUS_COMMUNITYBOARD              = Boolean.valueOf(cbSettings.getProperty("ShowStatusOnCommunityBoard", "true"));
			NAME_PAGE_SIZE_COMMUNITYBOARD           = Integer.parseInt(cbSettings.getProperty("NamePageSizeOnCommunityBoard", "50"));
			
			//avoid a client crash.
			if (NAME_PAGE_SIZE_COMMUNITYBOARD > 25) NAME_PAGE_SIZE_COMMUNITYBOARD = 25;
			NAME_PER_ROW_COMMUNITYBOARD             = Integer.parseInt(cbSettings.getProperty("NamePerRowOnCommunityBoard", "5"));
			
			//avoid a client crash.
			if (NAME_PER_ROW_COMMUNITYBOARD > 5)    NAME_PER_ROW_COMMUNITYBOARD = 5;
			SHOW_CURSED_WEAPON_OWNER                = Boolean.valueOf(cbSettings.getProperty("ShowCursedWeaponOwner", "false"));

		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+COMMUNITY_BOARD_FILE+" File.");
		}
	}
	
	//=============================================================================================================
	public static final String  SKILLS_FILE               = PathFindingService.SKILLS_FILE;
	//==============================================================================================================
	public static int     ALT_PERFECT_SHLD_BLOCK;
	public static int     ALT_BUFF_TIME;
	public static int     ALT_DANCE_AND_SONG_TIME;
	public static int     ALT_SEED_TIME;
	public static int     ADDITIONAL_TIME_4_MINITES_BUFFS; //additional time for buffs with time 4 minutes
	public static int     ADDITIONAL_TIME_2_MINUTES_BUFFS; //additional timer for buffs with time 1 minute or less
	public static int     ADDITIONAL_TIME_HEROES_1_MINUTE_BUFFS; //additional timer for heroes skills with 1 minute of duration
	public static int     ADDITIONAL_TIME_CLAN_HALL_BUFFS; //additional timer for clan hall buffs
	public static int     ALT_CRITICAL_CAP;
	public static int     ALT_BLOW_FRONT;
    public static int     ALT_BLOW_SIDE;
	public static int     ALT_BLOW_BEHIND;
	public static int     ALT_GAME_NUMBER_OF_CUMULATED_BUFF;
	public static int	  SEND_NOTDONE_SKILLS;
	public static float   ALT_ATTACK_DELAY;
	public static float   ALT_MAGES_PHYSICAL_DAMAGE_MULTI;
	public static float   ALT_MAGES_MAGICAL_DAMAGE_MULTI;
	public static float   ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
	public static float   ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI;
	public static float   ALT_PETS_PHYSICAL_DAMAGE_MULTI;
	public static float   ALT_PETS_MAGICAL_DAMAGE_MULTI;
	public static float   ALT_NPC_PHYSICAL_DAMAGE_MULTI;
	public static float   ALT_NPC_MAGICAL_DAMAGE_MULTI;
    public static float   ALT_DAGGER_DMG_VS_HEAVY;			// Alternative damage for dagger skills VS heavy
    public static float   ALT_DAGGER_DMG_VS_ROBE;			// Alternative damage for dagger skills VS robe
	public static float   ALT_DAGGER_DMG_VS_LIGHT;			// Alternative damage for dagger skills VS light
	public static float   ALT_ARCHER_DMG_VS_HEAVY;			// Alternative damage for archer skills VS heavy
	public static float   ALT_ARCHER_DMG_VS_LIGTH;			// Alternative damage for archer skills VS ligth 
	public static float   ALT_ARCHER_DMG_VS_ROBE;			// Alternative damage for archer skills VS robe
	public static boolean ALT_GAME_MAGICFAILURES;
	public static boolean ALT_GAME_TIREDNESS;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static boolean ES_SP_BOOK_NEEDED;					// Spell Book needet to enchant skill
	public static boolean ES_XP_NEEDED;
	public static boolean ES_SP_NEEDED;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean SP_BOOK_NEEDED;
	public static boolean LIFE_CRYSTAL_NEEDED;				// Clan Item needed to learn clan skills
	public static boolean FAIL_FAKEDEATH;
	public static boolean CHECK_SKILLS_ON_ENTER;
	public static boolean STORE_SKILL_COOLTIME;
	public static boolean ALT_CHARGE_SKILL_SAVE_FOCUS;
	public static boolean EFFECT_CANCELING;
	public static boolean ALT_DANCE_MP_CONSUME;
	public static boolean GRADE_PENALTY;
	//**********************************************************************************************
	public static void loadSkillsConfig()
	{
		System.out.println("Loading: " + SKILLS_FILE);
		try
		{
			Properties skillsSettings  = new L2Properties();
			InputStream is             = new FileInputStream(new File(SKILLS_FILE));
			skillsSettings.load(is);
			is.close();
			
			SEND_NOTDONE_SKILLS                                 = Integer.parseInt(skillsSettings.getProperty("SendNOTDONESkills", "2"));
			ALT_ARCHER_DMG_VS_HEAVY                             = Float.parseFloat(skillsSettings.getProperty("ArcherVSHeavy", "2.50"));
			ALT_ARCHER_DMG_VS_LIGTH                             = Float.parseFloat(skillsSettings.getProperty("ArcherVSRobe", "1.80"));
			ALT_ARCHER_DMG_VS_ROBE                              = Float.parseFloat(skillsSettings.getProperty("ArcherVSLight", "2.00"));
			ALT_DANCE_MP_CONSUME                                = Boolean.parseBoolean(skillsSettings.getProperty("AltDanceMpConsume", "false"));
			ALT_DAGGER_DMG_VS_HEAVY                             = Float.parseFloat(skillsSettings.getProperty("DaggerVSHeavy", "2.50"));
	        ALT_DAGGER_DMG_VS_ROBE                              = Float.parseFloat(skillsSettings.getProperty("DaggerVSRobe", "1.80"));
	        ALT_DAGGER_DMG_VS_LIGHT                             = Float.parseFloat(skillsSettings.getProperty("DaggerVSLight", "2.00"));
	        LIFE_CRYSTAL_NEEDED                                 = Boolean.parseBoolean(skillsSettings.getProperty("LifeCrystalNeeded", "true"));
			ALT_BLOW_FRONT                                      = Integer.parseInt(skillsSettings.getProperty("BlowFront", "50"));
			ALT_BLOW_SIDE                                       = Integer.parseInt(skillsSettings.getProperty("BlowSide", "60"));
			ALT_BLOW_BEHIND                                     = Integer.parseInt(skillsSettings.getProperty("BlowBehind", "70"));
			ALT_BUFFER_HATE                                     = Integer.parseInt(skillsSettings.getProperty("BufferHate", "4"));
			GRADE_PENALTY                                       = Boolean.parseBoolean(skillsSettings.getProperty("GradePenalty", "true"));
			ALT_GAME_SKILL_LEARN                                = Boolean.parseBoolean(skillsSettings.getProperty("AltGameSkillLearn", "false"));
			CHAR_VIP_SKIP_SKILLS_CHECK                          = Boolean.parseBoolean(skillsSettings.getProperty("CharViPSkipSkillsCheck", "false"));
			SP_BOOK_NEEDED                                      = Boolean.parseBoolean(skillsSettings.getProperty("SpBookNeeded", "true"));
			ALT_CHARGE_SKILL_SAVE_FOCUS                         = Boolean.parseBoolean(skillsSettings.getProperty("AltChargeSkillKeepFocus", "false"));
			AUTO_LEARN_SKILLS                                   = Boolean.parseBoolean(skillsSettings.getProperty("AutoLearnSkills", "false"));
			ALT_CRITICAL_CAP                                    = Integer.parseInt(skillsSettings.getProperty("AltCriticalCap", "500"));
			FAIL_FAKEDEATH                                      = Boolean.parseBoolean(skillsSettings.getProperty("FailFakeDeath", "true"));
			CHECK_SKILLS_ON_ENTER                               = Boolean.valueOf(skillsSettings.getProperty("CheckSkillsOnEnter","false")); 
			ALT_GAME_NUMBER_OF_CUMULATED_BUFF                   = Integer.parseInt(skillsSettings.getProperty("AltNbCumulatedBuff", "24"));
			ALT_BUFF_TIME                                       = Integer.parseInt(skillsSettings.getProperty("AltBuffTime", "1"))*60000;
			ALT_DANCE_AND_SONG_TIME                             = Integer.parseInt(skillsSettings.getProperty("AltDanceAndSongTime", "1"))*60000;
			ALT_MAGES_PHYSICAL_DAMAGE_MULTI                     = Float.parseFloat(skillsSettings.getProperty("AltPDamageMages", "1.00"));
			ALT_MAGES_MAGICAL_DAMAGE_MULTI                      = Float.parseFloat(skillsSettings.getProperty("AltMDamageMages", "1.00"));
			ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI                  = Float.parseFloat(skillsSettings.getProperty("AltPDamageFighters", "1.00"));
			ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI                   = Float.parseFloat(skillsSettings.getProperty("AltMDamageFighters", "1.00"));
			ALT_PETS_PHYSICAL_DAMAGE_MULTI                      = Float.parseFloat(skillsSettings.getProperty("AltPDamagePets", "1.00"));
			ALT_PETS_MAGICAL_DAMAGE_MULTI                       = Float.parseFloat(skillsSettings.getProperty("AltMDamagePets", "1.00"));
			ALT_NPC_PHYSICAL_DAMAGE_MULTI                       = Float.parseFloat(skillsSettings.getProperty("AltPDamageNpc", "1.00"));
			ALT_NPC_MAGICAL_DAMAGE_MULTI                        = Float.parseFloat(skillsSettings.getProperty("AltMDamageNpc", "1.00"));
			ALT_ATTACK_DELAY                                    = Float.parseFloat(skillsSettings.getProperty("AltAttackDelay", "1.00"));
			ALT_GAME_CANCEL_BOW                                 = skillsSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("bow") || skillsSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("all");
			ALT_GAME_CANCEL_CAST                                = skillsSettings.getProperty("AltGameCancelByHit", "Cast".trim()).equalsIgnoreCase("cast") || skillsSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("all");
			ALT_GAME_SHIELD_BLOCKS                              = Boolean.parseBoolean(skillsSettings.getProperty("AltShieldBlocks", "false"));
			ALT_PERFECT_SHLD_BLOCK                              = Integer.parseInt(skillsSettings.getProperty("AltPerfectShieldBlockRate", "10"));
			STORE_SKILL_COOLTIME                                = Boolean.parseBoolean(skillsSettings.getProperty("StoreSkillCooltime", "true"));
			EFFECT_CANCELING                                    = Boolean.valueOf(skillsSettings.getProperty("CancelLesserEffect", "true"));
			ADDITIONAL_TIME_4_MINITES_BUFFS                     = Integer.parseInt(skillsSettings.getProperty("AdditionalTimer1", "0"))*60000; //auto convert minutes to miliseconds
			ADDITIONAL_TIME_2_MINUTES_BUFFS                     = Integer.parseInt(skillsSettings.getProperty("AdditionalTimer2", "0"))*60000; //auto convert minutes to miliseconds
			ADDITIONAL_TIME_HEROES_1_MINUTE_BUFFS               = Integer.parseInt(skillsSettings.getProperty("HeroesOneMinuteBuffTimer", "0"))*60000; //auto convert minutes to miliseconds
			ADDITIONAL_TIME_CLAN_HALL_BUFFS                     = Integer.parseInt(skillsSettings.getProperty("ClanHallBuffTimer", "0"))*60000; //auto convert minutes to miliseconds
			ALT_SEED_TIME                                       = Integer.parseInt(skillsSettings.getProperty("AltSeedTime", "0"))*60000; //auto convert minutes to miliseconds
			ES_SP_BOOK_NEEDED                                   = Boolean.parseBoolean(skillsSettings.getProperty("EnchantSkillSpBookNeeded", "true"));
			ES_XP_NEEDED                                        = Boolean.parseBoolean(skillsSettings.getProperty("EnchSkillXpNeeded", "true"));
			ES_SP_NEEDED                                        = Boolean.parseBoolean(skillsSettings.getProperty("EnchSkillSpNeeded", "true"));
		
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+SKILLS_FILE+" File.");
		}
	}
	//========================================================================================================
	public static final String  CLASSMASTER_FILE            = PathFindingService.CLASSMASTER_FILE;
	//==========================================================================================================
	public static boolean              SPAWN_CLASS_MASTER;
	public static boolean              CLASS_MASTER_STRIDER_UPDATE;
	public static String               CLASS_MASTER_SETTINGS_LINE;
	public static ClassMasterSettings  CLASS_MASTER_SETTINGS;
	
	//*************************************************************************************************
	public static void loadClassMastersConfig()
	{
		System.out.println("Loading: " + CLASSMASTER_FILE);
		try
		{
			Properties cmSettings   = new L2Properties();
			InputStream is          = new FileInputStream(new File(CLASSMASTER_FILE));
			cmSettings.load(is);
			is.close();
			
			CLASS_MASTER_STRIDER_UPDATE                 = Boolean.valueOf(cmSettings.getProperty("ClassMasterUpdateStrider", "false"));  
			SPAWN_CLASS_MASTER                          = Boolean.valueOf(cmSettings.getProperty("SpawnClassMaster", "false"));
			CLASS_MASTER_STRIDER_UPDATE                 = Boolean.valueOf(cmSettings.getProperty("ClassMasterUpdateStrider", "false")); 
			
			if (!cmSettings.getProperty("ConfigClassMaster").trim().equalsIgnoreCase("false"))
				CLASS_MASTER_SETTINGS_LINE = cmSettings.getProperty("ConfigClassMaster");
			
			CLASS_MASTER_SETTINGS     = new ClassMasterSettings(CLASS_MASTER_SETTINGS_LINE);
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameSever: Failed to Load "+CLASSMASTER_FILE+" File.");
		}
	}
	//========================================================================================================
	public static final String  MANOR_FILE            = PathFindingService.MANOR_FILE;
	//==========================================================================================================
    public static int     ALT_MANOR_REFRESH_TIME;         // Manor Refresh Starting time
    public static int     ALT_MANOR_REFRESH_MIN;          // Manor Refresh Min
    public static int     ALT_MANOR_APPROVE_TIME;         // Manor Next Period Approve Starting time 
    public static int     ALT_MANOR_APPROVE_MIN;          //Manor Next Period Approve Min 
    public static int     ALT_MANOR_MAINTENANCE_PERIOD;   // Manor Maintenance Time 
    public static int     ALT_MANOR_SAVE_PERIOD_RATE;     // Manor Save Period Rate 
    public static boolean ALLOW_MANOR;                // Allow Manor system
    public static boolean ALT_MANOR_SAVE_ALL_ACTIONS; // Manor Save All Actions 
   
    //***********************************************************************************************
    public static void loadManorConfig()
    {
    	System.out.println("Loading: " + MANOR_FILE);
    	try
    	{
    		Properties manorSettings   = new L2Properties();
    		InputStream is             = new FileInputStream(new File(MANOR_FILE));
    		manorSettings.load(is);
    		is.close();

    		ALLOW_MANOR                    = Boolean.parseBoolean(manorSettings.getProperty("AllowManor", "false"));
    		ALT_MANOR_REFRESH_TIME         = Integer.parseInt(manorSettings.getProperty("AltManorRefreshTime","20"));
    		ALT_MANOR_REFRESH_MIN          = Integer.parseInt(manorSettings.getProperty("AltManorRefreshMin","00"));
    		ALT_MANOR_APPROVE_TIME         = Integer.parseInt(manorSettings.getProperty("AltManorApproveTime","6"));
    		ALT_MANOR_APPROVE_MIN          = Integer.parseInt(manorSettings.getProperty("AltManorApproveMin","00"));
    		ALT_MANOR_MAINTENANCE_PERIOD   = Integer.parseInt(manorSettings.getProperty("AltManorMaintenancePeriod","360000"));
    		ALT_MANOR_SAVE_ALL_ACTIONS     = Boolean.parseBoolean(manorSettings.getProperty("AltManorSaveAllActions","false"));
    		ALT_MANOR_SAVE_PERIOD_RATE     = Integer.parseInt(manorSettings.getProperty("AltManorSavePeriodRate","2"));

    	}
    	catch (Exception e)
    	{
    		_log.error(e.getMessage(),e);
    		throw new Error("GameSever: Failed to Load "+MANOR_FILE+" File.");
    	}
    }
    //========================================================================================================
	public static final String  PET_MANAGER_FILE            = PathFindingService.PET_MANAGER_FILE;
	//==========================================================================================================
	public static boolean  ALLOW_RENTPET;
	public static boolean  ALLOW_WYVERN_UPGRADER;
	public static boolean  SPAWN_WYVERN_MANAGER;
	
	//***********************************************************************************************
	public static void loadPetManagerConfig()
	{
		System.out.println("Loading: " + PET_MANAGER_FILE);
		try
		{
			Properties petManagerSettings  = new L2Properties();
			InputStream is                 = new FileInputStream(new File(PET_MANAGER_FILE));
			petManagerSettings.load(is);
			is.close();

			SPAWN_WYVERN_MANAGER     = Boolean.valueOf(petManagerSettings.getProperty("SpawnWyvernManager", "true"));
			ALLOW_WYVERN_UPGRADER    = Boolean.valueOf(petManagerSettings.getProperty("AllowWyvernUpgrader", "false"));
			ALLOW_RENTPET            = Boolean.valueOf(petManagerSettings.getProperty("AllowRentPet", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameSever: Failed to Load "+PET_MANAGER_FILE+" File.");
		}
	}
	//========================================================================================================
	public static final String  PERMISSIONS_FILE            = PathFindingService.PERMISSIONS_FILE;
	//==========================================================================================================
    public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_WEAR;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_GUARDS;
	public static boolean ALLOW_FISHING;
	public static boolean ALLOW_NPC_WALKERS;
	//***********************************************************************************************
	public static void loadPermissionsConfig()
	{
		System.out.println("Loading: " + PERMISSIONS_FILE);
		try
		{
			Properties permissionsSettings  = new L2Properties();
			InputStream is                  = new FileInputStream(new File(PERMISSIONS_FILE));
			permissionsSettings.load(is);
			is.close();
			ALLOW_NPC_WALKERS                           = Boolean.valueOf(permissionsSettings.getProperty("AllowNpcWalkers", "false"));
			ALLOW_GUARDS                                = Boolean.valueOf(permissionsSettings.getProperty("AllowGuards", "false"));
			ALLOW_CURSED_WEAPONS                        = Boolean.valueOf(permissionsSettings.getProperty("AllowCursedWeapons", "false"));
			ALLOW_WEAR                                  = Boolean.valueOf(permissionsSettings.getProperty("AllowWear", "false"));
			ALLOW_LOTTERY                               = Boolean.valueOf(permissionsSettings.getProperty("AllowLottery", "false"));
			ALLOW_WATER                                 = Boolean.valueOf(permissionsSettings.getProperty("AllowWater", "true"));
			ALLOW_FISHING                               = Boolean.valueOf(permissionsSettings.getProperty("AllowFishing", "true"));
			ALLOW_BOAT                                  = Boolean.valueOf(permissionsSettings.getProperty("AllowBoat", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameSever: Failed to Load "+PERMISSIONS_FILE+" File.");
		}
	}
	
	//==========================================================================================
	public static final String  CLAN_FILE    = PathFindingService.CLAN_FILE;
	//=======================================================================================
	
	public static int      ALT_CLAN_MEMBERS_FOR_WAR;
	public static int      ALT_CLAN_JOIN_DAYS;
	public static int      ALT_CLAN_CREATE_DAYS;
	public static int      ALT_CLAN_DISSOLVE_DAYS;
	public static int      ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int      ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int      ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int      ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int      ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int      MEMBER_FOR_LEVEL_SIX;                   // Number of members to level up a clan to lvl 6
	public static int      MEMBER_FOR_LEVEL_SEVEN;                 // Number of members to level up a clan to lvl 7
	public static int      MEMBER_FOR_LEVEL_EIGHT;                 // Number of members to level up a clan to lvl 8
	public static int      MINIMUN_LEVEL_FOR_PLEDGE_CREATION;      // minimun level to create a clan.
	public static boolean  ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;// Alternative gaming - clan members with see privilege can also withdraw from clan warehouse.
	 public static boolean 			REMOVE_CASTLE_CIRCLETS;				// Remove Castle circlets after clan lose his castle? - default true
	//***********************************************************************
	public static void loadClansConfig()
	{
		System.out.println("Loading: " + CLAN_FILE);
		try
		{
			Properties ClanSettings  = new L2Properties();
			InputStream is           = new FileInputStream(new File(CLAN_FILE));
			ClanSettings.load(is);
			is.close();
			
			REMOVE_CASTLE_CIRCLETS                              = Boolean.parseBoolean(ClanSettings.getProperty("RemoveCastleCirclets", "true"));
			MINIMUN_LEVEL_FOR_PLEDGE_CREATION                   = Integer.parseInt(ClanSettings.getProperty("MinLevelToCreatePledge", "10"));
			ALT_CLAN_MEMBERS_FOR_WAR                            = Integer.parseInt(ClanSettings.getProperty("AltClanMembersForWar", "15"));
			ALT_CLAN_JOIN_DAYS                                  = Integer.parseInt(ClanSettings.getProperty("DaysBeforeJoinAClan", "1"));
			ALT_CLAN_CREATE_DAYS                                = Integer.parseInt(ClanSettings.getProperty("DaysBeforeCreateAClan", "10"));
			ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH                = Boolean.parseBoolean(ClanSettings.getProperty("AltMembersCanWithdrawFromClanWH", "false"));
			ALT_MAX_NUM_OF_CLANS_IN_ALLY                        = Integer.parseInt(ClanSettings.getProperty("AltMaxNumOfClansInAlly", "3"));
			ALT_CLAN_DISSOLVE_DAYS                              = Integer.parseInt(ClanSettings.getProperty("DaysToPassToDissolveAClan", "7"));
			ALT_ALLY_JOIN_DAYS_WHEN_LEAVED                      = Integer.parseInt(ClanSettings.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
			ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED                   = Integer.parseInt(ClanSettings.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
			ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED                 = Integer.parseInt(ClanSettings.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
			ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED                 = Integer.parseInt(ClanSettings.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "10"));
		    MEMBER_FOR_LEVEL_SIX                                = Integer.parseInt(ClanSettings.getProperty("MemberForLevel6", "30"));
			MEMBER_FOR_LEVEL_SEVEN                              = Integer.parseInt(ClanSettings.getProperty("MemberForLevel7", "80"));
			MEMBER_FOR_LEVEL_EIGHT                              = Integer.parseInt(ClanSettings.getProperty("MemberForLevel8", "120"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+CLAN_FILE+" File.");
		}
	}
	
	//==========================================================================================
	public static final String  OLYMPIAD_FILE    = PathFindingService.OLYMPIAD_FILE;
	//===========================================================================================
	public static int 				ALT_OLY_START_TIME;			// Olympiad Competition Starting time
	public static int 				ALT_OLY_MIN;				// Olympiad Minutes
	public static int 				ALT_OLY_CPERIOD;			// Olympaid Competition Period
	public static int 				ALT_OLY_BATTLE;				// Olympiad Battle Period
	public static int 				ALT_OLY_BWAIT;				// Olympiad Battle Wait
	public static int ALT_OLY_IWAIT;
	public static int ALT_OLY_WPERIOD;
	public static int ALT_OLY_VPERIOD;
	public static boolean OLYMPIAD_ALLOW_AUTO_SS;
	public static boolean OLYMPIAD_ALLOW_BSS;
	public static boolean OLYMPIAD_GIVE_ACUMEN_MAGES;
	public static boolean OLYMPIAD_GIVE_HASTE_FIGHTERS;
	public static int OLYMPIAD_ACUMEN_LVL;
	public static int OLYMPIAD_HASTE_LVL;
	
	//*************************************************************************************************
	public static void loadOlympiadConfig()
	{
		System.out.println("Loading: " + OLYMPIAD_FILE);
		try
		{
			Properties OlympiadSettings  = new L2Properties();
			InputStream is               = new FileInputStream(new File(OLYMPIAD_FILE));
			OlympiadSettings.load(is);
			is.close();
			
			ALT_OLY_START_TIME                                  = Integer.parseInt(OlympiadSettings.getProperty("AltOlyStartTime", "18"));
			ALT_OLY_MIN                                         = Integer.parseInt(OlympiadSettings.getProperty("AltOlyMin","00"));
			ALT_OLY_CPERIOD                                     = Integer.parseInt(OlympiadSettings.getProperty("AltOlyPeriod","21600000"));
			ALT_OLY_BATTLE                                      = Integer.parseInt(OlympiadSettings.getProperty("AltOlyBattle","360000"));
			ALT_OLY_BWAIT                                       = Integer.parseInt(OlympiadSettings.getProperty("AltOlyBWait","600000"));
			ALT_OLY_IWAIT                                       = Integer.parseInt(OlympiadSettings.getProperty("AltOlyPwait","300000"));
			ALT_OLY_WPERIOD                                     = Integer.parseInt(OlympiadSettings.getProperty("AltOlyWperiod","604800000"));
			ALT_OLY_VPERIOD                                     = Integer.parseInt(OlympiadSettings.getProperty("AltOlyVperiod","86400000"));
			OLYMPIAD_ALLOW_AUTO_SS                              = Boolean.parseBoolean(OlympiadSettings.getProperty("OlympiadAllowAutoSS","false"));
			OLYMPIAD_ALLOW_BSS                                  = Boolean.parseBoolean(OlympiadSettings.getProperty("OlympiadAllowBSS","false"));
			OLYMPIAD_GIVE_ACUMEN_MAGES                          = Boolean.parseBoolean(OlympiadSettings.getProperty("OlympiadGiveAcumenMages","false"));
			OLYMPIAD_GIVE_HASTE_FIGHTERS                        = Boolean.parseBoolean(OlympiadSettings.getProperty("OlympiadGiveHasteFighters","true"));
			OLYMPIAD_ACUMEN_LVL                                 = Integer.parseInt(OlympiadSettings.getProperty("OlympiadAcumenLvl", "1"));
			OLYMPIAD_HASTE_LVL                                  = Integer.parseInt(OlympiadSettings.getProperty("OlympiadHasteLvl", "2"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+OLYMPIAD_FILE+" File.");
		}
	}
	
	//==========================================================================================
	public static final String  LOTTERY_FILE    = PathFindingService.LOTTERY_FILE;
	//=======================================================================================
	public static int   ALT_LOTTERY_PRIZE;
	public static int   ALT_LOTTERY_TICKET_PRICE;
	public static int   ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	
	//*********************************************************************************************
	public static void loadLotteryConfig()
	{
		System.out.println("Loading: " + LOTTERY_FILE);
		
		try
		{
			Properties LotterySettings  = new L2Properties();
			InputStream is              = new FileInputStream(new File(LOTTERY_FILE));
			LotterySettings.load(is);
			is.close();
			
			ALT_LOTTERY_PRIZE                                   = Integer.parseInt(LotterySettings.getProperty("AltLotteryPrize","50000"));
			ALT_LOTTERY_TICKET_PRICE                            = Integer.parseInt(LotterySettings.getProperty("AltLotteryTicketPrice","2000"));
			ALT_LOTTERY_5_NUMBER_RATE                           = Float.parseFloat(LotterySettings.getProperty("AltLottery5NumberRate","0.6"));
			ALT_LOTTERY_4_NUMBER_RATE                           = Float.parseFloat(LotterySettings.getProperty("AltLottery4NumberRate","0.2"));
			ALT_LOTTERY_3_NUMBER_RATE                           = Float.parseFloat(LotterySettings.getProperty("AltLottery3NumberRate","0.2"));
			ALT_LOTTERY_2_AND_1_NUMBER_PRIZE                    = Integer.parseInt(LotterySettings.getProperty("AltLottery2and1NumberPrize","200"));
			
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+LOTTERY_FILE+" File.");
		}
	}

	//==========================================================================================
	public static final String  INVENTORY_FILE    = PathFindingService.INVENTORY_FILE;
	//=======================================================================================
	public static int     INVENTORY_MAXIMUM_NO_DWARF;
	public static int     INVENTORY_MAXIMUM_DWARF;
	public static int     INVENTORY_MAXIMUM_GM;
	public static int     MAX_ITEM_IN_PACKET;
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean 			AUTO_LOOT_ADENA;
	public static boolean DESTROY_PLAYER_INVENTORY_DROP;	// Auto destroy items dropped by players from inventory    
	public static boolean FORCE_INVENTORY_UPDATE;
	public static boolean ALLOW_AUTOHERBS_CMD;
	public static boolean ALLOW_DISCARDITEM;
	
	//**********************************************************************************************************
	public static void loadInventoryConfig()
	{
		System.out.println("Loading: " + INVENTORY_FILE);
		try
		{
			Properties inventorySettings  = new L2Properties();
			InputStream is                = new FileInputStream(new File(INVENTORY_FILE));
			inventorySettings.load(is);
			is.close();
			
			
			AUTO_LOOT_ADENA                             = inventorySettings.getProperty("AutoLootAdena").trim().equalsIgnoreCase("True");
			ALLOW_DISCARDITEM                           = Boolean.valueOf(inventorySettings.getProperty("AllowDiscardItem", "true"));
			ALLOW_AUTOHERBS_CMD                         = inventorySettings.getProperty("AllowAutoHerbsCommand").equalsIgnoreCase("true");
			DESTROY_PLAYER_INVENTORY_DROP               = Boolean.valueOf(inventorySettings.getProperty("DestroyPlayerInventoryDrop", "false"));            
			FORCE_INVENTORY_UPDATE                      = Boolean.valueOf(inventorySettings.getProperty("ForceInventoryUpdate", "false"));
			AUTO_LOOT                                   = inventorySettings.getProperty("AutoLoot").trim().equalsIgnoreCase("true");
			AUTO_LOOT_HERBS                             = inventorySettings.getProperty("AutoLootHerbs").trim().equalsIgnoreCase("true");
			INVENTORY_MAXIMUM_NO_DWARF                  = Integer.parseInt(inventorySettings.getProperty("MaxInventorySlotsForOther", "100"));
			INVENTORY_MAXIMUM_DWARF                     = Integer.parseInt(inventorySettings.getProperty("MaxInventorySlotsForDwarf", "150"));
			INVENTORY_MAXIMUM_GM                        = Integer.parseInt(inventorySettings.getProperty("MaxInventorySlotsForGameMaster", "300"));
			MAX_ITEM_IN_PACKET                          = Math.max(INVENTORY_MAXIMUM_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+GRID_FILE+" File.");
		}
	}
	
	//==========================================================================================
	public static final String  GRID_FILE    = PathFindingService.GRID_FILE;
	//=======================================================================================
	public static int     				AUTODESTROY_ITEM_AFTER;			// Time after which item will auto-destroy
	public static int     				HERB_AUTO_DESTROY_TIME;			// Auto destroy herb time
	public static int                   GRID_NEIGHBOR_TURN_ON_TIME;
	public static int                   GRID_NEIGHBOR_TURN_OFF_TIME;
	public static int                   GRID_AUTO_DESTROY_ITEM_AFTER;
	public static int                   GRID_AUTO_DESTROY_HERB_TIME;
	public static int                   SAVE_DROPPED_ITEM_INTERVAL;
	public static boolean               DESTROY_DROPPED_PLAYER_ITEM;
	public static boolean               DESTROY_EQUIPABLE_PLAYER_ITEM;
	public static boolean               SAVE_DROPPED_ITEM;
	public static boolean               EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	public static boolean               CLEAR_DROPPED_ITEM_TABLE;
	public static boolean               GRIDS_ALWAYS_ON;
	public static boolean				GEODATA;						// Load geodata files
	public static boolean				GEO_CHECK_LOS;					// Enable Line Of Sight check for skills and aggro
	public static boolean				GEO_MOVE_PC;					// Movement check for playable instances
	public static boolean				GEO_MOVE_NPC;					// Movement check for NPCs
	public static boolean				GEO_PATH_FINDING;				// Enable Path Finding [ EXPERIMENTAL]
	public static boolean				FORCE_GEODATA;					// Force loading GeoData to psychical memory
	public static boolean				ACCEPT_GEOEDITOR_CONN;			// Accept connection from geodata editor
	public static String                PROTECTED_ITEMS;
	public static FastList<Integer>     LIST_PROTECTED_ITEMS = new FastList<Integer>();
	public static enum   CorrectSpawnsZ { TOWN, MONSTER, ALL, NONE }
	public static        CorrectSpawnsZ GEO_CORRECT_Z;					// Enable spawns' z-correction
	//***************************************************************************************************
	public static void loadGridConfig()
	{
		System.out.println("Loading: " + GRID_FILE);
		try
		{
			Properties gridSettings  = new L2Properties();
			InputStream is           = new FileInputStream(new File(GRID_FILE));
			gridSettings.load(is);
			is.close();
			ACCEPT_GEOEDITOR_CONN                       = Boolean.parseBoolean(gridSettings.getProperty("AcceptGeoeditorConn", "False"));
			GRID_AUTO_DESTROY_ITEM_AFTER                = Integer.parseInt(gridSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
			AUTODESTROY_ITEM_AFTER                      = Integer.parseInt(gridSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
			HERB_AUTO_DESTROY_TIME                      = Integer.parseInt(gridSettings.getProperty("AutoDestroyHerbTime","15"))*1000;
			GRID_AUTO_DESTROY_HERB_TIME                 = Integer.parseInt(gridSettings.getProperty("AutoDestroyHerbTime","15"))*1000;
			PROTECTED_ITEMS                             = gridSettings.getProperty("ListOfProtectedItems");
			
			LIST_PROTECTED_ITEMS = new FastList<Integer>();
			for (String id : PROTECTED_ITEMS.trim().split(",")) 
			{
				LIST_PROTECTED_ITEMS.add(Integer.parseInt(id.trim()));
			}
			
			DESTROY_DROPPED_PLAYER_ITEM                 = Boolean.valueOf(gridSettings.getProperty("DestroyPlayerDroppedItem", "false"));
			DESTROY_EQUIPABLE_PLAYER_ITEM               = Boolean.valueOf(gridSettings.getProperty("DestroyEquipableItem", "false"));
			SAVE_DROPPED_ITEM                           = Boolean.valueOf(gridSettings.getProperty("SaveDroppedItem", "false"));
			EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD         = Boolean.valueOf(gridSettings.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
			SAVE_DROPPED_ITEM_INTERVAL                  = Integer.parseInt(gridSettings.getProperty("SaveDroppedItemInterval", "0"))*60000;
			CLEAR_DROPPED_ITEM_TABLE                    = Boolean.valueOf(gridSettings.getProperty("ClearDroppedItemTable", "false"));
			GRIDS_ALWAYS_ON                             = Boolean.parseBoolean(gridSettings.getProperty("GridsAlwaysOn", "false"));
			GRID_NEIGHBOR_TURN_ON_TIME                  = Integer.parseInt(gridSettings.getProperty("GridNeighborTurnOnTime", "30"));
			GRID_NEIGHBOR_TURN_OFF_TIME                 = Integer.parseInt(gridSettings.getProperty("GridNeighborTurnOffTime", "300"));
			GEODATA                                     = Boolean.parseBoolean(gridSettings.getProperty("GeoData", "false"));
			GEO_CHECK_LOS                               = Boolean.parseBoolean(gridSettings.getProperty("GeoCheckLoS", "false")) && GEODATA;
			GEO_MOVE_PC                                 = Boolean.parseBoolean(gridSettings.getProperty("GeoCheckMovePlayable", "false")) && GEODATA;
			GEO_MOVE_NPC                                = Boolean.parseBoolean(gridSettings.getProperty("GeoCheckMoveNpc", "false")) && GEODATA;
			GEO_PATH_FINDING                            = Boolean.parseBoolean(gridSettings.getProperty("GeoPathFinding", "false")) && GEODATA;
			FORCE_GEODATA                               = Boolean.parseBoolean(gridSettings.getProperty("ForceGeoData", "true")) && GEODATA;
			String correctZ                             = GEODATA ? gridSettings.getProperty("GeoCorrectZ", "ALL") : "NONE";
			GEO_CORRECT_Z                               = CorrectSpawnsZ.valueOf(correctZ.toUpperCase());
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+GRID_FILE+" File.");
		}
	}
	
	//==========================================================================================
	public static final String  WEDDINGS_FILE    = PathFindingService.WEDDINGS_FILE;
	//=======================================================================================
	public static int     WEDDING_PRICE;
	public static int     WEDDING_TELEPORT_PRICE;
	public static int     WEDDING_TELEPORT_INTERVAL;
    public static int     WEDDING_DIVORCE_COSTS;
	public static boolean ALLOW_WEDDING;
	public static boolean WEDDING_PUNISH_INFIDELITY;
	public static boolean WEDDING_TELEPORT;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	//**************************************************************************************************
	public static void loadWeddingsConfig()
	{
		System.out.println("Loading: " + WEDDINGS_FILE);
		try
		{
			Properties weddingsSettings  = new L2Properties();
			InputStream is               = new FileInputStream(new File(WEDDINGS_FILE));
			weddingsSettings.load(is);
			is.close();
			
			ALLOW_WEDDING                               = Boolean.valueOf(weddingsSettings.getProperty("AllowWedding", "false"));
			WEDDING_PRICE                               = Integer.parseInt(weddingsSettings.getProperty("WeddingPrice", "500000"));
			WEDDING_PUNISH_INFIDELITY                   = Boolean.parseBoolean(weddingsSettings.getProperty("WeddingPunishInfidelity", "true"));
			WEDDING_TELEPORT                            = Boolean.parseBoolean(weddingsSettings.getProperty("WeddingTeleport", "true"));
			WEDDING_TELEPORT_PRICE                      = Integer.parseInt(weddingsSettings.getProperty("WeddingTeleportPrice", "500000"));
			WEDDING_TELEPORT_INTERVAL                   = Integer.parseInt(weddingsSettings.getProperty("WeddingTeleportInterval", "120"));
			WEDDING_SAMESEX                             = Boolean.parseBoolean(weddingsSettings.getProperty("WeddingAllowSameSex", "true"));
			WEDDING_FORMALWEAR                          = Boolean.parseBoolean(weddingsSettings.getProperty("WeddingFormalWear", "true"));
			WEDDING_DIVORCE_COSTS                       = Integer.parseInt(weddingsSettings.getProperty("WeddingDivorceCosts", "20"));
			
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+WEDDINGS_FILE+" File.");
		}
	}
	
	//========================================================================================
	public static final String  CLAN_HALL_FILE        = PathFindingService.CLAN_HALL_FILE;
	//========================================================================================
	public static int 	CH_TELE1_FEE;
	public static int 	CH_TELE2_FEE;
	public static int 	CH_ITEM1_FEE;
	public static int 	CH_ITEM2_FEE;
	public static int 	CH_ITEM3_FEE;
	public static int 	CH_MPREG1_FEE;
	public static int 	CH_MPREG2_FEE;
	public static int 	CH_MPREG3_FEE;
	public static int 	CH_MPREG4_FEE;
	public static int 	CH_MPREG5_FEE;    
	public static int 	CH_HPREG1_FEE;
	public static int 	CH_HPREG2_FEE;
	public static int 	CH_HPREG3_FEE;
	public static int 	CH_HPREG4_FEE;
	public static int 	CH_HPREG5_FEE;
	public static int 	CH_HPREG6_FEE;
	public static int 	CH_HPREG7_FEE;
	public static int 	CH_HPREG8_FEE;
	public static int 	CH_HPREG9_FEE;
	public static int 	CH_HPREG10_FEE;
	public static int 	CH_HPREG11_FEE;
	public static int 	CH_HPREG12_FEE;
	public static int 	CH_HPREG13_FEE;
	public static int 	CH_EXPREG1_FEE;
	public static int 	CH_EXPREG2_FEE;
	public static int 	CH_EXPREG3_FEE;
	public static int 	CH_EXPREG4_FEE;
	public static int 	CH_EXPREG5_FEE;
	public static int 	CH_EXPREG6_FEE;
	public static int 	CH_EXPREG7_FEE;
	public static int 	CH_SUPPORT1_FEE;
	public static int 	CH_SUPPORT2_FEE;
	public static int 	CH_SUPPORT3_FEE;
	public static int 	CH_SUPPORT4_FEE;
	public static int 	CH_SUPPORT5_FEE;
	public static int 	CH_SUPPORT6_FEE;
	public static int 	CH_SUPPORT7_FEE;
	public static int 	CH_SUPPORT8_FEE;
	public static int 	CH_CURTAIN1_FEE;
	public static int 	CH_CURTAIN2_FEE;
	public static int 	CH_FRONT1_FEE;
	public static int 	CH_FRONT2_FEE;    
	public static long 	CH_CURTAIN_FEE_RATIO;
	public static long 	CH_SUPPORT_FEE_RATIO;
	public static long 	CH_FRONT_FEE_RATIO;
	public static long 	CH_EXPREG_FEE_RATIO;
	public static long 	CH_HPREG_FEE_RATIO;
	public static long 	CH_MPREG_FEE_RATIO;
	public static long 	CH_ITEM_FEE_RATIO;
	public static long 	CH_TELE_FEE_RATIO;
	
	//*******************************************************************************************
	public static void loadChConfig()
	{
		System.out.println("Loading: " + CLAN_HALL_FILE);
		try
		{
			Properties clanhallSettings    = new L2Properties();
			InputStream is                 = new FileInputStream(new File(CLAN_HALL_FILE));
			clanhallSettings.load(is);
			is.close();
			
			CH_TELE_FEE_RATIO                                   = Long.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeRation", "86400000"));
			CH_TELE1_FEE                                        = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl1", "86400000"));
			CH_TELE2_FEE                                        = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl2", "86400000"));
			CH_SUPPORT_FEE_RATIO                                = Long.valueOf(clanhallSettings.getProperty("ClanHallSupportFunctionFeeRation", "86400000"));
			CH_SUPPORT1_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl1", "86400000"));
			CH_SUPPORT2_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl2", "86400000"));
			CH_SUPPORT3_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl3", "86400000"));
			CH_SUPPORT4_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl4", "86400000"));
			CH_SUPPORT5_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl5", "86400000"));
			CH_SUPPORT6_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl6", "86400000"));
			CH_SUPPORT7_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl7", "86400000"));
			CH_SUPPORT8_FEE                                     = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl8", "86400000"));
			CH_MPREG_FEE_RATIO                                  = Long.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFunctionFeeRation", "86400000"));
			CH_MPREG1_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl1", "86400000"));
			CH_MPREG2_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl2", "86400000"));
			CH_MPREG3_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl3", "86400000"));
			CH_MPREG4_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl4", "86400000"));
			CH_MPREG5_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl5", "86400000"));
			CH_HPREG_FEE_RATIO                                  = Long.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFunctionFeeRation", "86400000"));
			CH_HPREG1_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl1", "86400000"));
			CH_HPREG2_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl2", "86400000"));
			CH_HPREG3_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl3", "86400000"));
			CH_HPREG4_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl4", "86400000"));
			CH_HPREG5_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl5", "86400000"));
			CH_HPREG6_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl6", "86400000"));
			CH_HPREG7_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl7", "86400000"));
			CH_HPREG8_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl8", "86400000"));
			CH_HPREG9_FEE                                       = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl9", "86400000"));
			CH_HPREG10_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl10", "86400000"));
			CH_HPREG11_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl11", "86400000"));
			CH_HPREG12_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl12", "86400000"));
			CH_HPREG13_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl13", "86400000"));
			CH_EXPREG_FEE_RATIO                                 = Long.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFunctionFeeRation", "86400000"));
			CH_EXPREG1_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl1", "86400000"));
			CH_EXPREG2_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl2", "86400000"));
			CH_EXPREG3_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl3", "86400000"));
			CH_EXPREG4_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl4", "86400000"));
			CH_EXPREG5_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl5", "86400000"));
			CH_EXPREG6_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl6", "86400000"));
			CH_EXPREG7_FEE                                      = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl7", "86400000"));
			CH_ITEM_FEE_RATIO                                   = Long.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeRation", "86400000"));
			CH_ITEM1_FEE                                        = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl1", "86400000"));
			CH_ITEM2_FEE                                        = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl2", "86400000"));
			CH_ITEM3_FEE                                        = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl3", "86400000"));
			CH_CURTAIN_FEE_RATIO								= Long.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeRation", "86400000"));
			CH_CURTAIN1_FEE										= Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl1", "86400000"));
			CH_CURTAIN2_FEE										= Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl2", "86400000"));
			CH_FRONT_FEE_RATIO									= Long.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeRation", "86400000"));
			CH_FRONT1_FEE										= Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "86400000"));
			CH_FRONT2_FEE										= Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "86400000"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+CLAN_HALL_FILE+" File.");
		}
	}
	
	//=========================================================================================
	public static final String  SEVEN_SIGNS_FILE  = PathFindingService.SEVEN_SIGNS_FILE;
	//========================================================================================
	public static int 	  ALT_FESTIVAL_ARCHER_AGGRO;		// Aggro value of Archer in SevenSigns Festival
    public static int 	  ALT_FESTIVAL_CHEST_AGGRO;		// Aggro value of Chest in SevenSigns Festival
	public static int     ALT_FESTIVAL_MONSTER_AGGRO;		// Aggro value of Monster in SevenSigns Festival
	public static int     ALT_FESTIVAL_ANCIENT_ADENA_PRICE;
	public static int     ALT_FESTIVAL_MIN_PLAYER;
	public static int     ALT_MAXIMUM_PLAYER_CONTRIB;
	public static int     ALT_FESTIVAL_MANAGER_START;
	public static int     ALT_FESTIVAL_LENGTH;
	public static int     ALT_FESTIVAL_CYCLE_LENGTH;
	public static int     ALT_FESTIVAL_FIRST_SPAWN;
	public static int     ALT_FESTIVAL_FIRST_SWARM;
	public static int     ALT_FESTIVAL_SECOND_SPAWN;
	public static int     ALT_FESTIVAL_SECOND_SWARM;
	public static int     ALT_FESTIVAL_CHEST_SPAWN;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_STRICT_SEVENSIGNS;
	public static boolean ANNOUNCE_7S_AT_START_UP;
	public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	
	//**************************************************************************************************
	public static void load7sConfig()
	{
		System.out.println("Loading: " + SEVEN_SIGNS_FILE);
		try
		{
			Properties sevenSignsSettings  = new L2Properties();
			InputStream is                 = new FileInputStream(new File(SEVEN_SIGNS_FILE));
			sevenSignsSettings.load(is);
			is.close();
			
			ALT_FESTIVAL_ARCHER_AGGRO		   = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalArcherAggro", "200"));
			ALT_FESTIVAL_CHEST_AGGRO		   = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalChestAggro", "0"));
			ALT_FESTIVAL_MONSTER_AGGRO		   = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalMonsterAggro", "200"));
			ANNOUNCE_7S_AT_START_UP            = Boolean.parseBoolean(sevenSignsSettings.getProperty("Announce7s", "true"));
			ALT_STRICT_SEVENSIGNS              = Boolean.parseBoolean(sevenSignsSettings.getProperty("StrictSevenSigns", "true"));
			ALT_GAME_REQUIRE_CASTLE_DAWN       = Boolean.parseBoolean(sevenSignsSettings.getProperty("AltRequireCastleForDawn", "false"));
			ALT_FESTIVAL_ANCIENT_ADENA_PRICE   = Integer.parseInt(sevenSignsSettings.getProperty("ValueAA", "1"));
			ALT_GAME_REQUIRE_CLAN_CASTLE       = Boolean.parseBoolean(sevenSignsSettings.getProperty("AltRequireClanCastle", "false"));
			ALT_FESTIVAL_MIN_PLAYER            = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalMinPlayer", "5"));
			ALT_MAXIMUM_PLAYER_CONTRIB         = Integer.parseInt(sevenSignsSettings.getProperty("AltMaxPlayerContrib", "1000000"));
			ALT_FESTIVAL_MANAGER_START         = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalManagerStart", "2"))*60000;// converted to miliseconds
			ALT_FESTIVAL_LENGTH                = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalLength", "18"))*60000;// converted to miliseconds
			ALT_FESTIVAL_CYCLE_LENGTH          = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalCycleLength", "38"))*60000;// converted to miliseconds
			ALT_FESTIVAL_FIRST_SPAWN           = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalFirstSpawn", "2"))*60000;// converted to miliseconds
			ALT_FESTIVAL_FIRST_SWARM           = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalFirstSwarm", "5"))*60000;// converted to miliseconds
			ALT_FESTIVAL_SECOND_SPAWN          = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalSecondSpawn", "9"))*60000;// converted to miliseconds
			ALT_FESTIVAL_SECOND_SWARM          = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalSecondSwarm", "12"))*60000; // converted to miliseconds
			ALT_FESTIVAL_CHEST_SPAWN           = Integer.parseInt(sevenSignsSettings.getProperty("AltFestivalChestSpawn", "15"))*60000; // converted to miliseconds
			ANNOUNCE_MAMMON_SPAWN              = Boolean.parseBoolean(sevenSignsSettings.getProperty("AnnounceMammonSpawn", "false"));
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+SEVEN_SIGNS_FILE+" File.");
		}
	}
	//==========================================================================================
	public static final String  PVP_CONFIG_FILE             = PathFindingService.PVP_CONFIG_FILE;
	//==============================================================================================
	public static int               KARMA_MIN_KARMA;
	public static int               KARMA_MAX_KARMA;
	public static int               KARMA_XP_DIVIDER;
	public static int               KARMA_PK_LIMIT;           //the minimum ammount of killed ppl to drop equips
	public static int               KARMA_LOST_BASE;
	public static int               ALT_PLAYER_PROTECTION_LEVEL;
	public static int               PVP_NORMAL_TIME;    // Duration (in ms) while a player stay in PVP mode after hitting an innocent      
	public static int               PVP_PVP_TIME;       // Duration (in ms) while a player stay in PVP mode after hitting a purple player  
	public static int               PVP_TIME;
	public static boolean           KARMA_AWARD_PK_KILL;
	public static boolean           ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	public static boolean           ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean           ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_USE_GK;	// Allow player with karma to use GK ?    
	public static boolean           ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	public static boolean           ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	public static boolean           KARMA_DROP_GM;
	public static String            KARMA_NON_DROPPABLE_PET_ITEMS;
	public static String            KARMA_NON_DROPPABLE_ITEMS;
	public static FastList<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS = new FastList<Integer>();
	public static FastList<Integer> KARMA_LIST_NONDROPPABLE_ITEMS     = new FastList<Integer>();
   
	//********************************************************************************************
	public static void loadPvPConfig()
	{
		System.out.println("Loading: " + PVP_CONFIG_FILE);
		try
		{
			Properties pvpSettings      = new L2Properties();
			InputStream is              = new FileInputStream(new File(PVP_CONFIG_FILE));
			pvpSettings.load(is);
			is.close();
			
			ALT_GAME_KARMA_PLAYER_CAN_USE_GK                    = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanUseGK", "false"));
			KARMA_MIN_KARMA                                     = Integer.parseInt(pvpSettings.getProperty("MinKarma", "240"));
			KARMA_MAX_KARMA                                     = Integer.parseInt(pvpSettings.getProperty("MaxKarma", "10000"));
			KARMA_XP_DIVIDER                                    = Integer.parseInt(pvpSettings.getProperty("XpDivider", "260"));
			KARMA_DROP_GM                                       = Boolean.parseBoolean(pvpSettings.getProperty("CanGMDropEquipment", "false"));
			KARMA_PK_LIMIT                                      = Integer.parseInt(pvpSettings.getProperty("MinimumPKRequiredToDrop", "5"));
			KARMA_LOST_BASE                                     = Integer.parseInt(pvpSettings.getProperty("BaseKarmaLost", "0"));
			KARMA_AWARD_PK_KILL                                 = Boolean.parseBoolean(pvpSettings.getProperty("AwardPKKillPVPPoint", "true"));
			PVP_NORMAL_TIME                                     = Integer.parseInt(pvpSettings.getProperty("PvPVsNormalTime", "15000"));  
			PVP_PVP_TIME                                        = Integer.parseInt(pvpSettings.getProperty("PvPVsPvPTime", "30000"));  
			PVP_TIME = PVP_NORMAL_TIME;  
			ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE    = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_SHOP                      = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanShop", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_TELEPORT                  = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanTeleport", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_TRADE                     = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanTrade", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE             = Boolean.valueOf(pvpSettings.getProperty("AltKarmaPlayerCanUseWareHouse", "true"));
			ALT_PLAYER_PROTECTION_LEVEL                         = Integer.parseInt(pvpSettings.getProperty("AltPlayerProtectionLevel","0"));
			KARMA_NON_DROPPABLE_PET_ITEMS                       = pvpSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
			KARMA_NON_DROPPABLE_ITEMS                           = pvpSettings.getProperty("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369");
			
			KARMA_LIST_NONDROPPABLE_PET_ITEMS = new FastList<Integer>();
			for (String id : KARMA_NON_DROPPABLE_PET_ITEMS.trim().split(","))
			{
				KARMA_LIST_NONDROPPABLE_PET_ITEMS.add(Integer.parseInt(id.trim()));
			}
			
			KARMA_LIST_NONDROPPABLE_ITEMS = new FastList<Integer>();
			for (String id : KARMA_NON_DROPPABLE_ITEMS.trim().split(","))
			{
				KARMA_LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id.trim()));
			}
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("Failed to Load "+PVP_CONFIG_FILE+" File.");
			
		}
	}
	
	//=========================================================================================================
	public static final String  GM_ACCESS_FILE  = PathFindingService.GM_ACCESS_FILE;
	//=======================================================================================================
	public static int     	ADMIN_ACCESSLEVEL;      // General Admin AccessLevel
	public static int     	GM_ACCESSLEVEL;         // General GM Max AccessLevel
	public static int     	GM_MIN;					// General GM Minimal AccessLevel
	public static int       GM_RELOAD;
	public static int     	GM_ALTG_MIN_LEVEL;		// Minimum privileges level for a GM to do Alt+G
	public static int     	GM_ANNOUNCE;			// General GM AccessLevel to change announcements
	public static int     	GM_BAN;					// General GM AccessLevel can /ban /unban
	public static int     	GM_BAN_CHAT;			// General GM AccessLevel can /ban /unban for chat
	public static int     	GM_CREATE_ITEM;			// General GM AccessLevel can /create_item and /gmshop
	public static int     	GM_ENCHANT;				// General GM AccessLevel can enchant armor
	public static int     	GM_DELETE;				// General GM AccessLevel can /delete
	public static int     	GM_KICK;				// General GM AccessLevel can /kick /disconnect
	public static int     	GM_MENU;				// General GM AccessLevel for access to GMMenu
	public static int     	GM_GODMODE;				// General GM AccessLevel to use god mode command
	public static int     	GM_CHAR_EDIT;			// General GM AccessLevel with character edit rights
	public static int     	GM_CHAR_EDIT_OTHER;		// General GM AccessLevel with edit rights for other characters
	public static int     	GM_CHAR_VIEW;			// General GM AccessLevel with character view rights
    public static int     	GM_CHAR_VIEW_INFO;			// General GM AccessLevel with character view rights ALT+G
    public static int     	GM_CHAR_INVENTORY;			// General GM AccessLevel with character view inventory rights ALT+G
    public static int     	GM_CHAR_CLAN_VIEW;			// General GM AccessLevel with character view clan info rights ALT+G
    public static int     	GM_CHAR_VIEW_QUEST;			// General GM AccessLevel with character view quest rights ALT+G
    public static int     	GM_CHAR_VIEW_SKILL;			// General GM AccessLevel with character view skill rights ALT+G
    public static int     	GM_CHAR_VIEW_WAREHOUSE;			// General GM AccessLevel with character warehouse view rights ALT+G    
	public static int     	GM_NPC_EDIT;			// General GM AccessLevel with NPC edit rights
	public static int     	GM_NPC_VIEW;
	public static int     	GM_TELEPORT;			// General GM AccessLevel to teleport to any location
	public static int     	GM_TELEPORT_OTHER;		// General GM AccessLevel to teleport character to any location
	public static int     	GM_RESTART;				// General GM AccessLevel to restart server
	public static int     	GM_MONSTERRACE;			// General GM AccessLevel for MonsterRace
	public static int     	GM_RIDER;				// General GM AccessLevel to ride Wyvern
	public static int     	GM_ESCAPE;				// General GM AccessLevel to unstuck without 5min delay
	public static int     	GM_FIXED;				// General GM AccessLevel to resurect fixed after death
	public static int     	GM_CREATE_NODES;		// General GM AccessLevel to create Path Nodes
	public static int     	GM_DOOR;				// General GM AccessLevel to close/open Doors
	public static int     	GM_RES;					// General GM AccessLevel with Resurrection rights
	public static int     	GM_PEACEATTACK;			// General GM AccessLevel to attack in the peace zone   
	public static int     	GM_HEAL;				// General GM AccessLevel to heal
	public static int     	GM_IRC;					// General GM AccessLevel to IRC commands
	public static int     	GM_UNBLOCK;				// General GM AccessLevel to unblock IPs detected as hack IPs
	public static int 		GM_CACHE;				// General GM AccessLevel to use Cache commands				
	public static int 		GM_TALK_BLOCK;			// General GM AccessLevel to use test&st commands
	public static int 		GM_TEST;
	public static int 		STANDARD_RESPAWN_DELAY;	// Standard Respawn Delay
	public static int 		GM_TRANSACTION_MIN;
	public static int 		GM_TRANSACTION_MAX;
	public static int     	GM_CAN_GIVE_DAMAGE;		// Minimum level to allow a GM giving damage
	public static int     	GM_DONT_TAKE_EXPSP;		// Minimum level to don't give Exp/Sp in party
	public static int     	GM_DONT_TAKE_AGGRO;		// Minimum level to don't take aggro    
	public static int       GM_FUN_ENGINE;
	public static int       GM_FREE_SHOP;           // General GM AccessLevel can shop for free
	public static boolean 	GM_DISABLE_TRANSACTION;	// Disable transaction on AccessLevel
	public static boolean 	SHOW_GM_LOGIN;			// GM Announce at login
	public static boolean 	ALT_PRIVILEGES_ADMIN;
	public static boolean 	EVERYBODY_HAS_ADMIN_RIGHTS;		// For test servers - everybody has admin rights
	public static boolean 	HIDE_GM_STATUS;
	public static boolean 	GM_STARTUP_INVISIBLE;
	public static boolean 	GM_STARTUP_SILENCE;
	public static boolean 	GM_STARTUP_AUTO_LIST;
	public static boolean 	GM_HERO_AURA;			// Place an aura around the GM ?
	public static boolean 	GM_STARTUP_INVULNERABLE;	// Set the GM invulnerable at startup ?
	public static boolean   GM_AUDIT;
	public static boolean   ONLY_GM_ITEMS_FREE;
	public static boolean   ONLY_GM_TELEPORT_FREE;
	public static boolean   ONLY_GM_BUFFS_FREE;
	public static String  	GM_ADMIN_MENU_STYLE;
	
	//***********************************************************************************************************
	public static void loadGmAcessConfig()
	{
		System.out.println("Loading: "+ GM_ACCESS_FILE);
		try
		{
			Properties gmSettings   = new L2Properties();
			InputStream is          = new FileInputStream(new File(GM_ACCESS_FILE));
			gmSettings.load(is);
			is.close();
			 
			GM_FREE_SHOP                    = Integer.parseInt(gmSettings.getProperty("GMCanBuyFree", "100"));
			ONLY_GM_BUFFS_FREE              = Boolean.valueOf(gmSettings.getProperty("OnlyGMBuffsFree", "true"));
			ONLY_GM_TELEPORT_FREE           = Boolean.valueOf(gmSettings.getProperty("OnlyGMTeleportFree", "true"));
			GM_FUN_ENGINE                   = Integer.parseInt(gmSettings.getProperty("GMFunEngine", "75")); 
			GM_ADMIN_MENU_STYLE             = gmSettings.getProperty("GMAdminMenuStyle", "modern");
			GM_RELOAD                       = Integer.parseInt(gmSettings.getProperty("GMReload", "100"));
			GM_ALTG_MIN_LEVEL               = Integer.parseInt(gmSettings.getProperty("GMCanAltG", "100"));
			ALT_PRIVILEGES_ADMIN            = Boolean.parseBoolean(gmSettings.getProperty("AltPrivilegesAdmin", "false"));
			ONLY_GM_ITEMS_FREE              = Boolean.valueOf(gmSettings.getProperty("OnlyGMItemsFree", "true"));
			EVERYBODY_HAS_ADMIN_RIGHTS      = Boolean.parseBoolean(gmSettings.getProperty("EverybodyHasAdminRights", "false"));
			GM_ACCESSLEVEL                  = Integer.parseInt(gmSettings.getProperty("GMAccessLevel", "100"));
			GM_MIN                          = Integer.parseInt(gmSettings.getProperty("GMMinLevel", "75"));
			ADMIN_ACCESSLEVEL               = Integer.parseInt(gmSettings.getProperty("AdminAcessLevel", "200"));
			GM_ANNOUNCE                     = Integer.parseInt(gmSettings.getProperty("GMCanAnnounce", "100"));
			GM_BAN                          = Integer.parseInt(gmSettings.getProperty("GMCanBan", "100"));
			GM_BAN_CHAT                     = Integer.parseInt(gmSettings.getProperty("GMCanBanChat", "100"));
			GM_CREATE_ITEM                  = Integer.parseInt(gmSettings.getProperty("GMCanShop", "100"));
			GM_DELETE                       = Integer.parseInt(gmSettings.getProperty("GMCanDelete", "100"));
			GM_KICK                         = Integer.parseInt(gmSettings.getProperty("GMCanKick", "100"));
			GM_MENU                         = Integer.parseInt(gmSettings.getProperty("GMMenu", "100"));
			GM_GODMODE                      = Integer.parseInt(gmSettings.getProperty("GMGodMode", "100"));
			GM_CHAR_EDIT                    = Integer.parseInt(gmSettings.getProperty("GMCanEditChar", "100"));
			GM_CHAR_EDIT_OTHER              = Integer.parseInt(gmSettings.getProperty("GMCanEditCharOther", "100"));
			GM_CHAR_VIEW                    = Integer.parseInt(gmSettings.getProperty("GMCanViewChar", "100"));
            GM_CHAR_VIEW_INFO               = Integer.parseInt(gmSettings.getProperty("GMViewCharacterInfo", "100"));
            GM_CHAR_INVENTORY               = Integer.parseInt(gmSettings.getProperty("GMViewItemList", "100"));
            GM_CHAR_CLAN_VIEW               = Integer.parseInt(gmSettings.getProperty("GMViewClanInfo", "100"));
            GM_CHAR_VIEW_QUEST              = Integer.parseInt(gmSettings.getProperty("GMViewQuestList", "100"));
            GM_CHAR_VIEW_SKILL              = Integer.parseInt(gmSettings.getProperty("GMViewSkillInfo", "100"));
            GM_CHAR_VIEW_WAREHOUSE          = Integer.parseInt(gmSettings.getProperty("GMViewWarehouseWithdrawList", "100"));
			GM_NPC_EDIT                     = Integer.parseInt(gmSettings.getProperty("GMCanEditNPC", "100"));
			GM_NPC_VIEW                     = Integer.parseInt(gmSettings.getProperty("GMCanViewNPC", "100"));
			GM_TELEPORT                     = Integer.parseInt(gmSettings.getProperty("GMCanTeleport", "100"));
			GM_TELEPORT_OTHER               = Integer.parseInt(gmSettings.getProperty("GMCanTeleportOther", "100"));
			GM_RESTART                      = Integer.parseInt(gmSettings.getProperty("GMCanRestart", "100"));
			GM_MONSTERRACE                  = Integer.parseInt(gmSettings.getProperty("GMMonsterRace", "100"));
			GM_RIDER                        = Integer.parseInt(gmSettings.getProperty("GMRider", "100"));
			GM_ESCAPE                       = Integer.parseInt(gmSettings.getProperty("GMFastUnstuck", "100"));
			GM_FIXED                        = Integer.parseInt(gmSettings.getProperty("GMResurectFixed", "100"));
			GM_CREATE_NODES                 = Integer.parseInt(gmSettings.getProperty("GMCreateNodes", "100"));
			GM_DOOR                         = Integer.parseInt(gmSettings.getProperty("GMDoor", "100"));
			GM_RES                          = Integer.parseInt(gmSettings.getProperty("GMRes", "100"));
			GM_PEACEATTACK                  = Integer.parseInt(gmSettings.getProperty("GMPeaceAttack", "100"));
			GM_HEAL                         = Integer.parseInt(gmSettings.getProperty("GMHeal", "100"));
			GM_IRC         	                = Integer.parseInt(gmSettings.getProperty("GMIRC", "100"));
			GM_ENCHANT                      = Integer.parseInt(gmSettings.getProperty("GMEnchant", "100"));
			GM_UNBLOCK                      = Integer.parseInt(gmSettings.getProperty("GMUnblock", "100"));
			GM_CACHE                        = Integer.parseInt(gmSettings.getProperty("GMCache", "100"));
			GM_TALK_BLOCK                   = Integer.parseInt(gmSettings.getProperty("GMTalkBlock", "100"));
			GM_TEST                         = Integer.parseInt(gmSettings.getProperty("GMTest", "100"));
			GM_STARTUP_AUTO_LIST            = Boolean.parseBoolean(gmSettings.getProperty("GMStartupAutoList", "true"));
			GM_HERO_AURA                    = Boolean.parseBoolean(gmSettings.getProperty("GMHeroAura", "true"));
			GM_STARTUP_INVULNERABLE         = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvulnerable", "true"));
			GM_AUDIT                        = Boolean.valueOf(gmSettings.getProperty("GMAudit", "false"));
			STANDARD_RESPAWN_DELAY          = Integer.parseInt(gmSettings.getProperty("StandardRespawnDelay", "60"));
			GM_AUDIT                        = Boolean.valueOf(gmSettings.getProperty("GMAudit", "false"));
			
			String gmTrans                  = gmSettings.getProperty("GMDisableTransaction", "false");
			if (!gmTrans.trim().equalsIgnoreCase("false"))
			{
				String[] params = gmTrans.trim().split(",");
				GM_DISABLE_TRANSACTION = true;
				GM_TRANSACTION_MIN = Integer.parseInt(params[0].trim());
				GM_TRANSACTION_MAX = Integer.parseInt(params[1].trim());
			}
			else
			{
				GM_DISABLE_TRANSACTION = false;
			}
			GM_CAN_GIVE_DAMAGE     = Integer.parseInt(gmSettings.getProperty("GMCanGiveDamage", "90"));
			GM_DONT_TAKE_AGGRO     = Integer.parseInt(gmSettings.getProperty("GMDontTakeAggro", "90"));
			GM_DONT_TAKE_EXPSP     = Integer.parseInt(gmSettings.getProperty("GMDontGiveExpSp", "90"));
			SHOW_GM_LOGIN          = Boolean.parseBoolean(gmSettings.getProperty("ShowGMLogin", "false"));
			HIDE_GM_STATUS         = Boolean.parseBoolean(gmSettings.getProperty("HideGMStatus", "false"));
			GM_STARTUP_INVISIBLE   = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvisible", "true"));
			GM_STARTUP_SILENCE     = Boolean.parseBoolean(gmSettings.getProperty("GMStartupSilence", "true"));
			
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+GM_ACCESS_FILE+" File.");
		}
	}
	
	//===============================================================================================
	public static final String  COMMAND_PRIVILEGES_FILE = PathFindingService.COMMAND_PRIVILEGES_FILE;
	public static final Map<String, Integer> GM_COMMAND_PRIVILEGES = new FastMap<String, Integer>();
	
	//*****************************************************************************
	public static void loadPrivilegesConfig()
	{
		System.out.println("Loading: "+ COMMAND_PRIVILEGES_FILE);
		try
		{
			Properties CommandPrivileges  = new L2Properties();
			InputStream is                = new FileInputStream(new File(COMMAND_PRIVILEGES_FILE));  
			CommandPrivileges.load(is);
			is.close();
			
			for(Map.Entry<Object, Object> _command : CommandPrivileges.entrySet())
			{
				String command = String.valueOf(_command.getKey());
				String commandLevel = String.valueOf(_command.getValue()).trim();
				
				int accessLevel = GM_ACCESSLEVEL;
				
				try
				{
					accessLevel = Integer.parseInt(commandLevel);
				} 
				catch (Exception e)
				{
					_log.warn("Failed to parse command \""+command+"\"!",e );
				}
				
				GM_COMMAND_PRIVILEGES.put(command, accessLevel);
			}
		}
		catch (Exception e)
		{
			_log.error(e);
			throw new Error("GameServer: Failed to Load "+COMMAND_PRIVILEGES_FILE+" File.");
		}
	}
	
    //==============================================================================
	public static final String  CACHE_FILE     = PathFindingService.CACHE_FILE;
	//==============================================================================
	public static int                   CACHE_TIMETOLIVESECONDS;
	public static int                   CACHE_TIMETOIDLESECONDS;
	public static int                   CACHE_MAX_ELEM_IN_MEMORY;
	public static int                   WAREHOUSE_CACHE_TIME;  
	public static enum                  CacheType 
	{
		ehcache,
		mapcache,
		none
	};
	public static CacheType             TYPE_CACHE;
    public static boolean               WAREHOUSE_CACHE;
    
	//*******************************************************************
	public static void loadCacheConfig()
	{
		System.out.println("Loading: "+ CACHE_FILE);
		try
		{
			Properties CacheSettings  = new L2Properties();
			InputStream is            = new FileInputStream(new File(CACHE_FILE));  
			CacheSettings.load(is);
			is.close();
			
			WAREHOUSE_CACHE                             = Boolean.valueOf(CacheSettings.getProperty("WarehouseCache", "false"));
			WAREHOUSE_CACHE_TIME                        = Integer.parseInt(CacheSettings.getProperty("WarehouseCacheTime", "15"));
			TYPE_CACHE                                  = CacheType.valueOf(CacheSettings.getProperty("CacheType", "ehcache").toLowerCase());
			CACHE_MAX_ELEM_IN_MEMORY                    = Integer.parseInt(CacheSettings.getProperty("MaxElemInMemory", "9700"));
			CACHE_TIMETOLIVESECONDS                     = Integer.parseInt(CacheSettings.getProperty("TimeToLiveSeconds", "7200"));
			CACHE_TIMETOIDLESECONDS                     = Integer.parseInt(CacheSettings.getProperty("TimeToIdleSeconds", "3600"));
			
		}
		catch (Exception e)
		{
			_log.error(e);
			throw new Error("GameServer: Failed to Load "+CACHE_FILE+" File.");
		}
	}
    //==============================================================================
	public static final String  NETWORK_FILE      = PathFindingService.NETWORK_FILE;
	// ==============================================================================
	public static int           CACHE_CONNECTOR_PORT;   // Game Server to Cache port */
	public static int           CACHE_PORT;             // Cache port */
	public static int           NETWORK_TRAFFIC_OPTIMIZATION_MS; 
	public static int           GAME_SERVER_LOGIN_PORT;
	public static int           PORT_GAME;
	public static String        CACHE_LISTENER_HOST;    // Cache Listener Host */
    public static String        CACHE_CONNECTOR_HOST;   // Game Server Cache Host */
    public static String        GAME_SERVER_LOGIN_HOST;
	public static String        INTERNAL_HOSTNAME;
	public static String        INTERNAL_NETWORKS;
	public static String        EXTERNAL_HOSTNAME;
	public static String        OPTIONAL_NETWORKS;
	public static String 		GAMESERVER_HOSTNAME;			// Hostname of the Game Server
	public static boolean       NETWORK_TRAFFIC_OPTIMIZATION;  
    public static File          CACHE_DATAPACK_ROOT;    // Cache Datapack root directory */
	public static enum KickType
	{
		closeClient,
		relogin
	}				// how character will be kicked from server
	public static KickType      KICK_TYPE;
	
	//******************************************************************
	public static void loadNetworkConfig()
	{
		System.out.println("Loading: "+ NETWORK_FILE);
		try
		{
			Properties NetworkSettings  = new L2Properties();
			InputStream is              = new FileInputStream(new File(NETWORK_FILE));  
			NetworkSettings.load(is);
			is.close();
			
            CACHE_PORT 		                          = Integer.parseInt(NetworkSettings.getProperty("CacheServerPort","9024"));
			CACHE_LISTENER_HOST 	                  = NetworkSettings.getProperty("CacheServerListenerHost","127.0.0.1");
	        CACHE_DATAPACK_ROOT	                      = new File(NetworkSettings.getProperty("CacheDatapackRoot", ".")).getCanonicalFile();
            CACHE_CONNECTOR_PORT		              = Integer.parseInt(NetworkSettings.getProperty("CacheConnectPort", "9024"));
            CACHE_CONNECTOR_HOST		              = NetworkSettings.getProperty("CacheConnectHost", "127.0.0.1");
			KICK_TYPE                 	              = KickType.valueOf(NetworkSettings.getProperty("KickType", "relogin").toLowerCase());//rayan: avoid problem with case
			FLOOD_PROTECTION                          = Boolean.valueOf(NetworkSettings.getProperty("FloodProtection", "false"));
			PACKET_LIMIT                              = Integer.parseInt(NetworkSettings.getProperty("PacketLimit", "500"));
			PACKET_TIME_LIMIT                         = Integer.parseInt(NetworkSettings.getProperty("PacketTimeLimit", "1100"));
			PACKET_LIFETIME                           = Integer.parseInt(NetworkSettings.getProperty("PacketLifeTime", "0"));
			PACKET_EXECUTIONTIME                      = Long.parseLong(NetworkSettings.getProperty("PacketExecutionTime", "0"));
			EXTERNAL_HOSTNAME                         = NetworkSettings.getProperty("ExternalHostname", "*");
			INTERNAL_NETWORKS                         = NetworkSettings.getProperty("InternalNetworks", "");
			INTERNAL_HOSTNAME                         = NetworkSettings.getProperty("InternalHostname", "*");
			OPTIONAL_NETWORKS                         = NetworkSettings.getProperty("OptionalNetworks", "");
			GAME_SERVER_LOGIN_PORT                    = Integer.parseInt(NetworkSettings.getProperty("LoginPort","9014"));
			GAME_SERVER_LOGIN_HOST                    = NetworkSettings.getProperty("LoginHost","127.0.0.1");
			NETWORK_TRAFFIC_OPTIMIZATION              = Boolean.valueOf(NetworkSettings.getProperty("NetworkTrafficOptimization", "false"));  
			NETWORK_TRAFFIC_OPTIMIZATION_MS           = Integer.parseInt(NetworkSettings.getProperty("NetworkTrafficOptimizationMs", "1100"));  
			GAMESERVER_HOSTNAME                       = NetworkSettings.getProperty("GameserverHostname");
			PORT_GAME                                 = Integer.parseInt(NetworkSettings.getProperty("GameserverPort", "7777"));
		}
		catch (Exception e)
		{
			_log.error(e);
			throw new Error("GameServer: Failed to Load "+NETWORK_FILE+" File.");
		}
	}
    //==============================================================================
	public static final String  SECURITY_FILE    = PathFindingService.SECURITY_FILE;
    //==============================================================================
	public static int           DEFAULT_PUNISH;
	public static int           DEFAULT_PUNISH_PARAM;
	public static int           PACKET_LIMIT;
	public static int           PACKET_TIME_LIMIT;
	public static int           SAFE_REBOOT_TIME = 10; 
	public static int           MAX_UNKNOWN_PACKETS;
	public static int           UNKNOWN_PACKETS_PUNISHMENT;
	public static boolean       GAMEGUARD_ENFORCE;
	public static boolean       GAMEGUARD_PROHIBITACTION;
	public static boolean       FLOOD_PROTECTION;
	public static boolean       ENABLE_PACKET_PROTECTION;
	public static boolean       BLOCK_BAD_HTML_LINK;
	public static boolean       BYPASS_VALIDATION;
	public static boolean       SAFE_REBOOT;      // Safe mode will disable some feature during restart/shutdown to prevent exploit
    public static boolean       SAFE_REBOOT_DISABLE_ENCHANT; 
	public static boolean       SAFE_REBOOT_DISABLE_TELEPORT; 
	public static boolean       SAFE_REBOOT_DISABLE_CREATEITEM; 
	public static boolean       SAFE_REBOOT_DISABLE_TRANSACTION; 
	public static boolean       SAFE_REBOOT_DISABLE_PC_ITERACTION; 
	public static boolean       SAFE_REBOOT_DISABLE_NPC_ITERACTION; 
	public static int			FLOODPROTECTOR_INITIALSIZE;		// FloodProtector initial capacity	
   //*******************************************************
	public static void loadSecurityConfig()
	{
		System.out.println("Loading: "+SECURITY_FILE);
		try
		{
			Properties securitySettings  = new L2Properties();
			InputStream is               = new FileInputStream(new File(SECURITY_FILE));  
			securitySettings.load(is);
			is.close();
			
			FLOODPROTECTOR_INITIALSIZE		          = Integer.parseInt(securitySettings.getProperty("FloodProtectorInitialSize", "50"));
			DEFAULT_PUNISH                            = Integer.parseInt(securitySettings.getProperty("DefaultPunish", "2"));
			DEFAULT_PUNISH_PARAM                      = Integer.parseInt(securitySettings.getProperty("DefaultPunishParam", "0"));
			GAMEGUARD_ENFORCE                         = Boolean.valueOf(securitySettings.getProperty("GameGuardEnforce", "false"));
			GAMEGUARD_PROHIBITACTION                  = Boolean.valueOf(securitySettings.getProperty("GameGuardProhibitAction", "false"));
			BLOCK_BAD_HTML_LINK                       = Boolean.parseBoolean(securitySettings.getProperty("BlockBadHtmLink","true"));
			BYPASS_VALIDATION                         = Boolean.valueOf(securitySettings.getProperty("BypassValidation", "true"));
			SAFE_REBOOT                               = Boolean.parseBoolean(securitySettings.getProperty("SafeReboot", "true"));
			SAFE_REBOOT_TIME                          = Integer.parseInt(securitySettings.getProperty("SafeRebootTime", "10")); 
			SAFE_REBOOT_DISABLE_ENCHANT               = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisableEnchant", "false")); 
			SAFE_REBOOT_DISABLE_TELEPORT              = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisableTeleport", "false")); 
			SAFE_REBOOT_DISABLE_CREATEITEM            = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisableCreateItem", "false")); 
			SAFE_REBOOT_DISABLE_TRANSACTION           = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisableTransaction", "false")); 
			SAFE_REBOOT_DISABLE_PC_ITERACTION         = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisablePcIteraction", "false")); 
			SAFE_REBOOT_DISABLE_NPC_ITERACTION        = Boolean.parseBoolean(securitySettings.getProperty("SafeRebootDisableNpcIteraction", "false")); 
			ENABLE_PACKET_PROTECTION                  = Boolean.parseBoolean(securitySettings.getProperty("PacketProtection", "false"));
			MAX_UNKNOWN_PACKETS                       = Integer.parseInt(securitySettings.getProperty("UnknownPacketsBeforeBan", "5"));
			UNKNOWN_PACKETS_PUNISHMENT                = Integer.parseInt(securitySettings.getProperty("UnknownPacketsPunishment", "2"));
		}
		catch (Exception e)
		{
			_log.error(e);
			throw new Error("GameServer: Failed to Load "+SECURITY_FILE+" File.");
		}
	}
   //===========================================================================================
    public static final String  IRC_FILE    = PathFindingService.IRC_FILE;
   //===========================================================================================
    public static int 		IRC_PORT;
    public static String	IRC_SERVER;
    public static String	IRC_PASS;    
    public static String	IRC_NICK;
    public static String	IRC_USER;
    public static String	IRC_NAME;
    public static String	IRC_LOGIN_COMMAND;
    public static String	IRC_CHANNEL;
    public static String	IRC_FROM_GAME_TYPE;
    public static String	IRC_TO_GAME_TYPE;
    public static String	IRC_TO_GAME_SPECIAL_CHAR;
    public static String	IRC_TO_GAME_DISPLAY;
    public static String	IRC_NICKSERV_NAME;
    public static String	IRC_NICKSERV_COMMAND;
    public static boolean	IRC_ENABLED;
    public static boolean	IRC_LOG_CHAT;
    public static boolean 	IRC_SSL;
    public static boolean	IRC_ANNOUNCE;    
    public static boolean 	IRC_NICKSERV;
    
    //*******************************************************************************************
    public static void loadIrcConfig()
    {
    	System.out.println("Loading: "+IRC_FILE);
        try
        {
            Properties ircSettings       = new L2Properties();
            InputStream is               = new FileInputStream(new File(IRC_FILE));
            ircSettings.load(is);
            is.close();

            IRC_ENABLED                           				= Boolean.parseBoolean(ircSettings.getProperty("Enable", "false"));                
            IRC_LOG_CHAT                          				= Boolean.parseBoolean(ircSettings.getProperty("LogChat", "false"));
            IRC_SSL                                    			= Boolean.parseBoolean(ircSettings.getProperty("SSL", "false"));
            IRC_SERVER											= ircSettings.getProperty("Server", "localhost");
            IRC_PORT                                       		= Integer.parseInt(ircSettings.getProperty("Port", "6667"));
            IRC_PASS											= ircSettings.getProperty("Pass", "localhost");
            IRC_NICK											= ircSettings.getProperty("Nick", "l2jfbot");
            IRC_USER											= ircSettings.getProperty("User", "l2emu");
            IRC_NAME											= ircSettings.getProperty("Name", "l2emu");
            IRC_LOGIN_COMMAND									= ircSettings.getProperty("LoginCommand", "");
            IRC_CHANNEL											= ircSettings.getProperty("Channel", "#mychan");
            IRC_ANNOUNCE                           				= Boolean.parseBoolean(ircSettings.getProperty("IrcAnnounces", "false"));
            IRC_FROM_GAME_TYPE									= ircSettings.getProperty("GameToIrcType", "off");            
            IRC_TO_GAME_TYPE									= ircSettings.getProperty("IrcToGameType", "off");
            IRC_TO_GAME_SPECIAL_CHAR							= ircSettings.getProperty("IrcToGameSpecialChar", "#");
            IRC_TO_GAME_DISPLAY									= ircSettings.getProperty("IrcToGameDisplay", "tade");
            IRC_NICKSERV                               			= Boolean.parseBoolean(ircSettings.getProperty("NickServ", "false"));
            IRC_NICKSERV_NAME									= ircSettings.getProperty("NickservName", "nickserv");
            IRC_NICKSERV_COMMAND								= ircSettings.getProperty("NickservCommand", "");

        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("GameServer: Failed to Load "+IRC_FILE+" File.");
        }
    }
    
    //======================================================================================
    public static final String  SAILREN_FILE         = PathFindingService.SAILREN_FILE;
    //======================================================================================
    public static int     FWS_INTERVALOFSAILRENSPAWN;
    public static int     FWS_INTERVALOFNEXTMONSTER;
    public static int     FWS_ACTIVITYTIMEOFMOBS;    
    public static boolean FWS_ENABLESINGLEPLAYER;
   
    //*******************************************************************************************
    public static void loadSailrenConfig()
    {
    	System.out.println("Loading: "+SAILREN_FILE);
        try
        {
            Properties sailrenSettings   = new L2Properties();
            InputStream is               = new FileInputStream(new File(SAILREN_FILE));
            sailrenSettings.load(is);
            is.close();

            FWS_ENABLESINGLEPLAYER           = Boolean.parseBoolean(sailrenSettings.getProperty("EnableSinglePlayer", "false"));
            
            FWS_INTERVALOFSAILRENSPAWN       = Integer.parseInt(sailrenSettings.getProperty("IntervalOfSailrenSpawn", "5"));
            if(FWS_INTERVALOFSAILRENSPAWN <= 0) FWS_INTERVALOFSAILRENSPAWN = 1440;
            FWS_INTERVALOFSAILRENSPAWN = FWS_INTERVALOFSAILRENSPAWN * 60000;
            
            FWS_INTERVALOFNEXTMONSTER        = Integer.parseInt(sailrenSettings.getProperty("IntervalOfNextMonster", "1"));
            if(FWS_INTERVALOFNEXTMONSTER <= 0) FWS_INTERVALOFNEXTMONSTER = 1;
            FWS_INTERVALOFNEXTMONSTER = FWS_INTERVALOFNEXTMONSTER * 60000;
            
            FWS_ACTIVITYTIMEOFMOBS           = Integer.parseInt(sailrenSettings.getProperty("ActivityTimeOfMobs", "120"));
            if(FWS_ACTIVITYTIMEOFMOBS <= 0) FWS_ACTIVITYTIMEOFMOBS = 120;
            FWS_ACTIVITYTIMEOFMOBS = FWS_ACTIVITYTIMEOFMOBS * 60000;        
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+SAILREN_FILE+" File.");
        }
    }
    
	//=============================================================================================
	public static final String  HEXID_FILE                  = PathFindingService.HEXID_FILE;
	//================================================================================
	public static byte[] HEX_ID;	// Hexadecimal ID of the game server
	public static int SERVER_ID; 
	
	//*****************************************************************************
	public static void loadHexidConfig()
	{
		System.out.println("Loading: " + HEXID_FILE);
		try
		{
			Properties hexidSettings    = new L2Properties();
			InputStream is              = new FileInputStream(HEXID_FILE);
			hexidSettings.load(is);
			is.close();
			
			SERVER_ID = Integer.parseInt(hexidSettings.getProperty("ServerID")); 
			HEX_ID = new BigInteger(hexidSettings.getProperty("HexID"), 16).toByteArray();
		}
		catch (Exception e)
		{
			_log.warn("GameServer: Could Not load Hexid File ("+HEXID_FILE+"), Hopefully login will Give us one.");
		}
	}
	//===================================================================================================
	public static final String  SIEGE_CONFIGURATION_FILE    = PathFindingService.SIEGE_FILE;
   //===================================================================================================
    public static int SIEGE_MAX_ATTACKER;
    public static int SIEGE_MAX_DEFENDER;
    public static int SIEGE_RESPAWN_DELAY_ATTACKER;
    public static int SIEGE_RESPAWN_DELAY_DEFENDER;
    public static int SIEGE_CT_LOSS_PENALTY;
    public static int SIEGE_FLAG_MAX_COUNT;
    public static int SIEGE_CLAN_MIN_LEVEL;
    public static int SIEGE_LENGTH_MINUTES;
    public static int SIEGE_CLAN_MIN_MEMBERCOUNT; 
    public static boolean   SPAWN_SIEGE_GUARD;
    
    //**********************************************************************************
    public static void loadSiegeConfig()
    {
       System.out.println("Loading: " + SIEGE_CONFIGURATION_FILE);
        try
        {
            Properties siegeSettings = new L2Properties();
            InputStream is           = new FileInputStream(SIEGE_CONFIGURATION_FILE);
            siegeSettings.load(is);
            is.close();
           
            SPAWN_SIEGE_GUARD             = Boolean.parseBoolean(siegeSettings.getProperty("SpawnSiegeGuard", "true"));
            SIEGE_CLAN_MIN_MEMBERCOUNT    = Integer.parseInt(siegeSettings.getProperty("SiegeClanMinMembersCount", "1")); 
            SIEGE_MAX_ATTACKER            = Integer.parseInt(siegeSettings.getProperty("AttackerMaxClans", "500"));
            SIEGE_MAX_DEFENDER            = Integer.parseInt(siegeSettings.getProperty("DefenderMaxClans", "500"));
            SIEGE_RESPAWN_DELAY_ATTACKER  = Integer.parseInt(siegeSettings.getProperty("AttackerRespawn", "30000"));
            SIEGE_RESPAWN_DELAY_DEFENDER  = Integer.parseInt(siegeSettings.getProperty("DefenderRespawn", "30000"));
            SIEGE_CT_LOSS_PENALTY         = Integer.parseInt(siegeSettings.getProperty("CTLossPenalty", "20000"));
            SIEGE_FLAG_MAX_COUNT          = Integer.parseInt(siegeSettings.getProperty("MaxFlags", "1"));
            SIEGE_CLAN_MIN_LEVEL          = Integer.parseInt(siegeSettings.getProperty("SiegeClanMinLevel", "4"));
            SIEGE_LENGTH_MINUTES          = Integer.parseInt(siegeSettings.getProperty("SiegeLength", "120"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("GameServer: Failed to Load "+SIEGE_CONFIGURATION_FILE+" File.");
        }
    }
	
		//Added by NecroLorD
		//===================================================================================================
	public static final String  ShTMain_FILE   = PathFindingService.ShTMain_FILE;
	//===================================================================================================
	public static int    SHT_Fake_SECONDS_SHUTDOWN;
	public static int    SHT_SHUTDOWNCOUNT;
	public static boolean  ShT_FISHINGWATERREQUIRED;

	//**************************************************************************************************
	public static void loadShtExtConfig()
	{
		System.out.println("Loading: "+ShTMain_FILE);
		try
		{
			Properties shtmain    = new L2Properties();
			InputStream is           = new FileInputStream(new File(ShTMain_FILE));
			shtmain.load(is);
			is.close();
			
			SHT_Fake_SECONDS_SHUTDOWN                  = Integer.parseInt(shtmain.getProperty("ShutdownFakeSek", "10"));
			SHT_SHUTDOWNCOUNT                          = Integer.parseInt(shtmain.getProperty("ShutDownCont", "30"));
			ShT_FISHINGWATERREQUIRED                   = Boolean.parseBoolean(shtmain.getProperty("FishingWaterRequired", "true"));
		} 
		catch (Exception e) 
		{ 
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load: "+ShTMain_FILE+" File.");
		} 
	}
	//=====================================================================================================
		public static final String  BENOM_FILE   = PathFindingService.BENOM_FILE;
	//===================================================================================================
    public static boolean SHT_BENOM_ENABLED;
	public static long    SHT_SIEGE_BENOM_INTERVAL;
	public static int     SHT_BENOM_S_X;
	public static int     SHT_BENOM_S_Y;
	public static int     SHT_BENOM_S_Z;
	public static int     SHT_BENOM_S_HEAD;

	//**************************************************************************************************
	public static void loadBenomConfig()
	{
		System.out.println("Loading: " +BENOM_FILE);
		try
		{
			Properties BenomConfig  = new L2Properties();
			InputStream is          = new FileInputStream(new File(BENOM_FILE));
			BenomConfig.load(is);
			is.close();
			
			SHT_BENOM_ENABLED                  = Boolean.parseBoolean(BenomConfig.getProperty("EnableBenom", "true"));
			SHT_SIEGE_BENOM_INTERVAL           = Long.parseLong(BenomConfig.getProperty("FirstSpawnInterval", "86400000"));
			SHT_BENOM_S_X                      = Integer.parseInt(BenomConfig.getProperty("SiegeX", "11904"));
			SHT_BENOM_S_Y                      = Integer.parseInt(BenomConfig.getProperty("SiegeY", "-49195"));
			SHT_BENOM_S_Z                      = Integer.parseInt(BenomConfig.getProperty("SiegeZ", "989"));
			SHT_BENOM_S_HEAD                   = Integer.parseInt(BenomConfig.getProperty("SiegeHeading", "63371"));
		
		}
		catch (Exception e)
		{
			_log.error(e.getMessage(),e);
			throw new Error("GameServer: Failed to Load "+BENOM_FILE+" File.");
		}
	}
	
	//================================================================================================
	//End
	/**
	 *  Internal properties for developers tests only 
	 */
	public static int          FACTION_KILL_RATE = 1000;
	public static int          FACTION_QUEST_RATE = 1;
	public static boolean      TEST_KNOWNLIST = false;
	public static boolean      FACTION_ENABLED = false;
	public static boolean      FACTION_KILL_REWARD = false;

	/**
	 * Main Function to Load all Config Variables.
	 * calling all voids.
	 */
	public static void load()
	{ 
		loadLogConfig(); // must be loaded b4 first log output
		Util.printSection("Configuration");
		loadDbConfig();
		loadGsConfig();
		loadHexidConfig();
		loadRespawnsConfig();
		loadRegenSettings();
		loadPetitionSettings();
		loadClassMastersConfig();
		loadAlternativeConfig();
		loadChampionConfig();
		loadChConfig();
		loadClansConfig();
		loadCbConfig();
		loadCraftManagerConfig();
		loadCraftingConfig();
		loadCustomConfig();
		loadDevConfig();
		loadDropsConfig();
		loadEnchantConfig();
		loadPartyConfig();
		loadFsConfig();
		loadDmConfig();
		loadTvtConfig();
		loadCtfConfig();
		loadGmAcessConfig();
		loadGridConfig();
		loadIdFactoryConfig();
		loadJailConfig();
		loadLotteryConfig();
		loadNpcBufferConfig();
		loadOlympiadConfig();
		loadOptionalConfig();
		loadOtherConfig();
		loadWhConfig();
		loadPermissionsConfig();
		loadPvPConfig();
		loadRatesConfig();
		load7sConfig();
		loadSkillsConfig();
		loadTelnetConfig();
		loadWeddingsConfig();
		loadPrivilegesConfig(); 
		loadCacheConfig();
		loadNetworkConfig();
		loadSecurityConfig();
		loadIrcConfig();
		loadSailrenConfig();
		loadChatConfig();
		loadValakasConfig();
		loadAntharasConfig();
		loadBaiumConfig();
		// L2EMU_ADD_START
		if (ENABLEVANHALTERMANAGER)
			loadVanHalterConfig();
		loadProtectorConfig();
		// L2EMU_ADD_END
		loadAntNestConfig();
		loadNpcsConfig();
		loadNicksConfig();
		loadThreadsConfig();
		loadInventoryConfig();
		loadPvtStoresConfig();
		loadLevelingConfig();
		loadDrConfig();
		loadNpcAnnouncerConfig();
		loadSiegeConfig();
		loadManorConfig();
		loadChatFilterConfig();
		loadREConfig();
		loadNpcEnchConfig();
		loadNpcChLevelConfig();
		loadFortressSiegeConfig();
		initDbProperties();
		loadShtExtConfig();
		loadBenomConfig();
		Util.printSection("Information");
		_log.info("Dark Moon Interlude Server");
		_log.info("Version: 1.0");
		Util.printSection("Chat Filter");
		
	}
	/**
	 * To keep compatibility with old loginserver.properties, add db properties into system properties
	 * Spring will use those values later
	 */
	public static void initDbProperties() 
	{
		System.setProperty("net.sf.l2j.db.driverclass", DATABASE_DRIVER );
		System.setProperty("net.sf.l2j.db.urldb", DATABASE_URL );
		System.setProperty("net.sf.l2j.db.user", DATABASE_LOGIN );
		System.setProperty("net.sf.l2j.db.password", DATABASE_PASSWORD );     
		System.setProperty("net.sf.l2j.db.maximum.db.connection", Integer.toString(DATABASE_MAX_CONNECTIONS) );
	}    
	/**
	 * Set a new value to a game parameter from the admin console.
	 * @param pName (String) : name of the parameter to change
	 * @param pValue (String) : new value of the parameter
	 * @return boolean : true if modification has been made
	 * @link useAdminCommand
	 */
	
	//TODO: Organize this with currently emu settings.
	public static boolean setParameterValue(String pName, String pValue)
	{
		// Rates                                          
		if (pName.equalsIgnoreCase("RateXp"))                         RATE_XP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateSp"))                    RATE_SP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RatePartyXp"))               RATE_PARTY_XP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RatePartySp"))               RATE_PARTY_SP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateQuestsReward"))          RATE_QUESTS_REWARD = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateDropAdena"))             RATE_DROP_ADENA = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateConsumableCost"))        RATE_CONSUMABLE_COST = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateDropItems"))             RATE_DROP_ITEMS = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateDropSpoil"))             RATE_DROP_SPOIL = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateDropQuest"))             RATE_DROP_QUEST = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateDropManor"))             RATE_DROP_MANOR = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("RateKarmaExpLost"))          RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("RateSiegeGuardsPrice"))      RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
		
		else if (pName.equalsIgnoreCase("PlayerDropLimit"))           PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerRateDrop"))            PLAYER_RATE_DROP = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerRateDropItem"))        PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerRateDropEquip"))       PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerRateDropEquipWeapon")) PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("KarmaDropLimit"))            KARMA_DROP_LIMIT = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("KarmaRateDrop"))             KARMA_RATE_DROP = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("KarmaRateDropItem"))         KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("KarmaRateDropEquip"))        KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("KarmaRateDropEquipWeapon"))  KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("DeleteCharAfterDays"))       DELETE_DAYS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AutoDestroyDroppedItemAfter"))
		{
			GRID_AUTO_DESTROY_ITEM_AFTER = Integer.parseInt(pValue);
			AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
		}
		else if (pName.equalsIgnoreCase("DestroyPlayerDroppedItem"))        DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("DestroyEquipableItem"))            DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("SaveDroppedItem"))                 SAVE_DROPPED_ITEM = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("EmptyDroppedItemTableAfterLoad"))  EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("SaveDroppedItemInterval"))         SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ClearDroppedItemTable"))           CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("CoordSynchronize"))                COORD_SYNCHRONIZE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AllowCursedWeapons"))              ALLOW_CURSED_WEAPONS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowDiscardItem"))                ALLOW_DISCARDITEM = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowFreight"))                    ALLOW_FREIGHT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowWarehouse"))                  ALLOW_WAREHOUSE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowWear"))                       ALLOW_WEAR = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WearDelay"))                       WEAR_DELAY = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("WearPrice"))                       WEAR_PRICE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AllowWater"))                      ALLOW_WATER = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowRentPet"))                    ALLOW_RENTPET = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("ShowLevelOnCommunityBoard"))       SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("ShowStatusOnCommunityBoard"))      SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("NamePageSizeOnCommunityBoard"))    NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("NamePerRowOnCommunityBoard"))      NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ShowCursedWeaponOwner"))           SHOW_CURSED_WEAPON_OWNER = Boolean.valueOf(pValue); 
		else if (pName.equalsIgnoreCase("AllowBoat"))                       ALLOW_BOAT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("ChanceToBreak"))                   CHANCE_BREAK = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ChanceToLevel"))                   CHANCE_LEVEL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ShowNpcLevel"))                    SHOW_NPC_LVL = Boolean.valueOf(pValue);
		
		//Champion Mods
		else if (pName.equalsIgnoreCase("ChampionSpecialItemLevelDiff"))   CHAMPION_SPCL_LVL_DIFF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ChampionEnable"))                  CHAMPION_ENABLE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("ChampionFrequency"))               CHAMPION_FREQUENCY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHp"))                      CHAMPION_HP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHpRegen"))                 CHAMPION_HP_REGEN = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionAtk"))                     CHAMPION_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpdAtk"))                  CHAMPION_SPD_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewards"))                 CHAMPION_REWARDS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionAdenasRewards"))           CHAMPION_ADENA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionExpSp"))                   CHAMPION_EXP_SP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionBoss"))                    CHAMPION_BOSS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ChampionMinLevel"))                CHAMPION_MIN_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMaxLevel"))                CHAMPION_MAX_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMinions"))                 CHAMPION_MINIONS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemChance"))       CHAMPION_SPCL_CHANCE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemID"))           CHAMPION_SPCL_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemAmount"))       CHAMPION_SPCL_QTY = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ForceInventoryUpdate"))            FORCE_INVENTORY_UPDATE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AutoDeleteInvalidQuestData"))      AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("MaximumOnlineUsers"))              MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ZoneTown"))                        ZONE_TOWN = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ShowGMLogin"))                     SHOW_GM_LOGIN = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("HideGMStatus"))                    HIDE_GM_STATUS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("MaxSubclass"))                     MAX_SUBCLASS = Integer.parseInt(pValue);
		
		//Wedding Mods
		else if (pName.equalsIgnoreCase("AllowWedding"))			        ALLOW_WEDDING = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WeddingTeleport"))			        WEDDING_TELEPORT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WeddingTeleportPrice"))	        WEDDING_TELEPORT_PRICE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("WeddingTeleportDuration"))	        WEDDING_TELEPORT_INTERVAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("WeddingDivorceCosts"))		        WEDDING_DIVORCE_COSTS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("WeddingPunishInfidelity"))	        WEDDING_PUNISH_INFIDELITY = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WeddingFormalWear"))		        WEDDING_FORMALWEAR = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WeddingAllowSameSex"))		        WEDDING_SAMESEX = Boolean.valueOf(pValue);
		
		// Other settings
		else if (pName.equalsIgnoreCase("UseDeepBlueDropRules"))            DEEPBLUE_DROP_RULES = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AllowGuards"))                     ALLOW_GUARDS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("CancelLesserEffect"))              EFFECT_CANCELING = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("WyvernSpeed"))                     WYVERN_SPEED = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("StriderSpeed"))                    STRIDER_SPEED = Integer.parseInt(pValue);
		
		//Normal inventory slots
		else if (pName.equalsIgnoreCase("MaxInventorySlotsForPlayer"))        INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxInventorySlotsForDwarf"))         INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxInventorySlotsForGameMaster"))    INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
		
		//Wharehouse Slots System
		else if (pName.equalsIgnoreCase("MaxWarehouseSlotsForPlayer"))  WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxWarehouseSlotsForDwarf"))   WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxWarehouseSlotsForClan"))    MAX_WAREHOUSE_SLOTS_FOR_CLAN = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaximumWarehouseFreightSlots"))FREIGHT_SLOTS = Integer.parseInt(pValue);
		
		//Enchant System (enchant.properties)
		else if (pName.equalsIgnoreCase("EnchantChance"))              ENCHANT_CHANCE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantSafeMax"))             ENCHANT_SAFE_MAX = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceWeapon"))        ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceArmor"))         ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakWeapon"))         ENCHANT_BREAK_WEAPON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakArmor"))          ENCHANT_BREAK_ARMOR = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceWeaponCrystal")) ENCHANT_CHANCE_WEAPON_CRYSTAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceArmorCrystal"))  ENCHANT_CHANCE_ARMOR_CRYSTAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakWeaponCrystal"))  ENCHANT_BREAK_WEAPON_CRYSTAL = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakArmorCrystal"))   ENCHANT_BREAK_ARMOR_CRYSTAL = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceWeaponBlessed")) ENCHANT_CHANCE_WEAPON_BLESSED = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantChanceArmorBlessed"))  ENCHANT_CHANCE_ARMOR_BLESSED = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakWeaponBlessed"))  ENCHANT_BREAK_WEAPON_BLESSED = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantBreakArmorBlessed"))   ENCHANT_BREAK_ARMOR_BLESSED = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("EnchantMaxWeapon"))           ENCHANT_MAX_WEAPON = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantMaxArmor"))            ENCHANT_MAX_ARMOR = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantSafeMaxFull"))         ENCHANT_SAFE_MAX_FULL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf1Enchantlevel"))  ENCHANT_DWARF_1_ENCHANTLEVEL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf2Enchantlevel"))  ENCHANT_DWARF_2_ENCHANTLEVEL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf3Enchantlevel"))  ENCHANT_DWARF_3_ENCHANTLEVEL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf1Chance"))        ENCHANT_DWARF_1_CHANCE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf2Chance"))        ENCHANT_DWARF_2_CHANCE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("EnchantDwarf3Chance"))        ENCHANT_DWARF_3_CHANCE = Integer.parseInt(pValue);
		
		//Regeneration (regeneration.properties)
		else if (pName.equalsIgnoreCase("NpcHpRegenMultiplier"))        NPC_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("NpcMpRegenMultiplier"))        NPC_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("PlayerCpRegenMultiplier"))     PLAYER_CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("PlayerHpRegenMultiplier"))     PLAYER_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("PlayerMpRegenMultiplier"))     PLAYER_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("RaidHpRegenMultiplier"))       RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("RaidMpRegenMultiplier"))       RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("RaidDefenceMultiplier"))       RAID_DEFENCE_MULTIPLIER = Double.parseDouble(pValue) /100;
		else if (pName.equalsIgnoreCase("RaidMinionRespawnTime"))       RAID_MINION_RESPAWN_TIMER =Integer.parseInt(pValue); 
		
		//Player Stuff
		else if (pName.equalsIgnoreCase("PlayerUnstuckInterval"))         UNSTUCK_INTERVAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerFakeDeathUpProtection"))   PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerSpawnProtection"))         PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("PlayerRespawnRestoreCP"))        RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
		else if (pName.equalsIgnoreCase("PlayerRespawnRestoreHP"))        RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
		else if (pName.equalsIgnoreCase("PlayerRespawnRestoreMP"))        RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
		else if (pName.equalsIgnoreCase("StartingAdena"))                 STARTING_ADENA = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxPrivateStoreSlotsForDwarf"))  MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxPrivateStoreSlotsForOther"))  MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("StoreSkillCooltime"))            STORE_SKILL_COOLTIME = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("PartyXpCutoffMethod"))           PARTY_XP_CUTOFF_METHOD = pValue;
		else if (pName.equalsIgnoreCase("PartyXpCutoffPercent"))          PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("PartyXpCutoffLevel"))            PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AltMembersCanWithdrawFromClanWH")) ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.valueOf(pValue);
		
		else if (pName.equalsIgnoreCase("AltGameTiredness"))        ALT_GAME_TIREDNESS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameCreation"))         ALT_GAME_CREATION = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameCreationSpeed"))    ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("AltGameCreationXpRate"))   ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("AltGameCreationSpRate"))   ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
		else if (pName.equalsIgnoreCase("AltGameSkillLearn"))       ALT_GAME_SKILL_LEARN = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltBuffTime"))             ALT_BUFF_TIME = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AltNbCumulatedBuff"))      ALT_GAME_NUMBER_OF_CUMULATED_BUFF = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AltAttackDelay"))          ALT_ATTACK_DELAY = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("AltDanceTime"))            ALT_DANCE_AND_SONG_TIME = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxPAtkSpeed"))            MAX_PATK_SPEED = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MaxMAtkSpeed"))            MAX_MATK_SPEED = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("GradePenalty"))            GRADE_PENALTY = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("RemoveCastleCirclets"))    REMOVE_CASTLE_CIRCLETS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameCancelByHit"))
		{
			ALT_GAME_CANCEL_BOW     = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
			ALT_GAME_CANCEL_CAST    = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
		}
		
		else if (pName.equalsIgnoreCase("AltShieldBlocks"))                      ALT_GAME_SHIELD_BLOCKS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltPerfectShieldBlockRate"))            ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("Delevel"))                              ALT_GAME_DELEVEL = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("MagicFailures"))                        ALT_GAME_MAGICFAILURES = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameMobAttackAI"))                   ALT_GAME_MOB_ATTACK_AI = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltMobAgroInPeaceZone"))                ALT_MOB_AGRO_IN_PEACEZONE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameExponentXp"))                    ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("AltGameExponentSp"))                    ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("AltGameFreights"))                      ALT_GAME_FREIGHTS = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltGameFreightPrice"))                  ALT_GAME_FREIGHT_PRICE = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("DisableRaidBossFossilization"))         ALT_DISABLE_RAIDBOSS_PETRIFICATION = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("CraftingEnabled"))                      IS_CRAFTING_ENABLED = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("SpBookNeeded"))                         SP_BOOK_NEEDED = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("LifeCrystalNeeded"))                    LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("AutoLoot"))                             AUTO_LOOT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AutoLootHerbs"))                        AUTO_LOOT_HERBS = Boolean.valueOf(pValue);
		 else if (pName.equalsIgnoreCase("AutoLootAdena"))                       AUTO_LOOT_ADENA = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("AllowAutoHerbsCommand"))                ALLOW_AUTOHERBS_CMD = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltKarmaPlayerCanBeKilledInPeaceZone")) ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltKarmaPlayerCanShop"))                ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTeleport"))            ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTrade"))               ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseWareHouse"))        ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltFreeTeleporting"))                   ALT_GAME_FREE_TELEPORT = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltNewCharAlwaysIsNewbie"))             ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltSubClassWithoutQuests"))             ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MinKarma"))                             KARMA_MIN_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxKarma"))                             KARMA_MAX_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("XPDivider"))                            KARMA_XP_DIVIDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("BaseKarmaLost"))                        KARMA_LOST_BASE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CanGMDropEquipment"))                   KARMA_DROP_GM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AwardPKKillPVPPoint"))                  KARMA_AWARD_PK_KILL = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MinimumPKRequiredToDrop"))              KARMA_PK_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PvPTime"))                              PVP_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("GlobalChat"))                           DEFAULT_GLOBAL_CHAT = ChatMode.valueOf(pValue.toUpperCase());
        else if (pName.equalsIgnoreCase("TradeChat"))                            DEFAULT_TRADE_CHAT = ChatMode.valueOf(pValue.toUpperCase());
		else if (pName.equalsIgnoreCase("GMAdminMenuStyle"))                     GM_ADMIN_MENU_STYLE = pValue;
		
		//CTF Mod (ctf.properties)
		else if (pName.equalsIgnoreCase("CTFAllowInterference"))        CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFAllowPotions"))             CTF_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFAllowSummon"))              CTF_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFOnStartRemoveAllEffects"))  CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFOnStartUnsummonPet"))       CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("CTFEvenTeams"))                CTF_EVEN_TEAMS = pValue;  
		
		//TvT Mod (tvt.properties)
		else if (pName.equalsIgnoreCase("TvTAllowInterference"))        TVT_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTAllowPotions"))             TVT_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTAllowSummon"))              TVT_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTOnStartRemoveAllEffects"))  TVT_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTOnStartUnsummonPet"))       TVT_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("TvTEvenTeams"))                TVT_EVEN_TEAMS = pValue;  
		
		//DM Mod (dm.properties)
		else if (pName.equalsIgnoreCase("DMAllowInterference"))         DM_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMAllowPotions"))              DM_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMAllowSummon"))               DM_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMOnStartRemoveAllEffects"))   DM_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("DMOnStartUnsummonPet"))        DM_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		
		//fortress siege mod
		else if (pName.equalsIgnoreCase("FortressSiegeEvenTeams"))  FortressSiege_EVEN_TEAMS = pValue;
		else if (pName.equalsIgnoreCase("FortressSiegeAllowInterference")) FortressSiege_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("FortressSiegeAllowPotions")) FortressSiege_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("FortressSiegeAllowSummon")) FortressSiege_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("FortressSiegeOnStartRemoveAllEffects")) FortressSiege_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("FortressSiegeOnStartUnsummonPet")) FortressSiege_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("FortressSiegeSameIPPlayersAllowed")) FortressSiege_SAME_IP_PLAYERS_ALLOWED = Boolean.parseBoolean(pValue);
		//Custom (custom.properties)
	    else if (pName.equalsIgnoreCase("AltBlacksmithUseRecipes"))     ALT_BLACKSMITH_USE_RECIPES = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("FailFakeDeath"))               FAIL_FAKEDEATH = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("DaggerVSHeavy"))               ALT_DAGGER_DMG_VS_HEAVY = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("DaggerVSHeavy"))               ALT_DAGGER_DMG_VS_ROBE  = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("DaggerVSHeavy"))               ALT_DAGGER_DMG_VS_LIGHT = Float.parseFloat(pValue);
		else if (pName.equalsIgnoreCase("BlowFront"))                   ALT_BLOW_FRONT = Integer.parseInt(pValue);  
		else if (pName.equalsIgnoreCase("BlowBehind"))                  ALT_BLOW_BEHIND = Integer.parseInt(pValue);  
		else if (pName.equalsIgnoreCase("BlowSide"))                    ALT_BLOW_SIDE = Integer.parseInt(pValue);  
        else if (pName.equalsIgnoreCase("PacketProtection"))            ENABLE_PACKET_PROTECTION = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("UnknownPacketsBeforeBan"))     MAX_UNKNOWN_PACKETS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("UnknownPacketsPunishment"))    UNKNOWN_PACKETS_PUNISHMENT = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AltWeightLimit"))              ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
		
		// Seven Signs (seven_signs.properties)
		else if (pName.equalsIgnoreCase("AltRequireCastleForDawn"))       ALT_GAME_REQUIRE_CASTLE_DAWN = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltRequireClanCastle"))          ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AnnounceMammonSpawn"))           ANNOUNCE_MAMMON_SPAWN = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltFestivalAncientAdenaPrice"))  ALT_FESTIVAL_ANCIENT_ADENA_PRICE = Integer.parseInt(pValue);
		
		
        //Antharas (antharas.properties)
		else if (pName.equalsIgnoreCase("IntervalOfAntharas"))           FWA_INTERVALOFANTHARAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AppTimeOfAntharas"))            FWA_APPTIMEOFANTHARAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ActivityTimeOfAntharas"))       FWA_ACTIVITYTIMEOFANTHARAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("OldAntharas"))                  FWA_OLDANTHARAS = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("LimitOfWeak"))                  FWA_LIMITOFWEAK = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("LimitOfNormal"))                FWA_LIMITOFNORMAL = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("IntervalOfBehemothOnWeak"))     FWA_INTERVALOFBEHEMOTHONWEAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBehemothOnNormal"))   FWA_INTERVALOFBEHEMOTHONNORMAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBehemothOnStrong"))   FWA_INTERVALOFBEHEMOTHONSTRONG = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnWeak"))       FWA_INTERVALOFBOMBERONWEAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnNormal"))     FWA_INTERVALOFBOMBERONNORMAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnStrong"))     FWA_INTERVALOFBOMBERONSTRONG = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MoveAtRandom"))                 FWA_MOVEATRANDOM = Boolean.parseBoolean(pValue);
		
        //Baium (baium.properties)
        else if (pName.equalsIgnoreCase("IntervalOfBaium"))              FWB_INTERVALOFBAIUM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ActivityTimeOfBaium"))          FWB_ACTIVITYTIMEOFBAIUM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MoveAtRandom"))                 FWB_MOVEATRANDOM = Boolean.parseBoolean(pValue);
		
		//Valakas(valakas.properties)
		else if (pName.equalsIgnoreCase("IntervalOfValakas"))        FWV_INTERVALOFVALAKAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("AppTimeOfValakas"))         FWV_APPTIMEOFVALAKAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ActivityTimeOfValakas"))    FWV_ACTIVITYTIMEOFVALAKAS = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("CapacityOfLairOfValakas"))  FWV_CAPACITYOFLAIR = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("MoveAtRandom"))             FWV_MOVEATRANDOM = Boolean.parseBoolean(pValue);

		//Sailren (sailren.properties)
		else if (pName.equalsIgnoreCase("EnableSinglePlayer"))     FWS_ENABLESINGLEPLAYER = Boolean.parseBoolean(pValue);
		else if (pName.equalsIgnoreCase("IntervalOfSailrenSpawn")) FWS_INTERVALOFSAILRENSPAWN = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("IntervalOfNextMonster"))  FWS_INTERVALOFNEXTMONSTER = Integer.parseInt(pValue);
		else if (pName.equalsIgnoreCase("ActivityTimeOfMobs"))     FWS_ACTIVITYTIMEOFMOBS = Integer.parseInt(pValue);
		
		//Siege (castle_siege.properties)
        else if (pName.equalsIgnoreCase("AttackerMaxClans"))   SIEGE_MAX_ATTACKER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DefenderMaxClans"))   SIEGE_MAX_DEFENDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AttackerRespawn"))    SIEGE_RESPAWN_DELAY_ATTACKER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DefenderRespawn"))    SIEGE_RESPAWN_DELAY_DEFENDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CTLossPenalty"))      SIEGE_CT_LOSS_PENALTY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxFlags"))           SIEGE_FLAG_MAX_COUNT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("SiegeClanMinLevel"))  SIEGE_CLAN_MIN_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("SiegeLength"))        SIEGE_LENGTH_MINUTES = Integer.parseInt(pValue);
		
		//party.properties
        else if (pName.equalsIgnoreCase("AltPartyRange"))  ALT_PARTY_RANGE = Integer.parseInt(pValue);
	    else if (pName.equalsIgnoreCase("AltPartyRange2")) ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
		
		else return false;
		return true;
	}
	public static class  ClassMasterSettings
	{
		private FastMap<Integer,FastMap<Integer,Integer>> _claimItems;
		private FastMap<Integer,FastMap<Integer,Integer>> _rewardItems;
		private FastMap<Integer,Boolean> _allowedClassChange;
		
		public ClassMasterSettings(String _configLine)
		{
			_claimItems = new FastMap<Integer,FastMap<Integer,Integer>>();
			_rewardItems = new FastMap<Integer,FastMap<Integer,Integer>>();
			_allowedClassChange = new FastMap<Integer,Boolean>();
			if (_configLine != null)
				parseConfigLine(_configLine.trim());
		}
		
		private void parseConfigLine(String _configLine)
		{
			StringTokenizer st = new StringTokenizer(_configLine, ";");
			
			while (st.hasMoreTokens())
			{
				// get allowed class change
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				FastMap<Integer,Integer> _items = new FastMap<Integer,Integer>();
				// parse items needed for class change
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						int _itemId = Integer.parseInt(st3.nextToken());
						int _quantity = Integer.parseInt(st3.nextToken());
						_items.put(_itemId, _quantity);
					}
				}
				
				_claimItems.put(job, _items);
				
				_items = new FastMap<Integer,Integer>();
				// parse gifts after class change
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						int _itemId = Integer.parseInt(st3.nextToken());
						int _quantity = Integer.parseInt(st3.nextToken());
						_items.put(_itemId, _quantity);
					}
				}
				
				_rewardItems.put(job, _items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			else 
				return false;
		}
		
		public FastMap<Integer,Integer> getRewardItems(int job)
		{
			if (_rewardItems.containsKey(job))
				return _rewardItems.get(job);
			else 
				return null;
		}
		
		public FastMap<Integer,Integer> getRequireItems(int job)
		{
			if (_claimItems.containsKey(job))
				return _claimItems.get(job);
			else 
				return null;
		}
		
	}
	private Config() {}
	
	/**
	 * Save hexadecimal ID of the server in the properties file.
	 * @param string (String) : hexadecimal ID of the server to store
	 * @see HEXID_FILE
	 * @see saveHexid(String string, String fileName)
	 * @link LoginServerThread
	 */
	public static void saveHexid(int serverId, String string)
	{
		Config.saveHexid(serverId, string, HEXID_FILE);
	}
	
	/**
	 * Save hexadecimal ID of the server in the properties file.
	 * @param hexId (String) : hexadecimal ID of the server to store
	 * @param fileName (String) : name of the properties file
	 */
	public static void saveHexid(int serverId, String hexId, String fileName)
	{
		try
		{
			Properties hexSetting = new L2Properties();
			File file = new File(fileName);
			//Create a new empty file only if it doesn't exist
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);
			hexSetting.setProperty("ServerID",String.valueOf(serverId));
			hexSetting.setProperty("HexID",hexId);
			hexSetting.store(out,"the hexID to auth into login");
			out.close();
		}
		catch (Exception e)
		{
			_log.warn("Failed to save hex id to "+fileName+" File.");
		}
	}
	/**
	 * Loads all Filter Words
	 */
	public static void loadFilter()
	{
		try
		{
			LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File(PathFindingService.FILTER_FILE))));
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				FILTER_LIST.add(line.trim());
			}
			_log.info("GameServer: Chat Filter: Loaded " + FILTER_LIST.size() + " Filter Words");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("GameServer: Failed to Load "+PathFindingService.FILTER_FILE+" File.");
		}
	}
	/**
	 * Clear all buffered filter words on memory.
	 */
	public static void cleanUpFilter()
    {
		_log.info("GameServer: Cleaning all Chat Filter Words..");
		FILTER_LIST.clear(); //rayan: unallocate all memory buffered for chat filter.(need to check for npe)
    }
}