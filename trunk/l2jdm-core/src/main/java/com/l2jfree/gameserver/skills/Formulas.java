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
package com.l2jfree.gameserver.skills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.GameTimeController;
import com.l2jfree.gameserver.SevenSigns;
import com.l2jfree.gameserver.SevenSignsFestival;
import com.l2jfree.gameserver.instancemanager.CastleManager;
import com.l2jfree.gameserver.instancemanager.ClanHallManager;
import com.l2jfree.gameserver.instancemanager.FortManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2SiegeClan;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.L2Npc;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.actor.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jfree.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jfree.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfree.gameserver.model.actor.instance.L2GuardInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.base.PlayerState;
import com.l2jfree.gameserver.model.entity.Castle;
import com.l2jfree.gameserver.model.entity.ClanHall;
import com.l2jfree.gameserver.model.entity.Fort;
import com.l2jfree.gameserver.model.entity.Siege;
import com.l2jfree.gameserver.model.itemcontainer.Inventory;
import com.l2jfree.gameserver.model.restriction.global.GlobalRestrictions;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.skills.conditions.ConditionPlayerState;
import com.l2jfree.gameserver.skills.conditions.ConditionUsingItemType;
import com.l2jfree.gameserver.skills.funcs.Func;
import com.l2jfree.gameserver.templates.chars.L2PcTemplate;
import com.l2jfree.gameserver.templates.item.L2Item;
import com.l2jfree.gameserver.templates.item.L2Weapon;
import com.l2jfree.gameserver.templates.item.L2WeaponType;
import com.l2jfree.gameserver.templates.skills.L2SkillType;
import com.l2jfree.gameserver.util.Util;
import com.l2jfree.gameserver.util.Util.Direction;
import com.l2jfree.lang.L2Math;
import com.l2jfree.tools.random.Rnd;

/**
 * Global calculations, can be modified by server admins
 */
public final class Formulas
{
	/** Regen Task period */
	protected static final Log		_log							= LogFactory.getLog(L2Character.class);
	private static final int		HP_REGENERATE_PERIOD			= 3000;											// 3 secs

	public static final byte		SHIELD_DEFENSE_FAILED			= 0;												// no shield defense
	public static final byte		SHIELD_DEFENSE_SUCCEED			= 1;												// normal shield defense
	public static final byte		SHIELD_DEFENSE_PERFECT_BLOCK	= 2;												// perfect block

	public static final byte		SKILL_REFLECT_FAILED			= 0;												// no reflect
	public static final byte		SKILL_REFLECT_SUCCEED			= 1;												// normal reflect, some damage reflected some other not
	public static final byte		SKILL_REFLECT_VENGEANCE			= 2;												// 100% of the damage affect both

	private static final byte		MELEE_ATTACK_RANGE				= 40;

	public static int				MAX_STAT_VALUE					= 100;

	private static final double[]	STRCompute						= new double[] { 1.036, 34.845 };					//{1.016, 28.515}; for C1
	private static final double[]	INTCompute						= new double[] { 1.020, 31.375 };					//{1.020, 31.375}; for C1
	private static final double[]	DEXCompute						= new double[] { 1.009, 19.360 };					//{1.009, 19.360}; for C1
	private static final double[]	WITCompute						= new double[] { 1.050, 20.000 };					//{1.050, 20.000}; for C1
	private static final double[]	CONCompute						= new double[] { 1.030, 27.632 };					//{1.015, 12.488}; for C1
	private static final double[]	MENCompute						= new double[] { 1.010, -0.060 };					//{1.010, -0.060}; for C1

	protected static final double[]	WITbonus						= new double[MAX_STAT_VALUE];
	protected static final double[]	MENbonus						= new double[MAX_STAT_VALUE];
	protected static final double[]	INTbonus						= new double[MAX_STAT_VALUE];
	protected static final double[]	STRbonus						= new double[MAX_STAT_VALUE];
	protected static final double[]	DEXbonus						= new double[MAX_STAT_VALUE];
	protected static final double[]	CONbonus						= new double[MAX_STAT_VALUE];

	protected static final double[]	sqrtMENbonus					= new double[MAX_STAT_VALUE];
	protected static final double[]	sqrtCONbonus					= new double[MAX_STAT_VALUE];

