package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.bb2020.ReportStallerDetected;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepCheckStalling extends AbstractStep {
	public StepCheckStalling(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.CHECK_STALLING;
	}

	@Override
	public void start() {
		if (performCheck()) {
			String stallingPlayer = findStallingPlayer();
			getResult().addReport(new ReportStallerDetected(stallingPlayer));
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private String findStallingPlayer() {
		return null;
	}

	private boolean performCheck() {
		return ((GameOptionBoolean) getGameState().getGame().getOptions().getOptionWithDefault(GameOptionId.ENABLE_STALLING_CHECK)).isEnabled()
			&& getGameState().getPrayerState().shouldNotStall(getGameState().getGame().getActingTeam());
	}
}
