package com.balancedbytes.games.ffb.client.net;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
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
@WebSocket(maxMessageSize = 64 * 1024)
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

  @OnWebSocketMessage
  public void onTextMessage(Session pSession, String pTextMessage) {
    if ((pSession == null) || (pTextMessage == null) || !pSession.isOpen()) {
      return;
    }
    JsonValue jsonValue = JsonValue.readFrom(pTextMessage);
    NetCommand netCommand = fNetCommandFactory.forJsonValue(jsonValue);
    if (netCommand == null) {
      return;
    }
    if (NetCommandId.SERVER_PING == netCommand.getId()) {
      ServerCommandPing pingCommand = (ServerCommandPing) netCommand;
      pingCommand.setReceived(System.currentTimeMillis());
      fClient.getClientPingTask().setLastPingReceived(pingCommand.getReceived());
    }
    fClient.getCommunication().handleCommand(netCommand);
  }

  @OnWebSocketClose
  public void onClose(int pCloseCode, String pCloseReason) {
    System.out.printf("Connection closed: %d - %s%n", pCloseCode, pCloseReason);
    fSession = null;
    fCloseLatch.countDown();
  }

  @OnWebSocketConnect
  public void onConnect(Session pSession) {
    pSession.setIdleTimeout(Long.MAX_VALUE);
    System.out.printf("Got connect: %s%n", pSession);
    fSession = pSession;
  }

  public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
    return fCloseLatch.await(duration, unit);
  }

  public Future<Void> send(NetCommand pCommand) {
    if ((pCommand == null) || !isOpen()) {
      return null;
    }
    JsonValue jsonValue = pCommand.toJsonValue();
    if (jsonValue == null) {
      return null;
    }
    String textMessage = jsonValue.toString();
    if (textMessage == null) {
      return null;
    }
    return fSession.getRemote().sendStringByFuture(textMessage);
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
