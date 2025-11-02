package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.step.DeferredCommand;

public class AnimalSavageryEndTurnCommand implements DeferredCommand {
	private final Game game;

	public AnimalSavageryEndTurnCommand(Game game) {
		this.game = game;
	}

	public void execute() {
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (actingPlayer.getPlayerAction()) {
			case BLITZ:
			case BLITZ_MOVE:
			case KICK_EM_BLITZ:
				game.getTurnData().setBlitzUsed(true);
				break;
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				game.getTurnData().setKtmUsed(true);
				break;
			case PASS:
			case PASS_MOVE:
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				game.getTurnData().setPassUsed(true);
				break;
			case HAND_OVER:
			case HAND_OVER_MOVE:
				game.getTurnData().setHandOverUsed(true);
				break;
			case FOUL:
			case FOUL_MOVE:
				if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
					game.getTurnData().setFoulUsed(true);
				}
				break;
			default:
				break;
		}
		game.setPassCoordinate(null);
	}

}


