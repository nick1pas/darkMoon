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
package com.l2jfree.loginserver.services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;

import com.l2jfree.loginserver.beans.Accounts;
import com.l2jfree.loginserver.dao.AccountsDAO;
import com.l2jfree.loginserver.services.exception.AccountModificationException;
import com.l2jfree.tools.codec.Base64;

/**
 * Account service to handle account management
 * 
 */
public class AccountsServices
{
	private static Log	_log		= LogFactory.getLog(AccountsServices.class);

	private AccountsDAO	__accDAO	= null;

	public void setAccountsDAO(AccountsDAO accDAO)
	{
		__accDAO = accDAO;
	}

	/**
	 * Add or update an account
	 * @param account
	 * @param password
	 * @param level
	 * @param by
	 * @param bm
	 * @param bd
	 * @param gs
	 * @return the new account
	 * @throws AccountModificationException
	 */
	public Accounts addOrUpdateAccount(String account, String password, String level, int by, int bm, int bd, int gs) throws AccountModificationException
	{
		// o initialization
		// ---------------
		Accounts acc = null;

		// o Encode Password
		// ----------------
		MessageDigest md;
		byte[] newpass;
		try
		{
			md = MessageDigest.getInstance("SHA");
			newpass = password.getBytes("UTF-8");
			newpass = md.digest(newpass);
		}
		catch (NoSuchAlgorithmException e1)
		{
			throw new AccountModificationException("No algorithm to encode password.", e1);
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new AccountModificationException("Unsupported encoding.", e1);
		}

		// o update account
		// ---------------
		try
		{
			acc = new Accounts();
			Integer iLevel = new Integer(level);
			acc.setLogin(account);
			acc.setAccessLevel(iLevel);
			acc.setPassword(Base64.encodeBytes(newpass));
			acc.setBirthYear(Integer.valueOf(by));
			acc.setBirthMonth(Integer.valueOf(bm));
			acc.setBirthDay(Integer.valueOf(bd));
			acc.setLastServerId(Integer.valueOf(gs));
			__accDAO.createOrUpdate(acc);
			if (_log.isDebugEnabled())
				_log.info("Account " + account + " has been updated.");
		}
		catch (NumberFormatException e)
		{
			throw new AccountModificationException("Access level (" + level + ") should be an integer.", e);
		}
		return acc;
	}

	public Accounts addOrUpdateAccount(String account, String password, String level) throws AccountModificationException
	{
		return addOrUpdateAccount(account, password, level, 1900, 1, 1, 0);
	}

	/**
	 * Add or update an account
	 * @param acc
	 * @return the new account
	 * @throws AccountModificationException
	 */
	public Accounts addOrUpdateAccount(Accounts acc) throws AccountModificationException
	{
		// o update account
		// ---------------
		try
		{
			__accDAO.createOrUpdate(acc);
			if (_log.isDebugEnabled())
				_log.info("Account " + acc.getLogin() + " has been updated.");
		}
		catch (DataAccessException e)
		{
			throw new AccountModificationException("Unable to create account.", e);
		}
		return acc;
	}

	/**
	 * Change account level
	 * @param account - the account to update
	 * @param level - the new level
	 * @throws AccountModificationException
	 */
	public void changeAccountLevel(String account, String level) throws AccountModificationException
	{
		// Search account
		// ---------------
		Accounts acc = __accDAO.getAccountById(account);

		if (acc == null)
			throw new AccountModificationException("Account " + account + " doesn't exist.");

		// Update account
		// --------------
		try
		{
			Integer iLevel = new Integer(level);
			acc.setAccessLevel(iLevel);
			__accDAO.createOrUpdate(acc);
			if (_log.isDebugEnabled())
				_log.debug("Account " + account + " has been updated.");
		}
		catch (NumberFormatException e)
		{
			throw new AccountModificationException("Error : level (" + level + ") should be an integer.", e);
		}
	}

	/**
	 * Change account level
	 * @param account - the account to upadte
	 * @param level - the new level
	 * @throws AccountModificationException
	 */
	public void changeAccountLevel(String account, int level) throws AccountModificationException
	{
		// Search account
		// ---------------
		Accounts acc = __accDAO.getAccountById(account);

		if (acc == null)
			throw new AccountModificationException("Account " + account + " doesn't exist.");

		// Update account
		// --------------
		Integer iLevel = new Integer(level);
		acc.setAccessLevel(iLevel);
		__accDAO.update(acc);
		if (_log.isDebugEnabled())
			_log.debug("Account " + account + " has been updated.");
	}

	/**
	 * Delete account and all linked objects
	 * @param account
	 * @throws AccountModificationException if account doest not exist
	 */
	public void deleteAccount(String account) throws AccountModificationException
	{
		// Search and delete account
		// ---------------
		Accounts acc = null;
		try
		{
			acc = __accDAO.getAccountById(account);
		}
		catch (ObjectRetrievalFailureException e)
		{
			throw new AccountModificationException("Unable to delete account : " + account + ". This account does not exist.");
		}
		__accDAO.removeAccount(acc);
	}

	/**
	 * Get accounts information
	 *
	 */
	public List<Accounts> getAccountsInfo()
	{
		List<Accounts> list = __accDAO.getAllAccounts();
		return list;
	}

	/**
	 * Get account information for a specific id
	 * 
	 */
	public Accounts getAccountById(String id)
	{
		try
		{
			Accounts acc = __accDAO.getAccountById(id);
			return acc;
		}
		catch (ObjectRetrievalFailureException e)
		{
			if (_log.isDebugEnabled())
				_log.debug("Account not found in database: " + id, e);
			return null;
		}
		catch (Exception e)
		{
			_log.warn("", e);
			return null;
		}
	}

	public boolean exists(String accountName) {
		try {
			__accDAO.getAccountById(accountName);
			return true;
		}
		catch (ObjectRetrievalFailureException e) {
			return false;
		}
	}
}
