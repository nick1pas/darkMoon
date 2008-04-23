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
package net.sf.l2j.gameserver.hardcodedtables;

/**
 * Table Containg all Chats of Npcs Walkers<br><br>
 * 
 * <b> Structure:</b><br>
 * <li> home chat, <br>
 * <li> chat1, <br>
 * <li> chat2, <br>
 * <li> chat3 etc.... in sequence <br><br>
 * 
 * @author Rayan RPG
 * @since 881
 *
 */
public class ChatTable
{
	// =============== TI VILLAGE ==================================================
	
	/** Table Max Index 2 **/
	public static final String[] LEANDRO_CHAT_TABLE = 
	{
		"Where has he gone?",
		"Have you seen Windawood?",
		"Where did he go"
	};
	/** Table Max Index 3 **/
	public static final String[] REMY_CHAT_TABLE =
	{
		"A delivery for Mr. Lector? Very good!", 
		"I need a break!",
		"Hello, Mr. Lector! Long time no see, Mr. Jackson!",
		"Lulu!"
	};
	//================ ELF VILLAGE ==================================================
	/** Table Max Index 1**/
	public static final String[] KASIEL_CHAT_TABLE =
	{
		"The Mother Tree is always so gorgeous!",
		"Lady Mirabel, may the peace of the lake be with you!"
	};
	/** Table Max Index 1 **/
	public static final String[] JARADINE_CHAT_TABLE = 
	{
		"The Mother Tree is slowly dying.",
		"How can we save the Mother Tree?"
	};
	
	//================ DE VILLAGE ====================================================
	/** Table Max Index 1 **/
	public static final String[] ALHENA_CHAT_TABLE =
	{
		"You're a hard worker, Rayla!",
		"You're a hard worker!"
	};
	/** Table Max Index 0 **/
	public static final String KREED_CHAT_TABLE[] = 
	{
		"The mass of darkness will start in a couple of days. Pay more attention to the guard!"
	};
	
	
	//================ DWARVEN VILLAGE ================================================
	/** Table Max Index 2 **/
	public static final String[] TATE_CHAT_TABLE =
	{
		"Care to go a round?",
		"Have a nice day, Mr. Garita and Mion!",
		"Mr. Lid, Murdoc, and Airy! How are you doing?"
	};
	/** Table Max Index 2**/
	public static final String[] ROGIN_CHAT_TABLE =
	{
		"Have you seen Torocco today?",
		"Have you seen Torocco?",
		"Where is that fool hiding?"
	};	
	/** Table Max Index 0 **/
	public static final String[] UNIMPLEMENTED =
	{
		"my text is missing, still need to implement it!"
	};	
	//==================================================================================
}