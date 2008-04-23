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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.Olympiad;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SummonInstance;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.PetInfo;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>This class handles following admin commands:</b><br><br>
 * 
 * <li> admin_admin = slots for the  starting admin menus <br>
 * <li> admin_admin1 = slots for the  starting admin menus <br>
 * <li> admin_admin2 = slots for the  starting admin menus <br>
 * <li> admin_admin3 = slots for the  starting admin menus <br>
 * <li> admin_admin4 = slots for the  starting admin menus <br>
 * <li> admin_admin5 = slots for the  starting admin menus <br>
 * <li> admin_admin6 = slots for the  starting admin menus <br>
 * <li> admin_gmliston = includes/excludes active character from /gmlist results <br>
 * <li> admin_gmlistoff = includes/excludes active character from /gmlist results <br>
 * <li> admin_silence = toggles private messages acceptance mode <br>
 * <li> admin_diet = toggles weight penalty mode <br>
 * <li> admin_tradeoff = toggles trade acceptance mode <br>
 * <li> admin_set <br>
 * <li> admin_set_menu <br>
 * <li> admin_set_mod = alters specified server setting<br>
 * <li> admin_saveolymp = saves olympiad state manually <br>
 * <li> admin_manualhero = cycles olympiad and calculate new heroes. <br>
 * <li> admin_config_server <br>
 * <li> admin_config_server2 <br>
 * <li> admin_summon  <br>
 * <li> admin_unsummon <br><br>
 * 
 * <b>Usage:</b><br><br>
 * 
 * <li> //admin <br>
 * <li> //admin1 <br>
 * <li> //admin2 <br>
 * <li> //admin3 <br>
 * <li> //admin4 <br>
 * <li> //admin5 <br>
 * <li> //admin6 <br>
 * <li> //gmliston <br>
 * <li> //gmlistoff  <br>
 * <li> //silence <br>
 * <li> //diet <br>
 * <li> //tradeoff <br>
 * <li> //set <br>
 * <li> //set_menu <br>
 * <li> //set_mod <br>
 * <li> //saveolymp <br>
 * <li> //manualhero <br>
 * <li> //config_server <br>
 * <li> //config_server2 <br>
 * <li> //summon  <br>
 * <li> //unsummon <br><br>
 * 
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2007/07/28 10:06:06 $
 *
 */
public class AdminAdmin implements IAdminCommandHandler
{	
	@SuppressWarnings("unused")
	private final static Log _log = LogFactory.getLog(AdminAdmin.class);

	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_admin", 
		"admin_admin1", 
		"admin_admin2",
		"admin_admin3",
		"admin_admin4",
		"admin_admin5",
		"admin_gmliston",
		"admin_gmlistoff",
		"admin_silence", 
		"admin_diet",
		"admin_tradeoff",
		"admin_set",
		"admin_set_menu",
		"admin_set_mod",
		"admin_saveolymp",
		"admin_manualhero",

