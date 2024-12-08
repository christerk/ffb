package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

import java.util.HashSet;
import java.util.Set;

public class SelectGazeTargetLogicModule extends MoveLogicModule {

	public SelectGazeTargetLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}
	
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer.equals(actingPlayer.getPlayer()) && isSpecialAbilityAvailable(actingPlayer)) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		} else if (pPlayer.equals(actingPlayer.getPlayer()) || (isValidGazeTarget(game, pPlayer))) {
			client.getCommunication().sendTargetSelected(pPlayer.getId());
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public InteractionResult playerPeek(Player<?> pPlayer) {
		Game game = client.getGame();
		if (isValidGazeTarget(game, pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.INVALID);
		}
	}

	private boolean isValidGazeTarget(Game game, Player<?> target) {
		return !game.getActingTeam().hasPlayer(target) && (game.getFieldModel().getPlayerState(target).hasTacklezones());
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
		}};
	}
}
