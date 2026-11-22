package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandActingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.UtilActingPlayer;
import com.fumbbl.ffb.util.bb2025.SquigRageService;

/**
 * Lets a player who declared an action after rolling for Squig Rage, but has not done anything else yet, withdraw
 * the declaration and choose a different action.
 */
public class DeclaredActionChange {

	private final SquigRageService squigRageService = new SquigRageService();

	/**
	 * Whether the command asks to withdraw the action declared by the acting player.
	 */
	public boolean isRequested(Game game, ClientCommandActingPlayer command) {
		return command.getPlayerAction() == PlayerAction.SQUIG_RAGE && command.getPlayerId() != null
			&& command.getPlayerId().equals(game.getActingPlayer().getPlayerId()) && canChange(game);
	}

	/**
	 * Whether the acting player may still withdraw their declared action.
	 */
	public boolean canChange(Game game) {
		return squigRageService.canChangeDeclaredAction(game.getActingPlayer());
	}

	/**
	 * Returns the acting player to the activation by Squig Rage, which is still waiting for an action declaration.
	 */
	public void withdraw(GameState gameState) {
		Game game = gameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		FieldModel fieldModel = game.getFieldModel();

		TargetSelectionState targetSelectionState = fieldModel.getTargetSelectionState();
		if (targetSelectionState != null) {
			Player<?> target = game.getPlayerById(targetSelectionState.getSelectedPlayerId());
			if (target != null) {
				fieldModel.setPlayerState(target, fieldModel.getPlayerState(target).removeAllTargetSelections());
			}
			// the blitz only counts as used once a target has been selected
			if (actingPlayer.getPlayerAction().isBlitzing()) {
				game.getTurnData().setBlitzUsed(false);
			}
			targetSelectionState.getUsedSkills()
				.forEach(skill -> fieldModel.removeSkillEnhancements(player, skill.getName()));
			fieldModel.setTargetSelectionState(null);
		}

		Skill carrySkill = player.getSkillWithProperty(NamedProperties.canCarryPartner);
		if (carrySkill != null && gameState.getCarriedPlayer() != null) {
			UtilServerGame.undoPickUpPartner(gameState, actingPlayer, carrySkill);
		}

		// a bomb declaration remembers the bombardier
		gameState.getPassState().reset();

		UtilActingPlayer.changeActingPlayer(game, player.getId(), PlayerAction.SQUIG_RAGE, false);
		// standing up is only prepared when an action is declared
		actingPlayer.setCurrentMove(0);
		actingPlayer.setGoingForIt(false);
		actingPlayer.setAwaitingActionDeclaration(true);

		game.setDefenderId(null);
		fieldModel.clearMoveSquares();
		fieldModel.clearTrackNumbers();
		fieldModel.clearDiceDecorations();
		gameState.resetStalling();
	}

	/**
	 * Withdraws the declared action from within the sequence started for it, which is dropped in favour of a new
	 * selection where a different action can be declared.
	 */
	public void returnToActionSelection(IStep step) {
		GameState gameState = step.getGameState();
		withdraw(gameState);
		UtilServerDialog.hideDialog(gameState);
		gameState.getStepStack().clear();
		SequenceGeneratorFactory factory = gameState.getGame().getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		((Select) factory.forName(SequenceGenerator.Type.Select.name()))
			.pushSequence(new Select.SequenceParams(gameState, false));
		step.getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
