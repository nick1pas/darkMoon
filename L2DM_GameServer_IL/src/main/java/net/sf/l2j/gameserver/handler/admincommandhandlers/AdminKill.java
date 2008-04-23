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
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2ControllableMobInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_kill = kills target L2Character <br>
 * <li> admin_kill_monster = kills target non-player <br><br>
 * 
 *  <b> Usage: </b><br><br>
 *  
 * <li> //kill [radius] = If radius is specified, then ALL players only in that radius will be killed. <br>
 * <li> //kill_monster [radius] = If radius is specified, all non-players in that radius will be killed. <br><br>
 * 
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminKill implements IAdminCommandHandler 
{
    private final static Log _log = LogFactory.getLog(AdminKill.class);
    private static final String[] ADMIN_COMMANDS = 
    {
    	"admin_kill",
    	"admin_kill_monster"
    };
    private static final int REQUIRED_LEVEL = Config.GM_NPC_EDIT;
    
    public boolean useAdminCommand(String command, L2PcInstance admin) 
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) 
                return false;
        
        if (command.startsWith("admin_kill")) 
        {
            StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken(); // skip command

            if (st.hasMoreTokens())
            {
                String firstParam = st.nextToken();
                L2PcInstance plyr = L2World.getInstance().getPlayer(firstParam);
                if (plyr != null)
                {
                    if (st.hasMoreTokens())
                    {
                        try 
                        {
                            int radius  = Integer.parseInt(st.nextToken());
    
                            for (L2Character knownChar : plyr.getKnownList().getKnownCharactersInRadius(radius))
                            {
                                if (knownChar == null || knownChar instanceof L2ControllableMobInstance || knownChar.equals(admin)) continue;
                                
                                kill(admin, knownChar);
                            }

                            admin.sendMessage("Killed all characters within a " + radius + " unit radius.");
                            return true;
                        }
                        catch (NumberFormatException e) {
                            admin.sendMessage("Invalid radius.");
                            return false;
                        }
                    } else
                    {
                        kill(admin, plyr);
                    }
                }
                else
                {
                    try 
                    {
                        int radius  = Integer.parseInt(firstParam);

                        for (L2Character knownChar : admin.getKnownList().getKnownCharactersInRadius(radius))
                        {
                            if (knownChar == null || knownChar instanceof L2ControllableMobInstance || knownChar.equals(admin)) continue;
                            
                            kill(admin, knownChar);
                        }
                        
                        admin.sendMessage("Killed all characters within a " + radius + " unit radius.");
                        return true;
                    }
                    catch (NumberFormatException e) {
                        admin.sendMessage("Enter a valid player name or radius.");
                        return false;
                    }
                }
            } else
            {
                L2Object obj = admin.getTarget();

                if (obj == null || obj instanceof L2ControllableMobInstance || !(obj instanceof L2Character))
                {
                    admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
                } else
                {
                    kill(admin, (L2Character)obj);
                }
            }
        }
        
        return true;
    }
    /**
     * 
     * @param admin
     * @param target
     */
    private void kill(L2PcInstance admin, L2Character target)
    {
        if (target instanceof L2PcInstance)
        {
        	if(!((L2PcInstance)target).isGM())
        		target.stopAllEffects(); // e.g. invincibility effect
            target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, admin);
        }
        else if (target.isChampion())
        	target.reduceCurrentHp(target.getMaxHp()*Config.CHAMPION_HP + 1, admin);  
        else
            target.reduceCurrentHp(target.getMaxHp() + 1, admin);

        if (_log.isDebugEnabled()) 
            _log.debug("GM: "+admin.getName()+"("+admin.getObjectId()+")"+
                      " killed character "+target.getObjectId());
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