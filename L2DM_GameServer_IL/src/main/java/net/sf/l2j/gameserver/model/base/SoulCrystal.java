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
package net.sf.l2j.gameserver.model.base;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.hardcodedtables.HardcodedItemTable;
/** 
 * $ Rewrite 06.12.06 - Yesod 
 * */
public class SoulCrystal
{
    public static final int[][] HighSoulConvert = 
    {
     {HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_10, HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_11}, //RED 10 - 11
     {HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_11, HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_12}, //RED 11 - 12
     {HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_12, HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_13}, //RED 12 - 13
     
     {HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_10, HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_11}, //GRN 10 - 11
     {HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_11, HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_12}, //GRN 11 - 12
     {HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_12, HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_13}, //GRN 12 - 13
     
     {HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_10, HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_11}, //BLU 10 - 11
     {HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_11, HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_12}, //BLU 11 - 12
     {HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_12, HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_13}  //BLU 12 - 13
    };
   /** 
    * "First line is for Red Soul Crystals, second is Green and third is Blue Soul Crystals,
    *  ordered by ascending level, from 0 to 13..." 
    */
   public static final short[] SoulCrystalTable =
   { 
    	HardcodedItemTable.RED_SOUL_CRYSTAL,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_1,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_3,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_4,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_6,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_7,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_9,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_10,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_12,HardcodedItemTable.RED_SOUL_CRYSTAL_STAGE_13,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_1,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_3,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_4,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_6,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_7,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_9,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_10,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_12,HardcodedItemTable.GREEN_SOUL_CRYSTAL_STAGE_13,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_1,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_2,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_3,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_4,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_5,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_6,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_7,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_8,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_9,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_10,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_11,
    	HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_12,HardcodedItemTable.BLUE_SOUL_CRYSTAL_STAGE_13
   };
   
   public static final byte MAX_CRYSTALS_LEVEL = 13;
   public static final int BREAK_CHANCE = Config.CHANCE_BREAK;
   public static final int LEVEL_CHANCE = Config.CHANCE_LEVEL;
   
   public static final short RED_BROKEN_CRYSTAL = HardcodedItemTable.BROKEN_RED_SOUL_CRYSTAL;
   public static final short GRN_BROKEN_CYRSTAL = HardcodedItemTable.BROKEN_GREEN_SOUL_CRYSTAL;
   public static final short BLU_BROKEN_CRYSTAL = HardcodedItemTable.BROKEN_BLUE_SOUL_CRYSTAL;

   public static final short RED_NEW_CRYSTAL = HardcodedItemTable.RED_SOUL_CRYSTAL;
   public static final short GRN_NEW_CYRSTAL = HardcodedItemTable.GREEN_SOUL_CRYSTAL;
   public static final short BLU_NEW_CRYSTAL = HardcodedItemTable.BLUE_SOUL_CRYSTAL;

}
