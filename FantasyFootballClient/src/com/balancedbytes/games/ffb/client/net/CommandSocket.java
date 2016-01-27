package com.balancedbytes.games.ffb.client.net;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.json.LZString;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class CommandSocket implements WebSocket.OnTextMessage {

  private FantasyFootballClient fClient;
  private NetCommandFactory fNetCommandFactory;
  private Connection fConnection;
  private boolean fCommandCompression;

  private final CountDownLatch fCloseLatch;

  public CommandSocket(FantasyFootballClient pClient) {
    fClient = pClient;
    fNetCommandFactory = new NetCommandFactory();
    fCloseLatch = new CountDownLatch(1);
    String commandCompressionProperty = null;
    if (fClient != null) {
      commandCompressionProperty = fClient.getProperty(IClientProperty.CLIENT_COMMAND_COMPRESSION);
    }
    fCommandCompression = false;
    if (StringTool.isProvided(commandCompressionProperty)) {
      fCommandCompression = Boolean.parseBoolean(commandCompressionProperty);
    }
  }

  @Override
  public void onOpen(Connection pConnection) {
    fConnection = pConnection;
    System.out.printf("Got connect: %s%n", fConnection);
    fConnection.setMaxIdleTime(Integer.MAX_VALUE);
    fConnection.setMaxTextMessageSize(64 * 1024);
  }

  @Override
  public void onMessage(String pTextMessage) {

    if (!StringTool.isProvided(pTextMessage) || !isOpen()) {
      return;
    }

    JsonValue jsonValue = JsonValue.readFrom(
      fCommandCompression ? LZString.decompressFromUTF16(pTextMessage) : pTextMessage
    );

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

  @Override
  public void onClose(int pCloseCode, String pCloseReason) {
    System.out.printf("Connection closed: %d - %s%n", pCloseCode, pCloseReason);
    fClient.getUserInterface().getStatusReport().reportSocketClosed();
    fConnection = null;
    fCloseLatch.countDown();
  }

  public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
    return fCloseLatch.await(duration, unit);
  }

  public boolean send(NetCommand pCommand) throws IOException {

    if ((pCommand == null) || !isOpen()) {
      return false;
    }

    JsonValue jsonValue = pCommand.toJsonValue();
    if (jsonValue == null) {
      return false;
    }

    String textMessage = jsonValue.toString();
    if (fCommandCompression) {
      textMessage = LZString.compressToUTF16(textMessage);
    }

    if (!StringTool.isProvided(textMessage)) {
      return false;
    }

    fConnection.sendMessage(textMessage);
    return true;

  }

  public boolean isOpen() {
    return ((fConnection != null) && fConnection.isOpen());
  }

}