	// These values are 100% matching retail tables, no need to change and no need add
	// calculation into the stat bonus when accessing (not efficient),
	// better to have everything precalculated and use values directly (saves CPU)
	static
	{
		for (int i = 0; i < STRbonus.length; i++)
			STRbonus[i] = Math.floor(Math.pow(STRCompute[0], i - STRCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < INTbonus.length; i++)
			INTbonus[i] = Math.floor(Math.pow(INTCompute[0], i - INTCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < DEXbonus.length; i++)
			DEXbonus[i] = Math.floor(Math.pow(DEXCompute[0], i - DEXCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < WITbonus.length; i++)
			WITbonus[i] = Math.floor(Math.pow(WITCompute[0], i - WITCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < CONbonus.length; i++)
			CONbonus[i] = Math.floor(Math.pow(CONCompute[0], i - CONCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < MENbonus.length; i++)
			MENbonus[i] = Math.floor(Math.pow(MENCompute[0], i - MENCompute[1]) * 100 + .5d) / 100;

		// precompute  square root values
		for (int i = 0; i < sqrtCONbonus.length; i++)
			sqrtCONbonus[i] = Math.sqrt(CONbonus[i]);
		for (int i = 0; i < sqrtMENbonus.length; i++)
			sqrtMENbonus[i] = Math.sqrt(MENbonus[i]);
	}

	static class FuncAddLevel3 extends Func
	{
		static final FuncAddLevel3[]	_instancies	= new FuncAddLevel3[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();
			if (_instancies[pos] == null)
				_instancies[pos] = new FuncAddLevel3(stat);
			return _instancies[pos];
		}

		private FuncAddLevel3(Stats pStat)
		{
			super(pStat, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value += env.player.getLevel() / 3.0;
		}
	}

	static class FuncMultLevelMod extends Func
	{
		static final FuncMultLevelMod[]	_instancies	= new FuncMultLevelMod[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{

			int pos = stat.ordinal();
			if (_instancies[pos] == null)
				_instancies[pos] = new FuncMultLevelMod(stat);
			return _instancies[pos];
		}

		private FuncMultLevelMod(Stats pStat)
		{
			super(pStat, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncMultRegenResting extends Func
	{
		static final FuncMultRegenResting[]	_instancies	= new FuncMultRegenResting[Stats.NUM_STATS];

		/**
		 * Return the Func object corresponding to the state concerned.<BR>
		 * <BR>
		 */
		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();

			if (_instancies[pos] == null)
				_instancies[pos] = new FuncMultRegenResting(stat);

			return _instancies[pos];
		}

		/**
		 * Constructor of the FuncMultRegenResting.<BR>
		 * <BR>
		 */
		private FuncMultRegenResting(Stats pStat)
		{
			super(pStat, 0x20, null, new ConditionPlayerState(PlayerState.RESTING, true));
		}

		/**
		 * Calculate the modifier of the state concerned.<BR>
		 * <BR>
		 */
		@Override
		public void calc(Env env)
		{
			env.value *= 1.45;
		}
	}

	static class FuncPAtkMod extends Func
	{
		static final FuncPAtkMod	_fpa_instance	= new FuncPAtkMod();

		static Func getInstance()
		{
			return _fpa_instance;
		}

		private FuncPAtkMod()
		{
			super(Stats.POWER_ATTACK, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= STRbonus[env.player.getStat().getSTR()] * env.player.getLevelMod();
		}
	}

	static class FuncMAtkMod extends Func
	{
		static final FuncMAtkMod	_fma_instance	= new FuncMAtkMod();

		static Func getInstance()
		{
			return _fma_instance;
		}

		private FuncMAtkMod()
		{
			super(Stats.MAGIC_ATTACK, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			double intb = INTbonus[env.player.getINT()];
			double lvlb = env.player.getLevelMod();
			env.value *= (lvlb * lvlb) * (intb * intb);
		}
	}

	static class FuncMDefMod extends Func
	{
		static final FuncMDefMod	_fmm_instance	= new FuncMDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncMDefMod()
		{
			super(Stats.MAGIC_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				L2PcInstance p = (L2PcInstance) env.player;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) != null)
					env.value -= 5;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) != null)
					env.value -= 5;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) != null)
					env.value -= 9;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) != null)
					env.value -= 9;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) != null)
					env.value -= 13;
			}
			env.value *= MENbonus[env.player.getStat().getMEN()] * env.player.getLevelMod();
		}
	}

	static class FuncPDefMod extends Func
	{
		static final FuncPDefMod	_fmm_instance	= new FuncPDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncPDefMod()
		{
			super(Stats.POWER_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				L2PcInstance p = (L2PcInstance) env.player;
				boolean hasMagePDef = p.getClassId().isMage();
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null)
					env.value -= 12;
				L2ItemInstance chest = p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
				if (chest != null)
					env.value -= hasMagePDef ? 15 : 31;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null ||
						(chest != null && chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR))
					env.value -= hasMagePDef ? 8 : 18;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
					env.value -= 8;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
					env.value -= 7;
			}
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncGatesPDefMod extends Func
	{
		static final FuncGatesPDefMod	_fmm_instance	= new FuncGatesPDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncGatesPDefMod()
		{
			super(Stats.POWER_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
				env.value *= Config.ALT_SIEGE_DAWN_GATES_PDEF_MULT;
			else if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
				env.value *= Config.ALT_SIEGE_DUSK_GATES_PDEF_MULT;
		}
	}

	static class FuncGatesMDefMod extends Func
	{
		static final FuncGatesMDefMod	_fmm_instance	= new FuncGatesMDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncGatesMDefMod()
		{
			super(Stats.MAGIC_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
				env.value *= Config.ALT_SIEGE_DAWN_GATES_MDEF_MULT;
			else if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
				env.value *= Config.ALT_SIEGE_DUSK_GATES_MDEF_MULT;
		}
	}

	static class FuncBowAtkRange extends Func
	{
		private static final FuncBowAtkRange	_fbarInstance	= new FuncBowAtkRange();

		static Func getInstance()
		{
			return _fbarInstance;
		}

		private FuncBowAtkRange()
		{
			super(Stats.POWER_ATTACK_RANGE, 0x10, null, new ConditionUsingItemType(L2WeaponType.BOW.mask()));
		}

		@Override
		public void calc(Env env)
		{
			// default is 40 and with bow should be 500
			env.value += 460;
		}
	}

	static class FuncCrossBowAtkRange extends Func
	{
		private static final FuncCrossBowAtkRange	_fcb_instance	= new FuncCrossBowAtkRange();

		static Func getInstance()
		{
			return _fcb_instance;
		}

		private FuncCrossBowAtkRange()
		{
			super(Stats.POWER_ATTACK_RANGE, 0x10, null, new ConditionUsingItemType(L2WeaponType.CROSSBOW.mask()));
		}

		@Override
		public void calc(Env env)
		{
			// default is 40 and with crossbow should be 400
			env.value += 360;
		}
	}

	static class FuncAtkAccuracy extends Func
	{
		static final FuncAtkAccuracy	_faaInstance	= new FuncAtkAccuracy();

		static Func getInstance()
		{
			return _faaInstance;
		}

		private FuncAtkAccuracy()
		{
			super(Stats.ACCURACY_COMBAT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl + weapon hitbonus;
			env.value += Math.sqrt(p.getStat().getDEX()) * 6;
			env.value += p.getLevel();
			if (p instanceof L2Summon)
				env.value += (p.getLevel() < 60) ? 4 : 5;
			if (p.getLevel() > 77)
				env.value += (p.getLevel() - 77);
			if (p.getLevel() > 69)
				env.value += (p.getLevel() - 69);
		}
	}

	static class FuncAtkEvasion extends Func
	{
		static final FuncAtkEvasion	_faeInstance	= new FuncAtkEvasion();

		static Func getInstance()
		{
			return _faeInstance;
		}

		private FuncAtkEvasion()
		{
			super(Stats.EVASION_RATE, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl;
			env.value += Math.sqrt(p.getStat().getDEX()) * 6;
			env.value += p.getLevel();
			if (p.getLevel() > 77)
				env.value += (p.getLevel() - 77);
			if (p.getLevel() > 69)
				env.value += (p.getLevel() - 69);
		}
	}

	static class FuncAtkCritical extends Func
	{
		static final FuncAtkCritical	_facInstance	= new FuncAtkCritical();

		static Func getInstance()
		{
			return _facInstance;
		}

		private FuncAtkCritical()
		{
			super(Stats.CRITICAL_RATE, 0x09, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			if (p instanceof L2Summon)
				env.value = 40;
			else if (p instanceof L2PcInstance && p.getActiveWeaponInstance() == null)
				env.value = 40;
			else
			{
				env.value *= DEXbonus[p.getStat().getDEX()];
				env.value *= 10;
			}
		}
	}

	static class FuncMAtkCritical extends Func
	{
		static final FuncMAtkCritical	_fac_instance	= new FuncMAtkCritical();

		static Func getInstance()
		{
			return _fac_instance;
		}

		private FuncMAtkCritical()
		{
			super(Stats.MCRITICAL_RATE, 0x29 /*guess, but must be before 0x30*/, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2Summon)
				env.value = 8; // TODO: needs retail value
			else if (env.player instanceof L2PcInstance && env.player.getActiveWeaponInstance() != null)
				env.value *= WITbonus[env.player.getStat().getWIT()];
		}
	}

	static class FuncMoveSpeed extends Func
	{
		static final FuncMoveSpeed	_fmsInstance	= new FuncMoveSpeed();

		static Func getInstance()
		{
			return _fmsInstance;
		}

		private FuncMoveSpeed()
		{
			super(Stats.RUN_SPEED, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= DEXbonus[p.getStat().getDEX()];
		}
	}

	static class FuncPAtkSpeed extends Func
	{
		static final FuncPAtkSpeed	_fasInstance	= new FuncPAtkSpeed();

		static Func getInstance()
		{
			return _fasInstance;
		}

		private FuncPAtkSpeed()
		{
			super(Stats.POWER_ATTACK_SPEED, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= DEXbonus[p.getStat().getDEX()];
		}
	}

	static class FuncMAtkSpeed extends Func
	{
		static final FuncMAtkSpeed	_fasInstance	= new FuncMAtkSpeed();

		static Func getInstance()
		{
			return _fasInstance;
		}

		private FuncMAtkSpeed()
		{
			super(Stats.MAGIC_ATTACK_SPEED, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= WITbonus[p.getStat().getWIT()];
		}
	}

	static class FuncMaxLoad extends Func
	{
		static final FuncMaxLoad	_fmsInstance	= new FuncMaxLoad();

		static Func getInstance()
		{
			return _fmsInstance;
		}

		private FuncMaxLoad()
		{
			super(Stats.MAX_LOAD, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncHennaSTR extends Func
	{
		static final FuncHennaSTR	_fhInstance	= new FuncHennaSTR();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaSTR()
		{
			super(Stats.STAT_STR, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatSTR();
		}
	}

	static class FuncHennaDEX extends Func
	{
		static final FuncHennaDEX	_fhInstance	= new FuncHennaDEX();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaDEX()
		{
			super(Stats.STAT_DEX, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatDEX();
		}
	}

	static class FuncHennaINT extends Func
	{
		static final FuncHennaINT	_fhInstance	= new FuncHennaINT();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaINT()
		{
			super(Stats.STAT_INT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatINT();
		}
	}

	static class FuncHennaMEN extends Func
	{
		static final FuncHennaMEN	_fhInstance	= new FuncHennaMEN();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaMEN()
		{
			super(Stats.STAT_MEN, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatMEN();
		}
	}

	static class FuncHennaCON extends Func
	{
		static final FuncHennaCON	_fhInstance	= new FuncHennaCON();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaCON()
		{
			super(Stats.STAT_CON, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatCON();
		}
	}

	static class FuncHennaWIT extends Func
	{
		static final FuncHennaWIT	_fhInstance	= new FuncHennaWIT();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaWIT()
		{
			super(Stats.STAT_WIT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatWIT();
		}
	}

	static class FuncMaxHpAdd extends Func
	{
		static final FuncMaxHpAdd	_fmhaInstance	= new FuncMaxHpAdd();

		static Func getInstance()
		{
			return _fmhaInstance;
		}

		private FuncMaxHpAdd()
		{
			super(Stats.MAX_HP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double hpmod = t.getLvlHpMod() * lvl;
			double hpmax = (t.getLvlHpAdd() + hpmod) * lvl;
			double hpmin = (t.getLvlHpAdd() * lvl) + hpmod;
			env.value += (hpmax + hpmin) / 2;
		}
	}

	static class FuncMaxHpMul extends Func
	{
		static final FuncMaxHpMul	_fmhmInstance	= new FuncMaxHpMul();

		static Func getInstance()
		{
			return _fmhmInstance;
		}

		private FuncMaxHpMul()
		{
			super(Stats.MAX_HP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncMaxCpAdd extends Func
	{
		static final FuncMaxCpAdd	_fmcaInstance	= new FuncMaxCpAdd();

		static Func getInstance()
		{
			return _fmcaInstance;
		}

		private FuncMaxCpAdd()
		{
			super(Stats.MAX_CP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double cpmod = t.getLvlCpMod() * lvl;
			double cpmax = (t.getLvlCpAdd() + cpmod) * lvl;
			double cpmin = (t.getLvlCpAdd() * lvl) + cpmod;
			env.value += (cpmax + cpmin) / 2;
		}
	}

	static class FuncMaxCpMul extends Func
	{
		static final FuncMaxCpMul	_fmcmInstance	= new FuncMaxCpMul();

		static Func getInstance()
		{
			return _fmcmInstance;
		}

		private FuncMaxCpMul()
		{
			super(Stats.MAX_CP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncMaxMpAdd extends Func
	{
		static final FuncMaxMpAdd	_fmmaInstance	= new FuncMaxMpAdd();

		static Func getInstance()
		{
			return _fmmaInstance;
		}

		private FuncMaxMpAdd()
		{
			super(Stats.MAX_MP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double mpmod = t.getLvlMpMod() * lvl;
			double mpmax = (t.getLvlMpAdd() + mpmod) * lvl;
			double mpmin = (t.getLvlMpAdd() * lvl) + mpmod;
			env.value += (mpmax + mpmin) / 2;
		}
	}

	static class FuncMaxMpMul extends Func
	{
		static final FuncMaxMpMul	_fmmmInstance	= new FuncMaxMpMul();

		static Func getInstance()
		{
			return _fmmmInstance;
		}

		private FuncMaxMpMul()
		{
			super(Stats.MAX_MP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= MENbonus[p.getStat().getMEN()];
		}
	}

	/**
	 * Return the period between 2 regenerations task (3s for L2Character, 5 min
	 * for L2DoorInstance).<BR>
	 * <BR>
	 */
	public static int getRegeneratePeriod(L2Character cha)
	{
		if (cha instanceof L2DoorInstance)
			return HP_REGENERATE_PERIOD * 100; // 5 mins

		return HP_REGENERATE_PERIOD; // 3s
	}

	/**
	 * Return the standard NPC Calculator set containing ACCURACY_COMBAT and
	 * EVASION_RATE.<BR>
	 * <BR>
	 * 
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * A calculator is created to manage and dynamically calculate the effect of
	 * a character property (ex : MAX_HP, REGENERATE_HP_RATE...). In fact, each
	 * calculator is a table of Func object in which each Func represents a
	 * mathematic function : <BR>
	 * <BR>
	 * 
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
	 * <BR>
	 * 
	 * To reduce cache memory use, L2Npcs who don't have skills share the same
	 * Calculator set called <B>NPC_STD_CALCULATOR</B>.<BR>
	 * <BR>
	 * 
	 */
	public static Calculator[] getStdNPCCalculators()
	{
		Calculator[] std = new Calculator[Stats.NUM_STATS];

		// Add the FuncAtkAccuracy to the Standard Calculator of ACCURACY_COMBAT
		std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
		std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());

		// Add the FuncAtkEvasion to the Standard Calculator of EVASION_RATE
		std[Stats.EVASION_RATE.ordinal()] = new Calculator();
		std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());

		return std;
	}

	public static Calculator[] getStdDoorCalculators()
	{
		Calculator[] std = new Calculator[Stats.NUM_STATS];

		// Add the FuncAtkAccuracy to the Standard Calculator of ACCURACY_COMBAT
		std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
		std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());

		// Add the FuncAtkEvasion to the Standard Calculator of EVASION_RATE
		std[Stats.EVASION_RATE.ordinal()] = new Calculator();
		std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());

		//SevenSigns PDEF Modifier
		std[Stats.POWER_DEFENCE.ordinal()] = new Calculator();
		std[Stats.POWER_DEFENCE.ordinal()].addFunc(FuncGatesPDefMod.getInstance());

		//SevenSigns MDEF Modifier
		std[Stats.MAGIC_DEFENCE.ordinal()] = new Calculator();
		std[Stats.MAGIC_DEFENCE.ordinal()].addFunc(FuncGatesMDefMod.getInstance());

		return std;
	}

	/**
	 * Add basics Func objects to L2PcInstance and L2Summon.<BR>
	 * <BR>
	 * 
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * A calculator is created to manage and dynamically calculate the effect of
	 * a character property (ex : MAX_HP, REGENERATE_HP_RATE...). In fact, each
	 * calculator is a table of Func object in which each Func represents a
	 * mathematic function : <BR>
	 * <BR>
	 * 
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
	 * <BR>
	 * 
	 * @param cha L2PcInstance or L2Summon that must obtain basic Func objects
	 */
	public static void addFuncsToNewCharacter(L2Character cha)
	{
		if (cha instanceof L2PcInstance)
		{
			cha.addStatFunc(FuncMaxHpAdd.getInstance());
			cha.addStatFunc(FuncMaxHpMul.getInstance());
			cha.addStatFunc(FuncMaxCpAdd.getInstance());
			cha.addStatFunc(FuncMaxCpMul.getInstance());
			cha.addStatFunc(FuncMaxMpAdd.getInstance());
			cha.addStatFunc(FuncMaxMpMul.getInstance());
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_CP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncBowAtkRange.getInstance());
			cha.addStatFunc(FuncCrossBowAtkRange.getInstance());
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_ATTACK));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_DEFENCE));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.MAGIC_DEFENCE));
			if (Config.LEVEL_ADD_LOAD)
				cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.MAX_LOAD));
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
			cha.addStatFunc(FuncPAtkSpeed.getInstance());
			cha.addStatFunc(FuncMAtkSpeed.getInstance());
			cha.addStatFunc(FuncMoveSpeed.getInstance());
			cha.addStatFunc(FuncMaxLoad.getInstance());

			cha.addStatFunc(FuncHennaSTR.getInstance());
			cha.addStatFunc(FuncHennaDEX.getInstance());
			cha.addStatFunc(FuncHennaINT.getInstance());
			cha.addStatFunc(FuncHennaMEN.getInstance());
			cha.addStatFunc(FuncHennaCON.getInstance());
			cha.addStatFunc(FuncHennaWIT.getInstance());
		}
		else if (cha instanceof L2PetInstance)
		{
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
		}
		else if (cha instanceof L2Summon)
		{
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
		}

	}

	/**
	 * Calculate the HP regen rate (base + modifiers).<BR>
	 * <BR>
	 */
	public static final double calcHpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double hpRegenMultiplier;
		double hpRegenBonus = 0;

		if (cha.isRaid())
			hpRegenMultiplier = Config.RAID_HP_REGEN_MULTIPLIER;
		else if (cha instanceof L2PcInstance)
			hpRegenMultiplier = Config.PLAYER_HP_REGEN_MULTIPLIER;
		else
			hpRegenMultiplier = Config.NPC_HP_REGEN_MULTIPLIER;

		if (cha.isChampion())
			hpRegenMultiplier *= Config.CHAMPION_HP_REGEN;

		// The recovery power of Zaken decreases under sunlight.
		// The recovery power of Zaken increases during night.
		if (cha instanceof L2GrandBossInstance)
		{
			L2GrandBossInstance boss = (L2GrandBossInstance) cha;
			if (boss.getNpcId() == 29022)
			{
				if (boss.isInsideZone(L2Zone.FLAG_SUNLIGHTROOM))
					hpRegenMultiplier *= 0.75;
				else if (GameTimeController.getInstance().isNowNight())
					hpRegenMultiplier *= 1.75;
			}
		}

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseHpReg value for certain level of PC
			if (player.getLevel() >= 71)
				init = 8.5;
			else if (player.getLevel() >= 61)
				init = 7.5;
			else if (player.getLevel() >= 51)
				init = 6.5;
			else if (player.getLevel() >= 41)
				init = 5.5;
			else if (player.getLevel() >= 31)
				init = 4.5;
			else if (player.getLevel() >= 21)
				init = 3.5;
			else if (player.getLevel() >= 11)
				init = 2.5;
			else
				init = 2.0;

			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				init *= calcFestivalRegenModifier(player);
			else
			{
				double siegeModifier = calcSiegeRegenModifer(player);
				if (siegeModifier > 0)
					init *= siegeModifier;
			}

			if (player.isInsideZone(L2Zone.FLAG_CLANHALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHasHideout();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + (double) clansHall.getFunction(ClanHall.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Zone.FLAG_MOTHERTREE))
				hpRegenBonus += 2;

			if (player.isInsideZone(L2Zone.FLAG_CASTLE) && player.getClan() != null)
			{
				int castleIndex = player.getClan().getHasCastle();
				if (castleIndex > 0)
				{
					Castle castle = CastleManager.getInstance().getCastleById(castleIndex);
					if (castle != null)
						if (castle.getFunction(Castle.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + (double) castle.getFunction(Castle.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_FORT) && player.getClan() != null)
			{
				int fortIndex = player.getClan().getHasFort();
				if (fortIndex > 0)
				{
					Fort fort = FortManager.getInstance().getFortById(fortIndex);
					if (fort != null)
						if (fort.getFunction(Fort.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + (double) fort.getFunction(Fort.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			// Calculate Movement bonus
			if (player.isSitting() && player.getLevel() < 41) // Sitting below lvl 40
			{
				init *= 1.5;
				hpRegenBonus += (40 - player.getLevel()) * 0.7;
			}
			else if (player.isSitting())
				init *= 2.5; // Sitting
			else if (player.isRunning())
				init *= 0.7; // Running
			else if (player.isMoving())
				init *= 1.1; // Walking
			else
				init *= 1.5; // Staying

			// Add CON bonus
			init *= cha.getLevelMod() * CONbonus[cha.getStat().getCON()];
		}
		else if (cha instanceof L2PetInstance)
			init = ((L2PetInstance) cha).getPetData().getPetRegenHP();

		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null) * hpRegenMultiplier + hpRegenBonus;
	}

	/**
	 * Calculate the MP regen rate (base + modifiers).<BR>
	 * <BR>
	 */
	public static final double calcMpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseMpReg();
		double mpRegenMultiplier;
		double mpRegenBonus = 0;

		if (cha.isRaid())
			mpRegenMultiplier = Config.RAID_MP_REGEN_MULTIPLIER;
		else if (cha instanceof L2PcInstance)
			mpRegenMultiplier = Config.PLAYER_MP_REGEN_MULTIPLIER;
		else
			mpRegenMultiplier = Config.NPC_MP_REGEN_MULTIPLIER;

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseMpReg value for certain level of PC
			if (player.getLevel() >= 71)
				init = 3.0;
			else if (player.getLevel() >= 61)
				init = 2.7;
			else if (player.getLevel() >= 51)
				init = 2.4;
			else if (player.getLevel() >= 41)
				init = 2.1;
			else if (player.getLevel() >= 31)
				init = 1.8;
			else if (player.getLevel() >= 21)
				init = 1.5;
			else if (player.getLevel() >= 11)
				init = 1.2;
			else
				init = 0.9;

			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				init *= calcFestivalRegenModifier(player);

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Zone.FLAG_MOTHERTREE))
				mpRegenBonus += 2;

			if (player.isInsideZone(L2Zone.FLAG_CASTLE) && player.getClan() != null)
			{
				int castleIndex = player.getClan().getHasCastle();
				if (castleIndex > 0)
				{
					Castle castle = CastleManager.getInstance().getCastleById(castleIndex);
					if (castle != null)
						if (castle.getFunction(Castle.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1 + (double) castle.getFunction(Castle.FUNC_RESTORE_MP).getLvl() / 100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_FORT) && player.getClan() != null)
			{
				int fortIndex = player.getClan().getHasFort();
				if (fortIndex > 0)
				{
					Fort fort = FortManager.getInstance().getFortById(fortIndex);
					if (fort != null)
						if (fort.getFunction(Fort.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1 + (double) fort.getFunction(Fort.FUNC_RESTORE_MP).getLvl() / 100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_CLANHALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHasHideout();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1 + (double) clansHall.getFunction(ClanHall.FUNC_RESTORE_MP).getLvl() / 100;
				}
			}

			// Calculate Movement bonus
			if (player.isSitting())
				init *= 2.5; // Sitting.
			else if (player.isRunning())
				init *= 0.7; // Running
			else if (player.isMoving())
				init *= 1.1; // Walking
			else
				init *= 1.5; // Staying

			// Add MEN bonus
			init *= cha.getLevelMod() * MENbonus[cha.getStat().getMEN()];
		}
		else if (cha instanceof L2PetInstance)
			init = ((L2PetInstance) cha).getPetData().getPetRegenMP();

		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null) * mpRegenMultiplier + mpRegenBonus;
	}

	/**
	 * Calculate the CP regen rate (base + modifiers).<BR>
	 * <BR>
	 */
	public static final double calcCpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double cpRegenMultiplier = Config.PLAYER_CP_REGEN_MULTIPLIER;
		double cpRegenBonus = 0;

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseHpReg value for certain level of PC
			init += (player.getLevel() > 10) ? ((player.getLevel() - 1) / 10.0) : 0.5;

			// Calculate Movement bonus
			if (player.isSitting())
				init *= 1.5; // Sitting
			else if (!player.isMoving())
				init *= 1.1; // Staying
			else if (player.isRunning())
				init *= 0.7; // Running
		}
		else
		{
			// Calculate Movement bonus
			if (!cha.isMoving())
				init *= 1.1; // Staying
			else if (cha.isRunning())
				init *= 0.7; // Running
		}

		// Apply CON bonus
		init *= cha.getLevelMod() * CONbonus[cha.getStat().getCON()];
		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null) * cpRegenMultiplier + cpRegenBonus;
	}

	@SuppressWarnings("deprecation")
	public static final double calcFestivalRegenModifier(L2PcInstance activeChar)
	{
		final int[] festivalInfo = SevenSignsFestival.getInstance().getFestivalForPlayer(activeChar);
		final int oracle = festivalInfo[0];
		final int festivalId = festivalInfo[1];
		int[] festivalCenter;

		// If the player isn't found in the festival, leave the regen rate as it is.
		if (festivalId < 0)
			return 0;

		// Retrieve the X and Y coords for the center of the festival arena the player is in.
		if (oracle == SevenSigns.CABAL_DAWN)
			festivalCenter = SevenSignsFestival.FESTIVAL_DAWN_PLAYER_SPAWNS[festivalId];
		else
			festivalCenter = SevenSignsFestival.FESTIVAL_DUSK_PLAYER_SPAWNS[festivalId];

		// Check the distance between the player and the player spawn point, in the center of the arena.
		double distToCenter = activeChar.getDistance(festivalCenter[0], festivalCenter[1]);

		if (_log.isDebugEnabled())
			_log.info("Distance: " + distToCenter + ", RegenMulti: " + (distToCenter * 2.5) / 50);

		return 1.0 - (distToCenter * 0.0005); // Maximum Decreased Regen of ~ -65%;
	}

	public static final double calcSiegeRegenModifer(L2PcInstance activeChar)
	{
		if (activeChar == null || activeChar.getClan() == null)
			return 0;

		Siege siege = SiegeManager.getInstance().getSiege(activeChar);
		if (siege == null || !siege.getIsInProgress())
			return 0;

		L2SiegeClan siegeClan = siege.getAttackerClan(activeChar.getClan().getClanId());
		if (siegeClan == null || siegeClan.getFlag().isEmpty() || !Util.checkIfInRange(200, activeChar, siegeClan.getFlag().getFirst(), true))
			return 0;

		return 1.5; // If all is true, then modifer will be 50% more
	}
	
	/**
	 * Calculated damage caused by ATTACK of attacker on target, called
	 * separatly for each weapon, if dual-weapon is used.
	 * 
	 * @param attacker player or NPC that makes ATTACK
	 * @param target player or NPC, target of ATTACK
	 * @param skill
	 * @param shld one of ATTACK_XXX constants
	 * @param crit if the ATTACK have critical success
	 * @param dual if dual weapon is used
	 * @param ss if weapon item was charged by soulshot
	 * @return damage points
	 */
	public static final double calcPhysDam(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean crit, boolean ss)
	{
		boolean transformed = false;
		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance) attacker;
			if (pcInst.isGM() && pcInst.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
				return 0;
			transformed = pcInst.isTransformed();
		}
		
		double damage = attacker.getPAtk(target);
		damage += calcValakasAttribute(attacker, target, skill);
		
		if (skill != null)
		{
			double skillPower = skill.getPower(attacker);
			if (attacker instanceof L2Playable && target instanceof L2Playable)
				skillPower *= skill.getPvpPowerMultiplier();
			float ssBoost = skill.getSSBoost();
			
			if (ss)
			{
				if (ssBoost > 0)
					skillPower *= ssBoost;
				else
					skillPower *= 2;
			}
			
			damage += skillPower;
		}
		else if (ss)
			damage *= 2;
		
		double defence = target.getPDef(attacker);
		
		switch (shld)
		{
			case SHIELD_DEFENSE_SUCCEED:
				if (!Config.ALT_GAME_SHIELD_BLOCKS)
					defence += target.getShldDef();
				break;
			case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1.;
		}
		
		if (crit)
		{
			//Finally retail like formula
			damage *= 2 * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, target.getTemplate().getBaseCritVuln(), target, skill);
			//Crit dmg add is almost useless in normal hits...
			if (skill != null && skill.getSkillType() == L2SkillType.BLOW)
				damage += attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill) * 6.5;
			else
				damage += attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill);
		}
		
		damage *= 70. / defence;
		
		// In C5 summons make 10 % less dmg in PvP.
		if (attacker instanceof L2Summon && target instanceof L2PcInstance)
			damage *= 0.9;
		
		// defence modifier depending of the attacker weapon
		L2Weapon weapon = attacker.getActiveWeaponItem();
		
		double randomDamageMulti = 0.1;
		
		if (weapon != null)
		{
			randomDamageMulti = weapon.getRandomDamage() / 100.0;
			
			// defence modifier depending of the attacker weapon
			Stats stat = !transformed ? weapon.getItemType().getStat() : null;
			if (stat != null)
			{
				// get the vulnerability due to skills (buffs, passives, toggles, etc)
				damage = target.calcStat(stat, damage, target, null);
				
				if (target instanceof L2Npc)
				{
					// get the natural vulnerability for the template
					damage *= ((L2Npc)target).getTemplate().getVulnerability(stat);
				}
			}
		}
		
		// +/- 5..20%
		damage *= 1 + (2 * Rnd.nextDouble() - 1) * randomDamageMulti;
		
		if (shld > 0 && Config.ALT_GAME_SHIELD_BLOCKS)
		{
			damage -= target.getShldDef();
			if (damage < 0)
				damage = 0;
		}
		
		if (target instanceof L2Npc)
		{
			Stats stat = ((L2Npc)target).getTemplate().getRace().getOffensiveStat();
			
			if (stat != null)
				damage *= attacker.getStat().getMul(stat, target);
		}
		
		if (attacker instanceof L2Npc)
		{
			Stats stat = ((L2Npc)attacker).getTemplate().getRace().getDefensiveStat();
			
			if (stat != null)
				damage /= target.getStat().getMul(stat, attacker);
		}
		
		if (damage > 0 && damage < 1)
		{
			damage = 1;
		}
		else if (damage < 0)
		{
			damage = 0;
		}
		
		if (attacker instanceof L2PcInstance)
		{
			if (((L2PcInstance) attacker).getClassId().isMage())
				damage *= Config.ALT_MAGES_PHYSICAL_DAMAGE_MULTI;
			else
				damage *= Config.ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
		}
		else if (attacker instanceof L2Summon)
			damage *= Config.ALT_PETS_PHYSICAL_DAMAGE_MULTI;
		else if (attacker instanceof L2Npc)
			damage *= Config.ALT_NPC_PHYSICAL_DAMAGE_MULTI;

		return GlobalRestrictions.calcDamage(attacker, target, damage, skill);
	}

	public static final double calcMagicDam(L2CubicInstance attacker, L2Character target, L2Skill skill, boolean mcrit, byte shld)
	{
		double mAtk = attacker.getMAtk();
		double mDef = target.getMDef(attacker.getOwner(), skill);

		switch (shld)
		{
			case SHIELD_DEFENSE_SUCCEED:
				mDef += target.getShldDef(); // kamael
				break;
			case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1;
		}

		double damage = 91 * Math.sqrt(mAtk) / mDef * skill.getPower();
		L2PcInstance owner = attacker.getOwner();
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(owner, target, skill))
		{
			if (calcMagicSuccess(owner, target, skill) && getMagicLevelDifference(attacker.getOwner(), target, skill) >= -9)
			{
				owner.sendResistedMyMagicSlightlyMessage(target);
				damage /= 2;
			}
			else
			{
				owner.sendResistedMyMagicMessage(target);
				if (mcrit)
					damage = 1;
				else
					damage = Rnd.nextBoolean() ? 1 : 0;
			}
		}

		if (mcrit)
		{
			if (target instanceof L2Playable)
				damage *= Config.ALT_MCRIT_PVP_RATE;
			else
				damage *= Config.ALT_MCRIT_RATE;
		}

		// CT2.3 general magic vuln
		damage *= target.calcStat(Stats.MAGIC_DAMAGE_VULN, 1, null, null);
		damage *= Config.ALT_PETS_MAGICAL_DAMAGE_MULTI;

		return GlobalRestrictions.calcDamage(owner, target, damage, skill);
	}

	public static final double calcMagicDam(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss, boolean bss, boolean mcrit)
	{
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);

		switch (shld)
		{
			case SHIELD_DEFENSE_SUCCEED:
				mDef += target.getShldDef(); // kamael
				break;
			case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1;
		}

		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;

		double damage = 91 * Math.sqrt(mAtk) / mDef * skill.getPower(attacker);

		// In C5 summons make 10 % less dmg in PvP.
		if (attacker instanceof L2Summon && target instanceof L2PcInstance)
			damage *= 0.9;

		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill))
		{
			if (attacker instanceof L2Playable)
			{
				L2PcInstance attOwner = attacker.getActingPlayer();
				if (calcMagicSuccess(attacker, target, skill) && getMagicLevelDifference(attacker, target, skill) >= -9)
				{
					// ~1/10 - weak resist
					attOwner.sendResistedMyMagicSlightlyMessage(target);
					damage /= 2;
				}
				else // retail message & dmg, verified
				{
					attOwner.sendResistedMyMagicMessage(target);
					if (mcrit)
						damage = 1;
					else
						damage = Rnd.nextBoolean() ? 1 : 0;
				}
			}
		}

		// Critical can happen even when failing
		if (mcrit)
		{
			if (attacker instanceof L2Playable && target instanceof L2Playable)
				damage *= Config.ALT_MCRIT_PVP_RATE;
			else
				damage *= Config.ALT_MCRIT_RATE;
		}

		//random magic damage
		damage *= 1 + (2 * Rnd.nextDouble() - 1) * 0.2;

		// CT2.3 general magic vuln
		damage *= target.calcStat(Stats.MAGIC_DAMAGE_VULN, 1, null, null);

		if (attacker instanceof L2PcInstance)
		{
			if (((L2PcInstance) attacker).getClassId().isMage())
				damage *= Config.ALT_MAGES_MAGICAL_DAMAGE_MULTI;
			else
				damage *= Config.ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI;
		}
		else if (attacker instanceof L2Summon)
			damage *= Config.ALT_PETS_MAGICAL_DAMAGE_MULTI;
		else if (attacker instanceof L2Npc)
			damage *= Config.ALT_NPC_MAGICAL_DAMAGE_MULTI;

		return GlobalRestrictions.calcDamage(attacker, target, damage, skill);
	}
	
	public static final double calcSoulBonus(L2Character activeChar, L2Skill skill)
	{
		if (skill != null && skill.getMaxSoulConsumeCount() > 0 && activeChar instanceof L2PcInstance)
		{
			switch (((L2PcInstance) activeChar).getLastSoulConsume())
			{
				case 0:
					return 1.00;
				case 1:
					return 1.10;
				case 2:
					return 1.12;
				case 3:
					return 1.15;
				case 4:
					return 1.18;
				default:
					return 1.20;
			}
		}
		
		return 1.0;
	}

	/** Returns true in case of critical hit */
	public static boolean calcSkillCrit(L2Character attacker, L2Character target, L2Skill skill)
	{
		final double rate = skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(attacker);

		return calcCrit(attacker, target, rate);
	}

	public static boolean calcCriticalHit(L2Character attacker, L2Character target)
	{
		final double rate = attacker.getStat().getCriticalHit(target);

		if (!calcCrit(attacker, target, rate))
			return false;

		// support for critical damage evasion
		return Rnd.calcChance(200 - target.getStat().calcStat(Stats.CRIT_DAMAGE_EVASION, 100, attacker, null), 100);

		// l2jserver's version:
		//With default value 1.0 for CRIT_DAMAGE_EVASION critical hits will never be evaded at all.
		//After got buff with CRIT_DAMAGE_EVASION increase (1.3 for exabple) Rnd.get(130) will generate 30% chance to evade crit hit.

		// little weird, but remember what CRIT_DAMAGE_EVASION > 1 increase chances to _evade_ crit hits
		// return Rnd.get((int)target.getStat().calcStat(Stats.CRIT_DAMAGE_EVASION, 100, null, null)) < 100;
	}

	private static boolean calcCrit(L2Character attacker, L2Character target, double rate)
	{
		switch (Direction.getDirection(attacker, target))
		{
			case SIDE:
				rate *= 1.2;
				break;
			case BACK:
				rate *= 1.35;
				break;
		}

		rate *= 1 + getHeightModifier(attacker, target, 0.15);

		return Rnd.calcChance(rate, 1000);
	}

	private static double getHeightModifier(L2Character attacker, L2Character target, double base)
	{
		return base * L2Math.limit(-1.0, ((double) attacker.getZ() - target.getZ()) / 50., 1.0);
	}

	/** Calculate value of blow success */
	public static final boolean calcBlow(L2Character attacker, L2Character target, L2Skill skill)
	{
		if ((skill.getCondition() & L2Skill.COND_BEHIND) != 0 && !attacker.isBehind(target))
			return false;

		double chance = attacker.calcStat(Stats.BLOW_RATE, 40 + 0.5 * attacker.getStat().getDEX(), target, skill);

		switch (Direction.getDirection(attacker, target))
		{
			case SIDE:
				chance += 5;
				break;
			case BACK:
				chance += 15;
				break;
		}

		chance += getHeightModifier(attacker, target, 5);

		chance += 2 * getMagicLevelDifference(attacker, target, skill);

		return Rnd.calcChance(chance, 100);
	}

	/**
	 * 
	 * @param attacker
	 * @param target
	 * @param skill
	 * @return magic level influenced, balanced (attacker level - target level)
	 */
	public static int getMagicLevelDifference(L2Character attacker, L2Character target, L2Skill skill)
	{
		int attackerLvlmod = attacker.getLevel();
		int targetLvlmod = target.getLevel();
		
		// this was definitely overdrawn too
		//if (attackerLvlmod > 75)
		//	attackerLvlmod = 75 + (attackerLvlmod - 75) / 2;
		//if (targetLvlmod > 75)
		//	targetLvlmod = 75 + (targetLvlmod - 75) / 2;
		
		if (skill.getMagicLevel() > 0)
			return (skill.getMagicLevel() + attackerLvlmod) / 2 - targetLvlmod;
		else
			return attackerLvlmod - targetLvlmod;
	}

	/** Calculate value of lethal chance */
	private static final double calcLethal(L2Character activeChar, L2Character target, int baseLethal, L2Skill skill)
	{
		if (baseLethal <= 0)
			return 0;
		
		final double chance;
		final int delta = getMagicLevelDifference(activeChar, target, skill);
		
		
		// delta [-3,infinite)
		if (delta >= -3)
		{
			chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
		}
		// delta [-9, -3[
		else if (delta < -3 && delta >= -9)
		{
			//               baseLethal
			// chance = -1 * -----------
			//               (delta / 3)
			chance = (-3) * (baseLethal / (delta));
		}
		//delta [-infinite,-9[
		else
		{
			chance = baseLethal / 15;
		}
		
		return 10 * activeChar.calcStat(Stats.LETHAL_RATE, chance, target, null);
	}

	public static final boolean calcLethalHit(L2Character activeChar, L2Character target, L2Skill skill)
	{
		if (target.isRaid() || target instanceof L2DoorInstance)
			return false;
		
		if (target instanceof L2Npc && ((L2Npc)target).getNpcId() == 35062)
			return false;
		
		final int chance = Rnd.get(1000);
		
		// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1)
		if (chance < calcLethal(activeChar, target, skill.getLethalChance2(), skill))
		{
			if (target instanceof L2PcInstance) // If is a active player set his HP and CP to 1
			{
				target.getStatus().reduceHp(target.getCurrentCp() + target.getCurrentHp() - 1, activeChar);
				target.getStatus().setCurrentHp(1); // just to be sure (transfer damage, etc)
				target.getStatus().setCurrentCp(1); // just to be sure (transfer damage, etc)
				
				target.sendPacket(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL);
			}
			else if (target instanceof L2Npc) // If is a npc set his HP to 1
				target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar, skill);
			
			activeChar.sendPacket(SystemMessageId.LETHAL_STRIKE);
			return true;
		}
		else if (chance < calcLethal(activeChar, target, skill.getLethalChance1(), skill))
		{
			if (target instanceof L2PcInstance) // Set CP to 1
			{
				target.getStatus().reduceHp(target.getCurrentCp() - 1, activeChar);
				target.getStatus().setCurrentCp(1); // just to be sure (transfer damage, etc)
				
				target.sendPacket(SystemMessageId.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
			}
			else if (target instanceof L2Npc) // If is a monster remove first damage and after 50% of current hp
				target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar, skill);
			
			activeChar.sendPacket(SystemMessageId.HALF_KILL);
			return true;
		}
		
		return false;
	}

	public static final boolean calcMCrit(double mRate)
	{
		return mRate > Rnd.get(1000);
	}

	/** Returns true in case when ATTACK is canceled due to hit */
	public static final boolean calcAtkBreak(L2Character target, double dmg)
	{
		if (target.getFusionSkill() != null)
			return true;

		if (target.isRaid() || target.isInvul())
			return false; // No attack break

		double init;

		if (Config.ALT_GAME_CANCEL_CAST && target.isCastingNow() && target.canAbortCast())
			init = 15;
		else if (Config.ALT_GAME_CANCEL_BOW && target.isAttackingNow() &&
				target.getActiveWeaponItem() != null &&
				target.getActiveWeaponItem().getItemType() == L2WeaponType.BOW)
		{
			init = 15;
		}
		else
			return false;

		// Chance of break is higher with higher dmg
		init += Math.sqrt(13 * dmg);

		// Chance is affected by target MEN
		init -= (MENbonus[target.getStat().getMEN()] * 100 - 100);

		// Calculate all modifiers for ATTACK_CANCEL
		double rate = target.calcStat(Stats.ATTACK_CANCEL, init, null, null);

		// Adjust the rate to be between 1 and 99
		rate = L2Math.limit(1, rate, 99);

		return Rnd.get(100) < rate;
	}

	/** Calculate delay (in milliseconds) before next ATTACK */
	public static final int calcPAtkSpd(L2Character attacker, L2Character target, double atkSpd, double base)
	{
		if (attacker instanceof L2PcInstance)
			base *= Config.ALT_ATTACK_DELAY;

		if (atkSpd < 10)
			atkSpd = 10;

		return (int) (base / atkSpd);
	}

	public static double calcCastingRelatedTimeMulti(L2Character attacker, L2Skill skill)
	{
		if (skill.isMagic())
			return 333.3 / attacker.getMAtkSpd();
		else
			return 333.3 / attacker.getPAtkSpd();
	}

	/**
	 * Returns true if hit missed (target evaded) Formula based on
	 * http://l2p.l2wh.com/nonskillattacks.html
	 */
	public static boolean calcHitMiss(L2Character attacker, L2Character target)
	{
		if (attacker instanceof L2GuardInstance)
			return false;

		double chance = getBaseHitChance(attacker, target);

		switch (Direction.getDirection(attacker, target))
		{
			case SIDE:
				chance *= 1.1;
				break;
			case BACK:
				chance *= 1.2;
				break;
		}

		chance *= 1 + getHeightModifier(attacker, target, 0.05);

		return !Rnd.calcChance(chance, 1000);
	}

	private static int getBaseHitChance(L2Character attacker, L2Character target)
	{
		final int diff = attacker.getStat().getAccuracy() - target.getStat().getEvasionRate(attacker);

		if (diff >= 10)
			return 980;

		switch (diff)
		{
			case 9:
				return 975;
			case 8:
				return 970;
			case 7:
				return 965;
			case 6:
				return 960;
			case 5:
				return 955;
			case 4:
				return 945;
			case 3:
				return 935;
			case 2:
				return 925;
			case 1:
				return 915;
			case 0:
				return 905;
			case -1:
				return 890;
			case -2:
				return 875;
			case -3:
				return 860;
			case -4:
				return 845;
			case -5:
				return 830;
			case -6:
				return 815;
			case -7:
				return 800;
			case -8:
				return 785;
			case -9:
				return 770;
			case -10:
				return 755;
			case -11:
				return 735;
			case -12:
				return 715;
			case -13:
				return 695;
			case -14:
				return 675;
			case -15:
				return 655;
			case -16:
				return 625;
			case -17:
				return 595;
			case -18:
				return 565;
			case -19:
				return 535;
			case -20:
				return 505;
			case -21:
				return 455;
			case -22:
				return 405;
			case -23:
				return 355;
			case -24:
				return 305;
		}

		return 275;
	}

	/**
	 * @param attacker
	 * @param target
	 * @param sendSysMsg
	 * @return 0 = shield defense doesn't succeed<br>
	 *         1 = shield defense succeed<br>
	 *         2 = perfect block<br>
	 */
	public static byte calcShldUse(L2Character attacker, L2Character target)
	{
		return calcShldUse(attacker, target, null);
	}

	public static byte calcShldUse(L2Character attacker, L2Character target, L2Skill skill)
	{
		return calcShldUse(attacker, target, skill, true);
	}

	public static byte calcShldUse(L2Character attacker, L2Character target, L2Skill skill, boolean sendSysMsg)
	{
		if (skill != null && skill.ignoreShld())
			return SHIELD_DEFENSE_FAILED;

		// if shield not exists
		Inventory inv = target.getInventory();
		if (inv != null && inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
			return SHIELD_DEFENSE_FAILED;

		if (!attacker.isInFrontOf(target, target.calcStat(Stats.SHIELD_ANGLE, 120, target, skill) / 2))
			return SHIELD_DEFENSE_FAILED;

		double shldRate = target.calcStat(Stats.SHIELD_RATE, 0, attacker, skill) * DEXbonus[target.getStat().getDEX()];
		if (shldRate == 0.0)
			return SHIELD_DEFENSE_FAILED;

		// if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
		L2Weapon weapon = attacker.getActiveWeaponItem();
		if (weapon != null && weapon.getItemType().isBowType())
			shldRate *= 1.3;

		if (!Rnd.calcChance(shldRate, 100))
			return SHIELD_DEFENSE_FAILED;

		byte shldSuccess = Rnd.calcChance(Config.ALT_PERFECT_SHLD_BLOCK, 100) ? SHIELD_DEFENSE_PERFECT_BLOCK : SHIELD_DEFENSE_SUCCEED;

		if (sendSysMsg && target instanceof L2PcInstance)
		{
			switch (shldSuccess)
			{
				case SHIELD_DEFENSE_SUCCEED:
					target.sendPacket(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL);
					break;
				case SHIELD_DEFENSE_PERFECT_BLOCK:
					target.sendPacket(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
					break;
			}
		}

		return shldSuccess;
	}

	public static boolean calcMagicAffected(L2Character actor, L2Character target, L2Skill skill)
	{
		// TODO: CHECK/FIX THIS FORMULA UP!!
		L2SkillType type = skill.getSkillType();
		double defence = 0;
		if (skill.isActive() && skill.isOffensive())
			defence = target.getMDef(actor, skill);

		double attack = 2 * actor.getMAtk(target, skill) * calcSkillVulnerability(actor, target, skill, type);
		double d = (attack - defence) / (attack + defence);
		
		if (target.isRaid() && !calcRaidAffected(type))
			return false;

		d += 0.5 * Rnd.nextGaussian();
		return d > 0;
	}
	
	private static double calcSkillVulnerability(L2Character attacker, L2Character target, L2Skill skill, L2SkillType type)
	{
		if (target.isPreventedFromReceivingBuffs())
			return 0;

		double multiplier = 1; // initialize...
		
		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		if (skill.getElement() > 0)
			multiplier *= Math.sqrt(calcElemental(attacker, target, skill));
		
		/* I would believe this is more logical, and comment BUFF & DEBUFF in switch
		if (skill.isOffensive())
			multiplier = target.calcStat(Stats.DEBUFF_VULN, multiplier, target, null);
		else
			multiplier = target.calcStat(Stats.BUFF_VULN, multiplier, target, null);
		*/
		
		switch (type)
		{
			case BLEED:
				multiplier = target.calcStat(Stats.BLEED_VULN, multiplier, target, null);
				break;
			case POISON:
				multiplier = target.calcStat(Stats.POISON_VULN, multiplier, target, null);
				break;
			case STUN:
				multiplier = target.calcStat(Stats.STUN_VULN, multiplier, target, null);
				break;
			case PARALYZE:
				multiplier = target.calcStat(Stats.PARALYZE_VULN, multiplier, target, null);
				break;
			case ROOT:
				multiplier = target.calcStat(Stats.ROOT_VULN, multiplier, target, null);
				break;
			case SLEEP:
				multiplier = target.calcStat(Stats.SLEEP_VULN, multiplier, target, null);
				break;
			case MUTE:
			case FEAR:
			case BETRAY:
			case AGGREDUCE_CHAR:
				multiplier = target.calcStat(Stats.DERANGEMENT_VULN, multiplier, target, null);
				break;
			case CONFUSION:
			case CONFUSE_MOB_ONLY:
				multiplier = target.calcStat(Stats.CONFUSION_VULN, multiplier, target, null);
				break;
			case DEBUFF:
			case WEAKNESS:
				multiplier = target.calcStat(Stats.DEBUFF_VULN, multiplier, target, null);
				break;
			case CANCEL:
			case NEGATE:
				multiplier = target.calcStat(Stats.CANCEL_VULN, multiplier, target, null);
				break;
			case BUFF:
				multiplier = target.calcStat(Stats.BUFF_VULN, multiplier, target, null);
				break;
			default:
		}

		return multiplier;
	}
	
	private static double calcSkillTypeProficiency(L2Character attacker, L2Character target, L2SkillType type)
	{
		double multiplier = 1;
		
		switch (type)
		{
			case BLEED:
				multiplier = attacker.calcStat(Stats.BLEED_PROF, multiplier, target, null);
				break;
			case POISON:
				multiplier = attacker.calcStat(Stats.POISON_PROF, multiplier, target, null);
				break;
			case STUN:
				multiplier = attacker.calcStat(Stats.STUN_PROF, multiplier, target, null);
				break;
			case PARALYZE:
				multiplier = attacker.calcStat(Stats.PARALYZE_PROF, multiplier, target, null);
				break;
			case ROOT:
				multiplier = attacker.calcStat(Stats.ROOT_PROF, multiplier, target, null);
				break;
			case SLEEP:
				multiplier = attacker.calcStat(Stats.SLEEP_PROF, multiplier, target, null);
				break;
			case MUTE:
			case FEAR:
			case BETRAY:
			case AGGREDUCE_CHAR:
				multiplier = attacker.calcStat(Stats.DERANGEMENT_PROF, multiplier, target, null);
				break;
			case CONFUSION:
			case CONFUSE_MOB_ONLY:
				multiplier = attacker.calcStat(Stats.CONFUSION_PROF, multiplier, target, null);
				break;
			case DEBUFF:
			case WEAKNESS:
				multiplier = attacker.calcStat(Stats.DEBUFF_PROF, multiplier, target, null);
				break;
			case CANCEL:
			case NEGATE:
				multiplier = attacker.calcStat(Stats.CANCEL_PROF, multiplier, target, null);
				break;
		}
		
		return multiplier;
	}
	
	private static double calcSkillStatModifier(L2SkillType type, L2Character target)
	{
		double multiplier = 1;
		
		switch (type)
		{
			case STUN:
			case BLEED:
			case POISON:
				multiplier = 2 - sqrtCONbonus[target.getStat().getCON()];
				break;
			case SLEEP:
			case DEBUFF:
			case WEAKNESS:
			case ERASE:
			case ROOT:
			case MUTE:
			case FEAR:
			case BETRAY:
			case CONFUSION:
			case CONFUSE_MOB_ONLY:
			case AGGREDUCE_CHAR:
			case PARALYZE:
				multiplier = 2 - sqrtMENbonus[target.getStat().getMEN()];
				break;
		}
		
		return Math.max(0, multiplier);
	}
	
	public static boolean calcSkillSuccess(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss, boolean sps, boolean bss)
	{
		final double baseChance = skill.getEffectPower();
		
		return calcSkillSuccess(baseChance, attacker, target, skill, shld, ss, sps, bss);
	}
	
	public static boolean calcSkillSuccess(final double baseChance, L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss, boolean sps, boolean bss)
	{
		if (skill.hasEffects()) // debuffs should fail if already applied
		{
			L2Effect e = target.getEffects().getFirstEffect(skill.getEffectTemplates()[0].stackTypes);
			if (e != null)
			{
				if (skill.getEffectTemplates()[0].stackOrder <= e.getStackOrder())
					return false;
			}
		}
		
		if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) // perfect block
			return false;
		
		final L2SkillType type = skill.getEffectType();
		
		if (target.isRaid() && !calcRaidAffected(type))
			return false;
		
		if (skill.ignoreResists())
			return Rnd.get(100) < baseChance;
		
		final double statmodifier = calcSkillStatModifier(type, target);
		final double resmodifier = calcSkillVulnerability(attacker, target, skill, type);
		final double profmodifier = calcSkillTypeProficiency(attacker, target, type);
		final double ssmodifier = (bss ? 150 : (sps || ss ? 125 : 100));
		final double lvlModifier = getLevelModifier(skill.getLevelDepend(), attacker, target, skill);
		
		// Calculate BaseRate.
		double rate = baseChance * statmodifier;
		
		// Add Matk/Mdef Bonus
		if (skill.isMagic())
			rate *= Math.pow((double) attacker.getMAtk(target, skill) / (target.getMDef(attacker, skill) + (shld == 1 ? target.getShldDef() : 0)), 0.1);
		
		// Add Bonus for Sps/SS
		if (ssmodifier != 100)
		{
			if (rate > 10000 / (100 + ssmodifier))
				rate = 100 - (100 - rate) * 100 / ssmodifier;
			else
				rate = rate * ssmodifier / 100;
		}
		
		rate *= lvlModifier;
		
		rate = L2Math.limit(1, rate, 99);
		
		//Finaly apply resists.
		rate *= resmodifier * profmodifier;
		
		return Rnd.get(100) < rate;
	}
	
	public static boolean calcCubicSkillSuccess(L2CubicInstance attacker, L2Character target, L2Skill skill, byte shld)
	{
		// if target reflect this skill then the effect will fail
		if (calcSkillReflect(target, skill) != SKILL_REFLECT_FAILED)
			return false;
		
		if (skill.hasEffects()) // debuffs should fail if already applied
			if (target.getEffects().hasEffect(skill.getEffectTemplates()[0].stackTypes))
				return false;
		
		if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) // perfect block
			return false;
		
		final L2SkillType type = skill.getEffectType();
		
		if (target.isRaid() && !calcRaidAffected(type))
			return false;
		
		final double baseChance = skill.getEffectPower();
		
		if (skill.ignoreResists())
			return Rnd.get(100) < baseChance;
		
		final double statmodifier = calcSkillStatModifier(type, target);
		final double resmodifier = calcSkillVulnerability(attacker.getOwner(), target, skill, type);
		final double lvlModifier = getLevelModifier(skill.getLevelDepend(), attacker.getOwner(), target, skill);
		
		double rate = baseChance * statmodifier * resmodifier;
		
		if (skill.isMagic())
			rate += Math.pow((double)attacker.getMAtk() / (target.getMDef(attacker.getOwner(), skill) + (shld == 1 ? target.getShldDef() : 0)), 0.1) * 100 - 100;
		
		rate *= lvlModifier;
		
		rate = L2Math.limit(1, rate, 99);
		
		return Rnd.get(100) < rate;
	}
	
	private static double getLevelModifier(int lvlDepend, L2Character attacker, L2Character target, L2Skill skill)
	{
		if (lvlDepend <= 0)
			return 1;
		
		// this totally screwed PvP over lvl80, lets take an example:
		// lvl85 target, lvl85 attacker, lvl75 skill (since most of the debuffs are lower...)
		// delta resulted in -12..-14... -> chance was multiplied with 0.3...0.4 -> almost never land
		//
		//int attackerLvlmod = attacker.getLevel();
		//int targetLvlmod = target.getLevel();
		//if (attackerLvlmod >= 70)
		//	attackerLvlmod = ((attackerLvlmod - 69) * 2) + 70;
		//if (targetLvlmod >= 70)
		//	targetLvlmod = ((targetLvlmod - 69) * 2) + 70;
		
		double delta = getMagicLevelDifference(attacker, target, skill);
		
		// just a guess, but lvlDepend wasn't used at all, so somehow it must be added
		delta *= 1 + 0.1 * lvlDepend;
		
		if (delta <= -19)
		{
			return 0.05;
		}
		else if (/*-19 < delta && */delta < -3)
		{
			return 1 + delta / 20;
		}
		else/* if (-3 <= delta)*/
		{
			return 1 + delta / 75;
		}
	}

	public static boolean calcMagicSuccess(L2Character attacker, L2Character target, L2Skill skill)
	{
		double lvlDifference = -1 * getMagicLevelDifference(attacker, target, skill);
		double rate = Math.round(Math.pow(1.3, lvlDifference) * 100);
		rate = attacker.getStat().calcStat(Stats.MAGIC_FAIL_RATE, rate, target, skill);

		return (Rnd.get(10000) > (int) rate);
	}

	public static boolean calculateUnlockChance(L2Skill skill)
	{
		int level = skill.getLevel();
		int chance = 0;
		switch (level)
		{
			case 1:
				chance = 30;
				break;

			case 2:
				chance = 50;
				break;

			case 3:
				chance = 75;
				break;

			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				chance = 100;
				break;
		}

		return Rnd.get(120) <= chance;
	}

	public static double calcManaDam(L2Character attacker, L2Character target, L2Skill skill, boolean ss, boolean bss)
	{
		//Mana Burnt = (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		double mp = target.getMaxMp();
		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;

		double damage = (Math.sqrt(mAtk) * skill.getPower(attacker) * (mp / 97)) / mDef;
		damage *= calcSkillVulnerability(attacker, target, skill, skill.getSkillType());
		return GlobalRestrictions.calcDamage(attacker, target, damage, skill);
	}

	public static double calculateSkillResurrectRestorePercent(final double baseRestorePercent, int casterWIT)
	{
		if (baseRestorePercent == 0 || baseRestorePercent == 100)
			return baseRestorePercent;
		
		double restorePercent = baseRestorePercent * WITbonus[casterWIT];
		
		if(restorePercent - baseRestorePercent > 20.0)
			restorePercent += 20.0;
		
		return L2Math.limit(baseRestorePercent, restorePercent, 90);
	}
	
	public static double getINTBonus(L2Character activeChar)
	{
		return INTbonus[activeChar.getStat().getINT()];
	}
	
	public static double getSTRBonus(L2Character activeChar)
	{
		return STRbonus[activeChar.getStat().getSTR()];
	}
	
	public static boolean calcPhysicalSkillEvasion(L2Character target, L2Skill skill)
	{
		if (skill.isMagic() && skill.getSkillType() != L2SkillType.BLOW)
			return false;
		
		return Rnd.get(100) < target.calcStat(Stats.P_SKILL_EVASION, 0, null, skill);
	}
	
	public static boolean calcSkillMastery(L2Character actor, L2Skill skill)
	{
		if (skill.getSkillType() == L2SkillType.FISHING || skill.isToggle())
			return false;
		
		double val = actor.getStat().calcStat(Stats.SKILL_MASTERY, 0, null, null);
		
		if (actor instanceof L2PcInstance)
		{
			if (((L2PcInstance)actor).isMageClass())
				val *= getINTBonus(actor);
			else
				val *= getSTRBonus(actor);
		}
		
		return Rnd.get(100) < val;
	}
	
	private static double calcValakasAttribute(L2Character attacker, L2Character target, L2Skill skill)
	{
		double calcPower = 0;
		double calcDefen = 0;
		
		if (skill != null && skill.getAttributeName().contains("valakas"))
		{
			calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
			calcDefen = target.calcStat(Stats.VALAKAS_RES, calcDefen, target, skill);
		}
		else
		{
			calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
			if (calcPower > 0)
			{
				calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
				calcDefen = target.calcStat(Stats.VALAKAS_RES, calcDefen, target, skill);
			}
		}
		return calcPower - calcDefen;
	}
	
	public static double calcElemental(L2Character attacker, L2Character target, L2Skill skill)
	{
		if (skill != null)
		{
			final byte element = skill.getElement();
			if (element >= 0)
			{
				int calcPower = skill.getElementPower();
				int calcDefen = target.getDefenseElementValue(element);
				
				if (attacker.getAttackElement() == element)
					calcPower += attacker.getAttackElementValue(element);
				
				int calcTotal = calcPower - calcDefen;
				if (calcTotal > 0)
				{
					if (calcTotal < 75)
						return 1 + calcTotal * 0.0052;
					else if (calcTotal < 150)
						return 1.4;
					else if (calcTotal < 290)
						return 1.7;
					else if (calcTotal < 300)
						return 1.8;
					else
						return 2.0;
				}
			}
		}
		else
		{
			final byte element = attacker.getAttackElement();
			if (element >= 0)
			{
				int calcPower = attacker.getAttackElementValue(element);
				int calcDefen = target.getDefenseElementValue(element);
				
				return 1 + L2Math.limit(-20, calcPower - calcDefen, 100) * 0.007;
			}
		}
		
		return 1;
	}

	/**
	 * Calculate skill reflection according these three possibilities: <li>
	 * Reflect failed</li> <li>Mormal reflect (just effects). <U>Only possible
	 * for skilltypes: BUFF, REFLECT, HEAL_PERCENT, MANAHEAL_PERCENT, HOT,
	 * CPHOT, MPHOT</U></li> <li>vengEance reflect (100% damage reflected but
	 * damage is also dealt to actor). <U>This is only possible for skills with
	 * skilltype PDAM, BLOW, CHARGEDAM, MDAM or DEATHLINK</U></li> <br>
	 * <br>
	 * 
	 * @param actor
	 * @param target
	 * @param skill
	 * @return SKILL_REFLECTED_FAILED, SKILL_REFLECT_SUCCEED or
	 *         SKILL_REFLECT_VENGEANCE
	 */
	public static byte calcSkillReflect(L2Character target, L2Skill skill)
	{
		/*
		 * Neither some special skills (like hero debuffs...) or those skills
		 * ignoring resistances can be reflected
		 */
		if (skill.ignoreResists() || !skill.canBeReflected())
			return SKILL_REFLECT_FAILED;
		
		// only magic and melee skills can be reflected
		if (!skill.isMagic() && (skill.getCastRange() == -1 || skill.getCastRange() > MELEE_ATTACK_RANGE))
			return SKILL_REFLECT_FAILED;
		
		byte reflect = SKILL_REFLECT_FAILED;
		// check for non-reflected skilltypes, need additional retail check
		switch (skill.getSkillType())
		{
			case BUFF:
			case REFLECT:
			case HEAL_PERCENT:
			case MANAHEAL_PERCENT:
			case HOT:
			case CPHOT:
			case MPHOT:
			case AGGDEBUFF:
			case CONT:
				return SKILL_REFLECT_FAILED;
				// these skill types can deal damage
			case PDAM:
			case BLOW:
			case MDAM:
			case DEATHLINK:
			case CHARGEDAM:
				final Stats stat = skill.isMagic() ? Stats.VENGEANCE_SKILL_MAGIC_DAMAGE : Stats.VENGEANCE_SKILL_PHYSICAL_DAMAGE;
				final double venganceChance = target.getStat().calcStat(stat, 0, target, skill);
				if (venganceChance > Rnd.get(100))
					reflect |= SKILL_REFLECT_VENGEANCE;
				break;
		}
		
		final double reflectChance = target.calcStat(skill.isMagic() ? Stats.REFLECT_SKILL_MAGIC : Stats.REFLECT_SKILL_PHYSIC, 0, null, skill);
		
		if (Rnd.get(100) < reflectChance)
			reflect |= SKILL_REFLECT_SUCCEED;
		
		return reflect;
	}

	/**
	 * Returns damage multiplicator according to the position of the attacker
	 */
	public static double calcPositionRate(L2Character attacker, L2Character target)
	{
		switch (Direction.getDirection(attacker, target))
		{
			case SIDE:
				return 1.05;
			case BACK:
				return 1.2;
		}
		
		return 1.0;
	}
	
	public static boolean calcRaidAffected(L2SkillType type)
	{
		switch (type)
		{
			case CONFUSION:
			case ROOT:
			case STUN:
			case MUTE:
			case FEAR:
			case DEBUFF:
			case PARALYZE:
			case SLEEP:
			case AGGDEBUFF:
			case AGGREDUCE_CHAR:
				if (Rnd.get(1000) == 1)
					return true;
		}
		return false;
	}
}
