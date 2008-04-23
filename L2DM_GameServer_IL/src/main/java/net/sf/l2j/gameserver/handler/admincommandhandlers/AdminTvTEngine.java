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
 * @rewritten and Fixed by Rayan for L2EmuProject rev 2.0.2
 *
 */

package net.sf.l2j.gameserver.handler.admincommandhandlers;

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

public class AdminTvTEngine implements IAdminCommandHandler {

 private static final String[][] ADMIN_COMMANDS = 
		//*********** GENERAL COMMANDS ***********************************************/
 		{{"admin_tvt",
 			"Opens Main Event Configuration Panel",
 			"Usage: //tvt",
 		},
 		
 		{"admin_tvt_name",
 			"Sets the Currently TvT Event Name",
 			"Usage: //tvt_name",
 		},
 		
 		{"admin_tvt_desc",
 			 "A General Description of the Event.",
 			 "Usage: //tvt_desc",
		},
 			
		{"admin_tvt_join_loc",
			 "Decribe the location of Event",
			 "Usage: //tvt_join_loc",
		},
 		
		{"admin_tvt_minlvl",
			 "Set the Min level to Participate in Event",
			 "Usage: //tvt_minlvl",
		},
		
		{"admin_tvt_maxlvl",
			 "The Maximum Level to Participate in TvT",
			 "usage: //tvt_maxlvl",
		},
                                           
		{"admin_tvt_npc",
			 "Set the ID of Npc That Will Manage TvT",
			 "Usage: //tvt_npc",
		},
		
		{"admin_tvt_npc_pos",
			 "set the coords that TvT Npc Manager Will Spawn",
			 "Usage: //tvt_npc_pos",
		},

		{"admin_tvt_reward", 
			 "Set the reward ID that players will receive Per win TvT",
			 "Usage: //tvt_reward",
		}, 
		
		{"admin_tvt_reward_amount",
			 "Set the reward amount that winners of TvT Will Receive",
			 "Usage: //tvt_reward_amount",
		},

		{"admin_tvt_team_add",
			 "Registers a Team on TvT",
			 "Usage: //tvt_team_add <TeamName>",
		}, 
		
		{"admin_tvt_team_remove",
			 "removes a Team From TvT",
			 "Usage: //tvt_team_remove",
		}, 
		
		{"admin_tvt_team_pos",
			 "sets teams position",
			 "Usage: //tvt_team_pos",
		}, 
		
		{"admin_tvt_team_color",
			 "sets teams color",
			 "Usage: //tvt_team_color",
		},
            
		{"admin_tvt_join",
		     "starts the TvT joining time (manual event).",
		     "Usage: //tvt_join",
		}, 
		
		{"admin_tvt_teleport",
		     "teleport players to team spots locations (manual event).",
			 "Usage: //tvt_teleport",
		}, 
		
		{"admin_tvt_start",
			 "starts the event (manual event).",
			 "Usage: //tvt_start",
		},
		
		{"admin_tvt_abort",
			 "aborts a manual.",
			 "Usage: //admin_tvt_abort",
		}, 
		
		{"admin_tvt_finish",
			 "finishes an manual event.",
			 "Usage: //tvt_finish",
		},
		
		{"admin_tvt_sit", 
			 "sits players in event. (manual event)",
			 "Usage: //tvt_sit",
		}, 
		
		{"admin_tvt_dump",
		     "print general info about event in console.",
			 "Usage: //tvt_dump",
		}, 
		
		{"admin_tvt_save", 
			 "saves event data.",
			 "Usage: //tvt_save",
		}, 
		
		{"admin_tvt_load", 
			 "loads latest safe event data.",
			 "Usage: //tvt_load",
		}, 
		
		{"admin_tvt_jointime", 
		     "set the period before of joining till event start.",
			 "Usage: //tvt_jointime",
		},
		
		{"admin_tvt_eventtime",
			 "the length that this event will last for.",
			 "Usage: //tvt_eventtime",
		}, 
		
		{"admin_tvt_autoevent",
			 "launches a automatic event.",
			 "Usage: //tvt_autoevent",
		},
		
		{"admin_tvt_minplayers",
			 "set the minimum player for an TvT Match.",
			 "Usage: //tvt_minplayers",
		},
		
		{"admin_tvt_maxplayers",
			 "set the maximum number of participants for a TvT Match.",
			 "Usage: //tvt_maxplayers",
		},
		
		{"admin_tvt_auto_interval",
			 "define the interval between one interval and other (auto event).",
			 "Usage: //tvt_auto_interval",
		}};
 
