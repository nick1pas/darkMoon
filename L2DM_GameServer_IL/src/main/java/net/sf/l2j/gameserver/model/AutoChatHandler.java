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
package net.sf.l2j.gameserver.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.SevenSigns;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.CreatureSay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Auto Chat Handler 
 * 
 * Allows NPCs to automatically send messages to nearby players
 * at a set time interval.
 *   
 * @author Tempy
 */
public class AutoChatHandler implements SpawnListener
{
    protected static Log _log = LogFactory.getLog(AutoChatHandler.class.getName());
    private static AutoChatHandler _instance;

    private static final long DEFAULT_CHAT_DELAY = 30000; // 30 secs by default
    private static final int  DEFAULT_CHAT_RANGE = 1500; 
    
    protected FastMap<Integer, AutoChatInstance> _registeredChats;

    protected AutoChatHandler()
    {
        _registeredChats = new FastMap<Integer, AutoChatInstance>();
        restoreChatData();
        L2Spawn.addSpawnListener(this);
    }

    private void restoreChatData()
    {
        int numLoaded = 0;
        java.sql.Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);

            statement = con.prepareStatement("SELECT * FROM auto_chat ORDER BY groupId ASC");
            rs = statement.executeQuery();
                        
            while (rs.next())
            {
            	int groupId = rs.getInt("groupId");
                int npcId = rs.getInt("npcId");
                long chatDelay = rs.getLong("chatDelay") * 1000;
                int chatRange = rs.getInt("chatRange");
                boolean chatRandom = rs.getBoolean("chatRandom");
                
            	numLoaded++;

            	statement2 = con.prepareStatement("SELECT * FROM auto_chat_text WHERE groupId=?");
                statement2.setInt(1, groupId);
                rs2 = statement2.executeQuery();

                ArrayList<String> chatTexts = new ArrayList<String>();                

                while (rs2.next())
                	chatTexts.add(rs2.getString("chatText"));
 
                if (chatTexts.size() > 0)
                		 registerGlobalChat(npcId, 
                							chatTexts.toArray(new String[chatTexts.size()]), 
                							chatDelay, 
                							chatRange, 
                							chatRandom);
                else
                	_log.warn("AutoChatHandler: Chat group " + groupId + " is empty.");

                statement2.close();
            }

            statement.close();

