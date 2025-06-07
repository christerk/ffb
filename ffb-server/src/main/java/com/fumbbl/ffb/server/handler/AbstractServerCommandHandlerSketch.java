package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.commands.ClientSketchCommand;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ServerSketchManager;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import org.eclipse.jetty.websocket.api.Session;

public abstract class AbstractServerCommandHandlerSketch<C extends ClientSketchCommand, S extends ServerCommand>
	extends ServerCommandHandler {
	protected final ServerSketchManager sketchManager;
	protected final ReplaySessionManager replaySessionManager;

	protected AbstractServerCommandHandlerSketch(FantasyFootballServer pServer) {
		super(pServer);
		sketchManager = getServer().getSketchManager();
		replaySessionManager = getServer().getReplaySessionManager();
	}

	@Override
	public final boolean handleCommand(ReceivedCommand receivedCommand) {
		//noinspection unchecked
		C command = (C) receivedCommand.getCommand();
		Session session = receivedCommand.getSession();
		if (replaySessionManager.has(session)) {
			if (command.requiresControl() && !replaySessionManager.hasControl(session))  {
				return true;
			}
			updateSketchManager(session, command);
			replaySessionManager.otherSessions(session)
				.forEach(otherSession ->
					getServer().getCommunication().sendToReplaySession(otherSession, createServerCommand(otherSession, command)));
		}
		return true;
	}

	protected abstract void updateSketchManager(Session session, C command);

	protected abstract S createServerCommand(Session session, C command);
}
