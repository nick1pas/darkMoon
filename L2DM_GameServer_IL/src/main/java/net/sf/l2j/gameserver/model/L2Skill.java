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
package net.sf.l2j.gameserver.model;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GeoData;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedSkillTable;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.FourSepulchersManager;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.actor.instance.L2ArtefactInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2ChestInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SummonInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.entity.Couple;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.gameserver.skills.conditions.Condition;
import net.sf.l2j.gameserver.skills.effects.EffectCharge;
import net.sf.l2j.gameserver.skills.effects.EffectTemplate;
import net.sf.l2j.gameserver.skills.funcs.Func;
import net.sf.l2j.gameserver.skills.funcs.FuncTemplate;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillCharge;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillChargeDmg;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillChargeEffect;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillCreateItem;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillDefault;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillDrain;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillExitBuffs;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillNeedCharge;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillSeed;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillSummon;
import net.sf.l2j.gameserver.templates.L2WeaponType;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This class...
 * 
 * @version $Revision: 1.3.2.8.2.22 $ $Date: 2005/04/06 16:13:42 $
 */
public abstract class L2Skill
{
    protected static Log _log = LogFactory.getLog(L2Skill.class.getName());
    
    public static final int SKILL_CUBIC_MASTERY = 143;
    public static final int SKILL_LUCKY = 194;
    public static final int SKILL_CREATE_COMMON = 1320; 
    public static final int SKILL_CREATE_DWARVEN = 172;
    public static final int SKILL_CRYSTALLIZE = 248;
    
    public static final int SKILL_FAKE_INT = 9001;
    public static final int SKILL_FAKE_WIT = 9002;
    public static final int SKILL_FAKE_MEN = 9003;
    public static final int SKILL_FAKE_CON = 9004;
    public static final int SKILL_FAKE_DEX = 9005;
    public static final int SKILL_FAKE_STR = 9006;
    
    public static enum SkillOpType { OP_PASSIVE, OP_ACTIVE, OP_TOGGLE }
    
    /** Target types of skills : SELF, PARTY, CLAN, PET... */
    public static enum SkillTargetType 
    {
        TARGET_NONE,
        TARGET_SELF,
        TARGET_ONE,
        TARGET_PARTY,
        TARGET_ALLY,
        TARGET_CLAN,
        TARGET_PET,
        TARGET_AREA,
        TARGET_AURA,
        TARGET_CORPSE,
        TARGET_AREA_UNDEAD,
        TARGET_MULTIFACE,
        TARGET_CORPSE_ALLY,
        TARGET_CORPSE_CLAN,
        TARGET_CORPSE_PLAYER,
        TARGET_CORPSE_PET,
        TARGET_ITEM,
        TARGET_AREA_CORPSE_MOB,
        TARGET_CORPSE_MOB,
        TARGET_UNLOCKABLE,
        TARGET_HOLY,
        TARGET_PARTY_MEMBER,
        TARGET_PARTY_OTHER,
        TARGET_ENEMY_SUMMON,
        TARGET_OWNER_PET,
        TARGET_ENEMY_ALLY,
        TARGET_ENEMY_PET,
        TARGET_GATE,
        TARGET_MOB,
        TARGET_AREA_MOB,
        TARGET_KNOWNLIST,
        //L2EMU_EDIT_ADD
        TARGET_COUPLE,
        //TARGET_BOSS
        TARGET_PIG
        //L2EMU_EDIT_ADD
        }
    
    
    public static enum SkillType 
    {
    	//L2EMU_ADD
    	EXITBUFFS           (L2SkillExitBuffs.class),
    	NEEDCHARGE          (L2SkillNeedCharge.class),
    	//L2EMU_ADD
        PDAM,
        MDAM,
        CPDAM,
        DOT,
        BLEED,
        POISON,
        HEAL,
        HOT,
        COMBATPOINTHEAL,
        CPHOT,
        MANAHEAL,
        MANAHEAL_PERCENT,
        MANARECHARGE, 
        MPHOT,
        AGGDAMAGE,
        BUFF,
        DEBUFF,
        STUN,
        ROOT,
        RESURRECT,
        PASSIVE,
        CONT,
        SIGNET,
        CONFUSION,
        UNLOCK,
        CHARGE                (L2SkillCharge.class),
        FEAR,
        MHOT,
        DRAIN                 (L2SkillDrain.class),
        NEGATE,
        CANCEL,
        SLEEP,
        AGGREDUCE,
        AGGREMOVE,
        AGGREDUCE_CHAR,
        CHARGEDAM             (L2SkillChargeDmg.class),
        CHARGE_EFFECT         (L2SkillChargeEffect.class),
        CONFUSE_MOB_ONLY,
        DEATHLINK,
        BLOW,
        FATALCOUNTER,
        DETECT_WEAKNESS,
        ENCHANT_ARMOR,
        ENCHANT_WEAPON,
        FEED_PET,
        HEAL_PERCENT,
        HEAL_STATIC,
        LUCK,
        MANADAM,
        MDOT,
        MUTE,
        RECALL,
        REFLECT,
        SUMMON_FRIEND,
        SOULSHOT,
        SPIRITSHOT,
        SPOIL,
        SWEEP,
        SUMMON                (L2SkillSummon.class),
        WEAKNESS,
        DEATHLINK_PET,
        MANA_BY_LEVEL,
        FAKE_DEATH,
        UNBLEED,
        UNPOISON,
        SIEGEFLAG,
        TAKECASTLE,
        UNDEAD_DEFENSE,
        SEED                (L2SkillSeed.class),
        BEAST_FEED,
        FORCE_BUFF,
        PARALYZE,
        DRAIN_SOUL,
        COMMON_CRAFT, 
        DWARVEN_CRAFT,
        WEAPON_SA,
        DELUXE_KEY_UNLOCK,
        SOW,
        HARVEST,
        GET_PLAYER,
        
        FISHING,
        PUMPING,
        REELING,
        CANCEL_TARGET,
        CREATE_ITEM      (L2SkillCreateItem.class),
        AGGDEBUFF,
        COMBATPOINTPERHEAL,
        SUMMONCP,
        SUMMON_TREASURE_KEY,
        SUMMON_CURSED_BONES,
        ERASE,
        MAGE_BANE, 
        WARRIOR_BANE,
        STRSIEGEASSAULT, 
        LUCKNOBLESSE    (L2SkillCreateItem.class),
        RAID_DESCRIPTION,
        UNSUMMON_ENEMY_PET,
        BETRAY,
        BALANCE_LIFE,
        SERVER_SIDE,
        NOTDONE;
        
        private final Class<? extends L2Skill> _class;
        
        public L2Skill makeSkill(StatsSet set)
        {
            try {
                Constructor<? extends L2Skill> c =
                    _class.getConstructor(StatsSet.class);
                
                return c.newInstance(set);
            } 
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
       private SkillType() 
        {
           _class = L2SkillDefault.class;
       }
        
       private SkillType(Class<? extends L2Skill> classType) 
        {
           _class = classType;
        }
    }
    
    //elements
    public final static int ELEMENT_WIND = 1;
    public final static int ELEMENT_FIRE = 2;
    public final static int ELEMENT_WATER = 3;
    public final static int ELEMENT_EARTH = 4;
    public final static int ELEMENT_HOLY = 5; 
    public final static int ELEMENT_DARK = 6; 
    public final static int ELEMENT_UNHOLY = 5;
    public final static int ELEMENT_SACRED = 6;
    
    //save vs 
    public final static int SAVEVS_INT = 1;
    public final static int SAVEVS_WIT = 2;
    public final static int SAVEVS_MEN = 3;
    public final static int SAVEVS_CON = 4;
    public final static int SAVEVS_DEX = 5;
    public final static int SAVEVS_STR = 6;
    
    //stat effected
    public final static int STAT_PATK = 301;           // pAtk 
    public final static int STAT_PDEF = 302;           // pDef
    public final static int STAT_MATK = 303;           // mAtk
    public final static int STAT_MDEF = 304;           // mDef
    public final static int STAT_MAXHP = 305;          // maxHp
    public final static int STAT_MAXMP = 306;          // maxMp
    public final static int STAT_CURHP = 307;
    public final static int STAT_CURMP = 308;
    public final static int STAT_HPREGEN = 309;        // regHp
    public final static int STAT_MPREGEN = 310;        // regMp
    public final static int STAT_CASTINGSPEED = 311;   // sCast
    public final static int STAT_ATKSPD = 312;         // sAtk
    public final static int STAT_CRITDAM = 313;        // critDmg
    public final static int STAT_CRITRATE = 314;       // critRate
    public final static int STAT_FIRERES = 315;        // fireRes
    public final static int STAT_WINDRES = 316;        // windRes
    public final static int STAT_WATERRES = 317;       // waterRes
    public final static int STAT_EARTHRES = 318;       // earthRes
    public final static int STAT_HOLYRES = 336;        // holyRes 
    public final static int STAT_DARKRES = 337;        // darkRes     
    public final static int STAT_ROOTRES = 319;        // rootRes
    public final static int STAT_SLEEPRES = 320;       // sleepRes
    public final static int STAT_CONFUSIONRES = 321;   // confusRes
    public final static int STAT_BREATH = 322;         // breath
    public final static int STAT_AGGRESSION = 323;     // aggr
    public final static int STAT_BLEED = 324;          // bleed
    public final static int STAT_POISON = 325;         // poison
    public final static int STAT_STUN = 326;           // stun
    public final static int STAT_ROOT = 327;           // root
    public final static int STAT_MOVEMENT = 328;       // move
    public final static int STAT_EVASION = 329;        // evas
    public final static int STAT_ACCURACY = 330;       // accu
    public final static int STAT_COMBAT_STRENGTH = 331;
    public final static int STAT_COMBAT_WEAKNESS = 332;
    public final static int STAT_ATTACK_RANGE = 333;   // rAtk
    public final static int STAT_NOAGG = 334;          // noagg
    public final static int STAT_SHIELDDEF = 335;      // sDef
    public final static int STAT_MP_CONSUME_RATE = 336;      // Rate of mp consume per skill use
    public final static int STAT_HP_CONSUME_RATE = 337;      // Rate of hp consume per skill use
	public final static int	STAT_MCRITRATE			= 338;
        
