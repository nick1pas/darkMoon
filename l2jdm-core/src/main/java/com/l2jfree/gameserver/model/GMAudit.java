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
package com.l2jfree.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class GMAudit
{
	private static final Log _log = LogFactory.getLog(GMAudit.class);
	
	public static void auditGMAction(L2PcInstance gm, String type, String action, String param)
	{
		if (Config.GM_AUDIT)
		{
			String gm_name = gm.getAccountName() + " - " + gm.getName();
			String target = "null";
			
			L2Object targetChar = gm.getTarget();
			if (targetChar != null)
				target = targetChar.getObjectId() + " - " + targetChar.getName();
			
			auditGMAction(gm_name, target, type, action, param);
		}
	}
	
	public static void auditGMAction(String gm_name, String target, String type, String action, String param)
	{
		if (Config.GM_AUDIT)
		{
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con
					.prepareStatement("INSERT INTO gm_audit(gm_name, target, type, action, param, date) VALUES(?,?,?,?,?,now())");
				
				statement.setString(1, gm_name);
				statement.setString(2, target);
				statement.setString(3, type);
				statement.setString(4, action);
				statement.setString(5, param);
				
				statement.executeUpdate();
			}
			catch (Exception e)
			{
				_log.fatal("", e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
	}
}
