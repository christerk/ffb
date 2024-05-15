package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.PlayerInteractionResult;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Set;

public abstract class LogicModule {
	protected final FantasyFootballClient client;

	public LogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	public void perform(Player<?> player, ClientAction action) {
		if (availableActions().contains(action)) {
			performAvailableAction(player, action);
		} else {
			client.logError("Unsupported action " + action.name() + " in logic module " + this.getClass().getCanonicalName());
		}
	}

	public boolean isHypnoticGazeActionAvailable(boolean declareAtStart, Player<?> player, ISkillProperty property) {
		Game game = client.getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		return ((mechanic.declareGazeActionAtStart() == declareAtStart)
			&& mechanic.isGazeActionAllowed(game.getTurnMode(), actingPlayer.getPlayerAction())
			&& UtilPlayer.canGaze(game, player, property));
	}

	public boolean isTreacherousAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isTreacherousAvailable(actingPlayer.getPlayer());
	}

	protected boolean isTreacherousAvailable(Player<?> player) {
		Game game = client.getGame();
		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canStabTeamMateForBall)
			&& Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getActingTeam(), game.getFieldModel().getPlayerCoordinate(player)))
			.anyMatch(adjacentPlayer -> UtilPlayer.hasBall(game, adjacentPlayer));
	}

	public boolean isCatchOfTheDayAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isCatchOfTheDayAvailable(actingPlayer.getPlayer());
	}

	protected boolean isCatchOfTheDayAvailable(Player<?> player) {
		Game game = client.getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		FieldCoordinate ballCoordinate = game.getFieldModel().getBallCoordinate();

		return UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.canGetBallOnGround)
			&& game.getFieldModel().isBallMoving() && playerCoordinate.distanceInSteps(ballCoordinate) <= 3;
	}


	public boolean isWisdomAvailable(ActingPlayer actingPlayer) {
		return !actingPlayer.hasActed() && isWisdomAvailable(actingPlayer.getPlayer());
	}

	protected boolean isWisdomAvailable(Player<?> player) {
		Game game = client.getGame();

		Set<Skill> ownedSkills = player.getSkillsIncludingTemporaryOnes();

		boolean canGainSkill = Constant.getGrantAbleSkills(game.getFactory(FactoryType.Factory.SKILL)).stream()
			.map(SkillWithValue::getSkill)
			.anyMatch(skillClass -> !ownedSkills.contains(skillClass));

		return canGainSkill && Arrays.stream(UtilPlayer.findAdjacentPlayersWithTacklezones(game, player.getTeam(),
				game.getFieldModel().getPlayerCoordinate(player), false))
			.anyMatch(teamMate -> teamMate.hasSkillProperty(NamedProperties.canGrantSkillsToTeamMates) && !teamMate.isUsed(NamedProperties.canGrantSkillsToTeamMates));
	}
	
	public abstract Set<ClientAction> availableActions();

	protected abstract void performAvailableAction(Player<?> player, ClientAction action);

	public void endTurn() {
	}

	public void deselectActingPlayer() {
		client.getCommunication().sendActingPlayer(null, null, false);
	}

	public abstract PlayerInteractionResult playerInteraction(Player<?> player);

}