    //COMBAT DAMAGE MODIFIER SKILLS...DETECT WEAKNESS AND WEAKNESS/STRENGTH
    public final static int COMBAT_MOD_ANIMAL = 200;
    public final static int COMBAT_MOD_BEAST    = 201;
    public final static int COMBAT_MOD_BUG  = 202;
    public final static int COMBAT_MOD_DRAGON = 203;
    public final static int COMBAT_MOD_MONSTER= 204;
    public final static int COMBAT_MOD_PLANT    = 205;
    public final static int COMBAT_MOD_HOLY = 206;
    public final static int COMBAT_MOD_UNHOLY = 207;
    public final static int COMBAT_MOD_BOW  = 208;
    public final static int COMBAT_MOD_BLUNT    = 209;
    public final static int COMBAT_MOD_DAGGER = 210;
    public final static int COMBAT_MOD_FIST     = 211;
    public final static int COMBAT_MOD_DUAL     = 212;
    public final static int COMBAT_MOD_SWORD    = 213;
    public final static int COMBAT_MOD_POISON = 214;
    public final static int COMBAT_MOD_BLEED    = 215;
    public final static int COMBAT_MOD_FIRE = 216;
    public final static int COMBAT_MOD_WATER    = 217;
    public final static int COMBAT_MOD_EARTH    = 218;
    public final static int COMBAT_MOD_WIND = 219;
    public final static int COMBAT_MOD_ROOT = 220;
    public final static int COMBAT_MOD_STUN = 221;
    public final static int COMBAT_MOD_CONFUSION = 222;
    public final static int COMBAT_MOD_DARK = 223;
    
    //conditional values
    public final static int COND_RUNNING =   0x0001;
    public final static int COND_WALKING =   0x0002;
    public final static int COND_SIT     =   0x0004;
    public final static int COND_BEHIND  =   0x0008;
    public final static int COND_CRIT    =   0x0010;
    public final static int COND_LOWHP   =   0x0020;
    public final static int COND_ROBES   =   0x0040;
    public final static int COND_CHARGES =   0x0080;
    public final static int COND_SHIELD  =   0x0100;
    public final static int COND_GRADEA  = 0x010000;
    public final static int COND_GRADEB  = 0x020000;
    public final static int COND_GRADEC  = 0x040000;
    public final static int COND_GRADED  = 0x080000;
    public final static int COND_GRADES  = 0x100000;
    
    
    private static final Func[] _emptyFunctionSet = new Func[0];
    private static final L2Effect[] _emptyEffectSet = new L2Effect[0];
    
    // these two build the primary key
    private final int _id;
    private final int _level;
    
    /** Instant Kill Rate (iRate) **/
    private final int _iRate;
    private final boolean _iKill;
    
    /** Identifier for a skill that client can't display */
    private int _displayId;

    // not needed, just for easier debug
    private final String _name;
    private final SkillOpType _operateType;
    private final boolean _magic;
    private final int _mpConsume;
    private final int _mpInitialConsume;
    private final int _hpConsume;
    private final int _itemConsume;
    private final int _itemConsumeId;
    // item consume count over time
    private final int _itemConsumeOT;
    // item consume id over time
    private final int _itemConsumeIdOT;
    // how many times to consume an item
    private final int _itemConsumeSteps;
    // for summon spells: 
    // a) What is the total lifetime of summons (in millisecs)
    private final int _summonTotalLifeTime;
    // b) how much lifetime is lost per second of idleness (non-fighting) 
    private final int _summonTimeLostIdle;
    // c) how much time is lost per second of activity (fighting) 
    private final int _summonTimeLostActive;    
    // item consume time in milliseconds 
    private final int _itemConsumeTime;
    private final int _castRange;
    private final int _effectRange;
    
    // all times in milliseconds 
    private final int _hitTime;
    private final int _skillInterruptTime;
    private final int _coolTime;
    private final int _reuseDelay;
    //L2EMU_EDIT
    private int _buffDuration;
    //L2EMU_EDIT
   
    /** Target type of the skill : SELF, PARTY, CLAN, PET... */
    private final SkillTargetType _targetType;
    private final double _power;
    private final int _effectPoints;
    private final int _levelDepend;
    
    // Effecting area of the skill, in radius.
    // The radius center varies according to the _targetType:
    // "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
    private final int _skillRadius;
    
    private final SkillType _skillType;
    private final SkillType _effectType;
    private final int _effectPower;
    private final int _effectLvl;
    private final int _skill_landing_percent;

    private final boolean _ispotion;
    private final int _element;
    private final int _savevs;
    private final boolean _isSuicideAttack;
    private final int _activateRate;
    private final int _levelModifier;
    private final int _magicLevel;
    private final String[] _negateStats;
    private final float _negatePower;
    private final int _negateId;
    
    private final Stats _stat;
    
    private final int _condition;
    private final int _conditionValue;
    private final boolean _overhit;
    private final boolean _critical;
    private final boolean _ignoreShld;
    private final int _weaponsAllowed;
    private final int _armorsAllowed;
    
    private final int _addCrossLearn; // -1 disable, otherwice SP price for others classes, default 1000
    private final float _mulCrossLearn; // multiplay for others classes, default 2
    private final float _mulCrossLearnRace; // multiplay for others races, default 2
    private final float _mulCrossLearnProf; // multiplay for fighter/mage missmatch, default 3
    private final FastList<ClassId> _canLearn; // which classes can learn
    private final FastList<Integer> _teachers; // which NPC teaches
    private final boolean _isOffensive;
    private final int _numCharges;
    private final int _triggeredId;

    private final int _baseCritRate;  // percent of success for skill critical hit (especially for PDAM & BLOW - they're not affected by rCrit values or buffs). Default loads -1 for all other skills but 0 to PDAM & BLOW
    private final int _lethalEffect1;     // percent of success for lethal 1st effect (hit cp to 1 or if mob hp to 50%) (only for PDAM skills)
    private final int _lethalEffect2;     // percent of success for lethal 2nd effect (hit cp,hp to 1 or if mob hp to 1) (only for PDAM skills)
    private final boolean _directHpDmg;   // If true then dmg is being make directly 
    private final boolean _isDance;       // If true then casting more dances will cost more MP
    private final int _nextDanceCost;
    private final float _sSBoost;         // If true skill will have SoulShot boost (power*2)
    
    //L2EMU_DISABLE
    //private final int _timeMulti;
    //L2EMU_DISABLE
    private final boolean _isAdvanced; // Used by siege flag summon skills

    private final float _successRate; 
    private final int _minPledgeClass;

    private final int _aggroPoints;
	
	private final float _pvpMulti;
    
    protected Condition _preCondition;
    protected Condition _itemPreCondition;
    protected FuncTemplate[] _funcTemplates;
    protected EffectTemplate[] _effectTemplates;
    protected EffectTemplate[] _effectTemplatesSelf;
    
