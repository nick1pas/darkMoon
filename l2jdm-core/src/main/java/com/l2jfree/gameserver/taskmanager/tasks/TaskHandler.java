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
package com.l2jfree.gameserver.taskmanager.tasks;

import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.taskmanager.tasks.TaskManager.ExecutedTask;

/**
 * @author Layane
 */
abstract class TaskHandler
{
	static final Log _log = LogFactory.getLog(TaskHandler.class);
	
	final String getName()
	{
		return getClass().getSimpleName().replace("Task", "");
	}
	
	ScheduledFuture<?> launchSpecial(ExecutedTask task)
	{
		return null;
	}
	
	abstract void onTimeElapsed(ExecutedTask task, String[] params);
	
	void onDestroy(ExecutedTask task)
	{
	}
}
