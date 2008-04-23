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
 * <b> This Class Returns The Following Infos About System: </b><br><br>
 * 
 * <li> CPU Info<br>
 * <li> System OS <br>
 * <li> System Architecture <br>
 * <li> System JRE <br>
 * <li> Mahcine Info <br>
 * <li> JVM Info <br>
 * <li> General Infos Related to Memory <br><br>
 * 
 * @author Rayan RPG for L2Emu Project
 * 
 * @since 512
 *
 */
public class SystemService
{
	private static final Log _log = LogFactory.getLog(SystemService.class.getName());
	private static SystemService _instance;

	public static SystemService getInstance()
	{
		if(_instance == null)
		{
			_instance = new SystemService();
		}
		return _instance;
	}

	/**
	 * returns how many processar are installed on this system.
	 */
	public void getCpuInfo()
	{
		_log.info("Detecting System Processor please wait...");
		_log.info("processing...");
		ThreadService.processSleep(5);
		_log.info("Avaible Processors: "+Runtime.getRuntime().availableProcessors());
		_log.info("Processor Identifier: "+System.getenv("PROCESSOR_IDENTIFIER"));        
		_log.info("..................................................");
		_log.info("..................................................");
		_log.info("GameServer is Starting up...");
	}

	/**
	 * returns the operational system server is running on it.
	 */
	public void getOperationalSystem()
	{
		_log.info("Detecting Operational System Information....");
		_log.info("processing...");
		ThreadService.processSleep(3);
		_log.info("Operational System: "+System.getProperty("os.name")+ " Build: "+System.getProperty("os.version"));//returns operation system version and name
		_log.info("Operational System Architecture: "+System.getProperty("os.arch")); //returns operation system architecture
		_log.info("..................................................");
		_log.info("..................................................");
	}

	/**
	 * returns JAVA Runtime Enviroment properties
	 */
	public void getJREInfo()
	{
		_log.info("Detecting Java Runtime Environment(JRE) please wait...");
		_log.info("processing...");
		ThreadService.processSleep(3);
		_log.info("JRE vendor: "+System.getProperty("java.vendor"));//Java Runtime Environment vendor
		_log.info("JRE vendo url: "+System.getProperty("java.vendor.url"));
		_log.info("JRE specification version: "+System.getProperty("java.specification.version"));// Java Runtime Environment specification version 
		_log.info("JRE specification vendor: "+System.getProperty("java.specification.vendor"));// Java Runtime Environment specification vendor 
		_log.info("JRE version: "+System.getProperty("java.version"));//Java Runtime Environment version
		_log.info("..................................................");
		_log.info("..................................................");

	}

	/**
	 * returns general infos related to machine
	 */
	public void getMachineInfo()
	{

		_log.info("Detecting Machine please wait...");
		_log.info("processing...");
		ThreadService.processSleep(2);
		_log.info("Total Memory: "+Runtime.getRuntime().totalMemory()/1024 * 1024+" MB");
		_log.info("Free Ram: "+Runtime.getRuntime().freeMemory()/1024 * 1024+" MB");
		_log.info("..................................................");
		_log.info("..................................................");
		ThreadService.processSleep(2);
	}

	/** 
	 * calls time service to get system time.
	 */
	public void getSystemTime()
	{
		_log.info("..................................................");
		_log.info("System Time: "+TimeService.Datenow());
		_log.info("..................................................");
	}

	/**
	 * detects system JVM properties.
	 */
	public void getJVMInfo()
	{
		_log.info("Detecting Java Virtual Machine (JVM) please wait...");
		_log.info("processing...");
		ThreadService.processSleep(3);
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

	//**************************************** MEMORY IN MB ***********************************************/
	/**
	 * freeMemory the unused memory in the allocation pool. <br>
	 * returns unused memory in mb <br>
	 * @return
	 */
	public long getFreeMemory()
	{
		return (Runtime.getRuntime().maxMemory()- Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576;
	}

	/**
	 * returns used memory in mb <br>
	 * @retugrn
	 */
	public long getUsedMemory()
	{
		return  getTotalMemory() - getFreeMemory();
	}

	/**
	 * returns total memory in mb <br>
	 * totalMemory the size of the current allocation pool.
	 * @return
	 */
	public long getTotalMemory()
	{
		return Runtime.getRuntime().maxMemory() / 1048576;
	}

	//****************************** MEMORY IN BYTES ************************************/
	/**
	 * Get current size of heap in bytes <br>
	 * @return 
	 * @return Runtime.getRuntime().totalMemory();
	 */
	public long getHeapSize()
	{
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Get maximum size of heap in bytes. <br>
	 * The heap cannot grow beyond this size. <br>
	 * Any attempt will result in an OutOfMemoryException.
	 * @return 
	 * @return Runtime.getRuntime().maxMemory();
	 */
	public long getHeapMaxSize()
	{
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * Get amount of free memory within the heap in bytes. <br>
	 * This size will increase after garbage collection and decrease as new objects are created. <br>
	 * @return 
	 * @return Runtime.getRuntime().freeMemory();
	 */
	public long getHeapFreeSize()
	{
		return Runtime.getRuntime().freeMemory();
	}
}