/*
 * $HeadURL: $
 *
 * $Author: $
 * $Date: $
 * $Revision: $
 *
 * 
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
package net.sf.l2j.loginserver.dao;

import java.util.Collection;
import java.util.List;

import net.sf.l2j.loginserver.beans.Accounts;

/**
 * Data access object interface for account
 * The interface define access functions to accounts
 */
public interface AccountsDAO
{
    /**
     * Return all objects related to the implementation of this DAO with no filter.
     */
    public List <Accounts> getAllAccounts () ;
    
    
    /**
     * Persist the given transient instance, first assigning a generated identifier.
     * (Or using the current value of the identifier property if the assigned generator is used.)
     */
    public String createAccount(Object obj);

    /**
     * Either save() or update() the given instance, depending upon the value of its
     * identifier property.
     */
    public void createOrUpdate(Object obj);

    /**
     * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
     * instance with the same identifier in the current session.
     * @param obj a transient instance containing updated state
     */
    public void update(Object obj);

    /**
     * Delete an object.
     */
    public void removeAccount(Object obj);

    /**
     * Search by id
     * @param id the id  (login)
     * @return the account
     */
    public Accounts getAccountById(String id);
    
    /**
     * Delete an object by id
     */
    public void removeAccountById(String login) ;
    
    /**
     * Delete a collection of object
     */
    public void removeAll(Collection entities) ;
        
    /**
     * Persist an entire collection
     */
    public void createOrUpdateAll(Collection entities) ;    
    
}