	protected L2Skill(StatsSet set)
    {
        _id           = set.getInteger("skill_id");
        _level        = set.getInteger("level");

        _displayId    = set.getInteger("displayId", _id);
        _name         = set.getString("name");
        _operateType  = set.getEnum("operateType", SkillOpType.class);
        _magic        = set.getBool  ("isMagic", false);
        _ispotion     = set.getBool  ("isPotion", false);
        _mpConsume    = set.getInteger("mpConsume", 0);
        _mpInitialConsume = set.getInteger("mpInitialConsume", 0);
        _hpConsume    = set.getInteger("hpConsume", 0);
        _itemConsume  = set.getInteger("itemConsumeCount", 0);
        _itemConsumeId= set.getInteger("itemConsumeId", 0);
        _itemConsumeOT = set.getInteger("itemConsumeCountOT", 0);
        _itemConsumeIdOT = set.getInteger("itemConsumeIdOT", 0);
        _itemConsumeTime = set.getInteger("itemConsumeTime", 0);
        _itemConsumeSteps = set.getInteger("itemConsumeSteps", 0);
        _summonTotalLifeTime= set.getInteger("summonTotalLifeTime", 1200000);  // 20 minutes default
        _summonTimeLostIdle= set.getInteger("summonTimeLostIdle", 0);
        _summonTimeLostActive= set.getInteger("summonTimeLostActive", 0);
        _iRate        = set.getInteger("iRate", 0);
        _iKill        = set.getBool  ("iKill", false);
        _castRange    = set.getInteger("castRange", 0);
        _effectRange  = set.getInteger("effectRange", -1);

        _hitTime      = set.getInteger("hitTime", 0);
        _coolTime = set.getInteger("coolTime", 0);
        _skillInterruptTime = (_hitTime / 2);
        _reuseDelay   = set.getInteger("reuseDelay", 0);

        _skillType    = set.getEnum("skillType", SkillType.class);
        _isDance      = set.getBool("isDance",false);
        //L2EMU_EDIT_ADD_START
        /*if(_isDance)
            _timeMulti = Config.ALT_DANCE_TIME;
        else if(_skillType==SkillType.SEED)
            _timeMulti = Config.ALT_SEED_TIME;
        else
            _timeMulti = Config.ALT_BUFF_TIME;*/
 
        //dances and songs (1 miute duration)
        //seed type skills
        if(_skillType==SkillType.SEED)
        {
        	_buffDuration = set.getInteger("buffDuration", 1) + Config.ALT_SEED_TIME;
        }
        else if(_isDance && Config.ALT_DANCE_AND_SONG_TIME > 0)
        {
        	_buffDuration = set.getInteger("buffDuration", 1) + Config.ALT_DANCE_AND_SONG_TIME;
        }
        
        //only read sif any config enabled.
        else if(Config.ALT_BUFF_TIME > 0 
        		|| Config.ADDITIONAL_TIME_4_MINITES_BUFFS > 0
        		|| Config.ADDITIONAL_TIME_2_MINUTES_BUFFS > 0 
        		|| Config.ADDITIONAL_TIME_HEROES_1_MINUTE_BUFFS > 0
        		|| Config.ADDITIONAL_TIME_CLAN_HALL_BUFFS > 0)
        {
        	switch(_id)
        	{
        	
        	//additional time for buffs with time 4 minutes
        	case HardcodedSkillTable.PROPHECY_OF_WATER:
        	case HardcodedSkillTable.PROPHECY_OF_FIRE:
        	case HardcodedSkillTable.PROPHECY_OF_WIND:
        	case HardcodedSkillTable.CHANT_OF_VICTORY:
        	case HardcodedSkillTable.BLESSING_OF_QUEEN:
        	case HardcodedSkillTable.GIFT_OF_QUEEN:
        	case HardcodedSkillTable.BLESSING_OF_SERAPHIM:
        	case HardcodedSkillTable.GIFT_OF_SERAPHIM:

        		_buffDuration = set.getInteger("buffDuration", 1) + Config.ADDITIONAL_TIME_4_MINITES_BUFFS;
        		break;

        	//additional time for clan hall buffs
        	case HardcodedSkillTable.CLAN_HALL_WIND_WALK:
        	case HardcodedSkillTable.CLAN_HALL_DECREASE_WEIGHT:
        	case HardcodedSkillTable.CLAN_HALL_SHIELD:
        	case HardcodedSkillTable.CLAN_HALL_MIGHT:
        	case HardcodedSkillTable.CLAN_HALL_MENTAL_SHIELD:
        	case HardcodedSkillTable.CLAN_HALL_BLESSED_BODY:
        	case HardcodedSkillTable.CLAN_HALL_BLESSED_SOUL:
        	case HardcodedSkillTable.CLAN_HALL_MAGIC_BARRIER:
        	case HardcodedSkillTable.CLAN_HALL_RESIST_SHOCK:
        	case HardcodedSkillTable.CLAN_HALL_CONCENTRATION:
        	case HardcodedSkillTable.CLAN_HALL_BERSERKER_SPIRIT:
        	case HardcodedSkillTable.CLAN_HALL_BLESS_SHIELD:
        	case HardcodedSkillTable.CLAN_HALL_VAMPIRIC_RAGE:
        	case HardcodedSkillTable.CLAN_HALL_ACUMEN:
        	case HardcodedSkillTable.CLAN_HALL_EMPOWER:
        	case HardcodedSkillTable.CLAN_HALL_HASTE:
        	case HardcodedSkillTable.CLAN_HALL_GUIDANCE:
        	case HardcodedSkillTable.CLAN_HALL_FOCUS:
        	case HardcodedSkillTable.CLAN_HALL_DEATH_WHISPER:
        		
        		_buffDuration = set.getInteger("buffDuration", 1) + Config.ADDITIONAL_TIME_CLAN_HALL_BUFFS;
        		break;

        	//heroes skills timers
        	case HardcodedSkillTable.HEROIC_BERSERKER:
        	case HardcodedSkillTable.HEROIC_MIRACLE:
        	case HardcodedSkillTable.HEROIC_VALOR:
        		_buffDuration = set.getInteger("buffDuration", 1) + Config.ADDITIONAL_TIME_HEROES_1_MINUTE_BUFFS;
        		break;
        		
        	default:
            	_buffDuration = set.getInteger("buffDuration", 1) + Config.ALT_BUFF_TIME;
        	
        	}
        }
        //L2EMU_EDIT_ADD_END
        
        //general buffs with default time 19 minutes
        else
        {
        	_buffDuration = set.getInteger("buffDuration", 1) + Config.ALT_BUFF_TIME;
        }
        //_buffDuration = set.getInteger("buffDuration", 1);
        _skillRadius  = set.getInteger("skillRadius", 80);
        
        _targetType   = set.getEnum("target", SkillTargetType.class);
        _power        = set.getFloat("power", 0.f);
        _effectPoints = set.getInteger("effectPoints", 0);
        _negateStats = set.getString("negateStats", "").split(" ");
        _negatePower = set.getFloat("negatePower", 0.f);
        _negateId = set.getInteger("negateId", 0);
        _levelDepend  = set.getInteger("lvlDepend", 0);
        _stat         = set.getEnum("stat", Stats.class, null);

        _isAdvanced   = set.getBool("isAdvanced",false); // Used by siege flag summon skills

        _effectType   = set.getEnum("effectType", SkillType.class, null);
        _effectPower  = set.getInteger("effectPower", 0);
        _effectLvl = set.getInteger("effectLevel", 0);
        _skill_landing_percent = set.getInteger("skill_landing_percent", 0);
        _element      = set.getInteger("element", 0);
        _savevs       = set.getInteger("save", 0);
        _activateRate = set.getInteger("activateRate", -1);
        _levelModifier= set.getInteger("levelModifier", 1);
        _magicLevel   = set.getInteger("magicLvl", SkillTreeTable.getInstance().getMinSkillLevel(_id, _level));
        
        _ignoreShld      = set.getBool("ignoreShld", false);
        _critical        = set.getBool("critcal", false);
        _condition       = set.getInteger("condition", 0);
        _conditionValue  = set.getInteger("conditionValue", 0);
        _overhit         = set.getBool("overHit", false);
        _isSuicideAttack = set.getBool("isSuicideAttack", false);
        _weaponsAllowed  = set.getInteger("weaponsAllowed", 0);
        _armorsAllowed   = set.getInteger("armorsAllowed", 0);

        _addCrossLearn       = set.getInteger("addCrossLearn", 1000);
        _mulCrossLearn       = set.getFloat("mulCrossLearn", 2.f);
        _mulCrossLearnRace   = set.getFloat("mulCrossLearnRace", 2.f);
        _mulCrossLearnProf   = set.getFloat("mulCrossLearnProf", 3.f);
        _isOffensive         = set.getBool("offensive",isSkillTypeOffensive());
        _numCharges          = set.getInteger("num_charges", getLevel());
        _successRate         = set.getFloat("rate", 1);
        _minPledgeClass      = set.getInteger("minPledgeClass", 0);
        _triggeredId         = set.getInteger("triggeredId", 0);

        _baseCritRate = set.getInteger("baseCritRate", (_skillType == SkillType.PDAM  || _skillType == SkillType.BLOW) ? 0 : -1);
        _lethalEffect1 = set.getInteger("lethal1",0);
        _lethalEffect2 = set.getInteger("lethal2",0);
        _directHpDmg  = set.getBool("dmgDirectlyToHp",false);
        _nextDanceCost = set.getInteger("nextDanceCost", 0);
        _sSBoost = set.getFloat("SSBoost", 0.f);
        
        _aggroPoints = set.getInteger("aggroPoints", 0);
		
		_pvpMulti = set.getFloat("pvpMulti", 1.f);
		
        String canLearn = set.getString("canLearn", null);
        if (canLearn == null)
        {
            _canLearn = null;
        } 
        else 
        {
            _canLearn = new FastList<ClassId>();
            StringTokenizer st = new StringTokenizer(canLearn, " \r\n\t,;");
            while (st.hasMoreTokens()) 
            {
                String cls = st.nextToken();
                try 
                {
                    _canLearn.add(ClassId.valueOf(cls));
                }
                catch (Throwable t)
                {
                    _log.fatal( "Bad class "+cls+" to learn skill", t);
                }
            }
        }

        String teachers = set.getString("teachers", null);
        if (teachers == null)
        {
            _teachers = null;
        } 
        else 
        {
            _teachers = new FastList<Integer>();
            StringTokenizer st = new StringTokenizer(teachers, " \r\n\t,;");
            while (st.hasMoreTokens())
            {
                String npcid = st.nextToken();
                try
                {
                    _teachers.add(Integer.parseInt(npcid));
                }
                catch (Throwable t)
                {
                    _log.fatal( "Bad teacher id "+npcid+" to teach skill", t);
                }
            }
        }
    }
    
    public abstract void useSkill(L2Character caster, L2Object[] targets);
    

    public final boolean isPotion()
    {
        return _ispotion;
    }

    public final int getArmorsAllowed()
    {
        return _armorsAllowed;
    }
    
    public final int getConditionValue()
    {
        return _conditionValue;
    }
    
    public final SkillType getSkillType() 
    {
        return _skillType;
    }
    
    public final int getSavevs() {
        return _savevs;
    }

    public final int getActivateRate() {
        return _activateRate;
    }

    public final int getLevelModifier() {
        return _levelModifier;
    }

    public final int getMagicLevel() {
        return _magicLevel;
    }
    
    public final int getElement() 
    {
        return _element;
    }
    
    /**
     * Return the target type of the skill : SELF, PARTY, CLAN, PET...<BR><BR>
     * 
     */
    public final SkillTargetType getTargetType() 
    {
        return _targetType;
    }
    
    public final int getCondition() {
        return _condition;
    }
    
    public final boolean isCritical() 
    {
        return _critical;
    }
    public final boolean ignoreShld()
    {
        return _ignoreShld;
    }
    public final boolean isOverhit() 
    {
        return _overhit;
    }

    public final boolean isSuicideAttack()
    {
         return _isSuicideAttack;
    }

    /** INSTANT KILL **/
    public final boolean isInstantKill() 
    {
        return _iKill;
    }
    /** Return the rate in Perecent% of chance to InstantKill**/
    public final int getInstantKillRate() 
    {
        return _iRate;
    }
    
    /**
     * Return the power of the skill.<BR><BR>
     */
    public final double getPower(L2Character activeChar)
    {
        return _power;
    }

    public final double getPower()
    {
        return _power;
    }

    public final int getEffectPoints()
    {
        return _effectPoints;
    }

    public final String[] getNegateStats()
    {
        return _negateStats;
    }

    public final float getNegatePower()
    {
        return _negatePower;
    }

    public final int getNegateId()
    {
        return _negateId;
    }

    public final int getMagicLvl()
    {
        return _magicLevel;
    }

    public int getTriggeredId()
    {
        return _triggeredId;
    }

    public final int getLevelDepend()
    {
        return _levelDepend;
    } 
             
    /** 
    * Return the skill landing percent probability.<BR><BR> 
    */ 
    public final int getLandingPercent() 
    { 
        return _skill_landing_percent; 
    } 
    
    /** 
     * Return the additional effect power or base probability.<BR><BR> 
     */ 
     public final int getEffectPower() 
     { 
         return _effectPower; 
     } 
     
     /**
      * Return the additional effect level.<BR><BR>
      */
     public final int getEffectLvl()
     {
         return _effectLvl;
     }
    
    /** Return the additional effect skill type (ex : STUN, PARALYZE,...).<BR><BR> 
    */ 
    public final SkillType getEffectType()  
    { 
        return _effectType;
    }
    
    /**
     * @return Returns the buffDuration.
     */
    public final int getBuffDuration()
    {
    	//L2EMU_EDIT_DISABLE_START
    	return _buffDuration;
    	//return _buffDuration * _timeMulti;
    }

    /**
     * @return Returns the timeMulti.
     */
    /*public final int getTimeMulti()
    {
        return _timeMulti;
    }*/
    //L2EMU_EDIT_DISABLE_END
    /**
     * @return Returns the castRange.
     */
    public final int getCastRange()
    {
        return _castRange;
    }

    /**
     * @return Returns the effectRange.
     */
    public final int getEffectRange()
    {
        return _effectRange;
    }

    /**
     * @return Returns the hitTime.
     */
    public final int getHitTime()
    {
        return _hitTime;
    }
	
	//added for more compatibility with old-jython scripts
	    public final int getSkillTime()
    {
        return _hitTime;
    }
//end
    /**
     * @return Returns the hpConsume.
     */
    public final int getHpConsume()
    {
        return _hpConsume;
    }

    /**
     * @return Returns the id.
     */
    public final int getId()
    {
        return _id;
    }

    public int getDisplayId()
    {
        return _displayId;
    }

    public void setDisplayId(int id)
    {
        _displayId = id;
    }

    public float getSuccessRate()
    {
        return _successRate;
    }

    public int getMinPledgeClass()
    {
        return _minPledgeClass;
    }


    /**
     * Return the skill type (ex : BLEED, SLEEP, WATER...).<BR><BR>
     */
    public final Stats getStat()
    {
        return _stat;
    }
    
    /**
     * @return Returns the itemConsume.
     */
    public final int getItemConsume()
    {
        return _itemConsume;
    }

    /**
     * @return Returns the itemConsumeId.
     */
    public final int getItemConsumeId()
    {
        return _itemConsumeId;
    }
    
    /**
     * @return Returns the itemConsume count over time.
     */
    public final int getItemConsumeOT()
    {
        return _itemConsumeOT;
    }
    
    /**
     * @return Returns the itemConsumeId over time.
     */
    public final int getItemConsumeIdOT()
    {
        return _itemConsumeIdOT;
    }

