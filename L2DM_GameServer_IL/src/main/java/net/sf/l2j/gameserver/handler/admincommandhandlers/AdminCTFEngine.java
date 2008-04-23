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

/**
 *
 * @author: FBIagent / fixed by SqueezeD
 *
 */

package net.sf.l2j.gameserver.handler.admincommandhandlers;

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.CTF;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

public class AdminCTFEngine implements IAdminCommandHandler {

 private static final String[] ADMIN_COMMANDS = {"admin_ctf",
                                           "admin_ctf_name", "admin_ctf_desc", "admin_ctf_join_loc",
                                           "admin_ctf_npc", "admin_ctf_npc_pos",
                                           "admin_ctf_reward", "admin_ctf_reward_amount",
                                           "admin_ctf_team_add", "admin_ctf_team_remove", "admin_ctf_team_pos", "admin_ctf_team_color", "admin_ctf_team_flag",
                                           "admin_ctf_join", "admin_ctf_teleport", "admin_ctf_start", "admin_ctf_abort", "admin_ctf_finish",
                                           "admin_ctf_sit","admin_ctf_minlvl","admin_ctf_maxlvl",
                                           "admin_ctf_dump", "admin_ctf_save", "admin_ctf_load"};
 //L2EMU_EDIT_BEGIN
 private static final int REQUIRED_LEVEL = Config.GM_FUN_ENGINE;
 //L2EMU_EDIT_END
 public boolean useAdminCommand(String command, L2PcInstance activeChar)
 {
     try{
 	if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM())) return false;
     
