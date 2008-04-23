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
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_add_exp_sp_to_character = show menu <br>
 * <li> admin_add_exp_sp = adds exp & sp to target <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 * <li> //add_exp_sp_to_character<br>
 * <li> //add_exp_sp exp sp = [exp_value] [sp_value] (needs target) <br><br>
 * 
 * 
 * @version $Revision: 1.2.4.6 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminExpSp implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminExpSp.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_exp_sp",
		"admin_remove_exp_sp"
	};
	private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;

	public boolean useAdminCommand(String command, L2PcInstance admin) {

		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if (command.startsWith("admin_add_exp_sp"))
		{
			try
			{
				String val = command.substring(16); 
				adminAddExpSp(admin, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{	//Case of empty character name
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Error while adding Exp-Sp.");
				admin.sendPacket(sm);
				//listCharacters(client, 0);
			}			
		}
		else if(command.startsWith("admin_remove_exp_sp"))
		{
			try
			{
				String val = command.substring(19); 
				adminRemoveExpSP(admin, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{   //Case of empty character name
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Error while removing Exp-Sp.");
				admin.sendPacket(sm);
				//listCharacters(client, 0);            
			}
		}

		return true;
	}
	private void adminAddExpSp(L2PcInstance admin, String ExpSp)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance)target;
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("Incorrect target.");
			admin.sendPacket(sm);
			return;
		}

		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens()!=2)
		{
			return;
		}
		else
		{
			String exp = st.nextToken();
			String sp = st.nextToken();
			long expval = 0;
			int spval = 0;
			try
			{
				expval = Long.parseLong(exp);
				spval = Integer.parseInt(sp);
			}
			catch (NumberFormatException e)
			{
				//Wrong number (maybe it's too big?)
				SystemMessage smA = new SystemMessage(SystemMessageId.S1_S2);
				smA.addString("Wrong Number Format");
				admin.sendPacket(smA);
			}
			if(expval != 0 || spval != 0)
			{
				//Common character information
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Admin is adding you "+expval+" xp and "+spval+" sp.");
				player.sendPacket(sm);

				player.addExpAndSp(expval,spval);

				//Admin information	
				SystemMessage smA = new SystemMessage(SystemMessageId.S1_S2);
				smA.addString("Added "+expval+" xp and "+spval+" sp to "+player.getName()+".");
				admin.sendPacket(smA);
				if (_log.isDebugEnabled())
				{
					_log.debug("GM: "+admin.getName()+"("+admin.getObjectId()+") added "+expval+
							" xp and "+spval+" sp to "+player.getObjectId()+".");
				}

			}
		}
	}

	private void adminRemoveExpSP(L2PcInstance admin, String ExpSp)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance)target;
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("Incorrect target.");
			admin.sendPacket(sm);
			return;
		}

		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens()!=2)
		{
			return;
		}
		else
		{
			String exp = st.nextToken();
			String sp = st.nextToken();
			long expval = 0;
			int spval = 0;
			try
			{
				expval = Long.parseLong(exp);
				spval = Integer.parseInt(sp);
			}
			catch (NumberFormatException e)
			{
				//Wrong number (maybe it's too big?)
				SystemMessage smA = new SystemMessage(SystemMessageId.S1_S2);
				smA.addString("Wrong Number Format");
				admin.sendPacket(smA);
			}
			if(expval != 0 || spval != 0)
			{
				//Common character information
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Admin is removing you "+expval+" xp and "+spval+" sp.");
				player.sendPacket(sm);

				player.removeExpAndSp(expval,spval);

				//Admin information 
				SystemMessage smA = new SystemMessage(SystemMessageId.S1_S2);
				smA.addString("Removed "+expval+" xp and "+spval+" sp from "+player.getName()+".");
				admin.sendPacket(smA);
				if (_log.isDebugEnabled())
				{
					_log.debug("GM: "+admin.getName()+"("+admin.getObjectId()+") added "+expval+
							" xp and "+spval+" sp to "+player.getObjectId()+".");
				}

			}
		}
	}

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