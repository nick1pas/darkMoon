/* This program is free software; you can redistribute it and/or modify
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
package net.sf.l2j.gameserver.model.actor.knownlist;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2TownPetInstance;

public class TownPetKnownList extends AttackableKnownList
{
    //=========================================================
    // Data Field    
    // =========================================================
    // Constructor
    public TownPetKnownList(L2TownPetInstance activeChar)
    {
        super(activeChar);
    }
    // =========================================================
    // Method - Public
    public boolean addKnownObject(L2Object object) { return addKnownObject(object, null); }
    public boolean addKnownObject(L2Object object, L2Character dropper)
    {
        if (!super.addKnownObject(object, dropper)) return false;
        if (getActiveChar().getHomeX() == 0) getActiveChar().getHomeLocation();
        // Set the L2TownPetInstance Intention to AI_INTENTION_ACTIVE if the state was AI_INTENTION_IDLE
        if (object instanceof L2PcInstance && getActiveChar().getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) 
            getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);     
        return true;
    } 
    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public
    public final L2TownPetInstance getActiveChar() { return (L2TownPetInstance)super.getActiveChar(); 
    }
}