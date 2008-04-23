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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedItemTable;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedSkillTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.entity.events.CTF;
import net.sf.l2j.gameserver.model.entity.events.DM;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.services.ThreadService;
import net.sf.l2j.gameserver.services.WindowService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/** 
 * This Class Manages NPC Buffer <br>
 * 
 * <li> Implemented Dances 05/10/06 <br>
 * <li> Implemented Songs  08/10/06 <br>
 * <li> Implemented Full Prophet/Songs and Dances in a Click 19/10/2006 <br>
 * <li> Implemented Shaman/Overlord/Warcryer Buffs 02/11/06<br>
 * <li> Implemented Pop up 24/03/07 <br>
 * <li> Implemented Cubics 30/03/07 <br>
 * <li> Implemented Buffs from Summons/pets 30/03/07 <br>
 * <li> Implemented Noble/Hero Skills. <br>
 * <li> Converted code to correct Convension 28/08/07 <br>
 * <li> Changed all code to lower case to avoid typos.
 * 
 * 
 * @author Scar69
 * 
 * @Rewritten by Rayan RPG and Fenix <br>
 * 
 * @since 1.1.3
 */

public class L2NpcBufferInstance extends L2NpcInstance
{
	/** bypass type */
	private int _bypassType;

	/** required money */
	private int _requiredMoney;

	/**
	 * @param objectId
	 * @param template
	 */
	public L2NpcBufferInstance(int objectId, L2NpcTemplate template)
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

