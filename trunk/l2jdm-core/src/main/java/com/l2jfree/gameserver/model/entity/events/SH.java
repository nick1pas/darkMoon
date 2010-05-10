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
package com.l2jfree.gameserver.model.entity.events;

/**
 * 
 * @author Vital
 * 
 */

import java.util.concurrent.CopyOnWriteArrayList;

import javolution.text.TextBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.datatables.SpawnTable;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.restriction.global.SHRestriction;
import com.l2jfree.gameserver.network.SystemChatChannelId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.CreatureSay;
import com.l2jfree.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jfree.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;
import com.l2jfree.gameserver.templates.item.L2Item;

public class SH
{
	static
	{
		SHRestriction.getInstance().activate(); // TODO: must be checked
	}
	
	private final static Log		_log			= LogFactory.getLog(SH.class);
	private static String			_eventTitle		= new String();
	private static String			_eventDesc		= new String();
	private static String			_location		= new String();
	private static String			_announceName	= new String();
	private static boolean			_started		= false;
	private static L2Spawn			_npcSpawn;
	private static int				_npcId			= 0;
	private static int				_npcX			= 0;
	private static int				_npcY			= 0;
	private static int				_npcZ			= 0;
	private static int				_npcHeading		= 0;

	private static CopyOnWriteArrayList<Items>	_items			= new CopyOnWriteArrayList<Items>();
	private static CopyOnWriteArrayList<Items>	_prizes			= new CopyOnWriteArrayList<Items>();
	private static CopyOnWriteArrayList<String>	_winners		= new CopyOnWriteArrayList<String>();

	public static class Items
	{
		private final L2Item	_item;
		private final int		_count;

		public Items(L2Item item, int count)
		{
			_item = item;
			_count = count;
		}

		public L2Item getItem()
		{
			return _item;
		}

		public int getCount()
		{
			return _count;
		}
	}

	public static void announceToAll(String announce)
	{
		if (getAnnounceName().equals(""))
			setAnnounceName("Scavenger Hunt");

		CreatureSay cs = new CreatureSay(0, SystemChatChannelId.Chat_Hero, getAnnounceName(), announce);
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			player.sendPacket(cs);
	}

