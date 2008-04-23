package net.sf.l2j.gameserver.skills.l2skills;

import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.templates.StatsSet;

/**
 * Class to Manage Symbol Type Skills <br>
 * @author Rayan RPG
 *
 */
public class L2SkillSymbol extends L2Skill
{
	int _symbolType;
	int _symbolRadius;
	/**
	 * Symbol of the Assassin
	 * Symbol of the Sniper
	 * Symbol of Energy
	 * Symbol of Honor
	 * Symbol of Resistance
	 * Symbol of Noise
	 * Symbol of Defense
	 * 
	 * Generates a signet that maximizes the defense abilities of those nearby. 
	 * Applies to all targets within the affected area. 
	 * The effect disappears if you leave the area.
	 * Level 2 or higher Battle Force required. Consumes 1 Battle Symbol.
	 * @param set
	 */
	public L2SkillSymbol(StatsSet set)
	{
		super(set);

		_symbolType    = set.getInteger("1", getLevel());
		_symbolRadius  = set.getInteger("SymbolRadius");
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if (activeChar.isAlikeDead()) return;
		
		// get the effect
		//SymbolEffect se = (SymbolEffect)activeChar.getEffect(_symbolType);
		// TODO Auto-generated method stub
		
	}

}
