package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.util.pathfinding.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.JumpMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPassing;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class OnTheBallMechanic extends com.fumbbl.ffb.mechanics.OnTheBallMechanic {

	@Override
	public Set<Player<?>> findPassBlockers(Game game, Team pTeam, boolean pCheckCanReach) {
		Set<Player<?>> passBlockers = new HashSet<>();
		Player<?>[] players = pTeam.getPlayers();
		com.fumbbl.ffb.mechanics.JumpMechanic mechanic = (JumpMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.JUMP.name());
		Set<FieldCoordinate> validPassBlockEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
		for (Player<?> player : players) {
			if (player.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)) {
				PlayerState playerState = game.getFieldModel().getPlayerState(player);
				FieldCoordinate startPosition = game.getFieldModel().getPlayerCoordinate(player);
				if (!pCheckCanReach || (playerState.hasTacklezones()
					&& ArrayTool.isProvided(PathFinderWithPassBlockSupport.INSTANCE.allowPassBlockMove(game, player, startPosition, 3, mechanic.canJump(game, player, startPosition), validPassBlockEndCoordinates)))) {
					passBlockers.add(player);
				}
			}
		}
		return passBlockers;
	}

	@Override
	public boolean validPassBlockMove(Game game, ActingPlayer actingPlayer, FieldCoordinate fromCoordinate, FieldCoordinate toCoordinate,
	                                  Set<FieldCoordinate> validPassBlockCoordinates, boolean canStillJump, int distance) {
		return (validPassBlockCoordinates.contains(toCoordinate)
			|| ArrayTool.isProvided(PathFinderWithPassBlockSupport.INSTANCE.allowPassBlockMove(game,
			actingPlayer.getPlayer(), toCoordinate, 3 - distance - actingPlayer.getCurrentMove(),
			canStillJump, validPassBlockCoordinates)));
	}

	@Override
	public String displayStringPassInterference() {
		return "Pass Block";
	}

	@Override
	public String[] passInterferenceDialogDescription() {
		return new String[] { "You may move your players with PASS BLOCK skill up to 3 squares.",
			"The move must end in a square where the player can intercept or put a TZ on thrower or catcher." };
	}

	@Override
	public String passInterferenceStatusDescription() {
		return "Waiting for coach to move pass blockers.";
	}

	@Override
	public String displayStringKickOffInterference() {
		return "Kick-Off Return";
	}

	@Override
	public boolean hasReachedValidPosition(Game game, Player<?> player) {
		Set<FieldCoordinate> validEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(game);
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		return validEndCoordinates.contains(playerCoordinate);
	}
}
