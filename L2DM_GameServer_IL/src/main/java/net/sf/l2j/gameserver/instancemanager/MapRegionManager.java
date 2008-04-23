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
package net.sf.l2j.gameserver.instancemanager;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastMap;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.entity.Town;
import net.sf.l2j.gameserver.model.mapregion.L2MapArea;
import net.sf.l2j.gameserver.model.mapregion.L2MapRegion;
import net.sf.l2j.gameserver.model.mapregion.L2MapRegionRestart;
import net.sf.l2j.gameserver.model.mapregion.TeleportWhereType;
import net.sf.l2j.gameserver.model.zone.IZone;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.RestartType;
import net.sf.l2j.gameserver.model.zone.ZoneEnum.ZoneType;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.tools.geometry.Point3D;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Noctarius
 *
 */
public class MapRegionManager
{
    private static Log _log = LogFactory.getLog(MapRegionManager.class.getName());

    private static MapRegionManager _instance = null;

    private Map<Integer, L2MapRegion> _mapRegions = new FastMap<Integer, L2MapRegion>();
    private Map<Integer, L2MapRegionRestart> _mapRegionRestart = new FastMap<Integer, L2MapRegionRestart>();
    private Map<Integer, L2MapArea> _mapRestartArea = new FastMap<Integer, L2MapArea>();
    private Map<Integer, L2MapArea> _mapAreas = new FastMap<Integer, L2MapArea>();
    
    private Map<Integer, L2MapRegion> _mapRegionsReload = null;
    private Map<Integer, L2MapRegionRestart> _mapRegionRestartReload = null;
    private Map<Integer, L2MapArea> _mapRestartAreaReload = null;
    private Map<Integer, L2MapArea> _mapAreasReload = null;
    
    public final static MapRegionManager getInstance()
    {
    	if (_instance == null)
    		_instance = new MapRegionManager();
    	
    	return _instance;
    }
    
    public MapRegionManager()
    {
    	load();
    }
    
    private void load()
    {
    	_mapRegionsReload = new FastMap<Integer, L2MapRegion>();
    	_mapRegionRestartReload = new FastMap<Integer, L2MapRegionRestart>();
        _mapRestartAreaReload = new FastMap<Integer, L2MapArea>();
    	_mapAreasReload = new FastMap<Integer, L2MapArea>();
    	
    	for (File xml : Util.getDatapackFiles("mapregion", ".xml"))
    	{
    		//L2EMU_ADD
    		if(xml == null)
    		{
    			_log.fatal("Error, cannot load file : "+xml.getName()+" file doesn't exists. ");
    			return;
    		}
    		//L2EMU_ADD
    		
        	Document doc = null;
        	
        	try
        	{
        		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        		
        		factory.setValidating(false);
        		factory.setIgnoringComments(true);
        		
        		doc = factory.newDocumentBuilder().parse(xml);
        	}
        	catch (Exception e)
        	{
				_log.warn("MapRegionManager: Error while loading XML definition: "+xml.getName());
				e.printStackTrace();
				return;
        	}
        	
        	try
        	{
        		parseDocument(doc);
        	}
        	catch(Exception e)
        	{
				_log.warn("MapRegionManager: Error in XML definition: "+xml.getName());
				e.printStackTrace();
				return;
        	}
    	}

    	// Replace old maps with reloaded ones
    	_mapRegions = _mapRegionsReload;
    	_mapRegionRestart = _mapRegionRestartReload;
        _mapRestartArea = _mapRestartAreaReload;
    	_mapAreas = _mapAreasReload;
    	
    	// Reset unused maps
    	_mapRegionsReload = null;
    	_mapRegionRestartReload = null;
    	_mapRestartAreaReload = null;
    	_mapAreasReload = null;
    	
    	int redirectCount = 0;
    	
    	for (L2MapRegionRestart restart : _mapRegionRestart.values())
    	{
    		if (restart.getBannedRace() != null)
    			redirectCount++;
    	}
    	
    	_log.info("MapRegionManager: Loaded " + _mapRegionRestart.size() + " restartpoint(s).");
    	_log.info("MapRegionManager: Loaded " + _mapRestartArea.size()+ " restartareas with "+_mapAreas.size() + " arearegion(s).");
    	_log.info("MapRegionManager: Loaded " + _mapRegions.size() + " zoneregion(s).");
    	_log.info("MapRegionManager: Loaded " + redirectCount + " race depending redirects.");
    }
    
