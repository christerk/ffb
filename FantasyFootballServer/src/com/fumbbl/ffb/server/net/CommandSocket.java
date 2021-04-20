package com.fumbbl.ffb.server.net;

import java.nio.charset.Charset;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.LZString;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandFactory;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.handler.IReceivedCommandHandler;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandSocketClosed;

/**
 * 
 * @author Kalimar
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class CommandSocket {

	private IReceivedCommandHandler fCommandHandler;
	private NetCommandFactory fNetCommandFactory;
	private boolean fCommandCompression;
	private FantasyFootballServer server;

	public CommandSocket(FantasyFootballServer server, boolean commandCompression) {
		this.server = server;
		fCommandHandler = server.getCommunication();
		fNetCommandFactory = new NetCommandFactory(server.getFactorySource());
		fCommandCompression = commandCompression;
	}

	@OnWebSocketMessage
	public void onBinaryMessage(Session pSession, byte buf[], int offset, int length) {
		this.onTextMessage(pSession, new String(buf, offset, length, Charset.forName("UTF8")));
	}

	@OnWebSocketMessage
	public void onTextMessage(Session pSession, String pTextMessage) {

		if ((pSession == null) || (pTextMessage == null) || !pSession.isOpen()) {
			return;
		}

		try {
			String decompressed = fCommandCompression ? LZString.decompressFromUTF16(pTextMessage) : pTextMessage;
			JsonValue jsonValue = JsonValue.readFrom(decompressed);

			long gameId = server.getSessionManager().getGameIdForSession(pSession);
			GameState gameState = server.getGameCache().getGameStateById(gameId);
			Game game = gameState != null ? gameState.getGame() : null;
			IFactorySource source = game != null ? game.getRules() : server.getFactorySource();
			NetCommand netCommand = fNetCommandFactory.forJsonValue(source, jsonValue);
			if (netCommand == null) {
				return;
			}

			ReceivedCommand receivedCommand = new ReceivedCommand(netCommand, pSession);
			fCommandHandler.handleCommand(receivedCommand);
		} catch (Exception e) {
		}

	}

	@OnWebSocketConnect
	public void onConnect(Session pSession) {
		pSession.setIdleTimeout(Long.MAX_VALUE);
	}

	@OnWebSocketClose
	public void onClose(Session pSession, int pCloseCode, String pCloseReason) {
		if (pSession == null) {
			return;
		}
		fCommandHandler.handleCommand(new ReceivedCommand(new InternalServerCommandSocketClosed(), pSession));
	}

}
