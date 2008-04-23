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
package net.sf.l2j.gameserver.idfactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.2.1.2.7 $ $Date: 2005/04/11 10:06:12 $
 */
public abstract class IdFactory
{
	private final static Log _log = LogFactory.getLog(IdFactory.class.getName());

	protected static final String[] ID_UPDATES = 
	{
        "UPDATE items                 SET owner_id = ?    WHERE owner_id = ?",
        "UPDATE items                 SET object_id = ?   WHERE object_id = ?",
        "UPDATE character_quests      SET char_id = ?     WHERE char_id = ?",
        "UPDATE character_friends     SET char_id = ?     WHERE char_id = ?",
        "UPDATE character_friends     SET friend_id = ?   WHERE friend_id = ?",
        "UPDATE character_hennas      SET char_obj_id = ? WHERE char_obj_id = ?",
        "UPDATE character_recipebook  SET char_id = ?     WHERE char_id = ?",
        "UPDATE character_shortcuts   SET char_obj_id = ? WHERE char_obj_id = ?",
        "UPDATE character_shortcuts   SET shortcut_id = ? WHERE shortcut_id = ? AND type = 1", // items
        "UPDATE character_macroses    SET char_obj_id = ? WHERE char_obj_id = ?",
        "UPDATE character_skills      SET char_obj_id = ? WHERE char_obj_id = ?",
		"UPDATE character_skills_save SET char_obj_id = ? WHERE char_obj_id = ?",
        "UPDATE character_subclasses  SET char_obj_id = ? WHERE char_obj_id = ?",
        "UPDATE characters            SET obj_Id = ?      WHERE obj_Id = ?",
        "UPDATE characters            SET clanid = ?      WHERE clanid = ?",
        "UPDATE clan_data             SET clan_id = ?     WHERE clan_id = ?",
        "UPDATE siege_clans           SET clan_id = ?     WHERE clan_id = ?",
        "UPDATE clan_data             SET ally_id = ?     WHERE ally_id = ?",
        "UPDATE clan_data             SET leader_id = ?   WHERE leader_id = ?",
        "UPDATE pets                  SET item_obj_id = ? WHERE item_obj_id = ?",
        // added by DaDummy
        "UPDATE auction_bid          SET bidderId = ?      WHERE bidderId = ?",
        "UPDATE auction_watch        SET charObjId = ?     WHERE charObjId = ?",
        "UPDATE character_hennas     SET char_obj_id = ?   WHERE char_obj_id = ?",
        "UPDATE clan_wars            SET clan1 = ?         WHERE clan1 = ?",
        "UPDATE clan_wars            SET clan2 = ?         WHERE clan2 = ?",
        "UPDATE clanhall             SET ownerId = ?       WHERE ownerId = ?",
        "UPDATE petitions            SET char_id = ?       WHERE char_id = ?",
        "UPDATE posts                SET post_ownerid = ?  WHERE post_ownerid = ?",
        "UPDATE seven_signs          SET char_obj_id = ?   WHERE char_obj_id = ?",
        "UPDATE topic                SET topic_ownerid = ? WHERE topic_ownerid = ?",
        "UPDATE itemsonground        SET object_id = ?   WHERE object_id = ?"
	};

