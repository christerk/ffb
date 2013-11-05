package com.balancedbytes.games.ffb.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.client.ui.LogComponent;
import com.balancedbytes.games.ffb.net.IConnectionListener;
import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;
import com.balancedbytes.games.ffb.util.StringTool;

public class NioClient implements Runnable {
	
	private static final int _SELECT_TIMEOUT_MS = 10000;

  // set true to stop the client
  private boolean fStopped;
  
	// the host:port combination to connect to
	private InetAddress fHostAddress;

	private int fPort;

	// the selector we'll be monitoring
	private Selector fSelector;

	// the buffer into which we'll read data when it's available
	private ByteBuffer fReadBuffer;

	// maps a SocketChannel to a list of ByteBuffer instances
	private List<ByteBuffer> fPendingData;

	private INetCommandHandler fCommandHandler;

	private SocketChannel fSocketChannel;
	
	private IConnectionListener fConnectionListener;
	
	private FantasyFootballClient fClient;
	
	private NetCommandFactory fNetCommandFactory;

	public NioClient(FantasyFootballClient pClient) throws IOException {
	  
		fClient = pClient;
	  fHostAddress = getClient().getServerHost();
    fPort = getClient().getServerPort();
		fCommandHandler = getClient().getCommunication();
		fPendingData = Collections.synchronizedList(new LinkedList<ByteBuffer>());
		fReadBuffer = ByteBuffer.allocate(16 * 1024);
		fReadBuffer.clear();
		fSelector = initSelector();
	  fNetCommandFactory = new NetCommandFactory();
		
		//TODO: Initialize fDelayProxy instance if enabled. Leave as null to disable it.
		// 50 to 2000 ms delay is pretty unenjoyable, but should be playable. A large spread is necessary to generate packet bursts.
		//fDelayProxy = new DelayProxy(this, 50, 2000); // Use 50-2000 ms delay.
		
	}
	
	public FantasyFootballClient getClient() {
    return fClient;
  }

	public InetAddress getHostAddress() {
    return fHostAddress;
  }
	
	public int getPort() {
    return fPort;
  }
	
  public void send(NetCommand pChatCommand) throws IOException {
    // queue the data we want written
    synchronized (fPendingData) {
    	fPendingData.add(ByteBuffer.wrap(pChatCommand.toBytes()));
    }
    // wake up our selecting thread so it can make the required changes
    fSelector.wakeup();
  }	
	
