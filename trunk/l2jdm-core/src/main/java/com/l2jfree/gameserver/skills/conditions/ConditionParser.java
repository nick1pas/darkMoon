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
package com.l2jfree.gameserver.skills.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jfree.gameserver.model.base.PlayerState;
import com.l2jfree.gameserver.model.base.Race;
import com.l2jfree.gameserver.skills.conditions.ConditionGameTime.CheckGameTime;
import com.l2jfree.gameserver.templates.item.L2ArmorType;
import com.l2jfree.gameserver.templates.item.L2WeaponType;
import com.l2jfree.gameserver.util.Util;

/**
 * @author NB4L1
 */
public abstract class ConditionParser
{
	private static final ConditionParser _instance = new ConditionParser() {
		@Override
		protected String getNodeValue(String nodeValue, Object template)
		{
			return nodeValue;
		}
	};
	
	public static ConditionParser getDefaultInstance()
	{
		return _instance;
	}
	
	protected abstract String getNodeValue(String nodeValue, Object template);
	
	public final Condition parseConditionWithMessage(Node n, Object template)
	{
		Condition cond = parseExistingCondition(n.getFirstChild(), template);
		
		Node msg = n.getAttributes().getNamedItem("msg");
		if (msg != null)
			cond.setMessage(msg.getNodeValue());
		
		Node msgId = n.getAttributes().getNamedItem("msgId");
		if (msgId != null)
			cond.setMessageId(Integer.decode(msgId.getNodeValue()));
		
		return cond;
	}
	
	public final Condition parseExistingCondition(Node n, Object template)
	{
		return parseCondition(n, template, true, false);
	}
	
	public final Condition parseConditionIfExists(Node n, Object template)
	{
		return parseCondition(n, template, false, false);
	}
	
	private Condition parseCondition(Node n, Object template, boolean force, boolean onlyFirst)
	{
		Condition cond = null;
		for (; n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				if (cond != null)
					throw new IllegalStateException("Full condition");
				
				else if ("and".equalsIgnoreCase(n.getNodeName()))
					cond = parseLogicAnd(n, template);
				
				else if ("or".equalsIgnoreCase(n.getNodeName()))
					cond = parseLogicOr(n, template);
				
				else if ("not".equalsIgnoreCase(n.getNodeName()))
					cond = parseLogicNot(n, template);
				
				else if ("player".equalsIgnoreCase(n.getNodeName()))
					cond = parsePlayerCondition(n, template);
				
				else if ("target".equalsIgnoreCase(n.getNodeName()))
					cond = parseTargetCondition(n, template);
				
				else if ("using".equalsIgnoreCase(n.getNodeName()))
					cond = parseUsingCondition(n, template);
				
				else if ("game".equalsIgnoreCase(n.getNodeName()))
					cond = parseGameCondition(n, template);
				
				else
					throw new IllegalStateException("Unrecognized condition <" + n.getNodeName() + ">");
				
				if (onlyFirst)
					return cond;
			}
		}
		
		if (force && cond == null)
			throw new IndexOutOfBoundsException("Empty condition");
		
