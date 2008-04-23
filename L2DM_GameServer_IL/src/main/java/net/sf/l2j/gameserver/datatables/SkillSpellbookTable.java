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
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.L2Skill;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SkillSpellbookTable
{
	private final static Log _log = LogFactory.getLog(SkillTreeTable.class.getName());
	private static SkillSpellbookTable _instance;

	private static FastMap<Integer, Integer> _skillSpellbooks;

	public static SkillSpellbookTable getInstance()
	{
        if (_instance == null)
            _instance = new SkillSpellbookTable();
        
		return _instance;
	}
    
	private SkillSpellbookTable()
	{
		_skillSpellbooks = new FastMap<Integer, Integer>();
		java.sql.Connection con = null;
        
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(con);
			PreparedStatement statement = con.prepareStatement("SELECT skill_id, item_id FROM skill_spellbooks");
			ResultSet spbooks = statement.executeQuery();

			while (spbooks.next())
				_skillSpellbooks.put(spbooks.getInt("skill_id") , spbooks.getInt("item_id"));

			spbooks.close();
			statement.close();
            
			//L2EMU_EDIT
			_log.info("TablesManager: Loaded " + _skillSpellbooks.size() + " Spellbooks.");
			//L2EMU_EDIT
		}
		catch (Exception e)
		{
			_log.warn("Error while loading spellbook data: " +  e);
		}
		finally
		{
			try	
            {
				con.close(); 
			} 
            catch (Exception e) {}
		}
	}

    public int getBookForSkill(int skillId)
    {
        if (!_skillSpellbooks.containsKey(skillId))
            return -1;
        
        return _skillSpellbooks.get(skillId);
    }
    
    public int getBookForSkill(L2Skill skill)
    {
        return getBookForSkill(skill.getId());
    }
}
