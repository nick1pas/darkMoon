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

public class VanguardTempleKnight extends L2Transformation
{
	public VanguardTempleKnight()
	{
		// id
		super(314);
	}
	
	@Override
	public void transformedSkills(L2PcInstance player)
	{
		if (player.getLevel() > 43)
		{
			int level = player.getLevel() - 43;
			addSkill(player, 293, level); // Two handed mastery
			addSkill(player, 814, level); // Full Swing
			addSkill(player, 816, level); // Power Divide
		}
		
		player.addTransformAllowedSkill(new int[] { 28, 18, 10, 67, 449, 400, 197 });
	}
	
	@Override
	public void removeSkills(L2PcInstance player)
	{
		removeSkill(player, 293); // Two handed mastery
		removeSkill(player, 814); // Full Swing
		removeSkill(player, 816); // Power Divide
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new VanguardTempleKnight());
	}
}
