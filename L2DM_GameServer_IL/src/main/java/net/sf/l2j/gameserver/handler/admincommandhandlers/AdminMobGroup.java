/* This program is free software; you can redistribute it and/or modify
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
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.MobGroup;
import net.sf.l2j.gameserver.model.MobGroupTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.util.Broadcast;

/**
 *<b> This class handles following admin commands: </b><br><br>
 *
 * <li> admin_mobmenu = <br>
 * <li> admin_mobgroup_list = <br>
 * <li> admin_mobgroup_create = <br>
 * <li> admin_mobgroup_remove = <br>
 * <li> admin_mobgroup_delete = <br>
 * <li> admin_mobgroup_spawn = <br>
 * <li> admin_mobgroup_unspawn = <br>
 * <li> admin_mobgroup_kill = <br>
 * <li> admin_mobgroup_idle = <br>
 * <li> admin_mobgroup_attack  = <br>
 * <li> admin_mobgroup_rnd = <br>
 * <li> admin_mobgroup_return = <br>
 * <li> admin_mobgroup_follow = <br>
 * <li> admin_mobgroup_casting = <br>
 * <li> admin_mobgroup_nomove = <br>
 * <li> admin_mobgroup_attackgrp = <br>
 * <li> admin_mobgroup_invul = <br>
 * <li> admin_mobinst = <br><br>
 *
 *<b> Usage: </b><br><br> 
 *
 * <li> //mobmenu  <br>
 * <li> //mobgroup_list <br>
 * <li> //mobgroup_create <br>
 * <li> //mobgroup_remove <br>
 * <li> //mobgroup_delete <br>
 * <li> //mobgroup_spawn <br>
 * <li> //mobgroup_unspawn <br>
 * <li> //mobgroup_kill <br>
 * <li> //mobgroup_idle <br>
 * <li> //mobgroup_attack  <br>
 * <li> //mobgroup_rnd <br>
 * <li> //mobgroup_return <br>
 * <li> //mobgroup_follow <br>
 * <li> //mobgroup_casting <br>
 * <li> //mobgroup_nomove <br>
 * <li> //mobgroup_attackgrp <br>
 * <li> //mobgroup_invul <br>
 * <li> //mobinst <br><br>
 * 
 *  @author littlecrow
 *  
 */
public class AdminMobGroup implements IAdminCommandHandler 
{
	private static final String[] ADMIN_COMMANDS = 
	{ 
		"admin_mobmenu", 
		"admin_mobgroup_list",
		"admin_mobgroup_create",
		"admin_mobgroup_remove",
		"admin_mobgroup_delete",
		"admin_mobgroup_spawn",
		"admin_mobgroup_unspawn",
		"admin_mobgroup_kill",
		"admin_mobgroup_idle",
		"admin_mobgroup_attack", 
		"admin_mobgroup_rnd",
		"admin_mobgroup_return",
		"admin_mobgroup_follow",
		"admin_mobgroup_casting",
		"admin_mobgroup_nomove",
		"admin_mobgroup_attackgrp",
		"admin_mobgroup_invul",
		"admin_mobinst"
	};

	private static final int REQUIRED_LEVEL = Config.GM_MIN;

