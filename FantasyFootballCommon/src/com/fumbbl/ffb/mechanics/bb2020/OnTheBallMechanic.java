package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class OnTheBallMechanic extends com.fumbbl.ffb.mechanics.OnTheBallMechanic {
	@Override
	public Set<Player<?>> findPassBlockers(Game game, Team pTeam, boolean pCheckCanReach) {
		Set<Player<?>> passBlockers = new HashSet<>();
		Player<?>[] players = pTeam.getPlayers();
		for (Player<?> player : players) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (player.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses) && playerState.hasTacklezones()) {
				passBlockers.add(player);
			}
		}
		return passBlockers;
	}

	@Override
	public boolean validPassBlockMove(Game game, ActingPlayer actingPlayer, FieldCoordinate fromCoordinate, FieldCoordinate toCoordinate,
	                                  Set<FieldCoordinate> validPassBlockCoordinates, boolean canStillJump, int distance) {
		return distance + actingPlayer.getCurrentMove() <= 3;
	}

	@Override
	public String displayStringPassInterference() {
		return "On The Ball";
	}

	@Override
	public String[] passInterferenceDialogDescription() {
		return new String[] { "You may move your players with ON THE BALL skill up to 3 squares."};
	}

	@Override
	public String passInterferenceStatusDescription() {
		return "Waiting for coach to move players with \"On The Ball\".";
	}

	@Override
	public String displayStringKickOffInterference() {
		return displayStringPassInterference();
	}
}
