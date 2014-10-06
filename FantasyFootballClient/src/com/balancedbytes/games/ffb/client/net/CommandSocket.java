package com.balancedbytes.games.ffb.client.net;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class CommandSocket {

  private FantasyFootballClient fClient;
  private NetCommandFactory fNetCommandFactory;
  private Session fSession;

  private final CountDownLatch fCloseLatch;

  public CommandSocket(FantasyFootballClient pClient) {
    fClient = pClient;
    fNetCommandFactory = new NetCommandFactory();
    fCloseLatch = new CountDownLatch(1);
  }
  
  @OnWebSocketConnect
  public void onConnect(Session pSession) {
    fSession = pSession;
    fSession.getPolicy().setIdleTimeout(Long.MAX_VALUE);
    fSession.getPolicy().setMaxTextMessageSize(64 * 1024);
  }

  @OnWebSocketMessage
  public void onMessage(String pTextMessage) {
    
    if ((pTextMessage == null) || !isOpen()) {
      return;
    }
    
    // old:
    JsonValue jsonValue = JsonValue.readFrom(pTextMessage);
    
    // new:
    /*
    JsonValue jsonValue = null;
    try {
      jsonValue = UtilJson.inflateFromBase64(pTextMessage);
    } catch (IOException pIoException) {
      return;
    }
    */

    NetCommand netCommand = fNetCommandFactory.forJsonValue(jsonValue);
    if (netCommand == null) {
      return;
    }
    
    if (NetCommandId.SERVER_PING == netCommand.getId()) {
      ServerCommandPing pingCommand = (ServerCommandPing) netCommand;
      pingCommand.setReceived(System.currentTimeMillis());
      fClient.getClientPingTask().setLastPingReceived(pingCommand.getReceived());
    // } else {
      // System.out.println("Received: " + netCommand.getId().getName() + " (" + pTextMessage.length() + " bytes)");
    }
    
    fClient.getCommunication().handleCommand(netCommand);
    
  }

  @OnWebSocketClose
  public void onClose(int pCloseCode, String pCloseReason) {
    fSession = null;
    fCloseLatch.countDown();
  }

  public void close() {
    if (isOpen()) {
      fSession.close();
    } else {
      fCloseLatch.countDown();
    }
  }

  public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
    return fCloseLatch.await(duration, unit);
  }

  public boolean send(NetCommand pCommand) throws IOException {
    
    if ((pCommand == null) || !isOpen()) {
      return false;
    }
    
    // old:
    JsonValue jsonValue = pCommand.toJsonValue();
    if (jsonValue == null) {
      return false;
    }
    
    String message = jsonValue.toString();
    if (message == null) {
      return false;
    }

    // new:
    /*
    String message = UtilJson.deflateToBase64(pCommand.toJsonValue());
    if (message == null) {
      return false;
    }
    */
    
    fSession.getRemote().sendString(message);
    return true;
    
  }

  public boolean isOpen() {
    return ((fSession != null) && fSession.isOpen());
  }

  // LogComponent log = getClient().getUserInterface().getLog();
  // log.append(null, null, null);
  // if (StringTool.isProvided(pIoe.getMessage())) {
  // log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, "Connection Problem:");
  // log.append(null, null, null);
  // log.append(ParagraphStyle.INDENT_1, TextStyle.NONE,
  // StringTool.print(pIoe.getMessage()));
  // } else {
  // log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD,
  // "Unknown Connection Problem");
  // }
  // log.append(null, null, null);
  // log.append(ParagraphStyle.INDENT_0, TextStyle.BOLD, "retrying ...");
  // log.append(null, null, null);

}
