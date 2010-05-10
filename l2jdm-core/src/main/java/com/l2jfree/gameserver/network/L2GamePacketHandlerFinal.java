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
package com.l2jfree.gameserver.network;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.network.L2GameClient.GameClientState;
import com.l2jfree.gameserver.network.clientpackets.*;
import com.l2jfree.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jfree.mmocore.network.IPacketHandler;

/**
 * Stateful Packet Handler<BR>
 * The Stateful approach prevents the server from handling inconsistent packets, examples:<BR>
 * <li>Clients sends a MoveToLocation packet without having a character attached. (Potential errors handling the
 * packet).</li>
 * <li>Clients sends a RequestAuthLogin being already authed. (Potential exploit).</li>
 * <BR>
 * <BR>
 * Note: If for a given exception a packet needs to be handled on more then one state, then it should be added to all
 * these states.
 * 
 * @author KenM
 */
public final class L2GamePacketHandlerFinal implements
	IPacketHandler<L2GameClient, L2GameClientPacket, L2GameServerPacket>
{
	private static final Log _log = LogFactory.getLog(L2GamePacketHandlerFinal.class);
	
	public L2GameClientPacket handlePacket(ByteBuffer buf, L2GameClient client, final int opcode)
	{
		L2GameClientPacket msg = null;
		GameClientState state = client.getState();
		
		switch (state)
		{
			case CONNECTED:
				switch (opcode)
				{
					case 0x0e:
						msg = new ProtocolVersion();
						break;
					case 0x2b:
						msg = new AuthLogin();
						break;
					default:
						printDebug(buf, client, opcode);
						break;
				}
				break;
			case AUTHED:
				switch (opcode)
				{
					case 0x00:
						msg = new Logout();
						break;
					case 0x0c:
						msg = new NewCharacter();
						break;
					case 0x0d:
						msg = new CharacterDelete();
						break;
					case 0x0f:
						// MoveBackwardsToLocation, lag issue
						break;
					case 0x12:
						msg = new CharacterSelected();
						break;
					case 0x13:
						msg = new NewCharacterInit();
						break;
					case 0x57:
						// RequestRestart, lag issue
						break;
					case 0x7b:
						msg = new CharacterRestore();
						break;
					case 0xd0:
						int id2 = -1;
						if (buf.remaining() >= 2)
						{
							id2 = buf.getShort() & 0xffff;
						}
						else
						{
							if (Config.PACKET_HANDLER_DEBUG)
								_log.warn("Client: " + client.toString() + " sent a 0xd0 without the second opcode.");
							break;
						}
						
						switch (id2)
						{
							case 0x24:
								// RequestSaveInventoryOrder, lag issue
								break;
							case 0x36:
								msg = new CharacterPrevState();
								break;
							case 0x39:
								// most probably using L2NET
								break;
							case 0x3d: // client definitely sends it right now, enable if supposed to be
								//msg = new RequestAllFortressInfo();
								break;
							case 0x5a:
								int id3 = 0;
								if (buf.remaining() >= 4)
								{
									id3 = buf.getInt() & 0xffffffff;
								}
								else
								{
									if (Config.PACKET_HANDLER_DEBUG)
										_log.warn("Client: " + client + " sent a 0xd0:0x5a without the third opcode.");
									break;
								}
								
								switch (id3)
								{
									case 0x00:
										msg = new RequestExCubeGameChangeTeam();
										break;
									default:
										printDebug(buf, client, opcode, id2, id3);
										break;
								}
								break;
							default:
								printDebug(buf, client, opcode, id2);
						}
						break;
					// to avoid unnecessary warning about invalid opcode (if the client lags a bit, then it starts spamming this packet)
					case 0x59: // ValidatePosition
						break;
					//
					default:
						printDebug(buf, client, opcode);
						break;
				}
				break;
			case IN_GAME:
				switch (opcode)
				{
					// to avoid unnecessary warning about invalid opcode (player clicked the button multiple times)
					case 0x12: // CharacterSelected
						break;
					//
					case 0x00:
						msg = new Logout();
						break;
					case 0x01:
						msg = new AttackRequest();
						break;
					case 0x03:
						msg = new RequestStartPledgeWar();
						break;
					case 0x04:
						msg = new RequestReplyStartPledgeWar();
						break;
					case 0x05:
						msg = new RequestStopPledgeWar();
						break;
					case 0x06: // RequestSCCheck
						msg = new RequestReplyStopPledgeWar();
						break;
					case 0x07:
						msg = new RequestSurrenderPledgeWar();
						break;
					case 0x08:
						msg = new RequestReplySurrenderPledgeWar();
						break;
					case 0x09:
						msg = new RequestSetPledgeCrest();
						break;
					case 0x0b:
						msg = new RequestGiveNickName();
						break;
					case 0x0f:
						msg = new MoveBackwardToLocation();
						break;
					case 0x10:
						// Say
						break;
					case 0x11:
						msg = new EnterWorld();
						break;
					case 0x14:
						msg = new RequestItemList();
						break;
					case 0x15:
						// RequestEquipItem
						break;
					case 0x16:
						msg = new RequestUnEquipItem();
						break;
					case 0x17:
						msg = new RequestDropItem();
						break;
					case 0x19:
						msg = new UseItem();
						break;
					case 0x1a:
						msg = new TradeRequest();
						break;
					case 0x1b:
						msg = new AddTradeItem();
						break;
					case 0x1c:
						msg = new TradeDone();
						break;
					case 0x1f:
						msg = new Action();
						break;
					case 0x22:
						msg = new RequestLinkHtml();
						break;
					case 0x23:
						msg = new RequestBypassToServer();
						break;
					case 0x24:
						msg = new RequestBBSwrite();
						break;
					case 0x25:
						// RequestCreatePledge
						break;
					case 0x26:
						msg = new RequestJoinPledge();
						break;
					case 0x27:
						msg = new RequestAnswerJoinPledge();
						break;
					case 0x28:
						msg = new RequestWithdrawalPledge();
						break;
					case 0x29:
						msg = new RequestOustPledgeMember();
						break;
					case 0x2c:
						msg = new RequestGetItemFromPet();
						break;
					case 0x2e:
						msg = new RequestAllyInfo();
						break;
					case 0x2f:
						msg = new RequestCrystallizeItem();
						break;
					case 0x30:
						msg = new RequestPrivateStoreManageSell();
						break;
					case 0x31:
						msg = new SetPrivateStoreListSell();
						break;
					case 0x32:
						msg = new AttackRequest();
						break;
					case 0x33:
						// RequestTeleportPacket
						break;
					case 0x34:
						msg = new RequestSocialAction();
						break;
					case 0x35:
						msg = new ChangeMoveType();
						break;
					case 0x36:
						msg = new ChangeWaitType();
						break;
					case 0x37:
						msg = new RequestSellItem();
						break;
					case 0x38:
						// RequestMagicSkillList
						break;
					case 0x39:
						msg = new RequestMagicSkillUse();
						break;
					case 0x3a:
						msg = new SendAppearing();
						break;
					case 0x3b:
						if (Config.ALLOW_WAREHOUSE)
							msg = new SendWareHouseDepositList();
						break;
					case 0x3c:
						msg = new SendWareHouseWithDrawList();
						break;
					case 0x3d:
						msg = new RequestShortCutReg();
						break;
					case 0x3f:
						msg = new RequestShortCutDel();
						break;
					case 0x40:
						msg = new RequestBuyItem();
						break;
					case 0x41:
						// RequestDismissPledge
						break;
					case 0x42:
						msg = new RequestJoinParty();
						break;
					case 0x43:
						msg = new RequestAnswerJoinParty();
						break;
					case 0x44:
						msg = new RequestWithDrawalParty();
						break;
					case 0x45:
						msg = new RequestOustPartyMember();
						break;
					case 0x46:
						// RequestDismissParty
						break;
					case 0x47:
						msg = new CannotMoveAnymore();
						break;
					case 0x48:
						msg = new RequestTargetCanceld();
						break;
					case 0x49:
						msg = new Say2();
						break;
					case 0x4a:
						int id_2 = -1;
						if (buf.remaining() >= 2)
						{
							id_2 = buf.get() & 0xff;
						}
						else
						{
							if (Config.PACKET_HANDLER_DEBUG)
								_log.warn("Client: " + client.toString() + " sent a 0x4a without the second opcode.");
							break;
						}
						
						switch (id_2)
						{
							case 0x00:
								msg = new SuperCmdCharacterInfo();
								break;
							case 0x01:
								msg = new SuperCmdSummonCmd();
								break;
							case 0x02:
								msg = new SuperCmdServerStatus();
								break;
							case 0x03:
								msg = new SendL2ParamSetting();
								break;
							default:
								printDebug(buf, client, opcode, id_2);
								break;
						}
						break;
					case 0x4d:
						msg = new RequestPledgeMemberList();
						break;
					case 0x4f:
						//RequestMagicList
						break;
					case 0x50:
						msg = new RequestSkillList();
						break;
					case 0x52:
						msg = new MoveWithDelta();
						break;
					case 0x53:
						msg = new RequestGetOnVehicle();
						break;
					case 0x54:
						msg = new RequestGetOffVehicle();
						break;
					case 0x55:
						msg = new AnswerTradeRequest();
						break;
					case 0x56:
						msg = new RequestActionUse();
						break;
					case 0x57:
						msg = new RequestRestart();
						break;
					case 0x58:
						msg = new RequestSiegeInfo();
						break;
					case 0x59:
						msg = new ValidatePosition();
						break;
					case 0x5a:
						// RequestSEKCustom
						break;
					case 0x5b:
						// StartRotating
						break;
					case 0x5c:
						// FinishRotating
						break;
					case 0x5e:
						msg = new RequestShowBoard();
						break;
					case 0x5f:
						msg = new RequestEnchantItem();
						break;
					case 0x60:
						msg = new RequestDestroyItem();
						break;
					case 0x62:
						msg = new RequestQuestList();
						break;
					case 0x63: // RequestDestroyQuest
						msg = new RequestQuestAbort();
						break;
					case 0x65:
						msg = new RequestPledgeInfo();
						break;
					case 0x66:
						msg = new RequestPledgeExtendedInfo();
						break;
					case 0x67:
						msg = new RequestPledgeCrest();
						break;
					case 0x6b:
						msg = new RequestSendFriendMsg();
						break;
					case 0x6c:
						msg = new RequestShowMiniMap();
						break;
					case 0x6d:
						// RequestSendMsnChatLog
						break;
					case 0x6e: //RequestReload
						msg = new RequestRecordInfo();
						break;
					case 0x6f:
						msg = new RequestHennaEquip();
						break;
					case 0x70:
						msg = new RequestHennaRemoveList();
						break;
					case 0x71:
						msg = new RequestHennaItemRemoveInfo();
						break;
					case 0x72:
						msg = new RequestHennaRemove();
						break;
					case 0x73:
						msg = new RequestAquireSkillInfo();
						break;
					case 0x74:
						msg = new SendBypassBuildCmd();
						break;
					case 0x75:
						msg = new RequestMoveToLocationInVehicle();
						break;
					case 0x76:
						msg = new CannotMoveAnymoreInVehicle();
						break;
					case 0x77:
						msg = new RequestFriendInvite();
						break;
					case 0x78:
						msg = new RequestAnswerFriendInvite();
						break;
					case 0x79:
						msg = new RequestFriendList();
						break;
					case 0x7a:
						msg = new RequestFriendDel();
						break;
					case 0x7c: // send when a skill to be learned is selected
						msg = new RequestAquireSkill();
						break;
					case 0x7d:
						msg = new RequestRestartPoint();
						break;
					case 0x7e:
						msg = new RequestGMCommand();
						break;
					case 0x7f:
						msg = new RequestPartyMatchConfig();
						break;
					case 0x80:
						msg = new RequestPartyMatchList();
						break;
					case 0x81:
						msg = new RequestPartyMatchDetail();
						break;
					case 0x83:
						msg = new RequestPrivateStoreBuy();
						break;
					case 0x85:
						msg = new RequestTutorialLinkHtml();
						break;
					case 0x86:
						msg = new RequestTutorialPassCmdToServer();
						break;
					case 0x87:
						msg = new RequestTutorialQuestionMark();
						break;
					case 0x88:
						msg = new RequestTutorialClientEvent();
						break;
					case 0x89:
						msg = new RequestPetition();
						break;
					case 0x8a:
						msg = new RequestPetitionCancel();
						break;
					case 0x8b:
						msg = new RequestGmList();
						break;
					case 0x8c:
						msg = new RequestJoinAlly();
						break;
					case 0x8d:
						msg = new RequestAnswerJoinAlly();
						break;
					case 0x8e:
						msg = new RequestWithdrawAlly();
						break;
					case 0x8f:
						msg = new RequestOustAlly();
						break;
					case 0x90:
						msg = new RequestDismissAlly();
						break;
					case 0x91:
						msg = new RequestSetAllyCrest();
						break;
					case 0x92:
						msg = new RequestAllyCrest();
						break;
					case 0x93:
						msg = new RequestChangePetName();
						break;
					case 0x94:
						msg = new RequestPetUseItem();
						break;
					case 0x95:
						msg = new RequestGiveItemToPet();
						break;
					case 0x96:
						msg = new RequestPrivateStoreQuitSell();
						break;
					case 0x97:
						msg = new SetPrivateStoreMsgSell();
						break;
					case 0x98:
						msg = new RequestPetGetItem();
						break;
					case 0x99:
						msg = new RequestPrivateStoreManageBuy();
						break;
					case 0x9a:
						msg = new SetPrivateStoreListBuy();
						break;
					case 0x9c:
						msg = new RequestPrivateStoreQuitBuy();
						break;
					case 0x9d:
						msg = new SetPrivateStoreMsgBuy();
						break;
					case 0x9f: // SendPrivateStoreBuyList
						msg = new RequestPrivateStoreSell();
						break;
					case 0xa0:
						//SendTimeCheckPacket
						break;
					case 0xa6:
						// RequestSkillCoolTime
						break;
					case 0xa7:
						msg = new RequestPackageSendableItemList();
						break;
					case 0xa8:
						msg = new RequestPackageSend();
						break;
					case 0xa9:
						msg = new RequestBlock();
						break;
					case 0xaa:
						msg = new RequestSiegeInfo();
						break;
					case 0xab:
						msg = new RequestSiegeAttackerList();
						break;
					case 0xac:
						msg = new RequestSiegeDefenderList();
						break;
					case 0xad:
						msg = new RequestJoinSiege();
						break;
					case 0xae:
						msg = new RequestConfirmSiegeWaitingList();
						break;
					case 0xaf:
						// RequestSetCastleSiegeTime
						break;
					case 0xb0:
						msg = new MultiSellChoose();
						break;
					case 0xb1:
						// NetPing
						break;
					case 0xb2:
						msg = new RequestRemainTime();
						break;
					case 0xb3:
						msg = new BypassUserCmd();
						break;
					case 0xb4:
						msg = new SnoopQuit();
						break;
					case 0xb5:
						msg = new RequestRecipeBookOpen();
						break;
					case 0xb6: // RequestRecipeItemDelete
						msg = new RequestRecipeBookDestroy();
						break;
					case 0xb7:
						msg = new RequestRecipeItemMakeInfo();
						break;
					case 0xb8:
						msg = new RequestRecipeItemMakeSelf();
						break;
					case 0xb9:
						// RequestRecipeShopManageList
						break;
					case 0xba:
						msg = new RequestRecipeShopMessageSet();
						break;
					case 0xbb:
						msg = new RequestRecipeShopListSet();
						break;
					case 0xbc:
						msg = new RequestRecipeShopManageQuit();
						break;
					case 0xbd:
						// RequestRecipeShopManageCancel
						break;
					case 0xbe:
						msg = new RequestRecipeShopMakeInfo();
						break;
					case 0xbf:
						msg = new RequestRecipeShopMakeItem();
						break;
					case 0xc0:
						msg = new RequestRecipeShopSellList();
						break;
					case 0xc1:
						msg = new RequestObserverEnd();
						break;
					case 0xc2: // VoteSociality
						msg = new VoteSociality();
						break;
					case 0xc3: // RequestHennaItemList
						msg = new RequestHennaDrawList();
						break;
					case 0xc4:
						msg = new RequestHennaItemDrawInfo();
						break;
					case 0xc5:
						msg = new RequestBuySeed();
						break;
					case 0xc6:
						msg = new ConfirmDlgAnswer();
						break;
					case 0xc7:
						msg = new RequestWearItem();
						break;
					case 0xc8:
						msg = new RequestSSQStatus();
						break;
					case 0xc9:
						// PetitionVote
						break;
					case 0xcb:
						msg = new GameGuardReply();
						break;
					case 0xcc:
						// Clan Privileges
						msg = new RequestPledgePower();
						break;
					case 0xcd:
						msg = new RequestMakeMacro();
						break;
					case 0xce:
						msg = new RequestDeleteMacro();
						break;
					// Manor
					case 0xcf: // RequestProcureCrop
						msg = new RequestBuyProcure();
						break;
					case 0xd0:
						int id2 = -1;
						if (buf.remaining() >= 2)
						{
							id2 = buf.getShort() & 0xffff;
						}
						else
						{
							if (Config.PACKET_HANDLER_DEBUG)
								_log.warn("Client: " + client.toString() + " sent a 0xd0 without the second opcode.");
							break;
						}
						
						switch (id2)
						{
							case 0x01:
								msg = new RequestManorList();
								break;
							case 0x02:
								msg = new RequestProcureCropList();
								break;
							case 0x03:
								msg = new RequestSetSeed();
								break;
							case 0x04:
								msg = new RequestSetCrop();
								break;
							case 0x05:
								msg = new RequestWriteHeroWords();
								break;
							case 0x06:
								msg = new RequestExAskJoinMPCC();
								break;
							case 0x07:
								msg = new RequestExAcceptJoinMPCC();
								break;
							case 0x08:
								msg = new RequestExOustFromMPCC();
								break;
							case 0x09:
								msg = new RequestOustFromPartyRoom();
								break;
							case 0x0a:
								msg = new RequestDismissPartyRoom();
								break;
							case 0x0b:
								msg = new RequestWithdrawPartyRoom();
								break;
							case 0x0c:
								msg = new RequestChangePartyLeader();
								break;
							case 0x0d:
								msg = new RequestAutoSoulShot();
								break;
							case 0x0e:
								msg = new RequestExEnchantSkillInfo();
								break;
							case 0x0f:
								msg = new RequestExEnchantSkill();
								break;
							case 0x10:
								msg = new RequestExPledgeCrestLarge();
								break;
							case 0x11:
								msg = new RequestExSetPledgeCrestLarge();
								break;
							case 0x12:
								msg = new RequestPledgeSetAcademyMaster();
								break;
							case 0x13:
								msg = new RequestPledgePowerGradeList();
								break;
							case 0x14:
								msg = new RequestPledgeMemberPowerInfo();
								break;
							case 0x15:
								msg = new RequestPledgeSetMemberPowerGrade();
								break;
							case 0x16:
								msg = new RequestPledgeMemberInfo();
								break;
							case 0x17:
								msg = new RequestPledgeWarList();
								break;
							case 0x18:
								msg = new RequestExFishRanking();
								break;
							case 0x19:
								msg = new RequestPCCafeCouponUse();
								break;
							case 0x1b:
								msg = new RequestDuelStart();
								break;
							case 0x1c:
								msg = new RequestDuelAnswerStart();
								break;
							case 0x1d:
								// RequestExSetTutorial
								break;
							case 0x1e:
								msg = new RequestExRqItemLink();
								break;
							case 0x1f:
								// CanNotMoveAnymoreAirShip
								break;
							case 0x20:
								msg = new MoveToLocationInAirShip();
								break;
							case 0x21:
								msg = new RequestKeyMapping();
								break;
							case 0x22:
								// RequestSaveKeyMapping
								break;
							case 0x23:
								msg = new RequestExRemoveItemAttribute();
								break;
							case 0x24:
								msg = new RequestSaveInventoryOrder();
								break;
							case 0x25:
								msg = new RequestExitPartyMatchingWaitingRoom();
								break;
							case 0x26:
								msg = new RequestConfirmTargetItem();
								break;
							case 0x27:
								msg = new RequestConfirmRefinerItem();
								break;
							case 0x28:
								msg = new RequestConfirmGemStone();
								break;
							case 0x29:
								msg = new RequestOlympiadObserverEnd();
								break;
							case 0x2a:
								msg = new RequestCursedWeaponList();
								break;
							case 0x2b:
								msg = new RequestCursedWeaponLocation();
								break;
							case 0x2c:
								msg = new RequestPledgeReorganizeMember();
								break;
							case 0x2d:
								msg = new RequestExMPCCShowPartyMembersInfo();
								break;
							case 0x2e:
								msg = new RequestOlympiadMatchList();
								break;
							case 0x2f:
								msg = new RequestAskJoinPartyRoom();
								break;
							case 0x30:
								msg = new AnswerJoinPartyRoom();
								break;
							case 0x31:
								msg = new RequestListPartyMatchingWaitingRoom();
								break;
							case 0x32:
								msg = new RequestExEnchantSkillSafe();
								break;
							case 0x33:
								msg = new RequestExEnchantSkillUntrain();
								break;
							case 0x34:
								msg = new RequestExEnchantSkillRouteChange();
								break;
							case 0x35:
								msg = new RequestExEnchantItemAttribute();
								break;
							case 0x36:
								msg = new ExGetOnAirShip();
								break;
							case 0x38:
								// MoveToLocationAirShip
								break;
							case 0x39:
								msg = new RequestBidItemAuction();
								break;
							case 0x3a:
								msg = new RequestInfoItemAuction();
								break;
							case 0x3b:
								msg = new RequestExChangeName();
								break;
							case 0x3c:
								msg = new RequestAllCastleInfo();
								break;
							case 0x3d:
								msg = new RequestAllFortressInfo();
								break;
							case 0x3e:
								msg = new RequestAllAgitInfo();
								break;
							case 0x3f:
								msg = new RequestFortressSiegeInfo();
								break;
							case 0x40:
								msg = new RequestGetBossRecord();
								break;
							case 0x41:
								msg = new RequestRefine();
								break;
							case 0x42:
								msg = new RequestConfirmCancelItem();
								break;
							case 0x43:
								msg = new RequestRefineCancel();
								break;
							case 0x44:
								msg = new RequestExMagicSkillUseGround();
								break;
							case 0x45:
								msg = new RequestDuelSurrender();
								break;
							case 0x46:
								msg = new RequestExEnchantSkillInfoDetail();
								break;
							case 0x48:
								msg = new RequestFortressMapInfo();
								break;
							case 0x49:
								msg = new RequestPVPMatchRecord();
								break;
							case 0x4a:
								msg = new SetPrivateStoreWholeMsg();
								break;
							case 0x4b:
								msg = new RequestDispel();
								break;
							case 0x4c:
								msg = new RequestExTryToPutEnchantTargetItem();
								break;
							case 0x4d:
								msg = new RequestExTryToPutEnchantSupportItem();
								break;
							case 0x4e:
								msg = new RequestExCancelEnchantItem();
								break;
							case 0x4f:
								msg = new RequestChangeNicknameColor();
								break;
							case 0x50:
								msg = new RequestResetNickname();
								break;
							case 0x51:
								int id3 = 0;
								if (buf.remaining() >= 4)
								{
									id3 = buf.getInt() & 0xffffffff;
								}
								else
								{
									if (Config.PACKET_HANDLER_DEBUG)
										_log.warn("Client: " + client + " sent a 0xd0:0x51 without the third opcode.");
									break;
								}
								
								switch (id3)
								{
									case 0x00:
										msg = new RequestBookMarkSlotInfo();
										break;
									case 0x01:
										msg = new RequestSaveBookMarkSlot();
										break;
									case 0x02:
										msg = new RequestModifyBookMarkSlot();
										break;
									case 0x03:
										msg = new RequestDeleteBookMarkSlot();
										break;
									case 0x04:
										msg = new RequestTeleportBookMark();
										break;
									case 0x05:
										// RequestChangeBookMarkSlot
										break;
									default:
										printDebug(buf, client, opcode, id2, id3);
										break;
								}
								break;
							case 0x52:
								msg = new RequestWithDrawPremiumItem();
								break;
							case 0x53:
								msg = new RequestJump();
								break;
							case 0x54:
								msg = new RequestStartShowCrataeCubeRank();
								break;
							case 0x55:
								msg = new RequestStopShowCrataeCubeRank();
								break;
							case 0x56:
								msg = new NotifyStartMiniGame();
								break;
							case 0x57:
								msg = new RequestJoinDominionWar();
								break;
							case 0x58:
								msg = new RequestDominionInfo();
								break;
							case 0x59:
								msg = new RequestExCleftEnter();
								break;
							case 0x5a:
								id3 = 0;
								if (buf.remaining() >= 4)
								{
									id3 = buf.getInt() & 0xffffffff;
								}
								else
								{
									_log.warn("Client: " + client + " sent a 0xd0:0x5a without the third opcode.");
									break;
								}
								
								switch (id3)
								{
									case 0x00:
										msg = new RequestExCubeGameChangeTeam();
										break;
									default:
										printDebug(buf, client, opcode, id2, id3);
										break;
								}
								break;
							case 0x5b:
								msg = new EndScenePlayer();
								break;
							case 0x5c:
								msg = new RequestExBlockGameVote();
								break;
							case 0x63:
								msg = new RequestSeedPhase();
								break;
							case 0x65:
								msg = new BrGamePoint();
								break;
							case 0x66:
								msg = new BrProductList();
								break;
							case 0x67:
								msg = new BrProductInfo();
								break;
							case 0x68:
								msg = new BrBuyProduct();
								break;
							default:
								printDebug(buf, client, opcode, id2);
								break;
						}
						break;
					/*
					 * case 0xee: msg = new RequestChangePartyLeader(data,
					 * _client); break;
					 */
					default:
						printDebug(buf, client, opcode);
						break;
				}
				break;
		}
		return msg;
	}
	
	private void printDebug(ByteBuffer buf, L2GameClient client, int... opcodes)
	{
		L2GameSelectorThread.getInstance().printDebug(buf, client, opcodes);
	}
}
