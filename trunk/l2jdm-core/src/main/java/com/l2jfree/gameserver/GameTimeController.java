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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.datatables.DoorTable;
import com.l2jfree.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jfree.gameserver.instancemanager.hellbound.HellboundManager;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance.ConditionListenerDependency;
import com.l2jfree.gameserver.network.serverpackets.ClientSetTime;
import com.l2jfree.gameserver.util.Broadcast;
import com.l2jfree.lang.L2Thread;

public final class GameTimeController
{
	private static final Log _log = LogFactory.getLog(GameTimeController.class);
	
	public static final int TICKS_PER_SECOND = 10;
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	
	public static GameTimeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private final Calendar _calendar = new GregorianCalendar();
	
	private GameTimeController()
	{
		new File("data/serial").mkdirs();
		
		final Calendar cal = loadData();
		
		if (cal != null)
		{
			_calendar.setTimeInMillis(cal.getTimeInMillis());
		}
		else
		{
			_calendar.set(Calendar.YEAR, 1281);
			_calendar.set(Calendar.MONTH, 5);
			_calendar.set(Calendar.DAY_OF_MONTH, 5);
			_calendar.set(Calendar.HOUR_OF_DAY, 23);
			_calendar.set(Calendar.MINUTE, 45);
			
			saveData();
		}
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new MinuteCounter(), 0, 60000 / Config.DATETIME_MULTI);
		
		_log.info("GameTimeController: Initialized.");
	}
	
	private GregorianCalendar loadData()
	{
		if (!Config.DATETIME_SAVECAL)
			return null;
		
		ObjectInputStream is = null;
		try
		{
			is = new ObjectInputStream(new FileInputStream("data/serial/clock.dat"));
			
			return (GregorianCalendar)is.readObject();
		}
		catch (Exception e)
		{
			_log.warn("", e);
			
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}
	
	private void saveData()
	{
		if (!Config.DATETIME_SAVECAL)
			return;
		
		ObjectOutputStream os = null;
		try
		{
			os = new ObjectOutputStream(new FileOutputStream("data/serial/clock.dat"));
			
			os.writeObject(_calendar);
		}
		catch (IOException e)
		{
			_log.warn("", e);
		}
		finally
		{
			IOUtils.closeQuietly(os);
		}
	}
	
	private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("hh:mm a");
	private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	
	public String getFormattedGameTime()
	{
		return (Config.DATETIME_SAVECAL ? FORMAT1 : FORMAT2).format(_calendar.getTime());
	}
	
	public int getGameTime()
	{
		return _calendar.get(Calendar.HOUR_OF_DAY) * 60 + _calendar.get(Calendar.MINUTE);
	}
	
	public boolean isNowNight()
	{
		final int hour = _calendar.get(Calendar.HOUR_OF_DAY);
		
		return hour < Config.DATETIME_SUNRISE || Config.DATETIME_SUNSET <= hour;
	}
	
	private final class MinuteCounter implements Runnable
	{
		public void run()
		{
			final boolean isNight = isNowNight();
			
			final int oldHour = _calendar.get(Calendar.HOUR_OF_DAY);
			final int oldDay = _calendar.get(Calendar.DAY_OF_YEAR);
			final int oldYear = _calendar.get(Calendar.YEAR);
			
			_calendar.add(Calendar.MINUTE, 1);
			
			final int newHour = _calendar.get(Calendar.HOUR_OF_DAY);
			
			//check if one hour passed
			if (oldHour != newHour)
			{
				//update time for all players
				Broadcast.toAllOnlinePlayers(ClientSetTime.STATIC_PACKET);
				
				// check for zaken door
				if (newHour == Config.ALT_TIME_IN_A_DAY_OF_OPEN_A_DOOR)
				{
					DoorTable.getInstance().getDoor(21240006).openMe();

					ThreadPoolManager.getInstance().schedule(new Runnable()
					{
						public void run()
						{
							DoorTable.getInstance().getDoor(21240006).closeMe();
						}
					}, Config.ALT_TIME_OF_OPENING_A_DOOR * 60 * 1000);
				}

				// Blacksmith Shadai
				if (newHour == 0 || newHour == Config.DATETIME_SUNRISE)
				{
					HellboundManager.getInstance().recalculateShadaiSpawn();
				}
				
				//check if night state changed
				if (isNight != isNowNight())
				{
					DayNightSpawnManager.getInstance().notifyChangeMode();
					
					for (L2PcInstance player : L2World.getInstance().getAllPlayers())
						player.refreshConditionListeners(ConditionListenerDependency.GAME_TIME);
				}
				
				final int newDay = _calendar.get(Calendar.DAY_OF_YEAR);
				
				//check if a whole day passed
				if (oldDay != newDay)
				{
					_log.info("An in-game day passed - it's now: " + getFormattedGameTime());
					
					final int newYear = _calendar.get(Calendar.YEAR);
					
					if (oldYear != newYear)
					{
						Announcements.getInstance().announceToAll(
							"A new year has begun, good luck to all in the year " + newYear);
					}
				}
				
				saveData();
			}
		}
	}
	
	public static void stopTimer()
	{
		try
		{
			_timingThread.shutdown();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private static final long _gameStarted = System.currentTimeMillis();
	private static volatile int _gameTicks;
	
	public static int getGameTicks()
	{
		return _gameTicks;
	}
	
	private static final TimingThread _timingThread = new TimingThread();
	
	private static final class TimingThread extends L2Thread
	{
		private TimingThread()
		{
			super("TimingThread");
			setPriority(Thread.MAX_PRIORITY);
			setDaemon(true);
			
			start();
		}
		
		@Override
		protected void runTurn()
		{
			_gameTicks = (int)((System.currentTimeMillis() - _gameStarted) / MILLIS_IN_TICK);
		}
		
		@Override
		protected void sleepTurn() throws InterruptedException
		{
			long delay = _gameStarted + (_gameTicks + 1) * MILLIS_IN_TICK - System.currentTimeMillis();
			
			if (delay > 0)
				Thread.sleep(delay);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final GameTimeController _instance = new GameTimeController();
	}
}
