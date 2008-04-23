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
package net.sf.l2j.loginserver.services;

import junit.framework.TestCase;
import net.sf.l2j.loginserver.beans.Accounts;
import net.sf.l2j.loginserver.services.exception.AccountModificationException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class for AccountsServices 
 * 
 */
public class AccountsServicesMockTest extends TestCase
{
    private ClassPathXmlApplicationContext context = null;
    
    private AccountsServices services = null;
    
    private void setAccountsServices (AccountsServices _services)
    {
        services = _services ;
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        context = new ClassPathXmlApplicationContext(
        "classpath*:/**/**/applicationContext-TestMock.xml");
        setAccountsServices ( (AccountsServices) context.getBean("AccountsServices"));
    }
    
    public void testAddAccount () throws Exception
    {
        assertEquals(2,services.getAccountsInfo().size());
        Accounts acc = services.addOrUpdateAccount("player1", "pass", "1");
        
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(1));        
        assertEquals(2,services.getAccountsInfo().size());
    }
    
    public void testAddAccountWithBadLevel () throws Exception
    {
        assertEquals(2,services.getAccountsInfo().size());
        try
        {
            services.addOrUpdateAccount("player1", "pass", "x");
            fail("No error");
        }
        catch (AccountModificationException e)
        {
            assertNotNull(e);
        }
    }
    
    public void testUpdateLevel () throws Exception
    {
        assertEquals(2,services.getAccountsInfo().size());
        Accounts acc = services.addOrUpdateAccount("player1", "pass", "1");
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(1));
        acc = services.addOrUpdateAccount("player2", "pass1", "2");
        
        services.changeAccountLevel("player1", "2");
        
        acc = services.getAccountById("player1");
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(2));
    }    
    
    public void testUpdateLevelIncorretValue () throws Exception
    {
        assertEquals(2,services.getAccountsInfo().size());
        Accounts acc = services.addOrUpdateAccount("player1", "pass", "1");
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(1));
        acc = services.addOrUpdateAccount("player2", "pass1", "2");
        
        try
        {
            services.changeAccountLevel("player1", "x");
        }
        catch (AccountModificationException e)
        {
            assertNotNull(e);
        }
    }       
    
    public void testGetUnknownAccount ()
    {
        Accounts acc = services.getAccountById("unknown");
        assertEquals(acc, null);
        
    }
    
    public void testDeleteAccount () throws Exception
    {
        assertEquals(2,services.getAccountsInfo().size());
        Accounts acc = services.addOrUpdateAccount("player1", "pass", "1");
        
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(1));        
        assertEquals(2,services.getAccountsInfo().size());
        
        services.deleteAccount("player1");
        assertEquals(1,services.getAccountsInfo().size());
    }    
    
    public void testDeleteUnknownAccount ()
    {
        assertEquals(2,services.getAccountsInfo().size());
        Accounts acc=null;
        try
        {
            acc = services.addOrUpdateAccount("player1", "pass", "1");
        }
        catch (AccountModificationException e1)
        {
            fail (e1.getMessage());
        }
        
        assertEquals(acc.getLogin(), "player1");
        assertEquals(acc.getAccessLevel(), new Integer(1));        
        assertEquals(2,services.getAccountsInfo().size());
        
        try
        {
            services.deleteAccount("unknown");
            fail("able to delete unknown object ?");
        }
        catch (AccountModificationException e)
        {
            assertNotNull(e);
        }
        assertEquals(2,services.getAccountsInfo().size());
    }    

}
