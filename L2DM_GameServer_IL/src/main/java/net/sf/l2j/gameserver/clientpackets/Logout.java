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
package net.sf.l2j.gameserver.clientpackets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.Olympiad;
import net.sf.l2j.gameserver.SevenSignsFestival;
import net.sf.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.FriendList;
import net.sf.l2j.gameserver.serverpackets.LeaveWorld;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class Logout extends L2GameClientPacket
{
    private static final String _C__09_LOGOUT = "[C] 09 Logout";
    private final static Log _log = LogFactory.getLog(Logout.class.getName());
    
    // c

    /**
     * @param decrypt
     */
    @Override
    protected void readImpl()
    {

    }

    @Override
    protected void runImpl()
    {
        // Dont allow leaving if player is fighting
        L2PcInstance player = getClient().getActiveChar();
        
        if (player == null)
            return;

        // [L2J_JP ADD START]
        if (!(player.isGM()))
        {
            if(ZoneManager.getInstance().checkIfInZone(ZoneType.NoEscape, player)){
                player.sendMessage("You can not log out in here.");
                player.sendPacket(new ActionFailed());
                return;                   
            }
        }

        if(player.isFlying())
        {
            player.sendMessage("You can not log out while flying.");
            player.sendPacket(new ActionFailed());
            return;                   
        }
        //L2EMU_ADD_START
        if(player._inEventTvT && TvT._started)
        {
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
        	sm.addString("You cannot logout while in registered in an event.");
        	player.sendPacket(sm);
        	sm = null;
            player.sendPacket(new ActionFailed());
            return;                   
        }
        //L2EMU_ADD_END
        // [L2J_JP ADD END]

        if(AttackStanceTaskManager.getInstance().getAttackStanceTask(player))
        {
            if (_log.isDebugEnabled()) _log.debug("Player " + player.getName() + " tried to logout while fighting");
            
            player.sendPacket(new SystemMessage(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
            player.sendPacket(new ActionFailed());
            return;
        }
        
        if (player.getPet() != null && !player.isBetrayed() && (player.getPet() instanceof L2PetInstance))
        {
            L2PetInstance pet = (L2PetInstance)player.getPet();

            if (pet.isAttackingNow())
            {
                pet.sendPacket(new SystemMessage(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE));
                player.sendPacket(new ActionFailed());
                return;
            }
            pet.unSummon(player);
        }
        
        if(player.atEvent)
        {
            player.sendMessage("A superior power doesn't allow you to leave the event.");
            return;
        }
        
        if (player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
        {
            player.sendMessage("You can't logout in olympiad mode.");
            return;
        }
        
        // Prevent player from logging out if they are a festival participant
        // and it is in progress, otherwise notify party members that the player
        // is not longer a participant.
        if (player.isFestivalParticipant())
        {
            if (SevenSignsFestival.getInstance().isFestivalInitialized()) 
            {
                player.sendMessage("You cannot log out while you are a participant in a festival.");
                return;
            }
            L2Party playerParty = player.getParty();
            
            if (playerParty != null)
                player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
        }

        if (player.isFlying()) 
        { 
           player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
        }

        if (player.getPrivateStoreType() != 0)
        {
            player.sendMessage("Cannot log out while trading.");
            return;
        }

        if (player.getActiveRequester() != null)
        {
            player.getActiveRequester().onTradeCancel(player);
            player.onTradeCancel(player.getActiveRequester());
        }

        RegionBBSManager.getInstance().changeCommunityBoard();

        player.getInventory().updateDatabase();
        player.deleteMe();

        // notify friends
        notifyFriends(player);

        //save character
        L2GameClient.saveCharToDisk(player);

        // normally the server would send serveral "delete object" before "leaveWorld"
        // we skip that for now
        sendPacket(new LeaveWorld());
    }

	private void notifyFriends(L2PcInstance cha)
	{
		java.sql.Connection con = null;
	
		try {
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement;
			statement = con.prepareStatement("SELECT friend_name FROM character_friends WHERE char_id=?");
			statement.setInt(1, cha.getObjectId());
			ResultSet rset = statement.executeQuery();
	
			L2PcInstance friend;
			String friendName;
	
			while (rset.next())
			{
				friendName = rset.getString("friend_name");
	
				friend = L2World.getInstance().getPlayer(friendName);
	
				if (friend != null) //friend logged in.
				{
					friend.sendPacket(new FriendList(friend));
				}
			}
			
			rset.close();
			statement.close();
		} 
		catch (Exception e) {
			_log.warn("could not restore friend data:"+e);
		} 
		finally
		{
			try {con.close();} catch (Exception e){}
		}
	}

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
     */
    @Override
    public String getType()
    {
        return _C__09_LOGOUT;
    }
}