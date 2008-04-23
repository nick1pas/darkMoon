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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.TaskPriority;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2ShortCut;
import net.sf.l2j.gameserver.model.L2SkillLearn;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.serverpackets.CharCreateFail;
import net.sf.l2j.gameserver.serverpackets.CharCreateOk;
import net.sf.l2j.gameserver.serverpackets.CharSelectInfo;
import net.sf.l2j.gameserver.templates.L2Item;
import net.sf.l2j.gameserver.templates.L2PcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.9.2.3.2.8 $ $Date: 2005/03/27 15:29:30 $
 */
@SuppressWarnings("unused")
public class CharacterCreate extends L2GameClientPacket
{
	private static final String _C__0B_CHARACTERCREATE = "[C] 0B CharacterCreate";
	private final static Log _log = LogFactory.getLog(CharacterCreate.class.getName());
	
	// cSdddddddddddd
	private String _name;
    private int _race;
    private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	public TaskPriority getPriority() { return TaskPriority.PR_HIGH; }
	
	/**
	 * @param decrypt
	 */
    @Override
    protected void readImpl()
    {
		
		_name      = readS();
		_race      = readD();
        _sex       = (byte)readD();
		_classId   = readD();
		_int       = readD();
		_str       = readD();
		_con       = readD();
		_men       = readD();
		_dex       = readD();
		_wit       = readD();
		_hairStyle = (byte)readD();
		_hairColor = (byte)readD();
		_face      = (byte)readD();
	}

    @Override
    protected void runImpl()
	{
        if (CharNameTable.getInstance().accountCharNumber(getClient().getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT && Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0)
        {
            if (_log.isDebugEnabled())
                _log.debug("Max number of characters reached. Creation failed.");
            CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
            sendPacket(ccf);
            return;
        }
        else if (CharNameTable.getInstance().doesCharNameExist(_name))
		{
			if (_log.isDebugEnabled())
				_log.debug("charname: "+ _name + " already exists. creation failed.");
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
			sendPacket(ccf);
			return;
		}
        else if (!Config.CNAME_PATTERN.matcher(_name).matches())
		{
			if (_log.isDebugEnabled()) 
				_log.debug("charname: " + _name + " is invalid. creation failed.");
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS);
			sendPacket(ccf);
			return;
		}

		if (_log.isDebugEnabled())
			_log.debug("charname: " + _name + " classId: " + _classId);
		
        L2PcTemplate template = CharTemplateTable.getInstance().getTemplate(_classId);
		if(template == null || template.getClassBaseLevel() > 1) 
		{
			CharCreateFail ccf = new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED);
			sendPacket(ccf);
			return;
		}
		
		int objectId = IdFactory.getInstance().getNextId();
		L2PcInstance newChar = L2PcInstance.create(objectId, template, getClient().getAccountName(),_name, _hairStyle, _hairColor, _face, _sex!=0);
		newChar.getStatus().setCurrentHp(template.getBaseHpMax());
		newChar.getStatus().setCurrentCp(template.getBaseCpMax());
		newChar.getStatus().setCurrentMp(template.getBaseMpMax());
		//newChar.setMaxLoad(template.baseLoad);
		
		// send acknowledgement
		CharCreateOk cco = new CharCreateOk();
		sendPacket(cco);

		initNewChar(getClient(), newChar);
	}
	
	private void initNewChar(L2GameClient client, L2PcInstance newChar)
	{   
		if (_log.isDebugEnabled()) _log.debug("Character init start");
		L2World.getInstance().storeObject(newChar);
		
		L2PcTemplate template = newChar.getTemplate();
		
		newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
		//L2JONEO_ADD_START
		newChar.addAncientAdena("Init", Config.STARTING_AA, null, false);
		
		if (Config.SPAWN_CHAR)
			newChar.getPosition().setXYZInvisible(Config.SPAWN_X, Config.SPAWN_Y, Config.SPAWN_Z);
		else
		newChar.getPosition().setXYZInvisible(template.getSpawnX(), template.getSpawnY(), template.getSpawnZ());

		if (Config.CHAR_TITLE)
             newChar.setTitle(Config.ADD_CHAR_TITLE);
		else
		newChar.setTitle("");
		//L2JONEO_ADD_END
		
		L2ShortCut shortcut;
		//add attack shortcut
		shortcut = new L2ShortCut(0,0,3,2,-1,1);
		newChar.registerShortCut(shortcut);
		//add take shortcut
		shortcut = new L2ShortCut(3,0,3,5,-1,1);
		newChar.registerShortCut(shortcut);
		//add sit shortcut
		shortcut = new L2ShortCut(10,0,3,0,-1,1);
		newChar.registerShortCut(shortcut);
		
		ItemTable itemTable = ItemTable.getInstance();
		L2Item[] items = template.getItems();
		for (L2Item element : items) {
			L2ItemInstance item = newChar.getInventory().addItem("Init", element.getItemId(), 1, newChar, null);
			if (item.getItemId()==5588){
			    //add tutbook shortcut
			    shortcut = new L2ShortCut(11,0,1,item.getObjectId(),-1,1);
			    newChar.registerShortCut(shortcut);
			}
			if (item.isEquipable()){
			  if (newChar.getActiveWeaponItem() == null || !(item.getItem().getType2() != L2Item.TYPE2_WEAPON))
			    newChar.getInventory().equipItemAndRecord(item);
			}
		}
		
		L2SkillLearn[] startSkills = SkillTreeTable.getInstance().getAvailableSkills(newChar, newChar.getClassId());
		for (L2SkillLearn element : startSkills) {
			newChar.addSkill(SkillTable.getInstance().getInfo(element.getId(), element.getLevel()), true);
			if (element.getId()==1001 || element.getId()==1177){
			    shortcut = new L2ShortCut(1,0,2,element.getId(),1,1);
			    newChar.registerShortCut(shortcut);
			}
			if (element.getId()==1216){
			    shortcut = new L2ShortCut(10,0,2,element.getId(),1,1);
			    newChar.registerShortCut(shortcut);
			}
			if (_log.isDebugEnabled()) 
				_log.debug("adding starter skill:" + element.getId()+ " / "+ element.getLevel());
		}
		
		L2GameClient.saveCharToDisk(newChar);
		newChar.deleteMe(); // release the world of this character and it's inventory
		
		// send char list
		
		CharSelectInfo cl =	new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.getConnection().sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
        if (_log.isDebugEnabled()) _log.debug("Character init end");
	}
	
	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__0B_CHARACTERCREATE;
	}
}
