package com.balancedbytes.games.ffb.server.net;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class ChannelManager {
  
  private Map<Long, Set<SocketChannel>> fChannelsByGameId;
  private Map<SocketChannel, JoinedClient> fClientByChannel;
  private Map<SocketChannel, Long> fLastPingByChannel;
  
  private class JoinedClient {
    
    private long fGameId;
    private String fCoach;
    private ClientMode fMode;
    private boolean fHomeCoach;
    
    public JoinedClient(long pGameId, String pCoach, ClientMode pMode, boolean pHomeCoach) {
      fGameId = pGameId;
      fCoach = pCoach;
      fMode = pMode;
      fHomeCoach = pHomeCoach;
    }
    
    public long getGameId() {
      return fGameId;
    }
    
    public String getCoach() {
      return fCoach;
    }
    
    public ClientMode getMode() {
      return fMode;
    }
    
    public boolean isHomeCoach() {
      return fHomeCoach;
    }
    
  }
  
  public ChannelManager() {
    fChannelsByGameId = new HashMap<Long, Set<SocketChannel>>();
    fClientByChannel = new HashMap<SocketChannel, JoinedClient>();
    fLastPingByChannel = new HashMap<SocketChannel, Long>();
  }
  
  public long getGameIdForChannel(SocketChannel pChannel) {
    JoinedClient client = fClientByChannel.get(pChannel);
    if (client != null) {
      return client.getGameId();
    } else {
      return 0;
    }
  }
  
  public String getCoachForChannel(SocketChannel pChannel) {
    JoinedClient client = fClientByChannel.get(pChannel);
    if (client != null) {
      return client.getCoach();
    } else {
      return null;
    }
  }
  
  public ClientMode getModeForChannel(SocketChannel pChannel) {
    JoinedClient client = fClientByChannel.get(pChannel);
    if (client != null) {
      return client.getMode();
    } else {
      return null;
    }
  }
  
  public SocketChannel[] getChannelsForGameId(long pGameId) {
    Set<SocketChannel> channels = fChannelsByGameId.get(pGameId);
    if (channels != null) {
      return (SocketChannel[]) channels.toArray(new SocketChannel[channels.size()]);
    } else {
      return new SocketChannel[0];
    }
  }
  
  public SocketChannel getChannelOfHomeCoach(GameState pGameState) {
    SocketChannel channelHomeCoach = null;
    if (pGameState != null) {
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if (channels != null) {
        Iterator<SocketChannel> channelIterator = channels.iterator();
        while ((channelHomeCoach == null) && channelIterator.hasNext()) {
          SocketChannel channel = channelIterator.next();
          JoinedClient client = fClientByChannel.get(channel);
          if ((client != null) && (client.getMode() == ClientMode.PLAYER) && client.isHomeCoach()) {
            channelHomeCoach = channel;            
          }
        }
      }
    }
    return channelHomeCoach;
  }

  public boolean isHomeCoach(GameState pGameState, String pCoach) {
    JoinedClient clientHomeCoach = fClientByChannel.get(getChannelOfHomeCoach(pGameState));
    return ((clientHomeCoach != null) && clientHomeCoach.getCoach().equals(pCoach));
  }
  
  public SocketChannel getChannelOfAwayCoach(GameState pGameState) {
    SocketChannel channelAwayCoach = null;
    if (pGameState != null) {
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if (channels != null) {
        Iterator<SocketChannel> channelIterator = channels.iterator();
        while ((channelAwayCoach == null) && channelIterator.hasNext()) {
          SocketChannel channel = channelIterator.next();
          JoinedClient client = fClientByChannel.get(channel);
          if ((client != null) && (client.getMode() == ClientMode.PLAYER) && !client.isHomeCoach()) {
            channelAwayCoach = channel;            
          }
        }
      }
    }
    return channelAwayCoach;
  }

  public boolean isAwayCoach(GameState pGameState, String pCoach) {
    JoinedClient clientAwayCoach = fClientByChannel.get(getChannelOfAwayCoach(pGameState));
    return ((clientAwayCoach != null) && clientAwayCoach.getCoach().equals(pCoach));
  }

  public SocketChannel[] getChannelsWithoutAwayCoach(GameState pGameState) {
    SocketChannel[] filteredChannels = new SocketChannel[0];
    if (pGameState != null) {
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if ((channels != null) && (channels.size() > 0)) {
        SocketChannel channelAwayCoach = getChannelOfAwayCoach(pGameState);
        if (channelAwayCoach != null) {
          int i = 0;
          filteredChannels = new SocketChannel[channels.size() - 1];
          Iterator<SocketChannel> channelIterator = channels.iterator();
          while (channelIterator.hasNext()) {
            SocketChannel channel = channelIterator.next();
            if (channel != channelAwayCoach) {
              filteredChannels[i] = channel;
              i++;
            }
          }
        }
      }
    }
    return filteredChannels;
  }

  public SocketChannel[] getChannelsWithoutHomeCoach(GameState pGameState) {
    SocketChannel[] filteredChannels = new SocketChannel[0];
    if (pGameState != null) {
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if ((channels != null) && (channels.size() > 0)) {
        SocketChannel channelHomeCoach = getChannelOfHomeCoach(pGameState);
        if (channelHomeCoach != null) {
          int i = 0;
          filteredChannels = new SocketChannel[channels.size() - 1];
          Iterator<SocketChannel> channelIterator = channels.iterator();
          while (channelIterator.hasNext()) {
            SocketChannel channel = channelIterator.next();
            if (channel != channelHomeCoach) {
              filteredChannels[i] = channel;
              i++;
            }
          }
        }
      }
    }
    return filteredChannels;
  }

  public SocketChannel[] getChannelsOfSpectators(GameState pGameState) {
    SocketChannel[] filteredChannels = new SocketChannel[0];
    if (pGameState != null) {
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if ((channels != null) && (channels.size() > 0)) {
        SocketChannel channelAwayCoach = getChannelOfAwayCoach(pGameState);
        SocketChannel channelHomeCoach = getChannelOfHomeCoach(pGameState);
        if ((channelHomeCoach != null) && (channelAwayCoach != null)) {
          int i = 0;
          filteredChannels = new SocketChannel[channels.size() - 2];
          Iterator<SocketChannel> channelIterator = channels.iterator();
          while (channelIterator.hasNext()) {
            SocketChannel channel = channelIterator.next();
            if ((channel != channelAwayCoach) && (channel != channelHomeCoach)) {
              filteredChannels[i] = channel;
              i++;
            }
          }
        }
      }
    }
    return filteredChannels;
  }

  public void addChannel(SocketChannel pChannel, GameState pGameState, String pCoach, ClientMode pMode, boolean pHomeCoach) {
    if (pGameState != null) {
      JoinedClient client = new JoinedClient(pGameState.getId(), pCoach, pMode, pHomeCoach);
      fClientByChannel.put(pChannel, client);
      Set<SocketChannel> channels = fChannelsByGameId.get(pGameState.getId());
      if (channels == null) {
        channels = new HashSet<SocketChannel>();
        fChannelsByGameId.put(pGameState.getId(), channels);
      }
      channels.add(pChannel);
    }
  }
  
  public void removeChannel(SocketChannel pChannel) {
    long gameId = getGameIdForChannel(pChannel);
    fClientByChannel.remove(pChannel);
    Set<SocketChannel> channels = fChannelsByGameId.get(gameId);
    if (channels != null) {
      channels.remove(pChannel);
      if (channels.size() == 0) {
        fChannelsByGameId.remove(gameId);
      }
    }
  }

  public void setLastPing(SocketChannel pChannel, long pPing) {
    fLastPingByChannel.put(pChannel, pPing);
  }
  
  public long getLastPing(SocketChannel pChannel) {
    Long lastPing = fLastPingByChannel.get(pChannel);
    return (lastPing != null) ? lastPing : 0;
  }
  
  public SocketChannel[] getAllChannels() {
    synchronized (fClientByChannel) {
      return fClientByChannel.keySet().toArray(new SocketChannel[fClientByChannel.size()]);
    }
  }
  
}
