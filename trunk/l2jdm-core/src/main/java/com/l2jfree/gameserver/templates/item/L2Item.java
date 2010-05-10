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
package com.l2jfree.gameserver.templates.item;

import org.apache.commons.lang.ArrayUtils;

import com.l2jfree.Config;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Character;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.actor.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.skills.Env;
import com.l2jfree.gameserver.skills.conditions.Condition;
import com.l2jfree.gameserver.skills.funcs.FuncOwner;
import com.l2jfree.gameserver.templates.StatsSet;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of :
 * <LI>L2Armor</LI>
 * <LI>L2EtcItem</LI>
 * <LI>L2Weapon</LI>
 * @version $Revision: 1.7.2.2.2.5 $ $Date: 2005/04/06 18:25:18 $
 */
public abstract class L2Item implements FuncOwner
{
	public static final int				TYPE1_WEAPON_RING_EARRING_NECKLACE	= 0;
	public static final int				TYPE1_SHIELD_ARMOR					= 1;
	public static final int				TYPE1_ITEM_QUESTITEM_ADENA			= 4;

	public static final int				TYPE2_WEAPON						= 0;
	public static final int				TYPE2_SHIELD_ARMOR					= 1;
	public static final int				TYPE2_ACCESSORY						= 2;
	public static final int				TYPE2_QUEST							= 3;
	public static final int				TYPE2_MONEY							= 4;
	public static final int				TYPE2_OTHER							= 5;
	public static final int				TYPE2_PET_WOLF						= 6;
	public static final int				TYPE2_PET_HATCHLING					= 7;
	public static final int				TYPE2_PET_STRIDER					= 8;
	public static final int				TYPE2_PET_BABY						= 9;
	public static final int				TYPE2_PET_EVOLVEDWOLF				= 10;

	public static final int				SLOT_NONE							= 0x0000;
	public static final int				SLOT_UNDERWEAR						= 0x0001;
	public static final int				SLOT_R_EAR							= 0x0002;
	public static final int				SLOT_L_EAR							= 0x0004;
	public static final int				SLOT_LR_EAR							= 0x00006;
	public static final int				SLOT_NECK							= 0x0008;
	public static final int				SLOT_R_FINGER						= 0x0010;
	public static final int				SLOT_L_FINGER						= 0x0020;
	public static final int				SLOT_LR_FINGER						= 0x0030;
	public static final int				SLOT_HEAD							= 0x0040;
	public static final int				SLOT_R_HAND							= 0x0080;
	public static final int				SLOT_L_HAND							= 0x0100;
	public static final int				SLOT_GLOVES							= 0x0200;
	public static final int				SLOT_CHEST							= 0x0400;
	public static final int				SLOT_LEGS							= 0x0800;
	public static final int				SLOT_FEET							= 0x1000;
	public static final int				SLOT_BACK							= 0x2000;
	public static final int				SLOT_LR_HAND						= 0x4000;
	public static final int				SLOT_FULL_ARMOR						= 0x8000;
	public static final int				SLOT_HAIR							= 0x010000;
	public static final int				SLOT_ALLDRESS						= 0x020000;
	public static final int				SLOT_HAIR2							= 0x040000;
	public static final int				SLOT_HAIRALL						= 0x080000;
	public static final int				SLOT_R_BRACELET						= 0x100000;
	public static final int				SLOT_L_BRACELET						= 0x200000;
	public static final int				SLOT_DECO							= 0x400000;
	public static final int 			SLOT_BELT 							= 0x10000000;
	
	public static final int				SLOT_WOLF							= -100;
	public static final int				SLOT_HATCHLING						= -101;
	public static final int				SLOT_STRIDER						= -102;
	public static final int				SLOT_BABYPET						= -103;
	public static final int				SLOT_GREATWOLF						= -104;

