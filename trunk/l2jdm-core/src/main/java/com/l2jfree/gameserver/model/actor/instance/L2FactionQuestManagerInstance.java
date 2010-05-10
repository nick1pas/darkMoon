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
package com.l2jfree.gameserver.model.actor.instance;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.instancemanager.FactionManager;
import com.l2jfree.gameserver.model.entity.faction.Faction;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

/**
 * @author evill33t
 */
public class L2FactionQuestManagerInstance extends L2NpcInstance
{
    public L2FactionQuestManagerInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }

	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player)) return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				showMessageWindow(player);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
    
    private void showMessageWindow(L2PcInstance player)
    {
        int factionId = getTemplate().getNpcFaction();
        String factionName = getTemplate().getNpcFactionName();
        String filename = "data/html/npcdefault.htm";
        String replace = null;

        if (factionId != 0)
        {
            filename = "data/html/faction/" + String.valueOf(factionId)  +  "/start.htm";
            replace = getName();
        }
        sendHtmlMessage(player, filename, replace, factionName);
    }
    
    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
        // Standard msg
        int factionId = getTemplate().getNpcFaction();
        Faction faction = FactionManager.getInstance().getFactions(factionId);
        int factionPrice = faction.getPrice();
        String filename = "data/html/npcdefault.htm";
        String factionName = getTemplate().getNpcFactionName();
        String replace = null;

        if (factionId != 0)
        {
            String path = "data/html/faction" + String.valueOf(factionId) + "/";
            replace = String.valueOf(factionPrice);
            
            if (player.getNPCFaction() != null)
            {
                // Quest stuff here
            }
            else if (command.startsWith("Join"))
                filename = path + "wrong.htm";
        }
        sendHtmlMessage(player, filename, replace, factionName);
    }

    private void sendHtmlMessage(L2PcInstance player, String filename, String replace, String factionName)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%replace%", replace);
        html.replace("%npcname%", getName());
        html.replace("%factionName%", factionName);
        player.sendPacket(html);
    }
}