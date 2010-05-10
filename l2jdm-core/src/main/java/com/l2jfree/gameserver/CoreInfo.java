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
package com.l2jfree.gameserver;

import java.util.Date;

import com.l2jfree.L2Config;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.mmocore.network.SelectorThread;
import com.l2jfree.versionning.Version;

/**
 * @author noctarius
 */
public final class CoreInfo
{
	private CoreInfo()
	{
	}
	
	private static final CoreVersion coreVersion = new CoreVersion(GameServer.class);
	private static final CoreVersion commonsVersion = new CoreVersion(L2Config.class);
	private static final CoreVersion mmocoreVersion = new CoreVersion(SelectorThread.class);
	
	public static void showStartupInfo()
	{

		System.out.println("L2DarkMoon [starting version: " + coreVersion.getVersionNumber() + "]");
	}
	
	public static final void versionInfo(L2PcInstance activeChar)
	{
		activeChar.sendMessage("Welcome to");
		activeChar.sendMessage("l2DarkMoon " + getVersionInfo());
		activeChar.sendMessage("Chaotic Throne: Gracia Final");
		activeChar.sendMessage("");
		activeChar.sendMessage("");
	}
	
	public static String getVersionInfo()
	{
		return coreVersion.versionInfo;
	}
	
	public static String[] getFullVersionInfo()
	{
		return new String[] {
				"l2jdm-core   :    " + coreVersion.fullVersionInfo,
				"l2j-commons  :    " + commonsVersion.fullVersionInfo,
				"l2j-mmocore  :    " + mmocoreVersion.fullVersionInfo };
	}
	
	private static final class CoreVersion extends Version
	{
		public final String versionInfo;
		public final String fullVersionInfo;
		
		public CoreVersion(Class<?> c)
		{
			super(c);
			
			versionInfo = String.format("%-6s [ %4s ]", getVersionNumber(), getRevisionNumber());
			fullVersionInfo = versionInfo + " - " + getBuildJdk() + " - " + new Date(getBuildTime());
		}
	}
}
