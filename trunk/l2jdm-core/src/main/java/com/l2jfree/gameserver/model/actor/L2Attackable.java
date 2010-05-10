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
package com.l2jfree.gameserver.model.actor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ItemsAutoDestroy;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.ai.L2AttackableAI;
import com.l2jfree.gameserver.ai.L2CharacterAI;
import com.l2jfree.gameserver.ai.L2FortSiegeGuardAI;
import com.l2jfree.gameserver.ai.L2SiegeGuardAI;
import com.l2jfree.gameserver.datatables.EventDroplist;
import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.datatables.EventDroplist.DateDrop;
import com.l2jfree.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jfree.gameserver.model.L2CharPosition;
import com.l2jfree.gameserver.model.L2CommandChannel;
import com.l2jfree.gameserver.model.L2DropCategory;
import com.l2jfree.gameserver.model.L2DropData;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Manor;
import com.l2jfree.gameserver.model.L2Party;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2ChestInstance;
import com.l2jfree.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfree.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jfree.gameserver.model.actor.instance.L2SummonInstance;
import com.l2jfree.gameserver.model.actor.knownlist.AttackableKnownList;
import com.l2jfree.gameserver.model.actor.knownlist.CharKnownList;
import com.l2jfree.gameserver.model.actor.status.AttackableStatus;
import com.l2jfree.gameserver.model.actor.status.CharStatus;
import com.l2jfree.gameserver.model.base.SoulCrystal;
import com.l2jfree.gameserver.model.itemcontainer.PcInventory;
import com.l2jfree.gameserver.model.quest.Quest;
import com.l2jfree.gameserver.model.quest.QuestState;
import com.l2jfree.gameserver.model.quest.State;
import com.l2jfree.gameserver.network.SystemChatChannelId;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.CreatureSay;
import com.l2jfree.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.skills.Stats;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;
import com.l2jfree.gameserver.util.Util;
import com.l2jfree.lang.L2Math;
import com.l2jfree.tools.random.Rnd;
import com.l2jfree.util.LinkedBunch;
import com.l2jfree.util.SingletonMap;

/**
 * This class manages all NPC that can be attacked.<BR>
 * <BR>
 * L2Attackable :<BR>
 * <BR>
 * <li>L2ArtefactInstance</li> <li>L2FriendlyMobInstance</li> <li>
 * L2MonsterInstance</li> <li>L2SiegeGuardInstance</li>
 * 
 * @version $Revision: 1.24.2.3.2.16 $ $Date: 2005/04/11 19:11:21 $
 */
public class L2Attackable extends L2Npc
{
	public static final int FLEEING_NOT_STARTED		= 0;
	public static final int FLEEING_STARTED			= 1;
	public static final int FLEEING_DONE_WAITING	= 2;
	public static final int FLEEING_DONE_RETURNING	= 3;

	/**
	 * This class contains all AggroInfo of the L2Attackable against the
	 * attacker L2Character.<BR>
	 * <BR>
	 * <B><U> Data</U> :</B><BR>
	 * <BR>
	 * <li>attacker : The attaker L2Character concerned by this AggroInfo of
	 * this L2Attackable</li> <li>hate : Hate level of this L2Attackable against
	 * the attaker L2Character (hate = damage)</li> <li>damage : Number of
	 * damages that the attaker L2Character gave to this L2Attackable</li><BR>
	 * <BR>
	 */
	public static final class AggroInfo
	{
		/**
		 * Hate level of this L2Attackable against the attaker L2Character (hate
		 * = damage)
		 */
		protected int			_hate;

		/**
		 * Number of damages that the attaker L2Character gave to this
		 * L2Attackable
		 */
		protected int			_damage;

		/**
		 * Constructor of AggroInfo.<BR>
		 * <BR>
		 */
		AggroInfo()
		{
		}

		public int getHate()
		{
			return _hate;
		}

		public int getDamage()
		{
			return _damage;
		}
	}

	/**
	 * This class contains all RewardInfo of the L2Attackable against the any
	 * attacker L2Character, based on amount of damage done.<BR>
	 * <BR>
	 * <B><U> Data</U> :</B><BR>
	 * <BR>
	 * <li>attacker : The attaker L2Character concerned by this RewardInfo of
	 * this L2Attackable</li> <li>dmg : Total amount of damage done by the
	 * attacker to this L2Attackable (summon + own)</li>
	 */
	protected static final class RewardInfo
	{
		protected int			_dmg	= 0;

		RewardInfo(int pDmg)
		{
			_dmg = pDmg;
		}

		public void addDamage(int pDmg)
		{
			_dmg += pDmg;
		}
	}

	/**
	 * This class contains all AbsorberInfo of the L2Attackable against the
	 * absorber L2Character.<BR>
	 * <BR>
	 * <B><U> Data</U> :</B><BR>
	 * <BR>
	 * <li>absorber : The attaker L2Character concerned by this AbsorberInfo of
	 * this L2Attackable</li>
	 */
	protected static final class AbsorberInfo
	{
		/**
		 * The attaker L2Character concerned by this AbsorberInfo of this
		 * L2Attackable
		 */
		protected int			_crystalId;
		protected double		_absorbedHP;

		/**
		 * Constructor of AbsorberInfo.<BR>
		 * <BR>
		 */
		AbsorberInfo(int pCrystalId, double pAbsorbedHP)
		{
			_crystalId = pCrystalId;
			_absorbedHP = pAbsorbedHP;
		}
	}

	/**
	 * This class is used to create item reward lists instead of creating item
	 * instances.<BR>
	 * <BR>
	 */
	public static final class RewardItem
	{
		protected int	_itemId;
		protected int	_count;

		RewardItem(int itemId, int count)
		{
			_itemId = itemId;
			_count = count;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public int getCount()
		{
			return _count;
		}
	}

	/**
	 * The table containing all autoAttackable L2Character in its Aggro Range
	 * and L2Character that attacked the L2Attackable This Map is Thread Safe,
	 * but Removing Object While Interating Over It Will Result NPE
	 */
	private final Map<L2Character, AggroInfo> _aggroList = new SingletonMap<L2Character, AggroInfo>().setShared();

	/** Use this to Read or Put Object to this Map */
	public final Map<L2Character, AggroInfo> getAggroListRP()
	{
		return _aggroList;
	}

	/**
	 * Use this to Remove Object from this Map This Should be Synchronized While
	 * Interating over This Map - ie u cant interating and removing object at
	 * once
	 */
	public final Map<L2Character, AggroInfo> getAggroList()
	{
		return _aggroList;
	}

	private boolean	_isReturningToSpawnPoint	= false;

	public final boolean isReturningToSpawnPoint()
	{
		return _isReturningToSpawnPoint;
	}

	public final void setisReturningToSpawnPoint(boolean value)
	{
		_isReturningToSpawnPoint = value;
	}

	private boolean	_canReturnToSpawnPoint	= true;

	public final boolean canReturnToSpawnPoint()
	{
		return _canReturnToSpawnPoint;
	}

	public final void setCanReturnToSpawnPoint(boolean value)
	{
		_canReturnToSpawnPoint = value;
	}

	/** Table containing all Items that a Dwarf can Sweep on this L2Attackable */
	private RewardItem[]						_sweepItems;

	/** crops */
	private RewardItem[]						_harvestItems;
	private boolean								_seeded;
	private int									_seedType						= 0;
	private L2PcInstance						_seeder							= null;

	/**
	 * True if an over-hit enabled skill has successfully landed on the
	 * L2Attackable
	 */
	private boolean								_overhit;

	/**
	 * Stores the extra (over-hit) damage done to the L2Attackable when the
	 * attacker uses an over-hit enabled skill
	 */
	private double								_overhitDamage;

	/**
	 * Stores the attacker who used the over-hit enabled skill on the
	 * L2Attackable
	 */
	private L2Character							_overhitAttacker;

	/**
	 * First CommandChannel who attacked the L2Attackable and meet the
	 * requirements
	 **/
	private L2CommandChannel					_firstCommandChannelAttacked	= null;
	private CommandChannelTimer					_commandChannelTimer			= null;

	/** True if a Soul Crystal was successfuly used on the L2Attackable */
	private boolean								_absorbed;

	/**
	 * The table containing all L2PcInstance that successfuly absorbed the soul
	 * of this L2Attackable
	 */
	private final Map<L2PcInstance, AbsorberInfo> _absorbersList = new SingletonMap<L2PcInstance, AbsorberInfo>().setShared();

	public Map<L2PcInstance, AbsorberInfo> getAbsorbersList()
	{
		return _absorbersList;
	}

	/** Have this L2Attackable to reward Exp and SP on Die? **/
	private boolean								_mustGiveExpSp;

	private boolean								_beenAttacked;
	private int									_fleeing;
	private L2CharPosition						_moveAroundPos;

	// Used for Chimeras on Hellbound
	private boolean								_bottled = false;
	private float								_hpWhenBottled = 1;