 private static final int REQUIRED_LEVEL = Config.GM_FUN_ENGINE;

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM())) return false;
        
        if (command.equals("admin_tvt"))
            showMainPage(activeChar);
        else if (command.startsWith("admin_tvt_name "))
        {
            TvT._eventName = command.substring(15);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_desc "))
        {
            TvT._eventDesc = command.substring(15);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_minlvl "))
        {
            if (!TvT.checkMinLevel(Integer.valueOf(command.substring(17))))
                return false;
            TvT._minlvl = Integer.valueOf(command.substring(17));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_maxlvl "))
        {
            if (!TvT.checkMaxLevel(Integer.valueOf(command.substring(17))))
                return false;
            TvT._maxlvl = Integer.valueOf(command.substring(17));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_minplayers "))
        {
            TvT._minPlayers = Integer.valueOf(command.substring(21));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_maxplayers "))
        {
            TvT._maxPlayers = Integer.valueOf(command.substring(21));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_join_loc "))
        {
            TvT._joiningLocationName = command.substring(19);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_npc "))
        {
            TvT._npcId = Integer.valueOf(command.substring(14));
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_npc_pos"))
        {
            TvT.setNpcPos(activeChar);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_reward "))
        {
            TvT._rewardId = Integer.valueOf(command.substring(17));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_reward_amount "))
        {
            TvT._rewardAmount = Integer.valueOf(command.substring(24));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_jointime "))
        {
            TvT._joinTime = Integer.valueOf(command.substring(19));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_eventtime "))
        {
            TvT._eventTime = Integer.valueOf(command.substring(20));
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_team_add "))
        {
            String teamName = command.substring(19);
            
            TvT.addTeam(teamName);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_team_remove "))
        {
            String teamName = command.substring(22);

            TvT.removeTeam(teamName);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_team_pos "))
        {
            String teamName = command.substring(19);

            TvT.setTeamPos(teamName, activeChar);
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_tvt_team_color "))
        {
            String[] params;

            params = command.split(" ");
            
            if (params.length != 3)
            {
                activeChar.sendMessage("Wrong usege: //tvt_team_color <colorHex> <teamName>");
                return false;
            }

            TvT.setTeamColor(command.substring(params[0].length()+params[1].length()+2), Integer.decode("0x" + params[1]));
            showMainPage(activeChar);
        }
        else if(command.equals("admin_tvt_join"))
        {
        	TvT.startJoin(activeChar);
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_teleport"))
        {
            TvT.teleportStart();
            showMainPage(activeChar);
        }
        else if(command.equals("admin_tvt_start"))
        {
        	TvT.startEvent(activeChar);
            showMainPage(activeChar);
        }
        else if(command.equals("admin_tvt_abort"))
        {
            activeChar.sendMessage("Aborting event");
            TvT.abortEvent();
            showMainPage(activeChar);
        }
        else if(command.equals("admin_tvt_finish"))
        {
            TvT.finishEvent();
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_sit"))
        {
            TvT.sit();
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_load"))
        {
            TvT.loadData();
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_autoevent"))
        {
        	if(TvT._joinTime>0 && TvT._eventTime>0)
        		TvT.GMinit();
        	else
        		activeChar.sendMessage("Wrong usege: join time or event time invallid.");
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_save"))
        {
            TvT.saveData();
            showMainPage(activeChar);
        }
        else if (command.equals("admin_tvt_dump"))
            TvT.dumpData();
        else if (command.startsWith("admin_tvt_auto_interval "))
        {
        	TvT._IntervalBetweenMatchs = Integer.valueOf(command.substring(30))*60000;//auto convert from mins to milis
            showMainPage(activeChar);
        }

        return true;
    }

    public String[] getAdminCommandList()
    {
		String[] ADMIN_COMMANDSOnly = new String[ADMIN_COMMANDS.length];
		for (int i=0; i < ADMIN_COMMANDS.length; i++)
		{
			ADMIN_COMMANDSOnly[i]= ADMIN_COMMANDS[i][0];
		}
		return ADMIN_COMMANDSOnly;
    }

    private boolean checkLevel(int level) 
    {
        return (level >= REQUIRED_LEVEL);
    }

    public void showMainPage(L2PcInstance activeChar)
    {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        TextBuilder replyMSG = new TextBuilder("<html><body>");
        
        replyMSG.append("<center><font color=\"LEVEL\">[TvT Engine]</font></center><br><br><br>");
        replyMSG.append("<table><tr><td><edit var=\"input1\" width=\"125\"></td><td><edit var=\"input2\" width=\"125\"></td></tr></table>");
        replyMSG.append("<table border=\"0\"><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Name\" action=\"bypass -h admin_tvt_name $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Description\" action=\"bypass -h admin_tvt_desc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Join Location\" action=\"bypass -h admin_tvt_join_loc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Max lvl\" action=\"bypass -h admin_tvt_maxlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Min lvl\" action=\"bypass -h admin_tvt_minlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Max players\" action=\"bypass -h admin_tvt_maxplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Min players\" action=\"bypass -h admin_tvt_minplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"NPC\" action=\"bypass -h admin_tvt_npc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"NPC Pos\" action=\"bypass -h admin_tvt_npc_pos\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Reward\" action=\"bypass -h admin_tvt_reward $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Reward Amount\" action=\"bypass -h admin_tvt_reward_amount $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Join Time\" action=\"bypass -h admin_tvt_jointime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Event Time\" action=\"bypass -h admin_tvt_eventtime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Add\" action=\"bypass -h admin_tvt_team_add $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Color\" action=\"bypass -h admin_tvt_team_color $input1 $input2\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Pos\" action=\"bypass -h admin_tvt_team_pos $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Remove\" action=\"bypass -h admin_tvt_team_remove $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Join\" action=\"bypass -h admin_tvt_join\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Teleport\" action=\"bypass -h admin_tvt_teleport\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Start\" action=\"bypass -h admin_tvt_start\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Abort\" action=\"bypass -h admin_tvt_abort\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Finish\" action=\"bypass -h admin_tvt_finish\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Sit Force\" action=\"bypass -h admin_tvt_sit\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Dump\" action=\"bypass -h admin_tvt_dump\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Save\" action=\"bypass -h admin_tvt_save\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Load\" action=\"bypass -h admin_tvt_load\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Auto Event\" action=\"bypass -h admin_tvt_autoevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Set Interval\" action=\"bypass -h admin_tvt_auto_event_interval $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><br>");
        replyMSG.append("Current event...<br1>");
        replyMSG.append("    ... name:&nbsp;<font color=\"00FF00\">" + TvT._eventName + "</font><br1>");
        replyMSG.append("    ... description:&nbsp;<font color=\"00FF00\">" + TvT._eventDesc + "</font><br1>");
        replyMSG.append("    ... joining location name:&nbsp;<font color=\"00FF00\">" + TvT._joiningLocationName + "</font><br1>");
        replyMSG.append("    ... joining NPC ID:&nbsp;<font color=\"00FF00\">" + TvT._npcId + " on pos " + TvT._npcX + "," + TvT._npcY + "," + TvT._npcZ + "</font><br1>");
        replyMSG.append("    ... reward ID:&nbsp;<font color=\"00FF00\">" + TvT._rewardId + "</font><br1>");
        replyMSG.append("    ... reward Amount:&nbsp;<font color=\"00FF00\">" + TvT._rewardAmount + "</font><br><br>");
        replyMSG.append("    ... Min lvl:&nbsp;<font color=\"00FF00\">" + TvT._minlvl + "</font><br>");
        replyMSG.append("    ... Max lvl:&nbsp;<font color=\"00FF00\">" + TvT._maxlvl + "</font><br><br>");
        replyMSG.append("    ... Min Players:&nbsp;<font color=\"00FF00\">" + TvT._minPlayers + "</font><br>");
        replyMSG.append("    ... Max Players:&nbsp;<font color=\"00FF00\">" + TvT._maxPlayers + "</font><br><br>");
        replyMSG.append("    ... Joining Time:&nbsp;<font color=\"00FF00\">" + TvT._joinTime + "</font><br>");
        replyMSG.append("    ... Event Timer:&nbsp;<font color=\"00FF00\">" + TvT._eventTime + "</font><br><br>");
        replyMSG.append("Current teams:<br1>");
        replyMSG.append("<center><table border=\"0\">");
        
        for (String team : TvT._teams)
        {
            replyMSG.append("<tr><td width=\"100\"><font color=\"LEVEL\">" + team + "</font>");

            if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
                replyMSG.append("&nbsp;(" + TvT.teamPlayersCount(team) + " joined)");
            else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
            {
                if (TvT._teleport || TvT._started)
                    replyMSG.append("&nbsp;(" + TvT.teamPlayersCount(team) + " in)");
            }

            replyMSG.append("</td></tr><tr><td>");
            replyMSG.append(TvT._teamColors.get(TvT._teams.indexOf(team)));
            replyMSG.append("</td></tr><tr><td>");
            replyMSG.append(TvT._teamsX.get(TvT._teams.indexOf(team)) + ", " + TvT._teamsY.get(TvT._teams.indexOf(team)) + ", " + TvT._teamsZ.get(TvT._teams.indexOf(team)));
            replyMSG.append("</td></tr><tr><td width=\"60\"><button value=\"Remove\" action=\"bypass -h admin_tvt_team_remove " + team + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
        }
        
        replyMSG.append("</table></center>");
        
        if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
        {
            if (!TvT._started)
            {
                replyMSG.append("<br1>");
                replyMSG.append(TvT._playersShuffle.size() + " players participating. Waiting to shuffle in teams(done on teleport)!");
                replyMSG.append("<br><br>");
            }
        }

        replyMSG.append("</body></html>");
        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply); 
    }
}


/**
 *
 * @author: FBIagent / fixed by SqueezeD
 *
 */

/*package net.sf.l2j.gameserver.handler.admincommandhandlers;

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

/**
 * 
 * @author FBIAgent
 * @rewritten and Fixed by Rayan for L2EmuProject rev 2.0.2
 */
/*public class AdminTvTEngine implements IAdminCommandHandler {

	private static String[][] ADMIN_COMMANDS = 
	
	//*********** GENERAL COMMANDS ***********************************************/
	/*{{"admin_tvt",
		"Opens Main Event Configuration Panel",
		"Usage: //tvt",
	},
	                                       
	{"admin_tvt_event_name",
	 "Sets the Currently TvT Event Name",
	 "Usage: //tvt_event_name",
	},
	
	{"admin_tvt_event_desc",
	 "A General Description of the Event.",
	 "Usage: //tvt_event_desc",
	},
	
	{"admin_tvt_event_minlvl",
	 "Set the Min level to Participate in Event",
	 "Usage: //tvt_event_minlvl",
	},
	
	{"admin_tvt_event_maxlvl",
	 "The Maximum Level to Participate in TvT",
	 "usage: //tvt_maxlvl",
	},
	{"admin_tvt_minplayers",
	 "set the minimum player for an TvT Match.",
	 "Usage: //tvt_minplayers",
	},
	{"admin_tvt_maxplayers",
	 "set the maximum number of participants for a TvT Match.",
	 "Usage: //tvt_maxplayers",
	},
	{"admin_tvt_event_join_loc",
		 "Decribe the location of Event",
		 "Usage: //tvt_event_join_loc",
	},
	{"admin_tvt_npc",
	 "Set the ID of Npc That Will Manage TvT",
	 "Usage: //tvt_npc",
	},
	
	{"admin_tvt_npc_pos",
	 "set the coords that TvT Npc Manager Will Spawn",
	 "Usage: //tvt_npc_pos",
	},
	
	{"admin_tvt_reward", 
	 "Set the reward ID that players will receive Per win TvT",
	 "Usage: //tvt_reward",
	},
	
	{"admin_tvt_reward_amount",
	 "Set the reward amount that winners of TvT Will Receive",
	 "Usage: //tvt_reward_amount",
	},
	{"admin_tvt_jointime", 
	     "set the period before of joining till event start.",
		 "Usage: //tvt_jointime",
	},
	{"admin_tvt_eventtime",
	 "the length that this event will last for.",
	 "Usage: //tvt_eventtime",
	},
	
	{"admin_tvt_team_add",
	 "Registers a Team on TvT",
	 "Usage: //tvt_team_add <TeamName>",
	},
	
	{"admin_tvt_team_remove",
	 "removes a Team From TvT",
	 "Usage: //tvt_team_remove",
	},
	
	{"admin_tvt_team_pos",
	 "sets teams position",
	 "Usage: //tvt_team_pos",
	},
	{"admin_tvt_team_color",
	 "sets teams color",
	 "Usage: //tvt_team_color",
	},

	{"admin_tvt_dump",
     "print general info about event in console.",
	 "Usage: //tvt_dump",
	},

	{"admin_tvt_save", 
	 "saves event data.",
	 "Usage: //tvt_save",
	},

	{"admin_tvt_load", 
	 "loads latest safe event data.",
	 "Usage: //tvt_load",
	},
     {"admin_tvt_event_clear",
	 "clear event console for new configuration.",
	 "Usage: //tvt_event_clear",
	 },
	 {"admin_tvt_add_player",
		 "clear event for new configuration.",
		 "Usage: //tvt_add_player",
	 },
	 {"admin_tvt_abort_manualevent",
		 "aborts a manual.",
		 "Usage: //admin_tvt_abort_manualevent",
	 },
	 {"admin_tvt_remove_allplayers",
		 "removes all participations in event.",
		 "Usage: //tvt_remove_allplayers",
	 },
	//************************* MANUAL EVENT COMMANDS ******************************************/
	/*{"admin_tvt_manual_join",
     "starts the TvT joining time (manual event).",
     "Usage: //tvt_manual_join",
	},
	{"admin_tvt_manual_teleport",
     "teleport players to team spots locations (manual event).",
	 "Usage: //tvt_manual_teleport",
	},
	
	{"admin_tvt_manualstart",
	 "starts the event (manual event).",
	 "Usage: //tvt_manualstart",
	},
	{"admin_tvt_finish_manualevent",
	 "finishes an manual event.",
	 "Usage: //tvt_finish_manualevent",
	},
	
	{"admin_tvt_manual_sit", 
	 "sits players in event. (manual event)",
	 "Usage: //tvt_abort_manual_sit",
	},
	//************************* AUTOMATIC EVENT COMMANDS ******************************************/
	/*{"admin_tvt_autoevent",
	 "launches a automatic event.",
	 "Usage: //tvt_autoevent",
	},
	
	{"admin_tvt_abort_autoevent",
	 "aborts an automatic event.",
	 "Usage: //tvt_abort_event",
	},
	
    {"admin_tvt_finish_autoevent",
	 "finishes an automatic event.",
	 "Usage: //tvt_dinish_autoevent",
	},
	
	{"admin_tvt_auto_event_interval",
	 "define the interval between one interval and other (auto event).",
	 "Usage: //tvt_auto_event_interval",
	}};
                                           //L2EMU_ADD_END
 
 private static final int REQUIRED_LEVEL = Config.GM_FUN_ENGINE;

    public boolean useAdminCommand(String command, L2PcInstance admin)
    {
        if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
        
        //************* GENERAL COMMANDS *******************************/
        /*if (command.equals("admin_tvt"))
            showMainPage(admin);
        else if (command.startsWith("admin_tvt_event_name "))
        {
            TvT._eventName = command.substring(21);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_event_desc "))
        {
            TvT._eventDesc = command.substring(21);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_event_minlvl "))
        {
            if (!TvT.checkMinLevel(Integer.valueOf(command.substring(23))))
                return false;
            TvT._minlvl = Integer.valueOf(command.substring(23));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_event_maxlvl "))
        {
            if (!TvT.checkMaxLevel(Integer.valueOf(command.substring(23))))
                return false;
            TvT._maxlvl = Integer.valueOf(command.substring(23));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_minplayers "))
        {
            TvT._minPlayers = Integer.valueOf(command.substring(21));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_maxplayers "))
        {
            TvT._maxPlayers = Integer.valueOf(command.substring(21));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_event_join_loc "))
        {
            TvT._joiningLocationName = command.substring(25);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_npc "))
        {
            TvT._npcId = Integer.valueOf(command.substring(14));
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_npc_pos"))
        {
            TvT.setNpcPos(admin);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_reward "))
        {
            TvT._rewardId = Integer.valueOf(command.substring(17));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_reward_amount "))
        {
            TvT._rewardAmount = Integer.valueOf(command.substring(24));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_jointime "))
        {
            TvT._joinTime = Integer.valueOf(command.substring(19));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_eventtime "))
        {
            TvT._eventTime = Integer.valueOf(command.substring(20));
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_team_add "))
        {
            String teamName = command.substring(19);
            
            TvT.addTeam(teamName);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_team_remove "))
        {
            String teamName = command.substring(22);

            TvT.removeTeam(teamName);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_team_pos "))
        {
            String teamName = command.substring(19);

            TvT.setTeamPos(teamName, admin);
            showMainPage(admin);
        }
        else if (command.startsWith("admin_tvt_team_color "))
        {
            String[] params;

            params = command.split(" ");
            
            if (params.length != 3)
            {
                admin.sendMessage("Wrong usege: //tvt_team_color <colorHex> <teamName>");
                return false;
            }

            TvT.setTeamColor(command.substring(params[0].length()+params[1].length()+2), Integer.decode("0x" + params[1]));
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_manual_sit"))
        {
            TvT.sit();
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_load"))
        {
            TvT.loadData();
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_save"))
        {
            TvT.saveData();
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_dump"))
        {
         TvT.dumpData();
         admin.sendMessage("dumping data on console please check it there.");
         showMainPage(admin);
        }
        //************************* MANUAL COMMANDS *******************/
        /*else if(command.equals("admin_tvt_manual_join"))
        {
        	TvT.ManualEvent(admin);
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_manual_teleport"))
        {
            TvT.teleportToManualEvent();
            showMainPage(admin);
        }
        else if(command.equals("admin_tvt_manualstart"))
        {
        	TvT.startManualEvent(admin);
            showMainPage(admin);
        }
        else if(command.equals("admin_tvt_abort_manualevent"))
        {
            if(TvT._EventType==2)
            {
            admin.sendMessage("an auto event is running, cannot perform a manual abort.");
            return false;
            }
            TvT.abortManualEvent();
            showMainPage(admin);
        }
       
        else if(command.equals("admin_tvt_finish_manualevent"))
        {
        	if(TvT._EventType==2)
        	{
                admin.sendMessage("a manual event is running, cannot perform this operation.");
                return false;
            }
            TvT.finishManualEvent();
            showMainPage(admin);
        }
        else if(command.equals("admin_tvt_add_player"))
        {
        	 int endOfId = command.indexOf('_', 5);
             String id;
             if (endOfId > 0)
                 id = command.substring(4, endOfId);
             else
                 id = command.substring(4);
             
        	String teamName = command.substring(endOfId+1).substring(16);
        if (TvT._joining)
            TvT.addPlayer(admin, teamName);
        else
            admin.sendMessage("The event is already started. You can not join now!");
        }
        //************* AUTO EVENT COMMANDS ***************************************/
        /*else if (command.startsWith("admin_tvt_auto_event_interval "))
        {
        	TvT._IntervalBetweenMatchs = Integer.valueOf(command.substring(30))*60000;//auto convert from mins to milis
            showMainPage(admin);
        }
        else if(command.equals("admin_tvt_finish_autoevent"))
        {
        	if(TvT._EventType==1)
        	{
                admin.sendMessage("a manual event is running, cannot perform this operation.");
                return false;
            }
            TvT.finishAutoEvent();
            showMainPage(admin);
        }
        else if(command.equals("admin_tvt_abort_autoevent"))
        {
            if(TvT._EventType==1)
            {
            admin.sendMessage("a manual event is running, cannot perform an auto event abort.");
            return false;
        }
            TvT.abortAutoEvent();
            showMainPage(admin);
        }
        else if (command.equals("admin_tvt_autoevent"))
        {
        	 if(TvT._restarted)
             {admin.sendMessage("event has already restarted, try abort.");return false;}
         if(TvT._started)
            {admin.sendMessage("event is already started!");return false;}
  		if(TvT._teleport)
  		    {admin.sendMessage("event is teleporting players!.");return false;}
  		if(TvT._joining)
  		    {admin.sendMessage("event in on join time!");return false;}
  		if(TvT._teams.size() < 2)
  		    {admin.sendMessage("not enougth teams for event!");return false;}
  		if(TvT. _eventName.equals(""))
  			{admin.sendMessage("event name is missing.");return false;}
  		if(TvT._joiningLocationName.equals(""))
  			{admin.sendMessage("joining location name of event is missing.");return false;}
  		if(TvT._eventDesc.equals(""))
  			{admin.sendMessage("event description is missing.");return false;}
  		if(TvT._npcId == 0)
  			{admin.sendMessage("the npc id is not valid.");return false;}
  		if(TvT._npcX == 0)
  			{admin.sendMessage("missing npc location coords.");return false;}
  		if(TvT._npcY == 0)
  			{admin.sendMessage("missing npc location coords.");return false;}
  		if(TvT._npcZ == 0)
  			{admin.sendMessage("missing npc location coords.");return false;}
  		if(TvT._rewardId == 0)
  			{admin.sendMessage("reward id is not valid");return false;}
  		if(TvT._rewardAmount == 0)
  			{admin.sendMessage("reward amount is not valid.");return false;}
  		if(TvT._teamsX.contains(0))
  			{admin.sendMessage("missing teams spawn location coords.");return false;}
  		if(TvT._teamsY.contains(0))
  			{admin.sendMessage("missing teams spawn location coords.");return false;}
  		if(TvT._teamsZ.contains(0))
  			{admin.sendMessage("missing teams spawn location coords.");return false;}
  		
        	if(TvT._joinTime>0 && TvT._eventTime>0)
        	{
        		TvT.autoEvent();
        	    showMainPage(admin);
        	}
        	else
        	{
        		admin.sendMessage("Wrong usege: join time or event time invallid.");
                showMainPage(admin);
        	}
        }
        return true;
    }

    public String[] getAdminCommandList()
	{
		String[] ADMIN_COMMANDSOnly = new String[ADMIN_COMMANDS.length];
		for (int i=0; i < ADMIN_COMMANDS.length; i++)
		{
			ADMIN_COMMANDSOnly[i]= ADMIN_COMMANDS[i][0];
		}
		return ADMIN_COMMANDSOnly;
	}

    private boolean checkLevel(int level) 
    {
        return (level >= REQUIRED_LEVEL);
    }

    public void showMainPage(L2PcInstance admin)
    {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        TextBuilder replyMSG = new TextBuilder("<html><body>");
        
        replyMSG.append("<center><font color=\"LEVEL\">[L2EmuProject TvT Engine]</font></center><br><br><br>");
        replyMSG.append("<table><tr><td><edit var=\"input1\" width=\"125\"></td><td><edit var=\"input2\" width=\"125\"></td></tr></table><br>");
        replyMSG.append("<center><font color=\"LEVEL\">[General Settings]</font></center><br><br>");
        replyMSG.append("<table border=\"0\"><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Name\" action=\"bypass -h admin_tvt_name $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Description\" action=\"bypass -h admin_tvt_desc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Join Location\" action=\"bypass -h admin_tvt_join_loc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Max lvl\" action=\"bypass -h admin_tvt_maxlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Min lvl\" action=\"bypass -h admin_tvt_minlvl $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Max players\" action=\"bypass -h admin_tvt_maxplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Min players\" action=\"bypass -h admin_tvt_minplayers $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"NPC\" action=\"bypass -h admin_tvt_npc $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"NPC Pos\" action=\"bypass -h admin_tvt_npc_pos\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Reward\" action=\"bypass -h admin_tvt_reward $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Reward Amount\" action=\"bypass -h admin_tvt_reward_amount $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Join Time\" action=\"bypass -h admin_tvt_jointime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Event Time\" action=\"bypass -h admin_tvt_eventtime $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Add\" action=\"bypass -h admin_tvt_team_add $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Color\" action=\"bypass -h admin_tvt_team_color $input1 $input2\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Pos\" action=\"bypass -h admin_tvt_team_pos $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Team Remove\" action=\"bypass -h admin_tvt_team_remove $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Reset\" action=\"bypass -h admin_tvt_event_clear\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br>");
        replyMSG.append("<table border=\"0\"><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Save\" action=\"bypass -h admin_tvt_save\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Load\" action=\"bypass -h admin_tvt_load\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Dump\" action=\"bypass -h admin_tvt_dump\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br>");
        replyMSG.append("<center><font color=\"LEVEL\">[Manual Event Settings]</font></center><br><br>");
        replyMSG.append("<table><tr><td width=\"100\"><button value=\"Join\" action=\"bypass -h admin_tvt_manual_join\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Teleport\" action=\"bypass -h admin_tvt_manual_teleport\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Start\" action=\"bypass -h admin_tvt_manualstart\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Abort\" action=\"bypass -h admin_tvt_abort_manualevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Finish\" action=\"bypass -h admin_tvt_finish_manualevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><table><tr>");
        replyMSG.append("<td width=\"100\"><button value=\"Sit Force\" action=\"bypass -h admin_tvt_manual_sit\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><br>");
        replyMSG.append("<center><font color=\"LEVEL\">[Automatic Event Settings]</font></center><br><br>");
        replyMSG.append("<table><tr><td width=\"100\"><button value=\"Finish\" action=\"bypass -h admin_tvt_finish_autoevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Run\" action=\"bypass -h admin_tvt_autoevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<td width=\"100\"><button value=\"Set Interval\" action=\"bypass -h admin_tvt_auto_event_interval $input1\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("</tr></table><br><br>");
        replyMSG.append("<td width=\"100\"><button value=\"Abort\" action=\"bypass -h admin_tvt_abort_autoevent\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
        replyMSG.append("<br>");
        replyMSG.append("Current event...<br1>");
        replyMSG.append("    ... Event Type:&nbsp;<font color=\"00FF00\">" +TvT.getEventTypeByName(TvT._EventType)+ "</font><br1>");
        replyMSG.append("    ... name:&nbsp;<font color=\"00FF00\">" + TvT._eventName + "</font><br1>");
        replyMSG.append("    ... description:&nbsp;<font color=\"00FF00\">" + TvT._eventDesc + "</font><br1>");
        replyMSG.append("    ... joining location name:&nbsp;<font color=\"00FF00\">" + TvT._joiningLocationName + "</font><br1>");
        replyMSG.append("    ... joining NPC ID:&nbsp;<font color=\"00FF00\">" + TvT._npcId + " on pos " + TvT._npcX + "," + TvT._npcY + "," + TvT._npcZ + "</font><br1>");
        replyMSG.append("    ... reward ID:&nbsp;<font color=\"00FF00\">" + TvT._rewardId + "</font><br1>");
        replyMSG.append("    ... reward Amount:&nbsp;<font color=\"00FF00\">" + TvT._rewardAmount + "</font><br><br>");
        replyMSG.append("    ... Min lvl:&nbsp;<font color=\"00FF00\">" + TvT._minlvl + "</font><br>");
        replyMSG.append("    ... Max lvl:&nbsp;<font color=\"00FF00\">" + TvT._maxlvl + "</font><br><br>");
        replyMSG.append("    ... Min Players:&nbsp;<font color=\"00FF00\">" + TvT._minPlayers + "</font><br>");
        replyMSG.append("    ... Max Players:&nbsp;<font color=\"00FF00\">" + TvT._maxPlayers + "</font><br><br>");
        replyMSG.append("    ... Joining Time:&nbsp;<font color=\"00FF00\">" + TvT._joinTime + "</font><br>");
        replyMSG.append("    ... Event Timer:&nbsp;<font color=\"00FF00\">" + TvT._eventTime + "</font><br><br>");
        replyMSG.append("    ... Events Starts Every:&nbsp;<font color=\"00FF00\">" +TvT.getIntervalBetweenMatchs()+" minutes.</font><br><br>");
        replyMSG.append("Current teams:<br1>");
        replyMSG.append("<center><table border=\"0\">");
        
        for (String team : TvT._teams)
        {
            replyMSG.append("<tr><td width=\"100\"><font color=\"LEVEL\">" + team + "</font>");

            if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
                replyMSG.append("&nbsp;(" + TvT.teamPlayersCount(team) + " joined)");
            else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
            {
                if (TvT._teleport || TvT._started)
                    replyMSG.append("&nbsp;(" + TvT.teamPlayersCount(team) + " in)");
            }

            replyMSG.append("</td></tr><tr><td>");
            replyMSG.append(TvT._teamColors.get(TvT._teams.indexOf(team)));
            replyMSG.append("</td></tr><tr><td>");
            replyMSG.append(TvT._teamsX.get(TvT._teams.indexOf(team)) + ", " + TvT._teamsY.get(TvT._teams.indexOf(team)) + ", " + TvT._teamsZ.get(TvT._teams.indexOf(team)));
            replyMSG.append("</td></tr><tr><td width=\"60\"><button value=\"Remove\" action=\"bypass -h admin_tvt_team_remove " + team + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
        }
        
        replyMSG.append("</table></center>");
        
        if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
        {
            if (!TvT._started)
            {
                replyMSG.append("<br1>");
                replyMSG.append(TvT._playersShuffle.size() + " players participating. Waiting to shuffle in teams(done on teleport)!");
                replyMSG.append("<br><br>");
            }
        }

        replyMSG.append("</body></html>");
        adminReply.setHtml(replyMSG.toString());
        admin.sendPacket(adminReply); 
    }
 
}*/