    public void reload()
    {
    	load();
    }
    
    private void parseDocument(Document doc) throws Exception
    {
    	Map<Integer, L2MapRegion> regions = new FastMap<Integer, L2MapRegion>();
    	Map<Integer, L2MapRegionRestart> restarts = new FastMap<Integer, L2MapRegionRestart>();
    	Map<Integer, L2MapArea> restartAreas = new FastMap<Integer, L2MapArea>();
    	Map<Integer, L2MapArea> areas = new FastMap<Integer, L2MapArea>();
    	
    	for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
    	{
    		if ("mapregion".equalsIgnoreCase(n.getNodeName()))
    		{
    			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
    			{
    				if ("regions".equalsIgnoreCase(d.getNodeName()))
    				{
    					for (Node f = d.getFirstChild(); f != null; f = f.getNextSibling())
    					{
    						if ("region".equalsIgnoreCase(f.getNodeName()))
    						{
    							L2MapRegion region = new L2MapRegion(f);
    							
    							if (region != null)
    								if (!regions.containsKey(region.getId()))
    									regions.put(region.getId(), region);
    								else
    									throw new Exception("Duplicate zoneRegionId: "+region.getId()+".");
    						}
    					}
    				}
    				else if ("restartpoints".equalsIgnoreCase(d.getNodeName()))
    				{
    					for (Node f = d.getFirstChild(); f != null; f = f.getNextSibling())
    					{
    						if ("restartpoint".equalsIgnoreCase(f.getNodeName()))
    						{
    							L2MapRegionRestart restart = new L2MapRegionRestart(f);
    							
    							if (restart != null)
    								if (!restarts.containsKey(restart.getId()))
    									restarts.put(restart.getId(), restart);
    								else
    									throw new Exception("Duplicate restartpointId: "+restart.getId()+".");
    						}
    					}
    				}
    				else if ("areas".equalsIgnoreCase(d.getNodeName()))
    				{
    					for (Node f = d.getFirstChild(); f != null; f = f.getNextSibling())
    					{
    						if ("restartarea".equalsIgnoreCase(f.getNodeName()))
    						{
    							int id = -1;
    							
    							Node e = f.getAttributes().getNamedItem("id");
    							if (e != null)
    							{
    								id = Integer.parseInt(e.getTextContent());
    								
    								for (Node r = f.getFirstChild(); r != null; r = r.getNextSibling())
    								{
    									if ("map".equalsIgnoreCase(r.getNodeName()))
    									{
    										int X = 0;
    										int Y = 0;
    										
    										Node t = r.getAttributes().getNamedItem("X");
    										if (t != null)
    											X = Integer.parseInt(t.getTextContent());
    										
    										t = r.getAttributes().getNamedItem("Y");
    										if (t != null)
    											Y = Integer.parseInt(t.getTextContent());
    										
    										L2MapArea area = new L2MapArea(id, X, Y);
    										
    										if (area != null)
    											if (!areas.containsKey(area.getId()))
    											{
    												restartAreas.put(id, area);
    												areas.put(area.getId(), area);
    												regions.put(area.getId(), area.getMapRegion());
    											}
    											else
    												throw new Exception("Duplicate areaRegionId: "+area.getId()+".");
    									}
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	_mapRegionsReload = regions;
    	_mapRegionRestartReload = restarts;
    	_mapRestartAreaReload = restartAreas;
    	_mapAreasReload = areas;
    }
    
    public Map<Integer, L2MapRegion> getRegions()
    {
    	return _mapRegions;
    }

    public L2MapRegionRestart getRestartLocation(L2PcInstance activeChar)
    {
    	L2MapRegion region = getRegion(activeChar);
    	
    	int restartId = region.getRestartId(activeChar.getRace());
    	
    	return _mapRegionRestart.get(restartId);
    }
    
    public L2MapRegionRestart getRestartLocation(int restartId)
    {
    	return _mapRegionRestart.get(restartId);
    }
    
    public Point3D getRestartPoint(L2PcInstance activeChar)
    {
    	L2MapRegion region = getRegion(activeChar);
    	
    	if (region != null)
    	{
	    	int restartId = region.getRestartId(activeChar.getRace());
	    	
	    	L2MapRegionRestart restart = _mapRegionRestart.get(restartId);
	    	
	    	if (restart != null)
	    		return restart.getRandomRestartPoint(activeChar.getRace());
    	}
    	
    	L2MapRegionRestart restart = _mapRegionRestart.get(Config.ALT_DEFAULT_RESTARTTOWN);
    	return restart.getRandomRestartPoint(activeChar.getRace());
    }
    
    public Point3D getRestartPoint(int restartId)
    {
    	L2MapRegionRestart restart = _mapRegionRestart.get(restartId);
    	
    	if (restart != null)
    		return restart.getRandomRestartPoint();
    	
    	restart = _mapRegionRestart.get(Config.ALT_DEFAULT_RESTARTTOWN);
    	return restart.getRandomRestartPoint();
    } 

    public Point3D getChaosRestartPoint(L2PcInstance activeChar)
    {
    	L2MapRegion region = getRegion(activeChar);
    	
    	if (region != null)
    	{
	    	int restartId = region.getRestartId(activeChar.getRace());
	    	
	    	L2MapRegionRestart restart = _mapRegionRestart.get(restartId);
	    	
	    	if (restart != null)
	    		return restart.getRandomChaosRestartPoint(activeChar.getRace());
    	}
	    	
    	L2MapRegionRestart restart = _mapRegionRestart.get(Config.ALT_DEFAULT_RESTARTTOWN);
    	return restart.getRandomChaosRestartPoint(activeChar.getRace());
    }
    
    public Point3D getChaosRestartPoint(int restartId)
    {
    	L2MapRegionRestart restart = _mapRegionRestart.get(restartId);
    	
    	if (restart != null)
    		return restart.getRandomChaosRestartPoint();
    	
    	restart = _mapRegionRestart.get(Config.ALT_DEFAULT_RESTARTTOWN);
    	return restart.getRandomChaosRestartPoint();
    }
    
    public L2MapRegion getRegion(L2Character activeChar)
    {
    	return getRegion(activeChar.getX(), activeChar.getY(), activeChar.getZ());
    }
    
    public L2MapRegion getRegionById(int regionId)
    {
    	return _mapRegions.get(regionId);
    }
    
    public L2MapRegion getRegion(int x, int y, int z)
    {
    	L2MapRegion areaRegion = null; 
    	
    	for (L2MapRegion region : _mapRegions.values())
    		if (region.checkIfInRegion(x, y, z))
    		{
    			// prefer special regions
    			if (region.isSpecialRegion())
    				return region;
    			else
    				areaRegion = region;
    		}

    	return areaRegion;
    }
   
    public L2MapRegion getRegion(int x, int y)
    {
    	return getRegion(x, y, -1);
    }
    
    //TODO: Needs to be clean rewritten
    public Location getTeleToLocation(L2Character activeChar, TeleportWhereType teleportWhere)
    {
    	if (activeChar instanceof L2PcInstance)
    	{
    		L2PcInstance player = (L2PcInstance)activeChar;
            L2Clan clan = player.getClan();
            Castle castle = null;
            ClanHall clanhall = null;

            // Checking if in Dimensinal Gap
            if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getX(), player.getY(), player.getZ(), true)) // true -> ignore waiting room :)
            {
                if(player.isInParty() && player.getParty().isInDimensionalRift())
                {
                    player.getParty().getDimensionalRift().usedTeleport(player);
                }
                
                return DimensionalRiftManager.getInstance().getWaitingRoomTeleport();
            }
            
            // Checking if in arena
            for (IZone arena : ZoneManager.getInstance().getZones(ZoneType.Arena, player.getX(), player.getY()))
            {
                if (arena != null && arena.checkIfInZone(player))
                {
                    Location loc = arena.getRestartPoint(RestartType.RestartNormal);
                    if (loc == null)
                        loc = arena.getRestartPoint(RestartType.RestartRandom);
                    return loc;
                }
            }

            if (teleportWhere == TeleportWhereType.Town)
            {
                // Karma player land out of city
                if (player.getKarma() > 1 || player.isCursedWeaponEquiped())
                {
                	return getLocationFromPoint3D(getChaosRestartPoint(player));
                }

                return getLocationFromPoint3D(getRestartPoint(player));
            }

            if (clan != null)
            {
                // If teleport to clan hall
                if (teleportWhere == TeleportWhereType.ClanHall)
                {
                    clanhall = ClanHallManager.getInstance().getClanHallByOwner(clan);
                    if (clanhall != null)
                    {
                        IZone zone = clanhall.getZone();

                        if (zone != null)
                        {
                            Location loc = zone.getRestartPoint(RestartType.RestartOwner);
                            if (loc == null)
                                loc = zone.getRestartPoint(RestartType.RestartRandom);
                            return loc;
                        }
                    }
                }

                // If teleport to castle
                if (teleportWhere == TeleportWhereType.Castle)
                    castle = CastleManager.getInstance().getCastleByOwner(clan);

                // If Teleporting to castle or
                if (castle != null && teleportWhere == TeleportWhereType.Castle)
                {

                    IZone zone = castle.getZone();

                    // If is on castle with siege and player's clan is defender
                    if (castle.getSiege() != null && castle.getSiege().getIsInProgress())
                    {
                        // Karma player respawns out of siege zone
                        if (player.getKarma() > 1 || player.isCursedWeaponEquiped())
                        {
                            zone = castle.getZone();
                            if (zone != null)
                            {
                                return zone.getRestartPoint(RestartType.RestartChaotic);
                            }
                        } else
                        {

                            zone = castle.getDefenderSpawn();
                            if (zone != null)
                            {
                                return zone.getRestartPoint(RestartType.RestartRandom);
                            }
                        }
                    }

                    zone = castle.getZone();
                    if (zone != null)
                    {
                        return zone.getRestartPoint(RestartType.RestartOwner);
                    }
                }
                else if (teleportWhere == TeleportWhereType.SiegeFlag)
                {
                    Siege siege = SiegeManager.getInstance().getSiege(clan);

                    // Check if player's clan is attacker
                    if (siege != null && siege.checkIsAttacker(clan) && siege.checkIfInZone(player))
                    {
                        // Karma player respawns out of siege zone
                        if (player.getKarma() > 1 || player.isCursedWeaponEquiped())
                        {
                            IZone zone = siege.getCastle().getZone();
                            if (zone != null)
                            {
                                return zone.getRestartPoint(RestartType.RestartChaotic);
                            }
                        }
                        // get nearest flag
                        L2NpcInstance flag = siege.getClosestFlag(player);
                        // spawn to flag
                        if (flag != null)
                            return new Location(flag.getX(), flag.getY(), flag.getZ());
                    }
                }
            }
    	}
    	
    	// teleport to Aden if nothing else will work
        return getLocationFromPoint3D(getRestartPoint(9));
    }
    
    public Location getLocationFromPoint3D(Point3D point)
    {
    	return new Location(point.getX(), point.getY(), point.getZ());
    }
    
    public int getAreaCastle(L2Character activeChar)
    {
    	Town town = TownManager.getInstance().getClosestTown(activeChar);
    	
    	if (town == null)
    		return 5;
    	
    	return town.getCastleId();
    }
}
