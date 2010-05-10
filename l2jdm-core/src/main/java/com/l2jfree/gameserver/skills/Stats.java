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

import java.util.NoSuchElementException;

/**
 * Enum of basic stats.
 * 
 * @author mkizub
 */
public enum Stats
{
	//
	// Base stats, for each in Calculator a slot is allocated
	//
	
	// HP & MP
	MAX_HP("maxHp"),
	MAX_MP("maxMp"),
	MAX_CP("maxCp"),
	REGENERATE_HP_RATE("regHp"),
	REGENERATE_CP_RATE("regCp"),
	REGENERATE_MP_RATE("regMp"),
	RECHARGE_MP_RATE("gainMp"),
	HEAL_EFFECTIVNESS("gainHp"),
	HEAL_PROFICIENCY("giveHp"),
	HEAL_STATIC_BONUS("bonusHp"),
	
	// Atk & Def
	POWER_DEFENCE("pDef"),
	MAGIC_DEFENCE("mDef"),
	POWER_ATTACK("pAtk"),
	MAGIC_ATTACK("mAtk"),
	POWER_ATTACK_SPEED("pAtkSpd"),
	MAGIC_ATTACK_SPEED("mAtkSpd"), // how fast a spell is casted (including animation)
	SHIELD_DEFENCE("sDef"),
	CRITICAL_DAMAGE("cAtk"),
	CRITICAL_DAMAGE_ADD("cAtkAdd"), // this is another type for special critical damage mods - vicious stance, crit power and crit damage SA
	PVP_PHYSICAL_DMG("pvpPhysDmg"),
	PVP_MAGICAL_DMG("pvpMagicalDmg"),
	PVP_PHYS_SKILL_DMG("pvpPhysSkillsDmg"),
	PVP_PHYSICAL_DEF("pvpPhysDef"),
	PVP_MAGICAL_DEF("pvpMagicalDef"),
	PVP_PHYS_SKILL_DEF("pvpPhysSkillsDef"),
	// Atk & Def rates
	EVASION_RATE("rEvas"),
	P_SKILL_EVASION("pSkillEvas"),
	SHIELD_RATE("rShld"),
	SHIELD_ANGLE("shldAngle"),
	CRITICAL_RATE("rCrit"),
	BLOW_RATE("blowRate"),
	LETHAL_RATE("lethalRate"),
	MCRITICAL_RATE("mCritRate"),
	EXPSP_RATE("rExp"),
	ATTACK_CANCEL("cancel"),
	// Accuracy and range
	ACCURACY_COMBAT("accCombat"),
	POWER_ATTACK_RANGE("pAtkRange"),
	MAGIC_ATTACK_RANGE("mAtkRange"),
	POWER_ATTACK_ANGLE("pAtkAngle"),
	ATTACK_COUNT_MAX("atkCountMax"),
	MAGIC_FAIL_RATE("mFailRate"),
	
	// Run speed,
	// walk & escape speed are calculated proportionally,
	// magic speed is a buff
	RUN_SPEED("runSpd"),
	WALK_SPEED("walkSpd"),
	
	//
	// Player-only stats
	//
	STAT_STR("STR"),
	STAT_CON("CON"),
	STAT_DEX("DEX"),
	STAT_INT("INT"),
	STAT_WIT("WIT"),
	STAT_MEN("MEN"),
	
	// Unused until DP is updated
	LOST_EXP("lostExp"),
	LOST_EXP_PVP("lostExpPvp"),
	//
	// Special stats, share one slot in Calculator
	//
	
