package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;

public class TalkHandlerInjuryLive extends TalkHandlerInjury {

	public TalkHandlerInjuryLive() {
		super(new DecoratingCommandAdapter(), TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE, TalkRequirements.Privilege.EDIT_STATE);
	}

	@Override
	protected void applyInjury(FantasyFootballServer server, GameState gameState, RosterPlayer player, SeriousInjury lastingInjury) {
		Game game = gameState.getGame();
		Team team = player.getTeam();

		PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
		String info = null;
		if (lastingInjury == null) {
			info = "Removing injuries from player " + player.getName() + ".";

			playerResult.setSeriousInjuryDecay(null);
			playerResult.setSeriousInjury(null);
		} else {
			if (playerResult.getSeriousInjury() == null) {
				playerResult.setSeriousInjury(lastingInjury);
				info = "Player " + player.getName() + " suffers a injury: " + lastingInjury.getName() + ".";
			} else if (playerResult.getSeriousInjuryDecay() == null) {
				playerResult.setSeriousInjuryDecay(lastingInjury);
				info = "Player " + player.getName() + " suffers a second injury: " + lastingInjury.getName() + ".";
			}
		}
		server.getCommunication().sendAddPlayer(gameState, team.getId(), player,
			game.getFieldModel().getPlayerState(player), playerResult);
		if (info != null) {
			server.getCommunication().sendPlayerTalk(gameState, null, info);
		}
	}
}
