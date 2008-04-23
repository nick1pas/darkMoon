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

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.AdminForgePacket;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;

/**
 * This class handles commands for gm to forge packets
 * 
 * @author Maktakien
 *
 */
public class AdminPForge implements IAdminCommandHandler
{
    //private final static Log _log = LogFactory.getLog(AdminPForge.class);
    private static final String[] ADMIN_COMMANDS = {"admin_forge","admin_forge2","admin_forge3" };
    private static final int REQUIRED_LEVEL = Config.GM_MIN;
	

	public boolean useAdminCommand(String command, L2PcInstance admin)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
        	
    		if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
                return false;
        
		if (command.equals("admin_forge"))
		{
			showMainPage(admin);
		}
		else if (command.startsWith("admin_forge2"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				String format = st.nextToken();
				showPage2(admin,format);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				admin.sendMessage("Usage: //forge2 format");
			}            
		}
		else if (command.startsWith("admin_forge3"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				String format = st.nextToken();
				boolean broadcast = false;
				if(format.toLowerCase().equals("broadcast"))
				{
					format = st.nextToken();
					broadcast = true;
				}
				AdminForgePacket sp = new AdminForgePacket();
				for(int i = 0; i < format.length();i++)
				{
					String val = st.nextToken();
					if(val.toLowerCase().equals("$objid"))
					{
						val = String.valueOf(admin.getObjectId());
					}
					else if(val.toLowerCase().equals("$tobjid"))
					{
						val = String.valueOf(admin.getTarget().getObjectId());
					}
					else if(val.toLowerCase().equals("$bobjid"))
					{
						if(admin.getBoat() != null)
						{
							val = String.valueOf(admin.getBoat().getObjectId());
						}                		  
					}
					else if(val.toLowerCase().equals("$clanid"))
					{
						val = String.valueOf(admin.getCharId());
					}
					else if(val.toLowerCase().equals("$allyid"))
					{
						val = String.valueOf(admin.getAllyId());
					}
					else if(val.toLowerCase().equals("$tclanid"))
					{
						val = String.valueOf(((L2PcInstance) admin.getTarget()).getCharId());
					}
					else if(val.toLowerCase().equals("$tallyid"))
					{
						val = String.valueOf(((L2PcInstance) admin.getTarget()).getAllyId());
					}
					else if(val.toLowerCase().equals("$x"))
					{
						val = String.valueOf(admin.getX());
					}
					else if(val.toLowerCase().equals("$y"))
					{
						val = String.valueOf(admin.getY());
					}
					else if(val.toLowerCase().equals("$z"))
					{
						val = String.valueOf(admin.getZ());
					} 
					else if(val.toLowerCase().equals("$heading"))
					{
						val = String.valueOf(admin.getHeading());
					} 
					else if(val.toLowerCase().equals("$tx"))
					{
						val = String.valueOf(admin.getTarget().getX());
					}
					else if(val.toLowerCase().equals("$ty"))
					{
						val = String.valueOf(admin.getTarget().getY());
					}
					else if(val.toLowerCase().equals("$tz"))
					{
						val = String.valueOf(admin.getTarget().getZ());
					}
					else if(val.toLowerCase().equals("$theading"))
					{
						val = String.valueOf(((L2PcInstance) admin.getTarget()).getHeading());
					} 

					sp.addPart(format.getBytes()[i],val);
				}
				if(broadcast == true)
				{
					admin.broadcastPacket(sp);
				}
				else
				{
					if(admin.getTarget() == null)
						admin.sendPacket(sp);
					else if(admin.getTarget() instanceof L2PcInstance)
						((L2PcInstance)admin.getTarget()).sendPacket(sp);
				}
				showPage3(admin,format,command);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return true;
	}

	private void showMainPage(L2PcInstance admin)
	{
		//L2EMU_EDIT
		AdminHelpPage.showSubMenuPage(admin, "pforge_menu1.htm");
		//L2EMU_EDIT
	}

	private void showPage2(L2PcInstance admin,String format)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/pforge_menu2.htm");
		//L2EMU_EDIT
		adminReply.replace("%format%", format);
		TextBuilder replyMSG = new TextBuilder();
		for(int i = 0; i < format.length();i++)
			replyMSG.append(format.charAt(i)+" : <edit var=\"v"+i+"\" width=100><br1>");
		adminReply.replace("%valueditors%", replyMSG.toString());
		replyMSG.clear();
		for(int i = 0; i < format.length();i++)
			replyMSG.append(" \\$v"+i);
		adminReply.replace("%send%", replyMSG.toString());
		admin.sendPacket(adminReply);
	}

	private void showPage3(L2PcInstance admin,String format,String command)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/pforge_menu3.htm");
		//L2EMU_EDIT
		adminReply.replace("%format%", format);
		adminReply.replace("%command%", command);
		admin.sendPacket(adminReply); 
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