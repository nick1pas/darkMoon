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

public class UnicornWeak extends L2Transformation
{
	public UnicornWeak()
	{
		// id, colRadius, colHeight
		super(206, 15, 28);
	}
	
	@Override
	public void transformedSkills(L2PcInstance player)
	{
		addSkill(player, 563, 2); // Horn of Doom
		addSkill(player, 564, 2); // Gravity Control
		addSkill(player, 565, 2); // Horn Assault
		addSkill(player, 567, 2); // Light of Heal
	}
	
	@Override
	public void removeSkills(L2PcInstance player)
	{
		removeSkill(player, 563); // Horn of Doom
		removeSkill(player, 564); // Gravity Control
		removeSkill(player, 565); // Horn Assault
		removeSkill(player, 567); // Light of Heal
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new UnicornWeak());
	}
}
