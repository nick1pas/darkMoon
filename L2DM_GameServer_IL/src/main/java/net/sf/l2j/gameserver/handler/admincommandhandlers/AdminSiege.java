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
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.AuctionManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.zone.IZone;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.RestartType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 * This class handles all siege commands:
 * Todo: change the class name, and neaten it up
 * 
 *
 */
public class AdminSiege implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{   
		"admin_siege",
		"admin_add_attacker",
		"admin_add_defender",
		"admin_add_guard",
		"admin_list_siege_clans", 
		"admin_clear_siege_list",
		"admin_move_defenders",
		"admin_spawn_doors",
		"admin_endsiege",
		"admin_startsiege",
		"admin_setcastle", 
		"admin_removecastle",
		"admin_clanhall",
		"admin_clanhallset",
		"admin_clanhalldel",
		"admin_clanhallopendoors",
		"admin_clanhallclosedoors",
		"admin_clanhallteleportself"
	};
	private static final int REQUIRED_LEVEL = Config.GM_NPC_EDIT;

	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (admin.getAccessLevel() < REQUIRED_LEVEL || !admin.isGM()) {return false;}

		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command

		// Get castle
		Castle castle = null;
		ClanHall clanhall = null;
		if (command.startsWith("admin_clanhall"))
			clanhall = ClanHallManager.getInstance().getClanHall(Integer.parseInt(st.nextToken()));
		else if (st.hasMoreTokens())
			castle = CastleManager.getInstance().getCastleByName(st.nextToken());
		// Get castle
		String val = "";
		if (st.hasMoreTokens())
			val = st.nextToken();
		if ((castle == null  || castle.getCastleId() < 0) && clanhall == null)
			// No castle specified
			showCastleSelectPage(admin);
		else
		{
			L2Object target = admin.getTarget();
			L2PcInstance player = null;
			if (target instanceof L2PcInstance)
				player = (L2PcInstance)target;

			if (command.equalsIgnoreCase("admin_add_attacker"))
			{
				if (player == null)
					admin.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.getSiege().registerAttacker(player,true);
			}
			else if (command.equalsIgnoreCase("admin_add_defender"))
			{
				if (player == null)
					admin.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.getSiege().registerDefender(player,true);
			}
			else if (command.equalsIgnoreCase("admin_add_guard"))
			{
				try
				{
					int npcId = Integer.parseInt(val);
					castle.getSiege().getSiegeGuardManager().addSiegeGuard(admin, npcId);
				}
				catch (Exception e)
				{
					admin.sendMessage("Usage: //add_guard npcId");
				}
			}
			else if (command.equalsIgnoreCase("admin_clear_siege_list"))
			{
				castle.getSiege().clearSiegeClan();
			}
			else if (command.equalsIgnoreCase("admin_endsiege"))
			{
				castle.getSiege().endSiege();
			}
			else if (command.equalsIgnoreCase("admin_list_siege_clans"))
			{
				castle.getSiege().listRegisterClan(admin);
				return true;
			}
			else if (command.equalsIgnoreCase("admin_move_defenders"))
			{
				admin.sendPacket(SystemMessage.sendString("Not implemented yet."));
			}
			else if (command.equalsIgnoreCase("admin_setcastle"))
			{
				if (player == null || player.getClan() == null)
					admin.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else
					castle.setOwner(player.getClan());
			}
			else if (command.equalsIgnoreCase("admin_removecastle"))
			{
				L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				if (clan != null)
					castle.removeOwner(clan);
				else
					admin.sendMessage("Unable to remove castle");
			}
			else if (command.equalsIgnoreCase("admin_clanhallset"))
			{
				if (player == null || player.getClan() == null)
					admin.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
				else if(!ClanHallManager.getInstance().isFree(clanhall.getId()))
					admin.sendMessage("This ClanHall isn't free!");
				else if(player.getClan().getHasHideout() == 0)
				{
					ClanHallManager.getInstance().setOwner(clanhall.getId(), player.getClan());
					if(AuctionManager.getInstance().getAuction(clanhall.getId()) != null)
						AuctionManager.getInstance().getAuction(clanhall.getId()).deleteAuctionFromDB();
				}
				else
					admin.sendMessage("You have already a ClanHall!");
			}
			else if (command.equalsIgnoreCase("admin_clanhalldel"))
			{
				if(!ClanHallManager.getInstance().isFree(clanhall.getId())){
					ClanHallManager.getInstance().setFree(clanhall.getId());
					AuctionManager.getInstance().initNPC(clanhall.getId());
				}else
					admin.sendMessage("This ClanHall is already Free!");
			}
			else if (command.equalsIgnoreCase("admin_clanhallopendoors"))
			{
				clanhall.openCloseDoors(true);
			}
			else if (command.equalsIgnoreCase("admin_clanhallclosedoors"))
			{
				clanhall.openCloseDoors(false);
			}
			else if (command.equalsIgnoreCase("admin_clanhallteleportself"))
			{
				IZone zone = clanhall.getZone();
				int[] coord;
				if (zone != null)
				{
					admin.teleToLocation(zone.getRestartPoint(RestartType.RestartRandom), true); 
				}
			}
			else if (command.equalsIgnoreCase("admin_spawn_doors"))
			{
				castle.spawnDoor();
			}
			else if (command.equalsIgnoreCase("admin_startsiege"))
			{
				castle.getSiege().startSiege();
			}
			if (clanhall != null)
				showClanHallPage(admin, clanhall);
			else
				showSiegePage(admin, castle.getName());
		}
		return true;
	}

	private void showCastleSelectPage(L2PcInstance admin)
	{
		int i=0;
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/castles_menu2.htm");
		//L2EMU_EDIT
		TextBuilder cList = new TextBuilder();
		for (Castle castle: CastleManager.getInstance().getCastles().values())
		{
			if (castle != null)
			{
				String name=castle.getName();
				cList.append("<td fixwidth=90><a action=\"bypass -h admin_siege "+name+"\">"+name+"</a></td>");
				i++;
			}
			if (i>2)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%castles%", cList.toString());
		cList.clear();
		i=0;
		for (ClanHall clanhall: ClanHallManager.getInstance().getClanHalls().values())
		{
			if (clanhall != null)
			{
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall "+clanhall.getId()+"\">");
				cList.append(clanhall.getName()+"</a></td>");
				i++;
			}
			if (i>1)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%clanhalls%", cList.toString());
		cList.clear();
		i=0;
		for (ClanHall clanhall: ClanHallManager.getInstance().getFreeClanHalls().values())
		{
			if (clanhall != null)
			{
				cList.append("<td fixwidth=134><a action=\"bypass -h admin_clanhall "+clanhall.getId()+"\">");
				cList.append(clanhall.getName()+"</a></td>");
				i++;
			}
			if (i>1)
			{
				cList.append("</tr><tr>");
				i=0;
			}
		}
		adminReply.replace("%freeclanhalls%", cList.toString());
		admin.sendPacket(adminReply);
	}

	private void showSiegePage(L2PcInstance admin, String castleName)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/castle_menu.htm");
		//L2EMU_EDIT
		adminReply.replace("%castleName%", castleName);
		admin.sendPacket(adminReply);
	}

	private void showClanHallPage(L2PcInstance admin, ClanHall clanhall)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/clanhall_menu.htm");
		//L2EMU_EDIT
		adminReply.replace("%clanhallName%", clanhall.getName());
		adminReply.replace("%clanhallId%", String.valueOf(clanhall.getId()));
		L2Clan owner = ClanTable.getInstance().getClan(clanhall.getOwnerId()); 
		if (owner == null)
			adminReply.replace("%clanhallOwner%","None");
		else
			adminReply.replace("%clanhallOwner%",owner.getName());
		admin.sendPacket(adminReply);
	}

	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}