	public void run() {

    while (true) {

			try {

        SelectionKey key = null;
        if (fSocketChannel != null) {
          key = fSocketChannel.keyFor(fSelector);
          if (key == null) {
            fSocketChannel.register(fSelector, SelectionKey.OP_CONNECT);
          } else if (!key.isValid()) {
            continue;
          } else {
            synchronized (fPendingData) {
              if (!fPendingData.isEmpty() && (key.interestOps() == SelectionKey.OP_READ)) {
                key.interestOps(SelectionKey.OP_WRITE);
              }
            }
          }
        }
        
        // wait for an event one of the registered channels
        fSelector.select(_SELECT_TIMEOUT_MS);
        
        if (fStopped) {
          break;
        }

        // Iterate over the set of keys for which events are available
        Iterator<SelectionKey> selectedKeys = fSelector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {

          key = selectedKeys.next();
          selectedKeys.remove();

          if (!key.isValid()) {
            continue;
          }

          // Check what event is available and deal with it
          if (key.isConnectable()) {
            finishConnection(key);
          } else if (key.isReadable()) {
            read(key);
          } else if (key.isWritable()) {
            write(key);
          }
          
        }
			
      } catch (IOException pIoe) {
      	
      	// TODO: do we need to initiate a new connection? what does the server do?
      	
      	LogComponent log = getClient().getUserInterface().getLog();
        log.append(null, null, null);
      	if (StringTool.isProvided(pIoe.getMessage())) {
          log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, "Connection Problem:");
          log.append(null, null, null);
          log.append(ParagraphStyle.INDENT_1, TextStyle.NONE, StringTool.print(pIoe.getMessage()));
      	} else {
          log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, "Unknown Connection Problem");
      	}
        log.append(null, null, null);
        log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, "retrying ...");
        log.append(null, null, null);
        
      }
      
		}
    
	}

	private void read(SelectionKey pSelectionKey) throws IOException {

		SocketChannel socketChannel = (SocketChannel) pSelectionKey.channel();

		// Attempt to read off the channel
		int nrBytesRead = 0;
		try {
		  nrBytesRead = socketChannel.read(fReadBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			pSelectionKey.cancel();
			socketChannel.close();
			return;
		}

		if (nrBytesRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			pSelectionKey.channel().close();
			pSelectionKey.cancel();
			return;
		}
		
    byte[] readBytes = fReadBuffer.array();
    int totalReadBytes = fReadBuffer.position();
    int offset = 0;
    while (offset < totalReadBytes) {
      byte[] cmdBytes = fNetCommandFactory.nextCommandBytes(readBytes, offset, totalReadBytes);
      if (cmdBytes != null) {
        NetCommand netCommand = fNetCommandFactory.fromBytes(cmdBytes);
        netCommand.setSender(socketChannel);
        if (NetCommandId.SERVER_PING == netCommand.getId()) {
          ServerCommandPing pingCommand = (ServerCommandPing) netCommand; 
          pingCommand.setReceived(System.currentTimeMillis());
          getClient().getClientPingTask().setLastPingReceived(pingCommand.getReceived());
        }
        fCommandHandler.handleNetCommand(netCommand);
        offset += netCommand.size();
      } else {
        break;
      }
    }

		fReadBuffer.clear();
    for (int i = offset; i < totalReadBytes; i++) {
      fReadBuffer.put(readBytes[i]);
    }

	}
	
  private void removeChannel(SocketChannel pSocketChannel) throws IOException {
    if (pSocketChannel != null) {
      pSocketChannel.close();
      SelectionKey selectionKey = pSocketChannel.keyFor(fSelector);
      if (selectionKey != null) {
        selectionKey.cancel();
        fSelector.wakeup();
      }
    }
  }

	private void write(SelectionKey pSelectionKey) throws IOException {

    SocketChannel socketChannel = (SocketChannel) pSelectionKey.channel();

    boolean writeFinished = true;
    ByteBuffer writeBuffer = null;

    synchronized (fPendingData) {
      if (!fPendingData.isEmpty()) {
        writeBuffer = fPendingData.get(0);
      }
    }

    if (writeBuffer != null) {
      
      try {
        socketChannel.write(writeBuffer);
      } catch (IOException e) {
        // the remote forcibly closed the connection,
        // cancel the selection key and close the channel.
        removeChannel(socketChannel);
        return;
      }
      
      writeFinished = !writeBuffer.hasRemaining();
      if (writeFinished) {    
        synchronized (fPendingData) {
          fPendingData.remove(0);
          writeFinished = fPendingData.isEmpty();
        }
      }
      
    }
    
    if ((writeFinished) && (pSelectionKey.interestOps() == SelectionKey.OP_WRITE)) {
      pSelectionKey.interestOps(SelectionKey.OP_READ);
    }

    fSelector.wakeup();

	}

	private void finishConnection(SelectionKey pSelectionKey) throws IOException {

	  boolean connectionSuccessful = true;
	  
		SocketChannel socketChannel = (SocketChannel) pSelectionKey.channel();

		// finish the connection,
		// if the connection operation failed this will raise an IOException
		try {
			socketChannel.finishConnect();
		} catch (IOException ioe) {
			connectionSuccessful = false;
		}
		
		if (connectionSuccessful) {

		  pSelectionKey.interestOps(SelectionKey.OP_READ);
		  
  		// wake up our selecting thread so it can make the required changes
  		fSelector.wakeup();
  		
		} else {
		  
      pSelectionKey.cancel();

		}
		
		if (fConnectionListener != null) {
		  fConnectionListener.connectionEstablished(connectionSuccessful);
		}

	}

	public void initiateConnection(IConnectionListener pConnectionListener) throws IOException {
		
		fConnectionListener = pConnectionListener;
			
		// create a non-blocking socket channel
		fSocketChannel = SocketChannel.open();
		fSocketChannel.configureBlocking(false);

		// kick off connection establishment
		fSocketChannel.connect(new InetSocketAddress(fHostAddress, fPort));

		// wake up our selecting thread so it can make the required changes
		fSelector.wakeup();

	}

	private Selector initSelector() throws IOException {

		// create a new selector
		return SelectorProvider.provider().openSelector();

	}
	
  public void stop() throws IOException {
  	if (!fStopped) {
      fStopped = true;
      removeChannel(fSocketChannel);
  	}
  }
  
  public boolean isRunning() {
    return !fStopped;
  }

}