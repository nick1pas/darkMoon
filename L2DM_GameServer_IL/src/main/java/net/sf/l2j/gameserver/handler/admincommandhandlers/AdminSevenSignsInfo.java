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
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 *  <li> admin_seven_signs_cycle <br>
 *	<li> admin_seven_signs_period <br>
 *  <li> admin_festival_time <br>
 *	<li> admin_festival_start <br>
 *	<li> admin_festival_end <br>
 *	<li> admin_spawn_orators <br> 
 *	<li> admin_spawn_preachers <br>
 *	<li> admin_unspawn_orators <br> 
 *	<li> admin_unspawn_preachers <br>
 *	<li> admin_spawn_dusk_crest <br>
 *	<li> admin_spawn_dawn_crest <br>
 *	<li> admin_uspawn_dawn_crest <br>
 *	<li> admin_uspawn_dusk_crest <br>
 *	<li> admin_spawn_mammon <br>
 *	<li> admin_uspawn_mammon <br>
 *	<li> admin_spawn_lilith <br>
 *	<li> admin_spawn_anakim <br>
 *	<li> admin_unspawn_lilith <br>
 *	<li> admin_unspawn_anakim <br><br>
 * 
 * <b> Usage: </b><br><br>
 * 
 *  <li> //seven_signs_cycle <br>
 *	<li> //seven_signs_period <br>
 *  <li> //festival_time <br>
 *	<li> //festival_start <br>
 *	<li> //festival_end <br>
 *	<li> //spawn_orators <br> 
 *	<li> //spawn_preachers <br>
 *	<li> //unspawn_orators <br> 
 *	<li> //unspawn_preachers <br>
 *	<li> //spawn_dusk_crest <br>
 *	<li> //spawn_dawn_crest <br>
 *	<li> //uspawn_dawn_crest <br>
 *	<li> //uspawn_dusk_crest <br>
 *	<li> //spawn_mammon <br>
 *	<li> //uspawn_mammon <br>
 *	<li> //spawn_lilith <br>
 *	<li> //spawn_anakim <br>
 *	<li> //unspawn_lilith <br>
 *	<li> //unspawn_anakim <br><br>
 *
 * @author  Rayan RPG for L2Emu Project !
 * 
 * @since 688
 */
public class AdminSevenSignsInfo implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS = { 
		//seven signs cycle
		"admin_seven_signs_cycle",
		"admin_seven_signs_period",

		//festival related command.
		"admin_festival_time",
		"admin_festival_start",
		"admin_festival_end",

		//npcs spawn related
		"admin_spawn_orators", 
		"admin_spawn_preachers",
		"admin_unspawn_orators", 
		"admin_unspawn_preachers",

		//crests spawns related
		"admin_spawn_dusk_crest",
		"admin_spawn_dawn_crest",
		"admin_uspawn_dawn_crest",
		"admin_uspawn_dusk_crest",

		//blacksmisth
		"admin_spawn_mammon",
		"admin_uspawn_mammon",

		//special bosses in 7s
		"admin_spawn_lilith",
		"admin_spawn_anakim",
		"admin_unspawn_lilith",
		"admin_unspawn_anakim",
	};


	private static final int REQUIRED_LEVEL = Config.GM_GODMODE;

    //TODO: finish this handler.
	/**
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#useAdminCommand(java.lang.String, net.sf.l2j.gameserver.model.actor.instance.L2PcInstance)
	 */
	protected static Log _log = LogFactory.getLog(AdminSevenSignsInfo.class.getName());
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;
		}
		if (command.startsWith(""))
		{
		}
		else if (command.startsWith(""))
		{
			_log.info("");

		}
		else  if (command.startsWith(""));
		{
		}
		return false;

	}
	/**
	 * @see net.sf.l2j.gameserver.handler.IAdminCommandHandler#getAdminCommandList()
	 */
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
}