package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BlockLogicExtension extends LogicModule {


	public BlockLogicExtension(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		throw new UnsupportedOperationException("getId not implemented for extensions");
	}

	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.BLOCK);
			add(ClientAction.STAB);
			add(ClientAction.CHAINSAW);
			add(ClientAction.PROJECTILE_VOMIT);
			add(ClientAction.TREACHEROUS);
			add(ClientAction.WISDOM);
			add(ClientAction.RAIDING_PARTY);
			add(ClientAction.LOOK_INTO_MY_EYES);
			add(ClientAction.BALEFUL_HEX);
			add(ClientAction.BLACK_INK);
			add(ClientAction.BREATHE_FIRE);
			add(ClientAction.THEN_I_STARTED_BLASTIN);
		}};
	}

	@Override
	public ActionContext actionContext(ActingPlayer actingPlayer) {

		ActionContext actionContext = new ActionContext();

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

	public ActionContext blockActionContext(ActingPlayer actingPlayer, boolean multiBlock) {
		Player<?> attacker = actingPlayer.getPlayer();
		ActionContext actionContext = new ActionContext();
		if (attacker.hasSkillProperty(NamedProperties.providesStabBlockAlternative)) {
			actionContext.add(ClientAction.STAB);
		}
		if (attacker.hasSkillProperty(NamedProperties.providesChainsawBlockAlternative) && !multiBlock) {
			actionContext.add(ClientAction.CHAINSAW);
		}
		Optional<Skill> vomitSkill = UtilCards.getUnusedSkillWithProperty(attacker, NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
		if (vomitSkill.isPresent() && !multiBlock) {
			if (vomitSkill.get().hasSkillProperty(NamedProperties.canUseVomitAfterBlock)) {
				actionContext.add(Influences.VOMIT_DUE_TO_PUTRID_REGURGITATION);
			}
			actionContext.add(ClientAction.PROJECTILE_VOMIT);
		}

		Optional<Skill> fireSkill = UtilCards.getUnusedSkillWithProperty(attacker, NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover);
		if (fireSkill.isPresent() && !multiBlock) {
			actionContext.add(ClientAction.BREATHE_FIRE);
		}

		if (isGoredAvailable()) {
			actionContext.add(ClientAction.GORED_BY_THE_BULL);
		}
		actionContext.add(ClientAction.BLOCK);

		return actionContext;
	}

	protected void performAvailableAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();
		switch (action) {
			case BLOCK:
				block(actingPlayer.getPlayerId(), player, false, false, false, false);
				break;
			case STAB:
				block(actingPlayer.getPlayerId(), player, true, false, false, false);
				break;
			case CHAINSAW:
				block(actingPlayer.getPlayerId(), player, false, true, false, false);
				break;
			case PROJECTILE_VOMIT:
				block(actingPlayer.getPlayerId(), player, false, false, true, false);
				break;
			case TREACHEROUS:
				Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
				communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
				break;
			case WISDOM:
				communication.sendUseWisdom();
				break;
			case RAIDING_PARTY:
				Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
				communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
				break;
			case LOOK_INTO_MY_EYES:
				UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
					.ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
				break;
			case BALEFUL_HEX:
				Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
				communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
				break;
			case BLACK_INK:
				Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
				communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
				break;
			case BREATHE_FIRE:
				block(actingPlayer.getPlayerId(), player, false, false, false, true);
				break;
			case THEN_I_STARTED_BLASTIN:
				if (isThenIStartedBlastinAvailable(actingPlayer)) {
					Skill blastinSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
					communication.sendUseSkill(blastinSkill, true, actingPlayer.getPlayerId());
				}
				break;
			default:
				break;

		}
	}

	public void block(String pActingPlayerId, Player<?> pDefender, boolean pUsingStab,
										boolean usingChainsaw, boolean usingVomit, boolean usingBreatheFire) {
		// TODO is this needed? Was in place in old structure
		//pClientState.getClient().getUserInterface().getFieldComponent().refresh();
		client.getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab, usingChainsaw, usingVomit, usingBreatheFire);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		return playerInteraction(player, false, false);
	}

	public InteractionResult playerInteraction(Player<?> pDefender, boolean pDoBlitz, boolean multiBlock) {
		if (pDefender == null) {
			return InteractionResult.ignore();
		}

		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		// rooted players can not move but still spend movement for the blitz action
		if (isBlockable(game, pDefender) && (!pDoBlitz || playerState.isRooted() || UtilPlayer.isNextMovePossible(game, false))) {
			FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pDefender);
			if (UtilCards.hasUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.providesBlockAlternative) || (isGoredAvailable() && pDoBlitz)) {
				return InteractionResult.selectAction(blockActionContext(actingPlayer, multiBlock));
			} else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
				block(actingPlayer.getPlayerId(), pDefender, false, false, false, false);
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}


	public boolean isBlockable(Game game, Player<?> pPlayer) {
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		return isValidBlitzTarget(game, pPlayer) && (defenderCoordinate.isAdjacent(attackerCoordinate) || actingPlayer.getPlayerAction() == PlayerAction.VICIOUS_VINES)
			&& (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null);
	}

	public boolean isValidBlitzTarget(Game game, Player<?> pPlayer) {
		if (pPlayer != null) {
			FieldModel fieldModel = game.getFieldModel();
			PlayerState defenderState = fieldModel.getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
			return (defenderState.canBeBlocked() && game.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
				&& (fieldModel.getTargetSelectionState() == null || pPlayer.getId().equals(fieldModel.getTargetSelectionState().getSelectedPlayerId())));
		}
		return false;
	}

	public boolean isGoredAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
		if (targetSelectionState != null && UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canAddBlockDie)) {
			FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(game.getPlayerById(targetSelectionState.getSelectedPlayerId()));
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			DiceDecoration diceDecoration = game.getFieldModel().getDiceDecoration(targetCoordinate);
			Player<?> defender = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
			boolean opponentCanMove = UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.canMoveBeforeBeingBlocked);
			return diceDecoration != null
				&& (diceDecoration.getNrOfDice() == 1 || diceDecoration.getNrOfDice() == 2 || (diceDecoration.getNrOfDice() == 3 && opponentCanMove))
				&& targetCoordinate.isAdjacent(playerCoordinate);
		}

		return false;
	}
}
