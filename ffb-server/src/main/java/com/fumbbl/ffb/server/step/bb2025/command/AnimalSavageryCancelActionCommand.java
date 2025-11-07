package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.IStep;

public class AnimalSavageryCancelActionCommand implements DeferredCommand {

	public void execute(IStep step) {
		Game game = step.getGameState().getGame();
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

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		//TODO
		return null;
	}

	@Override
	public JsonValue toJsonValue() {
		//TODO
		return null;
	}

}