	// stats of various abilities
	BREATH("breath"),
	//
	//
	//AGGRESSION_VULN("aggressionVuln"),
	BLEED_VULN("bleedVuln"),
	POISON_VULN("poisonVuln"),
	STUN_VULN("stunVuln"),
	PARALYZE_VULN("paralyzeVuln"),
	ROOT_VULN("rootVuln"),
	SLEEP_VULN("sleepVuln"),
	CONFUSION_VULN("confusionVuln"),
	FIRE_RES("fireRes"),
	WIND_RES("windRes"),
	WATER_RES("waterRes"),
	EARTH_RES("earthRes"),
	HOLY_RES("holyRes"),
	DARK_RES("darkRes"),
	CANCEL_VULN("cancelVuln"), // Resistance for cancel type skills
	DERANGEMENT_VULN("derangementVuln"),
	DEBUFF_VULN("debuffVuln"),
	FALL_VULN("fallVuln"),
	CRIT_VULN("critVuln"), // Resistence to Crit DMG.
	MAGIC_DAMAGE_VULN("magicDamVul"),
	CRIT_DAMAGE_EVASION("critDamEvas"),
	BUFF_VULN("buffVuln"),
	VALAKAS("valakas"),
	VALAKAS_RES("valakasRes"),
	VENGEANCE_SKILL_MAGIC_DAMAGE("vengeanceMdam"),
	VENGEANCE_SKILL_PHYSICAL_DAMAGE("vengeancePdam"),
	
	//AGGRESSION_PROF("aggressionProf"),
	BLEED_PROF("bleedProf"),
	POISON_PROF("poisonProf"),
	STUN_PROF("stunProf"),
	PARALYZE_PROF("paralyzeProf"),
	ROOT_PROF("rootProf"),
	SLEEP_PROF("sleepProf"),
	CONFUSION_PROF("confusionProf"),
	CANCEL_PROF("cancelProf"),
	DERANGEMENT_PROF("derangementProf"),
	DEBUFF_PROF("debuffProf"),
	
	SWORD_WPN_VULN("swordWpnVuln"),
	BLUNT_WPN_VULN("bluntWpnVuln"),
	DAGGER_WPN_VULN("daggerWpnVuln"),
	BOW_WPN_VULN("bowWpnVuln"),
	CROSSBOW_WPN_VULN("crossbowWpnVuln"),
	POLE_WPN_VULN("poleWpnVuln"),
	DUAL_WPN_VULN("dualWpnVuln"),
	DUALFIST_WPN_VULN("dualFistWpnVuln"),
	BIGSWORD_WPN_VULN("bigSwordWpnVuln"),
	BIGBLUNT_WPN_VULN("bigBluntWpnVuln"),
	DUALDAGGER_WPN_VULN("dualDaggerWpnVuln"),
	RAPIER_WPN_VULN("rapierWpnVuln"),
	ANCIENT_WPN_VULN("ancientWpnVuln"),
	PET_WPN_VULN("petWpnVuln"),
	
	REFLECT_DAMAGE_PERCENT("reflectDam"),
	REFLECT_SKILL_MAGIC("reflectSkillMagic"),
	REFLECT_SKILL_PHYSIC("reflectSkillPhysic"),
	ABSORB_DAMAGE_PERCENT("absorbDam"),
	TRANSFER_DAMAGE_PERCENT("transDam"),
	ABSORB_MANA_DAMAGE_PERCENT("absorbDamMana"),
	//ABSORB_CP_PERCENT("absorbCpPercent"),
	SKILL_MASTERY("skillMastery"),

	// vitality
	VITALITY_CONSUME_RATE("vitalityConsumeRate"),
	
	//Skills Power
	FIRE_POWER("firePower"),
	WATER_POWER("waterPower"),
	WIND_POWER("windPower"),
	EARTH_POWER("earthPower"),
	HOLY_POWER("holyPower"),
	DARK_POWER("darkPower"),
	
	MAX_LOAD("maxLoad"),
	WEIGHT_LIMIT("weightLimit"),
	
	// FIXME: should be pAtk with ConditionTargetRaceId
	PATK_PLANTS("pAtk-plants"),
	PATK_INSECTS("pAtk-insects"),
	PATK_ANIMALS("pAtk-animals"),
	PATK_MONSTERS("pAtk-monsters"),
	PATK_DRAGONS("pAtk-dragons"),
	PATK_GIANTS("pAtk-giants"),
	PATK_MAGIC("pAtk-magic"),
	
