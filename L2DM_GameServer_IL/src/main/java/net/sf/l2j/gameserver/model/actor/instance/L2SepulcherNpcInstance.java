/*
 * $HeadURL: $
 *
 * $Author: $
 * $Date: $
 * $Revision: $
 *
 * 
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

import java.util.Map;
import java.util.concurrent.Future;

import javolution.util.FastMap;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.instancemanager.FourSepulchersManager;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SocialAction;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.ValidateLocation;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * @version $Revision: $ $Date: $
 * @author  sandman
 */
public class L2SepulcherNpcInstance extends L2NpcInstance
{
	private final static Log _log = LogFactory.getLog(L2SepulcherNpcInstance.class.getName());

    protected static Map<Integer,Integer> _hallGateKeepers = new FastMap<Integer,Integer>();

    protected Future _closeTask = null;
    protected Future _spawnNextMysteriousBoxTask = null;
    protected Future _spawnMonsterTask = null;
    
    private final String _HTML_FILE_PATH = "data/html/SepulcherNpc/";
    
    private final int HALLS_KEY = 7260;

    public L2SepulcherNpcInstance(int objectID, L2NpcTemplate template)
    {
        super(objectID, template);

        _hallGateKeepers.clear();
        _hallGateKeepers.put(31925, 25150012);
        _hallGateKeepers.put(31926, 25150013);
        _hallGateKeepers.put(31927, 25150014);
        _hallGateKeepers.put(31928, 25150015);
        _hallGateKeepers.put(31929, 25150016);
        _hallGateKeepers.put(31930, 25150002);
        _hallGateKeepers.put(31931, 25150003);
        _hallGateKeepers.put(31932, 25150004);
        _hallGateKeepers.put(31933, 25150005);
        _hallGateKeepers.put(31934, 25150006);
        _hallGateKeepers.put(31935, 25150032);
        _hallGateKeepers.put(31936, 25150033);
        _hallGateKeepers.put(31937, 25150034);
        _hallGateKeepers.put(31938, 25150035);
        _hallGateKeepers.put(31939, 25150036);
        _hallGateKeepers.put(31940, 25150022);
        _hallGateKeepers.put(31941, 25150023);
        _hallGateKeepers.put(31942, 25150024);
        _hallGateKeepers.put(31943, 25150025);
        _hallGateKeepers.put(31944, 25150026);

        if(_closeTask != null) _closeTask.cancel(true);
        if(_spawnNextMysteriousBoxTask != null) _spawnNextMysteriousBoxTask.cancel(true);
        if(_spawnMonsterTask != null) _spawnMonsterTask.cancel(true);
        _closeTask = null;
        _spawnNextMysteriousBoxTask = null;
        _spawnMonsterTask = null; 
    }

    @Override
    public void deleteMe()
    {
        if (_closeTask != null)
        {
        	_closeTask.cancel(true);
        	_closeTask = null;
        }
        if (_spawnNextMysteriousBoxTask != null)
        {
        	_spawnNextMysteriousBoxTask.cancel(true);
        	_spawnNextMysteriousBoxTask = null;
        }
        if(_spawnMonsterTask != null)
    	{
        	_spawnMonsterTask.cancel(true);
            _spawnMonsterTask = null; 
    	}
        super.deleteMe();
    }