	/**
	 * Constructor of L2Attackable (use L2Character and L2NpcInstance
	 * constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to set the _template of the
	 * L2Attackable (copy skills from template to object and link _calculators
	 * to NPC_STD_CALCULATOR)</li> <li>Set the name of the L2Attackable</li> <li>
	 * Create a RandomAnimation Task that will be launched after the calculated
	 * delay if the server allow it</li><BR>
	 * <BR>
	 * 
	 * @param objectId Identifier of the object to initialized
	 * @param L2NpcTemplate Template to apply to the NPC
	 */
	public L2Attackable(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		getKnownList(); // init knownlist
		setIsInvul(false);
		_mustGiveExpSp = true;
	}

	@Override
	protected CharKnownList initKnownList()
	{
		return new AttackableKnownList(this);
	}

	@Override
	public AttackableKnownList getKnownList()
	{
		return (AttackableKnownList) _knownList;
	}

	@Override
	protected L2CharacterAI initAI()
	{
		return new L2AttackableAI(new AIAccessor());
	}

	public final void startCommandChannelTimer(L2Character attacker)
	{
		// CommandChannel
		if (_commandChannelTimer == null && attacker != null && isRaid() && attacker.isInParty() && attacker.getParty().isInCommandChannel()
				&& attacker.getParty().getCommandChannel().meetRaidWarCondition(this))
		{
			_firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
			_commandChannelTimer = new CommandChannelTimer(attacker.getParty().getCommandChannel());
			ThreadPoolManager.getInstance().scheduleGeneral(_commandChannelTimer, 300000); // 5 min
			_firstCommandChannelAttacked.broadcastToChannelMembers(new CreatureSay(0, SystemChatChannelId.Chat_Party_Room, "",
			"You have looting rights!"));
		}
	}

	@Override
	protected CharStatus initStatus()
	{
		return new AttackableStatus(this);
	}

	@Override
	public AttackableStatus getStatus()
	{
		return (AttackableStatus)_status;
	}

	public synchronized void setMustRewardExpSp(boolean value)
	{
		_mustGiveExpSp = value;
	}

	public synchronized boolean getMustRewardExpSP()
	{
		return _mustGiveExpSp;
	}

