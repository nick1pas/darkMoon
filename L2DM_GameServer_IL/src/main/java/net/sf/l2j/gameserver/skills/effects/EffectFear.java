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

import net.sf.l2j.gameserver.GeoData;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2FolkInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2RiftInvaderInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import net.sf.l2j.gameserver.skills.Env;

/**
 * @author littlecrow
 *
 * Implementation of the Fear Effect
 */
final class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 500;
	
	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}

	/** Notify started */
	public void onStart()
	{
		if (!getEffected().isAfraid())
		{
			getEffected().startFear();
			onActionTime();
		}
	}

	/** Notify exited */
	public void onExit()
	{
		getEffected().stopFear(this);
	}

    public boolean onActionTime()
    {
        // Fear skills cannot be used l2pcinstance to l2pcinstance. Heroic Dread, Curse: Fear, Fear and Horror are the exceptions.
        if(getEffected() instanceof L2PcInstance && getEffector() instanceof L2PcInstance && getSkill().getId() != 1376 && getSkill().getId() != 1169 && getSkill().getId() != 65 && getSkill().getId() != 1092) return false;
        if(getEffected() instanceof L2FolkInstance) return false;
        if(getEffected() instanceof L2SiegeGuardInstance) return false;

        //L2EMU_ADD_START
        if(getEffected() instanceof L2RiftInvaderInstance) return false; 
        //L2EMU_ADD_END
        
        // Fear skills cannot be used on Headquarters Flag.
        if(getEffected() instanceof L2SiegeFlagInstance) return false;
        
        if(getEffected() instanceof L2SiegeSummonInstance) return false;

        int posX = getEffected().getX();
        int posY = getEffected().getY();
        int posZ = getEffected().getZ();

        int signx=-1;
        int signy=-1;
        if (getEffected().getX()>getEffector().getX())
            signx=1;
        if (getEffected().getY()>getEffector().getY())
            signy=1;

        posX += signx*FEAR_RANGE;
        posY += signy*FEAR_RANGE;

        Location destiny = GeoData.getInstance().moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), posX, posY, posZ);
        getEffected().setRunning();
        
        getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,
                 new L2CharPosition(destiny.getX(),destiny.getY(),destiny.getZ(),0));
        
        destiny = null;
        return true;
    }
}