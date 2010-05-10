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
package com.l2jfree.gameserver.model.restriction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Noctarius
 */
public final class ObjectRestrictions
{
	// Restrictions SQL String Definitions:
	private static final String				RESTORE_RESTRICTIONS	= "SELECT obj_Id, type, delay, message FROM obj_restrictions";
	private static final String				DELETE_RESTRICTIONS		= "DELETE FROM obj_restrictions";
	private static final String				INSERT_RESTRICTIONS		= "INSERT INTO obj_restrictions (`obj_Id`, `type`, `delay`, `message`) VALUES (?, ?, ?, ?)";

	private static final Log				_log					= LogFactory.getLog(ObjectRestrictions.class);

	private static final ObjectRestrictions	_instance				= new ObjectRestrictions();

	public static ObjectRestrictions getInstance()
	{
		return _instance;
	}

	private final Map<Integer, EnumSet<AvailableRestriction>>	_restrictionList	= new FastMap<Integer, EnumSet<AvailableRestriction>>();
	private final Map<Integer, List<PausedTimedEvent>>			_pausedActions		= new FastMap<Integer, List<PausedTimedEvent>>();
	private final Map<Integer, List<TimedRestrictionAction>>	_runningActions		= new FastMap<Integer, List<TimedRestrictionAction>>();

	private ObjectRestrictions()
	{
		_log.info("ObjectRestrictions: loading...");

		int count = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_RESTRICTIONS);
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				final Integer objId = rset.getInt("obj_Id");
				final AvailableRestriction type = AvailableRestriction.forName(rset.getString("type"));
				final int delay = rset.getInt("delay");
				final String message = rset.getString("message");

