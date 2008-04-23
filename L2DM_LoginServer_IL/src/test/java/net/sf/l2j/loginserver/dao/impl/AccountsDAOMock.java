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
package net.sf.l2j.loginserver.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.l2j.loginserver.beans.Accounts;
import net.sf.l2j.loginserver.dao.AccountsDAO;

import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * DAO object for domain model class Accounts.
 * @see net.sf.l2j.loginserver.beans.Accounts
 */
public class AccountsDAOMock  implements AccountsDAO
{
    //private static final Log _log = LogFactory.getLog(AccountsDAOHib.class);
    
    private Map<String,Accounts> referential = new HashMap<String,Accounts>();
    
    public AccountsDAOMock()
    {
        referential.put("player1", new Accounts("player1","UqW5IPUACYelC13kW52+69qJwxQ=",new BigDecimal(0),new Integer(100),"127.0.0.1"));
        referential.put("player2", new Accounts("player2","UqW5IPUACYelC13kW52+69qJwxQ=",new BigDecimal(0),new Integer(-1),"127.0.0.2"));
    }
    

    /**
     * Search by id
     * @param id
     * @return
     */
    public Accounts getAccountById(String id)
    {
        if ( ! referential.containsKey(id))
        {
            throw new ObjectRetrievalFailureException ("Accounts",id);
        }
        return referential.get(id);
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#createAccount(java.lang.Object)
     */
    public String createAccount(Object obj)
    {
        Accounts acc = (Accounts)obj;
        referential.put(acc.getLogin(),acc);
        return acc.getLogin();
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#createOrUpdate(java.lang.Object)
     */
    public void createOrUpdate(Object obj)
    {
        createAccount(obj);
        
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#createOrUpdateAll(java.util.Collection)
     */
    public void createOrUpdateAll(Collection entities)
    {
        Iterator it = entities.iterator();
        while (it.hasNext())
        {
            createAccount(it.next());
        }
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#getAllAccounts()
     */
    public List <Accounts> getAllAccounts()
    {
        return new ArrayList<Accounts> (referential.values());
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#removeAccount(java.lang.Object)
     */
    public void removeAccount(Object obj)
    {
        referential.remove(((Accounts)obj).getLogin());
        
    }

    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#removeAccountById(java.io.Serializable)
     */
    public void removeAccountById(String login)
    {
        referential.remove(login);
    }


    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#removeAll(java.util.Collection)
     */
    public void removeAll(Collection entities)
    {
        Iterator it = entities.iterator();
        while (it.hasNext())
        {
            removeAccount(it.next());
        }        
    }


    /**
     * @see net.sf.l2j.loginserver.dao.AccountsDAO#update(java.lang.Object)
     */
    public void update(Object obj)
    {
        Accounts acc = (Accounts)obj;
        removeAccountById(acc.getLogin());
        createAccount(obj);
        
    }
}
