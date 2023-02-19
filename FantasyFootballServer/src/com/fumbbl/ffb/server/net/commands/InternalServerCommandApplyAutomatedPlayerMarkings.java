package com.fumbbl.ffb.server.net.commands;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class InternalServerCommandApplyAutomatedPlayerMarkings extends InternalServerCommand {

	private final AutoMarkingConfig autoMarkingConfig;
	private final GameState gameState;
	private final Session session;

	public InternalServerCommandApplyAutomatedPlayerMarkings(AutoMarkingConfig autoMarkingConfig, GameState gameState, Session session) {
		this.autoMarkingConfig = autoMarkingConfig;
		this.gameState = gameState;
		this.session = session;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_APPLY_AUTOMATIC_PLAYER_MARKINGS;
	}

	public AutoMarkingConfig getAutoMarkingConfig() {
		return autoMarkingConfig;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Session getSession() {
		return session;
	}
}