     if (command.equals("admin_ctf"))
         showMainPage(activeChar);
     else if (command.startsWith("admin_ctf_name "))
     {
         CTF._eventName = command.substring(15);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_desc "))
     {
         CTF._eventDesc = command.substring(15);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_minlvl "))
     {
         if (!CTF.checkMinLevel(Integer.valueOf(command.substring(17))))
             return false;
         CTF._minlvl = Integer.valueOf(command.substring(17));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_team_flag "))
     {
         String[] params;

         params = command.split(" ");
         
         if (params.length != 2)
         {
             activeChar.sendMessage("Wrong usge: //ctf_team_flag <teamName>");
             return false;
         }

         CTF.setTeamFlag(params[1], activeChar);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_maxlvl "))
     {
         if (!CTF.checkMaxLevel(Integer.valueOf(command.substring(17))))
             return false;
         CTF._maxlvl = Integer.valueOf(command.substring(17));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_minplayers "))
     {
         CTF._minPlayers = Integer.valueOf(command.substring(21));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_maxplayers "))
     {
         CTF._maxPlayers = Integer.valueOf(command.substring(21));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_join_loc "))
     {
         CTF._joiningLocationName = command.substring(19);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_npc "))
     {
         CTF._npcId = Integer.valueOf(command.substring(14));
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_npc_pos"))
     {
         CTF.setNpcPos(activeChar);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_reward "))
     {
         CTF._rewardId = Integer.valueOf(command.substring(17));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_reward_amount "))
     {
         CTF._rewardAmount = Integer.valueOf(command.substring(24));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_jointime "))
     {
         CTF._joinTime = Integer.valueOf(command.substring(19));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_eventtime "))
     {
         CTF._eventTime = Integer.valueOf(command.substring(20));
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_team_add "))
     {
         String teamName = command.substring(19);
         
         CTF.addTeam(teamName);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_team_remove "))
     {
         String teamName = command.substring(22);


         CTF.removeTeam(teamName);
         showMainPage(activeChar);
     }















     else if (command.startsWith("admin_ctf_team_pos "))
     {
         String teamName = command.substring(19);

         CTF.setTeamPos(teamName, activeChar);
         showMainPage(activeChar);
     }
     else if (command.startsWith("admin_ctf_team_color "))
     {
         String[] params;

         params = command.split(" ");
         
         if (params.length != 3)
         {
             activeChar.sendMessage("Wrong usege: //ctf_team_color <colorHex> <teamName>");
             return false;
         }

         CTF.setTeamColor(command.substring(params[0].length()+params[1].length()+2), Integer.decode("0x" + params[1]));
         showMainPage(activeChar);
     }
     else if(command.equals("admin_ctf_join"))
     {
     	CTF.startJoin(activeChar);
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_teleport"))
     {
         CTF.teleportStart();
         showMainPage(activeChar);
     }
     else if(command.equals("admin_ctf_start"))
     {
     	CTF.startEvent(activeChar);
         showMainPage(activeChar);
     }
     else if(command.equals("admin_ctf_abort"))
     {
         activeChar.sendMessage("Aborting event");
         CTF.abortEvent();
         showMainPage(activeChar);
     }
     else if(command.equals("admin_ctf_finish"))
     {
         CTF.finishEvent();
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_sit"))
     {
         CTF.sit();
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_load"))
     {
         CTF.loadData();
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_autoevent"))
     {
     	if(CTF._joinTime>0 && CTF._eventTime>0)
     		CTF.autoEvent();
     	else
     		activeChar.sendMessage("Wrong usege: join time or event time invallid.");
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_save"))
     {
         CTF.saveData();
         showMainPage(activeChar);
     }
     else if (command.equals("admin_ctf_dump"))
         CTF.dumpData();

     return true;
     }catch(Throwable t){
     	activeChar.sendMessage("The command was not used correctly");
     	return false;
     }
 }

 public String[] getAdminCommandList()
 {
     return ADMIN_COMMANDS;
 }

 private boolean checkLevel(int level) 
 {
     return (level >= REQUIRED_LEVEL);
 }

 public void showMainPage(L2PcInstance activeChar)
 {
     NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
     TextBuilder replyMSG = new TextBuilder("<html><body>");
     
     replyMSG.append("<center><font color=\"LEVEL\">[CTF Engine]</font></center><br><br><br>");
     replyMSG.append("<table><tr><td><edit var=\"input1\" width=\"125\"></td><td><edit var=\"input2\" width=\"125\"></td></tr></table>");
     replyMSG.append("<table border=\"0\"><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Name\" action=\"bypass -h admin_ctf_name $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Description\" action=\"bypass -h admin_ctf_desc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Join Location\" action=\"bypass -h admin_ctf_join_loc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Max lvl\" action=\"bypass -h admin_ctf_maxlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Min lvl\" action=\"bypass -h admin_ctf_minlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Max players\" action=\"bypass -h admin_ctf_maxplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Min players\" action=\"bypass -h admin_ctf_minplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"NPC\" action=\"bypass -h admin_ctf_npc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"NPC Pos\" action=\"bypass -h admin_ctf_npc_pos\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Reward\" action=\"bypass -h admin_ctf_reward $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Reward Amount\" action=\"bypass -h admin_ctf_reward_amount $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Join Time\" action=\"bypass -h admin_ctf_jointime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Event Time\" action=\"bypass -h admin_ctf_eventtime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Team Add\" action=\"bypass -h admin_ctf_team_add $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Team Color\" action=\"bypass -h admin_ctf_team_color $input1 $input2\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Team Pos\" action=\"bypass -h admin_ctf_team_pos $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Team Flag\" action=\"bypass -h admin_ctf_team_flag $input1 $input2\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");


     replyMSG.append("<td width=\"100\"><button value=\"Team Remove\" action=\"bypass -h admin_ctf_team_remove $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Join\" action=\"bypass -h admin_ctf_join\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Teleport\" action=\"bypass -h admin_ctf_teleport\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Start\" action=\"bypass -h admin_ctf_start\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Abort\" action=\"bypass -h admin_ctf_abort\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Finish\" action=\"bypass -h admin_ctf_finish\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Sit Force\" action=\"bypass -h admin_ctf_sit\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Dump\" action=\"bypass -h admin_ctf_dump\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><br><table><tr>");
     replyMSG.append("<td width=\"100\"><button value=\"Save\" action=\"bypass -h admin_ctf_save\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Load\" action=\"bypass -h admin_ctf_load\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("<td width=\"100\"><button value=\"Auto Event\" action=\"bypass -h admin_ctf_autoevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
     replyMSG.append("</tr></table><br><br>");
     replyMSG.append("Current event...<br1>");
     replyMSG.append("    ... name:&nbsp;<font color=\"00FF00\">" + CTF._eventName + "</font><br1>");
     replyMSG.append("    ... description:&nbsp;<font color=\"00FF00\">" + CTF._eventDesc + "</font><br1>");
     replyMSG.append("    ... joining location name:&nbsp;<font color=\"00FF00\">" + CTF._joiningLocationName + "</font><br1>");
     replyMSG.append("    ... joining NPC ID:&nbsp;<font color=\"00FF00\">" + CTF._npcId + " on pos " + CTF._npcX + "," + CTF._npcY + "," + CTF._npcZ + "</font><br1>");
     replyMSG.append("    ... reward ID:&nbsp;<font color=\"00FF00\">" + CTF._rewardId + "</font><br1>");
     replyMSG.append("    ... reward Amount:&nbsp;<font color=\"00FF00\">" + CTF._rewardAmount + "</font><br><br>");
     replyMSG.append("    ... Min lvl:&nbsp;<font color=\"00FF00\">" + CTF._minlvl + "</font><br>");
     replyMSG.append("    ... Max lvl:&nbsp;<font color=\"00FF00\">" + CTF._maxlvl + "</font><br><br>");
     replyMSG.append("    ... Min Players:&nbsp;<font color=\"00FF00\">" + CTF._minPlayers + "</font><br>");
     replyMSG.append("    ... Max Players:&nbsp;<font color=\"00FF00\">" + CTF._maxPlayers + "</font><br><br>");
     replyMSG.append("    ... Joining Time:&nbsp;<font color=\"00FF00\">" + CTF._joinTime + "</font><br>");
     replyMSG.append("    ... Event Timer:&nbsp;<font color=\"00FF00\">" + CTF._eventTime + "</font><br><br>");
     replyMSG.append("Current teams:<br1>");
     replyMSG.append("<center><table border=\"0\">");
     
     for (String team : CTF._teams)
     {
         replyMSG.append("<tr><td width=\"100\"><font color=\"LEVEL\">" + team + "</font>");

         if (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE"))
             replyMSG.append("&nbsp;(" + CTF.teamPlayersCount(team) + " joined)");
         else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
         {
             if (CTF._teleport || CTF._started)
                 replyMSG.append("&nbsp;(" + CTF.teamPlayersCount(team) + " in)");
         }

         replyMSG.append("</td></tr><tr><td>");
         replyMSG.append(CTF._teamColors.get(CTF._teams.indexOf(team)));
         replyMSG.append("</td></tr><tr><td>");
         replyMSG.append(CTF._teamsX.get(CTF._teams.indexOf(team)) + ", " + CTF._teamsY.get(CTF._teams.indexOf(team)) + ", " + CTF._teamsZ.get(CTF._teams.indexOf(team)));
         replyMSG.append("</td></tr><tr><td>");
         replyMSG.append("Flag Id: " +CTF._flagIds.get(CTF._teams.indexOf(team)));
         replyMSG.append("</td></tr><tr><td>");


         replyMSG.append(CTF._flagsX.get(CTF._teams.indexOf(team)) + ", " + CTF._flagsY.get(CTF._teams.indexOf(team)) + ", " + CTF._flagsZ.get(CTF._teams.indexOf(team)));
         replyMSG.append("</td></tr><tr><td width=\"60\"><button value=\"Remove\" action=\"bypass -h admin_ctf_team_remove " + team + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
     }
     
     replyMSG.append("</table></center>");
     
     if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
     {
         if (!CTF._started)
         {
             replyMSG.append("<br1>");
             replyMSG.append(CTF._playersShuffle.size() + " players participating. Waiting to shuffle in teams(done on teleport)!");
             replyMSG.append("<br><br>");
         }
     }

     replyMSG.append("</body></html>");
     adminReply.setHtml(replyMSG.toString());
     activeChar.sendPacket(adminReply); 
 }
}