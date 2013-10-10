package com.balancedbytes.games.ffb.server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.commands.InternalCommandSocketClosed;

public class NioServer implements Runnable {
  
  private static final boolean _TRACE = false;
  
  // set false to stop the server
  private boolean fStopped;
  
  // The host:port combination to listen on
  private InetAddress fHostAddress;
  private int fPort;

  // The channel on which we'll accept connections
  private ServerSocketChannel fServerChannel;

  // The selector we'll be monitoring
  private Selector fSelector;

  // The buffer into which we'll read data when it's available
  private Map<SocketChannel,ByteBuffer> fReadBufferByChannel;

  private INetCommandHandler fCommandHandler;

  // Maps a SocketChannel to a list of ByteBuffer instances
  private Map<SocketChannel,List<ByteBuffer>> fPendingData;
  
  private NetCommandFactory fNetCommandFactory;
    
  public NioServer(InetAddress pHostAddress, int pPort, INetCommandHandler pCommandHandler) throws IOException {
    fHostAddress = pHostAddress;
    fPort = pPort;
    fCommandHandler = pCommandHandler;
    fReadBufferByChannel = new HashMap<SocketChannel, ByteBuffer>();
    fPendingData = Collections.synchronizedMap(new HashMap<SocketChannel,List<ByteBuffer>>());
    fSelector = initSelector();
    fNetCommandFactory = new NetCommandFactory();
  }
  
  public void send(SocketChannel pSocketChannel, NetCommand pNetCommand) {

    // queue the data we want written
    synchronized (fPendingData) {
      List<ByteBuffer> queue = fPendingData.get(pSocketChannel);
      if (queue == null) {
        queue = new LinkedList<ByteBuffer>();
        fPendingData.put(pSocketChannel, queue);
      }
      queue.add(ByteBuffer.wrap(pNetCommand.toBytes()));
      if (_TRACE) {
        System.out.println(" ++ Queued " + pSocketChannel + " " + pNetCommand.getId());
      }
    }
    
    // finally, wake up our selecting thread so it can make the required changes
    fSelector.wakeup();
    
  }

