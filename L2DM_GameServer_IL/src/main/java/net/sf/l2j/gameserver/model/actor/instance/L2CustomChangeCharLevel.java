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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;

public class L2CustomChangeCharLevel {

	public static boolean ChangeCharLevel(L2PcInstance player, int DeltaLevel, boolean increase)
	{
		if (!Config.ALLOW_NPC_CHANGELEVEL) return false;
		int curlevel = player.getLevel();
		int reslevel = 0;
		if (increase)
			reslevel = curlevel + DeltaLevel;
		else
			reslevel = curlevel - DeltaLevel;
		long xpcur = 0;
		long xpres = 0;

		L2PlayableInstance target;
	    int Price = 0;
	    if (curlevel >0 && curlevel < 20)
	    {
	    	Price = Config.CUSTOM_DECREASE_PRICE0*DeltaLevel;
	    }
	    else if (curlevel >= 20 && curlevel < 40)
	    {
	    	Price = Config.CUSTOM_DECREASE_PRICE1*DeltaLevel;
	    }
	    else if (curlevel >= 40 && curlevel < 76)
	    {
	    	Price = Config.CUSTOM_DECREASE_PRICE2*DeltaLevel;
	    }
	    else 
	    {
	    	Price = Config.CUSTOM_DECREASE_PRICE3*DeltaLevel;
	    }
	    
	    if (player.getRace().toString() == "dwarf")
	    	Price = Math.round(Price/Config.CUSTOM_COEFF_DIVISIONPRICE_R4);
		if (player.getInventory().getAdena() < Price)
		{
			player.sendMessage("Incorrect Adena in you inventory. You do not have "+Price+" Adena.");
			return false;
		}
			
		if ((curlevel < 20 && reslevel > 0) 
			||(curlevel < 40 && reslevel > 20)
			||(curlevel < 76 && reslevel > 40)
			||(curlevel > 76 && reslevel > 76)
			)
		{
			target = (L2PlayableInstance)player;
			try
			{
				xpcur = target.getStat().getExp();
				xpres = target.getStat().getExpForLevel(reslevel);
				
				if (xpcur > xpres)
					target.getStat().removeExp(xpcur - xpres);
				else
					target.getStat().addExp(xpres - xpcur);
				
				player.reduceAdena("BuyChangeLevel", Price, player.getLastFolkNPC(), true);
				player.sendMessage("You change self level to "+reslevel);
				return true;
			}
			catch (Exception e)
			{
				player.sendMessage("Incorrect action.");
				return false;
			}
		}
		else
			player.sendMessage("Incorrect level amount. You do not change level for another grade");
	return false;
	}

}