	public static final int				MATERIAL_STEEL						= 0x00;									// ??
	public static final int				MATERIAL_FINE_STEEL					= 0x01;									// ??
	public static final int				MATERIAL_BLOOD_STEEL				= 0x02;									// ??
	public static final int				MATERIAL_BRONZE						= 0x03;									// ??
	public static final int				MATERIAL_SILVER						= 0x04;									// ??
	public static final int				MATERIAL_GOLD						= 0x05;									// ??
	public static final int				MATERIAL_MITHRIL					= 0x06;									// ??
	public static final int				MATERIAL_ORIHARUKON					= 0x07;									// ??
	public static final int				MATERIAL_PAPER						= 0x08;									// ??
	public static final int				MATERIAL_WOOD						= 0x09;									// ??
	public static final int				MATERIAL_CLOTH						= 0x0a;									// ??
	public static final int				MATERIAL_LEATHER					= 0x0b;									// ??
	public static final int				MATERIAL_BONE						= 0x0c;									// ??
	public static final int				MATERIAL_HORN						= 0x0d;									// ??
	public static final int				MATERIAL_DAMASCUS					= 0x0e;									// ??
	public static final int				MATERIAL_ADAMANTAITE				= 0x0f;									// ??
	public static final int				MATERIAL_CHRYSOLITE					= 0x10;									// ??
	public static final int				MATERIAL_CRYSTAL					= 0x11;									// ??
	public static final int				MATERIAL_LIQUID						= 0x12;									// ??
	public static final int				MATERIAL_SCALE_OF_DRAGON			= 0x13;									// ??
	public static final int				MATERIAL_DYESTUFF					= 0x14;									// ??
	public static final int				MATERIAL_COBWEB						= 0x15;									// ??
	public static final int				MATERIAL_SEED						= 0x15;									// ??

	public static final int				CRYSTAL_NONE						= 0x00;									// ??
	public static final int				CRYSTAL_D							= 0x01;									// ??
	public static final int				CRYSTAL_C							= 0x02;									// ??
	public static final int				CRYSTAL_B							= 0x03;									// ??
	public static final int				CRYSTAL_A							= 0x04;									// ??
	public static final int				CRYSTAL_S							= 0x05;									// ??
	public static final int				CRYSTAL_S80							= 0x06;									// ??
	public static final int				CRYSTAL_S84							= 0x07;									// ??

	private static final int[] crystalItemId             = { 0, 1458, 1459, 1460, 1461, 1462 };
	private static final int[] crystalEnchantBonusArmor  = { 0,   11,    6,   11,   19,   25 };
	private static final int[] crystalEnchantBonusWeapon = { 0,   90,   45,   67,  144,  250 };

	private final int					_itemId;
	private final int					_itemDisplayId;
	private final String				_name;
	private final int					_type1;																		// needed for item list (inventory)
	private final int					_type2;																		// different lists for armor, weapon, etc
	private final int					_weight;
	private final boolean				_crystallizable;
	private final boolean				_stackable;
	private final int					_materialType;
	private final int					_crystalType;																	// default to none-grade
	private final int					_duration;
	private final long					_time;
	private final int					_bodyPart;
	private final int					_referencePrice;
	private final int					_crystalCount;
	private final boolean				_sellable;
	private final boolean				_dropable;
	private final boolean				_destroyable;
	private final boolean				_tradeable;
	private final boolean				_depositable;
	private final boolean				_isCommonItem;
	private final boolean				_heroItem;
	private final boolean				_pvpItem;

	protected final AbstractL2ItemType	_type;

	private Condition[] _preConditions = Condition.EMPTY_ARRAY;

	/**
	 * Constructor of the L2Item that fill class variables.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>type</LI>
	 * <LI>_itemId</LI>
	 * <LI>_name</LI>
	 * <LI>_type1 & _type2</LI>
	 * <LI>_weight</LI>
	 * <LI>_crystallizable</LI>
	 * <LI>_stackable</LI>
	 * <LI>_materialType & _crystalType & _crystlaCount</LI>
	 * <LI>_duration</LI>
	 * <LI>_bodypart</LI>
	 * <LI>_referencePrice</LI>
	 * <LI>_sellable</LI>
	 * 
	 * @param type : Enum designating the type of the item
	 * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	protected L2Item(AbstractL2ItemType type, StatsSet set)
	{
		_type = type;
		_itemId = set.getInteger("item_id");
		_itemDisplayId = set.getInteger("item_display_id");
		_name = set.getString("name").intern();
		_type1 = set.getInteger("type1"); // needed for item list (inventory)
		_type2 = set.getInteger("type2"); // different lists for armor, weapon, etc
		_weight = set.getInteger("weight");
		_crystallizable = set.getBool("crystallizable");
		_stackable = set.getBool("stackable", false);
		_materialType = set.getInteger("material");
		_crystalType = set.getInteger("crystal_type", CRYSTAL_NONE); // default to none-grade
		_duration = set.getInteger("duration");
		_time = set.getInteger("time");
		_bodyPart = set.getInteger("bodypart");
		_referencePrice = set.getInteger("price");
		_crystalCount = set.getInteger("crystal_count", 0);
		_sellable = set.getBool("sellable", true);
		_dropable = set.getBool("dropable", true);
		_destroyable = set.getBool("destroyable", true);
		_tradeable = set.getBool("tradeable", true);
		_depositable = set.getBool("depositable", true);
		_isCommonItem = (_itemId >= 12006 && _itemId <= 12361) || (_itemId >= 11605 && _itemId <= 12308)
				|| _name.startsWith("Common Item") || _name.startsWith("Standard Item");
		_heroItem = (_itemId >= 6611 && _itemId <= 6621) || (_itemId >= 9388 && _itemId <= 9390) || _itemId == 6842;
		_pvpItem = (_itemId >= 10667 && _itemId <= 10792) || (_itemId >= 10793 && _itemId <= 10835)
				|| (_itemId >= 12852 && _itemId <= 12977) || (_itemId >= 14363 && _itemId <= 14519)
				|| (_itemId >= 14520 && _itemId <= 14525) || _itemId == 14528 || _itemId == 14529 || _itemId == 14558;
	}

	/**
	 * Returns the itemType.
	 * @return AbstractL2ItemType
	 */
	public AbstractL2ItemType getItemType()
	{
		return _type;
	}

