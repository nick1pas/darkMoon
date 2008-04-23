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
package net.sf.l2j.gameserver.model.actor.instance;

import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.BuffTemplateTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.entity.events.CTF;
import net.sf.l2j.gameserver.model.entity.events.DM;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.services.ThreadService;
import net.sf.l2j.gameserver.services.WindowService;
import net.sf.l2j.gameserver.templates.L2BuffTemplate;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * This Class Manages L2Donator Buffer<br>
 * an especial instance for players donate for server admin :)<br>
 * in db admin will regfister player name and only that names can take buffs <br>
 * 
 * @author Rayan
 */

public class L2DonatorBufferInstance extends L2NpcInstance
{

	/** bypass type */
	private byte _bypassType;

	/**
	 * @param objectId
	 * @param template
	 */
	public L2DonatorBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}
	/**
	 * Loads the main npc window(based on id)
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#getHtmlPath(int, int)
	 */
	 public String getHtmlPath(int npcId, int val)
		{
			String pom = "";

			if (val == 0) pom = "" + npcId;
			else pom = npcId + "-" + val;

			return HtmlPathService.DONATOR_BUFFER_HTML_PATH + pom + ".htm";
		}
	/**
	 * Checks if Player is Donator
	 * @param player
	 * @return
	 */
	private boolean checkDonatorState(L2PcInstance player)
	{
		if(player.isDonator())
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	/**
	 * 
	 * @param player
	 */
	private void SendNonDonatorWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename = HtmlPathService.DONATOR_BUFFER_HTML_PATH+"non-donator.htm";
		html.setFile(filename);
		if (filename != null)
		{ 
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%playername%", player.getName());
			player.sendPacket(html);
		}
	}
	/**
	 * 
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#onBypassFeedback(net.sf.l2j.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */
	public void onBypassFeedback(L2PcInstance player, String command)
	{      
		if(command.equalsIgnoreCase("BuyProphetBuffs")) //Profhets Bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 0;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyDances")) //Dances Bypass 
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 1;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-dances.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuySongs")) //Songs Bypass  
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 2;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-songs.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOrcBuffs")) //Orc Bypass  
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 4;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-orc.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOtherBuffs"))//other bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 9;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-other.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("return_to_main_window")) //dirty hack to come back to main window
		{   
			int npcID = getNpcId();  
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());//will add proper code soon was too lazy for atm :P
			String filename = HtmlPathService.DONATOR_BUFFER_HTML_PATH+ npcID+".htm";
			html.setFile(filename);
			if(filename!=null)
			{
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html);
			}
		}
		if(command.equalsIgnoreCase("BuyCubics")) //Cubics Bypass  
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 5;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-cubics.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuySummonBuffs"))//summon buffs bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 6;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-summons.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyHeroBuffs"))//hero bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 7;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-hero.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyNobleBuffs"))//noble bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			_bypassType = 8;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-noble.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("ExchangeMySP"))//exchange sp bypass
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-sp.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%playerlevel%", String.valueOf(player.getLevel()));
			html.replace("%playersp%", String.valueOf(player.getSp()));
			player.sendPacket(html);
		}

		else if(command.startsWith("cancel"))
		{
			cancel(player);
			player.sendMessage("all your buffs has been removed.");
		}
		/**
		 * @Prophet Buffs
		 * Structure (target, buffname, resendhtml)
		 */
		else if(command.startsWith("Greater Empower"))
			MakeVipBuffs(player, "Greater Empower", true);

		else if(command.startsWith("Acumen"))
			MakeVipBuffs(player, "Acumen", true);

		else if(command.startsWith("Focus"))
			MakeVipBuffs(player,"Focus", true);

		else if(command.startsWith("Greater Might"))
			MakeVipBuffs(player, "Greater Might", true);

		else if(command.startsWith("Greater Shield"))
			MakeVipBuffs(player, "Greater Shield", true);

		else if(command.startsWith("Berserker Spirit"))
			MakeVipBuffs(player, "Berserker Spirit", true);

		else if(command.startsWith("Invigor"))
			MakeVipBuffs(player, "Invigor", true);

		else if(command.startsWith("Mental Aegis"))
			MakeVipBuffs(player, "Mental Aegis", true);

		else if(command.startsWith("Magic Barrier"))
			MakeVipBuffs(player, "Magic Barrier", true);

		else if(command.startsWith("Regeneration"))
			MakeVipBuffs(player, "Regeneration", true);

		else if(command.startsWith("Blessed Body"))
			MakeVipBuffs(player, "Blessed Body", true);

		else if(command.startsWith("Blessed Soul"))
			MakeVipBuffs(player, "Blessed Soul", true);

		else if(command.startsWith("Greater Concentration"))
			MakeVipBuffs(player, "Greater Concentration", true);

		else if(command.startsWith("Haste"))
			MakeVipBuffs(player, "Haste", true);

		else if(command.startsWith("Agility"))
			MakeVipBuffs(player, "Agility", true);

		else if(command.startsWith("Wind Walk"))
			MakeVipBuffs(player, "Wind Walk", true);

		else if(command.startsWith("Guidance"))
			MakeVipBuffs(player, "Guidance", true);

		else if(command.startsWith("Death Whisper"))
			MakeVipBuffs(player, "Death Whisper", true);

		else if(command.startsWith("Vampiric Rage"))
			MakeVipBuffs(player, "Vampiric Rage", true);

		else if(command.startsWith("Bless Shield"))
			MakeVipBuffs(player, "Bless Shield", true);

		//****************** CASTS ALL BUFFS IN A UNIQUE COMMAND *****************/
		else if(command.startsWith("ProphetSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullPMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("DancesSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullDMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("SongsSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullSMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("OverlordSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullOvMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("WarcryerSupport"))
		{
			makeFullWaMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("ShamanSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullShaMagic(player);
			ResendMainWindow(player);
		}
		else if (command.startsWith("CubicsSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullCuMagic(player);
			ResendMainWindow(player);
		}
		else if (command.startsWith("SummonsSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullSuMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("NobleSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullNobleMagic(player);
			ResendMainWindow(player);
		}
		else if(command.startsWith("HeroSupport"))
		{
			if(!checkDonatorState(player))
			{
				SendNonDonatorWindow(player);
				return;
			}
			makeFullHeroMagic(player);
			ResendMainWindow(player);
		}
		//******************************************************************************************************
		/**
		 * @Dances Buffs
		 * Structure (target, buffname, resendhtml)
		 */

		else if(command.startsWith("Dance of Warrior"))
			MakeVipBuffs(player, "Dance of Warrior",true);

		else if(command.startsWith("Dance of Inspiration"))
			MakeVipBuffs(player, "Dance of Inspiration",true);

		else if(command.startsWith("Dance of Mystic"))
			MakeVipBuffs(player, "Dance of Mystic",true);

		else if(command.startsWith("Dance of Fire"))
			MakeVipBuffs(player, "Dance of Fire",true);

		else if(command.startsWith("Dance of Fury"))
			MakeVipBuffs(player, "Dance of Fury",true);

		else if(command.startsWith("Dance of Concentration"))
			MakeVipBuffs(player, "Dance of Concentration",true);

		else if(command.startsWith("Dance of Light"))
			MakeVipBuffs(player, "Dance of Light",true);

		else if(command.startsWith("Dance of Aqua Guard"))
			MakeVipBuffs(player, "Dance of Aqua Guard",true);

		else if(command.startsWith("Dance of Earth Guard"))
			MakeVipBuffs(player, "Dance of Earth Guard",true);

		else if(command.startsWith("Dance of Vampire"))
			MakeVipBuffs(player, "Dance of Vampire",true);

		else if(command.startsWith("Dance of Protection"))
			MakeVipBuffs(player, "Dance of Protection",true);

		else if(command.startsWith("Dance of Siren"))
			MakeVipBuffs(player, "Dance of Siren",true);

		else if(command.startsWith("Dance of Shadow"))
			MakeVipBuffs(player, "Dance of Shadow",true);


		//TODO: Missing Dances: of Resist,nihil, weakness
		/**
		 * @Songs Buffs
		 * Structure (L2Pcinstance player,String buffname, boolean ResendHTML)
		 */
		else if(command.startsWith("Song of Earth"))
			MakeVipBuffs(player, "Song of Earth",true);

		else if(command.startsWith("Song of Life"))
			MakeVipBuffs(player, "Song of Life",true);

		else if(command.startsWith("Song of Water"))
			MakeVipBuffs(player, "Song of Water",true);

		else if(command.startsWith("Song of Warding"))
			MakeVipBuffs(player, "Song of Warding",true);

		else if(command.startsWith("Song of Wind"))
			MakeVipBuffs(player, "Song of Wind",true);

		else if(command.startsWith("Song of Hunter"))
			MakeVipBuffs(player, "Song of Hunter",true);

		else if(command.startsWith("Song of Invocation"))
			MakeVipBuffs(player, "Song of Invocation",true);

		else if(command.startsWith("Song of Vitality"))
			MakeVipBuffs(player, "Song of Vitality",true);

		else if(command.startsWith("Song of Vengeance"))
			MakeVipBuffs(player, "Song of Vengeance",true);

		else if(command.startsWith("Song of Flame Guard"))
			MakeVipBuffs(player, "Song of Flame Guard",true);

		else if(command.startsWith("Song of Storm Guard"))
			MakeVipBuffs(player, "Song of Storm Guard",true);

		else if(command.startsWith("Song of Renewal"))
			MakeVipBuffs(player, "Song of Renewal",true);

		else if(command.startsWith("Song of Meditation"))
			MakeVipBuffs(player, "Song of Meditation",true);

		else if(command.startsWith("Song of Champion"))
			MakeVipBuffs(player, "Song of Champion",true);

		/*
		 * TODO: Missing in Skill Trees
		 * else if(command.startsWith("Song of Seduce"))
         MakeVipBuffs("Song of Seduce", Config.NPC_BUFFER_PRICE_PER_SONG, 4536, 1, player, true, false);
         else if(command.startsWith("Song of Sweet Whisper"))
         MakeVipBuffs("Song of Sweet Whisper", Config.NPC_BUFFER_PRICE_PER_SONG, 4537, 1, player, true, false);
         else if(command.startsWith("Song of Temptation"))
         MakeVipBuffs("Song of Temptation", Config.NPC_BUFFER_PRICE_PER_SONG, 4538, 1, player, true, false);
		 */

		/**
		 * @Orc Buffs
		 */
		//**************** SHAMAN BUFFS ************************************
		else if(command.startsWith("Flame Chant"))   
			MakeVipBuffs(player,"Flame Chant",true);

		else if(command.startsWith("Pa'agrian Gift"))  
			MakeVipBuffs(player,"Pa'agrian Gift",true);

		else if(command.startsWith("Blessings of Pa'agrio"))   
			MakeVipBuffs(player,"Blessings of Pa'agrio",true);

		else if(command.startsWith("Chant of Fire"))   
			MakeVipBuffs(player,"Chant of Fire",true);

		else if(command.startsWith("Chant of Battle"))   
			MakeVipBuffs(player,"Chant of Battle",true);

		else if(command.startsWith("Chant of Shielding")) 
			MakeVipBuffs(player,"Chant of Shielding",true);

		else if(command.startsWith("Soul Shield"))
			MakeVipBuffs(player,"Soul Shield",true);
		//*************************************************************

		//************ OVERLORD  BUFFS ******************************
		else if(command.startsWith("The Wisdom of Pa'agrio"))   
			MakeVipBuffs(player,"The Wisdom of Pa'agrio",true);

		else if(command.startsWith("The Glory of Pa'agrio"))    //overlord
			MakeVipBuffs(player,"The Glory of Pa'agrio",true);

		else if(command.startsWith("The Vision of Pa'agrio"))    //overlord
			MakeVipBuffs(player,"The Vision of Pa'agrio",true);

		else if(command.startsWith("Under the Protection of Pa'agrio"))   
			MakeVipBuffs(player,"Under the Protection of Pa'agrio",true);

		else if(command.startsWith("The Heart of Pa'agrio"))   
			MakeVipBuffs(player,"The Heart of Pa'agrio",true);

		else if(command.startsWith("The Tact of Pa'agrio"))    //overlord
			MakeVipBuffs(player,"The Tact of Pa'agrio",true);

		else if(command.startsWith("The Rage of Pa'agrio"))    //overlord
			MakeVipBuffs(player,"The Rage of Pa'agrio",true);

		else if(command.startsWith("The Honor of Pa'agrio"))    //overlord
			MakeVipBuffs(player,"The Honor of Pa'agrio",true);

		else if(command.startsWith("The Eye of Pa'agrio"))//overlord
			MakeVipBuffs(player,"The Eye of Pa'agrio",true);

		else if(command.startsWith("The Soul of Pa'agrio"))//overlord
			MakeVipBuffs(player,"The Soul of Pa'agrio",true);

		//****************** WARCRYER BUFFS *******************************************************
		else if(command.startsWith("Chant of Fury"))    //warcryer
			MakeVipBuffs(player,"Chant of Fury",true);

		else if(command.startsWith("Chant of Evasion"))    //warcryer
			MakeVipBuffs(player,"Chant of Evasion",true);

		else if(command.startsWith("Chant of Rage"))    //warcryer
			MakeVipBuffs(player,"Chant of Rage",true);

		else if(command.startsWith("Chant of Revenge"))    //warcryer
			MakeVipBuffs(player,"Chant of Revenge",true);

		else if(command.startsWith("Chant of Predator"))//warcryer
			MakeVipBuffs(player,"Chant of Predator",true);

		else if(command.startsWith("Chant of Eagle"))//warcryer
			MakeVipBuffs(player,"Chant of Eagle",true);

		else if(command.startsWith("Chant of Vampire"))//warcryer
			MakeVipBuffs(player,"Chant of Vampire",true);

		else if(command.startsWith("Chant of Spirit")) //warcryer
			MakeVipBuffs(player,"Chant of Spirit",true);

		else if(command.startsWith("Chant of Victory"))//warcryer
			MakeVipBuffs(player,"Chant of Victory",true);

		else if(command.startsWith("War Chant"))//warcryer
			MakeVipBuffs(player,"War Chant",true);

		else if(command.startsWith("Earth Chant"))//warcryer
			MakeVipBuffs(player,"Earth Chant",true);

		//***************************************************************************************************************

		//*************** CUBICS ***************************
		else if(command.startsWith("Summon Phantom Cubic"))
			MakeVipBuffs(player,"Summon Phantom Cubic",true);

		else if(command.startsWith("Summon Vampiric Cubic"))
			MakeVipBuffs(player,"Summon Vampiric Cubic",true);

		else if(command.startsWith("Summon Spark Cubic"))
			MakeVipBuffs(player,"Summon Spark Cubic",true);

		else if(command.startsWith("Summon Aqua Cubic"))
			MakeVipBuffs(player,"Summon Aqua Cubic",true);

		else if(command.startsWith("Summon Viper Cubic"))
			MakeVipBuffs(player,"Summon Viper Cubic",true);

		else if(command.startsWith("Summon Storm Cubic"))
			MakeVipBuffs(player,"Summon Storm Cubic",true);

		else if(command.startsWith("Summon Life Cubic"))
			MakeVipBuffs(player,"Summon Life Cubic",true);

		else if(command.startsWith("Summon Binding Cubic"))
			MakeVipBuffs(player,"Summon Binding Cubic",true);
		//************************************************

		/** 
		 * @Summons Buffs
		 */
		else if (command.startsWith("Blessing of Queen"))
			MakeVipBuffs(player,"Blessing of Queen" ,true);

		else if (command.startsWith("Gift of Queen"))
			MakeVipBuffs(player,"Gift of Queen" ,true);

		else if (command.startsWith("Blessing of Seraphim"))
			MakeVipBuffs(player,"Blessing of Seraphim" ,true);

		else if (command.startsWith("Gift of Serapfim"))
			MakeVipBuffs(player,"Gift of Serapfim" ,true);

		else if (command.startsWith("Cure of Queen"))
			MakeVipBuffs(player,"Cure of Queen" ,true);

		else if (command.startsWith("Cure of Seraphim"))
			MakeVipBuffs(player,"Cure of Seraphim" ,true);

		/**
		 * @Hero Buffs
		 */
		else if(command.startsWith("Heroic Miracle"))
			MakeVipBuffs(player,"Heroic Miracle", true);

		else if(command.startsWith("Heroic Berserker"))
			MakeVipBuffs(player,"Heroic Berserker", true);

		else if(command.startsWith("Heroic Valor"))
			MakeVipBuffs(player,"Heroic Valor", true);


		/**
		 * @Noble Buffs
		 */
		else if(command.startsWith("Blessing of Noblesse"))
			MakeVipBuffs(player,"Blessing of Noblesse", true);

		else if(command.startsWith("Fortune of Noblesse"))
			MakeVipBuffs(player,"Fortune of Noblesse", true);


		//*********** OTHER BUFFS ****************/
		else if(command.startsWith("prophecy of water"))
			MakeVipBuffs(player, "prophecy of water", true);

		else if(command.startsWith("prophecy of fire"))
			MakeVipBuffs(player, "prophecy of fire", true);

		else if(command.startsWith("prophecy of wind"))
			MakeVipBuffs(player, "prophecy of wind", true);

		else if(command.startsWith("wild magic"))
			MakeVipBuffs(player, "wild magic", true);

		else if(command.startsWith("might"))
			MakeVipBuffs(player, "might", true);

		else if(command.startsWith("shield"))
			MakeVipBuffs(player, "shield", true);

		else if(command.startsWith("unholy resistence"))
			MakeVipBuffs(player, "unholy resistence", true);

		else if(command.startsWith("holy resistence"))
			MakeVipBuffs(player, "holy resistence", true);

		else if(command.startsWith("elemental protection"))
			MakeVipBuffs(player, "elemental protection", true);

		else if(command.startsWith("divine protection"))
			MakeVipBuffs(player, "divine protection", true);

		else if(command.startsWith("Resist Shock"))
			MakeVipBuffs(player, "Resist Shock", true);

		else if(command.startsWith("arcane protection"))
			MakeVipBuffs(player, "arcane protection", true);

		else if(command.startsWith("body of avatar"))
			MakeVipBuffs(player, "body of avatar", true);
		//***************************************************************
		/**
		 * @Other Features from NPC Buffer
		 */
		else if(command.startsWith("RestoreDonatorCP")&& player.isDonator())
			RestoreDonatorCP(player);
		else if(command.startsWith("RestoreDonatorHP")&& player.isDonator())
			RestoreDonatorHP(player);
		else if(command.startsWith("RestoreDonatorMP")&& player.isDonator())
			RestoreDonatorMP(player);


		/**
		 * @exchange sp
		 */
		else if(command.startsWith("Exchange500SP")&& player.isDonator())
			ValidateSpCount(player, 500, 5593);
		else if(command.startsWith("Exchange5000SP")&& player.isDonator())
			ValidateSpCount(player, 5000, 5594);
		else if(command.startsWith("Exchange100kSP")&& player.isDonator())
			ValidateSpCount(player, 0x186a0, 5595);
	}
	/**
	 * 
	 * @param player
	 */
	private void RestoreDonatorCP(L2PcInstance player)
	{
		if(player.isDonator())
		{
			SendRestoreWindow(player);
			player.getStatus().setCurrentCp(player.getMaxCp());
		}
		else
		{
			SendNonDonatorWindow(player);
			return;
		}
	}
	/**
	 * 
	 * @param player
	 */
	private void RestoreDonatorHP(L2PcInstance player)
	{
		if(player.isDonator())
		{
			player.getStatus().setCurrentHp(player.getMaxHp());
			SendRestoreWindow(player);
		}
		else
		{
			SendNonDonatorWindow(player);
			return;
		}
	}
	/**
	 * 
	 * @param player
	 */
	private void RestoreDonatorMP(L2PcInstance player)
	{
		if(player.isDonator())
		{
			player.getStatus().setCurrentMp(player.getMaxMp());
			SendRestoreWindow(player);
		}
		else
		{
			SendNonDonatorWindow(player);
			return;
		}
	}
	/**
	 * valites effects
	 *
	 */
	protected void ValidateEffect()
	{
		L2Effect Effect=null;
		if (Effect != null) 
			Effect.setInUse(true); 
	}
	/**
	 * function to send the main donator npc page for player.
	 * @param player
	 */
	protected void ResendMainWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int npcID = getNpcId();
		String filename = HtmlPathService.DONATOR_BUFFER_HTML_PATH+npcID+".htm";
		html.setFile(filename);
		if(filename!=null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * function to send an informal window to playrr when restore stats
	 * @param player
	 */
	protected void SendRestoreWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.DONATOR_BUFFER_HTML_PATH+"restore.htm");
		html.setHtml(filename);
		if (filename != null)
		{   
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * casts all prophets buffs in a unique command.
	 * @param target
	 */
	public void makeFullPMagic(L2PcInstance target)
	{	
		// Prevent a cursed weapon weilder of being buffed
		if (target.isCursedWeaponEquiped())
			return;

		int _id =  BuffTemplateTable.getInstance().getTemplateIdByName("ProphetSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullDMagic(L2PcInstance target)
	{
		// Prevent a cursed weapon weilder of being buffed
		if (target.isCursedWeaponEquiped())
			return;

		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("DanceSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullSMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("SongSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullOvMagic(L2PcInstance target)
	{

		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("OverlordSupport");

		if (_id  == 0) return;


		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullWaMagic(L2PcInstance target)
	{

		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("WarcryerSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullSuMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("SummonSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullCuMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("CubicsSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullShaMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("ShamanSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullNobleMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("NobleSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * casts all buffs in a unique command.
	 * @param target
	 */
	public void makeFullHeroMagic(L2PcInstance target)
	{
		int _id = BuffTemplateTable.getInstance().getTemplateIdByName("HeroSupport");

		if (_id  == 0) return;

		makeBuffs(target,_id);
	}
	/**
	 * Cast buffs on player, this function ignore target type
	 * only buff effects are aplied to player
	 * 
	 * @param player Target player
	 * @param buffTemplate Name of buff template
	 * @param resendHTML 
	 */
	public void MakeVipBuffs(L2PcInstance player, String buffTemplate, boolean resendHTML)
	{
		int _templateId = 0;
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int npcID = getNpcId();
		try
		{
			_templateId = Integer.parseInt(buffTemplate); 
		}
		catch (NumberFormatException  e)
		{
			_templateId = BuffTemplateTable.getInstance().getTemplateIdByName(buffTemplate);
		}

		if(resendHTML)
		{
			switch (_bypassType)
			{ 
			case 0://prophets
			{ 
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
				break;
			}
			case 1: //dances
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-dances.htm").toString());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
				break;
			}
			case 2: //songs
			{  
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-songs.htm").toString());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName()); 
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html);
				break;
			}
			case 4: //orc
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-orc.htm").toString());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html);
				break;

			}
			case 5: //cubics
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-cubics.htm").toString());
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html); 
				break;
			}
			case 6:// summons buffs
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-summons.htm").toString());
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html); 
				break;
			}
			case 7://hero
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-hero.htm").toString());
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
				break;
			}
			case 8://noble
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-noble.htm").toString());
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
				break;
			}
			case 9://other
			{
				html.setFile((new StringBuilder(HtmlPathService.DONATOR_BUFFER_HTML_PATH)).append(npcID).append("-other.htm").toString());
				html.replace("%npcname%", String.valueOf(getName()));
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%playername%", player.getName());
				player.sendPacket(html);
				break;
			}
			default:
				WindowService.sendWindow(player, HtmlPathService.HTML_PATH, "default.htm");
				break;
			}
			if (_templateId > 0 && player.isDonator()) makeVipBuffs(player, _templateId); 

		}
	}

	/**
	 * 
	 * @param player
	 * @param string,int _templateId
	 * @param ResendHTML
	 */
	public void makeVipBuffs(L2PcInstance player, int _templateId)
	{	
		int _priceTotal = 0;

		if(Config.ALLOW_NPC_BUFFER && player.isDonator())
		{
			if (player == null) return;

			FastList<L2BuffTemplate> _templateBuffs = new  FastList<L2BuffTemplate>();

			_templateBuffs = BuffTemplateTable.getInstance().getBuffTemplate(_templateId);

			if (_templateBuffs == null  || _templateBuffs.size() == 0) return;

			this.setTarget(player);

			//dont buffs dead or fake dead player
			if (player.isDead()|| player.isAlikeDead()) 
			{
				player.sendPacket(new SystemMessage(SystemMessageId.NOTHING_HAPPENED));
				return;
			}
			if(Config.TVT_DISABLE_NPC_BUFFER && player._inEventTvT && Config.TVT_ON_START_REMOVE_ALL_EFFECTS && TvT._started && !player.isGM())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Buffs are not allowed while in event.");
				player.sendPacket(sm);
				sm = null;
				return;
			}
			if(Config.CTF_DISABLE_NPC_BUFFER && player._inEventCTF && Config.CTF_ON_START_REMOVE_ALL_EFFECTS && CTF._started && !player.isGM())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Buffs are not allowed while in event.");
				player.sendPacket(sm);
				sm = null;
				return;
			}
			if(Config.DM_DISABLE_NPC_BUFFER && player._inEventDM && Config.DM_ON_START_REMOVE_ALL_EFFECTS && DM._started && !player.isGM())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Buffs are not allowed while in event.");
				player.sendPacket(sm);
				sm = null;
				return;
			}
			for (L2BuffTemplate _buff:_templateBuffs)
			{
				if ( _buff.checkPlayer(player) && _buff.checkPrice(player)) 
				{
					if (player.getInventory().getAdena() >= (_priceTotal + _buff.getAdenaPrice()))
					{
						_priceTotal+=_buff.getAdenaPrice();
						player.reduceAdena("NpcBuffer", _priceTotal, player.getLastFolkNPC(), true);

						if (_buff.forceCast() || player.getFirstEffect(_buff.getSkill()) == null)
						{
							if(Config.REGENERATE_STATS_FOR_FREE)
							{   
								//regenerates stats if enabled.
								getStatus().setCurrentHpMp(getMaxHp(), getMaxMp());
							}

							// yes, its not for all skills right, but atleast player. will know 
							// for what he paid =)
							SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT); 
							sm.addSkillName(_buff.getSkill().getId());
							player.sendPacket(sm);
							sm = null;

							if (_buff.getSkill().getTargetType() == SkillTargetType.TARGET_SELF)
							{
								L2Effect oldEffect = player.getFirstEffect(_buff.getSkill().getId());
								
								//remove old effects so we can update all efefcts in progress
								if(oldEffect!=null)
								oldEffect.exit();

								//disables animation if config disabled
								if(!Config.DISABLE_ANIMATION)
								{
									// Ignore skill cast time, using 100ms for NPC buffer's animation
									MagicSkillUser msu = new MagicSkillUser(player, player, _buff.getSkill().getId(), _buff.getSkill().getLevel(), 100, 0);
									broadcastPacket(msu);
								}
								for (L2Effect effect : _buff.getSkill().getEffectsSelf(player))
								{
									player.addEffect(effect);
									player.updateEffectIcons();
								}
								// hack for newbie summons
								if (_buff.getSkill().getSkillType() == SkillType.SUMMON)
								{
									player.doCast(_buff.getSkill());
									ValidateEffect();
								}
							}
							else
							{   
                                  L2Effect oldEffect = player.getFirstEffect(_buff.getSkill().getId());
								
								//remove old effects so we can update all efefcts in progress
                                if(oldEffect!=null)
								oldEffect.exit();

								//disables animation if config disabled
								if(!Config.DISABLE_ANIMATION)
								{
								// Ignore skill cast time, using 100ms for NPC buffer's animation
								MagicSkillUser msu = new MagicSkillUser(this, player, _buff.getSkill().getId(), _buff.getSkill().getLevel(), 100, 0);
								broadcastPacket(msu);
								}
							}

							for (L2Effect effect : _buff.getSkill().getEffects(this, player))
							{
								player.addEffect(effect);
								ValidateEffect();
							}
							try {
								Thread.sleep(ThreadService.BUFFER_CAST_DELAY_TIMER);//sleep between casts
							}
							catch(InterruptedException ex) {
							}

							player.reduceAdena("NpcBuffer", _priceTotal, player.getLastFolkNPC(), true);
						}
					}

				}
			}
		}
	}
	/**
	 * valiudates player sp count
	 * @param player
	 * @param requiredSpCount
	 * @param itemID
	 */
	private void ValidateSpCount(L2PcInstance player, int requiredSpCount, int itemID)
	{
		int CurrentyPlayerSpCount = player.getSp();

		if(CurrentyPlayerSpCount < requiredSpCount)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			return;
		}
		else
		{
			tradeSpPointsForSpScrolls(player, itemID, 1);
			player.setSp(player.getSp() - requiredSpCount * Config.SP_MULTIPLIER);
			player.sendMessage((new StringBuilder("You have selled ")).append(requiredSpCount).append(" SP").toString());
			StatusUpdate su = new StatusUpdate(player.getObjectId());
			su.addAttribute(StatusUpdate.SP, player.getSp());
			sendPacket(su);
			return;
		}
	}
	/**
	 * Function Used to Trade SP Points For Sp Scrools.
	 * @param player
	 * @param itemID
	 * @param count
	 */
	private void tradeSpPointsForSpScrolls(L2PcInstance player, int ItemID, int count)
	{
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), ItemID);
		if(item == null)
		{
			return;
		}
		else
		{
			item.setCount(count);
			player.getInventory().addItem("ReduceSP", item, player, player);
			SystemMessage smsg = new SystemMessage(SystemMessageId.S1_S2);
			smsg.addNumber(count);
			smsg.addItemName(item.getItemId());
			player.sendPacket(smsg);
			ItemList il = new ItemList(player, false);
			player.sendPacket(il);
			return;
		}
	}
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		// TODO Auto-generated method stub
	}
}