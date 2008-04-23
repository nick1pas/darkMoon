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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 * This class handles following admin commands: - delete = deletes target
 * 
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/04/11 10:05:56 $
 */
public class AdminDelete implements IAdminCommandHandler
{
    //private final static Log _log = LogFactory.getLog(AdminDelete.class.getName());

    private static final String[] ADMIN_COMMANDS = {"admin_delete"};

    private static final int REQUIRED_LEVEL = Config.GM_NPC_EDIT;

    public boolean useAdminCommand(String command, L2PcInstance admin)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
        {
            if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
        }

        if (command.equals("admin_delete")) handleDelete(admin);
        return true;
    }
    private void handleDelete(L2PcInstance admin)
    {
        L2Object obj = admin.getTarget();
        if ((obj != null) && (obj instanceof L2NpcInstance))
        {
            L2NpcInstance target = (L2NpcInstance) obj;
            target.deleteMe();

            L2Spawn spawn = target.getSpawn();
            if (spawn != null)
            {
                spawn.stopRespawn();

                if (RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcId())) RaidBossSpawnManager.getInstance().deleteSpawn(spawn,true);
                else SpawnTable.getInstance().deleteSpawn(spawn, true);
            }

            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Deleted " + target.getName() + " from " + target.getObjectId() + ".");
            admin.sendPacket(sm);
        }
        else
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Incorrect target.");
            admin.sendPacket(sm);
        }
    }
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }

    private boolean checkLevel(int level)
    {
        return (level >= REQUIRED_LEVEL);
    }
}