    protected static final String[] ID_CHECKS = 
	{
        "SELECT owner_id    FROM items                 WHERE object_id >= ?   AND object_id < ?",
        "SELECT object_id   FROM items                 WHERE object_id >= ?   AND object_id < ?",
        "SELECT char_id     FROM character_quests      WHERE char_id >= ?     AND char_id < ?",
		"SELECT char_id     FROM character_friends     WHERE char_id >= ?     AND char_id < ?",
        "SELECT char_id     FROM character_friends     WHERE friend_id >= ?   AND friend_id < ?",
        "SELECT char_obj_id FROM character_hennas      WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT char_id     FROM character_recipebook  WHERE char_id >= ?     AND char_id < ?",
        "SELECT char_obj_id FROM character_shortcuts   WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT char_obj_id FROM character_macroses    WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT char_obj_id FROM character_skills      WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT char_obj_id FROM character_skills_save WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT char_obj_id FROM character_subclasses  WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT obj_Id      FROM characters            WHERE obj_Id >= ?      AND obj_Id < ?",
        "SELECT clanid      FROM characters            WHERE clanid >= ?      AND clanid < ?",
        "SELECT clan_id     FROM clan_data             WHERE clan_id >= ?     AND clan_id < ?",
        "SELECT clan_id     FROM siege_clans           WHERE clan_id >= ?     AND clan_id < ?",
        "SELECT ally_id     FROM clan_data             WHERE ally_id >= ?     AND ally_id < ?",
        "SELECT leader_id   FROM clan_data             WHERE leader_id >= ?   AND leader_id < ?",
        "SELECT item_obj_id FROM pets                  WHERE item_obj_id >= ? AND item_obj_id < ?",
        // added by DaDummy
        "SELECT friend_id   FROM character_friends    WHERE friend_id >= ?   AND friend_id < ?",
        "SELECT char_obj_id FROM seven_signs          WHERE char_obj_id >= ? AND char_obj_id < ?",
        "SELECT object_id   FROM itemsonground        WHERE object_id >= ?   AND object_id < ?"
	};
	
    protected boolean _initialized;
	
    public static final int FIRST_OID            = 0x10000000;
    public static final int LAST_OID             = 0x7FFFFFFF;
    public static final int FREE_OBJECT_ID_SIZE  = LAST_OID - FIRST_OID;
    
    protected static IdFactory _instance = null;

	protected IdFactory()
	{
        setAllCharacterOffline();
        cleanUpDB();
    }
    
    static
    {
        switch (Config.IDFACTORY_TYPE)
        {
            case Compaction:
                _instance   = new CompactionIDFactory();
                break;
            case BitSet:
                _instance   = new BitSetIDFactory();
                break;
            case Stack:
                _instance   = new StackIDFactory();
                break;
            case Increment:
                _instance   = new IncrementIDFactory();
        }
    }

    /**
     * Sets all character offline
     */
    protected void setAllCharacterOffline()
    {
        java.sql.Connection con2 = null;
        try
        {
            con2 = L2DatabaseFactory.getInstance().getConnection(con2);
            Statement s2 = con2.createStatement();
            s2.executeUpdate("update characters set online=0");
            if (_log.isDebugEnabled())_log.debug("Updated characters online status.");
            s2.close();
        }
        catch (SQLException e)
        {
        }
        finally
        {
            try
            {
                con2.close();
            }
            catch (Exception e)
            {
            }
        }
    }
    