	public boolean useAdminCommand(String command, L2PcInstance admin) 
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM()))
				return false;

		if (command.equals("admin_mobmenu")) 
		{
			showMainPage(admin,command);
			return true;
		}			
		else if (command.equals("admin_mobinst")) 
		{
			showMainPage(admin,command);
			return true;
		}
		else if (command.equals("admin_mobgroup_list")) 
			showGroupList(admin);
		else if (command.startsWith("admin_mobgroup_create"))
			createGroup(command, admin);
		else if (command.startsWith("admin_mobgroup_delete") || 
				command.startsWith("admin_mobgroup_remove"))
			removeGroup(command, admin);
		else if (command.startsWith("admin_mobgroup_spawn"))
			spawnGroup(command, admin);
		else if (command.startsWith("admin_mobgroup_unspawn"))
			unspawnGroup(command, admin);
		else if (command.startsWith("admin_mobgroup_kill"))
			killGroup(command, admin);
		else if (command.startsWith("admin_mobgroup_attackgrp"))
			attackGrp(command, admin);
		else if (command.startsWith("admin_mobgroup_attack")) 
		{
			if (admin.getTarget() instanceof L2Character) 
			{
				L2Character target = (L2Character) admin.getTarget();
				attack(command, admin, target);
			}
		}
		else if (command.startsWith("admin_mobgroup_rnd"))
			setNormal(command, admin);
		else if (command.startsWith("admin_mobgroup_idle"))
			idle(command, admin);
		else if (command.startsWith("admin_mobgroup_return"))
			returnToChar(command, admin);
		else if (command.startsWith("admin_mobgroup_follow"))
			follow(command, admin, admin);
		else if (command.startsWith("admin_mobgroup_casting"))
			setCasting(command, admin);
		else if (command.startsWith("admin_mobgroup_nomove"))
			noMove(command, admin);
		else if (command.startsWith("admin_mobgroup_invul"))
			invul(command, admin);
		else if (command.startsWith("admin_mobgroup_teleport"))
			teleportGroup(command, admin);

		showMainPage(admin,command);
		return true;
	}

	/** 
	 * @param admin 
	 */ 
	private void showMainPage(L2PcInstance admin, String command) 
	{ 
		// L2EMU DISABLE
		/* String filename = "mobgroup.htm";
		if (command.contains("mobinst")) 
			filename = "mobgrouphelp.htm"; 
		AdminHelpPage.showHelpPage(admin, filename);*/ 
		// L2EMU DISABLE END
		// L2EMU ADD BEGIN
		if (command.contains("mobinst")) 
		{
			AdminHelpPage.showHelpPage(admin, "mobgrouphelp.htm");
		}
		else
		{
	        AdminHelpPage.showSubMenuPage(admin, "mobgroup_menu.htm");
		}
		// L2EMU ADD END

	} 

	private void returnToChar(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]); 
		} 
		catch (Exception e) {
			admin.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.returnGroup(admin);
	}

	private void idle(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e) 
		{
			admin.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.setIdleMode();
	}

	private void setNormal(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.setAttackRandom();
	}

	private void attack(String command, L2PcInstance admin, L2Character target) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.setAttackTarget(target);
	}

	private void follow(String command, L2PcInstance admin, L2Character target) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.setFollowMode(target);
	}

	private void createGroup(String command, L2PcInstance admin) 
	{
		int groupId;
		int templateId;
		int mobCount;

		try
		{
			String[] cmdParams = command.split(" ");

			groupId = Integer.parseInt(cmdParams[1]); 
			templateId = Integer.parseInt(cmdParams[2]);
			mobCount = Integer.parseInt(cmdParams[3]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_create <group> <npcid> <count>");
			return;
		}

		if (MobGroupTable.getInstance().getGroup(groupId) != null)
		{
			admin.sendMessage("Mob group " + groupId + " already exists.");
			return;
		}

		L2NpcTemplate template = NpcTable.getInstance().getTemplate(templateId);

		if (template == null)
		{
			admin.sendMessage("Invalid NPC ID specified.");
			return;
		}

		MobGroup group = new MobGroup(groupId, template, mobCount);
		MobGroupTable.getInstance().addGroup(groupId, group);

		admin.sendMessage("Mob group " + groupId + " created.");
	}

	private void removeGroup(String command, L2PcInstance admin)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]); 
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_remove <groupId>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		doAnimation(admin);
		group.unspawnGroup();

		if (MobGroupTable.getInstance().removeGroup(groupId))
			admin.sendMessage("Mob group " + groupId + " unspawned and removed.");
	}

	private void spawnGroup(String command, L2PcInstance admin) 
	{
		int groupId;
		boolean topos = false;
		int posx = 0;
		int posy = 0;
		int posz = 0;

		try
		{
			String[] cmdParams = command.split(" ");
			groupId = Integer.parseInt(cmdParams[1]);

			try
			{ // we try to get a position
				posx = Integer.parseInt(cmdParams[2]);
				posy = Integer.parseInt(cmdParams[3]);
				posz = Integer.parseInt(cmdParams[4]);
				topos = true;
			} 
			catch (Exception e)
			{ 
				// no position given
			}
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_spawn <group> [ x y z ]");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		doAnimation(admin);

		if (topos)
			group.spawnGroup(posx, posy, posz);
		else
			group.spawnGroup(admin);

		admin.sendMessage("Mob group " + groupId + " spawned.");
	}

	private void unspawnGroup(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_unspawn <groupId>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		doAnimation(admin);
		group.unspawnGroup();

		admin.sendMessage("Mob group " + groupId + " unspawned.");
	}

	private void killGroup(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_kill <groupId>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		doAnimation(admin);
		group.killGroup(admin);
	}

	private void setCasting(String command, L2PcInstance admin) 
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]); 
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_casting <groupId>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.setCastMode();
	}

	private void noMove(String command, L2PcInstance admin) 
	{
		int groupId;
		String enabled;

		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_nomove <groupId> <on|off>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}

		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
			group.setNoMoveMode(true);
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
			group.setNoMoveMode(false);
		else 
			admin.sendMessage("Incorrect command arguments.");
	}

	private void doAnimation(L2PcInstance admin) 
	{
		Broadcast.toSelfAndKnownPlayersInRadius(admin, new MagicSkillUser(admin, 1008, 1, 4000, 0), 2250000/*1500*/);
		admin.sendPacket(new SetupGauge(0, 4000));
	}

	private void attackGrp(String command, L2PcInstance admin) 
	{
		int groupId;
		int othGroupId;

		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			othGroupId = Integer.parseInt(command.split(" ")[2]);
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_attackgrp <groupId> <TargetGroupId>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		MobGroup othGroup = MobGroupTable.getInstance().getGroup(othGroupId);
		if (othGroup == null) 
		{
			admin.sendMessage("Incorrect target group.");
			return;
		}

		group.setAttackGroup(othGroup);
	}

	private void invul(String command, L2PcInstance admin) 
	{
		int groupId;
		String enabled;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		} 
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_invul <groupId> <on|off>");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}

		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
			group.setInvul(true);
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
			group.setInvul(false);
		else
			admin.sendMessage("Incorrect command arguments.");
	}

	private void teleportGroup(String command, L2PcInstance admin)
	{
		int groupId;
		String targetPlayerStr = null;
		L2PcInstance targetPlayer = null;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			targetPlayerStr = command.split(" ")[2];

			if (targetPlayerStr != null)
				targetPlayer = L2World.getInstance().getPlayer(targetPlayerStr);
			if (targetPlayer == null)
				targetPlayer = admin;
		}
		catch (Exception e)
		{
			admin.sendMessage("Usage: //mobgroup_teleport <groupId> [playerName]");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null) 
		{
			admin.sendMessage("Invalid group specified.");
			return;
		}
		group.teleportGroup(admin);
	}

	private void showGroupList(L2PcInstance admin)
	{
		MobGroup[] mobGroupList = MobGroupTable.getInstance().getGroups();

		admin.sendMessage("======= <Mob Groups> =======");

		for (MobGroup mobGroup : mobGroupList)
			admin.sendMessage(mobGroup.getGroupId() + ": " + mobGroup.getActiveMobCount() + " alive out of " +  mobGroup.getMaxMobCount() + 
					" of NPC ID " + mobGroup.getTemplate().getNpcId() + " (" + mobGroup.getStatus() + ")");

		admin.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_FOOTER));
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