  public void run() {
    
    fStopped = false;
    
    while (true) {
      
      try {
        
        synchronized (fPendingData) {
          if (fPendingData.size() > 0) {
            Iterator<SocketChannel> socketChannelIterator = fPendingData.keySet().iterator();
            while (socketChannelIterator.hasNext()) {
              SocketChannel socketChannel = socketChannelIterator.next();
              if (socketChannel == null)
                continue;
              SelectionKey key = socketChannel.keyFor(fSelector);
              if ((key == null) || !key.isValid()) {
                continue;
              }
              if (key.interestOps() == SelectionKey.OP_READ) {
                key.interestOps(SelectionKey.OP_WRITE);
                if (_TRACE) {
                  System.out.println(" -- Change Ops WRITE " + socketChannel);
                }
              }
            }
          }
        }
                
        // Wait for an event one of the registered channels
        fSelector.select();
        
        if (fStopped) {
          break;
        }

        // Iterate over the set of keys for which events are available
        Iterator<SelectionKey> selectedKeys = fSelector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
          
          SelectionKey key = selectedKeys.next();
          selectedKeys.remove();

          if (!key.isValid()) {
            continue;
          }

          // Check what event is available and deal with it
          if (key.isAcceptable()) {
            accept(key);
          } else if (key.isReadable()) {
            read(key);
          } else if (key.isWritable()) {
            write(key);
          }
          
        }
      
      } catch (Exception e) {
        throw new FantasyFootballException(e);
      }
      
    }
    
  }

  private void accept(SelectionKey pSelectionKey) throws IOException {
    
    // For an accept to be pending the channel must be a server socket channel.
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) pSelectionKey.channel();

    // Accept the connection and make it non-blocking
    SocketChannel socketChannel = serverSocketChannel.accept();
    // Socket socket = socketChannel.socket();
    socketChannel.configureBlocking(false);

    // Register the new SocketChannel with our Selector, indicating
    // we'd like to be notified when there's data waiting to be read
    socketChannel.register(fSelector, SelectionKey.OP_READ);
    
  }

  private void read(SelectionKey pSelectionKey) throws IOException {

    SocketChannel socketChannel = (SocketChannel) pSelectionKey.channel();
    ByteBuffer readBuffer = fReadBufferByChannel.get(socketChannel);
    if (readBuffer == null) {
      readBuffer = ByteBuffer.allocate(16 * 1024);
      readBuffer.clear();
      fReadBufferByChannel.put(socketChannel, readBuffer);
    }

    // Attempt to read off the channel
    int nrBytesRead = 0;
    try {
      nrBytesRead = socketChannel.read(readBuffer);
    } catch (IOException e) {
      // The remote forcibly closed the connection, cancel
      // the selection key and close the channel.
      removeChannel(socketChannel);
      return;
    }

    if (nrBytesRead == -1) {
      // Remote entity shut the socket down cleanly. Do the
      // same from our end and cancel the channel.
      removeChannel(socketChannel);
      return;
    }

    byte[] readBytes = readBuffer.array();
    int totalReadBytes = readBuffer.position();
    int offset = 0;
    while (offset < nrBytesRead) {
      byte[] cmdBytes = fNetCommandFactory.nextCommandBytes(readBytes, offset, totalReadBytes);
      if (cmdBytes != null) {
        NetCommand netCommand = fNetCommandFactory.fromBytes(cmdBytes);
        if (netCommand != null) {
          netCommand.setSender(socketChannel);
          fCommandHandler.handleNetCommand(netCommand);
          offset += netCommand.size();
        }
      } else {
        break;
      }
    }

    readBuffer.clear();
    for (int i = offset; i < nrBytesRead; i++) {
      readBuffer.put(readBytes[i]);
    }
    
  }
  
  public void removeChannel(SocketChannel pSocketChannel) throws IOException {
    if (pSocketChannel != null) {
      pSocketChannel.close();
      SelectionKey selectionKey = pSocketChannel.keyFor(fSelector);
      if (selectionKey != null) {
        selectionKey.cancel();
      }
      fReadBufferByChannel.remove(pSocketChannel);
      fSelector.wakeup();
      fCommandHandler.handleNetCommand(new InternalCommandSocketClosed(pSocketChannel));
    }
  }
  
  private void write(SelectionKey pSelectionKey) throws IOException {

    SocketChannel socketChannel = (SocketChannel) pSelectionKey.channel();

    boolean writeFinished = true;
    ByteBuffer writeBuffer = null;

    synchronized (fPendingData) {
      List<ByteBuffer> queue = fPendingData.get(socketChannel);
      if ((queue != null) && !queue.isEmpty()) {
        writeBuffer = queue.get(0);
      }
    }

    if (writeBuffer != null) {
      
      try {
        socketChannel.write(writeBuffer);
      } catch (IOException e) {
        // The remote forcibly closed the connection, cancel
        // the selection key and close the channel.
        removeChannel(socketChannel);
        return;
      }
      
      writeFinished = !writeBuffer.hasRemaining();
      if (writeFinished) {    
        synchronized (fPendingData) {
          List<ByteBuffer> queue = fPendingData.get(socketChannel);
          if ((queue != null) && !queue.isEmpty()) {
            queue.remove(0);
          }
          writeFinished = queue.isEmpty();
          if (writeFinished) {
            fPendingData.remove(socketChannel);
          }
        }
      }
      
    }
    
    if ((writeFinished) && (pSelectionKey.interestOps() == SelectionKey.OP_WRITE)) {
      if (_TRACE) {
        System.out.println(" -- Change Ops READ " + socketChannel);
      }
      pSelectionKey.interestOps(SelectionKey.OP_READ);
    }

    fSelector.wakeup();
    
  }

  private Selector initSelector() throws IOException {

    // Create a new selector
    Selector socketSelector = SelectorProvider.provider().openSelector();

    // Create a new non-blocking server socket channel
    fServerChannel = ServerSocketChannel.open();
    fServerChannel.configureBlocking(false);

    // Bind the server socket to the specified address and port
    InetSocketAddress isa = new InetSocketAddress(fHostAddress, fPort);
    fServerChannel.socket().bind(isa);

    // Register the server socket channel, indicating an interest in 
    // accepting new connections
    fServerChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

    return socketSelector;
    
  }
  
  public void stop() {
    fStopped = true;
    fSelector.wakeup();
  }
    
}
