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
 * Special instace to manage general pathfinding.
 * 
 * @author Rayan RPG For L2Emu Project !
 * 
 * @since 505
 *
 */
public class PathFindingService 
{
    // Paths
	//============================================================================================
	public static final String adminPath    = "./config/administration/";
	public static final String bossesPath   = "./config/bosses/";
	public static final String chatPath     = "./config/chat/";
	public static final String customPath   = "./config/custom/";
	public static final String devPath      = "./config/developer/";
	public static final String filterPath   = "./config/filters/";
	public static final String mainPath     = "./config/main/";
	public static final String eventPath    = "./config/main/events/";
	public static final String networkPath  = "./config/network/";
	public static final String zonePath     = "./config/zones/";
	public static final String modsPath     = "./config/mods/";
	public static final String extPath      = "./config/extensions/";
	
	//Main Files
	//============================================================================================
	public static final String GAMESERVER_FILE       = mainPath+"gameserver.properties";
	public static final String NICKS_FILE            = mainPath+"nicks.properties";
	public static final String NPC_FILE              = mainPath+"npc.properties";
	public static final String THREADS_FILE          = mainPath+"threads.properties";
	public static final String OPTIONS_FILE          = mainPath+"options.properties";
	public static final String CRAFTING_FILE         = mainPath+"crafting.properties";
	public static final String ID_FACTORY_FILE       = mainPath+"id_factory.properties";
	public static final String PVTSTORE_FILE         = mainPath+"pvtstores.properties";
	public static final String WAREHOUSE_FILE        = mainPath+"warehouse.properties";
	public static final String OTHER_FILE            = mainPath+"other_settings.properties";
	public static final String RESPAWN_FILE          = mainPath+"respawns.properties";
	public static final String PETITION_FILE         = mainPath+"petitions.properties";
	public static final String REGEN_FILE            = mainPath+"regeneration.properties";
	public static final String ENCHANT_FILE          = mainPath+"enchant.properties";
	public static final String RATES_FILE            = mainPath+"rates.properties";
	public static final String LEVELING_FILE         = mainPath+"leveling.properties";
	public static final String ALT_FILE              = mainPath+"alternative_settings.properties";
	public static final String PARTY_FILE            = mainPath+"party.properties";
	public static final String DROP_FILE             = mainPath+"drops.properties";
	public static final String COMMUNITY_BOARD_FILE  = mainPath+"communityboard.properties";
	public static final String SKILLS_FILE           = mainPath+"skills.properties";
	public static final String CLASSMASTER_FILE      = mainPath+"classmaster.properties";
	public static final String PERMISSIONS_FILE      = mainPath+"permissions.properties";
	public static final String INVENTORY_FILE        = mainPath+"inventory.properties";
	public static final String GRID_FILE             = mainPath+"grid.properties";
	public static final String PVP_CONFIG_FILE       = mainPath+"pvp_settings.properties";
	public static final String CLAN_HALL_FILE        = mainPath+"clan_hall.properties";
	public static final String CLAN_FILE             = mainPath+"clans.properties";
	public static final String MANOR_FILE            = mainPath+"manor.properties";
	public static final String PET_MANAGER_FILE      = mainPath+"pet_manager.properties";
	public static final String DYNAMIC_EXT_FILE      = mainPath+"dynamic_extensions.properties";
	
	//Filters
	//============================================================================================
	public static final String SAY_FILTER_FILE       = filterPath+"chat_filter.properties";
	public static final String FILTER_FILE           = filterPath+"chatfilter.txt";
	
	//Events Files
	//============================================================================================
	public static final String OLYMPIAD_FILE         = eventPath+"olympiad.properties";
	public static final String OLYMPIAD_FILE_SAVE    = eventPath+"olympiad_save.properties";
	public static final String LOTTERY_FILE          = eventPath+"lottery.properties";
	public static final String SEVEN_SIGNS_FILE      = eventPath+"seven_signs.properties";
	public static final String SIEGE_FILE            = eventPath+"castle_siege.properties";
	
	//Mods Files
	//============================================================================================
	public static final String TVT_FILE              = modsPath+"tvt.properties";
	public static final String CTF_FILE              = modsPath+"ctf.properties";
	public static final String DM_FILE               = modsPath+"dm.properties";
	public static final String FORTRESS_SIEGE_FILE   = modsPath+"fortress_siege.properties";
	//Administration Files
	//============================================================================================
	public static final String GM_ACCESS_FILE          = adminPath+"gm_access.properties";
	public static final String COMMAND_PRIVILEGES_FILE = adminPath+"command-privileges.properties";

	//Network Files
	//============================================================================================
	public static final String TELNET_FILE           = networkPath+"telnet.properties";
	public static final String DATABASE_FILE         = networkPath+"database.properties";
	public static final String CACHE_FILE            = networkPath+"cache.properties";
	public static final String NETWORK_FILE          = networkPath+"network.properties";
	public static final String HEXID_FILE            = networkPath+"hexid.txt";
	public static final String SECURITY_FILE         = networkPath+"security.properties";

	//Chat Files
	//============================================================================================
	public static final String IRC_FILE              = chatPath+"irc.properties";
	public static final String CHAT_FILE             = chatPath+"chat.properties";
	
	//Zones Files
	//============================================================================================
	public static final String ANTNEST_FILE          = zonePath+"ant_nest.properties";
	public static final String DIMENSIONAL_RIFT_FILE = zonePath+"dimensional_rift.properties";
	public static final String FOUR_SEPULCHERS_FILE  = zonePath+"four_sepulchers.properties";

	//Bosses Files
	//============================================================================================
	public static final String SAILREN_FILE          = bossesPath+"sailren.properties";
	public static final String ANTAHARAS_FILE        = bossesPath+"antharas.properties";
	public static final String BAIUM_FILE            = bossesPath+"baium.properties";
	public static final String VALAKAS_FILE          = bossesPath+"valakas.properties";
	public static final String VANHALTER_FILE		 = bossesPath+"vanhalter.properties";
	//sht addon, Benom conf file
	public static final String BENOM_FILE		     = bossesPath+"benom.conf";
	
	
	//Developer Files
	//============================================================================================
	public static final String DEV_FILE              = devPath+"settings.properties";

	//Custom Files
	//============================================================================================
	public static final String WEDDINGS_FILE         = customPath+"wedding.properties";
	public static final String CUSTOM_FILE           = customPath+"custom.properties";
	public static final String JAIL_FILE             = customPath+"jail.properties";
	public static final String CRAFT_MANAGER_FILE    = customPath+"craftmanager.properties";
	public static final String CHAMPION_FILE         = customPath+"champion_mobs.properties";
	public static final String NPC_BUFFER_FILE       = customPath+"npc_buffer.properties";
	public static final String NPC_ANNOUNCER_FILE    = customPath+"announcer_npc.properties";
	public static final String RAID_ENGINE_FILE      = customPath+"raid_event.properties";
	public static final String NPC_ENCHANTER_FILE    = customPath+"npc_enchanter.properties";
	public static final String NPC_CHANGELEVEL_FILE  = customPath+"npc_changelevel.properties";
	public static final String PROTECTOR_FILE        = customPath+"npc_protector.properties";
	
	//============================================================================================
	public static final String SCRIPT_ENGINE_PATH    = "data/script/";
	//============================================================================================
	
	public static final String ShTMain_FILE        = extPath+"ShTMain.conf";
}