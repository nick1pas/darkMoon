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

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.serverpackets.LeaveWorld;
import net.sf.l2j.gameserver.serverpackets.ServerClose;
import net.sf.l2j.gameserver.services.ThreadService;
 /**
  * <b> this class manages the following admin commands: </b><br><br>
  * 
  * <li> admin_kick = kicks from server a selected player <br>
  * <li> admin_kick_non_gm = kicks from server all players excluding gms <br><br>
  * 
  * <b> Usage: </b><br><br>
  *  
  * <li> //kick <br>
  * <li> //kick_non_gm <br><br>
  *   
  * @author ?
  *
  */
public class AdminKick implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS = 
    {
    	"admin_kick",
    	"admin_kick_non_gm"
    };
    private static final int REQUIRED_LEVEL = Config.GM_KICK;
    
    public boolean useAdminCommand(String command, L2PcInstance admin)
    {

        if (!Config.ALT_PRIVILEGES_ADMIN)
        {
            if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
                return false;
        }
        
        if (command.startsWith("admin_kick"))
        {
            StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1)
            {
                st.nextToken();
                String plyr = st.nextToken();
                L2PcInstance player = L2World.getInstance().getPlayer(plyr);
                if (player != null)
                {
                    kickPlayer (player);
                    //L2EMU_ADD_START
                    admin.sendMessage("player "+player.getName()+" has been kicked.");
                    //L2EMU_ADD_END
                    RegionBBSManager.getInstance().changeCommunityBoard();

                }
            }
        }
        if (command.startsWith("admin_kick_non_gm"))
        {
            int counter = 0;
            for (L2PcInstance player : L2World.getInstance().getAllPlayers())
            {
                if(!player.isGM())
                {
                    counter++;
                    kickPlayer (player);
                }
            }
            admin.sendMessage("you have Kicked "+counter+" players");
        }
        return true;
    }
    //L2EMU_EDIT_START
    private void kickPlayer (L2PcInstance player)
    {
    	//    	Logout Character
    	try {
    		// save player's stats and effects
    		L2GameClient.saveCharToDisk(player);
            
    		player.sendMessage("a GM has kicked you.");
    		ThreadService.processSleep(2);
    		switch(Config.KICK_TYPE)
    		{
    		case relogin:
    			// close server this packet come backs to login a kick must close :P
    			ServerClose sc = new ServerClose();
    			player.sendPacket(sc);
    			break;
    		case closeClient:
    			// closes client ^^(sometimes crash :P)
    			LeaveWorld lw = new LeaveWorld();
    			player.sendPacket(lw);
    			break;
    		}
    		//make sure to save ALL data
    		player.deleteMe();

    	} catch (Throwable t)   {}

    	try {
    		player.closeNetConnection();
    	} catch (Throwable t)   {} 
    }
    //L2EMU_EDIT END
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
    /**
     * 
     * @param level
     * @return
     */
    private boolean checkLevel(int level) {
        return (level >= REQUIRED_LEVEL);
    }
}