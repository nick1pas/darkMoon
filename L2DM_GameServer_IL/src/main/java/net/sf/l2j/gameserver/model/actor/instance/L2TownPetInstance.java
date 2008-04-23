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
package net.sf.l2j.gameserver.model.actor.instance;

import org.apache.log4j.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Attackable;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2WorldRegion;
import net.sf.l2j.gameserver.model.actor.knownlist.TownPetKnownList;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.util.Rnd;
import net.sf.l2j.gameserver.ai.L2AttackableAI;

  public final class L2TownPetInstance extends L2Attackable
  {	 
	  private static Logger _log = Logger.getLogger(L2TownPetInstance.class.getName());
    private int _homeX;
    private int _homeY;
	private int _homeZ;
	private static final int RETURN_INTERVAL = 1;
	
    public L2TownPetInstance(int objectId, L2NpcTemplate template)
    {
    	super(objectId, template);
		this.getKnownList();	// init knownlist	
		
		 ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ReturnTask(),RETURN_INTERVAL,RETURN_INTERVAL+Rnd.nextInt(1));
	  }
    public class ReturnTask implements Runnable
    {
        public void run()
        {
            if(getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
                returnHome();
        }
    }
    public final TownPetKnownList getKnownList()
    {
     if(super.getKnownList() == null || !(super.getKnownList() instanceof TownPetKnownList))
     this.setKnownList(new TownPetKnownList(this));
     return (TownPetKnownList)super.getKnownList();
    } 
    public boolean isAutoAttackable(L2Character attacker) 
	{
        return false;
    }
    
    public boolean isAttackable()
    {
        return false;
    }  
    /**
     * Sets home location of townpet. pet will always try to return to this location.
     */
    public void getHomeLocation()
    {
        _homeX = getX();
        _homeY = getY();
        _homeZ = getZ();
        
        if (Config.DEVELOPER)
            _log.info(getObjectId()+": Home location set to"+
                    " X:" + _homeX + " Y:" + _homeY + " Z:" + _homeZ);
    }   
    public int getHomeX()
    {
    	return _homeX;
    }  
    public int getHomeY()
    {
    	return _homeY;
    }
    public void returnHome()
	{
        if (!isInsideRadius(_homeX, _homeY, 2, false))
		{
			if (Config.DEVELOPER) _log.info(getObjectId()+": moving hometo" + " X:" + _homeX + " Y:" + _homeY + " Z:" + _homeZ);			
			getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(_homeX, _homeY, _homeZ, 0));
		}
	}
	public void onSpawn()
	{
		_homeX = getX();
		_homeY = getY();
		_homeZ = getZ();
		
		if (Config.DEVELOPER)
			_log.info(getObjectId()+": Home location set to"+" X:" + _homeX + " Y:" + _homeY + " Z:" + _homeZ);

        // check the region where this mob is, do not activate the AI if region is inactive.
        L2WorldRegion region = L2World.getInstance().getRegion(getX(),getY());
        if ((region !=null) && (!region.isActive()))
            ((L2AttackableAI) getAI()).stopAITask();        
	}
    public boolean isAggressive()
	{
		return false;
	}
    public boolean hasRandomAnimation()
	{
		return (Config.MAX_NPC_ANIMATION > 0);
    } 
    public void onAction(L2PcInstance player)
    {
        if (getObjectId() != player.getTargetId())
       {
       	player.sendPacket(new ActionFailed());
      }
    }
    }