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

import javolution.util.FastList;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.templates.L2BuffTemplate;
import net.sf.l2j.gameserver.templates.StatsSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This class represents the buff templates list
 * 
 * Author: G1ta0 
 * 
 */

public class BuffTemplateTable
{
    private final static Log _log = LogFactory.getLog(BuffTemplateTable.class.getName());
    
    private static BuffTemplateTable _instance;

    /** The table containing all buff templates */
    private FastList<L2BuffTemplate> _buffs;
    
    
    public static BuffTemplateTable getInstance()
    {
        if (_instance == null)
            _instance = new BuffTemplateTable();
        return _instance;
    }
    
    /**
     * Create and Load the buff templates from SQL Table buff_templates
     */
    public BuffTemplateTable()
    {
        _buffs = new FastList<L2BuffTemplate>();
        ReloadBuffTemplates();
    }
    
    /**
     * Read and Load the buff templates from SQL Table buff_templates
     */
    public void ReloadBuffTemplates()
    {
        _buffs.clear();
        
        java.sql.Connection con = null;
        try
        {
            try 
            {
                con = L2DatabaseFactory.getInstance().getConnection(con);
                PreparedStatement statement = con.prepareStatement("SELECT * FROM buff_templates ORDER BY id, skill_order");
                ResultSet rset = statement.executeQuery();
                
                int _buffTemplates = 0;
                int templateId = -1;

                while (rset.next())
                {
                    StatsSet Buff = new StatsSet(); 
                    
                    if (templateId != rset.getInt("id")) _buffTemplates++;
                    templateId = rset.getInt("id");
                    
                    Buff.set("id", templateId);
                    Buff.set("name", rset.getString("name"));
                    Buff.set("skillId", rset.getInt("skill_id"));
                    Buff.set("skillLevel", rset.getInt("skill_level"));
                    Buff.set("skillOrder", rset.getInt("skill_order"));
                    Buff.set("forceCast", rset.getInt("skill_force"));
                    Buff.set("minLevel", rset.getInt("char_min_level"));
                    Buff.set("maxLevel", rset.getInt("char_max_level"));
                    Buff.set("race", rset.getInt("char_race"));
                    Buff.set("class", rset.getInt("char_class"));
                    Buff.set("faction", rset.getInt("char_faction"));
                    Buff.set("adena", rset.getInt("price_adena"));
                    Buff.set("points", rset.getInt("price_points"));
                    
                    // Add this buff template to the buff template list
                    L2BuffTemplate template = new L2BuffTemplate(Buff);
                    if (template.getSkill() == null)
                        {
                        _log.warn("Error while loading buff template Id " + template.getId() + " skill Id " + template.getSkillId()+" is null");
                        }
                    else
                        _buffs.add(template);
                }
                 //L2EMU_EDIT
                _log.info("TablesManager: Loaded " + _buffTemplates + " Buff Templates.");
                 //L2EMU_EDIT
                rset.close();
                statement.close();
            } 
            catch (Exception e) 
            {
                _log.warn("Error while loading buff templates "+e.getMessage());
            }
             
        } 
        finally 
        {
            try { con.close(); } catch (Exception e) {}
        }
    }
    
    /**
     * @return Returns the buffs of template by template Id
     */
    public FastList<L2BuffTemplate> getBuffTemplate(int Id)
    {
        FastList<L2BuffTemplate> _templateBuffs = new  FastList<L2BuffTemplate>();
        
        for(L2BuffTemplate _bt: _buffs)
        {
            if (_bt.getId()== Id) 
            {
                _templateBuffs.add(_bt);
            }
         }
        
         return _templateBuffs;
    }

    /**
     * @return Returns the template Id by template Name
     */
    public int getTemplateIdByName(String _name)
    {
        
        int _id = 0;
        
        for(L2BuffTemplate _bt: _buffs)
        {
            if (_bt.getName().equals(_name)) 
            {
                _id = _bt.getId();
                break;
            }
         }
        
         return _id;
    }
    
    /**
     * @return Returns the lowest char level for Buff template
     */
    public int getLowestLevel(int Id)
    {
        int _lowestLevel = 255;
        
        for(L2BuffTemplate _bt: _buffs)
        {
            if ((_bt.getId()== Id) && (_lowestLevel>_bt.getMinLevel())) 
            {
                _lowestLevel = _bt.getMinLevel();
            }
         }
        
         return _lowestLevel;
    }
    
    /**
     * @return Returns the lowest char level for Buff template
     */
    public int getHighestLevel(int Id)
    {
        int _highestLevel = 0;
        
        for(L2BuffTemplate _bt: _buffs)
        {
            if ((_bt.getId()== Id) && (_highestLevel<_bt.getMaxLevel())) 
            {
                _highestLevel = _bt.getMaxLevel();
            }
         }
        
         return _highestLevel;
    }
    
    /**
     * @return Returns the buff templates list
     */
    public FastList<L2BuffTemplate> getBuffTemplateTable()
    {
        return _buffs;
    }
    
}
