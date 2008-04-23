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

import net.sf.l2j.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * This returns general Infos About System.
 *
 * @author Rayan
 *
 */
public class SystemService
{
	private static final Log _log = LogFactory.getLog(SystemService.class.getName());
	private static SystemService _instance;
	String ServerMode = "LoginServer";
	public static SystemService getInstance()
    {
        if(_instance == null)
        {
        	 _instance = new SystemService();
        }
		return _instance;
    }
	/**
	 * 
	 *
	 */
	public void getOperationalSystem()
	{
		_log.info("Detecting Operational System Information....");
		_log.info("processing...");
		ThreadMilisService.processSleep(3);
		_log.info("Operational System: "+System.getProperty("os.name")+ " Build: "+System.getProperty("os.version"));//returns operation system version and name
		_log.info("Operational System Architecture: "+System.getProperty("os.arch")); //returns operation system architecture
		_log.info("..................................................");
		_log.info("..................................................");
	}
	/**
	 * 
	 *
	 */
	public void getJREInfo()
	{
		_log.info("Detecting Java Runtime Environment(JRE) please wait...");
		_log.info("processing...");
		ThreadMilisService.processSleep(3);
		_log.info("JRE vendor: "+System.getProperty("java.vendor"));//Java Runtime Environment vendor
		_log.info("JRE vendo url: "+System.getProperty("java.vendor.url"));
		_log.info("JRE specification version: "+System.getProperty("java.specification.version"));// Java Runtime Environment specification version 
		_log.info("JRE specification vendor: "+System.getProperty("java.specification.vendor"));// Java Runtime Environment specification vendor 
		_log.info("JRE version: "+System.getProperty("java.version"));//Java Runtime Environment version
		_log.info("..................................................");
		_log.info("..................................................");
		
	}
	/**
	 * 
	 *
	 */
	public void getMachineInfo()
	{
		
		_log.info("Detecting Machine please wait...");
		_log.info("processing...");
		ThreadMilisService.processSleep(2);
		_log.info("Total Memory: "+Runtime.getRuntime().totalMemory()/1024 * 1024+" MB");
		_log.info("Free Ram: "+Runtime.getRuntime().freeMemory()/1024 * 1024+" MB");
		if(!Config.DEVELOPER)
		{
		_log.info("JRE version: "+System.getProperty("java.version"));
		_log.info("JRE vendor url: "+System.getProperty("java.vendor.url"));
		_log.info("JRE specification vendor: "+System.getProperty("java.specification.vendor"));
		}
		_log.info("..................................................");
		_log.info("..................................................");
		ThreadMilisService.processSleep(2);
	}
	/**
	 * 
	 *
	 */
	public void getSystemTime()
	{
		_log.info("..................................................");
		_log.info("System Time: "+TimeService.Datenow());
		_log.info("..................................................");
	}
	/**
	 * 
	 *
	 */
	public void getJVMInfo()
	{
		_log.info("Detecting Java Virtual Machine (JVM) please wait...");
		_log.info("processing...");
		ThreadMilisService.processSleep(3);
		_log.info("JVM installation directory: "+System.getProperty("java.home"));
		_log.info("JVM Avaible Memory(RAM): "+Runtime.getRuntime().maxMemory()/1024 * 1024+" MB");
		_log.info("JVM specification version: "+System.getProperty("java.vm.specification.version"));//Java Virtual Machine specification version 
		_log.info("JVM specification vendor: "+System.getProperty("java.vm.specification.vendor"));// Java Virtual Machine specification vendor 
		_log.info("JVM specification name: "+System.getProperty("java.vm.specification.name"));// Java Virtual Machine specification name 
		_log.info("JVM implementation version: "+System.getProperty("java.vm.version"));// Java Virtual Machine implementation version 
		_log.info("JVM implementation vendor: "+System.getProperty("java.vm.vendor"));// Java Virtual Machine implementation vendor 
		_log.info("JVM implementation name: "+System.getProperty("java.vm.name"));// Virtual Machine implementation name 
		_log.info("..................................................");
	}
	
	/**
	 * 
	 *
	 */
	public void getCpuInfo()
	{
		_log.info("Detecting System Processor please wait...");
		_log.info("processing...");
		ThreadMilisService.processSleep(5);
		_log.info("Avaible Processors: "+Runtime.getRuntime().availableProcessors());
		_log.info("Processor Identifier: "+System.getenv("PROCESSOR_IDENTIFIER"));        
		_log.info("..................................................");
		_log.info("..................................................");
        _log.info(ServerMode+" is Starting up...");
	}
}