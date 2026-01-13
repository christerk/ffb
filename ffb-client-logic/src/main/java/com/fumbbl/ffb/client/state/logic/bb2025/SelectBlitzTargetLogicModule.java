package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.pathfinding.PathFinderWithMultiJump;

import java.util.HashSet;
import java.util.Set;

public class SelectBlitzTargetLogicModule extends MoveLogicModule {
	private final BlockLogicExtension extension;

	public SelectBlitzTargetLogicModule(FantasyFootballClient pClient) {
		super(pClient);
		extension = new BlockLogicExtension(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SELECT_BLITZ_TARGET;
	}

	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer.equals(actingPlayer.getPlayer()) && isBlitzSpecialAbilityAvailable(actingPlayer)) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		} else if (pPlayer.equals(actingPlayer.getPlayer()) || canBeBlitzed(pPlayer, actingPlayer, game)) {
			client.getCommunication().sendTargetSelected(pPlayer.getId());
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	private boolean canBeBlitzed(Player<?> pPlayer, ActingPlayer actingPlayer, Game game) {
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate targetCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
		FieldCoordinate blitzerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
		return !actingPlayer.hasBlocked() && extension.isValidBlitzTarget(game, pPlayer) &&
			(blitzerCoordinate.isAdjacent(targetCoordinate) ||
				ArrayTool.isProvided(PathFinderWithMultiJump.INSTANCE.getPathToBlitzTarget(game, pPlayer)));
	}

	public InteractionResult playerPeek(Player<?> pPlayer) {
		Game game = client.getGame();
		FieldComponent fieldComponent = client.getUserInterface().getFieldComponent();
		fieldComponent.getLayerUnderPlayers().clearMovePath();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (canBeBlitzed(pPlayer, actingPlayer, game)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.invalid();
		}
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		actionContext.add(ClientAction.END_MOVE);

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
			actionContext.add(ClientAction.THEN_I_STARTED_BLASTIN
			);
		}
		if (isFrenziedRushAvailable(actingPlayer)) {
			actionContext.add(ClientAction.FRENZIED_RUSH);
		}
		if (isSlashingNailsAvailable(actingPlayer)) {
			actionContext.add(ClientAction.SLASHING_NAILS);
		}
		return actionContext;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case END_MOVE:
					client.getCommunication().sendTargetSelected(player.getId());
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
						client.getCommunication().sendUseSkill(raidingSkill, true, player.getId());
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
				case FRENZIED_RUSH:
					if (isFrenziedRushAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGainFrenzyForBlitz);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				case SLASHING_NAILS:
					if (isSlashingNailsAvailable(player)) {
						Skill skill = player.getSkillWithProperty(NamedProperties.canGainClawsForBlitz);
						communication.sendUseSkill(skill, true, player.getId());
					}
					break;
				default:
					break;
			}
		}
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
			add(ClientAction.FRENZIED_RUSH);
			add(ClientAction.SLASHING_NAILS);
		}};
	}
}
