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
package transformations;

import com.l2jfree.gameserver.instancemanager.TransformationManager;
import com.l2jfree.gameserver.model.L2Transformation;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class OnyxBeast extends L2Transformation
{
	public OnyxBeast()
	{
		// id, colRadius, colHeight
		super(1, 14, 14.5);
	}
	
	@Override
	public void transformedSkills(L2PcInstance player)
	{
		addSkill(player, 584, 1); // Power Claw
		addSkill(player, 585, 1); // Fast Moving
	}
	
	@Override
	public void removeSkills(L2PcInstance player)
	{
		removeSkill(player, 584); // Power Claw
		removeSkill(player, 585); // Fast Moving
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new OnyxBeast());
	}
}