    @Override
    public void onAction(L2PcInstance player)
    {
        if (!canTarget(player)) return;

        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget())
        {
            if (_log.isDebugEnabled()) _log.info("new target selected:"+getObjectId());
            
            // Set the target of the L2PcInstance player
            player.setTarget(this);
            

            
            // Check if the player is attackable (without a forced attack)
            if (isAutoAttackable(player))
            {
                // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
                // The player.getLevel() - getLevel() permit to display the correct color in the select window
                MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
                player.sendPacket(my);

                // Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
                StatusUpdate su = new StatusUpdate(getObjectId());
                su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
                su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
                player.sendPacket(su);
            }
            else
            {
                // Send a Server->Client packet MyTargetSelected to the L2PcInstance player
                MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
                player.sendPacket(my);
            }
            
            // Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
            player.sendPacket(new ValidateLocation(this));
        }
        else
        {
            // Check if the player is attackable (without a forced attack) and isn't dead
            if (isAutoAttackable(player) && !isAlikeDead())
            {
                // Check the height difference
                if (Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
                {
                    // Set the L2PcInstance Intention to AI_INTENTION_ATTACK
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
                }
                else
                {
                    // Send a Server->Client packet ActionFailed (target is out of attack range) to the L2PcInstance player
                    player.sendPacket(new ActionFailed());
                }
            }
            
            if(!isAutoAttackable(player)) 
            {
                // Calculate the distance between the L2PcInstance and the L2NpcInstance
                if (!canInteract(player))
                {
                    // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
                else
                {
                    // Send a Server->Client packet SocialAction to the all L2PcInstance on the _knownPlayer of the L2NpcInstance
                    // to display a social action of the L2NpcInstance on their client
                    SocialAction sa = new SocialAction(getObjectId(), Rnd.get(8));
                    broadcastPacket(sa);
                    
                    doAction(player);
                }
            }
            // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
            player.sendPacket(new ActionFailed());
        }
    }

    private void doAction(L2PcInstance player)
    {
        if(isDead())
        {
            player.sendPacket(new ActionFailed());
            return;
        }
        
        switch(getNpcId())
        {
            case 31468:
            case 31469:
            case 31470:
            case 31471:
            case 31472:
            case 31473:
            case 31474:
            case 31475:
            case 31476:
            case 31477:
            case 31478:
            case 31479:
            case 31480:
            case 31481:
            case 31482:
            case 31483:
            case 31484:
            case 31485:
            case 31486:
            case 31487:
                reduceCurrentHp(getMaxHp() + 1, player);
                if(_spawnMonsterTask != null) _spawnMonsterTask.cancel(true);
                _spawnMonsterTask = 
                	ThreadPoolManager.getInstance().scheduleEffect(new SpawnMonster(getNpcId()),3500); 
                break;
                
            case 31455:
            case 31456:
            case 31457:
            case 31458:
            case 31459:
            case 31460:
            case 31461:
            case 31462:
            case 31463:
            case 31464:
            case 31465:
            case 31466:
            case 31467:
                reduceCurrentHp(getMaxHp() + 1, player);
				player.addItem("Quest", HALLS_KEY, 1, player, true);
                break;

            default:
                showChatWindow(player,0);
        }
        player.sendPacket(new ActionFailed());
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

        return _HTML_FILE_PATH + pom + ".htm";
    }

    @Override
    public void showChatWindow(L2PcInstance player, int val)
    {
        String filename = getHtmlPath(getNpcId(),val);
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
        player.sendPacket( new ActionFailed() );
    }
    
    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
        if (isBusy())
        {
            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/npcbusy.htm");
            html.replace("%busymessage%", getBusyMessage());
            html.replace("%npcname%", getName());
            html.replace("%playername%", player.getName());
            player.sendPacket(html);
        }
        else if (command.startsWith("Chat"))
        {
            int val = 0;
            try 
            {
                val = Integer.parseInt(command.substring(5));
            }
            catch (IndexOutOfBoundsException ioobe){}
            catch (NumberFormatException nfe){}
            showChatWindow(player, val);
        }
        else if (command.startsWith("open_gate"))
        {
            L2ItemInstance hallsKey = player.getInventory().getItemByItemId(HALLS_KEY);
            if(hallsKey != null && FourSepulchersManager.getInstance().isAttackTime())
            {
                switch(getNpcId())
                {
                    case 31929:
                    case 31934:
                    case 31939:
                    case 31944:
                        FourSepulchersManager.getInstance().spawnShadow(getNpcId());
                    default:
                        openNextDoor(getNpcId());
                        player.destroyItemByItemId("Quest", HALLS_KEY, hallsKey.getCount(), player, true);
                }
            }
        }
        else if (command.startsWith("Entry"))
        {
            FourSepulchersManager.getInstance().entry(getNpcId(),player);
        }
        else
        {
            super.onBypassFeedback(player, command);
        }
    }

    public void openNextDoor(int npcId)
    {
        int doorId = _hallGateKeepers.get(npcId).intValue();
        DoorTable _doorTable = DoorTable.getInstance();
        _doorTable.getDoor(doorId).openMe();
        
        if(_closeTask != null) _closeTask.cancel(true);
        _closeTask = ThreadPoolManager.getInstance().scheduleEffect(new CloseNextDoor(npcId,doorId),10000);
    }

    private class CloseNextDoor implements Runnable
    {
        final DoorTable _DoorTable = DoorTable.getInstance();
        private int _NpcId;
        private int _DoorId;
        
        public CloseNextDoor(int npcId,int doorId)
        {
        	_NpcId = npcId;
            _DoorId = doorId;
        }
        
        public void run()
        {
            try
            {
                _DoorTable.getDoor(_DoorId).closeMe();
            }
            catch (Exception e)
            {
                _log.warn(e.getMessage());
            }
            
            if(_spawnNextMysteriousBoxTask != null) _spawnNextMysteriousBoxTask.cancel(true);
            _spawnNextMysteriousBoxTask = ThreadPoolManager.getInstance().scheduleEffect(new SpawnNextMysteriousBox(_NpcId),10000);
        }
    }

    private class SpawnNextMysteriousBox implements Runnable
    {
    	private int _NpcId;
    	public SpawnNextMysteriousBox(int npcId)
    	{
    		_NpcId = npcId;
    	}
        public void run()
        {
        	FourSepulchersManager.getInstance().spawnMysteriousBox(_NpcId);
        }
    }

    private class SpawnMonster implements Runnable
    {
    	private int _NpcId;
    	public SpawnMonster(int npcId)
    	{
    		_NpcId = npcId;
    	}
        public void run()
        {
            FourSepulchersManager.getInstance().spawnMonster(_NpcId);
        }
    }
}
