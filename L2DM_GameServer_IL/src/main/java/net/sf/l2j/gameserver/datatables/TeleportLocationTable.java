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
package net.sf.l2j.gameserver.datatables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastMap;
import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.L2TeleportLocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * This class Manages Teleport Table
 */
public class TeleportLocationTable
{
    private final static Log _log = LogFactory.getLog(TeleportLocationTable.class.getName());
    
    private static TeleportLocationTable _instance;
    
    private FastMap<Integer, L2TeleportLocation> _teleports;
    //L2EMU_ADD
    private FastMap<Integer, L2TeleportLocation> _cTeleports;
    //L2EMU_ADD
    public static TeleportLocationTable getInstance()
    {
        if (_instance == null)
        {
            _instance = new TeleportLocationTable();
            _instance.reloadAll();
        }
        return _instance;
    }
    
    private TeleportLocationTable()
    {
    }
    public void reloadAll()
    {
        _teleports = new FastMap<Integer, L2TeleportLocation>();
        
        java.sql.Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("SELECT Description, id, loc_x, loc_y, loc_z, price, fornoble FROM teleport");
            ResultSet rset = statement.executeQuery();
            L2TeleportLocation teleport;
            
            while (rset.next())
            {
                teleport = new L2TeleportLocation();
                
                teleport.setTeleId(rset.getInt("id"));
                teleport.setLocX(rset.getInt("loc_x"));
                teleport.setLocY(rset.getInt("loc_y"));
                teleport.setLocZ(rset.getInt("loc_z"));
                if(Config.ALT_GAME_FREE_TELEPORT)
                    teleport.setPrice(0);
                else
                    teleport.setPrice(rset.getInt("price"));
                    teleport.setIsForNoble(rset.getInt("fornoble")==1);

                _teleports.put(teleport.getTeleId(), teleport);
            }
            
            rset.close();
            statement.close();
          //L2EMU_EDIT
            _log.info("TablesManager: Loaded " + _teleports.size() + " Teleport Locations.");
          //L2EMU_EDIT
        }
        catch (Exception e)
        {
            _log.info("System: Error While Creating Teleport Table, Reason: "+e);
        }
      
        finally 
        {
            try
            {
                con.close();
                con = null;
            }
            catch (Exception e)
            {
                
            }
        }
        //L2EMU_ADD
        if(Config.LOAD_CUSTOM_TELEPORTS)
        {
        	_cTeleports = new FastMap<Integer, L2TeleportLocation>();

        	try
        	{
        		con = L2DatabaseFactory.getInstance().getConnection(con);
        		PreparedStatement statement = con.prepareStatement("SELECT description, id, loc_x, loc_y, loc_z, price FROM custom_teleports");
        		ResultSet rset = statement.executeQuery();
        		L2TeleportLocation cTeleport;

        		while (rset.next())
        		{
        			cTeleport = new L2TeleportLocation();

        			cTeleport.setTeleId(rset.getInt("id"));
        			cTeleport.setLocX(rset.getInt("loc_x"));
        			cTeleport.setLocY(rset.getInt("loc_y"));
        			cTeleport.setLocZ(rset.getInt("loc_z"));
        			if(Config.ALT_GAME_FREE_TELEPORT)
        				cTeleport.setPrice(0);
        			else
        				cTeleport.setPrice(rset.getInt("price"));

        			_cTeleports.put(cTeleport.getTeleId(), cTeleport);
        		}

        		rset.close();
        		statement.close();

        		_log.info("TablesManager: Loaded " + _cTeleports.size() + " Custom Teleport Locations.");
        	}
        	catch (Exception e)
        	{
        		_log.error("GameServer: Error While Creating Custom Teleport Table, Reason: "+e);
        	} 
        	finally
        	{
        		try
        		{
        			con.close();
        		}
        		catch (Exception e)
        		{
        		}
        	}
        }
    }
    //L2EMU_ADD
    public L2TeleportLocation getCustomTemplate(int id)
    {
        return _cTeleports.get(id);
    }
    public L2TeleportLocation getTemplate(int id)
    {
        return _teleports.get(id);
    }
}