	PDEF_PLANTS("pDef-plants"),
	PDEF_INSECTS("pDef-insects"),
	PDEF_ANIMALS("pDef-animals"),
	PDEF_MONSTERS("pDef-monsters"),
	PDEF_DRAGONS("pDef-dragons"),
	PDEF_GIANTS("pDef-giants"),
	PDEF_MAGIC("pDef-magic"),
	
	MAGIC_REUSE_RATE("mReuse"),
	PHYS_REUSE_RATE("pReuse"),
	BOW_REUSE("bowReuse"),
	
	//ExSkill :)
	INV_LIM("inventoryLimit"),
	WH_LIM("whLimit"),
	FREIGHT_LIM("FreightLimit"),
	P_SELL_LIM("PrivateSellLimit"),
	P_BUY_LIM("PrivateBuyLimit"),
	REC_D_LIM("DwarfRecipeLimit"),
	REC_C_LIM("CommonRecipeLimit"),
	
	//C4 Stats
	PHYSICAL_CONSUME_RATE("PhysicalMpConsumeRate"),
	MAGIC_CONSUME_RATE("MagicalMpConsumeRate"),
	DANCE_CONSUME_RATE("DanceMpConsumeRate"),
	BOW_MP_CONSUME_RATE("BowMpConsumeRate"),
	HP_CONSUME_RATE("HpConsumeRate"),
	MP_CONSUME("MpConsume"),
	SOULSHOT_COUNT("soulShotCount"),
	
	TALISMAN_SLOTS("talisman"),
	CLOAK_SLOT("cloak"),
	;
	
	private final String _value;
	
	private Stats(String value)
	{
		_value = value;
	}
	
	public String getValue()
	{
		return _value;
	}
	
	public static final Stats[] VALUES = values();
	public static final int NUM_STATS = VALUES.length;
	
	public static Stats valueOfXml(String name)
	{
		for (Stats s : VALUES)
			if (s.getValue().equals(name))
				return s;
		
		throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
	}
	
	//TODO: add more stats
	public boolean isAdditiveResist()
	{
		switch (this)
		{
			// defensive
			case FIRE_RES:
			case WIND_RES:
			case WATER_RES:
			case EARTH_RES:
			case HOLY_RES:
			case DARK_RES:
			case VALAKAS_RES:
				// offensive
			case FIRE_POWER:
			case WATER_POWER:
			case WIND_POWER:
			case EARTH_POWER:
			case HOLY_POWER:
			case DARK_POWER:
			case VALAKAS:
				return true;
			default:
				return false;
		}
	}
	
	//TODO: add more stats
	public boolean isMultiplicativeResist()
	{
		switch (this)
		{
			// defensive
			case BLEED_VULN:
			case POISON_VULN:
			case STUN_VULN:
			case PARALYZE_VULN:
			case ROOT_VULN:
			case SLEEP_VULN:
			case CONFUSION_VULN:
			case CANCEL_VULN:
			case DERANGEMENT_VULN:
			case DEBUFF_VULN:
			case FALL_VULN:
			case CRIT_VULN:
			case MAGIC_DAMAGE_VULN:
			case BUFF_VULN:
				// offensive
			case BLEED_PROF:
			case POISON_PROF:
			case STUN_PROF:
			case PARALYZE_PROF:
			case ROOT_PROF:
			case SLEEP_PROF:
			case CONFUSION_PROF:
			case CANCEL_PROF:
			case DERANGEMENT_PROF:
			case DEBUFF_PROF:
				// defensive
			case SWORD_WPN_VULN:
			case BLUNT_WPN_VULN:
			case DAGGER_WPN_VULN:
			case BOW_WPN_VULN:
			case CROSSBOW_WPN_VULN:
			case POLE_WPN_VULN:
			case DUAL_WPN_VULN:
			case DUALFIST_WPN_VULN:
			case BIGSWORD_WPN_VULN:
			case BIGBLUNT_WPN_VULN:
				return true;
			default:
				return false;
		}
	}
}
