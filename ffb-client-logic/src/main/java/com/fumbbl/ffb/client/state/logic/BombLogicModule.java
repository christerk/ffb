package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilRangeRuler;

import java.util.HashSet;
import java.util.Set;

public class BombLogicModule extends LogicModule {

	private boolean showRangeRuler;
	public BombLogicModule(FantasyFootballClient client) {
		super(client);
		showRangeRuler = true;
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.BOMB;
	}

	@Override
	public Set<ClientAction> availableActions() {

		return new HashSet<ClientAction>() {{
			add(ClientAction.END_MOVE);
			add(ClientAction.HAIL_MARY_BOMB);
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

			if (isHailMaryPassActionAvailable()) {
				actionContext.add(ClientAction.HAIL_MARY_BOMB);
				if (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction())  {
					actionContext.add(Influences.IS_THROWING_HAIL_MARY);
				}
			}

			if (isEndTurnActionAvailable()) {
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

		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		switch (action) {
			case END_MOVE:
				if (isEndTurnActionAvailable()) {
					client.getCommunication().sendActingPlayer(null, null, false);
				}
				break;

			case HAIL_MARY_BOMB:
				if (isHailMaryPassActionAvailable()) {
					if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
						client.getCommunication().sendActingPlayer(player, PlayerAction.THROW_BOMB, actingPlayer.isJumping());
						setShowRangeRuler(true);
					} else {
						client.getCommunication().sendActingPlayer(player, PlayerAction.HAIL_MARY_BOMB, actingPlayer.isJumping());
						setShowRangeRuler(false);
					}
					if (!showRangeRuler() && (game.getFieldModel().getRangeRuler() != null)) {
						game.getFieldModel().setRangeRuler(null);
					}
				}
				break;
			case TREACHEROUS:
				if (isTreacherousAvailable(actingPlayer)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
					client.getCommunication().sendUseSkill(skill, true, player.getId());
				}
				break;
			case WISDOM:
				if (isWisdomAvailable(actingPlayer)) {
					client.getCommunication().sendUseWisdom();
				}
				break;
			case RAIDING_PARTY:
				if (isRaidingPartyAvailable(actingPlayer)) {
					Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
					client.getCommunication().sendUseSkill(raidingSkill, true, player.getId());
				}
				break;
			case LOOK_INTO_MY_EYES:
				if (isLookIntoMyEyesAvailable(player)) {
					UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
						.ifPresent(lookSkill -> client.getCommunication().sendUseSkill(lookSkill, true, player.getId()));
				}
				break;
			case BALEFUL_HEX:
				if (isBalefulHexAvailable(actingPlayer)) {
					Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
					client.getCommunication().sendUseSkill(balefulSkill, true, player.getId());
				}
				break;
			case BLACK_INK:
				if (isBlackInkAvailable(actingPlayer)) {
					Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
					client.getCommunication().sendUseSkill(blackInkSkill, true, player.getId());
				}
				break;
			case CATCH_OF_THE_DAY:
				if (isCatchOfTheDayAvailable(actingPlayer)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
					client.getCommunication().sendUseSkill(skill, true, player.getId());
				}
				break;
			case THEN_I_STARTED_BLASTIN:
				if (isThenIStartedBlastinAvailable(actingPlayer)) {
					Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
					client.getCommunication().sendUseSkill(skill, true, player.getId());
				}
				break;
			default:
				break;
		}
		
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			return new InteractionResult(InteractionResult.Kind.PERFORM, playerCoordinate);
		}
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, coordinate, false);
		if ((PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) || (passingDistance != null)) {
			game.setPassCoordinate(coordinate);
			client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		client.getClientData().setSelectedPlayer(player);
		Game game = client.getGame();
		return new InteractionResult(InteractionResult.Kind.PERFORM, game.getFieldModel().getPlayerCoordinate(player));
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {

		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			if (showRangeRuler()) {
				RangeRuler rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), coordinate, false);
				game.getFieldModel().setRangeRuler(rangeRuler);
				return new InteractionResult(InteractionResult.Kind.DRAW, rangeRuler);
			} else {
				return new InteractionResult(InteractionResult.Kind.IGNORE);
			}
		}
	}

	public boolean showRangeRuler() {
		return showRangeRuler && (client.getGame().getPassCoordinate() == null);
	}

	public void setShowRangeRuler(boolean showRangeRuler) {
		this.showRangeRuler = showRangeRuler;
	}

	public boolean isHailMaryPassActionAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare)
			&& !(game.getFieldModel().getWeather().equals(Weather.BLIZZARD)));
	}

	public boolean isEndTurnActionAvailable() {
		Game game = client.getGame();
		return !game.getTurnMode().isBombTurn() && !game.getActingPlayer().isMustCompleteAction();
	}

	public boolean playerIsAboutToThrow() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB);
	}
}