	/**
	 * Kill the L2Attackable (the corpse disappeared after 7 seconds),
	 * distribute rewards (EXP, SP, Drops...) and notify Quest Engine.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Distribute Exp and SP rewards to L2PcInstance (including Summon
	 * owner) that hit the L2Attackable and to their Party members</li> <li>
	 * Notify the Quest Engine of the L2Attackable death if necessary</li> <li>
	 * Kill the L2NpcInstance (the corpse disappeared after 7 seconds)</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards
	 * to L2PetInstance</B></FONT><BR>
	 * <BR>
	 * 
	 * @param killer The L2Character that has killed the L2Attackable
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		// Kill the L2NpcInstance (the corpse disappeared after 7 seconds)
		if (!super.doDie(killer))
			return false;

		// Enhance soul crystals of the attacker if this L2Attackable had its soul absorbed
		try
		{
			levelSoulCrystals(killer);
		}
		catch (Exception e)
		{
			_log.fatal("", e);
		}

		// Notify the Quest Engine of the L2Attackable death if necessary
		try
		{
			if (killer != null)
			{
				L2PcInstance player = null;
				player = killer.getActingPlayer();

				if (player != null)
				{
					//only 1 randomly choosen quest of all quests registered to this character can be applied
					Quest[] allOnKillQuests = getTemplate().getEventQuests(Quest.QuestEventType.ON_KILL);
					if (allOnKillQuests != null && allOnKillQuests.length > 0)
					{
						//Quest quest;
						//if(allOnKillQuests.length > 1)
						//    quest = allOnKillQuests[Rnd.get(allOnKillQuests.length)];
						//else
						//    quest = allOnKillQuests[0];
						for (Quest quest : allOnKillQuests)
							quest.notifyKill(this, player, killer instanceof L2Summon);
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.fatal("", e);
		}

		setChampion(false);

		return true;
	}

	/**
	 * Distribute Exp and SP rewards to L2PcInstance (including Summon owner)
	 * that hit the L2Attackable and to their Party members.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the L2PcInstance owner of the L2SummonInstance (if necessary) and
	 * L2Party in progress</li> <li>Calculate the Experience and SP rewards in
	 * function of the level difference</li> <li>Add Exp and SP rewards to
	 * L2PcInstance (including Summon penalty) and to Party members in the known
	 * area of the last attacker</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards
	 * to L2PetInstance</B></FONT><BR>
	 * <BR>
	 * 
	 * @param lastAttacker The L2Character that has killed the L2Attackable
	 */
	@SuppressWarnings("null")
	@Override
	protected void calculateRewards(L2Character lastAttacker)
	{
		// Creates an empty list of rewards
		FastMap<L2Character, RewardInfo> rewards = new FastMap<L2Character, RewardInfo>().setShared(true);

		try
		{
			if (getAggroListRP().isEmpty())
				return;

			if (!isMagicBottled())
			{
				// Manage Base, Quests and Sweep drops of the L2Attackable
				doItemDrop(lastAttacker);
				// Manage drop of Special Events created by GM for a defined period
				doEventDrop(lastAttacker);
			}

			if (!getMustRewardExpSP())
				return;

			int rewardCount = 0;
			int damage;
			L2Character attacker, ddealer;
			RewardInfo reward;

			// While iterating over this map removing objects is not allowed
			synchronized (getAggroList())
			{
				// Go through the _aggroList of the L2Attackable
				for (Map.Entry<L2Character, AggroInfo> entry : getAggroListRP().entrySet())
				{
					AggroInfo info = entry.getValue();
					if (info == null)
						continue;

					// Get the L2Character corresponding to this attacker
					attacker = entry.getKey();

					// Get damages done by this attacker
					damage = info._damage;

					// Prevent unwanted behavior
					if (damage > 1)
					{
						if ((attacker instanceof L2SummonInstance)
								|| ((attacker instanceof L2PetInstance) && ((L2PetInstance) attacker).getPetData().getOwnerExpTaken() > 0))
							ddealer = ((L2Summon) attacker).getOwner();
						else
							ddealer = entry.getKey();

						// Check if ddealer isn't too far from this (killed monster)
						if (!Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, ddealer, true))
							continue;

						// Calculate real damages (Summoners should get own damage plus summon's damage)
						reward = rewards.get(ddealer);

						if (reward == null)
						{
							reward = new RewardInfo(damage);
							rewardCount++;
						}
						else
						{
							reward.addDamage(damage);
						}
						rewards.put(ddealer, reward);
					}
				}
			}
			if (!rewards.isEmpty())
			{
				L2Party attackerParty;
				long exp;
				int levelDiff, partyDmg, partyLvl, sp;
				float partyMul, penalty;
				RewardInfo reward2;
				int[] tmp;

				for (FastMap.Entry<L2Character, RewardInfo> entry = rewards.head(), end = rewards.tail(); (entry = entry.getNext()) != end;)
				{
					if (entry == null)
						continue;

					reward = entry.getValue();
					if (reward == null)
						continue;

					// Penalty applied to the attacker's XP
					penalty = 0;

					// Attacker to be rewarded
					attacker = entry.getKey();

					// Total amount of damage done
					damage = reward._dmg;

					// If the attacker is a Pet, get the party of the owner
					if (attacker instanceof L2PetInstance)
						attackerParty = attacker.getParty();
					else if (attacker instanceof L2PcInstance)
						attackerParty = attacker.getParty();
					else
						return;

					// If this attacker is a L2PcInstance with a summoned L2SummonInstance, get Exp Penalty applied for the current summoned L2SummonInstance
					if (attacker instanceof L2PcInstance && attacker.getPet() instanceof L2SummonInstance)
					{
						penalty = ((L2SummonInstance) ((L2PcInstance) attacker).getPet()).getExpPenalty();
					}

					// We must avoid "over damage", if any
					if (damage > getMaxHp())
						damage = getMaxHp();

					// If there's NO party in progress
					if (attackerParty == null)
					{
						// Calculate Exp and SP rewards
						if (attacker.getKnownList().knowsObject(this))
						{
							// Calculate the difference of level between this attacker (L2PcInstance or L2SummonInstance owner) and the L2Attackable
							// mob = 24, atk = 10, diff = -14 (full xp)
							// mob = 24, atk = 28, diff = 4 (some xp)
							// mob = 24, atk = 50, diff = 26 (no xp)
							levelDiff = attacker.getLevel() - getLevel();

							tmp = calculateExpAndSp(levelDiff, damage);
							exp = tmp[0];
							exp *= 1 - penalty;
							sp = tmp[1];

							// Check for an over-hit enabled strike
							if (attacker instanceof L2PcInstance)
							{
								L2PcInstance player = (L2PcInstance) attacker;
								if (isOverhit() && attacker == getOverhitAttacker())
								{
									int overHitExp = (int) calculateOverhitExp(exp);
									SystemMessage sms = new SystemMessage(SystemMessageId.ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT);
									sms.addNumber(overHitExp);
									player.sendPacket(sms);
									exp += overHitExp;
								}
							}

							// Distribute the Exp and SP between the L2PcInstance and its L2Summon
							if (isChampion())
							{
								exp *= Config.CHAMPION_EXP_SP;
								sp *= Config.CHAMPION_EXP_SP;
							}

							if (isMagicBottled())
							{
								exp = Math.round(exp / 100 * _hpWhenBottled);
								sp = Math.round(sp / 100 * _hpWhenBottled);
							}

							if (!attacker.isDead())
							{
								long addexp = Math.round(attacker.calcStat(Stats.EXPSP_RATE, exp, null, null));
								int addsp = (int) attacker.calcStat(Stats.EXPSP_RATE, sp, null, null);
								if (attacker instanceof L2PcInstance && !(this instanceof L2ChestInstance))
								{
									// Soul Mastery skill
									int soulMasteryLevel = attacker.getSkillLevel(L2Skill.SKILL_SOUL_MASTERY);
									if (soulMasteryLevel > 0)
									{
										L2Skill skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_SOUL_MASTERY, soulMasteryLevel);
										if (skill.getExpNeeded() <= addexp)
											((L2PcInstance) attacker).absorbSoulFromNpc(skill, this);
									}
									((L2PcInstance)attacker).addExpAndSp(addexp,addsp, useVitalityRate());
									if (addexp > 0)
										((L2PcInstance)attacker).updateVitalityPoints(getVitalityPoints(damage), true, false);
								}
								else
									attacker.addExpAndSp(addexp,addsp);
							}
						}
					}
					else
					{
						//share with party members
						partyDmg = 0;
						partyMul = 1.f;
						partyLvl = 0;

						// Get all L2Character that can be rewarded in the party
						FastList<L2Playable> rewardedMembers = new FastList<L2Playable>();

						// Go through all L2PcInstance in the party
						List<L2PcInstance> groupMembers;
						if (attackerParty.isInCommandChannel())
							groupMembers = attackerParty.getCommandChannel().getMembers();
						else
							groupMembers = attackerParty.getPartyMembers();

						for (L2PcInstance pl : groupMembers)
						{
							if (pl == null || pl.isDead())
								continue;

							// Get the RewardInfo of this L2PcInstance from L2Attackable rewards
							reward2 = rewards.get(pl);

							// If the L2PcInstance is in the L2Attackable rewards add its damages to party damages
							if (reward2 != null)
							{
								if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, pl, true))
								{
									partyDmg += reward2._dmg; // Add L2PcInstance damages to party damages
									rewardedMembers.add(pl);
									if (pl.getLevel() > partyLvl)
									{
										if (attackerParty.isInCommandChannel())
											partyLvl = attackerParty.getCommandChannel().getLevel();
										else
											partyLvl = pl.getLevel();
									}
								}
								rewards.remove(pl); // Remove the L2PcInstance from the L2Attackable rewards
							}
							else
							{
								// Add L2PcInstance of the party (that have attacked or not) to members that can be rewarded
								// and in range of the monster.
								if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, pl, true))
								{
									rewardedMembers.add(pl);
									if (pl.getLevel() > partyLvl)
									{
										if (attackerParty.isInCommandChannel())
											partyLvl = attackerParty.getCommandChannel().getLevel();
										else
											partyLvl = pl.getLevel();
									}
								}
							}
							L2Playable summon = pl.getPet();
							if (summon != null && summon instanceof L2PetInstance)
							{
								reward2 = rewards.get(summon);
								if (reward2 != null) // Pets are only added if they have done damage
								{
									if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, summon, true))
									{
										partyDmg += reward2._dmg; // Add summon damages to party damages
										rewardedMembers.add(summon);
										if (summon.getLevel() > partyLvl)
											partyLvl = summon.getLevel();
									}
									rewards.remove(summon); // Remove the summon from the L2Attackable rewards
								}
							}
						}

						// If the party didn't killed this L2Attackable alone
						if (partyDmg < getMaxHp())
							partyMul = ((float) partyDmg / (float) getMaxHp());

						// Avoid "over damage"
						if (partyDmg > getMaxHp())
							partyDmg = getMaxHp();

						int newLevel = 0;
						for (L2Character member : rewardedMembers)
						{
							if (member.getLevel() > newLevel)
								newLevel = member.getLevel();
						}

						// Calculate the level difference between Party and L2Attackable
						levelDiff = partyLvl - getLevel();

						// Calculate Exp and SP rewards
						tmp = calculateExpAndSp(levelDiff, partyDmg);
						exp = tmp[0];
						sp = tmp[1];

						exp *= partyMul;
						sp *= partyMul;

						// Check for an over-hit enabled strike
						// (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
						if (attacker instanceof L2PcInstance)
						{
							L2PcInstance player = (L2PcInstance) attacker;
							if (isOverhit() && attacker == getOverhitAttacker())
							{
								player.sendPacket(SystemMessageId.OVER_HIT);
								exp += calculateOverhitExp(exp);
							}
						}

						// champion xp/sp :)
						if (isChampion())
						{
							exp *= Config.CHAMPION_EXP_SP;
							sp *= Config.CHAMPION_EXP_SP;
						}

						if (isMagicBottled())
						{
							exp = Math.round(exp / 100 * _hpWhenBottled);
							sp = Math.round(sp / 100 * _hpWhenBottled);
						}

						// Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker
						if (partyDmg > 0)
							attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl, partyDmg, this);
					}
				}
			}

			rewards = null;
		}
		catch (Exception e)
		{
			_log.fatal("", e);
		}
	}

	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable
	 * _aggroList.<BR>
	 * <BR>
	 * 
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage The number of damages given by the attacker L2Character
	 */
	public void addDamage(L2Character attacker, int damage, L2Skill skill)
	{
		// Notify the L2Attackable AI with EVT_ATTACKED
		if (!isDead())
		{
			try
			{
				L2PcInstance player = attacker.getActingPlayer();
				if (player != null)
				{
					Quest[] quests = getTemplate().getEventQuests(Quest.QuestEventType.ON_ATTACK);
					if (quests != null)
						for (Quest quest: quests)
							quest.notifyAttack(this, player, damage, attacker instanceof L2Summon, skill);
				}
			}
			catch (Exception e)
			{
				_log.fatal(e.getMessage(), e);
			}
		}
	}

	public void addDamage(L2Character attacker, int damage)
	{
		addDamage(attacker, damage, null);
	}

	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable
	 * _aggroList.<BR>
	 * <BR>
	 * 
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage The number of damages given by the attacker L2Character
	 * @param aggro The hate (=damage) given by the attacker L2Character
	 */
	public void addDamageHate(L2Character attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;

		// Get the AggroInfo of the attacker L2Character from the _aggroList of the L2Attackable
		AggroInfo ai = getAggroListRP().get(attacker);
		if (ai == null)
		{
			ai = new AggroInfo();
			getAggroListRP().put(attacker, ai);

			ai._damage = 0;
			ai._hate = 0;
		}
		ai._damage += damage;
		ai._hate += aggro;

		L2PcInstance targetPlayer = attacker.getActingPlayer();
		if (targetPlayer != null && aggro == 0)
		{
			Quest[] quests = getTemplate().getEventQuests(Quest.QuestEventType.ON_AGGRO_RANGE_ENTER);
			if (quests != null)
				for (Quest quest: quests)
					quest.notifyAggroRangeEnter(this, targetPlayer, (attacker instanceof L2Summon));
		}

		// Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
		if (aggro > 0 && getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	public void reduceHate(L2Character target, int amount)
	{
		if (getAI() instanceof L2SiegeGuardAI || getAI() instanceof L2FortSiegeGuardAI)
		{
			stopHating(target);
			setTarget(null);
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
			return;
		}
		if (target == null) // whole aggrolist
		{
			L2Character mostHated = getMostHated();
			if (mostHated == null) // makes target passive for a moment more
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				return;
			}

			for (L2Character aggroed : getAggroListRP().keySet())
			{
				AggroInfo ai = getAggroListRP().get(aggroed);
				if (ai == null)
					return;
				ai._hate -= amount;
			}

			amount = getHating(mostHated);
			if (amount <= 0)
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				clearAggroList();
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				setWalking();
			}
			return;
		}
		AggroInfo ai = getAggroListRP().get(target);
		if (ai == null)
			return;
		ai._hate -= amount;

		if (ai._hate <= 0)
		{
			if (getMostHated() == null)
			{
				((L2AttackableAI) getAI()).setGlobalAggro(-25);
				clearAggroList();
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				setWalking();
			}
		}
	}

	/**
	 * Clears _aggroList hate of the L2Character without removing from the list.<BR>
	 * <BR>
	 */
	public void stopHating(L2Character target)
	{
		if (target == null)
			return;
		AggroInfo ai = getAggroListRP().get(target);
		if (ai == null)
			return;
		ai._hate = 0;
	}

	/**
	 * Return the most hated L2Character of the L2Attackable _aggroList.<BR>
	 * <BR>
	 */
	public L2Character getMostHated()
	{
		if (getAggroListRP().isEmpty() || isAlikeDead())
			return null;

		L2Character mostHated = null;
		int maxHate = 0;

		// While iterating over this map removing objects is not allowed
		synchronized (getAggroList())
		{
			// Go through the aggroList of the L2Attackable
			for (Map.Entry<L2Character, AggroInfo> entry : getAggroListRP().entrySet())
			{
				L2Character attacker = entry.getKey();
				AggroInfo ai = entry.getValue();
				if (ai == null)
					continue;
				if (attacker.isAlikeDead() || !getKnownList().knowsObject(attacker) || !attacker.isVisible())
					ai._hate = 0;
				if (ai._hate > maxHate)
				{
					mostHated = attacker;
					maxHate = ai._hate;
				}
			}
		}
		return mostHated;
	}

	/**
	 * Return the 2 most hated L2Character of the L2Attackable _aggroList.<BR>
	 * <BR>
	 */
	public L2Character[] get2MostHated()
	{
		if (getAggroListRP().isEmpty() || isAlikeDead())
			return null;

		L2Character mostHated = null;
		L2Character secondMostHated = null;
		int maxHate = 0;
		int secondMaxHate = 0;
		L2Character[] result = new L2Character[2];

		// While iterating over this map removing objects is not allowed
		synchronized (getAggroList())
		{
			// Go through the aggroList of the L2Attackable
			for (Map.Entry<L2Character, AggroInfo> entry : getAggroListRP().entrySet())
			{
				L2Character attacker = entry.getKey();
				AggroInfo ai = entry.getValue();
				if (ai == null)
					continue;
				if (attacker.isAlikeDead() || !getKnownList().knowsObject(attacker) || !attacker.isVisible())
					ai._hate = 0;
				if (ai._hate > maxHate)
				{
					secondMostHated = mostHated;
					secondMaxHate = maxHate;
					mostHated = attacker;
					maxHate = ai._hate;
				}
				else if (ai._hate > secondMaxHate)
				{
					secondMostHated = attacker;
					secondMaxHate = ai._hate;
				}
			}
		}
		result[0] = mostHated;
		if (secondMostHated != null && getAttackByList().contains(secondMostHated))
			result[1] = secondMostHated;

		return result;
	}

	/**
	 * Return the hate level of the L2Attackable against this L2Character
	 * contained in _aggroList.<BR>
	 * <BR>
	 * 
	 * @param target The L2Character whose hate level must be returned
	 */
	public int getHating(L2Character target)
	{
		if (target == null || getAggroListRP().isEmpty())
			return 0;

		AggroInfo ai = getAggroListRP().get(target);
		if (ai == null)
			return 0;
		if (target instanceof L2PcInstance && (((L2PcInstance)target).getAppearance().isInvisible() || target.isInvul()))
		{
			//Remove Object Should Use This Method and Can be Blocked While Interating
			getAggroList().remove(target);
			return 0;
		}
		if (!target.isVisible())
		{
			getAggroList().remove(target);
			return 0;
		}
		if (target.isAlikeDead())
		{
			ai._hate = 0;
			return 0;
		}
		return ai._hate;
	}

	private boolean shouldPunishDeepBlueDrops()
	{
		return (!isRaid() && Config.DEEPBLUE_DROP_RULES)
		|| (isRaid() && Config.DEEPBLUE_DROP_RULES_RAID);
	}

	/**
	 * Calculates quantity of items for specific drop acording to current
	 * situation <br>
	 * 
	 * @param drop The L2DropData count is being calculated for
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param deepBlueDrop Factor to divide the drop chance
	 * @param levelModifier level modifier in %'s (will be subtracted from drop
	 *            chance)
	 */
	private RewardItem calculateRewardItem(L2PcInstance lastAttacker, L2DropData drop, int levelModifier, boolean isSweep)
	{
		// Get default drop chance
		final long dropChance = calculateDropChance(drop.getChance(), drop, isSweep, levelModifier);

		return dropItem(dropChance, drop);
	}

	private static boolean isBossJewel(int itemId)
	{
		switch (itemId)
		{
		case 6656: // Earring of Antharas
		case 6657: // Necklace of Valakas
		case 6658: // Ring of Baium
		case 6659: // Zaken's Earring
		case 6660: // Ring of Queen Ant
		case 6661: // Earring of Orfen
		case 6662: // Ring of Core
		case 8191: // Frintezza's Necklace
		case 10170: // Baylor's Earring
		case 10314: // Beleth's Ring
			return true;
		default:
			return false;
		}
	}

	private long calculateDropChance(double chance, L2DropData drop, boolean isSweep, int levelModifier)
	{
		if (drop != null && drop.getItemId() == PcInventory.ADENA_ID)
		{
			if (this instanceof L2RaidBossInstance)
				chance *= Config.RATE_DROP_ADENA_RAID;
			else if (this instanceof L2GrandBossInstance)
				chance *= Config.RATE_DROP_ADENA_GRAND_BOSS;
			else
				chance *= Config.RATE_DROP_ADENA;
		}
		else if (drop != null && ItemTable.isSealStone(drop.getItemId()))
		{
			chance *= Config.RATE_DROP_SEALSTONE;
		}
		else if (isSweep)
		{
			if (this instanceof L2RaidBossInstance)
				chance *= Config.RATE_DROP_SPOIL_RAID;
			else if (this instanceof L2GrandBossInstance)
				chance *= Config.RATE_DROP_SPOIL_GRAND_BOSS;
			else
				chance *= Config.RATE_DROP_SPOIL;
		}
		else
		{
			if (drop != null && isBossJewel(drop.getItemId()))
				chance *= Config.RATE_DROP_ITEMS_JEWEL;
			else if (this instanceof L2RaidBossInstance)
				chance *= Config.RATE_DROP_ITEMS_RAID;
			else if (this instanceof L2GrandBossInstance)
				chance *= Config.RATE_DROP_ITEMS_GRAND_BOSS;
			else
				chance *= Config.RATE_DROP_ITEMS;
		}

		if (isChampion())
		{
			if (drop != null && drop.getItemId() == PcInventory.ADENA_ID)
				chance *= Config.CHAMPION_ADENA;
			else if (drop != null && ItemTable.isSealStone(drop.getItemId()))
				chance *= Config.CHAMPION_SEALSTONE;
			else
				chance *= Config.CHAMPION_REWARDS;
		}

		if (shouldPunishDeepBlueDrops())
		{
			if (levelModifier > 0)
			{
				// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
				// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
				chance /= 3;
				if (drop != null && drop.getItemId() == PcInventory.ADENA_ID && Config.RATE_DROP_ITEMS != 0)
					chance /= Config.RATE_DROP_ITEMS;
			}

			chance *= (100.0 - levelModifier) / 100.0;
		}

		return (long)chance;
	}

	private RewardItem dropItem(long dropChance, L2DropData drop)
	{
		if (dropChance <= 0)
			return null;

		int multiplier = (int)(dropChance / L2DropData.MAX_CHANCE);

		dropChance %= L2DropData.MAX_CHANCE;

		// Check if the Item must be dropped
		if (Rnd.get(L2DropData.MAX_CHANCE) < dropChance)
			multiplier++;

		if (multiplier <= 0)
			return null;

		// Get min and max Item quantity that can be dropped in one time
		final int minCount = drop.getMinDrop();
		final int maxCount = drop.getMaxDrop();
		// Get the item quantity dropped
		int itemCount;

		if (minCount == maxCount)
			itemCount = minCount * multiplier;
		else if (minCount < maxCount)
			itemCount = Rnd.get(minCount * multiplier, maxCount * multiplier);
		else
			itemCount = multiplier;

		if (itemCount > 1 && !Config.MULTIPLE_ITEM_DROP && !ItemTable.getInstance().getTemplate(drop.getItemId()).isStackable())
			itemCount = 1;

		if (itemCount > 0)
			return new RewardItem(drop.getItemId(), itemCount);
		else
			return null;
	}

	/**
	 * Calculates quantity of items for specific drop CATEGORY according to
	 * current situation <br>
	 * Only a max of ONE item from a category is allowed to be dropped.
	 * 
	 * @param drop The L2DropData count is being calculated for
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param deepBlueDrop Factor to divide the drop chance
	 * @param levelModifier level modifier in %'s (will be subtracted from drop
	 *            chance)
	 */
	private RewardItem calculateCategorizedRewardItem(L2PcInstance lastAttacker, L2DropCategory categoryDrops, int levelModifier)
	{
		if (categoryDrops == null)
			return null;

		// Get default drop chance for the category (that's the sum of chances for all items in the category)
		// keep track of the base category chance as it'll be used later, if an item is drop from the category.
		// for everything else, use the total "categoryDropChance"
		final long categoryDropChance = calculateDropChance(categoryDrops.getCategoryChance(), null, false, levelModifier);

		// Check if an Item from this category must be dropped
		if (Rnd.get(L2DropData.MAX_CHANCE) < categoryDropChance)
		{
			L2DropData drop = categoryDrops.dropOne();
			if (drop == null)
				return null;

			// Now decide the quantity to drop based on the rates and penalties.  To get this value
			// simply divide the modified categoryDropChance by the base category chance.  This
			// results in a chance that will dictate the drops amounts: for each amount over 100
			// that it is, it will give another chance to add to the min/max quantities.
			//
			// For example, If the final chance is 120%, then the item should drop between
			// its min and max one time, and then have 20% chance to drop again.  If the final
			// chance is 330%, it will similarly give 3 times the min and max, and have a 30%
			// chance to give a 4th time.
			// At least 1 item will be dropped for sure.  So the chance will be adjusted to 100%
			// if smaller.

			final long dropChance = calculateDropChance(drop.getChance(), drop, false, levelModifier);

			return dropItem(Math.max(dropChance, L2DropData.MAX_CHANCE), drop);
		}

		return null;
	}

	/**
	 * Calculates the level modifier for drop<br>
	 * 
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 */
	private int calculateLevelModifierForDrop(L2PcInstance lastAttacker)
	{
		if (shouldPunishDeepBlueDrops())
		{
			int highestLevel = lastAttacker.getLevel();

			// Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
			if (getAttackByList() != null && !getAttackByList().isEmpty())
			{
				for (L2Character atkChar : getAttackByList())
					if (atkChar != null && atkChar.getLevel() > highestLevel)
						highestLevel = atkChar.getLevel();
			}

			// According to official data (Prima), deep blue mobs are 9 or more levels below players
			if (highestLevel - 9 >= getLevel())
				return ((highestLevel - (getLevel() + 8)) * 9);
		}

		return 0;
	}

	public void doItemDrop(L2Character lastAttacker)
	{
		doItemDrop(getTemplate(), lastAttacker);
	}

	/**
	 * Manage Base, Quests and Special Events drops of L2Attackable (called by
	 * calculateRewards).<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * During a Special Event all L2Attackable can drop extra Items. Those extra
	 * Items are defined in the table <B>allNpcDateDrops</B> of the
	 * EventDroplist. Each Special Event has a start and end date to stop to
	 * drop extra Items automaticaly. <BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Manage drop of Special Events created by GM for a defined period</li>
	 * <li>Get all possible drops of this L2Attackable from L2NpcTemplate and
	 * add it Quest drops</li> <li>For each possible drops (base + quests),
	 * calculate which one must be dropped (random)</li> <li>Get each Item
	 * quantity dropped (random)</li> <li>Create this or these L2ItemInstance
	 * corresponding to each Item Identifier dropped</li> <li>If the autoLoot
	 * mode is actif and if the L2Character that has killed the L2Attackable is
	 * a L2PcInstance, give this or these Item(s) to the L2PcInstance that has
	 * killed the L2Attackable</li> <li>If the autoLoot mode isn't actif or if
	 * the L2Character that has killed the L2Attackable is not a L2PcInstance,
	 * add this or these Item(s) in the world as a visible object at the
	 * position where mob was last</li><BR>
	 * <BR>
	 * 
	 * @param lastAttacker The L2Character that has killed the L2Attackable
	 */
	public void doItemDrop(L2NpcTemplate npcTemplate, L2Character lastAttacker)
	{
		if (lastAttacker == null)
			return;

		L2PcInstance player = lastAttacker.getActingPlayer();

		if (player == null)
			return; // Don't drop anything if the last attacker or ownere isn't L2PcInstance

		int levelModifier = calculateLevelModifierForDrop(player); // level modifier in %'s (will be subtracted from drop chance)

		// Check the drop of a cursed weapon
		CursedWeaponsManager.getInstance().checkDrop(this, player);

		// now throw all categorized drops and handle spoil.
		if (npcTemplate.getDropData() != null)
		{
			for (L2DropCategory cat : npcTemplate.getDropData())
			{
				RewardItem item = null;
				if (cat.isSweep())
				{
					// according to sh1ny, seeded mobs CAN be spoiled and swept.
					if (isSpoil()/* && !isSeeded() */)
					{
						LinkedBunch<RewardItem> sweepList = new LinkedBunch<RewardItem>();

						for (L2DropData drop : cat.getAllDrops())
						{
							item = calculateRewardItem(player, drop, levelModifier, true);
							if (item == null)
								continue;

							if (_log.isDebugEnabled())
								_log.info("Item id to spoil: " + item.getItemId() + " amount: " + item.getCount());
							sweepList.add(item);
						}

						// Set the table _sweepItems of this L2Attackable
						if (!sweepList.isEmpty())
							_sweepItems = sweepList.moveToArray(new RewardItem[sweepList.size()]);
					}
				}
				else
				{
					if (isSeeded())
					{
						L2DropData drop = cat.dropSeedAllowedDropsOnly();
						if (drop == null)
							continue;

						item = calculateRewardItem(player, drop, levelModifier, false);
					}
					else
					{
						item = calculateCategorizedRewardItem(player, cat, levelModifier);
					}

					if (item != null)
					{
						if (_log.isDebugEnabled())
							_log.info("Item id to drop: " + item.getItemId() + " amount: " + item.getCount());

						// Looting process
						player.doAutoLoot(this, item);

						// Broadcast message if RaidBoss was defeated
						if (this instanceof L2Boss)
						{
							SystemMessage sm = new SystemMessage(SystemMessageId.C1_DIED_DROPPED_S3_S2);
							sm.addCharName(this);
							sm.addItemName(item.getItemId());
							sm.addItemNumber(item.getCount());
							broadcastPacket(sm);
						}
					}
				}
			}
		}

		// Apply Special Item drop with rnd qty for champions
		if (isChampion() && Math.abs(getLevel() - player.getLevel()) <= Config.CHAMPION_SPCL_LVL_DIFF && Config.CHAMPION_SPCL_CHANCE > 0
				&& Rnd.get(100) < Config.CHAMPION_SPCL_CHANCE)
		{
			int champqty = Rnd.get(Config.CHAMPION_SPCL_QTY) + 1; //quantity should actually vary between 1 and whatever admin specified as max, inclusive.

			// Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
			RewardItem item = new RewardItem(Config.CHAMPION_SPCL_ITEM, champqty);

			player.doAutoLoot(this, item);
		}

		//Instant Item Drop :>
		if (getTemplate().dropHerbs())
		{
			boolean _hp = false;
			boolean _mp = false;
			boolean _spec = false;

			//ptk - patk type enhance
			int random = Rnd.get(1000); // note *10
			if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec) // && !_spec useless yet
			{
				RewardItem item = new RewardItem(8612, 1); // Herb of Warrior

				player.doAutoLoot(this, item);
				_spec = true;
			}
			else
				for (int i = 0; i < 5; i++)
				{
					random = Rnd.get(100);
					if (random < Config.RATE_DROP_COMMON_HERBS)
					{
						RewardItem item = null;
						switch (i)
						{
						case 0:
							item = new RewardItem(8606, 1); // Herb of Power
							break;
						case 1:
							item = new RewardItem(8608, 1); // Herb of Atk. Spd.
							break;
						case 2:
							item = new RewardItem(8610, 1); // Herb of Critical Attack - Rate
							break;
						case 3:
							item = new RewardItem(10655, 1); // Herb of Life Force Absorption
							break;
						default:
							item = new RewardItem(10656, 1); // Herb of Critical Attack - Power
							break;
						}

						player.doAutoLoot(this, item);
						break;
					}
				}
			//mtk - matk type enhance
			random = Rnd.get(1000); // note *10
			if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec)
			{
				RewardItem item = new RewardItem(8613, 1); // Herb of Mystic

				player.doAutoLoot(this, item);
				_spec = true;
			}
			else
				for (int i = 0; i < 2; i++)
				{
					random = Rnd.get(100);
					if (random < Config.RATE_DROP_COMMON_HERBS)
					{
						RewardItem item = null;

						if (i == 0)
							item = new RewardItem(8607, 1); // Herb of Magic
						else
							item = new RewardItem(8609, 1); // Herb of Casting Speed

						player.doAutoLoot(this, item);
						break;
					}
				}
			//hp+mp type
			random = Rnd.get(1000); // note *10
			if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec)
			{
				RewardItem item = new RewardItem(8614, 1); // Herb of Recovery

				player.doAutoLoot(this, item);
				_mp = true;
				_hp = true;
				_spec = true;
			}
			//hp - restore hp type
			if (!_hp)
			{
				random = Rnd.get(100);
				if (random < Config.RATE_DROP_MP_HP_HERBS)
				{
					RewardItem item = new RewardItem(8600, 1); // Herb of Life

					player.doAutoLoot(this, item);
					_hp = true;
				}
			}
			if (!_hp)
			{
				random = Rnd.get(100);
				if (random < Config.RATE_DROP_GREATER_HERBS)
				{
					RewardItem item = new RewardItem(8601, 1); // Greater Herb of Life

					player.doAutoLoot(this, item);
					_hp = true;
				}
			}
			if (!_hp)
			{
				random = Rnd.get(1000); // note *10
				if (random < Config.RATE_DROP_SUPERIOR_HERBS)
				{
					RewardItem item = new RewardItem(8602, 1); // Superior Herb of Life

					player.doAutoLoot(this, item);
				}
			}
			//mp - restore mp type
			if (!_mp)
			{
				random = Rnd.get(100);
				if (random < Config.RATE_DROP_MP_HP_HERBS)
				{
					RewardItem item = new RewardItem(8603, 1); // Herb of Manna

					player.doAutoLoot(this, item);
					_mp = true;
				}
			}
			if (!_mp)
			{
				random = Rnd.get(100);
				if (random < Config.RATE_DROP_GREATER_HERBS)
				{
					RewardItem item = new RewardItem(8604, 1); // Greater Herb of Mana

					player.doAutoLoot(this, item);
					_mp = true;
				}
			}
			if (!_mp)
			{
				random = Rnd.get(1000); // note *10
				if (random < Config.RATE_DROP_SUPERIOR_HERBS)
				{
					RewardItem item = new RewardItem(8605, 1); // Superior Herb of Mana

					player.doAutoLoot(this, item);
				}
			}
			// speed enhance type
			random = Rnd.get(100);
			if (random < Config.RATE_DROP_COMMON_HERBS)
			{
				RewardItem item = new RewardItem(8611, 1); // Herb of Speed

				player.doAutoLoot(this, item);
			}
			// Enlarge Head type
			random = Rnd.get(100);
			if (random < Config.RATE_DROP_COMMON_HERBS)
			{
				RewardItem item = new RewardItem(10657, 1); // Herb of Doubt

				player.doAutoLoot(this, item);
			}
			// Vitality Herb
			if (Config.ENABLE_VITALITY && Config.ENABLE_DROP_VITALITY_HERBS)
			{
				random = Rnd.get(100);
				if (random < Config.RATE_DROP_VITALITY_HERBS)
				{
					RewardItem item = new RewardItem(13028, 1);

					player.doAutoLoot(this, item);
				}
			}
		}
	}

	/**
	 * Manage Special Events drops created by GM for a defined period.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * During a Special Event all L2Attackable can drop extra Items. Those extra
	 * Items are defined in the table <B>allNpcDateDrops</B> of the
	 * EventDroplist. Each Special Event has a start and end date to stop to
	 * drop extra Items automaticaly. <BR>
	 * <BR>
	 * <B><U> Actions</U> : <I>If an extra drop must be generated</I></B><BR>
	 * <BR>
	 * <li>Get an Item Identifier (random) from the DateDrop Item table of this
	 * Event</li> <li>Get the Item quantity dropped (random)</li> <li>Create
	 * this or these L2ItemInstance corresponding to this Item Identifier</li>
	 * <li>If the autoLoot mode is actif and if the L2Character that has killed
	 * the L2Attackable is a L2PcInstance, give this or these Item(s) to the
	 * L2PcInstance that has killed the L2Attackable</li> <li>If the autoLoot
	 * mode isn't actif or if the L2Character that has killed the L2Attackable
	 * is not a L2PcInstance, add this or these Item(s) in the world as a
	 * visible object at the position where mob was last</li><BR>
	 * <BR>
	 * 
	 * @param lastAttacker The L2Character that has killed the L2Attackable
	 */
	public void doEventDrop(L2Character lastAttacker)
	{
		L2PcInstance player = null;
		if (lastAttacker instanceof L2PcInstance)
			player = (L2PcInstance) lastAttacker;
		else if (lastAttacker instanceof L2Summon)
			player = ((L2Summon) lastAttacker).getOwner();
		else if (lastAttacker instanceof L2Trap)
			player = ((L2Trap) lastAttacker).getOwner();

		if (player == null)
			return; // Don't drop anything if the last attacker or ownere isn't L2PcInstance

		if (player.getLevel() - getLevel() > 9)
			return;

		// Go through DateDrop of EventDroplist allNpcDateDrops within the date range
		for (DateDrop drop : EventDroplist.getInstance().getAllDrops())
		{
			if (Rnd.get(L2DropData.MAX_CHANCE) < drop.chance)
			{
				RewardItem item = new RewardItem(drop.items[Rnd.get(drop.items.length)], Rnd.get(drop.min, drop.max));

				player.doAutoLoot(this, item);
			}
		}
	}

	/**
	 * Drop reward item.<BR>
	 * <BR>
	 */
	public L2ItemInstance dropItem(L2PcInstance lastAttacker, RewardItem item)
	{
		int randDropLim = 70;

		L2ItemInstance ditem = null;
		for (int i = 0; i < item.getCount(); i++)
		{
			// Randomize drop position
			int newX = getX() + Rnd.get(randDropLim * 2 + 1) - randDropLim;
			int newY = getY() + Rnd.get(randDropLim * 2 + 1) - randDropLim;

			//FIXME: temp hack, do something nicer when we have geodatas
			int newZ = Math.max(getZ(), lastAttacker.getZ()) + 20;

			if (ItemTable.getInstance().getTemplate(item.getItemId()) != null)
			{
				// Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
				ditem = ItemTable.getInstance().createItem("Loot", item.getItemId(), item.getCount(), lastAttacker, this);
				ditem.dropMe(this, newX, newY, newZ);

				// Add drop to auto destroy item task
				ItemsAutoDestroy.tryAddItem(ditem);

				ditem.setProtected(false);
				// If stackable, end loop as entire count is included in 1 instance of item
				if (ditem.isStackable() || !Config.MULTIPLE_ITEM_DROP)
					break;
			}
			//if item doesn't exist....
			else
			{
				_log.error("Item doesn't exist so cannot be dropped. Item ID: " + item.getItemId());
			}
		}
		return ditem;
	}

	public L2ItemInstance dropItem(L2PcInstance lastAttacker, int itemId, int itemCount)
	{
		return dropItem(lastAttacker, new RewardItem(itemId, itemCount));
	}

	/**
	 * Return the active weapon of this L2Attackable (= null).<BR>
	 * <BR>
	 */
	public L2ItemInstance getActiveWeapon()
	{
		return null;
	}

	/**
	 * Return True if the _aggroList of this L2Attackable is Empty.<BR>
	 * <BR>
	 */
	public boolean noTarget()
	{
		return getAggroListRP().isEmpty();
	}

	/**
	 * Return True if the _aggroList of this L2Attackable contains the
	 * L2Character.<BR>
	 * <BR>
	 * 
	 * @param player The L2Character searched in the _aggroList of the
	 *            L2Attackable
	 */
	public boolean containsTarget(L2Character player)
	{
		return getAggroListRP().containsKey(player);
	}

	/**
	 * Clear the _aggroList of the L2Attackable.<BR>
	 * <BR>
	 */
	public void clearAggroList()
	{
		getAggroList().clear();
	}

	/**
	 * Return True if a Dwarf use Sweep on the L2Attackable and if item can be
	 * spoiled.<BR>
	 * <BR>
	 */
	public boolean isSweepActive()
	{
		return _sweepItems != null;
	}

	/**
	 * Return table containing all L2ItemInstance that can be spoiled.<BR>
	 * <BR>
	 */
	public synchronized RewardItem[] takeSweep()
	{
		RewardItem[] sweep = _sweepItems;

		_sweepItems = null;

		return sweep;
	}

	/**
	 * Return table containing all L2ItemInstance that can be harvested.<BR>
	 * <BR>
	 */
	public synchronized RewardItem[] takeHarvest()
	{
		RewardItem[] harvest = _harvestItems;
		_harvestItems = null;
		return harvest;
	}

	/**
	 * Set the over-hit flag on the L2Attackable.<BR>
	 * <BR>
	 * 
	 * @param status The status of the over-hit flag
	 */
	public void overhitEnabled(boolean status)
	{
		_overhit = status;
	}

	/**
	 * Set the over-hit values like the attacker who did the strike and the
	 * ammount of damage done by the skill.<BR>
	 * <BR>
	 * 
	 * @param attacker The L2Character who hit on the L2Attackable using the
	 *            over-hit enabled skill
	 * @param damage The ammount of damage done by the over-hit enabled skill on
	 *            the L2Attackable
	 */
	public void setOverhitValues(L2Character attacker, double damage)
	{
		// Calculate the over-hit damage
		// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
		double overhitDmg = ((getStatus().getCurrentHp() - damage) * (-1));
		if (overhitDmg < 0)
		{
			// we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
			// let's just clear all the over-hit related values
			overhitEnabled(false);
			_overhitDamage = 0;
			_overhitAttacker = null;
			return;
		}
		overhitEnabled(true);
		_overhitDamage = overhitDmg;
		_overhitAttacker = attacker;
	}

	/**
	 * Return the L2Character who hit on the L2Attackable using an over-hit
	 * enabled skill.<BR>
	 * <BR>
	 * 
	 * @return L2Character attacker
	 */
	public L2Character getOverhitAttacker()
	{
		return _overhitAttacker;
	}

	public void setOverhitAttacker(L2Character cha)
	{
		_overhitAttacker = cha;
	}

	/**
	 * Return the ammount of damage done on the L2Attackable using an over-hit
	 * enabled skill.<BR>
	 * <BR>
	 * 
	 * @return double damage
	 */
	public double getOverhitDamage()
	{
		return _overhitDamage;
	}

	/**
	 * Return True if the L2Attackable was hit by an over-hit enabled skill.<BR>
	 * <BR>
	 */
	public boolean isOverhit()
	{
		return _overhit;
	}

	/**
	 * Activate the absorbed soul condition on the L2Attackable.<BR>
	 * <BR>
	 */
	public void absorbSoul()
	{
		_absorbed = true;

	}

	/**
	 * Return True if the L2Attackable had his soul absorbed.<BR>
	 * <BR>
	 */
	public boolean isAbsorbed()
	{
		return _absorbed;
	}

	/**
	 * Adds an attacker that successfully absorbed the soul of this L2Attackable
	 * into the _absorbersList.<BR>
	 * <BR>
	 * params: attacker - a valid L2PcInstance condition - an integer indicating
	 * the event when mob dies. This should be: = 0 - "the crystal scatters"; =
	 * 1 - "the crystal failed to absorb. nothing happens"; = 2 -
	 * "the crystal resonates because you got more than 1 crystal on you"; = 3 -
	 * "the crystal cannot absorb the soul because the mob level is too low"; =
	 * 4 - "the crystal successfuly absorbed the soul";
	 */
	public void addAbsorber(L2PcInstance attacker, int crystalId)
	{
		// This just works for targets like L2MonsterInstance
		if (!(this instanceof L2MonsterInstance))
			return;

		// The attacker must not be null
		if (attacker == null)
			return;

		// This L2Attackable must be of one type in the _absorbingMOBS_levelXX tables.
		// OBS: This is done so to avoid triggering the absorbed conditions for mobs that can't be absorbed.
		if (getAbsorbLevel() == 0)
			return;

		// If we have no _absorbersList initiated, do it
		AbsorberInfo ai = _absorbersList.get(attacker);

		// If the L2Character attacker isn't already in the _absorbersList of this L2Attackable, add it
		if (ai == null)
		{
			ai = new AbsorberInfo(crystalId, getStatus().getCurrentHp());
			_absorbersList.put(attacker, ai);
		}
		else
		{
			ai._crystalId = crystalId;
			ai._absorbedHP = getStatus().getCurrentHp();
		}

		// Set this L2Attackable as absorbed
		absorbSoul();
	}

	/**
	 * Calculate the leveling chance of Soul Crystals based on the attacker that
	 * killed this L2Attackable
	 * 
	 * @param attacker The player that last killed this L2Attackable $ Rewrite
	 *            06.12.06 - Yesod
	 */
	private void levelSoulCrystals(L2Character attacker)
	{
		// Only L2PcInstance can absorb a soul
		if (!(attacker instanceof L2Playable))
		{
			resetAbsorbList();
			return;
		}

		int maxAbsorbLevel = getAbsorbLevel();
		int minAbsorbLevel = 0;

		// If this is not a valid L2Attackable, clears the _absorbersList and just return
		if (maxAbsorbLevel == 0)
		{
			resetAbsorbList();
			return;
		}
		// All boss mobs with maxAbsorbLevel 13 have minAbsorbLevel of 12 else 10
		if (maxAbsorbLevel > 10)
			minAbsorbLevel = maxAbsorbLevel > 12 ? 12 : 10;

			//Init some useful vars
			boolean isSuccess = true;
			boolean doLevelup = true;
			boolean isBossMob = maxAbsorbLevel > 10;

			L2NpcTemplate.AbsorbCrystalType absorbType = getTemplate().getAbsorbType();

			L2PcInstance killer = (attacker instanceof L2Summon) ? ((L2Summon) attacker).getOwner() : (L2PcInstance) attacker;

			// If this mob is a boss, then skip some checkings
			if (!isBossMob)
			{
				// Fail if this L2Attackable isn't absorbed or there's no one in its _absorbersList
				if (!isAbsorbed() /*|| _absorbersList == null*/)
				{
					resetAbsorbList();
					return;
				}

				// Fail if the killer isn't in the _absorbersList of this L2Attackable and mob is not boss
				AbsorberInfo ai = _absorbersList.get(killer);
				if (ai == null)
					isSuccess = false;

				// Check if the soul crystal was used when HP of this L2Attackable wasn't higher than half of it
				if (ai != null && ai._absorbedHP > (getMaxHp() / 2.0))
					isSuccess = false;

				if (!isSuccess)
				{
					resetAbsorbList();
					return;
				}
			}

			// ********
			String[] crystalNFO = null;

			int dice = Rnd.get(100);
			int crystalQTY = 0;
			int crystalLVL = 0;
			int crystalOLD = 0;
			int crystalNEW = 0;

			// ********
			// Now we have four choices:
			// 1- The Monster level is too low for the crystal. Nothing happens.
			// 2- Everything is correct, but it failed. Nothing happens. (57.5%)
			// 3- Everything is correct, the crystal level up. A sound event is played. (32.5%)

			List<L2PcInstance> players;

			if (absorbType == L2NpcTemplate.AbsorbCrystalType.FULL_PARTY && killer.isInParty())
				players = killer.getParty().getPartyMembers();
			else if (absorbType == L2NpcTemplate.AbsorbCrystalType.PARTY_ONE_RANDOM && killer.isInParty())
			{
				// This is a naive method for selecting a random member.  It gets any random party member and
				// then checks if the member has a valid crystal.  It does not select the random party member
				// among those who have crystals, only.  However, this might actually be correct (same as retail).
				players = Collections.singletonList(killer.getParty().getPartyMembers().get(Rnd.get(killer.getParty().getMemberCount())));
			}
			else
				players = Collections.singletonList(killer);

			for (L2PcInstance player : players)
			{
				if (player == null)
					continue;
				QuestState st = player.getQuestState("350_EnhanceYourWeapon");
				if (st == null)
					continue;
				if (st.getState() != State.STARTED)
					continue;

				crystalQTY = 0;

				L2ItemInstance[] inv = player.getInventory().getItems();
				for (L2ItemInstance item : inv)
				{
					int itemId = item.getItemId();
					for (int id : SoulCrystal.SoulCrystalTable)
					{
						// Find any of the 39 possible crystals.
						if (id == itemId)
						{
							// Keep count but make sure the player has no more than 1 crystal
							if (++crystalQTY > 1)
							{
								isSuccess = false;
								break;
							}

							// Validate if the crystal has already leveled
							if (id != SoulCrystal.RED_NEW_CRYSTAL && id != SoulCrystal.GRN_NEW_CYRSTAL && id != SoulCrystal.BLU_NEW_CRYSTAL)
							{
								try
								{
									if (item.getItem().getName().contains("Grade"))
									{
										// Split the name of the crystal into 'name' & 'level'
										crystalNFO = item.getItem().getName().trim().replace(" Grade ", "-").split("-");
										// Set Level to 13
										crystalLVL = 13;
									}
									else
									{
										// Split the name of the crystal into 'name' & 'level'
										crystalNFO = item.getItem().getName().trim().replace(" Stage ", "").split("-");
										// Get Level
										crystalLVL = Integer.parseInt(crystalNFO[1].trim());
									}
									// Allocate current and levelup ids' for higher level crystals
									if (crystalLVL > 9)
									{
										for (int[] element : SoulCrystal.HighSoulConvert)
										{
											// Get the next stage above 10 using array.
											if (id == element[0])
											{
												crystalNEW = element[1];
												break;
											}
										}
									}
									else
										crystalNEW = id + 1;
								}
								catch (NumberFormatException nfe)
								{
									_log.warn("An attempt to identify a soul crystal failed, " + "verify the names have not changed in etcitem " + "table.", nfe);

									player.sendMessage("There has been an error handling your soul crystal." + " Please notify your server admin.");

									isSuccess = false;
									break;
								}
								catch (Exception e)
								{
									_log.warn(e.getMessage(), e);
									isSuccess = false;
									break;
								}
							}
							else
							{
								crystalNEW = id + 1;
							}

							// Done
							crystalOLD = id;
							break;
						}
					}
					if (!isSuccess)
						break;
				}

				// If the crystal level is way too high for this mob, say that we can't increase it
				if ((crystalLVL < minAbsorbLevel) || (crystalLVL >= maxAbsorbLevel))
					doLevelup = false;

				// The player doesn't have any crystals with him get to the next player.
				if (crystalQTY != 1 || !isSuccess || !doLevelup)
				{
					// Too many crystals in inventory.
					if (crystalQTY > 1)
					{
						player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
					}
					// The soul crystal stage of the player is way too high
					else if (!doLevelup && crystalQTY > 0)
						player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);

					crystalQTY = 0;
					continue;
				}

				/* TODO: Confirm boss chance for crystal level up and for crystal breaking.
				 * It is known that bosses with FULL_PARTY crystal level ups have 100% success rate, but this is not
				 * the case for the other bosses (one-random or last-hit).
				 * While not confirmed, it is most reasonable that crystals leveled up at bosses will never break.
				 * Also, the chance to level up is guessed as around 70% if not higher.
				 */
				int chanceLevelUp = isBossMob ? 70 : SoulCrystal.LEVEL_CHANCE;

				// If succeeds or it is a full party absorb, level up the crystal.
				if (((absorbType == L2NpcTemplate.AbsorbCrystalType.FULL_PARTY) && doLevelup) || (dice <= chanceLevelUp))
				{
					// Give staged crystal
					exchangeCrystal(player, crystalOLD, crystalNEW, false);
				}
				else
					player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED);
			}
	}

	private void exchangeCrystal(L2PcInstance player, int takeid, int giveid, boolean broke)
	{
		L2ItemInstance Item = player.getInventory().destroyItemByItemId("SoulCrystal", takeid, 1, player, this);
		if (Item != null)
		{
			// Prepare inventory update packet
			InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addRemovedItem(Item);

			// Add new crystal to the killer's inventory
			Item = player.getInventory().addItem("SoulCrystal", giveid, 1, player, this);
			playerIU.addItem(Item);

			// Send a sound event and text message to the player
			if (broke)
			{
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_BROKE);
			}
			else
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_SUCCEEDED);

			// Send system message
			SystemMessage sms = new SystemMessage(SystemMessageId.EARNED_S1);
			sms.addItemName(giveid);
			player.sendPacket(sms);

			// Send inventory update packet
			player.sendPacket(playerIU);
		}
	}

	public void resetAbsorbList()
	{
		_absorbed = false;
		_absorbersList.clear();
	}

	/**
	 * Calculate the Experience and SP to distribute to attacker (L2PcInstance,
	 * L2SummonInstance or L2Party) of the L2Attackable.<BR>
	 * <BR>
	 * 
	 * @param diff The difference of level between attacker (L2PcInstance,
	 *            L2SummonInstance or L2Party) and the L2Attackable
	 * @param damage The damages given by the attacker (L2PcInstance,
	 *            L2SummonInstance or L2Party)
	 */
	private int[] calculateExpAndSp(int diff, int damage)
	{
		double xp;
		double sp;

		if (diff < -5)
			diff = -5; // makes possible to use ALT_GAME_EXPONENT configuration
		xp = (double) getExpReward() * damage / getMaxHp();
		if (Config.ALT_GAME_EXPONENT_XP != 0)
			xp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_XP);

		sp = (double) getSpReward() * damage / getMaxHp();
		if (Config.ALT_GAME_EXPONENT_SP != 0)
			sp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_SP);

		if (Config.ALT_GAME_EXPONENT_XP == 0 && Config.ALT_GAME_EXPONENT_SP == 0)
		{
			if (diff > 5) // formula revised May 07
			{
				double pow = L2Math.pow((double) 5 / 6, diff - 5);
				xp = xp * pow;
				sp = sp * pow;
			}

			if (xp <= 0)
			{
				xp = 0;
				sp = 0;
			}
			else if (sp <= 0)
			{
				sp = 0;
			}
		}

		int[] tmp =
		{ (int) xp, (int) sp };

		return tmp;
	}

	public long calculateOverhitExp(long normalExp)
	{
		// Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the L2Attackable
		double overhitPercentage = ((getOverhitDamage() * 100) / getMaxHp());

		// Over-hit damage percentages are limited to 25% max
		if (overhitPercentage > 25)
			overhitPercentage = 25;

		// Get the overhit exp bonus according to the above over-hit damage percentage
		// (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
		double overhitExp = ((overhitPercentage / 100) * normalExp);

		// Return the rounded ammount of exp points to be added to the player's normal exp reward
		long bonusOverhit = Math.round(overhitExp);
		return bonusOverhit;
	}

	/**
	 * Return True.<BR>
	 * <BR>
	 */
	@Override
	public boolean isAttackable()
	{
		return true;
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
		// Clear mob spoil,seed
		setSpoil(false);
		// Clear all aggro char from list
		clearAggroList();
		// Clear Harvester Rewrard List
		_harvestItems = null;
		// Clear mod Seeded stat
		setSeeded(false);

		_sweepItems = null;
		resetAbsorbList();

		setWalking();
		// check the region where this mob is, do not activate the AI if region is inactive.
		if (!isInActiveRegion())
		{
			getAI().stopAITask();
		}
	}

	/**
	 * Sets state of the mob to seeded. Paramets needed to be set before.
	 */
	public void setSeeded()
	{
		if (_seedType != 0 && _seeder != null)
			setSeeded(_seedType, _seeder.getLevel());
	}

	/**
	 * Sets the seed parametrs, but not the seed state
	 * 
	 * @param id - id of the seed
	 * @param seeder - player who is sowind the seed
	 */
	public void setSeeded(int id, L2PcInstance seeder)
	{
		if (!_seeded)
		{
			_seedType = id;
			_seeder = seeder;
		}
	}

	/**
	 * @param id
	 * @param seederLvl
	 */
	public void setSeeded(int id, int seederLvl)
	{
		_seeded = true;
		_seedType = id;
		int count = 1;

		{
			for (L2Skill skill : getAllSkills())
			{
				switch (skill.getId())
				{
				case 4303: //Strong type x2
					count *= 2;
					break;
				case 4304: //Strong type x3
					count *= 3;
					break;
				case 4305: //Strong type x4
					count *= 4;
					break;
				case 4306: //Strong type x5
					count *= 5;
					break;
				case 4307: //Strong type x6
					count *= 6;
					break;
				case 4308: //Strong type x7
					count *= 7;
					break;
				case 4309: //Strong type x8
					count *= 8;
					break;
				case 4310: //Strong type x9
					count *= 9;
					break;
				}
			}
		}

		int diff = (getLevel() - (L2Manor.getInstance().getSeedLevel(_seedType) - 5));

		// hi-lvl mobs bonus
		if (diff > 0)
			count += diff;

		_harvestItems = new RewardItem[] {
				new RewardItem(L2Manor.getInstance().getCropType(_seedType), count * Config.RATE_DROP_MANOR) };
	}

	public void setSeeded(boolean seeded)
	{
		_seeded = seeded;
	}

	public L2PcInstance getSeeder()
	{
		return _seeder;
	}

	public void setSeeder(L2PcInstance player)
	{
		_seeder = player;
	}

	public int getSeedType()
	{
		return _seedType;
	}

	public boolean isSeeded()
	{
		return _seeded;
	}

	private int getAbsorbLevel()
	{
		return getTemplate().getAbsorbLevel();
	}

	/**
	 * Check if the server allows Random Animation.<BR>
	 * <BR>
	 */
	// This is located here because L2Monster and L2FriendlyMob both extend this class. The other non-pc instances extend either L2NpcInstance or L2MonsterInstance.
	@Override
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_MONSTER_ANIMATION > 0);
	}

	@Override
	public boolean isMob()
	{
		return true; // This means we use MAX_MONSTER_ANIMATION instead of MAX_NPC_ANIMATION
	}

	protected void setCommandChannelTimer(CommandChannelTimer commandChannelTimer)
	{
		_commandChannelTimer = commandChannelTimer;
	}

	public CommandChannelTimer getCommandChannelTimer()
	{
		return _commandChannelTimer;
	}

	public L2CommandChannel getFirstCommandChannelAttacked()
	{
		return _firstCommandChannelAttacked;
	}

	public void setFirstCommandChannelAttacked(L2CommandChannel firstCommandChannelAttacked)
	{
		_firstCommandChannelAttacked = firstCommandChannelAttacked;
	}

	private class CommandChannelTimer implements Runnable
	{
		private final L2CommandChannel _channel;

		public CommandChannelTimer(L2CommandChannel channel)
		{
			_channel = channel;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			setCommandChannelTimer(null);
			setFirstCommandChannelAttacked(null);
			for (L2Character player : getAggroListRP().keySet())
			{
				if (player.isInParty() && player.getParty().isInCommandChannel())
				{
					if (player.getParty().getCommandChannel().equals(_channel))
					{
						// if a player which is in first attacked CommandChannel, restart the timer ;)
						setCommandChannelTimer(this);
						setFirstCommandChannelAttacked(_channel);
						ThreadPoolManager.getInstance().scheduleGeneral(this, 300000); // 5 min
						break;
					}
				}
			}
		}
	}

	/*
	 * Return vitality points decrease (if positive)
	 * or increase (if negative) based on damage.
	 * Maximum for damage = maxHp.
	 */
	public float getVitalityPoints(int damage)
	{
		// sanity check
		if (damage <= 0)
			return 0;

		final float divider = getTemplate().getBaseVitalityDivider();
		if (divider == 0)
			return 0;

		// negative value - vitality will be consumed
		return - Math.min(damage, getMaxHp()) / divider;
	}

	/*
	 * True if vitality rate for exp and sp should be applied
	 */
	public boolean useVitalityRate()
	{
		if (isChampion() && !Config.ENABLE_VITALITY_CHAMPION)
			return false;

		return true;
	}

	public boolean hasBeenAttacked()
	{
		return _beenAttacked;
	}

	public void setBeenAttacked(boolean has)
	{
		_beenAttacked = has;
	}

	public int getFleeingStatus()
	{
		return _fleeing;
	}

	public void setFleeingStatus(int fleeing)
	{
		_fleeing = fleeing;
	}

	public L2CharPosition getMoveAroundPos()
	{
		return _moveAroundPos;
	}

	public void setMoveAroundPos(L2CharPosition pos)
	{
		_moveAroundPos = pos;
	}

	public void setMagicBottled(boolean bottled, float percent)
	{
		_bottled = bottled;
		_hpWhenBottled = percent;
	}

	public boolean isMagicBottled()
	{
		return _bottled;
	}
}