		return HtmlPathService.PLAYER_BUFFER_HTML_PATH + pom + ".htm";
	}

	/**
	 * 
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#onBypassFeedback(net.sf.l2j.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */
	public void onBypassFeedback(L2PcInstance player, String command)
	{      
		if(command.equalsIgnoreCase("BuyProphetBuffs") && Config.CAN_SEEL_PROPHET_BUFFS) //Prophets Bypass
		{

			_bypassType = 0;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%prophet_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_prophet_buffs_price%", String.valueOf(Config.FULL_PROPHET_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyDances") && Config.CAN_SEEL_DANCES) //Dances Bypass 
		{
			_bypassType = 1;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-dances.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%dance_price%", String.valueOf(Config.PRICE_PER_DANCE));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_dances_price%", String.valueOf(Config.FULL_DANCES_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuySongs") && Config.CAN_SEEL_SONGS) //Songs Bypass  
		{
			_bypassType = 2;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-songs.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%song_price%", String.valueOf(Config.PRICE_PER_SONG));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_songs_price%", String.valueOf(Config.FULL_SONGS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOrcBuffs") && Config.CAN_SEEL_ORC_BUFFS) //Orc Bypass  
		{
			_bypassType = 4;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-orc.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_orc_buff%", String.valueOf(Config.PRICE_PER_ORC_BUFF));
			html.replace("%full_shaman_buff_price%", String.valueOf(Config.SHAMAN_BUFFS_PRICE));
			html.replace("%full_warcryer_buff_price%", String.valueOf(Config.WARCRYER_BUFFS_PRICE));
			html.replace("%full_overlord_buff_price%", String.valueOf(Config.OVERLORD_BUFFS_PRICE));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("return_to_main_window")) //dirty hack to come back to main window
		{   
			int npcID = getNpcId();  
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());//will add proper code soon was too lazy for atm :P
			String filename = HtmlPathService.PLAYER_BUFFER_HTML_PATH+npcID+".htm";
			html.setFile(filename);
			if(filename!=null)
			{
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html);
			}
		}
		if(command.equalsIgnoreCase("BuyCubics") && Config.CAN_SEEL_CUBICS_BUFFS) //Cubics Bypass  
		{
			_bypassType = 5;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-cubics.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_cubic%", String.valueOf(Config.PRICE_PER_CUBIC));
			html.replace("%full_cubics_price%", String.valueOf(Config.FULL_CUBICS_PRICE));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuySummonBuffs") && Config.CAN_SEEL_SUMMON_BUFFS)//summon buffs bypass
		{
			_bypassType = 6;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();         
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-summons.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_summon_buff%", String.valueOf(Config.PRICE_PER_SUMMON_BUFF));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_summon_buffs_price%", String.valueOf(Config.FULL_SUMMON_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyHeroBuffs") && Config.CAN_SEEL_HERO_BUFFS)//hero bypass
		{
			_bypassType = 7;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();         
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-hero.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_hero_buff%", String.valueOf(Config.PRICE_PER_HERO_BUFF));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_hero_buffs_price%", String.valueOf(Config.FULL_HERO_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyNobleBuffs") && Config.CAN_SEEL_NOBLE_BUFFS)//noble bypass
		{
			_bypassType = 8;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-noble.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_noble_buff%", String.valueOf(Config.PRICE_PER_NOBLE_BUFF));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
			html.replace("%full_noble_buffs_price%", String.valueOf(Config.FULL_NOBLE_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOtherBuffs"))//other bypass
		{
			_bypassType = 9;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			int Mpz = player.getMaxMp() - (int)player.getStatus().getCurrentMp();
			int Hpz = player.getMaxHp() - (int)player.getStatus().getCurrentHp();
			int Cpz = player.getMaxCp() - (int)player.getStatus().getCurrentCp();
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-other.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%other_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
			html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
			html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
			html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));			
			player.sendPacket(html);
		}
		else if(command.equalsIgnoreCase("ExchangeMySP"))//exchange sp bypass
		{
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-sp.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%playerlevel%", String.valueOf(player.getLevel()));
			html.replace("%playersp%", String.valueOf(player.getSp()));
			player.sendPacket(html);
		}

		/**
		 * @Cancel Buffs
		 */
		else if(command.startsWith("cancel"))
		{
			cancel(player);
			player.sendMessage("all your buffs has been removed.");
		}
		/**
		 * @Prophet Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Greater Empower"))
			makeFreeBuffs("Greater Empower", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.GREATER_EMPOWER, 3, player, true);

		else if(command.startsWith("Acumen"))
			makeFreeBuffs("Acumen", Config.PRICE_PER_PROPHET_BUFF,HardcodedSkillTable.ACUMEN, 3, player, true);

		else if(command.startsWith("Focus"))
			makeFreeBuffs("Focus", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.FOCUS, 3, player, true);

		else if(command.startsWith("Greater Might"))
			makeFreeBuffs("Greater Might", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.GREATER_MIGHT, 3, player, true);

		else if(command.startsWith("Greater Shield"))
			makeFreeBuffs("Greater Shield", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.GREATER_SHIELD, 3, player, true);

		else if(command.startsWith("Berserker Spirit"))
			makeFreeBuffs("Berserker Spirit", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.BERSERKER_SPIRIT, 2, player, true);

		else if(command.startsWith("Invigor"))
			makeFreeBuffs("Invigor", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.INVIGOR, 3, player, true);

		else if(command.startsWith("MentalAegis"))
			makeFreeBuffs("Mental Aegis", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.MENTAL_AEGIS, 4, player, true);

		else if(command.startsWith("MagicBarrier"))
			makeFreeBuffs("Magic Barrier", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.MAGIC_BARRIER, 2, player, true);

		else if(command.startsWith("Regeneration"))
			makeFreeBuffs("Regeneration", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.REGENERATION, 3, player, true);

		else if(command.startsWith("BlessedBody"))
			makeFreeBuffs("Blessed Body", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.BLESS_THE_BODY, 6, player, true);

		else if(command.startsWith("BlessedSoul"))
			makeFreeBuffs("Blessed Soul", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.BLESS_THE_SOUL, 6, player, true);

		else if(command.startsWith("Greater Concentration"))
			makeFreeBuffs("Greater Concentration", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.GREATER_CONCENTRATION, 6, player, true);

		else if(command.startsWith("Haste"))
			makeFreeBuffs("Haste", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.HASTE, 2, player, true);

		else if(command.startsWith("Agility"))
			makeFreeBuffs("Agility", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.AGILITY, 3, player, true);

		else if(command.startsWith("WindWalk"))
			makeFreeBuffs("Wind Walk", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.WIND_WALK, 2, player, true);

		else if(command.startsWith("Guidance"))
			makeFreeBuffs("Guidance", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.GUIDANCE, 3, player, true);

		else if(command.startsWith("DeathWhisper"))
			makeFreeBuffs("Death Whisper", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.DEATH_WHISPER, 3, player, true);

		else if(command.startsWith("VampiricRage"))
			makeFreeBuffs("Vampiric Rage", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.VAMPIRIC_RAGE, 4, player, true);

		else if(command.startsWith("BlessShield"))
			makeFreeBuffs("BlessShield", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.BLESS_SHIELD, 6, player, true);

		/**
		 * All Prophet Buffs For Mages
		 */
		else if(command.startsWith("Full_Prophet_Buffs_Mages"))
		{
			_requiredMoney = 0;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Greater Empower", 0, 1059, 3, player, false); //Greater Empower
			makeFreeBuffs("Acumen", 0, 1085, 3, player, false); //Acumen
			makeFreeBuffs("Greater Shield", 0, HardcodedSkillTable.GREATER_SHIELD, 3, player, false); //Greater Shield
			makeFreeBuffs("Berseker Spirit", 0, HardcodedSkillTable.BERSERKER_SPIRIT, 2, player, false); //Berseker Spirit
			makeFreeBuffs("Mental Aegis", 0, HardcodedSkillTable.MENTAL_AEGIS, 4, player, false); //Mental Aegis
			makeFreeBuffs("Magic Barrier", 0, HardcodedSkillTable.MAGIC_BARRIER, 2, player, false); //Magic Barrier
			makeFreeBuffs("Regeneration", 0, HardcodedSkillTable.REGENERATION , 3, player, false); //Regeneration
			makeFreeBuffs("Blessed Body", 0, HardcodedSkillTable.BLESS_THE_BODY, 6, player, false); //Bless the Body 
			makeFreeBuffs("Blessed Soul", 0, HardcodedSkillTable.BLESS_THE_SOUL, 6, player, false); //Bless The Soul
			makeFreeBuffs("Greater Concentration", 0, HardcodedSkillTable.GREATER_CONCENTRATION, 6, player, false); //Greater Concentration
			makeFreeBuffs("Wind Walk", 0, HardcodedSkillTable.WIND_WALK, 2, player, false); //Wind Walk   
			makeFreeBuffs("Bless Shield", 0, HardcodedSkillTable.BLESS_SHIELD, 6, player, false); //Bless Shield    
			resendMainPage(player);
		}
		/**
		 * All Prophet Buffs For Fighters
		 */
		else if(command.startsWith("Full_Prophet_Buffs_Fighters"))
		{
			_requiredMoney = 0;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Focus", 0, HardcodedSkillTable.FOCUS, 3, player, false); //Focus
			makeFreeBuffs("Greater Shield", 0, HardcodedSkillTable.GREATER_SHIELD, 3, player, false); //Greater Shield
			makeFreeBuffs("Berseker Spirit", 0, HardcodedSkillTable.BERSERKER_SPIRIT, 2, player, false); //Berseker Spirit
			makeFreeBuffs("Invigor", 0, HardcodedSkillTable.INVIGOR, 3, player, false); //Invigor
			makeFreeBuffs("Mental Aegis", 0, HardcodedSkillTable.MENTAL_AEGIS, 4, player, false); //Mental Aegis
			makeFreeBuffs("Magic Barrier", 0, HardcodedSkillTable.MAGIC_BARRIER, 2, player, false); //Magic Barrier
			makeFreeBuffs("Regeneration", 0, HardcodedSkillTable.REGENERATION , 3, player, false); //Regeneration
			makeFreeBuffs("Blessed Body", 0, HardcodedSkillTable.BLESS_THE_BODY, 6, player, false); //Bless the Body 
			makeFreeBuffs("Blessed Soul", 0, HardcodedSkillTable.BLESS_THE_SOUL, 6, player, false); //Bless The Soul
			makeFreeBuffs("Haste", 0, HardcodedSkillTable.HASTE, 2, player, false); //Haste
			makeFreeBuffs("Agility", 0, HardcodedSkillTable.AGILITY, 3, player, false); //Agility  
			makeFreeBuffs("Wind Walk", 0, HardcodedSkillTable.WIND_WALK, 2, player, false); //Wind Walk   
			makeFreeBuffs("Guidance", 0, HardcodedSkillTable.GUIDANCE, 3, player, false); //Guidance
			makeFreeBuffs("Death Wispher", 0, HardcodedSkillTable.DEATH_WHISPER, 3, player, false); //Death Wispher
			makeFreeBuffs("Vampiric Range", 0, HardcodedSkillTable.VAMPIRIC_RAGE, 4, player, false); //Vampiric Range
			resendMainPage(player);
		}
		/**
		 * @Dances Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */

		else if(command.startsWith("Dance of Warrior"))
			makeFreeBuffs("Dance of Warrior", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_WARRIOR, 1, player, true);

		else if(command.startsWith("Dance of Inspiration"))
			makeFreeBuffs("Dance of Inspiration", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_INSPIRATION, 1, player, true);

		else if(command.startsWith("Dance of Mystic"))
			makeFreeBuffs("Dance of Mystic", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_MYSTIC, 1, player, true);

		else if(command.startsWith("Dance of Fire"))
			makeFreeBuffs("Dance of Fire", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_FIRE, 1, player, true);

		else if(command.startsWith("Dance of Fury"))
			makeFreeBuffs("Dance of Fury", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_FURY, 1, player, true);

		else if(command.startsWith("Dance of Concentration"))
			makeFreeBuffs("Dance of Concentration", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_CONCENTRATION, 1, player, true);

		else if(command.startsWith("Dance of Light"))
			makeFreeBuffs("Dance of Light", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_LIGHT, 1, player, true);

		else if(command.startsWith("Dance of Aqua Guard"))
			makeFreeBuffs("Dance of Aqua Guard", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_AQUA_GUARD, 1, player, true);

		else if(command.startsWith("Dance of Earth Guard"))
			makeFreeBuffs("Dance of Earth Guard", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_EARTH_GUARD, 1, player, true);

		else if(command.startsWith("Dance of Vampire"))
			makeFreeBuffs("Dance of Vampire", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_VAMPIRE, 1, player, true);

		else if(command.startsWith("Dance of Protection"))
			makeFreeBuffs("Dance of Protection", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_PROTECTION, 1, player, true);

		else if(command.startsWith("Dance of Siren"))
			makeFreeBuffs("Dance of Siren", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_SIREN, 1, player, true);

		else if(command.startsWith("Dance of Shadow"))
			makeFreeBuffs("Dance of Shadow", Config.PRICE_PER_DANCE, HardcodedSkillTable.DANCE_OF_SHADOW, 1, player, true);


		//TODO: Missing Dance of Resist,nihil, weakness

		/**
		 * All Dances Buffs 
		 * Structure (Skillid, Skill Level, Target)
		 */ 
		else if(command.startsWith("Full_Dances"))
		{
			_requiredMoney = 1;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Dance of Warrior", 0, HardcodedSkillTable.DANCE_OF_WARRIOR, 1, player,false); //Dance of Warrior
			makeFreeBuffs("Dance of Inspiration", 0, HardcodedSkillTable.DANCE_OF_INSPIRATION, 1, player,false); //Dance of Inspiration
			makeFreeBuffs("Dance of Mystic", 0, HardcodedSkillTable.DANCE_OF_MYSTIC, 1, player, false); //Dance of Mystic
			makeFreeBuffs("Dance of Fire", 0, HardcodedSkillTable.DANCE_OF_FIRE, 1, player, false); //Dance of Fire
			makeFreeBuffs("Dance of Fury", 0, HardcodedSkillTable.DANCE_OF_FURY, 1, player, false); //Dance of Fury 
			makeFreeBuffs("Dance of Concentration", 0, HardcodedSkillTable.DANCE_OF_CONCENTRATION, 1, player, false); //Dance of Concentration
			makeFreeBuffs("Dance of Light", 0, HardcodedSkillTable.DANCE_OF_LIGHT, 1, player, false); //Dance of Light
			makeFreeBuffs("Dance of Aqua Guard", 0, HardcodedSkillTable.DANCE_OF_AQUA_GUARD, 1, player, false); //Dance of Aqua Guard
			makeFreeBuffs("Dance of Earth Guard", 0, HardcodedSkillTable.DANCE_OF_EARTH_GUARD, 1, player, false); //Dance of Earth Guard
			makeFreeBuffs("Dance of Vampire", 0, HardcodedSkillTable.DANCE_OF_VAMPIRE, 1, player, false); //Dance of Vampire
			makeFreeBuffs("Dance of Protection", 0, HardcodedSkillTable.DANCE_OF_PROTECTION, 1, player, false); //Dance of Protection
			makeFreeBuffs("Dance of Siren", 0, HardcodedSkillTable.DANCE_OF_SIREN, 1, player, false); //Dance of Siren
			resendMainPage(player);

		}
		/**
		 * @Songs Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Song of Earth"))
			makeFreeBuffs("Song of Earth", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_EARTH, 1, player, true);

		else if(command.startsWith("Song of Life"))
			makeFreeBuffs("Song of Life", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_LIFE, 1, player, true);

		else if(command.startsWith("Song of Water"))
			makeFreeBuffs("Song of Water", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_WATER, 1, player, true);

		else if(command.startsWith("Song of Warding"))
			makeFreeBuffs("Song of Warding", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_WARDING, 1, player, true);

		else if(command.startsWith("Song of Wind"))
			makeFreeBuffs("Song of Wind", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_WIND, 1, player, true);

		else if(command.startsWith("Song of Hunter"))
			makeFreeBuffs("Song of Hunter", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_HUNTER, 1, player, true);

		else if(command.startsWith("Song of Invocation"))
			makeFreeBuffs("Song of Invocation", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_INVOCATION, 1, player, true);

		else if(command.startsWith("Song of Vitality"))
			makeFreeBuffs("Song of Vitality", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_VITALITY, 1, player, true);

		else if(command.startsWith("Song of Vengeance"))
			makeFreeBuffs("Song of Vengeance", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_VENGEANCE, 1, player, true);

		else if(command.startsWith("Song of Flame Guard"))
			makeFreeBuffs("Song of Flame Guard", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_FLAME_GUARD, 1, player, true);

		else if(command.startsWith("Song of Storm Guard"))
			makeFreeBuffs("Song of Storm Guard", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_STORM_GUARD, 1, player, true);

		else if(command.startsWith("Song of Renewal"))
			makeFreeBuffs("Song of Renewal", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_RENEWAL, 1, player, true);

		else if(command.startsWith("Song of Meditation"))
			makeFreeBuffs("Song of Meditation", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_MEDITATION, 1, player, true);

		else if(command.startsWith("Song of Champion"))
			makeFreeBuffs("Song of Champion", Config.PRICE_PER_SONG, HardcodedSkillTable.SONG_OF_CHAMPION, 1, player, true);

		/*
		 * TODO: Missing in Skill Trees
		 * else if(command.startsWith("Song of Seduce"))
			MakeBuffs("Song of Seduce", Config.PRICE_PER_SONG, 4536, 1, player, true, false);
		else if(command.startsWith("Song of Sweet Whisper"))
			MakeBuffs("Song of Sweet Whisper", Config.PRICE_PER_SONG, 4537, 1, player, true, false);
		else if(command.startsWith("Song of Temptation"))
			MakeBuffs("Song of Temptation", Config.PRICE_PER_SONG, 4538, 1, player, true, false);
		 */

		/**
		 * All Songs Buffs 
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Full_Songs"))
		{
			_requiredMoney = 2;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Song of Earth", 0, HardcodedSkillTable.SONG_OF_EARTH, 1, player, false); //Song of Earth
			makeFreeBuffs("Song of Life", 0, HardcodedSkillTable.SONG_OF_LIFE, 1, player, false); //Song of Life
			makeFreeBuffs("Song of Water", 0, HardcodedSkillTable.SONG_OF_WATER, 1, player, false); //Song of Water
			makeFreeBuffs("Song of Warding", 0, HardcodedSkillTable.SONG_OF_WARDING, 1, player, false); //Song of Warding
			makeFreeBuffs("Song of Wind ", 0, HardcodedSkillTable.SONG_OF_WIND, 1, player, false); //Song of Wind
			makeFreeBuffs("Song of Hunter", 0, HardcodedSkillTable.SONG_OF_HUNTER, 1, player, false); //Song of Hunter
			makeFreeBuffs("Song of Invocation", 0, HardcodedSkillTable.SONG_OF_INVOCATION, 1, player, false); //Song of Invocation
			makeFreeBuffs("Song of Vitality", 0, HardcodedSkillTable.SONG_OF_VITALITY, 1, player, false); //Song of Vitality
			makeFreeBuffs("Song of Vengeance", 0, HardcodedSkillTable.SONG_OF_VENGEANCE, 1, player, false); //Song of Vengeance
			makeFreeBuffs("Song of Flame Guard", 0, HardcodedSkillTable.SONG_OF_FLAME_GUARD, 1, player, false); //Song of Flame Guard
			makeFreeBuffs("Song of Storm Guard", 0, HardcodedSkillTable.SONG_OF_STORM_GUARD, 1, player, false); //Song of Storm Guard
			makeFreeBuffs("Song of Renewal", 0, HardcodedSkillTable.SONG_OF_RENEWAL, 1, player, false); //Song of Renewal
			makeFreeBuffs("Song of Meditation", 0, HardcodedSkillTable.SONG_OF_MEDITATION, 1, player, false); //Song of Meditation
			makeFreeBuffs("Song of Champion", 0, HardcodedSkillTable.SONG_OF_CHAMPION, 1, player, false); //Song of Champion
			resendMainPage(player);
		}

		/**
		 * @Orc Buffs
		 */
		else if(command.startsWith("Flame Chant"))//shaman
			makeFreeBuffs("Flame Chant", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.FLAME_CHANT, 3, player, true);

		else if(command.startsWith("Pa'agrian Gift"))//shaman
			makeFreeBuffs("Pa'agrian Gift", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.PAAGRIAN_GIFT, 3, player, true);

		else if(command.startsWith("Blessings of Pa'agrio"))//shaman
			makeFreeBuffs("Blessings of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.BLESSINGS_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("Chant of Fire"))//shaman
			makeFreeBuffs("Chant of Fire", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_FIRE, 3, player, true);

		else if(command.startsWith("Chant of Battle"))//shaman
			makeFreeBuffs("Chant of Battle", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_BATTLE, 2, player, true);

		else if(command.startsWith("Chant of Shielding"))//shaman
			makeFreeBuffs("Chant of Shielding", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_SHIELDING, 3, player, true);

		else if(command.startsWith("Soul Shield"))//shaman
			makeFreeBuffs("Soul Shield", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.SOUL_SHIELD, 3, player, true);

		else if(command.startsWith("The Wisdom of Pa'agrio"))//overlord
			makeFreeBuffs("The Wisdom of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_WISDOM_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Glory of Pa'agrio"))//overlord
			makeFreeBuffs("The Glory of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_GLORY_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Vision of Pa'agrio"))//overlord
			makeFreeBuffs("The Vision of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_VISION_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("Under the Protection of Pa'agrio"))//overlord
			makeFreeBuffs("Under the Protection of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.UNDER_THE_PROTECTION_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Heart of Pa'agrio"))//overlord
			makeFreeBuffs("The Heart of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_HEART_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Tact of Pa'agrio"))//overlord
			makeFreeBuffs("The Tact of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_TACT_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Rage of Pa'agrio"))//overlord
			makeFreeBuffs("The Rage of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_RAGE_OF_PAAGRIO, 2, player, true);

		else if(command.startsWith("The Honor of Pa'agrio"))//overlord
			makeFreeBuffs("The Honor of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_HONOR_OF_PAAGRIO, 5, player, true);

		else if(command.startsWith("The Eye of Pa'agrio"))//overlord
			makeFreeBuffs("The Eye of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_EYE_OF_PAAGRIO, 1, player, true);

		else if(command.startsWith("The Soul of Pa'agrio"))//overlord
			makeFreeBuffs("The Soul of Pa'agrio", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.THE_SOUL_OF_PAAGRIO, 1, player, true);

		else if(command.startsWith("Chant of Fury"))//warcryer
			makeFreeBuffs("Chant of Fury", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_FURY, 2, player, true);

		else if(command.startsWith("Chant of Evasion"))//warcryer
			makeFreeBuffs("Chant of Evasion", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_EVASION, 3, player, true);

		else if(command.startsWith("Chant of Rage"))//warcryer
			makeFreeBuffs("Chant of Rage", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_RAGE, 3, player, true);

		else if(command.startsWith("Chant of Revenge"))//warcryer
			makeFreeBuffs("Chant of Revenge", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_REVENGE, 3, player, true);

		else if(command.startsWith("Chant of Predator"))//warcryer
			makeFreeBuffs("Chant of Predator", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_PREDATOR, 3, player, true);

		else if(command.startsWith("Chant of Eagle"))//warcryer
			makeFreeBuffs("Chant of Eagle", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_EAGLE, 3, player, true);

		else if(command.startsWith("Chant of Vampire"))//warcryer
			makeFreeBuffs("Chant of Vampire", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_VAMPIRE, 4, player, true);

		else if(command.startsWith("Chant of Spirit"))//warcryer
			makeFreeBuffs("Chant of Spirit", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_SPIRIT, 1, player, true);

		else if(command.startsWith("Chant of Victory"))//warcryer
			makeFreeBuffs("Chant of Victory", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.CHANT_OF_VICTORY, 1, player, true);

		else if(command.startsWith("War Chant"))//warcryer
			makeFreeBuffs("War Chant", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.WAR_CHANT, 3, player, true);

		else if(command.startsWith("Earth Chant"))//warcryer
			makeFreeBuffs("Earth Chant", Config.PRICE_PER_ORC_BUFF, HardcodedSkillTable.EARTH_CHANT, 3, player, true);


		/**
		 * @Shaman buffs
		 */
		else if(command.startsWith("Shaman_Buffs") && Config. CAN_SEEL_SHAMAN_BUFFS)
		{
			_requiredMoney = 4; //global variable to get money for 
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Flame Chant", 0, HardcodedSkillTable.FLAME_CHANT, 1, player, false);
			makeFreeBuffs("Pa'agrian Gift", 0, HardcodedSkillTable.PAAGRIAN_GIFT, 3, player, false);
			makeFreeBuffs("Blessings of Pa'agrio", 0, HardcodedSkillTable.BLESSINGS_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("Chant of Fire", 0, HardcodedSkillTable.CHANT_OF_FIRE, 3, player, false);
			makeFreeBuffs("Chant of Battle", 0, HardcodedSkillTable.CHANT_OF_BATTLE, 2, player, false);
			makeFreeBuffs("Chant of Shielding", 0, HardcodedSkillTable.CHANT_OF_SHIELDING, 3, player, false);
			makeFreeBuffs("Soul Shield", 0, HardcodedSkillTable.SOUL_SHIELD, 3, player, false);
			resendMainPage(player);
		}
		/**
		 * @Overlord Buffs
		 */
		else if(command.startsWith("Overlord_Buffs") && Config.CAN_SEEL_OVERLORD_BUFFS)
		{
			_requiredMoney = 5;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("The Wisdom of Pa'agrio", 0, HardcodedSkillTable.THE_WISDOM_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("The Glory of Pa'agrio", 0, HardcodedSkillTable.THE_GLORY_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("The Vision of Pa'agrio", 0, HardcodedSkillTable.THE_VISION_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("Under the Protection of Pa'agrio", 0, HardcodedSkillTable.UNDER_THE_PROTECTION_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("The Heart of Pa'agrio", 0, HardcodedSkillTable.THE_HEART_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("The Tact of Pa'agrio", 0, HardcodedSkillTable.THE_TACT_OF_PAAGRIO, 3, player, false);
			makeFreeBuffs("The Rage of Pa'agrio", 0, HardcodedSkillTable.THE_RAGE_OF_PAAGRIO, 2, player, false);
			makeFreeBuffs("The Honor of Pa'agrio", 0, HardcodedSkillTable.THE_HONOR_OF_PAAGRIO, 5, player, false);
			makeFreeBuffs("The Eye of Paagrio", 0, HardcodedSkillTable.THE_EYE_OF_PAAGRIO, 1, player, false);
			makeFreeBuffs("The Soul of Paagrio", 0, HardcodedSkillTable.THE_SOUL_OF_PAAGRIO, 1, player, false);
			resendMainPage(player);
		}
		/**
		 * @Warcryer Buffs
		 */
		else if(command.startsWith("Warcryer_Buffs") && Config.CAN_SEEL_WARCRYER_BUFFS)
		{
			_requiredMoney = 6;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Chant of Fury", 0, HardcodedSkillTable.CHANT_OF_FURY, 2, player, false);
			makeFreeBuffs("Chant of Evasion", 0, HardcodedSkillTable.CHANT_OF_EVASION, 3, player, false);
			makeFreeBuffs("Chant of Rage", 0, HardcodedSkillTable.CHANT_OF_RAGE, 3, player, false);
			makeFreeBuffs("Chant of Revenge", 0, HardcodedSkillTable.CHANT_OF_REVENGE, 3, player, false);
			makeFreeBuffs("Chant of Predator", 0, HardcodedSkillTable.CHANT_OF_PREDATOR, 3, player, false);
			makeFreeBuffs("Chant of Eagle", 0, HardcodedSkillTable.CHANT_OF_EAGLE, 3, player, false);
			makeFreeBuffs("Chant of Vampire", 0, HardcodedSkillTable.CHANT_OF_VAMPIRE, 4, player, false);
			makeFreeBuffs("Chant of Spirit", 0, HardcodedSkillTable.CHANT_OF_SPIRIT, 1, player, false);
			makeFreeBuffs("Chant of Victory", 0, HardcodedSkillTable.CHANT_OF_VICTORY, 1, player, false);
			makeFreeBuffs("War Chant", 0, HardcodedSkillTable.WAR_CHANT, 3, player, false);
			makeFreeBuffs("Earth Chant", 0, HardcodedSkillTable.EARTH_CHANT, 3, player, false);
			resendMainPage(player);
		}
		/**
		 * @Cubics Buffs
		 */
		else if (command.startsWith("FullCubics"))
		{
			_requiredMoney = 7;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Summon Phantom Cubic", 0, HardcodedSkillTable.SUMMON_PHANTOM_CUBIC, 8, player, false);
			makeFreeBuffs("Summon Vampiric Cubic", 0, HardcodedSkillTable.SUMMON_VAMPIRIC_CUBIC, 7, player, false);
			makeFreeBuffs("Summon Spark Cubic", 0, HardcodedSkillTable.SUMMON_SPARK_CUBIC, 9, player, false);
			makeFreeBuffs("Summon Aqua Cubic", 0, HardcodedSkillTable.SUMMON_AQUA_CUBIC, 9, player, false);
			makeFreeBuffs("Summon Viper Cubic", 0, HardcodedSkillTable.SUMMON_VIPER_CUBIC, 6, player, false);
			makeFreeBuffs("Summon Storm Cubic", 0, HardcodedSkillTable.SUMMON_STORM_CUBIC, 8, player, false);
			makeFreeBuffs("Summon Life Cubic", 0, HardcodedSkillTable.SUMMON_LIFE_CUBIC, 7, player, false);
			makeFreeBuffs("Summon Binding Cubic", 0, HardcodedSkillTable.SUMMON_BINDING_CUBIC, 9, player, false);
			resendMainPage(player);
		}
		else if(command.startsWith("Summon Phantom Cubic"))
			makeFreeBuffs("Summon Phantom Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_PHANTOM_CUBIC, 8, player, true);

		else if(command.startsWith("Summon Vampiric Cubic"))
			makeFreeBuffs("Summon Vampiric Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_VAMPIRIC_CUBIC, 7, player, true);

		else if(command.startsWith("Summon Spark Cubic"))
			makeFreeBuffs("Summon Spark Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_SPARK_CUBIC, 9, player, true);

		else if(command.startsWith("Summon Aqua Cubic"))
			makeFreeBuffs("Summon Aqua Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_AQUA_CUBIC, 9, player, true);

		else if(command.startsWith("Summon Viper Cubic"))
			makeFreeBuffs("Summon Viper Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_VIPER_CUBIC, 6, player, true);

		else if(command.startsWith("Summon Storm Cubic"))
			makeFreeBuffs("Summon Storm Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_STORM_CUBIC, 8, player, true);

		else if(command.startsWith("Summon Life Cubic"))
			makeFreeBuffs("Summon Life Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_LIFE_CUBIC, 7, player, true);

		else if(command.startsWith("Summon Binding Cubic"))
			makeFreeBuffs("Summon Binding Cubic", Config.PRICE_PER_CUBIC, HardcodedSkillTable.SUMMON_BINDING_CUBIC, 9, player, true);

		/** 
		 * @Full Summons Buffs
		 */
		else if (command.startsWith("FullSummonBuffs"))
		{
			_requiredMoney = 8;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Blessing of Queen", 0, HardcodedSkillTable.BLESSING_OF_QUEEN, 13, player, false);
			makeFreeBuffs("Gift of Queen", 0, HardcodedSkillTable.GIFT_OF_QUEEN, 13, player, false);
			makeFreeBuffs("Blessing of Seraphim", 0, HardcodedSkillTable.BLESSING_OF_SERAPHIM, 13, player, false);
			makeFreeBuffs("Gift of Serapfim", 0, HardcodedSkillTable.GIFT_OF_SERAPHIM, 13, player, false);
			makeFreeBuffs("Cure of Queen", 0, HardcodedSkillTable.CURE_OF_QUEEN, 13, player, false);
			makeFreeBuffs("Cure of Seraphim", 0, HardcodedSkillTable.CURE_OF_SERAPHIM, 13, player, false);
			resendMainPage(player);
		}
		/** 
		 * @Summons Buffs
		 */
		else if (command.startsWith("Blessing of Queen"))
			makeFreeBuffs("Blessing of Queen", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.BLESSING_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Gift of Queen"))
			makeFreeBuffs("Gift of Queen", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.GIFT_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Blessing of Seraphim"))
			makeFreeBuffs("Blessing of Seraphim", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.BLESSING_OF_SERAPHIM, 13, player, true);

		else if (command.startsWith("Gift of Serapfim"))
			makeFreeBuffs("Gift of Serapfim", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.GIFT_OF_SERAPHIM, 13, player,true);

		else if (command.startsWith("Cure of Queen"))
			makeFreeBuffs("Cure of Queen", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.CURE_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Cure of Seraphim"))
			makeFreeBuffs("Cure of Seraphim", Config.PRICE_PER_SUMMON_BUFF, HardcodedSkillTable.CURE_OF_SERAPHIM, 13, player, true);

		/**
		 * @Hero Buffs
		 */
		else if(command.startsWith("Heroic Miracle"))
			makeFreeBuffs("Heroic Miracle", Config.PRICE_PER_HERO_BUFF, HardcodedSkillTable.HEROIC_MIRACLE, 1, player, true);
		else if(command.startsWith("Heroic Berserker"))
			makeFreeBuffs("Heroic Berserker", Config.PRICE_PER_HERO_BUFF, HardcodedSkillTable.HEROIC_BERSERKER, 1, player, true);
		else if(command.startsWith("Heroic Valor"))
			makeFreeBuffs("Heroic Valor", Config.PRICE_PER_HERO_BUFF, HardcodedSkillTable.HEROIC_VALOR, 1, player, true);

		/**
		 * @Full Hero Buffs
		 */
		else if(command.startsWith("FullHeroBuffs"))
		{
			_requiredMoney = 9;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Heroic Miracle", 0, HardcodedSkillTable.HEROIC_MIRACLE, 1, player, false);
			makeFreeBuffs("Heroic Berserker", 0, HardcodedSkillTable.HEROIC_BERSERKER, 1, player, false);
			makeFreeBuffs("Heroic Valor", 0, HardcodedSkillTable.HEROIC_VALOR, 1, player, false);
			resendMainPage(player);
		}

		/**
		 * @Other Buffs
		 */
		else if(command.startsWith("prophecy of water"))
			makeFreeBuffs("prophecy of water", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.PROPHECY_OF_WATER, 1, player, true);

		else if(command.startsWith("prophecy of fire"))
			makeFreeBuffs("prophecy of fire", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.PROPHECY_OF_FIRE, 1, player, true);

		else if(command.startsWith("prophecy of wind"))
			makeFreeBuffs("prophecy of wind", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.PROPHECY_OF_WIND, 1, player, true);

		else if(command.startsWith("wild magic"))
			makeFreeBuffs("wild magic", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.WILD_MAGIC, 2, player, true);

		else if(command.startsWith("might"))
			makeFreeBuffs("might", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.MIGHT, 3, player, true);

		else if(command.startsWith("shield"))
			makeFreeBuffs("shield", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.SHIELD, 3, player, true);

		else if(command.startsWith("unholy resistence"))
			makeFreeBuffs("unholy resistence", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.UNHOLY_RESISTENCE, 3, player, true);

		else if(command.startsWith("holy resistence"))
			makeFreeBuffs("holy resistence", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.HOLY_RESISTENCE, 3, player, true);

		else if(command.startsWith("elemental protection"))
			makeFreeBuffs("elemental protection", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.ELEMENTAL_PROTECTION, 1, player, true);

		else if(command.startsWith("divine protection"))
			makeFreeBuffs("divine protection", Config.PRICE_PER_PROPHET_BUFF, HardcodedSkillTable.DIVINE_PROTECTION, 1, player, true);

		/**
		 * @Noble Buffs
		 */
		else if(command.startsWith("Blessing of Noblesse"))
			makeFreeBuffs("Blessing of Noblesse", Config.PRICE_PER_NOBLE_BUFF, HardcodedSkillTable.BLESSING_OF_NOBLESSE, 1, player, true);
		else if(command.startsWith("Fortune of Noblesse"))
			makeFreeBuffs("Fortune of Noblesse", Config.PRICE_PER_NOBLE_BUFF, HardcodedSkillTable.FORTUNE_OF_NOBLESSE, 1, player, true);

		/**
		 * @Full Noble Buffs
		 */
		else if(command.startsWith("FullNobleBuffs"))
		{
			_requiredMoney = 10;
			validateConditionToBuyFullBuffs(player);
			makeFreeBuffs("Blessing of Noblesse", 0, HardcodedSkillTable.BLESSING_OF_NOBLESSE, 1, player, false);
			makeFreeBuffs("Fortune of Noblesse", 0, HardcodedSkillTable.FORTUNE_OF_NOBLESSE, 1, player, false);
			resendMainPage(player);
		}
		/**
		 * @Other Features from NPC Buffer
		 */
		else if(command.startsWith("RestorePlayerCP"))
			restorePlayerCp(player);
		else if(command.startsWith("RestorePlayerMP"))
			restorePlayerMp(player);
		else if(command.startsWith("RestorePlayerHP"))
			restorePlayerHp(player);
		/**
		 * @Restore Summons Stats
		 */
		else if(command.startsWith("Exchange500SP"))
			validateSpCount(player, 500, 5593);
		else if(command.startsWith("Exchange5000SP"))
			validateSpCount(player, 5000, 5594);
		else if(command.startsWith("Exchange100kSP"))
			validateSpCount(player, 100000, 5595);
	}

	private void validateEffect()
	{
		L2Effect Effect = null;
		if (Effect != null) 
			Effect.setInUse(true); 
	}

	/**
	 * @param player
	 */
	private void restorePlayerCp(L2PcInstance player)
	{
		if(Config.ALLOW_NPC_BUFFER)
		{
			int currentmoney = player.getAdena();
			double plyrcp = player.getStatus().getCurrentCp();
			double plyrcpmax = player.getMaxCp();
			int plyrCpToRestore = (int)plyrcpmax - (int)plyrcp;

			if(plyrCpToRestore * Config.PRICE_PER_CP_POINT > currentmoney && !player.isGM())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				return;
			}
			if(!player.isGM())
				player.reduceAdena("NpcBuffer: CP recovery", plyrCpToRestore * Config.PRICE_PER_CP_POINT, player, false);

			if(plyrCpToRestore<=0)
			{
				player.sendMessage("Your CP is full, restore not needed.");
				return;
			}
			sendRestoreWindow(player);
			player.getStatus().setCurrentCp(plyrcpmax + 1.0D);
		}
		else
		{
			sendNegateWindow(player);
			return;
		}
	}
	/**
	 * @param player
	 */
	protected void resendMainPage(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int npcID = getNpcId();
		String filename = HtmlPathService.PLAYER_BUFFER_HTML_PATH+npcID+".htm";
		html.setFile(filename);
		if(filename!=null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * @param player
	 */
	protected void sendNotAllowedLvlWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.PLAYER_BUFFER_HTML_PATH+"level-not-allowed.htm");
		html.setFile(filename);
		if (filename != null)
		{ 
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%minlevel%", String.valueOf(Config.MIN_LEVEL_TO_GET_BUFFS));
			html.replace("%maxlevel%", String.valueOf(Config.MAX_LEVEL_TO_GET_BUFFS));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
	}
	/**
	 * @param player
	 */
	protected void sendRestoreWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.PLAYER_BUFFER_HTML_PATH+"restore.htm");
		html.setHtml(filename);
		if (filename != null)
		{   
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * @param player
	 */
	protected void sendNegateWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.PLAYER_BUFFER_HTML_PATH+"negate.htm");
		html.setHtml(filename); 
		if (filename != null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * @param player
	 */
	private void restorePlayerHp(L2PcInstance player)
	{
		if(Config.ALLOW_NPC_BUFFER)
		{
			int currentmoney = player.getAdena();
			double plyrhp = player.getStatus().getCurrentHp();
			double plyrhpmax = player.getMaxHp();
			int plyrHpToRestore = (int)plyrhpmax - (int)plyrhp;
			if(plyrHpToRestore * Config.PRICE_PER_HP_POINT > currentmoney)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				return;
			}
			if(plyrHpToRestore <= 0)
			{
				player.sendMessage("Your HP is full, restore not needed.");
				return;
			}
			player.reduceAdena("NpcBuffer: HP recovery", plyrHpToRestore * Config.PRICE_PER_HP_POINT, player, false);
			player.getStatus().setCurrentHp(plyrhpmax + 1.0D);
			sendRestoreWindow(player);
		}
		else
		{
			sendNegateWindow(player);
			return;
		}
	}
	/**
	 * @param player
	 */
	private void restorePlayerMp(L2PcInstance player)
	{
		if(Config.ALLOW_NPC_BUFFER)
		{
			int currentmoney = player.getAdena();
			double plyrmp = player.getStatus().getCurrentMp();
			double plyrmpmax = player.getMaxMp();
			int plyrMpToRestore = (int)plyrmpmax - (int)plyrmp;

			if(plyrMpToRestore * Config.PRICE_PER_MP_POINT > currentmoney)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				return;
			}
			if(plyrMpToRestore<=0)
			{
				player.sendMessage("Your MP is full, restore not needed.");
			}
			player.reduceAdena("NpcBuffer: MP recovery", plyrMpToRestore * Config.PRICE_PER_MP_POINT, player, false);
			player.getStatus().setCurrentMp(plyrmpmax + 1.0D);
			sendRestoreWindow(player);
		}
		else
		{
			sendNegateWindow(player);
			return;
		}
	}

	/**
	 * Main Function for Buffer Casting: <br>
	 * 
	 * <li> checks enabled configs <br>
	 * <li> checks player state <br>
	 * <li> resends pages 
	 * 
	 * @param buffName
	 * @param priceInConfig
	 * @param skillId
	 * @param skillLevel
	 * @param requester
	 * @param resendPage
	 */
	private void makeFreeBuffs(String buffName, int priceInConfig, int skillId, int skillLevel, L2PcInstance requester, boolean resendPage)
	{
		int npcID = getNpcId();
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int Mpz = requester.getMaxMp() - (int)requester.getStatus().getCurrentMp();
		int Hpz = requester.getMaxHp() - (int)requester.getStatus().getCurrentHp();
		int Cpz = requester.getMaxCp() - (int)requester.getStatus().getCurrentCp();
		int lvl = requester.getLevel();

		if(Config.ALLOW_NPC_BUFFER)
		{
			//Cofnig to allow/disallow karma players to make buffs
			if(!Config.ALLOW_KARMA_PLAYER && requester.getKarma() > 0)
			{
				html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-pk.htm").toString());
				requester.sendPacket(html);
				return;
			}
			if(lvl >= Config.MIN_LEVEL_TO_GET_BUFFS && lvl <= Config.MAX_LEVEL_TO_GET_BUFFS || !requester.isGM())
			{
				/*int town = MapRegionTable.getInstance().getMapRegion(requester.getPosition().getX(),requester.getPosition().getY());

			    //checks if any siege is in progress and config 
			    if(TownManager.getInstance().townHasCastleInSeige(town) && Config.DISABLE_NPC_BUFFER_DURING_SIEGE)
			    {
			    	SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2); 
					sm.addString("Buffs are allowed while a siege is in progress.");
					requester.sendPacket(sm);
					sm = null;
					return;
			    }*/
				//checks for enabled tvt buffs not allowed.
				if(Config.TVT_DISABLE_NPC_BUFFER && requester._inEventTvT && Config.TVT_ON_START_REMOVE_ALL_EFFECTS && TvT._started && !requester.isGM())
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2); 
					sm.addString("Buffs are not allowed while in event.");
					requester.sendPacket(sm);
					sm = null;
					return;
				}

				//checks for enabled ctf buffs not allowed.
				if(Config.CTF_DISABLE_NPC_BUFFER && requester._inEventCTF && Config.CTF_ON_START_REMOVE_ALL_EFFECTS && CTF._started && !requester.isGM())
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2); 
					sm.addString("Buffs are not allowed while in event.");
					requester.sendPacket(sm);
					sm = null;
					return;
				}

				//checks for enabled dm buffs not allowed.
				if(Config.DM_DISABLE_NPC_BUFFER && requester._inEventDM && Config.DM_ON_START_REMOVE_ALL_EFFECTS  && DM._started && !requester.isGM())
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2); 
					sm.addString("Buffs are not allowed while in event.");
					requester.sendPacket(sm);
					sm = null;
					return;
				}

				//dont buffs dead or fake dead player
				if (requester.isDead()|| requester.isAlikeDead())
				{
					return;
				}

				//excludes gms 
				if((requester.getInventory().getAdena() <= priceInConfig && !requester.isGM()) || (requester.getInventory().getAdena() <= priceInConfig && requester.isGM() && !Config.ONLY_GM_BUFFS_FREE))
				{
					requester.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
					return;
				}
				requester.reduceAdena((new StringBuilder("NpcBuffer: ")).append(buffName).toString(), priceInConfig, requester, true);


				//gets the skill info
				L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

				if(_log.isDebugEnabled())
					_log.info("For Skill "+skill+" Taken.");

				if(Config.REGENERATE_STATS_FOR_FREE)
				{
					//regenerates.
					getStatus().setCurrentHpMp(getMaxHp(), getMaxMp());
				}

				//yes, its not for all skills right, but atleast player will know 
				// for what he paid =)
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT); 
				sm.addSkillName(skill.getId());
				requester.sendPacket(sm);
				sm = null;

				if(resendPage)
				{
					switch (_bypassType)
					{ 
					case 0: //prophets
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%playername%", requester.getName());
						html.replace("%prophet_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
						html.replace("%full_prophet_buffs_price%", String.valueOf(Config.FULL_PROPHET_BUFFS_PRICE));
						requester.sendPacket(html);
						break;
					}
					case 1: //dances
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-dances.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%playername%", requester.getName());
						html.replace("%dance_price%", String.valueOf(Config.PRICE_PER_DANCE));
						html.replace("%full_dances_price%", String.valueOf(Config.FULL_DANCES_PRICE));
						requester.sendPacket(html);
						break;
					}
					case 2: //songs
					{  
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-songs.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName()); 
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%song_price%", String.valueOf(Config.PRICE_PER_SONG));
						html.replace("%full_songs_price%", String.valueOf(Config.FULL_SONGS_PRICE));
						requester.sendPacket(html);
						break;
					}
					case 4: //orc
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-orc.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%price_per_orc_buff%", String.valueOf(Config.PRICE_PER_ORC_BUFF));
						html.replace("%full_shaman_buff_price%", String.valueOf(Config.SHAMAN_BUFFS_PRICE));
						html.replace("%full_warcryer_buff_price%", String.valueOf(Config.WARCRYER_BUFFS_PRICE));
						html.replace("%full_overlord_buff_price%", String.valueOf(Config.OVERLORD_BUFFS_PRICE));
						requester.sendPacket(html);
						break;
					}
					case 5: //cubics
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-cubics.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%price_per_cubic%", String.valueOf(Config.PRICE_PER_CUBIC));
						html.replace("%full_cubics_price%", String.valueOf(Config.FULL_CUBICS_PRICE));
						requester.sendPacket(html); 
						break;
					}
					case 6: //summons buffs
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-summons.htm").toString());
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						html.replace("%price_per_summon_buff%", String.valueOf(Config.PRICE_PER_SUMMON_BUFF));
						html.replace("%full_summon_buffs_price%", String.valueOf(Config.FULL_SUMMON_BUFFS_PRICE));
						requester.sendPacket(html); 
						break;
					}
					case 7: //hero
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-hero.htm").toString());
						html.replace("%price_per_hero_buff%", String.valueOf(Config.PRICE_PER_HERO_BUFF));
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%full_hero_buffs_price%", String.valueOf(Config.FULL_HERO_BUFFS_PRICE));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						requester.sendPacket(html);
						break;
					}
					case 8: //noble
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-noble.htm").toString());
						html.replace("%price_per_noble_buff%", String.valueOf(Config.PRICE_PER_NOBLE_BUFF));
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%full_noble_buffs_price%", String.valueOf(Config.FULL_NOBLE_BUFFS_PRICE));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						requester.sendPacket(html);
						break;
					}
					case 9: //other
					{
						html.setFile((new StringBuilder(HtmlPathService.PLAYER_BUFFER_HTML_PATH)).append(npcID).append("-other.htm").toString());
						html.replace("%other_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
						html.replace("%price_per_cp_point%", String.valueOf(Cpz * Config.PRICE_PER_CP_POINT));
						html.replace("%price_per_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_HP_POINT));
						html.replace("%price_per_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", requester.getName());
						requester.sendPacket(html);
						break;
					}
					default:
						WindowService.sendWindow(requester, HtmlPathService.HTML_PATH, "npcdefault.htm");
					break;
					}
				}
				if (skill.getTargetType() == SkillTargetType.TARGET_SELF)
				{
					L2Effect oldEffect = requester.getFirstEffect(skill.getId());

					//remove old effects so we can update all efefcts in progress
					if(oldEffect!=null)
					oldEffect.exit();

					//shows/disables animation depending on config
					if(!Config.DISABLE_ANIMATION)
					{
						//Ignore skill cast time, using config pc animation for PC buff effect animation
						MagicSkillUser msu = new MagicSkillUser(requester, requester, skillId, skillLevel, 100, 0);
						broadcastPacket(msu);
					}
					//applys effect
					for (L2Effect effect : skill.getEffectsSelf(requester))
					{
						requester.addEffect(effect);
					}

					// hack for newbie summons
					if (skill.getSkillType() == SkillType.SUMMON)
					{
						requester.useMagic(skill, true,false);
					}
				}
				else
				{   
					L2Effect oldEffect = requester.getFirstEffect(skill.getId());
					
					//remove old effects so we can update all efefcts in progress
					if(oldEffect!=null)
					oldEffect.exit();

					//shows/disables animation depending on config
					if(!Config.DISABLE_ANIMATION)
					{
						// Ignore skill cast time, using 100ms for NPC buffer's animation
						MagicSkillUser msu = new MagicSkillUser(this, requester, skillId, skillLevel, 100, 0);
						broadcastPacket(msu);
					}
					//applys effects to player
					for (L2Effect effect : skill.getEffects(this, requester))
					{
						requester.addEffect(effect);
					}
				}
				//sleeps between casts
				try {
					Thread.sleep(ThreadService.BUFFER_CAST_DELAY_TIMER);//sleep between casts
				}
				catch(InterruptedException ex) {}
			}
			else
			{
				//sends a page of not allowed level
				sendNotAllowedLvlWindow(requester);
				return;
			}
		}
		else
		{
			//sends a page of negation
			sendNegateWindow(requester);
			return;
		}
		validateEffect();
	}
	/**
	 * Validates Player Sp Count For TradeSpPointsForSpScrolls()Function
	 * @param ActiveChar
	 * @param SpCount
	 * @param ItemId
	 * 
	 */
	private void validateSpCount(L2PcInstance player, int requiredSpCount, int itemID)
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
			player.sendPacket(su);
			return;
		}
	}
	/**
	 * Function Used for Check the RequiredMoney Ammount Depending on Bypass
	 * and Config.
	 * 
	 * <br>Target<br>
	 * @param player
	 */
	private void validateConditionToBuyFullBuffs(L2PcInstance player)                             
	{
		switch(_requiredMoney)
		{
		case 0: 
			_requiredMoney = Config.FULL_PROPHET_BUFFS_PRICE; 
			break;

		case 1: 
			_requiredMoney = Config.FULL_DANCES_PRICE;
			break;

		case 2:
			_requiredMoney = Config.FULL_SONGS_PRICE; 
			break;

		case 4:
			_requiredMoney = Config.SHAMAN_BUFFS_PRICE;
			break;

		case 5:
			_requiredMoney = Config.OVERLORD_BUFFS_PRICE; 
			break;
		case 6:
			_requiredMoney = Config.WARCRYER_BUFFS_PRICE;
			break;
		case 7:
			_requiredMoney = Config.FULL_CUBICS_PRICE;
			break;

		case 8:
			_requiredMoney = Config.FULL_SUMMON_BUFFS_PRICE;
			break;

		case 9:
			_requiredMoney = Config.FULL_HERO_BUFFS_PRICE;
			break;

		case 10:
			_requiredMoney = Config.FULL_NOBLE_BUFFS_PRICE;
			break;

		default:
			_requiredMoney = 100000;
		break;

		}

		int playerMoney = player.getAdena();
		SystemMessage sm;

		//checks if player has required adena.
		if (_requiredMoney > playerMoney)
		{
			sm = new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);  //returns if player dont have enough adena
			player.sendPacket(sm);
			sm = null;
			return;
		}

		if (!player.reduceAdena("Buffs", _requiredMoney, player.getLastFolkNPC(), true)) 
			return;

		//notify L2pcInstance that adena was spent 
		sm = new SystemMessage(SystemMessageId.DISSAPEARED_ADENA);
		sm.addNumber(_requiredMoney);
		player.sendPacket(sm);
		sm = null;

		//Updates L2pcinstance inventory
		InventoryUpdate iu = new InventoryUpdate(); 
		iu.addModifiedItem(player.getInventory().getItemByItemId(HardcodedItemTable.ADENA_ID));
		player.sendPacket(iu);  
	}
	/**
	 * Function Used to Trade SP Points For Sp Scrools.
	 * @param ActiveChar
	 * @param ItemID
	 * @param Count
	 */
	private void tradeSpPointsForSpScrolls(L2PcInstance player, int itemID, int count)
	{
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemID);
		if(item == null)
		{
			return;
		}
		else
		{
			item.setCount(count);
			player.getInventory().addItem("ReduceSP", item, player, player);
			SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
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