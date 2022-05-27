package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.util.UtilActingPlayer;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerResetStackLive extends TalkHandler {

	public TalkHandlerResetStackLive() {
		super("/reset_stack", 0, new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		UtilActingPlayer.changeActingPlayer(gameState.getGame(), null, null, false);

		gameState.getStepStack().clear();

		SequenceGeneratorFactory factory = gameState.getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		Select generator = (Select) factory.forName(SequenceGenerator.Type.Select.name());

		generator.pushSequence(new Select.SequenceParams(gameState, true));

		server.getCommunication().sendPlayerTalk(gameState, null, "Reset step stack.");
	}
}
