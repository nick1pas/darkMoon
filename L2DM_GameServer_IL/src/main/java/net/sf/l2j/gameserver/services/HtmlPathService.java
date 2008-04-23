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
package net.sf.l2j.gameserver.services;

/**
 * This Class Manages HTML PATHS to Avoid Typos. <br>
 * 
 * @author Rayan RPG for L2Emu Project
 * 
 * @since Rev 2.0.2
 *
 */
public class HtmlPathService
{ 

	public static String _serviceName = HtmlPathService.class.getName().toString();


	//************ MODS *************************************************************
	/**
	 * returns donator bufer html path
	 */
	public final static String DONATOR_BUFFER_HTML_PATH ="data/html/mods/donator_buffer/";

	/**
	 * returns npc enchanter html path
	 */
	public final static String ENCHANTER_HTML_PATH ="data/html/mods/npc_enchanter/";

	/**
	 * returns announcer npc html path
	 */
	public final static String ANNOUNCER_NPC_HTML_PATH ="data/html/mods/announcer_npc/";

	/**
	 * returns announcer npc html path
	 */
	public final static String RAIDEVENT_NPC_HTML_PATH ="data/html/mods/raidevent/";
	
	/**
	 * returns free bufer html path
	 */
	public static final String PLAYER_BUFFER_HTML_PATH ="data/html/mods/npc_buffer/";

	/**
	 * returns summon bufer html path
	 */
	public static final String SUMMON_BUFFER_NPC_HTML_PATH ="data/html/mods/summon_buffer/";

	/**
	 * returns tvt html path
	 */
	public static final String TVT_HTML_PATH ="data/html/mods/tvt/";

	/**
	 *  returns jail manager html path
	 */
	public static final String JAIL_MANAGER_HTML_PATH ="data/html/mods/jail_manager/";

	// **************************** OTHERS ***************************************************
	/**
	 * returns petition system html path
	 */
	public static final String PETITIONS_HTML_PATH ="data/html/petition/";

	/**
	 * returns petition olympiad html path
	 */
	public static final String OLYMPIAD_HTML_PATH ="data/html/olympiad/";

	/**
	 * returns the admin html path
	 */
	public static final String ADMIN_HTML_PATH ="data/html/admin/";
	public static final String ADMIN_HTML_PATH_MENUS =ADMIN_HTML_PATH+"menus/";
	public static final String ADMIN_HTML_PATH_INFO =ADMIN_HTML_PATH+"info/";
	/**
	 * returns the default html path
	 */
	public static final String DEFAULT_HTML_PATH ="data/html/default/";

	/**
	 * returns adventurer html paths
	 */
	public static final String ADVENTURER_HTML_PATH = "data/html/adventurer_guildsman/";
	/** not used
	 * public static final String ADVENTURER_HTML_PATH_RAID_INFO = ADVENTURER_HTML_PATH+"raid_info/";
	 * public static final String ADVENTURER_HTML_PATH_RAID_LEVEL = ADVENTURER_HTML_PATH_RAID_INFO+"level/";
	 */

	/**
	 *  returns main html path
	 */
	public static final String HTML_PATH = "data/html/";
	
	/**
	 * Returns the Npc Walker Html Path
	 * 
	 */
	public static final String NPC_WALKER_PATH = "data/html/walker/";

	/**
	 *  returns Auctioneer html path
	 */
	public static final String AUCTIONEER_HTML_PATH = "data/html/auction/";
	
	/**
	 *  returns Blacksmith html path
	 */
	public static final String BLACKSMITH_HTML_PATH = "data/html/blacksmith/";
	
	/**
	 *  returns BoxInstance html path
	 */
	public static final String BOX_HTML_PATH = "data/html/custom/";
	
	/**
	 *  returns CastleBlackSmith html path
	 */
	public static final String CASTLE_BLACKSMITH_HTML_PATH = "data/html/castleblacksmith/";
	
	/**
	 *  returns CastleChamberlain html path
	 */
	public static final String CASTLE_CHAMBERLAIN_HTML_PATH = "data/html/chamberlain/";
	public static final String CASTLE_CHAMBERLAIN_MANOR_HTML_PATH = CASTLE_CHAMBERLAIN_HTML_PATH + "manor/";
	
	/**
	 *  returns CastleTeleport html path
	 */
	public static final String CASTLE_TELEPORT_HTML_PATH = "data/html/teleporter/";
	
	/**
	 *  returns CastleWarehouse html path
	 */
	public static final String CASTLE_WAREHOUSE_HTML_PATH = "data/html/castlewarehouse/";
	
	/**
	 *  returns clanHallManager html path
	 */
	public static final String CH_PATH = "data/html/clanHallManager/";
	public static final String CH_SUPPORT_PATH = CH_PATH + "support/";
	
	/**
	 *  returns CraftManager html path
	 */
	public static final String CRAFT_MANAGER_HTML_PATH = "data/html/CrafterManager/";
	
	/**
	 *  returns Doormen html path
	 */
	public static final String DOORMEN_HTML_PATH = "data/html/doormen/";
	
	/**
	 *  returns Dooropen html path
	 */
	public static final String DOOROPEN_HTML_PATH = "data/html/dooropen/";
	
	/**
	 *  returns faction html path
	 */
	public static final String FACTION_HTML_PATH = "data/html/faction";
	public static final String FACTION_FULL_HTML_PATH = FACTION_HTML_PATH + "/";
}