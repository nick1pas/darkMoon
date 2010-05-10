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
package com.l2jfree.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.model.L2Clan;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.entity.CCHSiege;
import com.l2jfree.gameserver.model.entity.ClanHall;
import com.l2jfree.gameserver.network.SystemMessageId;

/**
 * Siegeable clan hall manager.
 * @author Savormix
 */
public final class CCHManager
{
	private static final Log _log = LogFactory.getLog(CCHManager.class);
	
	private static final class SingletonHolder
	{
		private static final CCHManager INSTANCE = new CCHManager();
	}
	
	public static CCHManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	/** Return true if object is inside zone */
	public final boolean checkIfInZone(L2Object obj)
	{
		return (getSiege(obj) != null);
	}

	/** Return true if object is inside zone */
	public final boolean checkIfInZone(int x, int y, int z)
	{
		return (getSiege(x, y, z) != null);
	}

	public static boolean checkIfOkToPlaceFlag(L2PcInstance player, boolean isCheckOnly)
	{
		// Get siege battleground
		L2Clan clan = player.getClan();
		CCHSiege siege = getInstance().getSiege(player);
		ClanHall hideout = (siege == null) ? null : siege.getHideout();

		SystemMessageId sm = null;

		if (siege == null || !siege.getIsInProgress())
			sm = SystemMessageId.ONLY_DURING_SIEGE;
		else if (clan == null || clan.getLeaderId() != player.getObjectId() || siege.getAttackerClan(clan) == null)
			sm = SystemMessageId.CANNOT_USE_ON_YOURSELF;
		else if (hideout == null || !hideout.checkIfInZoneHeadQuarters(player))
			sm = SystemMessageId.ONLY_DURING_SIEGE;
		else if (hideout.getSiege().getAttackerClan(clan).getNumFlags() >= Config.SIEGE_FLAG_MAX_COUNT)
			sm = SystemMessageId.NOT_ANOTHER_HEADQUARTERS;
		else
			return true;

		if (!isCheckOnly)
			player.sendPacket(sm);
		return false;
	}

	public static boolean checkIfOkToUseStriderSiegeAssault(L2PcInstance player, boolean isCheckOnly)
	{
		// Get siege battleground
		CCHSiege siege = getInstance().getSiege(player);

		SystemMessageId sm = null;

		if (siege == null)
			sm = SystemMessageId.YOU_ARE_NOT_IN_SIEGE;
		else if (!siege.getIsInProgress())
			sm = SystemMessageId.ONLY_DURING_SIEGE;
		else if (!(player.getTarget() instanceof L2DoorInstance))
			sm = SystemMessageId.TARGET_IS_INCORRECT;
		else if (!player.isRidingStrider() && !player.isRidingRedStrider())
			sm = SystemMessageId.CANNOT_USE_ON_YOURSELF;
		else
			return true;

		if (!isCheckOnly)
			player.sendPacket(sm);
		return false;
	}

	public final boolean checkIsRegistered(L2Clan clan)
	{
		return (checkIsRegistered(clan, 34) || checkIsRegistered(clan, 64));
	}

	public final boolean checkIsRegistered(L2Clan clan, int hallid)
	{
		if (clan == null)
			return false;

		if (clan.getHasHideout() > 0)
			return true;

		Connection con = null;
		boolean register = false;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM siege_clans WHERE clan_id=? AND castle_id=?");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, hallid);
			register = statement.executeQuery().next();
			statement.close();
		}
		catch (Exception e)
		{
			_log.error("Exception: checkIsRegistered(): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}

		return register;
	}

	public final CCHSiege getSiege(L2Object activeObject)
	{
		return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}

	/** * get active siege for clan ** */
	public final CCHSiege getSiege(L2Clan clan)
	{
		if (clan == null)
			return null;
		for (ClanHall hideout : ClanHallManager.getInstance().getClanHalls().values())
		{
			CCHSiege cs = hideout.getSiege();
			if (cs.getIsInProgress() && cs.checkIsAttacker(clan))
				return cs;
		}
		return null;
	}

	public final CCHSiege getSiege(int x, int y, int z)
	{
		for (ClanHall hideout : ClanHallManager.getInstance().getAllClanHalls().values())
		{
			CCHSiege cs = hideout.getSiege();
			if (cs == null)
				continue;
			if (cs.checkIfInZone(x, y, z))
				return cs;
		}
		return null;
	}

	public final List<CCHSiege> getSieges()
	{
		FastList<CCHSiege> sieges = new FastList<CCHSiege>();
		for (ClanHall hideout : ClanHallManager.getInstance().getClanHalls().values())
			if (hideout.getSiege() != null)
				sieges.add(hideout.getSiege());
		return sieges;
	}
}
