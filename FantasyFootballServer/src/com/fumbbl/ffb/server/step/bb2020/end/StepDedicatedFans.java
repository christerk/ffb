package com.fumbbl.ffb.server.step.bb2020.end;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.report.bb2020.ReportDedicatedFans;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepDedicatedFans extends AbstractStep {
	public StepDedicatedFans(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.DEDICATED_FANS;
	}

	@Override
	public void start() {

		TeamResult homeResult = getGameState().getGame().getGameResult().getTeamResultHome();
		TeamResult awayResult = getGameState().getGame().getGameResult().getTeamResultAway();

		int homeDie;
		int awayDie;

		String concededTeam = null;
		Team winningTeam = null;

		Team teamHome = getGameState().getGame().getTeamHome();
		Team teamAway = getGameState().getGame().getTeamAway();

		if (homeResult.hasConceded()) {
			homeDie = 3;
			concededTeam = teamHome.getId();
			winningTeam = getGameState().getGame().getTeamAway();
		} else {
		 homeDie = 6;
		}

		if (awayResult.hasConceded()) {
			awayDie = 3;
			concededTeam = getGameState().getGame().getTeamAway().getId();
			winningTeam = teamHome;
		} else {
			awayDie = 6;
		}

		int rollHome =  getGameState().getDiceRoller().rollDice(homeDie);
		int rollAway =  getGameState().getDiceRoller().rollDice(awayDie);

		if (winningTeam == null) {
			if (homeResult.getScore() > awayResult.getScore()) {
				winningTeam = teamHome;
			} else if (awayResult.getScore() > homeResult.getScore()) {
				winningTeam = getGameState().getGame().getTeamAway();
			}
		}

		if (winningTeam == null) {
			getResult().addReport(new ReportDedicatedFans());
		} else {
			getResult().addReport(new ReportDedicatedFans(
				rollHome,
				modifier(rollHome, teamHome.getDedicatedFans(), winningTeam == teamHome, teamHome.getId().equals(concededTeam)),
				rollAway,
				modifier(rollAway, teamAway.getDedicatedFans(), winningTeam == teamAway, teamAway.getId().equals(concededTeam)),
				concededTeam,
				concededTeam != null
				)
			);
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private int modifier(int roll, int dedicatedFans, boolean winning, boolean conceded) {
		if (conceded) {
			return Math.min(roll, dedicatedFans - 1);
		} else if (winning) {
			return (roll >= dedicatedFans) ? 1 : 0;
		} else {
			return (roll >= dedicatedFans) ? 0 : -1;
		}
	}
}