		//L2EMU_ADD
		"admin_camera",	// test for moviemode.
		"admin_config_server",
		"admin_config_server2",
		"admin_summon", 
	    "admin_unsummon",
		"admin_admin6"};

	private static final int REQUIRED_LEVEL = Config.GM_MENU;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if (command.startsWith("admin_admin"))
		{
			showMainPage(admin, command);
		}
		else if (command.equals("admin_config_server"))
		{
			ShowConfigPage(admin);
		}
		else if (command.equals("admin_config_server2"))
		{
			ShowConfigPage2(admin);
		}
		else if(command.startsWith("admin_gmliston"))
		{
			GmListTable.getInstance().showGm(admin);
			admin.sendMessage("Registered into gm list, Showing on gm list");
		}
		else if(command.startsWith("admin_gmlistoff"))
		{
			GmListTable.getInstance().hideGm(admin);
			admin.sendMessage("Hiding from gm list, Removed from gm list");
		}
		else if(command.startsWith("admin_silence"))
		{
			if (admin.getMessageRefusal()) // already in message refusal mode
			{
				admin.setMessageRefusal(false);
				admin.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_ACCEPTANCE_MODE));
			}
			else
			{
				admin.setMessageRefusal(true);
				admin.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_REFUSAL_MODE));
			}		
		}
		//[L2J_JP_ADD
        else if(command.startsWith("admin_camera"))
        {
            if(admin.getTarget() == null)
            {
            	admin.sendMessage("Target incorrect.");
            	admin.sendMessage("Usage:  //camera dist yaw pitch time duration");
            }
            else
            {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();

                try
                {
                	L2Object target = admin.getTarget();
                	int scDist = Integer.parseInt(st.nextToken());
                	int scYaw = Integer.parseInt(st.nextToken());
                	int scPitch = Integer.parseInt(st.nextToken());
                	int scTime = Integer.parseInt(st.nextToken());
                	int scDuration = Integer.parseInt(st.nextToken());
                	admin.enterMovieMode();
                	admin.specialCamera(target, scDist, scYaw, scPitch, scTime, scDuration);
                }
                catch(Exception e)
                {
                	admin.sendMessage("Usage:  //camera dist yaw pitch time duration");
                }
                finally
                {
                	admin.leaveMovieMode();
                }
            }
        }
		else if(command.startsWith("admin_summon"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try{
				int npcId = Integer.parseInt(st.nextToken());
				if(npcId != 0)
					adminSummon(admin, npcId);
			}catch(Exception e){
				admin.sendMessage("Usage: //summon <npcid>");
			}
		}
		else if(command.startsWith("admin_unsummon"))
		{
			if (admin.getPet() != null)
				admin.getPet().unSummon(admin);
		}
		else if(command.startsWith("admin_saveolymp"))
		{
			try
			{
				Olympiad.getInstance().save();
			}
			catch(Exception e){e.printStackTrace();}
			admin.sendMessage("Olympiad stuff saved!");
		}
		else if(command.startsWith("admin_manualhero"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
			}
			catch(Exception e){e.printStackTrace();}
			admin.sendMessage("Heroes formed");
		}
		else if(command.startsWith("admin_diet"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if(st.nextToken().equalsIgnoreCase("on"))
				{
					admin.setDietMode(true);
					admin.sendMessage("Diet mode on");
				}
				else if(st.nextToken().equalsIgnoreCase("off"))
				{
					admin.setDietMode(false);
					admin.sendMessage("Diet mode off");
				}
			}
			catch(Exception ex)
			{
				if(admin.getDietMode())
				{
					admin.setDietMode(false);
					admin.sendMessage("Diet mode off");
				}
				else
				{
					admin.setDietMode(true);
					admin.sendMessage("Diet mode on");
				}
			}
			finally
			{
				admin.refreshOverloaded();
			}
		}
		else if(command.startsWith("admin_tradeoff"))
		{
			try
			{
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					admin.setTradeRefusal(true);
					admin.sendMessage("Trade refusal enabled");
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					admin.setTradeRefusal(false);
					admin.sendMessage("Trade refusal disabled");
				}
			}
			catch(Exception ex)
			{
				if(admin.getTradeRefusal())
				{
					admin.setTradeRefusal(false);
					admin.sendMessage("Trade refusal disabled");
				}
				else
				{
					admin.setTradeRefusal(true);
					admin.sendMessage("Trade refusal enabled");
				}
			}
		}
		else if(command.startsWith("admin_set_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String pName = st.nextToken();
                String pValue = st.nextToken();
				if (Config.setParameterValue(pName, pValue))
					admin.sendMessage("parameter "+pName+" succesfully set to "+pValue);
				else
					admin.sendMessage("Invalid parameter!");
                 //resends page main
				Thread.sleep(300);
				AdminHelpPage.showMenuPage(admin, "main_menu.htm");
			}
			catch(Exception e)
			{
				admin.sendMessage("Usage: //set <parameter> <value>");
			}
		}
		else if(command.startsWith("admin_set_mod"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String pName = st.nextToken();
                String pValue = st.nextToken();

    			if (pName.equals("WeddingTeleport"))
    				pValue = String.valueOf(!Config.WEDDING_TELEPORT);
    			else if (pName.equals("WeddingPunishInfidelity"))
    				pValue = String.valueOf(!Config.WEDDING_PUNISH_INFIDELITY);
    			else if (pName.equals("WeddingFormalWear"))
    				pValue = String.valueOf(!Config.WEDDING_FORMALWEAR);
    			else if (pName.equals("WeddingAllowSameSex"))
    				pValue = String.valueOf(!Config.WEDDING_SAMESEX);

				if (Config.setParameterValue(pName, pValue))
					admin.sendMessage("parameter "+pName+" succesfully set to "+pValue);
				else
					admin.sendMessage("Invalid parameter!");
				//resends page mods
				Thread.sleep(300);
				AdminHelpPage.showMenuPage(admin, "mods_menu.htm");
			}
			catch(Exception e)
			{
				admin.sendMessage("Usage: //set <parameter> <value>");
			}
		}
		else if(command.startsWith("admin_set"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String pName = st.nextToken();
                String pValue = st.nextToken();
				if (Config.setParameterValue(pName, pValue))
					admin.sendMessage("parameter "+pName+" succesfully set to "+pValue);
				else 
					admin.sendMessage("Invalid parameter!");
				//resends page config
				Thread.sleep(300);
				ShowConfigPage(admin);
			}
			catch(Exception e)
			{
				admin.sendMessage("Usage: //set <parameter> <value>");
			}
		}
		return true;
	}
	
	public void showMainPage(L2PcInstance admin, String command)
	{
		int mode = 0;
		String filename=null;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e) {}
		switch (mode)
		{
		case 1:
			filename="main";
			break;
		case 2:
			filename="game";
			break;
		case 3:
			//L2EMU_EDIT
			filename="submenus/effects";
			//L2EMU_EDIT
			break;
		case 4:
			filename="server";
			break;
		case 5:
			filename="mods";
			break;
		case 6:
			filename="gmaction";
			break;
		default:
			if (Config.GM_ADMIN_MENU_STYLE.equals("modern"))
				filename="main";
			else
				filename="classic";
		break;
		}
		//L2EMU_EDIT
		AdminHelpPage.showMenuPage(admin, filename+"_menu.htm");
		//L2EMU_EDIT
	}
	
	public void ShowConfigPage2(L2PcInstance admin)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><body>");
		//L2EMU_EDIT_START
		replyMSG.append("<center><table width=270><tr><td width=60><button value=\"main\" action=\"bypass -h admin_admin\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td width=150><font color=\"LEVEL\">Config Server Panel</font></td><td width=60><button value=\"Panel1\" action=\"bypass -h admin_config_server\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table></center><br>");
		//L2EMU_EDIT_END
		replyMSG.append("<center><table width=260>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Show GM Login</font> = " + Config.SHOW_GM_LOGIN + "</td><td></td><td><button value=\""+ !Config.SHOW_GM_LOGIN +"\" action=\"bypass -h admin_set ShowGMLogin " + !Config.SHOW_GM_LOGIN + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Hide GM Status</font> = " + Config.HIDE_GM_STATUS + "</td><td></td><td><button value=\""+ !Config.HIDE_GM_STATUS +"\" action=\"bypass -h admin_set HideGMStatus " + !Config.HIDE_GM_STATUS + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Spawn Siege Guard</font> = " + Config.SPAWN_SIEGE_GUARD + "</td><td></td><td><button value=\""+ !Config.SPAWN_SIEGE_GUARD +"\" action=\"bypass -h admin_set SpawnSiegeGuard " + !Config.SPAWN_SIEGE_GUARD + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Auto Loot</font> = " + Config.AUTO_LOOT + "</td><td></td><td><button value=\""+ !Config.AUTO_LOOT +"\" action=\"bypass -h admin_set AutoLoot " + !Config.AUTO_LOOT + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Magic Failures</font> = " + Config.ALT_GAME_MAGICFAILURES + "</td><td></td><td><button value=\""+ !Config.ALT_GAME_MAGICFAILURES +"\" action=\"bypass -h admin_set MagicFailures " + !Config.ALT_GAME_MAGICFAILURES + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Book Needed</font> = " + Config.SP_BOOK_NEEDED + "</td><td></td><td><button value=\""+ !Config.SP_BOOK_NEEDED +"\" action=\"bypass -h admin_set SpBookNeeded " + !Config.SP_BOOK_NEEDED + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Learn Other Skill</font> = " + Config.ALT_GAME_SKILL_LEARN + "</td><td></td><td><button value=\""+ !Config.ALT_GAME_SKILL_LEARN +"\" action=\"bypass -h admin_set AltGameSkillLearn " + !Config.ALT_GAME_SKILL_LEARN + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Shield Block From All</font> = " + Config.ALT_GAME_SHIELD_BLOCKS + "</td><td></td><td><button value=\""+ !Config.ALT_GAME_SHIELD_BLOCKS +"\" action=\"bypass -h admin_set AltShieldBlocks " + !Config.ALT_GAME_SHIELD_BLOCKS + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Free Teleport</font> = " + Config.ALT_GAME_FREE_TELEPORT + "</td><td></td><td><button value=\""+ !Config.ALT_GAME_FREE_TELEPORT +"\" action=\"bypass -h admin_set AltFreeTeleporting " + !Config.ALT_GAME_FREE_TELEPORT + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Can Discard Items</font> = " + Config.ALLOW_DISCARDITEM + "</td><td></td><td><button value=\""+ !Config.ALLOW_DISCARDITEM +"\" action=\"bypass -h admin_set AllowDiscardItem " + !Config.ALLOW_DISCARDITEM + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Global Chat</font> = " + Config.DEFAULT_GLOBAL_CHAT + "</td><td><edit var=\"menu_command1\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set GlobalChat $menu_command1\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Coord Synchronize</font> = " + Config.COORD_SYNCHRONIZE + "</td><td><edit var=\"menu_command2\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set CoordSynchronize $menu_command2\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("</table></body></html>");
		adminReply.setHtml(replyMSG.toString());
		admin.sendPacket(adminReply);
	}
	
	public void ShowConfigPage(L2PcInstance admin)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><body>"); 
		replyMSG.append("<center><table width=270><tr><td width=60><button value=\"Admin\" action=\"bypass -h admin_admin\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td width=150><font color=\"LEVEL\">Config Server Panel</font></td><td width=60><button value=\"Panel2\" action=\"bypass -h admin_config_server2\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table></center><br>");
		replyMSG.append("<center><table width=260>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Auto loot</font> = " + Config.AUTO_LOOT + "</td><td></td><td><button value=\""+ !Config.AUTO_LOOT +"\" action=\"bypass -h admin_set AutoLoot " + !Config.AUTO_LOOT + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Allow Discard Item</font> = " + Config.ALLOW_DISCARDITEM + "</td><td></td><td><button value=\""+ !Config.ALLOW_DISCARDITEM +"\" action=\"bypass -h admin_set AllowDiscardItem " + !Config.ALLOW_DISCARDITEM + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Deep Blue Mobs Drop Rule</font> = " + Config.DEEPBLUE_DROP_RULES + "</td><td></td><td><button value=\""+ !Config.DEEPBLUE_DROP_RULES +"\" action=\"bypass -h admin_set UseDeepBlueDropRules " + !Config.DEEPBLUE_DROP_RULES + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Auto Destroy Item aft. sec.</font> = " + Config.AUTODESTROY_ITEM_AFTER + "</td><td><edit var=\"menu_command\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set AutoDestroyDroppedItemAfter $menu_command\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate EXP</font> = " + Config.RATE_XP + "</td><td><edit var=\"menu_command1\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateXP $menu_command1\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate SP</font> = " + Config.RATE_SP + "</td><td><edit var=\"menu_command2\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateSP $menu_command2\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Drop Adena</font> = " + Config.RATE_DROP_ADENA + "</td><td><edit var=\"menu_command3\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateDropAdena $menu_command3\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Drop Items</font> = " + Config.RATE_DROP_ITEMS + "</td><td><edit var=\"menu_command4\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateDropItems $menu_command4\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Drop Spoil</font> = " + Config.RATE_DROP_SPOIL + "</td><td><edit var=\"menu_command5\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateDropSpoil $menu_command5\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Quest Reward</font> = " + Config.RATE_QUESTS_REWARD + "</td><td><edit var=\"menu_command6\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RateQuestsReward $menu_command6\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Raid Regen HP</font> = " + Config.RAID_HP_REGEN_MULTIPLIER + "</td><td><edit var=\"menu_command7\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RaidHpRegenMultiplier $menu_command7\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Raid Regen MP</font> = " + Config.RAID_MP_REGEN_MULTIPLIER + "</td><td><edit var=\"menu_command8\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RaidMpRegenMultiplier $menu_command8\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Raid Defence</font> = " + Config.RAID_DEFENCE_MULTIPLIER + "</td><td><edit var=\"menu_command9\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set RaidDefenceMultiplier $menu_command9\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Alt Buff Time</font> = " + Config.ALT_BUFF_TIME + "</td><td><edit var=\"menu_command10\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_set AltBuffTime $menu_command10\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Alt game creation</font> = " + Config.ALT_GAME_CREATION + "</td><td></td><td><button value=\""+ !Config.ALT_GAME_CREATION +"\" action=\"bypass -h admin_set AltGameCreation " + !Config.ALT_GAME_CREATION + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("</table></body></html>");
		adminReply.setHtml(replyMSG.toString());
		admin.sendPacket(adminReply);
	}
	
	//[L2J_JP_ADD]
	public void adminSummon(L2PcInstance admin, int npcId){
		if (admin.getPet() != null) {
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			admin.sendPacket(sm);
			admin.getPet().unSummon(admin);
		}

		L2NpcTemplate summonTemplate = NpcTable.getInstance().getTemplate(npcId);
		L2SummonInstance summon = new L2SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, admin, null);

		summon.setTitle(admin.getName());
		summon.setExpPenalty(0);
		if (summon.getLevel() >= Experience.LEVEL.length)
		{
			summon.getStat().setExp(Experience.LEVEL[Experience.LEVEL.length - 1]);
		}
		else
		{
			summon.getStat().setExp(Experience.LEVEL[(summon.getLevel() % Experience.LEVEL.length)]);
		}
		summon.getStat().setExp(0);
		summon.getStatus().setCurrentHp(summon.getMaxHp());
		summon.getStatus().setCurrentMp(summon.getMaxMp());
		summon.setHeading(admin.getHeading());
		summon.setRunning();
		admin.setPet(summon);
		L2World.getInstance().storeObject(summon);
		summon.spawnMe(admin.getX()+50, admin.getY()+100, admin.getZ());
		summon.setFollowStatus(true);
		summon.setShowSummonAnimation(false); // addVisibleObject created the info packets with summon animation
		admin.sendPacket(new PetInfo(summon));
	}
	
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private boolean checkLevel(int level) 
	{
		return (level >= REQUIRED_LEVEL);
	}
}