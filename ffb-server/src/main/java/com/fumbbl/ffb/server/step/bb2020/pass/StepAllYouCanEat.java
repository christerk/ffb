package com.fumbbl.ffb.server.step.bb2020.pass;

import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.bb2020.ReportAllYouCanEatRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerReRoll;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepAllYouCanEat extends AbstractStepWithReRoll {

	public StepAllYouCanEat(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.ALL_YOU_CAN_EAT;
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus stepCommandStatus = super.handleCommand(pReceivedCommand);
		if (stepCommandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return stepCommandStatus;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		boolean doRoll = true;
		boolean success = false;
		boolean reRolled = false;
		Player<?> player = game.getPlayerById(getGameState().getPassState().getOriginalBombardier());

		if (getReRolledAction() == ReRolledActions.ALL_YOU_CAN_EAT) {
			if (getReRollSource() == null || !UtilServerReRoll.useReRoll(this, getReRollSource(), player)) {
				doRoll = false;
			}
		}


		if (doRoll) {
			int roll = getGameState().getDiceRoller().rollSkill();
			int minimumRoll = 4;
			reRolled = getReRollSource() != null && getReRolledAction() == ReRolledActions.ALL_YOU_CAN_EAT;
			success = roll >= minimumRoll;
			getResult().addReport(new ReportAllYouCanEatRoll(player.getId(), success, roll, minimumRoll, reRolled));

			if (!success && !reRolled
				&& UtilServerReRoll.askForReRollIfAvailable(getGameState(), player, ReRolledActions.ALL_YOU_CAN_EAT, 4, false)) {
				return;
			}

		}

		if (!success) {
			StepParameterSet parameterSet = new StepParameterSet();
			parameterSet.add(from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BOMB));

			getGameState().getStepStack().push(getGameState().getStepFactory().create(StepId.EJECT_PLAYER, null, parameterSet));
			getGameState().getStepStack().push(getGameState().getStepFactory().create(StepId.BRIBES, null, parameterSet));
			if (!reRolled) {
				getResult().setSound(SoundId.WHISTLE);
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}
}
