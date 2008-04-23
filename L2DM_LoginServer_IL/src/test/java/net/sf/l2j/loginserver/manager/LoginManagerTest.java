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
package net.sf.l2j.loginserver.manager;

import java.io.IOException;
import java.net.InetAddress;

import junit.framework.TestCase;
import net.sf.l2j.Config;
import net.sf.l2j.loginserver.beans.SessionKey;
import net.sf.l2j.loginserver.services.exception.AccountBannedException;
import net.sf.l2j.loginserver.services.exception.AccountWrongPasswordException;
import net.sf.l2j.tools.L2Registry;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class test ban management
 * 
 */
public class LoginManagerTest extends TestCase
{
    private ClassPathXmlApplicationContext context = null;
    private LoginManager loginManager;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        context = new ClassPathXmlApplicationContext("classpath*:/**/**/applicationContext-TestMock.xml");
        L2Registry.setApplicationContext(context);
        
        if ( LoginManager.getInstance() == null )
        {
        	LoginManager.load();
        }
        loginManager = LoginManager.getInstance();
    }

    /**
     *
     */
    public void testAssignSessionKey()
    {
        SessionKey sk = loginManager.assignSessionKeyToLogin("player1", null);
        assertNotNull(sk);

        assertTrue(loginManager.isAccountInLoginServer("player1"));
    }

    /**
     *
     */
    public void testRemoveAccount()
    {
        loginManager.assignSessionKeyToLogin("player1", null);

        loginManager.removeAuthedLoginClient("player1");

        assertTrue(!loginManager.isAccountInLoginServer("player1"));
        assertNull(loginManager.getKeyForAccount("player1"));

    }

    public void testChangeAccountLevel()
    {
        Config.GM_MIN = 100;
        // check that an account is a GM
        assertTrue(loginManager.isGM("player1"));
        loginManager.setAccountAccessLevel("player1", 1);
        assertTrue(!loginManager.isGM("player1"));
    }

    public void testAccountBanned () throws Exception
    {
        Config.LOGIN_TRY_BEFORE_BAN = 3;
        
        InetAddress netAddress = InetAddress.getByName("123.123.123.123");
        try
        {
            loginManager.loginValid("player2", "testpwd", netAddress);
            fail ("the user is banned should fail");
        }
        catch (AccountBannedException e)
        {
            assertNotNull(e.getMessage(),e);
        }
    }
    public void testConnection() throws Exception
    {
        Config.LOGIN_TRY_BEFORE_BAN = 3;
        
        InetAddress netAddress = InetAddress.getByName("123.123.123.123");
        try
        {
            loginManager.loginValid("player1", "testpwd", netAddress);
            fail ("the password should fail");
        }
        catch (AccountWrongPasswordException e)
        {
            assertNotNull(e.getMessage(),e);
        }
            
        try
        {
            assertTrue(loginManager.loginValid("player1", "testpwd1", netAddress));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
    
    public void testHackingAttempt() throws IOException
    {
        Config.LOGIN_TRY_BEFORE_BAN = 3;
        InetAddress netAddress = InetAddress.getByName("123.123.123.123");

        try
        {
            // First try, failed connect = 1
            assertFalse(loginManager.loginValid("player1", "testpwd", netAddress));
            assertFalse(BanManager.getInstance().isBannedAddress(netAddress));
            // 2nd try, failed connect = 2
            assertFalse(loginManager.loginValid("player1", "testpwd2", netAddress));
            assertFalse(BanManager.getInstance().isBannedAddress(netAddress));
            // 3rd try, failed connect = 3 => ban ip
            assertFalse(loginManager.loginValid("player1", "testpwd3", netAddress));
            assertTrue(BanManager.getInstance().isBannedAddress(netAddress));
        }
        catch (Exception e)
        {
            assertNotNull(e);
        }
        // don't forget to unban client to avoid perturbation on other tests
        BanManager.getInstance().removeBanForAddress(netAddress.getHostAddress());
    }
    
    public void testLoginWithNullAdress () throws Exception
    {
        InetAddress address = null;
        assertFalse(loginManager.loginValid("unknownplayer", "pwdforplayer", address));
        Config.AUTO_CREATE_ACCOUNTS = true;
        assertTrue(loginManager.loginValid("unknownplayer", "pwdforplayer", address));
    }
    
    public void testAutoCreateAccount () throws IOException
    {
        Config.AUTO_CREATE_ACCOUNTS = true;
        InetAddress netAddress = InetAddress.getByName("123.123.123.123");
        
        try
        {
            assertTrue(loginManager.loginValid("unknownplayer", "pwdforplayer", netAddress));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

}