		return cond;
	}
	
	private Condition parseExistingConditionInsideLogic(Node n, Object template)
	{
		return parseCondition(n, template, true, true);
	}
	
	private Condition parseLogicAnd(Node n, Object template)
	{
		ConditionLogicAnd cond = new ConditionLogicAnd();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
				cond.add(parseExistingConditionInsideLogic(n, template));
		}
		
		return cond.getCanonicalCondition();
	}
	
	private Condition parseLogicOr(Node n, Object template)
	{
		ConditionLogicOr cond = new ConditionLogicOr();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
				cond.add(parseExistingConditionInsideLogic(n, template));
		}
		
		return cond.getCanonicalCondition();
	}
	
	private Condition parseLogicNot(Node n, Object template)
	{
		Condition cond = null;
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				if (cond != null)
					throw new IllegalStateException("Full <not> condition");
				
				cond = parseExistingConditionInsideLogic(n, template);
			}
		}
		
		if (cond == null)
			throw new IllegalStateException("Empty <not> condition");
		
		return new ConditionLogicNot(cond);
	}
	
	private Condition parsePlayerCondition(Node n, Object template)
	{
		Condition cond = null;
		
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			
			Condition condOr = null;
			
			for (String nodeValue : StringUtils.split(getNodeValue(a.getNodeValue(), template), "|"))
				condOr = joinOr(condOr, parsePlayerCondition(a.getNodeName(), nodeValue));
			
			cond = joinAnd(cond, condOr);
		}
		
		if (cond == null)
			throw new IllegalStateException("Empty <player> condition");
		
		return cond;
	}
	
	private Condition parsePlayerCondition(String nodeName, String nodeValue)
	{
		if ("skill".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionWithSkill(val);
		}
		else if ("race".equalsIgnoreCase(nodeName))
		{
			Race race = Race.valueOf(nodeValue);
			return new ConditionPlayerRace(race);
		}
		else if ("level".equalsIgnoreCase(nodeName))
		{
			int lvl = Integer.decode(nodeValue);
			return new ConditionPlayerLevel(lvl);
		}
		else if ("resting".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.RESTING, val);
		}
		else if ("moving".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.MOVING, val);
		}
		else if ("running".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.RUNNING, val);
		}
		else if ("walking".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.WALKING, val);
		}
		else if ("behind".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.BEHIND, val);
		}
		else if ("front".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.FRONT, val);
		}
		else if ("chaotic".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.CHAOTIC, val);
		}
		else if ("olympiad".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.OLYMPIAD, val);
		}
		else if ("flying".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerState(PlayerState.FLYING, val);
		}
		else if ("hp".equalsIgnoreCase(nodeName))
		{
			int hp = Integer.decode(nodeValue);
			return new ConditionPlayerHp(hp);
		}
		else if ("mp".equalsIgnoreCase(nodeName))
		{
			int mp = Integer.decode(nodeValue);
			return new ConditionPlayerMp(mp);
		}
		else if ("cp".equalsIgnoreCase(nodeName))
		{
			int cp = Integer.decode(nodeValue);
			return new ConditionPlayerCp(cp);
		}
		else if ("attack_stance".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerAttackStance(val);
		}
		else if ("grade".equalsIgnoreCase(nodeName))
		{
			int expIndex = Integer.decode(nodeValue);
			return new ConditionPlayerGrade(expIndex);
		}
		else if ("siegezone".equalsIgnoreCase(nodeName))
		{
			int value = Integer.decode(nodeValue);
			return new ConditionSiegeZone(value, true);
		}
		else if ("battle_force".equalsIgnoreCase(nodeName))
		{
			byte battleForce = Byte.decode(nodeValue);
			return new ConditionForceBuff(battleForce, (byte)0);
		}
		else if ("spell_force".equalsIgnoreCase(nodeName))
		{
			byte spellForce = Byte.decode(nodeValue);
			return new ConditionForceBuff((byte)0, spellForce);
		}
		else if ("weight".equalsIgnoreCase(nodeName))
		{
			int weight = Integer.decode(nodeValue);
			return new ConditionPlayerWeight(weight);
		}
		else if ("invSize".equalsIgnoreCase(nodeName))
		{
			int size = Integer.decode(nodeValue);
			return new ConditionPlayerInvSize(size);
		}
		else if ("pledgeClass".equalsIgnoreCase(nodeName))
		{
			int pledgeClass = Integer.decode(nodeValue);
			return new ConditionPlayerPledgeClass(pledgeClass);
		}
		else if ("clanHall".equalsIgnoreCase(nodeName))
		{
			List<Integer> array = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				array.add(Integer.decode(st.nextToken().trim()));
			}
			return new ConditionPlayerHasClanHall(array);
		}
		else if ("fort".equalsIgnoreCase(nodeName))
		{
			int fort = Integer.decode(nodeValue);
			return new ConditionPlayerHasFort(fort);
		}
		else if ("castle".equalsIgnoreCase(nodeName))
		{
			int castle = Integer.decode(nodeValue);
			return new ConditionPlayerHasCastle(castle);
		}
		else if ("sex".equalsIgnoreCase(nodeName))
		{
			int sex = Integer.decode(nodeValue);
			return new ConditionPlayerSex(sex);
		}
		else if ("flyMounted".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.valueOf(nodeValue);
			return new ConditionPlayerFlyMounted(val);
		}
		else if ("landingZone".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.valueOf(nodeValue);
			return new ConditionPlayerLandingZone(val);
		}
		else if ("active_skill_id".equalsIgnoreCase(nodeName) || "active_skill_id_lvl".equalsIgnoreCase(nodeName))
		{
			return new ConditionPlayerActiveSkillId(nodeValue);
		}
		else if ("agathionId".equalsIgnoreCase(nodeName))
		{
			int agathionId = Integer.decode(nodeValue);
			return new ConditionAgathionSummoned(agathionId);
		}
		else if ("active_effect_id".equalsIgnoreCase(nodeName))
		{
			return new ConditionPlayerActiveEffectId(nodeValue);
		}
		else if ("class_id_restriction".equalsIgnoreCase(nodeName))
		{
			List<Integer> array = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				array.add(Integer.decode(st.nextToken().trim()));
			}
			return new ConditionPlayerClassIdRestriction(array);
		}
		else if ("isClanLeader".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionPlayerIsClanLeader(val);
		}
		else if ("subclass".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.valueOf(nodeValue);
			return new ConditionPlayerSubclass(val);
		}
		else if ("instanceid".equalsIgnoreCase(nodeName))
		{
			List<Integer> array = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				array.add(Integer.decode(st.nextToken().trim()));
			}
			return new ConditionPlayerInstanceId(array);
		}
		throw new IllegalStateException("Invalid attribute at <player>: " + nodeName + "='" + nodeValue + "'");
	}
	
	private Condition parseTargetCondition(Node n, Object template)
	{
		Condition cond = null;
		
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			
			Condition condOr = null;
			
			for (String nodeValue : StringUtils.split(getNodeValue(a.getNodeValue(), template), "|"))
				condOr = joinOr(condOr, parseTargetCondition(a.getNodeName(), nodeValue));
			
			cond = joinAnd(cond, condOr);
		}
		
		if (cond == null)
			throw new IllegalStateException("Empty <target> condition");
		
		return cond;
	}
	
	private Condition parseTargetCondition(String nodeName, String nodeValue)
	{
		if ("aggro".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionTargetAggro(val);
		}
		else if ("siegezone".equalsIgnoreCase(nodeName))
		{
			int value = Integer.decode(nodeValue);
			return new ConditionSiegeZone(value, false);
		}
		else if ("level".equalsIgnoreCase(nodeName))
		{
			int lvl = Integer.decode(nodeValue);
			return new ConditionTargetLevel(lvl);
		}
		else if ("class_id_restriction".equalsIgnoreCase(nodeName))
		{
			List<Integer> array = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				array.add(Integer.decode(st.nextToken().trim()));
			}
			return new ConditionTargetClassIdRestriction(array);
		}
		else if ("active_effect_id".equalsIgnoreCase(nodeName))
		{
			return new ConditionTargetActiveEffectId(nodeValue);
		}
		else if ("active_skill_id".equalsIgnoreCase(nodeName))
		{
			return new ConditionTargetActiveSkillId(nodeValue);
		}
		else if ("mindistance".equalsIgnoreCase(nodeName))
		{
			int distance = Integer.decode(nodeValue);
			return new ConditionMinDistance(distance * distance);
		}
		// used for npc race
		else if ("race_id".equalsIgnoreCase(nodeName))
		{
			ArrayList<Integer> array = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				//-1 because we want to take effect for exactly race that is by -1 lower in FastList
				array.add(Integer.decode(st.nextToken().trim()) - 1);
			}
			return new ConditionTargetRaceId(array);
		}
		// used for player race
		else if ("race".equalsIgnoreCase(nodeName))
		{
			Race race = Race.valueOf(nodeValue);
			return new ConditionTargetRace(race);
		}
		else if ("undead".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionTargetUndead(val);
		}
		else if ("using".equalsIgnoreCase(nodeName))
		{
			int mask = 0;
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				String item = st.nextToken().trim();
				for (L2WeaponType wt : L2WeaponType.values())
				{
					if (wt.toString().equalsIgnoreCase(item))
					{
						mask |= wt.mask();
						break;
					}
				}
				for (L2ArmorType at : L2ArmorType.values())
				{
					if (at.toString().equalsIgnoreCase(item))
					{
						mask |= at.mask();
						break;
					}
				}
			}
			return new ConditionTargetUsesWeaponKind(mask);
		}
		else if ("npcId".equalsIgnoreCase(nodeName))
		{
			int[] npcIds = Util.toIntArray(nodeValue.trim(), " ");
			return new ConditionTargetNpcId(npcIds);
		}
		else if ("npcType".equalsIgnoreCase(nodeName))
		{
			String[] npcTypes = nodeValue.trim().split(" ");
			return new ConditionTargetNpcType(npcTypes);
		}
		else if ("doorId".equalsIgnoreCase(nodeName))
		{
			int[] doorIds = Util.toIntArray(nodeValue.trim(), " ");
			return new ConditionTargetDoorId(doorIds);
		}
		else if ("abnormal".equalsIgnoreCase(nodeName))
		{
			int abnormalId = Integer.decode(nodeValue);
			return new ConditionTargetAbnormal(abnormalId);
		}
		
		throw new IllegalStateException("Invalid attribute at <target>: " + nodeName + "='" + nodeValue + "'");
	}
	
	private Condition parseUsingCondition(Node n, Object template)
	{
		Condition cond = null;
		
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			
			Condition condOr = null;
			
			for (String nodeValue : StringUtils.split(getNodeValue(a.getNodeValue(), template), "|"))
				condOr = joinOr(condOr, parseUsingCondition(a.getNodeName(), nodeValue));
			
			cond = joinAnd(cond, condOr);
		}
		
		if (cond == null)
			throw new IllegalStateException("Empty <using> condition");
		
		return cond;
	}
	
	private Condition parseUsingCondition(String nodeName, String nodeValue)
	{
		if ("kind".equalsIgnoreCase(nodeName))
		{
			int mask = 0;
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			while (st.hasMoreTokens())
			{
				String item = st.nextToken().trim();
				for (L2WeaponType wt : L2WeaponType.values())
				{
					if (wt.toString().equalsIgnoreCase(item))
					{
						mask |= wt.mask();
						break;
					}
				}
				for (L2ArmorType at : L2ArmorType.values())
				{
					if (at.toString().equalsIgnoreCase(item))
					{
						mask |= at.mask();
						break;
					}
				}
			}
			return new ConditionUsingItemType(mask);
		}
		else if ("skill".equalsIgnoreCase(nodeName))
		{
			StringTokenizer st = new StringTokenizer(nodeValue, ",");
			int skillId = Integer.decode(st.nextToken());
			int minSkillLvl = st.hasMoreTokens() ? Integer.decode(st.nextToken()) : -1;
			int maxSkillLvl = st.hasMoreTokens() ? Integer.decode(st.nextToken()) : minSkillLvl;
			return new ConditionUsingSkill(skillId, minSkillLvl, maxSkillLvl);
		}
		else if ("slotitem".equalsIgnoreCase(nodeName))
		{
			StringTokenizer st = new StringTokenizer(nodeValue, ";");
			int id = Integer.decode(st.nextToken().trim());
			int slot = Integer.decode(st.nextToken().trim());
			int enchant = 0;
			if (st.hasMoreTokens())
				enchant = Integer.decode(st.nextToken().trim());
			return new ConditionSlotItemId(slot, id, enchant);
		}
		else if ("weaponChange".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionChangeWeapon(val);
		}
		
		throw new IllegalStateException("Invalid attribute at <using>: " + nodeName + "='" + nodeValue + "'");
	}
	
	private Condition parseGameCondition(Node n, Object template)
	{
		Condition cond = null;
		
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			
			Condition condOr = null;
			
			for (String nodeValue : StringUtils.split(getNodeValue(a.getNodeValue(), template), "|"))
				condOr = joinOr(condOr, parseGameCondition(a.getNodeName(), nodeValue));
			
			cond = joinAnd(cond, condOr);
		}
		
		if (cond == null)
			throw new IllegalStateException("Empty <game> condition");
		
		return cond;
	}
	
	private Condition parseGameCondition(String nodeName, String nodeValue)
	{
		if ("night".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionGameTime(CheckGameTime.NIGHT, val);
		}
		else if ("chance".equalsIgnoreCase(nodeName))
		{
			int val = Integer.decode(nodeValue);
			return new ConditionGameChance(val);
		}
		else if ("skill".equalsIgnoreCase(nodeName))
		{
			boolean val = Boolean.parseBoolean(nodeValue);
			return new ConditionWithSkill(val);
		}
		
		throw new IllegalStateException("Invalid attribute at <game>: " + nodeName + "='" + nodeValue + "'");
	}
	
	private Condition joinAnd(Condition cond, Condition c)
	{
		if (cond == null)
			return c;
		
		if (cond instanceof ConditionLogicAnd)
		{
			((ConditionLogicAnd)cond).add(c);
			return cond;
		}
		
		ConditionLogicAnd and = new ConditionLogicAnd();
		and.add(cond);
		and.add(c);
		return and;
	}
	
	private Condition joinOr(Condition cond, Condition c)
	{
		if (cond == null)
			return c;
		
		if (cond instanceof ConditionLogicOr)
		{
			((ConditionLogicOr)cond).add(c);
			return cond;
		}
		
		ConditionLogicOr and = new ConditionLogicOr();
		and.add(cond);
		and.add(c);
		return and;
	}
}
