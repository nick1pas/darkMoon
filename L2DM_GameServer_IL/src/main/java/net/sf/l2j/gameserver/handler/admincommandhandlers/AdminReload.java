package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.NpcWalkerRoutesTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.datatables.TradeListTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.Manager;
import net.sf.l2j.gameserver.instancemanager.MapRegionManager;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.model.L2Multisell;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
/**
 * 
 * @author Rayan RPG
 *
 */
public class AdminReload implements IAdminCommandHandler
{
	private static final int   REQUIRED_LEVEL  = Config.GM_RELOAD;

	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_reload",
		"admin_reload_menu",
		"admin_config_reload",
		"admin_config_reload_menu"
	};
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if(command.startsWith("admin_config_reload"))
		{
			sendConfigReloadPage(admin);
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();

			try
			{
				String type = st.nextToken();
				if(type.equals("rates"))
				{
					Config.loadRatesConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Rates Configs Reloaded.");
				}
				else if(type.equals("enchant"))
				{
					Config.loadEnchantConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Enchant Configs Reloaded.");
				}
				else if(type.equals("mapregion"))
				{
					MapRegionManager.getInstance().reload();
					admin.sendMessage("MapRegions reloaded.");
				}
				else if(type.equals("pvp"))
				{
					Config.loadPvPConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("PvP Config Configs Reloaded");
				}
				else if(type.equals("options"))
				{
					Config.loadOptionalConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Options Configs Reloaded");
				}
				else if(type.equals("other"))
				{
					Config.loadOtherConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Other Config Configs Reloaded.");
				}
				else if(type.equals("alt"))
				{
					Config.loadAlternativeConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Alternative Configs Reloaded");
				}
				else if(type.equals("clans"))
				{
					Config.loadClansConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Clans Configs Reloaded");
				}
				else if(type.equals("champions"))
				{
					Config.loadChampionConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Champions Configs Reloaded");
				}
				else if(type.equals("lottery"))
				{
					Config.loadLotteryConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Lottery Configs Reloaded");
				}
				else if(type.equals("sepulchurs"))
				{
					Config.loadFsConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Four Sepulchers Configs Reloaded");
				}
				else if(type.equals("clanhall"))
				{
					Config.loadChConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("clanhall Configs Reloaded.");
				}
				else if(type.equals("sevensigns"))
				{
					Config.load7sConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Seven Signs Configs Reloaded.");
				}
				else if(type.equals("gmaccess"))
				{
					Config.loadGmAcessConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("GMAccess Configs Reloaded.");
				}
				else if(type.equals("chatfilter"))
				{
					Config.cleanUpFilter();
					Config.loadChatFilterConfig();
					Config.loadFilter();
					sendConfigReloadPage(admin);
					admin.sendMessage("Chat Filter Reloaded.");
				}
				else if(type.equals("classmaster"))
				{
					Config.loadClassMastersConfig();
					sendConfigReloadPage(admin);
					admin.sendMessage("Classmaster Configs  Reloaded.");
				}
				else if(type.equals("all"))
				{
					Config.cleanUpFilter();
					Config.load();
					sendConfigReloadPage(admin);
					admin.sendMessage("All Configs  Reloaded.");
				}
				else if(type.equals("irc"))
				{
					Config.loadIrcConfig();
					admin.sendMessage("irc config reloaded");
				}
				else if(type.equals("siege"))
				{
					SiegeManager.getInstance().reload();
					admin.sendMessage("Siege config reloaded");
				}
				else if(type.equals("wedding"))
				{
					Config.loadWeddingsConfig();
					admin.sendMessage("Wedding config reloaded");
				}
				else if(type.equals("sailren"))
				{
					Config.loadSailrenConfig();
					admin.sendMessage("sailren config reloaded");
				}
			}
			catch(Exception e)
			{
				admin.sendMessage("Usage:  //reload <type>"); 
			}
		}
		if(command.equals("admin_config_reload_menu"))
		{
			sendConfigReloadPage(admin);
		}
		if(command.equals("admin_reload_menu"))
		{
			sendReloadPage(admin);
		}
		if(command.startsWith("admin_reload"))
		{
			sendReloadPage(admin);
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();

			try
			{
				String type = st.nextToken();

				if(type.equals("multisell"))
				{
					L2Multisell.getInstance().reload();
					sendReloadPage(admin);
					admin.sendMessage("Multisell Reload Complete.");
				}
				else if(type.startsWith("teleport"))
				{
					TeleportLocationTable.getInstance().reloadAll();
					sendReloadPage(admin);
					admin.sendMessage("All Teleport Tables Reloaded.");
				}
				else if(type.startsWith("tradelist"))  
				{  
					TradeListTable.getInstance().reloadAll();  
					sendReloadPage(admin);
					admin.sendMessage("Buylists Reload Complete.");  
				}  

				else if(type.startsWith("skill"))
				{
					SkillTable.getInstance().reload();
					sendReloadPage(admin);
					admin.sendMessage("Skills reload Complete.");
				}
				else if(type.equals("npcs"))
				{
					NpcTable.getInstance().cleanUp();
					NpcTable.getInstance().reloadAll();
					sendReloadPage(admin);
					admin.sendMessage("NPCs Reload Complete.");
				}
				else if(type.equals("spawnlist"))
				{
					SpawnTable.getInstance().reloadAll();
					sendReloadPage(admin);
					admin.sendMessage("Spawns Reload Complete.");
				}
				else if(type.startsWith("html"))
				{
					HtmCache.getInstance().reload();
					sendReloadPage(admin);
					admin.sendMessage("Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage()  + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " files loaded.");
				}
				else if(type.startsWith("item"))
				{
					ItemTable.getInstance().reload();
					sendReloadPage(admin);
					admin.sendMessage("Item Templates Reload Complete.");
				}
				else if(type.startsWith("quest"))
				{
					sendReloadPage(admin);
					admin.sendMessage("Quests Reload Complete.");
				}
				else if(type.startsWith("instancemanager"))
				{
					Manager.reloadAll();
					sendReloadPage(admin);
					admin.sendMessage("Instance Managers Reload Complete.");
				}
				else if(type.startsWith("npcwalkers"))
				{
					NpcWalkerRoutesTable.getInstance().load();
					admin.sendMessage("All NPC walker routes have been reloaded");
				}				
			}
			catch(Exception e)
			{
				admin.sendMessage("Usage:  //reload <type>"); 
			}
		}
		return true;
	}

	/**
	 * send reload page
	 * @param admin
	 */
	private void sendReloadPage(L2PcInstance admin)
	{
		AdminHelpPage.showSubMenuPage(admin,"reload_menu.htm"); 
	}

	/**
	 * sends config reload page
	 * @param admin
	 */
	private void sendConfigReloadPage(L2PcInstance admin)
	{
		AdminHelpPage.showSubMenuPage(admin,"config_reload_menu.htm"); 
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}