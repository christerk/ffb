package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class TtmMechanic extends com.fumbbl.ffb.mechanics.TtmMechanic {

	public Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower) {
		List<Player<?>> throwablePlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);
		Player<?>[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, pThrower.getTeam(), throwerCoordinate,
			false);
		for (Player<?> adjacentPlayer : adjacentPlayers) {

			if (adjacentPlayer.canBeThrown()) {
				throwablePlayers.add(adjacentPlayer);
			}
		}
		return throwablePlayers.toArray(new Player[0]);
	}

	@Override
	public boolean canBeThrown(Game game, Player<?> player) {
		return player.canBeThrown()
			&& game.getFieldModel().getPlayerState(player).hasTacklezones()
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public boolean canBeKicked(Game game, Player<?> player) {
		return player.hasSkillProperty(NamedProperties.canBeKicked)
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public int minimumRoll(PassingDistance distance, Set<PassModifier> modifiers) {
		return Math.max(2, 2 + modifierSum(distance, modifiers) );
	}

	@Override
	public int modifierSum(PassingDistance distance, Set<PassModifier> modifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : modifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return modifierTotal - distance.getModifier2016();
	}

	@Override
	public boolean isValidEndScatterCoordinate(Game game, FieldCoordinate coordinate) {
		return game.getFieldModel().getPlayer(coordinate) == null;
	}

	@Override
	public boolean handleKickLikeThrow() {
		return false;
	}

	@Override
	public boolean isKtmAvailable(TurnData turnData) {
		return !turnData.isBlitzUsed();
	}
}
