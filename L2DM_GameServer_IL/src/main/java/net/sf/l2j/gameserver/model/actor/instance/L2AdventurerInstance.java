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
import net.sf.l2j.gameserver.hardcodedtables.HardcodedMultisellist;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.TownManager;
import net.sf.l2j.gameserver.model.L2Multisell;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.entity.Town;
import net.sf.l2j.gameserver.serverpackets.ExQuestInfo;
import net.sf.l2j.gameserver.serverpackets.RadarControl;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: $ $Date: $
 * @author  LBaldi
 */
public class L2AdventurerInstance extends L2FolkInstance
{
    private final static Log _log = LogFactory.getLog(L2AdventurerInstance.class.getName());
    
    public L2AdventurerInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
    	if (command.startsWith("npcfind_byid"))
        {
            try
            {
                int bossId = Integer.parseInt(command.substring(12).trim());
                switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossId))
                {
                    case ALIVE:
                    case DEAD:
                        L2Spawn spawn = RaidBossSpawnManager.getInstance().getSpawns().get(bossId);
                        player.sendPacket(new RadarControl(0, 1, spawn.getLocx(), spawn.getLocy(),
                                                           spawn.getLocz()));
                        break;
                    case UNDEFINED:
                        player.sendMessage("This Boss isn't in game - notify L2Emu Datapack Dev Team");
                        break;
                }
            }
            catch (NumberFormatException e)
            {
                _log.warn("Invalid Bypass to Server command parameter.");
            }
        }
    	// not used folder don't exist and bypass never use
        /*else if (command.startsWith("raidInfo"))
        {
            int bossLevel = Integer.parseInt(command.substring(9).trim());
            String filename = HtmlPathService.ADVENTURER_HTML_PATH_RAID_INFO+"info.htm";
            if (bossLevel != 0)
            {
                filename = HtmlPathService.ADVENTURER_HTML_PATH_RAID_LEVEL + bossLevel + ".htm";
            }
            showChatWindow(player, bossLevel, filename);
        }*/
        else if (command.startsWith("LifeCrystal2"))
        {
        	 String filename = HtmlPathService.ADVENTURER_HTML_PATH + "LifeCrystal2.htm";   
        	 showChatWindow(player, filename);
        }
        else if (command.startsWith("LifeCrystal"))
        {
        	 String filename = HtmlPathService.ADVENTURER_HTML_PATH + "LifeCrystal.htm";  
             showChatWindow(player, filename);  
        }
        else if (command.startsWith("AssembleLifeCrystal"))
        {
        	Town town = TownManager.getInstance().getTown(player.getX(), player.getY(), player.getZ());
        	int townId = town.getTownId();
        	String name =  TownManager.getInstance().getTownName(townId);
         	
        	if(name!=null && Config.DEVELOPER)
        	_log.info("town Name = "+name+ " ID: "+town.getTownId());
        	
        	switch (townId)
				{
				case 5:
					// Gludio
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.GLUDIO_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 6:
					// Gludin
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.GLUDIN_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 7:
					// Dion
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.DION_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 8:
					// Giran
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.GIRAN_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 9:
					// Oren
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.OREN_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 10:
					// Aden
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.ADEN_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 11:
					// HV
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.HUNTERS_VILLAGE_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 13:
					// Heine
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.HEINE_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 14:
					// Rune
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.RUNE_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 15:
					// Goddard
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.GODDARD_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
				case 16:
					// Scuttgart
					L2Multisell.getInstance().SeparateAndSend(HardcodedMultisellist.SCHUTTGART_LIFE_CRYSTAL, player, false, getCastle().getTaxRate());
					break;
					
					//sets aden default, just in case an unescpectted expetion accour 
					//i think this will never happen, but in java we never know.^^
					default :
						townId = 5;
					break;
				}
        }
        else if (command.equalsIgnoreCase("questlist"))
        {
            player.sendPacket(new ExQuestInfo());
        }
        else
        {
            super.onBypassFeedback(player, command);
        }
    }
    @Override
    public String getHtmlPath(int npcId, int val)
    {
        String pom = "";

        if (val == 0) pom = "" + npcId;
        else pom = npcId + "-" + val;

        return HtmlPathService.ADVENTURER_HTML_PATH + pom + ".htm";
    }
    private void showChatWindow(L2PcInstance player, int bossLevel, String filename)
    {
        showChatWindow(player, filename);
    }
}
