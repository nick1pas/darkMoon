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
package net.sf.l2j.loginserver.services;
/**
 * an special instace to manage Config pathfinding.
 * @author Rayan
 *
 */
public class PathFindingService {

	/**
	 * network
	 */
	public static final String  DB_FILE          = "./config/network/database.properties";
	public static final String  TELNET_FILE		 = "./config/network/telnet.properties";
	public static final String  NETWORK_FILE     = "./config/network/network.properties";
	public static final String SECURITY_FILE     = "./config/network/security.properties";
	
	/**
	 * dev
	 */	
	public static final String  DEV_FILE         = "./config/developer/settings.properties";
	
	/**
	 * main
	 */
	public static final String  BAN_FILE         = "./config/main/ban.properties";
	public static final String  LOGIN_FILE       = "./config/main/loginserver.properties";
	
}
