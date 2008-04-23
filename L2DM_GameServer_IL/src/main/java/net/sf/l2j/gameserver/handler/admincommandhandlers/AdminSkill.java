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
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2SkillLearn;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_show_skills <br>
 * <li> admin_remove_skills <br>
 * <li> admin_skill_list <br>
 * <li> admin_skill_index <br>
 * <li> admin_add_skill <br>
 * <li> admin_remove_skill <br>
 * <li> admin_get_skills <br>
 * <li> admin_reset_skills<br>
 * <li> admin_give_all_skills <br>
 * <li> admin_remove_all_skills  <br>
 * <li> admin_add_clan_skills<br>
 * <li> admin_cast_skill <br><br>

 * <b> Usage: </b><br><br>
 * 
 * <li> //show_skills <br>
 * <li> //remove_skills <br>
 * <li> //skill_list <br>
 * <li> //skill_index <br>
 * <li> //add_skill <br>
 * <li> //remove_skill <br>
 * <li> //get_skills <br>
 * <li> //reset_skills<br>
 * <li> //give_all_skills <br>
 * <li> //remove_all_skills  <br>
 * <li> //add_clan_skills<br>
 * <li> //cast_skill <br><br>
 * 
 * @version $Revision: 1.2.4.7 $ $Date: 2005/04/11 10:06:02 $
 */
