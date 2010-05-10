/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import javolution.text.TextBuilder;

import com.l2jfree.gameserver.handler.IAdminCommandHandler;
import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminBuffs implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_getbuffs",
		"admin_stopbuff",
		"admin_stopallbuffs",
		"admin_areacancel"
	};
	
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_getbuffs"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();
			
			if (st.hasMoreTokens())
			{
				L2PcInstance player = null;
				String playername = st.nextToken();
				
				try
				{
					player = L2World.getInstance().getPlayer(playername);
				}
				catch (Exception e)
				{
				}
				
				if (player != null)
				{
					showBuffs(player, activeChar);
					return true;
				}

				activeChar.sendMessage("The player " + playername + " is not online");
				return false;
			}
			else if ((activeChar.getTarget() != null) && (activeChar.getTarget() instanceof L2PcInstance))
			{
				showBuffs((L2PcInstance) activeChar.getTarget(), activeChar);
				return true;
			}
			else
				return true;
		}
		
		else if (command.startsWith("admin_stopbuff"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				
				st.nextToken();
				String playername = st.nextToken();
				int SkillId = Integer.parseInt(st.nextToken());
				
				removeBuff(activeChar, playername, SkillId);
				return true;
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Failed removing effect: " + e.getMessage());
				activeChar.sendMessage("Usage: //stopbuff <playername> [skillId]");
				return false;
			}
		}
		else if (command.startsWith("admin_stopallbuffs"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			String playername = st.nextToken();
			if (playername != null)
			{
				removeAllBuffs(activeChar, playername);
				return true;
			}

			return false;
		}
		else if (command.startsWith("admin_areacancel"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			String val = st.nextToken();
			try
			{
				int radius = Integer.parseInt(val);
				
				for (L2Character knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius))
				{
					if ((knownChar instanceof L2PcInstance) && !(knownChar.equals(activeChar)))
						knownChar.stopAllEffects();
				}
				
				activeChar.sendMessage("All effects canceled within raidus " + radius);
				return true;
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Usage: //areacancel <radius>");
				return false;
			}
		}
		else
			return true;
		
	}
	
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	public void showBuffs(L2PcInstance player, L2PcInstance activeChar)
	{
		TextBuilder html = new TextBuilder();
		html.append("<html><center><font color=\"LEVEL\">Effects of "
		        + player.getName() + "</font><center><br>");
		
		L2Effect[] effects = player.getAllEffects();
		
		html.append("<table>");
		html.append("<tr><td width=200>Skill</td><td width=70>Action</td></tr>");
		
		for (L2Effect e : effects)
		{
			if (e != null)
			{
				html.append("<tr><td>"
				        + e.getSkill().getName()
				        + "</td><td><button value=\"Remove\" action=\"bypass -h admin_stopbuff "
				        + player.getName()
				        + " "
				        + String.valueOf(e.getSkill().getId())
				        + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
			}
		}
		
		html.append("</table><br>");
		html.append("<button value=\"Remove All\" action=\"bypass -h admin_stopallbuffs "
		        + player.getName()
		        + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		html.append("</html>");
		
		NpcHtmlMessage ms = new NpcHtmlMessage(1);
		ms.setHtml(html.toString());
		
		activeChar.sendPacket(ms);
	}
	
	private void removeBuff(L2PcInstance remover, String playername, int SkillId)
	{
		L2PcInstance player = null;
		try
		{
			player = L2World.getInstance().getPlayer(playername);
		}
		catch (Exception e)
		{
		}
		
		if ((player != null) && (SkillId > 0))
		{
			L2Effect[] effects = player.getAllEffects();
			
			for (L2Effect e : effects)
			{
				if ((e != null) && (e.getSkill().getId() == SkillId))
				{
					e.exit();
					remover.sendMessage("Removed " + e.getSkill().getName() + " level " + e.getSkill().getLevel() + " from " + playername);
				}
			}
			showBuffs(player, remover);
		}
	}
	
	private void removeAllBuffs(L2PcInstance remover, String playername)
	{
		L2PcInstance player = null;
		try
		{
			player = L2World.getInstance().getPlayer(playername);
		}
		catch (Exception e)
		{
		}
		
		if (player != null)
		{
			player.stopAllEffects();
			remover.sendMessage("Removed all effects from " + playername);
			showBuffs(player, remover);
		}
		else
		{
			remover.sendMessage("The player " + playername + " is not online");
		}
	}
	
}