	public static void autoEnd(int minuts)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
			public void run()
			{
				endEvent();
			}
		}, 1000 * 60 * minuts);
	}

	public static void startEvent(L2PcInstance activeChar)
	{
		if (!canStart())
		{
			if (activeChar != null)
				activeChar.sendMessage("Event not set up propertly.");
			_log.warn("Event not setted propertly.");
			return;
		}

		if (_started)
			return;

		spawnEventNpc(activeChar != null ? activeChar : null);

		announceToAll(_eventTitle);
		announceToAll("Look for event NPC at " + _location + " for details!");

		_started = true;
	}

	public static boolean canStart()
	{
		if (_eventTitle.equals("") || _location.equals("") || _npcId == 0 || _npcX == 0 || _npcY == 0 || _npcZ == 0 || _items.size() == 0 || _prizes.size() == 0)
			return false;
		return true;
	}

	public static void checkPlayer(L2PcInstance activeChar)
	{
		int itemCount = 0;

		if (getWinners().contains(activeChar.getName()))
		{
			activeChar.sendMessage("You have already won in this event, cannot win again!");
			return;
		}
		else if (getWinners().size() >= getPrizes().size())
		{
			activeChar.sendMessage("Sorry, there has already been " + getWinners().size() + " winners.");
			return;
		}

		for (Items item : getItems())
		{
			if (item.getCount() <= activeChar.getInventory().getInventoryItemCount(item.getItem().getItemId(), -1))
				itemCount++;
		}

		if (itemCount == getItems().size())
			processWinner(activeChar);
		else
			activeChar.sendMessage("You do not have all the items requested!");
	}

	public static void processWinner(L2PcInstance activeChar)
	{
		// takeItems(activeChar); still thinking about it
		Items item = getPrizes().get(getWinners().size());
		activeChar.addItem("Scavenger Hunt", item.getItem().getItemId(), item.getCount(), activeChar, true, true);
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 5103, 1, 1196, 0));
		getWinners().add(activeChar.getName());
		announceToAll(activeChar.getName() + " got " + suffixTool(getWinners().size()) + " place!");

		if (getWinners().size() >= getPrizes().size())
			endEvent();
	}

	public static void endEvent()
	{
		unspawnEventNpc();
		announceToAll("The event is now over!");
		getWinners().clear();
		_started = false;
	}

	private static void spawnEventNpc(L2PcInstance activeChar)
	{
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(_npcId);

		try
		{
			_npcSpawn = new L2Spawn(tmpl);

			_npcSpawn.setLocx(_npcX);
			_npcSpawn.setLocy(_npcY);
			_npcSpawn.setLocz(_npcZ);
			_npcSpawn.setAmount(1);
			_npcSpawn.setHeading(_npcHeading);
			_npcSpawn.setRespawnDelay(1);

			_npcSpawn.init();
			_npcSpawn.getLastSpawn().getStatus().setCurrentHp(999999999);
			_npcSpawn.getLastSpawn().setTitle(_eventTitle);

			_npcSpawn.getLastSpawn()._isEventMobSH = true;
			_npcSpawn.getLastSpawn().isAggressive();
			_npcSpawn.getLastSpawn().decayMe();
			SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
			_npcSpawn.getLastSpawn().spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());

			_npcSpawn.getLastSpawn().broadcastPacket(new MagicSkillUse(_npcSpawn.getLastSpawn(), _npcSpawn.getLastSpawn(), Config.TVTI_JOIN_NPC_SKILL, 1, 1, 1));
		}
		catch (Exception e)
		{
			if (activeChar == null)
				_log.error("SH Engine[spawnEventNpc(exception: ", e);
			else
				_log.error("SH Engine[spawnEventNpc(" + activeChar.getName() + ")]: exception: ", e);
		}
	}

	public static void showEventHtml(L2PcInstance eventPlayer, String objectId)
	{
		try
		{
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			TextBuilder replyMSG = new TextBuilder("<html><body>");

			replyMSG.append("<title>Scavanger Hunt Event</title>");
			replyMSG.append("<table width=\"300\"><tr>");
			replyMSG.append("<td>Current event...</td>");
			replyMSG.append("<tr></tr>");
			replyMSG.append("<td>    ... name: <font color=\"00FF00\">" + _eventTitle + "</font></td>");
			replyMSG.append("<tr></tr>");
			replyMSG.append("<td>    ... description: <font color=\"00FF00\">" + _eventDesc + "</font></td>");
			replyMSG.append("<tr></tr>");
			replyMSG.append("<td>    ... items to collect:</td>");
			for (Items i : getItems())
			{
				replyMSG.append("<tr></tr>");
				replyMSG.append("<td><font color=\"FFFFFF\">(" + i.getCount() + ") " + i.getItem().getName() + "</font></td>");
			}
			replyMSG.append("<tr></tr>");
			replyMSG.append("<td><center><button value=\"I have the items!\" action=\"bypass -h npc_" + objectId + "_sh_player_has_items\" width=150 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td>");
			replyMSG.append("<tr></tr>");
			if (getWinners().size() == 0)
				replyMSG.append("<td>There are not current winners</td>");
			else
				replyMSG.append("<td>    ... current winners:</td>");
			for (int i = 0; i < getWinners().size(); i++)
			{
				replyMSG.append("<tr></tr>");
				replyMSG.append("<td><font color=\"FFFFFF\">[" + SH.suffixTool(i + 1) + "] " + getWinners().get(i) + "</font></td>");
			}

			replyMSG.append("<tr></tr>");
			replyMSG.append("<td>    ... prizes:</td>");
			for (int i = 0; i < getPrizes().size(); i++)
			{
				replyMSG.append("<tr></tr>");
				replyMSG.append("<td><font color=\"FFFFFF\">[" + SH.suffixTool(i + 1) + "] (" + SH.getPrizes().get(i).getCount() + ") " + SH.getPrizes().get(i).getItem().getName() + "</font></td>");
			}
			replyMSG.append("</tr></table>");

			replyMSG.append("</body></html>");
			adminReply.setHtml(replyMSG.toString());
			eventPlayer.sendPacket(adminReply);

			// Send a Server->Client ActionFailed to the L2PcInstance in order
			// to avoid that the client wait another packet
			eventPlayer.sendPacket(ActionFailed.STATIC_PACKET);
		}
		catch (Exception e)
		{
			_log.warn("SH Engine[showEventHtlm(" + eventPlayer.getName() + ", " + objectId + ")]: exception", e);
		}
	}

	public static String suffixTool(int value)
	{
		String temp = String.valueOf(value);
		temp = temp.substring(temp.length() - 1);

		switch (Integer.parseInt(temp))
		{
			case 1:
				return value + "st";
			case 2:
				return value + "nd";
			case 3:
				return value + "rd";
			default:
				return value + "th";
		}
	}

	public static void addNewItem(int itemId, int count)
	{
		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		addItem(new Items(item, count));
	}

	public static void remItem(int idx)
	{
		getItems().remove(idx);
	}

	public static void editItem(int idx, int itemId, int count)
	{
		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		getItems().set(idx, new Items(item, count));
	}

	public static void addNewPrize(int itemId, int count)
	{
		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		addPrize(new Items(item, count));
	}

	public static void remPrize(int idx)
	{
		getPrizes().remove(idx);
	}

	public static void editPrize(int idx, int itemId, int count)
	{
		L2Item item = ItemTable.getInstance().getTemplate(itemId);
		getPrizes().set(idx, new Items(item, count));
	}

	public static void unspawnEventNpc()
	{
		if (_npcSpawn == null)
			return;

		_npcSpawn.getLastSpawn().deleteMe();
		_npcSpawn.stopRespawn();
	}

	public static void setSpawn(int locX, int locY, int locZ)
	{
		_npcX = locX;
		_npcY = locY;
		_npcZ = locZ;
	}

	public static void setSpawn(L2PcInstance activeChar)
	{
		_npcX = activeChar.getX();
		_npcY = activeChar.getY();
		_npcZ = activeChar.getZ();
	}

	public static void setEventTitle(String title)
	{
		_eventTitle = title;
	}

	public static String getEventTitle()
	{
		return _eventTitle;
	}

	public static void setEventDesc(String desc)
	{
		_eventDesc = desc;
	}

	public static String getEventDesc()
	{
		return _eventDesc;
	}

	public static void setLocation(String Loc)
	{
		_location = Loc;
	}

	public static String getLocation()
	{
		return _location;
	}

	public static void setAnnounceName(String name)
	{
		_announceName = name;
	}

	public static String getAnnounceName()
	{
		return _announceName;
	}

	public static boolean isStarted()
	{
		return _started;
	}

	public static L2Spawn getJoinNpc()
	{
		return _npcSpawn;
	}

	public static void setNpcId(int npcId)
	{
		_npcId = npcId;
	}

	public static int getNpcId()
	{
		return _npcId;
	}

	public static int getNpcX()
	{
		return _npcX;
	}

	public static int getNpcY()
	{
		return _npcY;
	}

	public static int getNpcZ()
	{
		return _npcZ;
	}

	public static void addItem(Items item)
	{
		_items.add(item);
	}

	public static CopyOnWriteArrayList<Items> getItems()
	{
		return _items;
	}

	public static void addPrize(Items item)
	{
		_prizes.add(item);
	}

	public static CopyOnWriteArrayList<Items> getPrizes()
	{
		return _prizes;
	}

	public static void addWinner(String winner)
	{
		_winners.add(winner);
	}

	public static CopyOnWriteArrayList<String> getWinners()
	{
		return _winners;
	}
}