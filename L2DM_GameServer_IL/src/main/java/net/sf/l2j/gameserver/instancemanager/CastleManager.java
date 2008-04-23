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
package net.sf.l2j.gameserver.instancemanager;

import java.sql.PreparedStatement;

import javolution.util.FastMap;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.SevenSigns;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.zone.IZone;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CastleManager
{
	protected static Log _log = LogFactory.getLog(CastleManager.class.getName());

	private static CastleManager _instance;
	private FastMap<Integer, Castle> _castles;
	
	public static final CastleManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new CastleManager();
			_instance.load();
		}
		return _instance;
	}

	private static final int _castleCirclets[] = { 0, 6838, 6835, 6839, 6837, 6840, 6834, 6836, 8182, 8183 };
	public CastleManager() {}

	public final Castle getClosestCastle(L2Object activeObject)
	{
		Castle castle = getCastle(activeObject);
		if (castle == null)
		{
			double closestDistance = Double.MAX_VALUE;
			double distance;
			
			for (Castle castle_check : getCastles().values())
			{
				if (castle_check  == null)
					continue;
				distance = castle_check.getZone().getZoneDistance(activeObject.getX(), activeObject.getY());
				if (closestDistance > distance)
				{
					closestDistance = distance;
					castle = castle_check;
				}
			}
		}
		return castle;
	}

	public void reload()
	{
		getCastles().clear();
		load();
	}

	private final void load()
	{
		for (IZone zone : ZoneManager.getInstance().getZones(ZoneType.CastleArea))
			if (zone != null)
				getCastles().put(zone.getId(), new Castle(zone.getId()));
		//L2EMU_EDIT
		_log.info("GameServer: Loaded: " + getCastles().size() + " Castles.");
		//L2EMU_EDIT
	}

	public final Castle getCastleById(int castleId)
	{
		return getCastles().get(castleId);
	}

	public final Castle getCastle(L2Object activeObject)
	{
		return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}

	public final Castle getCastle(int x, int y, int z)
	{
		Castle castle;
		for (int i = 1; i <= getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if (castle != null && castle.checkIfInZone(x, y, z))
				return castle;
		}
		return null;
	}

	public final Castle getCastleByName(String name)
	{
		Castle castle;
		for (int i = 1; i <= getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if (castle != null && castle.getName().equalsIgnoreCase(name.trim()))
				return castle;
		}
		return null;
	}

	public final Castle getCastleByOwner(L2Clan clan)
	{
		if (clan == null)
			return null;

		Castle castle;
		for (int i = 1; i <= getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if (castle != null && castle.getOwnerId() == clan.getClanId())
				return castle;
		}
		return null;
	}

	public final FastMap<Integer, Castle> getCastles()
	{
		if (_castles == null)
			_castles = new FastMap<Integer, Castle>();
		return _castles;
	}

	public final void validateTaxes(int sealStrifeOwner)
	{
		int maxTax;
		switch (sealStrifeOwner)
		{
		case SevenSigns.CABAL_DUSK:
			maxTax = 5;
			break;
		case SevenSigns.CABAL_DAWN:
			maxTax = 25;
			break;
		default: // no owner
			maxTax = 15;
			break;
		}

		for (Castle castle : _castles.values())
			if (castle.getTaxPercent() > maxTax)
				castle.setTaxPercent(maxTax);
	}

	int _castleId = 1; // from this castle
	public int getCirclet()
	{
		return getCircletByCastleId(_castleId);
	}

	public int getCircletByCastleId(int castleId)
	{
		if (castleId > 0 && castleId < 10)
			return _castleCirclets[castleId];
		
		return 0;
	}

	// remove this castle's circlets from the clan
	public void removeCirclet(L2Clan clan, int castleId)
	{
		for (L2ClanMember member : clan.getMembers())
			removeCirclet(member, castleId);
	}

	public void removeCirclet(L2ClanMember member, int castleId)
	{
		if (member == null) return;
		L2PcInstance player = member.getPlayerInstance();
		int circletId = getCircletByCastleId(castleId);
		
		if (circletId != 0)
		{
			// online-player circlet removal
			if (player != null)
			{
				try 
				{
					L2ItemInstance circlet = player.getInventory().getItemByItemId(circletId);
					if (circlet != null)
					{
						if (circlet.isEquipped())
							player.getInventory().unEquipItemInSlotAndRecord(circlet.getEquipSlot());
						player.destroyItemByItemId("CastleCircletRemoval", circletId, 1, player, true);
					}
					return;
				}
				catch (NullPointerException e)
				{
					// continue removing offline
				}
			}
			// else offline-player circlet removal
			java.sql.Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE owner_id = ? and item_id = ?");
				statement.setInt(1, member.getObjectId());
				statement.setInt(2, circletId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.error("Failed to remove castle circlets offline for player "+member.getName());
				e.printStackTrace();
			}
			finally
			{
				try { con.close(); } catch (Exception e) {}
			}
		}
	}
}