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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.PlaySound;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.services.ThreadService;
import net.sf.l2j.gameserver.services.WindowService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * Instance to manage jailed players :)
 *  
 * @author Rayan RPG for L2Emu Project !
 * 
 * @since 471
 *
 */
public class L2JailManagerInstance extends L2NpcInstance
{
	public L2JailManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		// TODO Auto-generated constructor stub
	}
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";

		if (val == 0) pom = "" + npcId;
		else pom = npcId + "-" + val;

		return HtmlPathService.JAIL_MANAGER_HTML_PATH + pom + ".htm";
	}
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if(command.equalsIgnoreCase("start_mission") && Config.ALLOW_JAIL_MANAGER)
		{
			//informs player what he have to do to leave jail.
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(HtmlPathService.JAIL_MANAGER_HTML_PATH +"mission.htm");
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%amount%", String.valueOf(Config.REQUIRED_JAIL_POINTS));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);

			//dont allows a dead or fake death player to perform actions
			if(player.isDead() || player.isFakeDeath())
			{
				player.sendMessage("Cant leave jail while dead or fake death.");
				return;
			}
		}
		else if(command.equalsIgnoreCase("check_points") && player.isInJailMission())
		{
			//informs player currently ammount of points
			NpcHtmlMessage html2 = new NpcHtmlMessage(getObjectId());
			html2.setFile(HtmlPathService.JAIL_MANAGER_HTML_PATH +"points.htm");
			html2.replace("%objectId%", String.valueOf(getObjectId()));
			html2.replace("%points%", String.valueOf(player.getJailPoints()));
			html2.replace("%rest%", String.valueOf(Config.REQUIRED_JAIL_POINTS - player.getJailPoints()));
			html2.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html2);

			//dont allows a dead or fake death player to perform actions
			if(player.isDead() || player.isFakeDeath())
			{
				player.sendMessage("Cant check points while dead or fake death.");
				return;
			}
		}
		else if(command.equalsIgnoreCase("get_mission"))
		{
			if(player.isInJailMission())
			{
				WindowService.sendWindow(player, HtmlPathService.JAIL_MANAGER_HTML_PATH, "mission_already.htm");
				return;
			}
			
			//sets jail mission state
			player.setIsInJailMission(true);

			//plays a sound for mission start
			PlaySound ps = new PlaySound(0, "ItemSound2.race_start", 0, player.getObjectId(), player.getX(), player.getY(), player.getZ());
			player.sendPacket(ps);

			//sends a informative window
			WindowService.sendWindow(player, HtmlPathService.JAIL_MANAGER_HTML_PATH,"started.htm");

			//dont allows a dead or fake death player to perform actions
			if(player.isDead() || player.isFakeDeath())
			{
				player.sendMessage("Cant get mission while dead or fake death.");
				return;
			}
		}
		else if (command.equalsIgnoreCase("finish_mission"))
		{
			//informs player that he has not even started a mission if not started mission
			if(!player.isInJailMission())
			{
				WindowService.sendWindow(player, HtmlPathService.JAIL_MANAGER_HTML_PATH ,"notstarted.htm");
				return;
			}
			
			//checks if player has enougth jail points
			if(player.getJailPoints()< Config.REQUIRED_JAIL_POINTS)
			{
				if(Config.DEVELOPER){
				_log.info("JailManager: Required points: "+Config.REQUIRED_JAIL_POINTS);
				_log.info("JailManager: Player points: " +player.getJailPoints());
				}
				//informs player that mission is not complete
				WindowService.sendWindow(player, HtmlPathService.JAIL_MANAGER_HTML_PATH ,"notcompleted.htm");

				//notify gms if enabled
				if(Config.NOTIY_ADMINS_OF_ILLEGAL_ACTION)
					GmListTable.broadcastMessageToGMs("Player "+player.getName()+" is trying to leave Jail Withour Required Jail Points!");
				return;
			}

			//dont allows a dead or fake death player to perform actions
			if(player.isDead() || player.isFakeDeath())
			{
				player.sendMessage("Cant finish mission while dead or fake death.");
				return;
			}

			//plays a mission end sound
			PlaySound ps = new PlaySound(0, "ItemSound.quest_finish", 0, player.getObjectId(), player.getX(), player.getY(), player.getZ());
			player.sendPacket(ps);

			//informs player that mission is complete
			WindowService.sendWindow(player, HtmlPathService.JAIL_MANAGER_HTML_PATH ,"completed.htm");

			//sleeps thread a lil just to player read stuff and hear sound :D
			ThreadService.processSleep(5);

			//removes player from jail
			player.setInJail(false, 0);

			//resets player jail points to 0
			player.resetJailPoints();
			player.sendMessage("Your jail points has been reseted.");

			//resets jail mission state
			player.setIsInJailMission(false);
		}
		super.onBypassFeedback(player, command);
	}
}