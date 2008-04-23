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
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Summon;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.L2Skill.SkillType;
import net.sf.l2j.gameserver.model.entity.events.CTF;
import net.sf.l2j.gameserver.model.entity.events.DM;
import net.sf.l2j.gameserver.model.entity.events.TvT;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.services.HtmlPathService;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * This class ...
 * 
 * @Author: Rayan RPG for L2Emu Project
 * 
 * 
 * @Date: april, 01, 2007
 *  
 *  
 *  @since  2.0.0 $
 * 
 * A Especial Instance To Manage Buffing Summons/Pets
 *
 */
public class L2SummonBufferInstance extends L2NpcInstance
{
	/** bypas type */
	private byte _bpType;

	/** ammount of money */
	private int _money;

	/** delay between npc buffer casts */
	private int _castingDelay = 1500;

	/**
	 * @param objectId
	 * @param template
	 */
	public L2SummonBufferInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		// TODO Auto-generated constructor stub
	}   
	/**
	 * Gets the main npc window(based on id)
	 * @see net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance#getHtmlPath(int, int)
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";

		if (val == 0) pom = "" + npcId;
		else pom = npcId + "-" + val;

		return HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH + pom + ".htm";
	}

	public void onBypassFeedback(L2PcInstance player, String command)
	{      
		HardcodedSkillTable hst = new HardcodedSkillTable();
		if(command.equalsIgnoreCase("BuyProphetBuffs")&& Config.CAN_SEEL_PROPHET_BUFFS) //Profhets Bypass
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 0;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%prophet_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_prophet_buffs_price%", String.valueOf(Config.FULL_PROPHET_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyDances")&& Config.CAN_SEEL_DANCES) //Dances Bypass 
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 1;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-dances.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%dance_price%", String.valueOf(Config.PRICE_PER_DANCE));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_dances_price%", String.valueOf(Config.FULL_DANCES_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuySongs") && Config.CAN_SEEL_SONGS) //Songs Bypass  
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 2;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-songs.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%song_Price%", String.valueOf(Config.PRICE_PER_SONG));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_songs_price%", String.valueOf(Config.FULL_SONGS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOrcBuffs")&& Config.CAN_SEEL_ORC_BUFFS) //Orc Bypass  
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 4;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-orc.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_orc_buff%", String.valueOf(Config.PRICE_PER_ORC_BUFF));
			html.replace("%full_shaman_buff_price%", String.valueOf(Config.SHAMAN_BUFFS_PRICE));
			html.replace("%full_warcryer_buff_price%", String.valueOf(Config.WARCRYER_BUFFS_PRICE));
			html.replace("%full_overlord_buff_price%", String.valueOf(Config.OVERLORD_BUFFS_PRICE));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("return_to_main_window")) //dirty hack to come back to main window
		{   
			int npcID = getNpcId();  
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());//will add proper code soon was too lazy for atm :P
			String filename = HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+ npcID+".htm";
			html.setFile(filename);
			if(filename!=null)
			{
				html.replace("%objectId%", String.valueOf(getObjectId()));
				html.replace("%npcname%", String.valueOf(getName()));
				player.sendPacket(html);
			}
		}
		if(command.equalsIgnoreCase("BuySummonBuffs") && Config.CAN_SEEL_SUMMON_BUFFS)//summon buffs bypass
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}

			_bpType = 5;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);

			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-summons.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_summon_buff%", String.valueOf(Config.PRICE_PER_SUMMON_BUFF));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_summon_buffs_price%",   String.valueOf(Config.FULL_SUMMON_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyHeroBuffs") && Config.CAN_SEEL_HERO_BUFFS)//summon buffs bypass
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}

			_bpType = 6;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);

			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-hero.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_hero_buff%", String.valueOf(Config.PRICE_PER_HERO_BUFF));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_hero_buffs_price%",   String.valueOf(Config.FULL_HERO_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyNobleBuffs") && Config.CAN_SEEL_NOBLE_BUFFS)//noble bypass
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 7;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(1);

			int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
			int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-noble.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			html.replace("%price_per_noble_buff%", String.valueOf(Config.PRICE_PER_NOBLE_BUFF));
			html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
			html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
			html.replace("%full_noble_buffs_price%",   String.valueOf(Config.FULL_NOBLE_BUFFS_PRICE));
			player.sendPacket(html);
		}
		if(command.equalsIgnoreCase("BuyOtherBuffs"))//other bypass
		{
			if(player.getPet() == null)
			{
				SendMissingSummonWindow(player);
				return;
			}
			_bpType = 8;
			int npcID = getNpcId();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-other.htm").toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%playername%", player.getName());
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}
		/**
		 * @Prophet Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Greater Empower"))
			makeSummonBuffs("Greater Empower", Config.PRICE_PER_PROPHET_BUFF, hst.GREATER_EMPOWER, 3, player, true);

		else if(command.startsWith("Acumen"))
			makeSummonBuffs("Acumen", Config.PRICE_PER_PROPHET_BUFF, hst.ACUMEN, 3, player, true);

		else if(command.startsWith("Focus"))
			makeSummonBuffs("Focus", Config.PRICE_PER_PROPHET_BUFF, hst.FOCUS, 3, player, true);

		else if(command.startsWith("Greater Might"))
			makeSummonBuffs("Greater Might", Config.PRICE_PER_PROPHET_BUFF, hst.GREATER_MIGHT, 3, player, true);

		else if(command.startsWith("Greater Shield"))
			makeSummonBuffs("Greater Shield", Config.PRICE_PER_PROPHET_BUFF, hst.GREATER_SHIELD, 3, player, true);

		else if(command.startsWith("Berserker Spirit"))
			makeSummonBuffs("Berserker Spirit", Config.PRICE_PER_PROPHET_BUFF, hst.BERSERKER_SPIRIT, 2, player, true);

		else if(command.startsWith("Invigor"))
			makeSummonBuffs("Invigor", Config.PRICE_PER_PROPHET_BUFF, hst.INVIGOR, 3, player, true);

		else if(command.startsWith("MentalAegis"))
			makeSummonBuffs("Mental Aegis", Config.PRICE_PER_PROPHET_BUFF, hst.MENTAL_AEGIS, 4, player, true);

		else if(command.startsWith("MagicBarrier"))
			makeSummonBuffs("Magic Barrier", Config.PRICE_PER_PROPHET_BUFF, hst.MAGIC_BARRIER, 2, player, true);

		else if(command.startsWith("Regeneration"))
			makeSummonBuffs("Regeneration", Config.PRICE_PER_PROPHET_BUFF, hst.REGENERATION, 3, player, true);

		else if(command.startsWith("BlessedBody"))
			makeSummonBuffs("Blessed Body", Config.PRICE_PER_PROPHET_BUFF, hst.BLESS_THE_BODY, 6, player, true);

		else if(command.startsWith("BlessedSoul"))
			makeSummonBuffs("Blessed Soul", Config.PRICE_PER_PROPHET_BUFF, hst.BLESS_THE_SOUL, 6, player, true);

		else if(command.startsWith("Greater Concentration"))
			makeSummonBuffs("Greater Concentration", Config.PRICE_PER_PROPHET_BUFF, hst.GREATER_CONCENTRATION, 6, player, true);

		else if(command.startsWith("Haste"))
			makeSummonBuffs("Haste", Config.PRICE_PER_PROPHET_BUFF, hst.HASTE, 2, player, true);

		else if(command.startsWith("Agility"))
			makeSummonBuffs("Agility", Config.PRICE_PER_PROPHET_BUFF, hst.AGILITY, 3, player, true);

		else if(command.startsWith("WindWalk"))
			makeSummonBuffs("Wind Walk", Config.PRICE_PER_PROPHET_BUFF, hst.WIND_WALK, 2, player, true);

		else if(command.startsWith("Guidance"))
			makeSummonBuffs("Guidance", Config.PRICE_PER_PROPHET_BUFF, hst.GUIDANCE, 3, player, true);

		else if(command.startsWith("DeathWhisper"))
			makeSummonBuffs("Death Whisper", Config.PRICE_PER_PROPHET_BUFF, hst.DEATH_WHISPER, 3, player, true);

		else if(command.startsWith("VampiricRage"))
			makeSummonBuffs("Vampiric Rage", Config.PRICE_PER_PROPHET_BUFF, hst.VAMPIRIC_RAGE, 4, player, true);

		else if(command.startsWith("BlessShield"))
			makeSummonBuffs("BlessShield", Config.PRICE_PER_PROPHET_BUFF, hst.BLESS_SHIELD, 6, player, true);

		/**
		 * All Prophet Buffs
		 */
		else if(command.startsWith("Full_Prophet_Buffs"))
		{
			_money = 0;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Greater Empower", 0, 1059, 3, player, false); //Greater Empower
			makeSummonBuffs("Acumen", 0, 1085, 3, player, false); //Acumen
			makeSummonBuffs("Focus", 0, hst.FOCUS, 3, player, false); //Focus
			makeSummonBuffs("Greater Migth", 0,hst.GREATER_MIGHT, 3, player, false); //Greater Migth
			makeSummonBuffs("Greater Shield", 0, hst.GREATER_SHIELD, 3, player, false); //Greater Shield
			makeSummonBuffs("Berseker Spirit", 0, hst.BERSERKER_SPIRIT, 2, player, false); //Berseker Spirit
			makeSummonBuffs("Invigor", 0, hst.INVIGOR, 3, player, false); //Invigor
			makeSummonBuffs("Mental Aegis", 0, hst.MENTAL_AEGIS, 4, player, false); //Mental Aegis
			makeSummonBuffs("Magic Barrier", 0, hst.MAGIC_BARRIER, 2, player, false); //Magic Barrier
			makeSummonBuffs("Regeneration", 0, hst.REGENERATION , 3, player, false); //Regeneration
			makeSummonBuffs("Blessed Body", 0, hst.BLESS_THE_BODY, 6, player, false); //Bless the Body
			makeSummonBuffs("Blessed Soul", 0, hst.BLESS_THE_SOUL, 6, player, false); //Bless The Soul
			makeSummonBuffs("Greater Concentration", 0, hst.GREATER_CONCENTRATION, 6, player, false); //Greater Concentration
			makeSummonBuffs("Haste", 0, hst.HASTE, 2, player, false); //Haste
			makeSummonBuffs("Agility", 0, hst.AGILITY, 3, player, false); //Agility
			makeSummonBuffs("Wind Walk", 0, hst.WIND_WALK, 2, player, false); //Wind Walk
			makeSummonBuffs("Guidance", 0, hst.GUIDANCE, 3, player, false); //Guidance
			makeSummonBuffs("Death Wispher", 0, hst.DEATH_WHISPER, 3, player, false); //Death Wispher
			makeSummonBuffs("Vampiric Range", 0, hst.VAMPIRIC_RAGE, 4, player, false); //Vampiric Range
			makeSummonBuffs("Bless Shield", 0, hst.BLESS_SHIELD, 6, player, false); //Bless Shield    
			ResendMainWindow(player);
		}
		/**
		 * @Dances Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */

		else if(command.startsWith("Dance of Warrior"))
			makeSummonBuffs("Dance of Warrior", Config.PRICE_PER_DANCE, hst.DANCE_OF_WARRIOR, 1, player, true);

		else if(command.startsWith("Dance of Inspiration"))
			makeSummonBuffs("Dance of Inspiration", Config.PRICE_PER_DANCE, hst.DANCE_OF_INSPIRATION, 1, player, true);

		else if(command.startsWith("Dance of Mystic"))
			makeSummonBuffs("Dance of Mystic", Config.PRICE_PER_DANCE, hst.DANCE_OF_MYSTIC, 1, player, true);

		else if(command.startsWith("Dance of Fire"))
			makeSummonBuffs("Dance of Fire", Config.PRICE_PER_DANCE, hst.DANCE_OF_FIRE, 1, player, true);

		else if(command.startsWith("Dance of Fury"))
			makeSummonBuffs("Dance of Fury", Config.PRICE_PER_DANCE, hst.DANCE_OF_FURY, 1, player, true);

		else if(command.startsWith("Dance of Concentration"))
			makeSummonBuffs("Dance of Concentration", Config.PRICE_PER_DANCE, hst.DANCE_OF_CONCENTRATION, 1, player, true);

		else if(command.startsWith("Dance of Light"))
			makeSummonBuffs("Dance of Light", Config.PRICE_PER_DANCE, hst.DANCE_OF_LIGHT, 1, player, true);

		else if(command.startsWith("Dance of Aqua Guard"))
			makeSummonBuffs("Dance of Aqua Guard", Config.PRICE_PER_DANCE, hst.DANCE_OF_AQUA_GUARD, 1, player, true);

		else if(command.startsWith("Dance of Earth Guard"))
			makeSummonBuffs("Dance of Earth Guard", Config.PRICE_PER_DANCE, hst.DANCE_OF_EARTH_GUARD, 1, player, true);

		else if(command.startsWith("Dance of Vampire"))
			makeSummonBuffs("Dance of Vampire", Config.PRICE_PER_DANCE, hst.DANCE_OF_VAMPIRE, 1, player, true);

		else if(command.startsWith("Dance of Protection"))
			makeSummonBuffs("Dance of Protection", Config.PRICE_PER_DANCE, hst.DANCE_OF_PROTECTION, 1, player, true);

		else if(command.startsWith("Dance of Siren"))
			makeSummonBuffs("Dance of Siren", Config.PRICE_PER_DANCE, hst.DANCE_OF_SIREN, 1, player, true);

		else if(command.startsWith("Dance of Shadow"))
			makeSummonBuffs("Dance of Shadow", Config.PRICE_PER_DANCE, hst.DANCE_OF_SHADOW, 1, player, true);


		//TODO: Missing Dance of Resist,nihil, weakness

		/**
		 * All Dances Buffs 
		 * Structure (Skillid, Skill Level, Target)
		 */ 
		else if(command.startsWith("Full_Dances"))
		{
			_money = 1;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Dance of Warrior", 0, hst.DANCE_OF_WARRIOR, 1, player,false); //Dance of Warrior
			makeSummonBuffs("Dance of Inspiration", 0, hst.DANCE_OF_INSPIRATION, 1, player,false); //Dance of Inspiration
			makeSummonBuffs("Dance of Mystic", 0, hst.DANCE_OF_MYSTIC, 1, player, false); //Dance of Mystic
			makeSummonBuffs("Dance of Fire", 0, hst.DANCE_OF_FIRE, 1, player, false); //Dance of Fire
			makeSummonBuffs("Dance of Fury", 0, hst.DANCE_OF_FURY, 1, player, false); //Dance of Fury
			makeSummonBuffs("Dance of Concentration", 0, hst.DANCE_OF_CONCENTRATION, 1, player, false); //Dance of Concentration
			makeSummonBuffs("Dance of Light", 0, hst.DANCE_OF_LIGHT, 1, player, false); //Dance of Light
			makeSummonBuffs("Dance of Aqua Guard", 0, hst.DANCE_OF_AQUA_GUARD, 1, player, false); //Dance of Aqua Guard
			makeSummonBuffs("Dance of Earth Guard", 0, hst.DANCE_OF_EARTH_GUARD, 1, player, false); //Dance of Earth Guard
			makeSummonBuffs("Dance of Vampire", 0, hst.DANCE_OF_VAMPIRE, 1, player, false); //Dance of Vampire
			makeSummonBuffs("Dance of Protection", 0, hst.DANCE_OF_PROTECTION, 1, player, false); //Dance of Protection
			makeSummonBuffs("Dance of Siren", 0, hst.DANCE_OF_SIREN, 1, player, false); //Dance of Siren
			ResendMainWindow(player);

		}
		/**
		 * @Songs Buffs
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Song of Earth"))
			makeSummonBuffs("Song of Earth", Config.PRICE_PER_SONG, hst.SONG_OF_EARTH, 1, player, true);

		else if(command.startsWith("Song of Life"))
			makeSummonBuffs("Song of Life", Config.PRICE_PER_SONG, hst.SONG_OF_LIFE, 1, player, true);

		else if(command.startsWith("Song of Water"))
			makeSummonBuffs("Song of Water", Config.PRICE_PER_SONG, hst.SONG_OF_WATER, 1, player, true);

		else if(command.startsWith("Song of Warding"))
			makeSummonBuffs("Song of Warding", Config.PRICE_PER_SONG, hst.SONG_OF_WARDING, 1, player, true);

		else if(command.startsWith("Song of Wind"))
			makeSummonBuffs("Song of Wind", Config.PRICE_PER_SONG, hst.SONG_OF_WIND, 1, player, true);

		else if(command.startsWith("Song of Hunter"))
			makeSummonBuffs("Song of Hunter", Config.PRICE_PER_SONG, hst.SONG_OF_HUNTER, 1, player, true);

		else if(command.startsWith("Song of Invocation"))
			makeSummonBuffs("Song of Invocation", Config.PRICE_PER_SONG, hst.SONG_OF_INVOCATION, 1, player, true);

		else if(command.startsWith("Song of Vitality"))
			makeSummonBuffs("Song of Vitality", Config.PRICE_PER_SONG, hst.SONG_OF_VITALITY, 1, player, true);

		else if(command.startsWith("Song of Vengeance"))
			makeSummonBuffs("Song of Vengeance", Config.PRICE_PER_SONG, hst.SONG_OF_VENGEANCE, 1, player, true);

		else if(command.startsWith("Song of Flame Guard"))
			makeSummonBuffs("Song of Flame Guard", Config.PRICE_PER_SONG, hst.SONG_OF_FLAME_GUARD, 1, player, true);

		else if(command.startsWith("Song of Storm Guard"))
			makeSummonBuffs("Song of Storm Guard", Config.PRICE_PER_SONG, hst.SONG_OF_STORM_GUARD, 1, player, true);

		else if(command.startsWith("Song of Renewal"))
			makeSummonBuffs("Song of Renewal", Config.PRICE_PER_SONG, hst.SONG_OF_RENEWAL, 1, player, true);

		else if(command.startsWith("Song of Meditation"))
			makeSummonBuffs("Song of Meditation", Config.PRICE_PER_SONG, hst.SONG_OF_MEDITATION, 1, player, true);

		else if(command.startsWith("Song of Champion"))
			makeSummonBuffs("Song of Champion", Config.PRICE_PER_SONG, hst.SONG_OF_CHAMPION, 1, player, true);

		/*
		 * TODO: Missing in Skill Trees
		 * else if(command.startsWith("Song of Seduce"))
       makeSummonBuffs("Song of Seduce", Config.PRICE_PER_SONG, 4536, 1, player, true, false);
        else if(command.startsWith("Song of Sweet Whisper"))
       makeSummonBuffs("Song of Sweet Whisper", Config.PRICE_PER_SONG, 4537, 1, player, true, false);
        else if(command.startsWith("Song of Temptation"))
       makeSummonBuffs("Song of Temptation", Config.PRICE_PER_SONG, 4538, 1, player, true, false);
		 */

		/**
		 * All Songs Buffs 
		 * Structure (Skillid, Skill Level, Target)
		 */
		else if(command.startsWith("Full_Songs"))
		{
			_money = 2;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Song of Earth", 0, hst.SONG_OF_EARTH, 1, player, false); //Song of Earth
			makeSummonBuffs("Song of Life", 0, hst.SONG_OF_LIFE, 1, player, false); //Song of Life
			makeSummonBuffs("Song of Water", 0, hst.SONG_OF_WATER, 1, player, false); //Song of Water
			makeSummonBuffs("Song of Warding", 0, hst.SONG_OF_WARDING, 1, player, false); //Song of Warding
			makeSummonBuffs("Song of Wind ", 0, hst.SONG_OF_WIND, 1, player, false); //Song of Wind
			makeSummonBuffs("Song of Hunter", 0, hst.SONG_OF_HUNTER, 1, player, false); //Song of Hunter
			makeSummonBuffs("Song of Invocation", 0, hst.SONG_OF_INVOCATION, 1, player, false); //Song of Invocation
			makeSummonBuffs("Song of Vitality", 0, hst.SONG_OF_VITALITY, 1, player, false); //Song of Vitality
			makeSummonBuffs("Song of Vengeance", 0, hst.SONG_OF_VENGEANCE, 1, player, false); //Song of Vengeance
			makeSummonBuffs("Song of Flame Guard", 0, hst.SONG_OF_FLAME_GUARD, 1, player, false); //Song of Flame Guard
			makeSummonBuffs("Song of Storm Guard", 0, hst.SONG_OF_STORM_GUARD, 1, player, false); //Song of Storm Guard
			makeSummonBuffs("Song of Renewal", 0, hst.SONG_OF_RENEWAL, 1, player, false); //Song of Renewal
			makeSummonBuffs("Song of Meditation", 0, hst.SONG_OF_MEDITATION, 1, player, false); //Song of Meditation
			makeSummonBuffs("Song of Champion", Config.PRICE_PER_SONG, hst.SONG_OF_CHAMPION, 1, player, false); //Song of Champion
			ResendMainWindow(player);
		}
		/**
		 * @Orc Buffs
		 */
		else if(command.startsWith("Flame Chant"))    //shaman
			makeSummonBuffs("Flame Chant", Config.PRICE_PER_ORC_BUFF, hst.FLAME_CHANT, 1, player, true);

		else if(command.startsWith("Pa'agrian Gift"))    //shaman
			makeSummonBuffs("Pa'agrian Gift", Config.PRICE_PER_ORC_BUFF, hst.PAAGRIAN_GIFT, 3, player, true);

		else if(command.startsWith("Blessings of Pa'agrio"))    //shaman
			makeSummonBuffs("Blessings of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.BLESSINGS_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("Chant of Fire"))   //shaman
			makeSummonBuffs("Chant of Fire", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_FIRE, 3, player, true);

		else if(command.startsWith("Chant of Battle"))    //shaman
			makeSummonBuffs("Chant of Battle", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_BATTLE, 2, player, true);

		else if(command.startsWith("Chant of Shielding"))   //shaman
			makeSummonBuffs("Chant of Shielding", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_SHIELDING, 3, player, true);

		else if(command.startsWith("Soul Shield"))   //shaman
			makeSummonBuffs("Soul Shield", Config.PRICE_PER_ORC_BUFF, hst.SOUL_SHIELD, 3, player, true);

		else if(command.startsWith("The Wisdom of Pa'agrio"))    //overlord
			makeSummonBuffs("The Wisdom of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_WISDOM_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Glory of Pa'agrio"))    //overlord
			makeSummonBuffs("The Glory of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_GLORY_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Vision of Pa'agrio"))    //overlord
			makeSummonBuffs("The Vision of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_VISION_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("Under the Protection of Pa'agrio"))    //overlord
			makeSummonBuffs("Under the Protection of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.UNDER_THE_PROTECTION_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Heart of Pa'agrio"))    //overlord
			makeSummonBuffs("The Heart of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_HEART_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Tact of Pa'agrio"))    //overlord
			makeSummonBuffs("The Tact of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_TACT_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Rage of Pa'agrio"))    //overlord
			makeSummonBuffs("The Rage of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_RAGE_OF_PAAGRIO, 3, player, true);

		else if(command.startsWith("The Honor of Pa'agrio"))    //overlord
			makeSummonBuffs("The Honor of Pa'agrio", Config.PRICE_PER_ORC_BUFF, hst.THE_HONOR_OF_PAAGRIO, 5, player, true);

		else if(command.startsWith("The Eye of Paagrio"))//overlord
			makeSummonBuffs("The Eye of Paagrio", Config.PRICE_PER_ORC_BUFF, hst.THE_EYE_OF_PAAGRIO, 1, player, true);

		else if(command.startsWith("The Soul of Paagrio"))//overlord
			makeSummonBuffs("The Soul of Paagrio", Config.PRICE_PER_ORC_BUFF, hst.THE_SOUL_OF_PAAGRIO, 1, player, true);

		else if(command.startsWith("Chant of Fury"))    //warcryer
			makeSummonBuffs("Chant of Fury", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_FURY, 2, player, true);

		else if(command.startsWith("Chant of Evasion"))    //warcryer
			makeSummonBuffs("Chant of Evasion", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_EVASION, 3, player, true);

		else if(command.startsWith("Chant of Rage"))    //warcryer
			makeSummonBuffs("Chant of Rage", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_RAGE, 3, player, true);

		else if(command.startsWith("Chant of Revenge"))    //warcryer
			makeSummonBuffs("Chant of Revenge", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_REVENGE, 3, player, true);

		else if(command.startsWith("Chant of Predator"))//warcryer
			makeSummonBuffs("Chant of Predator", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_PREDATOR, 3, player, true);

		else if(command.startsWith("Chant of Eagle"))//warcryer
			makeSummonBuffs("Chant of Eagle", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_EAGLE, 3, player, true);

		else if(command.startsWith("Chant of Vampire"))//warcryer
			makeSummonBuffs("Chant of Vampire", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_VAMPIRE, 4, player, true);

		else if(command.startsWith("Chant of Spirit")) //warcryer
			makeSummonBuffs("Chant of Spirit", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_SPIRIT, 1, player, true);

		else if(command.startsWith("Chant of Victory"))//warcryer
			makeSummonBuffs("Chant of Victory", Config.PRICE_PER_ORC_BUFF, hst.CHANT_OF_VICTORY, 1, player, true);

		else if(command.startsWith("War Chant"))//warcryer
			makeSummonBuffs("War Chant", Config.PRICE_PER_ORC_BUFF, hst.WAR_CHANT, 3, player, true);

		else if(command.startsWith("Earth Chant"))//warcryer
			makeSummonBuffs("Earth Chant", Config.PRICE_PER_ORC_BUFF, hst.EARTH_CHANT, 3, player, true);


		/**
		 * @Shaman buffs
		 */
		else if(command.startsWith("Shaman_Buffs") && Config. CAN_SEEL_SHAMAN_BUFFS)
		{
			_money = 4; //global variable to get _money for 
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Flame Chant", 0, hst.FLAME_CHANT, 1, player, false);
			makeSummonBuffs("Pa'agrian Gift", 0, hst.PAAGRIAN_GIFT, 3, player, false);
			makeSummonBuffs("Blessings of Pa'agrio", 0, hst.BLESSINGS_OF_PAAGRIO, 3, player, false);  
			makeSummonBuffs("Chant of Fire", 0, hst.CHANT_OF_FIRE, 3, player, false);
			makeSummonBuffs("Chant of Battle", 0, hst.CHANT_OF_BATTLE, 2, player, false);
			makeSummonBuffs("Chant of Shielding", 0, hst.CHANT_OF_SHIELDING, 3, player, false);
			makeSummonBuffs("Soul Shield", 0, hst.SOUL_SHIELD, 3, player, false);
			ResendMainWindow(player);
		}
		/**
		 * @Overlord Buffs
		 */
		else if(command.startsWith("Overlord_Buffs")&& Config.CAN_SEEL_OVERLORD_BUFFS)
		{
			_money = 5;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("The Wisdom of Pa'agrio", 0, hst.THE_WISDOM_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Glory of Pa'agrio", 0, hst.THE_GLORY_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Vision of Pa'agrio", 0, hst.THE_VISION_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("Under the Protection of Pa'agrio", 0, hst.UNDER_THE_PROTECTION_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Heart of Pa'agrio", 0, hst.THE_HEART_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Tact of Pa'agrio", 0, hst.THE_TACT_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Rage of Pa'agrio", 0, hst.THE_RAGE_OF_PAAGRIO, 3, player, false);
			makeSummonBuffs("The Honor of Pa'agrio", 0, hst.THE_HONOR_OF_PAAGRIO, 5, player, false);
			makeSummonBuffs("The Eye of Paagrio", 0, hst.THE_EYE_OF_PAAGRIO, 1, player, false);
			makeSummonBuffs("The Soul of Paagrio", 0, hst.THE_SOUL_OF_PAAGRIO, 1, player, false);
			ResendMainWindow(player);
		}
		/**
		 * @Warcryer Buffs
		 */
		else if(command.startsWith("Warcryer_Buffs") && Config.CAN_SEEL_WARCRYER_BUFFS)
		{
			_money = 6;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Chant of Fury", 0, hst.CHANT_OF_FURY, 2, player, false);
			makeSummonBuffs("Chant of Evasion", 0, hst.CHANT_OF_EVASION, 3, player, false);
			makeSummonBuffs("Chant of Rage", 0, hst.CHANT_OF_RAGE, 3, player, false);
			makeSummonBuffs("Chant of Revenge", 0, hst.CHANT_OF_REVENGE, 3, player, false);
			makeSummonBuffs("Chant of Predator", 0, hst.CHANT_OF_PREDATOR, 3, player, false);
			makeSummonBuffs("Chant of Eagle", 0, hst.CHANT_OF_EAGLE, 3, player, false);
			makeSummonBuffs("Chant of Vampire", 0, hst.CHANT_OF_VAMPIRE, 4, player, false);
			makeSummonBuffs("Chant of Spirit", 0, hst.CHANT_OF_SPIRIT, 1, player, false);
			makeSummonBuffs("Chant of Victory", 0, hst.CHANT_OF_VICTORY, 1, player, false);
			makeSummonBuffs("War Chant", 0, hst.WAR_CHANT, 3, player, false);
			makeSummonBuffs("Earth Chant", 0, hst.EARTH_CHANT, 3, player, false);
			ResendMainWindow(player);
		}

		/** 
		 * @Full Summons Buffs
		 */
		else if (command.startsWith("FullSummonBuffs"))
		{
			_money = 7;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Blessing of Queen", 0, hst.BLESSING_OF_QUEEN, 13, player, false);
			makeSummonBuffs("Gift of Queen", 0, hst.GIFT_OF_QUEEN, 3, player, false);
			makeSummonBuffs("Blessing of Seraphim", 0, hst.BLESSING_OF_SERAPHIM, 13, player, false);
			makeSummonBuffs("Gift of  Serapfim", 0, hst.GIFT_OF_SERAPHIM, 13, player, false);
			makeSummonBuffs("Cure of Seraphim", 0, hst.CURE_OF_SERAPHIM, 13, player, false);
			ResendMainWindow(player);
		}
		/** 
		 * @Summons Buffs
		 */
		else if (command.startsWith("Blessing of Queen"))
			makeSummonBuffs("Blessing of Queen",  Config.PRICE_PER_SUMMON_BUFF, hst.BLESSING_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Gift of Queen"))
			makeSummonBuffs("Gift of Queen",  Config.PRICE_PER_SUMMON_BUFF, hst.GIFT_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Blessing of Seraphim"))
			makeSummonBuffs("Blessing of Seraphim",  Config.PRICE_PER_SUMMON_BUFF, hst.BLESSING_OF_SERAPHIM, 13, player, true);

		else if (command.startsWith("Gift of Serapfim"))
			makeSummonBuffs("Gift of  Serapfim",  Config.PRICE_PER_SUMMON_BUFF, hst.GIFT_OF_SERAPHIM, 13, player,true);

		else if (command.startsWith("Cure of Queen"))
			makeSummonBuffs("Cure of Queen",  Config.PRICE_PER_SUMMON_BUFF, hst.CURE_OF_QUEEN, 13, player, true);

		else if (command.startsWith("Cure of Seraphim"))
			makeSummonBuffs("Cure of Queen",  Config.PRICE_PER_SUMMON_BUFF, hst.CURE_OF_SERAPHIM, 13, player, true);



		/**
		 * @Hero Buffs
		 */
		else if(command.startsWith("Heroic Miracle"))
			makeSummonBuffs("Heroic Miracle", Config.PRICE_PER_HERO_BUFF, hst.HEROIC_MIRACLE, 1, player, true);
		else if(command.startsWith("Heroic Berserker"))
			makeSummonBuffs("Heroic Berserker", Config.PRICE_PER_HERO_BUFF, hst.HEROIC_BERSERKER, 1, player, true);
		else if(command.startsWith("Heroic Valor"))
			makeSummonBuffs("Heroic Valor", Config.PRICE_PER_HERO_BUFF, hst.HEROIC_VALOR, 1, player, true);

		/**
		 * @Full Hero Buffs
		 */
		else if(command.startsWith("FullHeroBuffs"))
		{
			_money = 8;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Heroic Miracle",0, hst.HEROIC_MIRACLE, 1, player, false);
			makeSummonBuffs("Heroic Berserker",0, hst.HEROIC_BERSERKER, 1, player, false);
			makeSummonBuffs("Heroic Valor",0, hst.HEROIC_VALOR, 1, player, false);
			ResendMainWindow(player);
		}
		/**
		 * @Noble Buffs
		 */
		else if(command.startsWith("Blessing of Noblesse"))
			makeSummonBuffs("Blessing of Noblesse", Config.PRICE_PER_NOBLE_BUFF, hst.BLESSING_OF_NOBLESSE, 1, player, true);
		else if(command.startsWith("Fortune of Noblesse"))
			makeSummonBuffs("Fortune of Noblesse", Config.PRICE_PER_NOBLE_BUFF, hst.FORTUNE_OF_NOBLESSE, 1, player, true);


		/**
		 * @Full Noble Buffs
		 */
		else if(command.startsWith("FullNobleBuffs"))
		{
			_money = 9;
			ValidateConditionToBuyFullSummonBuffs(player);
			makeSummonBuffs("Blessing of Noblesse",0, hst.BLESSING_OF_NOBLESSE, 1, player, false);
			makeSummonBuffs("Fortune of Noblesse",0, hst.FORTUNE_OF_NOBLESSE, 1, player, false);
			ResendMainWindow(player);
		}

		//*********** OTHER BUFFS ****************/
		else if(command.startsWith("prophecy of water"))
			makeSummonBuffs("prophecy of water", Config.PRICE_PER_PROPHET_BUFF, hst.PROPHECY_OF_WATER, 1, player, true);

		else if(command.startsWith("prophecy of fire"))
			makeSummonBuffs("prophecy of fire", Config.PRICE_PER_PROPHET_BUFF, hst.PROPHECY_OF_FIRE, 1, player, true);

		else if(command.startsWith("prophecy of wind"))
			makeSummonBuffs("prophecy of wind", Config.PRICE_PER_PROPHET_BUFF, hst.PROPHECY_OF_WIND, 1, player, true);

		else if(command.startsWith("wild magic"))
			makeSummonBuffs("wild magic", Config.PRICE_PER_PROPHET_BUFF, hst.WILD_MAGIC, 1, player, true);

		else if(command.startsWith("migth"))
			makeSummonBuffs("migth", Config.PRICE_PER_PROPHET_BUFF, hst.MIGHT, 3, player, true);

		else if(command.startsWith("shield"))
			makeSummonBuffs("shield", Config.PRICE_PER_PROPHET_BUFF, hst.SHIELD, 3, player, true);

		else if(command.startsWith("unholy resistence"))
			makeSummonBuffs("unholy resistence", Config.PRICE_PER_PROPHET_BUFF, hst.UNHOLY_RESISTENCE, 3, player, true);

		else if(command.startsWith("holy resistence"))
			makeSummonBuffs("holy resistence", Config.PRICE_PER_PROPHET_BUFF, hst.HOLY_RESISTENCE, 3, player, true);

		else if(command.startsWith("elemental protection"))
			makeSummonBuffs("elemental protection", Config.PRICE_PER_PROPHET_BUFF, hst.ELEMENTAL_PROTECTION, 1, player, true);

		else if(command.startsWith("divine protection"))
			makeSummonBuffs("divine protection", Config.PRICE_PER_PROPHET_BUFF, hst.DIVINE_PROTECTION, 1, player, true);
		//***************************************************************************************************
		else if(command.startsWith("RestoreSummonHP"))
			RestoreSummonHP(player);
		else if(command.startsWith("RestoreSummonMP"))
			RestoreSummonMP(player);
	}
	/**
	 * Function Used for Check the Requiredmoney Ammount Depending on Bypass
	 * and Config.
	 * 
	 * @param Requiredmoney <br>
	 * @param prophet Requiredmoney 0<br>
	 * @param dances Requiredmoney 1<br>
	 * @param songs Requiredmoney 2<br>
	 * @param shaman Requiredmoney 4<br>
	 * @param overlord Requiredmoney 5<br>
	 * @param warcryer Requiredmoney 6<br>
	 * @param Cubics Requiredmoney 7<br>
	 * @param Summons Requiredmoney 8<br>
	 * <br>Target<br>
	 * @param ActivePlayer
	 */
	private void ValidateConditionToBuyFullSummonBuffs(L2PcInstance ActivePlayer)                             
	{
		switch(_money)
		{
		case 0: 
			_money = Config.FULL_PROPHET_BUFFS_PRICE; 
			break;
		case 1: 
			_money = Config.FULL_DANCES_PRICE;
			break;
		case 2:
			_money = Config.FULL_SONGS_PRICE; 
			break;
		case 4:
			_money = Config.SHAMAN_BUFFS_PRICE;
			break;
		case 5:
			_money = Config.OVERLORD_BUFFS_PRICE; 
			break;
		case 6:
			_money = Config.WARCRYER_BUFFS_PRICE;
			break;
		case 7:
			_money = Config.FULL_SUMMON_BUFFS_PRICE;
			break;
		case 8:
			_money = Config.FULL_HERO_BUFFS_PRICE;
			break;
		case 9:
			_money = Config.FULL_NOBLE_BUFFS_PRICE;
			break;

		default:
			_money = 100000;
		}
		SystemMessage sm;
		int currentmoney = ActivePlayer.getAdena();
		if (_money > currentmoney)
		{
			sm = new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA);  //returns if player dont have enough adena
			ActivePlayer.sendPacket(sm);
			sm = null;
			return;
		}

		if (!ActivePlayer.reduceAdena("Buffs", _money, ActivePlayer.getLastFolkNPC(), true)) 
			return;

		sm = new SystemMessage(SystemMessageId.DISSAPEARED_ADENA); //notify L2pcInstance that adena was spent 
		sm.addNumber(_money); //Removes the value depending on Switch case
		sm = null;

		InventoryUpdate iu = new InventoryUpdate(); 
		iu.addModifiedItem(ActivePlayer.getInventory().getItemByItemId(HardcodedItemTable.ADENA_ID)); //removes the value
		ActivePlayer.sendPacket(iu);  //Updates  L2pcinstance  inventory
	}
	/**
	 * 
	 * @param ActivePlayer
	 */
	private void RestoreSummonHP(L2PcInstance player)
	{
		if(Config.ALLOW_NPC_BUFFER)
		{
			int currentmoney = player.getAdena();
			double SummonCurrentHP = player.getPet().getStatus().getCurrentCp();
			double SummmonMaxHP = player.getPet().getMaxHp();

			int SummonHpToRestore = (int)SummmonMaxHP - (int)SummonCurrentHP;

			if(SummonHpToRestore * Config.PRICE_PER_SUMMON_HP_POINT > currentmoney && !player.isGM())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				return;
			}
			if(!player.isGM())
				player.reduceAdena("NpcBuffer: HP recovery", SummonHpToRestore * Config.PRICE_PER_SUMMON_HP_POINT, player, false);

			if(SummonHpToRestore<=0)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("The HP of yor summon is full, restore not needed.");
				player.sendPacket(sm);
				sm = null;
				return;
			}
			SendRestoreWindow(player);
			player.getPet().getStatus().setCurrentHp(SummmonMaxHP + 1.0D);
		}
		else
		{
			SendNegateWindow(player);
			return;
		}
	}
	/**
	 * 
	 * @param ActivePlayer
	 */
	private void RestoreSummonMP(L2PcInstance ActivePlayer)
	{
		if(Config.ALLOW_NPC_BUFFER)
		{
			int currentmoney = ActivePlayer.getAdena();
			double SummonCurrentMP = ActivePlayer.getPet().getStatus().getCurrentMp();
			double SummonMaxMP = ActivePlayer.getPet().getMaxMp();
			int SummonHpToRestore = (int)SummonMaxMP - (int)SummonCurrentMP;

			if(SummonHpToRestore * Config.PRICE_PER_SUMMON_MP_POINT > currentmoney && !ActivePlayer.isGM())
			{
				ActivePlayer.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				return;
			}
			if(!ActivePlayer.isGM())
				ActivePlayer.reduceAdena("NpcBuffer: MP recovery", SummonHpToRestore * Config.PRICE_PER_SUMMON_MP_POINT, ActivePlayer, false);

			if(SummonHpToRestore<=0)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("The MP of yor summon is full, restore not needed.");
				ActivePlayer.sendPacket(sm);
				sm = null;
				return;
			}
			SendRestoreWindow(ActivePlayer);
			ActivePlayer.getPet().getStatus().setCurrentMp(SummonMaxMP + 1.0D);
		}
		else
		{
			SendNegateWindow(ActivePlayer);
			return;
		}
	}
	/**
	 * 
	 * @param player
	 */
	private void SendMissingSummonWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+"missing-summon.htm");
		html.setHtml(filename); 
		if (filename != null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * @param BuffName
	 * @param PriceInConfig
	 * @param SkillID
	 * @param SkillLVL
	 * @param ActivePlayer
	 * @param ResendHTML
	 */
	private void makeSummonBuffs(String buffName, int priceInConfig, int skillId, int skillLvl, L2PcInstance player, boolean resendPage)
	{
		int npcID = getNpcId();
		NpcHtmlMessage html = new NpcHtmlMessage(1);

		int Mpz = player.getPet().getMaxMp() - (int)player.getPet().getStatus().getCurrentMp();
		int Hpz = player.getPet().getMaxHp() - (int)player.getPet().getStatus().getCurrentHp();

		int summonlvl = player.getPet().getLevel();

		L2Summon Summon = player.getPet();
		setTarget(Summon);
		if(Config.ALLOW_NPC_BUFFER)
		{
			if(summonlvl >= Config.MIN_SUMMON_LEVEL_TO_GET_BUFFS && summonlvl <= Config.MAX_SUMMON_LEVEL_TO_GET_BUFFS)
			{
				if(player.getInventory().getAdena() <= priceInConfig && !player.isGM())//exclude GMS
				{
					player.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
					return;
				}
				//dont buffs if player is dead or fake dead 
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
				if(Config.CTF_DISABLE_NPC_BUFFER &&player._inEventCTF && Config.CTF_ON_START_REMOVE_ALL_EFFECTS && CTF._started && !player.isGM())
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
				if(player.getPet()!= null && player.getPet().isDead())//npe prevention
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("Pet is dead, can't buff it.");
					player.sendPacket(sm);
					sm = null;
					return;
				}
				player.reduceAdena((new StringBuilder("NpcBuffer: ")).append(buffName).toString(), priceInConfig, player, true);

				//gets the skill info
				L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if(_log.isDebugEnabled())
					_log.info("For Skill "+skill+" Taken.");

				setTarget(player.getPet());

				if(_log.isDebugEnabled())
					_log.info("new target set.");

				if(Config.REGENERATE_STATS_FOR_FREE)
				{
					//regenerates summon stats
					player.getPet().getStatus().setCurrentHpMp(player.getPet().getMaxHp(), player.getPet().getMaxMp());
				}
				Summon.getOwner().sendMessage("Your summon can feel "+skill.getName()+" effect."); 

				if(resendPage)
				{
					switch (_bpType)
					{ 
					case 0://prophets
					{ 
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-prophet.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%playername%", player.getName());
						html.replace("%prophet_buff_price%", String.valueOf(Config.PRICE_PER_PROPHET_BUFF));
						html.replace("%full_prophet_buffs_price%", String.valueOf(Config.FULL_PROPHET_BUFFS_PRICE));
						player.sendPacket(html);
						break;
					}
					case 1: //dances
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-dances.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%playername%", player.getName());
						html.replace("%dance_Price%", String.valueOf(Config.PRICE_PER_DANCE));
						html.replace("%full_dances_price%", String.valueOf(Config.FULL_DANCES_PRICE));
						player.sendPacket(html);
						break;
					}
					case 2: //songs
					{  
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-songs.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName()); 
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%song_price%", String.valueOf(Config.PRICE_PER_SONG));
						html.replace("%full_songs_price%", String.valueOf(Config.FULL_SONGS_PRICE));
						player.sendPacket(html);
						break;
					}
					case 4: //orc
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-orc.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%price_per_orc_buff%", String.valueOf(Config.PRICE_PER_ORC_BUFF));
						html.replace("%full_shaman_buff_price%", String.valueOf(Config.SHAMAN_BUFFS_PRICE));
						html.replace("%full_warcryer_buff_price%", String.valueOf(Config.WARCRYER_BUFFS_PRICE));
						html.replace("%full_overlord_buff_price%", String.valueOf(Config.OVERLORD_BUFFS_PRICE));
						player.sendPacket(html);
						break;

					}
					case 5:// summons buffs
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-summons.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						html.replace("%price_per_summon_buff%", String.valueOf(Config.PRICE_PER_SUMMON_BUFF));
						html.replace("%full_summon_buffs_price%", String.valueOf(Config.FULL_SUMMON_BUFFS_PRICE));
						player.sendPacket(html); 
						break;
					}
					case 6:// hero buffs
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-hero.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						html.replace("%price_per_hero_buff%", String.valueOf(Config.PRICE_PER_HERO_BUFF));
						html.replace("%full_hero_buffs_price%", String.valueOf(Config.FULL_HERO_BUFFS_PRICE));
						player.sendPacket(html); 
						break;
					}
					case 7:// noble buffs
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-noble.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						html.replace("%price_per_noble_buff%", String.valueOf(Config.PRICE_PER_NOBLE_BUFF));
						html.replace("%full_noble_buffs_price%", String.valueOf(Config.FULL_NOBLE_BUFFS_PRICE));
						player.sendPacket(html); 
						break;
					}
					case 8://other
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).append("-other.htm").toString());
						html.replace("%price_per_summon_hp_point%", String.valueOf(Hpz * Config.PRICE_PER_SUMMON_HP_POINT));
						html.replace("%price_per_summon_mp_point%", String.valueOf(Mpz * Config.PRICE_PER_SUMMON_MP_POINT));
						html.replace("%npcname%", String.valueOf(getName()));
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						player.sendPacket(html);
						break;
					}
					default:
					{
						html.setFile((new StringBuilder(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH)).append(npcID).toString());
						html.replace("%objectId%", String.valueOf(getObjectId()));
						html.replace("%playername%", player.getName());
						player.sendPacket(html); 
						break;
					}

					}
				}

				doCast(skill);
				if(_log.isDebugEnabled())
					_log.info("casting skill "+skill+" for summon "+player.getPet().getName()+" .");
				ValidateEffect();
				for (L2Effect effect : skill.getEffectsSelf(Summon))
				{
					Summon.addEffect(effect);
				}
				// Hack for newbie summons
				if (skill.getSkillType() == SkillType.SUMMON 
						|| skill.getTargetType() == SkillTargetType.TARGET_SELF
						|| skill.getTargetType() == SkillTargetType.TARGET_PARTY_MEMBER
						|| skill.getTargetType() == SkillTargetType.TARGET_PARTY
						|| skill.getTargetType() == SkillTargetType.TARGET_ALLY
						||(skill.getTargetType() == SkillTargetType.TARGET_CLAN))
				{
					Summon.useMagic(skill, true,false);
				}
				else
				{  
					doCast(skill);
				}

				for (L2Effect effect : skill.getEffects(this, Summon))
				{
					Summon.addEffect(effect);
				}     
				ValidateEffect();
				try {
					Thread.sleep(_castingDelay);//sleep between casts
				}
				catch(InterruptedException ex) {
				}
			}
			else
			{
				SendNotAllowedLvlWindow(player);
				return;
			}
		}
		else
		{
			SendNegateWindow(player);
			return;
		}

	}
	private void ValidateEffect()
	{
		L2Effect Effect=null;
		if (Effect != null) 
			Effect.setInUse(true); 
	}
	/**
	 * 
	 * @param player
	 */
	private void ResendMainWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		int npcID = getNpcId();
		String filename = HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+npcID+".htm";
		html.setFile(filename);
		if(filename!=null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * 
	 * @param player
	 */
	private void SendNotAllowedLvlWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+"level-not-allowed.htm");
		html.setFile(filename);
		if (filename != null)
		{ 
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%minlevel%", String.valueOf(Config.MIN_SUMMON_LEVEL_TO_GET_BUFFS));
			html.replace("%maxlevel%", String.valueOf(Config.MAX_SUMMON_LEVEL_TO_GET_BUFFS));
			html.replace("%npcname%",  String.valueOf(getName()));
			player.sendPacket(html);
		}
	}
	/**
	 * 
	 * @param player
	 */
	private void SendRestoreWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+"restore.htm");
		html.setHtml(filename);
		if (filename != null)
		{   
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
	/**
	 * 
	 * @param player
	 */
	private void SendNegateWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String filename =  HtmCache.getInstance().getHtm(HtmlPathService.SUMMON_BUFFER_NPC_HTML_PATH+"negate.htm");
		html.setHtml(filename); 
		if (filename != null)
		{
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%npcname%", String.valueOf(getName()));
			player.sendPacket(html);
		}    
	}
}