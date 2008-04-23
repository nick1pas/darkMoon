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

/**
 * @author NightMarez
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 *
 */

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.GatekeeperManager;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.TownManager;
import net.sf.l2j.gameserver.instancemanager.GatekeeperManager.StatusEnum;
import net.sf.l2j.gameserver.model.L2TeleportLocation;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.services.ThreadService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L2TeleporterInstance extends L2FolkInstance
{
    private final static Log _log = LogFactory.getLog(L2TeleporterInstance.class.getName());
    
	private static final int COND_ALL_FALSE = 0;
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_OWNER = 2;
	private static final int COND_REGULAR = 3;
	
	//L2EMU_ADD
	private StatusEnum _gatekeeperStatus;
	
	public void setGkStatus (GatekeeperManager.StatusEnum status)
	{
		_gatekeeperStatus = status;
	}

	public GatekeeperManager.StatusEnum getGkStatus()
	{
		return _gatekeeperStatus;
	}
	//L2EMU_ADD
	
	/**
	 * @param template
	 */
	public L2TeleporterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		int condition = validateCondition(player);

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if (actualCommand.equalsIgnoreCase("goto"))
		{
            int npcId = getTemplate().getNpcId();
            
            switch (npcId)
            {
                case 31095: //
                case 31096: //
                case 31097: //
                case 31098: // Enter Necropolises
                case 31099: //
                case 31100: //
                case 31101: // 
                case 31102: //

                case 31114: //
                case 31115: //
                case 31116: // Enter Catacombs
                case 31117: //
                case 31118: //
                case 31119: //
                    player.setIsIn7sDungeon(true);
                    break;
                case 31103: //
                case 31104: //
                case 31105: //
                case 31106: // Exit Necropolises
                case 31107: //
                case 31108: //
                case 31109: // 
                case 31110: //

                case 31120: //
                case 31121: //
                case 31122: // Exit Catacombs
                case 31123: //
                case 31124: //
                case 31125: //
                    player.setIsIn7sDungeon(false);
                    break;
            }
            
			if (st.countTokens() <= 0) {return;}
			int whereTo = Integer.parseInt(st.nextToken());
			if (condition == COND_REGULAR)
			{
				doTeleport(player, whereTo);
				return;
			}
			else if (condition == COND_OWNER)
			{
				int minPrivilegeLevel = 0;          // NOTE: Replace 0 with highest level when privilege level is implemented
				if (st.countTokens() >= 1) {minPrivilegeLevel = Integer.parseInt(st.nextToken());}
				if (10 >= minPrivilegeLevel)        // NOTE: Replace 10 with privilege level of player
					doTeleport(player, whereTo);
				else
				    player.sendMessage("You don't sufficient access level to teleport there.");
                return;
		    }
		}
		//L2EMU_ADD_START
		else if (actualCommand.equalsIgnoreCase("ctele"))
	     {
	         if(_log.isDebugEnabled())
	         _log.info("requested custom tele bypass ok");
	         if (st.countTokens() <= 0) {return;}
	         int whereTo = Integer.parseInt(st.nextToken());
	         if (condition == COND_REGULAR)
	         {
	             doCustomTeleport(player, whereTo);
	             return;
	         }
	         else if (condition == COND_OWNER)
	         {
	             int minPrivilegeLevel = 0;  //Replace 0 with highest level when privilege level is implemented
	             if (st.countTokens() >= 1) {minPrivilegeLevel = Integer.parseInt(st.nextToken());}
	             
	             if (10 >= minPrivilegeLevel)  //Replace 10 with privilege level of player
	                 doTeleport(player, whereTo);
	             else
	                 player.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
	             return;
	         }
	         super.onBypassFeedback(player, command);
	         if(_log.isDebugEnabled())
	         _log.info("player teleported using custom bypass");
	     }
        super.onBypassFeedback(player, command);
	}
	//L2EMU_ADD_END
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
		
		return "data/html/teleporter/" + pom + ".htm";
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		
		int condition = validateCondition(player);
		if (condition == COND_REGULAR)
		{
			super.showChatWindow(player);
			return;
		}
		else if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
			else if (condition == COND_OWNER)                                // Clan owns castle
				filename = getHtmlPath(getNpcId(), 0);                       // Owner message window
		}

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	private void doTeleport(L2PcInstance player, int val)
	{
		L2TeleportLocation list = TeleportLocationTable.getInstance().getTemplate(val);
		if (list != null)
		{
            //you cannot teleport to village that is in siege
            if (SiegeManager.getInstance().checkIfInZone(list.getLocX(), list.getLocY(), list.getLocZ()))
            {
                player.sendPacket(new SystemMessage(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE));
                return;
            }
            else if (TownManager.getInstance().townHasCastleInSiege(list.getLocX(), list.getLocY(), list.getLocZ()))
            {
                player.sendPacket(new SystemMessage(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE));
                return;
            }
            else if (player.isAlikeDead())
            {
               return;
            }
            //L2EMU_ADD
            else if (!list.isForNoble() && Config.ONLY_GM_TELEPORT_FREE && player.isGM()){
            	 if (_log.isDebugEnabled())
                     _log.debug("Teleporting GM "+player.getName()+" to new location: "+list.getLocX()+":"+list.getLocY()+":"+list.getLocZ()+"For Free");
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ());
			}
            else if (list.isForNoble() && Config.ONLY_GM_TELEPORT_FREE && player.isGM()){
           	 if (_log.isDebugEnabled())
                    _log.debug("Teleporting GM "+player.getName()+" to new location: "+list.getLocX()+":"+list.getLocY()+":"+list.getLocZ()+"For Free");
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ());
			}
            //L2EMU_ADD
            else if (list.isForNoble() && !player.isNoble())
            {
            	String filename = "data/html/teleporter/nobleteleporter-no.htm";
            	NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile(filename);
                html.replace("%objectId%", String.valueOf(getObjectId()));
                html.replace("%npcname%", getName());
                player.sendPacket(html);
                return;
            }
            else if (player.isAlikeDead())
            {
               return;
            }
            else if (!list.isForNoble() && (Config.ALT_GAME_FREE_TELEPORT || player.reduceAdena("Teleport", list.getPrice(), this, true)))
            {
                if (_log.isDebugEnabled())
                    _log.debug("Teleporting player " + player.getName() + " to new location: " + list.getLocX() + ":" + list.getLocY() + ":" + list.getLocZ());
                player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), true);
                //L2EMU_ADD
                if (Config.PLAYER_SPAWN_PROTECTION > 0 && !player.getInPeaceZone()){
                	ThreadService.processSleep(5);
                	player.sendMessage("You have "+Config.PLAYER_SPAWN_PROTECTION+" seconds till spawn protection ends.");
                }
                //L2EMU_ADD
            }
            else if (list.isForNoble() && (Config.ALT_GAME_FREE_TELEPORT || player.destroyItemByItemId("Noble Teleport", 6651, list.getPrice(), this, true)))
            {
                if (_log.isDebugEnabled())                    
                    _log.debug("Teleporting player "+player.getName()+" to new location: "+list.getLocX()+":"+list.getLocY()+":"+list.getLocZ());
                player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ());
                //L2EMU_ADD
                if (Config.PLAYER_SPAWN_PROTECTION > 0 && !player.getInPeaceZone()){
                	ThreadService.processSleep(5);
                	player.sendMessage("You have "+Config.PLAYER_SPAWN_PROTECTION+" seconds till spawn protection ends.");
                }
                //L2EMU_ADD
            }
        }
        else
        {
            _log.warn("No teleport destination with id:" +val);
        }
        player.sendPacket( new ActionFailed() );
    }

    private int validateCondition(L2PcInstance player)
    {
        if (CastleManager.getInstance().getCastle(this) == null)     // Teleporter isn't on castle ground
            return COND_REGULAR;                                     // Regular access
        else if (getCastle().getSiege().getIsInProgress())           // Teleporter is on castle ground and siege is in progress
            return COND_BUSY_BECAUSE_OF_SIEGE;                       // Busy because of siege
        else if (player.getClan() != null)                           // Teleporter is on castle ground and player is in a clan
        {
            if (getCastle().getOwnerId() == player.getClanId())      // Clan owns castle
                return COND_OWNER;                                   // Owner
        }
        
        return COND_ALL_FALSE;
    }
	//L2EMU_ADD_START
    private void doCustomTeleport(L2PcInstance player, int val)
    {
        L2TeleportLocation clist = TeleportLocationTable.getInstance().getCustomTemplate(val);
        
        if (clist!= null)
        {
            //player cannot teleport to village that is in siege
            if (!Config.ALLOW_TELE_IN_SIEGE_TOWN && SiegeManager.getInstance().checkIfInZone(clist.getLocX(), clist.getLocY(), clist.getLocZ()) && !player.isGM())
            {
                player.sendPacket(new SystemMessage(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE));
                return;
            }
            // Check if karma punishement code is enabled
            else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0 && !player.isGM()) //karma
            {
                SystemMessage sms = new SystemMessage(SystemMessageId.NOTHING_HAPPENED);  
                player.sendPacket(sms);
                return;
            }
            else if (player.isAlikeDead()) 
            { 
                return; 
            } 
            else if(Config.ALT_GAME_FREE_TELEPORT || player.reduceAdena("Teleport", clist.getPrice(), this, true))
            {
                if (Config.DEVELOPER) _log.debug("[L2TeleporterInstance]: Teleporting Player "+player.getName()+" To: "+clist.getLocX()+":"+clist.getLocY()+":"+clist.getLocZ());
                    player.teleToLocation(clist.getLocX(), clist.getLocY(), clist.getLocZ());
                    if (Config.PLAYER_SPAWN_PROTECTION > 0 && !player.getInPeaceZone()){
                    	ThreadService.processSleep(5);
                    	player.sendMessage("You have "+Config.PLAYER_SPAWN_PROTECTION+" seconds till spawn protection ends.");
                    }
            }
        }
        else
        {
            _log.warn("GameServer: No Custom Teleport Destination For ID:" +val);
        }
        player.sendPacket( new ActionFailed() );
    }
	//L2EMU_ADD_END
}