    /**
     * @return Returns the itemConsume count over time.
     */
    public final int getItemConsumeSteps()
    {
        return _itemConsumeSteps;
    }
    /**
     * @return Returns the itemConsume count over time.
     */
    public final int getTotalLifeTime()
    {
        return _summonTotalLifeTime;
    }
    /**
     * @return Returns the itemConsume count over time.
     */
    public final int getTimeLostIdle()
    {
        return _summonTimeLostIdle;
    }
    /**
     * @return Returns the itemConsumeId over time.
     */
    public final int getTimeLostActive()
    {
        return _summonTimeLostActive;
     }

    /**
     * @return Returns the itemConsume time in milliseconds.
     */
    public final int getItemConsumeTime()
    {
        return _itemConsumeTime;
    }

    /**
     * @return Returns the level.
     */
    public final int getLevel()
    {
        return _level;
    }

    /**
     * @return Returns the magic.
     */
    public final boolean isMagic()
    {
        return _magic;
    }

    /**
     * @return Returns the mpConsume.
     */
    public final int getMpConsume()
    {
        return _mpConsume;
    }
    
    /**
     * @return Returns the mpInitialConsume.
     */
    public final int getMpInitialConsume()
    {
        return _mpInitialConsume;
    }

    /**
     * @return Returns the name.
     */
    public final String getName()
    {
        return _name;
    }

    /**
     * @return Returns the reuseDelay.
     */
    public final int getReuseDelay()
    {
        return _reuseDelay;
    }

    public final int getCoolTime()
    {
        return _coolTime;
    }

    public final int getSkillInterruptTime()
    {
        return _skillInterruptTime;
    }
    
    public final int getSkillRadius()
    {
        return _skillRadius;
    }

    public final boolean isActive()
    {
        return _operateType == SkillOpType.OP_ACTIVE;
    }
    
    public final boolean isPassive()
    {
        return _operateType == SkillOpType.OP_PASSIVE;
    }
    
    public final boolean isToggle()
    {
        return _operateType == SkillOpType.OP_TOGGLE;
    }

    public final boolean isDance()
    {
        return _isDance;
    }
    
    public final int getNextDanceMpCost()
    {
        return _nextDanceCost;
    }

    public final boolean isAdvanced()
    {
        return _isAdvanced;
    }

    public final float getSSBoost()
    {
        return _sSBoost;
    }

    public final int getAggroPoints()
    {
        return _aggroPoints;
    }

	public final float getPvpMulti()
	{
		return _pvpMulti;
	}

    public final boolean useSoulShot()
    {
        return ((getSkillType() == SkillType.PDAM)||
                (getSkillType() == SkillType.STUN)||
                (getSkillType() == SkillType.CHARGEDAM));
    }
    
    public final boolean useSpiritShot()
    {
        return isMagic();
    }

    public final boolean useFishShot()
    {
       return ((getSkillType() == SkillType.PUMPING) || (getSkillType() == SkillType.REELING) );
    }

    public final int getWeaponsAllowed()
    {
        return _weaponsAllowed;
    }

    public final int getCrossLearnAdd()
    {
        return _addCrossLearn;
    }

    public final float getCrossLearnMul()
    {
        return _mulCrossLearn;
    }

    public final float getCrossLearnRace()
    {
        return _mulCrossLearnRace;
    }

    public final float getCrossLearnProf()
    {
        return _mulCrossLearnProf;
    }

    public final boolean getCanLearn(ClassId cls)
    {
        return _canLearn == null || _canLearn.contains(cls);
    }

    public final boolean canTeachBy(int npcId) {
        return _teachers == null || _teachers.contains(npcId);
    }

    public final boolean isPvpSkill()
    {
        switch (_skillType)
        {
        case DOT:
        case BLEED:
        case CONFUSION:
        case POISON:
        case DEBUFF:
        case AGGDEBUFF:
        case STUN:
        case ROOT:
        case FEAR:
        case SLEEP:
        case MDOT:
        case MANADAM:
        case MUTE:
        case WEAKNESS:
        case PARALYZE:
        case CANCEL:
        case MAGE_BANE:
        case WARRIOR_BANE:
        case CANCEL_TARGET:
        case BETRAY:
        case DELUXE_KEY_UNLOCK:
        case FATALCOUNTER:
            return true;
        default:
            return false;
        }
    }

    public final boolean isOffensive()
    {
        return _isOffensive;
    }

    public final int getNumCharges()
    {
        return _numCharges;
    }

    public final int getBaseCritRate()
    {
        return _baseCritRate;
    }
    
    public final int getLethalChance1()
    {
        return _lethalEffect1;
    }

    public final int getLethalChance2()
    {
        return _lethalEffect2;
    }

    public final boolean getDmgDirectlyToHP()
    {
       return _directHpDmg;
    }

	public final static boolean skillLevelExists(int skillId, int level)
	{
		return SkillTable.getInstance().getInfo(skillId, level) != null;
	}

    public final boolean isSkillTypeOffensive()
    {
        switch (_skillType)
        {
            case PDAM:
            case MDAM:
            case CPDAM:
            case DOT:
            case BLEED:
            case POISON:
            case AGGDAMAGE:
            case DEBUFF:
            case AGGDEBUFF:
            case STUN:
            case ROOT:
            case CONFUSION:
            case ERASE:
            case BLOW:
            case FEAR:
            case DRAIN:
            case SLEEP:
            case CHARGEDAM:
            case CONFUSE_MOB_ONLY:
            case DEATHLINK:
            case FATALCOUNTER:
            case DETECT_WEAKNESS:
            case MDOT:
            case MANADAM:
            case MUTE:
            case SOULSHOT:
            case SPIRITSHOT:
            case SPOIL:
            case WEAKNESS:
            case MANA_BY_LEVEL:
            case SWEEP:
            case PARALYZE:
            case DRAIN_SOUL:
            case AGGREDUCE:
            case CANCEL:
            case MAGE_BANE:
            case WARRIOR_BANE:
            case AGGREMOVE:
            case AGGREDUCE_CHAR: 
            case UNSUMMON_ENEMY_PET:
            case CANCEL_TARGET:
            case BETRAY:
            case SOW:
            case HARVEST:
                return true;
            default:
                return false;
        }
    }

    public final boolean isPositive() {
        switch (_skillType)
        {
              case BUFF:
              case HEAL:
              case HEAL_PERCENT:
              case HOT:
              case MANAHEAL:
              case MANARECHARGE:
              case NEGATE:
              case CANCEL:
              case REFLECT:
              case UNBLEED:
              case UNPOISON:
              case SEED:
            return true;
        default:
            return false;
        }
    }

    public final boolean isNeedWeapon() { return (_skillType == SkillType.MDAM) ? true : false; }
    
//  int weapons[] = {L2Weapon.WEAPON_TYPE_ETC, L2Weapon.WEAPON_TYPE_BOW,
//           L2Weapon.WEAPON_TYPE_POLE, L2Weapon.WEAPON_TYPE_DUALFIST,
//           L2Weapon.WEAPON_TYPE_DUAL, L2Weapon.WEAPON_TYPE_BLUNT,
//           L2Weapon.WEAPON_TYPE_SWORD, L2Weapon.WEAPON_TYPE_DAGGER};
    
    public final boolean getWeaponDependancy(L2Character activeChar)
    {
        int weaponsAllowed = getWeaponsAllowed();
        //check to see if skill has a weapon dependency.
        if(weaponsAllowed == 0)
            return true;
        if(activeChar.getActiveWeaponItem() != null)
        {
            L2WeaponType playerWeapon;
            playerWeapon = activeChar.getActiveWeaponItem().getItemType();
            int mask = playerWeapon.mask(); 
            if((mask & weaponsAllowed) != 0)
                return true;
            // can be on the secondary weapon
            if (activeChar.getSecondaryWeaponItem() != null)
            {
                playerWeapon = activeChar.getSecondaryWeaponItem().getItemType();
                mask = playerWeapon.mask();
                if((mask & weaponsAllowed) != 0)
                    return true;
            }
        }
        TextBuilder skillmsg = new TextBuilder();
        skillmsg.append(getName());
        skillmsg.append(" can only be used with weapons of type ");
        for(L2WeaponType wt : L2WeaponType.values())
        {
            if((wt.mask() & weaponsAllowed) != 0)
                skillmsg.append(wt).append('/');
        }
        skillmsg.setCharAt(skillmsg.length()-1, '.');
        SystemMessage message = new SystemMessage(SystemMessageId.S1_S2);
        message.addString(skillmsg.toString()); 
        activeChar.sendPacket(message); 
        
        return false;
    }
    
