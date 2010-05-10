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
package com.l2jfree.loginserver.clientpackets;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import com.l2jfree.Config;
import com.l2jfree.loginserver.L2LoginClient;
import com.l2jfree.loginserver.L2LoginClient.LoginClientState;
import com.l2jfree.loginserver.beans.GameServerInfo;
import com.l2jfree.loginserver.manager.LoginManager;
import com.l2jfree.loginserver.manager.LoginManager.AuthLoginResult;
import com.l2jfree.loginserver.serverpackets.LoginFail;
import com.l2jfree.loginserver.serverpackets.LoginOk;
import com.l2jfree.loginserver.serverpackets.ServerList;
import com.l2jfree.loginserver.services.exception.AccountBannedException;
import com.l2jfree.loginserver.services.exception.AccountWrongPasswordException;
import com.l2jfree.loginserver.services.exception.IPRestrictedException;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an
 * password
 */
public class RequestAuthLogin extends L2LoginClientPacket
{
	private final byte[]	_raw	= new byte[128];

	private String	_user;
	private String	_password;
	private int		_ncotp;

	public String getPassword()
	{
		return _password;
	}

	public String getUser()
	{
		return _user;
	}

	public int getOneTimePassword()
	{
		return _ncotp;
	}

	@Override
	protected int getMinimumLength()
	{
		return 128;
	}
	
	@Override
	public void readImpl()
	{
		readB(_raw);

		/* First three bytes will match with AuthGameGuard's additional block's
		 * 2nd, 3rd and 4th byte respectively, then goes the randomly deviated 1st byte.
		 * Then 16 null bytes, a byte equal to 8, 10 null bytes, 4 unknown bytes,
		 * rest null bytes
		byte[] b = new byte[47];
		readB(b);
		_log.info("RAL: " + HexUtil.printData(b));
		*/
		skip(47);
	}

	@Override
	public void runImpl()
	{
		byte[] decrypted = null;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, getClient().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		}
		catch (GeneralSecurityException e)
		{
			e.printStackTrace();
			return;
		}

		_user = new String(decrypted, 0x5E, 14).trim();
		_user = _user.toLowerCase();
		_password = new String(decrypted, 0x6C, 16).trim();
		_ncotp = decrypted[0x7c];
		_ncotp |= decrypted[0x7d] << 8;
		_ncotp |= decrypted[0x7e] << 16;
		_ncotp |= decrypted[0x7f] << 24;

		LoginManager lc = LoginManager.getInstance();
		L2LoginClient client = getClient();
		try
		{
			AuthLoginResult result = lc.tryAuthLogin(_user, _password, client);
			switch (result)
			{
				case AUTH_SUCCESS:
					client.setAccount(_user);
					client.setState(LoginClientState.AUTHED_LOGIN);
					client.setSessionKey(lc.assignSessionKeyToClient(_user, client));
					if (Config.SECURITY_CARD_LOGIN)
						client.sendPacket(new LoginFail(LoginFail.REASON_INVALID_SECURITY_CARD_NO));
					else if (Config.SHOW_LICENCE)
						client.sendPacket(new LoginOk(client.getSessionKey()));
					else
						client.sendPacket(new ServerList(client));
					break;
				case ALREADY_ON_LS:
					L2LoginClient oldClient;
					if ((oldClient = lc.getAuthedClient(_user)) != null)
					{
						// kick the other client
						oldClient.closeLogin(LoginFail.REASON_ALREADY_IN_USE);
						lc.removeAuthedLoginClient(_user);
					}
					// kick also current client
					client.closeLogin(LoginFail.REASON_ALREADY_IN_USE);
					break;
				case ALREADY_ON_GS:
					GameServerInfo gsi;
					if ((gsi = lc.getAccountOnGameServer(_user)) != null)
					{
						client.closeLogin(LoginFail.REASON_ALREADY_IN_USE);

						// kick from there
						if (gsi.isAuthed())
							gsi.getGameServerThread().kickPlayer(_user);
					}
					break;
				case SYSTEM_ERROR:
				default:
					client.closeLogin(LoginFail.REASON_THERE_IS_A_SYSTEM_ERROR);

			}
		}
		// catch (HackingException e)
		// {
		// 	InetAddress address = getClient().getSocket().getInetAddress();
		// 	BanManager.getInstance().addBanForAddress(address, Config.LOGIN_BLOCK_AFTER_BAN * 1000);
		// 	_log.info("Banned (" + address + ") for " + Config.LOGIN_BLOCK_AFTER_BAN + " seconds, due to " + e.getConnects() + " incorrect login attempts.");
		// }
		catch (AccountBannedException e)
		{
			client.closeBanned();
		}
		catch (AccountWrongPasswordException e)
		{
			client.closeLogin(LoginFail.REASON_PASSWORD_INCORRECT);
		}
        catch (IPRestrictedException e)
        {
        	//client.closeBanned(e.getMinutesLeft());
        	client.closeBanned(-1);
		}
	}
}
