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
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.serverpackets.Ride;

/**
 * 
 *
 */
public class CastleDoors implements IVoicedCommandHandler
{
	//L2EMU_EDIT
    private static final String[] VOICED_COMMANDS = { "open", "close","ridewyvern"}; 
   //L2EMU_EDIT
    
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        if(command.startsWith("open")&&target.equals("doors")&&(activeChar.isClanLeader())){
            if (activeChar.getTarget() instanceof L2DoorInstance){
            L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
            Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getHasCastle());
            if (door == null || castle == null) return false;
            if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
            {
                door.openMe();
            }}else return false;
        }
        else if(command.startsWith("close")&&target.equals("doors")&&(activeChar.isClanLeader())){
            if (activeChar.getTarget() instanceof L2DoorInstance){
            L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
            Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getHasCastle());
            if (door == null || castle == null) return false;
            if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
            {
                door.closeMe();
            }} else return false;
        }
        //L2EMU_ADD
        else if (command.startsWith("ridewyvern") &&(activeChar.getClan() != null) )
		{
            Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getHasCastle());
            if ( castle != null && activeChar.isClanLeader())
            {
                if (castle.checkIfInZone(activeChar.getX(), activeChar.getY(), activeChar.getZ()))
                {
                	if(!activeChar.disarmWeapons()) return false;
               	 	Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, 12621);
               	 	activeChar.sendPacket(mount);
               	 	activeChar.broadcastPacket(mount);
               	 	activeChar.setMountType(mount.getMountType());
                }
            }
   		}
      //L2EMU_ADD
        return true;
    }
    public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}