    public boolean checkCondition(L2Character activeChar, L2Object target, boolean itemOrWeapon)
    {
        if((getCondition() & L2Skill.COND_SHIELD) != 0)
        {
            //TODO: add checks for shield here.
            
        }

        Condition preCondition = _preCondition;
        if(itemOrWeapon) preCondition = _itemPreCondition;
        if (preCondition == null) return true;

        Env env = new Env();
        env.player = activeChar;
        if (target instanceof L2Character)
            env.target = (L2Character)target;
        env.skill = this;

        if (!preCondition.test(env))
        {
            String msg = preCondition.getMessage();
            if (msg != null)
            {
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                sm.addString(msg);
                activeChar.sendPacket(sm);
            }
            return false;
        }
        return true;
    }
    
    
    /**
     * Return all targets of the skill in a table in function a the skill type.<BR><BR>
     * 
     * <B><U> Values of skill type</U> :</B><BR><BR>
     * <li>ONE : The skill can only be used on the L2PcInstance targeted, or on the caster if it's a L2PcInstance and no L2PcInstance targeted</li>
     * <li>SELF</li>
     * <li>HOLY, UNDEAD</li>
     * <li>PET</li>
     * <li>AURA, AURA_CLOSE</li>
     * <li>AREA</li>
     * <li>MULTIFACE</li>
     * <li>PARTY, CLAN</li>
     * <li>CORPSE_PLAYER, CORPSE_MOB, CORPSE_CLAN</li>
     * <li>UNLOCKABLE</li>
     * <li>ITEM</li><BR><BR>
     * 
     * @param activeChar The L2Character who use the skill
     * 
     */
    public final L2Object[] getTargetList(L2Character activeChar, boolean onlyFirst)
    {
        FastList<L2Character> targetList = new FastList<L2Character>();
        
        // Get the target type of the skill 
        // (ex : ONE, SELF, HOLY, PET, AURA, AURA_CLOSE, AREA, MULTIFACE, PARTY, CLAN, CORPSE_PLAYER, CORPSE_MOB, CORPSE_CLAN, UNLOCKABLE, ITEM, UNDEAD)
        SkillTargetType targetType = getTargetType();
        
        // Init to null the target of the skill
        L2Character target = null;
        
        // Get the L2Objcet targeted by the user of the skill at this moment
        L2Object objTarget = activeChar.getTarget();
        
        // Get the type of the skill
        // (ex : PDAM, MDAM, DOT, BLEED, POISON, HEAL, HOT, MANAHEAL, MANARECHARGE, AGGDAMAGE, BUFF, DEBUFF, STUN, ROOT, RESURRECT, PASSIVE...)
        SkillType skillType = getSkillType();

        // If the L2Object targeted is a L2Character, it becomes the L2Character target
        if(objTarget instanceof L2Character)
        {
            target = (L2Character) objTarget;
        }
        
        
        switch(targetType)
        {
        //L2EMU_DISABLE_ADD_START
        // The skill can only be used on the L2Character targeted, or on the
        // caster itself
        case TARGET_ONE:
        {
        	boolean canTargetSelf = false;
        	switch(skillType)
        	{
        	case BUFF:
        	case HEAL:
        	case HOT:
        	case HEAL_PERCENT:
        	case MANARECHARGE:
        	case MANAHEAL:
        	case NEGATE:
        	case CANCEL:
        	case REFLECT: 
        	case UNBLEED: 
        	case UNPOISON:
        	case SEED: 
        	case COMBATPOINTHEAL:
        	case MAGE_BANE: 
        	case WARRIOR_BANE:
        	case BETRAY:
        	case BALANCE_LIFE: 
        	case FORCE_BUFF:
        		canTargetSelf = true;
        		break;
        	}
        	// automaticly selects caster if no target is selected (only positive skills)  
        	if (isPositive() && target == null)  
        		target = activeChar;

        	// Check for null target or any other invalid target
        	if (target == null || target.isDead() || (target == activeChar && !canTargetSelf))
        	{
        		activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
        		return null;
        	}

        	if(!GeoData.getInstance().canSeeTarget(activeChar, target)) return null;
        	// If a target is found, return it in a table else send a system message TARGET_IS_INCORRECT
        	return new L2Character[]{target};
        }
        // The skill can only be used on the L2Character targeted, or on the caster itself
        /*case TARGET_ONE:
        {
            // automaticly selects caster if no target is selected (only positive skills)  
            if (isPositive() && target == null)  
                    target = activeChar;

            boolean canTargetSelf = false;
            switch(skillType)
            {
                case BUFF: case HEAL: case HOT: case HEAL_PERCENT:
                case MANARECHARGE: case MANAHEAL: case NEGATE:
                case CANCEL: case REFLECT: case UNBLEED: case UNPOISON:
                case SEED: case COMBATPOINTHEAL: case MAGE_BANE: case WARRIOR_BANE:
                case BETRAY: case BALANCE_LIFE: case FORCE_BUFF:
                    canTargetSelf = true;
                    break;
            }

            // Check for null target or any other invalid target
            if (target == null || target.isDead() || (target == activeChar && !canTargetSelf))
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }
            if(!GeoData.getInstance().canSeeTarget(activeChar, target)) return null;
            return new L2Character[]{target};
        }*/
      //L2EMU_DISABLE_ADD_END
        case TARGET_SELF:
        {     
            return new L2Character[]{activeChar};
        }
        /*case TARGET_BOSS:
        {
            L2MinionInstance Minion = null;
            Minion = (L2MinionInstance)target;
            if (activeChar instanceof L2MinionInstance)
             return new L2Character[]{target};
        }*/
        case TARGET_HOLY:
        {
            if (activeChar instanceof L2PcInstance){
            	 if (activeChar.getTarget() instanceof L2ArtefactInstance)
            		 return new L2Character[] {(L2ArtefactInstance) activeChar.getTarget()};
            	 else if ( ((L2PcInstance)activeChar).checkFOS())
            		 return new L2Character[] {(L2NpcInstance) activeChar.getTarget()};
            }

            return null;
        }
        case TARGET_COUPLE:
        {
            if (target != null && target instanceof L2PcInstance)
            {
            	int _chaid = activeChar.getObjectId();
            	int targetId = target.getObjectId();
                for(Couple cl: CoupleManager.getInstance().getCouples())
                {
                   if((cl.getPlayer1Id()==_chaid && cl.getPlayer2Id()==targetId) || (cl.getPlayer2Id()==_chaid && cl.getPlayer1Id()==targetId))
                	   return new L2Character[]{target};
                }
            }

            return null;
        }
        case TARGET_PET:
        {
            target = activeChar.getPet();
            if (target != null && !target.isDead())
                return new L2Character[]{target}; 
            
            return null;
        }
        case TARGET_OWNER_PET:
        {
           if (activeChar instanceof L2Summon)
           {
               target = ((L2Summon)activeChar).getOwner();
               if (target != null && !target.isDead())
                   return new L2Character[]{target}; 
            }
           
            return null;
        }
        case TARGET_ENEMY_PET:
        {
            if(target != null && target instanceof L2Summon)
            {      
                L2Summon targetPet = null;
                targetPet = (L2Summon)target;
                if (activeChar instanceof L2PcInstance && activeChar.getPet() != targetPet 
                    && !targetPet.isDead() && targetPet.getOwner().getPvpFlag() != 0)
                {
                   return new L2Character[]{target};
                }
            }
            return null;
        }
        case TARGET_CORPSE_PET:
        {
            if (activeChar instanceof L2PcInstance)
            {
                target = activeChar.getPet();
                if (target != null && target.isDead())
                {
                    return new L2Character[]{target}; 
                }
            }
            
            return null;
        }   
        case TARGET_AURA:
        {
            int radius = getSkillRadius();
            boolean srcInPvP = ZoneManager.getInstance().checkIfInZonePvP(activeChar)
                        && !SiegeManager.getInstance().checkIfInZone(activeChar);
 
            L2PcInstance src = null;
            if (activeChar instanceof L2PcInstance) src = (L2PcInstance)activeChar;
            if (activeChar instanceof L2Summon) src = ((L2Summon)activeChar).getOwner();
            
            // Go through the L2Character _knownList
            for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
            {
                if (obj != null && (obj instanceof L2Attackable || obj instanceof L2PlayableInstance))
                {
                    boolean targetInPvP = ZoneManager.getInstance().checkIfInZonePvP(obj)
                        && !SiegeManager.getInstance().checkIfInZone(obj);

                    // Don't add this target if this is a Pc->Pc pvp casting and pvp condition not met
                    if (obj == activeChar || obj == src) continue;
                    if (src != null) 
                    {
                        if (!GeoData.getInstance().canSeeTarget(activeChar, obj))
                            continue;
                        // check if both attacker and target are L2PcInstances and if they are in same party 
                        if (obj instanceof L2PcInstance) 
                        {
                            if(!src.checkPvpSkill(obj, this)) continue;
                            if((src.getParty() != null && ((L2PcInstance) obj).getParty() != null) && src.getParty().getPartyLeaderOID() == ((L2PcInstance) obj).getParty().getPartyLeaderOID())
                                continue;
                            if(!srcInPvP && !targetInPvP)
                            {
                                if(src.getClanId() != 0 && src.getClanId() == ((L2PcInstance)obj).getClanId())
                                    continue;
                            }
                        }
                        else if(obj instanceof L2Summon)
                        {
                            L2PcInstance trg = ((L2Summon)obj).getOwner();
                            if(trg == src) continue;
                            if(!src.checkPvpSkill(trg, this)) continue;
                            if((src.getParty() != null && trg.getParty() != null) && 
                                src.getParty().getPartyLeaderOID() == trg.getParty().getPartyLeaderOID()) 
                                continue;
                            if(!srcInPvP && !targetInPvP )
                            {
                                if(src.getClanId() != 0 && src.getClanId() == trg.getClanId())
                                    continue;
                            }
                        }
                    }
                    if (!Util.checkIfInRange(radius, activeChar, obj, true)) continue;

                    if (onlyFirst == false) targetList.add((L2Character) obj);
                    else return new L2Character[] {(L2Character) obj};
                }
            }
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        // [L2J_JP ADD SANDMAN]
        //case TARGET_AURA:
        /*case TARGET_AREA:
        {
            return getAreaTargetList(activeChar);
        }*/
        case TARGET_MULTIFACE:
        {
            return getMultiFaceTargetList(activeChar);
        }
        case TARGET_AREA:
        {
            if ((!(target instanceof L2Attackable || target instanceof L2PlayableInstance)) ||  //   Target is not L2Attackable or L2PlayableInstance
                (getCastRange() >= 0 && (target == null || target == activeChar || target.isAlikeDead()))) //target is null or self or dead/faking
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }

            L2Character cha;
            
            if (getCastRange() >= 0)
            {
                cha = target;
                
                if(!onlyFirst) targetList.add(cha); // Add target to target list
                else return new L2Character[]{cha};
            }
            else cha = activeChar;
            
            boolean effectOriginIsL2PlayableInstance = (cha instanceof L2PlayableInstance);

            L2PcInstance src = null;
            if (activeChar instanceof L2PcInstance) src = (L2PcInstance)activeChar;
            else if (activeChar instanceof L2Summon) src = ((L2Summon)activeChar).getOwner();
            
            int radius = getSkillRadius();
            
            boolean srcInPvP = ZoneManager.getInstance().checkIfInZonePvP(activeChar)
                             && !SiegeManager.getInstance().checkIfInZone(activeChar);
            
            for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
            {
                if (obj == null) continue;
                if (!(obj instanceof L2Attackable || obj instanceof L2PlayableInstance)) continue;
                if (obj == cha) continue;
                target = (L2Character) obj;
                boolean targetInPvP = ZoneManager.getInstance().checkIfInZonePvP(obj)
                                 && !SiegeManager.getInstance().checkIfInZone(obj);
                
                if (!GeoData.getInstance().canSeeTarget(activeChar, target))
                    continue;
                
                if(!target.isAlikeDead() && (target != activeChar))   
                {
                    if (!Util.checkIfInRange(radius, obj, cha, true))
                      continue;
                    
                    if (src != null) // caster is l2playableinstance and exists
                    {
                        if(obj instanceof L2PcInstance)
                        { 
                            L2PcInstance trg = (L2PcInstance)obj;
                            if (trg == src) continue;
                            if((src.getParty() != null && trg.getParty() != null) && 
                                src.getParty().getPartyLeaderOID() == trg.getParty().getPartyLeaderOID()) 
                                continue;
							
							if(trg.getInPeaceZone()) continue;
                            
                            if(!srcInPvP && !targetInPvP)
                            {
                                if(src.getAllyId() == trg.getAllyId() && src.getAllyId() != 0) 
                                    continue;
                                
                                if(src.getClan() != null && trg.getClan() != null)
                                {
                                    if(src.getClan().getClanId() == trg.getClan().getClanId()) 
                                      continue;
                                }
                                   
                                if(!src.checkPvpSkill(obj, this)) 
                                    continue;
                            }
                        }
                        if(obj instanceof L2Summon)
                        {
                            L2PcInstance trg = ((L2Summon)obj).getOwner();
                            if (trg == src) continue; 
                            
                            if((src.getParty() != null && trg.getParty() != null) && 
                                    src.getParty().getPartyLeaderOID() == trg.getParty().getPartyLeaderOID()) 
                                continue;
                             
                            if(!srcInPvP && !targetInPvP)
                            {
                                if(src.getAllyId() == trg.getAllyId() && src.getAllyId() != 0) 
                                    continue;
                             
                                if(trg.getInPeaceZone()) continue;
                             
                                if(src.getClan() != null && trg.getClan() != null)
                                {
                                    if(src.getClan().getClanId() == trg.getClan().getClanId()) 
                                        continue;
                                }
                             
                                if(!src.checkPvpSkill(trg, this)) 
                                    continue;
                            
                            }
                        }
                    }
                    else
                    // Skill user is not L2PlayableInstance
                    {
                        // If effect starts at L2PlayableInstance and object is not L2PlayableInstance
                        if (effectOriginIsL2PlayableInstance && !(obj instanceof L2PlayableInstance))
                            continue;
                    }
                    
                    targetList.add((L2Character)obj);
                }
            }
            
            if (targetList.size() == 0)
                return null;
            
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        case TARGET_AREA_UNDEAD:
        {
            L2Character cha;
            int radius = getSkillRadius();
            if (getCastRange() >= 0 && (target instanceof L2NpcInstance || target instanceof L2SummonInstance)
                   && target.isUndead() && !target.isAlikeDead())
            {
                cha = target;

                if (onlyFirst == false) targetList.add(cha); // Add target to target list
                else return new L2Character[] {cha};

            }
            else cha = activeChar;

            if (cha != null && cha.getKnownList() != null)
                for (L2Object obj : cha.getKnownList().getKnownObjects().values())
                {
                    if (obj == null) continue;
                    if (obj instanceof L2NpcInstance)
                        target = (L2NpcInstance) obj;
                    else if (obj instanceof L2SummonInstance)
                        target = (L2SummonInstance) obj;
                    else continue;
                    
                    if (!(target instanceof L2DoorInstance) && !GeoData.getInstance().canSeeTarget(activeChar, target))
                        continue;
                    
                    if (!target.isAlikeDead()) // If target is not dead/fake death and not self
                    {
                        if (!target.isUndead()) continue;
                        if (!Util.checkIfInRange(radius, cha, obj, true)) // Go to next obj if obj isn't in range
                            continue;

                        if (onlyFirst == false) targetList.add((L2Character) obj); // Add obj to target lists
                        else return new L2Character[] {(L2Character) obj};
                    }
                }

            if (targetList.size() == 0) return null;
            return targetList.toArray(new L2Character[targetList.size()]);
        }        
        case TARGET_PARTY:
        {
            if (onlyFirst)
                return new L2Character[]{activeChar};
            
            targetList.add(activeChar);

            L2PcInstance player = null;

            if (activeChar instanceof L2Summon) 
            {
                player = ((L2Summon)activeChar).getOwner();
                targetList.add(player);
            }
            else if (activeChar instanceof L2PcInstance)
            {
                player = (L2PcInstance)activeChar;
                if (activeChar.getPet() != null)
                    targetList.add(activeChar.getPet());
            }

            if (activeChar.getParty() != null)
            {
                // Get all visible objects in a spheric area near the L2Character
                // Get a list of Party Members
                List<L2PcInstance> partyList = activeChar.getParty().getPartyMembers();

                for(L2PcInstance partyMember : partyList)
                {
                    if (partyMember == null || partyMember == player) continue;
                    if (player != null && player.isInDuel() && player.getDuelId() != partyMember.getDuelId())
                        continue;

                    if (!partyMember.isDead()
                            && Util.checkIfInRange(getSkillRadius(), activeChar, partyMember, true))
                    {
                        targetList.add(partyMember);

                        if (partyMember.getPet() != null && !partyMember.getPet().isDead())
                        {
                            targetList.add(partyMember.getPet());
                        }
                    }
                }
            }
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        case TARGET_PARTY_MEMBER:
        {
            if ((target != null
                    && target == activeChar) 
                || (target != null
                        && activeChar.getParty() != null
                        && target.getParty() != null
                        && activeChar.getParty().getPartyLeaderOID() == target.getParty().getPartyLeaderOID())
                || (target != null
                        && activeChar instanceof L2PcInstance
                        && target instanceof L2Summon
                        && activeChar.getPet() == target)
                || (target != null
                        && activeChar instanceof L2Summon
                        && target instanceof L2PcInstance
                        && activeChar == target.getPet()))
            {
                if (!target.isDead())
                {
                    // If a target is found, return it in a table else send a system message TARGET_IS_INCORRECT
                    return new L2Character[]{target};
                }
                else
                    return null;
            }
            else 
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }
        }
        case TARGET_PARTY_OTHER:
        {
            if (target != null && target != activeChar
                    && target instanceof L2PcInstance
                    && activeChar.getParty() != null && target.getParty() != null
                    && activeChar.getParty().getPartyLeaderOID() == target.getParty().getPartyLeaderOID())
            {
                if (!target.isDead())
                {
                    // If a target is found, return it in a table else send a system message TARGET_IS_INCORRECT
                    return new L2Character[]{target};
                }
                else
                    return null;
            }
            else
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }
        }
        case TARGET_CORPSE_ALLY:
        case TARGET_ALLY:
        {
            if (activeChar instanceof L2PcInstance)
            {
                int radius = getSkillRadius();
                L2PcInstance player = (L2PcInstance) activeChar;
                L2Clan clan = player.getClan();
                
                if (player.isInOlympiadMode())
                   return new L2Character[] {player};

                if (targetType != SkillTargetType.TARGET_CORPSE_ALLY)
                {
                    if (onlyFirst == false) targetList.add(player);
                    else return new L2Character[] {player};
                }

                if (clan != null)
                {
                    // Get all visible objects in a spheric area near the L2Character
                    // Get Clan Members
                    for (L2Object newTarget : activeChar.getKnownList().getKnownObjects().values())
                    {
                        if (newTarget == null || !(newTarget instanceof L2PcInstance)) continue;
                        if ((((L2PcInstance) newTarget).getAllyId() == 0 || ((L2PcInstance) newTarget).getAllyId() != player.getAllyId())
                            && (((L2PcInstance) newTarget).getClan() == null || ((L2PcInstance) newTarget).getClanId() != player.getClanId()))
                            continue;
                        if (player.isInDuel() &&
                            (player.getDuelId() != ((L2PcInstance)newTarget).getDuelId() ||
                            (player.getParty() != null && !player.getParty().getPartyMembers().contains(newTarget))))
                            continue;
                        if (targetType == SkillTargetType.TARGET_CORPSE_ALLY)
                        {
                            if (!((L2PcInstance) newTarget).isDead()) 
                                continue;
                            if (getSkillType() == SkillType.RESURRECT)
                            {
                                // check target is not in a active siege zone
                                Siege siege = SiegeManager.getInstance().getSiege(newTarget);
                                if (siege != null && siege.getIsInProgress())
                                    continue;
                            }
                        }

                        if (!Util.checkIfInRange(radius, activeChar, newTarget, true)) continue;

                        // Don't add this target if this is a Pc->Pc pvp casting and pvp condition not met
                        if (!player.checkPvpSkill(newTarget, this)) continue;

                        if (onlyFirst == false) targetList.add((L2Character) newTarget);
                        else return new L2Character[] {(L2Character) newTarget};

                    }
                }
            }
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        case TARGET_ENEMY_ALLY:
        {
            //int charX, charY, charZ, targetX, targetY, targetZ, dx, dy, dz;
            int radius = getSkillRadius();
            L2Character newTarget;
            
            if (getCastRange() > -1 && target != null)
            {
                newTarget = target;
            }
            else
                newTarget = activeChar;
            
            if (newTarget != activeChar || isSkillTypeOffensive())
                targetList.add(newTarget);
            
            for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(radius))
            {
                if (obj == newTarget || obj == activeChar)
                        continue;
                
                if (obj instanceof L2Attackable)
                {
                    if(!obj.isAlikeDead())
                    {
                        // Don't add this target if this is a PC->PC pvp casting and pvp condition not met
                        if (activeChar instanceof L2PcInstance && !((L2PcInstance)activeChar).checkPvpSkill(obj, this))
                            continue;
                        
                        // check if both attacker and target are L2PcInstances and if they are in same party or clan
                        if (   (activeChar instanceof L2PcInstance && obj instanceof L2PcInstance) && 
                             (((L2PcInstance)activeChar).getClanId() != ((L2PcInstance)obj).getClanId() ||
                             (((L2PcInstance)activeChar).getAllyId() != ((L2PcInstance)obj).getAllyId() &&
                             ((((L2PcInstance)activeChar).getParty() != null && ((L2PcInstance)obj).getParty() != null) &&
                             ((L2PcInstance)activeChar).getParty().getPartyLeaderOID() != ((L2PcInstance)obj).getParty().getPartyLeaderOID() ))))
                            continue;
                        
                        targetList.add(obj);
                    }
                }
            }
        }
        case TARGET_CORPSE_CLAN:
        case TARGET_CLAN:
        {
            if (activeChar instanceof L2PcInstance)
            {
                int radius = getSkillRadius();
                L2PcInstance player = (L2PcInstance) activeChar;
                L2Clan clan = player.getClan();
                
                if (player.isInOlympiadMode())
                   return new L2Character[] {player};

                if (targetType != SkillTargetType.TARGET_CORPSE_CLAN)
                {
                    if (onlyFirst == false) targetList.add(player);
                    else return new L2Character[] {player};
                }

                if (clan != null)
                {
                    // Get all visible objects in a spheric area near the L2Character
                    // Get Clan Members
                    for (L2ClanMember member : clan.getMembers())
                    {
                        L2PcInstance newTarget = member.getPlayerInstance();

                        if (newTarget == null) continue;

                        if (targetType == SkillTargetType.TARGET_CORPSE_CLAN)
                        {
                            if (!newTarget.isDead())
                                continue;
                            if (getSkillType() == SkillType.RESURRECT)
                            {
                                // check target is not in a active siege zone
                                Siege siege = SiegeManager.getInstance().getSiege(newTarget);
                                if (siege != null && siege.getIsInProgress())
                                    continue;
                            }
                        }

                        if (player.isInDuel() && (player.getDuelId() != newTarget.getDuelId() ||
                            (player.getParty() != null && !player.getParty().getPartyMembers().contains(newTarget))))
                            continue;

                        if (!Util.checkIfInRange(radius, activeChar, newTarget, true)) continue;

                        // Don't add this target if this is a Pc->Pc pvp casting and pvp condition not met
                        if (!player.checkPvpSkill(newTarget, this)) continue;

                        if (onlyFirst == false) targetList.add(newTarget);
                        else return new L2Character[] {newTarget};

                    }
                }
            }

            return targetList.toArray(new L2Character[targetList.size()]);
        }
        case TARGET_CORPSE_PLAYER:
        {
            if (target != null && target.isDead())
            {
                L2PcInstance player = null;

                if (activeChar instanceof L2PcInstance) player = (L2PcInstance) activeChar;
                L2PcInstance targetPlayer = null;

                if (target instanceof L2PcInstance) targetPlayer = (L2PcInstance) target;
                L2PetInstance targetPet = null;

                if (target instanceof L2PetInstance) targetPet = (L2PetInstance) target;

                if (player != null && (targetPlayer != null || targetPet != null))
                {
                    boolean condGood = true;

                    if (getSkillType() == SkillType.RESURRECT)
                    {
                        // check target is not in a active siege zone
                        Siege siege = null;

                        if (targetPlayer != null) siege = SiegeManager.getInstance().getSiege(targetPlayer);
                        else if (targetPet != null)
                            siege = SiegeManager.getInstance().getSiege(targetPet);

                        if (siege != null && siege.getIsInProgress())
                        {
                            condGood = false;
                            player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE));
                        }

                        if (targetPlayer != null)
                        {
                            if (targetPlayer.isReviveRequested())
                            {
                                if (targetPlayer.isRevivingPet())
                                    player.sendPacket(new SystemMessage(SystemMessageId.MASTER_CANNOT_RES)); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
                                else
                                    player.sendPacket(new SystemMessage(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED)); // Resurrection is already been proposed.
                                condGood = false;
                            }
                        }
                        else if (targetPet != null)
                        {
                            if (targetPet.getOwner() != player)
                            {
                                condGood = false;
                                player.sendMessage("You are not the owner of this pet");
                            }
                        }
                    }

                    if (condGood)
                    {
                        if (onlyFirst == false)
                        {
                            targetList.add(target);
                            return targetList.toArray(new L2Object[targetList.size()]);
                        }
                        else return new L2Character[] {target};

                    }
                }
            }
            activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
            return null;
        }
        case TARGET_CORPSE_MOB:
        {
            if (!(target instanceof L2Attackable) || !target.isDead())
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }

