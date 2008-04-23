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

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastList;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Couple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/** 
 * @author evill33t
 * 
 */
public class CoupleManager
{
    private static final Log _log = LogFactory.getLog(CoupleManager.class.getName());

    // =========================================================
    private static CoupleManager _instance;
    public static final CoupleManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new CoupleManager();
            _instance.load();
        }
        return _instance;
    }
    // =========================================================
    
    // =========================================================
    // Data Field
    private FastList<Couple> _couples;

    
    // =========================================================
    // Method - Public
    public void reload()
    {
        getCouples().clear();
        load();
    }

    // =========================================================
    // Method - Private
    private final void load()
    {
        java.sql.Connection con = null;
        try
        {
            PreparedStatement statement;
            ResultSet rs;

            con = L2DatabaseFactory.getInstance().getConnection(con);

            statement = con.prepareStatement("Select id from couples order by id");
            rs = statement.executeQuery();

            while (rs.next())
            {
                getCouples().add(new Couple(rs.getInt("id")));
            }

            statement.close();

            _log.info("CoupleManager: loaded " + getCouples().size() + " couples(s)");
        }
        catch (Exception e)
        {
            _log.error("Exception: CoupleManager.load(): " + e.getMessage(),e);
        }
        
        finally
        {
        	try { con.close(); }
        	catch (Exception e) {}
        }
    }

    // =========================================================
    // Property - Public
    public final Couple getCouple(int coupleId)
    {
        int index = getCoupleIndex(coupleId);
        if (index >= 0) return getCouples().get(index);
        return null;
    }
    
    public void createCouple(L2PcInstance player1,L2PcInstance player2)
    {
        if(player1!=null && player2!=null)
        {
            if(player1.getPartnerId()==0 && player2.getPartnerId()==0)
            {
                int _player1id = player1.getObjectId();
                int _player2id = player2.getObjectId();
                
                Couple _new = new Couple(player1,player2);
                getCouples().add(_new);
                player1.setPartnerId(_player2id);
                player2.setPartnerId(_player1id);
                player1.setCoupleId(_new.getId());
                player2.setCoupleId(_new.getId());
            }
        }
    }

    public void deleteCouple(int coupleId)
    {
        int index = getCoupleIndex(coupleId);
        Couple couple = getCouples().get(index);
        if(couple!=null)
        {
            L2PcInstance player1 = (L2PcInstance)L2World.getInstance().findObject(couple.getPlayer1Id());
            L2PcInstance player2 = (L2PcInstance)L2World.getInstance().findObject(couple.getPlayer2Id());
            L2ItemInstance item = null;
            if (player1 != null)
            {
                player1.setPartnerId(0);
                player1.setMaried(false);
                player1.setCoupleId(0);
                item = player1.getInventory().getItemByItemId(9140);
                if(player1.isOnline()==1 && item!= null)
                {
                    player1.destroyItem("Removing Cupids Bow", item, player1, true);
                    player1.getInventory().updateDatabase();
                }
                if(player1.isOnline()==0 && item!= null)
                {
                    Integer PlayerId = player1.getObjectId();
                    Integer ItemId = 9140;
                    java.sql.Connection con = null;
                    try
                    {
                        con = L2DatabaseFactory.getInstance().getConnection(con);
                        PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE owner_id = ? AND item_id = ?");
                        statement.setInt(1, PlayerId);
                        statement.setInt(2, ItemId);
                        statement.execute();
                        statement.close();
                    }
                    catch (Exception e) {} 
                    finally
                    {
                    	try { con.close(); }
                    	catch (Exception e) {}
                    }
                }
            }
            if (player2 != null)
            {
                player2.setPartnerId(0);
                player2.setMaried(false);
                player2.setCoupleId(0);
                item = player2.getInventory().getItemByItemId(9140);
                if(player2.isOnline()==1  && item!= null)
                {
                    player2.destroyItem("Removing Cupids Bow", item, player2, true);
                    player2.getInventory().updateDatabase();
                }
                if(player2.isOnline()==0  && item!= null)
                {
                    Integer Player2Id = player2.getObjectId();
                    Integer Item2Id = 9140;
                    java.sql.Connection con = null;
                    try
                    {
                        con = L2DatabaseFactory.getInstance().getConnection(con);
                        PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE owner_id = ? AND item_id = ?");
                        statement.setInt(1, Player2Id);
                        statement.setInt(2, Item2Id);
                        statement.execute();
                        statement.close();
                    }
                    catch (Exception e) {} 
                    finally
                    {
                    	try { con.close(); }
                    	catch (Exception e) {}
                    }
                }
            }
            couple.divorce();
            getCouples().remove(index);
        }
    }

    public final int getCoupleIndex(int coupleId)
    {
        int i=0;
        for (Couple temp : getCouples())
        {
            if (temp != null && temp.getId() == coupleId) return i;
            i++;
        }
        return -1;
    }

    public final FastList<Couple> getCouples()
    {
        if (_couples == null) _couples = new FastList<Couple>();
        return _couples;
    }
}