package com.fumbbl.ffb.client.net;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.ui.GameTitleUpdateTask;
import com.fumbbl.ffb.json.LZString;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandFactory;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandPong;
import com.fumbbl.ffb.util.StringTool;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Kalimar
 */
@ClientEndpoint
public class CommandEndpoint {

	private final FantasyFootballClient fClient;
	private final NetCommandFactory fNetCommandFactory;
	private boolean fCommandCompression;
	private Session fSession;

	private final CountDownLatch fCloseLatch;

	public CommandEndpoint(FantasyFootballClient pClient) {
		fClient = pClient;
		fNetCommandFactory = new NetCommandFactory(pClient.getFactorySource());
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

	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		fSession = session;
	}

	@OnMessage
	public void onBinary(byte[] buf, boolean last, Session session) {
		this.onMessage(new String(buf, 0, buf.length, StandardCharsets.UTF_8));
	}

	@OnMessage
	public void onMessage(String pTextMessage) {

		if (!StringTool.isProvided(pTextMessage) || !isOpen()) {
			return;
		}

		JsonValue jsonValue = JsonValue
				.readFrom(fCommandCompression ? LZString.decompressFromUTF16(pTextMessage) : pTextMessage);

		handleNetCommand(fNetCommandFactory.forJsonValue(fClient.getGame().getRules(), jsonValue));
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		fClient.getUserInterface().getStatusReport().reportSocketClosed();
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

		// fSession.getAsyncRemote().sendText(textMessage);
		fSession.getAsyncRemote().sendBinary(ByteBuffer.wrap(textMessage.getBytes(StandardCharsets.UTF_8)));
		return true;

	}

	public boolean isOpen() {
		return ((fSession != null) && fSession.isOpen());
	}

	private void handleNetCommand(NetCommand netCommand) {
		if (netCommand == null) {
		} else if (NetCommandId.SERVER_PONG == netCommand.getId()) {
			ServerCommandPong pongCommand = (ServerCommandPong) netCommand;
			if (pongCommand.getTimestamp() > 0) {
				long received = System.currentTimeMillis();
				GameTitle gameTitle = new GameTitle();
				gameTitle.setPingTime(received - pongCommand.getTimestamp());
				fClient.getUserInterface().invokeLater(new GameTitleUpdateTask(fClient, gameTitle));
			}
		} else {
			fClient.getCommunication().handleCommand(netCommand);
		}
	}

}