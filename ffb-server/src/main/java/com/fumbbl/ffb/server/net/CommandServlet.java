package com.fumbbl.ffb.server.net;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class CommandServlet extends WebSocketServlet implements WebSocketCreator {

	private FantasyFootballServer fServer;

	public CommandServlet(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(10000);
		factory.setCreator(this);
	}

	public Object createWebSocket(ServletUpgradeRequest pRequest, ServletUpgradeResponse pResponse) {
		String commandCompressionProperty = null;
		if (fServer != null) {
			commandCompressionProperty = fServer.getProperty(IServerProperty.SERVER_COMMAND_COMPRESSION);
		}
		boolean commandCompression = false;
		if (StringTool.isProvided(commandCompressionProperty)) {
			commandCompression = Boolean.parseBoolean(commandCompressionProperty);
		}
		return new CommandSocket(fServer, commandCompression);
	}

}