            //if (_log.isDebugEnabled())
                _log.info("GameServer: AutoChatHandler: Loaded " + numLoaded + " Chat Group(s) From the Database.");
        }
        catch (Exception e)
        {
            _log.warn("AutoSpawnHandler: Could not restore chat data: " + e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    public static AutoChatHandler getInstance()
    {
        if (_instance == null) 
        	{
        		_instance = new AutoChatHandler();
        		_log.info("GameServer: AutoChatHandler: Loaded " + _instance.size() + " Chat Handlers in Total.");
        	}

        return _instance;
    }

    public int size()
    {
        return _registeredChats.size();
    }

    /**
     * Registers a globally active auto chat for ALL instances of the given NPC ID.
     * <BR>
     * Returns the associated auto chat instance.
     * 
     * @param int npcId
     * @param String[] chatTexts
     * @param int chatDelay (-1 = default delay)
     * @return AutoChatInstance chatInst
     */
    public AutoChatInstance registerGlobalChat(int npcId, String[] chatTexts, long chatDelay, int chatRange, boolean chatRandom)
    {
        return registerChat(npcId, null, chatTexts, chatDelay, chatRange, chatRandom);
    }

    /**
     * Registers a NON globally-active auto chat for the given NPC instance, and adds to the currently 
     * assigned chat instance for this NPC ID, otherwise creates a new instance if 
     * a previous one is not found. 
     * <BR>
     * Returns the associated auto chat instance.
     * 
     * @param L2NpcInstance npcInst
     * @param String[] chatTexts
     * @param int chatDelay (-1 = default delay)
     * @return AutoChatInstance chatInst
     */
    public AutoChatInstance registerChat(L2NpcInstance npcInst, String[] chatTexts, 
			 long chatDelay)
    {
    	return registerChat(npcInst.getNpcId(), npcInst, chatTexts, chatDelay, DEFAULT_CHAT_RANGE, false);
    }
    
    public AutoChatInstance registerChat(L2NpcInstance npcInst, String[] chatTexts, 
    									 long chatDelay, int chatRange, boolean chatRandom)
    {
        return registerChat(npcInst.getNpcId(), npcInst, chatTexts, chatDelay, chatRange, chatRandom);
    }

    private final AutoChatInstance registerChat(int npcId, L2NpcInstance npcInst, String[] chatTexts,
                                                long chatDelay, int chatRange, boolean chatRandom)
    {
        AutoChatInstance chatInst = null;

        if (chatDelay < 0) chatDelay = DEFAULT_CHAT_DELAY;
        if (chatRange < 0) chatRange = DEFAULT_CHAT_RANGE;

        if (_registeredChats.containsKey(npcId)) chatInst = _registeredChats.get(npcId);
        else chatInst = new AutoChatInstance(npcId, chatTexts, chatDelay, chatRange, chatRandom, (npcInst == null));

        if (npcInst != null) chatInst.addChatDefinition(npcInst);

        _registeredChats.put(npcId, chatInst);

        return chatInst;
    }

    /**
     * Removes and cancels ALL auto chat definition for the given NPC ID, 
     * and removes its chat instance if it exists.
     * 
     * @param int npcId
     * @return boolean removedSuccessfully
     */
    public boolean removeChat(int npcId)
    {
        AutoChatInstance chatInst = _registeredChats.get(npcId);

        return removeChat(chatInst);
    }

    /**
     * Removes and cancels ALL auto chats for the given chat instance.
     * 
     * @param AutoChatInstance chatInst
     * @return boolean removedSuccessfully
     */
    public boolean removeChat(AutoChatInstance chatInst)
    {
        if (chatInst == null) return false;

        _registeredChats.remove(chatInst);
        chatInst.setActive(false);

        if (_log.isDebugEnabled())
            _log.info("AutoChatHandler: Removed auto chat for NPC ID " + chatInst.getNPCId());

        return true;
    }

    /**
     * Returns the associated auto chat instance either by the given NPC ID
     * or object ID.
     * 
     * @param int id
     * @param boolean byObjectId
     * @return AutoChatInstance chatInst
     */
    public AutoChatInstance getAutoChatInstance(int id, boolean byObjectId)
    {
        if (!byObjectId)
            return _registeredChats.get(id);
        for (AutoChatInstance chatInst : _registeredChats.values())
            if (chatInst.getChatDefinition(id) != null)
                return chatInst;

        return null;
    }

    /**
     * Sets the active state of all auto chat instances to that specified,
     * and cancels the scheduled chat task if necessary.
     * 
     * @param boolean isActive
     */
    public void setAutoChatActive(boolean isActive)
    {
        for (AutoChatInstance chatInst : _registeredChats.values())
            chatInst.setActive(isActive);
    }

    /**
     * Sets the active state of all auto chat instances of specified NPC ID
     * 
     * @param npcId
     * @param isActive
     */
    public void setAutoChatActive(int npcId, boolean isActive)
    {
        for (AutoChatInstance chatInst : _registeredChats.values())
            if (chatInst.getNPCId() == npcId) chatInst.setActive(isActive);
    }
    
    /**
     * Used in conjunction with a SpawnListener, this method is called every time
     * an NPC is spawned in the world.
     * <BR><BR>
     * If an auto chat instance is set to be "global", all instances matching the registered
     * NPC ID will be added to that chat instance.
     */
    public void npcSpawned(L2NpcInstance npc)
    {
        synchronized (_registeredChats)
        {
            if (npc == null) return;

            int npcId = npc.getNpcId();

            if (_registeredChats.containsKey(npcId))
            {
                AutoChatInstance chatInst = _registeredChats.get(npcId);

                if (chatInst != null && chatInst.isGlobal()) chatInst.addChatDefinition(npc);
            }
        }
    }

    /**
     * Auto Chat Instance
     * <BR><BR>
     * Manages the auto chat instances for a specific registered NPC ID.
     *  
     * @author Tempy
     */
    public class AutoChatInstance
    {
        protected int _npcId;
        private long _defaultDelay = DEFAULT_CHAT_DELAY;
        private int _defaultRange = DEFAULT_CHAT_RANGE;
        private String[] _defaultTexts;
        private boolean _defaultRandom;

        private boolean _globalChat = false;
        private boolean _isActive;

        private FastMap<Integer, AutoChatDefinition> _chatDefinitions = new FastMap<Integer, AutoChatDefinition>();
        protected ScheduledFuture _chatTask;

        protected AutoChatInstance(int npcId, String[] chatTexts, long chatDelay, int chatRange, boolean chatRandom, boolean isGlobal)
        {
            _defaultTexts = chatTexts;
            _npcId = npcId;
            _defaultDelay = (chatDelay<=0 ? DEFAULT_CHAT_DELAY : chatDelay);
            _defaultRange = chatRange;
            _defaultRandom = chatRandom;
            _globalChat = isGlobal;

            if (_log.isDebugEnabled())
                _log.info("AutoChatHandler: Registered auto chat for NPC ID " + _npcId
                    + " (Global Chat = " + _globalChat + ").");

            setActive(true);
        }

        protected AutoChatDefinition getChatDefinition(int objectId)
        {
            return _chatDefinitions.get(objectId);
        }

        protected AutoChatDefinition[] getChatDefinitions()
        {
            return _chatDefinitions.values().toArray(new AutoChatDefinition[_chatDefinitions.values().size()]);
        }

        /**
         * Defines an auto chat for an instance matching this auto chat instance's registered NPC ID,
         * and launches the scheduled chat task.
         * <BR>
         * Returns the object ID for the NPC instance, with which to refer
         * to the created chat definition.
         * <BR>
         * <B>Note</B>: Uses pre-defined default values for texts and chat delays from the chat instance.
         * 
         * @param L2NpcInstance npcInst
         * @return int objectId
         */
        public int addChatDefinition(L2NpcInstance npcInst)
        {
            return addChatDefinition(npcInst, null, 0);
        }

        /**
         * Defines an auto chat for an instance matching this auto chat instance's registered NPC ID,
         * and launches the scheduled chat task.
         * <BR>
         * Returns the object ID for the NPC instance, with which to refer
         * to the created chat definition.
         * 
         * @param L2NpcInstance npcInst
         * @param String[] chatTexts
         * @param int chatDelay
         * @return int objectId
         */
        public int addChatDefinition(L2NpcInstance npcInst, String[] chatTexts, long chatDelay)
        {
            int objectId = npcInst.getObjectId();
            AutoChatDefinition chatDef = new AutoChatDefinition(this, npcInst, chatTexts, chatDelay);

            _chatDefinitions.put(objectId, chatDef);
            return objectId;
        }

        /**
         * Removes a chat definition specified by the given object ID.
         * 
         * @param int objectId
         * @return boolean removedSuccessfully
         */
        public boolean removeChatDefinition(int objectId)
        {
            if (!_chatDefinitions.containsKey(objectId)) return false;

            AutoChatDefinition chatDefinition = _chatDefinitions.get(objectId);
            chatDefinition.setActive(false);

            _chatDefinitions.remove(objectId);

            return true;
        }

        /**
         * Tests if this auto chat instance is active.
         * 
         * @return boolean isActive
         */
        public boolean isActive()
        {
            return _isActive;
        }

        /**
         * Tests if this auto chat instance applies to 
         * ALL currently spawned instances of the registered NPC ID.
         * 
         * @return boolean isGlobal
         */
        public boolean isGlobal()
        {
            return _globalChat;
        }

        /**
         * Tests if random order is the DEFAULT for new chat definitions. 
         * 
         * @return boolean isRandom
         */
        public boolean isDefaultRandom()
        {
            return _defaultRandom;
        }

        /**
         * Tests if the auto chat definition given by its object ID is set to be random. 
         * 
         * @return boolean isRandom
         */
        public boolean isRandomChat(int objectId)
        {
            if (!_chatDefinitions.containsKey(objectId)) return false;

            return _chatDefinitions.get(objectId).isRandomChat();
        }

        /**
         * Returns the ID of the NPC type managed by this auto chat instance.
         *
         * @return int npcId
         */
        public int getNPCId()
        {
            return _npcId;
        }

        /**
         * Returns the number of auto chat definitions stored for this instance.
         * 
         * @return int definitionCount
         */
        public int getDefinitionCount()
        {
            return _chatDefinitions.size();
        }

        /**
         * Returns a list of all NPC instances handled by this auto chat instance.
         * 
         * @return L2NpcInstance[] npcInsts
         */
        public L2NpcInstance[] getNPCInstanceList()
        {
            FastList<L2NpcInstance> npcInsts = new FastList<L2NpcInstance>();

            for (AutoChatDefinition chatDefinition : _chatDefinitions.values())
                npcInsts.add(chatDefinition._npcInstance);

            return npcInsts.toArray(new L2NpcInstance[npcInsts.size()]);
        }

        /**
         * A series of methods used to get and set default values for new chat definitions.
         */
        public long getDefaultDelay()
        {
            return _defaultDelay;
        }

        public String[] getDefaultTexts()
        {
            return _defaultTexts;
        }

        public int getDefaultRange()
        {
            return _defaultRange;
        }
        
        public void setDefaultChatDelay(long delayValue)
        {
            _defaultDelay = delayValue;
        }

        public void setDefaultChatTexts(String[] textsValue)
        {
            _defaultTexts = textsValue;
        }

        public void setDefaultRange(int rangeValue)
        {
            _defaultRange = rangeValue;
        }
        
        public void setDefaultRandom(boolean randValue)
        {
            _defaultRandom = randValue;
        }

        /**
         * Sets a specific chat delay for the specified auto chat definition given by its object ID.
         * 
         * @param int objectId
         * @param long delayValue
         */
        public void setChatDelay(int objectId, long delayValue)
        {
            AutoChatDefinition chatDef = getChatDefinition(objectId);

            if (chatDef != null) chatDef.setChatDelay(delayValue);
        }

        /**
         * Sets a specific set of chat texts for the specified auto chat definition given by its object ID.
         * 
         * @param int objectId
         * @param String[] textsValue
         */
        public void setChatTexts(int objectId, String[] textsValue)
        {
            AutoChatDefinition chatDef = getChatDefinition(objectId);

            if (chatDef != null) chatDef.setChatTexts(textsValue);
        }

        /**
         * Sets specifically to use random chat order for the auto chat definition given by its object ID.
         * 
         * @param int objectId
         * @param boolean randValue
         */
        public void setRandomChat(int objectId, boolean randValue)
        {
            AutoChatDefinition chatDef = getChatDefinition(objectId);

            if (chatDef != null) chatDef.setRandomChat(randValue);
        }

        /**
         * Sets specifically to use chat range for the auto chat definition given by its object ID.
         * 
         * @param int objectId
         * @param boolean randValue
         */
        public void setChatRange(int objectId, int rangeValue)
        {
            AutoChatDefinition chatDef = getChatDefinition(objectId);

            if (chatDef != null) chatDef.setChatRange(rangeValue);
        }
        
        /**
         * Sets the activity of ALL auto chat definitions handled by this chat instance.
         * 
         * @param boolean isActive
         */
        public void setActive(boolean activeValue)
        {
            if (_isActive == activeValue) return;

            _isActive = activeValue;

            if (!isGlobal())
            {
                for (AutoChatDefinition chatDefinition : _chatDefinitions.values())
                    chatDefinition.setActive(activeValue);

                return;
            }

            if (isActive())
            {
                AutoChatRunner acr = new AutoChatRunner(_npcId, -1);
                _chatTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(acr,
                                               _defaultDelay, _defaultDelay);
            }
            else
            {
                _chatTask.cancel(false);
            }
        }

        /**
         * Auto Chat Definition
         * <BR><BR>
         * Stores information about specific chat data for an instance of the NPC ID
         * specified by the containing auto chat instance.
         * <BR>
         * Each NPC instance of this type should be stored in a subsequent AutoChatDefinition class.
         * 
         * @author Tempy
         */
        private class AutoChatDefinition
        {
            protected int _chatIndex = 0;
            protected L2NpcInstance _npcInstance;

            protected AutoChatInstance _chatInstance;

            private long _chatDelay = DEFAULT_CHAT_DELAY;
            private int _chatRange = DEFAULT_CHAT_RANGE;
            private String[] _chatTexts = null;
            private boolean _isActiveDefinition;
            private boolean _randomChat;

            protected AutoChatDefinition(AutoChatInstance chatInst, L2NpcInstance npcInst,
                                         String[] chatTexts, long chatDelay)
            {
                _npcInstance = npcInst;

                _chatInstance = chatInst;
                _randomChat = chatInst.isDefaultRandom();

                _chatDelay = (chatDelay<=0 ? DEFAULT_CHAT_DELAY : chatDelay );
                _chatRange = chatInst.getDefaultRange();
                _chatTexts = chatTexts;

                if (_log.isDebugEnabled())
                    _log.info("AutoChatHandler: Chat definition added for NPC ID "
                        + _npcInstance.getNpcId() + " (Object ID = " + _npcInstance.getObjectId() + ").");

                // If global chat isn't enabled for the parent instance, 
                // then handle the chat task locally.
                if (!chatInst.isGlobal()) setActive(true);
            }

            protected AutoChatDefinition(AutoChatInstance chatInst, L2NpcInstance npcInst)
            {
                this(chatInst, npcInst, null, -1);
            }

            protected String[] getChatTexts()
            {
                if (_chatTexts != null)
                    return _chatTexts;
                return _chatInstance.getDefaultTexts();
            }

            private long getChatDelay()
            {
                if (_chatDelay > 0)
                    return _chatDelay;
                return _chatInstance.getDefaultDelay();
            }

            private boolean isActive()
            {
                return _isActiveDefinition;
            }

            boolean isRandomChat()
            {
                return _randomChat;
            }

            void setRandomChat(boolean randValue)
            {
                _randomChat = randValue;
            }

            void setChatDelay(long delayValue)
            {
                _chatDelay = delayValue;
            }

            void setChatRange(int rangeValue)
            {
                _chatRange = rangeValue;
            }
            
            void setChatTexts(String[] textsValue)
            {
                _chatTexts = textsValue;
            }

            void setActive(boolean activeValue)
            {
                if (isActive() == activeValue) return;

                if (activeValue)
                {
                    AutoChatRunner acr = new AutoChatRunner(_npcId, _npcInstance.getObjectId());
                    _chatTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(
                                                                                           acr,
                                                                                           getChatDelay(),
                                                                                           getChatDelay());
                }
                else
                {
                    _chatTask.cancel(false);
                }

                _isActiveDefinition = activeValue;
            }
        }

        /**
         * Auto Chat Runner
         * <BR><BR>
         * Represents the auto chat scheduled task for each chat instance.
         * 
         * @author Tempy
         */
        private class AutoChatRunner implements Runnable
        {
            private int _runnerNpcId;
            private int _objectId;

            protected AutoChatRunner(int pNpcId, int pObjectId)
            {
                _runnerNpcId = pNpcId;
                _objectId = pObjectId;
            }

            public synchronized void run()
            {
                AutoChatInstance chatInst = _registeredChats.get(_runnerNpcId);
                AutoChatDefinition[] chatDefinitions;

                if (chatInst.isGlobal())
                {
                    chatDefinitions = chatInst.getChatDefinitions();
                }
                else
                {
                    AutoChatDefinition chatDef = chatInst.getChatDefinition(_objectId);

                    if (chatDef == null)
                    {
                        _log.warn("AutoChatHandler: Auto chat definition is NULL for NPC ID "+_npcId+".");
                        return;
                    }

                    chatDefinitions = new AutoChatDefinition[] {chatDef};
                }

                if (_log.isDebugEnabled())
                    _log.info("AutoChatHandler: Running auto chat for " + chatDefinitions.length
                        + " instances of NPC ID " + _npcId + "." + " (Global Chat = "
                        + chatInst.isGlobal() + ")");

                for (AutoChatDefinition chatDef : chatDefinitions)
                {
                    try
                    {
                        L2NpcInstance chatNpc = chatDef._npcInstance;
                        FastList<L2PcInstance> nearbyPlayers = new FastList<L2PcInstance>();
                        
                        for (L2Character player : chatNpc.getKnownList().getKnownCharactersInRadius(chatDef._chatRange))
                            if (player instanceof L2PcInstance) nearbyPlayers.add((L2PcInstance) player);

                        int maxIndex = chatDef.getChatTexts().length;
                        int lastIndex = Rnd.nextInt(maxIndex);

                        String creatureName = chatNpc.getName();
                        String text;

                        if (!chatDef.isRandomChat())
                        {
                            lastIndex = chatDef._chatIndex;
                            if (lastIndex == maxIndex) lastIndex = 0;
                            chatDef._chatIndex = lastIndex + 1;
                        }

                        text = chatDef.getChatTexts()[lastIndex];

                        if (text == null) return;

                        if (!nearbyPlayers.isEmpty())
                        {
                            final int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
                            int losingCabal = SevenSigns.CABAL_NULL;

                            if (winningCabal == SevenSigns.CABAL_DAWN) losingCabal = SevenSigns.CABAL_DUSK;
                            else if (winningCabal == SevenSigns.CABAL_DUSK)
                                losingCabal = SevenSigns.CABAL_DAWN;
                            
                            if (text.indexOf("%player_") > -1)
                            {
                            	ArrayList<Integer> karmaPlayers = new ArrayList<Integer>();
                            	ArrayList<Integer> winningCabals = new ArrayList<Integer>();
                            	ArrayList<Integer> losingCabals = new ArrayList<Integer>();
                            	
                                for (int i=0; i < nearbyPlayers.size(); i++)
                                {
                                	L2PcInstance nearbyPlayer = nearbyPlayers.get(i);
                                	
                                    // Get all nearby players with karma
                                	if (nearbyPlayer.getKarma() > 0)
                                    	karmaPlayers.add(i);
                                	
                                    // Get all nearby Seven Signs winners and loosers
                                    if (SevenSigns.getInstance().getPlayerCabal(nearbyPlayer) == winningCabal)
                                    	winningCabals.add(i);
                                    else if (SevenSigns.getInstance().getPlayerCabal(nearbyPlayer) == losingCabal)
                                    	losingCabals.add(i);
                                }
                                
                                if (text.indexOf("%player_random%") > -1)
                                {
                                	int randomPlayerIndex = Rnd.nextInt(nearbyPlayers.size());
                                	L2PcInstance randomPlayer = nearbyPlayers.get(randomPlayerIndex);
                                	text = text.replaceAll("%player_random%", randomPlayer.getName());
                                }
                                else if (text.indexOf("%player_killer%") > -1 && karmaPlayers.size() > 0)
                                {
                                	int randomPlayerIndex = karmaPlayers.get(Rnd.nextInt(karmaPlayers.size()));
                                	L2PcInstance randomPlayer = nearbyPlayers.get(randomPlayerIndex);
                                    text = text.replaceAll("%player_killer%", randomPlayer.getName());
                                }
                                else if (text.indexOf("%player_cabal_winner%") > -1 && winningCabals.size() > 0)
                                {
                                	int randomPlayerIndex = winningCabals.get(Rnd.nextInt(winningCabals.size()));
                                	L2PcInstance randomPlayer = nearbyPlayers.get(randomPlayerIndex);
                                    text = text.replaceAll("%player_cabal_winner%", randomPlayer.getName());
                                }
                                else if (text.indexOf("%player_cabal_loser%") > -1 && losingCabals.size() > 0)
                                {
                                	int randomPlayerIndex = losingCabals.get(Rnd.nextInt(losingCabals.size()));
                                	L2PcInstance randomPlayer = nearbyPlayers.get(randomPlayerIndex);
                                    text = text.replaceAll("%player_cabal_loser%", randomPlayer.getName());
                                }
                            }
                        }

                        if (text == null) return;

                        if (text.contains("%player_")) return;

                        CreatureSay cs = new CreatureSay(chatNpc.getObjectId(), 0, creatureName, text);

                        for (L2PcInstance nearbyPlayer : nearbyPlayers)
                            nearbyPlayer.sendPacket(cs);

                        if (_log.isDebugEnabled())
                            _log.info("AutoChatHandler: Chat propogation for object ID "
                                + chatNpc.getObjectId() + " (" + creatureName + ") with text '" + text
                                + "' sent to " + nearbyPlayers.size() + " nearby players.");
                    }
                    catch (Exception e)
                    {
                        _log.error(e.getMessage(),e);
                        return;
                    }
                }
            }
        }
    }
}