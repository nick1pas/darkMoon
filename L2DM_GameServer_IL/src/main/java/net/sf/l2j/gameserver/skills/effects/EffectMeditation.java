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
package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.skills.Env;

/**
 * Implementation of Meditation effect
 * 
 * @author Rayan RPG
 * 
 * @since 761
 */
public class EffectMeditation extends L2Effect
{
	public EffectMeditation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.MEDITATION;
	}

	@Override
	public void onStart()
	{
		//caster body must be immobilized
		getEffector().setIsImobilised(true);

		//set meditation flag
		setMeditating(true);

	}

	/** Notify effect is in progress */
	@Override
	public boolean onActionTime()
	{
		
       //if player is dead stops effect
		if (getEffected().isDead())
			return false;
 
		//checks if player has already a full mp and removes effect
		if (getEffected().getStatus().getCurrentMp() + 1 > getEffected().getMaxMp())
			return false;  

		// just stop this effect
    	return false;
	}
	/**
	 * applys meditation
	 * @param val
	 */
	private void setMeditating(boolean val)
	{
		if(getEffected() instanceof L2PcInstance)
			((L2PcInstance)getEffected()).setMeditating(val);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.l2j.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		// removes player imobilization
		getEffector().setIsImobilised(false);

		//stops meditation flag
		setMeditating(false);
	}
}
