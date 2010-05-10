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
package com.l2jfree.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author evill33t
 * 
 */
public class Couple
{
    private static final Log _log = LogFactory.getLog(Couple.class);
    
    // =========================================================
    // Data Field
    private int _id                             = 0;
    private int _player1Id                      = 0;
    private int _player2Id                      = 0;
    private boolean _maried                     = false;
    private long _affiancedDate;
    private long _weddingDate;

    // =========================================================
    // Constructor
    public Couple(int coupleId)
    {
        _id = coupleId;
        
        Connection con = null;
        try
        {
            PreparedStatement statement;
            ResultSet rs;

            con = L2DatabaseFactory.getInstance().getConnection(con);

            statement = con.prepareStatement("Select * from couples where id = ?");
            statement.setInt(1, _id);
            rs = statement.executeQuery();

            while (rs.next())
            {
                _player1Id = rs.getInt("player1Id");
                _player2Id = rs.getInt("player2Id");
                _maried    = rs.getBoolean("maried");

                _affiancedDate = rs.getLong("affiancedDate");
                _weddingDate = rs.getLong("weddingDate");
            }
            statement.close();
        }
        catch (Exception e)
        {
            _log.error("Exception: Couple.load(): " + e.getMessage(),e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
    }
    
    public Couple(L2PcInstance player1,L2PcInstance player2)
    {
        int _tempPlayer1Id = player1.getObjectId();
        int _tempPlayer2Id = player2.getObjectId();

        _player1Id = _tempPlayer1Id;
        _player2Id = _tempPlayer2Id;

        _affiancedDate = System.currentTimeMillis();
        _weddingDate =System.currentTimeMillis();
        
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement;
            _id = IdFactory.getInstance().getNextId();
            statement = con.prepareStatement("INSERT INTO couples (id, player1Id, player2Id, maried, affiancedDate, weddingDate) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setInt(1, _id);
            statement.setInt(2, _player1Id);
            statement.setInt(3, _player2Id);
            statement.setBoolean(4, false);
            statement.setLong(5, _affiancedDate);
            statement.setLong(6, _weddingDate);
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.error("",e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
    }
    
    public void marry()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement;

            statement = con.prepareStatement("UPDATE couples set maried = ?, weddingDate = ? where id = ?");
            statement.setBoolean(1, true);
            _weddingDate = System.currentTimeMillis();
            statement.setLong(2, _weddingDate);
            statement.setInt(3, _id);
            statement.execute();
            statement.close();
            _maried = true;
        }
        catch (Exception e)
        {
            _log.error("",e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
    }
    
    public void divorce()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement;
            
            statement = con.prepareStatement("DELETE FROM couples WHERE id=?");
            statement.setInt(1, _id);
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.error("Exception: Couple.divorce(): " + e.getMessage(),e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
    }
    
    public final int getId() { return _id; }

    public final int getPlayer1Id() { return _player1Id; }
    public final int getPlayer2Id() { return _player2Id; }

    public final boolean getMaried() { return _maried; }

    public final long getAffiancedDate() { return _affiancedDate; }
    public final long getWeddingDate() { return _weddingDate; }
}