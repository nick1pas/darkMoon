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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.GMViewPledgeInfo;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_pledge = opens pledge menu. <br>
 * <li> admin_pledge info = gets info about pledge. <br>
 * <li> admin_pledge dismiss = dismiss a pledge. <br>
 * <li> admin_pledge setlevel level = sets level of pledge. <br>
 * <li> admin_pledge rep reputation_points = set rep points to pledge. <br>
 * <li> admin_pledge create = creates a new clean.  (needs as target a character without clan) <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li>(With target in a clan leader) <br>
 * <li> //pledge create clanname <br>
 * <li> //pledge info <br>
 * <li> //pledge dismiss <br>
 * <li> //pledge setlevel level <br>
 * <li> //pledge rep reputation_points <br>
 */
public class AdminPledge implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_pledge"
	};
    private int REQUIRED_LEVEL = Config.GM_MIN;
    public boolean useAdminCommand(String command, L2PcInstance admin)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!admin.isGM() || admin.getAccessLevel() < Config.GM_ACCESSLEVEL || admin.getTarget() == null || !(admin.getTarget() instanceof L2PcInstance))
                return false;
        
        
        L2Object target = admin.getTarget();
        L2PcInstance player = null;
        if (target instanceof L2PcInstance)
            player = (L2PcInstance)target;
        else
        {
            admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
            return false;
        }
        String name = player.getName();
        if(command.startsWith("admin_pledge"))
        {
            String action = null;
            String parameter = null;
            StringTokenizer st = new StringTokenizer(command);
            try
            {
                st.nextToken();
                action = st.nextToken(); // create|info|dismiss|setlevel|rep
                parameter = st.nextToken(); // clanname|nothing|nothing|level|rep_points
            }
            catch (NoSuchElementException nse)
            {
            }
            if (action.equals("create"))
            {
                long cet = player.getClanCreateExpiryTime();
                player.setClanCreateExpiryTime(0);
                //L2EMU_ADD - prevents NPE
                if(parameter == null)
            		return false;
                //L2EMU_ADD
                L2Clan clan = ClanTable.getInstance().createClan(player, parameter);
                if (clan != null)
                    admin.sendMessage("Clan " + parameter + " created. Leader: " + name);
                else
                {
                    player.setClanCreateExpiryTime(cet);
                    admin.sendMessage("There was a problem while creating the clan.");
                }
            }
            else if (!player.isClanLeader())
            {
                admin.sendPacket(new SystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER).addString(name));
                return false;
            }
            else if(action.equals("dismiss"))
            {
                ClanTable.getInstance().destroyClan(player.getClanId());
                L2Clan clan = player.getClan();
                if (clan==null)
                    admin.sendMessage("Clan disbanded.");
                else
                    admin.sendMessage("There was a problem while destroying the clan.");
            }
            else if (parameter == null)
            {
                admin.sendMessage("Usage: //pledge <setlevel|rep> <number>");
            }
            else if (action.equals("info"))
            {
                admin.sendPacket(new GMViewPledgeInfo(player.getClan(),player));
            }
            else if(action.equals("setlevel"))
            {
                int level = 0;
                try
                {
                    level = Integer.parseInt(parameter);
                }
                catch(NumberFormatException nfe){}
                
                if (level>=0 && level <9)
                {
                    player.getClan().changeLevel(level);
                    admin.sendMessage("You set level " + level + " for clan " + player.getClan().getName());
                }
                else
                    admin.sendMessage("Level incorrect.");
            }
            else if (action.startsWith("rep"))
            {
                int points = 0;
                try
                {
                    points = Integer.parseInt(parameter);
                }
                catch(NumberFormatException nfe)
                {
                    admin.sendMessage("Usage: //pledge <rep> <number>");
                }
                
                L2Clan clan = player.getClan();
                if (clan.getLevel() < 5)
                {
                    admin.sendMessage("Only clans of level 5 or above may receive reputation points.");
                    return false;
                }
                clan.setReputationScore(clan.getReputationScore()+points, true);
                admin.sendMessage("You "+(points>0?"add ":"remove ")+Math.abs(points)+" points "+(points>0?"to ":"from ")+clan.getName()+"'s reputation. Their current score is "+clan.getReputationScore());
            }
        }
        return true;
    }
    /**
     * 
     */
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
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
}
