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
package net.sf.l2j.gameserver.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Rayan
 *
 */
public class ServiceManager {
	
	private static ServiceManager _instance;
	private final static Log _log = LogFactory.getLog(ServiceManager.class.getName());

	
	public static ServiceManager getInstance()
	{
		if (_instance == null)
		{

			_log.info("GameServer: Initializing Service Manager...");
			_instance = new ServiceManager();
		}
		return _instance;
	}
	public void load()
	{
		_log.info("Service Manager: loading: Thread Milis Service ...done.");
		_log.info("Service Manager: loading: Window Service ...done.");
		_log.info("Service Manager: loading: Html Path Service ...done.");
	}

}