    /**
     * Cleans up Database
     */
    protected void cleanUpDB()
    {
        // TODO:
        // Check for more cleanup query
        // Check order
                java.sql.Connection conn = null;
        try
        {
            int cleanCount = 0;
            conn = L2DatabaseFactory.getInstance().getConnection(conn);
            Statement stmt = conn.createStatement();
            
            // If a character not exists
            cleanCount += stmt.executeUpdate("DELETE FROM character_friends WHERE character_friends.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_friends WHERE character_friends.friend_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_hennas WHERE character_hennas.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_macroses WHERE character_macroses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_quests WHERE character_quests.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_recipebook WHERE character_recipebook.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_shortcuts WHERE character_shortcuts.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_skills WHERE character_skills.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_skills_save WHERE character_skills_save.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_subclasses WHERE character_subclasses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_data WHERE clan_data.leader_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM couples WHERE couples.player1Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM couples WHERE couples.player2Id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM heroes WHERE heroes.char_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE loc <> 'clanwh' and items.owner_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM pets WHERE pets.item_obj_id NOT IN (SELECT object_id FROM items);");
            cleanCount += stmt.executeUpdate("DELETE FROM seven_signs WHERE seven_signs.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
            
            // If a clan not exists
            cleanCount += stmt.executeUpdate("DELETE FROM auction_bid WHERE auction_bid.bidderId NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_privs WHERE clan_privs.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_skills WHERE clan_skills.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan1 NOT IN (SELECT clan_id FROM clan_data) OR clan_wars.clan2 NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM forums WHERE forum_owner_id <> 0 AND forums.forum_owner_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE loc = 'clanwh' and items.owner_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM siege_clans WHERE siege_clans.clan_id NOT IN (SELECT clan_id FROM clan_data);");

            stmt.executeUpdate("UPDATE characters SET clanid=0 WHERE characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
            stmt.executeUpdate("UPDATE clan_data SET ally_id=0 WHERE clan_data.ally_id NOT IN (SELECT clanid FROM characters WHERE clanid!=0 GROUP BY clanid);");
            stmt.executeUpdate("UPDATE clanhall SET ownerId=0, paidUntil=0, paid=0 WHERE clanhall.ownerId NOT IN (SELECT clan_id FROM clan_data);");

            // If the clanhall isn't free
            cleanCount += stmt.executeUpdate("DELETE FROM auction WHERE auction.id IN (SELECT id FROM clanhall WHERE ownerId <> 0);");
            cleanCount += stmt.executeUpdate("DELETE FROM auction_bid WHERE auction_bid.auctionId IN (SELECT id FROM clanhall WHERE ownerId <> 0);");

            stmt.executeUpdate("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid);");
            // If the clanhall is free
            cleanCount += stmt.executeUpdate("DELETE FROM clanhall_functions WHERE clanhall_functions.hall_id NOT IN (SELECT id FROM clanhall WHERE ownerId <> 0);");
            

            // Others
            stmt.executeUpdate("UPDATE items SET loc='INVENTORY' WHERE loc='PAPERDOLL' AND loc_data=0;");
                        
            stmt.close();
            _log.info("Cleaned " + cleanCount + " elements from database.");        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * @param con
     * @return
     * @throws SQLException
     */
    protected int[] extractUsedObjectIDTable() throws SQLException
    {
        java.sql.Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            
            //create a temporary table
            Statement s = con.createStatement();
            try
            {
                s.executeUpdate("drop table temporaryObjectTable");
            }
            catch (SQLException e)
            {
            }
            
            s.executeUpdate("create table temporaryObjectTable" + " (object_id int NOT NULL PRIMARY KEY)");
            
            s.executeUpdate("insert into temporaryObjectTable (object_id)" + " select obj_id from characters");
            s.executeUpdate("insert into temporaryObjectTable (object_id)" + " select object_id from items");
            s.executeUpdate("insert into temporaryObjectTable (object_id)" + " select clan_id from clan_data");
            //L2EMU_EDIT
            s.executeUpdate("replace into temporaryObjectTable (object_id)" + " select object_id from itemsonground");
             //L2EMU_EDIT
            ResultSet result = s.executeQuery("select count(object_id) from temporaryObjectTable");
            
            result.next();
            int size = result.getInt(1);
            int[] tmp_obj_ids = new int[size];
            if(_log.isDebugEnabled())
            	_log.info("tmp table size: " + tmp_obj_ids.length);
            result.close();
            
            result = s.executeQuery("select object_id from temporaryObjectTable ORDER BY object_id");
            
            int idx = 0;
            while (result.next())
            {
                tmp_obj_ids[idx++] = result.getInt(1);
            }
            
            result.close();
            s.close();
            
            return tmp_obj_ids;
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

	public boolean isInitialized()
	{
		return _initialized;
	}
	
	public static IdFactory getInstance()
	{
        return _instance;
	}


	public abstract int getNextId();
	
	/**
	 * return a used Object ID back to the pool
	 * @param object ID
	 */
	public abstract void releaseId(int id);
    
    public abstract int getCurrentId();
    
    public abstract int size();
}