            if (onlyFirst == false)
            {
                targetList.add(target);
                return targetList.toArray(new L2Object[targetList.size()]);
            }
            else return new L2Character[] {target};

        }
        case TARGET_AREA_CORPSE_MOB:
        {
            if ((!(target instanceof L2Attackable)) || !target.isDead())
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }

            if (onlyFirst == false) targetList.add(target);
            else return new L2Character[] {target};
            
            boolean srcInPvP = ZoneManager.getInstance().checkIfInZonePvP(activeChar);
            
            L2PcInstance src = null;
            if (activeChar instanceof L2PcInstance)
                src = (L2PcInstance)activeChar;
            L2PcInstance trg = null;

            int radius = getSkillRadius();
            if (activeChar.getKnownList() != null)
                for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
                {
                    boolean targetInPvP = ZoneManager.getInstance().checkIfInZonePvP(obj)
                              && !SiegeManager.getInstance().checkIfInZone(obj);

                    if (obj == null) continue;
                    if (!(obj instanceof L2Attackable || obj instanceof L2PlayableInstance) || ((L2Character) obj).isDead()
                        || ((L2Character) obj) == activeChar) continue;

                    if (!Util.checkIfInRange(radius, target, obj, true)) continue;
                    
                    if (!(target instanceof L2DoorInstance) && !GeoData.getInstance().canSeeTarget(activeChar, obj))
                        continue;

                    if (src != null) // caster is l2playableinstance and exists
                    {
                        if(obj instanceof L2PcInstance)
                        { 
                            trg = (L2PcInstance)obj;

                            if((src.getParty() != null && trg.getParty() != null) && 
                                src.getParty().getPartyLeaderOID() == trg.getParty().getPartyLeaderOID()) 
                                continue;

                            if(trg.getInPeaceZone()) continue;

                            if(!srcInPvP && !targetInPvP)
                            {
                                if(src.getAllyId() == trg.getAllyId() && src.getAllyId() != 0) 
                                    continue;

                                if(src.getClan() != null && trg.getClan() != null)
                                {
                                    if(src.getClan().getClanId() == trg.getClan().getClanId()) 
                                        continue;
                                }

                                if(!src.checkPvpSkill(obj, this)) 
                                    continue;
                            }

                            if(ZoneManager.getInstance().checkIfInZonePeace(obj)) continue;
                        }
                        else if(obj instanceof L2Summon)
                        {
                            trg = ((L2Summon)obj).getOwner();

                            if((src.getParty() != null && trg.getParty() != null) && 
                                src.getParty().getPartyLeaderOID() == trg.getParty().getPartyLeaderOID()) 
                                continue;

                            if(trg.getInPeaceZone()) continue;

                            if(!srcInPvP && !targetInPvP)
                            {
                                if(src.getAllyId() == trg.getAllyId() && src.getAllyId() != 0) 
                                    continue;

                                if(src.getClan() != null && trg.getClan() != null)
                                {
                                    if(src.getClan().getClanId() == trg.getClan().getClanId()) 
                                        continue;
                                }

                                if(!src.checkPvpSkill(trg, this)) 
                                    continue;
                            }

                            if(ZoneManager.getInstance().checkIfInZonePeace(obj)) continue;
                        }
                    }

                    targetList.add((L2Character) obj);
                }

