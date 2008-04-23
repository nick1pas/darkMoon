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
import net.sf.l2j.gameserver.model.entity.faction.FactionQuest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author evill33t
 *
 */
public class FactionQuestManager
{
    protected static Log _log = LogFactory.getLog(FactionQuestManager.class.getName());

    // =========================================================
    private static FactionQuestManager _instance;
    public static final FactionQuestManager getInstance()
    {
        if (_instance == null)
        {
            if ( _log.isDebugEnabled())_log.debug("Initializing FactionQuestManager");
            _instance = new FactionQuestManager();
            _instance.load();
        }
        return _instance;
    }
    // =========================================================

    
    // =========================================================
    // Data Field
    private FastList<FactionQuest> _quests;
    
    // =========================================================
    // Constructor
    public FactionQuestManager()
    {
    }

    // =========================================================
    // Method - Public
    public final void reload()
    {
        getFactionQuests().clear();
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

            statement = con.prepareStatement("Select id, faction_id, name, description, reward, mobid, amount, min_level from faction_quests order by id");
            rs = statement.executeQuery();
            while (rs.next())
            {
                getFactionQuests().add(new FactionQuest(
                                                        rs.getInt("id"),
                                                        rs.getInt("faction_id"),
                                                        rs.getString("name"),
                                                        rs.getString("description"),
                                                        rs.getInt("reward"),  rs.getInt("mobid"),
                                                        rs.getInt("amount"),
                                                        rs.getInt("min_level")                
                                                        ));
            }

            statement.close();

            _log.info("Loaded: " + getFactionQuests().size() + " factionquests");
        }
        catch (Exception e)
        {
            _log.error("Exception: FactionQuestManager.load(): " + e.getMessage(),e);
        }
        
        finally {try { con.close(); } catch (Exception e) {}}
        
    }

    // =========================================================
    // Property - Public
    public final FactionQuest getFactionQuest(int questId)
    {
        int index = getFactionQuestIndex(questId);
        if (index >= 0) return getFactionQuests().get(index);
        return null;
    }
    
    public final int getFactionQuestIndex(int questId)
    {
        FactionQuest quest;
        for (int i = 0; i < getFactionQuests().size(); i++)
        {
            quest = getFactionQuests().get(i);
            if (quest != null && quest.getId() == questId) return i;
        }
        return -1;
    }
    
    public final FastList<FactionQuest> getFactionQuests()
    {
        if (_quests == null) _quests = new FastList<FactionQuest>();
        return _quests;
    }
}