	/**
	 * Returns the duration of the item
	 * @return int
	 */
	public final int getDuration()
	{
		return _duration;
	}

	/**
	 * Returns the time of the item
	 * @return int
	 */
	public final long getTime()
	{
		return _time;
	}

	/**
	 * Returns the ID of the iden
	 * @return int
	 */
	public final int getItemId()
	{
		return _itemId;
	}

	public final int getItemDisplayId()
	{
		return _itemDisplayId;
	}
	
	/**
	 * Return the type of material of the item
	 * @return int
	 */
	public final int getMaterialType()
	{
		return _materialType;
	}

	/**
	 * Returns the type 2 of the item
	 * @return int
	 */
	public final int getType2()
	{
		return _type2;
	}

	/**
	 * Returns the weight of the item
	 * @return int
	 */
	public final int getWeight()
	{
		return _weight;
	}

	/**
	 * Returns if the item is crystallizable
	 * @return boolean
	 */
	public final boolean isCrystallizable()
	{
		return _crystallizable;
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final int getCrystalType()
	{
		return _crystalType;
	}

	/**
	 * Return the grade of crystal of the item and S if grade is higher than S
	 * @return int
	 */
	public final int getCrystalGrade()
	{
		return _crystalType > CRYSTAL_S ? CRYSTAL_S : _crystalType;
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final int getCrystalItemId()
	{
		return crystalItemId[getCrystalGrade()];
	}

	/**
	 * Returns the grade of the item.<BR><BR>
	 * <U><I>Concept :</I></U><BR>
	 * In fact, this fucntion returns the type of crystal of the item.
	 * @return int
	 */
	public final int getItemGrade()
	{
		return getCrystalType();
	}

	/**
	 * Returns the grade of the item.<BR><BR>
	 * For grades S80 and S84 return S
	 * @return int
	 */
	public final int getItemGradeSPlus()
	{
		/*
	        switch (getItemGrade())
	        {
	                case CRYSTAL_S80:
	                case CRYSTAL_S84:
	                        return CRYSTAL_S;
	            default:
	                        return getItemGrade();
	        }
	    */
		return getCrystalGrade();
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _crystalCount;
	}

	/**
	 * Returns the quantity of crystals for crystallization on specific enchant level
	 * @return int
	 */
	public final int getCrystalCount(int enchantLevel)
	{
		if (enchantLevel > 3)
			switch (_type2)
			{
			case TYPE2_SHIELD_ARMOR:
			case TYPE2_ACCESSORY:
				return _crystalCount + crystalEnchantBonusArmor[getCrystalGrade()] * (3 * enchantLevel - 6);
			case TYPE2_WEAPON:
				return _crystalCount + crystalEnchantBonusWeapon[getCrystalGrade()] * (2 * enchantLevel - 3);
			default:
				return _crystalCount;
			}
		else if (enchantLevel > 0)
			switch (_type2)
			{
			case TYPE2_SHIELD_ARMOR:
			case TYPE2_ACCESSORY:
				return _crystalCount + crystalEnchantBonusArmor[getCrystalGrade()] * enchantLevel;
			case TYPE2_WEAPON:
				return _crystalCount + crystalEnchantBonusWeapon[getCrystalGrade()] * enchantLevel;
			default:
				return _crystalCount;
			}
		else
			return _crystalCount;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	public final String getName()
	{
		return _name;
	}

	/**
	 * Return the part of the body used with the item.
	 * @return int
	 */
	public final int getBodyPart()
	{
		return _bodyPart;
	}

	/**
	 * Returns the type 1 of the item
	 * @return int
	 */
	public final int getType1()
	{
		return _type1;
	}

	/**
	 * Returns if the item is stackable
	 * @return boolean
	 */
	public final boolean isStackable()
	{
		return _stackable;
	}

	/**
	 * Returns if the item is consumable
	 * @return boolean
	 */
	public boolean isConsumable()
	{
		return false;
	}

	/**
	 * Returns if the item is a heroitem
	 * @return boolean
	 */
	public boolean isHeroItem()
	{
		return _heroItem;
	}

	/**
	 * Returns if item is pvp
	 * 
	 * @return
	 */
	public final boolean isPvpItem()
	{
		return _pvpItem;
	}

	public boolean isCommonItem()
	{
		return _isCommonItem;
	}

	/**
	 * Returns if item is common
	 * 
	 * @return boolean
	 */
	public final boolean isCommon()
	{
		return isCommonItem();
	}

	public boolean isEquipable()
	{
		return getBodyPart() != 0 && !(getItemType() instanceof L2EtcItemType);
	}

	/**
	 * Returns the price of reference of the item
	 * @return int
	 */
	public final int getReferencePrice()
	{
		return (isConsumable() ? (int) (_referencePrice * Config.RATE_CONSUMABLE_COST) : _referencePrice);
	}

	/**
	 * Returns if the item can be sold
	 * @return boolean
	 */
	public final boolean isSellable()
	{
		return _sellable;
	}

	/**
	 * Returns if the item can dropped
	 * @return boolean
	 */
	public final boolean isDropable()
	{
		return _dropable;
	}

	/**
	 * Returns if the item can destroy
	 * @return boolean
	 */
	public final boolean isDestroyable()
	{
		return _destroyable;
	}

	/**
	 * Returns if the item can add to trade
	 * @return boolean
	 */
	public final boolean isTradeable()
	{
		return _tradeable;
	}

	/**
	 * Returns if the item can be put into warehouse
	 * @return boolean
	 */
	public final boolean isDepositable()
	{
		return _depositable;
	}

	/**
	 * Returns if item is for hatchling
	 * @return boolean
	 */
	public boolean isForHatchling()
	{
		return (_type2 == TYPE2_PET_HATCHLING);
	}

	/**
	 * Returns if item is for strider
	 * @return boolean
	 */
	public boolean isForStrider()
	{
		return (_type2 == TYPE2_PET_STRIDER);
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForWolf()
	{
		return (_type2 == TYPE2_PET_WOLF);
	}

	/**
	 * Returns if item is for Evolved wolf
	 * @return boolean
	 */
	public boolean isForEvolvedWolf()
	{
		return (_type2 == TYPE2_PET_EVOLVEDWOLF);
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForBabyPet()
	{
		return (_type2 == TYPE2_PET_BABY);
	}

	public final void attach(Condition c)
	{
		if (c == null || ArrayUtils.contains(_preConditions, c))
			return;

		_preConditions = (Condition[]) ArrayUtils.add(_preConditions, c);
	}

	public boolean checkCondition(L2Character activeChar, L2Object target, boolean sendMessage)
	{
		if (activeChar instanceof L2PcInstance && activeChar.getActingPlayer().isGM() &&
				!Config.GM_ITEM_RESTRICTION)
			return true;

		for (Condition preCondition : _preConditions)
		{
			Env env = new Env();
			env.player = activeChar;
			if (target instanceof L2Character)
				env.target = target.getActingCharacter();
			else
				env.target = activeChar;
			
			if (!preCondition.test(env))
			{
				if (activeChar instanceof L2Summon)
					((L2Summon) activeChar).getOwner().sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
				else if (sendMessage && activeChar instanceof L2PcInstance)
					preCondition.sendMessage(activeChar.getActingPlayer(), this);
				
				return false;
			}
		}
		return true;
	}

	public boolean checkCondition(L2Playable activeChar, boolean sendMessage)
	{
		return checkCondition(activeChar, activeChar, sendMessage);
	}

	public boolean checkCondition(L2Summon pet)
	{
		// second param does nothing
		return checkCondition(pet, true);
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	@Override
	public String toString()
	{
		return _name;
	}
	
	public final String getFuncOwnerName()
	{
		return getName();
	}
	
	public final L2Skill getFuncOwnerSkill()
	{
		return null;
	}
}
