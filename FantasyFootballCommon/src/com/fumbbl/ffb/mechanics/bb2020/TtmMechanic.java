package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.PassModifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class TtmMechanic extends com.fumbbl.ffb.mechanics.TtmMechanic {

	public Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower) {

		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);

		return Arrays.stream(fieldModel.findAdjacentCoordinates(throwerCoordinate, FieldCoordinateBounds.FIELD,
			1, false))
			.map(fieldModel::getPlayer)
			.filter(Objects::nonNull)
			.filter(player -> canBeThrown(pGame, player)).toArray(Player[]::new);
	}

	@Override
	public boolean canBeThrown(Game game, Player<?> player) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return player.canBeThrown()
			&& !playerState.isRooted()
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public boolean canBeKicked(Game game, Player<?> player) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		return player.canBeThrown()
			&& playerState.getBase() == PlayerState.STANDING
			&& !playerState.isRooted()
			&& game.getActingTeam() == player.getTeam();
	}

	@Override
	public int minimumRoll(PassingDistance distance, Set<PassModifier> modifiers) {
		return 2 + modifierSum(distance, modifiers);
	}

	@Override
	public int modifierSum(PassingDistance distance, Set<PassModifier> modifiers) {
		return calculateModifiers(modifiers) + distance.getModifier2020();
	}

	@Override
	public boolean isValidEndScatterCoordinate(Game game, FieldCoordinate coordinate) {
		return true;
	}

	@Override
	public boolean handleKickLikeThrow() {
		return true;
	}

	@Override
	public boolean isKtmAvailable(TurnData turnData) {
		return !turnData.isKtmUsed();
	}

	@Override
	public boolean canThrow(Player<?> player) {
		return player.hasSkillProperty(NamedProperties.canThrowTeamMates) && player.getStrengthWithModifiers() >= 5;
	}

	private int calculateModifiers(Collection<PassModifier> pPassModifiers) {
		int modifierTotal = 0;
		for (PassModifier passModifier : pPassModifiers) {
			modifierTotal += passModifier.getModifier();
		}
		return modifierTotal;
	}
}
