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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <b> This class handles following admin commands: </b><br><br>
 * 
 * <li> admin_bookmark = 
 * <li> admin_show_moves =
 * <li> admin_show_moves_other =
 * <li> admin_show_teleport =
 * <li> admin_teleport_to_character =
 * <li> admin_teleportto =
 * <li> admin_move_to =
 * <li> admin_teleport_character =
 * <li> admin_recall =
 * <li> admin_recall_party =
 * <li> admin_recall_all = 
 * <li> admin_walk =
 * <li> admin_recall_npc",
 * <li> admin_go =
 * <li> admin_tele =
 * <li> admin_teleto = 
 * <li> admin_disable_gk = 
 * <li> admin_enable_gk = 
 * 
 * <b>Usage:</b><br><br>
 * 
 * <li> //bookmark <br>
 * <li> //show_moves <br>
 * <li> //show_moves_other <br>
 * <li> //show_teleport <br>
 * <li> //teleport_to_character <br>
 * <li> //teleportto <br>
 * <li> //move_to <br>
 * <li> //teleport_character <br>
 * <li> //recall <br>
 * <li> //recall_party <br>
 * <li> //recall_all <br> 
 * <li> //walk <br>
 * <li> //recall_npc <br>
 * <li> //go <br>
 * <li> //tele <br>
 * <li> //teleto <br><br>
 * 
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminTeleport implements IAdminCommandHandler 
{
	private static final Log _log = LogFactory.getLog(AdminTeleport.class.getName());
	
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_bookmark", // L2JP_JP ADD
		"admin_show_moves",
		"admin_show_moves_other",
		"admin_show_teleport",
		"admin_teleport_to_character",
		"admin_teleportto",
		"admin_move_to",
		"admin_teleport_character",
		"admin_recall",
		"admin_recall_gm", //[L2J_JP ADD - TSL]
		"admin_recall_party", //[L2J_JP ADD - TSL]
		"admin_recall_all", //[L2J_JP ADD - TSL]
		"admin_recall_offline",
		"admin_walk",
		"admin_recall_npc",
		"admin_go",
		"admin_tele",
		"admin_teleto"
		};
	
	private static final int REQUIRED_LEVEL = Config.GM_TELEPORT;
	private static final int REQUIRED_LEVEL2 = Config.GM_TELEPORT_OTHER;
	
	public boolean useAdminCommand(String command, L2PcInstance admin)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!(checkLevel(admin.getAccessLevel()) && admin.isGM())) return false;
		
		if (command.startsWith("admin_bookmark"))// L2J_JP ADD
		{
			bookmark(admin, command.substring(15));
		}
		
		else if (command.equals("admin_teleto"))
		{
			admin.setTeleMode(1);
		}
		else if (command.startsWith("admin_recall_offline "))
		{
			try{
				String[] param = command.split(" ");
				if (param.length<2){
					admin.sendMessage("Wrong usage: //recall_offline <player name>");
					return false;
				}
				changeCharacterPosition(admin, param[1]);
			}
			catch(Throwable t){			
			}
		}
		else if (command.equals("admin_teleto r"))
		{
			admin.setTeleMode(2);
		}
		
		else if (command.equals("admin_teleto end"))
		{
			admin.setTeleMode(0);
		}
		
		else if (command.equals("admin_show_moves"))
		{
			AdminHelpPage.showHelpPage(admin, "teleports.htm");
		}
		
		else if (command.equals("admin_show_moves_other"))
		{
			AdminHelpPage.showHelpPage(admin, "tele/areas/areas.html");
		}
		
		else if (command.equals("admin_show_teleport"))
		{
			showTeleportCharWindow(admin);
		}
		
		else if (command.equals("admin_recall_npc"))
		{
			recallNPC(admin);
		}
		
		else if (command.equals("admin_teleport_to_character"))
		{
			teleportToCharacter(admin, admin.getTarget());
		}
		//L2EMU_ADD
		else if (command.equals("admin_disable_gk"))
		{
			
		}
		else if (command.equals("admin_enable_gk"))
		{
			
		}
		//L2EMU_ADD
		else if (command.startsWith("admin_walk"))
		{
			try
			{
				String val = command.substring(11);
				StringTokenizer st = new StringTokenizer(val);
				String x1 = st.nextToken();
				int x = Integer.parseInt(x1);
				String y1 = st.nextToken();
				int y = Integer.parseInt(y1);
				String z1 = st.nextToken();
				int z = Integer.parseInt(z1);
				L2CharPosition pos = new L2CharPosition(x,y,z,0);
				admin.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,pos);
			}
			catch (Exception e)
			{
				if (_log.isDebugEnabled()) _log.info("admin_walk: "+e);
			}
		}
		else if (command.startsWith("admin_move_to"))
		{
			try
			{
				String val = command.substring(14);
				teleportTo(admin, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				//L2EMU_EDIT
				//Case of empty or missing coordinates
				AdminHelpPage.showTeleMenuPage(admin, "teleports.htm");
				//L2EMU_EDIT
			}		
		}
		else if (command.startsWith("admin_teleport_character"))
		{
			try
			{
				String val = command.substring(25); 
				
				if (admin.getAccessLevel()>=REQUIRED_LEVEL2)
					teleportCharacter(admin, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				//Case of empty coordinates
				admin.sendMessage("Wrong or no Coordinates given.");
				showTeleportCharWindow(admin); //back to character teleport
			}
		}
		else if (command.startsWith("admin_teleportto "))
		{
			try
			{
				String targetName = command.substring(17);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				teleportToCharacter(admin, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{ }
		}
		//L2EMU_EDIT_ADD_START
		else if (command.startsWith("admin_recall"))
		{
			try
			{
				String targetName = command.substring(13);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				
				if(player !=null && player.isInJail()){
					admin.sendMessage("sorry, player "+player.getName()+"is in Jail.");
					return false;
				}
				
				if (admin.getAccessLevel()>=REQUIRED_LEVEL2)
					teleportCharacter(player, admin.getX(), admin.getY(), admin.getZ());
				
				admin.sendMessage("recalled player "+player.getName());
			}
			catch (StringIndexOutOfBoundsException e)
			{
				admin.sendMessage("please specify character name.");
			}
		}
		//L2EMU_EDIT_ADD_END
		// [L2J_JP ADD START - TSL]
		else if (command.startsWith("admin_recall_party "))
		{
			try
			{
				String targetName = command.substring(16);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				
				//L2EMU_ADD
				if(player !=null  && player.isInJail()){
					admin.sendMessage("sorry, player "+player.getName()+"is in Jail.");
					return false;
				}
				//L2EMU_ADD
				
				if (admin.getAccessLevel()>=REQUIRED_LEVEL2)
				{
					if (player.getParty() != null)
					{
						for (L2PcInstance character : player.getParty().getPartyMembers())
						{
							if (character == admin) continue;
							teleportCharacter(character, admin.getX(), admin.getY(), admin.getZ());
							admin.sendMessage("sucessfully recalled "+character.getName()+" party.");
						}
					}
					else
						admin.sendMessage("Wrong or Player is not in party.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{ }
		}
		else if (command.startsWith("admin_recall_gm ")){
			try{
				for (L2PcInstance player : L2World.getInstance().getAllPlayers())
					if (player.getAccessLevel()>0)
						teleportCharacter(player, admin.getX(), admin.getY(), admin.getZ());
			}

			catch (StringIndexOutOfBoundsException e)
			{ }
		}
		else if (command.equals("admin_recall_all"))
		{
			if (admin.getAccessLevel()>=REQUIRED_LEVEL2)
			{
				for (L2PcInstance character : L2World.getInstance().getAllPlayers())
				{
					//L2EMU_ADD
					if(character!=null && character.isInJail()){
						admin.sendMessage("sorry, player "+character.getName()+"is in Jail.");
						return false;
					}
					//L2EMU_ADD
					
					if (character == admin) continue;
					teleportCharacter(character, admin.getX(), admin.getY(), admin.getZ());
					
					admin.sendMessage("you recalled all server players.");
					//L2EMU_ADD
				}
			}
		}
		// [L2J_JP ADD END - TSL]
		else if (command.equals("admin_tele"))
		{
			showTeleportWindow(admin);
		}
		else if (command.startsWith("admin_go"))
		{
			int intVal=150;
			int x = admin.getX(),y = admin.getY(),z = admin.getZ();
			try
			{
				String val = command.substring(8);
				StringTokenizer st = new StringTokenizer(val);
				String dir=st.nextToken();
				if (st.hasMoreTokens())
					intVal = Integer.parseInt(st.nextToken());
				if (dir.equals("east"))
					x+=intVal;
				else if (dir.equals("west"))
					x-=intVal;
				else if (dir.equals("north"))
					y-=intVal;
				else if (dir.equals("south"))
					y+=intVal;
				else if (dir.equals("up"))
					z+=intVal;
				else if (dir.equals("down"))
					z-=intVal;
				admin.teleToLocation(x, y, z, false);
				showTeleportWindow(admin);
			}
			catch (Exception e) 
			{
				admin.sendMessage("Usage: //go<north|south|east|west|up|down> [offset] (default 150)");
			}
		}
		return true;
	}

	/**
	 * 
	 * @param admin
	 * @param Name
	 */
	// L2J_JP ADD
	private void bookmark(L2PcInstance admin, String Name)
	{
		File file = new File(Config.DATAPACK_ROOT+"/"+"data/html/admin/tele/bookmarks.txt");
		LineNumberReader lnr = null;
		String bookmarks = "";
		try
		{
			int i=0;
			String line = null;
			lnr = new LineNumberReader(new FileReader(file));
			while ( (line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,"\r\n");
				if (st.hasMoreTokens())
				{   
					bookmarks += st.nextToken();
					i++;
				}
			}
			if(Name.equals("show"))
			{
				FileInputStream fis = null;
				//L2EMU_EDIT
				fis = new FileInputStream(new File(Config.DATAPACK_ROOT+"/"+"data/html/admin/menus/submenus/bookmarks_menu.htm"));
				//L2EMU_EDIT
				byte[] raw = new byte[fis.available()];
				fis.read(raw);
				String content = new String(raw, "UTF-8");
				content = content.replaceAll("%bookmarks%", bookmarks);
				NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
				adminReply.setHtml(content);
				admin.sendPacket(adminReply);
				fis.close();
			}
			else
			{
				FileWriter save = new FileWriter(file);
				bookmarks += "<tr><td width=\"270\"><a action=\"bypass -h admin_move_to "+admin.getX()+" "+admin.getY()+" "+admin.getZ()+"\">"+Name+"</a></td></tr>\r\n";
				save.write(bookmarks);
				save.close();
				bookmark(admin, "show");
			}
		}
		catch (FileNotFoundException e)
		{
			admin.sendMessage("bookmarks.txt not found");
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			try
			{
				lnr.close();
			}
			catch (Exception e2)
			{}
		}
	}
	/**
	 * 
	 * @param admin
	 * @param Cords
	 */
	private void teleportTo(L2PcInstance admin, String Cords)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(Cords);
			String x1 = st.nextToken();
			int x = Integer.parseInt(x1);
			String y1 = st.nextToken();
			int y = Integer.parseInt(y1);
			String z1 = st.nextToken();
			int z = Integer.parseInt(z1);
			
			admin.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			admin.teleToLocation(x, y, z, false);
			
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("You have been teleported to " + Cords);
			admin.sendPacket(sm);
		}
		catch (NoSuchElementException nsee)
		{
			admin.sendMessage("Wrong or no Coordinates given.");
		}
	}
	/**
	 * 
	 * @param admin
	 */
	private void showTeleportWindow(L2PcInstance admin)
	{
		//L2EMU_EDIT
		AdminHelpPage.showSubMenuPage(admin, "move_menu.htm");
		//L2EMU_EDIT
	}
	
	/**
	 * 
	 * @param admin
	 */
	private void showTeleportCharWindow(L2PcInstance admin)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) 
		{
			player = (L2PcInstance)target;
		} 
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5); 
		
		TextBuilder replyMSG = new TextBuilder("<html><title>Teleport Character</title>");
		replyMSG.append("<body>");
		replyMSG.append("The character you will teleport is " + player.getName() + ".");
		replyMSG.append("<br>");
		
		replyMSG.append("Coordinate x");
		replyMSG.append("<edit var=\"char_cord_x\" width=110>");
		replyMSG.append("Coordinate y");
		replyMSG.append("<edit var=\"char_cord_y\" width=110>");
		replyMSG.append("Coordinate z");
		replyMSG.append("<edit var=\"char_cord_z\" width=110>");
		replyMSG.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");	
		replyMSG.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + admin.getX() + " " + admin.getY() + " " + admin.getZ() + "\" width=115 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		replyMSG.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center>");
		replyMSG.append("</body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		admin.sendPacket(adminReply);			
	}
	/**
	 * 
	 * @param admin
	 * @param Cords
	 */
	private void teleportCharacter(L2PcInstance admin , String Cords)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) 
		{
			player = (L2PcInstance)target;
		} 
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		
		if (player.getObjectId() == admin.getObjectId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ON_YOURSELF));
		}
		else
		{
			try
			{
				StringTokenizer st = new StringTokenizer(Cords);
				String x1 = st.nextToken();
				int x = Integer.parseInt(x1);
				String y1 = st.nextToken();
				int y = Integer.parseInt(y1);
				String z1 = st.nextToken();
				int z = Integer.parseInt(z1);
				teleportCharacter(player, x,y,z);
			}
			catch (NoSuchElementException nsee){}
		}
	}
	
	/**
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 */
	private void teleportCharacter(L2PcInstance player, int x, int y, int z)
	{
		if (player != null)
		{
			player.sendMessage("Admin is teleporting you.");
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			player.teleToLocation(x, y, z);
		}
	}
	/**
	 * 
	 * @param admin
	 * @param target
	 */
	private void teleportToCharacter(L2PcInstance admin, L2Object target)
	{
		L2PcInstance player = null;
		if (target != null && target instanceof L2PcInstance) 
		{
			player = (L2PcInstance)target;
		} 
		else 
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		
		if (player.getObjectId() == admin.getObjectId())
		{	
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ON_YOURSELF));
		}
		else
		{
			int x = player.getX();
			int y = player.getY();
			int z = player.getZ();
			
			admin.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			admin.teleToLocation(x, y, z);
			
			admin.sendMessage("You have teleported to character " + player.getName() + ".");
		}
	}
	/**
	 * 
	 * @param admin
	 */
	private void recallNPC(L2PcInstance admin)
	{
		L2Object obj = admin.getTarget();
		if ((obj != null) && (obj instanceof L2NpcInstance))
		{
			L2NpcInstance target = (L2NpcInstance) obj;

			int monsterTemplate = target.getTemplate().getNpcId();
			L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(monsterTemplate);
			if (template1 == null)
			{
				admin.sendMessage("Incorrect monster template.");
				_log.warn("ERROR: NPC " + target.getObjectId() + " has a 'null' template.");
				return;
			}

			L2Spawn spawn = target.getSpawn();
			
			if (spawn == null)
			{
				admin.sendMessage("Incorrect monster spawn.");
				_log.warn("ERROR: NPC " + target.getObjectId() + " has a 'null' spawn.");
				 return;
			}
			
			target.decayMe();
			spawn.setLocx(admin.getX());
			spawn.setLocy(admin.getY());
			spawn.setLocz(admin.getZ());
			spawn.setHeading(admin.getHeading());
			spawn.respawnNpc(target);
			SpawnTable.getInstance().updateSpawn(spawn);
		}
		else
		{
			admin.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
		}
	}
	private void changeCharacterPosition(L2PcInstance activeChar, String name){
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=? WHERE char_name=?");
			statement.setInt(1, activeChar.getX());
			statement.setString(2, name);
			statement.execute();
			statement = con.prepareStatement("UPDATE characters SET y=? WHERE char_name=?");
			statement.setInt(1, activeChar.getY());
			statement.setString(2, name);
			statement.execute();
			statement = con.prepareStatement("UPDATE characters SET z=? WHERE char_name=?");
			statement.setInt(1, activeChar.getZ());
			statement.setString(2, name);
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			if (count == 0)
				activeChar.sendMessage("Character not found or position unaltered.");
			else{
				activeChar.sendMessage("Character's position is now set to ("+activeChar.getX()+","+activeChar.getY()+","+activeChar.getZ()+")");
			}
		}
		catch (SQLException se)
		{
			activeChar.sendMessage("SQLException while changing offline character's position");
		}
		finally
		{
			try 
			{
				con.close();
			}
			catch (Exception e) {}
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