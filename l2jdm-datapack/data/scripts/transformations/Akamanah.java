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

public class Akamanah extends L2Transformation
{
	public Akamanah()
	{
		// id, colRadius, colHeight
		super(302, 10, 32.73);
	}
	
	@Override
	public void onTransform(L2PcInstance player)
	{
		// Set charachter name to transformed name
		player.getAppearance().setVisibleName("Akamanah");
		player.getAppearance().setVisibleTitle("");
		
		// give transformation skills
		transformedSkills(player);
	}
	
	@Override
	public void transformedSkills(L2PcInstance player)
	{
		addSkill(player, 3630, 1); // Void Burst
		addSkill(player, 3631, 1); // Void Flow
	}
	
	@Override
	public void onUntransform(L2PcInstance player)
	{
		// set character back to true name.
		player.getAppearance().setVisibleName(null);
		player.getAppearance().setVisibleTitle(null);
		
		// remove transformation skills
		removeSkills(player);
	}
	
	@Override
	public void removeSkills(L2PcInstance player)
	{
		removeSkill(player, 3630); // Void Burst
		removeSkill(player, 3631); // Void Flow
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new Akamanah());
	}
}