				switch (delay)
				{
				case -1:
					addRestriction(objId, type);
					break;
				default:
					timedAddRestriction(objId, type, delay, message);
					break;
				}
				count++;
			}

			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warn("", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}

		for (Integer objectId : _runningActions.keySet())
			pauseTasks(objectId);

		_log.info("ObjectRestrictions: loaded " + count + " restrictions.");
	}

	public void shutdown()
	{
		System.out.println("ObjectRestrictions: storing started:");

		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			// Clean up old table data
			PreparedStatement statement = con.prepareStatement(DELETE_RESTRICTIONS);
			statement.execute();
			statement.close();

			System.out.println("ObjectRestrictions: storing permanent restrictions.");
			// Store permanent restrictions
			for (Entry<Integer, EnumSet<AvailableRestriction>> entry : _restrictionList.entrySet())
			{
				for (AvailableRestriction restriction : entry.getValue())
				{
					statement = con.prepareStatement(INSERT_RESTRICTIONS);

					statement.setInt(1, entry.getKey());
					statement.setString(2, restriction.name());
					statement.setLong(3, -1);
					statement.setString(4, "");

					statement.execute();
					statement.close();
				}
			}

			System.out.println("ObjectRestrictions: storing paused events.");
			// Store paused restriction events
			for (Entry<Integer, List<PausedTimedEvent>> entry : _pausedActions.entrySet())
			{
				for (PausedTimedEvent paused : entry.getValue())
				{
					statement = con.prepareStatement(INSERT_RESTRICTIONS);

					statement.setInt(1, entry.getKey());
					statement.setString(2, paused.getAction().getRestriction().name());
					statement.setLong(3, paused.getRemainingTime());
					statement.setString(4, paused.getAction().getMessage());

					statement.execute();
					statement.close();
				}
			}

			System.out.println("ObjectRestrictions: stopping and storing running events.");
			// Store running restriction events
			for (Entry<Integer, List<TimedRestrictionAction>> entry : _runningActions.entrySet())
			{
				for (TimedRestrictionAction action : entry.getValue())
				{
					// Shutdown task
					action.getTask().cancel(true);

					statement = con.prepareStatement(INSERT_RESTRICTIONS);

					statement.setInt(1, entry.getKey());
					statement.setString(2, action.getRestriction().name());
					statement.setLong(3, action.getRemainingTime());
					statement.setString(4, action.getMessage());

					statement.execute();
					statement.close();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}

		System.out.println("ObjectRestrictions: All data saved.");
	}

	/**
	 * Adds a restriction on startup
	 * 
	 * @param objId
	 * @param restriction
	 */
	private void addRestriction(Integer objId, AvailableRestriction restriction)
	{
		EnumSet<AvailableRestriction> set = _restrictionList.get(objId);

		if (set == null)
			_restrictionList.put(objId, set = EnumSet.noneOf(AvailableRestriction.class));

		if (set.add(restriction))
		{
			final L2PcInstance player = L2World.getInstance().findPlayer(objId);

			if (player != null)
				restriction.activatedOn(player);
		}
	}

	/**
	 * Adds a restriction without timelimit
	 * 
	 * @param owner
	 * @param restriction
	 * @throws RestrictionBindClassException
	 */
	public void addRestriction(L2PcInstance owner, AvailableRestriction restriction)
	{
		if (owner == null)
			return;

		addRestriction(owner.getObjectId(), restriction);
	}

	/**
	 * Removes a restriction
	 * 
	 * @param objId
	 * @param restriction
	 */
	public void removeRestriction(Integer objId, AvailableRestriction restriction)
	{
		final EnumSet<AvailableRestriction> set = _restrictionList.get(objId);

		if (set != null)
		{
			if (set.remove(restriction))
			{
				final L2PcInstance player = L2World.getInstance().findPlayer(objId);

				if (player != null)
					restriction.deactivatedOn(player);
			}
		}

		final List<TimedRestrictionAction> runningActions = _runningActions.get(objId);

		if (runningActions != null)
		{
			for (Iterator<TimedRestrictionAction> iter = runningActions.iterator(); iter.hasNext();)
			{
				TimedRestrictionAction action = iter.next();

				if (action.getRestriction() == restriction)
				{
					action.getTask().cancel(true);
					iter.remove();
				}
			}
		}

		final List<PausedTimedEvent> pausedActions = _pausedActions.get(objId);

		if (pausedActions != null)
		{
			for (Iterator<PausedTimedEvent> iter = pausedActions.iterator(); iter.hasNext();)
			{
				PausedTimedEvent paused = iter.next();

				if (paused.getAction().getRestriction() == restriction)
				{
					iter.remove();
				}
			}
		}
	}

	/**
	 * Removes a restriction
	 * 
	 * @param owner
	 * @param restriction
	 */
	public void removeRestriction(L2PcInstance owner, AvailableRestriction restriction)
	{
		if (owner == null)
			return;

		removeRestriction(owner.getObjectId(), restriction);
	}

	/**
	 * Checks if restriction is underway
	 * 
	 * @param owner
	 * @param restriction
	 * @return
	 */
	public boolean checkRestriction(L2PcInstance owner, AvailableRestriction restriction)
	{
		if (owner == null)
			return false;

		final EnumSet<AvailableRestriction> set = _restrictionList.get(owner.getObjectId());

		if (set == null)
			return false;

		return set.contains(restriction);
	}

	/**
	 * Schedules a new RemoveRestriction event without info message
	 * 
	 * @param objId
	 * @param restriction
	 * @param delay
	 */
	public void timedRemoveRestriction(Integer objId, AvailableRestriction restriction, long delay)
	{
		timedRemoveRestriction(objId, restriction, delay, null);
	}

	/**
	 * Schedules a new RemoveRestriction event with info message
	 * 
	 * @param objId
	 * @param restriction
	 * @param delay
	 * @param message
	 */
	public void timedRemoveRestriction(Integer objId, AvailableRestriction restriction, long delay, String message)
	{
		new TimedRestrictionAction(objId, restriction, TimedRestrictionType.REMOVE, delay, message);
	}

	/**
	 * Schedules a new AddRestriction event without info message
	 * 
	 * @param owner
	 * @param restriction
	 * @param delay
	 * @throws RestrictionBindClassException
	 */
	public void timedAddRestriction(L2PcInstance owner, AvailableRestriction restriction, long delay)
	{
		timedAddRestriction(owner, restriction, delay, null);
	}

	/**
	 * Schedules a new AddRestriction event with info message
	 * 
	 * @param owner
	 * @param restriction
	 * @param delay
	 * @param message
	 * @throws RestrictionBindClassException
	 */
	public void timedAddRestriction(L2PcInstance owner, AvailableRestriction restriction, long delay, String message)
	{
		timedAddRestriction(owner.getObjectId(), restriction, delay, message);
	}

	private void timedAddRestriction(Integer objId, AvailableRestriction restriction, long delay, String message)
	{
		new TimedRestrictionAction(objId, restriction, TimedRestrictionType.ADD, delay, message);
	}

	/**
	 * Adds a new active scheduled task
	 * 
	 * @param objId
	 * @param action
	 */
	private void addTask(Integer objId, TimedRestrictionAction action)
	{
		List<TimedRestrictionAction> list = _runningActions.get(objId);

		if (list == null)
			_runningActions.put(objId, list = new ArrayList<TimedRestrictionAction>());

		if (!list.contains(action))
			list.add(action);
	}

	private void removeTask(Integer objId, TimedRestrictionAction action)
	{
		List<TimedRestrictionAction> list = _runningActions.get(objId);

		if (list != null)
			list.remove(action);
	}

	/**
	 * Adds a new paused scheduled task
	 * 
	 * @param objId
	 * @param action
	 */
	private void addPausedTask(Integer objId, PausedTimedEvent action)
	{
		List<PausedTimedEvent> list = _pausedActions.get(objId);

		if (list == null)
			_pausedActions.put(objId, list = new ArrayList<PausedTimedEvent>());

		if (!list.contains(action))
			list.add(action);
	}

	/**
	 * Pauses tasks on player logout
	 * 
	 * @param objId
	 */
	public void pauseTasks(Integer objId)
	{
		final List<TimedRestrictionAction> list = _runningActions.remove(objId);

		if (list == null || list.isEmpty())
			return;

		for (TimedRestrictionAction action : list)
			action.pause();
	}

	/**
	 * Resumes tasks on player login
	 * 
	 * @param objId
	 */
	public void resumeTasks(Integer objId)
	{
		final List<PausedTimedEvent> list = _pausedActions.remove(objId);

		if (list == null || list.isEmpty())
			return;

		for (PausedTimedEvent paused : list)
			paused.activate();
	}

	private static enum TimedRestrictionType
	{
		REMOVE, ADD
	}

	private final class TimedRestrictionAction implements Runnable
	{
		private final Integer				_objId;
		private final AvailableRestriction	_restriction;
		private final TimedRestrictionType	_type;
		private final long					_delay;
		private final String				_message;
		private final long					_starttime	= System.currentTimeMillis();
		private final ScheduledFuture<?>	_task;

		private TimedRestrictionAction(Integer objId, AvailableRestriction restriction, TimedRestrictionType type, long delay, String message)
		{
			_objId = objId;
			_restriction = restriction;
			_type = type;
			_delay = delay;
			_message = message;

			_task = ThreadPoolManager.getInstance().schedule(this, delay);

			addTask(objId, this);
		}

		public void run()
		{
			removeTask(getObjectId(), this);

			switch (getType())
			{
			case ADD:
				addRestriction(getObjectId(), getRestriction());
				break;
			case REMOVE:
				removeRestriction(getObjectId(), getRestriction());
				break;
			}

			if (getMessage() != null)
			{
				final L2PcInstance owner = L2World.getInstance().findPlayer(getObjectId());

				if (owner != null)
					owner.sendMessage(getMessage());
			}
		}

		private void pause()
		{
			// Cancel active task
			getTask().cancel(true);

			// Save PausedEventObject
			new PausedTimedEvent(this);
		}

		private Integer getObjectId()
		{
			return _objId;
		}

		private AvailableRestriction getRestriction()
		{
			return _restriction;
		}

		private TimedRestrictionType getType()
		{
			return _type;
		}

		private String getMessage()
		{
			return _message;
		}

		private long getDelay()
		{
			return _delay;
		}

		private long getRemainingTime()
		{
			return getDelay() - (System.currentTimeMillis() - _starttime);
		}

		private ScheduledFuture<?> getTask()
		{
			return _task;
		}
	}

	private final class PausedTimedEvent
	{
		private final TimedRestrictionAction	_action;
		private final long						_remainingTime;

		private PausedTimedEvent(TimedRestrictionAction action)
		{
			_action = action;
			_remainingTime = action.getRemainingTime();

			addPausedTask(action.getObjectId(), this);
		}

		private long getRemainingTime()
		{
			return _remainingTime;
		}

		private TimedRestrictionAction getAction()
		{
			return _action;
		}

		private void activate()
		{
			switch (getAction().getType())
			{
			case ADD:
				timedAddRestriction(getAction().getObjectId(), getAction().getRestriction(), getRemainingTime(), getAction().getMessage());
				break;
			case REMOVE:
				timedRemoveRestriction(getAction().getObjectId(), getAction().getRestriction(), getRemainingTime(), getAction().getMessage());
				break;
			}
		}
	}
}
