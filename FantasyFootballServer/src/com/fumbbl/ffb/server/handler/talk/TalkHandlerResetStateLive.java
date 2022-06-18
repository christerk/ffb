package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.bb2020.pass.state.PassState;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.util.UtilActingPlayer;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerResetStateLive extends TalkHandler {

	public TalkHandlerResetStateLive() {
		super("/reset_state", 0, new IdentityCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		Game game = gameState.getGame();
		UtilActingPlayer.changeActingPlayer(game, null, null, false);

		gameState.getStepStack().clear();

		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

		Select generator = (Select) factory.forName(SequenceGenerator.Type.Select.name());

		generator.pushSequence(new Select.SequenceParams(gameState, true));

		game.setTurnMode(TurnMode.REGULAR);
		game.setLastTurnMode(null);
		gameState.setPassState(new PassState());
		gameState.setBlitzTurnState(null);

		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null) {
			String playerId = targetSelectionState.getSelectedPlayerId();
			if (playerId != null) {
				Player<?> player = game.getPlayerById(playerId);
				if (player != null) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState != null) {
						game.getFieldModel().setPlayerState(player, playerState.removeAllTargetSelections());
					}
				}
			}
			game.getFieldModel().setTargetSelectionState(null);
		}
		server.getCommunication().sendPlayerTalk(gameState, null, "Reset done:\n  - Acting player" +
			"\n  - Player action\n  - Step stack cleared and init sequence pushed\n  - TurnMode set to regular\n  - Last TurnMode deleted" +
			"\n  - New PassState set\n  - Target selection reset (blitz and gaze)\n  - Blitz turn data deleted");
	}
}