public class AdminSkill implements IAdminCommandHandler 
{
	private final static Log _log = LogFactory.getLog(AdminSkill.class.getName());

	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_show_skills",
		"admin_remove_skills",
		"admin_skill_list",
		"admin_skill_index",
		"admin_add_skill",
		"admin_remove_skill",
		"admin_get_skills",
		"admin_reset_skills",
		"admin_give_all_skills",
		"admin_remove_all_skills",
		"admin_ench_skills",
		"admin_add_clan_skill",
		// L2JFREE
		"admin_cast_skill"
	};
	private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;
	private static final int REQUIRED_LEVEL2 = Config.GM_CHAR_EDIT_OTHER;

	private static L2Skill[] adminSkills;

	public boolean useAdminCommand(String command, L2PcInstance admin) 
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;

		if (command.equals("admin_show_skills"))
		{
			showMainPage(admin);
		}
		else if (command.startsWith("admin_remove_skills"))
		{
			try {
				String val = command.substring(20);
				removeSkillsPage(admin, Integer.parseInt(val));
			}
			catch (StringIndexOutOfBoundsException e){}
		}
		else if (command.startsWith("admin_skill_list"))
		{
			//L2EMU_EDIT
			AdminHelpPage.showSubMenuPage(admin, "skills_menu.htm");
			//L2EMU_EDIT
		}
		else if (command.startsWith("admin_skill_index"))
		{
			try
			{
				String val = command.substring(18);
				AdminHelpPage.showHelpPage(admin, "skills/" + val + ".htm");
			}
			catch (StringIndexOutOfBoundsException e){}
		}
		else if (command.startsWith("admin_add_skill"))
		{
			try
			{
				String val = command.substring(15); 
				if (admin == admin.getTarget() || admin.getAccessLevel() >= REQUIRED_LEVEL2)
					adminAddSkill(admin, val);
			}
			catch (Exception e)
			{
				admin.sendMessage("Usage: //add_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_remove_skill"))
		{
			try
			{
				String id = command.substring(19);
				int idval = Integer.parseInt(id);
				if (admin == admin.getTarget() || admin.getAccessLevel() >= REQUIRED_LEVEL2)
					adminRemoveSkill(admin, idval);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("Usage: //remove_skill <skill_id>");
			}
		}
		else if (command.equals("admin_get_skills"))
		{
			adminGetSkills(admin);
		}
		else if (command.equals("admin_reset_skills"))
		{
			if (admin == admin.getTarget() || admin.getAccessLevel() >= REQUIRED_LEVEL2)
				adminResetSkills(admin);
		}
		else if (command.equals("admin_give_all_skills"))
		{
			if (admin == admin.getTarget() || admin.getAccessLevel() >= REQUIRED_LEVEL2)
				adminGiveAllSkills(admin);
		}

		else if (command.equals("admin_remove_all_skills"))
		{
			if (admin.getTarget() instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance)admin.getTarget();
				for (L2Skill skill : player.getAllSkills())
					player.removeSkill(skill);
				admin.sendMessage("You removed all skills from " + player.getName());
				player.sendMessage("Admin removed all skills from you.");
				  player.sendSkillList();
			}
		}
		else if (command.equals("admin_ench_skills"))
		{
			admin.sendMessage("Not implemented yet.");
		}
		else if (command.startsWith("admin_add_clan_skill"))
		{
			try
			{
				String[] val = command.split(" ");
				if (admin == admin.getTarget() || admin.getAccessLevel() >= REQUIRED_LEVEL2)
					adminAddClanSkill(admin, Integer.parseInt(val[1]),Integer.parseInt(val[2]));
			}
			catch (Exception e)
			{
				admin.sendMessage("Usage: //add_clan_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_cast_skill"))
		{
			castSkill(admin, command.substring(17));
		}
		return true;
	}

	/**
	 * This function will give all the skills that the target can learn at his/her level
	 * @param admin: the gm char
	 */
	private void adminGiveAllSkills(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		boolean countUnlearnable = true;
		int unLearnable = 0;
		int skillCounter = 0;
		L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableSkills(player, player.getClassId());
		while(skills.length > unLearnable)
		{
			for (L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if (sk == null || !sk.getCanLearn(player.getClassId()))
				{
					if(countUnlearnable)
						unLearnable++;
					continue;
				}
				if(player.getSkillLevel(sk.getId()) == -1)
					skillCounter++;
				player.addSkill(sk, true);
			}
			countUnlearnable = false;
			skills = SkillTreeTable.getInstance().getAvailableSkills(player, player.getClassId());
		}
		//Notify player and admin
		if(player != admin)
		player.sendMessage("A GM gave you " + skillCounter + " skills.");
		admin.sendMessage("You gave " + skillCounter + " skills to " + player.getName());
	}
	private void removeSkillsPage(L2PcInstance admin, int page)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
			return;
		}

		L2Skill[] skills = player.getAllSkills();

		int MaxSkillsPerPage = 10;
		int MaxPages = skills.length / MaxSkillsPerPage;
		if (skills.length > MaxSkillsPerPage * MaxPages)
			MaxPages++;

		if (page>MaxPages)
			page = MaxPages;

		int SkillsStart = MaxSkillsPerPage*page;
		int SkillsEnd = skills.length;
		if (SkillsEnd - SkillsStart > MaxSkillsPerPage)
			SkillsEnd = SkillsStart + MaxSkillsPerPage;

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);		
		TextBuilder replyMSG = new TextBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_edit "+player.getName()+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing <font color=\"LEVEL\">" + player.getName() + "</font></center>");
		replyMSG.append("<br><table width=270><tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().getClassName() + "</td></tr></table>");
		replyMSG.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
		replyMSG.append("<tr><td>ruin the game...</td></tr></table>");
		replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
		replyMSG.append("<br>");
		String pages = "<center><table width=270><tr>";
		for (int x=0; x<MaxPages; x++)
		{
			int pagenr = x + 1;
			pages += "<td><a action=\"bypass -h admin_remove_skills " + x + "\">Page " + pagenr + "</a></td>";
		}
		pages += "</tr></table></center>";
		replyMSG.append(pages);
		replyMSG.append("<br><table width=270>");		
		replyMSG.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		for (int i = SkillsStart; i < SkillsEnd; i++)
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill "+skills[i].getId()+"\">"+skills[i].getName()+"</a></td><td width=60>"+skills[i].getLevel()+"</td><td width=40>"+skills[i].getId()+"</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br><center><table>");
		replyMSG.append("Remove skill by ID :");
		replyMSG.append("<tr><td>Id: </td>");
		replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
		replyMSG.append("</table></center>");		
		replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_edit "+player.getName()+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		admin.sendPacket(adminReply);
	}

	private void showMainPage(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		//L2EMU_EDIT
		adminReply.setFile("data/html/admin/menus/submenus/charskills_menu.htm");
		//L2EMU_EDIT
		adminReply.replace("%name%",player.getName());
		adminReply.replace("%level%",String.valueOf(player.getLevel()));
		adminReply.replace("%class%",player.getTemplate().getClassName());
		admin.sendPacket(adminReply);
	}

	private void adminGetSkills(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{			
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		if (player.getName().equals(admin.getName()))
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ON_YOURSELF));
		else
		{
			L2Skill[] skills = player.getAllSkills();
			adminSkills = admin.getAllSkills();
			for (L2Skill element : adminSkills)
				admin.removeSkill(element);
			for (L2Skill element : skills)
				admin.addSkill(element, true);
			admin.sendMessage("You now have all the skills of "+player.getName()+".");
			admin.sendSkillList();
		}
		showMainPage(admin);
	}

	private void adminResetSkills(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		if (adminSkills==null)
			admin.sendMessage("You must get the skills of someone in order to do this.");
		else
		{
			L2Skill[] skills = player.getAllSkills();
			for (L2Skill element : skills)
				 player.removeSkill(element);
			for (int i=0;i<admin.getAllSkills().length;i++)
				player.addSkill(admin.getAllSkills()[i], true);
			for (L2Skill element : skills)
				admin.removeSkill(element);
			for (L2Skill element : adminSkills)
				admin.addSkill(element, true);
			player.sendMessage("[GM]"+admin.getName()+" updated your skills.");
			admin.sendMessage("You now have all your skills back.");
			adminSkills=null;
			admin.sendSkillList();
		}
		showMainPage(admin);
	}

	private void adminAddSkill(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			showMainPage(admin);
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		StringTokenizer st = new StringTokenizer(val);
		if (st.countTokens()!=2)
		{
			showMainPage(admin);
		}
		else
		{
			L2Skill skill=null;
			try
			{
				String id = st.nextToken();
				String level = st.nextToken();
				int idval = Integer.parseInt(id);
				int levelval = Integer.parseInt(level);
				skill = SkillTable.getInstance().getInfo(idval,levelval);
			}
			catch (Exception e) {}
			if (skill != null)
			{
				String name = skill.getName();
				player.sendMessage("Admin gave you the skill "+name+".");
				player.addSkill(skill, true);
				//Admin information	
				admin.sendMessage("You gave the skill "+name+" to "+player.getName()+".");
				if (_log.isDebugEnabled())
					_log.debug("[GM]"+admin.getName()+" gave skill "+name+" to "+player.getName()+".");
				player.sendSkillList();
			}
			else
				admin.sendMessage("Error: there is no such skill.");
			showMainPage(admin); //Back to start
		}
	}

	private void adminRemoveSkill(L2PcInstance admin, int idval)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		L2Skill skill = SkillTable.getInstance().getInfo(idval,player.getSkillLevel(idval));
		if (skill != null)
		{
			String skillname = skill.getName();
			player.sendMessage("Admin removed the skill "+skillname+" from your skills list.");
			player.removeSkill(skill);
			//Admin information	
			admin.sendMessage("You removed the skill "+skillname+" from "+player.getName()+".");
			if (_log.isDebugEnabled())
				_log.debug("[GM]"+admin.getName()+" removed skill "+skillname+" from "+player.getName()+".");
			player.sendSkillList();
		}
		else
			admin.sendMessage("Error: there is no such skill.");
		removeSkillsPage(admin, 0); //Back to previous page	
	}

	private void adminAddClanSkill(L2PcInstance admin, int id, int level)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			showMainPage(admin);
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		if (!player.isClanLeader())
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER).addString(player.getName()));
			showMainPage(admin);
			return;
		}
		if ((id < 370)|| (id > 391) || (level<1) || (level>3))
		{
			admin.sendMessage("Usage: //add_clan_skill <skill_id> <level>");
			showMainPage(admin);
			return;
		}
		else
		{
			L2Skill skill = SkillTable.getInstance().getInfo(id,level);
			if (skill != null)
			{
				String skillname = skill.getName();
				SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED);
				sm.addSkillName(id);
				player.sendPacket(sm);
				player.getClan().broadcastToOnlineMembers(sm);
				player.getClan().addNewSkill(skill);
				admin.sendMessage("You gave the Clan Skill: "+skillname+" to the clan "+player.getClan().getName()+".");
				admin.getClan().broadcastToOnlineMembers(new PledgeSkillList(admin.getClan()));
				for(L2PcInstance member: admin.getClan().getOnlineMembers(""))
				{
					member.sendSkillList();
				}
				showMainPage(admin);
				return;
			}
			else
			{
				admin.sendMessage("Error: there is no such skill.");
				return;
			}
		}
	}

	public void castSkill(L2PcInstance admin, String val)
	{
		int skillid = Integer.parseInt(val);
		L2Skill skill = SkillTable.getInstance().getInfo(skillid, 1);
		if (skill != null)
		{
			if (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF)
			{
				admin.setTarget(admin);
				MagicSkillUser msk = new MagicSkillUser(admin, skillid, 1, skill.getHitTime() , skill.getReuseDelay());
				admin.broadcastPacket(msk);
				if (_log.isDebugEnabled()) _log.debug("showing self skill, id: "+skill.getId()+" named: "+skill.getName());
			}
			else if (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_ONE)
			{
				if (_log.isDebugEnabled()) _log.debug("showing ATTACK skill, id: "+skill.getId()+" named: "+skill.getName());               
			}
		}
		else
		{
			if (_log.isDebugEnabled()) _log.debug("no such skill id: "+skillid);
			ActionFailed af = new ActionFailed();
			admin.broadcastPacket(af);
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