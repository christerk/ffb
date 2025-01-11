package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ThrowKegLogicModule extends LogicModule {

	public ThrowKegLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.THROW_KEG;
	}

	@Override
	public void postInit() {
		super.postInit();
		FieldModel fieldModel = client.getGame().getFieldModel();
		Player<?> player = client.getGame().getActingPlayer().getPlayer();
		MoveSquare[] squares = Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(player), FieldCoordinateBounds.FIELD,
			3, false)).map(fieldCoordinate -> new MoveSquare(fieldCoordinate, 0, 0)).toArray(MoveSquare[]::new);
		fieldModel.add(squares);
	}

	@Override
	public void teardown() {
		FieldModel fieldModel = client.getGame().getFieldModel();
		fieldModel.clearMoveSquares();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		} else {
			if (isValidTarget(player, game)) {
				client.getCommunication().sendThrowKeg(player);
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		client.getClientData().setSelectedPlayer(player);
		if (isValidTarget(player, game)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.invalid();
		}
	}

	private boolean isValidTarget(Player<?> player, Game game) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		int distance = playerCoordinate.distanceInSteps(actingPlayerCoordinate);

		PlayerState playerState = game.getFieldModel().getPlayerState(player);


		return distance <= 3 && playerState.getBase() == PlayerState.STANDING && player.getTeam() != game.getActingTeam();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.BLACK_INK);
			add(ClientAction.CATCH_OF_THE_DAY);
			add(ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		if (isEndPlayerActionAvailable()) {
			actionContext.add(ClientAction.END_MOVE);
		}

		if (isTreacherousAvailable(actingPlayer)) {
			actionContext.add(ClientAction.TREACHEROUS);
		}
		if (isWisdomAvailable(actingPlayer)) {
			actionContext.add(ClientAction.WISDOM);
		}
		if (isRaidingPartyAvailable(actingPlayer)) {
			actionContext.add(ClientAction.RAIDING_PARTY);
		}
		if (isLookIntoMyEyesAvailable(actingPlayer)) {
			actionContext.add(ClientAction.LOOK_INTO_MY_EYES);
		}
		if (isBalefulHexAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BALEFUL_HEX);
		}
		if (isBlackInkAvailable(actingPlayer)) {
			actionContext.add(ClientAction.BLACK_INK);
		}
		if (isCatchOfTheDayAvailable(actingPlayer)) {
			actionContext.add(ClientAction.CATCH_OF_THE_DAY);
		}
		if (isThenIStartedBlastinAvailable(actingPlayer)) {
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN);
		}
		return actionContext;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		switch (action) {
			case END_MOVE:
				if (isEndPlayerActionAvailable()) {
					communication.sendActingPlayer(null, null, false);
				}
				break;
			case TREACHEROUS:
				if (isTreacherousAvailable(player)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
					communication.sendUseSkill(skill, true, player.getId());
				}
				break;
			case WISDOM:
				if (isWisdomAvailable(player)) {
					communication.sendUseWisdom();
				}
				break;
			case RAIDING_PARTY:
				if (isRaidingPartyAvailable(player)) {
					Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
					communication.sendUseSkill(raidingSkill, true, player.getId());
				}
				break;
			case LOOK_INTO_MY_EYES:
				if (isLookIntoMyEyesAvailable(player)) {
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
						.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
				}
				break;
			case BALEFUL_HEX:
				if (isBalefulHexAvailable(player)) {
					Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
					communication.sendUseSkill(balefulSkill, true, player.getId());
				}
				break;
			case BLACK_INK:
				if (isBlackInkAvailable(player)) {
					Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
					communication.sendUseSkill(blackInkSkill, true, player.getId());
				}
				break;
			case CATCH_OF_THE_DAY:
				if (isCatchOfTheDayAvailable(player)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
					communication.sendUseSkill(skill, true, player.getId());
				}
				break;
			case THEN_I_STARTED_BLASTIN:
				if (isThenIStartedBlastinAvailable(player)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
					communication.sendUseSkill(skill, true, player.getId());
				}
				break;
			default:
				break;
		}
	}

	public boolean isEndPlayerActionAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !actingPlayer.hasActed();
	}

}
