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
package net.sf.l2j.gameserver.skills.funcs;

import junit.framework.TestCase;
import net.sf.l2j.gameserver.skills.Env;
import net.sf.l2j.gameserver.skills.Stats;

public class TestFuncDiv extends TestCase
{

    public void testFuncDivCalc()
    {
        FuncDiv fa = new FuncDiv(Stats.MAX_HP,1,null,new LambdaConst(2));
        
        Env env = new Env();
        env.value=1;
        fa.calc(env);
        assertEquals(0.5,env.value);
    }   
    
    public void testFuncDivCalcDivByZero()
    {
        FuncDiv fa = new FuncDiv(Stats.MAX_HP,1,null,new LambdaConst(0));
        
        Env env = new Env();
        env.value=1;
        fa.calc(env);
        assertEquals(Double.POSITIVE_INFINITY,env.value);
    }    

}
