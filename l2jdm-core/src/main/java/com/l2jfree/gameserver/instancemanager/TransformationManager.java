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
package com.l2jfree.gameserver.instancemanager;

import java.util.Collection;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.model.L2Transformation;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 * @author  KenM
 */
public class TransformationManager
{
	protected static final Log					_log		= LogFactory.getLog(TransformationManager.class);

	public static TransformationManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private final Map<Integer, L2Transformation>	_transformations;

	private TransformationManager()
	{
		_transformations = new FastMap<Integer, L2Transformation>();
	}

	public void report()
	{
		_log.info("Loaded: " + getAllTransformations().size() + " transformations.");
	}

	public boolean transformPlayer(int id, L2PcInstance player)
	{
		L2Transformation template = getTransformationById(id);
		if (template != null)
		{
			if (player.getPet() != null)
				player.getPet().unSummon(player);

			player.transform(template);
			return true;
		}

		return false;
	}

	public L2Transformation getTransformationById(int id)
	{
		return _transformations.get(id);
	}

	public L2Transformation registerTransformation(L2Transformation transformation)
	{
		return _transformations.put(transformation.getId(), transformation);
	}

	public Collection<L2Transformation> getAllTransformations()
	{
		return _transformations.values();
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final TransformationManager _instance = new TransformationManager();
	}
}
