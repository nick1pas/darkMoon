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

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.entity.events.L2EventChecks;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * This Class manages all the Requests to join a Raid Event.
 *
 * @author  polbat02
 */
public class L2EventManagerInstance extends L2NpcInstance
{
	//Local Variables Definition
	//--------------------------
	/** Number of Current Events */
	public static int _currentEvents = 0;
	/** Players from which we're waiting an answer */
	public static Vector<L2PcInstance> _awaitingplayers = new Vector<L2PcInstance>();
	/** Players that will finally get inside the Event  */
	public static Vector<L2PcInstance> _finalPlayers = new Vector<L2PcInstance>();

	public L2EventManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

    @Override
	public void onBypassFeedback(L2PcInstance player, String command)
    {
        player.sendPacket(new ActionFailed());
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken();
        _finalPlayers = new Vector<L2PcInstance>();

        if (actualCommand.equalsIgnoreCase("iEvent"))
        {
        	try
        	{
        		/*Type:1- Single //2- Clan //3- Party*/
               	int type = Integer.parseInt(st.nextToken());
               	/*Required Event Points needed to participate*/
               	int eventPoints = Integer.parseInt(st.nextToken());
               	/* NpcId of the Event mobs */
               	int npcId = Integer.parseInt(st.nextToken());
               	/* Number of NPcs */
               	int npcAm =Integer.parseInt(st.nextToken());
               	/* Minimum number of needed persons players to participate */
               	int minPeople = Integer.parseInt(st.nextToken());
               	/* Minimum level to participate */
               	int minLevel = Integer.parseInt(st.nextToken());
               	/* Buff List to apply */
               	int bufflist = Integer.parseInt(st.nextToken());
               	/* Level of The Prize to Hand out */
               	int prizeLevel = Integer.parseInt(st.nextToken());
               	if (player == null){return;}
                this.setTarget(player);

                if (_currentEvents>=Config.RAID_SYSTEM_MAX_EVENTS)
                {
                	player.sendMessage("There's alredy "+_currentEvents+" events in progress. " +
                			"Wait untill one of them ends to get into another one.");
                	return;
                }

    			if (L2EventChecks.usualChecks(player,minLevel))
    				_finalPlayers.add(player);
    			else return;
    			// If the player has passed the checks, then continue.
    			switch (type)
    			{
    			// Case Clan Events.
    			case 2:
                    {
                    	if(player.getClan()== null)
                    	{
                    		player.sendMessage("You Don't have a Clan!");
                    		return;
                    	}
                    	L2PcInstance[] onlineclanMembers = player.getClan().getOnlineMembers("");
                    	for (L2PcInstance member: onlineclanMembers)
                    	{
                    		boolean eligible = true;
                    		if(member == null)
                    			continue;
                    		if(!L2EventChecks.usualChecks(member,minLevel))
                    			eligible = false;
                    		if(eligible && !(_finalPlayers.contains(member)))
                    			_finalPlayers.add(member);
                    	}
                    	if (_finalPlayers.size()>1 && _finalPlayers.size()>=minPeople)
                    	{
                    		player.setRaidParameters(player,type,eventPoints,npcId,npcAm,minPeople,bufflist,prizeLevel,this, _finalPlayers);
                    		_awaitingplayers.add(player);
                    		player.sendPacket(new ConfirmDlg(614," A total of "+(_finalPlayers.size())+" members of your "
                    				+" clan are Eligible for the event. Do you want to continue?"));
                    	}
                    	else
                    	{
                    		String reason;
                    		if(_finalPlayers.size()>1)
                    			reason =": Only 1 Clan Member Online.";
                    		else if(_finalPlayers.size()<minPeople)
                    			reason =": Not enough members online to participate.";
                    		else reason=".";
                    		player.sendMessage("Cannot participate"+reason);
                    	}
                    	break;
                    }
                // Case Party Events.
    			case 3:
    				{
    					if(player.getParty()== null)
                    	{
                    		player.sendMessage("You DON'T have a Party!");
                    		return;
                    	}
                    	List<L2PcInstance> partyMembers = player.getParty().getPartyMembers();
                    	for (L2PcInstance member: partyMembers)
                    	{
                    		boolean eligible = true;
                    		if(member == null)
                    			continue;
                    		if(!L2EventChecks.usualChecks(member,minLevel))
                    			eligible = false;
                    		if(eligible && !(_finalPlayers.contains(member)))
                    			_finalPlayers.add(member);
                    	}
                    	if ((_finalPlayers.size())>1 && _finalPlayers.size()>=minPeople)
                    	{
                    		player.setRaidParameters(player,type,eventPoints,npcId,npcAm,minPeople,bufflist,prizeLevel,this, _finalPlayers);
                    		_awaitingplayers.add(player);
                    		player.sendPacket(new ConfirmDlg(614," A total of "+(_finalPlayers.size())+" members of your " +
                    				"party are Eligible for the event. Do you want to continue?"));
                    	}
                    	else
                    	{
                    		String reason;
                    		if(_finalPlayers.size()>1)
                    			reason =": Only 1 Party Member.";
                    		else if(_finalPlayers.size()<minPeople)
                    			reason =": Not enough members to participate.";
                    		else reason=".";
                    		player.sendMessage("Cannot participate"+reason);
                    	}
    					break;
    				}

    			default:
    			{
            		player.setRaidParameters(player,type,eventPoints,npcId,npcAm,minPeople,bufflist,prizeLevel,this, _finalPlayers);
    				player.setRaidAnswear(1);
    			}
    		}
                return;

        	}
        	catch (Exception e)
        	{
        		_log.warn("L2EventManagerInstance: Error while getting html command");
        		e.printStackTrace();
        	}
        }
        super.onBypassFeedback(player, command);
    }

    @Override
	public String getHtmlPath(int npcId, int val)
    {
        String pom = "";
        if (val == 0)
        {
            pom = "" + npcId;
        }
        else
        {
            pom = npcId + "-" + val;
        }

        return HtmlPathService.RAIDEVENT_NPC_HTML_PATH + pom + ".htm";
    }

    public static boolean addEvent()
    {
    	if (_currentEvents>=Config.RAID_SYSTEM_MAX_EVENTS)
    		return false;
    	else
    	{
    		_currentEvents += 1;
    		return true;
    	}
    }

    public static boolean removeEvent()
    {
    	if(_currentEvents>0)
    	{
    		_currentEvents-=1;
    		return true;
    	}
    	else return false;
    }
}