            if (targetList.size() == 0) return null;
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        case TARGET_UNLOCKABLE:
        {
            if (!(target instanceof L2DoorInstance) && !(target instanceof L2ChestInstance))
            {
                //activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
                return null;
            }

            if (onlyFirst == false)
            {
                targetList.add(target);
                return targetList.toArray(new L2Object[targetList.size()]);
            }
            else return new L2Character[] {target};

        }
        case TARGET_ITEM:
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Target type of skill is not currently handled.");
            activeChar.sendPacket(sm);
            return null;
        }
        case TARGET_ENEMY_SUMMON: 
        { 
            if(target != null && target instanceof L2Summon) 
            {       
                L2Summon targetSummon = (L2Summon)target;
                if (activeChar instanceof L2PcInstance && activeChar.getPet() != targetSummon && !targetSummon.isDead()
                        && (targetSummon.getOwner().getPvpFlag() != 0 || targetSummon.getOwner().getKarma() > 0)
                        || (targetSummon.getOwner().getInPvpZone() == true && ((L2PcInstance)activeChar).getInPvpZone()== true))
                   return new L2Character[]{targetSummon}; 
            } 
            return null; 
        }
        case TARGET_GATE:
        {
            // Check for null target or any other invalid target
            if (target == null
                       || target.isDead()
                       || !(target instanceof L2DoorInstance))
            {
               activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
               return null;
            }
            // If a target is found, return it in a table else send a system message TARGET_IS_INCORRECT
            return new L2Character[] {target};
        }
        case TARGET_KNOWNLIST:
        {
            if (target != null && target.getKnownList() != null)
                for (L2Object obj : target.getKnownList().getKnownObjects().values())
                {
                    if (obj == null) continue;
                    if (obj != null && (obj instanceof L2Attackable || obj instanceof L2PlayableInstance))
                        return new L2Character[] {(L2Character) obj};
                }

            if (targetList.size() == 0) return null;
            return targetList.toArray(new L2Character[targetList.size()]);
        }
        //L2EMU_ADD
        case TARGET_PIG:
        {
        	if(target != null && target instanceof L2NpcInstance){
        		int npcId = ((L2NpcInstance)target).getNpcId();
        		if((npcId >= 13031) && (npcId <= 13035)){
        			return new L2Character[]{target};
        		}
        	}	
        	return null;
        }
        //L2EMU_ADD
        default:
        {
            if (activeChar instanceof L2PcInstance || _log.isDebugEnabled()) // normally log only player skills errors
                _log.error("Target type of skill Id " + _id + " is not implemented.");
            return null;
        }
    }//end switch
    }
    
	// [L2J_JP ADD SANDMAN START]
	public final L2Object[] getAreaTargetList(L2Character activeChar)
	{
	    List<L2Character> targetList = new FastList<L2Character>();
	    L2Object target;
	    L2PcInstance tgOwner;
	    L2Clan acClan;
	    L2Clan tgClan;
	    L2Party acPt = activeChar.getParty();
	    int radius = getSkillRadius();
	
	    if (getCastRange() <= 0 || (getTargetType() == SkillTargetType.TARGET_AURA)) target = activeChar;
	    else target = activeChar.getTarget();
	
	    if (target == null || !(target instanceof L2Character))
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    if ((getTargetType() == SkillTargetType.TARGET_AREA) && (target.getObjectId() != activeChar.getObjectId()))
	    {
	        if (!((L2Character) target).isAlikeDead()) targetList.add((L2Character) target);
	        else
	        {
	            activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	            return null;
	        }
	    }
	
	    if (!(activeChar instanceof L2PlayableInstance))
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (obj instanceof L2PlayableInstance)
	            {
	                if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	        }
	        if (targetList.size() == 0) return null;
	        return targetList.toArray(new L2Character[targetList.size()]);
	    }
	
	    if (activeChar instanceof L2PcInstance) acClan = ((L2PcInstance) activeChar).getClan();
	    else if (activeChar instanceof L2Summon) acClan = ((L2Summon) activeChar).getOwner().getClan();
	    else
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    if (SiegeManager.getInstance().checkIfInZone(activeChar))
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	
	            if (obj instanceof L2PcInstance)
	            {
	                tgClan = ((L2PcInstance) obj).getClan();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(obj)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (tgClan != null)
	                {
	                    if (tgClan.getClanId() == acClan.getClanId()) continue;
	                    else if (tgClan.getAllyId() == acClan.getAllyId()) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Summon)
	            {
	                tgOwner = ((L2Summon) obj).getOwner();
	                tgClan = tgOwner.getClan();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(tgOwner)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (tgClan != null)
	                {
	                    if (tgClan.getClanId() == acClan.getClanId()) continue;
	                    else if (tgClan.getAllyId() == acClan.getAllyId()) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Attackable)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else
	            {
	                continue;
	            }
	        }
	    }
	    else if ((ZoneManager.getInstance().checkIfInZone(
	                                                      ZoneType.OlympiadStadia,
	                                                      activeChar.getX(), activeChar.getY()))
	        || (ZoneManager.getInstance().checkIfInZone(
	                                                    ZoneType.Arena,
	                                                    activeChar.getX(), activeChar.getY()))
	        || ((ZoneManager.getInstance().checkIfInZone(ZoneType.BossDangeon,"Four Sepulcher", activeChar) &&
	        		(activeChar.getZ() >= -7250 && activeChar.getZ() <= -6841)))
	    	)
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	
	            if (obj instanceof L2PcInstance)
	            {
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(obj)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Summon)
	            {
	                tgOwner = ((L2Summon) obj).getOwner();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(tgOwner)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Attackable)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else
	            {
	                continue;
	            }
	        }
	    }
	    else
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	
	            if (obj instanceof L2MonsterInstance)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	        }
	    }
	
	    if (targetList.size() == 0)
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	public final L2Object[] getMultiFaceTargetList(L2Character activeChar)
	{
	    List<L2Character> targetList = new FastList<L2Character>();
	    L2Object target;
	    L2Object FirstTarget;
	    L2PcInstance tgOwner;
	    L2Clan acClan;
	    L2Clan tgClan;
	    L2Party acPt = activeChar.getParty();
	    int radius = getSkillRadius();
	
	    if (getCastRange() <= 0) target = activeChar;
	    else target = activeChar.getTarget();
	    FirstTarget = target;
	
	    if (target == null || !(target instanceof L2Character))
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    int newHeading = getNewHeadingToTarget(activeChar, (L2Character) target);
	
	    if (target.getObjectId() != activeChar.getObjectId())
	    {
	        if (!((L2Character) target).isAlikeDead()) targetList.add((L2Character) target);
	        else
	        {
	            activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	            return null;
	        }
	    }
	
	    if (!(activeChar instanceof L2PlayableInstance))
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (obj instanceof L2PlayableInstance)
	            {
	                if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	                else if (isBehindFromCaster(newHeading, (L2Character) FirstTarget, (L2Character) target)) continue;
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	
	            }
	        }
	        if (targetList.size() == 0) return null;
	        return targetList.toArray(new L2Character[targetList.size()]);
	    }
	
	    if (activeChar instanceof L2PcInstance) acClan = ((L2PcInstance) activeChar).getClan();
	    else if (activeChar instanceof L2Summon) acClan = ((L2Summon) activeChar).getOwner().getClan();
	    else
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    if (SiegeManager.getInstance().checkIfInZone(activeChar))
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(obj instanceof L2PlayableInstance))
	                continue;
	            if (!(Util.checkIfInRange(radius, target, obj, true)))
	            continue;
	            else if (isBehindFromCaster(newHeading, (L2Character) FirstTarget, (L2Character) obj))
	                continue;
	
	            if (obj instanceof L2PcInstance)
	            {
	                tgClan = ((L2PcInstance) obj).getClan();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(obj)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (tgClan != null)
	                {
	                    if (tgClan.getClanId() == acClan.getClanId()) continue;
	                    else if (tgClan.getAllyId() == acClan.getAllyId()) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Summon)
	            {
	                tgOwner = ((L2Summon) obj).getOwner();
	                tgClan = tgOwner.getClan();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(tgOwner)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (tgClan != null)
	                {
	                    if (tgClan.getClanId() == acClan.getClanId()) continue;
	                    else if (tgClan.getAllyId() == acClan.getAllyId()) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Attackable)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else
	            {
	                continue;
	            }
	        }
	    }
	    else if ((ZoneManager.getInstance().checkIfInZone(ZoneType.OlympiadStadia,
	                                                      activeChar.getX(), activeChar.getY()))
	        || (ZoneManager.getInstance().checkIfInZone(ZoneType.Arena,
	                                                    activeChar.getX(), activeChar.getY()))
	        || ((FourSepulchersManager.getInstance().checkIfInDangeon(activeChar) &&
	        		(activeChar.getZ() >= -7250 && activeChar.getZ() <= -6841)))
	    	)
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(obj instanceof L2PlayableInstance))
	                continue;
	            if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	            else if (isBehindFromCaster(newHeading, (L2Character) FirstTarget, (L2Character) obj)) continue;
	
	            if (obj instanceof L2PcInstance)
	            {
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(obj)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Summon)
	            {
	                tgOwner = ((L2Summon) obj).getOwner();
	
	                if (acPt != null)
	                {
	                    if (activeChar.getParty().getPartyMembers().contains(tgOwner)) continue;
	                    else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	                }
	                else if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else if (obj instanceof L2Attackable)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	            else
	            {
	                continue;
	            }
	        }
	    }
	    else
	    {
	        for (L2Object obj : activeChar.getKnownList().getKnownObjects().values())
	        {
	            if (!(obj instanceof L2PlayableInstance))
	                continue;
	            if (!(Util.checkIfInRange(radius, target, obj, true))) continue;
	            else if (isBehindFromCaster(newHeading, (L2Character) FirstTarget, (L2Character) obj)) continue;
	
	            if (obj instanceof L2MonsterInstance)
	            {
	                if (!((L2Character) obj).isAlikeDead()) targetList.add((L2Character) obj);
	            }
	        }
	    }
	
	    if (targetList.size() == 0)
	    {
	        activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
	        return null;
	    }
	
	    return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	protected int getNewHeadingToTarget(L2Character caster, L2Character target)
	{
	    if (caster == null || target == null) return 0;
	
	    double befHeading = Util.convertHeadingToDegree(caster.getHeading());
	    if (befHeading > 360) befHeading -= 360;
	
	    int dx = caster.getX() - target.getX();
	    int dy = caster.getY() - target.getY();
	
	    double dist = Math.sqrt(dx * dx + dy * dy);
	
	    if (dist == 0) dist = 0.01;
	
	    double sin = dy / dist;
	    double cos = dx / dist;
	    int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
	
	    return heading;
	
	}
	
	public boolean isBehindFromCaster(int heading, L2Character caster, L2Character target)
	{
	    if (caster == null || target == null) return true;
	
	    double befHeading = Util.convertHeadingToDegree(heading);
	    if (befHeading > 360) befHeading -= 360;
	    else if (befHeading < 0) befHeading += 360;
	
	    int dx = caster.getX() - target.getX();
	    int dy = caster.getY() - target.getY();
	
	    double dist = Math.sqrt(dx * dx + dy * dy);
	
	    if (dist == 0) dist = 0.01;
	
	    double sin = dy / dist;
	    double cos = dx / dist;
	    int newheading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
	
	    double aftHeading = Util.convertHeadingToDegree(newheading);
	    if (aftHeading > 360) aftHeading -= 360;
	    else if (aftHeading < 0) aftHeading += 360;
	
	    double diffHeading = Math.abs(aftHeading - befHeading);
	    if (diffHeading > 360) diffHeading -= 360;
	    else if (diffHeading < 0) diffHeading += 360;
	
	    if ((diffHeading > 90) && (diffHeading < 270)) return true;
	    else return false;
	
	}
	// [L2J_JP ADD SANDMAN END]
    public final L2Object[] getTargetList(L2Character activeChar)
    {
       return getTargetList(activeChar, false); 
    }
    
    public final L2Object getFirstOfTargetList(L2Character activeChar)
    {
        L2Object[] targets;
        
        targets=getTargetList(activeChar, true);
        
        if(targets == null || targets.length == 0)
            return null;
        else
            return targets[0];
    }

    public final Func[] getStatFuncs(@SuppressWarnings("unused") L2Effect effect, L2Character player)
    {
        if (
                !(player instanceof L2PcInstance) &&
                !(player instanceof L2Attackable) &&
                !(player instanceof L2Summon)
            )
            return _emptyFunctionSet;
        if (_funcTemplates == null)
            return _emptyFunctionSet;
        FastList<Func> funcs = new FastList<Func>();
        for (FuncTemplate t : _funcTemplates) {
            Env env = new Env();
            env.player = player;
            env.skill = this;
            Func f = t.getFunc(env, this); // skill is owner
            if (f != null)
                funcs.add(f);
        }
        if (funcs.size() == 0)
            return _emptyFunctionSet;
        return funcs.toArray(new Func[funcs.size()]);
    }

    public boolean hasEffects()
        {
            return (_effectTemplates != null && _effectTemplates.length > 0);
        }
    
    public final L2Effect[] getEffects(L2Character effector, L2Character effected)
    {
        if (isPassive())
            return _emptyEffectSet;
        
        if (_effectTemplates == null) 
           return _emptyEffectSet;
        
        if ((effector != effected) && effected.isInvul())
            return _emptyEffectSet;
            
        FastList<L2Effect> effects = new FastList<L2Effect>();
        
        for (EffectTemplate et : _effectTemplates) 
        {
            Env env = new Env();
            env.player = effector;
            env.target = effected;
            env.skill = this;
            L2Effect e = et.getEffect(env);
            if (e != null)
                effects.add(e);
        }
        
        if (effects.size() == 0)
            return _emptyEffectSet;
        
        return effects.toArray(new L2Effect[effects.size()]);
    }
    
    public final L2Effect[] getEffectsSelf(L2Character effector)
    {
        if (isPassive()) return _emptyEffectSet;
    
        if (_effectTemplatesSelf == null) return _emptyEffectSet;

        FastList<L2Effect> effects = new FastList<L2Effect>();

        for (EffectTemplate et : _effectTemplatesSelf)
        {
            Env env = new Env();
            env.player = effector;
            env.target = effector;
            env.skill = this;
            L2Effect e = et.getEffect(env);
            if (e != null)
            {
                //Implements effect charge
                if (e.getEffectType()== L2Effect.EffectType.CHARGE)
                {
                    env.skill = SkillTable.getInstance().getInfo(8, effector.getSkillLevel(8));
                    EffectCharge effect = (EffectCharge) env.target.getFirstEffect(L2Effect.EffectType.CHARGE);
                    if (effect != null) 
                    {
                        if (effect.numCharges < _numCharges)
                        {
                            effect.numCharges++;
                            if (env.target instanceof L2PcInstance)
                            {
                                env.target.sendPacket(new EtcStatusUpdate((L2PcInstance)env.target));
                                SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1);
                                sm.addNumber(effect.numCharges);
                                env.target.sendPacket(sm);
                            }
                        }
                    }
                    else effects.add(e);
                }
                else effects.add(e);
            }
        }
        if (effects.size() == 0) return _emptyEffectSet;

        return effects.toArray(new L2Effect[effects.size()]);
    }

    public final void attach(FuncTemplate f)
    {
        if (_funcTemplates == null)
        {
            _funcTemplates = new FuncTemplate[]{f};
        }
        else
        {
            int len = _funcTemplates.length;
            FuncTemplate[] tmp = new FuncTemplate[len+1];
            System.arraycopy(_funcTemplates, 0, tmp, 0, len);
            tmp[len] = f;
            _funcTemplates = tmp;
        }
    }

    public final void attach(EffectTemplate effect)
    {
        if (_effectTemplates == null)
        {
            _effectTemplates = new EffectTemplate[]{effect};
        }
        else
        {
            int len = _effectTemplates.length;
            EffectTemplate[] tmp = new EffectTemplate[len+1];
            System.arraycopy(_effectTemplates, 0, tmp, 0, len);
            tmp[len] = effect;
            _effectTemplates = tmp;
        }
    }
    
    public final void attachSelf(EffectTemplate effect)
    {
        if (_effectTemplatesSelf == null)
        {
            _effectTemplatesSelf = new EffectTemplate[] {effect};
        }
        else
        {
            int len = _effectTemplatesSelf.length;
            EffectTemplate[] tmp = new EffectTemplate[len + 1];
            System.arraycopy(_effectTemplatesSelf, 0, tmp, 0, len);
            tmp[len] = effect;
            _effectTemplatesSelf = tmp;
        }
    }

    public final void attach(Condition c, boolean itemOrWeapon)
    {
       if(itemOrWeapon) _itemPreCondition = c;
       else _preCondition = c;
    }

    @Override
    public String toString()
    {
        return ""+_name+"[id="+_id+",lvl="+_level+"]";
    }
}
