package com.fumbbl.ffb.client.net;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.ui.GameTitleUpdateTask;
import com.fumbbl.ffb.json.LZString;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandFactory;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandPong;
import com.fumbbl.ffb.util.StringTool;

import javax.websocket.*;
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
		String commandCompressionProperty = fClient.getProperty(CommonProperty.CLIENT_COMMAND_COMPRESSION);
		fCommandCompression = false;
		if (StringTool.isProvided(commandCompressionProperty)) {
			fCommandCompression = Boolean.parseBoolean(commandCompressionProperty);
		}
	}

	@SuppressWarnings("unused")
	@OnOpen
	public void onOpen(Session session, EndpointConfig unused) {
		fSession = session;
	}

	@SuppressWarnings("unused")
	@OnMessage
	public void onBinary(byte[] buf, boolean unused, Session ignored) {
		this.onMessage(new String(buf, StandardCharsets.UTF_8));
	}

	@OnMessage
	public void onMessage(String pTextMessage) {

		if (!StringTool.isProvided(pTextMessage) || !isOpen()) {
			return;
		}

		@SuppressWarnings("deprecation") JsonValue jsonValue = JsonValue
			.readFrom(fCommandCompression ? LZString.decompressFromUTF16(pTextMessage) : pTextMessage);

		synchronized (this) {
			if (fClient.getGame().getRules().isInitialized()) {
				handleNetCommand(fNetCommandFactory.forJsonValue(fClient.getGame().getRules(), jsonValue));
			} else {
				boolean repeat = true;
				int retries = 3;
				while (repeat && retries > 0) {
					try {
						handleNetCommand(fNetCommandFactory.forJsonValue(fClient.getGame().getRules(), jsonValue));
						repeat = false;
					} catch (NullPointerException npe) {
						if (--retries > 0) {
							fClient.logError(0, "Retrying after npe");
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// ignored
							}
						} else {
							fClient.logWithOutGameId(npe);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	@OnClose
	public void onClose(Session ignoredUnused, CloseReason ignored) {
		fClient.getUserInterface().getStatusReport().reportSocketClosed();
		fCloseLatch.countDown();
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return fCloseLatch.await(duration, unit);
	}

	public void send(NetCommand pCommand) throws IOException {

		if ((pCommand == null) || !isOpen()) {
			return;
		}

		JsonValue jsonValue = pCommand.toJsonValue();
		if (jsonValue == null) {
			return;
		}

		String textMessage = jsonValue.toString();
		if (fCommandCompression) {
			textMessage = LZString.compressToUTF16(textMessage);
		}

		if (!StringTool.isProvided(textMessage)) {
			return;
		}

		fSession.getAsyncRemote().sendBinary(
			ByteBuffer.wrap(textMessage.getBytes(StandardCharsets.UTF_8)),
			new FfbSendHandler(fClient, jsonValue.toString())
		);

	}

	public boolean isOpen() {
		return ((fSession != null) && fSession.isOpen());
	}

	private void handleNetCommand(NetCommand netCommand) {
		if (netCommand != null) {
			if (NetCommandId.SERVER_PONG == netCommand.getId()) {
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

	private static class FfbSendHandler implements SendHandler {
		private final FantasyFootballClient client;
		private final String message;

		public FfbSendHandler(FantasyFootballClient client, String message) {
			this.client = client;
			this.message = message;
		}

		@Override
		public void onResult(SendResult sendResult) {
			if (!sendResult.isOK()) {
				client.logError("Could not send message: " + message);
				if (sendResult.getException() != null) {
					client.logWithOutGameId(sendResult.getException());
				}
			}
		}
	}
}